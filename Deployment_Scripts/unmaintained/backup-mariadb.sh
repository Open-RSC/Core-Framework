#!/bin/bash
#Backup a MariaDB database with a specific name and folder location then check that the output file is large enough.
#Here we pass in params like the database name and MySQL backup folder from the Makefile.
db=$2
mkdir -p $1
chmod -R 777 $1
filename=$(date "+%Y%m%d-%H%M-%Z")-$db.zip
email="openrsc.emailer@gmail.com";
discordwebhook=$(cat Deployment_Scripts/.discordstaffmonitorwebhook); #We are in the root core directory, not Deployment_Scripts, so we must specify Deployment_Scripts.
docker exec mariadb mysqldump -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} $db --single-transaction --quick --lock-tables=false | zip > $1/$filename
size=$(stat --printf="%s" $1/$filename);
if [[ $size -lt 100000 && $db != "laravel" ]]; then
    warning="Warning: OpenRSC $db DB backup file $filename is too small. Size is: $size";
    mail -s "OpenRSC $db DB backup size warning" $email <<< "$warning";
    curl -H "Content-Type: application/json" \
        -X POST \
        -d "{\"content\": \"$warning\"}" $discordwebhook 
fi