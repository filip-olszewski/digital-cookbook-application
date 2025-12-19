ALTER TABLE users ADD COLUMN username VARCHAR(255);

UPDATE users
SET username = LOWER(
    REGEXP_REPLACE(first_name || last_name || id, '[^a-zA-Z0-9]', '', 'g')
)
WHERE username IS NULL;

ALTER TABLE users ALTER COLUMN username SET NOT NULL;

CREATE UNIQUE INDEX idx_user_username ON users (username)
WHERE deleted_at IS NULL;