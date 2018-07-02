<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;

?>
<div id="wrapper" class="main container">
	<div id="userlist">
		<div class="title-block title-block-primary">
			<h2><i class="fa fa-fw fa-users"></i> <?php _e('Users', 'luna') ?></h2>
		</div>
		<div class="tab-content tab-content-fix">
			<?php if ($luna_user['g_search_users'] == '1') { ?>
				<form class="filters" id="userlist" method="get" action="userlist.php">
					<div class="row tab-fix">
						<div class="col-xs-6">
							<div class="form-group">
								<select class="form-control hidden-xs" name="sort">
									<option value="username"<?php if ($sort_by == 'username') echo ' selected' ?>><?php _e('Sort by username', 'luna') ?></option>
									<option value="registered"<?php if ($sort_by == 'registered') echo ' selected' ?>><?php _e('Sort by registration date', 'luna') ?></option>
									<option value="num_comments"<?php if ($sort_by == 'num_comments') echo ' selected' ?>><?php _e('Sort by number of comments', 'luna') ?></option>
								</select>
							</div>
						</div>
						<div class="col-xs-6">
							<div class="form-group">
								<div class="input-group">
									<input class="form-control" type="text" name="username" value="<?php echo luna_htmlspecialchars($username) ?>" placeholder="<?php _e('Search', 'luna') ?>" maxlength="25" />
									<span class="input-group-btn">
										<button class="btn btn-primary" type="submit" name="search" accesskey="s"><span class="fa fa-fw fa-search"></span></button>
									</span>
								</div>
							</div>
						</div>
					</div>
				</form>
			<?php } ?>
			<hr />
			<div class="row tab-fix">
				<div class="col-lg-12">
					<?php echo $paging_links ?>
				</div>
			</div>
			<div class="userlist row tab-fix">
				<?php draw_user_list() ?>
			</div>
			<div class="row tab-fix">
				<div class="col-lg-12">
					<?php echo $paging_links ?>
				</div>
			</div>
		</div>
	</div>
</div>