package com.compass.desafio3.domain.models.dtos;

public record PasswordResetDTO(String token, String newPassword) {
}
