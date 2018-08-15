#!/bin/bash

echo ""
echo ""
echo "Compiling the game server."
echo ""
sudo ant compile
echo ""
echo ""
echo "Done!"
./run_server.sh