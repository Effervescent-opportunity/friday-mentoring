ALTER TABLE users
    ADD COLUMN account_non_locked boolean NOT NULL DEFAULT TRUE;

ALTER TABLE users
    ADD COLUMN credentials_non_expired NOT NULL DEFAULT TRUE;

ALTER TABLE users
    ADD COLUMN enabled NOT NULL DEFAULT TRUE;

ALTER TABLE users
    ADD COLUMN password_expire_date timestamptz NOT NULL DEFAULT now() + 30;