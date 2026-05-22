package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public record CardCreateRequest(
        @Schema(description = "ID пользователя",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Необходимо указать ID владельца карты")
        Long userId,

        @Schema(description = "Номер новой карты",
                example = "0711150127193831",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Номер карты обязателен")
        @Pattern(regexp = "^\\d{16}$", message = "Номер карты должен состоять строго из 16 цифр")
        String cardNumber,

        @Schema(description = "Срок действия карты",
                example = "11/27",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Срок действия обязателен")
        @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Срок действия должен быть в формате MM/YY")
        String expirationDate,

        @Schema(description = "Начальный баланс карты",
                example = "5000",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Начальный баланс обязателен")
        BigDecimal initialBalance
) {}
