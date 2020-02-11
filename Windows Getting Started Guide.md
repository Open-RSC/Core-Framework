### Guide for running Open RSC on Windows

## Single Player

First, download the latest release of Open RSC Single Player as this is designed for Windows with virtually zero set up required:

https://orsc.dev/open-rsc/Single-Player/-/releases

## Installing Java

Download Open JDK 12 for Windows (MSI installer version) and if you already have Java installed (ex: Oracle Java 8), then it is highly suggested that you first uninstall it. Oracle will not be supporting non-enterprise Java much longer and Open JDK is the future.
<a href="https://cdn.azul.com/zulu/bin/zulu12.2.3-ca-jdk12.0.1-win_x64.msi">https://cdn.azul.com/zulu/bin/zulu12.2.3-ca-jdk12.0.1-win_x64.msi</a>

For the latest version of Open JDK: (pick the MSI installer version!)
<a href="https://www.azul.com/downloads/zulu/">https://www.azul.com/downloads/zulu/</a>

## Starting the game

You are now ready to double click on "Start-Windows.cmd"

Once open, press 1 and then press enter. This is for the option of "Start the game"

A new window will open stating that the game will start up in 10 seconds. When the game client opens, you will need to click "New User" to create a player.

If you become stuck on Tutorial Island or just wish to skip it, there is a link in the in-game options menu above the log out that reads "skip tutorial".

## Changing game features and configuration

At this point, you may wish to use different game features than the default RSC ones. This requires you to fully exit the game client and the game server (use choice #2 to exit in the command prompt window) to fully shut it down.


Now navigate to "Single-Player -> server" and delete the existing file named "local.conf", then make a copy of "rsccabbage.conf", renaming the duplicate file "local.conf". Same applies for "openrsc.conf" and the other files in that directory. If "local.conf" exists, the game server will override the settings that are in "default.conf". Think of "openrsc.conf" and "rsccabbage.conf" as ready to go config presets.


If you opt to use different named databases, edit the following line in your "local.conf" (or default.conf) to match the name of your database:
```
<entry key = "mysql_db">cabbage</entry>
```

Depending on which configuration is used, your server port may differ. If 43594 is instead now 43595 because "rsccabbage.conf" is being used and you do not wish to edit it any, you will need to go into "Single-Player -> client -> Cache" and edit "port.txt" to reflect the new port being used by the game server.

It is strongly suggested that you do not edit "default.conf" and instead make a copy of whatever .conf file, rename it to "local.conf" and edit that instead. That way, you always have working config to fall back on if something doesn't work the way you wish.

Last but not least, always restart the game server after fully exiting it when you have made changes to your "local.conf" in order to have them be applied. An already running server will not read any changes to the file.

## Changing a player's role

Are you ready to set a player role as an admin, moderator, or back to a player? It is possible to set the role / group ID while in-game using a command but you can also use the main menu option of "Change a player's in-game role"

## Upgrading

If you opt to not use git for pulling updated code from the repository, then the most efficient way to upgrade is to use the main menu option of "Backup game database" to create a database backup export file. Download and extract from the .zip the latest Single Player release and copy your database backup export file over to there. Run the "Start-Windows.cmd" script and from the main menu, select "Restore game database". Lastly, you will want to select "Upgrade the database" from the main menu to ensure the database has been fully upgraded.
