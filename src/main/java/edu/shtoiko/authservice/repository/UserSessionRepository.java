package edu.shtoiko.authservice.repository;

import edu.shtoiko.authservice.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    boolean existsByRefreshToken(String refreshToken);

    List<UserSession> findByUserId(Long userId);

    UserSession findByRefreshToken(String refreshToken);

    void deleteByUserId(Long userId);

    void deleteByRefreshToken(String refreshToken);

    void deleteAllByExpiresAtBefore(Instant time);
}
