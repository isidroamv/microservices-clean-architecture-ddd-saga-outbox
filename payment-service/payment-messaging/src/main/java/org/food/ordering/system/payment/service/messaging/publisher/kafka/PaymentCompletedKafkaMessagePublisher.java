package org.food.ordering.system.payment.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import lombok.extern.slf4j.Slf4j;
import org.food.ordering.system.kafka.producer.service.KafkaProducer;
import org.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import org.food.ordering.system.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import org.ordering.system.payment.service.domain.config.PaymentServiceConfigData;
import org.ordering.system.payment.service.domain.dto.PaymentRequest;
import org.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentCompleteKafkaMessagePublisher implements PaymentCompletedMessagePublisher {
    private final PaymentMessagingDataMapper paymentMessagingDataMapper;
    private final KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;
    private final PaymentServiceConfigData paymentServiceConfigData;

    public PaymentCompleteKafkaMessagePublisher(PaymentMessagingDataMapper paymentMessagingDataMapper,
                                                KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer,
                                                PaymentServiceConfigData paymentServiceConfigData) {
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.paymentServiceConfigData = paymentServiceConfigData;
    }

    @Override
    public void publish(PaymentCompletedEvent domainEvent) {
        String orderId = domainEvent.getPayment().getOrderId().getValue().toString();
        log.info("Received payment completed event for order id: {}", orderId);

        PaymentResponseAvroModel paymentResponseAvroModel =
                paymentMessagingDataMapper.paymentCompleteEventToPaymentResponseAvroModel(domainEvent);

        kafkaProducer.send(
                paymentServiceConfigData.getPaymentResponseTopicName(),
                paymentResponseAvroModel.getSagaId(),
                paymentResponseAvroModel,
                paymentResponseAvroModel.getPaymentId().toString()
        );
    }
}
