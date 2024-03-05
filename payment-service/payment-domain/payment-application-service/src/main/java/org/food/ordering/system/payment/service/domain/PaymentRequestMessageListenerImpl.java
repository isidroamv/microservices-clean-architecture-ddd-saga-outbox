package org.ordering.system.payment.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.food.ordering.system.payment.service.domain.event.PaymentCancelledEvent;
import org.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import org.food.ordering.system.payment.service.domain.event.PaymentEvent;
import org.food.ordering.system.payment.service.domain.event.PaymentFailedEvent;
import org.ordering.system.payment.service.domain.dto.PaymentRequest;
import org.ordering.system.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import org.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCancelledMessagePublisher;
import org.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import org.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentFailedMessagePublisher;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentRequestMessageListenerImpl implements PaymentRequestMessageListener {
    private final PaymentRequestHelper paymentRequestHelper;

    public PaymentRequestMessageListenerImpl(PaymentRequestHelper paymentRequestHelper) {
        this.paymentRequestHelper = paymentRequestHelper;
    }

    @Override
    public void completePayment(PaymentRequest paymentRequest) {
        PaymentEvent paymentEvent = paymentRequestHelper.persistPayment(paymentRequest);
        fireEvent(paymentEvent);
    }

    @Override
    public void cancelPayment(PaymentRequest paymentRequest) {
        PaymentEvent paymentEvent = paymentRequestHelper.persistCancelPayment(paymentRequest);
        fireEvent(paymentEvent);
    }

    private void fireEvent(PaymentEvent paymentEvent) {
        log.info("Publishing payment with payment id: {} and order id: {}", paymentEvent.getPayment().getId(),
                paymentEvent.getPayment().getOrderId());

        paymentEvent.fire();
    }
}
