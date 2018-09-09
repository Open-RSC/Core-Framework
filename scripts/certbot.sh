#!/bin/bash
exec 0</dev/tty

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
-d $domain -d $subdomain --expand \
-m $email

sudo docker start nginx

echo ""
echo "Done!"
echo ""
