# Open-Pi-Scape
The Raspberry Pi 3B+ (and earlier editions) do not have enough memory to run the game client and the game server at the same time. Raspberry Pi 4 and above with 2GB or more RAM are capable of running both.

This project is design for the Raspberry Pi 3B+ with 1GB of RAM to act as a home LAN game server for Android and PC game clients to connect to at home. Due to limited memory and processor speeds, it is not advisable to host a public server with a Raspberry Pi 3B+, nor have more than 2 or 3 online players at a time due to in-game slow downs.

Example result on a Raspberry Pi 3B+ when running the "top" command on a CentOS 7 install only running the game server with a single logged in player:
```
Tasks: 172 total,   1 running, 122 sleeping,   0 stopped,   0 zombie
%Cpu(s): 33.6 us,  5.5 sy,  0.0 ni, 60.5 id,  0.4 wa,  0.0 hi,  0.0 si,  0.0 st
KiB Mem :   948268 total,    10392 free,   720148 used,   217728 buff/cache
KiB Swap:   499708 total,   479484 free,    20224 used.   207656 avail Mem 

  PID USER      PR  NI    VIRT    RES    SHR S  %CPU %MEM     TIME+ COMMAND                                                                                                   
 2206 root      20   0  412612 351060  11088 S 151.3 37.0   2:23.28 java  
 ```


You will want to have a bootable microSD card running a fresh install of CentOS aarch64:
http://isoredirect.centos.org/altarch/7/isos/armhfp/CentOS-Userland-7-armv7hl-RaspberryPI-GNOME-1908-sda.raw.xz

You can write to your microSD card using various utilities. Here is a great open source tool for any platform:
https://www.balena.io/etcher/


See this guide:
https://wiki.centos.org/SpecialInterestGroup/AltArch/armhfp?action=show&redirect=SpecialInterestGroup%2FAltArch%2FArm32#head-0f62978700d6645d09caddc100a5d1aa2cbdac1f

First, SSH into your Pi and use the default credentials:
```
ssh root@YOUR_PI's_IP_ADDRESS
```

SSH user password: centos


You will need to expand the filesystem to use the entire microSD card:
```
/usr/bin/rootfs-expand
```

You next should turbo / overclock your Pi as it is generally pretty slow otherwise:
https://haydenjames.io/raspberry-pi-3-overclock/
```
yum install -y nano
nano /boot/config.txt
```

Paste the following:
```
core_freq=500 # GPU Frequency
arm_freq=1350 # CPU Frequency
over_voltage=4 # Electric power sent to CPU / GPU (4 = 1.3V)
disable_splash=1 # Disables the display of the electric alert screen
force_turbo=1
sdram_freq=600
boot_delay=1
# gpu_mem=320 # Reserved memory for GPU
```


Once installed, perform OS updates and install MariaDB (SQL server), and various utilities via:
```
yum update -y && sudo yum install git wget mariadb mariadb-server ant screen -y
```

Enable MariaDB to start up:
```
systemctl enable --now mariadb
```

Verify Java is installed correctly:
```
java -version
```

It should look something like:
```
openjdk version "1.8.0_222"
OpenJDK Runtime Environment (build 1.8.0_222-b10)
OpenJDK Zero VM (build 25.222-b10, interpreted mode)
```

Now download the zip release of this repo - or clone it with Git.
```
git clone https://gitlab.openrsc.com/open-rsc/Open-Pi-Scape.git && cd Open-Pi-Scape
```


Set the root user's password to "root" instead of the default and it being blank:
```
mysql -e "SET PASSWORD FOR 'root'@'localhost' = PASSWORD('root');"
```

Create the database(s) and import:
```
mysql -uroot -proot -e "create database openrsc;" && mysql openrsc < Required/openrsc_game_server.sql && mysql openrsc < Required/openrsc_game_players.sql

mysql -uroot -proot -e "create database cabbage;" && mysql cabbage < Required/cabbage_game_server.sql && mysql cabbage < Required/cabbage_game_players.sql
```

Open the firewall ports:
```
firewall-cmd --zone=public --add-port=80/tcp --permanent
firewall-cmd --zone=public --add-port=443/tcp --permanent
firewall-cmd --zone=public --add-port=43594/tcp --permanent
firewall-cmd --zone=public --add-port=43595/tcp --permanent
firewall-cmd --zone=public --add-port=43596/tcp --permanent
firewall-cmd --zone=public --add-port=43597/tcp --permanent
firewall-cmd --zone=public --add-port=43598/tcp --permanent
firewall-cmd --zone=public --add-port=43599/tcp --permanent
firewall-cmd --reload 
```

Start the game server in a background "screen" session:
```
./run_server.sh
```

To view the "screen" background session, type:
```
screen -r
```

Press CTRL + A + D to leave the screen view and keep the server running in the background.


Now that the game server is running, you will want to download the same project files on your PC and edit the following to set the server IP:
```
Cache/ip.txt
```

Replace the contents of this file with the local network IP address of your Raspberry Pi. If you do not know it, type "ifconfig" on the Pi over SSH.

Now start up the game client from your PC by launching "Open_RSC_Client.jar"
```
java -jar Open_RSC_Client.jar
```