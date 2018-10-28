#!/bin/bash
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0` # No Color

    export pass=$(whiptail --passwordbox "Please enter your desired MySQL password." 8 50 $pass --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    export domain=$(whiptail --inputbox "Please enter your server's domain name. (No http:// or www. needed)" 8 50 $domain --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    export subdomain=$(whiptail --inputbox "Please set your server's private subdomain if one exists or press enter." 8 50 $domain --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    export port=$(whiptail --inputbox "What port should the game use?" 8 50 43594 --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    export tick=$(whiptail --inputbox "What speed should the game run? (600 is the default and 300 is twice as fast)" 8 50 600 --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    tick=$(whiptail --inputbox "What speed should the game run? (600 is the default and 300 is twice as fast)" 8 50 600 --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
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
    batchprogress=$(whiptail --title "Open RSC Configuration" --radiolist "" 8 60 2 \
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
    sudo sed -i 's/combat_exp_rate">1/combat_exp_rate">'${combatrate}'/g' server/free.conf
    sudo sed -i 's/combat_exp_rate">1/combat_exp_rate">'${combatrate}'/g' server/members.conf
    sudo sed -i 's/skilling_exp_rate">1/skilling_exp_rate">'${skillrate}'/g' server/free.conf
    sudo sed -i 's/skilling_exp_rate">1/skilling_exp_rate">'${skillrate}'/g' server/members.conf
    sudo sed -i 's/bank_size">192/bank_size">'${banksize}'/g' server/free.conf
    sudo sed -i 's/bank_size">192/bank_size">'${banksize}'/g' server/members.conf
    sudo sed -i 's/spawn_auction_npcs">false/spawn_auction_npcs">'${auctionhouse}'/g' server/free.conf
    sudo sed -i 's/spawn_auction_npcs">false/spawn_auction_npcs">'${auctionhouse}'/g' server/members.conf
    sudo sed -i 's/spawn_iron_man_npcs">false/spawn_iron_man_npcs">'${ironman}'/g' server/free.conf
    sudo sed -i 's/spawn_iron_man_npcs">false/spawn_iron_man_npcs">'${ironman}'/g' server/members.conf
    sudo sed -i 's/show_floating_nametags">false/show_floating_nametags">'${nametags}'/g' server/free.conf
    sudo sed -i 's/show_floating_nametags">false/show_floating_nametags">'${nametags}'/g' server/members.conf
    sudo sed -i 's/want_clans">false/want_clans">'${clans}'/g' server/free.conf
    sudo sed -i 's/want_clans">false/want_clans">'${clans}'/g' server/members.conf
    sudo sed -i 's/fog_toggle">false/fog_toggle">'${fog}'/g' server/free.conf
    sudo sed -i 's/fog_toggle">false/fog_toggle">'${fog}'/g' server/members.conf
    sudo sed -i 's/ground_item_toggle">false/ground_item_toggle">'${grounditems}'/g' server/free.conf
    sudo sed -i 's/ground_item_toggle">false/ground_item_toggle">'${grounditems}'/g' server/members.conf
    sudo sed -i 's/batch_progression">false/batch_progression">'${batchprogress}'/g' server/free.conf
    sudo sed -i 's/batch_progression">false/batch_progression">'${batchprogress}'/g' server/members.conf
    sudo sed -i 's/side_menu_toggle">false/side_menu_toggle">'${sidemenu}'/g' server/free.conf
    sudo sed -i 's/side_menu_toggle">false/side_menu_toggle">'${sidemenu}'/g' server/members.conf
    sudo sed -i 's/inventory_count_toggle">false/inventory_count_toggle">'${inventorycount}'/g' server/free.conf
    sudo sed -i 's/inventory_count_toggle">false/inventory_count_toggle">'${inventorycount}'/g' server/members.conf
    sudo sed -i 's/items_on_death_menu">false/items_on_death_menu">'${itemdeath}'/g' server/free.conf
    sudo sed -i 's/items_on_death_menu">false/items_on_death_menu">'${itemdeath}'/g' server/members.conf
    sudo sed -i 's/want_global_chat">false/want_global_chat">'${globalchat}'/g' server/free.conf
    sudo sed -i 's/want_global_chat">false/want_global_chat">'${globalchat}'/g' server/members.conf
    sudo sed -i 's/want_custom_banks">false/want_custom_banks">'${custombank}'/g' server/free.conf
    sudo sed -i 's/want_custom_banks">false/want_custom_banks">'${custombank}'/g' server/members.conf
    sudo sed -i 's/want_bank_pins">false/want_bank_pins">'${bankpins}'/g' server/free.conf
    sudo sed -i 's/want_bank_pins">false/want_bank_pins">'${bankpins}'/g' server/members.conf
    sudo sed -i 's/want_drop_x">false/want_drop_x">'${dropx}'/g' server/free.conf
    sudo sed -i 's/want_drop_x">false/want_drop_x">'${dropx}'/g' server/members.conf
    sudo sed -i 's/want_kill_feed">false/want_kill_feed">'${killfeed}'/g' server/free.conf
    sudo sed -i 's/want_kill_feed">false/want_kill_feed">'${killfeed}'/g' server/members.conf
    sudo sed -i 's/zoom_view_toggle">false/zoom_view_toggle">'${zoomview}'/g' server/free.conf
    sudo sed -i 's/zoom_view_toggle">false/zoom_view_toggle">'${zoomview}'/g' server/members.conf
    sudo sed -i 's/experience_counter_toggle">false/experience_counter_toggle">'${expcounter}'/g' server/free.conf
    sudo sed -i 's/experience_counter_toggle">false/experience_counter_toggle">'${expcounter}'/g' server/members.conf
    sudo sed -i 's/want_exp_info">false/want_exp_info">'${expinfo}'/g' server/free.conf
    sudo sed -i 's/want_exp_info">false/want_exp_info">'${expinfo}'/g' server/members.conf
    sudo sed -i 's/want_skill_menus">false/want_skill_menus">'${skillmenu}'/g' server/free.conf
    sudo sed -i 's/want_skill_menus">false/want_skill_menus">'${skillmenu}'/g' server/members.conf
    sudo sed -i 's/want_quest_menus">false/want_quest_menus">'${questmenu}'/g' server/free.conf
    sudo sed -i 's/want_quest_menus">false/want_quest_menus">'${questmenu}'/g' server/members.conf
    sudo sed -i 's/want_keyboard_shortcuts">false/want_keyboard_shortcuts">'${shortcuts}'/g' server/free.conf
    sudo sed -i 's/want_keyboard_shortcuts">false/want_keyboard_shortcuts">'${shortcuts}'/g' server/members.conf
    sudo sed -i 's/custom_firemaking">false/custom_firemaking">'${customfiremaking}'/g' server/free.conf
    sudo sed -i 's/custom_firemaking">false/custom_firemaking">'${customfiremaking}'/g' server/members.conf
    sudo sed -i 's/experience_drops_toggle">false/experience_drops_toggle">'${experiencedrops}'/g' server/free.conf
    sudo sed -i 's/experience_drops_toggle">false/experience_drops_toggle">'${experiencedrops}'/g' server/members.conf

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
      ${RED}1${NC} - Docker virtual containers
      ${RED}2${NC} - Direct installation (Ubuntu Linux only)
      ${RED}3${NC} - Return to main menu"
    echo ""
    echo "Which of the above do you wish to do? Type the choice number and press enter."
    read installmode

    if [ "$installmode" == "2" ]; then
        # Database configuration
        sudo mysql -uroot -proot -Bse "CREATE USER 'openrsc'@'localhost' IDENTIFIED BY '$pass';GRANT ALL PRIVILEGES ON * . * TO 'openrsc'@'localhost';FLUSH PRIVILEGES;"
        export dbuser=openrsc
        sudo mysql -uopenrsc -p${pass} -Bse "
            DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1');
            FLUSH PRIVILEGES;"

        # Website
        sudo sed -i 's/dbuser = '\'root\''/dbroot = '\'openrsc\''/g' /var/www/html/elite/board/config.php
        sudo sed -i 's/dbpasswd = '\'root\''/dbpasswd = '\'${pass}\''/g' /var/www/html/elite/board/config.php
        sudo sed -i 's/game.opeenrsc.com/'${subdomain}'/g' /var/www/html/elite/index.php
        sudo sed -i 's/43594/'${port}'/g' /var/www/html/elite/index.php

        sudo make certbot-native


    elif [ "$installmode" == "1" ]; then
        # Database configuration
        sudo chmod 644 etc/mariadb/innodb.cnf

        export dbuser=root
        sudo docker exec -i mysql mysql -uroot -proot -Bse "CREATE USER 'openrsc'@'%' IDENTIFIED BY '$pass';GRANT ALL PRIVILEGES ON * . * TO 'openrsc'@'%';FLUSH PRIVILEGES;"

        export dbuser=openrsc
        sudo docker exec -i mysql mysql -uopenrsc -p${pass} -Bse "
            DELETE FROM mysql.user WHERE User='root';
            FLUSH PRIVILEGES;"

        # .env
        sudo sed -i 's/localhost/'${domain}'/g' .env
        sudo sed -i 's/MARIADB_ROOT_USER=root/MARIADB_ROOT_USER='openrsc'/g' .env
        sudo sed -i 's/MARIADB_ROOT_PASSWORD=root/MARIADB_ROOT_PASSWORD='${pass}'/g' .env
        sudo make stop && sudo make start

        # Website
        sudo sed -i 's/dbuser = '\'root\''/dbroot = '\'openrsc\''/g' Website/elite/board/config.php
        sudo sed -i 's/dbpasswd = '\'root\''/dbpasswd = '\'${pass}\''/g' Website/elite/board/config.php
        sudo sed -i 's/game.openrsc.com/'${subdomain}'/g' Website/elite/index.php
        sudo sed -i 's/43594/'${port}'/g"' Website/elite/index.php

        sudo make certbot-docker


    elif [ "$installmode" == "3" ]; then
        make go
    fi

    make get-updates
