package com.example.demo2.account.presentation.controller;

import com.example.demo2.account.application.service.AccountQueryService;
import com.example.demo2.account.presentation.dto.AccountCheckResponse;
import com.example.demo2.account.presentation.dto.AccountResponse;
import com.example.demo2.account.presentation.mapper.AccountDtoMapper;
import com.example.demo2.identity.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountQueryService accountQueryService;
    private final AccountDtoMapper mapper;

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getMyAccounts(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(accountQueryService.getUserAccounts(user.getId()).stream()
                .map(AccountResponse::from)
                .toList());
    }

    @GetMapping("/check/{iban}")
    public ResponseEntity<AccountCheckResponse> checkAccount(@PathVariable String iban) {
        return ResponseEntity.ok(accountQueryService.findByIban(iban)
                .map(mapper::toCheckResponse)
                .orElseGet(() -> new AccountCheckResponse(false, iban, null, null)));
    }
}