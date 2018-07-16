#!/bin/bash
echo "Launching in a new window. Type 'screen -r' to view the live server screen."
echo "Use CTRL + A + D to detach the live server screen."
echo ""
screen -dmS name ./ant_launcher.sh
