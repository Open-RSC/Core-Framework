<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

define('LUNA_ROOT', dirname(__FILE__).'/');
require LUNA_ROOT.'include/common.php';
define('LUNA_CANONICAL_TAG_TOPIC', 1);

if ($luna_user['g_read_board'] == '0')
	message(__('You do not have permission to view this page.', 'luna'), false, '403 Forbidden');

$action = isset($_GET['action']) ? $_GET['action'] : null;
$id = isset($_GET['id']) ? intval($_GET['id']) : 0;
$pid = isset($_GET['pid']) ? intval($_GET['pid']) : 0;
if ($id < 1 && $pid < 1)
	message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

// If a comment ID is specified we determine thread ID and page number so we can redirect to the correct message
if ($pid) {
	$result = $db->query('SELECT thread_id, commented FROM '.$db->prefix.'comments WHERE id='.$pid) or error('Unable to fetch thread ID', __FILE__, __LINE__, $db->error());
	if (!$db->num_rows($result))
		message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

	list($id, $commented) = $db->fetch_row($result);

	// Determine on which page the comment is located (depending on $forum_user['disp_comments'])
	$result = $db->query('SELECT COUNT(id) FROM '.$db->prefix.'comments WHERE thread_id='.$id.' AND commented<'.$commented) or error('Unable to count previous comments', __FILE__, __LINE__, $db->error());
	$num_comments = $db->result($result) + 1;

	$_GET['p'] = ceil($num_comments / $luna_user['disp_comments']);
} else {
	// If action=new, we redirect to the first new comment (if any)
	if ($action == 'new') {
		if (!$luna_user['is_guest']) {
			// We need to check if this thread has been viewed recently by the user
			$tracked_threads = get_tracked_threads();
			$last_viewed = isset($tracked_threads['threads'][$id]) ? $tracked_threads['threads'][$id] : $luna_user['last_visit'];

			$result = $db->query('SELECT MIN(id) FROM '.$db->prefix.'comments WHERE thread_id='.$id.' AND commented>'.$last_viewed) or error('Unable to fetch first new comment info', __FILE__, __LINE__, $db->error());
			$first_new_comment_id = $db->result($result);

			if ($first_new_comment_id) {
				header('Location: thread.php?pid='.$first_new_comment_id.'#p'.$first_new_comment_id);
				exit;
			}
		}

		// If there is no new comment, we go to the last comment
		$action = 'last';
	}

	// If action=last, we redirect to the last comment
	if ($action == 'last') {
		$result = $db->query('SELECT MAX(id) FROM '.$db->prefix.'comments WHERE thread_id='.$id) or error('Unable to fetch last comment info', __FILE__, __LINE__, $db->error());
		$last_comment_id = $db->result($result);


		if ($last_comment_id) {
			header('Location: thread.php?pid='.$last_comment_id.'#p'.$last_comment_id);
			exit;
		}
	}
}


// Fetch some info about the thread
if ($luna_user['is_guest'])
	$result = $db->query('SELECT t.subject, t.commenter, t.closed, t.num_replies, t.pinned, t.important, t.solved AS answer, t.first_comment_id, f.id AS forum_id, f.forum_name, f.moderators, fp.comment FROM '.$db->prefix.'threads AS t INNER JOIN '.$db->prefix.'forums AS f ON f.id=t.forum_id LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=f.id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.read_forum IS NULL OR fp.read_forum=1) AND t.id='.$id.' AND t.moved_to IS NULL') or error('Unable to fetch thread info', __FILE__, __LINE__, $db->error());
else
	$result = $db->query('SELECT t.subject, t.commenter, t.closed, t.num_replies, t.pinned, t.important, t.solved AS answer, t.first_comment_id, f.id AS forum_id, f.forum_name, f.moderators, fp.comment, s.user_id AS is_subscribed FROM '.$db->prefix.'threads AS t INNER JOIN '.$db->prefix.'forums AS f ON f.id=t.forum_id LEFT JOIN '.$db->prefix.'thread_subscriptions AS s ON (t.id=s.thread_id AND s.user_id='.$luna_user['id'].') LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=f.id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.read_forum IS NULL OR fp.read_forum=1) AND t.id='.$id.' AND t.moved_to IS NULL') or error('Unable to fetch thread info', __FILE__, __LINE__, $db->error());

if (!$db->num_rows($result))
	message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

$cur_thread = $db->fetch_assoc($result);
$started_by = $cur_thread['commenter'];

// Sort out who the moderators are and if we are currently a moderator (or an admin)
$mods_array = ($cur_thread['moderators'] != '') ? unserialize($cur_thread['moderators']) : array();
$is_admmod = ($luna_user['g_id'] == LUNA_ADMIN || ($luna_user['g_moderator'] == '1' && array_key_exists($luna_user['username'], $mods_array))) ? true : false;
if ($is_admmod)
$admin_ids = get_admin_ids();

