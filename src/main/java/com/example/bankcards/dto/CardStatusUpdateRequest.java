package com.example.bankcards.dto;

import com.example.bankcards.util.CardStatus;
import jakarta.validation.constraints.NotNull;

public record CardStatusUpdateRequest(
        @NotNull(message = "Статус карты не может быть пустым")
        CardStatus status
) {}
