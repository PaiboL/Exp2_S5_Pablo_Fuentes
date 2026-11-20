#!/bin/bash
set -e
createAppUser USUARIOS_APP "$APP_DB_PASSWORD"
createAppUser FORO_APP "$APP_DB_PASSWORD"
createAppUser MODERACION_APP "$APP_DB_PASSWORD"
for item in 'USUARIOS_APP usuarios' 'FORO_APP foro' 'MODERACION_APP moderacion'; do
  read -r schema script <<< "$item"
  sqlplus -s /nolog <<SQL
WHENEVER OSERROR EXIT FAILURE
WHENEVER SQLERROR EXIT SQL.SQLCODE
CONNECT ${schema}/"${APP_DB_PASSWORD}"@//localhost:1521/FREEPDB1
@/schema/${script}_install.sql
EXIT
SQL
done
