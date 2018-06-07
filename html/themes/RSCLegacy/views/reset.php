<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;

?>

<div class="main container">
	<div class="row">
		<div class="col-xs-12">
			<form method="post" action="delete.php?id=<?php echo $id ?>&action=reset">
				<div class="title-block title-block-primary">
					<h2><i class="fa fa-fw fa-trash"></i> <?php draw_delete_title(); ?><span class="pull-right"><button type="submit" class="btn btn-default" name="reset"><span class="fa fa-fw fa-trash"></span> <?php _e('Unhide', 'luna') ?></button></span></h2>
				</div>
				<div class="tab-content">
					<p><?php _e('This comment has been hidden. We\'ll unhide it again with a click on the button.', 'luna') ?></p>
					<hr />
					<?php echo $cur_comment['message'] ?>
				</div>
			</form>
		</div>
	</div>
</div>