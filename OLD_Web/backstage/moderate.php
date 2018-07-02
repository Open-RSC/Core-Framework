<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'content');
define('LUNA_PAGE', 'moderate');

require LUNA_ROOT.'include/common.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}

// This particular function doesn't require forum-based moderator access. It can be used
// by all moderators and admins
if (isset($_GET['get_host'])) {
	if (!$luna_user['is_admmod'])
		message_backstage(__('You do not have permission to access this page.', 'luna'), false, '403 Forbidden');

	// Is get_host an IP address or a comment ID?
	if (@preg_match('%^[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}$%', $_GET['get_host']) || @preg_match('%^((([0-9A-Fa-f]{1,4}:){7}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){6}:[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){5}:([0-9A-Fa-f]{1,4}:)?[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){4}:([0-9A-Fa-f]{1,4}:){0,2}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){3}:([0-9A-Fa-f]{1,4}:){0,3}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){2}:([0-9A-Fa-f]{1,4}:){0,4}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){6}((\b((25[0-5])|(1\d{2})|(2[0-4]\d)|(\d{1,2}))\b)\.){3}(\b((25[0-5])|(1\d{2})|(2[0-4]\d)|(\d{1,2}))\b))|(([0-9A-Fa-f]{1,4}:){0,5}:((\b((25[0-5])|(1\d{2})|(2[0-4]\d)|(\d{1,2}))\b)\.){3}(\b((25[0-5])|(1\d{2})|(2[0-4]\d)|(\d{1,2}))\b))|(::([0-9A-Fa-f]{1,4}:){0,5}((\b((25[0-5])|(1\d{2})|(2[0-4]\d)|(\d{1,2}))\b)\.){3}(\b((25[0-5])|(1\d{2})|(2[0-4]\d)|(\d{1,2}))\b))|([0-9A-Fa-f]{1,4}::([0-9A-Fa-f]{1,4}:){0,5}[0-9A-Fa-f]{1,4})|(::([0-9A-Fa-f]{1,4}:){0,6}[0-9A-Fa-f]{1,4})|(([0-9A-Fa-f]{1,4}:){1,7}:))$%', $_GET['get_host']))
		$ip = $_GET['get_host'];
	else {
		$get_host = intval($_GET['get_host']);
		if ($get_host < 1)
			message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

		$result = $db->query('SELECT commenter_ip FROM '.$db->prefix.'comments WHERE id='.$get_host) or error('Unable to fetch comment IP address', __FILE__, __LINE__, $db->error());
		if (!$db->num_rows($result))
			message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

		$ip = $db->result($result);
	}

	message_backstage(sprintf(__('The IP address is: %s', 'luna'), $ip).'<br />'.sprintf(__('The host name is: %s', 'luna'), @gethostbyaddr($ip)).'<br /><br /><a class="btn btn-primary" href="users.php?show_users='.$ip.'">'.__('Show more users for this IP', 'luna').'</a>');
}


// All other functions require moderator/admin access
$fid = isset($_GET['fid']) ? intval($_GET['fid']) : 0;
if ($fid < 1) {
    require 'header.php';

	?>
<div class="row">
    <div class="col-xs-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title"><?php _e('Moderate content', 'luna') ?></h3>
            </div>
            <div class="panel-body">
                <p><?php _e('Visit a forum or thread and choose "Moderate" in the moderator bar to moderate content.', 'luna') ?></p>
            </div>
        </div>
    </div>
</div>
	<?php

	require 'footer.php';
	exit;
}

$result = $db->query('SELECT moderators FROM '.$db->prefix.'forums WHERE id='.$fid) or error('Unable to fetch forum info', __FILE__, __LINE__, $db->error());

$moderators = $db->result($result);
$mods_array = ($moderators != '') ? unserialize($moderators) : array();

if ($luna_user['g_id'] != LUNA_ADMIN && ($luna_user['g_moderator'] == '0' || !array_key_exists($luna_user['username'], $mods_array)))
	message_backstage(__('You do not have permission to access this page.', 'luna'), false, '403 Forbidden');

// Get thread/forum tracking data
if (!$luna_user['is_guest'])
	$tracked_threads = get_tracked_threads();

