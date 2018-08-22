#!/bin/bash

sudo add-apt-repository ppa:webupd8team/java -y
sudo LC_ALL=C.UTF-8 add-apt-repository ppa:ondrej/php -y
sudo apt-get update
sudo apt remove mysql-server mysql-server-5.7 mysql-client -y
sudo apt-get install nano ant git oracle-java8-installer mariadb-server mariadb-client apache2 -y
sudo apt-get autoremove -y

sudo mysql_secure_installation
clear
echo "Please enter your MySQL password: "
read -s pass
echo "Please enter your server's domain name: "
read -s domain
sudo mysql -uroot -Bse "CREATE USER 'openrsc'@'localhost' IDENTIFIED BY '$pass';GRANT ALL PRIVILEGES ON * . * TO 'openrsc'@'localhost';FLUSH PRIVILEGES;"

sudo apt-get install php php-cgi libapache2-mod-php php-common php-pear php-mbstring php-fpm php-mysql php-gettext phpmyadmin -y
sudo phpenmod mbstring
sudo systemctl restart apache2

git clone git://github.com/Open-RSC/Game.git
sudo chmod -R 777 .

sudo sed -i 's/DB_LOGIN">root/DB_LOGIN">openrsc/g' server/config/config.xml
sudo sed -i 's/DB_PASS">root/DB_PASS">'$pass'/g' server/config/config.xml
sudo sed -i 's/String IP = "127.0.0.1";/String IP = "'$domain'";/g' client/src/org/openrsc/client/Config.java
sudo sed -i 's/String Domain = "localhost";/String Domain = "'$domain'";/g' Launcher/src/Main.java
sudo sed -i 's/String Dev_Domain = "localhost";/String Dev_Domain = "'$domain'";/g' Launcher/src/Main.java

sudo mkdir /var/www/html/downloads

# Server
clear
echo "Compiling the game server."
sudo ant -f "server/build.xml" compile

# Client
clear
echo "Compiling and preparing the game client. Any errors will be in updater.log"
sudo ant -f "client/build.xml" compile
cd client
sudo zip -r "client.zip" "Open_RSC_Client.jar"
cd ../
yes | sudo cp -rf "client/client.zip" "/var/www/html/downloads"
sudo rm "client/client.zip"

# Launcher
clear
echo "Compiling and preparing the game launcher."
sudo ant -f "Launcher/build.xml" jar
yes | sudo cp -rf "Launcher/dist/Open_RSC_Launcher.jar" "/var/www/html/downloads"

# Cache
clear
echo "Preparing the cache."
yes | sudo cp -rf "client/cache.zip" "/var/www/html/downloads"
sudo rm /var/www/html/downloads/hashes.txt
md5sum /var/www/html/downloads/client.zip | grep ^[a-zA-Z0-9]* | awk '{print "client="$1}'
md5sum /var/www/html/downloads/cache.zip | grep ^[a-zA-Z0-9]* | awk '{print "cache="$1}'

sudo mysql -u"root" -p"$pass" < "Databases/openrsc.sql"
sudo mysql -u"root" -p"$pass" < "Databases/openrsc_config.sql"
sudo mysql -u"root" -p"$pass" < "Databases/openrsc_tools.sql"
sudo mysql -u"root" -p"$pass" < "Databases/openrsc_logs.sql"

myip="$(dig +short myip.opendns.com @resolver1.opendns.com)"
echo "The installation script has completed."
echo "You should be able to download the client at: http://${myip}/downloads/Open_RSC_Launcher.jar in your web browser."
