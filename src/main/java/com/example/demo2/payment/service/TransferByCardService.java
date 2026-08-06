package com.example.demo2.payment.service;

import com.example.demo2.payment.dto.MoneyTransferResult;
import com.example.demo2.payment.dto.TransferByCardRequest;
import com.example.demo2.payment.exception.CardNotUsableException;
import com.example.demo2.payment.model.entity.BankAccount;
import com.example.demo2.payment.model.entity.PaymentCard;
import com.example.demo2.payment.repository.BankAccountRepository;
import com.example.demo2.payment.repository.PaymentCardRepository;
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
    public MoneyTransferResult transfer(Long currentUserId, TransferByCardRequest request) {
        String fromIban = normalizeIban(request.getFromIban());
        String targetPan = normalizePan(request.getTargetPan());
        String currency = request.getCurrency().trim().toUpperCase();

        BankAccount sourceAccount = accountRepository.findByIban(fromIban)
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

        BankAccount destinationAccount = accountRepository.findByIban(targetCard.getAccount().getIban())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Счёт карты получателя не найден"));

        return transferExecutionService.transfer(
                sourceAccount,
                destinationAccount,
                request.getAmount(),
                currency
        );
    }

    private String normalizeIban(String iban) {
        return iban.replaceAll("\\s+", "").toUpperCase();
    }

    private String normalizePan(String pan) {
        return pan.replaceAll("\\s+", "");
    }
}