package org.food.ordering.system.order.service.messaging.listener.kafka;

import org.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import org.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.food.ordering.system.kafka.consumer.KafkaConsumer;
import org.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import org.food.ordering.system.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import org.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PaymentResponseKafkaListener implements KafkaConsumer<PaymentResponseAvroModel> {
    private final PaymentResponseMessageListener paymentResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    public PaymentResponseKafkaListener(PaymentResponseMessageListener paymentResponseMessageListener,
                                        OrderMessagingDataMapper orderMessagingDataMapper) {
        this.paymentResponseMessageListener = paymentResponseMessageListener;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}",
            topics = "${order-service.payment-response-topic-name}")
    public void receive(@Payload List<PaymentResponseAvroModel> message,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        log.info("{} number of payment response messages received with keys: {}, partitions: {}, offsets: {}",
                message.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString()
        );

        message.forEach(paymentResponseAvroModel -> {
            if (PaymentStatus.COMPLETED == paymentResponseAvroModel.getPaymentStatus()) {
                log.info("Processing payment completed event for order id: {}", paymentResponseAvroModel.getOrderId());
                PaymentResponse paymentResponse = orderMessagingDataMapper
                        .paymentResponseAvroModelToPaymentResponse(paymentResponseAvroModel);
                paymentResponseMessageListener.paymentCompleted(paymentResponse);
            } else if (PaymentStatus.CANCELLED == paymentResponseAvroModel.getPaymentStatus() ||
                    PaymentStatus.FAILED == paymentResponseAvroModel.getPaymentStatus()) {
                log.info("Processing payment cancelled event for order id: {}", paymentResponseAvroModel.getOrderId());
                paymentResponseMessageListener.paymentCancelled(orderMessagingDataMapper
                        .paymentResponseAvroModelToPaymentResponse(paymentResponseAvroModel));

            }
        });
    }
}
