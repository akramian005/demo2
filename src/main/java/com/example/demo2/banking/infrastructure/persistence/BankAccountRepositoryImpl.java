package com.example.demo2.banking.infrastructure.persistence;

import com.example.demo2.banking.domain.model.BankAccount;
import com.example.demo2.banking.domain.repository.BankAccountRepository;
import com.example.demo2.banking.infrastructure.persistence.jpa.SpringDataBankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BankAccountRepositoryImpl implements BankAccountRepository {

    private final SpringDataBankAccountRepository springDataRepository;

    @Override
    public BankAccount save(BankAccount account) {
        return springDataRepository.save(account);
    }

    @Override
    public Optional<BankAccount> findById(Long id) {
        return springDataRepository.findById(id);
    }

    @Override
    public Optional<BankAccount> findByIban(String iban) {
        return springDataRepository.findByIban(iban);
    }

    @Override
    public Optional<BankAccount> findByIbanForUpdate(String iban) {
        return springDataRepository.findByIbanForUpdate(iban);
    }

    @Override
    public Optional<BankAccount> findByAccountNumber(String accountNumber) {
        return springDataRepository.findByAccountNumber(accountNumber);
    }

    @Override
    public List<BankAccount> findAllByUserId(Long userId) {
        return springDataRepository.findAllByUserId(userId);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return springDataRepository.existsByAccountNumber(accountNumber);
    }

    @Override
    public boolean existsByIban(String iban) {
        return springDataRepository.existsByIban(iban);
    }

    @Override
    public long count() {
        return springDataRepository.count();
    }
}
