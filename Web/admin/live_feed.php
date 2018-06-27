<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'live_feed_log');

require LUNA_ROOT.'include/common.php';
require LUNA_ROOT.'include/parser.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}
$player_username = isset($_GET['player_username']) && strlen($_GET['player_username']) <= 12 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_GET['player_username']) ? strtolower($_GET['player_username']) : null;
if(isset($player_username)) {
	$feed_query = "SELECT username, message, time FROM " . GAME_BASE . "live_feeds WHERE username = '".$db->escape($player_username)."' ORDER BY time DESC";
	$num_live_feeds = "SELECT COUNT(*) FROM " . GAME_BASE . "live_feeds WHERE username = '".$db->escape($player_username)."'";
} else {
	$feed_query = "SELECT username, message, time FROM " . GAME_BASE . "live_feeds ORDER BY time DESC";
	$num_live_feeds = "SELECT COUNT(*) FROM " . GAME_BASE . "live_feeds";
}
$total_results = $db->result($db->query($num_live_feeds));
		
$total_pages = ceil($total_results / 30);
$p = (!isset($_GET['p']) || $_GET['p'] <= 1 || $_GET['p'] > $total_pages) ? 1 : intval($_GET['p']);
$fix_curr_page = ($p-1) * 30;
$fetch_feeds = $db->query($feed_query . " LIMIT " . $fix_curr_page . " , 30");
$log_results = $db->num_rows($fetch_feeds);

require 'header.php';	
?>
<div class="content_wrapper col-sm-10">
	<form method="get" action="live_feed.php">
		<div class="row title">
			<div class="small-12 columns">
				<h2 class="page-heading">Live Feed Logs</h2>
			</div>
		</div>
		<div class="small-12 columns">
		<table class="table table-bordered">
		<tbody>
		<tr>
			<th>Player: </th>
			<td>
				<div class="input-wrap">
					<input class="form-control" type="text" maxlength="12" name="player_username" value="<?php echo $player_username; ?>" />
					<span class="input-group-btn">
						<button class="input-button" type="submit" value="Search">Search</button>
					</span>
				</div>
			</td>
		</tr>
		</tbody>
		</table>
		<?php
		
		if(isset($player_username)) {
			if($total_pages > 1)
				echo "<h4><b>" .  number_format($total_results) . "</b> feeds were found for <b>". ucwords($player_username) ."</b>, showing 30 feeds per page.</h4>";
				echo '<ul class="pagination">'.paginate($total_pages, $p, '?player_username=' . urlencode($player_username) ).'</ul>';
		} else {
			if($total_pages > 1)
				
				echo "<h4><b>" .  number_format($total_results) . "</b> feeds were found, showing 30 feeds per page.</h4>";
				echo '<ul class="pagination">'.paginate($total_pages, $p, 'live_feed.php?').'</ul>';
		}	
		if($log_results > 0)
		{	
		?>
		<table class="base-tbl">
			<tr>
				<th rowspan="1" colspan="1">Player</th>
				<th rowspan="1" colspan="1">Feed</th>
				<th rowspan="0" colspan="1">Date</th>
			</tr>
			<tbody role="alert" aria-live="polite" aria-relevant="all">
			<?php
				while($cur_feed = $db->fetch_assoc($fetch_feeds)) 
				{
					echo "
						<tr>
							<td><a href='index.php?player_input=".urlencode($cur_feed['username'])."&amp;search_type=0'>" . $cur_feed['username'] . "</a></td>
							<td>" . $cur_feed['message'] . "</td>
							<td>" . format_time($cur_feed['time']) . "</td>
						</tr>
						";
						if($r['extra'] != '') {
							?>
							<tr>
							
								<th class="extra-title"><i class="fa fa-angle-double-up" aria-hidden="true"></i> Message:</th>
								<td class="extra-table" colspan="7"><?php echo $r['extra'] ?> </td>
							</tr>
							<?php
						}
				}
			?>
			</tbody>
		</table>
		<?php } ?>
	</div>
	</form>
</div>
<?php
require 'footer.php';