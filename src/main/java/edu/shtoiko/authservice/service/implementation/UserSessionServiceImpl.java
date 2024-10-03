package edu.shtoiko.authservice.service.implementation;

import edu.shtoiko.authservice.exception.ResponseException;
import edu.shtoiko.authservice.model.SecuredUser;
import edu.shtoiko.authservice.model.UserSession;
import edu.shtoiko.authservice.repository.UserSessionRepository;
import edu.shtoiko.authservice.service.UserSessionService;
import edu.shtoiko.authservice.utils.JwtTokenUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserSessionServiceImpl implements UserSessionService {

    private final UserSessionRepository sessionRepository;
    private final JwtTokenUtils tokenUtils;

    @Override
    public Optional<UserSession> findByRefreshToken(String token) {
        return Optional.ofNullable(sessionRepository.findByRefreshToken(token));
    }

    @Override
    public UserSession save(UserSession userSession) {
        return sessionRepository.save(userSession);
    }

    @Override
    public void deleteByRefreshToken(String refreshToken) {
        if (!sessionRepository.existsByRefreshToken(refreshToken)) {
            throw new ResponseException(HttpStatus.BAD_REQUEST, "Session not found");
        }
        sessionRepository.deleteByRefreshToken(refreshToken);
    }

    @Override
    public boolean updateRefreshToken(String currentRefreshToken, String newRefreshToken) {
        UserSession session = findByRefreshToken(currentRefreshToken)
            .orElseThrow(() -> new ResponseException(HttpStatus.BAD_REQUEST, "Session not found"));
        session.setRefreshToken(newRefreshToken);
        session.setExpiresAt(tokenUtils.extractExpiration(newRefreshToken).toInstant());
        return save(session).getRefreshToken().equals(newRefreshToken);
    }

    @Override
    public UserSession saveSession(SecuredUser user, String token) {
        UserSession userSession = new UserSession();
        userSession.setUser(user);
        userSession.setUserAgent("someAgent");
        userSession.setDeviceType("someType");
        userSession.setIpAddress("123");
        userSession.setStartTime(Instant.now());
        userSession.setLastActivity(Instant.now());
        userSession.setExpiresAt(tokenUtils.extractExpiration(token).toInstant());
        userSession.setRefreshToken(token);
        return save(userSession);
    }

    @Override
    @Transactional
    public void deleteExpiredSessions() {
        sessionRepository.deleteAllByExpiresAtBefore(Instant.now());
    }
}
