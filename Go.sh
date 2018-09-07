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
    echo "Apple MacOS detected. Performing needed actions to make this script work properly."
    which -s brew
    if [[ $? != 0 ]] ; then
        # Install Homebrew
        ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
        continue
    fi
    brew tap AdoptOpenJDK/openjdk && brew install gnu-sed git newt unzip wget git curl zip screen adoptopenjdk-openjdk8 ant openjfx
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
deployment=${1:-"deployment"}
jumpto $start

# Start ===================================================>
start:
clear
echo "${RED}Open RSC:${NC}
An easy to use RSC private server framework.

What would you like to do?

Choices:
  ${RED}1${NC} - Install
  ${RED}2${NC} - Update
  ${RED}3${NC} - Run
  ${RED}4${NC} - Exit"
echo ""
echo "Which of the above do you wish to do? Type the choice number and press enter."
echo ""
read action

if [ "$action" == "1" ]; then
    jumpto $installer
elif [ "$action" == "2" ]; then
    jumpto $deployment
elif [ "$action" == "3" ]; then
    jumpto $run
elif [ "$action" == "4" ]; then
    exit
fi
# Start <===================================================>

# Install Choice ===================================================>
installer:
clear
echo "${RED}Open RSC Installer:${NC}
An easy to use RSC private server framework.

Which method of installation do you wish to use?

