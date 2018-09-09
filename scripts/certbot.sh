#!/bin/bash
exec 0</dev/tty

domain=$(whiptail --inputbox "Please enter your server's public domain name." 8 50 $domain --title "Open RSC HTTPS Configuration" 3>&1 1>&2 2>&3)
privatedomain=$(whiptail --inputbox "Please enter your server's private domain name if one exists." 8 50 $domain --title "Open RSC HTTPS Configuration" 3>&1 1>&2 2>&3)
email=$(whiptail --inputbox "Please enter your email address for Lets Encrypt HTTPS registration." 8 50 --title "Open RSC HTTPS Configuration" 3>&1 1>&2 2>&3)


echo ""
echo ""
sudo docker stop nginx
sudo mv etc/nginx/default.conf etc/nginx/default.conf.BAK
sudo mv etc/nginx/HTTPS_default.conf.BAK etc/nginx/default.conf
sudo sed -i 's/live\/openrsc.com/live\/'"$domain"'/g' etc/nginx/default.conf

echo ""
echo ""
echo "Enabling HTTPS"
echo ""
sudo certbot certonly \
--standalone \
--preferred-challenges http \
--agree-tos -n \
--config-dir ./etc/letsencrypt \
-d $domain -d $privatedomain --expand \
-m $email

sudo docker start nginx

echo ""
echo "Done!"
echo ""
