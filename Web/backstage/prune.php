<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */
// Tell common.php that we don't want output buffering
define('LUNA_DISABLE_BUFFERING', 1);

define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'maintenance');
define('LUNA_PAGE', 'prune');

require LUNA_ROOT.'include/common.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}

$action = isset($_REQUEST['action']) ? luna_trim($_REQUEST['action']) : '';

if ($action == 'prune') {
	$prune_from = luna_trim($_POST['prune_from']);
	$prune_pinned = intval($_POST['prune_pinned']);

	if (isset($_POST['prune_comply'])) {
		confirm_referrer('backstage/prune.php');

		$prune_days = intval($_POST['prune_days']);
		$prune_date = ($prune_days) ? time() - ($prune_days * 86400) : -1;

		@set_time_limit(0);

		if ($prune_from == 'all') {
			$result = $db->query('SELECT id FROM '.$db->prefix.'forums') or error('Unable to fetch forum list', __FILE__, __LINE__, $db->error());
			$num_forums = $db->num_rows($result);

			for ($i = 0; $i < $num_forums; ++$i) {
				$fid = $db->result($result, $i);

				prune($fid, $prune_pinned, $prune_date);
				update_forum($fid);
			}
		} else {
			$prune_from = intval($prune_from);
			prune($prune_from, $prune_pinned, $prune_date);
			update_forum($prune_from);
		}

		// Locate any "orphaned redirect threads" and delete them
		$result = $db->query('SELECT t1.id FROM '.$db->prefix.'threads AS t1 LEFT JOIN '.$db->prefix.'threads AS t2 ON t1.moved_to=t2.id WHERE t2.id IS NULL AND t1.moved_to IS NOT NULL') or error('Unable to fetch redirect threads', __FILE__, __LINE__, $db->error());
		$num_orphans = $db->num_rows($result);

		if ($num_orphans) {
			for ($i = 0; $i < $num_orphans; ++$i)
				$orphans[] = $db->result($result, $i);

			$db->query('DELETE FROM '.$db->prefix.'threads WHERE id IN('.implode(',', $orphans).')') or error('Unable to delete redirect threads', __FILE__, __LINE__, $db->error());
		}

		redirect('backstage/prune.php');
	}

	$prune_days = luna_trim($_POST['req_prune_days']);
	if ($prune_days == '' || preg_match('%[^0-9]%', $prune_days))
		message_backstage(__('Days to prune must be a positive integer value.', 'luna'));

	$prune_date = time() - ($prune_days * 86400);

	// Concatenate together the query for counting number of threads to prune
	$sql = 'SELECT COUNT(id) FROM '.$db->prefix.'threads WHERE last_comment<'.$prune_date.' AND moved_to IS NULL';

	if ($prune_pinned == '0')
		$sql .= ' AND pinned=0';

	if ($prune_from != 'all') {
		$prune_from = intval($prune_from);
		$sql .= ' AND forum_id='.$prune_from;

		// Fetch the forum name (just for cosmetic reasons)
		$result = $db->query('SELECT forum_name FROM '.$db->prefix.'forums WHERE id='.$prune_from) or error('Unable to fetch forum name', __FILE__, __LINE__, $db->error());
		$forum = '"'.luna_htmlspecialchars($db->result($result)).'"';
	} else
		$forum = __('All forums', 'luna');

	$result = $db->query($sql) or error('Unable to fetch thread prune count', __FILE__, __LINE__, $db->error());
	$num_threads = $db->result($result);

	if (!$num_threads)
		message_backstage(sprintf(__('There are no threads that are %s days old. Please decrease the value of "Days old" and try again.', 'luna'), $prune_days));

    require 'header.php';

?>
<div class="row">
	<div class="col-sm-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title"><?php _e('Prune', 'luna') ?></h3>
            </div>
            <div class="panel-body">
                <form method="post" action="prune.php">
                    <input type="hidden" name="action" value="prune" />
                    <input type="hidden" name="prune_days" value="<?php echo $prune_days ?>" />
                    <input type="hidden" name="prune_pinned" value="<?php echo $prune_pinned ?>" />
                    <input type="hidden" name="prune_from" value="<?php echo $prune_from ?>" />
                    <fieldset>
                        <h3><?php _e('Confirm prune comments', 'luna') ?></h3>
                        <p><?php printf(__('Are you sure that you want to prune all comments older than %s days from %s (%s threads).', 'luna'), $prune_days, $forum, forum_number_format($num_threads)) ?></p>
                        <p class="warntext"><?php _e('Pruning comments deletes them permanently.', 'luna') ?></p>
                    </fieldset>
                    <div class="btn-group">
                        <input class="btn btn-primary" type="submit" name="prune_comply" value="<?php _e('Prune', 'luna') ?>" />
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<?php

	require 'footer.php';
	exit;
}

