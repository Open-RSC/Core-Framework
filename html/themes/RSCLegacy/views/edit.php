<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;

$jumbo_style = ' style="background:#262626;"';
$btn_style = ' style="color:#262626;"';

?>
<div class="jumbotron edit-title"<?php echo $jumbo_style ?>>
	<div class="container">
		<h2 class="forum-title"><?php printf(__('Edit %s', 'luna'), luna_htmlspecialchars($cur_comment['subject'])) ?><span class="pull-right naviton"><a class="btn btn-default-2"<?php echo $btn_style ?> href="thread.php?id=<?php echo $cur_comment['tid'] ?>"><span class="fa fa-fw fa-chevron-left"></span> <?php _e('Cancel', 'luna') ?></a></span></h2>
	</div>
</div>
<div class="main container editor-only">
<?php
if (isset($errors))
	draw_error_panel($errors);
if (isset($message))
	draw_preview_panel($message);
?>

	<form id="edit" method="post" action="edit.php?id=<?php echo $id ?>&amp;action=edit" onsubmit="return process_form(this)">
		<?php if ($can_edit_subject): ?>
			<input class="info-textfield form-control" type="text" name="req_subject" maxlength="70" value="<?php echo luna_htmlspecialchars(isset($_POST['req_subject']) ? $_POST['req_subject'] : $cur_comment['subject']) ?>" tabindex="<?php echo $cur_index++ ?>" />
		<?php endif; ?>
		<?php draw_editor('20'); ?>
		<?php draw_admin_note(); ?>
	</form>
</div>