#!/bin/bash

# Check for any updates to the game server
echo "Pulling recent updates from the Open-RSC Game repository."
echo ""
echo ""
sudo make pull
echo ""
echo ""

# Compile the game server
echo "Compiling the game server."
echo ""
echo ""
sudo ant compile
echo ""
echo ""

# Run the game server in a detached screen
echo "Launching the game server in a new screen."
echo ""
echo "Type 'screen -r' to access the game server screen."
echo "Use CTRL + A + D to detach the live server screen so it runs in the background."
echo ""
echo ""
screen -dmS name ./ant_launcher.sh
