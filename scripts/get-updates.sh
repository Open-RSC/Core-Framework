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
sudo make compile

# Launcher
echo ""
echo "Compiling for development or production use?

Choices:
  ${RED}1${NC} - Development
  ${RED}2${NC} - Production"
echo ""
echo "Type the choice number and press enter."
echo ""
read -r compiling

if [ "$compiling" == "1" ]; then
  echo ""
elif [ "$compiling" == "2" ]; then
  # Client
  yes | sudo cp -rf client/*.jar ../Website/downloads/
  sudo chmod +x ../Website/downloads/*.jar
  sudo chmod 777 ../Website/downloads/*.jar

  # Launcher
  yes | sudo cp -rf Launcher/*.jar ../Website/downloads/
  sudo chmod +x ../Website/downloads/*.jar
  sudo chmod 777 ../Website/downloads/*.jar

  # Cache
  yes | sudo cp -a -rf "client/Cache/." "../Website/downloads/"
  sudo rm ../Website/downloads/MD5CHECKSUM
  sudo touch ../Website/downloads/MD5CHECKSUM && sudo chmod 777 ../Website/downloads/MD5CHECKSUM
  md5sum ../Website/downloads/* | sed 's/Website\/downloads\///g' | grep "^[a-zA-Z0-9]*" | awk '{print $2"="$1}' | tee ../Website/downloads/MD5CHECKSUM
  sudo sed -i 's/MD5CHECKSUM=/#MD5CHECKSUM=/g' "../Website/downloads/MD5CHECKSUM" # disables a bad line
  sudo sed -i 's/index=/#index=/g' "../Website/downloads/MD5CHECKSUM" # disables a bad line
  sudo sed -i 's/OpenRSC=/#OpenRSC=/g' "../Website/downloads/MD5CHECKSUM" # disables a bad line
  sudo sed -i 's/..\///g' "../Website/downloads/MD5CHECKSUM" # Removes ../
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