// All other thread moderation features require a thread ID in GET
if (isset($_GET['tid'])) {
	$tid = intval($_GET['tid']);

	if ($tid < 1)
		message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

	// Fetch some info about the thread
	$thread_info = $db->query('SELECT t.subject, t.num_replies, t.first_comment_id, f.id AS forum_id, forum_name FROM '.$db->prefix.'threads AS t INNER JOIN '.$db->prefix.'forums AS f ON f.id=t.forum_id LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=f.id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.read_forum IS NULL OR fp.read_forum=1) AND f.id='.$fid.' AND t.id='.$tid.' AND t.moved_to IS NULL') or error('Unable to fetch thread info', __FILE__, __LINE__, $db->error());

	if (!$db->num_rows($thread_info))
		message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

	$cur_thread = $db->fetch_assoc($thread_info);

	// Delete one or more comments
	if (isset($_POST['delete_comments']) || isset($_POST['delete_comments_comply'])) {
		$comments = isset($_POST['comments']) ? $_POST['comments'] : array();

		if (empty($comments))
			message_backstage(__('You must select at least one comment for split/delete.', 'luna'));

		if (isset($_POST['delete_comments_comply'])) {
			confirm_referrer('backstage/moderate.php');

			if (@preg_match('%[^0-9,]%', $comments))
				message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

			// Verify that the comment IDs are valid
			$admins_sql = ($luna_user['g_id'] != LUNA_ADMIN) ? ' AND commenter_id NOT IN('.implode(',', get_admin_ids()).')' : '';
			$comment_ids = $db->query('SELECT 1 FROM '.$db->prefix.'comments WHERE id IN('.$comments.') AND thread_id='.$tid.$admins_sql) or error('Unable to check comments', __FILE__, __LINE__, $db->error());

			if ($db->num_rows($comment_ids) != substr_count($comments, ',') + 1)
				message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

			decrease_comment_counts($comments);

			// Delete the comments
			$db->query('DELETE FROM '.$db->prefix.'comments WHERE id IN('.$comments.')') or error('Unable to delete comments', __FILE__, __LINE__, $db->error());

			require LUNA_ROOT.'include/search_idx.php';
			strip_search_index($comments);

			// Get last_comment, last_comment_id, and last_commenter for the thread after deletion
			$last_info = $db->query('SELECT id, commenter, commented FROM '.$db->prefix.'comments WHERE thread_id='.$tid.' ORDER BY id DESC LIMIT 1') or error('Unable to fetch comment info', __FILE__, __LINE__, $db->error());
			$last_comment = $db->fetch_assoc($last_info);

			// How many comments did we just delete?
			$num_comments_deleted = substr_count($comments, ',') + 1;

			// Update the thread
			$db->query('UPDATE '.$db->prefix.'threads SET last_comment='.$last_comment['commented'].', last_comment_id='.$last_comment['id'].', last_commenter=\''.$db->escape($last_comment['commenter']).'\', num_replies=num_replies-'.$num_comments_deleted.' WHERE id='.$tid) or error('Unable to update thread', __FILE__, __LINE__, $db->error());

			update_forum($fid);

			redirect('thread.php?id='.$tid);
		}

        require 'header.php';

		?>
<div class="row">
    <div class="col-xs-12">
		<form method="post" action="moderate.php?fid=<?php echo $fid ?>&amp;tid=<?php echo $tid ?>">
			<div class="panel panel-danger">
				<div class="panel-heading">
					<h3 class="panel-title"><?php _e('Delete comments', 'luna') ?><span class="pull-right"><input class="btn btn-danger" type="submit" name="delete_comments_comply" value="<?php _e('Delete', 'luna') ?>" /></span></h3>
				</div>
				<div class="panel-body">
					<fieldset>
						<input type="hidden" name="comments" value="<?php echo implode(',', array_map('intval', array_keys($comments))) ?>" />
						<p><?php _e('Are you sure you want to delete the selected comments?', 'luna') ?></p>
					</fieldset>
				</div>
			</div>
		</form>
    </div>
</div>
		<?php

		require 'footer.php';
        exit;
	} elseif (isset($_POST['split_comments']) || isset($_POST['split_comments_comply'])) {
		$comments = isset($_POST['comments']) ? $_POST['comments'] : array();
        
		if (empty($comments))
			message_backstage(__('You must select at least one comment for split/delete.', 'luna'));

		if (isset($_POST['split_comments_comply'])) {
			confirm_referrer('backstage/moderate.php');

			if (@preg_match('%[^0-9,]%', $comments))
				message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

			$move_to_forum = isset($_POST['move_to_forum']) ? intval($_POST['move_to_forum']) : 0;
			if ($move_to_forum < 1)
				message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

			// How many comments did we just split off?
			$num_comments_splitted = substr_count($comments, ',') + 1;

			// Verify that the comment IDs are valid
			$result = $db->query('SELECT 1 FROM '.$db->prefix.'comments WHERE id IN('.$comments.') AND thread_id='.$tid) or error('Unable to check comments', __FILE__, __LINE__, $db->error());
			if ($db->num_rows($result) != $num_comments_splitted)
				message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

			// Verify that the move to forum ID is valid
			$result = $db->query('SELECT 1 FROM '.$db->prefix.'forums AS f LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.group_id='.$luna_user['g_id'].' AND fp.forum_id='.$move_to_forum.') WHERE (fp.create_threads IS NULL OR fp.create_threads=1)') or error('Unable to fetch forum permissions', __FILE__, __LINE__, $db->error());
			if (!$db->num_rows($result))
				message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

			// Check subject
			$new_subject = isset($_POST['new_subject']) ? luna_trim($_POST['new_subject']) : '';

			if ($new_subject == '')
				message_backstage(__('Threads must contain a subject.', 'luna'));
			 elseif (luna_strlen($new_subject) > 70)
				message_backstage(__('Subjects cannot be longer than 70 characters.', 'luna'));

			// Get data from the new first commint
			$result = $db->query('SELECT p.id, p.commenter, p.commented FROM '.$db->prefix.'comments AS p WHERE id IN('.$comments.') ORDER BY p.id ASC LIMIT 1') or error('Unable to get first comment', __FILE__, __LINE__, $db->error());
			$first_comment_data = $db->fetch_assoc($result);

			// Create the new thread
			$db->query('INSERT INTO '.$db->prefix.'threads (commenter, subject, commented, first_comment_id, forum_id) VALUES (\''.$db->escape($first_comment_data['commenter']).'\', \''.$db->escape($new_subject).'\', '.$first_comment_data['commented'].', '.$first_comment_data['id'].', '.$move_to_forum.')') or error('Unable to create new thread', __FILE__, __LINE__, $db->error());
			$new_tid = $db->insert_id();

			// Move the comments to the new thread
			$db->query('UPDATE '.$db->prefix.'comments SET thread_id='.$new_tid.' WHERE id IN('.$comments.')') or error('Unable to move comments into new thread', __FILE__, __LINE__, $db->error());

			// Apply every subscription to both threads
			$db->query('INSERT INTO '.$db->prefix.'thread_subscriptions (user_id, thread_id) SELECT user_id, '.$new_tid.' FROM '.$db->prefix.'thread_subscriptions WHERE thread_id='.$tid) or error('Unable to copy existing subscriptions', __FILE__, __LINE__, $db->error());

			// Get last_comment, last_comment_id, and last_commenter from the thread and update it
			$result = $db->query('SELECT id, commenter, commented FROM '.$db->prefix.'comments WHERE thread_id='.$tid.' ORDER BY id DESC LIMIT 1') or error('Unable to fetch comment info', __FILE__, __LINE__, $db->error());
			$last_comment_data = $db->fetch_assoc($result);
			$db->query('UPDATE '.$db->prefix.'threads SET last_comment='.$last_comment_data['commented'].', last_comment_id='.$last_comment_data['id'].', last_commenter=\''.$db->escape($last_comment_data['commenter']).'\', num_replies=num_replies-'.$num_comments_splitted.' WHERE id='.$tid) or error('Unable to update thread', __FILE__, __LINE__, $db->error());

			// Get last_comment, last_comment_id, and last_commenter from the new thread and update it
			$result = $db->query('SELECT id, commenter, commented FROM '.$db->prefix.'comments WHERE thread_id='.$new_tid.' ORDER BY id DESC LIMIT 1') or error('Unable to fetch comment info', __FILE__, __LINE__, $db->error());
			$last_comment_data = $db->fetch_assoc($result);
			$db->query('UPDATE '.$db->prefix.'threads SET last_comment='.$last_comment_data['commented'].', last_comment_id='.$last_comment_data['id'].', last_commenter=\''.$db->escape($last_comment_data['commenter']).'\', num_replies='.($num_comments_splitted-1).' WHERE id='.$new_tid) or error('Unable to update thread', __FILE__, __LINE__, $db->error());

			update_forum($fid);
			update_forum($move_to_forum);

			redirect('thread.php?id='.$new_tid);
		}

		$result = $db->query('SELECT c.id AS cid, c.cat_name, f.id AS fid, f.forum_name FROM '.$db->prefix.'categories AS c INNER JOIN '.$db->prefix.'forums AS f ON c.id=f.cat_id LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=f.id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.create_threads IS NULL OR fp.create_threads=1) ORDER BY c.disp_position, c.id, f.disp_position') or error('Unable to fetch category/forum list', __FILE__, __LINE__, $db->error());

        require 'header.php';
		?>
<div class="row">
    <div class="col-xs-12">
		<form id="subject" class="form-horizontal" method="post" action="moderate.php?fid=<?php echo $fid ?>&amp;tid=<?php echo $tid ?>">
			<div class="panel panel-default">
				<div class="panel-heading">
                    <h3 class="panel-title"><?php _e('Split comments', 'luna') ?><span class="pull-right"><button input type="submit" class="btn btn-primary" name="split_comments_comply"><i class="fa fa-fw fa-code-fork"></i> <?php _e('Split', 'luna') ?></button></span></h3>
				</div>
				<div class="panel-body">
					<fieldset>
						<input type="hidden" class="form-control" name="comments" value="<?php echo implode(',', array_map('intval', array_keys($comments))) ?>" />
						<div class="form-group">
							<label class="col-sm-2 control-label"><?php _e('Move to', 'luna') ?></label>
							<div class="col-sm-10">
								<select class="form-control" name="move_to_forum">
		<?php

			$cur_category = 0;
			while ($cur_forum = $db->fetch_assoc($result)) {
				if ($cur_forum['cid'] != $cur_category) { // A new category since last iteration?
					if ($cur_category)
						echo "\t\t\t\t\t\t\t".'</optgroup>'."\n";

					echo "\t\t\t\t\t\t\t".'<optgroup label="'.luna_htmlspecialchars($cur_forum['cat_name']).'">'."\n";
					$cur_category = $cur_forum['cid'];
				}

				echo "\t\t\t\t\t\t\t\t".'<option value="'.$cur_forum['fid'].'"'.($fid == $cur_forum['fid'] ? ' selected' : '').'>'.luna_htmlspecialchars($cur_forum['forum_name']).'</option>'."\n";
			}
            echo '</optgroup>';

		?>
								</select>
							</div>
						</div>
						<div class="form-group">
							<label class="col-sm-2 control-label"><?php _e('New subject', 'luna') ?></label>
							<div class="col-sm-10">
								<input class="form-control" type="text" name="new_subject" maxlength="70" />
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
		exit;
	}

	// Show the moderate comments view

	// Used to disable the Move and Delete buttons if there are no replies to this thread
	$button_status = ($cur_thread['num_replies'] == 0) ? ' disabled="disabled"' : '';

	if (isset($_GET['action']) && $_GET['action'] == 'all')
		$luna_user['disp_comments'] = $cur_thread['num_replies'] + 1;

	// Determine the comment offset (based on $_GET['p'])
	$num_pages = ceil(($cur_thread['num_replies'] + 1) / $luna_user['disp_comments']);

	$p = (!isset($_GET['p']) || $_GET['p'] <= 1 || $_GET['p'] > $num_pages) ? 1 : intval($_GET['p']);
	$start_from = $luna_user['disp_comments'] * ($p - 1);

	// Generate paging links
	$paging_links = paginate($num_pages, $p, 'moderate.php?fid='.$fid.'&amp;tid='.$tid);

	if ($luna_config['o_censoring'] == '1')
		$cur_thread['subject'] = censor_words($cur_thread['subject']);

    require 'header.php';

	?>
<div class="row">
    <div class="col-xs-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title"><?php _e('Moderate content', 'luna') ?></h3>
            </div>
            <div class="panel-body">
                <div class="btn-group btn-breadcrumb btn-group-top">
                        <a class="btn btn-primary" href="../index.php"><span class="fa fa-fw fa-home"></span></a>
                        <a class="btn btn-primary" href="../viewforum.php?id=<?php echo $fid ?>"><?php echo luna_htmlspecialchars($cur_thread['forum_name']) ?></a>
                        <a class="btn btn-primary" href="../thread.php?id=<?php echo $tid ?>"><?php echo luna_htmlspecialchars($cur_thread['subject']) ?></a>
                    <a class="btn btn-primary" href="#"><?php _e('Moderate', 'luna') ?></a>
                </div>
                <span class="pull-right"><?php echo $paging_links ?></span>

                <form method="post" action="moderate.php?fid=<?php echo $fid ?>&amp;tid=<?php echo $tid ?>">
<?php

	require LUNA_ROOT.'include/parser.php';

	$comment_count = 0; // Keep track of comment numbers

	// Retrieve a list of comment IDs, LIMIT is (really) expensive so we only fetch the IDs here then later fetch the remaining data
	$result = $db->query('SELECT id FROM '.$db->prefix.'comments WHERE thread_id='.$tid.' ORDER BY id LIMIT '.$start_from.','.$luna_user['disp_comments']) or error('Unable to fetch comment IDs', __FILE__, __LINE__, $db->error());

	$comment_ids = array();
	for ($i = 0;$cur_comment_id = $db->result($result, $i);$i++)
		$comment_ids[] = $cur_comment_id;

	// Retrieve the comments (and their respective commenter)
	$result = $db->query('SELECT u.title, u.num_comments, g.g_id, g.g_user_title, p.id, p.commenter, p.commenter_id, p.message, p.hide_smilies, p.commented, p.edited, p.edited_by, o.user_id AS is_online FROM '.$db->prefix.'comments AS p INNER JOIN '.$db->prefix.'users AS u ON u.id=p.commenter_id INNER JOIN '.$db->prefix.'groups AS g ON g.g_id=u.group_id LEFT JOIN '.$db->prefix.'online AS o ON (o.user_id=u.id AND o.user_id!=1 AND o.idle=0) WHERE p.id IN ('.implode(',', $comment_ids).') ORDER BY p.id', true) or error('Unable to fetch comment info', __FILE__, __LINE__, $db->error());

	while ($cur_comment = $db->fetch_assoc($result)) {
		$comment_count++;

		// If the commenter is a registered user
		if ($cur_comment['commenter_id'] > 1) {
			if ($luna_user['g_view_users'] == '1')
				$commenter = '<a href="../profile.php?id='.$cur_comment['commenter_id'].'">'.luna_htmlspecialchars($cur_comment['commenter']).'</a>';
			else
				$commenter = luna_htmlspecialchars($cur_comment['commenter']);

			// get_title() requires that an element 'username' be present in the array
			$cur_comment['username'] = $cur_comment['commenter'];
			$user_title = get_title($cur_comment);

			if ($luna_config['o_censoring'] == '1')
				$user_title = censor_words($user_title);
		}
		// If the commenter is a guest (or a user that has been deleted)
		else {
			$commenter = luna_htmlspecialchars($cur_comment['commenter']);
			$user_title = __('Guest', 'luna');
		}

		// Format the online indicator, those are ment as CSS classes
		$is_online = ($cur_comment['is_online'] == $cur_comment['commenter_id']) ? 'is-online' : 'is-offline';

		// Perform the main parsing of the message (BBCode, smilies, censor words etc)
		$cur_comment['message'] = parse_message($cur_comment['message']);

?>
                    <div id="p<?php echo $cur_comment['id'] ?>" class="comment<?php if($cur_comment['id'] == $cur_thread['first_comment_id']) echo ' firstcomment' ?><?php echo ($comment_count % 2 == 0) ? ' roweven' : ' rowodd' ?>">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <h3 class="panel-title"><?php echo $commenter ?> <span class="small"><?php echo $user_title ?></span><span class="pull-right">#<?php echo ($start_from + $comment_count) ?> &middot; <a href="../thread.php?pid=<?php echo $cur_comment['id'].'#p'.$cur_comment['id'] ?>"><?php echo format_time($cur_comment['commented']) ?></a></span></h3>
                            </div>
                            <div class="panel-body">
                                <?php echo $cur_comment['message']."\n" ?>
                                <?php if ($cur_comment['edited'] != '') echo "\t\t\t\t\t\t".'<p class="comment-edited"><em>'.__('Last edited by', 'luna').' '.luna_htmlspecialchars($cur_comment['edited_by']).' ('.format_time($cur_comment['edited']).')</em></p>'."\n"; ?>
                            </div>
                            <div class="panel-footer">
                                <?php echo ($cur_comment['id'] != $cur_thread['first_comment_id']) ? '<div class="checkbox" style="margin-top: 0;"><label><input type="checkbox" name="comments['.$cur_comment['id'].']" value="1" /> '.__('Select', 'luna').'</label></div>' : '<p>'.__('First comment cannot be selected for split/delete.', 'luna').'</p>' ?>
                            </div>
                        </div>
                    </div>

<?php

	}

?>
                    <div class="btn-group btn-breadcrumb">
                        <a class="btn btn-primary" href="../index.php"><span class="fa fa-fw fa-home"></span></a>
                        <a class="btn btn-primary" href="../viewforum.php?id=<?php echo $fid ?>"><?php echo luna_htmlspecialchars($cur_thread['forum_name']) ?></a>
                        <a class="btn btn-primary" href="../thread.php?id=<?php echo $tid ?>"><?php echo luna_htmlspecialchars($cur_thread['subject']) ?></a>
                        <a class="btn btn-primary" href="#"><?php _e('Moderate', 'luna') ?></a>
                    </div>
                    <span class="pull-right"><?php echo $paging_links ?></span>
                    <div class="btn-group pull-right">
                        <button type="submit" class="btn btn-primary" name="split_comments" <?php echo $button_status ?>><i class="fa fa-fw fa-code-fork"></i> <?php _e('Split', 'luna') ?></button>
                        <button type="submit" class="btn btn-primary" name="delete_comments"<?php echo $button_status ?>><i class="fa fa-fw fa-trash"></i> <?php _e('Delete', 'luna') ?></button>
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


// Move one or more threads
if (isset($_REQUEST['move_threads']) || isset($_POST['move_threads_to'])) {
	if (isset($_POST['move_threads_to'])) {
		confirm_referrer('backstage/moderate.php');

		if (@preg_match('%[^0-9,]%', $_POST['threads']))
			message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

		$threads = explode(',', $_POST['threads']);
		$move_to_forum = isset($_POST['move_to_forum']) ? intval($_POST['move_to_forum']) : 0;
		if (empty($threads) || $move_to_forum < 1)
			message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

		// Verify that the thread IDs are valid
		$result = $db->query('SELECT 1 FROM '.$db->prefix.'threads WHERE id IN('.implode(',',$threads).') AND forum_id='.$fid) or error('Unable to check threads', __FILE__, __LINE__, $db->error());

		if ($db->num_rows($result) != count($threads))
			message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

		// Verify that the move to forum ID is valid
		$result = $db->query('SELECT 1 FROM '.$db->prefix.'forums AS f LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.group_id='.$luna_user['g_id'].' AND fp.forum_id='.$move_to_forum.') WHERE (fp.create_threads IS NULL OR fp.create_threads=1)') or error('Unable to fetch forum permissions', __FILE__, __LINE__, $db->error());
		if (!$db->num_rows($result))
			message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

		// Delete any redirect threads if there are any (only if we moved/copied the thread back to where it was once moved from)
		$db->query('DELETE FROM '.$db->prefix.'threads WHERE forum_id='.$move_to_forum.' AND moved_to IN('.implode(',',$threads).')') or error('Unable to delete redirect threads', __FILE__, __LINE__, $db->error());

		// Move the thread(s)
		$db->query('UPDATE '.$db->prefix.'threads SET forum_id='.$move_to_forum.' WHERE id IN('.implode(',',$threads).')') or error('Unable to move threads', __FILE__, __LINE__, $db->error());

		// Should we create redirect threads?
		if (isset($_POST['with_redirect'])) {
			foreach ($threads as $cur_thread) {
				// Fetch info for the redirect thread
				$result = $db->query('SELECT commenter, subject, commented, last_comment FROM '.$db->prefix.'threads WHERE id='.$cur_thread) or error('Unable to fetch thread info', __FILE__, __LINE__, $db->error());
				$moved_to = $db->fetch_assoc($result);

				// Create the redirect thread
				$db->query('INSERT INTO '.$db->prefix.'threads (commenter, subject, commented, last_comment, moved_to, forum_id) VALUES(\''.$db->escape($moved_to['commenter']).'\', \''.$db->escape($moved_to['subject']).'\', '.$moved_to['commented'].', '.$moved_to['last_comment'].', '.$cur_thread.', '.$fid.')') or error('Unable to create redirect thread', __FILE__, __LINE__, $db->error());
			}
		}

		update_forum($fid); // Update the forum FROM which the thread was moved
		update_forum($move_to_forum); // Update the forum TO which the thread was moved

		redirect('viewforum.php?id='.$move_to_forum);
	}

	if (isset($_POST['move_threads'])) {
		$threads = isset($_POST['threads']) ? $_POST['threads'] : array();
		if (empty($threads))
			message_backstage(__('You must select at least one thread for move/delete/open/close.', 'luna'));

		$threads = implode(',', array_map('intval', array_keys($threads)));
		$action = 'multi';
	} else {
		$threads = intval($_GET['move_threads']);
		if ($threads < 1)
			message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

		$action = 'single';
	}

	$result = $db->query('SELECT c.id AS cid, c.cat_name, f.id AS fid, f.forum_name FROM '.$db->prefix.'categories AS c INNER JOIN '.$db->prefix.'forums AS f ON c.id=f.cat_id LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=f.id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.create_threads IS NULL OR fp.create_threads=1) ORDER BY c.disp_position, c.id, f.disp_position') or error('Unable to fetch category/forum list', __FILE__, __LINE__, $db->error());
	if ($db->num_rows($result) < 2)
		message_backstage(__('There are no forums into which you can move threads.', 'luna'));

    require 'header.php';
	?>
<div class="col-xs-12">
    <div class="row">
	   <form class="form-horizontal panel panel-default" method="post" action="moderate.php?fid=<?php echo $fid ?>">
			<div class="panel-heading">
				<h3 class="panel-title"><?php echo ($action == 'single') ? __('Move thread', 'luna') : __('Move threads', 'luna') ?><span class="pull-right"><button type="submit" class="btn btn-primary" name="move_threads_to"><i class="fa fa-fw fa-arrows-alt"></i> <?php _e('Move', 'luna') ?></button></span></h3>
			</div>
			<div class="panel-body">
				<input type="hidden" name="threads" value="<?php echo $threads ?>" />
				<fieldset>
					<div class="form-group">
						<label class="col-sm-3 control-label"><?php _e('Move to', 'luna') ?></label>
						<div class="col-sm-9">
							<select class="form-control" name="move_to_forum">
	<?php

		$cur_category = 0;
		while ($cur_forum = $db->fetch_assoc($result)) {
			if ($cur_forum['cid'] != $cur_category) { // A new category since last iteration?
				if ($cur_category)
					echo "\t\t\t\t\t\t\t".'</optgroup>'."\n";

				echo "\t\t\t\t\t\t\t".'<optgroup label="'.luna_htmlspecialchars($cur_forum['cat_name']).'">'."\n";
				$cur_category = $cur_forum['cid'];
			}

			if ($cur_forum['fid'] != $fid)
				echo "\t\t\t\t\t\t\t\t".'<option value="'.$cur_forum['fid'].'">'.luna_htmlspecialchars($cur_forum['forum_name']).'</option>'."\n";
		}
        echo '</optgroup>';
	?>
							</select>
							<div class="checkbox">
								<label>
									<input type="checkbox" name="with_redirect" value="1" />
									<?php _e('Leave redirect thread(s).', 'luna') ?>
								</label>
							</div>
						</div>
					</div>
				</fieldset>
			</div>
	   </form>
    </div>
</div>
	<?php

	require 'footer.php';
}

// Merge two or more threads
elseif (isset($_POST['merge_threads']) || isset($_POST['merge_threads_comply'])) {
	if (isset($_POST['merge_threads_comply'])) {
		confirm_referrer('backstage/moderate.php');

		if (@preg_match('%[^0-9,]%', $_POST['threads']))
			message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

		$threads = explode(',', $_POST['threads']);
		if (count($threads) < 2)
			message_backstage(__('You must select at least two threads to merge.', 'luna'));

		// Verify that the thread IDs are valid (redirect links will point to the merged thread after the merge)
		$result = $db->query('SELECT id FROM '.$db->prefix.'threads WHERE id IN('.implode(',', $threads).') AND forum_id='.$fid.' ORDER BY id ASC') or error('Unable to check threads', __FILE__, __LINE__, $db->error());
		if ($db->num_rows($result) != count($threads))
			message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

		// The thread that we are merging into is the one with the smallest ID
		$merge_to_tid = $db->result($result);

		// Make any redirect threads point to our new, merged thread
		$query = 'UPDATE '.$db->prefix.'threads SET moved_to='.$merge_to_tid.' WHERE moved_to IN('.implode(',', $threads).')';

		// Should we create redirect threads?
		if (isset($_POST['with_redirect']))
			$query .= ' OR (id IN('.implode(',', $threads).') AND id != '.$merge_to_tid.')';

		$db->query($query) or error('Unable to make redirection threads', __FILE__, __LINE__, $db->error());

		// Merge the comments into the thread
		$db->query('UPDATE '.$db->prefix.'comments SET thread_id='.$merge_to_tid.' WHERE thread_id IN('.implode(',', $threads).')') or error('Unable to merge the comments into the thread', __FILE__, __LINE__, $db->error());

		// Update any subscriptions
		$result = $db->query('SELECT DISTINCT user_id FROM '.$db->prefix.'thread_subscriptions WHERE thread_id IN ('.implode(',', $threads).')') or error('Unable to fetch subscriptions of merged threads', __FILE__, __LINE__, $db->error());

		$subscribed_users = array();
		while ($row = $db->fetch_row($result))
			$subscribed_users[] = $row[0];

		$db->query('DELETE FROM '.$db->prefix.'thread_subscriptions WHERE thread_id IN ('.implode(',', $threads).')') or error('Unable to delete subscriptions of merged threads', __FILE__, __LINE__, $db->error());

		foreach ($subscribed_users as $cur_user_id)
			$db->query('INSERT INTO '.$db->prefix.'thread_subscriptions (thread_id, user_id) VALUES ('.$merge_to_tid.', '.$cur_user_id.')') or error('Unable to re-enter subscriptions for merge thread', __FILE__, __LINE__, $db->error());

		// Without redirection the old threads are removed
		if (!isset($_POST['with_redirect']))
			$db->query('DELETE FROM '.$db->prefix.'threads WHERE id IN('.implode(',', $threads).') AND id != '.$merge_to_tid) or error('Unable to delete old threads', __FILE__, __LINE__, $db->error());

		// Count number of replies in the thread
		$result = $db->query('SELECT COUNT(id) FROM '.$db->prefix.'comments WHERE thread_id='.$merge_to_tid) or error('Unable to fetch comment count for thread', __FILE__, __LINE__, $db->error());
		$num_replies = $db->result($result, 0) - 1;

		// Get last_comment, last_comment_id and last_commenter
		$result = $db->query('SELECT commented, id, commenter FROM '.$db->prefix.'comments WHERE thread_id='.$merge_to_tid.' ORDER BY id DESC LIMIT 1') or error('Unable to get last comment info', __FILE__, __LINE__, $db->error());
		list($last_comment, $last_comment_id, $last_commenter) = $db->fetch_row($result);

		// Update thread
		$db->query('UPDATE '.$db->prefix.'threads SET num_replies='.$num_replies.', last_comment='.$last_comment.', last_comment_id='.$last_comment_id.', last_commenter=\''.$db->escape($last_commenter).'\' WHERE id='.$merge_to_tid) or error('Unable to update thread', __FILE__, __LINE__, $db->error());

		// Update the forum FROM which the thread was moved and redirect
		update_forum($fid);
		redirect('viewforum.php?id='.$fid);
	}

	$threads = isset($_POST['threads']) ? $_POST['threads'] : array();
	if (count($threads) < 2)
		message_backstage(__('You must select at least two threads to merge.', 'luna'));
	else {
        require 'header.php';
		?>
<div class="row">
    <div class="col-xs-12">
		<form class="panel panel-default" method="post" action="moderate.php?fid=<?php echo $fid ?>">
            <div class="panel-heading">
                <h3 class="panel-title"><?php _e('Merge threads', 'luna') ?><span class="pull-right"><button type="submit" class="btn btn-primary" name="merge_threads_comply"><i class="fa fa-fw fa-compress"></i> <?php _e('Merge', 'luna') ?></button></span></h3>
            </div>
            <div class="panel-body">
                <input type="hidden" name="threads" value="<?php echo implode(',', array_map('intval', array_keys($threads))) ?>" />
                <div class="checkbox">
                    <label>
                        <input type="checkbox" name="with_redirect" value="1" />
                        <?php _e('Leave redirect thread(s)', 'luna') ?>
                    </label>
                </div>
            </div>
		</form>
    </div>
</div>

		<?php

		require 'footer.php';
	}
}

// Delete one or more threads
elseif (isset($_POST['delete_threads']) || isset($_POST['delete_threads_comply'])) {
	$threads = isset($_POST['threads']) ? $_POST['threads'] : array();
	if (empty($threads))
		message_backstage(__('You must select at least one thread for move/delete/open/close.', 'luna'));

	if (isset($_POST['delete_threads_comply'])) {
		confirm_referrer('backstage/moderate.php');

		if (@preg_match('%[^0-9,]%', $threads))
			message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

		require LUNA_ROOT.'include/search_idx.php';

		// Verify that the thread IDs are valid
		$result = $db->query('SELECT 1 FROM '.$db->prefix.'threads WHERE id IN('.$threads.') AND forum_id='.$fid) or error('Unable to check threads', __FILE__, __LINE__, $db->error());

		if ($db->num_rows($result) != substr_count($threads, ',') + 1)
			message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

		// Verify that the comments are not by admins
		if ($luna_user['g_id'] != LUNA_ADMIN) {
			$result = $db->query('SELECT 1 FROM '.$db->prefix.'comments WHERE thread_id IN('.$threads.') AND commenter_id IN('.implode(',', get_admin_ids()).')') or error('Unable to check comments', __FILE__, __LINE__, $db->error());
			if ($db->num_rows($result))
				message_backstage(__('You do not have permission to access this page.', 'luna'), false, '403 Forbidden');
		}

		// Delete the threads and any redirect threads
		$db->query('DELETE FROM '.$db->prefix.'threads WHERE id IN('.$threads.') OR moved_to IN('.$threads.')') or error('Unable to delete thread', __FILE__, __LINE__, $db->error());

		// Delete any subscriptions
		$db->query('DELETE FROM '.$db->prefix.'thread_subscriptions WHERE thread_id IN('.$threads.')') or error('Unable to delete subscriptions', __FILE__, __LINE__, $db->error());

		// Create a list of the comment IDs in this thread and then strip the search index
		$result = $db->query('SELECT id FROM '.$db->prefix.'comments WHERE thread_id IN('.$threads.')') or error('Unable to fetch comments', __FILE__, __LINE__, $db->error());

		$comment_ids = '';
		while ($row = $db->fetch_row($result))
			$comment_ids .= ($comment_ids != '') ? ','.$row[0] : $row[0];

		// We have to check that we actually have a list of comment IDs since we could be deleting just a redirect thread
		if ($comment_ids != '') {
			decrease_comment_counts($comment_ids);
			strip_search_index($comment_ids);
		}

		// Delete comments
		$db->query('DELETE FROM '.$db->prefix.'comments WHERE thread_id IN('.$threads.')') or error('Unable to delete comments', __FILE__, __LINE__, $db->error());

		update_forum($fid);

		redirect('viewforum.php?id='.$fid);
	}

    require 'header.php';
	?>

<div class="row">
    <div class="col-xs-12">
        <form method="post" class="panel panel-danger" action="moderate.php?fid=<?php echo $fid ?>">
			<div class="panel-heading">
				<h3 class="panel-title"><?php _e('Delete threads', 'luna') ?><span class="pull-right"><button type="submit" class="btn btn-danger" name="delete_threads_comply"><span class="fa fa-fw fa-trash"></span> <?php _e('Delete', 'luna') ?></button></span></h3>
			</div>
			<div class="panel-body">
				<input type="hidden" name="threads" value="<?php echo implode(',', array_map('intval', array_keys($threads))) ?>" />
				<fieldset>
					<p><?php _e('Are you sure you want to delete the selected threads?', 'luna') ?></p>
				</fieldset>
			</div>
        </form>
    </div>
</div>

	<?php
	require 'footer.php';
}


// Open or close one or more threads
elseif (isset($_REQUEST['open']) || isset($_REQUEST['close'])) {
	$action = (isset($_REQUEST['open'])) ? 0 : 1;

	// There could be an array of thread IDs in $_POST
	if (isset($_POST['open']) || isset($_POST['close'])) {
		confirm_referrer('backstage/moderate.php');

		$threads = isset($_POST['threads']) ? @array_map('intval', @array_keys($_POST['threads'])) : array();
		if (empty($threads))
			message_backstage(__('You must select at least one thread for move/delete/open/close.', 'luna'));

		$db->query('UPDATE '.$db->prefix.'threads SET closed='.$action.' WHERE id IN('.implode(',', $threads).') AND forum_id='.$fid) or error('Unable to Close threads', __FILE__, __LINE__, $db->error());

		redirect('backstage/moderate.php?fid='.$fid);
	} else { // Or just one in $_GET
		confirm_referrer(array('thread.php', 'backstage/moderate.php'));

		check_csrf($_GET['csrf_token']);

		$thread_id = ($action) ? intval($_GET['close']) : intval($_GET['open']);
		if ($thread_id < 1)
			message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

		$db->query('UPDATE '.$db->prefix.'threads SET closed='.$action.' WHERE id='.$thread_id.' AND forum_id='.$fid) or error('Unable to Close thread', __FILE__, __LINE__, $db->error());

		redirect('thread.php?id='.$thread_id);
	}
}

// Pin a thread
elseif (isset($_GET['pin'])) {
	confirm_referrer(array('thread.php', 'backstage/moderate.php'));

	check_csrf($_GET['csrf_token']);

	$pin = intval($_GET['pin']);
	if ($pin < 1)
		message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

	$db->query('UPDATE '.$db->prefix.'threads SET pinned=\'1\' WHERE id='.$pin.' AND forum_id='.$fid) or error('Unable to Pin thread', __FILE__, __LINE__, $db->error());

	redirect('thread.php?id='.$pin);
}


// unpin a thread
elseif (isset($_GET['unpin'])) {
	confirm_referrer(array('thread.php', 'backstage/moderate.php'));

	check_csrf($_GET['csrf_token']);

	$unpin = intval($_GET['unpin']);
	if ($unpin < 1)
		message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

	$db->query('UPDATE '.$db->prefix.'threads SET pinned=\'0\' WHERE id='.$unpin.' AND forum_id='.$fid) or error('Unable to Unpin thread', __FILE__, __LINE__, $db->error());

	redirect('thread.php?id='.$unpin);
}

// Mark as important
elseif (isset($_GET['important'])) {
	confirm_referrer(array('thread.php', 'backstage/moderate.php'));

	check_csrf($_GET['csrf_token']);

	$important = intval($_GET['important']);
	if ($important < 1)
		message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

	$db->query('UPDATE '.$db->prefix.'threads SET important=\'1\' WHERE id='.$important.' AND forum_id='.$fid) or error('Unable to mark thread as important', __FILE__, __LINE__, $db->error());

	redirect('thread.php?id='.$important);
}


// Mark as unimportant
elseif (isset($_GET['unimportant'])) {
	confirm_referrer(array('thread.php', 'backstage/moderate.php'));

	check_csrf($_GET['csrf_token']);

	$unimportant = intval($_GET['unimportant']);
	if ($unimportant < 1)
		message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

	$db->query('UPDATE '.$db->prefix.'threads SET important=\'0\' WHERE id='.$unimportant.' AND forum_id='.$fid) or error('Unable to mark thread as unimportant', __FILE__, __LINE__, $db->error());

	redirect('thread.php?id='.$unimportant);
}

// If absolutely none of them are going on
elseif (!isset($_GET['unpin']) && !isset($_GET['pin']) && !isset($_REQUEST['open']) && !isset($_REQUEST['close']) && !isset($_POST['delete_threads']) && !isset($_POST['delete_threads_comply']) && !isset($_GET['tid']) && !isset($_POST['merge_threads']) && !isset($_POST['merge_threads_comply'])) {

	// No specific forum moderation action was specified in the query string, so we'll display the moderator forum

	// Fetch some info about the forum
	$result = $db->query('SELECT f.forum_name, f.num_threads, f.sort_by FROM '.$db->prefix.'forums AS f LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=f.id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.read_forum IS NULL OR fp.read_forum=1) AND f.id='.$fid) or error('Unable to fetch forum info', __FILE__, __LINE__, $db->error());

	if (!$db->num_rows($result))
		message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

	$cur_forum = $db->fetch_assoc($result);

	switch ($cur_forum['sort_by']) {
		case 0:
			$sort_by = 'last_comment DESC';
			break;
		case 1:
			$sort_by = 'commented DESC';
			break;
		case 2:
			$sort_by = 'subject ASC';
			break;
		default:
			$sort_by = 'last_comment DESC';
			break;
	}

	// Determine the thread offset (based on $_GET['p'])
	$num_pages = ceil($cur_forum['num_threads'] / $luna_user['disp_threads']);

	$p = (!isset($_GET['p']) || $_GET['p'] <= 1 || $_GET['p'] > $num_pages) ? 1 : intval($_GET['p']);
	$start_from = $luna_user['disp_threads'] * ($p - 1);

	// Generate paging links
	$paging_links = paginate($num_pages, $p, 'moderate.php?fid='.$fid);

    require 'header.php';

	?>
<div class="row">
    <div class="col-xs-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title"><?php printf(__('Moderating "%s"', 'luna'), luna_htmlspecialchars($cur_forum['forum_name'])) ?></h3>
            </div>
            <form method="post" action="moderate.php?fid=<?php echo $fid ?>">
                <div class="panel-body">
                    <?php echo $paging_links ?>
<?php


// Retrieve a list of thread IDs, LIMIT is (really) expensive so we only fetch the IDs here then later fetch the remaining data
$result = $db->query('SELECT id FROM '.$db->prefix.'threads WHERE forum_id='.$fid.' ORDER BY pinned DESC, '.$sort_by.', id DESC LIMIT '.$start_from.', '.$luna_user['disp_threads']) or error('Unable to fetch thread IDs', __FILE__, __LINE__, $db->error());

// If there are threads in this forum
if ($db->num_rows($result)) {
	$thread_ids = array();
	for ($i = 0;$cur_thread_id = $db->result($result, $i);$i++)
		$thread_ids[] = $cur_thread_id;

	// Select threads
	$result = $db->query('SELECT id, commenter, subject, commented, last_comment, last_comment_id, last_commenter, last_commenter_id, num_views, num_replies, closed, pinned, moved_to, solved, important FROM '.$db->prefix.'threads WHERE id IN('.implode(',', $thread_ids).') ORDER BY pinned DESC, '.$sort_by.', id DESC') or error('Unable to fetch thread list for forum', __FILE__, __LINE__, $db->error());

	$button_status = '';
	$thread_count = 0;
?>
				<div class="list-group list-group-thread">
<?php
	while ($cur_thread = $db->fetch_assoc($result)) {

		++$thread_count;
		$status_text = array();
		$item_status = ($thread_count % 2 == 0) ? 'roweven' : 'rowodd';
		$icon_type = 'icon';
		if (luna_strlen($cur_thread['subject']) > 53)
			$subject = utf8_substr($cur_thread['subject'], 0, 50).'...';
		else
			$subject = luna_htmlspecialchars($cur_thread['subject']);
		$last_comment_date = '<a href="../thread.php?pid='.$cur_thread['last_comment_id'].'#p'.$cur_thread['last_comment_id'].'">'.format_time($cur_thread['last_comment']).'</a>';

		if (is_null($cur_thread['moved_to'])) {
			$thread_id = $cur_thread['id'];

			if ($luna_user['g_view_users'] == '1' && $cur_thread['last_commenter_id'] > '1')
				$last_commenter = '<span class="byuser">'.__('by', 'luna').' <a href="profile.php?id='.$cur_thread['last_commenter_id'].'">'.luna_htmlspecialchars($cur_thread['last_commenter']).'</a></span>';
			else
				$last_commenter = '<span class="byuser">'.__('by', 'luna').' '.luna_htmlspecialchars($cur_thread['last_commenter']).'</span>';
		} else {
			$last_commenter = '';
			$thread_id = $cur_thread['moved_to'];
		}

		if ($luna_config['o_censoring'] == '1')
			$cur_thread['subject'] = censor_words($cur_thread['subject']);

        if ($cur_thread['pinned'] == '1') {
            $item_status .= ' pinned-item';
            $status_text[] = '<i class="fa fa-fw fa-thumb-tack status-pinned"></i>';
        }

        if (isset($cur_thread['solved'])) {
            $item_status .= ' solved-item';
            $status_text[] = '<i class="fa fa-fw fa-check status-solved"></i>';
        }

        if ($cur_thread['important']) {
            $item_status .= ' important-item';
            $status_text[] = '<i class="fa fa-fw fa-map-marker status-important"></i>';
        }

        if ($cur_thread['moved_to'] != 0) {
            $status_text[] = '<i class="fa fa-fw fa-arrows-alt status-moved"></i>';
            $item_status .= ' moved-item';
        }

        if ($cur_thread['closed'] == '1') {
            $status_text[] = '<i class="fa fa-fw fa-lock status-closed"></i>';
            $item_status .= ' closed-item';
        }

		if (!$luna_user['is_guest'] && $cur_thread['last_comment'] > $luna_user['last_visit'] && (!isset($tracked_threads['threads'][$cur_thread['id']]) || $tracked_threads['threads'][$cur_thread['id']] < $cur_thread['last_comment']) && (!isset($tracked_threads['forums'][$id]) || $tracked_threads['forums'][$id] < $cur_thread['last_comment']) && is_null($cur_thread['moved_to'])) {
			$item_status .= ' new-item';
			$icon_type = 'icon icon-new';
            $status_text[] = '<a href="../thread.php?id='.$cur_thread['id'].'&amp;action=new" title="'.__('Go to the first new comment in the thread.', 'luna').'"><i class="fa fa-fw fa-bell status-new"></i></a>';
		}

		$url = '../thread.php?id='.$thread_id;
		$by = '<span class="byuser">'.__('by', 'luna').' '.luna_htmlspecialchars($cur_thread['commenter']).'</span>';

		$subject_status = implode(' ', $status_text);

		$num_pages_thread = ceil(($cur_thread['num_replies'] + 1) / $luna_user['disp_comments']);

		if ($num_pages_thread > 1)
			$subject_multipage = '<span class="inline-pagination"> &middot; '.simple_paginate($num_pages_thread, -1, '../thread.php?id='.$cur_thread['id']).'</span>';
		else
			$subject_multipage = null;

		$replies_label = _n('reply', 'replies', $cur_thread['num_replies'], 'luna');
		$views_label = _n('view', 'views', $cur_thread['num_views'], 'luna');

?>
					<div class="list-group-item <?php echo $item_status ?><?php if ($cur_thread['soft'] == true) echo ' soft'; ?>">
						<input type="checkbox" name="threads[<?php echo $cur_thread['id'] ?>]" value="1" />
						<span class="hidden-xs hidden-sm hidden-md hidden-lg">
							<?php echo forum_number_format($thread_count + $start_from) ?>
						</span>
						<?php echo $subject_status ?> <a href="<?php echo $url ?>"><?php echo $subject ?></a> <?php echo $by ?> <?php echo $subject_multipage ?>
						<?php if ($cur_thread['moved_to'] == 0) { ?>
							<span class="text-muted"> &middot;
								<span class="text-muted"><?php echo $last_comment_date ?></span> &middot;
								<?php if ($cur_thread['moved_to'] == 0) { ?><span class="label label-default"><?php echo forum_number_format($cur_thread['num_replies']) ?></span><?php } ?>
							</span>
						<?php } ?>
					</div>

<?php

	}
	?></div><?php
} else {
	$colspan = ($luna_config['o_thread_views'] == '1') ? 5 : 4;
	$button_status = ' disabled="disabled"';
	echo __('This forum has no threads yet.', 'luna');
}

?>
				</div>
				<div class="panel-footer">
					<div class="btn-group">
						<button type="submit" class="btn btn-primary" name="move_threads"<?php echo $button_status ?>><i class="fa fa-fw fa-arrows-alt"></i> <?php _e('Move', 'luna') ?></button>
						<button type="submit" class="btn btn-primary" name="delete_threads"<?php echo $button_status ?>><i class="fa fa-fw fa-trash-o"></i> <?php _e('Delete', 'luna') ?></button>
						<button type="submit" class="btn btn-primary" name="merge_threads"<?php echo $button_status ?>><i class="fa fa-fw fa-compress"></i> <?php _e('Merge', 'luna') ?></button>
					</div>
					<div class="btn-group">
						<button type="submit" class="btn btn-primary" name="open"<?php echo $button_status ?>><i class="fa fa-fw fa-check"></i> <?php _e('Open', 'luna') ?></button>
						<button type="submit" class="btn btn-primary" name="close"<?php echo $button_status ?>><i class="fa fa-fw fa-times"></i> <?php _e('Close', 'luna') ?></button>
					</div>
				</div>
			</form>
		</div>
    </div>
</div>
	<?php

	require 'footer.php';
}
