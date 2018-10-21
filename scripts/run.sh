#!/bin/bash

# Run the game server in a detached screen
echo ""
echo "Launching the game server in a new screen."
echo ""
echo "Type 'screen -r' to access the game server screen."
echo "Use CTRL + A + D to detach the live server screen so it runs in the background."
echo ""
echo ""
cd server
screen -dmS name ./ant_launcher.sh
