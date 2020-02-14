#!/bin/bash

# Client
yes | sudo cp -rf Client_Base/*.jar ../Website/downloads/
sudo chmod +x ../Website/downloads/*.jar
sudo chmod 777 ../Website/downloads/*.jar

# Launcher
yes | sudo cp -rf PC_Launcher/*.jar ../Website/downloads/
sudo chmod +x ../Website/downloads/*.jar
sudo chmod 777 ../Website/downloads/*.jar

# Cache
sudo chmod 777 -R 'Client_Base/Cache'
yes | sudo cp -a -rf "Client_Base/Cache/." "../Website/downloads/"
cd '../Website/downloads/' || exit
find -type f \( -not -name "MD5.SUM" \) -exec md5sum '{}' \; >MD5.SUM
cd '../../Game' || exit
