# Open RSC Core Game [![Build Status](https://travis-ci.org/Open-RSC/Game.svg?branch=master)](https://travis-ci.org/Open-RSC/Game)

![Death](https://i.imgur.com/tzLgEwV.png)

The game client registers new players upon their first login attempt.


Admin role is group_id = 1, players are group_id = 10

It is suggested that you clone the Docker-Home repository and use the setup script to install the various docker containers needed for automatic installation of this.

https://github.com/open-rsc/Docker-Home


If hosting externally on a VPS, it is possible to monitor the game server via JMX sampling with VisualVM.

 * ssh -l USERNAME VPS_DOMAIN -L 9990:localhost:9990

 * Open Visual VM and under "Local", right click and select "Add JMX Connection"

 * In "Connection", specify "localhost:9990" and click "OK"

 * Right click where it reads "localhost:9990 (pid xxxx)" and select "Sample"
