#!/bin/bash
exec 0</dev/tty

# Open RSC: A replica RSC private server framework
#
# Installs and updates Open RSC
#
# Install with this command (from your Linux machine):
#
# curl -sSL https://raw.githubusercontent.com/Open-RSC/Game/master/Linux_Cloner.sh | bash

if [[ "$OSTYPE" == "darwin"* ]]; then
    echo "MacOS detected. Performing needed actions to make this script work properly."
    which -s brew
    if [[ $? != 0 ]] ; then
        # Install Homebrew
        ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
        continue
    fi
    #brew tap AdoptOpenJDK/openjdk
    #brew install gnu-sed git newt unzip wget git curl zip screen adoptopenjdk-openjdk8 ant openjfx
    PATH="/usr/local/opt/gnu-sed/libexec/gnubin:$PATH"
fi

RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0` # No Color

function jumpto {
    label=$1
    cmd=$(sed -n "/$label:/{:a;n;p;ba};" $0 | grep -v ':$')
    eval "$cmd"
    exit
}
start=${1:-"start"}
jumpto $start

# Install Choice ===================================================>
start:
clear
echo "${RED}Open RSC Installer:${NC}
An easy to use RSC private server framework.

Which method of installation do you wish to use?

Choices:
  ${RED}1${NC} - Use Docker virtual containers (recommended)
  ${RED}2${NC} - Direct installation
  ${RED}3${NC} - Exit"
echo ""
echo "Which of the above do you wish to do? Type the choice number and press enter."
echo ""
read install

# Install Selection ===================================================>
if [ "$install" == "2" ]; then

    if (whiptail --title "Open RSC Native Installation" --yesno "Are you sure you wish to install Open RSC natively?" 7 70) then
        break
    else
        jumpto start
    fi

    phases=(
    'Installing Oracle JDK 8, MariaDB, nano, htop, screen, Apache Ant, and git...'
    'Installing PHP 7.2 and PHPMyAdmin...'
    'Treating Kessler Syndrome...'
    'Recruiting Kerbals...'
    )

    for i in $(seq 1 100); do

      sleep 1
      i=25
      echo -e "XXX\n$i\n${phases[phase]}\nXXX"
      sleep 1
      i=50
      echo -e "XXX\n$i\n${phases[phase]}\nXXX"
      sleep 1
      i=75
      echo -e "XXX\n$i\n${phases[phase]}\nXXX"
      sleep 1
      i=100
      exit

      if [ $i -eq 100 ]; then
          echo -e "XXX\n100\nDone!\nXXX"
      elif [ $(($i % 25)) -eq 0 ]; then
          let "phase = $i / 25"
          echo -e "XXX\n$i\n${phases[phase]}\nXXX"
      else
          echo $i
      fi
    done | whiptail --title 'Open RSC Native Installation' --gauge "${phases[0]}" 7 70 0


    # Software installations
    echo ""
    echo "Installing Oracle JDK 8, MariaDB, nano, htop, screen, Apache Ant, and git. Please wait."
    echo ""
    sudo add-apt-repository ppa:webupd8team/java -y
    sudo LC_ALL=C.UTF-8 add-apt-repository ppa:ondrej/php -y
    sudo apt-get update
    sudo apt remove mysql-server mysql-server-5.7 mysql-client apache2 -y
    sudo apt-get install nano htop screen ant git oracle-java8-installer mariadb-server mariadb-client nginx -y
    sudo apt-get autoremove -y

    # PHPMyAdmin installation
    echo ""
    echo "Installing PHP 7.2 and PHPMyAdmin. Please wait."
    echo ""
    sudo apt-get install php php-cgi php-common php-pear php-mbstring php-fpm php-mysql php-gettext phpmyadmin -y
    sudo phpenmod mbstring
    sudo systemctl restart nginx

    # Database configuration
    echo ""
    sudo mysql_secure_installation
    echo ""
    echo "Please enter your MySQL password."
    echo ""
    read -s pass
    echo ""
    echo "Please enter your server's domain name."
    echo ""
    read -s domain
    sudo mysql -uroot -Bse "DROP USER 'openrsc'@'localhost';FLUSH PRIVILEGES;"
    sudo mysql -uroot -Bse "CREATE USER 'openrsc'@'localhost' IDENTIFIED BY '$pass';GRANT ALL PRIVILEGES ON * . * TO 'openrsc'@'localhost';FLUSH PRIVILEGES;"

    # Database imports
    echo ""
    echo "Importing database."
    echo ""
    sudo mysql -u"root" -p"$pass" < "openrsc_game.sql"

    # Automated file edits
    #sudo sed -i 's/DB_LOGIN">root/DB_LOGIN">openrsc/g' server/config/config.xml
    #sudo sed -i 's/DB_PASS">root/DB_PASS">'$pass'/g' server/config/config.xml
    #sudo sed -i 's/String IP = "127.0.0.1";/String IP = "'$domain'";/g' client/src/org/openrsc/client/Config.java

    # Website
    sudo mkdir /var/www/html/downloads

    # Server
    echo ""
    echo "Compiling the game server."
    echo ""
    sudo ant -f "server/build.xml" compile_core
    sudo ant -f "server/build.xml" compile_plugins

    # Client
    echo ""
    echo "Compiling and preparing the game client."
    echo ""
    sudo ant -f "client/build.xml" compile
    yes | sudo cp -rf "client/Open_RSC_Client.jar" "/var/www/html/downloads"

    # Launcher
    echo ""
    echo "Compiling and preparing the game launcher. Any errors will be in updater.log"
    echo ""
    sudo ant -f "Launcher/nbbuild.xml" jar
    yes | sudo cp -rf "Launcher/dist/Open_RSC_Launcher.jar" "/var/www/html/downloads/"

    # Cache
    echo ""
    echo "Preparing the cache."
    echo ""
    yes | sudo cp -a -rf "client/Cache/." "/var/www/html/downloads/cache/"
    sudo rm /var/www/html/downloads/cache/MD5CHECKSUM
    sudo touch /var/www/html/downloads/cache/MD5CHECKSUM && sudo chmod 777 /var/www/html/downloads/cache/MD5CHECKSUM
    md5sum /var/www/html/downloads/cache/* | sed 's/\/var\/www\/html\/downloads\/cache\///g' |  grep ^[a-zA-Z0-9]* | awk '{print $2"="$1}' | tee /var/www/html/downloads/cache/MD5CHECKSUM
    sudo sed -i 's/MD5CHECKSUM=/#MD5CHECKSUM=/g' "/var/www/html/downloads/cache/MD5CHECKSUM"

    # Completion
    myip="$(dig +short myip.opendns.com @resolver1.opendns.com)"
    echo ""
    cd Game
    echo "The installation script has completed."
    echo ""
    echo "You should now be able to download the game launcher at: http://${myip}/downloads/Open_RSC_Launcher.jar"
    echo ""
    echo "Launch the game server via: ./Linux_Simple_Run.sh"
    echo ""
fi

# Exit ===================================================>
if [ "$install" == "3" ]; then
  exit
fi
# Exit <===================================================

# Docker Selection ===================================================>
if [ "$install" == "1" ]; then
    echo ""
    echo "Which operating system are you running?"
    echo ""
    echo "${RED}1${NC} - Ubuntu Linux 18.04 or above"
    echo "${RED}2${NC} - Mac OS High Sierra or above"
    echo ""
    echo "Which of the above do you wish to do? Type the choice number and press enter."
    echo ""
    read os

    # Ubuntu OS ===================================================>
    if [ "$os" == "1" ]; then
        sudo dpkg-reconfigure tzdata

        echo ""
        echo "Installing required software. Please wait, this will take a while."
        echo "Installing certbot, screen, zip, fail2ban, unzip, git, build-essential, "
        echo "software-properties-common, apt-transport-https, ca-certificates, and curl."
        echo ""
        sudo apt-get update
        sudo apt-get install software-properties-common -y
        sudo add-apt-repository ppa:certbot/certbot -y
        sudo apt-get update
        sudo apt-get install certbot screen zip fail2ban unzip git build-essential apt-transport-https ca-certificates curl -y

        echo ""
        echo "Attempting to install Docker CE and Docker Compose. Please wait."
        echo ""
        curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
        sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu bionic stable"
        sudo apt-get update && sudo apt-get install docker-ce docker-compose -y
        sudo setfacl -m user:$USER:rw /var/run/docker.sock

        echo ""
        echo "Setting Docker to have the correct storage driver and restarting the service."
        echo ""
        echo '{
    "storage-driver": "devicemapper"
}' | sudo tee /etc/docker/daemon.json && sudo service docker restart

        echo ""
        echo "Setting Ubuntu Firewall permissions."
        echo ""
        sudo ufw allow 22/tcp && sudo ufw allow 80/tcp && sudo ufw allow 8080/tcp && sudo ufw allow 443/tcp && sudo ufw allow 55555/tcp && sudo ufw allow 53595/tcp && sudo ufw deny 3306/tcp
        sudo sed -i 's/DEFAULT_FORWARD_POLICY="DENY"/DEFAULT_FORWARD_POLICY="ACCEPT"/g' /etc/default/ufw
        sudo ufw reload
        sudo ufw --force enable

        echo ""
        echo "Installing Oracle Java JDK 8, openjfx, and Apache ant. Please wait."
        echo ""
        sudo apt-get remove -y openjdk-6-jre default-jre default-jre-headless
        sudo add-apt-repository -y ppa:webupd8team/java
        sudo apt update
        sudo apt install -y openjfx ant
        echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | sudo debconf-set-selections
        sudo apt-get install -y oracle-java8-installer
        sudo apt install oracle-java8-set-default
        fi
    # Ubuntu OS <===================================================

    # Mac OS ===================================================>
  elif [ "$os" == "2" ]; then
        echo ""
        echo "Downloading the Docker for Mac installer."
        echo ""
        wget https://download.docker.com/mac/stable/Docker.dmg
        hdiutil attach Docker.dmg
        echo ""
        echo "Please drag Docker as instructed in the popup."
        echo ""
        echo "Press enter when finished."
        read

        echo ""
        open /Applications/Docker.app
        echo ""
        echo "Docker is launching. Please follow the directions that it gives you."
        echo ""
        echo "Press enter when finished."
        read
        fi
    # Mac OS <===================================================

# Install Choice <===================================================

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

  echo "You have picked ${GREEN}backup all databases!${NC}"
  echo ""
  sudo make backup
  echo ""
  echo "Done! - Press enter to return back to the menu."
  read
  ./Linux_Installer.sh
# 3. Backup <===================================================

else
    echo ""
    echo "Error! ${RED}$choice${NC} is not a valid option. Press enter to try again."
    echo ""
    read
    ./Linux_Installer.sh
    continue
fi
