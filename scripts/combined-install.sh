#!/bin/bash
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0` # No Color

# Ubuntu Linux
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Basics
    echo ""
    echo ""
    echo "Installing certbot, screen, zip, fail2ban, unzip, git, build-essential, software-properties-common, apt-transport-https, ca-certificates, curl, and setting the system timezone."
    echo ""
    sudo apt-get update && sudo apt-get upgrade -y && sudo apt-get autoremove -y
    sudo apt-get install software-properties-common -y
    sudo add-apt-repository ppa:certbot/certbot -y
    sudo apt-get update
    sudo apt-get install certbot screen zip fail2ban unzip git build-essential apt-transport-https ca-certificates curl -y
    sudo dpkg-reconfigure tzdata

    # Java
    echo ""
    echo ""
    echo "Installing Oracle Java JDK 8, openjfx, and Apache ant. Please wait."
    echo ""
    sudo apt-get remove -y openjdk-6-jre default-jre default-jre-headless
    sudo add-apt-repository -y ppa:webupd8team/java
    sudo apt update
    sudo apt install -y openjfx ant
    echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | sudo debconf-set-selections
    sudo apt-get install -y oracle-java8-installer
    sudo apt install oracle-java8-set-default

    # UFW Firewall
    echo ""
    echo ""
    echo "Setting Ubuntu Firewall permissions."
    echo ""
    sudo ufw allow 22/tcp && sudo ufw allow 80/tcp && sudo ufw allow 8080/tcp && sudo ufw allow 443/tcp && sudo ufw allow 55555/tcp && sudo ufw allow 53595/tcp && sudo ufw deny 3306/tcp
    sudo sed -i 's/DEFAULT_FORWARD_POLICY="DENY"/DEFAULT_FORWARD_POLICY="ACCEPT"/g' /etc/default/ufw
    sudo ufw reload
    sudo ufw --force enable


# Apple MacOS
elif [[ "$OSTYPE" == "darwin"* ]]; then
    echo "Apple MacOS detected. Performing needed actions to make this script work properly."
    which -s brew
    if [[ $? != 0 ]] ; then
        # Install Homebrew
        ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
        continue
    fi
    brew tap AdoptOpenJDK/openjdk && brew install gnu-sed git newt unzip wget git curl zip screen adoptopenjdk-openjdk8 ant openjfx
    PATH="/usr/local/opt/gnu-sed/libexec/gnubin:$PATH"
fi


# Installation selection
echo ""
echo "${RED}Open RSC Installer:${NC}
An easy to use RSC private server framework.

Which method of installation do you wish to use?

Choices:
  ${RED}1${NC} - Use Docker virtual containers (recommended)
  ${RED}2${NC} - Direct installation (Ubuntu Linux only)
  ${RED}3${NC} - Return to main menu"
echo ""
echo "Which of the above do you wish to do? Type the choice number and press enter."
echo ""
read install

if [ "$install" == "1" ]; then
    make docker-install
elif [ "$install" == "2" ]; then
    make direct-install
elif [ "$install" == "3" ]; then
    make go
fi
