<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

define('LUNA_ROOT', dirname(__FILE__).'/');
require LUNA_ROOT.'include/common.php';

if ($luna_user['g_read_board'] == '0')
	message(__('You do not have permission to view this page.', 'luna'), false, '403 Forbidden');
elseif ($luna_user['g_view_users'] == '0')
	message(__('You do not have permission to access this page.', 'luna'), false, '403 Forbidden');

// Determine if we are allowed to view comment counts
$show_comment_count = ($luna_config['o_show_comment_count'] == '1' || $luna_user['is_admmod']) ? true : false;

$username = isset($_GET['username']) && $luna_user['g_search_users'] == '1' ? luna_trim($_GET['username']) : '';
if (isset($_GET['sort'])) {
	if ($_GET['sort'] == 'username')
		$sort_query = 'username ASC';
	elseif ($_GET['sort'] == 'registered')
		$sort_query = 'registered ASC';
	else
		$sort_query = 'num_comments DESC';

	$sort_by = $_GET['sort'];
} else {
	$sort_query = 'username ASC';
	$sort_by = 'username';
}

// Create any SQL for the WHERE clause
$where_sql = array();
$like_command = ($db_type == 'pgsql') ? 'ILIKE' : 'LIKE';

if ($username != '')
	$where_sql[] = 'u.username '.$like_command.' \''.$db->escape(str_replace('*', '%', $username)).'\'';

// Fetch user count
$result = $db->query('SELECT COUNT(id) FROM '.$db->prefix.'users AS u WHERE u.id>1 AND u.group_id!='.LUNA_UNVERIFIED.(!empty($where_sql) ? ' AND '.implode(' AND ', $where_sql) : '')) or error('Unable to fetch user list count', __FILE__, __LINE__, $db->error());
$num_users = $db->result($result);

// Determine the user offset (based on $_GET['p'])
$num_pages = ceil($num_users / 50);

$p = (!isset($_GET['p']) || $_GET['p'] <= 1 || $_GET['p'] > $num_pages) ? 1 : intval($_GET['p']);
$start_from = 50 * ($p - 1);

$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), __('User list', 'luna'));
if ($luna_user['g_search_users'] == '1')
	$focus_element = array('userlist', 'username');

// Generate paging links
$paging_links = forum_paginate($num_pages, $p, 'userlist.php?username='.urlencode($username).'&amp;sort='.$sort_by);

define('LUNA_ALLOW_INDEX', 1);
define('LUNA_ACTIVE_PAGE', 'userlist');
require load_page('header.php');

require load_page('users.php');

require load_page('footer.php');
