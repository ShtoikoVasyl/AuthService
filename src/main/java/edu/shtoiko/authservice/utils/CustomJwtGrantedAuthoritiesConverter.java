package edu.shtoiko.authservice.utils;

import edu.shtoiko.authservice.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CustomJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>>{


    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("roles");
        if (roles == null) {
            return List.of();
        }
        return roles.stream()
                .map(Role::new)
                .collect(Collectors.toList());
    }
}
