package org.food.ordering.system.payment.service.dataaccess.payment.adpater;

import org.food.ordering.system.payment.service.dataaccess.payment.mapper.PaymentDataaccessMapper;
import org.food.ordering.system.payment.service.dataaccess.payment.repository.PaymentJpaRepository;
import org.food.ordering.system.payment.service.domain.entity.Payment;
import org.ordering.system.payment.service.domain.ports.output.repository.PaymentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentDataaccessMapper paymentDataaccessMapper;

    public PaymentRepositoryImpl(PaymentJpaRepository paymentJpaRepository,
                                 PaymentDataaccessMapper paymentDataaccessMapper) {
        this.paymentJpaRepository = paymentJpaRepository;
        this.paymentDataaccessMapper = paymentDataaccessMapper;
    }


    @Override
    public Payment save(Payment payment) {
        return paymentDataaccessMapper
                .paymentEntityToPayment(paymentJpaRepository
                        .save(paymentDataaccessMapper.paymentToPaymentEntity(payment)));
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return paymentJpaRepository.findByOrderId(orderId)
                .map(paymentDataaccessMapper::paymentEntityToPayment);
    }
}
