#!/bin/bash

# Run the game server in a detached screen
echo ""
echo "Launching the game server in a new screen."
echo ""
echo "Type 'screen -r' followed by the name of the server to access the game server console."
echo "Press 'CTRL + C' to shut down the server from that screen or"
echo "Use 'CTRL + A, D' to detach the live server screen so it runs in the background."
echo ""
cd server

# runs server with the configuration found in "server/default.conf"
bash ./ant_launcher.sh default

# uncomment to run additional servers with different configurations. Prod use: uncomment all below
#bash ./ant_launcher.sh openrsc &&  \
#bash ./ant_launcher.sh rsccabbage && \
#bash ./ant_launcher.sh uranium && \
#bash ./ant_launcher.sh rsccoleslaw && \
#bash ./ant_launcher.sh 2001scape && \
##bash ./ant_launcher.sh openpk
