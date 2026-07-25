package com.example.demo2.banking.presentation.controller.docs;

import com.example.demo2.banking.presentation.dto.AccountCheckResponse;
import com.example.demo2.banking.presentation.dto.AccountResponse;
import com.example.demo2.banking.presentation.dto.MoneyTransferRequest;
import com.example.demo2.banking.presentation.dto.MoneyTransferResponse;
import com.example.demo2.identity.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@Tag(name = "Банкинг", description = "Счета, проверка IBAN и переводы между счетами")
@SecurityRequirement(name = "bearerAuth")
public interface BankingApiDocs {

    @Operation(
            summary = "Мои счета",
            description = "Возвращает список банковских счетов текущего авторизованного пользователя"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список счетов успешно получен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    ResponseEntity<List<AccountResponse>> getMyAccounts(@AuthenticationPrincipal User user);

    @Operation(
            summary = "Проверить существование счёта по IBAN",
            description = "Используется перед переводом, чтобы убедиться, что счёт-получатель существует. "
                    + "Не раскрывает баланс и данные владельца — только факт существования, валюту и статус."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Проверка выполнена (счёт может как существовать, так и нет)")
    })
    ResponseEntity<AccountCheckResponse> checkAccount(
            @Parameter(description = "IBAN проверяемого счёта", example = "KG54DEMO0000000000000001")
            String iban
    );

    @Operation(
            summary = "Перевод денег между счетами",
            description = "Списывает сумму со счёта отправителя и зачисляет на счёт получателя. "
                    + "Счёт отправителя должен принадлежать текущему авторизованному пользователю."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Перевод выполнен успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса"),
            @ApiResponse(responseCode = "402", description = "Недостаточно средств на счёте отправителя"),
            @ApiResponse(responseCode = "403", description = "Счёт отправителя не принадлежит текущему пользователю"),
            @ApiResponse(responseCode = "404", description = "Счёт отправителя или получателя не найден")
    })
    ResponseEntity<MoneyTransferResponse> transfer(
            @AuthenticationPrincipal User user,
            @Valid MoneyTransferRequest request
    );
}
