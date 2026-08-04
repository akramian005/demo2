package com.example.demo2.transaction.application.service;

import com.example.demo2.account.domain.model.BankAccount;
import com.example.demo2.account.domain.repository.BankAccountRepository;
import com.example.demo2.card.domain.exception.CardNotUsableException;
import com.example.demo2.card.domain.model.PaymentCard;
import com.example.demo2.card.domain.repository.PaymentCardRepository;
import com.example.demo2.transaction.application.command.TransferByCardCommand;
import com.example.demo2.transaction.application.result.MoneyTransferResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TransferByCardService {

    private final BankAccountRepository accountRepository;
    private final PaymentCardRepository paymentCardRepository;
    private final TransferExecutionService transferExecutionService;

    @Transactional
    public MoneyTransferResult transfer(Long currentUserId, TransferByCardCommand command) {
        String fromIban = normalizeIban(command.fromIban());
        String targetPan = normalizePan(command.targetPan());
        String currency = command.currency().trim().toUpperCase();

        BankAccount sourceAccount = accountRepository.findByIbanForUpdate(fromIban)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Счёт отправителя не найден"));

        if (!sourceAccount.getUserId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нельзя переводить деньги с чужого счёта");
        }

        PaymentCard targetCard = paymentCardRepository.findByPan(targetPan)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Карта получателя не найдена"));
        try {
            targetCard.ensureUsable();
        } catch (CardNotUsableException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }

        BankAccount destinationAccount = accountRepository.findByIbanForUpdate(targetCard.getAccount().getIban())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Счёт карты получателя не найден"));

        return transferExecutionService.transfer(sourceAccount, destinationAccount, command.amount(), currency);
    }

    private String normalizeIban(String iban) {
        return iban.replaceAll("\\s+", "").toUpperCase();
    }

    private String normalizePan(String pan) {
        return pan.replaceAll("\\s+", "");
    }
}
