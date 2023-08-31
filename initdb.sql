CREATE TABLE auth_event(
id uuid PRIMARY KEY default gen_random_uuid(),
ip_address varchar(45) NOT NULL,
event_time timestamptz NOT NULL,
user_name varchar(30) NOT NULL,
type varchar(30) NOT NULL
);

create table outbox(
id uuid PRIMARY KEY default gen_random_uuid(),
created_at timestamptz NOT NULL,
retry_count numeric(3) NOT NULL,
event json NOT NULL
);

comment on table auth_event is 'Table with auth events';
comment on table outbox is 'Table with messages to send';