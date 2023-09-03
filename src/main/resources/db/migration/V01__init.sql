CREATE TABLE auth_event
(
    id         uuid PRIMARY KEY,
    ip_address varchar(45) NOT NULL,
    event_time timestamptz NOT NULL,
    user_name  varchar(30) NOT NULL,
    event_type varchar(30) NOT NULL
);

create table outbox
(
    id          uuid PRIMARY KEY,
    created_at  timestamptz NOT NULL,
    retry_count numeric(3)  NOT NULL,
    event       jsonb       NOT NULL
);

comment on table auth_event is 'Table with auth events';
comment on table outbox is 'Table with messages to send';