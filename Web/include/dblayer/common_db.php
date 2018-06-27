<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * License: http://opensource.org/licenses/MIT MIT
 */

// Make sure no one attempts to run this script "directly"
if (!defined('FORUM'))
	exit;


// Load the appropriate DB layer class
switch ($db_type) {
	case 'mysql':
		require_once LUNA_ROOT.'include/dblayer/mysql.php';
		break;

	case 'mysql_innodb':
		require_once LUNA_ROOT.'include/dblayer/mysql_innodb.php';
		break;

	case 'mysqli':
		require_once LUNA_ROOT.'include/dblayer/mysqli.php';
		break;

	case 'mysqli_innodb':
		require_once LUNA_ROOT.'include/dblayer/mysqli_innodb.php';
		break;

	case 'pgsql':
		require_once LUNA_ROOT.'include/dblayer/pgsql.php';
		break;

	case 'sqlite':
		require_once LUNA_ROOT.'include/dblayer/sqlite.php';
		break;

	case 'sqlite3':
		require_once LUNA_ROOT.'include/dblayer/sqlite3.php';
		break;

	default:
		error('\''.$db_type.'\' is not a valid database type. Please check settings in config.php.', __FILE__, __LINE__);
		break;
}


// Create the database adapter object (and open/connect to/select db)
$db = new DBLayer($db_host, $db_username, $db_password, $db_name, $db_prefix, $p_connect);
