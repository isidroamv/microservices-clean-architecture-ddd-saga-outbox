package org.food.ordering.system.payment.service.dataaccess.credithistory.mapper;

import org.food.ordering.system.domain.valueobject.CustomerId;
import org.food.ordering.system.domain.valueobject.Money;
import org.food.ordering.system.payment.service.dataaccess.credithistory.entity.CreditHistoryEntity;
import org.food.ordering.system.payment.service.domain.entity.CreditEntry;
import org.food.ordering.system.payment.service.domain.entity.CreditHistory;
import org.food.ordering.system.payment.service.domain.valueObject.CreditEntryId;
import org.food.ordering.system.payment.service.domain.valueObject.CreditHistoryId;
import org.springframework.stereotype.Component;

@Component
public class CreditHistoryDataaccessMapper {
    public CreditHistory creditHistoryEntityToCreditHistory(CreditHistoryEntity creditHistoryEntity) {
        return CreditHistory.builder()
                .creditHistoryId(new CreditHistoryId(creditHistoryEntity.getId()))
                .customerId(new CustomerId(creditHistoryEntity.getCustomerId()))
                .amount(new Money(creditHistoryEntity.getAmount()))
                .transactionType(creditHistoryEntity.getType())
                .build();
    }

    public CreditHistoryEntity creditHistoryToCreditHistoryEntity(CreditHistory creditHistory) {
        return CreditHistoryEntity.builder()
                .id(creditHistory.getId().getValue())
                .customerId(creditHistory.getCustomerId().getValue())
                .amount(creditHistory.getAmount().getAmount())
                .type(creditHistory.getTransactionType())
                .build();
    }
}
