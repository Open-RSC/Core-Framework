include .env
MYSQL_DUMPS_DIR=./data

go:
	`pwd`/Go.sh

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

certbot:
	`pwd`/scripts/certbot.sh

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
	sudo ant -f Launcher/build.xml jar

compile-windows-simple:
	ant -f server/build.xml compile_core
	ant -f server/build.xml compile_plugins
	ant -f client/build.xml compile

compile-windows-developer:
	ant -f server/build.xml compile_core
	ant -f server/build.xml compile_plugins
	ant -f client/build.xml compile
	ant -f Launcher/build.xml jar

import-game:
	docker exec -i $(shell sudo docker-compose ps -q mysqldb) mysql -u$(dbuser) -p$(pass) < Databases/openrsc_game.sql

import-forum:
	docker exec -i $(shell sudo docker-compose ps -q mysqldb) mysql -u$(dbuser) -p$(pass) < Databases/openrsc_forum.sql

import-game-windows:
	docker exec -i mysql mysql -u"root" -p"root" < Databases/openrsc_game.sql

import-forum-windows:
	docker exec -i mysql mysql -u"root" -p"root" < Databases/openrsc_forum.sql

clone-website:
	@$(shell sudo rm -rf Website && git clone https://github.com/Open-RSC/Website.git)

clone-website-windows:
	rmdir Website /s /Q
	git clone https://github.com/Open-RSC/Website.git

docker-toolbox-forward-windows:
	VBoxManage controlvm "default" natpf1 "http,tcp,,80,,80";
	VBoxManage controlvm "default" natpf1 "https,tcp,,443,,443";
	VBoxManage controlvm "default" natpf1 "mariadb,tcp,,3306,,3306";
	VBoxManage controlvm "default" natpf1 "phpmyadmin,tcp,,55555,,55555";
	VBoxManage controlvm "default" natpf1 "tomcat,tcp,,8080,,8080";

pull-website:
	@cd Website && git pull

pull-website-windows:
	cd Website && git pull

fix-mariadb-permissions-windows:
	 icacls.exe etc/mariadb/innodb.cnf /GRANT:R "$($env:USERNAME):(R)"

logs:
	@docker-compose logs -f

backup:
	@mkdir -p $(MYSQL_DUMPS_DIR)
	sudo chmod -R 777 $(MYSQL_DUMPS_DIR)
	sudo chmod 644 etc/mariadb/innodb.cnf
	docker exec mysql mysqldump --all-databases -u$(dbuser) -p$(pass) --all-databases | sudo zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`.zip

flush-website:
	@$(shell sudo rm -rf Website)
