#!/bin/sh
#Disclaimer: This script has been tested and works on Ubuntu 16.04. It may work on Ubuntu 18.04 or other distros, but it has not been tested on anything other than Ubuntu 16.04.
sudo add-apt-repository ppa:webupd8team/java -y 
sudo LC_ALL=C.UTF-8 add-apt-repository ppa:ondrej/php -y
sudo apt-get update
sudo apt-get install nano ant git oracle-java8-installer mysql-server mysql-client apache2 -y
sudo apt-get install php7.2 php7.2-mysql php7.2-curl php7.2-xml php7.2-gd -y
sudo apt-get install phpmyadmin php-7.2mbstring php7.2-gettext -y 
git clone git://github.com/Open-RSC/Game.git 
cd Game && cd server && ant compile 
cd .. && cd client && ant compile 
cd ..
echo "Please enter your MySQL password: "
read -s pass
mysql -p"$pass" -u"root" < ./Databases/openrsc.sql &>/dev/null
mysql -p"$pass" -u"root" < ./Databases/openrsc_config.sql &>/dev/null
mysql -p"$pass" -u"root" < ./Databases/openrsc_tools.sql &>/dev/null
mysql -p"$pass" -u"root" < ./Databases/openrsc_logs.sql &>/dev/null
sudo cp ./client/Open_RSC_Client.jar /var/www/html
myip="$(dig +short myip.opendns.com @resolver1.opendns.com)"
echo "The installation script has completed."
echo "You should be able to download the client at: http://${myip}/Open_RSC_Client.jar in your web browser."