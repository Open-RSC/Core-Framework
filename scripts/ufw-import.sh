unzip user.rules.zip
unzip user6.rules.zip
sudo cp user.rules /etc/ufw/user.rules
sudo cp user6.rules /etc/ufw/user6.rules
sudo chown root user.rules
sudo chown root user6.rules sudo ufw reload