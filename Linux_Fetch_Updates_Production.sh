#!/bin/bash
exec 0</dev/tty

# Open RSC: A replica RSC private server framework
#
# Installs and updates Open RSC
#
# Install with this command (from your Linux machine):
#
# curl -sSL https://raw.githubusercontent.com/Open-RSC/Docker-Home/master/Linux_Cloner.sh | bash

source .env

rm updater.log
touch updater.log && chmod 777 updater.log | tee updater.log | &>/dev/null

# Check for any updates to the game server
clear
echo "Pulling recent updates from the Open-RSC Game repository."
cd Game
sudo git pull | tee -a ../updater.log &>/dev/null

# Verifies permissions are set correctly
sudo chmod -R 777 Game | tee -a ../updater.log &>/dev/null
sudo setfacl -m user:$USER:rw /var/run/docker.sock | tee -a ../updater.log &>/dev/null
cd ..

# Server
clear
echo "Compiling the game server. Any errors will be in updater.log"
sudo ant -f "Game/server/build.xml" compile | tee updater.log &>/dev/null

# Client
clear
echo "Compiling and preparing the game client. Any errors will be in updater.log"
sudo ant -f "Game/client/build.xml" compile | tee -a ../../updater.log &>/dev/null
yes | sudo cp -rf "Game/client/Open_RSC_Client.jar" "Website/downloads/" | tee -a ../../updater.log &>/dev/null

# Launcher
clear
echo "Compiling and preparing the game launcher. Any errors will be in updater.log"
sudo ant -f "Game/Launcher/nbbuild.xml" jar | tee -a updater.log &>/dev/null
yes | sudo cp -rf "Game/Launcher/dist/Open_RSC_Launcher.jar" "Website/downloads/" | tee -a updater.log &>/dev/null

# Cache
clear
echo "Preparing the cache."
yes | sudo cp -a -rf "Game/client/Cache/." "Website/downloads/cache/" | tee -a updater.log &>/dev/null
sudo rm Website/downloads/cache/MD5CHECKSUM | tee -a updater.log &>/dev/null
sudo touch Website/downloads/cache/MD5CHECKSUM && sudo chmod 777 Website/downloads/cache/MD5CHECKSUM | tee updater.log | &>/dev/null
md5sum Website/downloads/cache/* | sed 's/Website\/downloads\/cache\///g' |  grep ^[a-zA-Z0-9]* | awk '{print $2"="$1}' | tee Website/downloads/cache/MD5CHECKSUM | tee -a updater.log &>/dev/null
sudo sed -i 's/MD5CHECKSUM=/#MD5CHECKSUM=/g' "Website/downloads/cache/MD5CHECKSUM"

# Database
clear
echo "Preparing the database."
docker exec -i $(sudo docker-compose ps -q mysqldb) mysql -u"$MYSQL_ROOT_USER" -p"$MYSQL_ROOT_PASSWORD" < Game/openrsc_game.sql 2>/dev/null

# Run the game server in a detached screen
./v2_Linux_Run_Production_Server.sh
