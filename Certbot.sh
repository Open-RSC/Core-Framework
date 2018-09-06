#!/bin/bash
exec 0</dev/tty

rm installer.log
touch certbot.log && chmod 777 certbot.log | tee certbot.log &>/dev/null

clear
echo "Please enter your server's public domain name."
read -s publicdomain

clear
echo "Please enter your server's private domain name if one exists or re-enter the public domain name again."
read -s privatedomain

clear
echo "Please enter your email address for Lets Encrypt HTTPS registration."
read -s email

sudo docker stop nginx | tee -a certbot.log &>/dev/null
sudo mv etc/nginx/default.conf etc/nginx/default.conf.BAK | tee -a certbot.log &>/dev/null
sudo mv etc/nginx/HTTPS_default.conf.BAK etc/nginx/default.conf | tee -a certbot.log &>/dev/null
sudo sed -i 's/live\/openrsc.com/live\/'"$publicdomain"'/g' etc/nginx/default.conf | tee -a certbot.log &>/dev/null

clear
echo "Enabling HTTPS"

sudo certbot certonly \
--standalone \
--preferred-challenges http \
--agree-tos -n \
--config-dir ./etc/letsencrypt \
-d $publicdomain -d $privatedomain --expand \
-m $email | tee -a certbot.log &>/dev/null

sudo docker start nginx | tee -a certbot.log &>/dev/null

clear
echo "Done!"
