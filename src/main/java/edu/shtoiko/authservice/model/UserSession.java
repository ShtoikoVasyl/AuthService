package edu.shtoiko.authservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "user_sessions")
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private SecuredUser user;

    @Column(name = "refresh_token", nullable = false, unique = true)
    private String refreshToken;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "user_agent", nullable = false)
    private String userAgent;

    @Column(name = "device_type", nullable = false)
    private String deviceType;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "last_activity", nullable = false)
    private Instant lastActivity;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

}
