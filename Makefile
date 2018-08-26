include .env

MYSQL_DUMPS_DIR=./data/db

start:
	docker-compose --file docker-compose-travis.yml up --force-recreate --remove-orphans -d

stop:
	@docker-compose -f docker-compose-travis.yml down -v

restart:
	@docker-compose -f docker-compose-travis.yml down -v
	docker-compose -f docker-compose-travis.yml up -d

ps:
	docker-compose -f docker-compose-travis.yml ps

compile:
	ant -f Server/build.xml compile_core
	ant -f Server/build.xml compile_plugins
	ant -f Client/build.xml compile
	ant -f Launcher/build.xml jar

import-game:
	docker exec -i mysql mysql -u"$(MYSQL_ROOT_USER)" -p"$(MYSQL_ROOT_PASSWORD)" < openrsc_game.sql

run-game:
	ant -f ant -f Server/build.xml runservermembers
