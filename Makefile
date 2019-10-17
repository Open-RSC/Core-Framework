include .env
MYSQL_DUMPS_DIR=./Backups

start:
	`pwd`/Start-Linux.sh

run-game:
	`pwd`/scripts/run.sh

combined-install:
	`pwd`/scripts/combined-install.sh

docker-install:
	`pwd`/scripts/docker-install.sh

get-updates:
	`pwd`/scripts/get-updates.sh

start:
	sudo docker-compose up -d

stop:
	sudo docker-compose down -v

restart:
	sudo docker-compose down -v
	sudo docker-compose up -d

ps:
	docker-compose ps

logs:
	@docker-compose logs -f

compile:
	sudo ant -f server/build.xml compile_core
	sudo ant -f server/build.xml compile_plugins
	sudo ant -f client/build.xml compile
	sudo ant -f Launcher/build.xml compile

# Call via "make create db=cabbage"
create:
	sudo docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "create database ${db};"

# Call via "make import db=cabbage"
import:
	sudo docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/${db}_game_server.sql
	sudo docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/${db}_game_players.sql

# Call via "make upgrade db=cabbage"
upgrade:
	sudo docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/${db}_game_server.sql

# Call via "make backup db=cabbage"
backup:
	@mkdir -p $(MYSQL_DUMPS_DIR)
	sudo chmod -R 777 $(MYSQL_DUMPS_DIR)
	sudo docker exec mysql mysqldump -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} --single-transaction --quick --lock-tables=false | sudo zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`-${db}.zip

# Call via clear-backups days=90
clear-backups:
	sudo find $(MYSQL_DUMPS_DIR)/*.zip -mtime +${days} -exec rm -f {} \;

# Call via "sudo make rank db=cabbage group=0 username=wolf"
rank:
	sudo docker exec -i mysql mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "USE ${db}; UPDATE openrsc_players SET group_id = '${group}' WHERE openrsc_players.username = '${username}';"
