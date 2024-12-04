ALTER TABLE app_user
    ADD COLUMN account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT TRUE;