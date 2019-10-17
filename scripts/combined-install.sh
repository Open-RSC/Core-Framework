#!/bin/bash
RED=$(tput setaf 1)
GREEN=$(tput setaf 2)
NC=$(tput sgr0) # No Color

# Ubuntu or CentOS Linux?
echo "Which Linux distribution are you using?
Note: Raspberry Pi 3B+ needs a manual installation. Don't use this installer as it uses Docker and there is not enough RAM on the Raspberry Pi to use Docker.

Choices:
  ${RED}1${NC} - Ubuntu 18.04 or newer
  ${RED}2${NC} - CentOS 7 or newer
  ${RED}3${NC} - Return to main menu"
echo ""
echo "Type the choice number and press enter."
read -r installmode

if [ "$installmode" == "1" ]; then
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
  sudo apt install openjdk-12-jdk ant -y
  export JAVA_HOME=/usr/lib/jvm/java-12-openjdk-amd64
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
  make docker-install
elif [ "$installmode" == "2" ]; then
  # Needed software
  sudo yum update -y && sudo yum install git wget ant screen -y

  # Firewall configuration
  firewall-cmd --zone=public --add-port=80/tcp --permanent
  firewall-cmd --zone=public --add-port=443/tcp --permanent
  firewall-cmd --zone=public --add-port=43594/tcp --permanent
  firewall-cmd --zone=public --add-port=43595/tcp --permanent
  firewall-cmd --zone=public --add-port=43596/tcp --permanent
  firewall-cmd --zone=public --add-port=43597/tcp --permanent
  firewall-cmd --zone=public --add-port=43598/tcp --permanent
  firewall-cmd --zone=public --add-port=43599/tcp --permanent
  firewall-cmd --zone=public --add-port=55555/tcp --permanent
  firewall-cmd --reload

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

  make docker-install
elif [ "$installmode" == "3" ]; then
  make start
fi
