include ../.env
MYSQL_DUMPS_DIR=../data

go:
	cd ../ && `pwd`/Go.sh

run:
	`pwd`/run.sh

certbot:
	`pwd`/certbot.sh

combined-install:
	`pwd`/combined-install.sh

direct-install:
	`pwd`/direct-install.sh

docker-install:
	`pwd`/docker-install.sh

get-updates:
	`pwd`/get-updates.sh

single-player:
	`pwd`/single-player.sh

file-edits:
	`pwd`/file-edits.sh

start:
	docker-compose up -d

start-single-player:
	docker-compose --file ../docker-compose-single-player.yml up -d

stop:
	@docker-compose down -v

restart:
	@docker-compose down -v
	docker-compose up -d

ps:
	docker-compose ps

compile:
	sudo ant -f ../server/build.xml compile_core
	sudo ant -f ../server/build.xml compile_plugins
	sudo ant -f ../client/build.xml compile
	sudo ant -f ../Launcher/build.xml jar

import-game:
	@docker exec -i mysql mysql -u"root" -p"root" < ../Databases/openrsc_game.sql 2>/dev/null

import-forum:
	@docker exec -i mysql mysql -u"root" -p"root" < ../Databases/openrsc_forum.sql 2>/dev/null

run-game:
	sudo ant -f ../server/build.xml runservermembers

clone-website:
	@$(shell git clone -b 2.0.0 https://github.com/Open-RSC/Website.git)

pull-website:
	@cd ../Website && git pull

logs:
	@docker-compose logs -f

backup:
	@mkdir -p $(MYSQL_DUMPS_DIR)
	sudo chmod -R 777 $(MYSQL_DUMPS_DIR)
	docker exec mysql mysqldump --all-databases -u"root" -p"root" | gzip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`.sql.zip

flush-website:
	@$(shell sudo rm -rf ../Website)
