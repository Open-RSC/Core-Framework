@echo off
:# Open RuneScape Classic: Striving for a replica RSC game and more

:# Variable paths:
SET Portable_Windows="Portable_Windows\"
SET mariadbpath="Portable_Windows\mariadb-10.5.8-winx64\bin\"

:<------------Begin Start------------>
REM Initial menu displayed to the user
:start
cls
echo:
echo What would you like to do?
echo:
echo Choices:
echo   %RED%1%NC% - Compile and start the game
echo   %RED%2%NC% - Start the game (faster if already compiled)
echo   %RED%3%NC% - Change a player's in-game role
echo   %RED%4%NC% - Backup database
echo   %RED%5%NC% - Restore database
echo   %RED%6%NC% - Perform a fresh install
echo   %RED%7%NC% - Exit
echo:
SET /P action=Please enter a number choice from above:
echo:

if /i "%action%"=="1" goto compileandrun
if /i "%action%"=="2" goto run
if /i "%action%"=="3" goto role
if /i "%action%"=="4" goto backup
if /i "%action%"=="5" goto import
if /i "%action%"=="6" goto reset
if /i "%action%"=="7" goto exit

echo Error! %action% is not a valid option. Press enter to try again.
echo:
SET /P action=""
goto start
:<------------End Start------------>


:<------------Begin Exit------------>
:exit
REM Shuts down existing processes
taskkill /F /IM Java*
taskkill /F /IM mysqld*
exit
:<------------End Exit------------>


:<------------Begin Compile and Run------------>
:compileandrun
cls
echo:
echo Starting Open RuneScape Classic.
echo:
cd Portable_Windows && call START "" compileandrun.cmd && cd ..
echo:
goto start
:<------------End Compile and Run------------>


:<------------Begin Run------------>
:run
cls
echo:
echo Starting Open RuneScape Classic.
echo:
cd Portable_Windows && call START "" run.cmd && cd ..
echo:
goto start
:<------------End Run------------>


:<------------Begin Role------------>
:role
cls
echo:
echo What would role should the player be set to?
echo:
echo Choices:
echo   %RED%1%NC% - Admin
echo   %RED%2%NC% - Mod
echo   %RED%3%NC% - Regular Player
echo   %RED%4%NC% - Return
echo:
SET /P role=Please enter a number choice from above:
echo:

if /i "%role%"=="1" goto admin
if /i "%role%"=="2" goto mod
if /i "%role%"=="3" goto regular
if /i "%role%"=="4" goto start

echo Error! %role% is not a valid option. Press enter to try again.
echo:
SET /P role=""
goto start

:admin
echo:
echo Make sure you are logged out first!
echo Type the username of the player you wish to set as an admin and press enter.
echo:
SET /P username=""
echo:
echo Type the name of the database where the player is saved. (Generally named "preservation", "openrsc", or "cabbage")
echo:
SET /P db=""
call START "" %mariadbpath%mysqld.exe --console
echo Player update will occur in 5 seconds (gives time to start the database server on slow PCs)
PING localhost -n 6 >NUL
call %mariadbpath%mysql.exe -uroot -proot  -D %db% -e "USE %db%; UPDATE `players` SET `group_id` = '0' WHERE `players`.`username` = '%username%';"
echo:
echo %username% has been made an admin in database %db%!
echo:
pause
goto start

:mod
echo:
echo Make sure you are logged out first!
echo Type the username of the player you wish to set as a mod and press enter.
echo:
SET /P username=""
echo:
echo Type the name of the database where the player is saved. (Generally named "preservation", "openrsc", or "cabbage")
echo:
SET /P db=""
call START "" %mariadbpath%mysqld.exe --console
echo Player update will occur in 5 seconds (gives time to start the database server on slow PCs)
PING localhost -n 6 >NUL
call %mariadbpath%mysql.exe -uroot -proot -D %db% -e "USE %db%; UPDATE `players` SET `group_id` = '2' WHERE `players`.`username` = '%username%';"
echo:
echo %username% has been made a mod in database %db%!
echo:
pause
goto start

