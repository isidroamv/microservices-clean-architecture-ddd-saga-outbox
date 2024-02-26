package org.food.ordering.system.payment.service.dataaccess.credithistory.adpater;

import org.food.ordering.system.domain.valueobject.CustomerId;
import org.food.ordering.system.payment.service.dataaccess.credithistory.mapper.CreditHistoryDataaccessMapper;
import org.food.ordering.system.payment.service.dataaccess.credithistory.repository.CreditHistoryJpaRepository;
import org.food.ordering.system.payment.service.domain.entity.CreditEntry;
import org.food.ordering.system.payment.service.domain.entity.CreditHistory;
import org.ordering.system.payment.service.domain.ports.output.repository.CreditEntryRepository;
import org.ordering.system.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CreditHistoryRepositoryImpl implements CreditHistoryRepository {
    private final CreditHistoryJpaRepository creditHistoryJpaRepository;
    private final CreditHistoryDataaccessMapper creditHistoryDataaccessMapper;

    public CreditHistoryRepositoryImpl(CreditHistoryJpaRepository creditHistoryJpaRepository,
                                       CreditHistoryDataaccessMapper creditHistoryDataaccessMapper) {
        this.creditHistoryJpaRepository = creditHistoryJpaRepository;
        this.creditHistoryDataaccessMapper = creditHistoryDataaccessMapper;
    }


    @Override
    public CreditHistory save(CreditHistory creditHistory) {
        return creditHistoryDataaccessMapper
                .creditHistoryEntityToCreditHistory(creditHistoryJpaRepository
                        .save(creditHistoryDataaccessMapper.creditHistoryToCreditHistoryEntity(creditHistory)));
    }

    @Override
    public Optional<List<CreditHistory>> findByCustomerId(CustomerId customerId) {
        return creditHistoryJpaRepository.findByCustomerId(customerId.getValue())
                .map(creditHistoryEntities ->
                        creditHistoryEntities.stream()
                                .map(creditHistoryDataaccessMapper::creditHistoryEntityToCreditHistory)
                                .toList());
    }
}
