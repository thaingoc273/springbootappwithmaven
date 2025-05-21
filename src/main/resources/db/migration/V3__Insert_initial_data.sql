-- Insert Users
INSERT INTO users (id, username, password, email) VALUES
(UUID(), 'ngoc', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'ngoc@example.com'),
(UUID(), 'dat', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'dat@example.com'),
(UUID(), 'ngan', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'ngan@example.com'),
(UUID(), 'phuong', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'phuong@example.com');

-- Insert Roles
-- Admin roles
INSERT INTO role (id, username, rolecode, roletype) VALUES
(UUID(), 'ngoc', 'ADMIN', 'SYSTEM_ADMIN');

-- Manager roles
INSERT INTO role (id, username, rolecode, roletype) VALUES
(UUID(), 'dat', 'MANAGER', 'TEAM_MANAGER'),
(UUID(), 'ngan', 'MANAGER', 'PROJECT_MANAGER');

-- User roles
INSERT INTO role (id, username, rolecode, roletype) VALUES
(UUID(), 'phuong', 'USER', 'REGULAR_USER'),
(UUID(), 'dat', 'USER', 'REGULAR_USER'),
(UUID(), 'ngan', 'USER', 'REGULAR_USER'); 