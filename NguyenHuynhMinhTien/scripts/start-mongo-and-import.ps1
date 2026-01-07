<#
Start MongoDB (temporary) and run CSV import into `student_management.students`.
Usage:
  powershell -ExecutionPolicy Bypass -File .\scripts\start-mongo-and-import.ps1

Options:
  -DbPath     Path to DB folder (default: D:\data\db)
  -TimeoutSec Seconds to wait for mongod to listen (default: 20)

This script will:
- Locate mongod.exe on PATH (or via Get-Command)
- Create db directory if missing
- Start mongod as a background process
- Wait for port 27017 to be open
- Run `npm install` and `npm run import` in `mongodb_setup`
- Report import result

NOTE: This does not register MongoDB as a Windows service. Use the MSI installer if you want a service.
#>
param(
    [string]$DbPath = 'D:\data\db',
    [int]$TimeoutSec = 20
)

Write-Host "===== start-mongo-and-import.ps1 ====="

# 1) Find mongod
$mongodCmd = Get-Command mongod -ErrorAction SilentlyContinue
if (-not $mongodCmd) {
    # try common local paths
    $candidates = @(
        "$PSScriptRoot\..\..\mongod.exe",
        "$PSScriptRoot\..\mongod.exe",
        "C:\\Program Files\\MongoDB\\Server\\*/bin/mongod.exe"
    )
    $found = $null
    foreach ($c in $candidates) {
        try {
            $files = Get-ChildItem -Path $c -ErrorAction SilentlyContinue
            if ($files) { $found = $files[0].FullName; break }
        } catch {}
    }
    if ($found) { $mongod = $found } else { Write-Host "mongod not found. Install MongoDB or add mongod.exe to PATH." -ForegroundColor Red; exit 2 }
} else {
    $mongod = $mongodCmd.Path
}
Write-Host "Found mongod: $mongod"

# 2) Ensure DB path
if (-not (Test-Path $DbPath)) {
    Write-Host "Creating db path: $DbPath"
    New-Item -ItemType Directory -Force -Path $DbPath | Out-Null
}

# 3) Start mongod
Write-Host "Starting mongod with --dbpath $DbPath"
$startArgs = "--dbpath","$DbPath","--bind_ip","127.0.0.1"
$proc = Start-Process -FilePath $mongod -ArgumentList $startArgs -PassThru -WindowStyle Hidden
Write-Host "mongod started (PID=$($proc.Id)). Waiting for port 27017 up (timeout ${TimeoutSec}s)..."

# 4) Wait for port
$sw = [Diagnostics.Stopwatch]::StartNew()
$up = $false
while ($sw.Elapsed.TotalSeconds -lt $TimeoutSec) {
    $res = Test-NetConnection -ComputerName localhost -Port 27017 -WarningAction SilentlyContinue
    if ($res.TcpTestSucceeded) { $up = $true; break }
    Start-Sleep -Seconds 1
}
if (-not $up) {
    Write-Host "mongod did not open port 27017 within timeout. Check process log or run mongod foreground to see errors." -ForegroundColor Red
    Write-Host "You may need to run: & '$mongod' --dbpath '$DbPath'" -ForegroundColor Yellow
    exit 3
}
Write-Host "mongod is listening on 27017" -ForegroundColor Green

# 5) Run import script
$importFolder = Join-Path $PSScriptRoot "..\mongodb_setup" | Resolve-Path -ErrorAction SilentlyContinue
if (-not $importFolder) { Write-Host "mongodb_setup folder not found in repo. Aborting import." -ForegroundColor Red; exit 4 }
$importFolder = $importFolder.Path
Push-Location $importFolder
if (-not (Test-Path 'node_modules')) {
    Write-Host "Installing mongodb_setup dependencies..."
    npm install
}
Write-Host "Running import script..."
$importResult = npm run import 2>&1
Write-Host $importResult

# 6) Check count via mongosh if available
$mongosh = Get-Command mongosh -ErrorAction SilentlyContinue
if ($mongosh) {
    $uri = if ($env:MONGODB_URI) { $env:MONGODB_URI } else { 'mongodb://localhost:27017' }
    $dbName = if ($env:DB_NAME) { $env:DB_NAME } else { 'student_management' }
    try {
        $countOut = & $mongosh "$uri/$dbName" --quiet --eval "db.students.count()" 2>&1
        Write-Host "students count: $countOut"
    } catch {
        Write-Host "mongosh error while counting students: $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "mongosh not found: cannot show students count. You can open Compass or install mongosh to inspect DB." -ForegroundColor Yellow
}
Pop-Location

Write-Host "Import finished. Please restart backend server (cd backend; npm start) if it is running, then test: http://localhost:3000/api/students" -ForegroundColor Green
Write-Host "===== Done ====="