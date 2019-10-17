## Guide for running Open RSC Single Player on Linux:

# Using the built in installation script with Docker

If you are okay with Docker running on your Linux OS, the installation script that is used in "Start-Linux.sh" can obtain Docker for you, download the MariaDB SQL server container, configure your firewall, SSH permissions and port, as well as install other packages that are used when hosting a public game server. It is not recommended unless you are okay with these changes. Example: port 22 for SSH is changed to port 55555 and the firewall modified to allow entry for all game server ports and the new SSH port. If in question, open "scripts/combined-install.sh" and "scripts/docker-install.sh" to get a better idea about what the built-in installer does.

# Installing Java

You will need to have Java 1.8 or above installed in order to run the game server and client. We recommend the latest version of OpenJDK.

# Database Server

The game operates using MySQL. We recommend MariaDB for the SQL server with username "root" and password "root". If you wish to use a different username and password for your SQL server, you will need to edit ".env" and set the variables in there accordingly.

You will need to create the necessary databases and import the SQL needed by the game server. Utilize the following make commands as needed:
```
make create db=openrsc
make create db=cabbage
make create db=preservation

make import db=openrsc
make import db=cabbage
make import db=preservation
```

If you need to upgrade a database due to changes in the repository, utilize:
```
make upgrade db=openrsc
```

# Starting the game

You are now ready to run "./Start-Linux.sh"

You must have the game server fully running before the game client. You can verify it is running after starting it up by typing "screen -r" and pressing enter. To "minimize" the screen instance of the running game server and not close it, type "CTRL + A + D" and it will return to your previous console view.

Once you open the game client, you will need to click "New User" to create a player. If you become stuck on Tutorial Island or just wish to skip it, there is a link in the in-game options menu above the log out that reads "skip tutorial".

# Changing game features and configuration

At this point, you may wish to use different game features than the default RSC ones. This requires you to fully exit the game client and the game server (use choice #2 to exit in the command prompt window) to fully shut it down.


Now navigate to "server" and delete the existing file named "local.conf", then make a copy of "rsccabbage.conf", renaming the duplicate file "local.conf". Same applies for "openrsc.conf" and the other files in that directory. If "local.conf" exists, the game server will override the settings that are in "default.conf". Think of "openrsc.conf" and "rsccabbage.conf" as ready to go config presets.


If you opt to use different named databases, edit the following line in your "local.conf" (or default.conf) to match the name of your database:
```
<entry key = "mysql_db">cabbage</entry>
```

Depending on which configuration is used, your server port may differ. If 43594 is instead now 43595 because "rsccabbage.conf" is being used and you do not wish to edit it any, you will need to go into "client/Cache" and edit "port.txt" to reflect the new port being used by the game server.

It is strongly suggested that you do not edit "default.conf" and instead make a copy of whatever .conf file, rename it to "local.conf" and edit that instead. That way, you always have working config to fall back on if something doesn't work the way you wish.

Last but not least, always restart the game server after fully exiting it when you have made changes to your "local.conf" in order to have them be applied. An already running server will not read any changes to the file.

# Changing a player's role

Are you ready to set a player role as an admin, moderator, or back to a player? It is possible to set the role / group ID while in-game using a command but you can also use the make command of:
```
make rank db=cabbage group=0 username=wolf
```

# Upgrading Single Player

The most efficient way to upgrade is to use the make command as shown below. It will create a timestamp-database name.sql.zip file in the "Backups" folder.
```
make backup db=openrsc
```

Download and extract from the .zip the latest Single Player release and copy your database backup export file over to the new location's "Backups" folder. Run the following make command from the new folder location: (updated as needed!)
```
make restore name=20191017-0226-EDT-cabbage.zip db=cabbage
```
Lastly, you will want to use the following make command to ensure the database has been fully upgraded:
```
make upgrade db=openrsc
make upgrade db=cabbage
make upgrade db=preservation
```