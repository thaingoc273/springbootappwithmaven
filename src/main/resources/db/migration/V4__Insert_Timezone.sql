-- Add createdAtZone column to users table
ALTER TABLE users
ADD COLUMN created_at_zone TIMESTAMP;

-- Add createdAtZone column to role table
ALTER TABLE role
ADD COLUMN created_at_zone TIMESTAMP;

-- Update existing records to set createdAtZone based on createdAt
-- UPDATE users
-- SET created_at_zone = created_at; -- DATE_ADD(created_at, INTERVAL 2 HOUR); -- CONVERT_TZ(created_at, 'UTC', 'Europe/Paris');

-- UPDATE role
-- SET created_at_zone = created_at; -- DATE_ADD(created_at, INTERVAL 2 HOUR); -- CONVERT_TZ(created_at, "UTC", "Europe/Paris")