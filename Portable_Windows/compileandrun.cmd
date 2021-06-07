@echo off
:# Open RSC: Striving for a replica RSC game and more

:# Path variables:
SET antpath="apache-ant-1.10.5\bin\"

cls
echo: Starting up the server and then launching the client. Close this window when you are finished playing.
call START /min "" %antpath%ant -f ../server\build.xml compile-and-run && PING localhost -n 20 >NUL && call START "" %antpath%ant -f ../Client_Base\build.xml compile-and-run

pause
taskkill /F /IM Java*
taskkill /F /IM mysqld*
exit
