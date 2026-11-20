# Ejecución y verificación

## Docker Desktop con Oracle

La configuración principal usa Oracle Free, tres esquemas separados y un volumen persistente. Las credenciales se generan localmente en `.env`; este archivo no se incluye en Git ni en el ZIP.

```powershell
powershell -ExecutionPolicy Bypass -File scripts/configurar.ps1
docker compose config --quiet
docker compose up --build -d
docker compose ps
docker compose logs --tail=80 oracle usuarios foro moderacion
```

Comprueba los endpoints de salud: http://localhost:8081/actuator/health, http://localhost:8082/actuator/health y http://localhost:8083/actuator/health. Deben responder 200 y `UP`. La interfaz se abre en http://localhost:8080.

El navegador llama rutas relativas `/api/...`. Nginx dirige autenticación y usuarios a `usuarios:8081`, contenido a `foro:8082` y moderación a `moderacion:8083`. Dentro de Compose se utilizan nombres de servicio. `localhost` dentro de un contenedor se refiere a ese mismo contenedor.

Los puertos publicados quedan enlazados a 127.0.0.1 para la demostración local. La base recibe conexiones internas por `oracle:1521/FREEPDB1`.

## Base de datos

En un volumen vacío, `database/init/01_init.sh` crea USUARIOS_APP, FORO_APP y MODERACION_APP. Luego ejecuta:

- Usuarios: `01_usuarios_oracle.sql` y `04_reset_oracle.sql`.
- Foro: `02_foro_oracle.sql` y `05_bloqueo_foro_oracle.sql`.
- Moderación: `06_moderacion_oracle.sql`.

Los archivos `*_install.sql` usan rutas del contenedor. Para SQL Developer o SQLcl, ejecuta directamente los archivos numerados en el esquema correspondiente. Hibernate usa `ddl-auto=validate`; no crea silenciosamente tablas Oracle.

Para continuar sobre los esquemas existentes de Semana 2, realiza respaldo y aplica solamente 04 a usuarios, 05 a foro y 06 al nuevo esquema de moderación. No repitas 01 y 02 sobre tablas existentes.

Verifica los datos conectado a cada esquema:

```sql
SELECT id, nombre, email FROM us_usuarios ORDER BY id;
SELECT id, nombre FROM fo_categorias ORDER BY id;
SELECT id, titulo, bloqueado FROM fo_temas ORDER BY id;
SELECT id, tema_id, bloqueado FROM fo_comentarios ORDER BY id;
SELECT id, tipo, recurso_id, bloqueado FROM mo_decisiones ORDER BY id;
```

Cada consulta corresponde al esquema propietario de la tabla. Las cuentas de aplicación tienen acceso a su esquema, no a los otros dos.

Crea un tema desde Angular, anota su ID, compruébalo en SQL, ejecuta `docker compose restart` y confirma que persiste. El volumen conserva los datos. `docker compose down` detiene y retira contenedores; no elimina el volumen. No uses `down -v` si quieres conservar información.

## Recuperación de contraseña

Desde la pantalla de acceso, selecciona “Olvidé mi contraseña”, ingresa una cuenta existente y abre http://localhost:8025. Copia el código del correo al formulario, introduce una contraseña nueva y confírmala. Comprueba que la contraseña anterior ya no permite iniciar sesión.

Mailpit captura mensajes exclusivamente para laboratorio. No envía al buzón externo del destinatario. Para un entorno real se requiere un SMTP autenticado, TLS, configuración adicional y controles contra abuso.

## Modo de demostración H2

Se proporciona una configuración independiente:

```bash
docker compose -f compose.demo.yaml up --build -d
```

No ejecutes ambas configuraciones simultáneamente porque usan los mismos puertos. H2 guarda datos en memoria y los pierde cuando reinicia el proceso. Este modo permite practicar la presentación, pero no reemplaza la evidencia Oracle.

Sin Docker, compila y abre tres terminales:

```bash
mvn -f microservicios/pom.xml clean verify
java -jar microservicios/usuarios-service/target/usuarios-service-1.0.0.jar --spring.profiles.active=demo
java -jar microservicios/foro-service/target/foro-service-1.0.0.jar --spring.profiles.active=demo
java -jar microservicios/moderacion-service/target/moderacion-service-1.0.0.jar --spring.profiles.active=demo
```

Cada comando Java queda ejecutándose en una terminal diferente. Para recuperación necesitas un servidor SMTP local en 1025 o configurar `SMTP_HOST` y `SMTP_PORT`. En una cuarta terminal ejecuta `cd frontend`, `npm ci` y `npm start`; abre http://localhost:4200. El proxy de Angular enruta las APIs a los tres puertos.

## Postman

Importa las dos colecciones y `Local.postman_environment.json`. Selecciona el entorno local y ejecuta primero `Foro_DSY2205`, luego `S5_Moderacion`. Las solicitudes se ejecutan en orden; capturan tokens e IDs automáticamente y limpian los recursos temporales que crean. Utiliza solamente una base de laboratorio.

Con Newman:

```bash
npx newman run postman/Foro_DSY2205.postman_collection.json -e postman/Local.postman_environment.json --timeout-request 15000
npx newman run postman/S5_Moderacion.postman_collection.json -e postman/Local.postman_environment.json --timeout-request 15000
```

La recuperación completa con código real se prueba desde la interfaz y Mailpit. La colección comprueba el rechazo de código inválido y la respuesta genérica para cuenta inexistente.

## Prueba automatizada integrada

Después de compilar los JAR y Angular, instala las dependencias de `qa` y Python indicadas allí. `python scripts/verificar_demo.py` inicia tres procesos H2, un SMTP temporal y un servidor de prueba para el frontend; ejecuta Postman, controles HTTP y Chromium, y detiene los procesos al finalizar. Los puertos 1025, 4200 y 8081 a 8083 deben estar disponibles. Este procedimiento no ejecuta contenedores ni Oracle.

## Diagnóstico

Si falla la construcción, revisa acceso a los repositorios Maven/npm y los logs de build. Si Oracle no alcanza estado saludable, revisa memoria disponible y logs. Si un servicio informa una tabla inexistente, comprueba esquema, migraciones y perfil; evita ocultarlo cambiando Oracle a `create`.

Si cambia `.env` después del primer inicio de Oracle, las contraseñas ya guardadas no se actualizan por reiniciar. Conserva los valores iniciales o cambia las contraseñas explícitamente en la base y en la configuración. Un 401 indica sesión ausente, vencida o firma incorrecta; un 403 indica falta de permisos; un 404 puede indicar recurso inexistente o bloqueado.
