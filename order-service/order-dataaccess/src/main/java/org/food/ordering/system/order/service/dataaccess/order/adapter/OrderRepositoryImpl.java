package org.food.ordering.system.order.service.dataaccess.order.adapter;

import org.food.ordering.system.domain.valueobject.OrderId;
import org.food.ordering.system.order.service.dataaccess.order.entity.OrderEntity;
import org.food.ordering.system.order.service.dataaccess.order.mapper.OrderDataAccessMapper;
import org.food.ordering.system.order.service.dataaccess.order.repository.OrderJpaRepository;
import org.food.ordering.system.order.service.domain.entity.Order;
import org.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import org.food.ordering.system.order.service.domain.valueobject.TrackingId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;
    private final OrderDataAccessMapper orderDataAccessMapper;

    public OrderRepositoryImpl(OrderJpaRepository orderJpaRepository,
                               OrderDataAccessMapper orderDataAccessMapper) {
        this.orderJpaRepository = orderJpaRepository;
        this.orderDataAccessMapper = orderDataAccessMapper;
    }

    @Override
    public Order save(Order order) {
        return orderDataAccessMapper.orderEntityToOrder(
                orderJpaRepository.save(orderDataAccessMapper.orderToOrderEntity(order))
        );
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        Optional<OrderEntity> order = orderJpaRepository.findById(orderId.getValue());
        return order.map(orderDataAccessMapper::orderEntityToOrder);
    }

    @Override
    public Optional<Order> findByTrackingId(TrackingId trackingId) {
        Optional<OrderEntity> byTrackingId = orderJpaRepository
                .findByTrackingId(trackingId.getValue());
        return byTrackingId.map(orderDataAccessMapper::orderEntityToOrder);
    }

}
