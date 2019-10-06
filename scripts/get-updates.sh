#!/bin/bash

RED=$(tput setaf 1)
GREEN=$(tput setaf 2)
NC=$(tput sgr0) # No Color

exec 0</dev/tty
source .env

# Permissions
echo ""
echo "Verifying file and user permissions are set correctly"
echo ""
sudo chmod -R 777 .

echo ""
echo "Compiling all code now."
echo ""
echo ""
sudo make compile

# Client
yes | sudo cp -rf client/*.jar /opt/Website/openrsc_web/public/downloads/cache/
sudo chmod +x /opt/Website/openrsc_web/public/downloads/cache/*.jar
sudo chmod 777 /opt/Website/openrsc_web/public/downloads/cache/*.jar

# Launcher
yes | sudo cp -rf Launcher/*.jar /opt/Website/openrsc_web/public/downloads/
sudo chmod +x /opt/Website/openrsc_web/public/downloads/*.jar
sudo chmod 777 /opt/Website/openrsc_web/public/downloads/*.jar

# Cache
yes | sudo cp -a -rf "client/Cache/." "/opt/Website/openrsc_web/public/downloads/cache/"
cd ..
sudo rm Website/downloads/cache/MD5CHECKSUM
sudo touch Website/downloads/cache/MD5CHECKSUM && sudo chmod 777 Website/downloads/cache/MD5CHECKSUM
md5sum Website/downloads/cache/* | sed 's/Website\/downloads\/cache\///g' | grep "^[a-zA-Z0-9]*" | awk '{print $2"="$1}' | tee Website/downloads/cache/MD5CHECKSUM
sudo sed -i 's/MD5CHECKSUM=/#MD5CHECKSUM=/g' "Website/downloads/cache/MD5CHECKSUM"
sudo sed -i 's/index=/#index=/g' "Website/downloads/cache/MD5CHECKSUM"
cd Game

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
read -r finished

if [ "$finished" == "1" ]; then
  make run-game
elif [ "$finished" == "2" ]; then
  make go
fi
