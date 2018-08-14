#!/bin/bash

# Run the game server
echo ""
echo "Running the game server. Press CTRL + C to shut it down or"
echo "CTRL + A + D to detach the screen so this continues in the background."
echo ""
echo "Console output is being saved to gameserver.log"
echo ""
java -jar Open_RSC_Server.jar > gameserver.log