if (!defined('LUNA_CACHE_FUNCTIONS_LOADED'))
	require LUNA_ROOT.'include/cache.php';

if (isset($_POST['notiprune'])) {
	if ($_POST['prune_type'] == 1)
		$type = ' AND viewed = 1';
	elseif ($_POST['prune_type'] == 2)
		$type = ' AND viewed = 0';
	else
		$type = '';

	$prune_days = intval($_POST['prune_days']);
	if ($prune_days != 0) {
		$prune_date = ($prune_days) ? time() - ($prune_days * 86400) : -1;

		$prune_date_sql = ($prune_date != -1) ? ' WHERE time<'.$prune_date : '';
	} else
		$prune_date_sql = '';

	// Fetch notifications to prune
	$result = $db->query('SELECT id FROM '.$db->prefix.'notifications'.$prune_date_sql.$type, true) or error('Unable to fetch notification IDs', __FILE__, __LINE__, $db->error());

	$notification_ids = '';
	while ($row = $db->fetch_row($result))
		$notification_ids .= (($notification_ids != '') ? ',' : '').$row[0];

	if ($notification_ids != '') {
		// Fetch posts to prune
		$result = $db->query('SELECT id FROM '.$db->prefix.'notifications WHERE id IN('.$notification_ids.')', true) or error('Unable to fetch posts', __FILE__, __LINE__, $db->error());

		$notification_ids = '';
		while ($row = $db->fetch_row($result))
			$notification_ids .= (($notification_ids != '') ? ',' : '').$row[0];

		if ($notification_ids != '')
			$db->query('DELETE FROM '.$db->prefix.'notifications WHERE id IN('.$notification_ids.')') or error('Unable to prune notifications', __FILE__, __LINE__, $db->error());
	}

	message_backstage(__('Pruning complete. Notifications pruned.', 'luna'));
}

if (isset($_POST['userprune'])) {
	// Make sure something something was entered
	if ((trim($_POST['days']) == '') || trim($_POST['comments']) == '')
		message_backstage('You need to set all settings!');

	if ($_POST['admods_delete'])
		$admod_delete = 'group_id > 0';
	else
		$admod_delete = 'group_id > 3';

	if ($_POST['verified'] == 1)
		$verified = '';
	elseif ($_POST['verified'] == 0)
		$verified = 'AND (group_id < 32000)';
	else
		$verified = 'AND (group_id = 32000)';

	$prune = ($_POST['prune_by'] == 1) ? 'registered' : 'last_visit';

	$user_time = time() - ($_POST['days'] * 86400);
	$result = $db->query('SELECT id FROM '.$db->prefix.'users WHERE (num_comments < '.intval($_POST['comments']).') AND ('.$prune.' < '.intval($user_time).') AND (id > 2) AND ('.$admod_delete.')'.$verified, true) or error('Unable to fetch users to prune', __FILE__, __LINE__, $db->error());

	$user_ids = array();
	while ($id = $db->result($result))
		$user_ids[] = $id;

	if (!empty($user_ids)) {
		$db->query('DELETE FROM '.$db->prefix.'users WHERE id IN ('.implode(',', $user_ids).')') or error('Unable to delete users', __FILE__, __LINE__, $db->error());
		$db->query('UPDATE '.$db->prefix.'comments SET commenter_id=1 WHERE commenter_id IN ('.implode(',', $user_ids).')') or error('Unable to mark comments as guest comments', __FILE__, __LINE__, $db->error());
	}

	// Regenerate the users info cache
	generate_users_info_cache();

	$users_pruned = count($user_ids);
	message_backstage(__('Pruning complete, all users that matched the requirements have been pruned.', 'luna'));
}


// Get the first comment ID from the db
$result = $db->query('SELECT id FROM '.$db->prefix.'comments ORDER BY id ASC LIMIT 1') or error('Unable to fetch thread info', __FILE__, __LINE__, $db->error());
if ($db->num_rows($result))
	$first_id = $db->result($result);

require 'header.php';
?>

