package org.food.ordering.system.payment.service.dataaccess.creditentry.adpater;

import org.food.ordering.system.domain.valueobject.CustomerId;
import org.food.ordering.system.payment.service.dataaccess.creditentry.mapper.CreditEntryDataaccessMapper;
import org.food.ordering.system.payment.service.dataaccess.creditentry.repository.CreditEntryJpaRepository;
import org.food.ordering.system.payment.service.domain.entity.CreditEntry;
import org.food.ordering.system.payment.service.domain.entity.Payment;
import org.ordering.system.payment.service.domain.ports.output.repository.CreditEntryRepository;
import org.ordering.system.payment.service.domain.ports.output.repository.PaymentRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CreditEntryRepositoryImpl implements CreditEntryRepository {
    private final CreditEntryJpaRepository creditEntryJpaRepository;
    private final CreditEntryDataaccessMapper creditEntryDataaccessMapper;

    public CreditEntryRepositoryImpl(CreditEntryJpaRepository creditEntryJpaRepository,
                                     CreditEntryDataaccessMapper creditEntryDataaccessMapper) {
        this.creditEntryJpaRepository = creditEntryJpaRepository;
        this.creditEntryDataaccessMapper = creditEntryDataaccessMapper;
    }


    @Override
    public CreditEntry save(CreditEntry creditEntry) {
        return creditEntryDataaccessMapper
                .creditEntryEntityToCreditEntry(creditEntryJpaRepository
                        .save(creditEntryDataaccessMapper.creditEntryToCreditEntryEntity(creditEntry)));
    }

    @Override
    public Optional<CreditEntry> findByCustomerId(CustomerId customerId) {
        return creditEntryJpaRepository.findByOrderId(customerId.getValue())
                .map(creditEntryDataaccessMapper::creditEntryEntityToCreditEntry);
    }

}
