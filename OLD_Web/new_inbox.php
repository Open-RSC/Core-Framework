<?php

/*
 * Copyright (C) 2014-2016 Luna
 * Based on work by Adaur (2010), Vincent Garnier, Connorhd and David 'Chacmool' Djurback
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

define('LUNA_ROOT', dirname(__FILE__).'/');
require LUNA_ROOT.'include/common.php';
require LUNA_ROOT.'include/email.php';
require LUNA_ROOT.'include/me_functions.php';

// No guest here !
if ($luna_user['is_guest'])
	message(__('You do not have permission to access this page.', 'luna'));

// User enable Inbox ?
if (!$luna_user['use_inbox'] == '1')
	message(__('You do not have permission to access this page.', 'luna'));

// Are we allowed to use this ?
if (!$luna_config['o_enable_inbox'] == '1' || $luna_user['g_inbox'] == '0')
	message(__('You do not have permission to access this page.', 'luna'));

$id = $luna_user['id'];

$p_destinataire = '';
$p_contact = '';
$p_subject = '';
$p_message = '';

// Clean informations
$r = (isset($_REQUEST['reply']) ? intval($_REQUEST['reply']) : '0');
$q = (isset($_REQUEST['quote']) ? intval($_REQUEST['quote']) : '0');
$edit = isset($_REQUEST['edit']) ? intval($_REQUEST['edit']) : '0';
$tid = isset($_REQUEST['tid']) ? intval($_REQUEST['tid']) : '0';
$mid = isset($_REQUEST['mid']) ? intval($_REQUEST['mid']) : '0';

$errors = array();

if (!empty($r) && !isset($_POST['form_sent'])) { // It's a reply
	// Make sure they got here from the site
	confirm_referrer(array('new_inbox.php', 'viewinbox.php'));

	$result = $db->query('SELECT DISTINCT owner, receiver FROM '.$db->prefix.'messages WHERE shared_id='.$r) or error('Unable to get the informations of the message', __FILE__, __LINE__, $db->error());

	if (!$db->num_rows($result))
		message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'));

	$p_ids = array();

	while ($arry_dests = $db->fetch_assoc($result)) {
		if ($arry_dests['receiver'] == '0')
			message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'));

		$p_ids[] = $arry_dests['owner'];
	}

	if (!in_array($luna_user['id'], $p_ids)) // Are we in the array? If not, we add ourselves
		$p_ids[] = $luna_user['id'];

	$p_ids = implode(', ', $p_ids);

	$result_subject = $db->query('SELECT subject FROM '.$db->prefix.'messages WHERE shared_id='.$r.' AND show_message=1') or error('Unable to fetch comment info', __FILE__, __LINE__, $db->error());

	if (!$db->num_rows($result_subject))
		message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'));

	$p_subject = $db->result($result_subject);

	$result_username = $db->query('SELECT username FROM '.$db->prefix.'users WHERE id IN ('.$p_ids.')') or error('Unable to find the owners of the message', __FILE__, __LINE__, $db->error());

	if (!$db->num_rows($result_username))
		message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'));

	$p_destinataire = array();

	while ($username_result = $db->fetch_assoc($result_username)) {
		$p_destinataire[] = $username_result['username'];
	}

	$p_destinataire = implode(', ', $p_destinataire);

	if (!empty($q) && $q > '0') { // It's a reply with a quote
		// Get message info
		$result = $db->query('SELECT sender, message FROM '.$db->prefix.'messages WHERE id='.$q.' AND owner='.$luna_user['id']) or error('Unable to find the informations of the message', __FILE__, __LINE__, $db->error());

		if (!$db->num_rows($result))
			message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'));

		$re_message = $db->fetch_assoc($result);

		// Quote the message
		$p_message = '[quote='.$re_message['sender'].']'.$re_message['message'].'[/quote]';
	}
} else if (!empty($edit) && !isset($_POST['form_sent'])) { // It's an edit
	// Make sure they got here from the site
	confirm_referrer(array('new_inbox.php', 'viewinbox.php'));

	// Check that $edit looks good
	if ($edit <= 0)
		message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'));

	$result = $db->query('SELECT sender_id, message, receiver FROM '.$db->prefix.'messages WHERE id='.$edit) or error('Unable to get the informations of the message', __FILE__, __LINE__, $db->error());

	if (!$db->num_rows($result))
		message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'));

	$edit_msg = $db->fetch_assoc($result);

	// If you're not the owner of this message, why do you want to edit it?
	if ($edit_msg['sender_id'] != $luna_user['id'] && !$luna_user['is_admmod'] || $edit_msg['receiver'] == '0' && !$luna_user['is_admmod'])
		message(__('You do not have permission to access this page.', 'luna'));

	// Insert the message
	$p_message = censor_words($edit_msg['message']);
} else if (isset($_POST['form_sent'])) { // The comment button has been pressed
	// Make sure they got here from the site
	confirm_referrer(array('new_inbox.php', 'viewinbox.php'));

	$hide_smilies = isset($_POST['hide_smilies']) ? '1' : '0';

	// Make sure form_user is correct
	if ($_POST['form_user'] != $luna_user['username'])
		message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'));

	// Flood protection by Newman
	if (!isset($_SESSION))
		session_start();

	if (isset($_SESION['last_session_request']))
		if(!$edit && !isset($_POST['preview']) && $_SESSION['last_session_request'] > time() - $luna_user['g_comment_flood'])
			$errors[] = sprintf( __('At least % seconds have to pass between sends. Please wait a little while and try send the message again.', 'luna'), $luna_user['g_comment_flood'] );

	// Check users boxes
	if ($luna_user['g_inbox_limit'] != '0' && !$luna_user['is_admmod'] && $luna_user['num_inbox'] >= $luna_user['g_inbox_limit'])
		$errors[] = __('Can\'t save message, your boxes are full.', 'luna');

	// Build receivers list
	$p_destinataire = isset($_POST['p_username']) ? luna_trim($_POST['p_username']) : '';
	$p_contact = isset($_POST['p_contact']) ? luna_trim($_POST['p_contact']) : '';
	$dest_list = explode(', ', $p_destinataire);

	if (!in_array($luna_user['username'], $dest_list))
		$dest_list[] = $luna_user['username'];

	if ($p_contact != '0')
		$dest_list[] = $p_contact;

	$dest_list = array_map('luna_trim', $dest_list);
	$dest_list = array_unique($dest_list);

	foreach ($dest_list as $k=>$v) {
		if ($v == '') unset($dest_list[$k]);
	}

	 if (count($dest_list) < '1' && $edit == '0')
		$errors[] = __('You must give at least one receiver', 'luna');
		elseif (count($dest_list) > $luna_config['o_max_receivers'])
		$errors[] = sprintf(__('You can send a message at the same time only to %s receivers maximum.', 'luna'), $luna_config['o_max_receivers']-1);

	$destinataires = array(); $i = '0';
	$list_ids = array();
	$list_usernames = array();
	foreach ($dest_list as $destinataire) {
		// Get receiver infos
		$result_username = $db->query("SELECT u.id, u.username, u.email, u.notify_inbox, u.notify_inbox_full, u.use_inbox, u.num_inbox, g.g_id, g.g_inbox_limit, g.g_inbox FROM ".$db->prefix."users AS u INNER JOIN ".$db->prefix."groups AS g ON (u.group_id=g.g_id) LEFT JOIN ".$db->prefix."messages AS ib ON (ib.owner=u.id) WHERE u.id!=1 AND u.username='".$db->escape($destinataire)."' GROUP BY u.username, u.id, g.g_id") or error("Unable to get user ID", __FILE__, __LINE__, $db->error());

		// List users infos
		if ($destinataires[$i] = $db->fetch_assoc($result_username)) {
			// Begin to build the IDs' list - Thanks to Yacodo!
			$list_ids[] = $destinataires[$i]['id'];
			// Did the user left?
			if (!empty($r)) {
				$result = $db->query('SELECT 1 FROM '.$db->prefix.'messages WHERE shared_id='.$r.' AND show_message=1 AND owner='.$destinataires[$i]['id']) or error('Unable to get the informations of the message', __FILE__, __LINE__, $db->error());
				if (!$db->num_rows($result))
					$errors[] = sprintf(__('%s has left the conversation.', 'luna'), luna_htmlspecialchars($destinataire));
			}
			// Begin to build usernames' list
			$list_usernames[] = $destinataires[$i]['username'];
			// Receivers enable Inbox ?
			if (!$destinataires[$i]['use_inbox'] == '1' || !$destinataires[$i]['g_inbox'] == '1')
				$errors[] = sprintf(__('%s disabled the private messages.', 'luna'), luna_htmlspecialchars($destinataire));
			// Check receivers boxes
			elseif ($destinataires[$i]['g_id'] > LUNA_GUEST && $destinataires[$i]['g_inbox_limit'] != '0' && $destinataires[$i]['num_inbox'] >= $destinataires[$i]['g_inbox_limit'])
				$errors[] = sprintf(__('%s inbox is full, you can not send you message to this user.', 'luna'), luna_htmlspecialchars($destinataire));
		} else
			$errors[] = sprintf(__('There\'s no user with the username "%s".', 'luna'), luna_htmlspecialchars($destinataire));
		$i++;
	}

	// Build IDs' & usernames' list : the end
	$ids_list = implode(', ', $list_ids);
	$usernames_list = implode(', ', $list_usernames);

	// Check subject
	$p_subject = luna_trim($_POST['req_subject']);

	if ($p_subject == '' && $edit == '0')
		$errors[] = __('Threads must contain a subject.', 'luna');
	elseif (luna_strlen($p_subject) > '70')
		$errors[] = __('Subjects cannot be longer than 70 characters.', 'luna');
	elseif ($luna_config['o_subject_all_caps'] == '0' && strtoupper($p_subject) == $p_subject && $luna_user['is_admmod'])
		$p_subject = ucwords(strtolower($p_subject));

	// Clean up message from POST
	$p_message = luna_linebreaks(luna_trim($_POST['req_message']));

	// Check message
	if ($p_message == '')
		$errors[] = __('You must enter a message.', 'luna');

	// Here we use strlen() not luna_strlen() as we want to limit the comment to LUNA_MAX_COMMENT_SIZE bytes, not characters
	elseif (strlen($p_message) > LUNA_MAX_COMMENT_SIZE)
		$errors[] = sprintf(__('Comments cannot be longer than %s bytes.', 'luna'), forum_number_format(LUNA_MAX_COMMENT_SIZE));
	elseif ($luna_config['o_message_all_caps'] == '0' && strtoupper($p_message) == $p_message && $luna_user['is_admmod'])
		$p_message = ucwords(strtolower($p_message));

	// Validate BBCode syntax
	require LUNA_ROOT.'include/parser.php';
	$p_message = preparse_bbcode($p_message, $errors);

	if (empty($errors) && !isset($_POST['preview'])) { // Send message(s)
		$_SESSION['last_session_request'] = $now = time();

	// Send message(s)
	if (empty($errors) && !isset($_POST['preview'])) {
		$_SESSION['last_session_request'] = $now = time();

		if ($luna_config['o_inbox_notification'] == '1') {
			require_once LUNA_ROOT.'include/email.php';

			// Load the new_inbox templates
			$mail_tpl = trim(__('Subject: You received a new private message on <board_title>

<sender> sent a private message to you.

You can read this private message at this address: <inbox_url>

--
<board_mailer>
(Do not reply to this message)', 'luna'));
			$mail_tpl_full = trim(__('Subject: You received a private message on <board_title>

<sender> sent a private message to you.

The message reads as follows:
-----------------------------------------------------------------------

<message>

-----------------------------------------------------------------------

You can read this private message at this address: <inbox_url>

--
<board_mailer> Mailer
(Do not reply to this message)', 'luna'));

			// The first row contains the subject
			$first_crlf = strpos($mail_tpl, "\n");
			$mail_subject = trim(substr($mail_tpl, 8, $first_crlf-8));
			$mail_message = trim(substr($mail_tpl, $first_crlf));

			$mail_subject = str_replace('<board_title>', $luna_config['o_board_title'], $mail_subject);
			$mail_message = str_replace('<sender>', $luna_user['username'], $mail_message);
			$mail_message = str_replace('<board_mailer>', sprintf(__('%s Mailer', 'luna'), $luna_config['o_board_title']), $mail_message);

			// The first row contains the subject
			$first_crlf_full = strpos($mail_tpl_full, "\n");
			$mail_subject_full = trim(substr($mail_tpl_full, 8, $first_crlf_full-8));
			$mail_message_full = trim(substr($mail_tpl_full, $first_crlf_full));

			$cleaned_message = bbcode2email($p_message, -1);

			$mail_subject_full = str_replace('<board_title>', $luna_config['o_board_title'], $mail_subject_full);
			$mail_message_full = str_replace('<sender>', $luna_user['username'], $mail_message_full);
			$mail_message_full = str_replace('<message>', $cleaned_message, $mail_message_full);
			$mail_message_full = str_replace('<board_mailer>', sprintf(__('%s Mailer', 'luna'), $luna_config['o_board_title']), $mail_message_full);
		} if (empty($r) && empty($edit)) { // It's a new message
			$result_shared = $db->query('SELECT last_shared_id FROM '.$db->prefix.'messages ORDER BY last_shared_id DESC LIMIT 1') or error('Unable to fetch last_shared_id', __FILE__, __LINE__, $db->error());

			if (!$db->num_rows($result_shared))
				$shared_id = '1';
			else {
				$shared_result = $db->result($result_shared);
				$shared_id = $shared_result + '1';
			}

			foreach ($destinataires as $dest) {
				$val_showed = '0';

				if ($dest['id'] == $luna_user['id'])
					$val_showed = '1';
				else
					$val_showed = '0';

				$db->query('INSERT INTO '.$db->prefix.'messages (shared_id, last_shared_id, owner, subject, message, sender, receiver, sender_id, receiver_id, sender_ip, hide_smilies, commented, show_message, showed) VALUES(\''.$shared_id.'\', \''.$shared_id.'\', \''.$dest['id'].'\', \''.$db->escape($p_subject).'\', \''.$db->escape($p_message).'\', \''.$db->escape($luna_user['username']).'\', \''.$db->escape($usernames_list).'\', \''.$luna_user['id'].'\', \''.$db->escape($ids_list).'\', \''.get_remote_address().'\', \''.$hide_smilies.'\',  \''.$now.'\', \'1\', \''.$val_showed.'\')') or error('Unable to send the message.', __FILE__, __LINE__, $db->error());
				$new_mp = $db->insert_id();
				$db->query('UPDATE '.$db->prefix.'messages SET last_comment_id='.$new_mp.', last_comment='.$now.', last_commenter=\''.$db->escape($luna_user['username']).'\' WHERE shared_id='.$shared_id.' AND show_message=1 AND owner='.$dest['id']) or error('Unable to update the message.', __FILE__, __LINE__, $db->error());
				$db->query('UPDATE '.$db->prefix.'users SET num_inbox=num_inbox+1 WHERE id='.$dest['id']) or error('Unable to update user', __FILE__, __LINE__, $db->error());

				// E-mail notification
				if ($luna_config['o_inbox_notification'] == '1' && $dest['notify_inbox'] == '1' && $dest['id'] != $luna_user['id']) {
					$mail_message = str_replace('<inbox_url>', $luna_config['o_base_url'].'/viewinbox.php?tid='.$shared_id.'&mid='.$new_mp.'&box=inbox', $mail_message);
					$mail_message_full = str_replace('<inbox_url>', $luna_config['o_base_url'].'/viewinbox.php?tid='.$shared_id.'&mid='.$new_mp.'&box=inbox', $mail_message_full);

					if ($dest['notify_inbox_full'] == '1')
						luna_mail($dest['email'], $mail_subject_full, $mail_message_full);
					else
						luna_mail($dest['email'], $mail_subject, $mail_message);
				}
			}
			$db->query('UPDATE '.$db->prefix.'users SET last_comment='.$now.' WHERE id='.$luna_user['id']) or error('Unable to update user', __FILE__, __LINE__, $db->error());
		} if (!empty($r)) { // It's a reply or a reply with a quote
			// Check that $edit looks good
			if ($r <= '0')
				message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'));

			foreach ($destinataires as $dest) {

				$val_showed = '0';

				if ($dest['id'] == $luna_user['id'])
					$val_showed = '1';
				else
					$val_showed = '0';

					$db->query('INSERT INTO '.$db->prefix.'messages (shared_id, owner, subject, message, sender, receiver, sender_id, receiver_id, sender_ip, hide_smilies, commented, show_message, showed) VALUES(\''.$r.'\', \''.$dest['id'].'\', \''.$db->escape($p_subject).'\', \''.$db->escape($p_message).'\', \''.$db->escape($luna_user['username']).'\', \''.$db->escape($usernames_list).'\', \''.$luna_user['id'].'\', \''.$db->escape($ids_list).'\', \''.get_remote_address().'\', \''.$hide_smilies.'\', \''.$now.'\', \'0\', \''.$val_showed.'\')') or error('Unable to send the message.', __FILE__, __LINE__, $db->error());
					$new_mp = $db->insert_id();
					$db->query('UPDATE '.$db->prefix.'messages SET last_comment_id='.$new_mp.', last_comment='.$now.', last_commenter=\''.$db->escape($luna_user['username']).'\' WHERE shared_id='.$r.' AND show_message=1 AND owner='.$dest['id']) or error('Unable to update the message.', __FILE__, __LINE__, $db->error());
					if ($dest['id'] != $luna_user['id']) {
						$db->query('UPDATE '.$db->prefix.'messages SET showed = 0 WHERE shared_id='.$r.' AND show_message=1 AND owner='.$dest['id']) or error('Unable to update the message.', __FILE__, __LINE__, $db->error());
					}

					// E-mail notification
					if ($luna_config['o_inbox_notification'] == '1' && $dest['notify_inbox'] == '1' && $dest['id'] != $luna_user['id']) {
						$mail_message = str_replace('<inbox_url>', $luna_config['o_base_url'].'/viewinbox.php?tid='.$r.'&mid='.$new_mp.'&box=inbox', $mail_message);
						$mail_message_full = str_replace('<inbox_url>', $luna_config['o_base_url'].'/viewinbox.php?tid='.$r.'&mid='.$new_mp.'&box=inbox', $mail_message_full);

						if ($dest['notify_inbox_full'] == '1')
							luna_mail($dest['email'], $mail_subject_full, $mail_message_full);
						else
							luna_mail($dest['email'], $mail_subject, $mail_message);
					}
				}
				$db->query('UPDATE '.$db->prefix.'users SET last_comment='.$now.' WHERE id='.$luna_user['id']) or error('Unable to update user', __FILE__, __LINE__, $db->error());
			}
			redirect('inbox.php');
		}
	}
} else {
	// To user(s)
	if (isset($_GET['uid'])) {
		$users_id = explode('-', $_GET['uid']);
		$users_id = array_map('intval', $users_id);
		foreach ($users_id as $k=>$v)
			if ($v <= 0) unset($users_id[$k]);

		$arry_dests = array();
		foreach ($users_id as $user_id) {
			$result = $db->query('SELECT username FROM '.$db->prefix.'users WHERE id='.$user_id) or error('Unable to find the informations of the message', __FILE__, __LINE__, $db->error());

			if (!$db->num_rows($result))
				message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'));

			$arry_dests[] = $db->result($result);
		}

		$p_destinataire = implode(', ', $arry_dests);
	} if (isset($_GET['lid'])) { // From list
		$id = intval($_GET['lid']);

		$arry_dests = array();
		$result = $db->query('SELECT receivers FROM '.$db->prefix.'sending_lists WHERE user_id='.$luna_user['id'].' AND id='.$id) or error('Unable to find the informations of the message', __FILE__, __LINE__, $db->error());

		if (!$db->num_rows($result))
			message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'));

		$arry_dests = unserialize($db->result($result));

		$p_destinataire = implode(', ', $arry_dests);
	}
}

$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), __('Private Messages', 'luna'), __('Send a message', 'luna'));

$required_fields = array('req_message' => __('Message', 'luna'));
$focus_element = array('comment');

if ($r == '0' && $q == '0' && $edit == '0') {
	$required_fields['req_subject'] = __('Subject', 'luna');
	$focus_element[] = 'p_username';
} else
	$focus_element[] = 'req_message';

define('LUNA_ACTIVE_PAGE', 'new-inbox');
require load_page('header.php');

require load_page('inbox-new.php');

require load_page('footer.php');
?>