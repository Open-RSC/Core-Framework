#!/bin/bash
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0` # No Color

echo ""
echo "${RED}Open RSC Hard Reset:${NC}
An easy to use RSC private server framework.

What do you wish to do?

Choices:
  ${RED}1${NC} - Perform a hard reset of game code (then get updates)
  ${RED}2${NC} - Return to the main menu"
echo ""
echo "Which of the above do you wish to do? Type the choice number and press enter."
echo ""
read reset

if [ "$reset" == "2" ]; then
    make go
else

    # Docker or native install mode?
    echo ""
    echo "Which are you using?

    Choices:
      ${RED}1${NC} - Docker Containers
      ${RED}2${NC} - Native Installation"
    echo ""
    echo "Which of the above do you wish to do? Type the choice number and press enter."
    echo ""
    read installmode

    if [ "$installmode" == "direct" ]; then
        #sudo service nginx stop
        #sudo apt-get purge "nginx*" -y
        #sudo rm -rf /var/www/http
        #sudo service mariadb stop
        #sudo apt-get purge "mariadb*" -y
        #sudo rm -rf /etc/mysql/
        sudo git reset HEAD --hard
        sudo git pull
        #export dbuser=root
        #export pass=root
        #export email=""
        #export installmode=""
        #export installedalready=""
        echo "Done!"
        make go
    else
        #sudo make stop
        #sudo rm -rf Website
        #sudo rm -rf data/db/mysql
        #sudo rm -rf etc/letsencrypt/live
        sudo git reset HEAD --hard
        sudo git pull
        #export dbuser=root
        #export pass=root
        #export email=""
        #export installmode=""
        #export installedalready=""
        echo "Done!"
        make go
    fi
fi
