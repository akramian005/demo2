package com.example.demo2.card.domain.model;

import com.example.demo2.account.domain.model.BankAccount;
import com.example.demo2.card.domain.exception.CardNotUsableException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_cards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // только для JPA
public class PaymentCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Храним только маскированный номер, полный PAN не персистим (PCI DSS).
    // В реальной системе полный номер живёт у платёжного процессора/токенизатора,
    // здесь для учебного проекта храним только последние 4 цифры.
    @Column(name = "last_four_digits", nullable = false, length = 4)
    private String lastFourDigits;

    @Column(nullable = false, length = 100)
    private String cardholderName;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    // CVV НЕ хранится вообще — ни в открытом виде, ни в хэше.
    // Он проверяется только в момент авторизации операции и никогда не сохраняется в БД.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CardStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", unique = true, nullable = false)
    private BankAccount account;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private PaymentCard(String lastFourDigits, String cardholderName, LocalDate expirationDate) {
        this.lastFourDigits = lastFourDigits;
        this.cardholderName = cardholderName;
        this.expirationDate = expirationDate;
        this.status = CardStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Выпуск новой карты. Полный номер карты генерируется/выдаётся инфраструктурой
     * (платёжным процессором) и сюда не попадает — только последние 4 цифры для отображения.
     */
    public static PaymentCard issue(String fullCardNumber, String cardholderName, LocalDate expirationDate) {
        if (fullCardNumber == null || !fullCardNumber.matches("\\d{16}")) {
            throw new IllegalArgumentException("Номер карты должен состоять ровно из 16 цифр");
        }
        if (cardholderName == null || cardholderName.isBlank()) {
            throw new IllegalArgumentException("Имя держателя карты обязательно");
        }
        if (expirationDate == null || expirationDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Некорректный срок действия карты");
        }
        String lastFour = fullCardNumber.substring(12);
        return new PaymentCard(lastFour, cardholderName, expirationDate);
    }

    /**
     * Проверка, можно ли проводить операцию по карте.
     */
    public boolean isUsable() {
        return this.status == CardStatus.ACTIVE && !LocalDate.now().isAfter(this.expirationDate);
    }

    /**
     * Бросает исключение, если картой сейчас нельзя пользоваться —
     * удобно вызывать перед списанием в application-слое.
     */
    public void ensureUsable() {
        if (!isUsable()) {
            throw new CardNotUsableException("Карта недоступна для операций. Статус: " + status
                    + ", срок действия: " + expirationDate);
        }
    }

    public void block() {
        this.status = CardStatus.BLOCKED;
    }

    public void activate() {
        if (LocalDate.now().isAfter(this.expirationDate)) {
            throw new IllegalStateException("Нельзя активировать карту с истёкшим сроком действия");
        }
        this.status = CardStatus.ACTIVE;
    }

    /**
     * Маскированный номер для отображения в UI, например "**** **** **** 1111".
     */
    public String getMaskedNumber() {
        return "**** **** **** " + lastFourDigits;
    }

    /**
     * Устанавливает владеющую сторону связи с BankAccount.
     * Вызывается из BankAccount.linkCard() при выпуске/привязке карты к счёту —
     * это единственная точка входа для установки этой связи.
     */
    public void linkToAccount(BankAccount account) {
        this.account = account;
    }
}