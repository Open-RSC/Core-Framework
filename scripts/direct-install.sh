#!/bin/bash
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0` # No Color
export installmode=direct

phases=(
'Uninstalling previous conflicting software' #0
'Adding needed repositories' #1
'Running APT Update' #2
'Installing nano and htop' #3
'Installing screen and git' #4
'Installing MariaDB server and client' #5
'Installing Nginx' #6
'Accepting Oracle JDK 8 licence' #7
'Installing Oracle JDK 8 and Apache Ant' #8
'Installing PHP and PHP-CGI' #9
'Installing PHP-Common, PHP-Pear, and PHP-MBString' #10
'Installing PHP-FPM and PHP-MySQL' #11
'Installing PHP-GetText and PHPMyAdmin' #12
'Configuring MBString' #13
'Restarting Nginx to apply changes' #14
'Setting up databases...' #15
'Creating website downloads folder' #16
''
)
for i in $(seq 1 100); do

    i=1
    echo -e "XXX\n$i\n${phases[0]}\nXXX"
    # Uninstalls previous conflicting software if exists
    sudo apt remove nano htop screen ant mariadb-server mariadb-client nginx oracle-java8-installer php php-cgi php-common php-pear php-mbstring php-fpm php7.2-fpm php-mysql php-gettext phpmyadmin -y &>/dev/null
    sudo apt autoremove -y &>/dev/null

    i=5
    echo -e "XXX\n$i\n${phases[1]}\nXXX"
    # Software installations
    sudo add-apt-repository ppa:webupd8team/java -y &>/dev/null
    sudo LC_ALL=C.UTF-8 add-apt-repository ppa:ondrej/php -y &>/dev/null

    i=10
    echo -e "XXX\n$i\n${phases[2]}\nXXX"
    sudo apt-get update -y &>/dev/null

    i=12
    echo -e "XXX\n$i\n${phases[3]}\nXXX"
    sudo apt-get install nano htop -y

    i=14
    echo -e "XXX\n$i\n${phases[4]}\nXXX"
    sudo apt-get install screen git -y

    i=16
    echo -e "XXX\n$i\n${phases[5]}\nXXX"
    sudo apt-get install mariadb-server mariadb-client -y

    i=20
    echo -e "XXX\n$i\n${phases[6]}\nXXX"
    sudo apt-get install nginx -y

    i=25
    echo -e "XXX\n$i\n${phases[7]}\nXXX"
    echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | sudo debconf-set-selections

    i=30
    echo -e "XXX\n$i\n${phases[8]}\nXXX"
    sudo apt-get install -y oracle-java8-installer ant

    i=40
    echo -e "XXX\n$i\n${phases[9]}\nXXX"
    # PHPMyAdmin installation
    sudo apt-get install php php-cgi  -y

    i=45
    echo -e "XXX\n$i\n${phases[10]}\nXXX"
    sudo apt-get install php-common php-pear php-mbstring  -y

    i=50
    echo -e "XXX\n$i\n${phases[11]}\nXXX"
    sudo apt-get install php-fpm php7.2-fpm php-mysql  -y

    i=60
    echo -e "XXX\n$i\n${phases[12]}\nXXX"
    debconf-set-selections <<< "phpmyadmin phpmyadmin/internal/skip-preseed boolean true"
    debconf-set-selections <<< "phpmyadmin phpmyadmin/reconfigure-webserver multiselect"
    debconf-set-selections <<< "phpmyadmin phpmyadmin/dbconfig-install boolean false"
    sudo apt-get install php-gettext phpmyadmin  -y
    sudo ln -s /usr/share/phpmyadmin /var/www/html

    i=70
    echo -e "XXX\n$i\n${phases[13]}\nXXX"
    sudo phpenmod mbstring
    sudo rm "/etc/nginx/sites-available/default"
    sudo cat "etc/nginx/simplified.conf" > "/etc/nginx/sites-available/default"

    i=80
    echo -e "XXX\n$i\n${phases[14]}\nXXX"
    sudo systemctl restart nginx

    i=90
    echo -e "XXX\n$i\n${phases[15]}\nXXX"
    sleep 1
    # Database configuration and imports
    sudo mysql -u"root" -p"root" < "Databases/openrsc_game.sql"
    sudo mysql -u"root" -p"root" < "Databases/openrsc_forum.sql"

    i=99
    echo -e "XXX\n$i\n${phases[16]}\nXXX"
    # Create Website downloads folder
    sudo mkdir /var/www/html/downloads

    i=100
    echo -e "XXX\n$i\n${phases[17]}\nXXX"
    exit
    if [ $i -eq 100 ]; then
        echo -e "XXX\n100\nDone!\nXXX"
    elif [ $(($i % 25)) -eq 0 ]; then
        let "phase = $i / 25"
        echo -e "XXX\n$i\n${phases[phase]}\nXXX"
    else
        echo $i
    fi
done | whiptail --title 'Open RSC Direct Installation' --gauge "${phases[0]}" 7 70 0

make file-edits
