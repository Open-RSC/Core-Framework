#!/bin/bash
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0` # No Color

sudo apt-get install mariadb-server nginx mariadb-client php php-cgi php-common php-pear php-mbstring php-fpm php7.2-fpm php-mysql -y

sudo debconf-set-selections <<< "phpmyadmin phpmyadmin/internal/skip-preseed boolean true"
sudo debconf-set-selections <<< "phpmyadmin phpmyadmin/reconfigure-webserver multiselect"
sudo debconf-set-selections <<< "phpmyadmin phpmyadmin/dbconfig-install boolean false"

sudo apt install php-gettext phpmyadmin -y
sudo ln -s /usr/share/phpmyadmin /var/www/html
sudo phpenmod mbstring

sudo rm "/etc/nginx/sites-available/default"
sudo cat "etc/nginx/native.conf.BAK" > "/etc/nginx/sites-available/default"
sudo systemctl restart nginx

sudo mysql -u"root" -p"root" -e "create database openrsc; GRANT ALL PRIVILEGES ON openrsc.* TO root@localhost IDENTIFIED BY 'root'"
sudo mysql -u"root" -p"root" openrsc < "Databases/openrsc_game_server.sql"
sudo mysql -u"root" -p"root" openrsc < "Databases/openrsc_game_players.sql"

sudo mysql -u"root" -p"root" -e "create database cabbage; GRANT ALL PRIVILEGES ON cabbage.* TO root@localhost IDENTIFIED BY 'root'"
sudo mysql -u"root" -p"root" cabbage < "Databases/openrsc_game_server.sql"
sudo mysql -u"root" -p"root" cabbage < "Databases/openrsc_game_players.sql"

sudo rm /var/www/html/*.*
make clone-website
sudo cp -a Website/. /var/www/html/