<?php

/*
 * Copyright (C) 2014-2016 Luna
 * Based on work by Adaur (2010), Vincent Garnier, Connorhd and David 'Chacmool' Djurback
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

define('LUNA_ROOT', dirname(__FILE__).'/');
require LUNA_ROOT.'include/common.php';
require LUNA_ROOT.'include/parser.php';
require LUNA_ROOT.'include/me_functions.php';

$inbox = 1;

// No guest here !
if ($luna_user['is_guest'])
	message(__('You do not have permission to access this page.', 'luna'));

// User enable PM ?
if (!$luna_user['use_inbox'] == '1')
	message(__('You do not have permission to access this page.', 'luna'));

// Are we allowed to use this ?
if (!$luna_config['o_enable_inbox'] =='1' || $luna_user['g_inbox'] == '0')
	message(__('You do not have permission to access this page.', 'luna'));

$id = $luna_user['id'];

// User block
$avatar_user_card = draw_user_avatar($luna_user['id']);

// Get the message's and thread's id
$mid = isset($_REQUEST['mid']) ? intval($_REQUEST['mid']) : '0';
$tid = isset($_REQUEST['tid']) ? intval($_REQUEST['tid']) : '0';
$pid = isset($_REQUEST['pid']) ? intval($_REQUEST['pid']) : '0';

$delete_all = '0';

$thread_msg = isset($_REQUEST['all_thread']) ? intval($_REQUEST['all_thread']) : '0';
$delete_all = isset($_POST['delete_all']) ? '1' : '0';

if ($pid) {
	$result = $db->query('SELECT shared_id FROM '.$db->prefix.'messages WHERE id='.$mid) or error('Unable to fetch comment info', __FILE__, __LINE__, $db->error());
	if (!$db->num_rows($result))
		message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'));

	$id = $db->result($result);

	// Determine on what page the comment is located (depending on $luna_user['disp_comments'])
	$result = $db->query('SELECT id FROM '.$db->prefix.'messages WHERE shared_id='.$id.' AND owner='.$luna_user['id'].' ORDER BY commented') or error('Unable to fetch comment info', __FILE__, __LINE__, $db->error());
	$num_comments = $db->num_rows($result);

	for ($i = 0; $i < $num_comments; ++$i) {
		$cur_id = $db->result($result, $i);
		if ($cur_id == $pid)
			break;
	}
	++$i; // we started at 0

	$_REQUEST['p'] = ceil($i / $luna_user['disp_comments']);
}

// Replace num_replies' feature by a query :-)
$result = $db->query('SELECT COUNT(*) FROM '.$db->prefix.'messages WHERE shared_id='.$tid.' AND owner='.$luna_user['id']) or error('Unable to count the messages', __FILE__, __LINE__, $db->error());
list($num_replies) = $db->fetch_row($result);

// Determine the comment offset (based on $_GET['p'])
$num_pages = ceil($num_replies / $luna_user['disp_comments']);

// Page ?
$page = (!isset($_REQUEST['p']) || $_REQUEST['p'] <= '1') ? '1' : intval($_REQUEST['p']);
$start_from = $luna_user['disp_comments'] * ($page - 1);

// Check that $mid looks good
if ($mid <= 0)
	message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'));

// Action ?
$action = ((isset($_REQUEST['action']) && ($_REQUEST['action'] == 'delete')) ? $_REQUEST['action'] : '');

// Delete a single message or a full thread
if ($action == 'delete') {
	// Make sure they got here from the site
	confirm_referrer('viewinbox.php');

	if (isset($_POST['delete_comply'])) {
		if ($thread_msg > '1' || $thread_msg < '0')
			message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'));

		if ($thread_msg == '0') {
			if ($luna_user['is_admmod']) {
				if ($delete_all == '1') {
					$result_msg = $db->query('SELECT message FROM '.$db->prefix.'messages WHERE id='.$mid) or error('Unable to get the informations of the message', __FILE__, __LINE__, $db->error());

					if (!$db->num_rows($result_msg))
						message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'));

					$delete_msg = $db->fetch_assoc($result_msg);

					// To devs: maybe this query is unsafe? Maybe you know how to secure it? I'm open to your suggestions ;) !
					$result_ids = $db->query('SELECT id FROM '.$db->prefix.'messages WHERE message=\''.$db->escape($delete_msg).'\'') or error('Unable to get the informations of the message', __FILE__, __LINE__, $db->error());

					if (!$db->num_rows($result_ids))
						message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'));

					$ids_msg[] = $db->result($result_ids);

					// Finally, delete the messages!
					$db->query('DELETE FROM '.$db->prefix.'messages WHERE id IN ('.$ids_msg.')') or error('Unable to delete the message', __FILE__, __LINE__, $db->error());
				} else
					$db->query('DELETE FROM '.$db->prefix.'messages WHERE id='.$mid) or error('Unable to delete the message', __FILE__, __LINE__, $db->error());
			} else {
				$result = $db->query('SELECT owner FROM '.$db->prefix.'messages WHERE id='.$mid) or error('Unable to delete the message', __FILE__, __LINE__, $db->error());
				$owner = $db->result($result);

				if($owner != $luna_user['id']) // Double check : hackers are everywhere =)
					message(__('You do not have permission to access this page.', 'luna'));

				$db->query('DELETE FROM '.$db->prefix.'messages WHERE id='.$mid) or error('Unable to delete the message', __FILE__, __LINE__, $db->error());
			}
		} else {
			if ($luna_user['is_admmod']) {
				if ($delete_all == '1') {
					$result_ids = $db->query('SELECT DISTINCT owner FROM '.$db->prefix.'messages WHERE shared_id='.$tid) or error('Unable to get the informations of the message', __FILE__, __LINE__, $db->error());

					if (!$db->num_rows($result_ids))
						message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'));

					while ($user_ids = $db->fetch_assoc($result_ids)) {
						$ids_users[] = $user_ids['owner'];
					}

					$ids_users = implode(',', $ids_users);

					$db->query('UPDATE '.$db->prefix.'users SET num_inbox=num_inbox-1 WHERE id IN('.$ids_users.')') or error('Unable to update user', __FILE__, __LINE__, $db->error());
					$db->query('DELETE FROM '.$db->prefix.'messages WHERE shared_id='.$tid) or error('Unable to delete the message', __FILE__, __LINE__, $db->error());
				} else {
					$db->query('DELETE FROM '.$db->prefix.'messages WHERE shared_id='.$tid.' AND owner='.$luna_user['id']) or error('Unable to delete the message', __FILE__, __LINE__, $db->error());
					$db->query('UPDATE '.$db->prefix.'messages SET receiver=REPLACE(receiver,\''.$db->escape($luna_user['username']).'\',\''.$db->escape($luna_user['username'].' Deleted').'\') WHERE receiver LIKE \'%'.$db->escape($luna_user['username']).'%\' AND shared_id='.$tid) or error('Unable to update private messages', __FILE__, __LINE__, $db->error());
					$db->query('UPDATE '.$db->prefix.'users SET num_inbox=num_inbox-1 WHERE id='.$luna_user['id']) or error('Unable to update user', __FILE__, __LINE__, $db->error());
				}
			} else {
				$result = $db->query('SELECT owner FROM '.$db->prefix.'messages WHERE id='.$mid) or error('Unable to delete the message', __FILE__, __LINE__, $db->error());
				$owner = $db->result($result);

				if($owner != $luna_user['id']) // Double check : hackers are everywhere =)
					message(__('You do not have permission to access this page.', 'luna'));

				$db->query('DELETE FROM '.$db->prefix.'messages WHERE id='.$mid) or error('Unable to delete the message', __FILE__, __LINE__, $db->error());
				$db->query('UPDATE '.$db->prefix.'users SET num_inbox=num_inbox-1 WHERE id='.$luna_user['id']) or error('Unable to update user', __FILE__, __LINE__, $db->error());
			}
		}

		// Redirect
		redirect('inbox.php');
	} else {
		$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), __('Delete message', 'luna'));

		define('LUNA_ACTIVE_PAGE', 'pm');
		require load_page('header.php');

		// If you're not the owner of the message, you can't delete it.
		$result = $db->query('SELECT owner, show_message, commented, sender, message, hide_smilies FROM '.$db->prefix.'messages WHERE id='.$mid) or error('Unable to delete the message', __FILE__, __LINE__, $db->error());
		$cur_delete = $db->fetch_assoc($result);

		if($cur_delete['owner'] != $luna_user['id'] && !$luna_user['is_admmod'])
			message(__('You do not have permission to access this page.', 'luna'));

		$cur_delete['message'] = parse_message($cur_delete['message']);

		require load_page('inbox-delete-comment.php');

		require load_page('footer.php');
	}
} else {
	// Start building page
	$result_receivers = $db->query('SELECT DISTINCT receiver, owner, sender_id FROM '.$db->prefix.'messages WHERE shared_id='.$tid) or error('Unable to get the informations of the message', __FILE__, __LINE__, $db->error());

	if (!$db->num_rows($result_receivers))
			message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'));

	$owner = array();

	while ($receiver = $db->fetch_assoc($result_receivers)) {
		$r_usernames = $receiver['receiver'];
		$owner[] = $receiver['owner'];
		$uid = $receiver['sender_id'];
	}

	$r_usernames = str_replace('Deleted', __('Deleted', 'luna'), $r_usernames);

	$result = $db->query('SELECT subject FROM '.$db->prefix.'messages WHERE shared_id='.$tid.' AND show_message=1') or error('Unable to fetch comment info', __FILE__, __LINE__, $db->error());

	if (!$db->num_rows($result))
		message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'));

	$p_subject = $db->result($result);

	$messageh2 = luna_htmlspecialchars($p_subject).' '.__('with', 'luna').' '.luna_htmlspecialchars($r_usernames);

	$required_fields = array('req_message' => __('Message', 'luna'));

	$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), __('Private Messages', 'luna'), __('View a private discussion', 'luna'));

	define('LUNA_ACTIVE_PAGE', 'pm');
	require load_page('header.php');

	if(!in_array($luna_user['id'], $owner) && !$luna_user['is_admmod'])
		message(__('You do not have permission to access this page.', 'luna'));

	$comment_count = '0'; // Keep track of comment numbers

	$db->query('UPDATE '.$db->prefix.'messages SET showed=1 WHERE shared_id='.$tid.' AND show_message=1 AND owner='.$luna_user['id']) or error('Unable to update the status of the message', __FILE__, __LINE__, $db->error());

	$result = $db->query('SELECT m.id AS mid, m.shared_id, m.subject, m.sender_ip, m.message, m.hide_smilies, m.commented, m.showed, m.sender, m.sender_id, m.owner, u.id, u.group_id AS g_id, g.g_user_title, u.username, u.registered, u.email, u.title, u.url, u.location, u.email_setting, u.num_comments, u.admin_note, u.signature, u.use_inbox, o.user_id AS is_online FROM '.$db->prefix.'messages AS m, '.$db->prefix.'users AS u LEFT JOIN '.$db->prefix.'online AS o ON (o.user_id=u.id AND o.idle=0) LEFT JOIN '.$db->prefix.'groups AS g ON (u.group_id=g.g_id) WHERE u.id=m.sender_id AND m.shared_id='.$tid.' AND m.owner='.$luna_user['id'].' ORDER BY m.commented LIMIT '.$start_from.','.$luna_user['disp_comments']) or error('Unable to get the message and the informations of the user', __FILE__, __LINE__, $db->error());

	if (!$db->num_rows($result))
		message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'));

	$reply_link = '<a href="new_inbox.php?reply='.$tid.'">'.__('Reply', 'luna').'</a>';

	$paging_links = forum_paginate($num_pages, $page, 'viewinbox.php?tid='.$tid.'&amp;mid='.$mid);

	require load_page('inbox-view.php');

	require load_page('footer.php');
}
?>
