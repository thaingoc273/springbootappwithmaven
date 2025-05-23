-- Delete all data from role and users table
DELETE FROM role;
DELETE FROM users;

-- Insert test users
INSERT INTO users (id, username, password, email) VALUES
(UUID(), 'testuser1', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'test1@example.com'),
(UUID(), 'testuser2', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'test2@example.com');

-- Insert test roles
INSERT INTO role (id, username, rolecode, roletype) VALUES
(UUID(), 'testuser1', 'ADMIN', 'SYSTEM'),
(UUID(), 'testuser2', 'USER', 'SYSTEM'),
(UUID(), 'testuser1', 'USER', 'SYSTEM'),
(UUID(), 'testuser2', 'MANAGER', 'SYSTEM');