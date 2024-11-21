CREATE TABLE refresh_token
(
    id          UUID                     NOT NULL PRIMARY KEY,
    user_id     UUID                     NOT NULL UNIQUE REFERENCES app_user (id),
    token       TEXT                     NOT NULL UNIQUE,
    expiry_date TIMESTAMP WITH TIME ZONE NOT NULL
);