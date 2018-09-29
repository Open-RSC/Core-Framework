#!/bin/bash
exec 0</dev/tty

# Open RSC: A replica RSC private server framework

clear

echo ""
echo "${RED}Open RSC:${NC}"
echo "An easy to use RSC private server framework."
echo ""
echo ""
echo "Please wait while the core files are being downloaded."
echo ""
git clone https://github.com/Open-RSC/Game.git
cd Game && sudo chmod -R 777 . && "./Go-Linux.sh"
