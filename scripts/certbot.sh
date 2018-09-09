#!/bin/bash
exec 0</dev/tty

clear
echo "Please enter your server's public domain name."
echo ""
read -s publicdomain

clear
echo "Please enter your server's private domain name if one exists or re-enter the public domain name again."
echo ""
read -s privatedomain

clear
echo "Please enter your email address for Lets Encrypt HTTPS registration."
echo ""
read -s email

echo ""
echo ""
sudo docker stop nginx
sudo mv etc/nginx/default.conf etc/nginx/default.conf.BAK
sudo mv etc/nginx/HTTPS_default.conf.BAK etc/nginx/default.conf
sudo sed -i 's/live\/openrsc.com/live\/'"$publicdomain"'/g' etc/nginx/default.conf

echo ""
echo ""
echo "Enabling HTTPS"
echo ""
sudo certbot certonly \
--standalone \
--preferred-challenges http \
--agree-tos -n \
--config-dir ./etc/letsencrypt \
-d $publicdomain -d $privatedomain --expand \
-m $email

sudo docker start nginx

echo ""
echo "Done!"
echo ""
