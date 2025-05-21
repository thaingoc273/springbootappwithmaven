-- Insert test users
INSERT INTO users (id, username, email, password, created_at, updated_at)
VALUES 
    (UUID(), 'testuser1', 'test1@example.com', 'password123', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (UUID(), 'testuser2', 'test2@example.com', 'password456', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert test roles
INSERT INTO role (id, username, rolecode, roletype, created_at, updated_at)
VALUES 
    (UUID(), 'testuser1', 'USER', 'SYSTEM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (UUID(), 'testuser2', 'ADMIN', 'SYSTEM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); 