#!/bin/bash
exec 0</dev/tty
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0` # No Color
source .env

rm singleplayer.log
touch singleplayer.log && chmod 777 singleplayer.log | tee -a singleplayer.log &>/dev/null

# Check for any updates to the game server
clear
echo "Pulling recent updates from the Open-RSC Game repository."
sudo git pull | tee -a singleplayer.log &>/dev/null
sudo make pull-game | tee -a singleplayer.log &>/dev/null
sudo chmod -R 777 Game | tee -a installer.log &>/dev/null

# Docker
clear
echo "Starting Docker containers."
sudo make stop | tee -a singleplayer.log &>/dev/null
sudo make start-single-player | tee -a singleplayer.log &>/dev/null

# Compile the game server and client
clear
echo "Compiling the game client. Any errors will be in singleplayer.log"
sudo ant -f "Game/client/build.xml" compile | tee -a singleplayer.log &>/dev/null

clear
echo "Compiling the game server. Any errors will be in singleplayer.log"
sudo ant -f "Game/server/build.xml" compile_core | tee -a singleplayer.log &>/dev/null
sudo ant -f "Game/server/build.xml" compile_plugins | tee -a singleplayer.log &>/dev/null

# Run the game client in a new window
clear
echo "Launching the game client."
sudo ant -f Game/client/build.xml runclient &

# Run the game server in the current window
clear
echo "Launching the game server."
sudo ant -f Game/server/build.xml runservermembers
