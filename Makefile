include .env
MYSQL_DUMPS_DIR=./data

go:
	`pwd`/Go-Linux.sh

run:
	`pwd`/scripts/run.sh

run-game:
	`pwd`/scripts/run.sh

run-game-windows:
	cd scripts && call START "" run.cmd

hard-reset:
	`pwd`/scripts/hard-reset.sh

hard-reset-game-windows:
	git reset HEAD --hard

hard-reset-website-windows:
	cd Website && git reset HEAD --hard

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

file-edits:
	`pwd`/scripts/file-edits.sh

start:
	docker-compose up -d

start-single-player:
	docker-compose --file docker-compose-single-player.yml up -d

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

compile-windows-simple:
	ant -f server/build.xml compile_core
	ant -f server/build.xml compile_plugins
	ant -f client/build.xml compile

compile-windows-developer:
	ant -f server/build.xml compile_core
	ant -f server/build.xml compile_plugins
	ant -f client/build.xml compile
	ant -f Launcher/build.xml compile

import-game:
	docker exec -i mysql mysql -uroot -proot < Databases/openrsc_game.sql

import-forum:
	docker exec -i mysql mysql -uroot -proot < Databases/openrsc_forum.sql

import-mysql:
	docker exec -i mysql mysql -uroot -proot < Databases/mysql.sql

import-phpmyadmin:
	docker exec -i mysql mysql -uroot -proot < Databases/phpmyadmin.sql

import-game-windows:
	docker exec -i mysql mysql -u"root" -p"root" < Databases/openrsc_game.sql

import-forum-windows:
	docker exec -i mysql mysql -u"root" -p"root" < Databases/openrsc_forum.sql

clone-website:
	@$(shell sudo rm -rf Website && git clone https://github.com/Open-RSC/Website.git)
	sudo chmod 644 Website/sql/config.inc.php

flush-website-avatars-windows:
	rmdir "Website/avatars"

flush-website-windows:
	rmdir "Website"

clone-website-windows:
	git clone https://github.com/Open-RSC/Website.git

pull-website:
	@cd Website && git pull

pull-website-windows:
	cd Website && git pull

fix-mariadb-permissions-windows:
	icacls.exe etc/mariadb/innodb.cnf /GRANT:R "$($env:USERNAME):(R)"

fix-forum-permissions-windows:
	icacls.exe Website/board/cache /grant Everyone:F /t

logs:
	@docker-compose logs -f

backup:
	@mkdir -p $(MYSQL_DUMPS_DIR)
	sudo chmod -R 777 $(MYSQL_DUMPS_DIR)
	sudo chmod 644 etc/mariadb/innodb.cnf
	docker exec mysql mysqldump --all-databases -u$(dbuser) -p$(pass) --all-databases | sudo zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`.zip

flush-website:
	@$(shell sudo rm -rf Website)
