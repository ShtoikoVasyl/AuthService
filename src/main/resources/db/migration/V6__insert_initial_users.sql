INSERT INTO users (id, email, password)
VALUES
    (1, 'john.doe@example.com', '$2a$10$wceGJELIiL0BBUWrj.C7p.zxXJhYXLDYt5n7xDSddj.4C5HE07TH.'),
    (2, 'jane.doe@example.com', '$2a$10$v2/px8vg4kvqlmXQC059Iequ4uZQ0TS31UYCnl4hMF/yrqZqaP6YO');

INSERT INTO users_roles (user_id, role_id) VALUES (1, 2);
INSERT INTO users_roles (user_id, role_id) VALUES (1, 3);

INSERT INTO users_roles (user_id, role_id) VALUES (2, 1);