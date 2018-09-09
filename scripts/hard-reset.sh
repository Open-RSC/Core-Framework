#!/bin/bash
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0` # No Color


echo ""
echo "${RED}Open RSC Hard Reset:${NC}
An easy to use RSC private server framework.

What do you wish to do?

Choices:
  ${RED}1${NC} - Perform a hard reset of everything
  ${RED}2${NC} - Return to the main menu"
echo ""
echo "Which of the above do you wish to do? Type the choice number and press enter."
echo ""
read reset

if [ "$reset" == "2" ]; then
    make go
else
    if [ "$installmode" == "direct" ]; then
        sudo service nginx stop
        sudo apt-get purge "nginx*" -y
        sudo rm -rf /var/www/http
        sudo service mariadb stop
        sudo apt-get purge "mariadb*" -y
        sudo rm -rf /etc/mysql/
        sudo git reset HEAD --hard
        sudo git pull

        sudo rm .dbuser
        sudo touch .dbuser
        sudo chmod 777 .dbuser
        export dbuser=root
        echo "$dbuser" > .dbuser

        sudo rm .pass
        sudo touch .pass
        sudo chmod 777 .pass
        export pass=root
        echo "$pass" > .pass

        sudo rm .email
        sudo touch .email
        sudo chmod 777 .email
        export email=""
        echo "$email" > .email

        sudo rm .methodinstall
        sudo touch .methodinstall
        sudo chmod 777 .methodinstall
        export installmode=""
        echo "$installmode" > .methodinstall

        sudo rm .installedalready
        sudo touch .installedalready
        sudo chmod 777 .installedlaready
        export installedalready=""
        echo "$installedalready" > .installedalready

        echo "Done!"
        make go
    else
        sudo make stop
        sudo rm -rf Website
        sudo rm -rf data/db/mysql
        sudo rm -rf etc/letsencrypt/live
        sudo git reset HEAD --hard
        sudo git pull

        sudo rm .dbuser
        sudo touch .dbuser
        sudo chmod 777 .dbuser
        export dbuser=root
        echo "$dbuser" > .dbuser

        sudo rm .pass
        sudo touch .pass
        sudo chmod 777 .pass
        export pass=root
        echo "$pass" > .pass

        sudo rm .email
        sudo touch .email
        sudo chmod 777 .email
        export email=""
        echo "$email" > .email

        sudo rm .methodinstall
        sudo touch .methodinstall
        sudo chmod 777 .methodinstall
        export installmode=""
        echo "$installmode" > .methodinstall

        sudo rm .installedalready
        sudo touch .installedalready
        sudo chmod 777 .installedalready
        export installedalready=""
        echo "$installedalready" > .installedalready

        echo "Done!"
        make go
    fi
fi
