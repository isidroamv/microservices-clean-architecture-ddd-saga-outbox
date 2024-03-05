package org.food.ordering.system.order.service.domain.ports;

import lombok.extern.slf4j.Slf4j;
import org.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import org.food.ordering.system.order.service.domain.OrderDomainService;
import org.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import org.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import org.food.ordering.system.order.service.domain.entity.Customer;
import org.food.ordering.system.order.service.domain.entity.Order;
import org.food.ordering.system.order.service.domain.entity.Restaurant;
import org.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import org.food.ordering.system.order.service.domain.exception.OrderDomainException;
import org.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import org.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCreatePaymentRequestMessagePublisher;
import org.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import org.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import org.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderCreateCommandHandler {
    private final OrderCreateHelper orderCreateHelper;
    private final OrderDataMapper orderDataMapper;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderDomainService orderDomainService;
    private final OrderCreatePaymentRequestMessagePublisher orderCreatePaymentRequestMessagePublisher;

    public OrderCreateCommandHandler(OrderCreateHelper orderCreateHelper,
                                     OrderDataMapper orderDataMapper,
                                     OrderCreatePaymentRequestMessagePublisher orderCreatePaymentRequestMessagePublisher,
                                     OrderRepository orderRepository,
                                     CustomerRepository customerRepository,
                                     RestaurantRepository restaurantRepository,
                                     OrderDomainService orderDomainService) {
        this.orderCreateHelper = orderCreateHelper;
        this.orderDataMapper = orderDataMapper;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderDomainService = orderDomainService;
        this.orderCreatePaymentRequestMessagePublisher = orderCreatePaymentRequestMessagePublisher;
    }

    @Transactional
    public CreateOrderResponse persisOrder(CreateOrderCommand createOrderCommand) {
        checkCustomer(createOrderCommand.getCustomerId());
        Restaurant restaurant = checkRestaurant(createOrderCommand);
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, restaurant, orderCreatePaymentRequestMessagePublisher);
        Order orderResult = saveOrder(order);
        System.out.println("Tracking id: " + orderResult.getTrackingId());
        log.info("Order with id: {} is created.", orderResult.getId().getValue());
        return orderDataMapper.orderToCreateOrderResponse(orderResult, "Order created successfully");
    }

    private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
        Restaurant restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findRestaurantInformation(restaurant);
        if (optionalRestaurant.isEmpty()) {
            log.warn("Could not find restaurant with id " + createOrderCommand.getRestaurantId());
            throw new OrderDomainException("Could not find restaurant with id "
                    + createOrderCommand.getRestaurantId());
        }
        return optionalRestaurant.get();
    }

    private void checkCustomer(UUID customerId) {
        Optional<Customer> customer = customerRepository.findCustomer(customerId);
        if (customer.isEmpty()) {
            log.warn("Could not find customer with id " + customerId);
            throw new OrderDomainException("Could not find customer with id " + customerId);
        }
    }

    private Order saveOrder(Order order) {
        Order orderResult = orderRepository.save(order);
        if (orderResult == null) {
            log.error("Could not save order " + order.getId().getValue());
            throw new OrderDomainException("Could not save order " + order.getId().getValue());
        }
        log.info("Order with id: {} saved successfully", order.getId().getValue());
        return orderResult;
    }
}
