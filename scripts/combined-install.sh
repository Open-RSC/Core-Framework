#!/bin/bash
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0` # No Color

# Docker or native install mode?
echo ""
echo "${RED}Open RSC:${NC}
An easy to use RSC private server framework.

Which method of installation do you wish to use?

Choices:
  ${RED}1${NC} - Use Docker virtual containers (recommended)
  ${RED}2${NC} - Direct installation (Ubuntu Linux only)
  ${RED}3${NC} - Return to main menu"
echo ""
echo "Which of the above do you wish to do? Type the choice number and press enter."
read installmode

if [[ "$installmode" != "3" ]]; then # Follows this if choices 1(Docker) or 2(Direct) have been picked)

    # Ubuntu Linux
    if [[ "$OSTYPE" == "linux-gnu"* ]]; then

        # Basic software
        echo ""
        echo ""
        echo "Installing Certbot, Screen, Zip, Fail2Ban, Unzip, Git, Build-Essential, Software-Properties-Common, APT-Transport-HTTPS, CA-Certificates, Curl, and configuring the system timezone."
        echo ""
        sudo apt-get update && sudo apt-get upgrade -y && sudo apt-get autoremove -y
        sudo apt-get install software-properties-common -y
        sudo add-apt-repository ppa:certbot/certbot -y
        sudo apt-get update
        sudo apt-get install certbot screen zip fail2ban unzip git build-essential apt-transport-https ca-certificates curl -y
        sudo dpkg-reconfigure tzdata

        # Java related
        echo ""
        echo ""
        echo "Installing Oracle Java JDK 8, OpenJFX, and Apache ant. Please wait."
        echo ""
        sudo apt-get remove -y openjdk-6-jre default-jre default-jre-headless
        sudo add-apt-repository -y ppa:webupd8team/java
        sudo apt update
        sudo apt install -y openjfx ant
        echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | sudo debconf-set-selections
        sudo apt-get install -y oracle-java8-installer
        sudo apt install oracle-java8-set-default

        # UFW Firewall configuration
        echo ""
        echo ""
        echo "Setting Ubuntu Firewall permissions."
        echo ""
        sudo ufw allow 22/tcp && sudo ufw allow 80/tcp && sudo ufw allow 443/tcp && sudo ufw allow 55555/tcp && sudo ufw allow 53595/tcp && sudo ufw deny 3306/tcp
        sudo sed -i 's/DEFAULT_FORWARD_POLICY="DENY"/DEFAULT_FORWARD_POLICY="ACCEPT"/g' /etc/default/ufw
        sudo ufw reload
        sudo ufw --force enable

        # Configures and secures SSH access
        sudo sed -i 's/#ClientAliveInterval 0/ClientAliveInterval 720/g' /etc/ssh/sshd_config
        sudo sed -i 's/#ClientAliveCountMax 3/ClientAliveCountMax 720/g' /etc/ssh/sshd_config
        sudo sed -i 's/#MaxAuthTries/MaxAuthTries/g' /etc/ssh/sshd_config
        sudo service ssh restart

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
fi

if [ "$installmode" == "1" ]; then
    make docker-install
elif [ "$installmode" == "2" ]; then
    make direct-install
elif [ "$install" == "3" ]; then
    make go
fi
