#!/bin/bash

sudo apt update
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

## TODO: lock the audit configuration to prevent any modification of this file.
## If you want to be able to modify the audit rules again after locking you will have to reboot for changes to take place
# -e 2
