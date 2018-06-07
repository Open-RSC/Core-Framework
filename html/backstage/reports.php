<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'content');
define('LUNA_PAGE', 'reports');

require LUNA_ROOT.'include/common.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}

// Zap a report
if (isset($_POST['zap_id'])) {
	confirm_referrer('backstage/reports.php');

	$zap_id = intval(key($_POST['zap_id']));

	$result = $db->query('SELECT zapped FROM '.$db->prefix.'reports WHERE id='.$zap_id) or error('Unable to fetch report info', __FILE__, __LINE__, $db->error());
	$zapped = $db->result($result);

	if ($zapped == '') {
		$db->query('UPDATE '.$db->prefix.'reports SET zapped='.time().', zapped_by='.$luna_user['id'].' WHERE id='.$zap_id) or error('Unable to zap report', __FILE__, __LINE__, $db->error());
		$result = $db->query('SELECT comment_id FROM '.$db->prefix.'reports WHERE id='.$zap_id) or error('Unable to fetch report info', __FILE__, __LINE__, $db->error());
		$comment_id = $db->result($result);
		$db->query('UPDATE '.$db->prefix.'comments SET marked = 0 WHERE id='.$comment_id) or error('Unable to zap report', __FILE__, __LINE__, $db->error());
	}

	// Delete old reports (which cannot be viewed anyway)
	$result = $db->query('SELECT zapped FROM '.$db->prefix.'reports WHERE zapped IS NOT NULL ORDER BY zapped DESC LIMIT 10,1') or error('Unable to fetch read reports to delete', __FILE__, __LINE__, $db->error());
	if ($db->num_rows($result) > 0) {
		$zapped_threshold = $db->result($result);
		$db->query('DELETE FROM '.$db->prefix.'reports WHERE zapped <= '.$zapped_threshold) or error('Unable to delete old read reports', __FILE__, __LINE__, $db->error());
	}

	redirect('backstage/reports.php');
}

require LUNA_ROOT.'include/parser.php';

require 'header.php';
?>
<div class="row">
	<div class="col-sm-12">
        <div class="title title-md title-primary title-nav title-non">
            <ul class="nav nav-tabs" role="tablist">
                <li role="presentation" class="nav-item active"><a class="nav-link" href="#new" aria-controls="new" role="tab" data-toggle="tab"><i class="fa fa-fw fa-flag-o"></i> <?php _e('New reports', 'luna') ?></a></li>
                <li role="presentation" class="nav-item"><a class="nav-link" href="#old" aria-controls="old" role="tab" data-toggle="tab"><i class="fa fa-fw fa-flag"></i> <?php _e('Old reports', 'luna') ?></a></li>
            </ul>
        </div>
        <div class="tab-content">
            <div role="tabpanel" class="tab-pane fade in active" id="new">
                <form method="post" action="reports.php?action=zap">
<?php
$result = $db->query( 'SELECT r.id, r.thread_id, r.forum_id, r.reported_by, r.created, r.message, p.id AS pid, t.subject, f.forum_name, p.message AS comment, u.username AS reporter FROM '.$db->prefix.'reports AS r LEFT JOIN '.$db->prefix.'comments AS p ON r.comment_id=p.id LEFT JOIN '.$db->prefix.'threads AS t ON r.thread_id=t.id LEFT JOIN '.$db->prefix.'forums AS f ON r.forum_id=f.id LEFT JOIN '.$db->prefix.'users AS u ON r.reported_by=u.id WHERE r.zapped IS NULL ORDER BY created DESC' ) or error( 'Unable to fetch report list', __FILE__, __LINE__, $db->error() );

if ( $db->num_rows( $result ) ) {
	while ( $cur_report = $db->fetch_assoc( $result ) ) {
		$reporter = ($cur_report['reporter'] != '') ? '<a href="../profile.php?id='.$cur_report['reported_by'].'">'.luna_htmlspecialchars($cur_report['reporter'] ).'</a>' : __( 'Deleted user', 'luna' );
		$forum = ($cur_report['forum_name'] != '') ? '<li><a href="../viewforum.php?id='.$cur_report['forum_id'].'">'.luna_htmlspecialchars($cur_report['forum_name'] ).'</a></li>' : '<li>'.__( 'Deleted', 'luna' ).'</li>';
		$thread = ($cur_report['subject'] != '') ? '<li><a href="../thread.php?id='.$cur_report['thread_id'].'">'.luna_htmlspecialchars($cur_report['subject'] ).'</a></li>' : '<li>'.__( 'Deleted', 'luna' ).'</li>';
		$message = str_replace("\n", '<br />', luna_htmlspecialchars($cur_report['message'] ) );
		$comment_id = ( $cur_report['pid'] != '' ) ? '<li><a href="../thread.php?pid='.$cur_report['pid'].'#p'.$cur_report['pid'].'">'.sprintf(__( 'Comment #%s', 'luna' ), $cur_report['pid'] ).'</a></li>' : '<li>'.__( 'Deleted', 'luna' ).'</li>';
		$report_location = array($forum, $thread, $comment_id);
		$comment = ( $cur_report['comment'] != '' ) ? parse_message( $cur_report['comment'] ) : '<p>'.__( 'Deleted', 'luna' ).'</p>';
?>
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <h3 class="panel-title"><?php echo $reporter ?><span class="dimmed"> &middot; <?php echo format_time( $cur_report['created'] ) ?></span></h3>
                        </div>
                        <div class="panel-body">
                            <ol class="breadcrumb"><?php echo implode( '', $report_location ) ?></ol>
                            <?php echo $message ?>
                            <hr />
                            <?php echo $comment ?>
                        </div>
                        <div class="panel-footer">
                            <button class="btn btn-primary" type="submit" name="zap_id[<?php echo $cur_report['id'] ?>]"><span class="fa fa-fw fa-eye"></span> <?php _e( 'Mark as read', 'luna' ) ?></button>
                            <a class="btn btn-primary" href="../edit.php?id=<?php echo $cur_report['pid'] ?>"><i class="fa fa-fw fa-pencil"></i><?php _e( 'Edit', 'luna' ) ?></a>
                            <a class="btn btn-danger" href="../delete.php?id=<?php echo $cur_report['pid'] ?>&action=delete"><i class="fa fa-fw fa-trash"></i><?php _e( 'Remove', 'luna' ) ?></a>
                        </div>
                    </div>
<?php
	}
} else {
?>
                </form>
                <h3 class="text-center"><?php _e( 'There are no new reports.', 'luna' ) ?></h3>
<?php } ?>
            </div>
            <div role="tabpanel" class="tab-pane fade" id="old">