:regular
echo:
echo Make sure you are logged out first!
echo Type the username of the player you wish to set as a regular player and press enter.
echo:
SET /P username=""
echo:
echo Type the name of the database where the player is saved. (Generally named "preservation", "retrorsc", "openrsc", "cabbage", "uranium", or "coleslaw")
echo:
SET /P db=""
call START "" %mariadbpath%mysqld.exe --console
echo Player update will occur in 5 seconds (gives time to start the database server on slow PCs)
PING localhost -n 6 >NUL
call %mariadbpath%mysql.exe -uroot -proot -D %db% -e "USE %db%; UPDATE `players` SET `group_id` = '10' WHERE `players`.`username` = '%username%';"
echo:
echo %username% has been made an admin in database %db%!
echo:
pause
goto start
:<------------End Role------------>


:<------------Begin Backup------------>
:backup
REM Shuts down existing processes
taskkill /F /IM Java*

REM Performs a full database export
cls
echo:
echo Type the name of the database you wish to back up. (Generally named "preservation", "retrorsc", "openrsc", "cabbage", "uranium", or "coleslaw")
echo:
SET /P db=""
call START "" %mariadbpath%mysqld.exe --console
call START "" %mariadbpath%mysqldump.exe -uroot -proot --database %db% --result-file="Backups/%db%-%DATE:~-4%-%DATE:~4,2%-%DATE:~7,2%T%time:~-11,2%-%time:~-8,2%-%time:~-5,2%.sql"
echo:
echo Player database backup complete.
echo:
pause
goto start
:<------------End Backup------------>


:<------------Begin Import------------>
:import
REM Shuts down existing processes
taskkill /F /IM Java*

cls
REM Performs a full database import
echo:
dir /B *.sql
echo:
echo Type the full name of the database listed above that you wish to restore.
echo:
SET /P filename=""
echo:
echo Which database should this be restored to? (Generally named "preservation", "retrorsc", "openrsc", "cabbage", "uranium", or "coleslaw")
echo:
SET /P db=""
call START "" %mariadbpath%mysqld.exe --console
call %mariadbpath%mysql.exe -uroot -proot %db% < "Backups/%filename%.sql"
echo:
echo Player database restore complete.
echo:
pause
goto start
:<------------End Import------------>


:<------------Begin Fresh Install------------>
:reset
REM Shuts down existing processes
taskkill /F /IM Java*

REM Verifies the user wishes to clear existing player data
cls
echo:
echo Are you ABSOLUTELY SURE that you want to perform a fresh install and reset any existing game databases?
echo:
echo To confirm the database reset, type yes and press enter.
echo:
SET /P confirmwipe=""
echo:
if /i "%confirmwipe%"=="yes" goto wipe

echo Error! %confirmwipe% is not a valid option.
pause
goto start

:wipe
REM Starts up the database server and imports both server and database files to replace anything previously existing
call START "" %mariadbpath%mysqld.exe --console
cls
call %mariadbpath%mysqlcheck -uroot -proot -o --all-databases
cls
call %mariadbpath%mysql.exe -uroot -proot -e "DROP DATABASE IF EXISTS preservation;"
call %mariadbpath%mysql.exe -uroot -proot -e "DROP DATABASE IF EXISTS openrsc;"
call %mariadbpath%mysql.exe -uroot -proot -e "DROP DATABASE IF EXISTS cabbage;"
call %mariadbpath%mysql.exe -uroot -proot -e "DROP DATABASE IF EXISTS retrorsc;"
call %mariadbpath%mysql.exe -uroot -proot -e "DROP DATABASE IF EXISTS uranium;"
call %mariadbpath%mysql.exe -uroot -proot -e "DROP DATABASE IF EXISTS coleslaw;"

