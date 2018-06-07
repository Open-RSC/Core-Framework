<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;

?>
<div class="main container">
	<div id="search">
		<div class="title-block title-block-primary">
			<h2>
				<i class="fa fa-fw fa-search"></i> <?php _e('Search', 'luna') ?>
				<?php if ($luna_config['o_enable_advanced_search'] == '1') { ?>
					<span class="pull-right">
						<a class="btn btn-default-2" href="search.php?section=advanced"><?php _e('Advanced', 'luna') ?></a>
					</span>
				<?php } ?>
			</h2>
		</div>
		<div class="tab-content">
			<form id="search" method="get" action="search.php?section=simple">
				<fieldset>
					<input type="hidden" name="action" value="search" />
					<input type="hidden" name="sort_dir" value="DESC" />
					<div class="input-group"><input class="form-control" type="text" name="keywords" placeholder="<?php _e('Search', 'luna') ?>" maxlength="100" /><span class="input-group-btn"><button class="btn btn-primary" type="submit" name="search" accesskey="s"><span class="fa fa-fw fa-search"></span> <?php _e('Search', 'luna') ?></button></span></div>
				</fieldset>
			</form>
		</div>
	</div>
</div>