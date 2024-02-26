package org.food.ordering.system.payment.service.dataaccess.creditentry.payment.mapper;

import org.food.ordering.system.domain.valueobject.CustomerId;
import org.food.ordering.system.domain.valueobject.Money;
import org.food.ordering.system.domain.valueobject.OrderId;
import org.food.ordering.system.payment.service.dataaccess.creditentry.payment.entity.CreditEntryEntity;
import org.food.ordering.system.payment.service.domain.entity.Payment;
import org.food.ordering.system.payment.service.domain.valueObject.PaymentId;
import org.springframework.stereotype.Component;

@Component
public class CreditEntryDataaccessMapper {
    public CreditEntryEntity paymentToPaymentEntity(Payment payment) {
        return CreditEntryEntity.builder()
                .id(payment.getId().getValue())
                .customerId(payment.getCustomerId().getValue())
                .orderId(payment.getOrderId().getValue())
                .price(payment.getPrice().getAmount())
                .paymentStatus(payment.getPaymentStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    public Payment paymentEntityToPayment(CreditEntryEntity paymentEntity) {
        return Payment.builder()
                .paymentId(new PaymentId(paymentEntity.getId()))
                .customerId(new CustomerId(paymentEntity.getCustomerId()))
                .orderId(new OrderId(paymentEntity.getOrderId()))
                .price(new Money(paymentEntity.getPrice()))
                .paymentStatus(paymentEntity.getPaymentStatus())
                .createdAt(paymentEntity.getCreatedAt())
                .build();
    }
}
