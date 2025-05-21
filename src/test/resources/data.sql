-- Insert test users
INSERT INTO users (id, username, password, email) VALUES
(RANDOM_UUID(), 'testuser1', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'test1@example.com'),
(RANDOM_UUID(), 'testuser2', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'test2@example.com');

-- Insert test roles
INSERT INTO role (id, username, rolecode, roletype) VALUES
(RANDOM_UUID(), 'testuser1', 'ADMIN', 'SYSTEM'),
(RANDOM_UUID(), 'testuser2', 'USER', 'SYSTEM'),
(RANDOM_UUID(), 'testuser1', 'USER', 'SYSTEM'),
(RANDOM_UUID(), 'testuser2', 'MANAGER', 'SYSTEM');  