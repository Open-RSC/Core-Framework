include .env

# Creates a database export of the specified database and saves to the output directory specified in the .env file.  Good for utilizing as a crontab.
# Call via "make backup-mariadb db=cabbage"
backup-mariadb:
	@[ "${db}" ] || ( echo ">> db is not set"; exit 1 )
	mkdir -p $(MYSQL_DUMPS_DIR)
	chmod -R 777 $(MYSQL_DUMPS_DIR)
	mkdir -p $(MYSQL_DUMPS_DIR)/`date "+%Y%m"`
	chmod -R 777 $(MYSQL_DUMPS_DIR)/`date "+%Y%m"`
	mysqldump -u${MARIADB_ROOT_USER} -p${MARIADB_ROOT_PASSWORD} ${db} --single-transaction --quick --lock-tables=false | gzip > $(MYSQL_DUMPS_DIR)/`date "+%Y%m"`/`date "+%Y%m%d-%H%M-%Z"`-${db}.sql.gz
