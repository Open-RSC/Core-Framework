#!/bin/bash
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0` # No Color

    export pass=$(whiptail --passwordbox "Please enter your desired MySQL password." 8 50 $pass --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    export domain=$(whiptail --inputbox "Please enter your server's domain name. (No http:// or www. needed)" 8 50 $domain --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    export subdomain=$(whiptail --inputbox "Please set your server's private subdomain if one exists or press enter." 8 50 $domain --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    export port=$(whiptail --inputbox "What port should the game use?" 8 50 43594 --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    export tick=$(whiptail --inputbox "What speed should the game run? (600 is the default and 300 is twice as fast)" 8 50 600 --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    export tick=$(whiptail --inputbox "What speed should the game run? (600 is the default and 300 is twice as fast)" 8 50 600 --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    export gamename=$(whiptail --inputbox "Please enter the name of your game." 8 50 --title "Open RSC Configuration" 3>&1 1>&2 2>&3)

    # Server configuration edits
    sudo sed -i 's/mysql_user">root/mysql_user">openrsc/g' server/free.conf
    sudo sed -i 's/mysql_user">root/mysql_user">openrsc/g' server/members.conf
    sudo sed -i 's/mysql_pass">root/mysql_pass">'${pass}'/g' server/free.conf
    sudo sed -i 's/mysql_pass">root/mysql_pass">'${pass}'/g' server/members.conf
    sudo sed -i 's/game_tick">620/game_tick">'${tick}'/g' server/free.conf
    sudo sed -i 's/game_tick">620/game_tick">'${tick}'/g' server/members.conf
    sudo sed -i 's/43594/'${port}'/g' server/free.conf
    sudo sed -i 's/43594/'${port}'/g' server/members.conf
    sudo sed -i 's/server_name">Open RSC/server_name">"'${gamename}'"/g' server/free.conf
    sudo sed -i 's/server_name">Open RSC/server_name">"'${gamename}'"/g' server/members.conf

    #Client configuration edits
    sudo sed -i 's/SERVER_NAME = "Open RSC"/SERVER_NAME = "'${gamename}'"/g' client/src/orsc/Config.java
    sudo sed -i 's/SERVER_IP = "localhost"/SERVER_IP = "'${subdomain}'"/g' client/src/orsc/Config.java
    sudo sed -i 's/43594/'${port}'/g' client/src/orsc/Config.java

    #Launcher configuration edits
    sudo sed -i 's/frameTitle = "Open RSC"/frameTitle = "'${gamename}'"/g' Launcher/src/com/loader/openrsc/Constants.java
    sudo sed -i 's/localhost/'${domain}'/g' Launcher/src/com/loader/openrsc/Constants.java
    sudo sed -i 's/43594/'${port}'/g' Launcher/src/com/loader/openrsc/Constants.java

    # Docker or native install mode?
    echo ""
    echo "${RED}Open RSC:${NC}
    An easy to use RSC private server framework.

    Which method of installation do you wish to use?

    Choices:
      ${RED}1${NC} - Docker containers
      ${RED}2${NC} - Directly installed
      ${RED}3${NC} - Return to main menu"
    echo ""
    echo "Which of the above do you wish to do? Type the choice number and press enter."
    read installmode


    if [ "$installmode" == "1" ]; then
        # Database configuration
        sudo chmod 644 etc/mariadb/innodb.cnf

        export dbuser=root
        sudo docker exec -i mysql mysql -uroot -proot -Bse "CREATE USER 'openrsc'@'%' IDENTIFIED BY '$pass'; GRANT ALL PRIVILEGES ON * . * TO 'openrsc'@'%'; FLUSH PRIVILEGES;"

        export dbuser=openrsc
        sudo docker exec -i mysql mysql -uopenrsc -p${pass} -Bse "DELETE FROM mysql.user WHERE User='root'; FLUSH PRIVILEGES;"

        # .env
        sudo sed -i 's/localhost/'${domain}'/g' .env
        sudo sed -i 's/MARIADB_ROOT_USER=root/MARIADB_ROOT_USER='openrsc'/g' .env
        sudo sed -i 's/MARIADB_ROOT_PASSWORD=root/MARIADB_ROOT_PASSWORD='${pass}'/g' .env
        sudo make stop && sudo make start

        # Website
        sudo sed -i 's/dbuser = '\'root\''/dbuser = '\'openrsc\''/g' Website/elite/board/config.php
        sudo sed -i 's/dbpasswd = '\'root\''/dbpasswd = '\'${pass}\''/g' Website/elite/board/config.php
        sudo sed -i 's/game.openrsc.com/'${subdomain}'/g' Website/elite/index.php
        sudo sed -i 's/43594/'${port}'/g"' Website/elite/index.php

        sudo chmod 644 Website/sql/config.inc.php
      	sudo chmod 644 Website/elite/board/config.php
        sudo make certbot-docker

      elif [ "$installmode" == "2" ]; then
            # Database configuration
            sudo mysql -uroot -proot -Bse "CREATE USER 'openrsc'@'localhost' IDENTIFIED BY '$pass'; GRANT ALL PRIVILEGES ON * . * TO 'openrsc'@'localhost';FLUSH PRIVILEGES;"
            export dbuser=openrsc
            sudo mysql -uopenrsc -p${pass} -Bse "DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1'); FLUSH PRIVILEGES;"

            # Website
            sudo sed -i 's/dbuser = '\'root\''/dbuser = '\'openrsc\''/g' /var/www/html/elite/board/config.php
            sudo sed -i 's/dbpasswd = '\'root\''/dbpasswd = '\'${pass}\''/g' /var/www/html/elite/board/config.php
            sudo sed -i 's/game.opeenrsc.com/'${subdomain}'/g' /var/www/html/elite/index.php
            sudo sed -i 's/43594/'${port}'/g' /var/www/html/elite/index.php

            sudo make certbot-native


    elif [ "$installmode" == "3" ]; then
        make go
    fi

    make get-updates
