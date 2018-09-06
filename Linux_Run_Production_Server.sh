#!/bin/bash

# Backs up all databases
./Linux_Backup_Databases.sh

# Run the game server in a detached screen
clear
echo "Launching the game server in a new screen."
echo ""
echo "Type 'screen -r' to access the game server screen."
echo "Use CTRL + A + D to detach the live server screen so it runs in the background."
echo ""
echo ""
touch gameserver.log && chmod 777 gameserver.log &>/dev/null
cd Game/server
screen -dmS name ./ant_launcher.sh