call %mariadbpath%mysql.exe -uroot -proot -e "CREATE DATABASE `preservation`;"
call %mariadbpath%mysql.exe -uroot -proot -e "CREATE DATABASE `openrsc`;"
call %mariadbpath%mysql.exe -uroot -proot -e "CREATE DATABASE `cabbage`;"
call %mariadbpath%mysql.exe -uroot -proot -e "CREATE DATABASE `retrorsc`;"
call %mariadbpath%mysql.exe -uroot -proot -e "CREATE DATABASE `uranium`;"
call %mariadbpath%mysql.exe -uroot -proot -e "CREATE DATABASE `coleslaw`;"

call %mariadbpath%mysql.exe -uroot -proot preservation < Databases\core.sql
call %mariadbpath%mysql.exe -uroot -proot openrsc < Databases\core.sql
call %mariadbpath%mysql.exe -uroot -proot cabbage < Databases\core.sql
call %mariadbpath%mysql.exe -uroot -proot retrorsc < Databases\retro.sql
call %mariadbpath%mysql.exe -uroot -proot uranium < Databases\core.sql
call %mariadbpath%mysql.exe -uroot -proot coleslaw < Databases\core.sql

call %mariadbpath%mysql.exe -uroot -proot cabbage < Databases/Addons/add_auctionhouse.sql
call %mariadbpath%mysql.exe -uroot -proot cabbage < Databases/Addons/add_bank_presets.sql
call %mariadbpath%mysql.exe -uroot -proot cabbage < Databases/Addons/add_clans.sql
call %mariadbpath%mysql.exe -uroot -proot cabbage < Databases/Addons/add_custom_items.sql
call %mariadbpath%mysql.exe -uroot -proot cabbage < Databases/Addons/add_custom_npcs.sql
call %mariadbpath%mysql.exe -uroot -proot cabbage < Databases/Addons/add_custom_objects.sql
call %mariadbpath%mysql.exe -uroot -proot cabbage < Databases/Addons/add_equipment_tab.sql
call %mariadbpath%mysql.exe -uroot -proot cabbage < Databases/Addons/add_harvesting.sql
call %mariadbpath%mysql.exe -uroot -proot cabbage < Databases/Addons/add_ironman.sql
call %mariadbpath%mysql.exe -uroot -proot cabbage < Databases/Addons/add_npc_kill_counting.sql
call %mariadbpath%mysql.exe -uroot -proot cabbage < Databases/Addons/add_runecraft.sql

call %mariadbpath%mysql.exe -uroot -proot coleslaw < Databases/Addons/add_auctionhouse.sql
call %mariadbpath%mysql.exe -uroot -proot coleslaw < Databases/Addons/add_bank_presets.sql
call %mariadbpath%mysql.exe -uroot -proot coleslaw < Databases/Addons/add_clans.sql
call %mariadbpath%mysql.exe -uroot -proot coleslaw < Databases/Addons/add_custom_items.sql
call %mariadbpath%mysql.exe -uroot -proot coleslaw < Databases/Addons/add_custom_npcs.sql
call %mariadbpath%mysql.exe -uroot -proot coleslaw < Databases/Addons/add_custom_objects.sql
call %mariadbpath%mysql.exe -uroot -proot coleslaw < Databases/Addons/add_equipment_tab.sql
call %mariadbpath%mysql.exe -uroot -proot coleslaw < Databases/Addons/add_harvesting.sql
call %mariadbpath%mysql.exe -uroot -proot coleslaw < Databases/Addons/add_ironman.sql
call %mariadbpath%mysql.exe -uroot -proot coleslaw < Databases/Addons/add_npc_kill_counting.sql
call %mariadbpath%mysql.exe -uroot -proot coleslaw < Databases/Addons/add_runecraft.sql

echo:
echo Fresh install complete!
echo:
pause
goto start
:<------------End Fresh Install------------>