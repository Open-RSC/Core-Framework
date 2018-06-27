<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;

?>
<div class="main container">
	<div id="search_results">
		<div class="title-block title-block-primary">
			<h2><i class="fa fa-fw fa-search"></i> <?php _e('Search results', 'luna') ?><span class="pull-right"><?php echo $paging_links ?></span></h2>
		</div>
		<div class="list-group list-group-thread">
			<?php draw_search_results(); ?>
		</div>
	</div>
</div>