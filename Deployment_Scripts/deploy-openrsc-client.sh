#!/bin/bash

cd /opt/openrsc

#ant -f server/build.xml compile_core
#ant -f server/build.xml compile_plugins
ant -f Client_Base/build.xml compile
ant -f PC_Launcher/build.xml compile

# PC Client
yes | cp -f Client_Base/*.jar /opt/website-downloads/

# Launcher
yes | cp -rf PC_Launcher/*.jar /opt/website-downloads/

# Set file permissions within the Website downloads folder
chmod +x /opt/website-downloads/*.jar
# Cache copy and file permissions
yes | cp -a -rf "Client_Base/Cache/." "/opt/website-downloads/"
cd '/opt/website-downloads/' || exit

# Performs md5 hashing of all files in cache and writes to a text file for the launcher to read
find -type f \( -not -name "MD5.SUM" \) -exec md5sum '{}' \; >MD5.SUM
