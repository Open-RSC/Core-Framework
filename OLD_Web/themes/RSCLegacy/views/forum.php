<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;

?>
<div id="wrapper" class="container">
	<div class="forumview">
		<ol class="breadcrumb">
		  <li><a href="forum.php">Home</a></li>
		  <li><a href="forum.php">Forum Index</a></li>
		  <li class="active"><?php echo $faicon.luna_htmlspecialchars($cur_forum['forum_name']) ?></li>
		</ol>
		<?php echo $paging_links ?>
			<div class="forum-box">
				<div class="row title-block title-block-primary title-block-forum">
					<h2 class="forum-title"><?php echo $faicon.luna_htmlspecialchars($cur_forum['forum_name']) ?><span class="pull-right"><?php echo $comment_link ?></span></h2>
					<div class="forum-desc"><?php echo $cur_forum['forum_desc'] ?></div>
				</div>
				<div class="row forum-header">
					<div class="col-sm-8 col-xs-8 topics-title">Topics</div>
					<div class="col-sm-4 col-xs-4"><span class="last_message_tp_title">Last Message</span></div>
				</div>
				
				
			<?php draw_threads_list(); ?>
			</div>
			<?php echo $paging_links; ?>
			<div class="spacer"></div>
			<?php if (!$luna_user['is_guest']) { ?>
			<div class="dropdown">
				<button class="btn btn-primary dropdown-toggle topshape-btn" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
				Thread Options
				<span class="caret"></span>
				</button>
				<ul class="dropdown-menu" aria-labelledby="dropdownMenu1">
				<?php if (!$luna_user['is_guest'] && $luna_config['o_forum_subscriptions'] == '1') { ?>
				<li>
					<?php if ($cur_forum['is_subscribed']) { ?>
						<a href="misc.php?action=unsubscribe&amp;fid=<?php echo $id ?><?php echo $token_url ?>"><span class="fa fa-fw fa-star-o"></span> <?php _e('Unsubscribe', 'luna') ?></a>
					<?php } else { ?>
						<a href="misc.php?action=subscribe&amp;fid=<?php echo $id ?><?php echo $token_url ?>"><span class="fa fa-fw fa-star"></span> <?php _e('Subscribe', 'luna') ?></a>
					<?php } ?>
				</li>
				<?php } ?>
				<li>
					<?php draw_mark_read('', 'forumview') ?>
					<?php if ($id != '0' && $is_admmod) { ?>
						<a href="backstage/moderate.php?fid=<?php echo $forum_id ?>&p=<?php echo $p ?>"><span class="fa fa-fw fa-eye"></span> <?php _e('Moderate forum', 'luna') ?></a>
					<?php } ?>
				</li>
				<div class="visible-xs-block"><hr /></div>
				</ul>
			</div>
			<?php } ?>
		<ol class="breadcrumb">
		  <li><a href="forum.php">Home</a></li>
		  <li><a href="forum.php">Forum Index</a></li>
		  <li class="active"><?php echo $faicon.luna_htmlspecialchars($cur_forum['forum_name']) ?></li>
		</ol>
	</div>
</div>