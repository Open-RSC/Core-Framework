<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;

?>

<form id="search" method="get" action="search.php?section=advanced">
	<div class="main container">
		<div class="title-block title-block-primary">
			<h2>
				<i class="fa fa-fw fa-search"></i> <?php _e('Search', 'luna') ?>
				<?php if ($luna_config['o_enable_advanced_search'] == '1') { ?>
				<span class="pull-right">
					<button class="btn btn-default-2" type="submit" name="search"><i class="fa fa-fw fa-search"></i> <?php _e('Search', 'luna') ?></button>
				</span>
				<?php } ?>
			</h2>
		</div>
		<div class="tab-content">
			<input type="hidden"  name="action" value="search" />
			<div class="row tab-fix">
				<div class="col-md-2 col-sm-4 col-xs-6">
					<input placeholder="<?php _e('Keyword', 'luna') ?>" class="form-control" type="text" name="keywords" maxlength="100" />
				</div>
				<div class="col-md-2 col-sm-4 col-xs-6">
					<input placeholder="<?php _e('Author', 'luna') ?>"  class="form-control" id="author" type="text" name="author" maxlength="25" />
				</div>
				<div class="col-md-2 col-sm-4 col-xs-6">
					<select class="form-control" id="search_in" name="search_in">
						<option value="0"><?php _e('Comment text and thread subject', 'luna') ?></option>
						<option value="1"><?php _e('Comment text only', 'luna') ?></option>
						<option value="-1"><?php _e('Thread subject only', 'luna') ?></option>
					</select>
				</div>
				<div class="col-md-2 col-sm-4 col-xs-6">
					<select class="form-control" name="sort_by">
						<option value="0"><?php _e('Comment time', 'luna') ?></option>
						<option value="1"><?php _e('Author', 'luna') ?></option>
						<option value="2"><?php _e('Subject', 'luna') ?></option>
						<option value="3"><?php _e('Forum', 'luna') ?></option>
					</select>
				</div>
				<div class="col-md-2 col-sm-4 col-xs-6">
					<select class="form-control" name="sort_dir">
						<option value="DESC"><?php _e('Descending', 'luna') ?></option>
						<option value="ASC"><?php _e('Ascending', 'luna') ?></option>
					</select>
				</div>
				<div class="col-md-2 col-sm-4 col-xs-6">
					<select class="form-control" name="show_as">
						<option value="threads"><?php _e('Threads', 'luna') ?></option>
						<option value="comments"><?php _e('Comments', 'luna') ?></option>
					</select>
				</div>
			</div>
			<hr />
			<fieldset>
				<?php echo draw_search_forum_list(); ?>
			</fieldset>
		</div>
	</div>
</form>