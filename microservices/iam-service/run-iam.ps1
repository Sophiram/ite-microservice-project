# Load .env variables
Get-Content "$PSScriptRoot\.env" | ForEach-Object {
    $line = $_.Trim()
    if ($line -and -not $line.StartsWith('#') -and $line.Contains('=')) {
        $parts = $line.Split('=', 2)
        $k = $parts[0].Trim()
        $v = $parts[1].Trim()
        [System.Environment]::SetEnvironmentVariable($k, $v, 'Process')
    }
}

$javaBin = "C:\Users\MSI\.jdks\temurin-24.0.2\bin\java.exe"
$jarPath = "$PSScriptRoot\iam-main\build\libs\iam-main-1.0.0.jar"
$logPath = "$PSScriptRoot\iam-service.log"
$errPath = "$PSScriptRoot\iam-service-err.log"

Write-Host "Starting IAM Service with $jarPath..."
Start-Process -FilePath $javaBin -ArgumentList "-jar `"$jarPath`"" -WorkingDirectory "D:\MicroserviceProject\istad-ite-project" -RedirectStandardOutput $logPath -RedirectStandardError $errPath
Write-Host "Process spawned. Logging to $logPath"
