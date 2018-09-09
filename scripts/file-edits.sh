#!/bin/bash
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0` # No Color


if (whiptail --title "Open RSC Configuration" --yesno "Do you wish to configure Open RSC?" 7 70) then
    configure=true
else
    make go
fi


if [ "$configure" == "true" ]; then
    export pass=$(whiptail --passwordbox "Please enter your desired MySQL password." 8 50 $pass --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    echo "$pass" > .pass
    export domain=$(whiptail --inputbox "Please enter your server's domain name. (No http:// or www. needed)" 8 50 $domain --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    echo "$domain" > .domain
    export subdomain=$(whiptail --inputbox "Please set your server's private subdomain if one exists or press enter." 8 50 $domain --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    echo "$subdomain" > .subdomain
    export port=$(whiptail --inputbox "What port should the game use?" 8 50 43594 --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    echo "$port" > .port
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
    sudo sed -i 's/mysql_pass">root/mysql_pass">'$pass'/g' server/free.conf
    sudo sed -i 's/mysql_pass">root/mysql_pass">'$pass'/g' server/members.conf
    sudo sed -i 's/game_tick">620/game_tick">'$tick'/g' server/free.conf
    sudo sed -i 's/game_tick">620/game_tick">'$tick'/g' server/members.conf
    sudo sed -i 's/43594/'$port'/g' server/free.conf
    sudo sed -i 's/43594/'$port'/g' server/members.conf
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
    sudo sed -i 's/SERVER_IP = "localhost"/SERVER_IP = '$subdomain'/g' client/src/rsc/Config.java
    sudo sed -i 's/43594/'$port'/g' client/src/rsc/Config.java
    sudo sed -i 's/C_EXPERIENCE_DROPS = false/C_EXPERIENCE_DROPS = '$experiencedrops'/g' client/src/rsc/Config.java
    sudo sed -i 's/C_BATCH_PROGRESS_BAR = true/C_BATCH_PROGRESS_BAR = '$batchprogress'/g' client/src/rsc/Config.java
    sudo sed -i 's/C_SHOW_FOG = true/C_SHOW_FOG = '$fog'/g' client/src/rsc/Config.java
    #sudo sed -i 's/C_SHOW_GROUND_ITEMS = 0/C_SHOW_GROUND_ITEMS = 0/g' client/src/rsc/Config.java
    #sudo sed -i 's/C_MESSAGE_TAB_SWITCH = true/C_MESSAGE_TAB_SWITCH = true/g' client/src/rsc/Config.java
    sudo sed -i 's/C_NAME_CLAN_TAG_OVERLAY = true/C_NAME_CLAN_TAG_OVERLAY = '$nametags'/g' client/src/rsc/Config.java
    sudo sed -i 's/C_SIDE_MENU_OVERLAY = false/C_SIDE_MENU_OVERLAY = '$sidemenu'/g' client/src/rsc/Config.java
    sudo sed -i 's/C_KILL_FEED = false/C_KILL_FEED = '$killfeed'/g' client/src/rsc/Config.java
    #sudo sed -i 's/C_FIGHT_MENU = 1/C_FIGHT_MENU = 1/g' client/src/rsc/Config.java
    #sudo sed -i 's/C_ZOOM = 0/C_ZOOM = 0/g' client/src/rsc/Config.java
    sudo sed -i 's/C_INV_COUNT = false/C_INV_COUNT = '$inventorycount'/g' client/src/rsc/Config.java
    sudo sed -i 's/C_EXPERIENCE_CONFIG_SUBMENU = true/C_EXPERIENCE_CONFIG_SUBMENU = '$expinfo'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_SPAWN_AUCTION_NPCS = false/S_SPAWN_AUCTION_NPCS = '$auctionhouse'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_SPAWN_IRON_MAN_NPCS = false/S_SPAWN_IRON_MAN_NPCS = '$ironman'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_SHOW_FLOATING_NAMETAGS = false/S_SHOW_FLOATING_NAMETAGS = '$nametags'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_WANT_SKILL_MENUS = false/S_WANT_SKILL_MENUS = '$skillmenu'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_WANT_QUEST_MENUS = false/S_WANT_QUEST_MENUS = '$questmenu'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_WANT_KEYBOARD_SHORTCUTS = false/S_WANT_KEYBOARD_SHORTCUTS = '$shortcuts'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_WANT_CUSTOM_BANKS = false/S_WANT_CUSTOM_BANKS = '$custombank'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_WANT_BANK_PINS = false/S_WANT_BANK_PINS = '$bankpins'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_CUSTOM_FIREMAKING = false/S_CUSTOM_FIREMAKING = '$customfiremaking'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_WANT_DROP_X = false/S_WANT_DROP_X = '$dropx'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_WANT_EXP_INFO = false/S_WANT_EXP_INFO = '$expinfo'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_WANT_CLANS = false/S_WANT_CLANS = '$clans'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_WANT_KILL_FEED = false/S_WANT_KILL_FEED = '$killfeed'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_FOG_TOGGLE = false/S_FOG_TOGGLE = '$fog'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_GROUND_ITEM_TOGGLE = false/S_GROUND_ITEM_TOGGLE = '$grounditems'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_BATCH_PROGRESSION = false/S_BATCH_PROGRESSION = '$batchprogress'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_SIDE_MENU_TOGGLE = false/S_SIDE_MENU_TOGGLE = '$sidemenu'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_INVENTORY_COUNT_TOGGLE = false/S_INVENTORY_COUNT_TOGGLE = '$inventorycount'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_ZOOM_VIEW_TOGGLE = false/S_ZOOM_VIEW_TOGGLE = '$zoomview'/g' client/src/rsc/Config.java
    #sudo sed -i 's/S_MENU_COMBAT_STYLE_TOGGLE = false/S_MENU_COMBAT_STYLE_TOGGLE = '$'/g' client/src/rsc/Config.java
    #sudo sed -i 's/S_FIGHTMODE_SELECTOR_TOGGLE = false/S_FIGHTMODE_SELECTOR_TOGGLE = '$'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_EXPERIENCE_COUNTER_TOGGLE = false/S_EXPERIENCE_COUNTER_TOGGLE = '$expcounter'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_EXPERIENCE_DROPS_TOGGLE = false/S_EXPERIENCE_DROPS_TOGGLE = '$experiencedrops'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_ITEMS_ON_DEATH_MENU = false/S_ITEMS_ON_DEATH_MENU = '$itemdeath'/g' client/src/rsc/Config.java
    sudo sed -i 's/S_WANT_GLOBAL_CHAT = false/S_WANT_GLOBAL_CHAT = '$globalchat'/g' client/src/rsc/Config.java

    #Launcher configuration edits
    sudo sed -i 's/frameTitle = "Open RSC"/frameTitle = "'$gamename'"/g' Launcher/src/com/loader/openrsc/Constants.java
    sudo sed -i 's/localhost/'$domain'/g' Launcher/src/com/loader/openrsc/Constants.java
    sudo sed -i 's/43594/'$port'/g' Launcher/src/com/loader/openrsc/Constants.java

    if [ "$installmode" == "direct" ]; then
        # Database configuration
        export dbuser=root
        echo "$dbuser" > .dbuser
        sudo mysql -u$dbuser -proot -Bse "CREATE USER 'openrsc'@'localhost' IDENTIFIED BY '$pass';GRANT ALL PRIVILEGES ON * . * TO 'openrsc'@'localhost';FLUSH PRIVILEGES;"
        export dbuser=openrsc
        echo "$dbuser" > .dbuser
        sudo mysql -u$dbuser -p$pass -Bse "
            UPDATE mysql.user SET Password=PASSWORD('$pass') WHERE User='root';
            DELETE FROM mysql.user WHERE User='root' AND Host NOT IN ('localhost', '127.0.0.1', '::1');
            DELETE FROM mysql.user WHERE User='';
            DELETE FROM mysql.db WHERE Db='test' OR Db='test_%';
            FLUSH PRIVILEGES;"

        # Website
        sudo sed -i "s/dbuser = 'root'/dbuser = 'openrsc'/g" /var/www/html/board/config.php
        sudo sed -i "s/dbpasswd = 'root'/dbpasswd = '$pass'/g" /var/www/html/board/config.php
        sudo sed -i "s/localhost/$subdomain/g" /var/www/html/header.php
        sudo sed -i "s/localhost/$subdomain/g" /var/www/html/header5.php
        sudo sed -i "s/43594/$port/g" /var/www/html/header.php
        sudo sed -i "s/43594/$port/g" /var/www/html/header5.php

    elif [ "$installmode" == "docker" ]; then
        # Database configuration
        sudo chmod 644 etc/mariadb/innodb.cnf
        export dbuser=root
        echo "$dbuser" > .dbuser
        docker exec -i mysql mysql -u$dbuser -proot -Bse "CREATE USER 'openrsc'@'%' IDENTIFIED BY '$pass';GRANT ALL PRIVILEGES ON * . * TO 'openrsc'@'%';FLUSH PRIVILEGES;"
        export dbuser=openrsc
        echo "$dbuser" > .dbuser
        docker exec -i mysql mysql -u$dbuser -p$pass -Bse "
            UPDATE mysql.user SET Password=PASSWORD('$pass') WHERE User='root';
            UPDATE mysql.user SET Password=PASSWORD('$pass') WHERE User='user';
            DELETE FROM mysql.user WHERE User='';
            DELETE FROM mysql.db WHERE Db='test' OR Db='test_%';
            FLUSH PRIVILEGES;"

        # .env
        sudo sed -i 's/localhost/'$domain'/g' .env
        sudo sed -i 's/MARIADB_ROOT_USER=root/MARIADB_ROOT_USER='openrsc'/g' .env
        sudo sed -i 's/MARIADB_ROOT_PASSWORD=root/MARIADB_ROOT_PASSWORD='$pass'/g' .env
        sudo make stop && sudo make start

        # Website
        sudo sed -i "s/dbuser = 'root'/dbuser = 'openrsc'/g" Website/board/config.php
        sudo sed -i "s/dbpasswd = 'root'/dbpasswd = '$pass'/g" Website/board/config.php
        sudo sed -i "s/localhost/$subdomain/g" Website/header.php
        sudo sed -i "s/localhost/$subdomain/g" Website/header5.php
        sudo sed -i "s/43594/$port/g" Website/header.php
        sudo sed -i "s/43594/$port/g" Website/header5.php

        export email=$(whiptail --inputbox "Please enter your email address for Lets Encrypt HTTPS registration." 8 50 $email --title "Open RSC HTTPS Configuration" 3>&1 1>&2 2>&3)
        echo "$email" > .email
        make certbot
    fi

    make get-updates
fi
