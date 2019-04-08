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
  ${RED}1${NC} - Docker containers
  ${RED}2${NC} - Directly installed
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
        sudo apt update && sudo make apt upgrade -y && sudo apt autoremove -y
        sudo apt install git htop nano unzip zip fail2ban git build-essential apt-transport-https ca-certificates software-properties-common curl screen ack certbot mariadb-client libswt-gtk-4-java gtk3-nocsd openssh-server -y
        sudo dpkg-reconfigure tzdata

        # Java related
        echo ""
        echo ""
        echo "Installing OpenJDK and Apache ant. Please wait."
        echo ""
        sudo apt install openjdk-11-jdk ant -y
        export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
        source ~/.bashrc
        source /etc/profile

        # UFW Firewall configuration
        echo ""
        echo ""
        echo "Setting Ubuntu Firewall permissions."
        echo ""
        sudo ufw allow 80/tcp && sudo ufw allow 443/tcp && sudo ufw allow 55555/tcp && sudo ufw allow 43594/tcp && sudo ufw deny 3306/tcp
        sudo sed -i 's/DEFAULT_FORWARD_POLICY="DENY"/DEFAULT_FORWARD_POLICY="ACCEPT"/g' /etc/default/ufw

        sudo ufw reload
        sudo ufw --force enable

        # Configures and secures SSH access
        sudo sed -i 's/#ClientAliveInterval 0/ClientAliveInterval 720/g' /etc/ssh/sshd_config
        sudo sed -i 's/#ClientAliveCountMax 3/ClientAliveCountMax 720/g' /etc/ssh/sshd_config
        sudo sed -i 's/#MaxAuthTries/MaxAuthTries/g' /etc/ssh/sshd_config
        sudo sed -i 's/#PubkeyAuthentication/PubkeyAuthentication/g' /etc/ssh/sshd_config
        sudo sed -i 's/#Port 22/Port 55555/g' /etc/ssh/sshd_config
        sudo service ssh restart
        systemctl restart sshd

        # Configures Fail2Ban
        sudo echo '[sshd]
enabled = true
banaction = iptables-multiport
maxretry = 10
findtime = 43200
bantime = 86400

[sshlongterm]
port      = ssh
logpath   = %(sshd_log)s
banaction = iptables-multiport
maxretry  = 35
findtime  = 259200
bantime   = 608400
enabled   = true
filter    = sshd' | sudo tee /etc/fail2ban/jail.local
      sudo systemctl enable fail2ban
      sudo systemctl restart fail2ban

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
