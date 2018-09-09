#!/bin/bash
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0` # No Color


echo ""
echo "${RED}Open RSC Configuration:${NC}
An easy to use RSC private server framework.

Note: the player must exist and be offline for changes to take effect.

Choices:
  ${RED}1${NC} - Make a player an administrator in-game
  ${RED}2${NC} - Make a player a moderator in-game
  ${RED}3${NC} - Make a player a standard player in-game
  ${RED}4${NC} - Ban a player in-game
  ${RED}5${NC} - Return to the main menu"
echo ""
echo "Which of the above do you wish to do? Type the choice number and press enter."
echo ""
read rank

if [ "$rank" == "5" ]; then
    make go
else

    player=$(whiptail --inputbox "Please enter the player name." 8 50 --title "Open RSC Configuration" 3>&1 1>&2 2>&3)

    installmode=$(whiptail --title "Which install mode are you using?" --radiolist "" 8 60 2 \
        "docker" "Docker installation" ON \
        "direct" "Direct installation" OFF 3>&1 1>&2 2>&3)

    pass=$(whiptail --inputbox "Please enter the MySQL password." 8 50 --title "Open RSC Configuration" 3>&1 1>&2 2>&3)

    if [ "$installmode" == "direct" ]; then
        if [ "$rank" == "3" ]; then
            sudo mysql -uopenrsc -p$pass -Bse "UPDATE openrsc_game.openrsc_players SET group_id = '4' WHERE username = '$player';"
        elif [ "$rank" == "1" ]; then
            sudo mysql -uopenrsc -p$pass -Bse "UPDATE openrsc_game.openrsc_players SET group_id = '1' WHERE username = '$player';"
        elif [ "$rank" == "2" ]; then
            sudo mysql -uopenrsc -p$pass -Bse "UPDATE openrsc_game.openrsc_players SET group_id = '2' WHERE username = '$player';"
        elif [ "$rank" == "4" ]; then
            sudo mysql -uopenrsc -p$pass -Bse "UPDATE openrsc_game.openrsc_players SET banned = '-1' WHERE username = '$player';"
        fi

    elif [ "$installmode" == "docker" ]; then
        if [ "$rank" == "3" ]; then
            docker exec -i mysql mysql -uopenrsc -p$pass -Bse "UPDATE openrsc_game.openrsc_players SET group_id = '4' WHERE username = '$player';"
        elif [ "$rank" == "1" ]; then
            docker exec -i mysql mysql -uopenrsc -p$pass -Bse "UPDATE openrsc_game.openrsc_players SET group_id = '1' WHERE username = '$player';"
        elif [ "$rank" == "2" ]; then
            docker exec -i mysql mysql -uopenrsc -p$pass -Bse "UPDATE openrsc_game.openrsc_players SET group_id = '2' WHERE username = '$player';"
        elif [ "$rank" == "4" ]; then
            docker exec -i mysql mysql -uopenrsc -p$pass -Bse "UPDATE openrsc_game.openrsc_players SET banned = '-1' WHERE username = '$player';"
        fi
    fi
fi
