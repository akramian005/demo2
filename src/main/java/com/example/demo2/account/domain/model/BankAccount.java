package com.example.demo2.account.domain.model;

import com.example.demo2.account.domain.exception.AccountNotActiveException;
import com.example.demo2.card.domain.exception.CurrencyMismatchException;
import com.example.demo2.account.domain.exception.InsufficientFundsException;
import com.example.demo2.account.domain.exception.InvalidAmountException;
import com.example.demo2.card.domain.model.PaymentCard;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "account_number", unique = true, nullable = false, length = 20)
    private String accountNumber;

    @Column(unique = true, nullable = false, length = 34)
    private String iban;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private PaymentCard card;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    private Long version;

    private BankAccount(Long userId, String accountNumber, String iban, String currency) {
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.iban = iban;
        this.currency = currency;
        this.balance = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        this.status = AccountStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    public static BankAccount open(Long userId, String accountNumber, String iban, String currency) {
        if (userId == null) {
            throw new IllegalArgumentException("userId обязателен для открытия счёта");
        }
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Номер счёта не может быть пустым");
        }
        if (iban == null || iban.isBlank()) {
            throw new IllegalArgumentException("IBAN не может быть пустым");
        }
        if (currency == null || currency.length() != 3) {
            throw new IllegalArgumentException("Валюта должна быть в формате ISO-4217 (3 символа)");
        }
        return new BankAccount(userId, accountNumber, iban, currency);
    }

    public void debit(BigDecimal amount, String amountCurrency) {
        checkAccountIsActive();
        BigDecimal normalized = validateAndNormalize(amount, amountCurrency);

        if (this.balance.compareTo(normalized) < 0) {
            throw new InsufficientFundsException(
                    "Недостаточно средств на счёте IBAN: " + this.iban
                            + ". Доступно: " + this.balance + " " + this.currency);
        }

        this.balance = this.balance.subtract(normalized);
    }

    public void credit(BigDecimal amount, String amountCurrency) {
        checkAccountIsActive();
        BigDecimal normalized = validateAndNormalize(amount, amountCurrency);
        this.balance = this.balance.add(normalized);
    }

    public void freeze() {
        if (this.status == AccountStatus.CLOSED) {
            throw new IllegalStateException("Нельзя заморозить уже закрытый счёт IBAN: " + this.iban);
        }
        this.status = AccountStatus.FROZEN;
    }

    public void activate() {
        if (this.status == AccountStatus.CLOSED) {
            throw new IllegalStateException("Нельзя активировать закрытый счёт IBAN: " + this.iban);
        }
        this.status = AccountStatus.ACTIVE;
    }

    /**
     * Закрытие счёта. Баланс должен быть РОВНО нулевым — как положительный
     * (клиент не забрал деньги), так и отрицательный (технический овердрафт/долг)
     * баланс не позволяет закрыть счёт.
     */
    public void close() {
        if (this.balance.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Нельзя закрыть счёт с ненулевым балансом ("
                    + this.balance + " " + this.currency + "). Баланс должен быть ровно 0.00");
        }
        this.status = AccountStatus.CLOSED;
    }

    public void linkCard(PaymentCard card) {
        this.card = card;
        card.linkToAccount(this);
    }

    private void checkAccountIsActive() {
        if (this.status != AccountStatus.ACTIVE) {
            throw new AccountNotActiveException(
                    "Операция отклонена. Счёт находится в статусе: " + this.status);
        }
    }

    private BigDecimal validateAndNormalize(BigDecimal amount, String amountCurrency) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Сумма транзакции должна быть строго больше нуля");
        }
        if (amountCurrency != null && !amountCurrency.equals(this.currency)) {
            throw new CurrencyMismatchException(
                    "Валюта операции (" + amountCurrency + ") не совпадает с валютой счёта (" + this.currency + ")");
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}