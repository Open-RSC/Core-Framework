<div class="row topic-row <?php echo $item_status ?><?php if ($cur_thread['soft'] == true) echo ' soft'; ?>">
	<div class="col-sm-1 hidden-xs posterAvatar">
		<a href="#" class="container-avatar">
			<img src="<?php echo get_avatar( $cur_thread['avatar_id'] ) ?>" alt="Avatar" class="img-avatar img-center">
		</a>
	</div>
	<div class="col-sm-6 col-xs-6 titleTextRow">
		<div class="topics-forum-cnt">
			<div>
				<?php echo $subject_status ?><?php echo $subject."\n" ?><?php echo $subject_multipage ?>				
			</div>
		</div>
	</div>
	<div class="col-sm-2 hidden-xs forum-stats">
		<dl class="num-rv-cnt">
			<?php if (is_null($cur_thread['moved_to'])) { ?>
			<dt><?php echo $comments_label ?></dt>
			<dd><?php echo forum_number_format($cur_thread['num_replies']) ?></dd>
			<?php } ?>
		</dl>
		<br />
		<dl class="text-rv-cnt">
			<?php if (is_null($cur_thread['moved_to'])) { ?>
			<dt><?php echo $views_label ?></dt>
			<dd><?php echo forum_number_format($cur_thread['num_views']) ?></dd>
			<?php } ?>
		</dl>
	</div>
	<div class="col-sm-3 col-xs-6 lastPostedRow">
		<div class="lastP-forum-cnt">
		<?php echo $by ?>
		</div>
		<?php if ($cur_thread['moved_to'] == 0) { ?>
			<span class="text-muted"> 
				<?php echo $last_comment_date ?>
				<?php if (isset($forum_name)) { echo $forum_name; } ?>
			</span>
		<?php } ?>
		<br />
	</div>
</div>
