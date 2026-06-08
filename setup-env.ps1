# Android开发环境配置脚本
# 运行此脚本配置环境变量

# 设置JAVA_HOME
$javaHome = "C:\Program Files\Microsoft\jdk-17.0.19+10"
[Environment]::SetEnvironmentVariable("JAVA_HOME", $javaHome, "User")
Write-Host "✓ JAVA_HOME 已设置为: $javaHome" -ForegroundColor Green

# 设置ANDROID_HOME
$androidHome = "$env:LOCALAPPDATA\Android\Sdk"
[Environment]::SetEnvironmentVariable("ANDROID_HOME", $androidHome, "User")
Write-Host "✓ ANDROID_HOME 已设置为: $androidHome" -ForegroundColor Green

# 更新PATH
$currentPath = [Environment]::GetEnvironmentVariable("Path", "User")
$pathsToAdd = @(
    "$javaHome\bin",
    "$androidHome\platform-tools",
    "$androidHome\tools",
    "$androidHome\tools\bin"
)

foreach ($path in $pathsToAdd) {
    if ($currentPath -notlike "*$path*") {
        $currentPath = "$currentPath;$path"
    }
}

[Environment]::SetEnvironmentVariable("Path", $currentPath, "User")
Write-Host "✓ PATH 已更新" -ForegroundColor Green

Write-Host "`n环境配置完成！请重启终端或IDE使配置生效。" -ForegroundColor Cyan