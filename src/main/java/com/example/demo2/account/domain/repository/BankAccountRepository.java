package com.example.demo2.account.domain.repository;

import com.example.demo2.account.domain.model.BankAccount;

import java.util.List;
import java.util.Optional;

/**
 * Порт (интерфейс) для доступа к BankAccount.
 * Реализация (адаптер) находится в banking.infrastructure — там же,
 * где Spring Data JPA. Сам интерфейс не зависит ни от Spring, ни от JPA,
 * чтобы domain-слой оставался "чистой Java".
 */
public interface BankAccountRepository {

    BankAccount save(BankAccount account);

    Optional<BankAccount> findById(Long id);

    Optional<BankAccount> findByIban(String iban);

    Optional<BankAccount> findByIbanForUpdate(String iban);

    Optional<BankAccount> findByAccountNumber(String accountNumber);

    List<BankAccount> findAllByUserId(Long userId);

    boolean existsByAccountNumber(String accountNumber);

    boolean existsByIban(String iban);

    long count();
}
