#!/bin/bash
RED=$(tput setaf 1)
GREEN=$(tput setaf 2)
NC=$(tput sgr0) # No Color

  # Basic software
  echo ""
  echo ""
  echo "Installing Screen, Zip, Fail2Ban, Unzip, Git, Build-Essential, Software-Properties-Common, APT-Transport-HTTPS, CA-Certificates, Curl, and configuring the system timezone."
  sleep 3
  echo ""
  sudo apt update && sudo make apt upgrade -y && sudo apt autoremove -y
  sudo apt install git htop nano unzip zip fail2ban git build-essential apt-transport-https ca-certificates software-properties-common curl screen ack libswt-gtk-4-java gtk3-nocsd -y
  sudo dpkg-reconfigure tzdata

  # Java related
  echo ""
  echo ""
  echo "Installing OpenJDK 13, Gradle, and Apache ant. Please wait."
  sleep 3
  echo ""
  sudo add-apt-repository ppa:cwchien/gradle -y
  sudo apt install openjdk-13-jdk ant gradle -y
  export JAVA_HOME=/usr/lib/jvm/java-13-openjdk-amd64
  export PATH=$PATH:$JAVA_HOME/bin

  # UFW Firewall configuration
  echo ""
  echo ""
  echo "Setting Ubuntu Firewall permissions."
  echo ""
  echo "Allowing TCP ports: 80, 443, 55555, 43594-43599"
  echo "Denying TCP port 3306 (MySQL)"
  sleep 5
  echo ""
  sudo ufw allow 80/tcp && sudo ufw allow 443/tcp && sudo ufw allow 55555/tcp && sudo ufw allow 43594/tcp && sudo ufw allow 43595/tcp && sudo ufw allow 43596/tcp && sudo ufw allow 43597/tcp && sudo ufw allow 43598/tcp && sudo ufw allow 43599/tcp && sudo ufw deny 3306/tcp
  sudo sed -i 's/DEFAULT_FORWARD_POLICY="DENY"/DEFAULT_FORWARD_POLICY="ACCEPT"/g' /etc/default/ufw
  sudo ufw reload
  sudo ufw --force enable

  # Configures and secures SSH access
  echo ""
  echo "Hardening the SSH configuration against adversary activity."
  echo "Note: this is changing SSH port from default 22 to 55555."
  sleep 5
  echo ""
  sudo sed -i 's/#ClientAliveInterval 0/ClientAliveInterval 300/g' /etc/ssh/sshd_config
  sudo sed -i 's/#ClientAliveCountMax 3/ClientAliveCountMax 2/g' /etc/ssh/sshd_config
  sudo sed -i 's/#MaxAuthTries/MaxAuthTries/g' /etc/ssh/sshd_config
  sudo sed -i 's/X11Forwarding yes/X11Forwarding no/g' /etc/ssh/sshd_config
  sudo sed -i 's/#Port 22/Port 55555/g' /etc/ssh/sshd_config

  echo ""
  echo "It is time to create a new user, other than root, if one has not been created already."
  echo "Please state a username that should have SSH sudo access. To skip, type 'skip' and press enter."
  echo ""
  read -r user
  if [ "$user" == "skip" ]; then
    echo "Skipping..."
  elif [ "$compiling" != "skip" ]; then
    sudo useradd $user
    sudo usermod -aG sudo $user
  fi

  #echo ""
  #echo ""
  #sleep 3
  #echo ""
  #sudo sed -i 's/#PubkeyAuthentication/PubkeyAuthentication/g' /etc/ssh/sshd_config
  #sudo sed -i 's/#AuthorizedKeysFile/AuthorizedKeysFile/g' /etc/ssh/sshd_config
  #sudo sed -i 's/PermitRootLogin yes/PermitRootLogin no/g' /etc/ssh/sshd_config
  sudo service ssh restart
  systemctl restart sshd

  #Configures Fail2Ban for SSH access
  echo ""
  echo "Configuring Fail2Ban for SSH protection."
  sleep 3
  echo ""
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

  # Configures Google Authenticator MFA for SSH
  echo ""
  echo "Installing Google authenticator multifactor authentication for SSH"
  echo ""
  sleep 3
  sudo apt install libpam-google-authenticator -y
  echo ""
  echo "Starting Google authenticator MFA set up. You will need a smartphone for the QR code."
  echo "It is most secure to answer y to each prompt."
  echo ""
  google-authenticator
  sudo echo -e "auth required pam_google_authenticator.so" | sudo tee -a /etc/pam.d/sshd

  make docker-install
