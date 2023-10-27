insert into auth_event(id, ip_address, event_time, user_name, event_type)
values (gen_random_uuid(), '127.0.0.1', '2023-01-01 01:00:05Z', 'root', 'AUTHENTICATION_SUCCESS');

insert into auth_event(id, ip_address, event_time, user_name, event_type)
values (gen_random_uuid(), '127.0.0.1', '2023-02-01 01:00:05Z', 'root', 'AUTHENTICATION_FAILURE');

insert into auth_event(id, ip_address, event_time, user_name, event_type)
values (gen_random_uuid(), '127.0.0.1', '2023-03-01 01:00:05Z', 'root', 'AUTHORIZATION_FAILURE');

insert into auth_event(id, ip_address, event_time, user_name, event_type)
values (gen_random_uuid(), '127.0.0.2', '2023-04-01 01:00:05Z', 'other', 'AUTHENTICATION_SUCCESS');

insert into auth_event(id, ip_address, event_time, user_name, event_type)
values (gen_random_uuid(), '127.0.0.1', '2023-05-01 01:00:05Z', 'another', 'AUTHENTICATION_SUCCESS');