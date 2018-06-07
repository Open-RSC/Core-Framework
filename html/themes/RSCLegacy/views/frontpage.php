<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<div id="content-middle">
	<div class="row play">
		<div class="home-play-now play-now">
		  <a href="download.php">PLAY FOR FREE</a>
		</div>
	</div>
	<?php
		require load_page('frontpage-poll.php');
	?>
	<div class="col-sm-9 content-l-side">
		<section class="frontpage-news">
			<div class="tab-hd">
				<ul id="frontpage_list">
					<li class="current"><a href="javascript:void(0);" onclick="return false;" title="News">News <span class="tab-con fa fa-asterisk"></span></a></li>
					<li><a href="javascript:void(0);" onclick="return false;" title="Topics">Threads <span class="tab-con fa fa-comment"></span></a></li>
					<li><a href="javascript:void(0);" onclick="return false;" title="Recent Game Activity">Activity <span class="tab-con fa fa-rss"></span></a> </li>
				</ul>
			</div>
			<div class="tab-bd-con show_con-list">
				<?php
				while($cur_news = $db->fetch_assoc($fetch_news)) { 
				$newsMessage = $cur_news['message'];	
				$newsString = strip_tags($newsMessage);
				if (strlen($newsString) > 375) {
					$stringCut = substr($newsString, 0, 375);
					$newsString = substr($stringCut, 0, strrpos($stringCut, ' ')).'... [url=http://wolfkingdom.net/thread.php?id='.$cur_news['id'].']Read More[/url]'; 
				}
				?>
				<div class="frontpage-body">
				<div class="news-title"><a href="thread.php?id=<?php echo $cur_news['id'] ?>"><span class="fa fa-asterisk"></span> <?php echo $cur_news['subject'] ?></a></div>
				<div class="inner-cont">
				<?php echo parse_message($newsString) ?>
				</div>
				<div class="news_footer">
					<span class="news_left">
						Posted by <?php echo user_append_rank(luna_htmlspecialchars($cur_news['commenter']), $cur_news['group_id']) . " on " . date("M j, Y", $cur_news['commented'])?>
					</span>
					<span class="news_right">
						<a href="thread.php?id=<?php echo $cur_news['id'] ?>"><?php echo number_format($cur_news['num_replies']) ?></a> Comments, <?php echo number_format($cur_news['num_views']) ?> Views
					</span>
				</div>
				</div>
				<?php } ?>
				<div class="frontpage-button">
					<a class="btn btn-rounded-blue" href="viewforum.php?id=1">More News</a>		
				</div>
			</div>
			<div class="tab-bd-con show_con-list" style="display: none;">
				<ul class="m-list">
				<?php 
				 if ($db->num_rows($threads) > 0) {
					while ($pull_threads = $db->fetch_assoc($threads)) {
							echo '
							<li class="topics-icon">
							<span class="fa fa-comment"></span>
							<span class="date">' . date('m/d/y H:i:s', $pull_threads["commented"]) . '</span>
							<a class="tp" title="' . $pull_threads["subject"] . '" href="thread.php?pid=' . $pull_threads['first_comment_id'] . '#p' . $pull_threads["first_comment_id"] . '"><b>' . $pull_threads["subject"] . '</b></a>
							by: ' . user_append_rank(ucwords($pull_threads['commenter']), $pull_threads['group_id']) .'
							</li>
							';
					}
				} else {
					echo '<li><p class="list-group-item-text">No threads have been created..</p></li>';
				}
				?>
				</ul>
			</div>
			<div class="tab-bd-con show_con-list" style="display: none;">
				<ul class="m-list">
				<?php 
				 if ($db->num_rows($activity) > 0) {
					while ($pull_feeds = $db->fetch_assoc($activity)) {
						echo '
						<li>
						<p class="list-group-item-text">
						<font style="color:darkred"><span class="fa fa-rss"></span></font>
						<strong>' . $pull_feeds['username'] . '</strong>
						' . $pull_feeds['message'] . '
						<span class="frontpage-date pull-right">
						 ' . time_ago($pull_feeds['time']) . '</span>
						</p>
						</li>';
						}
				} else {
					echo '<li><p class="list-group-item-text">No game activity available..</p></li>';
				}
				?>
				</ul>
			</div>
		</section>
	</div>
</div>