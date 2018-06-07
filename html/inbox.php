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

// No guest here !
if ($luna_user['is_guest'])
	message(__('You do not have permission to access this page.', 'luna'));

// User enable Inbox ?
if (!$luna_user['use_inbox'] == '1')
	message(__('You do not have permission to access this page.', 'luna'));

// Are we allowed to use this ?
if (!$luna_config['o_enable_inbox'] =='1' || $luna_user['g_inbox'] == '0')
	message(__('You do not have permission to access this page.', 'luna'));

// Page ?
$page = (!isset($_REQUEST['p']) || $_REQUEST['p'] <= '1') ? '1' : intval($_REQUEST['p']);

// Action ?
$action = isset($_REQUEST['action']) ? $_REQUEST['action'] : '';

$id = $luna_user['id'];

// User block
$avatar_user_card = draw_user_avatar($id);

// Mark as read multiple comments
if (isset($_REQUEST['markread'])) {
	confirm_referrer('inbox.php');

	if (empty($_POST['selected_messages']))
		message(__('You must select some messages', 'luna'));

	$idlist = array_values($_POST['selected_messages']);
	$idlist = array_map('intval', $idlist);
	$idlist = implode(',', array_values($idlist));

	$db->query('UPDATE '.$db->prefix.'messages SET showed=1 WHERE shared_id IN ('.$idlist.') AND owner='.$id.' AND show_message=1') or error('Unable to update the status of the messages', __FILE__, __LINE__, $db->error());
	redirect('inbox.php');
} elseif (isset($_REQUEST['markunread'])) { // Mark as unread
	confirm_referrer('inbox.php');

	if (empty($_POST['selected_messages']))
		message(__('You must select some messages', 'luna'));

	$idlist = array_values($_POST['selected_messages']);
	$idlist = array_map('intval', $idlist);
	$idlist = implode(',', array_values($idlist));

	$db->query('UPDATE '.$db->prefix.'messages SET showed=0 WHERE shared_id IN ('.$idlist.') AND owner='.$id.' AND show_message=1') or error('Unable to update the status of the messages', __FILE__, __LINE__, $db->error());
	redirect('inbox.php');
} elseif (isset($_REQUEST['delete_multiple'])) { // Delete comments
	confirm_referrer('inbox.php');

	if (empty($_POST['selected_messages']))
		message(__('You must select some messages', 'luna'));

	$idlist = array_values($_POST['selected_messages']);
	$idlist = array_map('intval', $idlist);
	$idlist = implode(',', array_values($idlist));
	$number = explode(',', $idlist);
	$number = array_map('intval', $number);
	$number = count($number);

	$db->query('DELETE FROM '.$db->prefix.'messages WHERE shared_id IN ('.$idlist.') AND owner='.$id) or error('Unable to delete the messages', __FILE__, __LINE__, $db->error());
	$db->query('UPDATE '.$db->prefix.'users SET num_inbox=num_inbox-'.$number.' WHERE id='.$id) or error('Unable to update user', __FILE__, __LINE__, $db->error());

	redirect('inbox.php');
} else {

// Get message count for this box
$result = $db->query('SELECT COUNT(*) FROM '.$db->prefix.'messages WHERE show_message=1 AND owner='.$id) or error("Unable to count the messages", __FILE__, __LINE__, $db->error());
list($num_messages) = $db->fetch_row($result);

// What page are we on ?
$num_pages = ceil($num_messages/$luna_config['o_message_per_page']);
if ($page > $num_pages) $page = 1;
$start_from = intval($luna_config['o_message_per_page'])*($page-1);
$limit = $start_from.','.$luna_config['o_message_per_page'];

// Start building page
$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), __('Private Messages', 'luna'), __('Inbox', 'luna'));

$result = $db->query('SELECT u.id, u.username, u.email, u.title, u.realname, u.url, u.facebook, u.msn, u.twitter, u.google, u.location, u.signature, u.disp_threads, u.disp_comments, u.email_setting, u.notify_with_comment, u.auto_notify, u.show_smilies, u.show_img, u.show_img_sig, u.show_avatars, u.show_sig, u.php_timezone, u.language, u.num_comments, u.last_comment, u.registered, u.advanced_editor, u.dialog_editor, u.registration_ip, u.admin_note, u.date_format, u.time_format, u.last_visit, g.g_id, g.g_user_title, g.g_moderator FROM '.$db->prefix.'users AS u LEFT JOIN '.$db->prefix.'groups AS g ON g.g_id=u.group_id WHERE u.id='.$id) or error('Unable to fetch user info', __FILE__, __LINE__, $db->error());
if (!$db->num_rows($result))
	message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

$user = $db->fetch_assoc($result);

$user_username = luna_htmlspecialchars($user['username']);
$user_usertitle = get_title($user);

define('LUNA_ACTIVE_PAGE', 'inbox');
require load_page('header.php');

?>
<script type="text/javascript">
/* <![CDATA[ */
function checkAll(checkWhat,command){
	var inputs = document.getElementsByTagName('input');

	for(index = 0; index < inputs.length; index++){
		if(inputs[index].name == checkWhat){
			inputs[index].checked=document.getElementById(command).checked;
		}
	}
}
/* ]]> */
</script>
<?php
	require load_page('inbox.php');
}
require load_page('footer.php');
