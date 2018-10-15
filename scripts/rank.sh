#!/bin/bash
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0` # No Color

echo ""
echo "${RED}Open RSC Configuration:${NC}
An easy to use RSC private server framework.

Note: the player must be offline for changes to take effect.

Choices:
  ${RED}1${NC} - Make a player an administrator
  ${RED}2${NC} - Make a player a moderator
  ${RED}3${NC} - Make a player a standard player
  ${RED}4${NC} - Ban a player
  ${RED}4${NC} - Unban a player
  ${RED}5${NC} - Return to the main menu"
echo ""
echo "Which of the above do you wish to do? Type the choice number and press enter."
echo ""
read rank

if [ "$rank" == "5" ]; then
    make go
else

    player=$(whiptail --inputbox "Please enter the player name." 8 50 --title "Open RSC Configuration" 3>&1 1>&2 2>&3)

    export user=$(whiptail --passwordbox "Please enter your MySQL username." 8 50 ${user} --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
    export pass=$(whiptail --passwordbox "Please enter your MySQL password." 8 50 ${pass} --title "Open RSC Configuration" 3>&1 1>&2 2>&3)

    # Docker or native install mode?
    echo ""
    echo "${RED}Open RSC:${NC}
    An easy to use RSC private server framework.

    Which method of installation do you wish to use?

    Choices:
      ${RED}1${NC} - Use Docker virtual containers (recommended)
      ${RED}2${NC} - Direct installation (Ubuntu Linux only)
      ${RED}3${NC} - Return to main menu"
    echo ""
    echo "Which of the above do you wish to do? Type the choice number and press enter."
    read installmode

    if [ "$installmode" == "direct" ]; then
        if [ "$rank" == "1" ]; then
            sudo mysql -u${user} -p${pass} -Bse "UPDATE openrsc_game.openrsc_players SET group_id = '1' WHERE username = '$player';"
        elif [ "$rank" == "2" ]; then
            sudo mysql -u${user} -p${pass} -Bse "UPDATE openrsc_game.openrsc_players SET group_id = '2' WHERE username = '$player';"
        elif [ "$rank" == "3" ]; then
            sudo mysql -u${user} -p${pass} -Bse "UPDATE openrsc_game.openrsc_players SET group_id = '4' WHERE username = '$player';"
        elif [ "$rank" == "4" ]; then
            sudo mysql -u${user} -p${pass} -Bse "UPDATE openrsc_game.openrsc_players SET banned = '-1' WHERE username = '$player';"
        elif [ "$rank" == "5" ]; then
            sudo mysql -u${user} -p${pass} -Bse "UPDATE openrsc_game.openrsc_players SET banned = '0' WHERE username = '$player';"
        fi

    elif [ "$installmode" == "docker" ]; then
        if [ "$rank" == "1" ]; then
            docker exec -i mysql mysql -u${user} -p${pass} -Bse "UPDATE openrsc_game.openrsc_players SET group_id = '1' WHERE username = '$player';"
        elif [ "$rank" == "2" ]; then
            docker exec -i mysql mysql -u${user} -p${pass} -Bse "UPDATE openrsc_game.openrsc_players SET group_id = '2' WHERE username = '$player';"
        elif [ "$rank" == "3" ]; then
            docker exec -i mysql mysql -u${user} -p${pass} -Bse "UPDATE openrsc_game.openrsc_players SET group_id = '4' WHERE username = '$player';"
        elif [ "$rank" == "4" ]; then
            docker exec -i mysql mysql -u${user} -p${pass} -Bse "UPDATE openrsc_game.openrsc_players SET banned = '-1' WHERE username = '$player';"
        elif [ "$rank" == "5" ]; then
            docker exec -i mysql mysql -u${user} -p${pass} -Bse "UPDATE openrsc_game.openrsc_players SET banned = '0' WHERE username = '$player';"
        fi
    fi

    echo "Done!"
    make go
fi
