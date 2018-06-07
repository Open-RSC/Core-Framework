<div class="<?php echo $item_status ?> row forum-row">
	<!-- Continue the information per forum section -->
	<div class="col-sm-6 col-xs-7">
		<span class="<?php echo $icon_type ?>"><?php echo $add_icon_comment ?></span>
		<div class="tclcon">
			<div>
				<?php echo $forum_field."\n".$forum_desc ?>
			</div>
		</div>
		</div>
		<div class="col-sm-2 hidden-xs"><span class="thread-stats"><b><?php echo forum_number_format($cur_forum['num_threads']) ?></b> <?php echo $threads_label ?><br /><b><?php echo forum_number_format($cur_forum['num_comments']) ?></b> <?php echo $comments_label ?></span></div>
		<div class="col-sm-4 col-xs-5"><?php echo $last_comment ?></div>
</div>



