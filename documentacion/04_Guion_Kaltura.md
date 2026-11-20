# Guion para la presentación de Foro Nexo

Pablo Fuentes · DSY2205 · Experiencia 2 Semana 5

Duración objetivo: nueve minutos, combinando explicación y demostración. Ensaya con los servicios iniciados. El texto guía no acredita que Docker u Oracle hayan sido ejecutados: muestra esas partes únicamente después de verificarlas realmente. No muestres `.env`, tokens completos, contraseñas personales ni credenciales Oracle.

## De 0 a 45 segundos Presentación

“Hola, soy Pablo Fuentes. En esta presentación mostraré Foro Nexo, correspondiente a la segunda evaluación sumativa de Desarrollo Full Stack III. El proyecto continúa los servicios de usuarios y foro desarrollados anteriormente, e incorpora Angular, un arquetipo Maven propio, patrones de diseño y una configuración de ejecución con Docker.”

Muestra la portada del proyecto y su estructura en VS Code. Explica que el caso específico es un foro académico con cinco categorías.

## De 45 segundos a 2 minutos Acceso y perfil

“Comienzo creando una cuenta. El registro comprueba correo, campos obligatorios y una contraseña de al menos diez caracteres con mayúscula, minúscula, número y símbolo. El usuario nuevo recibe el rol MEMBER desde el backend; no puede asignarse permisos de administrador.”

Muestra un error de validación, completa el registro e inicia sesión. Modifica el nombre desde Mi perfil.

“Los permisos no dependen solamente de ocultar botones. Spring Security valida el token y los roles en las APIs. La sesión del frontend se conserva durante su vigencia y el acceso administrativo exige ADMIN.”

## De 2 a 3 minutos 15 segundos Publicaciones y comentarios

Muestra las cinco categorías. Busca un título, cambia de categoría y publica un tema breve. Abre el detalle, agrega un comentario, edítalo y elimina el comentario de prueba.

“El foro permite consultar, crear, modificar y eliminar contenido. Los servicios verifican quién es el autor antes de aceptar una modificación. La interfaz envía solicitudes HTTP y muestra el resultado devuelto por la API.”

Cambia el ancho de la ventana para mostrar teléfono, tablet y escritorio. Explica la grilla Bootstrap de doce columnas y las reglas CSS adaptables.

## De 3 minutos 15 segundos a 4 minutos 15 segundos Administración

Cierra la sesión de miembro e inicia sesión como administrador. Abre Administración, selecciona un tema y escribe el motivo del bloqueo. Comprueba que deja de aparecer y que su enlace directo no muestra contenido. Restaura el tema desde Moderación.

“El tercer microservicio registra las decisiones y solicita al servicio de foro aplicar el estado de bloqueo. Las categorías y los usuarios también se administran desde esta pantalla. El sistema conserva un mínimo de cinco categorías.”

## De 4 minutos 15 segundos a 5 minutos Recuperación

Solicita recuperación para la cuenta creada. Abre el buzón Mailpit local, copia el código, establece una contraseña nueva y vuelve a iniciar sesión.

“El código es aleatorio, vence en quince minutos y solo se utiliza una vez. En la base se almacena su hash, no el código original. Mailpit es un buzón de laboratorio; no representa un envío a un proveedor externo.”

## De 5 a 6 minutos 15 segundos Patrones y arquetipo

Abre ApiError y ErrorHandler. Señala el Builder y el ResponseEntity con su estado HTTP. Abre ForoFacade y ForoApiService para mostrar la separación entre estado de presentación y acceso a la API.

“El patrón Builder permite construir errores consistentes y validar sus campos. La fachada de Angular coordina la carga sin distribuir la misma lógica entre las páginas. Los componentes de Spring tienen alcance singleton por defecto; no implementé un Singleton clásico manual.”

Muestra `archetype-metadata.xml`, la instalación en Maven y el comando de generación. Enseña un paquete generado diferente del paquete de la base.

“El arquetipo contiene infraestructura reutilizable. Los tres proyectos se generaron desde esa plantilla y después recibieron la lógica de su dominio. Los logs incluidos permiten revisar la creación, instalación y generación.”

## De 6 minutos 15 segundos a 7 minutos 45 segundos Docker y Oracle

Muestra Docker Desktop o `docker compose ps`, los Dockerfiles y las rutas internas Nginx. Abre las consultas Oracle con el registro creado desde Angular. Reinicia los contenedores y comprueba que permanece.

“Cada backend se construye como una imagen y usa su propio esquema Oracle. El navegador accede a Angular y Nginx enruta las APIs a los servicios de la red de Compose. El volumen mantiene la base entre reinicios.”

No leas la frase anterior como resultado demostrado si todavía no has ejecutado esta parte. Si aparece un error, resuélvelo antes de grabar la entrega definitiva. La evidencia con H2 no reemplaza Oracle.

## De 7 minutos 45 segundos a 8 minutos 40 segundos Pruebas y Git

Muestra las colecciones Postman y algunos resultados 200, 201, 204, 400, 401, 403 y 404. Abre los resultados Java y Angular, indicando el perfil usado. Muestra Git, la rama de entrega y sus commits.

“Las pruebas revisan operaciones CRUD, autorización, validación, errores, recuperación y moderación. La ejecución automatizada del paquete utilizó H2; en esta demostración identifico aparte la comprobación realizada con Oracle. El repositorio incluye la estructura, el arquetipo, las pruebas y la documentación.”

## De 8 minutos 40 segundos a 9 minutos Cierre

“Esta solución integra el frontend con los servicios previos y agrega mecanismos reutilizables de desarrollo. Como mejoras posteriores considero revocación inmediata de tokens, paginación del panel y mayor tolerancia a fallos entre servicios. La entrega incluye el código, el arquetipo, los scripts de base de datos, la configuración Docker y los enlaces correspondientes. Gracias.”

## Antes de enviar

Reproduce la grabación completa. Comprueba duración entre siete y diez minutos, audio entendible, código legible y permisos de acceso al video. Copia su enlace en `05_Entrega_AVA.txt` y en el formulario del AVA. Comprueba el enlace Git en una ventana privada. La subida al AVA y la grabación se realizan desde tu cuenta.
