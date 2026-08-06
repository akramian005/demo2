package com.example.demo2.payment.repository;

import com.example.demo2.payment.model.entity.BankAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BankAccount> findByIban(String iban);

    List<BankAccount> findAllByUserId(Long userId);

    boolean existsByAccountNumber(String accountNumber);

    boolean existsByIban(String iban);
}