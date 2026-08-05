package com.example.demo2.payment.model.entity;

import com.example.demo2.payment.model.enums.RefundStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "refunds")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_transaction_id", nullable = false)
    private PaymentTransaction paymentTransaction;

    @Column(name = "refund_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal refundAmount;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false, length = 30)
    private RefundStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private Refund(PaymentTransaction paymentTransaction, BigDecimal refundAmount, String reason, RefundStatus status) {
        this.paymentTransaction = paymentTransaction;
        this.refundAmount = refundAmount.setScale(2, RoundingMode.HALF_UP);
        this.reason = reason;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public static Refund completed(PaymentTransaction paymentTransaction, BigDecimal refundAmount, String reason) {
        return create(paymentTransaction, refundAmount, reason, RefundStatus.COMPLETED);
    }

    public static Refund failed(PaymentTransaction paymentTransaction, BigDecimal refundAmount, String reason) {
        return create(paymentTransaction, refundAmount, reason, RefundStatus.FAILED);
    }

    private static Refund create(
            PaymentTransaction paymentTransaction,
            BigDecimal refundAmount,
            String reason,
            RefundStatus status
    ) {
        if (paymentTransaction == null) {
            throw new IllegalArgumentException("Транзакция обязательна для возврата");
        }
        if (refundAmount == null || refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма возврата должна быть строго больше нуля");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Причина возврата обязательна");
        }
        return new Refund(paymentTransaction, refundAmount, reason, status);
    }
}
