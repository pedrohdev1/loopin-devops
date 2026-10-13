@echo off
setlocal EnableExtensions
set "GRADLE_VERSION=8.14.3"
set "DIST_ROOT=%USERPROFILE%\.gradle\loopin-distributions"
set "GRADLE_HOME=%DIST_ROOT%\gradle-%GRADLE_VERSION%"
if not exist "%GRADLE_HOME%\bin\gradle.bat" (
  echo [Loopin] Baixando Gradle %GRADLE_VERSION% pela primeira vez...
  if not exist "%DIST_ROOT%" mkdir "%DIST_ROOT%"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; $zip=Join-Path $env:USERPROFILE '.gradle\loopin-distributions\gradle-%GRADLE_VERSION%-bin.zip'; Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile $zip; Expand-Archive -LiteralPath $zip -DestinationPath (Join-Path $env:USERPROFILE '.gradle\loopin-distributions') -Force; Remove-Item -LiteralPath $zip -Force"
  if errorlevel 1 (
    echo [Loopin] Nao foi possivel baixar o Gradle. Confira a conexao ou abra o projeto no Android Studio.
    exit /b 1
  )
)
call "%GRADLE_HOME%\bin\gradle.bat" %*
exit /b %ERRORLEVEL%
