#!/bin/bash

rm simpleinstall.log
touch simpleinstall.log && chmod 777 simpleinstall.log | tee -a simpleinstall.log &>/dev/null

# Software installations
clear
echo "Installing Oracle JDK 8, MariaDB, nano, htop, screen, Apache Ant, and git. Please wait."
sudo add-apt-repository ppa:webupd8team/java -y | tee -a simpleinstall.log &>/dev/null
sudo LC_ALL=C.UTF-8 add-apt-repository ppa:ondrej/php -y | tee -a simpleinstall.log &>/dev/null
sudo apt-get update | tee -a simpleinstall.log &>/dev/null
sudo apt remove mysql-server mysql-server-5.7 mysql-client apache2 -y | tee -a simpleinstall.log &>/dev/null
sudo apt-get install nano htop screen ant git oracle-java8-installer mariadb-server mariadb-client nginx -y | tee -a simpleinstall.log &>/dev/null
sudo apt-get autoremove -y | tee -a simpleinstall.log &>/dev/null

# PHPMyAdmin installation
clear
echo "Installing PHP 7.2 and PHPMyAdmin. Please wait."
sudo apt-get install php php-cgi php-common php-pear php-mbstring php-fpm php-mysql php-gettext phpmyadmin -y
sudo phpenmod mbstring | tee -a simpleinstall.log &>/dev/null
sudo systemctl restart nginx | tee -a simpleinstall.log &>/dev/null

# GitHub repository clone
clear
echo "Clonining the game from the GitHub repository."
git clone git://github.com/Open-RSC/Game.git | tee -a simpleinstall.log &>/dev/null
sudo chmod -R 777 . | tee -a simpleinstall.log &>/dev/null

# Database configuration
sudo mysql_secure_installation
clear
echo "Please enter your MySQL password: "
read -s pass
echo "Please enter your server's domain name: "
read -s domain
sudo mysql -uroot -Bse "CREATE USER 'openrsc'@'localhost' IDENTIFIED BY '$pass';GRANT ALL PRIVILEGES ON * . * TO 'openrsc'@'localhost';FLUSH PRIVILEGES;" | tee -a simpleinstall.log &>/dev/null

# Database imports
sudo mysql -u"root" -p"$pass" < "Databases/openrsc.sql" | tee -a simpleinstall.log &>/dev/null
sudo mysql -u"root" -p"$pass" < "Databases/openrsc_config.sql" | tee -a simpleinstall.log &>/dev/null
sudo mysql -u"root" -p"$pass" < "Databases/openrsc_tools.sql" | tee -a simpleinstall.log &>/dev/null
sudo mysql -u"root" -p"$pass" < "Databases/openrsc_logs.sql" | tee -a simpleinstall.log &>/dev/null

# Automated file edits
clear
sudo sed -i 's/DB_LOGIN">root/DB_LOGIN">openrsc/g' server/config/config.xml | tee -a simpleinstall.log &>/dev/null
sudo sed -i 's/DB_PASS">root/DB_PASS">'$pass'/g' server/config/config.xml | tee -a simpleinstall.log &>/dev/null
sudo sed -i 's/String IP = "127.0.0.1";/String IP = "'$domain'";/g' client/src/org/openrsc/client/Config.java | tee -a simpleinstall.log &>/dev/null
sudo sed -i 's/String Domain = "localhost";/String Domain = "'$domain'";/g' Launcher/src/Main.java | tee -a simpleinstall.log &>/dev/null
sudo sed -i 's/:8080";/:80";/g' Launcher/src/Main.java  | tee -a simpleinstall.log &>/dev/null
sudo sed -i 's/toolBar.getChildren().add(Dev/\/\/toolBar.getChildren().add(Dev/g' Launcher/src/Main.java  | tee -a simpleinstall.log &>/dev/null

# Website
clear
sudo mkdir /var/www/html/downloads | tee -a simpleinstall.log &>/dev/null

# Server
clear
echo "Compiling the game server. Any errors will be in simpleinstall.log"
sudo ant -f "server/build.xml" compile | tee -a simpleinstall.log &>/dev/null

# Client
clear
echo "Compiling and preparing the game client. Any errors will be in simpleinstall.log"
sudo ant -f "client/build.xml" compile | tee -a simpleinstall.log &>/dev/null
cd client
sudo zip -r "client.zip" "Open_RSC_Client.jar" | tee -a ../simpleinstall.log &>/dev/null
cd ../
yes | sudo cp -rf "client/client.zip" "/var/www/html/downloads" | tee -a simpleinstall.log &>/dev/null
sudo rm "client/client.zip" | tee -a simpleinstall.log &>/dev/null

# Launcher
clear
echo "Compiling and preparing the game launcher. Any errors will be in simpleinstall.log"
sudo ant -f "Launcher/build.xml" jar | tee -a simpleinstall.log &>/dev/null
yes | sudo cp -rf "Launcher/dist/Open_RSC_Launcher.jar" "/var/www/html/downloads" | tee -a simpleinstall.log &>/dev/null

# Cache
clear
echo "Preparing the cache."
yes | sudo cp -rf "client/cache.zip" "/var/www/html/downloads" | tee -a simpleinstall.log &>/dev/null
sudo rm /var/www/html/downloads/hashes.txt | tee -a simpleinstall.log &>/dev/null
md5sum /var/www/html/downloads/client.zip | grep ^[a-zA-Z0-9]* | awk '{print "client="$1}' | sudo tee -a /var/www/html/downloads/hashes.txt | tee -a simpleinstall.log &>/dev/null
md5sum /var/www/html/downloads/cache.zip | grep ^[a-zA-Z0-9]* | awk '{print "cache="$1}' | sudo tee -a /var/www/html/downloads/hashes.txt | tee -a simpleinstall.log &>/dev/null

# Completion
myip="$(dig +short myip.opendns.com @resolver1.opendns.com)" | tee -a simpleinstall.log &>/dev/null
echo "The installation script has completed."
echo "You should now be able to download the game launcher at: http://${myip}/downloads/Open_RSC_Launcher.jar"
