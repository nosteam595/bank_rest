package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public record CardCreateRequest(
        @NotNull(message = "Необходимо указать ID владельца карты")
        Long userId,

        @NotBlank(message = "Номер карты обязателен")
        @Pattern(regexp = "^\\d{16}$", message = "Номер карты должен состоять строго из 16 цифр")
        String cardNumber,

        @NotBlank(message = "Срок действия обязателен")
        @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Срок действия должен быть в формате MM/YY")
        String expirationDate,

        @NotNull(message = "Начальный баланс обязателен")
        BigDecimal initialBalance
) {}
