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
# Call via "make rank db=cabbage group=0 username=wolf"
rank:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	@[ "${group}" ] || ( echo ">> group is not set"; exit 1 	)
	@[ "${username}" ] || ( echo ">> username is not set"; exit 1 )
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "USE ${db}; UPDATE players SET group_id = '${group}' WHERE players.username = '${username}';"

# Creates a database that the user specifies the name of
# Call via "make create db=cabbage"
create:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} -e "create database ${db};"

# Imports the core.sql and custom.sql files to a specified database
# Call via "make import-core db=preservation"
import-authentic:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/core.sql

# Imports the core.sql and custom.sql files to a specified database
# Call via "make import-custom db=cabbage"
import-custom:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/core.sql
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/Addons/add_auctionhouse.sql
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/Addons/add_bank_presets.sql
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/Addons/add_clans.sql
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/Addons/add_custom_items.sql
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/Addons/add_custom_npcs.sql
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/Addons/add_custom_objects.sql
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/Addons/add_equipment_tab.sql
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/Addons/add_harvesting.sql
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/Addons/add_ironman.sql
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/Addons/add_npc_kill_counting.sql
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/Addons/add_runecraft.sql

# Imports the core.sql and custom.sql files to a specified database
# Call via "make import-retro db=retrorsc"
import-retro:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/retro.sql

# Imports specified add-on SQL file
# Call via "make import-addon db=cabbage name=add_custom_items.sql
import-addon:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} < Databases/Addons/${name}

# Creates a database export of the specified database and saves to the output directory specified in the .env file.  Good for utilizing as a crontab.
# Call via "make backup db=cabbage"
backup:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	mkdir -p $(MYSQL_DUMPS_DIR)
	chmod -R 777 $(MYSQL_DUMPS_DIR)
	docker exec mariadb mysqldump -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} --single-transaction --quick --lock-tables=false | zip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m%d-%H%M-%Z"`-${db}.zip

# Unzips a database backup zip file in the output directory specified in the .env file and then imports it into the specified database as a database restoration from backup method
# Call via "make restore name=20191017-0226-EDT-cabbage.zip db=cabbage"
restore:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	@[ "${name}" ] || ( echo ">> name is not set"; exit 1 )
	unzip -p $(MYSQL_DUMPS_DIR)/${name} | docker exec -i mariadb mysql -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db}

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