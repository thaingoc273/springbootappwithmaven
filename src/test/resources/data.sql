-- Delete existing data
DELETE FROM role;
DELETE FROM users;

-- Insert test users
INSERT INTO users (id, username, password, email, created_at, created_at_zone, updated_at) VALUES
(RANDOM_UUID(), 'testuser1', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'test1@example.com', 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(RANDOM_UUID(), 'testuser2', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'test2@example.com',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert test roles
INSERT INTO role (id, username, rolecode, roletype, created_at, created_at_zone, updated_at) VALUES
(RANDOM_UUID(), 'testuser1', 'ADMIN', 'SYSTEM', 
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(RANDOM_UUID(), 'testuser2', 'USER', 'SYSTEM',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(RANDOM_UUID(), 'testuser1', 'MANAGER', 'SYSTEM',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(RANDOM_UUID(), 'testuser2', 'MANAGER', 'SYSTEM',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Update created_at_zone for users
-- UPDATE users SET created_at_zone = created_at; -- DATE_ADD(created_at, INTERVAL 2 HOUR); -- CONVERT_TZ(created_at, 'UTC', 'Asia/Ho_Chi_Minh');

-- Update created_at_zone for roles
-- UPDATE roles SET created_at_zone = created_at; -- DATE_ADD(created_at, INTERVAL 2 HOUR); -- CONVERT_TZ(created_at, 'UTC', 'Europe/Paris');
