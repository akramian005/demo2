package com.example.demo2.transaction.presentation.controller;

import com.example.demo2.transaction.application.result.MoneyTransferResult;
import com.example.demo2.transaction.application.service.MoneyTransferService;
import com.example.demo2.transaction.application.service.TransferByCardService;
import com.example.demo2.transaction.application.service.TransferByPhoneService;
import com.example.demo2.transaction.presentation.dto.MoneyTransferResponse;
import com.example.demo2.transaction.presentation.dto.TransferByCardRequest;
import com.example.demo2.transaction.presentation.dto.TransferByIbanRequest;
import com.example.demo2.transaction.presentation.dto.TransferByPhoneRequest;
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
    private final TransferByPhoneService transferByPhoneService;
    private final TransferByCardService transferByCardService;
    private final TransactionDtoMapper mapper;

    @PostMapping("/iban")
    public ResponseEntity<MoneyTransferResponse> transferByIban(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TransferByIbanRequest request
    ) {
        MoneyTransferResult result = moneyTransferService.transfer(
                user.getId(),
                mapper.toCommand(request)
        );
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @PostMapping("/phone")
    public ResponseEntity<MoneyTransferResponse> transferByPhone(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TransferByPhoneRequest request
    ) {
        MoneyTransferResult result = transferByPhoneService.transfer(
                user.getId(),
                mapper.toCommand(request)
        );
        return ResponseEntity.ok(mapper.toResponse(result));
    }

    @PostMapping("/card")
    public ResponseEntity<MoneyTransferResponse> transferByCard(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TransferByCardRequest request
    ) {
        MoneyTransferResult result = transferByCardService.transfer(
                user.getId(),
                mapper.toCommand(request)
        );
        return ResponseEntity.ok(mapper.toResponse(result));
    }
}
