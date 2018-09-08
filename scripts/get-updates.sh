#!/bin/bash
exec 0</dev/tty
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

# Server
echo ""
echo "Compiling the game server. Any errors will be in updater.log"
echo ""
sudo ant -f "server/build.xml" compile

# Client
echo ""
echo "Compiling and preparing the game client. Any errors will be in updater.log"
echo ""
sudo ant -f "client/build.xml" compile
yes | sudo cp -rf "client/Open_RSC_Client.jar" "Website/downloads/"
sudo chmod +x "Website/downloads/Open_RSC_Client.jar"
sudo chmod 777 "Website/downloads/Open_RSC_Client.jar"

# Launcher
echo ""
echo "Compiling and preparing the game launcher. Any errors will be in updater.log"
echo ""
sudo ant -f "Launcher/nbbuild.xml" jar
yes | sudo cp -rf "Launcher/dist/Open_RSC_Launcher.jar" "Website/downloads/"
sudo chmod +x "Website/downloads/Open_RSC_Launcher.jar"
sudo chmod 777 "Website/downloads/Open_RSC_Launcher.jar"

# Cache
echo ""
echo "Preparing the client cache."
echo ""
yes | sudo cp -a -rf "client/Cache/." "Website/downloads/cache/"
sudo rm Website/downloads/cache/MD5CHECKSUM
sudo touch Website/downloads/cache/MD5CHECKSUM && sudo chmod 777 Website/downloads/cache/MD5CHECKSUM | tee updater.log
md5sum Website/downloads/cache/* | sed 's/Website\/downloads\/cache\///g' |  grep ^[a-zA-Z0-9]* | awk '{print $2"="$1}' | tee Website/downloads/cache/MD5CHECKSUM
sudo sed -i 's/MD5CHECKSUM=/#MD5CHECKSUM=/g' "Website/downloads/cache/MD5CHECKSUM"
