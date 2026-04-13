-- Ejecuta este script en Oracle XE.
-- Si la tabla ya existe, la elimina y la vuelve a crear.

BEGIN
    EXECUTE IMMEDIATE 'DROP TABLE PELICULAS';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            RAISE;
        END IF;
END;
/

CREATE TABLE PELICULAS (
    ID NUMBER PRIMARY KEY,
    TITULO VARCHAR2(150) NOT NULL,
    ANIO NUMBER(4) NOT NULL,
    DIRECTOR VARCHAR2(120) NOT NULL,
    GENERO VARCHAR2(80) NOT NULL,
    SINOPSIS VARCHAR2(500) NOT NULL
);

INSERT INTO PELICULAS (ID, TITULO, ANIO, DIRECTOR, GENERO, SINOPSIS) VALUES
(1, 'Interestelar', 2014, 'Christopher Nolan', 'Ciencia ficcion', 'Un grupo de exploradores viaja por un agujero de gusano para buscar un nuevo hogar para la humanidad.');

INSERT INTO PELICULAS (ID, TITULO, ANIO, DIRECTOR, GENERO, SINOPSIS) VALUES
(2, 'El Padrino', 1972, 'Francis Ford Coppola', 'Drama', 'La historia de la familia Corleone y el ascenso de Michael dentro de la mafia.');

INSERT INTO PELICULAS (ID, TITULO, ANIO, DIRECTOR, GENERO, SINOPSIS) VALUES
(3, 'Titanic', 1997, 'James Cameron', 'Romance', 'Dos jovenes de diferentes clases sociales se conocen a bordo del Titanic.');

INSERT INTO PELICULAS (ID, TITULO, ANIO, DIRECTOR, GENERO, SINOPSIS) VALUES
(4, 'Avatar', 2009, 'James Cameron', 'Ciencia ficcion', 'Un exmarine participa en una mision en Pandora y termina involucrandose con su pueblo nativo.');

INSERT INTO PELICULAS (ID, TITULO, ANIO, DIRECTOR, GENERO, SINOPSIS) VALUES
(5, 'Coco', 2017, 'Lee Unkrich', 'Animacion', 'Un nino apasionado por la musica viaja accidentalmente al mundo de los muertos.');

COMMIT;
