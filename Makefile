include .env

MYSQL_DUMPS_DIR=./data

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
	ant -f server/build.xml compile_core
	ant -f server/build.xml compile_plugins
	ant -f client/build.xml compile
	ant -f Launcher/build.xml jar

import-game:
	@docker exec -i $(shell sudo docker-compose ps -q mysqldb) mysql -u"$(MARIADB_ROOT_USER)" -p"$(MARIADB_ROOT_PASSWORD)" < Databases/openrsc_game.sql 2>/dev/null

run-game:
	ant -f server/build.xml runservermembers

clone-website:
	@$(shell git clone -b 2.0.0 https://github.com/Open-RSC/Website.git)

pull-website:
	@cd Website && git pull

logs:
	@docker-compose logs -f

backup:
	@mkdir -p $(MYSQL_DUMPS_DIR)
	docker exec $(shell docker-compose ps -q mysqldb) mysqldump --all-databases -u"$(MARIADB_ROOT_USER)" -p"$(MARIADB_ROOT_PASSWORD)" | gzip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d %H%M %Z"`.sql.zip

flush-website:
	@$(shell sudo rm -rf Website)
