-- Add createdAtZone column to users table
ALTER TABLE users
ADD COLUMN created_at_zone TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

-- Add createdAtZone column to role table
ALTER TABLE role
ADD COLUMN created_at_zone TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

-- Update existing records to set createdAtZone based on createdAt
UPDATE users
SET created_at_zone = created_at AT TIME ZONE 'UTC';

UPDATE role
SET created_at_zone = created_at AT TIME ZONE 'UTC';