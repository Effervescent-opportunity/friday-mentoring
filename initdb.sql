CREATE TABLE auth_event(
id uuid PRIMARY KEY default gen_random_uuid(),
ip_address varchar(45) NOT NULL,
event_time timestamptz NOT NULL,
user_name varchar(20) NOT NULL,
type varchar(20) NOT NULL
);

create table outbox(
id uuid PRIMARY KEY default gen_random_uuid(),
created_at timestamptz NOT NULL,
retry_count numeric(3) NOT NULL,
--event_id uuid,
payload json NOT NULL
);

--create table orders.outbox(id int AUTO_INCREMENT primary key, event varchar(1000),event_id int,payload json,created_at timestamp);


--create table orders.customer_order(id int AUTO_INCREMENT primary key, name varchar(1000),quantity int);
--todo create tables