#!/bin/bash

# Check for any updates to the game server
echo "Pulling recent updates from the Open-RSC Game repository."
echo ""
echo ""
sudo make pull-game
echo ""
echo ""

# Compile the game server
echo "Compiling the game server."
echo ""
echo ""
#sudo ant -f server/build.xml compile
sudo gradle -b build.gradle compile
echo ""
echo ""

# Run the game server in a detached screen
echo "Importing fresh openrsc_config.sql database."
echo ""
sudo docker exec -i $(sudo docker-compose ps -q mysqldb) mysql -u"root" -p"root" < Databases/openrsc_config.sql 2>/dev/null
echo ""
echo ""
echo "Launching the game server in a new screen."
echo ""
echo "Type 'screen -r' to access the game server screen."
echo "Use CTRL + A + D to detach the live server screen so it runs in the background."
echo ""
echo ""
screen -dmS name ./ant_launcher.sh
