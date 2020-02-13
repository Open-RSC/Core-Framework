include .env
#---------------------------------------------------------------

# Compiles and runs the game client.
# Note: Docker for Mac has a networking bug that prevents the client from accessing the game server running in a Docker container.
run-client:
	ant -f Client_Base/build.xml compile
	ant -f Client_Base/build.xml runclient

#---------------------------------------------------------------

# Section for Building the Docker image and pushing to DockerHub
build-and-push:
	docker-compose -f docker-compose-game.yml build
	docker push openrsc/openrsc_service:latest

#---------------------------------------------------------------

# OpenRSC-only server container control section
start-game:
	docker pull openrsc/openrsc_service:latest
	docker-compose -f docker-compose-game.yml up -d --no-build

stop-game:
	docker-compose -f docker-compose-game.yml down -v

restart-game:
	docker pull openrsc/openrsc_service:latest
	docker-compose -f docker-compose-game.yml down -v
	docker-compose -f docker-compose-game.yml up -d --no-build

logs-game:
	@docker-compose -f docker-compose-game.yml logs -f

#---------------------------------------------------------------

# MariaDB-only server container control section
start-db:
	docker-compose -f docker-compose-db.yml up -d

stop-db:
	docker-compose -f docker-compose-db.yml down -v

restart-db:
	docker-compose -f docker-compose-db.yml down -v
	docker-compose -f docker-compose-db.yml up -d

logs-db:
	@docker-compose -f docker-compose-db.yml logs -f

#---------------------------------------------------------------

# Compiles the game server, client, and launcher before restarting the game server
compile:
	docker pull openrsc/openrsc_service:latest
	docker exec -i openrsc ant -f server/build.xml compile_core
	docker exec -i openrsc ant -f server/build.xml compile_plugins
	docker exec -i openrsc ant -f Client_Base/build.xml compile
	docker exec -i openrsc ant -f PC_Launcher/build.xml compile
	docker-compose -f docker-compose-game.yml down -v
	docker-compose -f docker-compose-game.yml up -d --no-build

#---------------------------------------------------------------

# Sets a specified username to be in a specified group in a specified database
# Call via "sudo make rank db=cabbage group=0 username=wolf"
rank:
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "USE ${db}; UPDATE openrsc_players SET group_id = '${group}' WHERE openrsc_players.username = '${username}';"

# Creates a database that the user specifies the name of
# Call via "sudo make create db=cabbage"
create:
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "create database ${db};"

# Imports the {db}_game_players.sql and {db}_game_server.sql files to a specified database
# Call via "sudo make import db=cabbage"
import:
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/${db}_game_players.sql
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/${db}_game_server.sql

# Upgrades a database with only the {db}_game_server.sql file, not impacting any player save tables
# Call via "sudo make upgrade db=cabbage"
upgrade:
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/${db}_game_server.sql

# Creates a database export of the specified database and saves to the output directory specified in the .env file.  Good for utilizing as a crontab.
# Call via "sudo make backup db=cabbage"
backup:
	@mkdir -p $(MYSQL_DUMPS_DIR)
	chmod -R 777 $(MYSQL_DUMPS_DIR)
	docker exec mariadb mysqldump -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} --single-transaction --quick --lock-tables=false | sudo zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`-${db}.zip

# Unzips a database backup zip file in the output directory specified in the .env file and then imports it into the specified database as a database restoration from backup method
# Call via "sudo make restore name=20191017-0226-EDT-cabbage.zip db=cabbage"
restore:
	unzip -p $(MYSQL_DUMPS_DIR)/${name} | sudo docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db}

# Deletes database backup zip files odler than the number of days specified. Good for utilizing as a crontab.
# Call via "sudo clear-backups days=90"
clear-backups:
	find $(MYSQL_DUMPS_DIR)/*.zip -mtime +${days} -exec rm -f {} \;
