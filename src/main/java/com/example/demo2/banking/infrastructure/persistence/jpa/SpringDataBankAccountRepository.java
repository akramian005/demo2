package com.example.demo2.banking.infrastructure.persistence.jpa;

import com.example.demo2.banking.domain.model.BankAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataBankAccountRepository extends JpaRepository<BankAccount, Long> {

    Optional<BankAccount> findByIban(String iban);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select account from BankAccount account where account.iban = :iban")
    Optional<BankAccount> findByIbanForUpdate(@Param("iban") String iban);

    Optional<BankAccount> findByAccountNumber(String accountNumber);

    List<BankAccount> findAllByUserId(Long userId);

    boolean existsByAccountNumber(String accountNumber);

    boolean existsByIban(String iban);
}
