# Open RSC Game
###### [![Build Status](https://travis-ci.org/Open-RSC/Game.svg?branch=master)](https://travis-ci.org/Open-RSC/Game) Master
###### [![Build Status](https://travis-ci.org/Open-RSC/Game.svg?branch=2.0.0)](https://travis-ci.org/Open-RSC/Game) 2.0.0

![Death](https://i.imgur.com/tzLgEwV.png)

The game client registers new players upon their first login attempt.


Admin role is group_id = 1, players are group_id = 10


### Two methods for installation:

Docker version (Linux and Windows):

    curl -sSL https://raw.githubusercontent.com/Open-RSC/Docker-Home/master/Linux_Cloner.sh | bash

Direct install version (Linux only):

    curl -sSL https://raw.githubusercontent.com/Open-RSC/Game/master/Linux_Simple_Cloner.sh | bash

If hosting externally on a VPS, it is possible to monitor the game server via JMX sampling with VisualVM.

 * ssh -l USERNAME VPS_DOMAIN -L 9990:localhost:9990

 * Open Visual VM and under "Local", right click and select "Add JMX Connection"

 * In "Connection", specify "localhost:9990" and click "OK"

 * Right click where it reads "localhost:9990 (pid xxxx)" and select "Sample"
