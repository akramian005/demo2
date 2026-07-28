package com.example.demo2.identity.controller.docs;

import com.example.demo2.identity.dto.AuthResponse;
import com.example.demo2.identity.dto.LoginRequest;
import com.example.demo2.identity.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "Регистрация и аутентификация пользователей")
public interface AuthApiDocs {

    @Operation(summary = "Регистрация нового пользователя",
            description = "Создаёт нового пользователя и возвращает JWT-токен. " +
                    "Обязательны email и пароль. Имя, фамилия и телефон можно передать сразу, но они необязательны. " +
                    "После регистрации автоматически публикуется событие UserRegisteredEvent, " +
                    "по которому создаются связанные сущности в других модулях (аккаунт, корзина и т.д.)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                      "email": "user@example.com",
                                      "role": "USER"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации (некорректный email, короткий пароль)",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "email": "Некорректный формат email",
                              "password": "Пароль минимум 8 символов"
                            }
                            """))),
            @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует")
    })
    ResponseEntity<AuthResponse> register(@Valid RegisterRequest request);

    @Operation(summary = "Вход в систему",
            description = "Проверяет email и пароль, возвращает JWT-токен при успешной аутентификации")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешный вход",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                      "email": "user@example.com",
                                      "role": "USER"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Неверный email или пароль")
    })
    ResponseEntity<AuthResponse> login(@Valid LoginRequest request);
}
