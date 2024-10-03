package edu.shtoiko.authservice.utils;

import jakarta.validation.constraints.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.List;

public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter;

    @Override
    public AbstractAuthenticationToken convert(@NotNull Jwt jwt) {
        Collection<GrantedAuthority> authorities = jwtGrantedAuthoritiesConverter.convert(jwt);

        String userId = jwt.getClaimAsString("user_id");
        String tokenType = jwt.getClaimAsString("type");
        if (tokenType.equals("access")) {
            JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt, authorities);
            jwtAuthenticationToken.setDetails(userId);
            return jwtAuthenticationToken;
        } else {
            OAuth2Error error = new OAuth2Error("invalid_token", "Token type != assess", null);
            throw new JwtValidationException("Invalid JWT token", List.of(error));
        }
    }

    public void setJwtGrantedAuthoritiesConverter(
        Converter<Jwt, Collection<GrantedAuthority>> JwtGrantedAuthoritiesConverter) {
        this.jwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter;
    }
}
