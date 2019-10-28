include .env
MYSQL_DUMPS_DIR=./Backups

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

start:
	docker-compose up -d

stop:
	docker-compose down -v

restart:
	docker-compose down -v
	docker-compose up -d

ps:
	docker-compose ps

logs:
	@docker-compose logs -f

compile:
	ant -f server/build.xml compile_core
	ant -f server/build.xml compile_plugins
	ant -f Client_Base/build.xml compile
	ant -f PC_Launcher/build.xml compile

# Call via "sudo make create db=cabbage"
create:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "create database ${db};"

# Call via "sudo make import db=cabbage"
import:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/${db}_game_server.sql
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/${db}_game_players.sql

# Call via "sudo make upgrade db=cabbage"
upgrade:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/${db}_game_server.sql

# Call via "sudo make backup db=cabbage"
backup:
	@mkdir -p $(MYSQL_DUMPS_DIR)
	chmod -R 777 $(MYSQL_DUMPS_DIR)
	docker exec mysql mysqldump -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} --single-transaction --quick --lock-tables=false | sudo zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`-${db}.zip

# Call via "sudo make restore name=20191017-0226-EDT-cabbage.zip db=cabbage"
restore:
	unzip -p Backups/${name} | sudo docker exec -i mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db}

# Call via "sudo clear-backups days=90"
clear-backups:
	find $(MYSQL_DUMPS_DIR)/*.zip -mtime +${days} -exec rm -f {} \;

# Call via "sudo make rank db=cabbage group=0 username=wolf"
rank:
	docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "USE ${db}; UPDATE openrsc_players SET group_id = '${group}' WHERE openrsc_players.username = '${username}';"
