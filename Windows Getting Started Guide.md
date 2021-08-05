### Guide for running Open RuneScape Classic on Windows

## Install Java Development Kit (JDK)

The Open RuneScape Classic project team recommends developers download Open JDK. A MSI installer for Windows is the simplest option. Any Java version 8 or later will work.

<a href="https://adoptopenjdk.net/releases.html?variant=openjdk13&jvmVariant=hotspot">Download Adopt OpenJDK</a>

## Starting the game

You are now ready to launch "Start-Windows.cmd"

Once open, press 1 and then press enter. This is for the option of "Start the game"

A new window will open stating that the game will start up in 10 seconds. When the game client opens, you will need to click "New User" to create a player.

If you become stuck on Tutorial Island or just wish to skip it, there is a link in the in-game options menu above the log out that reads "skip tutorial".

## Changing game features and configuration

At this point, you may wish to use different game features than the default RSC ones. This requires you to fully exit the game client, and the game server (use choice #2 to exit in the command prompt window) to fully shut it down.


Now navigate to "core -> server" and delete the existing file named "local.conf", then make a copy of "rsccabbage.conf", renaming the duplicate file "local.conf". Same applies for "openrsc.conf" and the other files in that directory. If "local.conf" exists, the game server will override the settings that are in "default.conf". Think of "openrsc.conf" and "rsccabbage.conf" as ready to go config presets.

Depending on server configuration, your server port may differ. If 43594 is instead now 43595 because "rsccabbage.conf" is being used, and you do not wish to edit it any, you will need to go into "core -> Client_Base -> Cache" and edit "port.txt" to reflect the new port being used by the game server.

Developers should avoid editing "default.conf" and instead make a copy the chosen .conf file, rename it to "local.conf" and use that instead. That way, they always have working config to fall back on if something doesn't work the way they expect.

Last but not least, always restart the game server after fully exiting it when you have made changes to your "local.conf" in order to have them be applied. An already running server will not read any changes to the file.

## Changing a player's role

Are you ready to set a player role as an admin, moderator, or back to a player? It is possible to set the role / group ID while in-game using a command, but you can also use the main menu option of "Change a player's in-game role"
