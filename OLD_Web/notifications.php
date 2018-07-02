<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

define('LUNA_ROOT', dirname(__FILE__).'/');
require LUNA_ROOT.'include/common.php';

// Load the me functions script
require LUNA_ROOT.'include/me_functions.php';

// Include UTF-8 function
require LUNA_ROOT.'include/utf8/substr_replace.php';
require LUNA_ROOT.'include/utf8/ucwords.php'; // utf8_ucwords needs utf8_substr_replace
require LUNA_ROOT.'include/utf8/strcasecmp.php';

$action = isset($_GET['action']) ? $_GET['action'] : null;
$type = isset($_GET['type']) ? $_GET['type'] : null;
$id = $luna_user['id'];
if ($id < 2)
	message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

// Handle notifications 
if (isset($_GET['notification'])) {
    $notification = intval($_GET['notification']);
    
    if ( !is_int( $notification) )
	   message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');
        

    $result = $db->query('SELECT link FROM '.$db->prefix.'notifications WHERE id='.$notification.' AND user_id='.$id) or error('Unable to fetch notification info', __FILE__, __LINE__, $db->error());
    $notifi = $db->fetch_assoc($result);
    
    if (!$db->num_rows($result))
        message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');
     
    $result = $db->query('UPDATE '.$db->prefix.'notifications SET viewed = 1 WHERE id='.$notification.' AND user_id='.$id) or error('Unable to update notification info', __FILE__, __LINE__, $db->error());
    
    header('Location: '.$notifi['link']);
    exit;
}

if (isset($_GET['read_notification'])) {
    $notification = intval($_GET['read_notification']);
    
    if ( !is_int($notification) )
	   message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

    $result = $db->query('SELECT id FROM '.$db->prefix.'notifications WHERE id='.$notification.' AND user_id='.$id) or error('Unable to fetch notification info', __FILE__, __LINE__, $db->error());
    $notifi = $db->fetch_assoc($result);
    
    if (!$db->num_rows($result))
        message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');
     
    $result = $db->query('UPDATE '.$db->prefix.'notifications SET viewed = 1 WHERE id='.$notification.' AND user_id='.$id) or error('Unable to update notification info', __FILE__, __LINE__, $db->error());
    
	redirect('notifications.php');
}

if (isset($_GET['remove_notification'])) {
    $notification = intval($_GET['remove_notification']);
    
    if ( !is_int($notification) )
	   message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

    $result = $db->query('SELECT id, user_id, link FROM '.$db->prefix.'notifications WHERE id='.$notification.' AND user_id='.$id) or error('Unable to fetch notification info', __FILE__, __LINE__, $db->error());
    $notifi = $db->fetch_assoc($result);
    
    if (!$db->num_rows($result))
        message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');
     
    $result = $db->query('DELETE FROM '.$db->prefix.'notifications WHERE id='.$notification) or error('Unable to update notification info', __FILE__, __LINE__, $db->error());
    
	redirect('notifications.php');
}

if ($action == 'readnoti') {
    $result = $db->query('UPDATE '.$db->prefix.'notifications SET viewed = 1 WHERE user_id='.$id) or error('Unable to update notification info', __FILE__, __LINE__, $db->error());
	confirm_referrer('notifications.php');

	redirect('notifications.php?id='.$id);
} elseif ($action == 'delnoti') {
    $result = $db->query('DELETE FROM '.$db->prefix.'notifications WHERE viewed = 1 AND user_id='.$id) or error('Unable to remove notification info', __FILE__, __LINE__, $db->error());
	confirm_referrer('notifications.php');

	redirect('notifications.php?id='.$id);
}

// Show notifications
$result = $db->query('SELECT COUNT(id) FROM '.$db_prefix.'notifications WHERE viewed = 0 AND user_id = '.$id) or error ('Unable to load notifications', __FILE__, __LINE__, $db->error());
$num_not_unseen = $db->result($result);

if ($num_not_unseen == '0')
	$ind_not[] = '<div class="alert alert-info">'.__('No new notifications.', 'luna').'</div>';
