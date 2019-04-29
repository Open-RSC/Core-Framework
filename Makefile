include .env
MYSQL_DUMPS_DIR=./data

go:
	`pwd`/Go-Linux.sh

run:
	`pwd`/scripts/run.sh

run-game:
	`pwd`/scripts/run.sh

hard-reset:
	`pwd`/scripts/hard-reset.sh

certbot-native:
	`pwd`/scripts/certbot-native.sh

certbot-docker:
	`pwd`/scripts/certbot-docker.sh

rank:
	`pwd`/scripts/rank.sh

combined-install:
	`pwd`/scripts/combined-install.sh

direct-install:
	`pwd`/scripts/direct-install.sh

docker-install:
	`pwd`/scripts/docker-install.sh

get-updates:
	`pwd`/scripts/get-updates.sh

single-player:
	`pwd`/scripts/single-player.sh

start:
	docker-compose up -d

stop:
	@docker-compose down -v

restart:
	@docker-compose down -v
	docker-compose up -d

ps:
	docker-compose ps

compile:
	sudo ant -f server/build.xml compile_core
	sudo ant -f server/build.xml compile_plugins
	sudo ant -f client/build.xml compile
	sudo ant -f Launcher/build.xml compile

import-game:
	docker exec -i mysql mysql -uroot -proot < Databases/openrsc_game_server.sql
	docker exec -i mysql mysql -uroot -proot < Databases/openrsc_game_players.sql

clone-website:
	@$(shell sudo rm -rf Website && git clone https://gitlab.openrsc.com/open-rsc/Website.git)
	sudo chmod 644 Website/sql/config.inc.php

flush-website-avatars-windows:
	rmdir "Website/avatars"

pull-website:
	@cd Website && git pull

fix-mariadb-permissions-windows:
	icacls.exe etc/mariadb/innodb.cnf /GRANT:R "$($env:USERNAME):(R)"

logs:
	@docker-compose logs -f

backup-openrsc:
	@mkdir -p $(MYSQL_DUMPS_DIR)
	sudo chmod -R 777 $(MYSQL_DUMPS_DIR)
	sudo chmod 644 etc/mariadb/innodb.cnf
	docker exec mysql mysqldump -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} openrsc --single-transaction --quick --lock-tables=false | sudo zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`-openrsc.zip

backup-cabbage:
	@mkdir -p $(MYSQL_DUMPS_DIR)
	sudo chmod -R 777 $(MYSQL_DUMPS_DIR)
	sudo chmod 644 etc/mariadb/innodb.cnf
	docker exec mysql mysqldump -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} cabbage --single-transaction --quick --lock-tables=false | sudo zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`-cabbage.zip

backup-preservation:
	@mkdir -p $(MYSQL_DUMPS_DIR)
	sudo chmod -R 777 $(MYSQL_DUMPS_DIR)
	sudo chmod 644 etc/mariadb/innodb.cnf
	docker exec mysql mysqldump -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} preservation --single-transaction --quick --lock-tables=false | sudo zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`-preservation.zip

backup-openpk:
	@mkdir -p $(MYSQL_DUMPS_DIR)
	sudo chmod -R 777 $(MYSQL_DUMPS_DIR)
	sudo chmod 644 etc/mariadb/innodb.cnf
	docker exec mysql mysqldump -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} openpk --single-transaction --quick --lock-tables=false | sudo zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`-openpk.zip	

backup-wolfkingdom:
	@mkdir -p $(MYSQL_DUMPS_DIR)
	sudo chmod -R 777 $(MYSQL_DUMPS_DIR)
	sudo chmod 644 etc/mariadb/innodb.cnf
	docker exec mysql mysqldump -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} wolfkingdom --single-transaction --quick --lock-tables=false | sudo zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`-wolfkingdom.zip

init-laravel:
	cp Website/openrsc_web/.env.example Website/openrsc_web/.env

update-laravel:
	docker exec -i php bash -c "cd /var/www/html/openrsc_web && composer install && php artisan key:generate && php artisan optimize"

migrate-laravel:
	docker exec -i php bash -c "cd /var/www/html/openrsc_web && php artisan migrate --seed"

make-laravel:
	docker exec -i php bash -c "cd /var/www/html/openrsc_web && php artisan make:controller MyController"

list-route:
	docker exec -i php bash -c "cd /var/www/html/openrsc_web && php artisan route:list"

clear-views:
	docker exec -i php bash -c "cd /var/www/html/openrsc_web && php artisan view:clear"

clear-route:
	docker exec -i php bash -c "cd /var/www/html/openrsc_web && php artisan route:clear"

migrate:
	docker exec -i php bash -c "cd /var/www/html/openrsc_web && php artisan migrate"

migrate-refresh:
	docker exec -i php bash -c "cd /var/www/html/openrsc_web && php artisan migrate:refresh"

clear-config:
	docker exec -i php bash -c "cd /var/www/html/openrsc_web && php artisan config:cache"

publish-pagination:
	docker exec -i php bash -c "cd /var/www/html/openrsc_web && php artisan vendor:publish --tag=laravel-pagination"

version:
	docker exec -i php bash -c "cd /var/www/html/openrsc_web && php artisan --version"

clear-old-backups:
	sudo find $(MYSQL_DUMPS_DIR)/*.zip -mtime +7 -exec rm -f {} \;

