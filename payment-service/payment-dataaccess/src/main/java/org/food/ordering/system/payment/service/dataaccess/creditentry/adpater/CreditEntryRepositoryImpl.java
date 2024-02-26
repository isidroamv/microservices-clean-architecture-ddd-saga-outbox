package org.food.ordering.system.payment.service.dataaccess.creditentry.payment.adpater;

import org.food.ordering.system.payment.service.dataaccess.creditentry.payment.mapper.CreditEntryDataaccessMapper;
import org.food.ordering.system.payment.service.dataaccess.creditentry.payment.repository.PaymentJpaRepository;
import org.food.ordering.system.payment.service.domain.entity.Payment;
import org.ordering.system.payment.service.domain.ports.output.repository.PaymentRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PaymentRepositoryImpl implements PaymentRepository {
    private final PaymentJpaRepository paymentJpaRepository;
    private final CreditEntryDataaccessMapper creditEntryDataaccessMapper;

    public PaymentRepositoryImpl(PaymentJpaRepository paymentJpaRepository,
                                 CreditEntryDataaccessMapper creditEntryDataaccessMapper) {
        this.paymentJpaRepository = paymentJpaRepository;
        this.creditEntryDataaccessMapper = creditEntryDataaccessMapper;
    }


    @Override
    public Payment save(Payment payment) {
        return creditEntryDataaccessMapper
                .paymentEntityToPayment(paymentJpaRepository
                        .save(creditEntryDataaccessMapper.paymentToPaymentEntity(payment)));
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return paymentJpaRepository.findByOrderId(orderId)
                .map(creditEntryDataaccessMapper::paymentEntityToPayment);
    }
}
