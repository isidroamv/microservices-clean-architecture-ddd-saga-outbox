package org.food.ordering.system.payment.service.dataaccess.creditentry.mapper;

import org.food.ordering.system.domain.valueobject.CustomerId;
import org.food.ordering.system.domain.valueobject.Money;
import org.food.ordering.system.domain.valueobject.OrderId;
import org.food.ordering.system.payment.service.dataaccess.creditentry.entity.CreditEntryEntity;
import org.food.ordering.system.payment.service.domain.entity.CreditEntry;
import org.food.ordering.system.payment.service.domain.entity.Payment;
import org.food.ordering.system.payment.service.domain.valueObject.CreditEntryId;
import org.food.ordering.system.payment.service.domain.valueObject.PaymentId;
import org.springframework.stereotype.Component;

@Component
public class CreditEntryDataaccessMapper {
    public CreditEntryEntity creditEntryToCreditEntryEntity(CreditEntry creditEntry) {
        return CreditEntryEntity.builder()
                .id(creditEntry.getId().getValue())
                .customerId(creditEntry.getCustomerId().getValue())
                .totalCreditAmount(creditEntry.getTotalCreditAmount().getAmount())
                .build();
    }

    public CreditEntry creditEntryEntityToCreditEntry(CreditEntryEntity creditEntryEntity) {
        return CreditEntry.builder()
                .creditEntryId(new CreditEntryId(creditEntryEntity.getId()))
                .customerId(new CustomerId(creditEntryEntity.getCustomerId()))
                .totalCreditAmount(new Money(creditEntryEntity.getTotalCreditAmount()))
                .build();
    }
}
