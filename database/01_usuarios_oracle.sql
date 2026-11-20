-- Ejecutar conectado al esquema exclusivo de usuarios. Oracle 19c o superior.
-- Esquema vacio. No ejecutar de nuevo sobre datos existentes.
CREATE TABLE us_roles (
 id NUMBER(19,0) PRIMARY KEY,
 nombre VARCHAR2(20 CHAR) NOT NULL UNIQUE,
 CONSTRAINT ck_us_rol CHECK (nombre IN ('ADMIN','MODERATOR','MEMBER'))
);
CREATE TABLE us_usuarios (
 id NUMBER(19,0) PRIMARY KEY,
 nombre VARCHAR2(100 CHAR) NOT NULL,
 email VARCHAR2(150 CHAR) NOT NULL UNIQUE,
 password_hash VARCHAR2(100 CHAR) NOT NULL,
 rol_id NUMBER(19,0) NOT NULL,
 CONSTRAINT fk_us_rol FOREIGN KEY (rol_id) REFERENCES us_roles(id)
);
CREATE SEQUENCE us_usuario_seq START WITH 100 INCREMENT BY 1 NOCACHE;
CREATE INDEX ix_us_rol ON us_usuarios(rol_id);
INSERT INTO us_roles(id,nombre) VALUES (1,'ADMIN');
INSERT INTO us_roles(id,nombre) VALUES (2,'MODERATOR');
INSERT INTO us_roles(id,nombre) VALUES (3,'MEMBER');
INSERT INTO us_usuarios(id,nombre,email,password_hash,rol_id) VALUES (1,'Administrador','admin@foro.local','$2b$12$.BOXZPqsWLxGkmqeLd.l9.bNoUogrcI3VsPwNV.NnVkfRC6EjOXVq',1);
INSERT INTO us_usuarios(id,nombre,email,password_hash,rol_id) VALUES (2,'Moderador','moderador@foro.local','$2b$12$.BOXZPqsWLxGkmqeLd.l9.bNoUogrcI3VsPwNV.NnVkfRC6EjOXVq',2);
INSERT INTO us_usuarios(id,nombre,email,password_hash,rol_id) VALUES (3,'Estudiante','miembro@foro.local','$2b$12$.BOXZPqsWLxGkmqeLd.l9.bNoUogrcI3VsPwNV.NnVkfRC6EjOXVq',3);
COMMIT;
