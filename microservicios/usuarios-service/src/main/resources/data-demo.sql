INSERT INTO us_roles(id,nombre) VALUES (1,'ADMIN');
INSERT INTO us_roles(id,nombre) VALUES (2,'MODERATOR');
INSERT INTO us_roles(id,nombre) VALUES (3,'MEMBER');
INSERT INTO us_usuarios(id,nombre,email,password_hash,rol_id) VALUES (1,'Administrador','admin@foro.local','$2b$12$.BOXZPqsWLxGkmqeLd.l9.bNoUogrcI3VsPwNV.NnVkfRC6EjOXVq',1);
INSERT INTO us_usuarios(id,nombre,email,password_hash,rol_id) VALUES (2,'Moderador','moderador@foro.local','$2b$12$.BOXZPqsWLxGkmqeLd.l9.bNoUogrcI3VsPwNV.NnVkfRC6EjOXVq',2);
INSERT INTO us_usuarios(id,nombre,email,password_hash,rol_id) VALUES (3,'Estudiante','miembro@foro.local','$2b$12$.BOXZPqsWLxGkmqeLd.l9.bNoUogrcI3VsPwNV.NnVkfRC6EjOXVq',3);
