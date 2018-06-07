<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;

?>
<div class="profile-header container-fluid">
	<div class="jumbotron profile">
		<div class="container">
			<div class="col-sm-12">
				<h2 class="username"><?php echo $luna_user['username'] ?></h2>
			</div>
		</div>
	</div>
</div>
<div id="wrapper" class="container">
	<div class="profile">
		<div class="col-xs-12 col-sm-3 sidebar">
			<div class="container-avatar">
				<img src="<?php echo get_avatar( $luna_user['id'] ) ?>" alt="Avatar" class="img-avatar img-center">
			</div>
			<?php load_me_nav('inbox'); ?>
		</div>
		<div class="col-xs-12 col-sm-9">
<?php
// If there are errors, we display them
if (!empty($errors)) {
?>
			<div class="title-block title-block-danger">
				<h2><i class="fa fa-fw fa-exclamation-triangle "></i> <?php _e('Comment errors', 'luna') ?></h2>
			</div>
			<div class="tab-content tab-content-danger">
			<?php
				foreach ($errors as $cur_error)
					echo "\t\t\t\t".$cur_error."\n";
			?>
			</div>
<?php

} elseif (isset($_POST['preview'])) {
	require_once LUNA_ROOT.'include/parser.php';
	$preview_message = parse_message($p_message);

?>
			<div class="title-block title-block-primary">
				<h2><i class="fa fa-eye"></i> <?php _e('Comment preview', 'luna') ?></h2>
			</div>
			<div class="tab-content">
				<p><?php echo $preview_message."\n" ?></p>
			</div>
<?php

}

$cur_index = 1;

?>
			<form class="form-horizontal" method="post" id="comment" action="new_inbox.php" onsubmit="return process_form(this)">
				<div class="title-block title-block-primary">
					<h2><i class="fa fa-paper-plane-o"></i> <?php _e('Inbox', 'luna') ?></h2>
				</div>
				<div class="tab-content new-inbox">
					<fieldset>
						<input type="hidden" name="form_sent" value="1" />
						<input type="hidden" name="form_user" value="<?php echo luna_htmlspecialchars($luna_user['username']) ?>" />
						<?php echo (($r != '0') ? '<input type="hidden" name="reply" value="'.$r.'" />' : '') ?>
						<?php echo (($q != '0') ? '<input type="hidden" name="quote" value="1" />' : '') ?>
						<?php echo (($tid != '0') ? '<input type="hidden" name="tid" value="'.$tid.'" />' : '') ?>
						<input type="hidden" name="p_username" value="<?php echo luna_htmlspecialchars($p_destinataire) ?>" />
						<input type="hidden" name="req_subject" value="<?php echo luna_htmlspecialchars($p_subject) ?>" />
						<?php if ($r != '1') { ?>
						<div class="form-group">
							<input class="form-control" type="text" name="p_username" placeholder="<?php _e('Receivers', 'luna') ?>" id="p_username" size="30" value="<?php echo luna_htmlspecialchars($p_destinataire) ?>" tabindex="<?php echo $cur_index++ ?>" autofocus />
						</div>
						<div class="form-group">
							<input class="form-control" type="text" name="req_subject" placeholder="<?php _e('Subject', 'luna') ?>" value="<?php echo ($p_subject != '' ? luna_htmlspecialchars($p_subject) : ''); ?>" tabindex="<?php echo $cur_index++ ?>" />
						</div>
						<?php } ?>
						<div class="form-group">
							<?php draw_editor('10'); ?>
						</div>
					</fieldset>
				</div>
			</form>
		</div>
	</div>
</div>