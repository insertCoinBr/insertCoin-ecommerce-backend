package org.insertcoin.insertcoinorderservice.services;

import org.insertcoin.insertcoinorderservice.clients.AuthClient;
import org.insertcoin.insertcoinorderservice.clients.ProductClient;
import org.insertcoin.insertcoinorderservice.dtos.request.OrderCreateRequestDTO;
import org.insertcoin.insertcoinorderservice.dtos.request.OrderItemRequestDTO;
import org.insertcoin.insertcoinorderservice.dtos.response.AuthMeResponseDTO;
import org.insertcoin.insertcoinorderservice.dtos.response.ProductResponseDTO;
import org.insertcoin.insertcoinorderservice.entities.OrderEntity;
import org.insertcoin.insertcoinorderservice.entities.OrderItemEntity;
import org.insertcoin.insertcoinorderservice.enums.OrderStatus;
import org.insertcoin.insertcoinorderservice.repositories.OrderRepository;
import org.insertcoin.insertcoinorderservice.repositories.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AuthClient authClient;
    private final ProductClient productClient;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        AuthClient authClient,
                        ProductClient productClient) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.authClient = authClient;
        this.productClient = productClient;
    }

    public OrderEntity createOrder(String token, OrderCreateRequestDTO request) {

        // 1. Buscar usuário autenticado
        AuthMeResponseDTO user = authClient.getAuthenticatedUser(token);
        if (user == null) {
            throw new RuntimeException("Usuário não autenticado.");
        }

        // 2. Criar entidade do pedido
        OrderEntity order = new OrderEntity();
        order.setCustomerId(user.getId());
        order.setCustomerName(user.getName());
        order.setCustomerEmail(user.getEmail());
        order.setStatus(OrderStatus.PENDING);
        order.setOrderNumber(generateOrderNumber());

        BigDecimal total = BigDecimal.ZERO;

        // 3. Processar cada item do pedido
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
            item.setSku(product.getGameId());
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(unitPrice);
            item.setSubtotal(subtotal);

            // Adicionar item ao pedido
            order.addItem(item);

            total = total.add(subtotal);
        }

        // 4. Definir total
        order.setTotalAmount(total);

        // 5. Salvar pedido
        return orderRepository.save(order);
    }

    private String generateOrderNumber() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
