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

import-mysql:
	docker exec -i mysql mysql -uroot -proot < Databases/mysql.sql

import-phpmyadmin:
	docker exec -i mysql mysql -uroot -proot < Databases/phpmyadmin.sql

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

backup:
	@mkdir -p $(MYSQL_DUMPS_DIR)
	sudo chmod -R 777 $(MYSQL_DUMPS_DIR)
	sudo chmod 644 etc/mariadb/innodb.cnf
	docker exec mysql mysqldump -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${MARIADB_DATABASE} --single-transaction --quick --lock-tables=false | sudo zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`.zip

update-laravel:
	docker exec -i php bash -c "cd /var/www/html/openrsc_web && composer install && php artisan key:generate"

clear-old-backups:
	sudo find $(MYSQL_DUMPS_DIR)/*.zip -mtime +30 -exec rm -f {} \;
