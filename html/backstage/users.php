<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'users');
define('LUNA_PAGE', 'users');

require LUNA_ROOT.'include/common.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}

// Show IP statistics for a certain user ID
if (isset($_GET['ip_stats'])) {
	$ip_stats = intval($_GET['ip_stats']);
	if ($ip_stats < 1)
		message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

	// Fetch ip count
	$result = $db->query('SELECT commenter_ip, MAX(commented) AS last_used FROM '.$db->prefix.'comments WHERE commenter_id='.$ip_stats.' GROUP BY commenter_ip') or error('Unable to fetch comment info', __FILE__, __LINE__, $db->error());
	$num_ips = $db->num_rows($result);

	// Determine the ip offset (based on $_GET['p'])
	$num_pages = ceil($num_ips / 50);

	$p = (!isset($_GET['p']) || $_GET['p'] <= 1 || $_GET['p'] > $num_pages) ? 1 : intval($_GET['p']);
	$start_from = 50 * ($p - 1);

	// Generate paging links
	$paging_links = paginate($num_pages, $p, 'users.php?ip_stats='.$ip_stats );

	$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), __('Admin', 'luna'), __('Users', 'luna'), __('Search Results', 'luna'));
	define('LUNA_ACTIVE_PAGE', 'admin');
	require 'header.php';

?>
<div class="row">
	<div class="col-sm-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title"><?php _e('Search Results', 'luna') ?></h3>
            </div>
            <div class="panel-body">
                <?php echo $paging_links ?>
            </div>
            <table class="table table-js table-striped table-hover">
                <thead>
                    <tr>
                        <th><?php _e('IP/IP-ranges', 'luna') ?></th>
                        <th><?php _e('Last used', 'luna') ?></th>
                        <th><?php _e('Times found', 'luna') ?></th>
                        <th><?php _e('Action', 'luna') ?></th>
                    </tr>
                </thead>
                <tbody>
<?php

	$result = $db->query('SELECT commenter_ip, MAX(commented) AS last_used, COUNT(id) AS used_times FROM '.$db->prefix.'comments WHERE commenter_id='.$ip_stats.' GROUP BY commenter_ip ORDER BY last_used DESC LIMIT '.$start_from.', 50') or error('Unable to fetch comment info', __FILE__, __LINE__, $db->error());
	if ($db->num_rows($result)) {
		while ($cur_ip = $db->fetch_assoc($result)) {

?>
                    <tr>
                        <td><a href="../moderate.php?get_host=<?php echo $cur_ip['commenter_ip'] ?>"><?php echo luna_htmlspecialchars($cur_ip['commenter_ip']) ?></a></td>
                        <td><?php echo format_time($cur_ip['last_used']) ?></td>
                        <td><?php echo $cur_ip['used_times'] ?></td>
                        <td><a href="users.php?show_users=<?php echo luna_htmlspecialchars($cur_ip['commenter_ip']) ?>"><?php _e('Find more users for this ip', 'luna') ?></a></td>
                    </tr>
<?php

		}
	} else
		echo "\t\t\t\t".'<tr><td colspan="4">'.__('There are currently no comments by that user in the forum.', 'luna').'</td></tr>'."\n";

?>
                </tbody>
            </table>
            <div class="panel-body">
                <?php echo $paging_links ?>
            </div>
        </div>
    </div>
