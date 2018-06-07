<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;

?>
<div class="main container">
	<div class="row">
		<div class="col-xs-12">
			<form method="get" action="register.php">
				<div class="title-block title-block-primary">
				<h2><i class="fa fa-fw fa-exclamation-circle"></i> <?php _e('Rules', 'luna') ?><span class="pull-right"><button type="submit" class="btn btn-default" name="agree"><span class="fa fa-fw fa-check"></span> <?php _e('Agree', 'luna') ?></button></span></h2>
				</div>
				<div class="tab-content">
					<?php echo $luna_config['o_rules_message'] ?>
				</div>
			</form>
		</div>
	</div>
</div>