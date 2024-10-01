package edu.shtoiko.authservice.service;

import edu.shtoiko.authservice.model.SecuredUser;
import edu.shtoiko.authservice.model.UserSession;

import java.util.Optional;

public interface UserSessionService {
    Optional<UserSession> findByRefreshToken(String token);

    UserSession save(UserSession userSession);

    void deleteByRefreshToken(String refreshToken);

    boolean updateRefreshToken(String currentRefreshToken, String newRefreshToken);

    UserSession saveSession(SecuredUser user, String token);

    void deleteExpiredSessions();
}
