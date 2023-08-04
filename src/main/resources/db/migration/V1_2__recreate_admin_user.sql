truncate table users cascade;
select setval('users_id_seq', 1, false);
