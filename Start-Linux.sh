#!/bin/bash
exec 0</dev/tty
RED=$(tput setaf 1)
NC=$(tput sgr0) # No Color

function pause() {
    read -r -s -n 1 -p "Press any key to continue . . ."
    echo ""
}

function start() {
    clear
    echo ""
    echo "Open RuneScape Classic: Striving for a replica RSC game and more

What would you like to do?

Choices:
  ${RED}1${NC} - Compile and start the game
  ${RED}2${NC} - Start the game (faster if already compiled)
  ${RED}3${NC} - Change a player's in-game role
  ${RED}4${NC} - Change a player's name
  ${RED}5${NC} - Backup database
  ${RED}6${NC} - Restore database
  ${RED}7${NC} - Perform a fresh install
  ${RED}8${NC} - Exit"
    echo ""
    echo "Type the choice number and press enter."
    echo ""
    read -r action
    clear

    if [ "$action" == "1" ]; then # Compile and start the game
        echo "Starting Open RuneScape Classic."
        make compile
        pause
        echo "What would you like to do?

Choices:
    ${RED}1${NC} - Start single player edition (launch client and server)
    ${RED}2${NC} - Start the server only (public hosting)"
        read -r start
        if [ "$start" == "1" ]; then
            make run-server && sleep 5 && make run-client
        elif [ "$start" == "2" ]; then
            make run-server
        fi
        clear
        pause
        start
    elif [ "$action" == "2" ]; then # Start the game (faster if already compiled)
        echo "What would you like to do?

Choices:
    ${RED}1${NC} - Start single player edition (launch client and server)
    ${RED}2${NC} - Start the server only (public hosting)"
        read -r start
        if [ "$start" == "1" ]; then
            make run-server && sleep 5 && make run-client
        elif [ "$start" == "2" ]; then
            make run-server
        fi
        echo ""
        pause
        start
    elif [ "$action" == "3" ]; then # Change a player's in-game role
        echo "Make sure you are logged out first!"
        echo "Type the username of the player you wish to set and press enter."
        echo ""
        read -r username
        clear
        echo "What role should the player be set to?

Choices:
    ${RED}1${NC} - Admin
    ${RED}2${NC} - Mod
    ${RED}10${NC} - Player (default)"
        read -r group
        echo "Type the name of the database where the player is saved."
        echo ""
        echo "(preservation, openrsc, cabbage, uranium, coleslaw, 2001scape, or openpk)"
        echo ""
        echo "The default player database is named preservation."
        echo ""
        echo ""
        read -r db
        clear
        echo "Which database are you using?

Choices:
    ${RED}1${NC} - SQLite (default)
    ${RED}2${NC} - MariaDB (production game hosting)"
        read -r sql
        if [ "$sql" == "1" ]; then
            make rank-sqlite db=$db group=$group username=$username
        elif [ "$sql" == "2" ]; then
            make rank-mariadb db=$db group=$group username=$username
        fi
        clear
        echo "$username has been made group role $group in database $db!"
        pause
        start
    elif [ "$action" == "4" ]; then # Change a player's name
        echo "Make sure you are logged out first!"
        echo "What existing player should have their name changed?"
        echo ""
        read -r oldname
        echo ""
        echo "What would you like to change $oldname's name to?"
        echo ""
        read -r newname
        clear
        echo "Type the name of the database where the player is saved."
        echo ""
        echo "(preservation, openrsc, cabbage, uranium, coleslaw, 2001scape, or openpk)"
        echo ""
        echo "The default player database is named preservation."
        echo ""
        echo ""
        read -r db
        clear
        echo "Which database are you using?

Choices:
    ${RED}1${NC} - SQLite (default)
    ${RED}2${NC} - MariaDB (production game hosting)"
        read -r sql
        if [ "$sql" == "1" ]; then
            make namechange-sqlite db=$db oldname=$oldname newname=$newname
        elif [ "$sql" == "2" ]; then
            make namechange-mariadb db=$db oldname=$oldname newname=$newname
        fi
        clear
        echo "$oldname has been renamed to $newname!"
        pause
        start
    elif [ "$action" == "5" ]; then # Backup database
        echo "Type the name of the database that you wish to backup."
        echo ""
        echo "(preservation, openrsc, cabbage, uranium, coleslaw, 2001scape, or openpk)"
        echo ""
        echo "The default player database is named preservation."
        echo ""
        read -r db
        clear
        echo "Which database are you using?

Choices:
    ${RED}1${NC} - SQLite (default)
    ${RED}2${NC} - MariaDB (production game hosting)"
        read -r sql
        if [ "$sql" == "1" ]; then
            make backup-sqlite db=$db
        elif [ "$sql" == "2" ]; then
            make backup-mariadb db=$db
        fi
        clear
        echo "Database $db backup complete."
        pause
        start
    elif [ "$action" == "6" ]; then # Restore database
        echo "==========================================================================="
        ls Backups
        echo "==========================================================================="
        echo ""
        echo "Type the filename of the backup file listed above that you wish to restore."
        echo "(Copy and paste it exactly, including the .zip file extension)"
        echo ""
        read -r filename
        clear
        echo "Which database should this be restored to? (preservation, openrsc, cabbage, uranium, coleslaw, 2001scape, or openpk)"
        echo ""
        read -r db
        clear
        echo "Which database are you using?

Choices:
    ${RED}1${NC} - SQLite (default)
    ${RED}2${NC} - MariaDB (production game hosting)"
        read -r sql
        if [ "$sql" == "1" ]; then
            make restore-sqlite name=$filename db=$db
        elif [ "$sql" == "2" ]; then
            make restore-mariadb name=$filename db=$db
        fi
        clear
        echo "File $filename was restored to database $db."
        echo ""
        pause
        start
    elif [ "$action" == "7" ]; then # Perform a fresh install
        clear
        echo "Are you ABSOLUTELY SURE that you want to perform a fresh install and reset any existing game databases?"
        echo ""
        echo "To confirm the database reset, type yes and press enter."
        echo ""
        read -r confirmwipe
        echo ""
        if [ "$confirmwipe" == "yes" ]; then
            clear
            echo "Which database are you using?

Choices:
    ${RED}1${NC} - SQLite (default)
    ${RED}2${NC} - MariaDB (production game hosting)"
            read -r sql
            if [ "$sql" == "1" ]; then
                make import-authentic-sqlite db=preservation
                make import-authentic-sqlite db=openrsc
                make import-authentic-sqlite db=uranium
                make import-custom-sqlite db=cabbage
                make import-custom-sqlite db=coleslaw
                make import-custom-sqlite db=openpk
                make import-retro-sqlite db=2001scape
            elif [ "$sql" == "2" ]; then
                make import-authentic-mariadb db=preservation
                make import-authentic-mariadb db=openrsc
                make import-authentic-mariadb db=uranium
                make import-custom-mariadb db=cabbage
                make import-custom-mariadb db=coleslaw
                make import-custom-mariadb db=openpk
                make import-retro-mariadb db=2001scape
            fi
        else
            echo "Error! $confirmwipe is not a valid option."
            pause
            start
        fi
    elif [ "$action" == "8" ]; then # Exits
        exit
    fi
}

while true; do
    start
done
