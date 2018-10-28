#!/bin/bash
exec 0</dev/tty

export domain=$(whiptail --inputbox "Please enter your server's domain name. (No http:// or www. needed)" 8 50 ${domain} --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
export subdomain=$(whiptail --inputbox "Please set your server's private subdomain if one exists or press enter." 8 50 ${domain} --title "Open RSC Configuration" 3>&1 1>&2 2>&3)
export email=$(whiptail --inputbox "Please enter your email for Lets Encrypt registration" 8 50 ${email} --title "Open RSC Configuration" 3>&1 1>&2 2>&3)

echo ""
echo ""

sudo mv etc/nginx/HTTPS_native.conf.BAK /etc/nginx/sites-available/default
sudo sed -i 's/live\/openrsc.com/live\/'"$domain"'/g' /etc/nginx/sites-available/default

echo ""
echo ""
echo "Enabling HTTPS"
echo ""
sudo certbot certonly \
--standalone \
--preferred-challenges http \
--agree-tos -n \
--config-dir /etc/letsencrypt \
--pre-hook 'sudo service nginx stop' \
--post-hook 'sudo service nginx start' \
-d ${domain} -d ${subdomain} --expand \
-m ${email}
#--force-renewal
echo ""
echo "Done!"
echo ""
