"""Genera secretos locales, sin sobrescribir una configuración existente."""
from pathlib import Path
import secrets
root=Path(__file__).resolve().parents[1]
p=root/'.env'
if p.exists():raise SystemExit('.env ya existe; se conserva sin cambios.')
# Alfanuméricos para SQL*Plus, sin comillas ni caracteres de sustitución.
password=lambda:'NexoA9'+secrets.token_hex(12)
p.write_text('JWT_SECRET='+secrets.token_hex(32)+'\nORACLE_PASSWORD='+password()+'\nAPP_DB_PASSWORD='+password()+'\n')
p.chmod(0o600)
print('Configuración creada. Continúa con docker compose up --build -d.')
