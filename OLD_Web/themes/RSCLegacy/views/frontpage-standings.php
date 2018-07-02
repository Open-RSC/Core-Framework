<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<div id="standings">
	<div class="panel panel-default">
		<div class="panel-heading">
		<h4 class="panel-title"><i class="fa fa-trophy" aria-hidden="true"></i> Standings</h4>
		</div>
		<div class="base_body">
			<div class="base_title"><h2 class="t-gold">Highscore (Skill Total)</h2></div>
			<ul class="list-group">
			<?php while ($top = $db->fetch_assoc($top_highscore)) {
			$ORDER_PLAYERS++;
			?>
				<li>
				<a href="highscore.php?m=ranking&user=<?php echo urlencode($top['username']) ?>">
				<div class="inner">
					<div class="rank_<?php echo $ORDER_PLAYERS ?>">
					<div class="thumb"><img src="<?php echo get_player_card($top['id']); ?>" width="65" height="115"></div>
					<h2 class="player_name"><?php echo $top['username'] ?>
					<br>
					<span>Skill Total: <?php echo $top['skill_total'] ?></span>
					</h2>
					</div>
				</div>
				</a>
				</li>	
			<?php } ?>
			</ul>
		</div>
		<span class="space"></span>
		<div class="base_body">
			<div class="base_title"><h2 class="t-gold">Top PKers (K/D)</h2></div>
			<ul class="list-group">
				<?php while ($kd = $db->fetch_assoc($top_kd)) {
				$ORDER_PKERS++;
				$ratio = $kd['kills'] + $kd['deaths']; 
				if($kd['deaths'] != 0) {
					$ratio = $kd['kills'] / $kd['deaths'];
				} else {
					$ratio = $kd['kills'];
				}
				?>
					<li>
					<a href="highscore.php?m=ranking&user=<?php echo urlencode($kd['username']) ?>">
					<div class="inner">
						<div class="rank_<?php echo $ORDER_PKERS ?>">
						<div class="thumb"><img src="<?php echo get_player_card($kd['id']); ?>" width="65" height="115"></div>
						<h2 class="player_name"><?php echo $kd['username'] ?> &bull; <span class="kd_ratio">Ratio: <?php echo number_format($ratio, 2) ?></span>
						<br>
						<span>Kills: <?php echo number_format($kd['kills']) ?></span> <span>Deaths: <?php echo number_format($kd['deaths']) ?></span>
						</h2>
						</div>
					</div>
					</a>
					</li>	
				<?php } ?>	
			</ul>
		</div>
		<span class="space"></span>
		<div class="base_body">
			<div class="base_title"><h2 class="t-gold">Top Posters</h2></div>
			<ul class="list-group">
				<?php while ($post = $db->fetch_assoc($top_poster)) {
				$ORDER_POSTERS++;
				?>
					<li>
					<a href="profile.php?id=<?php echo $post['id'] ?>">
					<div class="inner">
						<div class="rank_<?php echo $ORDER_POSTERS ?>">
						<div class="display_favatar"><img src="<?php echo get_avatar($post['id']); ?>" width="30" height="30"></div>
						<h2 class="player_name"><?php echo $post['username'] ?>
						<br>
						<span>Comments: <?php echo $post['num_comments'] ?></span>
						</h2>
						</div>
					</div>
					</a>
					</li>	
				<?php } ?>	
			</ul>
		<div class="frontpage-button">
			<a class="btn btn-rounded-black" href="highscore.php?m=ranking">See More</a>		
		</div>
		</div>
	</div>
</div>