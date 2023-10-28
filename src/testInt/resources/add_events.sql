insert into auth_event(id, ip_address, event_time, user_name, event_type, was_sent)
values ('9ff071ee-17ee-49d4-a899-49bc5c045a14', '127.0.0.1', '2023-01-01 01:00:05Z', 'root', 'AUTHENTICATION_SUCCESS', true);

insert into auth_event(id, ip_address, event_time, user_name, event_type, was_sent)
values ('60612367-81d2-44b1-9e32-ddcd6566f494', '127.0.0.1', '2023-02-01 01:00:05Z', 'root', 'AUTHENTICATION_FAILURE', true);

insert into auth_event(id, ip_address, event_time, user_name, event_type, was_sent)
values ('efe3d699-f863-4e6a-a3d5-ab26a00a9404', '127.0.0.1', '2023-03-01 01:00:05Z', 'root', 'AUTHORIZATION_FAILURE', true);

insert into auth_event(id, ip_address, event_time, user_name, event_type, was_sent)
values ('952d5540-2e1f-45a2-9eaa-e1cd14fb7ad9', '127.0.0.2', '2023-04-01 01:00:05Z', 'other', 'AUTHENTICATION_SUCCESS', true);

insert into auth_event(id, ip_address, event_time, user_name, event_type, was_sent)
values ('915ac077-c5f4-4096-a52b-176b9e93dc15', '127.0.0.1', '2023-05-01 01:00:05Z', 'another', 'AUTHENTICATION_SUCCESS', true);