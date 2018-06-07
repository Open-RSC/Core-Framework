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
			<div class="title-block title-block-primary">
				<h2><i class="fa fa-paper-plane-o"></i> <?php _e('Inbox', 'luna') ?><span class="pull-right"><a type="button" class="btn btn-transparent" href="new_inbox.php?reply=<?php echo $tid ?>"><span class="fa fa-fw fa-reply"></span> <?php _e('Reply', 'luna') ?></a></span></h2>
			</div>
	<?php
	echo $paging_links;

	draw_response_list();

	echo $paging_links;
	?>
		<div class="btn-group">
			<a type="button" class="btn btn-transparent btn-lg btn-block btn-bottom" href="new_inbox.php?reply=<?php echo $tid ?>"><span class="fa fa-fw fa-reply"></span> <?php _e('Reply', 'luna') ?></a>
		</div>
		</div>
	</div>
</div>