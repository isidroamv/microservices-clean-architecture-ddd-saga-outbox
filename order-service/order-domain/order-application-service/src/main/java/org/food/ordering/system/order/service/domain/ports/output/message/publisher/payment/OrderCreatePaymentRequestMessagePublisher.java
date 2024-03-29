package org.food.ordering.system.order.service.domain.ports.output.message.publisher.payment;

import org.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import org.food.ordering.system.order.service.domain.event.OrderCreatedEvent;

public interface OrderCreatePaymentRequestMessagePublisher extends DomainEventPublisher<OrderCreatedEvent> {

}
