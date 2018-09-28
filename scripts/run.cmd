@echo off

call START /min "" ant -f ../server/build.xml runservermembers

echo Launching the game client in 10 seconds (gives time to start the server)
PING localhost -n 11 >NUL
cd ../client && call java -jar Open_RSC_Client.jar
taskkill /F /IM Java*
exit
