include .env
#---------------------------------------------------------------

# Section utilized by various shell scripts within Deployment_Scripts
start-linux:
	sudo `pwd`/Start-Linux.sh

run-server:
	sudo `pwd`/Deployment_Scripts/run.sh

run-client:
	sudo ant -f Client_Base/build.xml runclient

combined-install:
	sudo `pwd`/Deployment_Scripts/combined-install.sh

docker-install:
	sudo `pwd`/Deployment_Scripts/docker-install.sh

get-updates:
	sudo `pwd`/Deployment_Scripts/get-updates.sh

#---------------------------------------------------------------

# Section for Building the Docker image and pushing to DockerHub
build-and-push:
	sudo docker-compose -f docker-compose-game.yml build
	sudo docker push openrsc/openrsc_service:latest

#---------------------------------------------------------------

# OpenRSC-only server container control section
start-game:
	sudo docker-compose -f docker-compose-game.yml up -d

stop-game:
	sudo docker-compose -f docker-compose-game.yml down -v

restart-game:
	sudo docker-compose -f docker-compose-game.yml down -v
	sudo docker-compose -f docker-compose-game.yml up -d

logs-game:
	sudo docker-compose -f docker-compose-game.yml logs -f

#---------------------------------------------------------------

# MariaDB-only server container control section
start-db:
	sudo docker-compose -f docker-compose-db.yml up -d

stop-db:
	sudo docker-compose -f docker-compose-db.yml down -v

restart-db:
	sudo docker-compose -f docker-compose-db.yml down -v
	sudo docker-compose -f docker-compose-db.yml up -d

logs-db:
	sudo docker-compose -f docker-compose-db.yml logs -f

#---------------------------------------------------------------

# Compiles the game server, client, and launcher before restarting the game server
# Note: Mac Terminal has an error and will not complete the copy-files.sh script command, this only works on Linux.
compile:
	sudo docker-compose -f docker-compose-game.yml down -v
	sudo docker-compose -f docker-compose-compile.yml up
	sudo docker-compose -f docker-compose-compile.yml down -v
	sudo chmod +x Deployment_Scripts/copy-files.sh
	sudo ./Deployment_Scripts/copy-files.sh
	sudo docker-compose -f docker-compose-game.yml down -v
	sudo docker-compose -f docker-compose-game.yml up -d

#---------------------------------------------------------------

# Sets a specified username to be in a specified group in a specified database
# Call via "make rank db=cabbage group=0 username=wolf"
rank:
	sudo docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "USE ${db}; UPDATE openrsc_players SET group_id = '${group}' WHERE openrsc_players.username = '${username}';"

# Creates a database that the user specifies the name of
# Call via "make create db=cabbage"
create:
	sudo docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "create database ${db};"

# Imports the {db}_game_players.sql and {db}_game_server.sql files to a specified database
# Call via "make import db=cabbage"
import:
	sudo docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/${db}_game_players.sql
	sudo docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/${db}_game_server.sql

# Upgrades a database with only the {db}_game_server.sql file, not impacting any player save tables
# Call via "make upgrade db=cabbage"
upgrade:
	sudo docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/${db}_game_server.sql

# Creates a database export of the specified database and saves to the output directory specified in the .env file.  Good for utilizing as a crontab.
# Call via "make backup db=cabbage"
backup:
	sudo mkdir -p $(MYSQL_DUMPS_DIR)
	sudo chmod -R 777 $(MYSQL_DUMPS_DIR)
	sudo docker exec mariadb mysqldump -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} --single-transaction --quick --lock-tables=false | sudo zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`-${db}.zip

# Unzips a database backup zip file in the output directory specified in the .env file and then imports it into the specified database as a database restoration from backup method
# Call via "make restore name=20191017-0226-EDT-cabbage.zip db=cabbage"
restore:
	sudo unzip -p $(MYSQL_DUMPS_DIR)/${name} | sudo docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db}

# Deletes database backup zip files odler than the number of days specified. Good for utilizing as a crontab.
# Call via "clear-backups days=90"
clear-backups:
	sudo find $(MYSQL_DUMPS_DIR)/*.zip -mtime +${days} -exec rm -f {} \;
