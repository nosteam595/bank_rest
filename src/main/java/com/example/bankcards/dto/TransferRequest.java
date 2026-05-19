package com.example.bankcards.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TransferRequest(
        @NotNull(message = "Укажите карту списания")
        Long fromCardId,

        @NotNull(message = "Укажите карту зачисления")
        Long toCardId,

        @NotNull(message = "Укажите сумму перевода")
        @DecimalMin(value = "0.01", message = "Сумма перевода должна быть больше нуля")
        BigDecimal amount
) {}
