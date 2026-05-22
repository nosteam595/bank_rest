package com.example.bankcards.controller;

import com.example.bankcards.dto.UserStatusUpdateRequest;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Блокировка/разблокировка пользователей администратором",
            description = "Возвращает информацию о статусе пользователя: ACTIVE или BLOCKED")
    @ApiResponse(responseCode = "200", description = "Успешная смена статуса")
    @PatchMapping("/{id}/status")
    public ResponseEntity<String> changeUserStatus(
            @Parameter(description = "Идентификатор пользователя", schema = @Schema(type = "integer"))
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateRequest request) {
        userService.changeStatus(id, request);
        return ResponseEntity.ok("Статус пользователя успешно изменен на " + request);
    }
}
