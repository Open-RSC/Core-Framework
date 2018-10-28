#!/bin/bash
exec 0</dev/tty
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0` # No Color
source .env

# System Updates
echo ""
echo "Checking for system updates and upgrading if found."
echo ""
sudo apt-get update && sudo apt-get upgrade -y && sudo apt-get autoremove -y

# Game Repository
echo ""
echo "Checking for updates to the game code."
echo ""
sudo git pull

# Permissions
echo ""
echo "Verifying file and user permissions are set correctly"
echo ""
sudo chmod -R 777 .

echo ""
echo "Compiling all code now."
echo ""
echo ""
make compile

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

if [ "$installmode" == "2" ]; then
    sudo chmod 644 /var/www/html/elite/board/config.php

    # Client
    yes | sudo cp -rf "client/Open_RSC_Client.jar" "/var/www/html/downloads/cache"
    sudo chmod +x "/var/www/html/downloads/cache/Open_RSC_Client.jar"
    sudo chmod 777 "/var/www/html/downloads/cache/Open_RSC_Client.jar"

    # Launcher
    yes | sudo cp -rf "Launcher/OpenRSC.jar" "/var/www/html/downloads/"
    sudo chmod +x "/var/www/html/downloads/OpenRSC.jar"
    sudo chmod 777 "/var/www/html/downloads/OpenRSC.jar"

    # Cache
    yes | sudo cp -a -rf "client/Cache/." "/var/www/html/downloads/cache/"
    sudo rm /var/www/html/downloads/cache/MD5CHECKSUM
    sudo touch /var/www/html/downloads/cache/MD5CHECKSUM && sudo chmod 777 /var/www/html/downloads/cache/MD5CHECKSUM
    md5sum /var/www/html/downloads/cache/* | sed 's/\/var\/www\/html\/downloads\/cache\///g' |  grep ^[a-zA-Z0-9]* | awk '{print $2"="$1}' | tee /var/www/html/downloads/cache/MD5CHECKSUM
    sudo sed -i 's/MD5CHECKSUM=/#MD5CHECKSUM=/g' "/var/www/html/downloads/cache/MD5CHECKSUM"

elif [ "$installmode" == "1" ]; then
    sudo chmod 644 etc/mariadb/innodb.cnf
    sudo chmod 644 Website/elite/board/config.php
    sudo setfacl -m user:$USER:rw /var/run/docker.sock

    # Client
    yes | sudo cp -rf "client/Open_RSC_Client.jar" "Website/downloads/cache/"
    sudo chmod +x "Website/downloads/cache/Open_RSC_Client.jar"
    sudo chmod 777 "Website/downloads/cache/Open_RSC_Client.jar"

    # Launcher
    yes | sudo cp -rf "Launcher/OpenRSC.jar" "Website/downloads/"
    sudo chmod +x "Website/downloads/OpenRSC.jar"
    sudo chmod 777 "Website/downloads/OpenRSC.jar"

    # Cache
    yes | sudo cp -a -rf "client/Cache/." "Website/downloads/cache/"
    sudo rm Website/downloads/cache/MD5CHECKSUM
    sudo touch Website/downloads/cache/MD5CHECKSUM && sudo chmod 777 Website/downloads/cache/MD5CHECKSUM
    md5sum Website/downloads/cache/* | sed 's/Website\/downloads\/cache\///g' |  grep ^[a-zA-Z0-9]* | awk '{print $2"="$1}' | tee Website/downloads/cache/MD5CHECKSUM
    sudo sed -i 's/MD5CHECKSUM=/#MD5CHECKSUM=/g' "Website/downloads/cache/MD5CHECKSUM"
    sudo sed -i 's/index=/#index=/g' "Website/downloads/cache/MD5CHECKSUM"
fi

# Finished
echo ""
echo "${RED}Open RSC:${NC}
An easy to use RSC private server framework.

What would you like to do next?

Choices:
  ${RED}1${NC} - Run Open RSC
  ${RED}2${NC} - Return to the main menu"
echo ""
echo "Which of the above do you wish to do? Type the choice number and press enter."
echo ""
read finished

if [ "$finished" == "1" ]; then
    make run-game
elif [ "$finished" == "2" ]; then
    make go
fi
