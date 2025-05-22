-- Drop all data from users and roles table
DELETE FROM role;
DELETE FROM users;

-- Insert Users
INSERT INTO users ( username, `password`, email) VALUES
('ngoc', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'ngoc@example.com'),
('dat', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'dat@example.com'),
('ngan', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'ngan@example.com'),
('phuong', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'phuong@example.com');

-- Insert Roles
-- Admin roles
INSERT INTO role (username, rolecode, roletype) VALUES
('ngoc', 'ADMIN', 'SYSTEM_ADMIN');

-- Manager roles
INSERT INTO role (username, rolecode, roletype) VALUES
('dat', 'MANAGER', 'TEAM_MANAGER'),
('ngan', 'MANAGER', 'PROJECT_MANAGER');

-- User roles
INSERT INTO role (username, rolecode, roletype) VALUES
('phuong', 'USER', 'REGULAR_USER'),
('dat', 'USER', 'REGULAR_USER'),
('ngan', 'USER', 'REGULAR_USER'); 