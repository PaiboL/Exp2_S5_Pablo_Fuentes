-- Ejecutar conectado al esquema exclusivo de contenido. Oracle 19c o superior.
-- Esquema vacio. No ejecutar de nuevo sobre datos existentes.
CREATE TABLE fo_categorias (
 id NUMBER(19,0) PRIMARY KEY,
 nombre VARCHAR2(80 CHAR) NOT NULL UNIQUE,
 descripcion VARCHAR2(250 CHAR) NOT NULL
);
CREATE TABLE fo_temas (
 id NUMBER(19,0) PRIMARY KEY,
 titulo VARCHAR2(150 CHAR) NOT NULL,
 contenido CLOB NOT NULL,
 autor_id NUMBER(19,0) NOT NULL,
 categoria_id NUMBER(19,0) NOT NULL,
 creado_en TIMESTAMP NOT NULL,
 CONSTRAINT fk_fo_categoria FOREIGN KEY(categoria_id) REFERENCES fo_categorias(id)
);
CREATE TABLE fo_comentarios (
 id NUMBER(19,0) PRIMARY KEY,
 contenido CLOB NOT NULL,
 autor_id NUMBER(19,0) NOT NULL,
 tema_id NUMBER(19,0) NOT NULL,
 creado_en TIMESTAMP NOT NULL,
 CONSTRAINT fk_fo_tema FOREIGN KEY(tema_id) REFERENCES fo_temas(id)
);
CREATE SEQUENCE fo_categoria_seq START WITH 100 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE fo_tema_seq START WITH 100 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE fo_comentario_seq START WITH 100 INCREMENT BY 1 NOCACHE;
CREATE INDEX ix_fo_categoria ON fo_temas(categoria_id);
CREATE INDEX ix_fo_autor ON fo_temas(autor_id);
CREATE INDEX ix_fo_tema ON fo_comentarios(tema_id);
INSERT INTO fo_categorias(id,nombre,descripcion) VALUES (1,'Programacion','Java y desarrollo de software');
INSERT INTO fo_categorias(id,nombre,descripcion) VALUES (2,'Bases de datos','SQL y modelamiento de datos');
INSERT INTO fo_categorias(id,nombre,descripcion) VALUES (3,'Automatizacion','PLC y control industrial');
INSERT INTO fo_categorias(id,nombre,descripcion) VALUES (4,'Redes','Conectividad y protocolos');
INSERT INTO fo_categorias(id,nombre,descripcion) VALUES (5,'Ciberseguridad','Proteccion de sistemas');
INSERT INTO fo_temas(id,titulo,contenido,autor_id,categoria_id,creado_en) VALUES (1,'Primeros pasos con Spring Boot','Compartamos consejos para organizar controladores servicios y repositorios.',3,1,CURRENT_TIMESTAMP);
INSERT INTO fo_temas(id,titulo,contenido,autor_id,categoria_id,creado_en) VALUES (2,'Conexion a Oracle','Revisar usuario esquema y alias del Wallet antes de conectar.',3,2,CURRENT_TIMESTAMP);
INSERT INTO fo_temas(id,titulo,contenido,autor_id,categoria_id,creado_en) VALUES (3,'Comunicacion PLC y SCADA','Comparar protocolos y revisar la direccion de cada variable.',3,3,CURRENT_TIMESTAMP);
INSERT INTO fo_comentarios(id,contenido,autor_id,tema_id,creado_en) VALUES (1,'Conviene separar las responsabilidades por capas.',1,1,CURRENT_TIMESTAMP);
INSERT INTO fo_comentarios(id,contenido,autor_id,tema_id,creado_en) VALUES (2,'No incluir el Wallet ni las contrasenas en Git.',2,2,CURRENT_TIMESTAMP);
INSERT INTO fo_comentarios(id,contenido,autor_id,tema_id,creado_en) VALUES (3,'La prueba inicial puede realizarse en el laboratorio.',3,3,CURRENT_TIMESTAMP);
COMMIT;
