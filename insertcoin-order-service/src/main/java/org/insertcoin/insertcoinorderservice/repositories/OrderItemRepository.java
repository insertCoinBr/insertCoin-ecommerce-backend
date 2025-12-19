package org.insertcoin.insertcoinorderservice.repositories;

import org.insertcoin.insertcoinorderservice.entities.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, UUID> {
}
