package org.example.order.repository;

import org.example.order.model.OrderItem;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends R2dbcRepository<OrderItem, Long> {

}
