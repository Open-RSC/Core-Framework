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
    #brew tap AdoptOpenJDK/openjdk && brew install gnu-sed git newt unzip wget git curl zip screen adoptopenjdk-openjdk8 ant openjfx
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

    pass=$(whiptail --passwordbox "Please enter your desired MySQL password." 8 50 --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    domain=$(whiptail --inputbox "Please enter your server's domain name. (No http:// or www. needed)" 8 50 --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    tick=$(whiptail --inputbox "What speed should the game run? (620 is the default and 320 is twice as fast)" 8 50 620 --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    gamename=$(whiptail --inputbox "Please enter the name of your game." 8 50 --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    combatrate=$(whiptail --inputbox "Please enter the combat XP rate multiplier." 8 50 1 --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    skillrate=$(whiptail --inputbox "Please enter the skilling XP rate multiplier." 8 50 1 --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    banksize=$(whiptail --inputbox "Please enter the max bank size." 8 50 192 --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    auctionhouse=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Allow the auctionhouse" OFF \
        "false" "Do not allow the auctionhouse" ON 3>&1 1>&2 2>&3)
    ironman=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Allow ironman mode" OFF \
        "false" "Do not allow ironman mode" ON 3>&1 1>&2 2>&3)
    nametags=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Allow name tags over players" OFF \
        "false" "Do not allow name tags over players" ON 3>&1 1>&2 2>&3)
    clans=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Allow player clans" OFF \
        "false" "Do not allow player clans" ON 3>&1 1>&2 2>&3)
    killfeed=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Allow the kill feed" OFF \
        "false" "Do not allow the kill feed" ON 3>&1 1>&2 2>&3)
    fog=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Allow fog to be disabled" OFF \
        "false" "Do not allow fog to be disabled" ON 3>&1 1>&2 2>&3)
    grounditems=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Allow ground items to be hidden" OFF \
        "false" "Do not allow ground items to be hidden" ON 3>&1 1>&2 2>&3)
    batchprogression=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Allow batch mode progression" OFF \
        "false" "Do not allow batch mode progression" ON 3>&1 1>&2 2>&3)
    sidemenu=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Allow the side menu to be toggled on" OFF \
        "false" "Do not allow the side menu to be toggled on" ON 3>&1 1>&2 2>&3)
    inventorycount=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Allow inventory counting mode" OFF \
        "false" "Do not allow inventory counting mode" ON 3>&1 1>&2 2>&3)
    itemdeath=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Show the items on death menu option" OFF \
        "false" "Do not show the items on death menu option" ON 3>&1 1>&2 2>&3)
    globalchat=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Allow global chat" OFF \
        "false" "Do not allow global chat" ON 3>&1 1>&2 2>&3)
    skillmenus=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Allow skill menus" OFF \
        "false" "Do not allow skill menus" ON 3>&1 1>&2 2>&3)
    custombank=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Allow enhanced banking mode" OFF \
        "false" "Do not allow enhanced banking mode" ON 3>&1 1>&2 2>&3)
    bankpins=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Allow bank pins" OFF \
        "false" "Do not allow bank pins" ON 3>&1 1>&2 2>&3)
    dropx=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Allow drop X" OFF \
        "false" "Do not allow drop X" ON 3>&1 1>&2 2>&3)


        function firstroutine {
        $guake=true
        }
        function secondroutine {
        $gtkash=true
        }
        function thirdroutine {
        $nemogtkhash=true
        }
        whiptail --title "Test" --checklist --separate-output "Choose:" 20 78 15 \
        "guake" "" off \
        "gtkash" "" off \
        "nemogtkhash" "" off 2>results
        while read choice
        do case $choice in
        guake) firstroutine;;
        gtkash) secondroutine;;
        namogtkhash) thirdroutine;;
        *) ;;
        esac
        done < results





    =$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Allow " OFF \
        "false" "Do not allow " ON 3>&1 1>&2 2>&3)
    =$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Allow " OFF \
        "false" "Do not allow " ON 3>&1 1>&2 2>&3)
    =$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Allow " OFF \
        "false" "Do not allow " ON 3>&1 1>&2 2>&3)
    =$(whiptail --inputbox "" 8 50 DEF --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    =$(whiptail --inputbox "" 8 50 DEF --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    =$(whiptail --inputbox "" 8 50 DEF --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    =$(whiptail --inputbox "" 8 50 DEF --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    =$(whiptail --inputbox "" 8 50 DEF --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    =$(whiptail --inputbox "" 8 50 DEF --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    =$(whiptail --inputbox "" 8 50 DEF --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    =$(whiptail --inputbox "" 8 50 DEF --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    =$(whiptail --inputbox "" 8 50 DEF --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    =$(whiptail --inputbox "" 8 50 DEF --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    =$(whiptail --inputbox "" 8 50 DEF --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    =$(whiptail --inputbox "" 8 50 DEF --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    =$(whiptail --inputbox "" 8 50 DEF --title "Open RSC Configuration" 3>&1 1>&2 2>&3)

    phases=(
    'Installing Oracle JDK 8, MariaDB, nano, htop, screen, Apache Ant, and git...' #10
    'Installing PHP 7.2 and PHPMyAdmin...' #20
    'Setting up databases...' #30
    'Configuring game files based on your input...' #40
    'Creating the website downloads folder...' #60
    'Compiling the game server...' #70
    'Compiling and preparing the game client...' #80
    'Compiling and preparing the game launcher...' #90
    'Preparing the cache...' #100
    )
    for i in $(seq 1 100); do
        echo -e "XXX\n$i\n${phases[0]}\nXXX"
        i=10
        # Software installations
        sudo add-apt-repository ppa:webupd8team/java -y
        sudo LC_ALL=C.UTF-8 add-apt-repository ppa:ondrej/php -y
        sudo apt-get update
        sudo apt remove mysql-server mysql-server-5.7 mysql-client apache2 -y
        sudo apt-get install nano htop screen ant git oracle-java8-installer mariadb-server mariadb-client nginx -y
        sudo apt-get autoremove -y

        echo -e "XXX\n$i\n${phases[1]}\nXXX"
        i=20
        # PHPMyAdmin installation
        sudo apt-get install php php-cgi php-common php-pear php-mbstring php-fpm php-mysql php-gettext phpmyadmin -y
        sudo phpenmod mbstring
        sudo systemctl restart nginx

        echo -e "XXX\n$i\n${phases[2]}\nXXX"
        i=30
        sleep 1
        # Database configuration and imports
        sudo mysql_secure_installation
        sudo mysql -uroot -Bse "DROP USER 'openrsc'@'localhost';FLUSH PRIVILEGES;"
        sudo mysql -uroot -Bse "CREATE USER 'openrsc'@'localhost' IDENTIFIED BY '$pass';GRANT ALL PRIVILEGES ON * . * TO 'openrsc'@'localhost';FLUSH PRIVILEGES;"
        sudo mysql -u"root" -p"$pass" < "Databases/openrsc_game.sql"
        sudo mysql -u"root" -p"$pass" < "Databases/openrsc_forum.sql"

        echo -e "XXX\n$i\n${phases[3]}\nXXX"
        i=40
        # Automated file edits
        sudo sed -i 's/mysql_user">root/mysql_user">openrsc/g' server/free.conf
        sudo sed -i 's/mysql_user">root/mysql_user">openrsc/g' server/members.conf
        sudo sed -i 's/mysql_pass">root/mysql_pass">'$pass'/g' server/free.conf
        sudo sed -i 's/mysql_pass">root/mysql_pass">'$pass'/g' server/members.conf
        sudo sed -i 's/game_tick">620/game_tick">'$tick'/g' server/free.conf
        sudo sed -i 's/game_tick">620/game_tick">'$tick'/g' server/members.conf
        sudo sed -i 's/server_name">Open RSC/server_name">"'$gamename'"/g' server/free.conf
        sudo sed -i 's/server_name">Open RSC/server_name">"'$gamename'"/g' server/members.conf
        sudo sed -i 's/combat_exp_rate">1/combat_exp_rate">'$combatrate'/g' server/free.conf
        sudo sed -i 's/combat_exp_rate">1/combat_exp_rate">'$combatrate'/g' server/members.conf
        sudo sed -i 's/skilling_exp_rate">1/skilling_exp_rate">'$skillrate'/g' server/free.conf
        sudo sed -i 's/skilling_exp_rate">1/skilling_exp_rate">'$skillrate'/g' server/members.conf
        sudo sed -i 's/bank_size">192/bank_size">'$banksize'/g' server/free.conf
        sudo sed -i 's/bank_size">192/bank_size">'$banksize'/g' server/members.conf
        sudo sed -i 's/spawn_auction_npcs">false/spawn_auction_npcs">'$auctionhouse'/g' server/free.conf
        sudo sed -i 's/spawn_auction_npcs">false/spawn_auction_npcs">'$auctionhouse'/g' server/members.conf
        sudo sed -i 's/A/'$'/g' server/free.conf
        sudo sed -i 's/A/'$'/g' server/free.conf
        sudo sed -i 's/A/'$'/g' server/free.conf
        sudo sed -i 's/A/'$'/g' server/free.conf
        sudo sed -i 's/A/'$'/g' server/free.conf
        sudo sed -i 's/A/'$'/g' server/free.conf
        sudo sed -i 's/A/'$'/g' server/free.conf
        sudo sed -i 's/A/'$'/g' server/free.conf
        sudo sed -i 's/A/'$'/g' server/free.conf
        sudo sed -i 's/A/'$'/g' server/free.conf
        sudo sed -i 's/A/'$'/g' server/free.conf
        sudo sed -i 's/A/'$'/g' server/free.conf
        sudo sed -i 's/A/'$'/g' server/free.conf
        sudo sed -i 's/A/'$'/g' server/free.conf
        sudo sed -i 's/A/'$'/g' server/free.conf
        sudo sed -i 's/A/'$'/g' server/free.conf
        sudo sed -i 's/A/'$'/g' server/free.conf
        sudo sed -i 's/A/'$'/g' server/free.conf

        sudo sed -i 's/String IP = "127.0.0.1";/String IP = "'$domain'";/g' client/src/org/openrsc/client/Config.java

        echo -e "XXX\n$i\n${phases[4]}\nXXX"
        i=60
        # Create Website downloads folder
        sudo mkdir /var/www/html/downloads

        echo -e "XXX\n$i\n${phases[5]}\nXXX"
        i=70
        # Server
        sudo ant -f "server/build.xml" compile_core
        sudo ant -f "server/build.xml" compile_plugins

        echo -e "XXX\n$i\n${phases[6]}\nXXX"
        i=80
        # Client
        sudo ant -f "client/build.xml" compile
        yes | sudo cp -rf "client/Open_RSC_Client.jar" "/var/www/html/downloads"

        echo -e "XXX\n$i\n${phases[7]}\nXXX"
        i=90
        # Launcher
        sudo ant -f "Launcher/nbbuild.xml" jar
        yes | sudo cp -rf "Launcher/dist/Open_RSC_Launcher.jar" "/var/www/html/downloads/"

        echo -e "XXX\n$i\n${phases[8]}\nXXX"
        i=100
        # Cache
        yes | sudo cp -a -rf "client/Cache/." "/var/www/html/downloads/cache/"
        sudo rm /var/www/html/downloads/cache/MD5CHECKSUM
        sudo touch /var/www/html/downloads/cache/MD5CHECKSUM && sudo chmod 777 /var/www/html/downloads/cache/MD5CHECKSUM
        md5sum /var/www/html/downloads/cache/* | sed 's/\/var\/www\/html\/downloads\/cache\///g' |  grep ^[a-zA-Z0-9]* | awk '{print $2"="$1}' | tee /var/www/html/downloads/cache/MD5CHECKSUM
        sudo sed -i 's/MD5CHECKSUM=/#MD5CHECKSUM=/g' "/var/www/html/downloads/cache/MD5CHECKSUM"

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
