$ErrorActionPreference = 'Stop'
$destino = Join-Path $PSScriptRoot '..\.env'
if (Test-Path $destino) { throw '.env ya existe. Se conserva sin cambios.' }
function Nueva-Clave {
  $bytes = New-Object byte[] 12
  $rng = [Security.Cryptography.RandomNumberGenerator]::Create()
  $rng.GetBytes($bytes)
  $rng.Dispose()
  return 'NexoA9' + (($bytes | ForEach-Object { $_.ToString('x2') }) -join '')
}
$lineas = @(
  "JWT_SECRET=$(Nueva-Clave)$(Nueva-Clave)"
  "ORACLE_PASSWORD=$(Nueva-Clave)"
  "APP_DB_PASSWORD=$(Nueva-Clave)"
)
[IO.File]::WriteAllLines([IO.Path]::GetFullPath($destino), $lineas, (New-Object Text.UTF8Encoding($false)))
Write-Host 'Configuración creada. Ejecuta docker compose up --build -d'
