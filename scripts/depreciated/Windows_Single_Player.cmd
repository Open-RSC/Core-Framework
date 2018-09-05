@echo off

REM   Check for any updates to the game server
echo:
echo Pulling recent updates from the Open-RSC Game repository.
echo:
git pull
make pull-game
echo:
echo:
echo Starting Docker containers.
echo:
make start-single-player
echo:
echo:

REM   Compile the game server and client:
echo Compiling the game client.
echo:
call ant -f Game/client/build.xml compile
echo:
echo:
echo Compiling the game server.
echo:
call ant -f Game/server/build.xml compile
echo:
echo:

REM   Create game cache:
echo Removing old then extracting a fresh client cache to your home folder.
echo:
rmdir "%HOMEPATH%/OpenRSC" /s /Q
md "%HOMEPATH%/OpenRSC"
7z x "Game/client/cache.zip" -o"%HOMEPATH%/OpenRSC" -r
echo:
echo:

REM   Import fresh version of config database:
echo Importing a fresh openrsc_config.sql database.
echo:
docker exec -i mysql mysql -u"root" -p"root" < Game/Databases/openrsc_config.sql > NULL
echo:
echo:

REM   Generate updated cache files, copies them to cache folder overwriting existing:
echo Generating cache .dat files from current config database and copying to client cache in your home folder.
echo:
call sudo ant -f Game/server/build.xml npcs items objects > NULL
xcopy /y "Game/server/npcs.dat" "%HOMEPATH%/OpenRSC/npcs.dat"
xcopy /y "Game/server/objects.dat" "%HOMEPATH%/OpenRSC/objects.dat"
xcopy /y "Game/server/items.dat" "%HOMEPATH%/OpenRSC/items.dat"
echo:
echo:

REM   Run the game client in a new window:
echo Starting the game client in a new window.
echo:
call START "" java -jar Game/client/Open_RSC_Client.jar
echo:
echo:

REM   Run the game server in the current window:
echo Starting the game server in the current window.
echo:
cd Game/server
call java -jar Open_RSC_Server.jar
cd ../../
echo:
pause