<div class="row">
	<div class="col-sm-12">
        <div class="alert alert-info"><i class="fa fa-fw fa-info-circle"></i> <?php printf(__('It\'s recommended to activate %s while using the options below.', 'luna'), '<a href="maintenance.php#maintenance">'.__('maintenance mode', 'luna').'</a>') ?></div>
        <form class="form-horizontal" id="notiprune" method="post" action="<?php echo $_SERVER['REQUEST_URI'] ?>">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('Notifications', 'luna') ?><span class="pull-right"><button class="btn btn-primary" name="notiprune" tabindex="8"><span class="fa fa-fw fa-recycle"></span> <?php _e('Prune', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <input type="hidden" name="action" value="notiprune" />
                    <fieldset>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Days old', 'luna') ?><span class="help-block"><?php _e('The number of days old a notification must be to be pruned', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="prune_days" maxlength="3" tabindex="5" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Type', 'luna') ?></label>
                            <div class="col-sm-9">
                                <label class="radio-inline">
                                    <input type="radio" name="prune_type" value="0" tabindex="6" />
                                    <?php _e('All notifications', 'luna') ?>
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" name="prune_type" value="1" checked />
                                    <?php _e('Seen notifications', 'luna') ?>
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" name="prune_type" value="2" />
                                    <?php _e('New notifications', 'luna') ?>
                                </label>
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>
        </form>
        <form class="form-horizontal" method="post" action="prune.php" onsubmit="return process_form(this)">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('Comments', 'luna') ?><span class="pull-right"><button class="btn btn-primary" name="prune" tabindex="8"><span class="fa fa-fw fa-recycle"></span> <?php _e('Prune', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <input type="hidden" name="action" value="prune" />
                    <fieldset>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Days old', 'luna') ?><span class="help-block"><?php _e('The number of days old a thread must be to be pruned', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" name="req_prune_days" maxlength="3" tabindex="5" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Prune pinned threads', 'luna') ?></label>
                            <div class="col-sm-9">
                                <label class="radio-inline">
                                    <input type="radio" name="prune_pinned" value="1" tabindex="6" checked />
                                    <?php _e('Yes', 'luna') ?>
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" name="prune_pinned" value="0" />
                                    <?php _e('No', 'luna') ?>
                                </label>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Prune from', 'luna') ?></label>
                            <div class="col-sm-9">
                                <select class="form-control" name="prune_from" tabindex="7">
                                    <option value="all"><?php _e('All forums', 'luna') ?></option>
<?php

	$result = $db->query('SELECT c.id AS cid, c.cat_name, f.id AS fid, f.forum_name FROM '.$db->prefix.'categories AS c INNER JOIN '.$db->prefix.'forums AS f ON c.id=f.cat_id ORDER BY c.disp_position, c.id, f.disp_position') or error('Unable to fetch category/forum list', __FILE__, __LINE__, $db->error());

	$cur_category = 0;
	while ($forum = $db->fetch_assoc($result)) {
		if ($forum['cid'] != $cur_category) { // Are we still in the same category?
			if ($cur_category)
				echo "\t\t\t\t\t\t\t\t\t\t\t".'</optgroup>'."\n";

			echo "\t\t\t\t\t\t\t\t\t\t\t".'<optgroup label="'.luna_htmlspecialchars($forum['cat_name']).'">'."\n";
			$cur_category = $forum['cid'];
		}

		echo "\t\t\t\t\t\t\t\t\t\t\t\t".'<option value="'.$forum['fid'].'">'.luna_htmlspecialchars($forum['forum_name']).'</option>'."\n";
	}

    echo ' </optgroup>'
?>
                                </select>
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>
        </form>
        <form class="form-horizontal" id="userprune" method="post" action="<?php echo $_SERVER['REQUEST_URI'] ?>">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('Users', 'luna') ?><span class="pull-right"><button class="btn btn-primary" name="userprune" tabindex="2"><span class="fa fa-fw fa-recycle"></span> <?php _e('Prune', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <fieldset>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Minimum days', 'luna') ?></label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" name="days" value="28" tabindex="1" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Since', 'luna') ?></label>
                            <div class="col-sm-9">
                                <label class="radio-inline">
                                    <input type="radio" name="prune_by" value="1" checked />
                                    <?php _e('Registration', 'luna') ?>
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" name="prune_by" value="0" />
                                    <?php _e('Last login', 'luna') ?>
                                </label>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Maximum number of comments', 'luna') ?><span class="help-block"><?php _e('How many comments do you require before an users isn\'t pruned', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" name="comments" value="1"  tabindex="1" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Delete management', 'luna') ?></label>
                            <div class="col-sm-9">
                                <label class="radio-inline">
                                    <input type="radio" name="admods_delete" value="1" />
                                    <?php _e('Yes', 'luna') ?>
                                </label>
                                    <label class="radio-inline"><input type="radio" name="admods_delete" value="0" checked />
                                    <?php _e('No', 'luna') ?>
                                </label>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('User status', 'luna') ?></label>
                            <div class="col-sm-9">
                                <label class="radio-inline">
                                    <input type="radio" name="verified" value="1" />
                                    <?php _e('Delete any', 'luna') ?>
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" name="verified" value="0" checked />
                                    <?php _e('Delete only verified', 'luna') ?>
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" name="verified" value="2" />
                                    <?php _e('Delete only unverified', 'luna') ?>
                                </label>
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
