<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

define('LUNA_ROOT', '../');
require LUNA_ROOT.'include/common.php';
define('LUNA_SECTION', 'users');
define('LUNA_PAGE', 'bans');

if ($luna_user['g_id'] != LUNA_ADMIN && ($luna_user['g_moderator'] != '1' || $luna_user['g_mod_ban_users'] == '0')) {
	header("Location: login.php");
    exit;
}

// Add/edit a ban (stage 1)
if (isset($_REQUEST['add_ban']) || isset($_GET['edit_ban'])) {
	if (isset($_GET['add_ban']) || isset($_POST['add_ban'])) {
		// If the ID of the user to ban was provided through GET (a link from ../profile.php)
		if (isset($_GET['add_ban'])) {
			$user_id = intval($_GET['add_ban']);
			if ($user_id < 2)
				message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

			$result = $db->query('SELECT group_id, username, email FROM '.$db->prefix.'users WHERE id='.$user_id) or error('Unable to fetch user info', __FILE__, __LINE__, $db->error());
			if ($db->num_rows($result))
				list($group_id, $ban_user, $ban_email) = $db->fetch_row($result);
			else
				message_backstage(__('No user by that ID registered.', 'luna'));
		} else { // Otherwise the username is in POST
			$ban_user = luna_trim($_POST['new_ban_user']);

			if ($ban_user != '') {
				$result = $db->query('SELECT id, group_id, username, email FROM '.$db->prefix.'users WHERE username=\''.$db->escape($ban_user).'\' AND id>1') or error('Unable to fetch user info', __FILE__, __LINE__, $db->error());
				if ($db->num_rows($result))
					list($user_id, $group_id, $ban_user, $ban_email) = $db->fetch_row($result);
				else
					message_backstage(__('No user by that username registered. If you want to add a ban not tied to a specific username just leave the username blank.', 'luna'));
			}
		}

		// Make sure we're not banning an admin or moderator
		if (isset($group_id)) {
			if ($group_id == LUNA_ADMIN)
				message_backstage(sprintf(__('The user %s is an administrator and can\'t be banned. If you want to ban an administrator, you must first demote him/her.', 'luna'), luna_htmlspecialchars($ban_user)));

			$result = $db->query('SELECT g_moderator FROM '.$db->prefix.'groups WHERE g_id='.$group_id) or error('Unable to fetch group info', __FILE__, __LINE__, $db->error());
			$is_moderator_group = $db->result($result);

			if ($is_moderator_group)
				message_backstage(sprintf(__('The user %s is a moderator and can\'t be banned. If you want to ban a moderator, you must first demote him/her.', 'luna'), luna_htmlspecialchars($ban_user)));
		}

		// If we have a $user_id, we can try to find the last known IP of that user
		if (isset($user_id)) {
			$result = $db->query('SELECT commenter_ip FROM '.$db->prefix.'comments WHERE commenter_id='.$user_id.' ORDER BY commented DESC LIMIT 1') or error('Unable to fetch comment info', __FILE__, __LINE__, $db->error());
			$ban_ip = ($db->num_rows($result)) ? $db->result($result) : '';

			if ($ban_ip == '') {
				$result = $db->query('SELECT registration_ip FROM '.$db->prefix.'users WHERE id='.$user_id) or error('Unable to fetch user info', __FILE__, __LINE__, $db->error());
				$ban_ip = ($db->num_rows($result)) ? $db->result($result) : '';
			}
		}

		$mode = 'add';
	} else { // We are editing a ban
		$ban_id = intval($_GET['edit_ban']);
		if ($ban_id < 1)
			message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

		$result = $db->query('SELECT username, ip, email, message, expire FROM '.$db->prefix.'bans WHERE id='.$ban_id) or error('Unable to fetch ban info', __FILE__, __LINE__, $db->error());
		if ($db->num_rows($result))
			list($ban_user, $ban_ip, $ban_email, $ban_message, $ban_expire) = $db->fetch_row($result);
		else
			message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

		$ban_expire = ($ban_expire != '') ? date('Y-m-d', $ban_expire) : '';

		$mode = 'edit';
	}

	$focus_element = array('bans2', 'ban_user');

    require 'header.php';

?>
<div class="row">
	<div class="col-sm-12">
        <form class="form-horizontal" id="bans2" method="post" action="bans.php">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('Ban range', 'luna') ?><span class="pull-right"><button class="btn btn-danger" type="submit" name="add_edit_ban" tabindex="6"><span class="fa fa-fw fa-ban"></span> <?php _e('Ban', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <input type="hidden" name="mode" value="<?php echo $mode ?>" />
                    <?php if ($mode == 'edit'): ?><input type="hidden" name="ban_id" value="<?php echo $ban_id ?>" /><?php endif; ?>
                    <fieldset>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Username', 'luna') ?><span class="help-block"><?php _e('The username to ban', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="ban_user" maxlength="25" value="<?php if (isset($ban_user)) echo luna_htmlspecialchars($ban_user); ?>" tabindex="1" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('IP address/IP-ranges', 'luna') ?><span class="help-block"><?php _e('The IP you wish to ban, separate addresses with spaces', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="ban_ip" maxlength="255" value="<?php if (isset($ban_ip)) echo luna_htmlspecialchars($ban_ip); ?>" tabindex="2" />
                                <?php if ($ban_user != '' && isset($user_id)) printf('<span class="help-block">'.__('Click %s to see IP statistics for this user.', 'luna').'</span>', '<a href="users.php?ip_stats='.$user_id.'">'.__('here', 'luna').'</a>') ?>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Email', 'luna') ?><span class="help-block"><?php _e('The email or email domain you wish to ban', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="ban_email" maxlength="80" value="<?php if (isset($ban_email)) echo luna_htmlspecialchars($ban_email); ?>" tabindex="3" />
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('Ban details', 'luna') ?><span class="pull-right"><button class="btn btn-danger" type="submit" name="add_edit_ban" tabindex="6"><span class="fa fa-fw fa-ban"></span> <?php _e('Ban', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <fieldset>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Ban message', 'luna') ?><span class="help-block"><?php _e('A message for banned users', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="ban_message" maxlength="255" value="<?php if (isset($ban_message)) echo luna_htmlspecialchars($ban_message); ?>" tabindex="4" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Expire date', 'luna') ?><span class="help-block"><?php _e('When does the ban expire, blank for manually', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <input type="date" class="form-control" name="ban_expire" maxlength="10" placeholder="<?php _e('(yyyy-mm-dd)', 'luna') ?>" value="<?php if (isset($ban_expire)) echo $ban_expire; ?>" tabindex="5" />
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
}

// Add/edit a ban (stage 2)
elseif (isset($_POST['add_edit_ban'])) {
	confirm_referrer('backstage/bans.php');

	$ban_user = luna_trim($_POST['ban_user']);
	$ban_ip = luna_trim($_POST['ban_ip']);
	$ban_email = strtolower(luna_trim($_POST['ban_email']));
	$ban_message = luna_trim($_POST['ban_message']);
	$ban_expire = luna_trim($_POST['ban_expire']);

	if ($ban_user == '' && $ban_ip == '' && $ban_email == '')
		message_backstage(__('You must enter either a username, an IP address or an email address (at least).', 'luna'));
	elseif (strtolower($ban_user) == 'guest')
		message_backstage(__('The guest user cannot be banned.', 'luna'));

	// Make sure we're not banning an admin or moderator
	if (!empty($ban_user)) {
		$result = $db->query('SELECT group_id FROM '.$db->prefix.'users WHERE username=\''.$db->escape($ban_user).'\' AND id>1') or error('Unable to fetch user info', __FILE__, __LINE__, $db->error());
		if ($db->num_rows($result)) {
			$group_id = $db->result($result);

			if ($group_id == LUNA_ADMIN)
				message_backstage(sprintf(__('The user %s is an administrator and can\'t be banned. If you want to ban an administrator, you must first demote him/her.', 'luna'), luna_htmlspecialchars($ban_user)));

			$result = $db->query('SELECT g_moderator FROM '.$db->prefix.'groups WHERE g_id='.$group_id) or error('Unable to fetch group info', __FILE__, __LINE__, $db->error());
			$is_moderator_group = $db->result($result);

			if ($is_moderator_group)
				message_backstage(sprintf(__('The user %s is a moderator and can\'t be banned. If you want to ban a moderator, you must first demote him/her.', 'luna'), luna_htmlspecialchars($ban_user)));
		}
	}

	// Validate IP/IP range (it's overkill, I know)
	if ($ban_ip != '') {
		$ban_ip = preg_replace('%\s{2,}%S', ' ', $ban_ip);
		$addresses = explode(' ', $ban_ip);
		$addresses = array_map('luna_trim', $addresses);

		for ($i = 0; $i < count($addresses); ++$i) {
			if (strpos($addresses[$i], ':') !== false) {
				$octets = explode(':', $addresses[$i]);

				for ($c = 0; $c < count($octets); ++$c) {
					$octets[$c] = ltrim($octets[$c], "0");

					if ($c > 7 || (!empty($octets[$c]) && !ctype_xdigit($octets[$c])) || intval($octets[$c], 16) > 65535)
						message_backstage(__('You entered an invalid IP/IP-range.', 'luna'));
				}

				$cur_address = implode(':', $octets);
				$addresses[$i] = $cur_address;
			} else {
				$octets = explode('.', $addresses[$i]);

				for ($c = 0; $c < count($octets); ++$c) {
					$octets[$c] = (strlen($octets[$c]) > 1) ? ltrim($octets[$c], "0") : $octets[$c];

					if ($c > 3 || preg_match('%[^0-9]%', $octets[$c]) || intval($octets[$c]) > 255)
						message_backstage(__('You entered an invalid IP/IP-range.', 'luna'));
				}

				$cur_address = implode('.', $octets);
				$addresses[$i] = $cur_address;
			}
		}

		$ban_ip = implode(' ', $addresses);
	}

	require LUNA_ROOT.'include/email.php';
	if ($ban_email != '' && !is_valid_email($ban_email)) {
		if (!preg_match('%^[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,4})$%', $ban_email))
			message_backstage(__('The email address (e.g. user@domain.com) or partial email address domain (e.g. domain.com) you entered is invalid.', 'luna'));
	}

	if ($ban_expire != '' && $ban_expire != 'Never') {
		$ban_expire = strtotime($ban_expire.' GMT');

		if ($ban_expire == -1 || !$ban_expire)
			message_backstage(__('You entered an invalid expire date.', 'luna').' '.__('The format should be YYYY-MM-DD and the date must be at least one day in the future.', 'luna'));

		if ($ban_expire <= time())
			message_backstage(__('You entered an invalid expire date.', 'luna').' '.__('The format should be YYYY-MM-DD and the date must be at least one day in the future.', 'luna'));
	} else
		$ban_expire = 'NULL';

	$ban_user = ($ban_user != '') ? '\''.$db->escape($ban_user).'\'' : 'NULL';
	$ban_ip = ($ban_ip != '') ? '\''.$db->escape($ban_ip).'\'' : 'NULL';
	$ban_email = ($ban_email != '') ? '\''.$db->escape($ban_email).'\'' : 'NULL';
	$ban_message = ($ban_message != '') ? '\''.$db->escape($ban_message).'\'' : 'NULL';

	if ($_POST['mode'] == 'add')
		$db->query('INSERT INTO '.$db->prefix.'bans (username, ip, email, message, expire, ban_creator) VALUES('.$ban_user.', '.$ban_ip.', '.$ban_email.', '.$ban_message.', '.$ban_expire.', '.$luna_user['id'].')') or error('Unable to add ban', __FILE__, __LINE__, $db->error());
	else
		$db->query('UPDATE '.$db->prefix.'bans SET username='.$ban_user.', ip='.$ban_ip.', email='.$ban_email.', message='.$ban_message.', expire='.$ban_expire.' WHERE id='.intval($_POST['ban_id'])) or error('Unable to update ban', __FILE__, __LINE__, $db->error());

	// Regenerate the bans cache
	if (!defined('LUNA_CACHE_FUNCTIONS_LOADED'))
		require LUNA_ROOT.'include/cache.php';

	generate_bans_cache();

	redirect('backstage/bans.php');
}

// Remove a ban
elseif (isset($_GET['del_ban'])) {
	confirm_referrer('backstage/bans.php');

	$ban_id = intval($_GET['del_ban']);
	if ($ban_id < 1)
		message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

	$db->query('DELETE FROM '.$db->prefix.'bans WHERE id='.$ban_id) or error('Unable to delete ban', __FILE__, __LINE__, $db->error());

	// Regenerate the bans cache
	if (!defined('LUNA_CACHE_FUNCTIONS_LOADED'))
		require LUNA_ROOT.'include/cache.php';

	generate_bans_cache();

	redirect('backstage/bans.php');
}

// Find bans
elseif (isset($_GET['find_ban'])) {
	$form = isset($_GET['form']) ? $_GET['form'] : array();

	// trim() all elements in $form
	$form = array_map('luna_trim', $form);
	$conditions = $query_str = array();

	$expire_after = isset($_GET['expire_after']) ? luna_trim($_GET['expire_after']) : '';
	$expire_before = isset($_GET['expire_before']) ? luna_trim($_GET['expire_before']) : '';
	$order_by = isset($_GET['order_by']) && in_array($_GET['order_by'], array('username', 'ip', 'email', 'expire')) ? 'b.'.$_GET['order_by'] : 'b.username';
	$direction = isset($_GET['direction']) && $_GET['direction'] == 'DESC' ? 'DESC' : 'ASC';

	$query_str[] = 'order_by='.$order_by;
	$query_str[] = 'direction='.$direction;

	// Try to convert date/time to timestamps
	if ($expire_after != '') {
		$query_str[] = 'expire_after='.$expire_after;

		$expire_after = strtotime($expire_after);
		if ($expire_after === false || $expire_after == -1)
			message_backstage(__('You entered an invalid expire date.', 'luna'));

		$conditions[] = 'b.expire>'.$expire_after;
	}
	if ($expire_before != '') {
		$query_str[] = 'expire_before='.$expire_before;

		$expire_before = strtotime($expire_before);
		if ($expire_before === false || $expire_before == -1)
			message_backstage(__('You entered an invalid expire date.', 'luna'));

		$conditions[] = 'b.expire<'.$expire_before;
	}

	$like_command = ($db_type == 'pgsql') ? 'ILIKE' : 'LIKE';
	foreach ($form as $key => $input) {
		if ($input != '' && in_array($key, array('username', 'ip', 'email', 'message'))) {
			$conditions[] = 'b.'.$db->escape($key).' '.$like_command.' \''.$db->escape(str_replace('*', '%', $input)).'\'';
			$query_str[] = 'form%5B'.$key.'%5D='.urlencode($input);
		}
	}

	// Fetch ban count
	$result = $db->query('SELECT COUNT(id) FROM '.$db->prefix.'bans as b WHERE b.id>0'.(!empty($conditions) ? ' AND '.implode(' AND ', $conditions) : '')) or error('Unable to fetch ban list', __FILE__, __LINE__, $db->error());
	$num_bans = $db->result($result);

	// Determine the ban offset (based on $_GET['p'])
	$num_pages = ceil($num_bans / 50);

	$p = (!isset($_GET['p']) || $_GET['p'] <= 1 || $_GET['p'] > $num_pages) ? 1 : intval($_GET['p']);
	$start_from = 50 * ($p - 1);

	// Generate paging links
	$paging_links = paginate($num_pages, $p, 'bans.php?find_ban=&amp;'.implode('&amp;', $query_str));

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
            <table class="table table-striped table-hover">
                <thead>
                    <tr>
                        <th class="col-xs-1"><?php _e('Username', 'luna') ?></th>
                        <th class="col-xs-2"><?php _e('Email', 'luna') ?></th>
                        <th class="col-xs-1"><?php _e('IP', 'luna') ?></th>
                        <th class="col-xs-1"><?php _e('Expires', 'luna') ?></th>
                        <th class="col-xs-3"><?php _e('Message', 'luna') ?></th>
                        <th class="col-xs-1"><?php _e('By', 'luna') ?></th>
                        <th class="col-xs-3"><?php _e('Actions', 'luna') ?></th>
                    </tr>
                </thead>
                <tbody>
	<?php

	$result = $db->query('SELECT b.id, b.username, b.ip, b.email, b.message, b.expire, b.ban_creator, u.username AS ban_creator_username FROM '.$db->prefix.'bans AS b LEFT JOIN '.$db->prefix.'users AS u ON b.ban_creator=u.id WHERE b.id>0'.(!empty($conditions) ? ' AND '.implode(' AND ', $conditions) : '').' ORDER BY '.$db->escape($order_by).' '.$db->escape($direction).' LIMIT '.$start_from.', 50') or error('Unable to fetch ban list', __FILE__, __LINE__, $db->error());
	if ($db->num_rows($result)) {
		while ($ban_data = $db->fetch_assoc($result)) {

			$actions = '<div class="btn-group"><a class="btn btn-primary" href="bans.php?edit_ban='.$ban_data['id'].'"><span class="fa fa-fw fa-pencil-square-o"></span> '.__('Edit', 'luna').'</a><a class="btn btn-danger" href="bans.php?del_ban='.$ban_data['id'].'"><span class="fa fa-fw fa-trash"></span> '.__('Remove', 'luna').'</a></div>';
			$expire = format_time($ban_data['expire'], true);

?>
                    <tr>
                        <td><?php echo ($ban_data['username'] != '') ? luna_htmlspecialchars($ban_data['username']) : '&#160;' ?></td>
                        <td><?php echo ($ban_data['email'] != '') ? luna_htmlspecialchars($ban_data['email']) : '&#160;' ?></td>
                        <td><?php echo ($ban_data['ip'] != '') ? luna_htmlspecialchars($ban_data['ip']) : '&#160;' ?></td>
                        <td><?php echo $expire ?></td>
                        <td><?php echo ($ban_data['message'] != '') ? luna_htmlspecialchars($ban_data['message']) : '&#160;' ?></td>
                        <td><?php echo ($ban_data['ban_creator_username'] != '') ? '<a href="../profile.php?id='.$ban_data['ban_creator'].'">'.luna_htmlspecialchars($ban_data['ban_creator_username']).'</a>' : __('Unknown', 'luna') ?></td>
                        <td><?php echo $actions ?></td>
                    </tr>
<?php

		}
	} else
		echo "\t\t\t\t".'<tr><td class="tcl" colspan="7">'.__('No match', 'luna').'</td></tr>'."\n";

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
} else {
	$focus_element = array('bans', 'new_ban_user');

    require 'header.php';
?>
<div class="row">
	<div class="col-sm-12">
        <form class="panel panel-default form-horizontal" id="bans" method="post" action="bans.php?action=more">
            <div class="panel-heading">
                <h3 class="panel-title"><?php _e('Add ban', 'luna') ?><span class="pull-right"><button class="btn btn-danger" type="submit" name="add_ban" tabindex="2"><span class="fa fa-fw fa-plus"></span> <?php _e('Add', 'luna') ?></button></span></h3>
            </div>
            <div class="panel-body">
                <div class="form-group">
                    <label class="col-sm-3 control-label"><?php _e('Username', 'luna') ?><span class="help-block"><?php _e('If you want to ban a specific IP/IP-range or email, leave it blank', 'luna') ?></span></label>
                    <div class="col-sm-9">
                        <input type="text" class="form-control" name="new_ban_user" maxlength="25" tabindex="1" />
                    </div>
                </div>
            </div>
        </form>
        <form class="panel panel-default form-horizontal" id="find_bans" method="get" action="bans.php">
            <div class="panel-heading">
                <h3 class="panel-title"><?php _e('Ban search', 'luna') ?><span class="pull-right"><button class="btn btn-primary" type="submit" name="find_ban"><span class="fa fa-fw fa-search"></span> <?php _e('Search', 'luna') ?></button></span></h3>
            </div>
            <div class="panel-body">
                <p class="alert alert-info"><i class="fa fa-fw fa-info-circle"></i> <?php _e('Enter user data to filter by. Use the wildcard character * for partial matches.', 'luna') ?></p>
                <div class="form-group">
                    <label class="col-sm-3 control-label"><?php _e('Username', 'luna') ?></label>
                    <div class="col-sm-9">
                        <input type="text" class="form-control" name="form[username]" maxlength="25" tabindex="4" />
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label"><?php _e('IP address/IP-ranges', 'luna') ?></label>
                    <div class="col-sm-9">
                        <input type="text" class="form-control" name="form[ip]" maxlength="255" tabindex="5" />
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label"><?php _e('Email', 'luna') ?></label>
                    <div class="col-sm-9">
                        <input type="text" class="form-control" name="form[email]" maxlength="80" tabindex="6" />
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label"><?php _e('Message', 'luna') ?></label>
                    <div class="col-sm-9">
                        <input type="text" class="form-control" name="form[message]" maxlength="255" tabindex="7" />
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label"><?php _e('Expire after', 'luna') ?></label>
                    <div class="col-sm-9">
                        <input type="date" class="form-control" name="expire_after" maxlength="10" tabindex="8" placeholder="<?php _e('(yyyy-mm-dd)', 'luna') ?>" />
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label"><?php _e('Expire before', 'luna') ?></label>
                    <div class="col-sm-9">
                        <input type="date" class="form-control" name="expire_before" maxlength="10" tabindex="9" placeholder="<?php _e('(yyyy-mm-dd)', 'luna') ?>" />
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-sm-3 control-label"><?php _e('Order by', 'luna') ?></label>
                    <div class="col-sm-9">
                        <div class="row">
                            <div class="col-sm-6">
                                <select class="form-control" name="order_by" tabindex="10">
                                    <option value="username" selected><?php _e('Username', 'luna') ?></option>
                                    <option value="ip"><?php _e('IP', 'luna') ?></option>
                                    <option value="email"><?php _e('Email', 'luna') ?></option>
                                    <option value="expire"><?php _e('Expire date', 'luna') ?></option>
                                </select>
                            </div>
                            <div class="col-sm-6">
                                <select class="form-control" name="direction" tabindex="11">
                                    <option value="ASC" selected><?php _e('Ascending', 'luna') ?></option>
                                    <option value="DESC"><?php _e('Descending', 'luna') ?></option>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>
</div>
<?php

	require 'footer.php';
}
