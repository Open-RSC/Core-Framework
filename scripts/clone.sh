#!/bin/bash
exec 0</dev/tty

# Open RSC: A replica RSC private server framework

clear
echo "Gathering files"
echo ""
git clone https://github.com/Open-RSC/Game.git
cd Game && sudo chmod -R 777 . && "./Go-Linux.sh"
