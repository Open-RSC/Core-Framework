#!/bin/bash
exec 0</dev/tty
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0` # No Color
source .env

# System Updates
echo ""
echo "Checking for system updates and upgrading if found."
echo ""
sudo apt-get update && sudo apt-get upgrade -y && sudo apt-get autoremove -y

# Game Repository
echo ""
echo "Checking for updates to the game code."
echo ""
sudo git pull

# Permissions
echo ""
echo "Verifying file and user permissions are set correctly"
echo ""
sudo chmod -R 777 .
sudo setfacl -m user:$USER:rw /var/run/docker.sock

# Finished
echo ""
echo "${RED}Open RSC:${NC}
An easy to use RSC private server framework.

What would you like to do next?

Choices:
  ${RED}1${NC} - Run Open RSC
  ${RED}2${NC} - Return to the main menu"
echo ""
echo "Which of the above do you wish to do? Type the choice number and press enter."
echo ""
read finished

if [ "$finished" == "1" ]; then
    make run-game
elif [ "$finished" == "2" ]; then
    make go
fi
