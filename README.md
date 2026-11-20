# Foro Nexo

Desarrollo Full Stack III DSY2205 · Experiencia 2 Semana 5 · Pablo Fuentes

Foro académico con Angular, tres microservicios Spring Boot, arquetipo Maven propio y configuración de Docker Compose con Oracle. Continúa el código de usuarios y foro de la sumativa de Semana 2, conservando sus entidades, contratos HTTP, seguridad JWT y reglas de autoría.

Repositorio y rama de esta entrega: https://github.com/PaiboL/Exp2_S5_Pablo_Fuentes/tree/entrega-dsy2205-s5

La rama principal del repositorio remoto contiene un proyecto anterior. Para revisar esta entrega, abre el enlace completo anterior o clona con `--branch entrega-dsy2205-s5`. El historial local se incluye en historial-desarrollo.bundle.

## Empezar en Windows

1. Extrae el ZIP y abre la carpeta del proyecto en VS Code.
2. Inicia Docker Desktop con contenedores Linux. Reserva recursos suficientes para Oracle y tres JVM; como punto de partida, 6 GB de memoria disponibles para Docker.
3. Abre PowerShell en la carpeta que contiene `compose.yaml`.
4. Ejecuta `powershell -ExecutionPolicy Bypass -File scripts/configurar.ps1`. Como alternativa, `python scripts/configurar.py`.
5. Ejecuta `docker compose config --quiet` y después `docker compose up --build -d`.
6. Consulta `docker compose ps` y `docker compose logs --tail=80 oracle usuarios foro moderacion`. El primer inicio de Oracle puede tardar varios minutos.
7. Abre http://localhost:8080. El buzón de prueba está en http://localhost:8025.

Cuentas de laboratorio: `admin@foro.local`, `moderador@foro.local`, `miembro@foro.local`. Contraseña de las tres: `ForoDemo2026!`. Los datos iniciales son demostrativos. Usa estas cuentas solo en la ejecución local prevista.

## Funcionalidades

- Registro, acceso, recuperación por código enviado al correo y actualización del perfil.
- Cinco categorías iniciales, búsqueda por título y filtro por categoría.
- Creación, lectura, edición y eliminación de temas y comentarios con control de autoría.
- Administración de usuarios, roles y categorías; mínimo de cinco categorías.
- Bloqueo y restauración de temas y comentarios mediante el servicio de moderación.
- Interfaz adaptable, formularios con validaciones, mensajes de error y estados de carga.

## Organización

| Carpeta | Contenido |
|---|---|
| `frontend` | Angular 20, Bootstrap 5, rutas, fachadas, servicios y pruebas |
| `microservicios` | Usuarios 8081, foro 8082, moderación 8083 |
| `arquetipo` | Base, fuente del arquetipo y paquete instalable |
| `database` | DDL Oracle, datos iniciales y migraciones separadas |
| `postman` | Colección original y ampliación S5; entorno local |
| `scripts` | Configuración local y verificación integrada |
| `qa` | Pruebas de navegador y dependencias de validación |
| `documentacion` | Informe, pasos de ejecución, pauta y guion Kaltura |
| `evidencias` | Resultados reales de compilación, pruebas y generación |

## Validación realizada y pendiente

Se comprobaron generación del arquetipo, compilación y 41 pruebas Java; compilación Angular y 5 pruebas Jasmine; integración HTTP entre los tres procesos Java, correo SMTP local y recorridos reales en Chromium. Estas pruebas usaron H2 con el perfil `demo`.

Oracle y Docker están configurados, pero no se ejecutaron en el entorno donde se preparó el paquete. Antes de enviar al AVA, ejecuta la configuración Oracle en Docker Desktop, comprueba persistencia tras reiniciar, guarda esa evidencia y graba tu presentación Kaltura de 7 a 10 minutos. El guion y la lista de comprobación están incluidos. H2 no constituye evidencia de persistencia Oracle.

La orientación del docente permite Docker Desktop local. La pauta escrita conserva referencias a Cloud y Container Registry; esta entrega no declara un despliegue cloud ni publicación de imágenes en un registry.

## Compilar sin Docker

Requisitos: JDK 17 completo, Maven 3.9, Node.js 22.12 o posterior compatible con Angular 20, npm y Git. Se incluye Maven Wrapper en cada servicio y en la base del arquetipo.

```bash
mvn -f microservicios/pom.xml clean verify
cd frontend
npm ci
npm run build
npm test
```

`npm test` requiere Chrome/Chromium; si no se encuentra, configura `CHROME_BIN` con su ruta. Instrucciones adicionales en `documentacion/01_Ejecucion.md`.

## Entrega

ZIP: `Exp2_S5_Pablo_Fuentes.zip`. Adjunta en el AVA el enlace Git completo de la rama y el enlace del video Kaltura. No envíes la actividad hasta comprobar ambos enlaces y las evidencias pendientes.
