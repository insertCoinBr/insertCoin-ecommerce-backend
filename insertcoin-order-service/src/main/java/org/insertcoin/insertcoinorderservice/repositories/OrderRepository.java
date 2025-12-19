package org.insertcoin.insertcoinorderservice.repositories;

import org.insertcoin.insertcoinorderservice.entities.OrderEntity;
import org.insertcoin.insertcoinorderservice.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
    Optional<OrderEntity> findByIdAndCustomerId(UUID orderId, UUID customerId);
    List<OrderEntity> findByCustomerId(UUID customerId);

    Page<OrderEntity> findByCustomerId(UUID customerId, Pageable pageable);

    @Query("""
    SELECT o FROM OrderEntity o
    WHERE (:status IS NULL OR o.status = :status)
    AND (:orderNumber IS NULL OR o.orderNumber = :orderNumber)
""")
    Page<OrderEntity> searchOrders(
            @Param("status") OrderStatus status,
            @Param("orderNumber") String orderNumber,
            Pageable pageable
    );

    @Query("SELECT o FROM OrderEntity o LEFT JOIN FETCH o.items WHERE o.id = :orderId")
    Optional<OrderEntity> findByIdWithItems(@Param("orderId") UUID orderId);

}
