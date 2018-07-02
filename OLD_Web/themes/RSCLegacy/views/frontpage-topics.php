<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<div class="panel panel-default">
	<div class="panel-heading">
	<h4 class="panel-title">Topics</h4>
	</div>
	<div class="base_body">
		<ul class="list-group">
		<?php 
		while($latest = $db->fetch_assoc($fetch_topics)) { 
		
		$title = $latest['subject'];	
		if (strlen($title) > 20) {
			$title = substr($title, 0, 20).'...';
		}
		
		?>
		<li class="list-group-item">
			<div class="pull-right">
				<i class="fa fa-comments"></i> <?php echo number_format($latest['num_replies']) ?>
				<i class="fa fa-eye"></i> <?php echo number_format($latest['num_views']) ?>
			</div>
			<a class="topicA" href="thread.php?id=<?php echo $latest['id'] ?>"><?php echo $title ?></a><br>
			<span>Started by <a href="profile.php?id=<?php echo $latest['uid'] ?>"><?php echo user_append_rank($latest['commenter'], $latest['group_id'])?></a></span>
		</li>
		<?php } ?>				
		</ul>
		<div class="frontpage-button">
			<a class="btn btn-rounded-black" href="forum.php">See More</a>		
		</div>
	</div>
</div>