<?php
$result = $db->query('SELECT r.id, r.thread_id, r.forum_id, r.reported_by, r.message, r.zapped, r.zapped_by AS zapped_by_id, p.id AS pid, p.message AS comment, t.subject, f.forum_name, u.username AS reporter, u2.username AS zapped_by FROM '.$db->prefix.'reports AS r LEFT JOIN '.$db->prefix.'comments AS p ON r.comment_id=p.id LEFT JOIN '.$db->prefix.'threads AS t ON r.thread_id=t.id LEFT JOIN '.$db->prefix.'forums AS f ON r.forum_id=f.id LEFT JOIN '.$db->prefix.'users AS u ON r.reported_by=u.id LEFT JOIN '.$db->prefix.'users AS u2 ON r.zapped_by=u2.id WHERE r.zapped IS NOT NULL ORDER BY zapped DESC LIMIT 10') or error('Unable to fetch report list', __FILE__, __LINE__, $db->error() );

if ($db->num_rows($result) ) {
	while ($cur_report = $db->fetch_assoc($result) ) {
		$reporter = ($cur_report['reporter'] != '') ? '<a href="../profile.php?id='.$cur_report['reported_by'].'">'.luna_htmlspecialchars($cur_report['reporter'] ).'</a>' : __( 'Deleted user', 'luna' );
		$forum = ($cur_report['forum_name'] != '') ? '<li><a href="../viewforum.php?id='.$cur_report['forum_id'].'">'.luna_htmlspecialchars($cur_report['forum_name'] ).'</a></li>' : '<li>'.__( 'Deleted', 'luna' ).'</li>';
		$thread = ($cur_report['subject'] != '') ? ' <li><a href="../thread.php?id='.$cur_report['thread_id'].'">'.luna_htmlspecialchars($cur_report['subject'] ).'</a></li>' : '<li>'.__( 'Deleted', 'luna' ).'</li>';
		$message = str_replace("\n", '<br />', luna_htmlspecialchars($cur_report['message'] ) );
		$comment_id = ($cur_report['pid'] != '') ? '<li><a href="../thread.php?pid='.$cur_report['pid'].'#p'.$cur_report['pid'].'">'.sprintf(__( 'Comment #%s', 'luna' ), $cur_report['pid'] ).'</a></li>' : '<li>'.__( 'Deleted', 'luna' ).'</li>';
		$zapped_by = ($cur_report['zapped_by'] != '') ? '<a href="../profile.php?id='.$cur_report['zapped_by_id'].'">'.luna_htmlspecialchars($cur_report['zapped_by'] ).'</a>' : __( 'N/A', 'luna' );
		$zapped_by = ($cur_report['zapped_by'] != '') ? '<strong>'.luna_htmlspecialchars($cur_report['zapped_by'] ).'</strong>' : __( 'N/A', 'luna' );
		$report_location = array($forum, $thread, $comment_id);
		$comment = ( $cur_report['comment'] != '' ) ? parse_message( $cur_report['comment'] ) : '<p>'.__( 'Deleted', 'luna' ).'</p>';
?>
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <h3 class="panel-title"><?php echo $reporter ?><span class="dimmed"> &middot; <?php echo format_time( $cur_report['zapped'] ) ?></span></h3>
                    </div>
                    <div class="panel-body">
                        <ol class="breadcrumb"><?php echo implode( '', $report_location ) ?></ol>
                        <?php echo $message ?>
                        <hr />
                        <?php echo $comment ?>
                    </div>
                    <div class="panel-footer">
                        <?php printf( __( 'Managed by %s', 'luna'), $zapped_by ) ?>
                    </div>
                </div>
<?php
	}
} else {
?>
                <h3 class="text-center"><?php _e( 'There are no read reports.', 'luna' ) ?></h3>
<?php } ?>
            </div>
        </div>
    </div>
</div>
<?php
require 'footer.php';
