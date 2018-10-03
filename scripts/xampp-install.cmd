@echo off

cd C:\xampp\apache
call apache_installservice.bat
cd C:\xampp\mysql
call mysql_installservice.bat
cd C:\xampp
start xampp-control.exe