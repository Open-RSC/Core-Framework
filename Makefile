include .env
#---------------------------------------------------------------

# Section utilized by various shell scripts within Deployment_Scripts
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

compile:
	`pwd`/Deployment_Scripts/get-updates.sh

#---------------------------------------------------------------

# MariaDB-only server container control section
start:
	docker-compose -f docker-compose.yml up -d

stop:
	docker-compose -f docker-compose.yml down -v

restart:
	docker-compose -f docker-compose.yml down -v
	docker-compose -f docker-compose.yml up -d

logs:
	docker-compose -f docker-compose.yml logs -f

#---------------------------------------------------------------

# Sets a specified username to be in a specified group in a specified database
# Call via "make rank-mariadb db=cabbage group=0 username=wolf"
rank-mariadb:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	@[ "${group}" ] || ( echo ">> group is not set"; exit 1 	)
	@[ "${username}" ] || ( echo ">> username is not set"; exit 1 )
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "USE ${db}; UPDATE players SET group_id = '${group}' WHERE players.username = '${username}';"

# Sets a specified username to be in a specified group in a specified database
# Call via "make rank-sqlite db=cabbage group=0 username=wolf"
rank-sqlite:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	@[ "${group}" ] || ( echo ">> group is not set"; exit 1 	)
	@[ "${username}" ] || ( echo ">> username is not set"; exit 1 )
	sqlite3 server/inc/sqlite/${db}.db "UPDATE players SET group_id = '${group}' WHERE players.username = '${username}';" ".exit"

# Changes a specified username to be a new username in a specified database
# Call via "make namechange-mariadb db=cabbage oldname=wolf newname=wolf2"
namechange-mariadb:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	@[ "${oldname}" ] || ( echo ">> oldname is not set"; exit 1 	)
	@[ "${newname}" ] || ( echo ">> newname is not set"; exit 1 )
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "USE ${db}; UPDATE players SET username = '${newname}' WHERE players.username = '${oldname}';"

# Changes a specified username to be a new username in a specified database
# Call via "make namechange-sqlite db=cabbage oldname=wolf newname=wolf2"
namechange-sqlite:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	@[ "${oldname}" ] || ( echo ">> oldname is not set"; exit 1 	)
	@[ "${newname}" ] || ( echo ">> newname is not set"; exit 1 )
	sqlite3 server/inc/sqlite/${db}.db "UPDATE players SET username = '${newname}' WHERE players.username = '${oldname}';" ".exit"

# Creates a database that the user specifies the name of
# Call via "make create-mariadb db=cabbage"
create-mariadb:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "create database ${db};"

# Imports the core.sql file to a specified database
# Call via "make import-authentic-mariadb db=preservation"
import-authentic-mariadb:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < server/database/mysql/core.sql

# Imports the core.sqlite file to a specified database
# Call via "make import-authentic-sqlite db=preservation"
import-authentic-sqlite:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	cat server/database/sqlite/core.sqlite | sqlite3 server/inc/sqlite/${db}.db

# Imports the addon sql files to a specified database
# Call via "make import-custom-mariadb db=cabbage"
import-custom-mariadb:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < server/database/mysql/core.sql
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < server/database/mysql/addons/add_auctionhouse.sql
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < server/database/mysql/addons/add_bank_presets.sql
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < server/database/mysql/addons/add_clans.sql
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < server/database/mysql/addons/add_equipment_tab.sql
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < server/database/mysql/addons/add_harvesting.sql
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < server/database/mysql/addons/add_npc_kill_counting.sql
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < server/database/mysql/addons/add_runecraft.sql

# Imports the addon sqlite files to a specified database
# Call via "make import-custom-sqlite db=cabbage"
import-custom-sqlite:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	cat server/database/sqlite/core.sqlite | sqlite3 server/inc/sqlite/${db}.db
	cat server/database/sqlite/addons/add_auctionhouse.sqlite | sqlite3 server/inc/sqlite/${db}.db
	cat server/database/sqlite/addons/add_bank_presets.sqlite | sqlite3 server/inc/sqlite/${db}.db
	cat server/database/sqlite/addons/add_clans.sqlite | sqlite3 server/inc/sqlite/${db}.db
	cat server/database/sqlite/addons/add_equipment_tab.sqlite | sqlite3 server/inc/sqlite/${db}.db
	cat server/database/sqlite/addons/add_npc_kill_counting.sqlite | sqlite3 server/inc/sqlite/${db}.db

# Imports the retro.sql file to a specified database
# Call via "make import-retro-mariadb db=2001scape"
import-retro-mariadb:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < server/database/mysql/retro.sql

# Imports the retro.sqlite file to a specified database
# Call via "make import-retro-sqlite db=2001scape"
import-retro-sqlite:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	rm server/inc/sqlite/${db}.db
	cat server/database/sqlite/retro.sqlite | sqlite3 server/inc/sqlite/${db}.db

# Creates a database export of the specified database and saves to the output directory specified in the .env file.  Good for utilizing as a crontab.
# Call via "make backup-mariadb db=cabbage"
backup-mariadb:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	mkdir -p $(MYSQL_DUMPS_DIR)
	chmod -R 777 $(MYSQL_DUMPS_DIR)
	docker exec mariadb mysqldump -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} --single-transaction --quick --lock-tables=false | zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`-${db}.zip

# Creates a database export of the specified database and saves to the output directory specified in the .env file.  Good for utilizing as a crontab.
# Call via "make backup-sqlite db=cabbage"
backup-sqlite:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	mkdir -p $(MYSQL_DUMPS_DIR)
	chmod -R 777 $(MYSQL_DUMPS_DIR)
	echo .dump | sqlite3 server/inc/sqlite/${db}.db | zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`-${db}.zip

# Unzips a database backup zip file in the output directory specified in the .env file and then imports it into the specified database as a database restoration from backup method
# Call via "make restore-mariadb name=20191017-0226-EDT-cabbage.zip db=cabbage"
restore-mariadb:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	@[ "${name}" ] || ( echo ">> name is not set"; exit 1 )
	unzip -p $(MYSQL_DUMPS_DIR)/${name} | docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db}

# Unzips a database backup zip file in the output directory specified in the .env file and then imports it into the specified database as a database restoration from backup method
# Call via "make restore-mariadb name=20191017-0226-EDT-cabbage.zip db=cabbage"
restore-sqlite:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	@[ "${name}" ] || ( echo ">> name is not set"; exit 1 )
	rm server/inc/sqlite/${db}.db
	echo .read | unzip -p $(MYSQL_DUMPS_DIR)/${name} | sqlite3 server/inc/sqlite/${db}.db

# Deletes database backup zip files odler than the number of days specified. Good for utilizing as a crontab.
# Call via "clear-backups days=90"
clear-backups:
	@[ "${days}" ] || ( echo ">> days is not set"; exit 1 )
	find $(MYSQL_DUMPS_DIR)/*.zip -mtime +${days} -exec rm -f {} \;

# Truncates database log tables that account for backup size bloat on heavy bot worlds
# Call via "truncate db=uranium"
truncate:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "USE ${db}; TRUNCATE generic_logs; TRUNCATE droplogs; TRUNCATE chat_logs; TRUNCATE logins; TRUNCATE trade_logs;"
