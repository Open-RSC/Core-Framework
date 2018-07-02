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

//
// Generate the config cache PHP script
//
function generate_config_cache() {
	global $db;

	// Get the forum config from the DB
	$result = $db->query('SELECT * FROM '.$db->prefix.'config', true) or error('Unable to fetch forum config', __FILE__, __LINE__, $db->error());

	$output = array();
	while ($cur_config_item = $db->fetch_row($result))
		$output[$cur_config_item[0]] = $cur_config_item[1];

	// Output config as PHP code
	$content = '<?php'."\n\n".'define(\'LUNA_CONFIG_LOADED\', 1);'."\n\n".'$luna_config = '.var_export($output, true).';'."\n\n".'?>';
	luna_write_cache_file('cache_config.php', $content);
}


/*function generate_server_total_cache() {
	global $db;
	
	$default_string = "10\r\n575\r\n576\r\n577\r\n578\r\n579\r\n580\r\n581\r\n971\r\n677\r\n1156\r\n1278\r\n795\r\n593\r\n594\r\n1289\r\n";
	$find_defaults = isset($_POST['set_default']) && preg_match("/^[0-9;]+?$/i", $_POST['set_default']) ? $_POST['set_default'] : null;

	// Get the forum config from the DB
	$result = $db->query('SELECT * FROM '.$db->prefix.'config', true) or error('Unable to fetch forum config', __FILE__, __LINE__, $db->error());

	$output = array();
	while ($cur_config_item = $db->fetch_row($result))
		$output[$cur_config_item[0]] = $cur_config_item[1];

	// Output config as PHP code
	$content = '<?php'."\n\n".'define(\'LUNA_CONFIG_LOADED\', 1);'."\n\n".'$luna_config = '.var_export($output, true).';'."\n\n".'?>';
	luna_write_cache_file('cache_items.php', $content);
}*/
//
// Generate the bans cache PHP script
//
function generate_bans_cache() {
	global $db;

	// Get the ban list from the DB
	$result = $db->query('SELECT * FROM '.$db->prefix.'bans', true) or error('Unable to fetch ban list', __FILE__, __LINE__, $db->error());

	$output = array();
	while ($cur_ban = $db->fetch_assoc($result))
		$output[] = $cur_ban;

	// Output ban list as PHP code
	$content = '<?php'."\n\n".'define(\'LUNA_BANS_LOADED\', 1);'."\n\n".'$luna_bans = '.var_export($output, true).';'."\n\n".'?>';
	luna_write_cache_file('cache_bans.php', $content);
}


//
// Generate the ranks cache PHP script
//
function generate_ranks_cache() {
	global $db;

	// Get the rank list from the DB
	$result = $db->query('SELECT * FROM '.$db->prefix.'ranks ORDER BY min_comments', true) or error('Unable to fetch rank list', __FILE__, __LINE__, $db->error());

	$output = array();
	while ($cur_rank = $db->fetch_assoc($result))
		$output[] = $cur_rank;

	// Output ranks list as PHP code
	$content = '<?php'."\n\n".'define(\'LUNA_RANKS_LOADED\', 1);'."\n\n".'$luna_ranks = '.var_export($output, true).';'."\n\n".'?>';
	luna_write_cache_file('cache_ranks.php', $content);
}


//
// Generate the ranks cache PHP script
//
function generate_forum_cache() {
	global $db;

	// Get the forum list from the DB
	$result = $db->query('SELECT id, forum_name, color, icon FROM '.$db->prefix.'forums ORDER BY id', true) or error('Unable to fetch forum list', __FILE__, __LINE__, $db->error());

	$output = array();
	while ($cur_forum = $db->fetch_assoc($result))
		$output[] = $cur_forum;

	// Output ranks list as PHP code
	$content = '<?php'."\n\n".'define(\'LUNA_LIST_LOADED\', 1);'."\n\n".'$luna_forums = '.var_export($output, true).';'."\n\n".'?>';
	luna_write_cache_file('cache_forums.php', $content);
}


//
// Generate the censoring cache PHP script
//
function generate_censoring_cache() {
	global $db;

	$result = $db->query('SELECT search_for, replace_with FROM '.$db->prefix.'censoring') or error('Unable to fetch censoring list', __FILE__, __LINE__, $db->error());
	$num_words = $db->num_rows($result);

	$search_for = $replace_with = array();
	for ($i = 0; $i < $num_words; $i++) {
		list($search_for[$i], $replace_with[$i]) = $db->fetch_row($result);
		$search_for[$i] = '%(?<=[^\p{L}\p{N}])('.str_replace('\*', '[\p{L}\p{N}]*?', preg_quote($search_for[$i], '%')).')(?=[^\p{L}\p{N}])%iu';
	}

	// Output censored words as PHP code
	$content = '<?php'."\n\n".'define(\'LUNA_CENSOR_LOADED\', 1);'."\n\n".'$search_for = '.var_export($search_for, true).';'."\n\n".'$replace_with = '.var_export($replace_with, true).';'."\n\n".'?>';
	luna_write_cache_file('cache_censoring.php', $content);
}


