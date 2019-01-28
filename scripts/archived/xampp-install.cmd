@echo off

call C:\xampp\apache\apache_installservice.bat
call C:\xampp\mysql\mysql_installservice.bat
start /min C:\xampp\xampp-control.exe
