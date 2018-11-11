#!/bin/bash
exec 0</dev/tty
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0` # No Color

# Open RSC: A replica RSC private server framework

clear

echo ""
echo "${RED}Open RSC${NC}"
echo "An easy to use RSC private server framework"
echo ""
git clone https://github.com/Open-RSC/Game.git
cd Game && sudo chmod +x scripts/*.sh && sudo chmod -R 777 . && "./Go-Linux.sh"
