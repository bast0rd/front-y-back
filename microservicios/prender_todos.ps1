$dbPassword = $env:DB_PASSWORD

$services = @(
    @{ Name = "catalog";      Path = "catalog" },
    @{ Name = "reservations"; Path = "reservations" },
    @{ Name = "notify";       Path = "notify" },
    @{ Name = "audit";        Path = "audit" },
    @{ Name = "report";       Path = "report" },
    @{ Name = "bff"; Path = "bff" },
    @{ Name = "admin-rabbit"; path = "admin-rabbit"},
    @{ Name = "admin-kafka"; path = "admin-kafka"}
)

$root = $PSScriptRoot

foreach ($svc in $services) {
    $servicePath = Join-Path $root $svc.Path
    $title = $svc.Name

    Start-Process powershell -ArgumentList @(
        "-NoExit",
        "-Command",
        "cd '$servicePath'; `$host.ui.RawUI.WindowTitle = '$title'; `$env:DB_PASSWORD='$dbPassword'; ./mvnw spring-boot:run"
    )

    Start-Sleep -Seconds 2
}

Write-Host "Se lanzaron $($services.Count) microservicios, cada uno en su propia ventana."