package edu.shtoiko.authservice.utils;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomJwtDecoder implements JwtDecoder {

    private final JwtTokenUtils jwtTokenUtils;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            Claims claims = jwtTokenUtils.extractClaims(token);

            return Jwt.withTokenValue(token)
                    .headers(headers -> headers.put("alg", "HS256"))
                    .claims(claimsMap -> claimsMap.putAll(claims))
                    .issuedAt(claims.getIssuedAt().toInstant())
                    .expiresAt(claims.getExpiration().toInstant())
                    .subject(claims.getSubject())
                    .build();
        } catch (Exception e) {
            OAuth2Error error = new OAuth2Error("invalid_token", "JWT validation failed", null);
            throw new JwtValidationException("Invalid JWT token", List.of(error));
        }
    }
}
