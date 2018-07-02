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

$id = isset($_GET['id']) ? intval($_GET['id']) : 0;
if ($id < 1)
	message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

$action = isset($_GET['action']) ? $_GET['action'] : 0;

// Fetch some info about the comment, the thread and the forum
$result = $db->query('SELECT f.id AS fid, f.forum_name, f.moderators, fp.comment, fp.create_threads, t.id AS tid, t.subject, t.first_comment_id, t.closed, p.commented, p.commenter, p.commenter_id, p.message, p.hide_smilies FROM '.$db->prefix.'comments AS p INNER JOIN '.$db->prefix.'threads AS t ON t.id=p.thread_id INNER JOIN '.$db->prefix.'forums AS f ON f.id=t.forum_id LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=f.id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.read_forum IS NULL OR fp.read_forum=1) AND p.id='.$id) or error('Unable to fetch comment info', __FILE__, __LINE__, $db->error());
if (!$db->num_rows($result))
	message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

$cur_comment = $db->fetch_assoc($result);

if ($luna_config['o_censoring'] == '1')
	$cur_comment['subject'] = censor_words($cur_comment['subject']);

// Sort out who the moderators are and if we are currently a moderator (or an admin)
$mods_array = ($cur_comment['moderators'] != '') ? unserialize($cur_comment['moderators']) : array();
$is_admmod = ($luna_user['g_id'] == LUNA_ADMIN || ($luna_user['g_moderator'] == '1' && array_key_exists($luna_user['username'], $mods_array))) ? true : false;

$is_thread_comment = ($id == $cur_comment['first_comment_id']) ? true : false;

// Do we have permission to edit this comment?
if (($luna_user['g_delete_comments'] == '0' ||
	($luna_user['g_delete_threads'] == '0' && $is_thread_comment) ||
	$cur_comment['commenter_id'] != $luna_user['id'] ||
	$cur_comment['closed'] == '1') &&
	!$is_admmod)
	message(__('You do not have permission to access this page.', 'luna'), false, '403 Forbidden');

if ($is_admmod && $luna_user['g_id'] != LUNA_ADMIN && in_array($cur_comment['commenter_id'], get_admin_ids()))
	message(__('You do not have permission to access this page.', 'luna'), false, '403 Forbidden');

// Hide comments
if (isset($_POST['soft_delete'])) {
	// Make sure they got here from the site
	confirm_referrer('delete.php');

	require LUNA_ROOT.'include/search_idx.php';

	if ($is_thread_comment) {
		// Delete the thread and all of its comments
		delete_thread($cur_comment['tid'], "soft");
		update_forum($cur_comment['fid']);

		redirect('viewforum.php?id='.$cur_comment['fid']);
	} else {
		// Delete just this one comment
		$db->query('UPDATE '.$db->prefix.'comments SET soft = 1 WHERE id='.$id) or error('Unable to hide comment', __FILE__, __LINE__, $db->error());
		update_forum($cur_comment['fid']);

		// Redirect towards the previous comment
		$result = $db->query('SELECT id FROM '.$db->prefix.'comments WHERE thread_id='.$cur_comment['tid'].' AND id < '.$id.' ORDER BY id DESC LIMIT 1') or error('Unable to fetch comment info', __FILE__, __LINE__, $db->error());
		$comment_id = $db->result($result);

		redirect('thread.php?pid='.$comment_id.'#p'.$comment_id);
	}
}

// Unhide
if (isset($_POST['reset'])) {
	// Make sure they got here from the site
	confirm_referrer('delete.php');

	require LUNA_ROOT.'include/search_idx.php';

	if ($is_thread_comment) {
		// Reset the thread and all of its comments
		delete_thread($cur_comment['tid'], "reset");
		update_forum($cur_comment['fid']);

		redirect('viewforum.php?id='.$cur_comment['fid']);
	} else {
		// Reset just this one comment
		$db->query('UPDATE '.$db->prefix.'comments SET soft = 0 WHERE id='.$id) or error('Unable to unhide comment', __FILE__, __LINE__, $db->error());
		update_forum($cur_comment['fid']);

		// Redirect towards the comment
		redirect('thread.php?pid='.$id.'#p'.$id);
	}
}

if (isset($_POST['delete'])) {
	// Make sure they got here from the site
	confirm_referrer('delete.php');

	require LUNA_ROOT.'include/search_idx.php';

	if ($is_thread_comment) {
		// Delete the thread and all of its comments
		delete_thread($cur_comment['tid'], "hard");
		update_forum($cur_comment['fid']);

		redirect('viewforum.php?id='.$cur_comment['fid']);
	} else {
		// Delete just this one comment
		delete_comment($id, $cur_comment['tid'], $cur_comment['commenter_id']);
		update_forum($cur_comment['fid']);

		// Redirect towards the previous comment
		$result = $db->query('SELECT id FROM '.$db->prefix.'comments WHERE thread_id='.$cur_comment['tid'].' AND id < '.$id.' ORDER BY id DESC LIMIT 1') or error('Unable to fetch comment info', __FILE__, __LINE__, $db->error());
		$comment_id = $db->result($result);

		redirect('thread.php?pid='.$comment_id.'#p'.$comment_id);
	}
}

$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), __('Delete comment', 'luna'));
define ('LUNA_ACTIVE_PAGE', 'delete');

require LUNA_ROOT.'include/parser.php';
$cur_comment['message'] = parse_message($cur_comment['message']);

require load_page('header.php');

if ($action == "reset")
	require load_page('reset.php');
if ($action == "soft")
	require load_page('soft.php');
if ($action == "delete")
	require load_page('delete.php');

require load_page('footer.php');
