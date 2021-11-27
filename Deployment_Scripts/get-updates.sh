#!/bin/bash

RED=$(tput setaf 1)
NC=$(tput sgr0) # No Color

exec 0</dev/tty
source .env

clear
echo ""
echo "Compiling all code now."
echo ""
echo ""
ant -f server/build.xml compile_core
ant -f server/build.xml compile_plugins
ant -f Client_Base/build.xml compile
ant -f PC_Launcher/build.xml compile

# Launcher
echo ""
echo "Copy and md5sum compiled client and cache files to the Website downloads folder?

Choices:
  ${RED}1${NC} - No
  ${RED}2${NC} - Yes (prod)"
echo ""
echo "Type the choice number and press enter."
echo ""
read -r compiling

if [ "$compiling" == "1" ]; then
    echo ""
elif [ "$compiling" == "2" ]; then
    # PC Client
    yes | sudo cp -f Client_Base/*.jar ../Website/portal/public/downloads/

    # Launcher
    yes | sudo cp -rf PC_Launcher/*.jar ../Website/portal/public/downloads/

    # Set file permissions within the Website downloads folder
    sudo chmod +x ../Website/portal/public/downloads/*.jar

    # Cache copy and file permissions
    yes | sudo cp -a -rf "Client_Base/Cache/." "../Website/portal/public/downloads/"

    cd '../Website/portal/public/downloads/' || exit
	
	# Performs md5 hashing of all files in cache and writes to a text file for the launcher to read
    find -type f \( -not -name "MD5.SUM" \) -exec md5sum '{}' \; >MD5.SUM
    
	cd '../../../../Game' || exit
fi

clear

# Finished
echo ""
echo "What would you like to do?

Choices:
  ${RED}1${NC} - Run game server
  ${RED}Ctrl+C${NC} - Exit"
echo ""
echo "Type the choice number and press enter."
echo ""
read -r finished

if [ "$finished" == "1" ]; then
    make run-server
else
    exit
fi