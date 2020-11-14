@echo off
:# Open RSC: Striving for a replica RSC game and more

:# Path variables:
SET mariadbpath="mariadb-10.5.8-winx64\bin\"

call START /min "" %mariadbpath%mysqld.exe --console