Choices:
  ${RED}1${NC} - Use Docker virtual containers (recommended)
  ${RED}2${NC} - Direct installation (Ubuntu Linux only)
  ${RED}3${NC} - Skip to game deployment
  ${RED}4${NC} - Exit"
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
        "true" "Enable the auctionhouse" OFF \
        "false" "Do not enable the auctionhouse" ON 3>&1 1>&2 2>&3)
    ironman=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Enable ironman mode" OFF \
        "false" "Do not enable ironman mode" ON 3>&1 1>&2 2>&3)
    nametags=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Enable name tags over players" OFF \
        "false" "Do not enable name tags over players" ON 3>&1 1>&2 2>&3)
    clans=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Enable player clans" OFF \
        "false" "Do not enable player clans" ON 3>&1 1>&2 2>&3)
    fog=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Allow fog to be disabled" OFF \
        "false" "Do not allow fog to be disabled" ON 3>&1 1>&2 2>&3)
    grounditems=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Allow ground items to be hidden" OFF \
        "false" "Do not allow ground items to be hidden" ON 3>&1 1>&2 2>&3)
    batchprogression=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Enable batch mode progression" OFF \
        "false" "Do not enable batch mode progression" ON 3>&1 1>&2 2>&3)
    sidemenu=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Enable the side menu to be toggled" OFF \
        "false" "Do not enable the side menu to be toggled" ON 3>&1 1>&2 2>&3)
    inventorycount=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Enable inventory counting mode" OFF \
        "false" "Do not enable inventory counting mode" ON 3>&1 1>&2 2>&3)
    itemdeath=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Show the items on death menu" OFF \
        "false" "Do not show the items on death menu" ON 3>&1 1>&2 2>&3)
    globalchat=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Enable global chat" OFF \
        "false" "Do not enable global chat" ON 3>&1 1>&2 2>&3)
    custombank=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Enable enhanced banking mode" OFF \
        "false" "Do not enable enhanced banking mode" ON 3>&1 1>&2 2>&3)
    bankpins=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Enable bank pins" OFF \
        "false" "Do not enable bank pins" ON 3>&1 1>&2 2>&3)
    dropx=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Enable drop X" OFF \
        "false" "Do not enable drop X" ON 3>&1 1>&2 2>&3)
    killfeed=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Enable kill feed" OFF \
        "false" "Do not enable kill feed" ON 3>&1 1>&2 2>&3)
    zoomview=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Enable zoom view toggle" OFF \
        "false" "Do not enable zoom view toggle" ON 3>&1 1>&2 2>&3)
    expcounter=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Enable experience counters" OFF \
        "false" "Do not enable experience counters" ON 3>&1 1>&2 2>&3)
    expinfo=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Enable custom experience info" OFF \
        "false" "Do not enable custom experience info" ON 3>&1 1>&2 2>&3)
    experiencedrops=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Enable experience drops" OFF \
        "false" "Do not enable experience drops" ON 3>&1 1>&2 2>&3)
    skillmenu=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Enable skill menus" OFF \
        "false" "Do not enable skill menus" ON 3>&1 1>&2 2>&3)
    questmenu=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Enable quest menus" OFF \
        "false" "Do not enable quest menus" ON 3>&1 1>&2 2>&3)
    shortcuts=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Enable keyboard shortcuts" OFF \
        "false" "Do not enable keyboard shortcuts" ON 3>&1 1>&2 2>&3)
    customfiremaking=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
        "true" "Enable custom firemaking" OFF \
        "false" "Do not enable custom firemaking" ON 3>&1 1>&2 2>&3)

    phases=(
    'Uninstalling previous conflicting software' #1
    'Adding needed repositories' #2
    'Running APT Update' #3
    'Installing nano and htop' #4
    'Installing screen and git' #5
    'Installing MariaDB server and client' #6
    'Installing Nginx' #7
    'Accepting Oracle JDK 8 licence' #8
    'Installing Oracle JDK 8 and Apache Ant' #10
    'Installing PHP and PHP-CGI' #11
    'Installing PHP-Common, PHP-Pear, and PHP-MBString' #13
    'Installing PHP-FPM and PHP-MySQL' #15
    'Installing PHP-GetText and PHPMyAdmin' #16
    'Configuring MBString' #17
    'Restarting Nginx to apply changes' #20
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
        i=1
        # Uninstall previous conflicting software
        sudo apt remove nano htop screen ant mariadb-server mariadb-client nginx oracle-java8-installer php php-cgi php-common php-pear php-mbstring php-fpm php7.2-fpm php-mysql php-gettext phpmyadmin -y &>/dev/null
        sudo apt autoremove -y &>/dev/null
        sudo sed -i 's/#ClientAliveInterval 0/ClientAliveInterval 720/g' /etc/ssh/sshd_config
        sudo sed -i 's/#ClientAliveCountMax 3/ClientAliveCountMax 720/g' /etc/ssh/sshd_config
        sudo service ssh restart

        echo -e "XXX\n$i\n${phases[1]}\nXXX"
        i=2
        # Software installations
        sudo add-apt-repository ppa:webupd8team/java -y &>/dev/null
        sudo LC_ALL=C.UTF-8 add-apt-repository ppa:ondrej/php -y &>/dev/null

        echo -e "XXX\n$i\n${phases[2]}\nXXX"
        i=3
        sudo apt-get update -y &>/dev/null

        echo -e "XXX\n$i\n${phases[3]}\nXXX"
        i=4
        sudo apt-get install nano htop -y

        echo -e "XXX\n$i\n${phases[4]}\nXXX"
        i=5
        sudo apt-get install screen git -y

        echo -e "XXX\n$i\n${phases[5]}\nXXX"
        i=6
        sudo apt-get install mariadb-server mariadb-client -y

        echo -e "XXX\n$i\n${phases[6]}\nXXX"
        i=7
        sudo apt-get install nginx -y

        echo -e "XXX\n$i\n${phases[7]}\nXXX"
        i=8
        echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | sudo debconf-set-selections

        echo -e "XXX\n$i\n${phases[8]}\nXXX"
        i=10
        sudo apt-get install -y oracle-java8-installer ant

        echo -e "XXX\n$i\n${phases[9]}\nXXX"
        i=11
        # PHPMyAdmin installation
        sudo apt-get install php php-cgi  -y

        echo -e "XXX\n$i\n${phases[10]}\nXXX"
        i=13
        sudo apt-get install php-common php-pear php-mbstring  -y

        echo -e "XXX\n$i\n${phases[11]}\nXXX"
        i=15
        sudo apt-get install php-fpm php7.2-fpm php-mysql  -y

        echo -e "XXX\n$i\n${phases[12]}\nXXX"
        i=16
        debconf-set-selections <<< "phpmyadmin phpmyadmin/internal/skip-preseed boolean true"
        debconf-set-selections <<< "phpmyadmin phpmyadmin/reconfigure-webserver multiselect"
        debconf-set-selections <<< "phpmyadmin phpmyadmin/dbconfig-install boolean false"
        sudo apt-get install php-gettext phpmyadmin  -y
        sudo ln -s /usr/share/phpmyadmin /var/www/html

        echo -e "XXX\n$i\n${phases[13]}\nXXX"
        i=17
        sudo phpenmod mbstring
        sudo rm "/etc/nginx/sites-available/default"
        sudo cat "etc/nginx/simplified.conf" > "/etc/nginx/sites-available/default"
        echo -e "XXX\n$i\n${phases[14]}\nXXX"
        i=20
        sudo systemctl restart nginx

        echo -e "XXX\n$i\n${phases[15]}\nXXX"
        i=30
        sleep 1
        # Database configuration and imports
        sudo mysql -uroot -Bse "
            UPDATE mysql.user SET Password=PASSWORD('$pass') WHERE User='root';
            DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1');
            DELETE FROM mysql.user WHERE User='';
            DELETE FROM mysql.db WHERE Db='test' OR Db='test_%';
            FLUSH PRIVILEGES;"
        sudo mysql -uroot -Bse "CREATE USER 'openrsc'@'localhost' IDENTIFIED BY '$pass';GRANT ALL PRIVILEGES ON * . * TO 'openrsc'@'localhost';FLUSH PRIVILEGES;"
        sudo mysql -u"root" -p"$pass" < "Databases/openrsc_game.sql"
        sudo mysql -u"root" -p"$pass" < "Databases/openrsc_forum.sql"

        echo -e "XXX\n$i\n${phases[16]}\nXXX"
        i=40
        # Server configuration edits
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
        sudo sed -i 's/spawn_iron_man_npcs">false/spawn_iron_man_npcs">'$ironman'/g' server/free.conf
        sudo sed -i 's/spawn_iron_man_npcs">false/spawn_iron_man_npcs">'$ironman'/g' server/members.conf
        sudo sed -i 's/show_floating_nametags">false/show_floating_nametags">'$nametags'/g' server/free.conf
        sudo sed -i 's/show_floating_nametags">false/show_floating_nametags">'$nametags'/g' server/members.conf
        sudo sed -i 's/want_clans">false/want_clans">'$clans'/g' server/free.conf
        sudo sed -i 's/want_clans">false/want_clans">'$clans'/g' server/members.conf
        sudo sed -i 's/fog_toggle">false/fog_toggle">'$fog'/g' server/free.conf
        sudo sed -i 's/fog_toggle">false/fog_toggle">'$fog'/g' server/members.conf
        sudo sed -i 's/ground_item_toggle">false/ground_item_toggle">'$grounditems'/g' server/free.conf
        sudo sed -i 's/ground_item_toggle">false/ground_item_toggle">'$grounditems'/g' server/members.conf
        sudo sed -i 's/batch_progression">false/batch_progression">'$batchprogress'/g' server/free.conf
        sudo sed -i 's/batch_progression">false/batch_progression">'$batchprogress'/g' server/members.conf
        sudo sed -i 's/side_menu_toggle">false/side_menu_toggle">'$sidemenu'/g' server/free.conf
        sudo sed -i 's/side_menu_toggle">false/side_menu_toggle">'$sidemenu'/g' server/members.conf
        sudo sed -i 's/inventory_count_toggle">false/inventory_count_toggle">'$inventorycount'/g' server/free.conf
        sudo sed -i 's/inventory_count_toggle">false/inventory_count_toggle">'$inventorycount'/g' server/members.conf
        sudo sed -i 's/items_on_death_menu">false/items_on_death_menu">'$itemdeath'/g' server/free.conf
        sudo sed -i 's/items_on_death_menu">false/items_on_death_menu">'$itemdeath'/g' server/members.conf
        sudo sed -i 's/want_global_chat">false/want_global_chat">'$globalchat'/g' server/free.conf
        sudo sed -i 's/want_global_chat">false/want_global_chat">'$globalchat'/g' server/members.conf
        sudo sed -i 's/want_custom_banks">false/want_custom_banks">'$custombank'/g' server/free.conf
        sudo sed -i 's/want_custom_banks">false/want_custom_banks">'$custombank'/g' server/members.conf
        sudo sed -i 's/want_bank_pins">false/want_bank_pins">'$bankpins'/g' server/free.conf
        sudo sed -i 's/want_bank_pins">false/want_bank_pins">'$bankpins'/g' server/members.conf
        sudo sed -i 's/want_drop_x">false/want_drop_x">'$dropx'/g' server/free.conf
        sudo sed -i 's/want_drop_x">false/want_drop_x">'$dropx'/g' server/members.conf
        sudo sed -i 's/want_kill_feed">false/want_kill_feed">'$killfeed'/g' server/free.conf
        sudo sed -i 's/want_kill_feed">false/want_kill_feed">'$killfeed'/g' server/members.conf
        sudo sed -i 's/zoom_view_toggle">false/zoom_view_toggle">'$zoomview'/g' server/free.conf
        sudo sed -i 's/zoom_view_toggle">false/zoom_view_toggle">'$zoomview'/g' server/members.conf
        sudo sed -i 's/experience_counter_toggle">false/experience_counter_toggle">'$expcounter'/g' server/free.conf
        sudo sed -i 's/experience_counter_toggle">false/experience_counter_toggle">'$expcounter'/g' server/members.conf
        sudo sed -i 's/want_exp_info">false/want_exp_info">'$expinfo'/g' server/free.conf
        sudo sed -i 's/want_exp_info">false/want_exp_info">'$expinfo'/g' server/members.conf
        sudo sed -i 's/want_skill_menus">false/want_skill_menus">'$skillmenu'/g' server/free.conf
        sudo sed -i 's/want_skill_menus">false/want_skill_menus">'$skillmenu'/g' server/members.conf
        sudo sed -i 's/want_quest_menus">false/want_quest_menus">'$questmenu'/g' server/free.conf
        sudo sed -i 's/want_quest_menus">false/want_quest_menus">'$questmenu'/g' server/members.conf
        sudo sed -i 's/want_keyboard_shortcuts">false/want_keyboard_shortcuts">'$shortcuts'/g' server/free.conf
        sudo sed -i 's/want_keyboard_shortcuts">false/want_keyboard_shortcuts">'$shortcuts'/g' server/members.conf
        sudo sed -i 's/custom_firemaking">false/custom_firemaking">'$customfiremaking'/g' server/free.conf
        sudo sed -i 's/custom_firemaking">false/custom_firemaking">'$customfiremaking'/g' server/members.conf
        sudo sed -i 's/experience_drops_toggle">false/experience_drops_toggle">'$experiencedrops'/g' server/free.conf
        sudo sed -i 's/experience_drops_toggle">false/experience_drops_toggle">'$experiencedrops'/g' server/members.conf
        #Client configuration edits
        sudo sed -i 's/SERVER_NAME = "Open RSC"/SERVER_NAME = "'$gamename'"/g' client/src/rsc/Config.java
        sudo sed -i 's/SERVER_IP = "localhost"/SERVER_IP = "'$domain'"/g' client/src/rsc/Config.java
        sudo sed -i 's/C_EXPERIENCE_DROPS = false/C_EXPERIENCE_DROPS = "'$experiencedrops'"/g' client/src/rsc/Config.java
      	sudo sed -i 's/C_BATCH_PROGRESS_BAR = true/C_BATCH_PROGRESS_BAR = "'$batchprogress'"/g' client/src/rsc/Config.java
      	sudo sed -i 's/C_SHOW_FOG = true/C_SHOW_FOG = "'$fog'"/g' client/src/rsc/Config.java
      	#sudo sed -i 's/C_SHOW_GROUND_ITEMS = 0/C_SHOW_GROUND_ITEMS = 0/g' client/src/rsc/Config.java
      	#sudo sed -i 's/C_MESSAGE_TAB_SWITCH = true/C_MESSAGE_TAB_SWITCH = true/g' client/src/rsc/Config.java
      	sudo sed -i 's/C_NAME_CLAN_TAG_OVERLAY = true/C_NAME_CLAN_TAG_OVERLAY = "'$nametags'"/g' client/src/rsc/Config.java
      	sudo sed -i 's/C_SIDE_MENU_OVERLAY = false/C_SIDE_MENU_OVERLAY = "'$sidemenu'"/g' client/src/rsc/Config.java
      	sudo sed -i 's/C_KILL_FEED = false/C_KILL_FEED = "'$killfeed'"/g' client/src/rsc/Config.java
      	#sudo sed -i 's/C_FIGHT_MENU = 1/C_FIGHT_MENU = 1/g' client/src/rsc/Config.java
      	#sudo sed -i 's/C_ZOOM = 0/C_ZOOM = 0/g' client/src/rsc/Config.java
      	sudo sed -i 's/C_INV_COUNT = false/C_INV_COUNT = "'$inventorycount'"/g' client/src/rsc/Config.java
        sudo sed -i 's/C_EXPERIENCE_CONFIG_SUBMENU = true/C_EXPERIENCE_CONFIG_SUBMENU = "'$expinfo'"/g' client/src/rsc/Config.java
        sudo sed -i 's/S_SPAWN_AUCTION_NPCS = false/S_SPAWN_AUCTION_NPCS = "'$actionhouse'"/g' client/src/rsc/Config.java
      	sudo sed -i 's/S_SPAWN_IRON_MAN_NPCS = false/S_SPAWN_IRON_MAN_NPCS = "'$ironman'"/g' client/src/rsc/Config.java
        sudo sed -i 's/S_SHOW_FLOATING_NAMETAGS = false/S_SHOW_FLOATING_NAMETAGS = "'$nametags'"/g' client/src/rsc/Config.java
      	sudo sed -i 's/S_WANT_SKILL_MENUS = false/S_WANT_SKILL_MENUS = "'$skillmenu'"/g' client/src/rsc/Config.java
      	sudo sed -i 's/S_WANT_QUEST_MENUS = false/S_WANT_QUEST_MENUS = "'$questmenu'"/g' client/src/rsc/Config.java
        sudo sed -i 's/S_WANT_KEYBOARD_SHORTCUTS = false/S_WANT_KEYBOARD_SHORTCUTS = "'$shortcuts'"/g' client/src/rsc/Config.java
      	sudo sed -i 's/S_WANT_CUSTOM_BANKS = false/S_WANT_CUSTOM_BANKS = "'$custombank'"/g' client/src/rsc/Config.java
      	sudo sed -i 's/S_WANT_BANK_PINS = false/S_WANT_BANK_PINS = "'$bankpins'"/g' client/src/rsc/Config.java
      	sudo sed -i 's/S_CUSTOM_FIREMAKING = false/S_CUSTOM_FIREMAKING = "'$customfiremaking'"/g' client/src/rsc/Config.java
      	sudo sed -i 's/S_WANT_DROP_X = false/S_WANT_DROP_X = "'$dropx'"/g' client/src/rsc/Config.java
      	sudo sed -i 's/S_WANT_EXP_INFO = false/S_WANT_EXP_INFO = "'$expinfo'"/g' client/src/rsc/Config.java
        sudo sed -i 's/S_WANT_CLANS = false/S_WANT_CLANS = "'$clans'"/g' client/src/rsc/Config.java
      	sudo sed -i 's/S_WANT_KILL_FEED = false/S_WANT_KILL_FEED = "'$killfeed'"/g' client/src/rsc/Config.java
      	sudo sed -i 's/S_FOG_TOGGLE = false/S_FOG_TOGGLE = "'$fog'"/g' client/src/rsc/Config.java
      	sudo sed -i 's/S_GROUND_ITEM_TOGGLE = false/S_GROUND_ITEM_TOGGLE = "'$grounditems'"/g' client/src/rsc/Config.java
        sudo sed -i 's/S_BATCH_PROGRESSION = false/S_BATCH_PROGRESSION = "'$batchprogress'"/g' client/src/rsc/Config.java
      	sudo sed -i 's/S_SIDE_MENU_TOGGLE = false/S_SIDE_MENU_TOGGLE = "'$sidemenu'"/g' client/src/rsc/Config.java
      	sudo sed -i 's/S_INVENTORY_COUNT_TOGGLE = false/S_INVENTORY_COUNT_TOGGLE = "'$inventorycount'"/g' client/src/rsc/Config.java
      	sudo sed -i 's/S_ZOOM_VIEW_TOGGLE = false/S_ZOOM_VIEW_TOGGLE = "'$zoomview'"/g' client/src/rsc/Config.java
      	#sudo sed -i 's/S_MENU_COMBAT_STYLE_TOGGLE = false/S_MENU_COMBAT_STYLE_TOGGLE = "'$'"/g' client/src/rsc/Config.java
      	#sudo sed -i 's/S_FIGHTMODE_SELECTOR_TOGGLE = false/S_FIGHTMODE_SELECTOR_TOGGLE = "'$'"/g' client/src/rsc/Config.java
      	sudo sed -i 's/S_EXPERIENCE_COUNTER_TOGGLE = false/S_EXPERIENCE_COUNTER_TOGGLE = "'$expcounter'"/g' client/src/rsc/Config.java
      	sudo sed -i 's/S_EXPERIENCE_DROPS_TOGGLE = false/S_EXPERIENCE_DROPS_TOGGLE = "'$experiencedrops'"/g' client/src/rsc/Config.java
      	sudo sed -i 's/S_ITEMS_ON_DEATH_MENU = false/S_ITEMS_ON_DEATH_MENU = "'$itemdeath'"/g' client/src/rsc/Config.java
      	sudo sed -i 's/S_WANT_GLOBAL_CHAT = false/S_WANT_GLOBAL_CHAT = "'$globalchat'"/g' client/src/rsc/Config.java
        #Launcher
        sudo sed -i 's/frameTitle = "Open RSC"/frameTitle = ""'$gamename'""/g' Launcher/src/com/loader/openrsc/Constants.java
        sudo sed -i 's/localhost/"'$domain'"/g' Launcher/src/com/loader/openrsc/Constants.java
        sudo sed -i 's/43594/"'$port'"/g' Launcher/src/com/loader/openrsc/Constants.java

        echo -e "XXX\n$i\n${phases[17]}\nXXX"
        i=60
        # Create Website downloads folder
        sudo mkdir /var/www/html/downloads

        echo -e "XXX\n$i\n${phases[18]}\nXXX"
        i=70
        # Server
        sudo ant -f "server/build.xml" compile_core
        sudo ant -f "server/build.xml" compile_plugins

        echo -e "XXX\n$i\n${phases[19]}\nXXX"
        i=80
        # Client
        sudo ant -f "client/build.xml" compile
        yes | sudo cp -rf "client/Open_RSC_Client.jar" "/var/www/html/downloads"

        echo -e "XXX\n$i\n${phases[20]}\nXXX"
        i=90
        # Launcher
        sudo ant -f "Launcher/nbbuild.xml" jar
        yes | sudo cp -rf "Launcher/dist/Open_RSC_Launcher.jar" "/var/www/html/downloads/"

        echo -e "XXX\n$i\n${phases[21]}\nXXX"
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
    echo ""
    echo "The installation script has completed."
    echo ""
    echo "You should now be able to download the game launcher at: http://$domain/downloads/Open_RSC_Launcher.jar"
    echo ""
    echo "Launch the game server via: ./Linux_Simple_Run.sh"
    echo ""
    echo "Press enter to continue"
    echo ""
    read
    jumpto $deployment
fi

# Deployment ===================================================>
if [ "$install" == "3" ]; then
  jumpto $deployment
fi

# Exit ===================================================>
if [ "$install" == "4" ]; then
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

  echo "You have picked ${GREEN}backup all databases!${NC}"
  echo ""
  sudo make backup
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

# Run ===================================================>
run:
clear

# Backs up all databases
echo "Performing database backup"
echo ""
make backup
echo ""

# Run the game server in a detached screen
echo ""
echo "Launching the game server in a new screen."
echo ""
echo "Type 'screen -r' to access the game server screen."
echo "Use CTRL + A + D to detach the live server screen so it runs in the background."
echo ""
echo ""
touch gameserver.log && chmod 777 gameserver.log &>/dev/null
cd server
screen -dmS name ./ant_launcher.sh
exit
# Run <===================================================
