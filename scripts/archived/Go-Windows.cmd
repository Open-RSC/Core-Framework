@echo off
SETLOCAL ENABLEEXTENSIONS

:# Open RSC: A replica RSC private server framework
:#
:# Multi-purpose script for Open RSC

set GREEN=
set RED=
set NC=


:<------------Begin Admin Permission Elevation------------>
REM  -- Check for permissions
>nul 2>&1 "%SYSTEMROOT%\system32\cacls.exe" "%SYSTEMROOT%\system32\config\system"

REM -- If error flag set, we do not have admin.
if '%errorlevel%' NEQ '0' (
    echo Requesting administrative privileges...
    goto UACPrompt
) else ( goto gotAdmin )

:UACPrompt
    echo Set UAC = CreateObject^("Shell.Application"^) > "%temp%\getadmin.vbs"
    set params = %*:"="
    echo UAC.ShellExecute "cmd.exe", "/c %~s0 %params%", "", "runas", 1 >> "%temp%\getadmin.vbs"

    "%temp%\getadmin.vbs"
    del "%temp%\getadmin.vbs"
    exit /B

:gotAdmin
    pushd "%CD%"
    CD /D "%~dp0"
:<------------End Admin Permission Elevation------------>


:<------------Begin Start------------>
:start
echo:
echo:
echo %RED%Open RSC:%NC%
echo An easy to use RSC private server framework.
echo:
echo What would you like to do?
echo:
echo Choices:
echo   %RED%1%NC% - Install
echo   %RED%2%NC% - Run
echo   %RED%3%NC% - Reset everything
echo   %RED%4%NC% - Exit
echo:
SET /P action=Please enter a number choice from above:
echo:

if /i "%action%"=="1" goto install
if /i "%action%"=="2" goto run
if /i "%action%"=="3" goto reset
if /i "%action%"=="4" exit

echo Error! %action% is not a valid option. Press enter to try again.
echo:
SET /P action=""
goto start
:<------------End Start------------>


:<------------Begin Install------------>
:install
echo:
echo:
echo Installing everything needed. This will take a while, please do not close the window.
echo:
@"%SystemRoot%\System32\WindowsPowerShell\v1.0\powershell.exe" -NoProfile -InputFormat None -ExecutionPolicy Bypass -Command "iex ((New-Object System.Net.WebClient).DownloadString('https://chocolatey.org/install.ps1'))" && SET "PATH=%PATH%;%ALLUSERSPROFILE%\chocolatey\bin"
choco install -y 7zip make openjdk ant bitnami-xampp
echo:
call "scripts/xampp-install.cmd"

@"%SystemRoot%\System32\WindowsPowerShell\v1.0\powershell.exe" -NoProfile -InputFormat None -ExecutionPolicy Bypass -Command "((Get-Content -path C:\xampp\phpMyAdmin\config.inc.php -Raw)) -replace \"password'] = ''\", \"password'] = 'root'\" | Set-Content -Path \"C:\xampp\phpMyAdmin\config.inc.php\""

call
call C:\xampp\mysql\bin\mysql -uroot -proot < Databases/openrsc_game.sql
echo:
echo:
echo Compiling the game client and server.
echo:
make compile-windows-simple
echo:
echo:
goto start
:<------------End Simple------------>


:<------------Start Run------------>
:run
echo:
echo:
echo Starting Open RSC.
echo:
make run-game-windows
echo:
goto start
:<------------End Run------------>


:<------------Start Reset------------>
:reset
echo:
echo:
echo Performing a hard reset.
echo:
make hard-reset-game-windows
call C:\xampp\mysql\bin\mysql -uroot -proot < Databases/openrsc_game.sql
goto start
:<------------End Reset------------>
