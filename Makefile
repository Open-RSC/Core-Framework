MYSQL_DUMPS_DIR=./data/db

start:
	docker-compose -f docker-compose-travis.yml up -d

stop:
	@docker-compose -f docker-compose-travis.yml down -v

restart:
	@docker-compose -f docker-compose-travis.yml down -v
	docker-compose -f docker-compose-travis.yml up -d

ps:
	docker-compose -f docker-compose-travis.yml ps

compile:
	ant -f server/build.xml compile
	ant -f client/build.xml compile

import-game:
	docker exec -i mysql mysql -u"root" -p"root" < Databases/openrsc_config.sql
	docker exec -i mysql mysql -u"root" -p"root" < Databases/openrsc_logs.sql
	docker exec -i mysql mysql -u"root" -p"root" < Databases/openrsc.sql
	docker exec -i mysql mysql -u"root" -p"root" < Databases/openrsc_tools.sql
