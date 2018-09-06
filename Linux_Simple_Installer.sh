#!/bin/bash
exec 0</dev/tty

# Open RSC: A replica RSC private server framework
#
# Installs and updates Open RSC
#
# Install with this command (from your Linux machine):
#
# curl -sSL https://raw.githubusercontent.com/Open-RSC/Game/master/Linux_Simple_Cloner.sh | bash

rm installer.log
touch installer.log && chmod 777 installer.log | tee -a installer.log &>/dev/null

# Software installations
clear
echo "Installing Oracle JDK 8, MariaDB, nano, htop, screen, Apache Ant, and git. Please wait."
sudo add-apt-repository ppa:webupd8team/java -y | tee -a installer.log &>/dev/null
sudo LC_ALL=C.UTF-8 add-apt-repository ppa:ondrej/php -y | tee -a installer.log &>/dev/null
sudo apt-get update | tee -a installer.log &>/dev/null
sudo apt remove mysql-server mysql-server-5.7 mysql-client apache2 -y | tee -a installer.log &>/dev/null
sudo apt-get install nano htop screen ant git oracle-java8-installer mariadb-server mariadb-client nginx -y | tee -a installer.log &>/dev/null
sudo apt-get autoremove -y | tee -a installer.log &>/dev/null

# PHPMyAdmin installation
clear
echo "Installing PHP 7.2 and PHPMyAdmin. Please wait."
sudo apt-get install php php-cgi php-common php-pear php-mbstring php-fpm php-mysql php-gettext phpmyadmin -y
sudo phpenmod mbstring | tee -a installer.log &>/dev/null
sudo systemctl restart nginx | tee -a installer.log &>/dev/null

# Database configuration
clear
sudo mysql_secure_installation
clear
echo "Please enter your MySQL password."
read -s pass
clear
echo "Please enter your server's domain name."
read -s domain
sudo mysql -uroot -Bse "DROP USER 'openrsc'@'localhost';FLUSH PRIVILEGES;" | tee -a installer.log &>/dev/null
sudo mysql -uroot -Bse "CREATE USER 'openrsc'@'localhost' IDENTIFIED BY '$pass';GRANT ALL PRIVILEGES ON * . * TO 'openrsc'@'localhost';FLUSH PRIVILEGES;" | tee -a installer.log &>/dev/null

# Database imports
clear
echo "Importing database."
sudo mysql -u"root" -p"$pass" < "openrsc_game.sql" | tee -a installer.log &>/dev/null

# Automated file edits
#clear
#sudo sed -i 's/DB_LOGIN">root/DB_LOGIN">openrsc/g' server/config/config.xml | tee -a installer.log &>/dev/null
#sudo sed -i 's/DB_PASS">root/DB_PASS">'$pass'/g' server/config/config.xml | tee -a installer.log &>/dev/null
#sudo sed -i 's/String IP = "127.0.0.1";/String IP = "'$domain'";/g' client/src/org/openrsc/client/Config.java | tee -a installer.log &>/dev/null

# Website
clear
sudo mkdir /var/www/html/downloads | tee -a installer.log &>/dev/null

# Server
clear
echo "Compiling the game server. Any errors will be in installer.log"
sudo ant -f "server/build.xml" compile_core | tee -a installer.log &>/dev/null
sudo ant -f "server/build.xml" compile_plugins | tee -a installer.log &>/dev/null

# Client
clear
echo "Compiling and preparing the game client. Any errors will be in installer.log"
sudo ant -f "client/build.xml" compile | tee -a installer.log &>/dev/null
yes | sudo cp -rf "client/Open_RSC_Client.jar" "/var/www/html/downloads" | tee -a installer.log &>/dev/null

# Launcher
clear
echo "Compiling and preparing the game launcher. Any errors will be in updater.log"
sudo ant -f "Launcher/nbbuild.xml" jar | tee -a installer.log &>/dev/null
yes | sudo cp -rf "Launcher/dist/Open_RSC_Launcher.jar" "/var/www/html/downloads/" | tee -a installer.log &>/dev/null

# Cache
clear
echo "Preparing the cache."
yes | sudo cp -a -rf "client/Cache/." "/var/www/html/downloads/cache/" | tee -a updater.log &>/dev/null
sudo rm /var/www/html/downloads/cache/MD5CHECKSUM | tee -a updater.log &>/dev/null
sudo touch /var/www/html/downloads/cache/MD5CHECKSUM && sudo chmod 777 /var/www/html/downloads/cache/MD5CHECKSUM | tee updater.log | &>/dev/null
md5sum /var/www/html/downloads/cache/* | sed 's/\/var\/www\/html\/downloads\/cache\///g' |  grep ^[a-zA-Z0-9]* | awk '{print $2"="$1}' | tee /var/www/html/downloads/cache/MD5CHECKSUM | tee -a updater.log &>/dev/null
sudo sed -i 's/MD5CHECKSUM=/#MD5CHECKSUM=/g' "/var/www/html/downloads/cache/MD5CHECKSUM"

# Completion
myip="$(dig +short myip.opendns.com @resolver1.opendns.com)"
clear
cd Game
echo "The installation script has completed."
echo ""
echo "You should now be able to download the game launcher at: http://${myip}/downloads/Open_RSC_Launcher.jar"
echo ""
echo "Launch the game server via: ./Linux_Simple_Run.sh"
