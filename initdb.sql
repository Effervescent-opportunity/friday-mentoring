create table auth_event();

create table outbox();

create table orders.outbox(id int AUTO_INCREMENT primary key, event varchar(1000),event_id int,payload json,created_at timestamp);


create table orders.customer_order(id int AUTO_INCREMENT primary key, name varchar(1000),quantity int);
--todo create tables