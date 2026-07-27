package com.example.demo2.transaction.presentation.controller;

import com.example.demo2.transaction.application.result.MoneyTransferResult;
import com.example.demo2.transaction.application.service.MoneyTransferService;
import com.example.demo2.transaction.presentation.dto.MoneyTransferRequest;
import com.example.demo2.transaction.presentation.dto.MoneyTransferResponse;
import com.example.demo2.transaction.presentation.mapper.TransactionDtoMapper;
import com.example.demo2.identity.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final MoneyTransferService moneyTransferService;
    private final TransactionDtoMapper mapper;

    @PostMapping
    public ResponseEntity<MoneyTransferResponse> transfer(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody MoneyTransferRequest request
    ) {
        MoneyTransferResult result = moneyTransferService.transfer(
                user.getId(),
                mapper.toCommand(request)
        );
        return ResponseEntity.ok(mapper.toResponse(result));
    }
}