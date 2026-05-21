package com.example.bankcards.controller;

import com.example.bankcards.dto.UserStatusUpdateRequest;
import com.example.bankcards.service.UserService;
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

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> changeUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateRequest request) {

        userService.changeStatus(id, request);
        return ResponseEntity.ok("Статус пользователя успешно изменен на " + request);
    }
}
