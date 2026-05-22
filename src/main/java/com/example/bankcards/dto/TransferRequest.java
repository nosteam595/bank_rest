package com.example.bankcards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "Запрос на перевод средств между картами")
public record TransferRequest(
        @Schema(description = "ID карты списания",
                example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Укажите карту списания")
        Long fromCardId,

        @Schema(description = "ID карты зачисления",
                example = "2",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Укажите карту зачисления")
        Long toCardId,

        @Schema(description = "Сумма перевода", example = "500.00", minLength = 1)
        @NotNull(message = "Укажите сумму перевода")
        @DecimalMin(value = "0.01", message = "Сумма перевода должна быть больше нуля")
        BigDecimal amount
) {}
