package org.insertcoin.insertcoinorderservice.services;

import jakarta.transaction.Transactional;
import org.insertcoin.insertcoinorderservice.clients.AuthClient;
import org.insertcoin.insertcoinorderservice.clients.ProductClient;
import org.insertcoin.insertcoinorderservice.dtos.request.OrderCreateRequestDTO;
import org.insertcoin.insertcoinorderservice.dtos.request.OrderItemRequestDTO;
import org.insertcoin.insertcoinorderservice.dtos.response.AuthMeResponseDTO;
import org.insertcoin.insertcoinorderservice.dtos.response.OrderNotificationDataDTO;
import org.insertcoin.insertcoinorderservice.dtos.response.OrderProductDTO;
import org.insertcoin.insertcoinorderservice.dtos.response.ProductResponseDTO;
import org.insertcoin.insertcoinorderservice.entities.OrderEntity;
import org.insertcoin.insertcoinorderservice.entities.OrderItemEntity;
import org.insertcoin.insertcoinorderservice.enums.OrderStatus;
import org.insertcoin.insertcoinorderservice.repositories.CurrencyRepository;
import org.insertcoin.insertcoinorderservice.repositories.OrderRepository;
import org.insertcoin.insertcoinorderservice.repositories.OrderItemRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AuthClient authClient;
    private final ProductClient productClient;
    private final CurrencyRepository currencyRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        AuthClient authClient,
                        ProductClient productClient, RabbitTemplate rabbitTemplate, EmailService emailService, CurrencyRepository currencyRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.authClient = authClient;
        this.productClient = productClient;
        this.currencyRepository = currencyRepository;
    }

    @Transactional
    public OrderEntity createOrder(String token, OrderCreateRequestDTO request) {

        AuthMeResponseDTO user = authClient.getAuthenticatedUser(token);
        if (user == null) {
            throw new RuntimeException("Usuário não autenticado.");
        }

        OrderEntity order = new OrderEntity();
        order.setCustomerId(user.getId());
        order.setCustomerName(user.getName());
        order.setCustomerEmail(user.getEmail());
        order.setStatus(OrderStatus.PENDING);
        order.setOrderNumber(generateOrderNumber());

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequestDTO itemDTO : request.getItems()) {

            ProductResponseDTO product =
                    productClient.findById(itemDTO.getProductId(), token);

            if (product == null) {
                throw new RuntimeException("Produto não encontrado: " + itemDTO.getProductId());
            }

            BigDecimal unitPrice = product.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemDTO.getQuantity()));

            // Criar OrderItemEntity
            OrderItemEntity item = new OrderItemEntity();
            item.setProductId(itemDTO.getProductId());
            item.setProductName(product.getName());
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(unitPrice);
            item.setSubtotal(subtotal);
            item.setImageUrl(product.getImageUrl());

            // Adicionar item ao pedido
            order.addItem(item);

            total = total.add(subtotal);
        }

        order.setTotalAmount(total);

        return orderRepository.save(order);
    }

    private String generateOrderNumber() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public BigDecimal getConversionRate(String from, String to) {
        var currency = currencyRepository.findBySourceCurrencyAndTargetCurrency(from, to);
        if (currency == null) {
            throw new RuntimeException("Conversão de moeda não encontrada: " + from + " -> " + to);
        }
        return currency.getConversionRate();
    }

    public OrderEntity getOrderById(String token, UUID orderId) {
        AuthMeResponseDTO user = authClient.getAuthenticatedUser(token);
        if (user == null) {
            throw new RuntimeException("Usuário não autenticado.");
        }
        return orderRepository.findByIdAndCustomerId(orderId, user.getId())
                .orElse(null);
    }

    public List<OrderEntity> getOrdersByUser(
            String token,
            String targetCurrency,
            String statusFilter,
            String orderBy,
            String direction
    ) {
        AuthMeResponseDTO user = authClient.getAuthenticatedUser(token);
        if (user == null) {
            throw new RuntimeException("Usuário não autenticado.");
        }

        // Buscar todos os pedidos do usuário
        List<OrderEntity> orders = orderRepository.findByCustomerId(user.getId());

        // Filtrar por status
        if (statusFilter != null && !statusFilter.isEmpty()) {
            orders = orders.stream()
                    .filter(order -> order.getStatus().name().equalsIgnoreCase(statusFilter))
                    .collect(Collectors.toList());
        }

        // Ordenar
        if (orderBy != null && !orderBy.isEmpty()) {
            Comparator<OrderEntity> comparator = switch (orderBy.toLowerCase()) {
                case "totalamount" -> Comparator.comparing(OrderEntity::getTotalAmount);
                default -> Comparator.comparing(OrderEntity::getCreatedAt);
            };
            if ("desc".equalsIgnoreCase(direction)) {
                comparator = comparator.reversed();
            }
            orders.sort(comparator);
        }

        // Conversão de moeda
        if (targetCurrency != null && !"BRL".equalsIgnoreCase(targetCurrency)) {
            BigDecimal conversionRate = getConversionRate("BRL", targetCurrency);
            orders.forEach(order -> {
                order.getItems().forEach(item -> {
                    item.setUnitPrice(item.getUnitPrice().multiply(conversionRate).setScale(2, RoundingMode.HALF_UP));
                    item.setSubtotal(item.getSubtotal().multiply(conversionRate).setScale(2, RoundingMode.HALF_UP));
                });
                order.setTotalAmount(order.getTotalAmount().multiply(conversionRate).setScale(2, RoundingMode.HALF_UP));
            });
        }

        return orders;
    }

    public void deleteOrder(UUID orderId) {
        OrderEntity existing = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        orderRepository.delete(existing);
    }

    public Page<OrderEntity> searchOrders(OrderStatus status, String orderNumber, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.searchOrders(status, orderNumber, pageable);
    }

    public OrderEntity findByIdOrThrow(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    public void updateOrderStatus(UUID orderId, String status) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        OrderStatus orderStatus;
        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + status);
        }

        order.setStatus(orderStatus);
        orderRepository.save(order);
    }

    public Page<OrderEntity> getOrdersByUserId(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findByCustomerId(userId, pageable);
    }

    public OrderNotificationDataDTO getNotificationData(UUID orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderNotificationDataDTO dto = new OrderNotificationDataDTO();
        dto.setOrderId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setAmount(order.getTotalAmount());
        dto.setCurrency("BRL");

        List<OrderProductDTO> products = order.getItems().stream()
                .map(item -> {
                    OrderProductDTO p = new OrderProductDTO();
                    p.setProductName(item.getProductName());
                    p.setUnitPrice(item.getUnitPrice());
                    p.setQuantity(item.getQuantity());
                    p.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    return p;
                })
                .collect(Collectors.toList());

        dto.setProducts(products);

        return dto;
    }

}
