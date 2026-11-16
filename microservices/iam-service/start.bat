@echo off
set JAVA_HOME=C:\Users\MSI\.jdks\temurin-24.0.2
set PATH=%JAVA_HOME%\bin;%PATH%

for /f "usebackq tokens=1,* delims==" %%A in ("%~dp0.env") do (
    if not "%%A"=="" (
        set "%%A=%%B"
    )
)

cd /d "D:\MicroserviceProject\istad-ite-project"
start "IAM-Service" /B "C:\Users\MSI\.jdks\temurin-24.0.2\bin\javaw.exe" -jar "microservices\iam-service\iam-main\build\libs\iam-main-1.0.0.jar" > "%~dp0iam-service.log" 2>&1
