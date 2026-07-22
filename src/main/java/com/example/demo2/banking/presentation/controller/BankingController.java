package com.example.demo2.banking.presentation.controller;

import com.example.demo2.banking.application.command.TransferMoneyCommand;
import com.example.demo2.banking.application.result.MoneyTransferResult;
import com.example.demo2.banking.application.service.AccountQueryService;
import com.example.demo2.banking.application.service.MoneyTransferService;
import com.example.demo2.banking.domain.model.BankAccount;
import com.example.demo2.banking.presentation.dto.AccountCheckResponse;
import com.example.demo2.banking.presentation.dto.AccountResponse;
import com.example.demo2.banking.presentation.dto.MoneyTransferRequest;
import com.example.demo2.banking.presentation.dto.MoneyTransferResponse;
import com.example.demo2.identity.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/banking")
@RequiredArgsConstructor
public class BankingController {

    private final AccountQueryService accountQueryService;
    private final MoneyTransferService moneyTransferService;

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponse>> getMyAccounts(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountQueryService.getUserAccounts(user.getId()).stream()
                .map(AccountResponse::from)
                .toList());
    }

    @GetMapping("/accounts/check/{iban}")
    public ResponseEntity<AccountCheckResponse> checkAccount(@PathVariable String iban) {
        return ResponseEntity.ok(accountQueryService.findByIban(iban)
                .map(this::toCheckResponse)
                .orElseGet(() -> new AccountCheckResponse(false, iban, null, null)));
    }

    @PostMapping("/transfers")
    public ResponseEntity<MoneyTransferResponse> transfer(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody MoneyTransferRequest request
    ) {
        MoneyTransferResult result = moneyTransferService.transfer(user.getId(), toCommand(request));
        return ResponseEntity.ok(toResponse(result));
    }

    private TransferMoneyCommand toCommand(MoneyTransferRequest request) {
        return new TransferMoneyCommand(
                request.getFromIban(),
                request.getToIban(),
                request.getAmount(),
                request.getCurrency()
        );
    }

    private MoneyTransferResponse toResponse(MoneyTransferResult result) {
        return new MoneyTransferResponse(
                result.fromIban(),
                result.toIban(),
                result.amount(),
                result.currency(),
                result.senderBalance()
        );
    }

    private AccountCheckResponse toCheckResponse(BankAccount account) {
        return new AccountCheckResponse(
                true,
                account.getIban(),
                account.getCurrency(),
                account.getStatus().name()
        );
    }
}
