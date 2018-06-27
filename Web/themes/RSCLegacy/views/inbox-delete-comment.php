<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;

?>

<div class="modal fade modal-form" id="delete-form" tabindex="-1" role="dialog" aria-hidden="true">
	<div class="modal-dialog modal-xs">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title"><?php _e('Inbox', 'luna') ?></h4>
			</div>
			<div class="modal-body clearfix">
				<p><?php _e('Are you sure that you want to delete the message(s) from your inbox?', 'luna') ?></p>
				<button type="submit" name="delete_multiple" class="btn btn-danger btn-block"><span class="fa fa-fw fa-trash"></span> <?php _e('Delete', 'luna') ?></button>
				<a class="btn btn-default btn-block" data-dismiss="modal" href="#"><?php _e('Cancel', 'luna') ?></a>
			</div>
		</div>
	</div>
</div>
