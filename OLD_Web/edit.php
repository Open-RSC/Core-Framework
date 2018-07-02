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

// Fetch some info about the comment, the thread and the forum
$result = $db->query('SELECT f.id AS fid, f.forum_name, f.moderators, f.color, fp.comment, fp.create_threads, t.id AS tid, t.subject, t.commented, t.first_comment_id, t.pinned, t.closed, p.commenter, p.commenter_id, p.message, p.admin_note, p.hide_smilies FROM '.$db->prefix.'comments AS p INNER JOIN '.$db->prefix.'threads AS t ON t.id=p.thread_id INNER JOIN '.$db->prefix.'forums AS f ON f.id=t.forum_id LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=f.id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.read_forum IS NULL OR fp.read_forum=1) AND p.id='.$id) or error('Unable to fetch comment info', __FILE__, __LINE__, $db->error());
if (!$db->num_rows($result))
	message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

$cur_comment = $db->fetch_assoc($result);

// Sort out who the moderators are and if we are currently a moderator (or an admin)
$mods_array = ($cur_comment['moderators'] != '') ? unserialize($cur_comment['moderators']) : array();
$is_admmod = ($luna_user['g_id'] == LUNA_ADMIN || ($luna_user['g_moderator'] == '1' && array_key_exists($luna_user['username'], $mods_array))) ? true : false;

$can_edit_subject = $id == $cur_comment['first_comment_id'];

if ($luna_config['o_censoring'] == '1') {
	$cur_comment['subject'] = censor_words($cur_comment['subject']);
	$cur_comment['message'] = censor_words($cur_comment['message']);
}

// Do we have permission to edit this comment?
if (($luna_user['g_edit_comments'] == '0' ||
	$cur_comment['commenter_id'] != $luna_user['id'] ||
	$cur_comment['closed'] == '1') &&
	!$is_admmod)
	message(__('You do not have permission to access this page.', 'luna'), false, '403 Forbidden');

if ($is_admmod && $luna_user['g_id'] != LUNA_ADMIN && in_array($cur_comment['commenter_id'], get_admin_ids()))
	message(__('You do not have permission to access this page.', 'luna'), false, '403 Forbidden');

// Start with a clean slate
$errors = array();

if (isset($_POST['form_sent'])) {
	// Make sure they got here from the site
	confirm_referrer('edit.php');

	// If it's a thread it must contain a subject
	if ($can_edit_subject) {
		$subject = luna_trim($_POST['req_subject']);

		if ($luna_config['o_censoring'] == '1')
			$censored_subject = luna_trim(censor_words($subject));

		if ($subject == '')
			$errors[] = __('Threads must contain a subject.', 'luna');
		elseif ($luna_config['o_censoring'] == '1' && $censored_subject == '')
			$errors[] = __('Threads must contain a subject. After applying censoring filters, your subject was empty.', 'luna');
		elseif (luna_strlen($subject) > 70)
			$errors[] = __('Subjects cannot be longer than 70 characters.', 'luna');
		elseif ($luna_config['o_subject_all_caps'] == '0' && is_all_uppercase($subject) && !$luna_user['is_admmod'])
			$errors[] = __('Subjects cannot contain only capital letters.', 'luna');
	}

	// Clean up admin_note from POST
    if ($luna_user['g_id'] == LUNA_ADMIN) {
	   $admin_note = luna_linebreaks(luna_trim($_POST['admin_note']));
    } else {
	   $admin_note = $cur_comment['admin_note'];
    }

	// Clean up message from POST
	$message = luna_linebreaks(luna_trim($_POST['req_message']));

	// Here we use strlen() not luna_strlen() as we want to limit the comment to LUNA_MAX_COMMENT_SIZE bytes, not characters
	if (strlen($message) > LUNA_MAX_COMMENT_SIZE)
		$errors[] = sprintf(__('Comments cannot be longer than %s bytes.', 'luna'), forum_number_format(LUNA_MAX_COMMENT_SIZE));
	elseif ($luna_config['o_message_all_caps'] == '0' && is_all_uppercase($message) && !$luna_user['is_admmod'])
		$errors[] = __('Comments cannot contain only capital letters.', 'luna');

	// Validate BBCode syntax
	require LUNA_ROOT.'include/parser.php';
	$message = preparse_bbcode($message, $errors);

	if (empty($errors)) {
		if ($message == '')
			$errors[] = __('You must enter a message.', 'luna');
		elseif ($luna_config['o_censoring'] == '1') {
			// Censor message to see if that causes problems
			$censored_message = luna_trim(censor_words($message));

			if ($censored_message == '')
				$errors[] = __('You must enter a message. After applying censoring filters, your message was empty.', 'luna');
		}
	}

	$hide_smilies = isset($_POST['hide_smilies']) ? '1' : '0';
	$pin_thread = isset($_POST['pin_thread']) ? '1' : '0';
	if (!$is_admmod)
		$pin_thread = $cur_comment['pinned'];

	// Replace four-byte characters (MySQL cannot handle them)
	$message = strip_bad_multibyte_chars($message);

	// Did everything go according to plan?
	if (empty($errors) && !isset($_POST['preview'])) {
		$edited_sql = (!isset($_POST['silent']) || !$is_admmod) ? ', edited='.time().', edited_by=\''.$db->escape($luna_user['username']).'\'' : '';

		require LUNA_ROOT.'include/search_idx.php';

		if ($can_edit_subject) {
			// Update the thread and any redirect threads
			$db->query('UPDATE '.$db->prefix.'threads SET subject=\''.$db->escape($subject).'\', pinned='.$pin_thread.' WHERE id='.$cur_comment['tid'].' OR moved_to='.$cur_comment['tid']) or error('Unable to update thread', __FILE__, __LINE__, $db->error());

			// We changed the subject, so we need to take that into account when we update the search words
			update_search_index('edit', $id, $message, $subject);
		} else
			update_search_index('edit', $id, $message);

		// Update the comment
		$db->query('UPDATE '.$db->prefix.'comments SET message=\''.$db->escape($message).'\', hide_smilies='.$hide_smilies.$edited_sql.', admin_note=\''.$db->escape($admin_note).'\' WHERE id='.$id) or error('Unable to update comment', __FILE__, __LINE__, $db->error());

		redirect('thread.php?pid='.$id.'#p'.$id);
	}
}

$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), __('Edit comment', 'luna'));
$required_fields = array('req_subject' => __('Subject', 'luna'), 'req_message' => __('Message', 'luna'));
$focus_element = array('edit', 'req_message');
define('LUNA_ACTIVE_PAGE', 'edit');
require load_page('header.php');

$cur_index = 1;

require load_page('edit.php');

require load_page('footer.php');
