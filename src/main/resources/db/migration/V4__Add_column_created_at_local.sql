ALTER TABLE users
ADD COLUMN created_at_local TIMESTAMP;

ALTER TABLE role
ADD COLUMN created_at_local TIMESTAMP;

UPDATE users
SET created_at_local = TIMESTAMPADD(HOUR, 2, created_at);

UPDATE role
SET created_at_local = TIMESTAMPADD(HOUR, 2, created_at);

