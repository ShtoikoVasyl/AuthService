package edu.shtoiko.authservice.model.dto;

public record JwtResponse(String accessToken, String refreshToken, String tokenType) {
}
