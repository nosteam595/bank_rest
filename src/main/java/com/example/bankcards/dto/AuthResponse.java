package com.example.bankcards.dto;

public record AuthResponse(
       String username,
       String token,
       String role
) {}
