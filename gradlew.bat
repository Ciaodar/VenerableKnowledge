@rem
@rem Build runner for VenerableKnowledge 1.20.x (Fabric & Forge)
@rem
@echo off
setlocal

set "DIR=%~dp0"

if "%JAVA_HOME%"=="" (
    if exist "C:\Program Files\Java\jdk-21" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-21"
    )
)

echo ========================================================
echo Building Venerable Knowledge (Fabric 1.20.x)...
echo ========================================================
cd /d "%DIR%fabric"
call gradlew.bat %*
if errorlevel 1 (
    echo [ERROR] Fabric build failed.
    exit /b 1
)

echo ========================================================
echo Building Venerable Knowledge (Forge 1.20.x)...
echo ========================================================
cd /d "%DIR%forge"
call gradlew.bat %*
if errorlevel 1 (
    echo [ERROR] Forge build failed.
    exit /b 1
)

echo ========================================================
echo All 1.20.x builds completed successfully!
echo ========================================================
exit /b 0
