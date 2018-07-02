<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

define('LUNA_ROOT', dirname(__FILE__).'/');
require LUNA_ROOT.'include/common.php';
require LUNA_ROOT.'include/statistic_functions.php';

if ($luna_user['g_read_board'] == '0')
	message(__('You do not have permission to view this page.', 'luna'), false, '403 Forbidden');

// Get list of forums and threads with new comments since last visit
if (!$luna_user['is_guest']) {
	$result = $db->query('SELECT f.id, f.last_comment FROM '.$db->prefix.'forums AS f LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=f.id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.read_forum IS NULL OR fp.read_forum=1) AND f.last_comment>'.$luna_user['last_visit']) or error('Unable to fetch forum list', __FILE__, __LINE__, $db->error());

	if ($db->num_rows($result)) {
		$forums = $new_threads = array();
		$tracked_threads = get_tracked_threads();

		while ($cur_forum = $db->fetch_assoc($result)) {
			if (!isset($tracked_threads['forums'][$cur_forum['id']]) || $tracked_threads['forums'][$cur_forum['id']] < $cur_forum['last_comment'])
				$forums[$cur_forum['id']] = $cur_forum['last_comment'];
		}

		if (!empty($forums)) {
			if (empty($tracked_threads['threads']))
				$new_threads = $forums;
			else {
				$result = $db->query('SELECT forum_id, id, last_comment FROM '.$db->prefix.'threads WHERE forum_id IN('.implode(',', array_keys($forums)).') AND last_comment>'.$luna_user['last_visit'].' AND moved_to IS NULL') or error('Unable to fetch new threads', __FILE__, __LINE__, $db->error());

				while ($cur_thread = $db->fetch_assoc($result)) {
					if (!isset($new_threads[$cur_thread['forum_id']]) && (!isset($tracked_threads['forums'][$cur_thread['forum_id']]) || $tracked_threads['forums'][$cur_thread['forum_id']] < $forums[$cur_thread['forum_id']]) && (!isset($tracked_threads['threads'][$cur_thread['id']]) || $tracked_threads['threads'][$cur_thread['id']] < $cur_thread['last_comment']))
						$new_threads[$cur_thread['forum_id']] = $forums[$cur_thread['forum_id']];
				}
			}
		}
	}
}

if ($luna_config['o_feed_type'] == '1')
	$page_head = array('feed' => '<link rel="alternate" type="application/rss+xml" href="extern.php?action=feed&amp;type=rss" title="'.__('RSS active thread feed', 'luna').'" />');
elseif ($luna_config['o_feed_type'] == '2')
	$page_head = array('feed' => '<link rel="alternate" type="application/atom+xml" href="extern.php?action=feed&amp;type=atom" title="'.__('Atom active thread feed', 'luna').'" />');

$forum_actions = array();

// Someone clicked "Do not show again"
$action = isset($_GET['action']) ? $_GET['action'] : null;

// Or want to disable the cookiebar
if ($action == 'disable_cookiebar') {
	luna_cookiebarcookie();

	redirect('forum.php');
}

if ($luna_config['o_board_slogan'] == '')
    $page_title = array(luna_htmlspecialchars($luna_config['o_board_title']));
else
    $page_title = array(luna_htmlspecialchars($luna_config['o_board_title']).' &middot; '.$luna_config['o_board_slogan']);

define('LUNA_ALLOW_INDEX', 1);
define('LUNA_ACTIVE_PAGE', 'forum');
$footer_style = 'forum';

require load_page('header.php');

require load_page('forum-index.php');

require load_page('footer.php');
