
$ErrorActionPreference = "Stop"

$appsDir  = $PSScriptRoot
$root     = (Resolve-Path (Join-Path $appsDir "..\..")).Path
$backend  = Join-Path $root "microservicios"
$frontend = Join-Path $root "andesstay-frontend"
$jarsDir  = Join-Path $appsDir "jars"
$services = @("bff", "catalog", "reservations", "notify", "report", "audit", "admin-rabbit", "admin-kafka")

Write-Host "== 1/3 Empaquetando microservicios (tarda unos minutos)"
foreach ($s in $services) {
    Write-Host " -> $s"
    Push-Location (Join-Path $backend $s)
    try {
        & .\mvnw.cmd -q -DskipTests clean package
        if ($LASTEXITCODE -ne 0) { throw "Fallo el empaquetado de $s" }
        $jar = Get-ChildItem "target\*.jar" | Where-Object { $_.Name -notlike "*original*" } | Select-Object -First 1
        if (-not $jar) { throw "No se encontro el .jar de $s" }
        $dest = Join-Path $jarsDir $s
        New-Item -ItemType Directory -Force $dest | Out-Null
        Copy-Item $jar.FullName (Join-Path $dest "app.jar") -Force
        Copy-Item (Join-Path $appsDir "Dockerfile") (Join-Path $dest "Dockerfile") -Force
    }
    finally { Pop-Location }
}

Write-Host "== 2/3 Copiando la wallet de Oracle"
$walletDest = Join-Path $appsDir "wallet"
New-Item -ItemType Directory -Force $walletDest | Out-Null
Copy-Item (Join-Path $backend "wallet\*") $walletDest -Recurse -Force

Write-Host "== 3/3 Compilando el frontend"
Push-Location $frontend
try {
    "VITE_BFF_URL=" | Set-Content ".env.production" -Encoding ascii
    & npm run build
    if ($LASTEXITCODE -ne 0) { throw "Fallo npm run build" }
}
finally { Pop-Location }

$distDest = Join-Path $appsDir "front\dist"
if (Test-Path $distDest) { Remove-Item $distDest -Recurse -Force }
Copy-Item (Join-Path $frontend "dist") $distDest -Recurse

Write-Host ""
Write-Host "Listo. Ahora sube la carpeta infra\apps a la EC2 (ver la guia, paso 6)."
