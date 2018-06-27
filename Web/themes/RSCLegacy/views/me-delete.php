<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;

?>

<form class="form-horizontal" id="confirm_del_user" method="post" action="settings.php?id=<?php echo $id ?>">
	<div class="main container">
		<div class="row">
			<div class="col-xs-12">
				<div class="title-block title-block-danger">
					<h2><i class="fa fa-fw fa-user"></i> <?php _e('Confirm deletion', 'luna') ?> &middot; <?php echo luna_htmlspecialchars($username) ?><span class="pull-right"><button type="submit" class="btn btn-default" name="delete_user_comply" accesskey="s"><span class="fa fa-fw fa-trash"></span> <?php _e('Delete', 'luna') ?></button></span></h2>
				</div>
				<div class="tab-content tab-content-danger">
                    <div class="alert alert-warning"><i class="fa fa-fw fa-exclamation"></i> <?php _e('Deleted users and/or comments cannot be restored. If you choose not to delete the comments made by this user, the comments can only be deleted manually at a later time.', 'luna') ?></div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label"><?php _e('User content', 'luna') ?></label>
                        <div class="col-sm-9">
                            <div class="checkbox">
                                <label>
                                    <input type="checkbox" name="delete_comments" value="1" checked />
                                    <?php _e('Delete all comments and threads this user has made.', 'luna') ?>
                                </label>
                            </div>
                        </div>
                    </div>
				</div>
			</div>
		</div>
	</div>
</form>
<?php

	require load_page('footer.php');
