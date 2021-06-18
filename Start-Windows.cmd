@echo off
:# Open RuneScape Classic: Striving for a replica RSC game and more

:# Variable paths:
SET Portable_Windows="Portable_Windows\"
SET sqlitepath="Portable_Windows\"

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
echo   %RED%4%NC% - Change a player's name
echo   %RED%5%NC% - Backup database
echo   %RED%6%NC% - Restore database
echo   %RED%7%NC% - Perform a fresh install
echo   %RED%8%NC% - Exit
echo:
SET /P action=Please enter a number choice from above:
echo:

if /i "%action%"=="1" goto compileandrun
if /i "%action%"=="2" goto run
if /i "%action%"=="3" goto role
if /i "%action%"=="4" goto name
if /i "%action%"=="5" goto backup
if /i "%action%"=="6" goto import
if /i "%action%"=="7" goto reset
if /i "%action%"=="8" goto exit

echo Error! %action% is not a valid option. Press enter to try again.
echo:
SET /P action=""
goto start
:<------------End Start------------>


:<------------Begin Exit------------>
:exit
REM Shuts down existing java processes
taskkill /F /IM Java*
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
cls
echo:
echo Make sure you are logged out first!
echo Type the username of the player you wish to set and press enter.
echo:
SET /P username=""
cls
echo Type the name of the database where the player is saved.
echo:
echo (preservation, openrsc, cabbage, uranium, coleslaw, 2001scape, or openpk)
echo:
echo The default player database is named preservation.
echo:
echo:
SET /P db=""
cls
echo UPDATE `players` SET `group_id` = '0' WHERE `players`.`username` = '%username%' | %sqlitepath%sqlite3.exe .\server\inc\sqlite\%db%.db
echo:
echo %username% has been made an admin in database %db%!
echo:
pause
goto start

:mod
cls
echo:
echo Make sure you are logged out first!
echo Type the username of the player you wish to set and press enter.
echo:
SET /P username=""
cls
echo Type the name of the database where the player is saved.
echo:
echo (preservation, openrsc, cabbage, uranium, coleslaw, 2001scape, or openpk)
echo:
echo The default player database is named preservation.
echo:
echo:
SET /P db=""
cls
echo UPDATE `players` SET `group_id` = '2' WHERE `players`.`username` = '%username%' | %sqlitepath%sqlite3.exe .\server\inc\sqlite\%db%.db
echo:
echo %username% has been made an mod in database %db%!
echo:
pause
goto start

:regular
cls
echo:
echo Make sure you are logged out first!
echo Type the username of the player you wish to set and press enter.
echo:
SET /P username=""
cls
echo Type the name of the database where the player is saved.
echo:
echo (preservation, openrsc, cabbage, uranium, coleslaw, 2001scape, or openpk)
echo:
echo The default player database is named preservation.
echo:
echo:
SET /P db=""
cls
echo UPDATE `players` SET `group_id` = '10' WHERE `players`.`username` = '%username%' | %sqlitepath%sqlite3.exe .\server\inc\sqlite\%db%.db
echo:
echo %username% has been made a player in database %db%!
echo:
pause
goto start
:<------------End Role------------>


:<------------Begin Name Change------------>
:name
cls
echo Make sure you are logged out first!
echo What existing player should have their name changed?
echo:
SET /P oldname=""
echo:
echo What would you like to change "%oldname%"'s name to?
echo:
SET /P newname=""
cls
echo Type the name of the database where the player is saved.
echo:
echo (preservation, openrsc, cabbage, uranium, coleslaw, 2001scape, or openpk)
echo:
echo The default player database is named preservation.
echo:
echo:
SET /P db=""
cls
echo UPDATE `players` SET `username` = '%newname%' WHERE `players`.`username` = '%oldname%' | %sqlitepath%sqlite3.exe .\server\inc\sqlite\%db%.db
echo:
echo %oldname% has been renamed to %newname%!
echo:
pause
goto start
:<------------End Name Change------------>


:<------------Begin Backup------------>
:backup
REM Shuts down existing processes
taskkill /F /IM Java*

