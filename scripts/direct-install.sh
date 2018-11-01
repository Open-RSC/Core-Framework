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

sudo mysql -u"root" -p"root" < "Databases/mysql.sql"
sudo mysql -u"root" -p"root" < "Databases/phpmyadmin.sql"
sudo mysql -u"root" -p"root" < "Databases/openrsc_game.sql"
sudo mysql -u"root" -p"root" < "Databases/openrsc_forum.sql"

sudo rm /var/www/html/*.*
make clone-website
cp -a Website/. /var/www/html/
make file-edits
