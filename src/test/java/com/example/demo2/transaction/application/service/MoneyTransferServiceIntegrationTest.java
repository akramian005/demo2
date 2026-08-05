package com.example.demo2.transaction.application.service;

import com.example.demo2.payment.service.MoneyTransferService;
import com.example.demo2.payment.service.OpenAccountService;
import com.example.demo2.payment.service.TransferByCardService;
import com.example.demo2.payment.service.TransferByPhoneService;
import com.example.demo2.payment.dto.TransferByIbanCommand;
import com.example.demo2.payment.dto.TransferByCardCommand;
import com.example.demo2.payment.dto.TransferByPhoneCommand;
import com.example.demo2.payment.model.entity.BankAccount;
import com.example.demo2.payment.repository.BankAccountRepository;
import com.example.demo2.identity.entity.Role;
import com.example.demo2.identity.entity.User;
import com.example.demo2.identity.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MoneyTransferServiceIntegrationTest {

    @Autowired
    private MoneyTransferService moneyTransferService;

    @Autowired
    private TransferByPhoneService transferByPhoneService;

    @Autowired
    private TransferByCardService transferByCardService;

    @Autowired
    private OpenAccountService openAccountService;

    @Autowired
    private BankAccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void transferMovesMoneyBetweenAccounts() {
        User sender = saveUser("sender");
        User receiver = saveUser("receiver");
        BankAccount senderAccount = openAccountService.open(sender.getId(), "KGS");
        BankAccount receiverAccount = openAccountService.open(receiver.getId(), "KGS");
        senderAccount.credit(new BigDecimal("100.00"), "KGS");
        accountRepository.save(senderAccount);

        TransferByIbanCommand command = transferCommand(
                senderAccount.getIban(),
                receiverAccount.getIban(),
                "25.50",
                "KGS"
        );

        moneyTransferService.transfer(sender.getId(), command);

        BankAccount updatedSender = accountRepository.findByIban(senderAccount.getIban()).orElseThrow();
        BankAccount updatedReceiver = accountRepository.findByIban(receiverAccount.getIban()).orElseThrow();

        assertThat(updatedSender.getBalance()).isEqualByComparingTo("74.50");
        assertThat(updatedReceiver.getBalance()).isEqualByComparingTo("25.50");
    }

    @Test
    void transferRejectsForeignSenderAccount() {
        User owner = saveUser("owner");
        User attacker = saveUser("attacker");
        User receiver = saveUser("receiver");
        BankAccount ownerAccount = openAccountService.open(owner.getId(), "KGS");
        BankAccount receiverAccount = openAccountService.open(receiver.getId(), "KGS");

        TransferByIbanCommand command = transferCommand(
                ownerAccount.getIban(),
                receiverAccount.getIban(),
                "10.00",
                "KGS"
        );

        assertThatThrownBy(() -> moneyTransferService.transfer(attacker.getId(), command))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("403 FORBIDDEN");
    }

    @Test
    void transferByPhoneMovesMoneyToReceiversCurrencyAccount() {
        User sender = saveUser("sender");
        User receiver = saveUser("receiver");
        String receiverPhone = "+996" + String.valueOf(System.nanoTime()).substring(0, 9);
        receiver.setPhone(receiverPhone);
        userRepository.save(receiver);

        BankAccount senderAccount = openAccountService.open(sender.getId(), "KGS");
        BankAccount receiverAccount = openAccountService.open(receiver.getId(), "KGS");
        senderAccount.credit(new BigDecimal("100.00"), "KGS");
        accountRepository.save(senderAccount);

        transferByPhoneService.transfer(
                sender.getId(),
                new TransferByPhoneCommand(senderAccount.getIban(), receiverPhone, new BigDecimal("15.00"), "KGS")
        );

        BankAccount updatedSender = accountRepository.findByIban(senderAccount.getIban()).orElseThrow();
        BankAccount updatedReceiver = accountRepository.findByIban(receiverAccount.getIban()).orElseThrow();

        assertThat(updatedSender.getBalance()).isEqualByComparingTo("85.00");
        assertThat(updatedReceiver.getBalance()).isEqualByComparingTo("15.00");
    }

    @Test
    void transferByCardMovesMoneyToCardAccount() {
        User sender = saveUser("sender");
        User receiver = saveUser("receiver");
        BankAccount senderAccount = openAccountService.open(sender.getId(), "KGS");
        BankAccount receiverAccount = openAccountService.open(receiver.getId(), "KGS");
        senderAccount.credit(new BigDecimal("100.00"), "KGS");
        accountRepository.save(senderAccount);

        transferByCardService.transfer(
                sender.getId(),
                new TransferByCardCommand(senderAccount.getIban(), receiverAccount.getCard().getPan(), new BigDecimal("20.00"), "KGS")
        );

        BankAccount updatedSender = accountRepository.findByIban(senderAccount.getIban()).orElseThrow();
        BankAccount updatedReceiver = accountRepository.findByIban(receiverAccount.getIban()).orElseThrow();

        assertThat(updatedSender.getBalance()).isEqualByComparingTo("80.00");
        assertThat(updatedReceiver.getBalance()).isEqualByComparingTo("20.00");
    }

    private User saveUser(String prefix) {
        User user = new User();
        user.setFirstName(prefix);
        user.setLastName("User");
        user.setEmail(prefix + "-" + UUID.randomUUID() + "@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    private TransferByIbanCommand transferCommand(String fromIban, String toIban, String amount, String currency) {
        return new TransferByIbanCommand(fromIban, toIban, new BigDecimal(amount), currency);
    }
}
