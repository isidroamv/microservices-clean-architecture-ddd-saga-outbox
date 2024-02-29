package org.food.ordering.system.restaurant.service.domain.event;

import org.food.ordering.system.domain.event.DomainEvent;
import org.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import org.food.ordering.system.domain.valueobject.RestaurantId;
import org.food.ordering.system.restaurant.service.domain.entity.OrderApproval;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderApprovedEvent extends OrderApprovalEvent {
    private final DomainEventPublisher<OrderApprovedEvent> domainEventPublisher;


    public OrderApprovedEvent(OrderApproval orderApproval,
                                 RestaurantId restaurantId,
                                 List<String> failureMessages,
                                 ZonedDateTime createdAt,
                                 DomainEventPublisher<OrderApprovedEvent> domainEventPublisher) {
        super(orderApproval, restaurantId, failureMessages, createdAt);
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public void fire() {
        domainEventPublisher.publish(this);
    }
}
