# Open RSC Game
###### [![Build Status](https://travis-ci.org/Open-RSC/Game.svg?branch=2.0.0)](https://travis-ci.org/Open-RSC/Game) 2.0.0

# Table of contents <a name="top"></a>
1. [How to Install](#install)
2. [Default Credentials](#credentials)
3. [Minimum Requirements](#requirements)
4. [Hosting Your Own Server](#hosting)

## How to Install<a name="install"></a>

<b>Windows:</b>

1. <a href="https://desktop.githubusercontent.com/releases/1.4.1-4eda7cdc/GitHubDesktopSetup.exe">Install GitHub Desktop</a>

2. Click "Clone a repository" and specify URL: https://github.com/Open-RSC/Game.git
<img src="https://i.imgur.com/ZMXUSr7.png"/>

3. Click "Clone"

4. Launch "Game/Go-Windows.cmd"


<b>Linux:</b>

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
