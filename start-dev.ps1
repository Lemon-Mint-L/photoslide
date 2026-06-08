# photoslide 开发环境快速启动脚本
# 此脚本将启动Android Studio并打开项目

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  photoslide 开发环境快速启动" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# 检查Java
Write-Host "检查Java环境..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "✓ Java已安装: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ Java未安装" -ForegroundColor Red
    exit 1
}

# 检查Android Studio
Write-Host "`n检查Android Studio..." -ForegroundColor Yellow
$studioPath = "C:\Program Files\Android\Android Studio\bin\studio64.exe"
if (Test-Path $studioPath) {
    Write-Host "✓ Android Studio已安装" -ForegroundColor Green
} else {
    Write-Host "✗ Android Studio未安装" -ForegroundColor Red
    exit 1
}

# 检查项目目录
Write-Host "`n检查项目目录..." -ForegroundColor Yellow
$projectPath = "C:\Users\lsj\Desktop\opencode\photoslide\android"
if (Test-Path $projectPath) {
    Write-Host "✓ 项目目录存在" -ForegroundColor Green
} else {
    Write-Host "✗ 项目目录不存在" -ForegroundColor Red
    exit 1
}

# 启动Android Studio
Write-Host "`n正在启动Android Studio..." -ForegroundColor Yellow
Start-Process $studioPath -ArgumentList $projectPath
Write-Host "✓ Android Studio已启动" -ForegroundColor Green

Write-Host "`n=====================================" -ForegroundColor Cyan
Write-Host "  启动完成！" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "下一步操作：" -ForegroundColor Yellow
Write-Host "1. 等待Gradle同步完成" -ForegroundColor White
Write-Host "2. 创建模拟器 (Tools -> AVD Manager)" -ForegroundColor White
Write-Host "3. 点击Run按钮运行应用" -ForegroundColor White
Write-Host ""
Write-Host "如需帮助，请查看 SETUP.md 文件" -ForegroundColor Cyan