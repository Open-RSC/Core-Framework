<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

define('LUNA_ROOT', dirname(__FILE__).'/');
require LUNA_ROOT.'include/common.php';
define('LUNA_CANONICAL_TAG_FORUM', 1);

if ($luna_user['g_read_board'] == '0')
	message(__('You do not have permission to view this page.', 'luna'), false, '403 Forbidden');

$id = isset($_GET['id']) ? intval($_GET['id']) : 0;
if ($id < 1)
	message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

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

// Fetch some info about the forum
if (!$luna_user['is_guest'])
	$result = $db->query('SELECT f.forum_name, f.forum_desc, f.moderators, f.num_threads, f.sort_by, f.icon, f.color, f.solved, fp.create_threads, s.user_id AS is_subscribed FROM '.$db->prefix.'forums AS f LEFT JOIN '.$db->prefix.'forum_subscriptions AS s ON (f.id=s.forum_id AND s.user_id='.$luna_user['id'].') LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=f.id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.read_forum IS NULL OR fp.read_forum=1) AND f.id='.$id) or error('Unable to fetch forum info', __FILE__, __LINE__, $db->error());
else
	$result = $db->query('SELECT f.forum_name, f.forum_desc, f.moderators, f.num_threads, f.sort_by, f.icon, f.color, f.solved, fp.create_threads, 0 AS is_subscribed FROM '.$db->prefix.'forums AS f LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=f.id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.read_forum IS NULL OR fp.read_forum=1) AND f.id='.$id) or error('Unable to fetch forum info', __FILE__, __LINE__, $db->error());

if (!$db->num_rows($result))
	message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

$cur_forum = $db->fetch_assoc($result);

// Sort out who the moderators are and if we are currently a moderator (or an admin)
$mods_array = ($cur_forum['moderators'] != '') ? unserialize($cur_forum['moderators']) : array();
$is_admmod = ($luna_user['g_id'] == LUNA_ADMIN || ($luna_user['g_moderator'] == '1' && array_key_exists($luna_user['username'], $mods_array))) ? true : false;

switch ($cur_forum['sort_by']) {
	case 0:
		$sort_by = 'last_comment DESC';
		break;
	case 1:
		$sort_by = 'commented DESC';
		break;
	case 2:
		$sort_by = 'subject ASC';
		break;
	default:
		$sort_by = 'last_comment DESC';
		break;
}

// Can we or can we not comment new threads?
if (($cur_forum['create_threads'] == '' && $luna_user['g_create_threads'] == '1') || $cur_forum['create_threads'] == '1' || $is_admmod)
	$comment_link = "\t\t\t".'<a class="btn btn-success btn-comment" href="comment.php?fid='.$id.'"><span class="fa fa-fw fa-plus"></span> '.__('Create thread', 'luna').'</a>'."\n";
else
	$comment_link = '';

// Get thread/forum tracking data
if (!$luna_user['is_guest'])
	$tracked_threads = get_tracked_threads();

// Determine the thread offset (based on $_GET['p'])
$num_pages = ceil($cur_forum['num_threads'] / $luna_user['disp_threads']);

$p = (!isset($_GET['p']) || $_GET['p'] <= 1 || $_GET['p'] > $num_pages) ? 1 : intval($_GET['p']);
$start_from = $luna_user['disp_threads'] * ($p - 1);

// Get the icon
if ($cur_forum['icon'] != NULL)
	$faicon = '<span class="fa fa-fw fa-'.$cur_forum['icon'].'"></span> ';
else
	$faicon = '';

// Generate paging links
$paging_links = forum_paginate($num_pages, $p, 'viewforum.php?id='.$id);

if ($luna_config['o_feed_type'] == '1')
	$page_head = array('feed' => '<link rel="alternate" type="application/rss+xml" href="extern.php?action=feed&amp;fid='.$id.'&amp;type=rss" title="'.__('RSS forum feed', 'luna').'" />');
elseif ($luna_config['o_feed_type'] == '2')
	$page_head = array('feed' => '<link rel="alternate" type="application/atom+xml" href="extern.php?action=feed&amp;fid='.$id.'&amp;type=atom" title="'.__('Atom forum feed', 'luna').'" />');

$forum_actions = array();

// Subscribe
if (!$luna_user['is_guest'] && $luna_config['o_forum_subscriptions'] == '1') {
	$token_url = '&amp;csrf_token='.luna_csrf_token();

	if ($cur_forum['is_subscribed'])
		$thread_actions[] = '<a href="misc.php?action=unsubscribe&amp;fid='.$id.$token_url.'">'.__('Unsubscribe', 'luna').'</a>';
	else
		$thread_actions[] = '<a href="misc.php?action=subscribe&amp;fid='.$id.$token_url.'">'.__('Subscribe', 'luna').'</a>';
}

$forum_id = $id;
$footer_style = 'viewforum';

$meta_description = $cur_forum['forum_desc'];
$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), luna_htmlspecialchars($cur_forum['forum_name']));
define('LUNA_ALLOW_INDEX', 1);
define('LUNA_ACTIVE_PAGE', 'viewforum');
require load_page('header.php');

require load_page('forum.php');

require load_page('footer.php');
