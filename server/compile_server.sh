#!/bin/bash

echo ""
echo ""
echo "Compiling the game server."
echo ""
sudo ant compile_core
sudo ant compile_plugins
echo ""
echo ""
echo "Done!"
./run_server.sh
