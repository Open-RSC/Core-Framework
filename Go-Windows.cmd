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
echo   %RED%2%NC% - Update and Compile
echo   %RED%3%NC% - Run
echo   %RED%4%NC% - Manage Players
echo   %RED%5%NC% - Perform a Hard Reset
echo   %RED%6%NC% - Change Edition
echo   %RED%7%NC% - Exit
echo:
SET /P action=Please enter a number choice from above:
echo:

if /i "%action%"=="1" goto install
if /i "%action%"=="2" goto update
if /i "%action%"=="3" goto run
if /i "%action%"=="4" goto manage
if /i "%action%"=="5" goto reset
if /i "%action%"=="6" goto edition
if /i "%action%"=="7" exit

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
choco install -y 7zip make jdk8 ant bitnami-xampp
echo:
START C:\xampp\xampp-control.exe
START C:\xampp\apache\apache_installservice.bat
START C:\xampp\mysql\mysql_installservice.bat
call C:\xampp\mysql\bin\mysql -u root < Databases/openrsc_game.sql
call C:\xampp\mysql\bin\mysql -u root < Databases/openrsc_forum.sql
echo:
goto edition
:<------------End Install Everything------------>


:<------------Begin Edition------------>
:edition
echo:
echo:
echo Which edition of Open RSC would you like?
echo:
echo Choices:
echo  %RED%1%NC% - Single player mode (only the bare minimum needed to play)
echo  %RED%2%NC% - Developer mode (everything is included)
echo:
SET /P edition=Please enter a number choice from above:
echo:

if /i "%edition%"=="1" goto simple
if /i "%edition%"=="2" goto developer

echo Error! %edition% is not a valid option. Press enter to try again.
echo:
SET /P edition=""
goto edition
:<------------End Edition------------>


:<------------Begin Simple------------>
:simple
echo:
echo:
echo Compiling the game client and server.
echo:
make compile-windows-simple
echo:
echo:
echo Importing the game and forum databases.
echo:
make import-game-windows
echo:
goto start
:<------------End Simple------------>


:<------------Start Developer------------>
:developer
echo:
echo:
echo Would you like to install Git Kraken? It simplifies git!
echo:
echo Choices:
echo   %RED%1%NC% - Yes
echo   %RED%2%NC% - I'm all set, lets continue!
echo:
SET /P gitkraken=Please enter a number choice from above:
echo:
if /i "%gitkraken%"=="1" goto installgitkraken
if /i "%gitkraken%"=="2" goto askide

echo Error! %gitkraken% is not a valid option. Press enter to try again.
echo:
SET /P gitkraken=""
goto developer


:installgitkraken
echo:
echo:
echo Installing Git Kraken.
echo:
choco install -y gitkraken
echo:
goto askide


:askide
echo:
echo:
echo Would you like a programming IDE installed for editing code?
echo:
echo Choices:
echo   %RED%1%NC% - Install NetBeans (recommended)
echo   %RED%2%NC% - Install IntelliJ IDEA Community Edition
echo   %RED%3%NC% - Install Eclipse
echo   %RED%4%NC% - I'm all set, continue!
echo:
SET /P git=Please enter a number choice from above:
echo:
if /i "%git%"=="1" goto installnetbeans
if /i "%git%"=="2" goto installintellij
if /i "%git%"=="3" goto installeclipse
if /i "%git%"=="4" goto developerstart

echo Error! %git% is not a valid option. Press enter to try again.
echo:
SET /P git=""
goto askide


:installnetbeans
echo:
choco install -y netbeans
echo:
goto askide


:installintellij
echo:
choco install -y intellijidea-community
echo:
goto askide


:installeclipse
echo:
choco install -y eclipse
echo:
goto askide


:developerstart
echo:
echo:
echo Downloading a copy of the Website repository
echo:
make clone-website-windows
echo:
echo:
echo Importing the game and forum databases.
echo:
make import-game-windows
make import-forum-windows
make fix-forum-permissions-windows
echo:
echo:
echo Compiling the game client and server.
echo:
make compile-windows-developer
echo:
goto start
:<------------End Developer------------>


:<------------Begin Update------------>
:update
echo:
echo:
echo Checking for updates
echo:
git pull
make pull-website-windows:
echo:
echo:
echo Compiling the game client and server.
echo:
make compile-windows-developer
echo:
goto start
:<------------End Update------------>


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


:<------------Start Manage------------>
:manage
echo:
echo:
echo Manage Players
echo:
goto start
:<------------End Manage------------>


:<------------Start Reset------------>
:reset
echo:
echo:
echo Performing a hard reset.
echo:
make hard-reset-game-windows
make hard-reset-website-windows
goto start
:<------------End Reset------------>
