#!/bin/bash
# source => https://docs.docker.com/engine/installation/linux/docker-ce/ubuntu/
export installmode=docker

# Ubuntu Linux Docker installation
  echo ""
  echo "Installing Docker Community Edition"
  echo ""
  sudo apt-get update
  sudo apt-get install -y \
  linux-image-extra-"$(uname -r)" \
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
  sudo setfacl -m user:"$USER:rw" /var/run/docker.sock
  # 4.5  - Ensures Content trust for Docker is Enabled
  echo "DOCKER_CONTENT_TRUST=1" | sudo tee -a /etc/environment
  echo "DOCKER_OPTS='--iptables=false' | sudo tee -a /etc/default/docker"
  # Config to implement changes for 2.1 - 2.15
  sudo mv /tmp/daemon.json /etc/docker/daemon.json
  echo ""
  echo "Setting Docker to have the correct storage driver and restarting the service."
  echo '{
    "storage-driver": "overlay2"
    }' | sudo tee /etc/docker/daemon.json
  sudo chown root:root /etc/docker/daemon.json
  sudo service docker restart
  sudo docker network create nginx-proxy

  # Start Docker and pull containers
  sudo make start
