#!/bin/bash
echo "Pulling recent updates from the Open-RSC Game repo"
sudo make pull
echo ""
echo ""

#Compile
echo "Compiling the game server"
echo ""
echo ""
sudo ant compile

#Run game server
echo "Launching the game server"
echo ""
echo ""
ant build.xml runserver
