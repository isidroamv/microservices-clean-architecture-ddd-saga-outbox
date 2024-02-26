package org.food.ordering.system.payment.service.dataaccess.creditentry.adpater;

import org.food.ordering.system.payment.service.dataaccess.creditentry.mapper.CreditEntryDataaccessMapper;
import org.food.ordering.system.payment.service.dataaccess.creditentry.repository.CreditEntryJpaRepository;
import org.food.ordering.system.payment.service.domain.entity.Payment;
import org.ordering.system.payment.service.domain.ports.output.repository.PaymentRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CreditEntryRepositoryImpl implements PaymentRepository {
    private final CreditEntryJpaRepository creditEntryJpaRepository;
    private final CreditEntryDataaccessMapper creditEntryDataaccessMapper;

    public CreditEntryRepositoryImpl(CreditEntryJpaRepository creditEntryJpaRepository,
                                     CreditEntryDataaccessMapper creditEntryDataaccessMapper) {
        this.creditEntryJpaRepository = creditEntryJpaRepository;
        this.creditEntryDataaccessMapper = creditEntryDataaccessMapper;
    }


    @Override
    public Payment save(Payment payment) {
        return creditEntryDataaccessMapper
                .paymentEntityToPayment(creditEntryJpaRepository
                        .save(creditEntryDataaccessMapper.paymentToPaymentEntity(payment)));
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return creditEntryJpaRepository.findByOrderId(orderId)
                .map(creditEntryDataaccessMapper::paymentEntityToPayment);
    }
}
