#!/bin/bash
exec 0</dev/tty
source .env


# Check for any updates to the game server
clear
echo "Pulling recent updates from the Open-RSC Game repository."
sudo git pull
sudo make pull-game
sudo chmod -R 777 Game
sudo chmod 644 etc/mariadb/innodb.cnf

# Docker
clear
echo "Starting Docker containers."
sudo make stop
sudo make start-single-player

# Compile the game server and client
clear
echo "Compiling the game client. Any errors will be in singleplayer.log"
sudo ant -f "Game/client/build.xml" compile

clear
echo "Compiling the game server. Any errors will be in singleplayer.log"
sudo ant -f "Game/server/build.xml" compile_core
sudo ant -f "Game/server/build.xml" compile_plugins

# Run the game client in a new window
clear
echo "Launching the game client."
sudo ant -f Game/client/build.xml runclient &

# Run the game server in the current window
clear
echo "Launching the game server."
sudo ant -f Game/server/build.xml runservermembers
