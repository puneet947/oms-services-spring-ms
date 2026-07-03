package com.oms.auth.dto;

public record AuthResponse(String token, String tokenType, long expiresInSeconds) {
}
