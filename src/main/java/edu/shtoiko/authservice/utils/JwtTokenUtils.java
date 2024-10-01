package edu.shtoiko.authservice.utils;

import edu.shtoiko.authservice.model.Role;
import edu.shtoiko.authservice.model.dto.JwtResponse;
import edu.shtoiko.authservice.model.SecuredUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtils {

    @Value("${jwt.rest.key}")
    private String secretKey;

    @Value("${jwt.rest.expiration_time.access}")
    private Long accessTokenExpirationTime;

    @Value("${jwt.rest.expiration_time.refresh}")
    private Long refreshTokenExpirationTime;

    private final String tokenType = "Bearer";
    public String extractUsername(String token){
        return extractClaims(token, Claims::getSubject);
    }

    public JwtResponse createNewTokenPair(SecuredUser user){
        JwtResponse response =  new JwtResponse(generateAccessToken(user), generateRefreshToken(user), tokenType);
        return response;
    }

    public String generateAccessToken(SecuredUser userDetails){
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access");
         return createToken(claims, userDetails, accessTokenExpirationTime);
    }

    public String generateAccessToken(String refreshToken){
            return generateAccessToken(getSecuredUserForUpdateToken(refreshToken));
    }

    //todo: exception
    private SecuredUser getSecuredUserForUpdateToken(String refreshToken) {
        if(!extractTokenType(refreshToken).equals("refresh")){
            throw new IllegalArgumentException("Token type != 'refresh'");
        } else {
            SecuredUser user = new SecuredUser();
            user.setEmail(extractUsername(refreshToken));
            user.setRoles(extractRoles(refreshToken));
            return user;
        }
    }

    public String generateRefreshToken(String refreshToken){
        return generateRefreshToken(getSecuredUserForUpdateToken(refreshToken));
    }

    public String generateRefreshToken(SecuredUser userDetails){
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(claims, userDetails, refreshTokenExpirationTime);
    }

    private String createToken(Map<String, Object> claims, SecuredUser userDetails, long expirationTime){
        claims.put("user_id", userDetails.getId());
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return generateToken(claims, userDetails, expirationTime);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private final Function<Claims, List<Role>> getRoles = claims -> {
        List<String> roles = claims.get("roles", List.class);
        return roles.stream()
                .map(Role::new)
                .collect(Collectors.toList());
    };

    private final Function<Claims, String> getTokenType = claims -> claims.get("type", String.class);

    public List<Role> extractRoles(String token){
        return extractClaims(token, getRoles);
    }

    public String extractTokenType(String token){
        System.out.println("tokenType" + extractClaims(token, getTokenType));
        return extractClaims(token, getTokenType);
    }

    private Key getSignKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Map<String, Object> claims, SecuredUser user, long expirationTime){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public boolean isRefreshTokenNotExpired(String token){
        Date expirationTime = extractExpiration(token);
        Date currentTime = new Date(System.currentTimeMillis());
        return currentTime.before(expirationTime);
    }

    public boolean isRefreshTokenFresh(String token){
        return (new Date(System.currentTimeMillis() - 2 * accessTokenExpirationTime)).before(extractExpiration(token));
    }

    public Date extractExpiration(String token){
        return extractClaims(token, Claims::getExpiration);
    }

    public JwtResponse refreshToken(String refreshToken) {
        if(isRefreshTokenFresh(refreshToken)) {
            return new JwtResponse(generateAccessToken(refreshToken),
                    refreshToken, tokenType);
        }
        if (isRefreshTokenNotExpired(refreshToken)){
            return new JwtResponse(generateAccessToken(refreshToken),
                    generateRefreshToken(refreshToken), tokenType);
        } else {
            throw new IllegalArgumentException("RefreshToken expired");
        }
    }
}