//
// Generate the stopwords cache PHP script
//
function generate_stopwords_cache() {
	$stopwords = array();

	$d = dir(LUNA_ROOT.'lang');
	while (($entry = $d->read()) !== false) {
		if ($entry{0} == '.')
			continue;

		if (is_dir(LUNA_ROOT.'lang/'.$entry) && file_exists(LUNA_ROOT.'lang/'.$entry.'/stopwords.txt'))
			$stopwords = array_merge($stopwords, file(LUNA_ROOT.'lang/'.$entry.'/stopwords.txt'));
	}
	$d->close();

	// Tidy up and filter the stopwords
	$stopwords = array_map('luna_trim', $stopwords);
	$stopwords = array_filter($stopwords);

	// Output stopwords as PHP code
	$content = '<?php'."\n\n".'$cache_id = \''.generate_stopwords_cache_id().'\';'."\n".'if ($cache_id != generate_stopwords_cache_id()) return;'."\n\n".'define(\'LUNA_STOPWORDS_LOADED\', 1);'."\n\n".'$stopwords = '.var_export($stopwords, true).';'."\n\n".'?>';
	luna_write_cache_file('cache_stopwords.php', $content);
}


//
// Load some information about the latest registered users
//
function generate_users_info_cache() {
	global $db;

	$stats = array();

	$result = $db->query('SELECT COUNT(id)-1 FROM '.$db->prefix.'users WHERE group_id!='.LUNA_UNVERIFIED) or error('Unable to fetch total user count', __FILE__, __LINE__, $db->error());
	$stats['total_users'] = $db->result($result);

	$result = $db->query('SELECT id, username FROM '.$db->prefix.'users WHERE group_id!='.LUNA_UNVERIFIED.' ORDER BY registered DESC LIMIT 1') or error('Unable to fetch newest registered user', __FILE__, __LINE__, $db->error());
	$stats['last_user'] = $db->fetch_assoc($result);

	// Output users info as PHP code
	$content = '<?php'."\n\n".'define(\'LUNA_USERS_INFO_LOADED\', 1);'."\n\n".'$stats = '.var_export($stats, true).';'."\n\n".'?>';
	luna_write_cache_file('cache_users_info.php', $content);
}


//
// Generate the admins cache PHP script
//
function generate_admins_cache() {
	global $db;

	// Get admins from the DB
	$result = $db->query('SELECT id FROM '.$db->prefix.'users WHERE group_id='.LUNA_ADMIN) or error('Unable to fetch users info', __FILE__, __LINE__, $db->error());

	$output = array();
	while ($row = $db->fetch_row($result))
		$output[] = $row[0];

	// Output admin list as PHP code
	$content = '<?php'."\n\n".'define(\'LUNA_ADMINS_LOADED\', 1);'."\n\n".'$luna_admins = '.var_export($output, true).';'."\n\n".'?>';
	luna_write_cache_file('cache_admins.php', $content);
}


//
// Safely write out a cache file.
//
function luna_write_cache_file($file, $content) {
	$fh = @fopen(LUNA_CACHE_DIR.$file, 'wb');
	if (!$fh)
		error('Unable to write cache file '.luna_htmlspecialchars($file).' to cache directory. Please make sure PHP has write access to the directory \''.luna_htmlspecialchars(LUNA_CACHE_DIR).'\'', __FILE__, __LINE__);

	flock($fh, LOCK_EX);
	ftruncate($fh, 0);

	fwrite($fh, $content);

	flock($fh, LOCK_UN);
	fclose($fh);

	if (function_exists('apc_delete_file'))
		@apc_delete_file(LUNA_CACHE_DIR.$file);
}


//
// Delete all feed caches
//
function clear_feed_cache() {
	$d = dir(LUNA_CACHE_DIR);
	while (($entry = $d->read()) !== false) {
		if (substr($entry, 0, 10) == 'cache_feed' && substr($entry, -4) == '.php')
			@unlink(LUNA_CACHE_DIR.$entry);
	}
	$d->close();
}


define('LUNA_CACHE_FUNCTIONS_LOADED', true);
