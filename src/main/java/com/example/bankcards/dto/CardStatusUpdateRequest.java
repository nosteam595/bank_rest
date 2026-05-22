package com.example.bankcards.dto;

import com.example.bankcards.util.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CardStatusUpdateRequest(
        @Schema(description = "Статус карты",
                example = "ACTIVE",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Статус карты не может быть пустым")
        CardStatus status
) {}
