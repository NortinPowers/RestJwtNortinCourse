insert into roles (name)
values ('ROLE_ADMIN'),
       ('ROLE_USER');

insert into users (username, password, role_id)
values ('admin', 'admin', 1),
       ('user', 'user', 2);
