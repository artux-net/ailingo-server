ALTER TABLE app_user ADD COLUMN role VARCHAR(255) NOT NULL DEFAULT 'USER';
UPDATE app_user SET role = 'ADMIN' WHERE login = 'admin';