else {
	$result = $db->query('SELECT * FROM '.$db_prefix.'notifications WHERE viewed = 0 AND user_id = '.$id.' ORDER BY time DESC') or error ('Unable to load notifications', __FILE__, __LINE__, $db->error());
	while ($cur_notifi = $db->fetch_assoc($result)) {
		$notifitime = format_time($cur_notifi['time'], false, null, $luna_config['o_time_format'], true, true);
		$ind_not[] = '<div class="notification-row clearfix">
                        <div class="col-xs-10">
                             <a class="btn btn-default btn-block" href="notifications.php?notification='.$cur_notifi['id'].'"><span class="fa fa-fw '.$cur_notifi['icon'].'"></span>&nbsp; '.$cur_notifi['message'].'<span class="timestamp pull-right hidden-xxs">'.format_time($cur_notifi['time'], false, null, $luna_config['o_time_format'], true, true).'</span></a>
                        </div>
                        <div class="col-xs-2">
                            <a class="btn btn-primary btn-block" href="notifications.php?read_notification='.$cur_notifi['id'].'"><span class="fa fa-fw fa-eye"></span><span class="hidden-xxs"> '.__('Seen', 'luna').'</span></a>
                        </div>
                    </div>';
	}
}

$result = $db->query('SELECT COUNT(id) FROM '.$db_prefix.'notifications WHERE viewed = 1 AND user_id = '.$id) or error ('Unable to load notifications', __FILE__, __LINE__, $db->error());
$num_not_seen = $db->result($result);

if ($num_not_seen == '0')
	$ind_not_seen[] = '<div class="alert alert-info">'.__('No old notifications.', 'luna').'</div>';
else {
	$result = $db->query('SELECT * FROM '.$db_prefix.'notifications WHERE viewed = 1 AND user_id = '.$id.' ORDER BY time DESC') or error ('Unable to load notifications', __FILE__, __LINE__, $db->error());
	while ($cur_notifi = $db->fetch_assoc($result)) {
		$notifitime = format_time($cur_notifi['time'], false, null, $luna_config['o_time_format'], true, true);
		$ind_not_seen[] = '<div class="notification-row clearfix">
                            <div class="col-xs-10">
                                <a class="btn btn-default btn-block" href="notifications.php?notification='.$cur_notifi['id'].'"><span class="fa fa-fw '.$cur_notifi['icon'].'"></span>&nbsp; '.$cur_notifi['message'].'<span class="timestamp pull-right hidden-xxs">'.format_time($cur_notifi['time'], false, null, $luna_config['o_time_format'], true, true).'</span></a>
                            </div>
                            <div class="col-xs-2">
                                <a class="btn btn-danger btn-block" href="notifications.php?remove_notification='.$cur_notifi['id'].'"><span class="fa fa-fw fa-trash"></span><span class="hidden-xxs"> '.__('Remove', 'luna').'</span></a>
                            </div>
                        </div>';
	}
}

$not = implode('', $ind_not);
$not_seen = implode('', $ind_not_seen);

$result = $db->query('SELECT u.id, u.username, u.email, u.title, u.realname, u.url, u.facebook, u.msn, u.twitter, u.google, u.location, u.signature, u.disp_threads, u.disp_comments, u.email_setting, u.notify_with_comment, u.auto_notify, u.show_smilies, u.show_img, u.show_img_sig, u.show_avatars, u.show_sig, u.php_timezone, u.language, u.num_comments, u.last_comment, u.registered, u.advanced_editor, u.dialog_editor, u.registration_ip, u.admin_note, u.date_format, u.time_format, u.last_visit, g.g_id, g.g_user_title, g.g_moderator FROM '.$db->prefix.'users AS u LEFT JOIN '.$db->prefix.'groups AS g ON g.g_id=u.group_id WHERE u.id='.$id) or error('Unable to fetch user info', __FILE__, __LINE__, $db->error());
if (!$db->num_rows($result))
	message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

$user = $db->fetch_assoc($result);

$user_username = luna_htmlspecialchars($user['username']);
$user_usertitle = get_title($user);
$avatar_field = generate_avatar_markup($id);
$avatar_user_card = draw_user_avatar($id);

$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']).' / '.__('Profile', 'luna'));
define('LUNA_ACTIVE_PAGE', 'me');
include LUNA_ROOT.'header.php';
require load_page('header.php');

require load_page('notifications.php');

require load_page('footer.php');