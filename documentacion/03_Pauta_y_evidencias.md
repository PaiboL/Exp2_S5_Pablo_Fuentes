# Correspondencia con la pauta

La implementación responde al caso de foro online de las instrucciones específicas. Las cinco categorías corresponden a las áreas del foro; no se sustituyó el caso por un catálogo comercial genérico.

| Criterio | Peso | Implementación y evidencia | Pendiente antes de enviar |
|---|---:|---|---|
| Git patrones y arquetipo | 5 | Historial original, rama de trabajo, Builder, Facade, arquetipo instalado y tres generaciones registradas | Abrir enlace Git como evaluador |
| Diseño adaptable | 5 | Bootstrap, grilla y CSS; navegador a 375, 768 y 1280 px | Mostrar tamaños en video |
| Formularios | 10 | Registro, acceso, recuperación, perfil, temas, comentarios y administración | Demostración grabada |
| CSS | 5 | Identidad visual, estados, contraste y reglas por ancho | Demostración grabada |
| Lógica Angular | 20 | Rutas, guards, fachadas, interceptor y llamadas a APIs reales | Recorrido en ejecución Docker |
| Microservicios | 20 | Usuarios y foro conservados; moderación añadida; 41 pruebas Java | Mostrar APIs y reglas de negocio |
| Persistencia Oracle | 12 | Tres esquemas, DDL, migraciones y perfil validate | Ejecutar Oracle y demostrar persistencia |
| Docker | 16 | Dockerfiles y Compose con dependencias y salud | Construir imágenes y ejecutar integración local |
| Presentación | 7 | Guion de aproximadamente nueve minutos | Grabar en Kaltura y anexar URL |

Los porcentajes no constituyen una calificación. La evaluación depende de la ejecución y las evidencias solicitadas por el docente.

## Evidencia producida

- `01_crear_arquetipo.log`: generación desde la base.
- `02_instalar_arquetipo.log`: instalación local de la plantilla.
- `03_generar_*.log`: creación de usuarios, foro y moderación.
- `06_backend_verificacion.log`: compilación y pruebas Java.
- `07_angular_build.log`: compilación de producción con carga diferida.
- `08_angular_pruebas.log`: cinco pruebas Jasmine en Chromium.
- `09_postman_newman.log`: colección original, 55 solicitudes y 114 aserciones.
- `09_postman_s5.log`: ampliación S5 de moderación y recuperación.
- `09_integracion_http.json`: tres procesos Java, estado de bloqueo y correo SMTP.
- `10_navegador.json`: recorridos completos de la interfaz y tamaños de pantalla.

Los logs Maven fueron obtenidos con Java 21 y compilación source/target 17 porque el JDK del entorno no incluía el archivo ct.sym. En un JDK 17 completo, los comandos normales del README usan release 17. Las pruebas Java de persistencia usan H2; no se presentan como pruebas Oracle.

## Evidencias que debe producir Pablo

1. `docker compose ps` con los servicios funcionando.
2. Logs de inicio y salud de los tres servicios conectados a Oracle.
3. Consultas SQL con registro creado, modificado y eliminado desde la interfaz o Postman.
4. Registro que persiste después de reiniciar los contenedores sin borrar el volumen.
5. Postman sobre la ejecución Oracle, con resultados y estados HTTP visibles.
6. Video Kaltura de 7 a 10 minutos con voz y demostración propia.

No se incluye un video simulado ni se declara un despliegue cloud. Según el anuncio del docente, se preparó Docker Desktop local; confirma esa indicación frente al apartado cloud de la pauta al registrar la entrega.
