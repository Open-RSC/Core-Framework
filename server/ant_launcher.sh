#!/bin/bash

# If we didn't specify a config name, give an error message. 
# We could default to default.conf, but we may not want this.
if [[ -z $1 ]]; then
    echo "You must specify a config name, like this: ant_launcher.sh default"
    exit 1
fi

# Run the game server
echo "Running the game server with config named \"$1.conf\"."

# Default to ZGC for production use
command="ant runserverzgc -DconfFile=$1"
# Use Java 8 if we specify it, like from single player run_server.sh
if [[ -n $2 && $2 == "g1gc" ]]; then 
    command="ant runserver -DconfFile=$1"
fi    

screen -dmS $1 $command
