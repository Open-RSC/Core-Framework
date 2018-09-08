#!/bin/bash
exec 0</dev/tty
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0` # No Color

# Open RSC: A replica RSC private server framework
#
# Multi-purpose script for Open RSC
#
# Install everything with this command:
#
# curl -sSL https://raw.githubusercontent.com/Open-RSC/Game/master/scripts/clone.sh | bash

clear
echo "${RED}Open RSC:${NC}
An easy to use RSC private server framework.

What would you like to do?

Choices:
  ${RED}1${NC} - Install Open RSC
  ${RED}2${NC} - Update Open RSC
  ${RED}3${NC} - Run Open RSC
  ${RED}4${NC} - Exit"
echo ""
echo "Which of the above do you wish to do? Type the choice number and press enter."
echo ""
read action

if [ "$action" == "1" ]; then
    make combined-install
elif [ "$action" == "2" ]; then
    make get-updates
elif [ "$action" == "3" ]; then
    make run
elif [ "$action" == "4" ]; then
    exit
fi

#=================================================================

function jumpto {
    label=$1
    cmd=$(sed -n "/$label:/{:a;n;p;ba};" $0 | grep -v ':$')
    eval "$cmd"
    exit
}
start=${1:-"start"}
deployment=${1:-"deployment"}
jumpto $start

#=================================================================

start:




# Exit ===================================================>
if [ "$install" == "3" ]; then
  exit
fi
# Exit <===================================================



# Install Choice <===================================================
deployment:
clear
echo ""
echo "Fetching updates from the Docker-Home GitHub repository."
echo ""
sudo git pull

echo ""
echo "${RED}Open RSC Installer:${NC}
echo ""
An easy to use RSC private server using Docker magic.

Choices:
  ${RED}1${NC} - Set up for single player
  ${RED}2${NC} - Deploy to a VPS
  ${RED}3${NC} - Backup all databases
"
echo ""
echo "Which of the above do you wish to do? Type the choice number and press enter."
echo ""
read choice

# Selection ===================================================>
# 1. Set up for single player ===================================================>
if [ "$choice" == "1" ]; then
    echo ""
    echo "You have picked ${GREEN}set up for single player!${NC}"
    echo ""
    echo ""
    echo "Starting up the Docker containers. Please wait, this will take a while."
    echo ""
    sudo make start-single-player

    echo ""
    echo "Fetching the Game from the Open RSC git repo."
    echo ""
    sudo make clone-game
    sudo chmod -R 777 Game

    echo ""
    echo "Importing the game databases."
    echo ""
    sudo make import-game

    echo ""
    ./Linux_Single_Player.sh
# 1. Set up for single player <===================================================

# 2. Deployment for a publicly hosted server ===================================================>
elif [ "$choice" == "2" ]; then
    echo ""
    echo "You have picked ${GREEN}deploy to a VPS!${NC}"
    echo ""
    echo ""
    echo "Starting up the Docker containers."
    echo ""
    sudo chmod -R 777 .
    sudo make stop
    sudo make start

    # Website
    echo ""
    echo "Fetching the Website and Game from the Open RSC git repo."
    echo ""
    sudo make clone-game
    sudo make clone-website
    sudo chmod -R 777 .

    echo ""
    echo "Please enter your desired password for SQL user 'root'."
    echo ""
    read -s dbpass

    echo ""
    echo "Please enter your server's public domain name."
    echo ""
    read -s publicdomain

    echo ""
    echo "Please enter your server's private domain name if one exists or re-enter the public domain name again."
    echo ""
    read -s privatedomain

    echo ""
    echo "Do you want a Lets Encrypt HTTPS certificate installed?

    Choices:
      ${RED}1${NC} - Yes
      ${RED}2${NC} - No
    "
    echo ""
    echo "Which of the above do you wish to do? Type the choice number and press enter."
    echo ""
    read httpsask

    if [ "$httpsask" == "1" ]; then
        echo ""
        echo "Please enter your email address for Lets Encrypt HTTPS registration."
        echo ""
        read -s email

        sudo docker stop nginx
        sudo mv etc/nginx/default.conf etc/nginx/default.conf.BAK
        sudo mv etc/nginx/HTTPS_default.conf.BAK etc/nginx/default.conf
        sudo sed -i 's/live\/openrsc.com/live\/'"$publicdomain"'/g' etc/nginx/default.conf

        echo ""
        echo "Enabling HTTPS"
        echo ""
        sudo certbot certonly --standalone --preferred-challenges http --agree-tos -n --config-dir ./etc/letsencrypt -d $publicdomain -d $privatedomain --expand -m $email

    elif [ "$httpsask" == "2" ]; then
        continue
    fi

    #echo ""
    #echo "Please enter the name of your game."
    #echo ""
    #read -s gamename

    #echo ""
    #echo "What should the combat xp multiplier be? ex: 1, 1.5, 2, 5, 10"
    #echo ""
    #read -s xprate

    #echo ""
    #echo "What should the skill xp multiplier be? ex: 1, 1.5, 2, 5, 10"
    #echo ""
    #read -s skillrate

    #echo ""
    #echo "Should batched skills be enabled? 0 = disabled, 1 = try till success, 2 = full auto till empty"
    #echo ""
    #read -s loopmode

    # Automated edits of the .env file
    sudo sed -i 's/URL=http:\/\/localhost\/blog/URL=http:\/\/'"$publicdomain"'\/blog/g' .env
    sudo sed -i 's/NGINX_HOST=localhost/NGINX_HOST='"$publicdomain"'/g' .env
    sudo sed -i 's/MARIADB_PASS=pass/MARIADB_PASS='"$dbpass"'/g' .env
    sudo sed -i 's/MARIADB_ROOT_PASSWORD=root/MARIADB_ROOT_PASSWORD='"$dbpass"'/g' .env

    echo ""
    echo "Restarting Docker containers to enact changes."
    echo ""
    sudo make stop && sudo make start

    # Automated file edits
    #sudo sed -i 's/DB_PASS">root/DB_PASS">'"$dbpass"'/g' Game/server/config/config.xml
    #sudo sed -i 's/NAME">Open RSC/NAME">'"$gamename"'/g' Game/server/config/config.xml
    #sudo sed -i 's/\@OpenRSC/\@'"$gamename"'/g' Game/server/config/config.xml
    #sudo sed -i 's/COMBAT\_XP\_RATE">1/COMBAT\_XP\_RATE">'"$xprate"'/g' Game/server/config/config.xml
    #sudo sed -i 's/SKILL_XP_RATE">1/SKILL_XP_RATE">'"$skillrate"'/g' Game/server/config/config.xml
    #sudo sed -i 's/SKILL_LOOP_MODE">0/SKILL_LOOP_MODE">'"$loopmode"'/g' Game/server/config/config.xml
    #sudo sed -i 's/String IP = "127.0.0.1";/String IP = "'$privatedomain'";/g' Game/client/src/org/openrsc/client/Config.java

    echo ""
    echo "Importing the game database."
    echo ""
    sudo make import-game

    ./Linux_Fetch_Updates_Production.sh
# 2. Deployment for a publicly hosted server <===================================================

# 3. Backup ===================================================>
elif [ "$choice" == "3" ]; then
    backup:
    echo "You have picked ${GREEN}backup all databases!${NC}"
    echo ""
    make backup
    echo ""
    echo "Done! - Press enter to return back to the menu."
    read
    ./Go.sh
# 3. Backup <===================================================



else
    echo ""
    echo "Error! ${RED}$choice${NC} is not a valid option. Press enter to try again."
    echo ""
    read
    ./Go.sh
    continue
fi
