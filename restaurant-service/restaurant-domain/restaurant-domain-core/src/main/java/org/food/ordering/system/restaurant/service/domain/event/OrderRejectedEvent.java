package org.food.ordering.system.restaurant.service.domain.event;

import org.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import org.food.ordering.system.domain.valueobject.RestaurantId;
import org.food.ordering.system.restaurant.service.domain.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderRejectedEvent extends OrderApprovalEvent {
    private final DomainEventPublisher<OrderRejectedEvent> domainEventPublisher;

    public OrderRejectedEvent(OrderApproval orderApproval,
                                 RestaurantId restaurantId,
                                 List<String> failureMessages,
                                 ZonedDateTime createdAt,
                                 DomainEventPublisher<OrderRejectedEvent> domainEventPublisher) {
        super(orderApproval, restaurantId, failureMessages, createdAt);
        this.domainEventPublisher = domainEventPublisher;
    }


    @Override
    public void fire() {
        domainEventPublisher.publish(this);
    }
}
