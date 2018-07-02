<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;

?>
<div id="wrapper" class="container">
	<ol class="breadcrumb">
	  <li><a href="forum.php">Home</a></li>
	  <li><a href="forum.php">Forum Index</a></li>
	  <li><a href="viewforum.php?id=<?php echo $cur_thread['forum_id']; ?>"><?php echo $faicon.luna_htmlspecialchars($cur_thread['forum_name']) ?></a></li>
	  <li class="active"><?php echo $faicon.luna_htmlspecialchars($cur_thread['subject']) ?></li>
	</ol>
	
	<div class="thread">
		<div class="forum-posts-list">
			<div class="title-block title-block-primary">
				<span class="pull-right"> <ul class="pagination"><?php echo $paging_links ?></ul></span>
				<h2><?php echo luna_htmlspecialchars($cur_thread['subject']) ?></h2>
			</div>
			<?php draw_comment_list(); 
			echo $paging_links;
			if (!$luna_user['is_guest']) { ?>
            <ul class="pagination toolbar">
			<div class="btn-group pull-right">
			<a class="btn btn-transparent dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
				Thread Options
			</a>
			<ul class="dropdown-menu" role="menu">
				<?php if (!$luna_user['is_guest'] && $luna_config['o_thread_subscriptions'] == '1') { ?>
				<?php if ($cur_thread['is_subscribed']) { ?>
						<li ><a href="misc.php?action=unsubscribe&amp;tid=<?php echo $id ?><?php echo $token_url ?>" tabindex="-1"><span class="fa fa-fw fa-star-o"></span> <?php _e('Unsubscribe', 'luna') ?></a></li>
					<?php } else { ?>
						<li ><a href="misc.php?action=subscribe&amp;tid=<?php echo $id ?><?php echo $token_url ?>" tabindex="-1"><span class="fa fa-fw fa-star"></span> <?php _e('Subscribe', 'luna') ?></a></li>
					<?php } ?>
				<?php } ?>
				<?php if ($is_admmod): ?>
				<li>
				<a href="backstage/moderate.php?fid=<?php echo $cur_thread['forum_id'] ?>&tid=<?php echo $id ?>&p=<?php echo $p ?>"><span class="fa fa-fw fa-eye"></span> <?php _e('Moderate', 'luna') ?></a>
				<?php if($num_pages > 1) { ?>
					<a href="backstage/moderate.php?fid=<?php echo $cur_thread['forum_id'] ?>&tid=<?php echo $id ?>&action=all<?php echo $token_url ?>"><span class="fa fa-fw fa-list"></span> <?php _e('Show all', 'luna') ?></a>
				<?php } ?>
				<a href="backstage/moderate.php?fid=<?php echo $cur_thread['forum_id'] ?>&move_threads=<?php echo $id ?>"><span class="fa fa-fw fa-arrows-alt"></span> <?php _e('Move', 'luna') ?></a>
				<?php if ($cur_thread['closed'] == '1') { ?>
					<a class="btn-danger" href="backstage/moderate.php?fid=<?php echo $cur_thread['forum_id'] ?>&open=<?php echo $id ?><?php echo $token_url ?>"><span class="fa fa-fw fa-unlock"></span> <?php _e('Closed', 'luna') ?></a>
				<?php } else { ?>
					<a class="btn-success" href="backstage/moderate.php?fid=<?php echo $cur_thread['forum_id'] ?>&close=<?php echo $id ?><?php echo $token_url ?>"><span class="fa fa-fw fa-lock"></span> <?php _e('Opened', 'luna') ?></a>
				<?php } ?>
	
				<?php if ($cur_thread['pinned'] == '1') { ?>
					<a class="btn-success" href="backstage/moderate.php?fid=<?php echo $cur_thread['forum_id'] ?>&unpin=<?php echo $id ?><?php echo $token_url ?>"><span class="fa fa-fw fa-thumb-tack"></span> <?php _e('Unpinned', 'luna') ?></a>
				<?php } else { ?>
					<a href="backstage/moderate.php?fid=<?php echo $cur_thread['forum_id'] ?>&pin=<?php echo $id ?><?php echo $token_url ?>"><span class="fa fa-fw fa-thumb-tack"></span> <?php _e('Pinned', 'luna') ?></a>
				<?php } ?>
	
				<?php if ($cur_thread['important'] == '1') { ?>
					<a class="btn-success" href="backstage/moderate.php?fid=<?php echo $cur_thread['forum_id'] ?>&unimportant=<?php echo $id ?><?php echo $token_url ?>"><span class="fa fa-fw fa-map-marker"></span> <?php _e('Important', 'luna') ?></a>
				<?php } else { ?>
					<a href="backstage/moderate.php?fid=<?php echo $cur_thread['forum_id'] ?>&important=<?php echo $id ?><?php echo $token_url ?>"><span class="fa fa-fw fa-map-marker"></span> <?php _e('Unimportant', 'luna') ?></a>
				<?php } ?>
				</li>
				<?php endif; ?>
			</ul>
			</div>
			</ul>
			<?php 
			}
			if ($comment_field): ?>
				<form method="post" action="comment.php?tid=<?php echo $id ?>" onsubmit="window.onbeforeunload=null;this.submit.disabled=true;if(process_form(this)){return true;}else{this.submit.disabled=false;return false;}">
				<?php draw_editor('10', 1); ?>
				</form>
			<?php endif; ?>
		</div>
	</div>
	<ol class="breadcrumb">
	  <li><a href="forum.php">Home</a></li>
	  <li><a href="forum.php">Forum Index</a></li>
	  <li><a href="viewforum.php?id=<?php echo $cur_thread['forum_id']; ?>"><?php echo $faicon.luna_htmlspecialchars($cur_thread['forum_name']) ?></a></li>
	  <li class="active"><?php echo $faicon.luna_htmlspecialchars($cur_thread['subject']) ?></li>
	</ol>
</div>