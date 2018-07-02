<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;

?>
<div class="profile-header container-fluid">
	<div class="jumbotron profile">
		<div class="container">
			<div class="col-sm-12">
				<h2 class="username"><?php echo $user['username'] ?></h2>
			</div>
		</div>
	</div>
</div>
<div id="wrapper" class="container">
	<div class="profile">
		<div class="col-xs-12 col-sm-3 sidebar">
			<div class="container-avatar">
				<img src="<?php echo get_avatar( $user['id'] ) ?>" alt="Avatar" class="img-avatar img-center">
			</div>
			<?php load_me_nav('profile'); ?>
		</div>
		<div class="col-xs-12 col-sm-9">
			<div class="title-block title-block-primary">
				<h2><i class="fa fa-fw fa-user"></i> <?php echo luna_htmlspecialchars($user['username']) ?></h2>
			</div>
			<div class="tab-content tab-about">
				<div class="row">
					<h3 class="col-lg-3 col-md-4 col-sm-6 text-center">
						<small><?php _e('Title', 'luna') ?></small>
						<?php echo get_title($user) ?>
					</h3>
					<h3 class="col-lg-3 col-md-4 col-sm-6 text-center">
						<small><?php _e('Comments', 'luna') ?></small>
						<?php echo forum_number_format($user['num_comments']) ?>
					</h3>
					<h3 class="col-lg-3 col-md-4 col-sm-6 text-center">
						<small><?php _e('Latest comment', 'luna') ?></small>
						<?php echo $last_comment ?>
					</h3>
					<h3 class="col-lg-3 col-md-4 col-sm-6 text-center">
						<small><?php _e('Registered since', 'luna') ?></small>
						<?php echo format_time($user['registered'], true) ?>
					</h3>
					<h3 class="col-lg-3 col-md-4 col-sm-6 text-center">
						<small><?php _e('Latest visit', 'luna') ?></small>
						<?php echo format_time($user['last_visit'], true) ?>
					</h3>
					<?php if ($user['realname'] != '') { ?>
					<h3 class="col-lg-3 col-md-4 col-sm-6 text-center">
						<small><?php _e('Real name', 'luna') ?></small>
						<?php echo $user['realname'] ?>
					</h3>
					<?php } if ($user['location'] != '') { ?>
					<h3 class="col-lg-3 col-md-4 col-sm-6 text-center">
						<small><?php _e('Location', 'luna') ?></small>
						<?php echo $user['location'] ?>
					</h3>
					<?php } ?>
				</div>
			</div>
			<div class="tab-footer">
				<?php echo $user_activities ?>
			</div>
			<?php if (!empty($user_messaging) || (($user['email_setting'] != '0' && ($luna_user['g_send_email'] == '1')))): ?>
				<div class="title-block title-block-primary">
					<h2><i class="fa fa-fw fa-paper-plane-o"></i> <?php _e('Contact', 'luna') ?><?php if ($user['email_setting'] == '1' && !$luna_user['is_guest'] && $luna_user['g_send_email'] == '1') { echo '<span class="pull-right"><a class="btn btn-transparent" href="misc.php?email='.$id.'"><span class="fa fa-fw fa-send-o"></span> '.__('Send email', 'luna').'</a></span>'; } ?></h2>
				</div>
				<?php if (!empty($user_messaging)): ?>
					<div class="tab-content tab-contact">
						<?php echo implode("\n\t\t\t\t\t\t\t", $user_messaging)."\n" ?>
					</div>
				<?php endif; ?>
			<?php
			endif;

			if ($luna_config['o_signatures'] == '1' && isset($parsed_signature)) {
			?>
				<div class="title-block title-block-primary">
					<h2><i class="fa fa-fw fa-map-signs"></i> <?php _e('Signature', 'luna') ?></h2>
				</div>
				<div class="tab-content">
					<?php echo $user_signature ?>
				</div>
			<?php } ?>
		</div>
	</div>
</div>