package org.food.ordering.system.order.service.domain.ports;

import lombok.extern.slf4j.Slf4j;
import org.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import org.food.ordering.system.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentResponseMessageListenerImpl implements PaymentResponseMessageListener {
    @Override
    public void paymentCompleted(PaymentResponse paymentResponse) {
    }

    @Override
    public void paymentCancelled(PaymentResponse paymentResponse) {

    }
}
