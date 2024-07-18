package com.compass.desafio3.domain.models;

public record PasswordResetDTO(String token, String newPassword) {
}
