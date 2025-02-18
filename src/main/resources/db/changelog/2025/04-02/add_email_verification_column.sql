ALTER TABLE app_user
    ADD COLUMN is_email_verified BOOLEAN DEFAULT FALSE,
    ADD COLUMN verification_code VARCHAR(10) DEFAULT NULL;