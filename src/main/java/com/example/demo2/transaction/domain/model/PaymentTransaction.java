package com.example.demo2.transaction.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_account_id", nullable = false)
    private Long sourceAccountId;

    @Column(name = "destination_account_id", nullable = false)
    private Long destinationAccountId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TransactionType type;

    @Column(name = "status", nullable = false, length = 30)
    private PaymentTransactionStatus paymentTransactionStatus;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "paymentTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Refund> refunds = new ArrayList<>();

    private PaymentTransaction(
            Long sourceAccountId,
            Long destinationAccountId,
            BigDecimal amount,
            String currency,
            PaymentTransactionStatus paymentTransactionStatus,
            String errorMessage
    ) {
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
        this.type = TransactionType.TRANSFER;
        this.paymentTransactionStatus = paymentTransactionStatus;
        this.errorMessage = errorMessage;
        this.createdAt = LocalDateTime.now();
        this.completedAt = this.createdAt;
    }

    public static PaymentTransaction completedTransfer(
            Long sourceAccountId,
            Long destinationAccountId,
            BigDecimal amount,
            String currency
    ) {
        validateTransfer(sourceAccountId, destinationAccountId, amount, currency);
        return new PaymentTransaction(
                sourceAccountId,
                destinationAccountId,
                amount,
                currency,
                PaymentTransactionStatus.SUCCESS,
                null
        );
    }

    public static PaymentTransaction failedTransfer(
            Long sourceAccountId,
            Long destinationAccountId,
            BigDecimal amount,
            String currency,
            String errorMessage
    ) {
        validateTransfer(sourceAccountId, destinationAccountId, amount, currency);
        if (errorMessage == null || errorMessage.isBlank()) {
            throw new IllegalArgumentException("Сообщение об ошибке обязательно для неуспешной транзакции");
        }
        return new PaymentTransaction(
                sourceAccountId,
                destinationAccountId,
                amount,
                currency,
                PaymentTransactionStatus.FAILED,
                errorMessage
        );
    }

    private static void validateTransfer(
            Long sourceAccountId,
            Long destinationAccountId,
            BigDecimal amount,
            String currency
    ) {
        if (sourceAccountId == null) {
            throw new IllegalArgumentException("Счёт отправителя обязателен");
        }
        if (destinationAccountId == null) {
            throw new IllegalArgumentException("Счёт получателя обязателен");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма транзакции должна быть строго больше нуля");
        }
        if (currency == null || currency.length() != 3) {
            throw new IllegalArgumentException("Валюта должна быть в формате ISO-4217 (3 символа)");
        }
    }
}
