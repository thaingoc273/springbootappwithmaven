-- Delete all data from role and users table
DELETE FROM role;
DELETE FROM users;

-- Insert test users with proper timestamp handling
INSERT INTO users (id, username, password, email, created_at, created_at_zone, updated_at) VALUES
(UUID(), 'testuser1', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'test1@example.com', 
 TIMESTAMP '2025-01-01 00:00:00', TIMESTAMP '2025-01-01 00:00:00' AT TIME ZONE 'UTC', TIMESTAMP '2025-01-01 00:00:00'),
(UUID(), 'testuser2', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'test2@example.com',
 TIMESTAMP '2025-01-01 00:00:00', TIMESTAMP '2025-01-01 00:00:00' AT TIME ZONE 'UTC', TIMESTAMP '2025-01-01 00:00:00');

-- Insert test roles with proper timestamp handling
INSERT INTO role (id, username, rolecode, roletype, created_at, created_at_zone, updated_at) VALUES
(UUID(), 'testuser1', 'ADMIN', 'SYSTEM', 
 TIMESTAMP '2025-01-01 00:00:00', TIMESTAMP '2025-01-01 00:00:00' AT TIME ZONE 'UTC', TIMESTAMP '2025-01-01 00:00:00'),
(UUID(), 'testuser2', 'USER', 'SYSTEM',
 TIMESTAMP '2025-01-01 00:00:00', TIMESTAMP '2025-01-01 00:00:00' AT TIME ZONE 'UTC', TIMESTAMP '2025-01-01 00:00:00'),
(UUID(), 'testuser1', 'USER', 'SYSTEM',
 TIMESTAMP '2025-01-01 00:00:00', TIMESTAMP '2025-01-01 00:00:00' AT TIME ZONE 'UTC', TIMESTAMP '2025-01-01 00:00:00'),
(UUID(), 'testuser2', 'MANAGER', 'SYSTEM',
 TIMESTAMP '2025-01-01 00:00:00', TIMESTAMP '2025-01-01 00:00:00' AT TIME ZONE 'UTC', TIMESTAMP '2025-01-01 00:00:00');
