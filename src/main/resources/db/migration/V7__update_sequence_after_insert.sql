SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

SELECT setval('roles_id_seq', (SELECT MAX(id) FROM roles));