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
			<?php load_me_nav('notifications'); ?>
		</div>
		<div class="col-xs-12 col-sm-9">
			<div class="title-block title-block-primary title-block-nav">
				<h2><i class="fa fa-fw fa-circle-o"></i> <?php _e('Notifications', 'luna') ?></h2>
				<ul class="nav nav-tabs" role="tablist">
					<li role="presentation" class="active"><a class="first" href="#new" aria-controls="new" role="tab" data-toggle="tab"><i class="fa fa-fw fa-circle"></i><span class="hidden-sm hidden-xs"> <?php _e('New', 'luna') ?></span></a></li>
					<li role="presentation" ><a class="second" href="#seen" aria-controls="seen" role="tab" data-toggle="tab"><i class="fa fa-fw fa-circle-o"></i><span class="hidden-sm hidden-xs"> <?php _e('Seen', 'luna') ?></span></a></li>
				</ul>
			</div>
			<div class="tab-content">
				<div role="tabpanel" class="tab-pane active" id="new">
					<?php if ($num_not_unseen != '0') { ?>
						<a class="btn btn-primary" href="notifications.php?id=<?php echo $luna_user['id'] ?>&action=readnoti"><span class="fa fa-fw fa-eye"></span> <?php _e('Seen all', 'luna') ?></a>
					<?php } ?>
					<?php echo $not; ?>
				</div>
				<div role="tabpanel" class="tab-pane" id="seen">
					<?php if ($num_not_seen != '0') { ?>
						<a class="btn btn-danger" href="notifications.php?id=<?php echo $luna_user['id'] ?>&action=delnoti"><span class="fa fa-fw fa-trash"></span> <?php _e('Remove all', 'luna') ?></a>
					<?php } ?>
					<?php echo $not_seen; ?>
				</div>
			</div>
		</div>
	</div>
</div>