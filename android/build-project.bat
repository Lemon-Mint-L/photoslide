@echo off
setlocal
set JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%
cd /d C:\Users\lsj\Desktop\opencode\photoslide\android
echo Building project...
call gradlew.bat assembleDebug
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ============================
    echo BUILD SUCCESSFUL!
    echo ============================
) else (
    echo.
    echo ============================
    echo BUILD FAILED!
    echo ============================
)
pause