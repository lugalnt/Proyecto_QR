@echo off
setlocal

REM ------------------------------
REM Configuración
REM ------------------------------
set "XAMPP_PATH=C:\xampp"
set "NGROK_HOSTNAME=humane-pelican-briefly.ngrok-free.app"
set "NGROK_PORT=80"

REM Si ngrok.exe NO está en PATH pon su ruta absoluta aquí:
set "NGROK_EXE="

REM Títulos de ventana únicos para poder cerrarlos después
set "APACHE_TITLE=XAMPP-Apache"
set "MYSQL_TITLE=XAMPP-MySQL"
set "NGROK_TITLE=XAMPP-ngrok"

REM ------------------------------
REM Comprobar procesos
REM ------------------------------
echo Comprobando procesos...
tasklist /FI "IMAGENAME eq httpd.exe" 2>NUL | find /I "httpd.exe" >NUL
if %ERRORLEVEL%==0 (set "APACHE_IS_RUNNING=1") else (set "APACHE_IS_RUNNING=0")

tasklist /FI "IMAGENAME eq mysqld.exe" 2>NUL | find /I "mysqld.exe" >NUL
if %ERRORLEVEL%==0 (set "MYSQL_IS_RUNNING=1") else (set "MYSQL_IS_RUNNING=0")

tasklist /FI "IMAGENAME eq ngrok.exe" 2>NUL | find /I "ngrok.exe" >NUL
if %ERRORLEVEL%==0 (set "NGROK_IS_RUNNING=1") else (set "NGROK_IS_RUNNING=0")

echo Apache running: %APACHE_IS_RUNNING% ; MySQL running: %MYSQL_IS_RUNNING% ; ngrok running: %NGROK_IS_RUNNING%

REM Si alguno está corriendo => ir a detenerlos
if "%APACHE_IS_RUNNING%"=="1" goto :stop_any
if "%MYSQL_IS_RUNNING%"=="1" goto :stop_any
if "%NGROK_IS_RUNNING%"=="1" goto :stop_any

goto :start_services

:stop_any
echo Deteniendo procesos que estaban corriendo...

REM Detener procesos (con /T para cerrar el árbol, lo que normalmente cierra las ventanas hijas)
if "%NGROK_IS_RUNNING%"=="1" call :kill_ngrok_proc
if "%APACHE_IS_RUNNING%"=="1" call :kill_apache_proc
if "%MYSQL_IS_RUNNING%"=="1" call :kill_mysql_proc

REM Además, forzamos el cierre de las ventanas cmd con los títulos que usamos al iniciar
echo Cerrando ventanas de consola asociadas (si existen)...
taskkill /F /FI "WINDOWTITLE eq %NGROK_TITLE%" /IM cmd.exe >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq %APACHE_TITLE%" /IM cmd.exe >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq %MYSQL_TITLE%" /IM cmd.exe >nul 2>&1

echo Procesos y ventanas detenidos. Pulsa una tecla para salir.
pause >nul
endlocal
exit /B 0

:kill_ngrok_proc
echo - Matando ngrok.exe...
taskkill /F /IM ngrok.exe /T >nul 2>&1
goto :eof

:kill_apache_proc
echo - Matando httpd.exe (Apache)...
taskkill /F /IM httpd.exe /T >nul 2>&1
goto :eof

:kill_mysql_proc
echo - Matando mysqld.exe (MySQL)...
taskkill /F /IM mysqld.exe /T >nul 2>&1
goto :eof

:start_services
echo Ningún servicio detectado corriendo. Iniciando XAMPP/Apache/MySQL...

REM Iniciar Apache (siempre lo abrimos en ventana con título único)
if exist "%XAMPP_PATH%\apache_start.bat" (
    echo Iniciando Apache con apache_start.bat...
    start "%APACHE_TITLE%" cmd /k "%XAMPP_PATH%\apache_start.bat"
) else (
    if exist "%XAMPP_PATH%\xampp_start.exe" (
        echo Iniciando xampp_start.exe...
        start "%APACHE_TITLE%" "%XAMPP_PATH%\xampp_start.exe"
    ) else (
        if exist "%XAMPP_PATH%\apache\bin\httpd.exe" (
            echo Iniciando httpd.exe directamente...
            start "%APACHE_TITLE%" "%XAMPP_PATH%\apache\bin\httpd.exe"
        ) else (
            echo No se encontró binario de Apache en %XAMPP_PATH%.
        )
    )
)

REM Iniciar MySQL (en ventana con título único)
if exist "%XAMPP_PATH%\mysql_start.bat" (
    echo Iniciando MySQL con mysql_start.bat...
    start "%MYSQL_TITLE%" cmd /k "%XAMPP_PATH%\mysql_start.bat"
) else (
    if exist "%XAMPP_PATH%\mysql\bin\mysqld.exe" (
        echo Iniciando mysqld.exe directamente...
        start "%MYSQL_TITLE%" "%XAMPP_PATH%\mysql\bin\mysqld.exe"
    ) else (
        echo No se encontró binario de MySQL en %XAMPP_PATH%.
    )
)

REM Esperar a que arranquen
echo Esperando a que los servicios se inicien (8s)...
timeout /t 8 /nobreak >nul

REM Abrir navegador en el proyecto
echo Abriendo navegador en http://localhost/Proyecto_QR/ ...
start "" "http://localhost/Proyecto_QR/"

REM Iniciar ngrok en nueva ventana con título único
echo Iniciando ngrok...
if defined NGROK_EXE (
    if exist "%NGROK_EXE%" (
        start "%NGROK_TITLE%" cmd /k "%NGROK_EXE% http --url=%NGROK_HOSTNAME% %NGROK_PORT%"
    ) else (
        echo NGROK_EXE configurado pero no existe: %NGROK_EXE%
        echo Intentando ejecutar 'ngrok' desde PATH...
        start "%NGROK_TITLE%" cmd /k "ngrok http --url=%NGROK_HOSTNAME% %NGROK_PORT%"
    )
) else (
    if exist "%XAMPP_PATH%\ngrok.exe" (
        start "%NGROK_TITLE%" cmd /k "%XAMPP_PATH%\ngrok.exe http --url=%NGROK_HOSTNAME% %NGROK_PORT%"
    ) else (
        start "%NGROK_TITLE%" cmd /k "ngrok http --url=%NGROK_HOSTNAME% %NGROK_PORT%"
    )
)

echo Listo.
endlocal
exit /B 0
