CREATE TABLE auth_event(
id uuid PRIMARY KEY default gen_random_uuid(),
ip_address varchar(45) NOT NULL,
event_time
);

create table outbox();

create table orders.outbox(id int AUTO_INCREMENT primary key, event varchar(1000),event_id int,payload json,created_at timestamp);


create table orders.customer_order(id int AUTO_INCREMENT primary key, name varchar(1000),quantity int);
--todo create tables