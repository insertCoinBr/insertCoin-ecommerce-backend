package org.insertcoin.insertcoinorderservice.repositories;

import org.insertcoin.insertcoinorderservice.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {
}
