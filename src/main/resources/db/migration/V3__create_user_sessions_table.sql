CREATE TABLE user_sessions (
                               id BIGSERIAL PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               refresh_token TEXT NOT NULL UNIQUE,
                               ip_address VARCHAR(255) NOT NULL,
                               user_agent TEXT NOT NULL,
                               device_type VARCHAR(50) NOT NULL,
                               start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               last_activity TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               expires_at TIMESTAMP NOT NULL,
                               CONSTRAINT fk_user_sessions_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);