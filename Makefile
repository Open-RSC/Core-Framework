include .env
#---------------------------------------------------------------

# Section utilized by various shell scripts within Deployment_Scripts
start-linux:
	`pwd`/Start-Linux.sh

run-server:
	`pwd`/Deployment_Scripts/run.sh

run-client:
	ant -f Client_Base/build.xml runclient

combined-install:
	`pwd`/Deployment_Scripts/combined-install.sh

docker-install:
	`pwd`/Deployment_Scripts/docker-install.sh

get-updates:
	`pwd`/Deployment_Scripts/get-updates.sh

compile:
	`pwd`/Deployment_Scripts/get-updates.sh

#---------------------------------------------------------------

# MariaDB-only server container control section
start:
	docker-compose -f docker-compose.yml up -d

stop:
	docker-compose -f docker-compose.yml down -v

restart:
	docker-compose -f docker-compose.yml down -v
	docker-compose -f docker-compose.yml up -d

logs:
	docker-compose -f docker-compose.yml logs -f

#---------------------------------------------------------------

# Sets a specified username to be in a specified group in a specified database
# Call via "make rank db=cabbage group=0 username=wolf"
rank:
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "USE ${db}; UPDATE ${PREFIX}players SET group_id = '${group}' WHERE ${PREFIX}players.username = '${username}';"

# Creates a database that the user specifies the name of
# Call via "make create db=cabbage"
create:
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "create database ${db};"

# Imports the {db}_game_players.sql and {db}_game_server.sql files to a specified database
# Call via "make import db=cabbage"
import:
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/${db}_game_players.sql
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/${db}_game_server.sql

# Upgrades a database with only the {db}_game_server.sql file, not impacting any player save tables
# Call via "make upgrade db=cabbage"
upgrade:
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/${db}_game_server.sql

# Creates a database export of the specified database and saves to the output directory specified in the .env file.  Good for utilizing as a crontab.
# Call via "make backup db=cabbage"
backup:
	mkdir -p $(MYSQL_DUMPS_DIR)
	chmod -R 777 $(MYSQL_DUMPS_DIR)
	docker exec mariadb mysqldump -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} --single-transaction --quick --lock-tables=false | zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`-${db}.zip

# Unzips a database backup zip file in the output directory specified in the .env file and then imports it into the specified database as a database restoration from backup method
# Call via "make restore name=20191017-0226-EDT-cabbage.zip db=cabbage"
restore:
	unzip -p $(MYSQL_DUMPS_DIR)/${name} | docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db}

# Deletes database backup zip files odler than the number of days specified. Good for utilizing as a crontab.
# Call via "clear-backups days=90"
clear-backups:
	find $(MYSQL_DUMPS_DIR)/*.zip -mtime +${days} -exec rm -f {} \;
