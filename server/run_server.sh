#!/bin/bash

# Kills all java processes - needed for server auto restart process.
sudo killall java

echo ""
echo "Launching the game server in a new screen."
echo ""
echo "Type 'screen -r' to access the game server screen."
echo "Use CTRL + A + D to detach the live server screen so it runs in the background."
echo ""
echo ""
screen -dmS name ./ant_launcher.sh
