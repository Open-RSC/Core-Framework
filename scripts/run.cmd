@echo off


REM   Run the game server in a new window:
call START "" java -jar ../server/Open_RSC_Server.jar


echo Launching the game client in 10 seconds (gives time to start the server)
PING localhost -n 11 >NUL

REM   Run the game client in a new window:
call java -jar ../client/Open_RSC_Client.jar


echo:
echo:
echo When finished playing, press enter for shut down the game server.
SET /P quit=""
taskkill /IM java*
exit
