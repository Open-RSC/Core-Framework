<?php

/*
 * Copyright (C) 2014-2016 Luna
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

class Version {
	const LUNA_VERSION = '2.0.3';
    const LUNA_BRANCH = '2.0';
	const LUNA_CORE_VERSION = '2.0.5753';
	const LUNA_CODE_NAME = 'Emerald';

	// The database version number, every change in the database requires this number to go one up
	const LUNA_DB_VERSION = '91.26';

	// The parser version number, every change to the parser requires this number to go one up
	const LUNA_PARSER_VERSION = '11.4.1';

	// The search index version number, every change to the search index requires this number to go one up
	const LUNA_SI_VERSION = '2.0';

	// Luna system requirements
	const MIN_PHP_VERSION = '5.3.0';
	const MIN_MYSQL_VERSION = '5.0.0';
	const MIN_PGSQL_VERSION = '8.0.0';
}
?>