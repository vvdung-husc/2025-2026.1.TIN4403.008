<#
PowerShell script để kiểm tra nhanh backend
Chạy: PowerShell -ExecutionPolicy Bypass -File .\scripts\check-backend.ps1
#>
param(
    [string]$BackendDir = 'backend'
)

Write-Host "--- Kiem tra Backend (PowerShell) ---`n"

# 1) Kiểm Node/npm
if (-not (Get-Command node -ErrorAction SilentlyContinue)) {
    Write-Host "ERROR: Node.js khong duoc tim thay trong PATH" -ForegroundColor Red
    exit 2
}
Write-Host "Node: $(node -v)"
Write-Host "npm: $(npm -v)" -ForegroundColor Green

# 2) Check thư mục dự án
if (-not (Test-Path $BackendDir)) {
    Write-Host "ERROR: Thu muc '$BackendDir' khong ton tai." -ForegroundColor Red
    exit 2
}
Push-Location $BackendDir

if (-not (Test-Path 'package.json')) {
    Write-Host "ERROR: package.json khong tim thay trong $BackendDir" -ForegroundColor Red
    Pop-Location; exit 2
}

$pkg = Get-Content package.json | ConvertFrom-Json
if (-not $pkg.scripts.start) {
    Write-Host "WARNING: package.json khong co script 'start'" -ForegroundColor Yellow
} else {
    Write-Host "Found start script: $($pkg.scripts.start)" -ForegroundColor Green
}

if (-not (Test-Path 'server.js')) {
    Write-Host "WARNING: server.js khong ton tai. Kiem tra file entry." -ForegroundColor Yellow
}

# 3) Cài dependencies nếu cần
if (-not (Test-Path 'node_modules')) {
    Write-Host "node_modules khong ton tai. Chay npm install..." -ForegroundColor Yellow
    npm install
}

# 4) Đọc biến env (nếu có)
Write-Host "MONGODB_URI (session): $env:MONGODB_URI"
Write-Host "DB_NAME (session): $env:DB_NAME"

# 4.5) Kiểm tra kết nối MongoDB và collection students
$mongoUri = if ($env:MONGODB_URI) { $env:MONGODB_URI } else { 'mongodb://localhost:27017' }
$mongoDb = if ($env:DB_NAME) { $env:DB_NAME } else { 'student_management' }
Write-Host "Kiem ket noi MongoDB: $mongoUri/$mongoDb"
$mongoshCmd = (Get-Command mongosh -ErrorAction SilentlyContinue).Path
if (-not $mongoshCmd) {
    Write-Host "mongosh not found in PATH; skipping DB connectivity check" -ForegroundColor Yellow
} else {
    try {
        $ping = & $mongoshCmd "$mongoUri/$mongoDb" --eval "db.adminCommand({ping:1})" 2>&1
        if ($ping -match '"ok"\s*:\s*1') { Write-Host "MongoDB ping OK" -ForegroundColor Green } else { Write-Host "MongoDB ping response: $ping" -ForegroundColor Yellow }
        $countOut = & $mongoshCmd "$mongoUri/$mongoDb" --quiet --eval "db.students.count()" 2>&1
        if ($countOut -match '\d+') { $count = [int]($matches[0]); Write-Host "students collection count: $count" -ForegroundColor Green } else { Write-Host "Could not read students count: $countOut" -ForegroundColor Yellow }
        if ($count -eq 0) {
            $ans = Read-Host "students collection is empty. Run import from mongodb_setup? (y/n)"
            if ($ans -match '^[yY]') {
                Push-Location "mongodb_setup"
                if (-not (Test-Path 'node_modules')) { Write-Host "Installing mongodb_setup dependencies..."; npm install }
                Write-Host "Running import script..."
                npm run import
                Pop-Location
                Write-Host "Import finished. Re-checking count..."
                $countOut = & $mongoshCmd "$mongoUri/$mongoDb" --quiet --eval "db.students.count()" 2>&1
                if ($countOut -match '\d+') { $count = [int]($matches[0]); Write-Host "students collection count: $count" -ForegroundColor Green }
            }
        }
    } catch {
        Write-Host "Error during mongosh: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 5) Start server tạm thời và kiểm tra endpoints
Write-Host "Khoi dong server..."
$nodePath = (Get-Command node).Path
$proc = Start-Process -FilePath $nodePath -ArgumentList 'server.js' -PassThru -WindowStyle Hidden
Start-Sleep -Seconds 4

# kiem root
try {
    $root = Invoke-RestMethod http://localhost:3000/ -TimeoutSec 5
    Write-Host "ROOT OK: $root" -ForegroundColor Green
} catch {
    Write-Host "ROOT failed: $($_.Exception.Message)" -ForegroundColor Red
}

# kiem /api/students
try {
    $students = Invoke-RestMethod http://localhost:3000/api/students -TimeoutSec 5
    if ($students -is [System.Array]) { Write-Host "/api/students returned $($students.Length) items" -ForegroundColor Green } else { Write-Host "/api/students returned data" -ForegroundColor Green }
} catch {
    Write-Host "/api/students failed: $($_.Exception.Message)" -ForegroundColor Red
}

# kiem socket port
try {
    $conn = Get-NetTCPConnection -LocalPort 3000 -ErrorAction SilentlyContinue
    if ($conn) { Write-Host "Port 3000 dang listen" -ForegroundColor Green } else { Write-Host "Port 3000 khong thay proses lang nghe" -ForegroundColor Yellow }
} catch {}

# Stop server
if ($proc -and $proc.Id) {
    Write-Host "Dung server (PID=$($proc.Id))..."
    Stop-Process -Id $proc.Id -Force -ErrorAction SilentlyContinue
}

Pop-Location
Write-Host "--- Kiem tra hoan tat ---"