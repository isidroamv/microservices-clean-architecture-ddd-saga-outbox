package org.food.ordering.system.payment.service.dataaccess.credithistory.repository;

import org.food.ordering.system.payment.service.dataaccess.credithistory.entity.CreditEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreditEntryJpaRepository extends JpaRepository<CreditEntryEntity, UUID> {
    Optional<CreditEntryEntity> findByOrderId(UUID customerId);
}