if ($cur_thread['closed'] == '0') {
	if (($cur_thread['comment'] == '' && $luna_user['g_comment'] == '1') || $cur_thread['comment'] == '1' || $is_admmod)
		$comment_link = "\t\t\t".'<a class="btn btn-primary btn-comment" href="comment.php?tid='.$id.'">'.__('Comment', 'luna').'</a>'."\n";
	else
		$comment_link = '';
} else {
	$comment_link = '<a class="btn disabled btn-danger btn-comment"><span class="fa fa-fw fa-lock"></span></a>';

	if ($is_admmod)
		$comment_link .= '<a class="btn btn-primary btn-comment" href="comment.php?tid='.$id.'">'.__('Comment', 'luna').'</a>';

	$comment_link = $comment_link."\n";
}


// Add/update this thread in our list of tracked threads
if (!$luna_user['is_guest']) {
	$tracked_threads = get_tracked_threads();
	$tracked_threads['threads'][$id] = time();
	set_tracked_threads($tracked_threads);
}


// Determine the comment offset (based on $_GET['p'])
$num_pages = ceil(($cur_thread['num_replies'] + 1) / $luna_user['disp_comments']);

$p = (!isset($_GET['p']) || $_GET['p'] <= 1 || $_GET['p'] > $num_pages) ? 1 : intval($_GET['p']);
$start_from = $luna_user['disp_comments'] * ($p - 1);

// Generate paging links
$paging_links = forum_paginate($num_pages, $p, 'thread.php?id='.$id);

$comment_field = false;
if (($cur_thread['comment'] == '1' || ($cur_thread['comment'] == '' && $luna_user['g_comment'] == '1')) && ($cur_thread['closed'] == '0' || $is_admmod)) {
	$required_fields = array('req_message' => __('Message', 'luna'));
	if ($luna_user['is_guest']) {
		$required_fields['req_username'] = __('Name', 'luna');
		if ($luna_config['o_force_guest_email'] == '1')
			$required_fields['req_email'] = __('Email', 'luna');
	}

	$comment_field = true;
}

if ($luna_config['o_censoring'] == '1')
	$cur_thread['subject'] = censor_words($cur_thread['subject']);

if ($luna_config['o_feed_type'] == '1')
	$page_head = array('feed' => '<link rel="alternate" type="application/rss+xml" href="extern.php?action=feed&amp;tid='.$id.'&amp;type=rss" title="'.__('RSS thread feed', 'luna').'" />');
elseif ($luna_config['o_feed_type'] == '2')
	$page_head = array('feed' => '<link rel="alternate" type="application/atom+xml" href="extern.php?action=feed&amp;tid='.$id.'&amp;type=atom" title="'.__('Atom thread feed', 'luna').'" />');

$thread_actions = array();

if (!$luna_user['is_guest'] && $luna_config['o_thread_subscriptions'] == '1') {
	$token_url = '&amp;csrf_token='.luna_csrf_token();

	if ($cur_thread['is_subscribed'])
		$thread_actions[] = '<a href="misc.php?action=unsubscribe&amp;tid='.$id.$token_url.'">'.__('Unsubscribe', 'luna').'</a>';
	else
		$thread_actions[] = '<a href="misc.php?action=subscribe&amp;tid='.$id.$token_url.'">'.__('Subscribe', 'luna').'</a>';
}

$result = $db->query('SELECT f.solved FROM '.$db->prefix.'forums AS f LEFT JOIN '.$db->prefix.'threads AS t ON (f.id = t.forum_id) WHERE t.id='.$id) or error('Unable to fetch forum info', __FILE__, __LINE__, $db->error());

$cur_forum = $db->fetch_assoc($result);

$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), luna_htmlspecialchars($cur_thread['forum_name']), luna_htmlspecialchars($cur_thread['subject']));
if (!$pid)
	define('LUNA_ALLOW_INDEX', 1);
define('LUNA_ACTIVE_PAGE', 'thread');
require load_page('header.php');

require LUNA_ROOT.'include/parser.php';

$comment_count = 0; // Keep track of comment numbers

// Retrieve a list of comment IDs, LIMIT is (really) expensive so we only fetch the IDs here then later fetch the remaining data
if (!$luna_user['is_admmod'])
	$result = $db->query('SELECT id FROM '.$db->prefix.'comments WHERE soft = 0 AND thread_id='.$id.' ORDER BY id LIMIT '.$start_from.','.$luna_user['disp_comments']) or error('Unable to fetch comment IDs', __FILE__, __LINE__, $db->error());
else
	$result = $db->query('SELECT id FROM '.$db->prefix.'comments WHERE thread_id='.$id.' ORDER BY id LIMIT '.$start_from.','.$luna_user['disp_comments']) or error('Unable to fetch comment IDs', __FILE__, __LINE__, $db->error());

$comment_ids = array();
for ($i = 0;$cur_comment_id = $db->result($result, $i);$i++)
	$comment_ids[] = $cur_comment_id;

$token_url = '&amp;csrf_token='.luna_csrf_token();

if (empty($comment_ids))
	error('The comment table and thread table seem to be out of sync!', __FILE__, __LINE__);

$cur_index = 1;

require load_page('thread.php');

// Increment "num_views" for thread
if ($luna_config['o_thread_views'] == '1')
	$db->query('UPDATE '.$db->prefix.'threads SET num_views=num_views+1 WHERE id='.$id) or error('Unable to update thread', __FILE__, __LINE__, $db->error());

$forum_id = $cur_thread['forum_id'];
$footer_style = 'thread';

require load_page('footer.php');
