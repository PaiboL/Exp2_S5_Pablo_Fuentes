# Validación automatizada

Desde la raíz del proyecto, con JDK, Node y Python instalados:

```bash
mvn -f microservicios/pom.xml clean verify
npm --prefix frontend ci
npm --prefix frontend run build
npm --prefix qa install
npx --prefix qa playwright install chromium
python -m pip install -r qa/requirements.txt
python scripts/verificar_demo.py
```

El servidor temporal `/__qa/mail-token` solo existe dentro del proceso de pruebas para recuperar el correo capturado. No se incluye en Nginx ni en ningún microservicio. Los tests crean datos en H2, que se descartan al detener los procesos. Ejecuta con los puertos de laboratorio libres.

En este entorno restringido se usó Chromium headless con opciones especiales. En un computador normal utiliza el navegador instalado por Playwright. Puedes establecer CHROME_BIN para utilizar otro ejecutable compatible.
