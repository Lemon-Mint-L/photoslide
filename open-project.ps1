# 启动Android Studio并打开项目
# 运行此脚本打开photoslide项目

$studioPath = "C:\Program Files\Android\Android Studio\bin\studio64.exe"
$projectPath = "C:\Users\lsj\Desktop\opencode\photoslide\android"

if (Test-Path $studioPath) {
    Write-Host "正在启动 Android Studio..." -ForegroundColor Cyan
    Start-Process $studioPath -ArgumentList $projectPath
    Write-Host "✓ Android Studio 已启动" -ForegroundColor Green
    Write-Host "项目路径: $projectPath" -ForegroundColor Yellow
} else {
    Write-Host "✗ 未找到 Android Studio" -ForegroundColor Red
    Write-Host "请先安装 Android Studio" -ForegroundColor Yellow
}