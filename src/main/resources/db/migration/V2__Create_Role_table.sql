CREATE TABLE IF NOT EXISTS role (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    username VARCHAR(50) NOT NULL,
    rolecode VARCHAR(50) NOT NULL,
    roletype VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_username_rolecode UNIQUE (username, rolecode),
    CONSTRAINT fk_role_username FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
); 