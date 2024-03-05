package org.food.ordering.system.order.service.messaging.listener.kafka;

import org.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus;
import org.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import lombok.extern.slf4j.Slf4j;
import org.food.ordering.system.kafka.consumer.KafkaConsumer;
import org.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import org.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import org.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.food.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;

@Slf4j
@Component
public class RestaurantApprovalResponseKafkaListener implements KafkaConsumer<RestaurantApprovalResponseAvroModel> {
    private final RestaurantApprovalResponseMessageListener approvalResponse;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    public RestaurantApprovalResponseKafkaListener(
            RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener,
            OrderMessagingDataMapper orderMessagingDataMapper) {
        this.approvalResponse = restaurantApprovalResponseMessageListener;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
            topics = "${order-service.restaurant-approval-response-topic-name}")
    public void receive(@Payload List<RestaurantApprovalResponseAvroModel> message,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of payment response messages received with keys: {}, partitions: {}, offsets: {}",
                message.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString()
        );

        message.forEach(approvalResponseAvro -> {
            if (OrderApprovalStatus.APPROVED == approvalResponseAvro.getOrderApprovalStatus()) {
                log.info("Processing order approved event for order id: {}", approvalResponseAvro.getOrderId());
                RestaurantApprovalResponse restaurantApprovalResponse = orderMessagingDataMapper
                        .restaurantApprovalResponseAvroModelToRestaurantApprovalResponse(approvalResponseAvro);
                approvalResponse.orderApproved(restaurantApprovalResponse);
            } else if (OrderApprovalStatus.REJECTED == approvalResponseAvro.getOrderApprovalStatus()) {
                log.info("Processing order rejected event for order id: {} with message: {}",
                        approvalResponseAvro.getOrderId(),
                        String.join(FAILURE_MESSAGE_DELIMITER, approvalResponseAvro.getFailureMessages()));
                approvalResponse.orderReject(orderMessagingDataMapper
                        .restaurantApprovalResponseAvroModelToRestaurantApprovalResponse(approvalResponseAvro));
            }
        });
    }
}
