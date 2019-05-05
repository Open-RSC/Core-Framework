#!/bin/bash
# source => https://docs.docker.com/engine/installation/linux/docker-ce/ubuntu/
RED=`tput setaf 1`
GREEN=`tput setaf 2`
NC=`tput sgr0` # No Color
export installmode=docker

# Ubuntu Linux Docker installation
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    echo ""
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
    sudo apt install docker docker-compose -y
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
echo "Waiting 10 seconds then importing the databases."
echo ""
sleep 10
sudo chmod 644 etc/mariadb/innodb.cnf
sudo create-database-openrsc
sudo create-database-cabbage
sudo make import-openrsc
sudo make import-cabbage

sudo make clone-website