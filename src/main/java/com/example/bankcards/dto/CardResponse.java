package com.example.bankcards.dto;

import com.example.bankcards.util.CardStatus;
import java.math.BigDecimal;

public record CardResponse(
        Long id,
        Long userId,
        String maskedCardNumber,
        String expirationDate,
        CardStatus status,
        BigDecimal initialBalance
) {}
