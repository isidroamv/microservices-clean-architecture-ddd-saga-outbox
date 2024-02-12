package org.food.ordering.system.order.service.domain.ports.output.repository;

import org.food.ordering.system.order.service.domain.entity.Order;

import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findByTrackingId(String trackingId);

}
