DROP TABLE outbox;

ALTER TABLE auth_event
ADD COLUMN was_sent boolean NOT NULL DEFAULT FALSE;
--todo should I rename this file or move editing auth_event to another migration?