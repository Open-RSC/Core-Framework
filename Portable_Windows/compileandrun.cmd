@echo off
:# Open RSC: Striving for a replica RSC game and more

:# Path variables:
SET antpath="apache-ant-1.10.5\bin\"
SET "PATH=zulu8.50.0.51-ca-jdk8.0.275-win_x64\bin\;%PATH%"

cls
echo: Starting up the server and then launching the client. Close this window when you are finished playing.
echo:

REM Read the port from port.txt
SET /P serverport=<..\Client_Base\Cache\port.txt
echo Server port: %serverport%
echo:
echo Compiling and starting server...
call START /min "" %antpath%ant -f ../server\build.xml compile-and-run

:wait_for_server
echo Waiting for server to be ready on port %serverport%...
PING localhost -n 6 >NUL
netstat -an | find ":%serverport%" | find "LISTENING" >NUL
if errorlevel 1 (
    goto wait_for_server
)

echo Server is ready! Starting client...
call START "" %antpath%ant -f ../Client_Base\build.xml compile-and-run

pause
taskkill /F /IM Java*
taskkill /F /IM mysqld*
exit
