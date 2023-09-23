DROP TABLE outbox;

ALTER TABLE auth_event
ADD COLUMN was_sent boolean NOT NULL DEFAULT FALSE;