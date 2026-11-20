-- Ejecutar cada bloque con el usuario correspondiente.
-- Esquema USUARIOS: resultados iniciales 3 roles y 3 usuarios.
SELECT COUNT(*) AS roles FROM us_roles;
SELECT COUNT(*) AS usuarios FROM us_usuarios;
SELECT u.id, u.nombre, u.email, r.nombre AS rol
FROM us_usuarios u JOIN us_roles r ON r.id=u.rol_id ORDER BY u.id;
-- Esquema FORO: resultados iniciales 5 categorias, 3 temas y 3 comentarios.
SELECT COUNT(*) AS categorias FROM fo_categorias;
SELECT COUNT(*) AS temas FROM fo_temas;
SELECT COUNT(*) AS comentarios FROM fo_comentarios;
SELECT t.id,t.titulo,c.nombre AS categoria,t.autor_id
FROM fo_temas t JOIN fo_categorias c ON c.id=t.categoria_id ORDER BY t.id;
SELECT m.id,m.tema_id,m.autor_id,DBMS_LOB.SUBSTR(m.contenido,100,1) AS contenido
FROM fo_comentarios m ORDER BY m.id;
