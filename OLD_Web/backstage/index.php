<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'backstage');
define('LUNA_PAGE', 'index');

require LUNA_ROOT.'include/common.php';
require LUNA_ROOT.'include/statistic_functions.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}

$action = isset($_GET['action']) ? $_GET['action'] : null;

$install_file_exists = is_file(LUNA_ROOT.'install.php');

if (isset($_POST['form_sent'])) {
	confirm_referrer(array('backstage/index.php', 'backstage/'));

	$db->query('UPDATE '.$db->prefix.'config SET conf_value=\''.$db->escape(luna_htmlspecialchars($_POST['form']['admin_note'])).'\' WHERE conf_name=\'o_admin_note\'') or error('Unable to update board config', __FILE__, __LINE__, $db->error());

	// Regenerate the config cache
	if (!defined('LUNA_CACHE_FUNCTIONS_LOADED'))
		require LUNA_ROOT.'include/cache.php';

	generate_config_cache();
	clear_feed_cache();

	redirect('backstage/index.php?saved=true');
}

$result = $db->query('SELECT SUM(num_threads), SUM(num_comments) FROM '.$db->prefix.'forums') or error('Unable to fetch thread/comment count', __FILE__, __LINE__, $db->error());
list($stats['total_threads'], $stats['total_comments']) = array_map('intval', $db->fetch_row($result));

if ($stats['total_comments'] == 0)
	$stats['total_comments'] == '0';

if ($stats['total_threads'] == 0)
	$stats['total_threads'] == '0';

require 'header.php';
?>
<div class="row">
<?php
if (isset($_GET['saved']))
	echo '<div class="col-sm-12"><div class="alert alert-success"><i class="fa fa-fw fa-check"></i> '.__('Your settings have been saved.', 'luna').'</div></div>';
?>
	<div class="col-sm-8">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title"><?php _e('New reports', 'luna') ?><span class="pull-right"><a class="btn btn-primary" href="reports.php"><span class="fa fa-fw fa-eye"></span> <?php _e('View all', 'luna') ?></a></span></h3>
            </div>
            <table class="table">
                <thead>
                    <tr>
                        <th class="col-lg-3"><?php _e('Reported by', 'luna') ?></th>
                        <th class="col-lg-3"><?php _e('Date and time', 'luna') ?></th>
                        <th class="col-lg-6"><?php _e('Message', 'luna') ?></th>
                    </tr>
                </thead>
                <tbody>
<?php

$result = $db->query('SELECT r.id, r.thread_id, r.forum_id, r.reported_by, r.created, r.message, p.id AS pid, t.subject, f.forum_name, u.username AS reporter FROM '.$db->prefix.'reports AS r LEFT JOIN '.$db->prefix.'comments AS p ON r.comment_id=p.id LEFT JOIN '.$db->prefix.'threads AS t ON r.thread_id=t.id LEFT JOIN '.$db->prefix.'forums AS f ON r.forum_id=f.id LEFT JOIN '.$db->prefix.'users AS u ON r.reported_by=u.id WHERE r.zapped IS NULL ORDER BY created DESC') or error('Unable to fetch report list', __FILE__, __LINE__, $db->error());

if ($db->num_rows($result)) {
	while ($cur_report = $db->fetch_assoc($result)) {
		$reporter = ($cur_report['reporter'] != '') ? '<a href="../profile.php?id='.$cur_report['reported_by'].'">'.luna_htmlspecialchars($cur_report['reporter']).'</a>' : __('Deleted user', 'luna');
		$forum = ($cur_report['forum_name'] != '') ? '<span><a href="../viewforum.php?id='.$cur_report['forum_id'].'">'.luna_htmlspecialchars($cur_report['forum_name']).'</a></span>' : '<span>'.__('Deleted', 'luna').'</span>';
		$thread = ($cur_report['subject'] != '') ? '<span> <span class="divider">/</span> <a href="../thread.php?id='.$cur_report['thread_id'].'">'.luna_htmlspecialchars($cur_report['subject']).'</a></span>' : '<span>»&#160;'.__('Deleted', 'luna').'</span>';
		$comment = str_replace("\n", '<br />', luna_htmlspecialchars($cur_report['message']));
		$comment_id = ($cur_report['pid'] != '') ? '<span><a href="thread.php?pid='.$cur_report['pid'].'#p'.$cur_report['pid'].'">'.sprintf(__('Comment #%s', 'luna'), $cur_report['pid']).'</a></span>' : '<span>'.__('Deleted', 'luna').'</span>';
		$report_location = array($forum, $thread, $comment_id);

?>
                    <tr>
                        <td><?php printf($reporter) ?></td>
                        <td><?php printf(format_time($cur_report['created'])) ?></td>
                        <td><?php echo $comment ?></td>
                    </tr>
<?php

	}
} else {

?>
                    <tr>
                        <td colspan="4"><?php _e('There are no new reports.', 'luna') ?></td>
                    </tr>
<?php

}

?>
                </tbody>
            </table>
        </div>
        <form class="form-horizontal" method="post" action="index.php">
            <input type="hidden" name="form_sent" value="1" />
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('Admin notes', 'luna') ?><span class="pull-right"><button class="btn btn-primary" type="submit" name="save"><span class="fa fa-fw fa-check"></span> <?php _e('Save', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <textarea class="form-control" name="form[admin_note]" placeholder="<?php _e('Add a note...', 'luna') ?>" accesskey="n" rows="10"><?php echo $luna_config['o_admin_note'] ?></textarea>
                </div>
            </div>
        </form>
    </div>
	<div class="col-sm-4">
<?php
//Update checking
if (version_compare(Version::LUNA_CORE_VERSION, $update_cache, 'lt')) {
?>
		<div class="alert alert-info">
			<h4><i class="fa fa-fw fa-moon-o"></i> <?php echo sprintf(__('Luna v%s is available, %s!', 'luna'), $update_cache, '<a href="update.php">'.__('update now', 'luna').'</a>') ?></h4>
		</div>
<?php
}

if(substr(sprintf('%o', fileperms(LUNA_ROOT.'config.php')), -4) > '644'): ?>
        <div class="alert alert-warning"><i class="fa fa-fw fa-exclamation-triangle"></i> <?php _e('The config file is writeable at this moment, you might want to set the CHMOD to 640 or 644.', 'luna') ?></div>
<?php endif;

if ($install_file_exists) : ?>
        <div class="alert alert-warning"><i class="fa fa-fw fa-exclamation-triangle"></i> <?php _e('The file install.php still exists, but should be removed.', 'luna') ?></div>
<?php endif; ?>
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title"><?php _e('Statistics', 'luna') ?></h3>
            </div>
            <div class="panel-body">
                <div class="row">
                    <h4 class="text-center col-xs-4"><b><?php printf(forum_number_format($stats['total_comments'])) ?></b><br /><?php echo _n('comment', 'comments', $stats['total_comments'], 'luna') ?></h4>
                    <h4 class="text-center col-xs-4"><b><?php printf(forum_number_format($stats['total_threads'])) ?></b><br /><?php echo _n('thread', 'threads', $stats['total_threads'], 'luna') ?></h4>
                    <h4 class="text-center col-xs-4"><b><?php printf(forum_number_format($stats['total_users'])) ?></b><br /><?php echo _n('user', 'users', $stats['total_users'], 'luna') ?></h4>
                </div>
            </div>
        </div>
	</div>
</div>
<?php

require 'footer.php';