</div>
<?php

	require 'footer.php';
} elseif (isset($_GET['show_users'])) {
	$ip = luna_trim($_GET['show_users']);

	if (!@preg_match('%^[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}$%', $ip) && !@preg_match('%^((([0-9A-Fa-f]{1,4}:){7}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){6}:[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){5}:([0-9A-Fa-f]{1,4}:)?[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){4}:([0-9A-Fa-f]{1,4}:){0,2}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){3}:([0-9A-Fa-f]{1,4}:){0,3}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){2}:([0-9A-Fa-f]{1,4}:){0,4}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){6}((\b((25[0-5])|(1\d{2})|(2[0-4]\d)|(\d{1,2}))\b)\.){3}(\b((25[0-5])|(1\d{2})|(2[0-4]\d)|(\d{1,2}))\b))|(([0-9A-Fa-f]{1,4}:){0,5}:((\b((25[0-5])|(1\d{2})|(2[0-4]\d)|(\d{1,2}))\b)\.){3}(\b((25[0-5])|(1\d{2})|(2[0-4]\d)|(\d{1,2}))\b))|(::([0-9A-Fa-f]{1,4}:){0,5}((\b((25[0-5])|(1\d{2})|(2[0-4]\d)|(\d{1,2}))\b)\.){3}(\b((25[0-5])|(1\d{2})|(2[0-4]\d)|(\d{1,2}))\b))|([0-9A-Fa-f]{1,4}::([0-9A-Fa-f]{1,4}:){0,5}[0-9A-Fa-f]{1,4})|(::([0-9A-Fa-f]{1,4}:){0,6}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){1,7}:))$%', $ip))
		message_backstage(__('The supplied IP address is not correctly formatted.', 'luna'));

	// Fetch user count
	$result = $db->query('SELECT DISTINCT commenter_id, commenter FROM '.$db->prefix.'comments WHERE commenter_ip=\''.$db->escape($ip).'\'') or error('Unable to fetch comment info', __FILE__, __LINE__, $db->error());
	$num_users = $db->num_rows($result);

	// Determine the user offset (based on $_GET['p'])
	$num_pages = ceil($num_users / 50);

	$p = (!isset($_GET['p']) || $_GET['p'] <= 1 || $_GET['p'] > $num_pages) ? 1 : intval($_GET['p']);
	$start_from = 50 * ($p - 1);

	// Generate paging links
	$paging_links = paginate($num_pages, $p, 'users.php?show_users='.$ip);

	require 'header.php';

?>
<div class="row">
	<div class="col-sm-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title"><?php _e('Search Results', 'luna') ?></h3>
            </div>
            <div class="panel-body">
                <?php echo $paging_links ?>
            </div>
            <table class="table table-js table-striped table-hover">
                <thead>
                    <tr>
                        <th><?php _e('Username', 'luna') ?></th>
                        <th><?php _e('Email', 'luna') ?></th>
                        <th><?php _e('Title/Status', 'luna') ?></th>
                        <th class="text-center"><?php _e('Comments', 'luna') ?></th>
                        <th><?php _e('Admin note', 'luna') ?></th>
                        <th><?php _e('Actions', 'luna') ?></th>
                    </tr>
                </thead>
                <tbody>
<?php

	$result = $db->query('SELECT DISTINCT commenter_id, commenter FROM '.$db->prefix.'comments WHERE commenter_ip=\''.$db->escape($ip).'\' ORDER BY commenter ASC LIMIT '.$start_from.', 50') or error('Unable to fetch comment info', __FILE__, __LINE__, $db->error());
	$num_comments = $db->num_rows($result);

	if ($num_comments) {
		$commenters = $commenter_ids = array();
		while ($cur_commenter = $db->fetch_assoc($result)) {
			$commenters[] = $cur_commenter;
			$commenter_ids[] = $cur_commenter['commenter_id'];
		}

		$result = $db->query('SELECT u.id, u.username, u.email, u.title, u.num_comments, u.admin_note, g.g_id, g.g_user_title FROM '.$db->prefix.'users AS u INNER JOIN '.$db->prefix.'groups AS g ON g.g_id=u.group_id WHERE u.id>1 AND u.id IN('.implode(',', $commenter_ids).')') or error('Unable to fetch user info', __FILE__, __LINE__, $db->error());
		$user_data = array();
		while ($cur_user = $db->fetch_assoc($result))
			$user_data[$cur_user['id']] = $cur_user;

		// Loop through users and print out some info
		foreach ($commenters as $cur_commenter) {
			if (isset($user_data[$cur_commenter['commenter_id']])) {
				$user_title = get_title($user_data[$cur_commenter['commenter_id']]);

			$actions = '<a href="users.php?ip_stats='.$user_data[$cur_commenter['commenter_id']]['id'].'">'.__('IP stats', 'luna').'</a> &middot; <a href="../search.php?action=show_user_comments&amp;user_id='.$user_data[$cur_commenter['commenter_id']]['id'].'">'.__('Comments', 'luna').'</a>';
?>
                    <tr>
                        <td><?php echo '<a href="../profile.php?id='.$user_data[$cur_commenter['commenter_id']]['id'].'">'.luna_htmlspecialchars($user_data[$cur_commenter['commenter_id']]['username']).'</a>' ?></td>
                        <td><a href="mailto:<?php echo luna_htmlspecialchars($user_data[$cur_commenter['commenter_id']]['email']) ?>"><?php echo luna_htmlspecialchars($user_data[$cur_commenter['commenter_id']]['email']) ?></a></td>
                        <td><?php echo $user_title ?></td>
                        <td class="text-center"><?php echo forum_number_format($user_data[$cur_commenter['commenter_id']]['num_comments']) ?></td>
                        <td><?php echo ($user_data[$cur_commenter['commenter_id']]['admin_note'] != '') ? luna_htmlspecialchars($user_data[$cur_commenter['commenter_id']]['admin_note']) : '&#160;' ?></td>
                        <td><?php echo $actions ?></td>
                    </tr>
<?php

			} else {

?>
                    <tr>
                        <td><?php echo luna_htmlspecialchars($cur_commenter['commenter']) ?></td>
                        <td>&#160;</td>
                        <td><?php _e('Guest', 'luna') ?></td>
                        <td>&#160;</td>
                        <td>&#160;</td>
                        <td>&#160;</td>
                    </tr>
<?php

			}
		}
	} else
		echo "\t\t\t\t".'<tr><td colspan="6">'.__('The supplied IP address could not be found in the database.', 'luna').'</td></tr>'."\n";

?>
                </tbody>
            </table>
            <div class="panel-body">
                <?php echo $paging_links ?>
            </div>
        </div>
    </div>
</div>
<?php
	require 'footer.php';
}


