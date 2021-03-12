#!/bin/bash

# Run the game server
echo "Running the game server with config named \"$1.conf\"."

command="ant runserver -DconfFile=$1"
screen -dmS $1 $command
