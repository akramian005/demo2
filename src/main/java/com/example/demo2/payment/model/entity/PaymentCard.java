package com.example.demo2.payment.model.entity;

import com.example.demo2.payment.model.enums.CardStatus;
import com.example.demo2.payment.exception.CardNotUsableException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Table(name = "payment_cards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Полный номер карты (PAN).
     *
     * Учебный проект:
     * хранится в открытом виде для реализации переводов по номеру карты.
     *
     * В реальной банковской системе PAN хранится в зашифрованном виде
     * либо вынесен в отдельное защищённое хранилище.
     */
    @Column(nullable = false, unique = true, length = 16)
    private String pan;

    /**
     * Последние четыре цифры карты для отображения.
     */
    @Column(name = "last_four_digits", nullable = false, length = 4)
    private String lastFourDigits;

    @Column(nullable = false, length = 100)
    private String cardholderName;

    /**
     * Срок действия карты.
     * Хранится только месяц и год.
     */
    @Column(name = "expiration_date", nullable = false)
    private YearMonth expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CardStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", unique = true, nullable = false)
    private BankAccount account;

    public void linkToAccount(BankAccount account) {
        this.account = account;
    }

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private PaymentCard(
            String pan,
            String lastFourDigits,
            String cardholderName,
            YearMonth expirationDate
    ) {
        this.pan = pan;
        this.lastFourDigits = lastFourDigits;
        this.cardholderName = cardholderName;
        this.expirationDate = expirationDate;
        this.status = CardStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public static PaymentCard issue(
            String pan,
            String cardholderName,
            YearMonth expirationDate
    ) {

        if (pan == null || !pan.matches("\\d{16}")) {
            throw new IllegalArgumentException("Номер карты должен состоять ровно из 16 цифр");
        }

        if (cardholderName == null || cardholderName.isBlank()) {
            throw new IllegalArgumentException("Имя держателя карты обязательно");
        }

        if (expirationDate == null || expirationDate.isBefore(YearMonth.now())) {
            throw new IllegalArgumentException("Некорректный срок действия карты");
        }

        String lastFour = pan.substring(12);

        return new PaymentCard(
                pan,
                lastFour,
                cardholderName,
                expirationDate
        );
    }

    public boolean isUsable() {
        return status == CardStatus.ACTIVE
                && !YearMonth.now().isAfter(expirationDate);
    }

    public void ensureUsable() {
        if (!isUsable()) {
            throw new CardNotUsableException(
                    "Карта недоступна для операций. Статус: "
                            + status + ", срок действия: " + expirationDate
            );
        }
    }

//    public void block() {
//        this.status = CardStatus.BLOCKED;
//    }
//
//    public void activate() {
//        if (YearMonth.now().isAfter(expirationDate)) {
//            throw new IllegalStateException(
//                    "Нельзя активировать карту с истёкшим сроком действия");
//        }
//
//        this.status = CardStatus.ACTIVE;
//    }

//    public String getMaskedNumber() {
//        return "**** **** **** " + lastFourDigits;
//    }


}