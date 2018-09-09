@echo off
echo:
echo Stopping associated Docker containers.
echo:
make stop
echo:
echo:
echo Killing any instances of the game server that are running in the background.
echo:
tskill java /a
echo:
echo:
pause
