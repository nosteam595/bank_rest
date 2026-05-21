package com.example.bankcards.dto;

import com.example.bankcards.util.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UserStatusUpdateRequest(
        @NotNull(message = "Статус пользователя не может быть пустым")
        UserStatus status
) {}
