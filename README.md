# Open RSC Game
###### [![Build Status](https://travis-ci.org/Open-RSC/Game.svg?branch=master)](https://travis-ci.org/Open-RSC/Game) Master
###### [![Build Status](https://travis-ci.org/Open-RSC/Game.svg?branch=2.0.0)](https://travis-ci.org/Open-RSC/Game) 2.0.0

![Death](https://i.imgur.com/tzLgEwV.png)

# Table of contents <a name="top"></a>
1. [How to Install](#install)
2. [Choices](#choices)
3. [Default Credentials](#credentials)
4. [Minimum Requirements](#requirements)
5. [Required Step For Windows Users](#windows)
6. [Setup Process](#setup)
7. [Steps to Host on a VPS](#vps)


## How to Install Open RSC <a name="install"></a>

Docker version (Linux and Windows):

    curl -sSL https://raw.githubusercontent.com/Open-RSC/Game/master/Linux_Cloner.sh | bash

Direct install version (Linux only):

    curl -sSL https://raw.githubusercontent.com/Open-RSC/Game/master/Linux_Simple_Cloner.sh | bash

[Return to top](#top)
___

## Choices <a name="choices"></a>

  1. Single player RSC game + basic database editing (PHPMyAdmin)
  2. Game + Website + PHPMyAdmin

The game client registers new players upon their first login attempt.

Admin role is group_id = 1, players are group_id = 10

[Return to top](#top)
___

## Default Credentials <a name="credentials"></a>

#### Database

Username: root

Password: root

[Return to top](#top)
___

## Minimum Requirements <a name="requirements"></a>

* Windows 10

* Mac OS X High Sierra

* Ubuntu Linux 16.04

[Return to top](#top)
___

## Required Step For Windows Users <a name="windows"></a>

Open Docker and make your drives available to your Docker containers:

![Shared drives setting](https://i.imgur.com/6YsGkoZ.png)

[Return to top](#top)
___

## Setup Process <a name="setup"></a>

1. Perform the first time setup:

    ```sh
    ./Linux_Installer.sh
    ```

2. Open your favorite browser:

    * [http://localhost](http://localhost)
    * [http://localhost:55555](http://localhost:55555) PHPMyAdmin (default username: root, password: root)

3. Start the Docker containers and run the game server and client:

    ```sh
    ./Linux_Single_Player.sh
    ```

4. Backup all databases:

    ```sh
    ./Linux_Backup_Databases.sh
    ```

6. Stop the game's Docker containers and shut down the game server:

    ```sh
    make stop
    ```

[Return to top](#top)
___

## Steps to Host on a VPS <a name="vps"></a>

  * Run the installer:

  ```sh
  curl -sSL https://raw.githubusercontent.com/Open-RSC/Game/master/Linux_Installer.sh | bash
  ```

  * Follow the steps to install needed programs

  * Select "2. Deployment for a publicly hosted server"

  * You will be prompted to edit specific files. Below is what to do each:

PHPMyAdmin MariaDB SQL users

  * Create a new user in PHPMyAdmin (http://localhost:55555), grant it all permissions, remove pre-existing users.

    * Use % for the host associated with the new user. Docker containers do not have static IP addresses and we are using the PHPMyAdmin Docker container to connect to the MariaDB Docker container. Each has a unique internally assigned IP address that is not localhost. The Docker container port of tcp/3306 for MariaDB is bound to the server as localhost so there should be no threat of external connections.

[Return to top](#top)
