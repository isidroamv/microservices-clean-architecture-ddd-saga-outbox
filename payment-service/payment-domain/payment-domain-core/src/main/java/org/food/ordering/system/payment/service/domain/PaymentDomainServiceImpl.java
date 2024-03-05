package org.food.ordering.system.payment.service.domain;

import lombok.extern.slf4j.Slf4j;
import org.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import org.food.ordering.system.domain.valueobject.Money;
import org.food.ordering.system.domain.valueobject.PaymentStatus;
import org.food.ordering.system.payment.service.domain.entity.CreditEntry;
import org.food.ordering.system.payment.service.domain.entity.CreditHistory;
import org.food.ordering.system.payment.service.domain.entity.Payment;
import org.food.ordering.system.payment.service.domain.event.PaymentCancelledEvent;
import org.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import org.food.ordering.system.payment.service.domain.event.PaymentEvent;
import org.food.ordering.system.payment.service.domain.event.PaymentFailedEvent;
import org.food.ordering.system.payment.service.domain.valueObject.CreditHistoryId;
import org.food.ordering.system.payment.service.domain.valueObject.TransactionType;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.food.ordering.system.domain.DomainConstants.UTC;

@Slf4j
public class PaymentDomainServiceImpl implements PaymentDomainService {

    public PaymentEvent validateAndInitiatePayment(
            Payment payment, CreditEntry creditEntry,
            List<CreditHistory> creditHistories,
            List<String> failureMessages,
            DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher,
            DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher) {
        payment.validatePayment(failureMessages);
        payment.initializePayment();
        validateCreditEntry(payment, creditEntry, failureMessages);
        subtractCreditEntry(creditEntry, payment);
        updateCreditHistory(creditHistories, payment, TransactionType.DEBIT);
        validateCreditHistory(creditEntry, creditHistories, failureMessages);

        if (failureMessages.isEmpty()) {
            log.info("Payment initiated successfully");
            payment.updatePaymentStatus(PaymentStatus.COMPLETED);
            return new PaymentCompletedEvent(
                    payment, ZonedDateTime.now(ZoneId.of(UTC)), paymentCompletedEventDomainEventPublisher);
        } else {
            log.error("Payment initiation failed with failure message: {}", String.join(", ", failureMessages));
            payment.updatePaymentStatus(PaymentStatus.FAILED);
            return new PaymentFailedEvent(payment, ZonedDateTime.now(
                    ZoneId.of(UTC)), failureMessages, paymentFailedEventDomainEventPublisher);
        }
    }

    public PaymentEvent validateAndCancelPayment(
            Payment payment,
            CreditEntry creditEntry,
            List<CreditHistory> creditHistories,
            List<String> failureMessages,
            DomainEventPublisher<PaymentCancelledEvent> paymentCancelledEventDomainEventPublisher,
            DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher) {
        payment.validatePayment(failureMessages);
        addCreditEntry(creditEntry, payment);
        updateCreditHistory(creditHistories, payment, TransactionType.CREDIT);

        if (failureMessages.isEmpty()) {
            log.info("Payment cancelled successfully");
            payment.updatePaymentStatus(PaymentStatus.CANCELLED);
            return new PaymentCancelledEvent(payment, ZonedDateTime.now(ZoneId.of(UTC)), paymentCancelledEventDomainEventPublisher);
        } else {
            log.error("Payment cancellation failed");
            payment.updatePaymentStatus(PaymentStatus.FAILED);
            return new PaymentFailedEvent(payment, ZonedDateTime.now(ZoneId.of(UTC)), failureMessages, paymentFailedEventDomainEventPublisher);
        }
    }

    private void validateCreditEntry(Payment payment, CreditEntry creditEntry, List<String> failureMessages) {
        if (payment.getPrice().isGreaterThan(creditEntry.getTotalCreditAmount())) {
            log.error("Insufficient credit balance");
            failureMessages.add("Insufficient credit balance");
        }
    }

    private void subtractCreditEntry(CreditEntry creditEntry, Payment payment) {
        creditEntry.subtractCreditAmount(payment.getPrice());
    }

    private void updateCreditHistory(List<CreditHistory> creditHistories,
                                     Payment payment,
                                     TransactionType transactionType) {

        creditHistories.add(CreditHistory.builder()
                .creditHistoryId(new CreditHistoryId(UUID.randomUUID()))
                .customerId(payment.getCustomerId())
                .amount(payment.getPrice())
                .transactionType(transactionType)
                .build());

    }

    private void validateCreditHistory(CreditEntry creditEntry,
                                       List<CreditHistory> creditHistories,
                                       List<String> failureMessages) {

        Money totalCreditHistory = getTotalCreditAmount(creditHistories, TransactionType.CREDIT);
        Money totalDebitHistory = getTotalCreditAmount(creditHistories, TransactionType.DEBIT);

        if (totalDebitHistory.isGreaterThan(totalCreditHistory)) {
            log.error("Insufficient credit history");
            failureMessages.add("Insufficient credit history");
        }

        if (!creditEntry.getTotalCreditAmount().equals(totalCreditHistory.subtract(totalDebitHistory))) {
            log.error("Credit history mismatch");
            failureMessages.add("Credit history mismatch");
        }

    }

    private Money getTotalCreditAmount(List<CreditHistory> creditHistories, TransactionType transactionType) {
        return creditHistories.stream()
                .filter(creditHistory -> transactionType.equals(creditHistory.getTransactionType()))
                .map(creditHistory -> creditHistory.getAmount())
                .reduce(Money.ZERO, Money::add);
    }

    private void addCreditEntry(CreditEntry creditEntry, Payment payment) {
        creditEntry.addCreditAmount(payment.getPrice());
    }
}
