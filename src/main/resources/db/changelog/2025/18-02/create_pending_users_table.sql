CREATE TABLE pending_users
(
    id                BIGSERIAL PRIMARY KEY,
    login             VARCHAR(255)                NOT NULL,
    name              VARCHAR(255)                NOT NULL,
    email             VARCHAR(255)                NOT NULL,
    password          VARCHAR(255)                NOT NULL,
    verification_code VARCHAR(255)                NOT NULL
);