REM Performs a full database export
cls
echo Type the name of the database that you wish to backup.
echo:
echo (preservation, openrsc, cabbage, uranium, coleslaw, 2001scape, or openpk)
echo:
echo The default player database is named preservation.
echo:
SET /P db=""
cls
echo .dump | %sqlitepath%sqlite3.exe .\server\inc\sqlite\%db%.db > "Backups/%db%-%DATE:~-4%-%DATE:~4,2%-%DATE:~7,2%T%time:~-11,2%-%time:~-8,2%-%time:~-5,2%.sql"
echo:
echo Database "%db%" backup complete.
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
echo ===========================================================================
dir Backups
echo ===========================================================================
echo:
echo Type the filename of the backup file listed above that you wish to restore.
echo (Copy and paste it exactly)
echo:
SET /P filename=""
cls
echo Which database should this be restored to? (preservation, openrsc, cabbage, uranium, coleslaw, 2001scape, or openpk)
echo:
SET /P db=""
cls
echo .read Backups/%filename% | %sqlitepath%sqlite3.exe .\server\inc\sqlite\%db%.db
echo:
echo File "%filename%" was restored to database "%db%".
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
cls
echo .read ./server/database/sqlite/core.sqlite | %sqlitepath%sqlite3.exe .\server\inc\sqlite\preservation.db
echo .read ./server/database/sqlite/core.sqlite | %sqlitepath%sqlite3.exe .\server\inc\sqlite\openrsc.db
echo .read ./server/database/sqlite/core.sqlite | %sqlitepath%sqlite3.exe .\server\inc\sqlite\uranium.db
echo .read ./server/database/sqlite/core.sqlite | %sqlitepath%sqlite3.exe .\server\inc\sqlite\cabbage.db
echo .read ./server/database/sqlite/core.sqlite | %sqlitepath%sqlite3.exe .\server\inc\sqlite\coleslaw.db
echo .read ./server/database/sqlite/core.sqlite | %sqlitepath%sqlite3.exe .\server\inc\sqlite\openpk.db
echo .read ./server/database/sqlite/retro.sqlite | %sqlitepath%sqlite3.exe .\server\inc\sqlite\2001scape.db
echo .read ./server/database/sqlite/addons/add_auctionhouse.sqlite | %sqlitepath%sqlite3.exe .\server\inc\sqlite\cabbage.db
echo .read ./server/database/sqlite/addons/add_bank_presets.sqlite | %sqlitepath%sqlite3.exe .\server\inc\sqlite\cabbage.db
echo .read ./server/database/sqlite/addons/add_clans.sqlite | %sqlitepath%sqlite3.exe .\server\inc\sqlite\cabbage.db
echo .read ./server/database/sqlite/addons/add_equipment_tab.sqlite | %sqlitepath%sqlite3.exe .\server\inc\sqlite\cabbage.db
echo .read ./server/database/sqlite/addons/add_npc_kill_counting.sqlite | %sqlitepath%sqlite3.exe .\server\inc\sqlite\cabbage.db
echo .read ./server/database/sqlite/addons/add_auctionhouse.sqlite | %sqlitepath%sqlite3.exe .\server\inc\sqlite\coleslaw.db
echo .read ./server/database/sqlite/addons/add_bank_presets.sqlite | %sqlitepath%sqlite3.exe .\server\inc\sqlite\coleslaw.db
echo .read ./server/database/sqlite/addons/add_clans.sqlite | %sqlitepath%sqlite3.exe .\server\inc\sqlite\coleslaw.db
echo .read ./server/database/sqlite/addons/add_equipment_tab.sqlite | %sqlitepath%sqlite3.exe .\server\inc\sqlite\coleslaw.db
echo .read ./server/database/sqlite/addons/add_npc_kill_counting.sqlite | %sqlitepath%sqlite3.exe .\server\inc\sqlite\coleslaw.db
echo .read ./server/database/sqlite/addons/add_auctionhouse.sqlite | %sqlitepath%sqlite3.exe .\server\inc\sqlite\openpk.db
echo .read ./server/database/sqlite/addons/add_bank_presets.sqlite | %sqlitepath%sqlite3.exe .\server\inc\sqlite\openpk.db
echo .read ./server/database/sqlite/addons/add_clans.sqlite | %sqlitepath%sqlite3.exe .\server\inc\sqlite\openpk.db
echo .read ./server/database/sqlite/addons/add_equipment_tab.sqlite | %sqlitepath%sqlite3.exe .\server\inc\sqlite\openpk.db
echo .read ./server/database/sqlite/addons/add_npc_kill_counting.sqlite | %sqlitepath%sqlite3.exe .\server\inc\sqlite\openpk.db
echo:
echo Fresh install complete!
echo:
pause
goto start
:<------------End Fresh Install------------>
