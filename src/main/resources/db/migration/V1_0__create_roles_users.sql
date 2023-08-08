drop table if exists roles cascade;

create table roles
(
    id   bigserial
        constraint roles_pk
            primary key,
    name varchar(20) not null
);

drop table if exists users cascade;

create table users
(
    id       bigserial
        constraint users_pk
            primary key,
    username varchar(50) not null
        constraint users_pk2
            unique,
    password varchar(80) not null,
    role_id  bigserial   not null
        constraint users_roles_id_fk
            references roles
            on update cascade on delete cascade
);
