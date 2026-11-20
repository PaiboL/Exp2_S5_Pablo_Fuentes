# Arquitectura y reutilización

## Continuidad de Semana 2

La base utilizada es el proyecto `foro-academico`, commit local original `335f488`. Se conservaron Usuario, Rol, Categoria, Tema, Comentario, repositorios, contratos CRUD, BCrypt, JWT y restricciones de autoría. El código se trasladó a `microservicios` después de generar cada proyecto desde el arquetipo propio.

Usuarios incorpora recuperación de contraseña. Foro incorpora visibilidad por bloqueo y una API administrativa interna. Moderación es el tercer servicio, añadido para cubrir el nivel de tres microservicios indicado en la pauta. No sustituye los dos originales.

## Responsabilidades

| Componente | Responsabilidad | Persistencia |
|---|---|---|
| Angular | Formularios, navegación, estado de presentación y llamadas HTTP | Token de sesión en sessionStorage |
| Usuarios | Registro, login, perfil, roles y recuperación | USUARIOS_APP |
| Foro | Categorías, temas, comentarios, autoría y visibilidad | FORO_APP |
| Moderación | Decisiones de bloqueo y restauración | MODERACION_APP |
| Nginx | Archivos Angular y enrutamiento de `/api` | Sin datos de negocio |
| Mailpit | Captura de correo para laboratorio | Buzón de prueba |

## Patrón Builder en el backend

`ApiError.builder().status(...).error(...).details(...).build()` construye respuestas consistentes. El constructor valida estados de error y copia los detalles para impedir cambios externos. El `ErrorHandler` centralizado usa el Builder y devuelve `ResponseEntity` con el estado HTTP correspondiente. Incluir un número en JSON no cambia por sí solo el estado HTTP.

El arquetipo contiene Application, ApiError, ApiException, ErrorHandler, seguridad JWT, dependencias base y pruebas del Builder. No incluye entidades comerciales ni secretos Oracle. Los servicios generados incorporan después sus modelos específicos.

## Patrón Facade en Angular

`ForoFacade` coordina categorías, temas, paginación, carga y errores. `ForoApiService` concentra las peticiones HTTP. `AuthFacade` administra sesión y perfil. Las páginas consumen estas fachadas y servicios; el interceptor adjunta el token y procesa 401. Los guards mejoran la navegación, pero la autorización efectiva también se verifica en Spring Security.

La interfaz utiliza formularios reactivos para autenticación, perfil, temas y comentarios; el panel administrativo emplea formularios de plantilla. Bootstrap aporta la grilla de doce columnas. Las reglas CSS adaptan las pantallas a móvil, tablet y escritorio. Las rutas cargan sus páginas bajo demanda.

## Crear e instalar el arquetipo

Los logs 01, 02 y 03 de `evidencias` registran comandos ejecutados realmente. La plantilla entregada está en `arquetipo/foro-archetype`.

```bash
cd arquetipo/base-microservicio
mvn org.apache.maven.plugins:maven-archetype-plugin:3.4.1:create-from-project -DpackageName=cl.duoc.plantilla -Darchetype.keepParent=true
```

Maven genera `target/generated-sources/archetype`. La versión entregada copia ese resultado a `arquetipo/foro-archetype`, ajusta el artifactId a `foro-archetype` y conserva los parámetros de paquete y proyecto.

```bash
mvn -f arquetipo/foro-archetype/pom.xml clean install
```

Desde una carpeta vacía genera un proyecto de prueba:

```bash
mvn org.apache.maven.plugins:maven-archetype-plugin:3.4.1:generate -DarchetypeGroupId=cl.duoc.foro -DarchetypeArtifactId=foro-archetype -DarchetypeVersion=1.0.0 -DgroupId=cl.duoc.demo -DartifactId=ms-demo -Dversion=1.0.0 -Dpackage=cl.duoc.demo -DinteractiveMode=false -DarchetypeCatalog=local
mvn -f ms-demo/pom.xml test
```

`archetype-metadata.xml` marca los archivos Java como empaquetados y filtrados. Maven sustituye package, groupId, artifactId y version. El arquetipo se instala en `.m2`; no es lo mismo que el microservicio generado. La base requiere que se configure una fuente de datos y variables de seguridad para ejecutarse; sus pruebas del Builder no necesitan Oracle.

Los proyectos `usuarios-service`, `foro-service` y `moderacion-service` fueron generados con paquetes diferentes y luego especializados. Los logs de generación demuestran ese proceso.

## Decisiones y límites

La comunicación moderación–foro es HTTP síncrona y reutiliza el JWT del administrador. El bloqueo deseado es idempotente y tiene tiempos máximos de conexión/lectura. Las bases son independientes y no existe transacción distribuida: si el foro acepta un cambio y luego falla la persistencia de la decisión, se debe reintentar y revisar consistencia. Un outbox con eventos sería una mejora posterior.

Los JWT vencen a los quince minutos. Cambiar rol, borrar usuario o restablecer contraseña no revoca un token previamente emitido; esa sesión puede continuar hasta su vencimiento. El panel de usuarios muestra las primeras cien cuentas. Las listas administrativas de contenido no tienen paginación; es una limitación de escala, no de la demostración local.

La recuperación usa 32 bytes aleatorios, guarda únicamente SHA-256 del código y limita su vigencia a quince minutos. La nueva contraseña se almacena con BCrypt. No hay rate limiting ni protección distribuida contra intentos automatizados; el alcance configurado es el laboratorio local.

Referencias técnicas: https://maven.apache.org/archetype/maven-archetype-plugin/ ; https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html ; https://docs.docker.com/compose/how-tos/networking/ ; https://github.com/gvenzl/oci-oracle-free