// Move multiple users to other user groups
elseif (isset($_POST['move_users']) || isset($_POST['move_users_comply'])) {
	if ($luna_user['g_id'] > LUNA_ADMIN)
		message_backstage(__('You do not have permission to access this page.', 'luna'), false, '403 Forbidden');

	confirm_referrer('backstage/users.php');

	if (isset($_POST['users'])) {
		$user_ids = is_array($_POST['users']) ? array_keys($_POST['users']) : explode(',', $_POST['users']);
		$user_ids = array_map('intval', $user_ids);

		// Delete invalid IDs
		$user_ids = array_diff($user_ids, array(0, 1));
	} else
		$user_ids = array();

	if (empty($user_ids))
		message_backstage(__('No users selected.', 'luna'));

	// Are we trying to batch move any admins?
	$result = $db->query('SELECT COUNT(*) FROM '.$db->prefix.'users WHERE id IN ('.implode(',', $user_ids).') AND group_id='.LUNA_ADMIN) or error('Unable to fetch user info', __FILE__, __LINE__, $db->error());
	if ($db->result($result) > 0)
		message_backstage(__('For security reasons, you are not allowed to move multiple administrators to another group. If you want to move these administrators, you can do so on their respective user profiles.', 'luna'));

	// Fetch all user groups
	$all_groups = array();
	$result = $db->query('SELECT g_id, g_title FROM '.$db->prefix.'groups WHERE g_id NOT IN ('.LUNA_GUEST.','.LUNA_ADMIN.') ORDER BY g_title ASC') or error('Unable to fetch groups', __FILE__, __LINE__, $db->error());
	while ($row = $db->fetch_row($result))
		$all_groups[$row[0]] = $row[1];

	if (isset($_POST['move_users_comply'])) {
		if (isset($_POST['new_group']) && isset($all_groups[$_POST['new_group']]))
			$new_group = $_POST['new_group'];
		else
			message_backstage(__('Invalid group ID.', 'luna'));

		// Is the new group a moderator group?
		$result = $db->query('SELECT g_moderator FROM '.$db->prefix.'groups WHERE g_id='.$new_group) or error('Unable to fetch group info', __FILE__, __LINE__, $db->error());
		$new_group_mod = $db->result($result);

		// Fetch user groups
		$user_groups = array();
		$result = $db->query('SELECT id, group_id FROM '.$db->prefix.'users WHERE id IN ('.implode(',', $user_ids).')') or error('Unable to fetch user groups', __FILE__, __LINE__, $db->error());
		while ($cur_user = $db->fetch_assoc($result)) {
			if (!isset($user_groups[$cur_user['group_id']]))
				$user_groups[$cur_user['group_id']] = array();

			$user_groups[$cur_user['group_id']][] = $cur_user['id'];
		}

		// Are any users moderators?
		$group_ids = array_keys($user_groups);
		$result = $db->query('SELECT g_id, g_moderator FROM '.$db->prefix.'groups WHERE g_id IN ('.implode(',', $group_ids).')') or error('Unable to fetch group moderators', __FILE__, __LINE__, $db->error());
		while ($cur_group = $db->fetch_assoc($result)) {
			if ($cur_group['g_moderator'] == '0')
				unset($user_groups[$cur_group['g_id']]);
		}

		if (!empty($user_groups) && $new_group != LUNA_ADMIN && $new_group_mod != '1') {
			// Fetch forum list and clean up their moderator list
			$result = $db->query('SELECT id, moderators FROM '.$db->prefix.'forums') or error('Unable to fetch forum list', __FILE__, __LINE__, $db->error());
			while ($cur_forum = $db->fetch_assoc($result)) {
				$cur_moderators = ($cur_forum['moderators'] != '') ? unserialize($cur_forum['moderators']) : array();

				foreach ($user_groups as $group_users)
					$cur_moderators = array_diff($cur_moderators, $group_users);

				$cur_moderators = (!empty($cur_moderators)) ? '\''.$db->escape(serialize($cur_moderators)).'\'' : 'NULL';
				$db->query('UPDATE '.$db->prefix.'forums SET moderators='.$cur_moderators.' WHERE id='.$cur_forum['id']) or error('Unable to update forum', __FILE__, __LINE__, $db->error());
			}
		}

		// Change user group
		$db->query('UPDATE '.$db->prefix.'users SET group_id='.$new_group.' WHERE id IN ('.implode(',', $user_ids).')') or error('Unable to change user group', __FILE__, __LINE__, $db->error());

		redirect('backstage/users.php');
	}

	require 'header.php';

?>
<div class="row">
	<div class="col-sm-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title"><?php _e('Change user group', 'luna') ?></h3>
            </div>
            <div class="panel-body">
                <form class="form-horizontal" name="confirm_move_users" method="post" action="users.php">
                    <input type="hidden" name="users" value="<?php echo implode(',', $user_ids) ?>" />
                    <fieldset>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('New group', 'luna') ?><span class="help-block"><?php _e('Select a new user group', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <div class="input-group">
                                    <select class="form-control" name="new_group" tabindex="1">
            <?php foreach ($all_groups as $gid => $group) : ?>											<option value="<?php echo $gid ?>"><?php echo luna_htmlspecialchars($group) ?></option>
            <?php endforeach; ?>
                                    </select>
                                    <span class="input-group-btn"><input class="btn btn-primary" type="submit" name="move_users_comply" value="<?php _e('Save', 'luna') ?>" tabindex="2" /></span>
                                </div>
                            </div>
                        </div>
                    </fieldset>
                </form>
            </div>
        </div>
    </div>
</div>
<?php

	require 'footer.php';
}


// Delete multiple users
elseif (isset($_POST['delete_users']) || isset($_POST['delete_users_comply'])) {
	if ($luna_user['g_id'] > LUNA_ADMIN)
		message_backstage(__('You do not have permission to access this page.', 'luna'), false, '403 Forbidden');

	confirm_referrer('backstage/users.php');

	if (isset($_POST['users'])) {
		$user_ids = is_array($_POST['users']) ? array_keys($_POST['users']) : explode(',', $_POST['users']);
		$user_ids = array_map('intval', $user_ids);

		// Delete invalid IDs
		$user_ids = array_diff($user_ids, array(0, 1));
	} else
		$user_ids = array();

	if (empty($user_ids))
		message_backstage(__('No users selected.', 'luna'));

	// Are we trying to delete any admins?
	$result = $db->query('SELECT COUNT(*) FROM '.$db->prefix.'users WHERE id IN ('.implode(',', $user_ids).') AND group_id='.LUNA_ADMIN) or error('Unable to fetch user info', __FILE__, __LINE__, $db->error());
	if ($db->result($result) > 0)
		message_backstage(__('Administrators cannot be deleted. In order to delete administrators, you must first move them to a different user group.', 'luna'));

	if (isset($_POST['delete_users_comply'])) {
		// Fetch user groups
		$user_groups = array();
		$result = $db->query('SELECT id, group_id FROM '.$db->prefix.'users WHERE id IN ('.implode(',', $user_ids).')') or error('Unable to fetch user groups', __FILE__, __LINE__, $db->error());
		while ($cur_user = $db->fetch_assoc($result)) {
			if (!isset($user_groups[$cur_user['group_id']]))
				$user_groups[$cur_user['group_id']] = array();

			$user_groups[$cur_user['group_id']][] = $cur_user['id'];
		}

		// Are any users moderators?
		$group_ids = array_keys($user_groups);
		$result = $db->query('SELECT g_id, g_moderator FROM '.$db->prefix.'groups WHERE g_id IN ('.implode(',', $group_ids).')') or error('Unable to fetch group moderators', __FILE__, __LINE__, $db->error());
		while ($cur_group = $db->fetch_assoc($result)) {
			if ($cur_group['g_moderator'] == '0')
				unset($user_groups[$cur_group['g_id']]);
		}

		// Fetch forum list and clean up their moderator list
		$result = $db->query('SELECT id, moderators FROM '.$db->prefix.'forums') or error('Unable to fetch forum list', __FILE__, __LINE__, $db->error());
		while ($cur_forum = $db->fetch_assoc($result)) {
			$cur_moderators = ($cur_forum['moderators'] != '') ? unserialize($cur_forum['moderators']) : array();

			foreach ($user_groups as $group_users)
				$cur_moderators = array_diff($cur_moderators, $group_users);

			$cur_moderators = (!empty($cur_moderators)) ? '\''.$db->escape(serialize($cur_moderators)).'\'' : 'NULL';
			$db->query('UPDATE '.$db->prefix.'forums SET moderators='.$cur_moderators.' WHERE id='.$cur_forum['id']) or error('Unable to update forum', __FILE__, __LINE__, $db->error());
		}

		// Delete any subscriptions
		$db->query('DELETE FROM '.$db->prefix.'thread_subscriptions WHERE user_id IN ('.implode(',', $user_ids).')') or error('Unable to delete thread subscriptions', __FILE__, __LINE__, $db->error());
		$db->query('DELETE FROM '.$db->prefix.'forum_subscriptions WHERE user_id IN ('.implode(',', $user_ids).')') or error('Unable to delete forum subscriptions', __FILE__, __LINE__, $db->error());

		// Remove them from the online list (if they happen to be logged in)
		$db->query('DELETE FROM '.$db->prefix.'online WHERE user_id IN ('.implode(',', $user_ids).')') or error('Unable to remove users from online list', __FILE__, __LINE__, $db->error());

		// Should we delete all comments made by these users?
		if (isset($_POST['delete_comments'])) {
			require LUNA_ROOT.'include/search_idx.php';
			@set_time_limit(0);

			// Find all comments made by this user
			$result = $db->query('SELECT p.id, p.commenter_id, p.thread_id, t.forum_id FROM '.$db->prefix.'comments AS p INNER JOIN '.$db->prefix.'threads AS t ON t.id=p.thread_id INNER JOIN '.$db->prefix.'forums AS f ON f.id=t.forum_id WHERE p.commenter_id IN ('.implode(',', $user_ids).')') or error('Unable to fetch comments', __FILE__, __LINE__, $db->error());
			if ($db->num_rows($result)) {
				while ($cur_comment = $db->fetch_assoc($result)) {
					// Determine whether this comment is the "thread comment" or not
					$result2 = $db->query('SELECT id FROM '.$db->prefix.'comments WHERE thread_id='.$cur_comment['thread_id'].' ORDER BY commented LIMIT 1') or error('Unable to fetch comment info', __FILE__, __LINE__, $db->error());

					if ($db->result($result2) == $cur_comment['id'])
						delete_thread($cur_comment['thread_id']);
					else
						delete_comment($cur_comment['id'], $cur_comment['thread_id'], $cur_comment['commenter_id']);

					update_forum($cur_comment['forum_id']);
				}
			}
		} else
			// Set all their comments to guest
			$db->query('UPDATE '.$db->prefix.'comments SET commenter_id=1 WHERE commenter_id IN ('.implode(',', $user_ids).')') or error('Unable to update comments', __FILE__, __LINE__, $db->error());

		// Delete the users
		$db->query('DELETE FROM '.$db->prefix.'users WHERE id IN ('.implode(',', $user_ids).')') or error('Unable to delete users', __FILE__, __LINE__, $db->error());

		// Delete user avatars
		foreach ($user_ids as $user_id)
			delete_avatar($user_id);

		// Regenerate the users info cache
		if (!defined('LUNA_CACHE_FUNCTIONS_LOADED'))
			require LUNA_ROOT.'include/cache.php';

		generate_users_info_cache();

		redirect('backstage/users.php?deleted=true');
	}

	require 'header.php';

?>
<div class="row">
	<div class="col-sm-12">
        <form name="confirm_del_users" method="post" action="users.php">
            <div class="panel panel-danger">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('Delete users', 'luna') ?></h3>
                </div>
                <div class="panel-body">
                    <input type="hidden" name="users" value="<?php echo implode(',', $user_ids) ?>" />
                    <fieldset>
                        <p><?php _e('Deleted users and/or comments cannot be restored. If you choose not to delete the comments made by this user, the comments can only be deleted manually at a later time.', 'luna') ?></p>
                        <div class="checkbox">
                            <label>
                                <input type="checkbox" name="delete_comments" value="1" checked />
                                <?php _e('Delete all comments and threads this user has made', 'luna') ?>
                            </label>
                        </div>
                    </fieldset>
                </div>
                <div class="panel-footer">
                    <button class="btn btn-danger" type="submit" name="delete_users_comply"><span class="fa fa-fw fa-trash"></span> <?php _e('Delete', 'luna') ?></button>
                </div>
            </div>
        </form>
    </div>
</div>
<?php

	require 'footer.php';
}

// Ban multiple users
elseif (isset($_POST['ban_users']) || isset($_POST['ban_users_comply'])) {
	if ($luna_user['g_id'] != LUNA_ADMIN && ($luna_user['g_moderator'] != '1' || $luna_user['g_mod_ban_users'] == '0'))
		message_backstage(__('You do not have permission to access this page.', 'luna'), false, '403 Forbidden');

	confirm_referrer('backstage/users.php');

	if (isset($_POST['users'])) {
		$user_ids = is_array($_POST['users']) ? array_keys($_POST['users']) : explode(',', $_POST['users']);
		$user_ids = array_map('intval', $user_ids);

		// Delete invalid IDs
		$user_ids = array_diff($user_ids, array(0, 1));
	} else
		$user_ids = array();

	if (empty($user_ids))
		message_backstage(__('No users selected.', 'luna'));

	// Are we trying to ban any admins?
	$result = $db->query('SELECT COUNT(*) FROM '.$db->prefix.'users WHERE id IN ('.implode(',', $user_ids).') AND group_id='.LUNA_ADMIN) or error('Unable to fetch group info', __FILE__, __LINE__, $db->error());
	if ($db->result($result) > 0)
		message_backstage(__('Administrators cannot be banned. In order to ban administrators, you must first move them to a different user group.', 'luna'));

	// Also, we cannot ban moderators
	$result = $db->query('SELECT COUNT(*) FROM '.$db->prefix.'users AS u INNER JOIN '.$db->prefix.'groups AS g ON u.group_id=g.g_id WHERE g.g_moderator=1 AND u.id IN ('.implode(',', $user_ids).')') or error('Unable to fetch moderator group info', __FILE__, __LINE__, $db->error());
	if ($db->result($result) > 0)
		message_backstage(__('Moderators cannot be banned. In order to ban moderators, you must first move them to a different user group.', 'luna'));

	if (isset($_POST['ban_users_comply'])) {
		$ban_message = luna_trim($_POST['ban_message']);
		$ban_expire = luna_trim($_POST['ban_expire']);
		$ban_the_ip = isset($_POST['ban_the_ip']) ? intval($_POST['ban_the_ip']) : 0;

		if ($ban_expire != '' && $ban_expire != 'Never') {
			$ban_expire = strtotime($ban_expire.' GMT');

			if ($ban_expire == -1 || !$ban_expire)
				message_backstage(__('You entered an invalid expire date.', 'luna').' '.__('The format should be YYYY-MM-DD and the date must be at least one day in the future.', 'luna'));

			if ($ban_expire <= time())
				message_backstage(__('You entered an invalid expire date.', 'luna').' '.__('The format should be YYYY-MM-DD and the date must be at least one day in the future.', 'luna'));
		} else
			$ban_expire = 'NULL';

		$ban_message = ($ban_message != '') ? '\''.$db->escape($ban_message).'\'' : 'NULL';

		// Fetch user information
		$user_info = array();
		$result = $db->query('SELECT id, username, email, registration_ip FROM '.$db->prefix.'users WHERE id IN ('.implode(',', $user_ids).')') or error('Unable to fetch user info', __FILE__, __LINE__, $db->error());
		while ($cur_user = $db->fetch_assoc($result))
			$user_info[$cur_user['id']] = array('username' => $cur_user['username'], 'email' => $cur_user['email'], 'ip' => $cur_user['registration_ip']);

		// Overwrite the registration IP with one from the last comment (if it exists)
		if ($ban_the_ip != 0) {
			$result = $db->query('SELECT p.commenter_id, p.commenter_ip FROM '.$db->prefix.'comments AS p INNER JOIN (SELECT MAX(id) AS id FROM '.$db->prefix.'comments WHERE commenter_id IN ('.implode(',', $user_ids).') GROUP BY commenter_id) AS i ON p.id=i.id') or error('Unable to fetch comment info', __FILE__, __LINE__, $db->error());
			while ($cur_address = $db->fetch_assoc($result))
				$user_info[$cur_address['commenter_id']]['ip'] = $cur_address['commenter_ip'];
		}

		// And insert the bans!
		foreach ($user_ids as $user_id) {
			$ban_username = '\''.$db->escape($user_info[$user_id]['username']).'\'';
			$ban_email = '\''.$db->escape($user_info[$user_id]['email']).'\'';
			$ban_ip = ($ban_the_ip != 0) ? '\''.$db->escape($user_info[$user_id]['ip']).'\'' : 'NULL';

			$db->query('INSERT INTO '.$db->prefix.'bans (username, ip, email, message, expire, ban_creator) VALUES('.$ban_username.', '.$ban_ip.', '.$ban_email.', '.$ban_message.', '.$ban_expire.', '.$luna_user['id'].')') or error('Unable to add ban', __FILE__, __LINE__, $db->error());
		}

		// Regenerate the bans cache
		if (!defined('LUNA_CACHE_FUNCTIONS_LOADED'))
			require LUNA_ROOT.'include/cache.php';

		generate_bans_cache();

		redirect('backstage/users.php');
	}

	$focus_element = array('bans2', 'ban_message');
	require 'header.php';

?>
<div class="row">
	<div class="col-sm-12">
        <form id="bans2" class="form-horizontal" name="confirm_ban_users" method="post" action="users.php">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('Ban users', 'luna') ?><span class="pull-right"><button class="btn btn-danger" type="submit" name="ban_users_comply" tabindex="3"><span class="fa fa-fw fa-ban"></span> <?php _e('Ban', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <input type="hidden" name="users" value="<?php echo implode(',', $user_ids) ?>" />
                    <fieldset>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Ban message', 'luna') ?></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="ban_message" maxlength="255" tabindex="1" />
                                <span class="help-block"><?php _e('A message for banned users', 'luna') ?></span>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Expire date', 'luna') ?></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="ban_expire" maxlength="10" tabindex="2" />
                                <span class="help-block"><?php _e('When does the ban expire, blank for manually', 'luna') ?></span>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Ban IP addresses', 'luna') ?></label>
                            <div class="col-sm-9">
                                <label class="radio-inline">
                                    <input type="radio" name="ban_the_ip" tabindex="3" value="1" checked />
                                    <?php _e('Yes', 'luna') ?>
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" name="ban_the_ip" tabindex="4" value="0" checked />
                                    <?php _e('No', 'luna') ?>
                                </label>
                                <span class="help-block"><?php _e('Also ban the IP addresses of the banned users to make registering a new account more difficult for them.', 'luna') ?></span>
                            </div>
                        </div>
                    </fieldset>
                 </div>
            </div>
        </form>
    </div>
</div>
<?php

	require 'footer.php';
} elseif (isset($_GET['find_user'])) {
	$form = isset($_GET['form']) ? $_GET['form'] : array();

	// trim() all elements in $form
	$form = array_map('luna_trim', $form);
	$conditions = $query_str = array();

	$comments_greater = isset($_GET['comments_greater']) ? luna_trim($_GET['comments_greater']) : '';
	$comments_less = isset($_GET['comments_less']) ? luna_trim($_GET['comments_less']) : '';
	$order_by = isset($_GET['order_by']) && in_array($_GET['order_by'], array('username', 'email', 'num_comments', 'last_comment', 'last_visit', 'registered')) ? $_GET['order_by'] : 'username';
	$direction = isset($_GET['direction']) && $_GET['direction'] == 'DESC' ? 'DESC' : 'ASC';
	$user_group = isset($_GET['user_group']) ? intval($_GET['user_group']) : -1;

	$query_str[] = 'order_by='.$order_by;
	$query_str[] = 'direction='.$direction;
	$query_str[] = 'user_group='.$user_group;

	if (preg_match('%[^0-9]%', $comments_greater.$comments_less))
		message_backstage(__('You entered a non-numeric value into a numeric only column.', 'luna'));

	$like_command = ($db_type == 'pgsql') ? 'ILIKE' : 'LIKE';
	foreach ($form as $key => $input) {
		if ($input != '' && in_array($key, array('username', 'email', 'title', 'realname', 'admin_note'))) {
			$conditions[] = 'u.'.$db->escape($key).' '.$like_command.' \''.$db->escape(str_replace('*', '%', $input)).'\'';
			$query_str[] = 'form%5B'.$key.'%5D='.urlencode($input);
		}
	}

	if ($comments_greater != '') {
		$query_str[] = 'comments_greater='.$comments_greater;
		$conditions[] = 'u.num_comments>'.$comments_greater;
	}
	if ($comments_less != '') {
		$query_str[] = 'comments_less='.$comments_less;
		$conditions[] = 'u.num_comments<'.$comments_less;
	}

	if ($user_group > -1)
		$conditions[] = 'u.group_id='.$user_group;

	// Fetch user count
	$result = $db->query('SELECT COUNT(id) FROM '.$db->prefix.'users AS u LEFT JOIN '.$db->prefix.'groups AS g ON g.g_id=u.group_id WHERE u.id>1'.(!empty($conditions) ? ' AND '.implode(' AND ', $conditions) : '')) or error('Unable to fetch user info', __FILE__, __LINE__, $db->error());
	$num_users = $db->result($result);

	// Determine the user offset (based on $_GET['p'])
	$num_pages = ceil($num_users / 50);

	$p = (!isset($_GET['p']) || $_GET['p'] <= 1 || $_GET['p'] > $num_pages) ? 1 : intval($_GET['p']);
	$start_from = 50 * ($p - 1);

	// Generate paging links
	$paging_links = paginate($num_pages, $p, 'users.php?find_user=&amp;'.implode('&amp;', $query_str));

	// Some helper variables for permissions
	$can_delete = $can_move = $luna_user['g_id'] == LUNA_ADMIN;
	$can_ban = $luna_user['g_id'] == LUNA_ADMIN || ($luna_user['g_moderator'] == '1' && $luna_user['g_mod_ban_users'] == '1');
	$can_action = ($can_delete || $can_ban || $can_move) && $num_users > 0;

	$page_head = array('js' => '<script type="text/javascript" src="../common.js"></script>');
	require 'header.php';

?>
<div class="row">
	<div class="col-sm-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title"><?php _e('Search Results', 'luna') ?></h3>
            </div>
            <form id="search-users-form" action="users.php" method="post">
                <div class="panel-body">
                    <?php echo $paging_links ?>
                    <?php if ($can_action): ?>
                        <span class="btn-toolbar pull-right">
                            <div class="btn-group">
                                <?php if ($can_ban) : ?>
                                <button class="btn btn-danger" type="submit" name="ban_users"><span class="fa fa-fw fa-ban"></span> <?php _e('Ban', 'luna') ?></button>
                                <?php endif; if ($can_delete) : ?>
                                <button class="btn btn-danger" type="submit" name="delete_users"><span class="fa fa-fw fa-trash"></span> <?php _e('Delete', 'luna') ?></button>
                                <?php endif; if ($can_move) : ?>
                                <button class="btn btn-primary" type="submit" name="move_users"><span class="fa fa-fw fa-exchange"></span> <?php _e('Change group', 'luna') ?></button>
                                <?php endif; ?>
                            </div>
                        </span>
                    <?php endif; ?>
                </div>
                <table class="table table-js table-striped table-hover">
                    <thead>
                        <tr>
                            <?php if ($can_action): ?><th style="width: 25px;"><input type="checkbox" id="checkall" /></th><?php endif; ?>
                            <th><?php _e('Username', 'luna') ?></th>
                            <th><?php _e('Email', 'luna') ?></th>
                            <th><?php _e('Title/Status', 'luna') ?></th>
                            <th class="text-center"><?php _e('Comments', 'luna') ?></th>
                            <th><?php _e('Admin note', 'luna') ?></th>
                            <th><?php _e('Actions', 'luna') ?></th>
                        </tr>
                    </thead>
                    <tbody>
<?php

	$result = $db->query('SELECT u.id, u.username, u.email, u.title, u.num_comments, u.admin_note, g.g_id, g.g_user_title FROM '.$db->prefix.'users AS u LEFT JOIN '.$db->prefix.'groups AS g ON g.g_id=u.group_id WHERE u.id>1'.(!empty($conditions) ? ' AND '.implode(' AND ', $conditions) : '').' ORDER BY '.$db->escape($order_by).' '.$db->escape($direction).' LIMIT '.$start_from.', 50') or error('Unable to fetch user info', __FILE__, __LINE__, $db->error());
	if ($db->num_rows($result)) {
		while ($user_data = $db->fetch_assoc($result)) {
			$user_title = get_title($user_data);

			// This script is a special case in that we want to display "Not verified" for non-verified users
			if (($user_data['g_id'] == '' || $user_data['g_id'] == LUNA_UNVERIFIED) && $user_title != __('Banned', 'luna'))
				$user_title = '<span class="warntext">'.__('Not verified', 'luna').'</span>';

			$actions = '<a href="users.php?ip_stats='.$user_data['id'].'">'.__('IP stats', 'luna').'</a> &middot; <a href="../search.php?action=show_user_comments&amp;user_id='.$user_data['id'].'">'.__('Comments', 'luna').'</a>';

?>
                        <tr>
                            <?php if ($can_action): ?><td><input type="checkbox" name="users[<?php echo $user_data['id'] ?>]" value="1" /></td><?php endif; ?>
                            <td><?php echo '<a href="../profile.php?id='.$user_data['id'].'">'.luna_htmlspecialchars($user_data['username']).'</a>' ?></td>
                            <td><a href="mailto:<?php echo luna_htmlspecialchars($user_data['email']) ?>"><?php echo luna_htmlspecialchars($user_data['email']) ?></a></td>				 <td><?php echo $user_title ?></td>
                            <td class="text-center"><?php echo forum_number_format($user_data['num_comments']) ?></td>
                            <td><?php echo ($user_data['admin_note'] != '') ? luna_htmlspecialchars($user_data['admin_note']) : '&#160;' ?></td>
                            <td><?php echo $actions ?></td>
                        </tr>
<?php

		}
	} else
		echo "\t\t\t\t".'<tr><td colspan="6">'.__('No match', 'luna').'</td></tr>'."\n";

?>
                    </tbody>
                </table>
                <div class="panel-body">
                    <?php echo $paging_links ?>
                    <?php if ($can_action): ?>
                        <span class="btn-toolbar pull-right">
                            <div class="btn-group">
                                <?php if ($can_ban) : ?>
                                <button class="btn btn-danger" type="submit" name="ban_users"><span class="fa fa-fw fa-ban"></span> <?php _e('Ban', 'luna') ?></button>
                                <?php endif; if ($can_delete) : ?>
                                <button class="btn btn-danger" type="submit" name="delete_users"><span class="fa fa-fw fa-trash"></span> <?php _e('Delete', 'luna') ?></button>
                                <?php endif; if ($can_move) : ?>
                                <button class="btn btn-primary" type="submit" name="move_users"><span class="fa fa-fw fa-exchange"></span> <?php _e('Change group', 'luna') ?></button>
                                <?php endif; ?>
                            </div>
                        </span>
                    <?php endif; ?>
                </div>
            </form>
        </div>
    </div>
</div>
<?php

	require 'footer.php';
} else {
	$focus_element = array('find_user', 'form[username]');
	require 'header.php';
?>
<div class="row">
	<div class="col-sm-12">
<?php
if (isset($_GET['saved']))
	echo '<div class="alert alert-success"><i class="fa fa-fw fa-check"></i> '.__('Your settings have been saved.', 'luna').'</div>';
if (isset($_GET['deleted']))
    echo '<div class="alert alert-danger"><i class="fa fa-fw fa-check"></i> '.__('The user has been deleted.', 'luna').'</div>';
?>
        <form id="find_user" method="get" action="users.php" class="form-horizontal">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('User search', 'luna') ?><span class="pull-right"><button class="btn btn-primary" type="submit" name="find_user"><span class="fa fa-fw fa-search"></span> <?php _e('Search', 'luna') ?></button></span></h3>
                </div>
                <fieldset>
                    <div class="panel-body">
                        <p class="alert alert-info"><i class="fa fa-fw fa-info-circle"></i> <?php _e('Enter a username to search for and/or a user group to filter by. Use the wildcard character * for partial matches.', 'luna') ?></p>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Username', 'luna') ?></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="form[username]" maxlength="25" tabindex="2" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Email address', 'luna') ?></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="form[email]" maxlength="80" tabindex="3" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Title', 'luna') ?></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="form[title]" maxlength="50" tabindex="4" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Real name', 'luna') ?></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="form[realname]" maxlength="40" tabindex="5" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('User group', 'luna') ?></label>
                            <div class="col-sm-9">
                                <select class="form-control" name="user_group" tabindex="23">
                                    <option value="-1" selected><?php _e('All groups', 'luna') ?></option>
                                    <option value="0"><?php _e('Unverified users', 'luna') ?></option>
<?php

	$result = $db->query('SELECT g_id, g_title FROM '.$db->prefix.'groups WHERE g_id!='.LUNA_GUEST.' ORDER BY g_title') or error('Unable to fetch user group list', __FILE__, __LINE__, $db->error());

	while ($cur_group = $db->fetch_assoc($result))
		echo "\t\t\t\t\t\t\t\t\t\t\t".'<option value="'.$cur_group['g_id'].'">'.luna_htmlspecialchars($cur_group['g_title']).'</option>'."\n";

?>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Admin note', 'luna') ?></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="form[admin_note]" maxlength="30" tabindex="13" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Less comments than', 'luna') ?></label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" name="comments_less" maxlength="8" tabindex="14" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('More comments than', 'luna') ?></label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" name="comments_greater" maxlength="8" tabindex="15" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Order by', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="row">
                                    <div class="col-sm-6">
                                        <select class="form-control" name="order_by" tabindex="22">
                                            <option value="username" selected><?php _e('Username', 'luna') ?></option>
                                            <option value="email"><?php _e('Email', 'luna') ?></option>
                                            <option value="num_comments"><?php _e('Number of comments', 'luna') ?></option>
                                            <option value="last_comment"><?php _e('Last comment', 'luna') ?></option>
                                            <option value="last_visit"><?php _e('Last visit', 'luna') ?></option>
                                            <option value="registered"><?php _e('Registered', 'luna') ?></option>
                                        </select>
                                    </div>
                                    <div class="col-sm-6">
                                        <select class="form-control" name="direction" tabindex="23">
                                            <option value="ASC" selected><?php _e('Ascending', 'luna') ?></option>
                                            <option value="DESC"><?php _e('Descending', 'luna') ?></option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </fieldset>
            </div>
        </form>
        <form method="get" action="users.php" class="form-horizontal panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title"><?php _e('IP search', 'luna') ?><span class="pull-right"><button class="btn btn-primary" type="submit"><span class="fa fa-fw fa-search"></span> <?php _e('Find IP address', 'luna') ?></button></span></h3>
            </div>
            <div class="panel-body">
                <div class="form-group">
                    <label class="col-sm-3 control-label"><?php _e('IP search', 'luna') ?><span class="help-block"><?php _e('The IP address to search for in the comment database', 'luna') ?></span></label>
                    <div class="col-sm-9">
                        <input type="text" class="form-control" name="show_users" maxlength="15" tabindex="24" />
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>
<?php

	require 'footer.php';
}
