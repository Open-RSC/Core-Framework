#!/bin/bash

RED=$(tput setaf 1)
NC=$(tput sgr0) # No Color

exec 0</dev/tty
source .env

clear

# Permissions
echo ""
echo "Verifying file and user permissions are set correctly"
echo ""
sudo chmod -R 777 .

echo ""
echo "Compiling all code now."
echo ""
echo ""
ant -f server/build.xml compile_core
ant -f server/build.xml compile_plugins
ant -f Client_Base/build.xml compile
ant -f PC_Launcher/build.xml compile
#gradle -b Android_Client/Open\ RSC\ Android\ Client/build.gradle assembleDebug

# Launcher
echo ""
echo "Copy and md5sum compiled client and cache files to the Website downloads folder?

Choices:
  ${RED}1${NC} - No
  ${RED}2${NC} - Yes"
echo ""
echo "Type the choice number and press enter."
echo ""
read -r compiling

if [ "$compiling" == "1" ]; then
    echo ""
elif [ "$compiling" == "2" ]; then
    # PC Client
    yes | sudo cp -f Client_Base/*.jar ../Website/site/public/downloads/

    # Android client
    #yes | sudo cp -f Android_Client/Open\ RSC\ Android\ Client/*.apk ../Website/site/public/downloads/

    # Launcher
    yes | sudo cp -rf PC_Launcher/*.jar ../Website/site/public/downloads/

    # Set file permissions within the Website downloads folder
    sudo chmod +x ../Website/site/public/downloads/*.jar
    sudo chmod +x ../Website/site/public/downloads/*.jar
    sudo chmod -R 777 ../Website/site/public/downloads

    # Cache copy and file permissions
    sudo chmod 777 -R 'Client_Base/Cache'                                                       # Normal cache related files
    yes | sudo cp -a -rf "Client_Base/Cache/." "../Website/site/public/downloads/"                          # Normal cache related files

    cd '../Website/site/public/downloads/' || exit
    find -type f \( -not -name "MD5.SUM" \) -exec md5sum '{}' \; >MD5.SUM # Performs md5 hashing of all files in cache and writes to a text file for the launcher to read
    cd '../../../../Game' || exit
fi

clear

# Finished
echo ""
echo "What would you like to do?

Choices:
  ${RED}1${NC} - Run game server
  ${RED}2${NC} - Return to the main menu"
echo ""
echo "Type the choice number and press enter."
echo ""
read -r finished

if [ "$finished" == "1" ]; then
    make run-server
elif [ "$finished" == "2" ]; then
    make start-linux
fi
