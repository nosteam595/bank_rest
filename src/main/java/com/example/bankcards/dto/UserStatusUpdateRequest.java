package com.example.bankcards.dto;

import com.example.bankcards.util.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UserStatusUpdateRequest(
        @Schema(description = "Статус пользователя",
                example = "ACTIVE",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Статус пользователя не может быть пустым")
        UserStatus status
) {}
