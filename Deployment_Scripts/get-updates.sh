#!/bin/bash

RED=$(tput setaf 1)
NC=$(tput sgr0) # No Color

exec 0</dev/tty
source .env

clear

# Permissions
echo ""
echo "Verifying file and user permissions are set correctly"
echo ""
sudo chmod -R 777 .

echo ""
echo "Compiling all code now."
echo ""
echo ""
ant -f server/build.xml compile_core
ant -f server/build.xml compile_plugins
ant -f Client_Base/build.xml compile
ant -f PC_Launcher/build.xml compile

# Export the Android SDK path 
export ANDROID_SDK_ROOT=/usr/lib/android-sdk
export PATH=$ANDROID_SDK_ROOT/cmdline-tools/tools/bin:$PATH

ANDROID_COMPILE_SDK="29"
ANDROID_BUILD_TOOLS="29.0.3"
ANDROID_SDK_TOOLS="6200805"

echo y | sdkmanager --version &>/dev/null
echo y | sdkmanager --update &>/dev/null
yes | sdkmanager --sdk_root=$ANDROID_SDK_ROOT --licenses &>/dev/null
echo y | sdkmanager --sdk_root=$ANDROID_SDK_ROOT "platforms;android-$ANDROID_COMPILE_SDK" &>/dev/null
echo y | sdkmanager --sdk_root=$ANDROID_SDK_ROOT "platform-tools" &>/dev/null
echo y | sdkmanager --sdk_root=$ANDROID_SDK_ROOT "build-tools;$ANDROID_BUILD_TOOLS" &>/dev/null
export PATH=$PATH:$ANDROID_SDK_ROOT/platform-tools/

# Gradle compile Android client
gradle -b Android_Client/Open\ RSC\ Android\ Client/build.gradle assembleDebug

# Launcher
echo ""
echo "Copy and md5sum compiled client and cache files to the Website downloads folder?

Choices:
  ${RED}1${NC} - No
  ${RED}2${NC} - Yes (prod)
  ${RED}3${NC} - Yes (dev)"
echo ""
echo "Type the choice number and press enter."
echo ""
read -r compiling

if [ "$compiling" == "1" ]; then
    echo ""
elif [ "$compiling" == "2" ]; then
    # PC Client
    yes | sudo cp -f Client_Base/*.jar ../Website/site/public/downloads/

    # Android client
    yes | sudo find . -type f -path '*Android_Client*/*Open RSC Android Client*/*' -name \*.apk | xargs -I {} sudo cp "{}" ../Website/site/public/downloads/
    ##yes | sudo cp -f Android_Client/Open\ RSC\ Android\ Client/*.apk ../Website/site/public/downloads/

    # Launcher
    yes | sudo cp -rf PC_Launcher/*.jar ../Website/site/public/downloads/

    # Set file permissions within the Website downloads folder
    sudo chmod +x ../Website/site/public/downloads/*.jar
    sudo chmod +x ../Website/site/public/downloads/*.jar
    sudo chmod +x ../Website/site/public/downloads/*.apk
    sudo chmod -R 777 ../Website/site/public/downloads

    # Cache copy and file permissions
    sudo chmod 777 -R 'Client_Base/Cache'                                                       # Normal cache related files
    yes | sudo cp -a -rf "Client_Base/Cache/." "../Website/site/public/downloads/"                          # Normal cache related files

    cd '../Website/site/public/downloads/' || exit
    find -type f \( -not -name "MD5.SUM" \) -exec md5sum '{}' \; >MD5.SUM # Performs md5 hashing of all files in cache and writes to a text file for the launcher to read
    cd '../../../../Game' || exit
elif [ "$compiling" == "3" ]; then
    # PC Client
	sudo mv Client_Base/*.jar Client_Base/Open_RSC_Client_dev.jar
    yes | sudo cp -f Client_Base/Open_RSC_Client_dev.jar ../Website/site/public/downloads/

    # Android client
    yes | sudo find . -type f -path '*Android_Client*/*Open RSC Android Client*/*' -name \*.apk | xargs -I {} sudo cp "{}" ../Website/site/public/downloads/
    ##yes | sudo cp -f Android_Client/Open\ RSC\ Android\ Client/*.apk ../Website/site/public/downloads/

    # Launcher
    #yes | sudo cp -rf PC_Launcher/*.jar ../Website/site/public/downloads/

    # Set file permissions within the Website downloads folder
    sudo chmod +x ../Website/site/public/downloads/*.jar
    sudo chmod +x ../Website/site/public/downloads/*.jar
    sudo chmod +x ../Website/site/public/downloads/*.apk
    sudo chmod -R 777 ../Website/site/public/downloads

    # Cache copy and file permissions
    #sudo chmod 777 -R 'Client_Base/Cache'                                                       # Normal cache related files
    #yes | sudo cp -a -rf "Client_Base/Cache/." "../Website/site/public/downloads/"                          # Normal cache related files

    cd '../Website/site/public/downloads/' || exit
    find -type f \( -not -name "MD5.SUM" \) -exec md5sum '{}' \; >MD5.SUM # Performs md5 hashing of all files in cache and writes to a text file for the launcher to read
    cd '../../../../Game' || exit
fi

clear

# Finished
echo ""
echo "What would you like to do?

Choices:
  ${RED}1${NC} - Run game server
  ${RED}2${NC} - Return to the main menu"
echo ""
echo "Type the choice number and press enter."
echo ""
read -r finished

if [ "$finished" == "1" ]; then
    make run-server
elif [ "$finished" == "2" ]; then
    make start-linux
fi
