-- Delete all data from role and users table
DELETE FROM role;
DELETE FROM users;

-- Insert test users with proper timestamp handling
INSERT INTO users (id, username, password, email) VALUES
(UUID(), 'testuser1', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'test1@example.com'),
(UUID(), 'testuser2', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'test2@example.com');

-- Insert test roles with proper timestamp handling
INSERT INTO role (id, username, rolecode, roletype) VALUES
(UUID(), 'testuser1', 'ADMIN', 'SYSTEM'),
(UUID(), 'testuser2', 'USER', 'SYSTEM'),
(UUID(), 'testuser1', 'MANAGER', 'SYSTEM'),
(UUID(), 'testuser2', 'MANAGER', 'SYSTEM');

-- Update created_at_zone for users
UPDATE users SET created_at_zone = CONVERT_TZ(created_at, 'UTC', 'Europe/Paris');

-- Update created_at_zone for roles
UPDATE role SET created_at_zone = CONVERT_TZ(created_at, 'UTC', 'Europe/Paris');
