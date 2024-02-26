package org.ordering.system.payment.service.domain.ports.input.message.listener;

import org.ordering.system.payment.service.domain.dto.PaymentRequest;

public interface PaymentMessageListener {
    void completePayment(PaymentRequest paymentRequest);
    void cancelPayment(PaymentRequest paymentRequest);
}
