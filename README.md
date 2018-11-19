# Open RSC
###### [![Build Status](https://travis-ci.org/Open-RSC/Game.svg?branch=2.0.0)](https://travis-ci.org/Open-RSC/Game)

# Table of contents <a name="top"></a>
1. [General Information](#general)
2. [Enable High DPI Scaling For High Res Monitors](#dpi)
3. [How to Install](#install)
4. [Default Credentials](#credentials)
5. [Minimum Requirements](#requirements)
6. [Hosting Your Own Server](#hosting)

## General Information<a name="general"></a>

The first priority of our development is ensuring the authentic content found in the original Runescape Classic game is replicated to its full extent. We are in no way associated or affiliated with JaGex, Runescape Classic, or any other similar companies or products. Our goal is to program and play Runescape Classic, and have fun doing it!

You can obtain a sample of the running client from:
https://openrsc.com

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
___

## How to Install<a name="install"></a>

<b>Windows:</b>

1. <a href="https://desktop.githubusercontent.com/releases/1.4.1-4eda7cdc/GitHubDesktopSetup.exe">Install GitHub Desktop</a>

2. Click "Clone a repository" and specify URL: https://github.com/Open-RSC/Game.git
<img src="https://i.imgur.com/ZMXUSr7.png"/>

3. Click "Clone"

4. Launch "Game/Go-Windows.cmd"


<b>Linux / Mac:</b>

Paste the following in Terminal to begin the install process:

```
bash <(curl -s https://raw.githubusercontent.com/Open-RSC/Game/2.0.0/scripts/clone.sh)
```

[Return to top](#top)
___

## Default Credentials (user / pass)<a name="credentials"></a>

<b>Database:</b>

root / root

<b>Forum:</b>

Administrator / password

[Return to top](#top)
___

## Minimum Requirements<a name="requirements"></a>

* Windows 7 x64

* Mac OS X High Sierra

* Ubuntu Linux 16.04

* 1GB RAM

[Return to top](#top)
___

## Hosting Your Own Server<a name="hosting"></a>

For those who want to host their own Open RSC server, you can get $100 in DigitalOcean credit that is good for 60 days: https://m.do.co/c/be6a177e6664

We believe security is the most important part of running a server and suggest the following Droplet firewall configuration if you use DigitalOcean: <img src="https://i.imgur.com/Lpal89h.png"/>

After you configure this, you can test to see what ports are exposed to the public internet using "nmap DOMAIN -Pn" if you are using Linux and have installed "nmap" <img src="https://i.imgur.com/BNMYuGJ.png"/>

We also recommend that you use CloudFlare to proxy your web traffic. Please use the following settings to ensure you are well protected:

<img src="https://i.imgur.com/W3MqU3W.png"/>
<img src="https://i.imgur.com/dIGlpNB.png"/>
<img src="https://i.imgur.com/dxBi00O.png"/>
<img src="https://i.imgur.com/A5MXjgo.png"/>
<img src="https://i.imgur.com/0J5krT9.png"/>
<img src="https://i.imgur.com/7Z5foy0.png"/>
<img src="https://i.imgur.com/YeKseHB.png"/>
<img src="https://i.imgur.com/RkqfwI7.png"/>
___

[Return to top](#top)
___
