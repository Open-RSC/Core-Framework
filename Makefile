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

build:
	docker-compose build

compile:
	docker exec -i openrsc ant -f server/build.xml compile_core
	docker exec -i openrsc ant -f server/build.xml compile_plugins
	docker exec -i openrsc ant -f Client_Base/build.xml compile
	docker exec -i openrsc ant -f PC_Launcher/build.xml compile
	docker-compose down -v
	docker-compose up -d





