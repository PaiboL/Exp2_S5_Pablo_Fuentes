# Versionado del proyecto

Enlace directo de entrega: https://github.com/PaiboL/Exp2_S5_Pablo_Fuentes/tree/entrega-dsy2205-s5

El repositorio remoto conserva un proyecto anterior en main. La rama `entrega-dsy2205-s5` contiene el foro DSY2205 de Semana 5. La rama `base-foro-s2-dsy2205` conserva el punto de partida. No se reemplaza la rama principal anterior.

El historial local de construcción parte del commit `335f488` del foro de Semana 2, utiliza `feature/semana5` y se integra en main local después de las verificaciones. El archivo `historial-desarrollo.bundle` conserva ese historial original para consulta. El historial remoto incorpora la base S2 y la evolución S5 en commits de publicación; sus identificadores difieren del historial local incluido en el ZIP.

Para obtener el código publicado:

```bash
git clone --branch entrega-dsy2205-s5 https://github.com/PaiboL/Exp2_S5_Pablo_Fuentes.git foro-nexo
cd foro-nexo
git log --oneline --graph --decorate -12
```

Para consultar el historial de construcción que acompaña al ZIP:

```bash
git clone historial-desarrollo.bundle historial-local
git -C historial-local log --oneline --graph --all
```

Para continuar trabajando, crea una rama por funcionalidad, comprueba los cambios, ejecuta las pruebas pertinentes y realiza commits pequeños con propósito claro. Integra mediante revisión y evita publicar credenciales, dependencias instaladas o datos personales. No se ha inventado una colaboración con otras personas; la actividad es individual.
