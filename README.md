# Open RSC
###### [![Build Status](https://travis-ci.org/Open-RSC/Game.svg?branch=2.0.0)](https://travis-ci.org/Open-RSC/Game)

# Table of contents <a name="top"></a>
1. [Project Information](#general)
2. [How to Install](#install)
3. [Default Credentials](#credentials)
4. [Minimum Requirements](#requirements)
5. [Bug Reports, Discord, Reddit, and Twitter](#bugs)
6. [Enable High DPI Scaling For High Res Monitors](#dpi)

## Project Information<a name="general"></a>
The first priority of our development is ensuring the authentic content found in the original Runescape Classic game is replicated to its full extent. We are in no way associated or affiliated with JaGex, Runescape Classic, or any other similar companies or products. Our goal is to program and play Runescape Classic, and have fun doing it!

<a href="https://github.com/Open-RSC/Game/blob/2.0.0/CODE_OF_CONDUCT.md">Code of conduct</a>

<a href="https://github.com/Open-RSC/Game/blob/2.0.0/CONTRIBUTING.md">Contributing guide</a>


## How to Install<a name="install"></a>
<b>Windows Single Player:</b>

1. <a href="https://github.com/Open-RSC/Game/releases">Download the latest release "Source code (zip)"</a>

2. Unzip the downloaded file and open the folder it created.

3. Double click on "Go-Windows.cmd"

4. Press "1" for "Install" and press enter.

5. When install completes and you are back on the main menu, press "3" for "Run" and press enter.


<b>Windows Developers:</b>

1. Fork the repository and clone your fork of it locally with git.

2. Please download IntelliJ IDEA Community IDE and Git Kraken to work on the project files.

3. The "Go-Windows.cmd" script will get the basics installed for you, so follow the <b>Windows Single Player</b> guide above. 


<b>Linux / Mac:</b>

Paste the following in Terminal to begin the install process to get started:

```
bash <(curl -s https://raw.githubusercontent.com/Open-RSC/Game/2.0.0/scripts/clone.sh)
```

It is strongly suggested that you download IntelliJ IDEA Community IDE and Git Kraken for the project files. While this game is hosted on an Ubuntu VPS, you must learn the ins and outs before you will be ready to host a public facing server. 


[Return to top](#top)


## Default Credentials (user / pass)<a name="credentials"></a>

user: root

pass: root

[Return to top](#top)


## Minimum Requirements<a name="requirements"></a>

* Windows 7 x64

* Mac OS X High Sierra

* Ubuntu Linux 18.04

* 2GB RAM

[Return to top](#top)


## Bug Reports, Discord, Reddit, and Twitter<a name="bugs"></a>
Feel free to submit bug reports in the repository issues section! If you would like to chat with developers and players of this project, join our Discord server!

<a href="https://discordapp.com/invite/94vVKND">Discord</a>

<a href="https://www.reddit.com/r/openrsc">Reddit</a>

<a href="https://twitter.com/openrsc">Twitter</a>

<a href="https://openrsc.com">OpenRSC Website</a>


## Enable high DPI Scaling For High Res Monitors<a name="dpi"></a>

<b>Windows:</b>

Note: This requires Java 9 or newer to be installed. Java 8 does not support DPI scaling.

1. Launch the Open RSC game client

2. Open task manager (CTRL + ALT + DEL)

3. Right click the process "Java(TM) Platform SE binary"

4. Click "Open file location"

5. Right click on "javaw.exe" and click "Properties"

6. Click on the "Compatibility" tab

7. Set this: <img src="https://i.imgur.com/5gJqSMr.png"/>

8. Re-launch the Open RSC game client

[Return to top](#top)
