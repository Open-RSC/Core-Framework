#!/bin/bash
# source => https://docs.docker.com/engine/installation/linux/docker-ce/ubuntu/
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0` # No Color

# Ubuntu Linux Docker installation
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    echo "Installing Docker Community Edition"
    echo ""
    sudo apt-get update
    sudo apt-get install -y \
      linux-image-extra-$(uname -r) \
      linux-image-extra-virtual
    sudo apt-get update
    sudo apt-get install -y \
      apt-transport-https \
      ca-certificates \
      curl \
      software-properties-common
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
    sudo add-apt-repository \
      "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
      $(lsb_release -cs) \
      stable"
    sudo apt-get update
    sudo apt-get install docker-ce docker-compose -y
    # Adds instance user to docker group so it can execute commands.
    sudo usermod -a -G docker ubuntu
    # Permits instance user to execute Docker commands without sudo
    sudo setfacl -m user:$USER:rw /var/run/docker.sock
    # 4.5  - Ensures Content trust for Docker is Enabled
    echo "DOCKER_CONTENT_TRUST=1" | sudo tee -a /etc/environment
    # Config to implement changes for 2.1 - 2.15
    sudo mv /tmp/daemon.json /etc/docker/daemon.json
    echo ""
    echo "Setting Docker to have the correct storage driver and restarting the service."
    echo '{
    "storage-driver": "devicemapper"
    }' | sudo tee /etc/docker/daemon.json
    sudo chown root:root /etc/docker/daemon.json
    sudo service docker restart

    # Auditd security auditing
    echo "Installing auditd for Docker security auditing"
    echo ""
    sudo apt-get install -y auditd
    # 1.5  - Ensure auditing is configured for the Docker daemon
    echo "-w /usr/bin/docker -p wa" | sudo tee -a /etc/audit/rules.d/audit.rules
    # 1.6  - Ensure auditing is configured for Docker files and directories - /var/lib/docker
    echo "-w /var/lib/docker -p wa" | sudo tee -a /etc/audit/rules.d/audit.rules
    # 1.7  - Ensure auditing is configured for Docker files and directories - /etc/docker"
    echo "-w /etc/docker -p wa" | sudo tee -a /etc/audit/rules.d/audit.rules
    # 1.8  - Ensure auditing is configured for Docker files and directories - docker.service
    echo "-w /lib/systemd/system/docker.service -p wa" | sudo tee -a /etc/audit/rules.d/audit.rules
    # 1.9  - Ensure auditing is configured for Docker files and directories - docker.socket
    echo "-w /lib/systemd/system/docker.socket -p wa" | sudo tee -a /etc/audit/rules.d/audit.rules
    # 1.10 - Ensure auditing is configured for Docker files and directories - /etc/default/docker
    echo "-w /etc/default/docker -p wa" | sudo tee -a /etc/audit/rules.d/audit.rules
    # 1.11 - Ensure auditing is configured for Docker files and directories - /etc/docker/daemon.json
    echo "-w /etc/docker/daemon.json -p wa" | sudo tee -a /etc/audit/rules.d/audit.rules
    # 1.12 - Ensure auditing is configured for Docker files and directories - /usr/bin/docker-containerd
    echo "-w /usr/bin/docker-containerd -p wa" | sudo tee -a /etc/audit/rules.d/audit.rules
    # 1.13 - Ensure auditing is configured for Docker files and directories - /usr/bin/docker-runc
    echo "-w /usr/bin/docker-runc -p wa" | sudo tee -a /etc/audit/rules.d/audit.rules
    sudo service auditd restart

    # Data-store volume (disabled due to repeat install runs causing issues)
    #echo "Creating a Docker data-store volume"
    #echo ""
    #sudo mkfs -t ext4 /dev/xvdf
    #sudo mkdir /mnt/data-store
    #sudo mount /dev/xvdf /mnt/data-store
    #echo "/var/lib/docker /mnt/data-store bind defaults,bind 0 0" | sudo tee -a /etc/fstab
    #echo ""

    # Start Docker and pull containers
    sudo make start


# Apple MacOS Docker installation
elif [[ "$OSTYPE" == "darwin"* ]]; then
    echo "Apple MacOS detected."
    echo ""
    echo "Downloading Docker Community Edition for MacOS."
    echo ""
    wget https://download.docker.com/mac/stable/Docker.dmg
    hdiutil attach Docker.dmg
    echo ""
    echo "Please drag Docker as instructed in the popup."
    echo ""
    echo "Press enter back here when finished to continue."
    read
    echo ""
    open /Applications/Docker.app
    echo ""
    echo "Docker is launching. Please follow the directions that it gives you."
    echo ""
    echo "Press enter back here when finished to continue."
    read
fi


# Database imports
echo ""
echo "Importing the game and forum databases."
echo ""
sudo make import-game
sudo make import-forum
sudo make backup


# Website clone
sudo make clone-website


# Finished
echo ""
echo "${RED}Open RSC Installer:${NC}
An easy to use RSC private server framework.

What would you like to do next?

Choices:
  ${RED}1${NC} - Run Open RSC
  ${RED}2${NC} - Return to the main menu"
echo ""
echo "Which of the above do you wish to do? Type the choice number and press enter."
echo ""
read finished

if [ "$finished" == "1" ]; then
    make run
elif [ "$finished" == "2" ]; then
    make go
fi
