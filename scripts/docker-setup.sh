#!/bin/bash
# source => https://docs.docker.com/engine/installation/linux/docker-ce/ubuntu/

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
sudo apt-get install -y docker-ce

# add instance user to docker group so it can execute commands.
sudo usermod -a -G docker ubuntu

# 4.5  - Ensure Content trust for Docker is Enabled
echo "DOCKER_CONTENT_TRUST=1" | sudo tee -a /etc/environment

# config to implement changes for 2.1 - 2.15
sudo mv /tmp/daemon.json /etc/docker/daemon.json
sudo chown root:root /etc/docker/daemon.json

sudo service docker restart
