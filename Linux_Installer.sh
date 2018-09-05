#!/bin/bash
exec 0</dev/tty

# Open RSC: A replica RSC private server framework
#
# Installs and updates Open RSC
#
# Install with this command (from your Linux machine):
#
# curl -sSL https://raw.githubusercontent.com/Open-RSC/Docker-Home/master/Linux_Cloner.sh | bash

rm installer.log
touch installer.log && chmod 777 installer.log | tee installer.log &>/dev/null

choice=""
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0` # No Color

# Install Choice ===================================================>
clear
echo "${RED}Open RSC Installer:${NC}
An easy to use RSC private server environment using Docker magic.

Do you wish to have all the pre-requiste software installed by this script?

Choices:
  ${RED}1${NC} - Yes please, install for me!
  ${RED}2${NC} - No thanks, continue (default)"
echo ""
echo "Which of the above do you wish to do? Type the choice number and press enter."
read install

# OS Selection ===================================================>
if [ "$install" == "1" ]; then
    clear
    echo "Which operating system are you running?"
    echo ""
    echo "${RED}1${NC} - Ubuntu Linux 18.04 or above"
    echo "${RED}2${NC} - Mac OS High Sierra or above"
    echo ""
    echo "Which of the above do you wish to do? Type the choice number and press enter."
    read os

    # Ubuntu OS ===================================================>
    if [ "$os" == "1" ]; then
        clear
        sudo dpkg-reconfigure tzdata

        clear
        echo "Installing required software. Please wait, this will take a while."
        echo "Installing certbot, screen, zip, fail2ban, unzip, git, build-essential, "
        echo "software-properties-common, apt-transport-https, ca-certificates, and curl."
        echo ""
        echo "Installation logs are being sent to installer.log"
        sudo apt-get update | tee -a installer.log &>/dev/null
        sudo apt-get install software-properties-common -y | tee -a installer.log &>/dev/null
        sudo add-apt-repository ppa:certbot/certbot -y | tee -a installer.log &>/dev/null
        sudo apt-get update | tee -a installer.log &>/dev/null
        sudo apt-get install certbot screen zip fail2ban unzip git build-essential apt-transport-https ca-certificates curl -y | tee -a installer.log &>/dev/null

        clear
        echo "Attempting to install Docker CE and Docker Compose. Please wait."
        curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add - | tee -a installer.log &>/dev/null
        sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu bionic stable" | tee -a installer.log &>/dev/null
        sudo apt-get update | tee -a installer.log &>/dev/null && sudo apt-get install docker-ce docker-compose -y | tee -a installer.log &>/dev/null
        sudo setfacl -m user:$USER:rw /var/run/docker.sock | tee -a ../installer.log &>/dev/null

        clear
        echo "Setting Docker to have the correct storage driver and restarting the service."
        echo '{
    "storage-driver": "devicemapper"
}' | sudo tee /etc/docker/daemon.json && sudo service docker restart | tee -a installer.log &>/dev/null

        clear
        echo "Setting Ubuntu Firewall permissions."
        sudo ufw allow 22/tcp | tee -a installer.log &>/dev/null && sudo ufw allow 80/tcp | tee -a installer.log &>/dev/null && sudo ufw allow 8080/tcp | tee -a installer.log &>/dev/null && sudo ufw allow 443/tcp | tee -a installer.log &>/dev/null && sudo ufw allow 55555/tcp | tee -a installer.log &>/dev/null && sudo ufw allow 53595/tcp | tee -a installer.log &>/dev/null && sudo ufw deny 3306/tcp | tee -a installer.log &>/dev/null
        sudo sed -i 's/DEFAULT_FORWARD_POLICY="DENY"/DEFAULT_FORWARD_POLICY="ACCEPT"/g' /etc/default/ufw | tee -a installer.log &>/dev/null
        sudo ufw reload | tee -a installer.log &>/dev/null
        sudo ufw --force enable | tee -a installer.log &>/dev/null

        clear
        echo "Installing Oracle Java JDK 8, openjfx, and Apache ant. Please wait."
        sudo apt-get remove -y openjdk-6-jre default-jre default-jre-headless | tee -a installer.log &>/dev/null
        sudo add-apt-repository -y ppa:webupd8team/java | tee -a installer.log &>/dev/null
        sudo apt update | tee -a installer.log &>/dev/null
        sudo apt install -y openjfx ant | tee -a installer.log &>/dev/null
        echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | sudo debconf-set-selections | tee -a installer.log &>/dev/null
        sudo apt-get install -y oracle-java8-installer | tee -a installer.log &>/dev/null
        sudo apt install oracle-java8-set-default | tee -a installer.log &>/dev/null
        fi
    # Ubuntu OS <===================================================

    # Mac OS ===================================================>
  elif [ "$os" == "2" ]; then
        clear
        echo "Do you have Brew installed? It is required for this."
        echo ""
        echo "${RED}1${NC} - No, install it for me!"
        echo "${RED}2${NC} - Yes"
        echo ""
        echo "Which of the above do you wish to do? Type the choice number and press enter."
        read brew

        # Mac Brew ===================================================>
        if [ "$brew" == "1" ]; then
            /usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)" | tee -a installer.log &>/dev/null
            brew install git | tee -a installer.log &>/dev/null
        fi
        # Mac Brew <===================================================

        clear
        echo "Verifying the basics are installed via Brew."
        brew install unzip wget git curl zip screen | tee -a installer.log &>/dev/null
        brew tap AdoptOpenJDK/openjdk | tee -a installer.log &>/dev/null
        brew install adoptopenjdk-openjdk8 ant openjfx | tee -a installer.log &>/dev/null

        clear
        echo "Downloading the Docker for Mac installer."
        wget https://download.docker.com/mac/stable/Docker.dmg | tee -a installer.log &>/dev/null
        hdiutil attach Docker.dmg | tee -a installer.log &>/dev/null
        echo ""
        echo "Please drag Docker as instructed in the popup."
        echo ""
        echo "Press enter when finished."
        read

        clear
        open /Applications/Docker.app
        echo "Docker is launching. Please follow the directions that it gives you."
        echo ""
        echo "Press enter when finished."
        read
        fi
    # Mac OS <===================================================
    # OS Selection <===================================================

# Install Choice <===================================================

clear
echo "Fetching updates from the Docker-Home GitHub repository."
sudo git pull | tee -a installer.log &>/dev/null

clear
echo "${RED}Open RSC Installer:${NC}
An easy to use RSC private server using Docker magic.

Choices:
  ${RED}1${NC} - Set up for single player
  ${RED}2${NC} - Deploy to a VPS
  ${RED}3${NC} - Backup all databases
"
echo ""
echo "Which of the above do you wish to do? Type the choice number and press enter."
read choice

# Selection ===================================================>
# 1. Set up for single player ===================================================>
if [ "$choice" == "1" ]; then
    clear
    echo "You have picked ${GREEN}set up for single player!${NC}"
    echo ""
    echo ""
    echo "Starting up the Docker containers. Please wait, this will take a while."
    echo ""
    echo "Installation logs are being sent to installer.log"
    sudo make start-single-player | tee -a installer.log &>/dev/null

    clear
    echo "Fetching the Game from the Open RSC git repo."
    sudo make clone-game-v2 | tee -a installer.log &>/dev/null
    sudo chmod -R 777 Game | tee -a installer.log &>/dev/null

    clear
    echo "Importing the game databases."
    sudo make import-game-v2 | tee -a installer.log &>/dev/null

    clear
    ./v2_Linux_Single_Player.sh
# 1. Set up for single player <===================================================

# 2. Deployment for a publicly hosted server ===================================================>
elif [ "$choice" == "2" ]; then
    clear
    echo "You have picked ${GREEN}deploy to a VPS!${NC}"
    echo ""
    echo ""
    echo "Starting up the Docker containers."
    sudo chmod -R 777 . | tee -a installer.log &>/dev/null
    sudo make stop | tee -a installer.log &>/dev/null
    sudo make start | tee -a installer.log &>/dev/null

    # Website
    clear
    echo "Fetching the Website and Game from the Open RSC git repo."
    sudo make clone-game-v2 | tee -a installer.log &>/dev/null
    sudo make clone-website-v2 | tee -a installer.log &>/dev/null
    sudo chmod -R 777 . | tee -a installer.log &>/dev/null

    clear
    echo "Please enter your desired password for SQL user 'root'."
    read -s dbpass

    clear
    echo "Please enter your server's public domain name."
    read -s publicdomain

    clear
    echo "Please enter your server's private domain name if one exists or re-enter the public domain name again."
    read -s privatedomain

    clear
    echo "Do you want a Lets Encrypt HTTPS certificate installed?

    Choices:
      ${RED}1${NC} - Yes
      ${RED}2${NC} - No
    "
    echo ""
    echo "Which of the above do you wish to do? Type the choice number and press enter."
    read httpsask

    if [ "$httpsask" == "1" ]; then
        clear
        echo "Please enter your email address for Lets Encrypt HTTPS registration."
        read -s email

        sudo docker stop nginx | tee -a installer.log &>/dev/null
        sudo mv etc/nginx/default.conf etc/nginx/default.conf.BAK | tee -a installer.log &>/dev/null
        sudo mv etc/nginx/HTTPS_default.conf.BAK etc/nginx/default.conf | tee -a installer.log &>/dev/null
        sudo sed -i 's/live\/openrsc.com/live\/'"$publicdomain"'/g' etc/nginx/default.conf | tee -a installer.log &>/dev/null

        clear
        echo "Enabling HTTPS"

        sudo certbot certonly --standalone --preferred-challenges http --agree-tos -n --config-dir ./etc/letsencrypt -d $publicdomain -d $privatedomain --expand -m $email | tee -a installer.log &>/dev/null

    elif [ "$httpsask" == "2" ]; then
        continue
    fi

    #clear
    #echo "Please enter the name of your game."
    #read -s gamename

    #clear
    #echo "What should the combat xp multiplier be? ex: 1, 1.5, 2, 5, 10"
    #read -s xprate

    #clear
    #echo "What should the skill xp multiplier be? ex: 1, 1.5, 2, 5, 10"
    #read -s skillrate

    #clear
    #echo "Should batched skills be enabled? 0 = disabled, 1 = try till success, 2 = full auto till empty"
    #read -s loopmode

    # Automated edits of the .env file
    clear
    sudo sed -i 's/URL=http:\/\/localhost\/blog/URL=http:\/\/'"$publicdomain"'\/blog/g' .env | tee -a installer.log &>/dev/null
    sudo sed -i 's/NGINX_HOST=localhost/NGINX_HOST='"$publicdomain"'/g' .env | tee -a installer.log &>/dev/null
    sudo sed -i 's/MARIADB_PASS=pass/MARIADB_PASS='"$dbpass"'/g' .env | tee -a installer.log &>/dev/null
    sudo sed -i 's/MARIADB_ROOT_PASSWORD=root/MARIADB_ROOT_PASSWORD='"$dbpass"'/g' .env | tee -a installer.log &>/dev/null

    clear
    echo "Restarting Docker containers to enact changes."
    sudo make stop | tee -a installer.log &>/dev/null && sudo make start | tee -a installer.log &>/dev/null

    # Automated file edits
    #clear
    #echo "Configuring Open RSC based on your input."
    #sudo sed -i 's/DB_PASS">root/DB_PASS">'"$dbpass"'/g' Game/server/config/config.xml | tee -a installer.log &>/dev/null
    #sudo sed -i 's/NAME">Open RSC/NAME">'"$gamename"'/g' Game/server/config/config.xml | tee -a installer.log &>/dev/null
    #sudo sed -i 's/\@OpenRSC/\@'"$gamename"'/g' Game/server/config/config.xml | tee -a installer.log &>/dev/null
    #sudo sed -i 's/COMBAT\_XP\_RATE">1/COMBAT\_XP\_RATE">'"$xprate"'/g' Game/server/config/config.xml | tee -a installer.log &>/dev/null
    #sudo sed -i 's/SKILL_XP_RATE">1/SKILL_XP_RATE">'"$skillrate"'/g' Game/server/config/config.xml | tee -a installer.log &>/dev/null
    #sudo sed -i 's/SKILL_LOOP_MODE">0/SKILL_LOOP_MODE">'"$loopmode"'/g' Game/server/config/config.xml | tee -a installer.log &>/dev/null
    #sudo sed -i 's/String IP = "127.0.0.1";/String IP = "'$privatedomain'";/g' Game/client/src/org/openrsc/client/Config.java | tee -a installer.log &>/dev/null

    clear
    echo "Importing the game database."
    sudo make import-game-v2 | tee -a installer.log &>/dev/null

    clear
    ./v2_Linux_Fetch_Updates_Production.sh
# 2. Deployment for a publicly hosted server <===================================================

# 3. Backup ===================================================>
elif [ "$choice" == "3" ]; then

  echo "You have picked ${GREEN}backup all databases!${NC}"
  sudo make backup | tee -a installer.log &>/dev/null
  clear
  echo "Done! - Press enter to return back to the menu."
  read
  ./v2_Linux_Installer.sh
# 3. Backup <===================================================

else
    clear
    echo "Error! ${RED}$choice${NC} is not a valid option. Press enter to try again."
    echo ""
    read
    ./v2_Linux_Installer.sh
    continue
fi
