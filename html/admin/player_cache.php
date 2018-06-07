<?php

/*
 * Copyright (C) 2016 RSCL
 * Author: Imposter.
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'player_cache');

require LUNA_ROOT.'include/common.php';
require LUNA_ROOT.'include/parser.php';

if ($luna_user['g_id'] != LUNA_ADMIN) {
	header("Location: ../backstage/login.php");
    exit;
}
$player_search = isset($_GET['search_player']) && strlen($_GET['search_player']) <= 12 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_GET['search_player']) ? strtolower($_GET['search_player']) : null;
$key_search = isset($_GET['search_key_cache']) && strlen($_GET['search_key_cache']) <= 12 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_GET['search_key_cache']) ? strtolower($_GET['search_key_cache']) : null;
if(isset($player_search)) {
	$cache_query = "SELECT p.username, c.type, c.key, c.value FROM ".GAME_BASE."player_cache AS c LEFT JOIN ".GAME_BASE."players AS p ON c.playerID = p.id WHERE p.username = '".$db->escape($player_search)."'";
} else if(isset($key_search)) {
	$cache_query = "SELECT p.username, c.type, c.key, c.value FROM ".GAME_BASE."player_cache AS c LEFT JOIN ".GAME_BASE."players AS p ON c.playerID = p.id WHERE c.key LIKE '".$db->escape($key_search)."%'";
} else {
	$cache_query = "SELECT p.username, c.type, c.key, c.value FROM ".GAME_BASE."player_cache AS c LEFT JOIN ".GAME_BASE."players AS p ON c.playerID = p.id";
}
$total_results = $db->num_rows($db->query($cache_query));	
$total_pages = ceil($total_results / 25);
$p = (!isset($_GET['p']) || $_GET['p'] <= 1 || $_GET['p'] > $total_pages) ? 1 : intval($_GET['p']);
$fix_curr_page = ($p-1) * 25;
$find_logs = $db->query($cache_query . " LIMIT " . $fix_curr_page . " , 25");
$cache_results = $db->num_rows($find_logs);	

require 'header.php';	
?>
<div class="content_wrapper col-sm-10">
		<div class="row title">
			<div class="small-12 columns">
				<h2 class="page-heading">Player Cache Logs</h2>
			</div>
		</div>
		<div class="small-12 columns">
		<table class="table table-bordered">
		<tbody>
		<tr>
			<th>Player Name: </th>
			<td>
			
				<form method="get" action="player_cache.php">
				<div class="input-wrap">
					<input class="form-control" type="text" maxlength="50" name="search_player" value="<?php echo ($player_search ? htmlspecialchars($player_search) : null) ?>" />
					<span class="input-group-btn">
						<button class="input-button" type="submit" value="Search">Search</button>
					</span>
				</div>
				</form>
			</td>
			<th>Player Cache Key: </th>
			<td>
				<form method="get" action="player_cache.php">
				<div class="input-wrap">
					<input class="form-control" type="text" maxlength="50" name="search_key_cache" value="<?php echo ($key_search ? htmlspecialchars($key_search) : null) ?>" />
					<span class="input-group-btn">
						<button class="input-button" type="submit" value="Search">Search</button>
					</span>
				</div>
				</form>
			</td>
		</tr>
		</tbody>
		</table>
		<?php
		echo "<h4><b>" . number_format($total_results) . "</b> orders were found, showing 25 orders per page.</h4>"; 
		if(isset($player_search)) {
			echo '<ul class="pagination">'.paginate($total_pages, $p, 'player_cache.php?search_player=' . $forum_search ).'</ul>';
		} else if(isset($key_search)) {
			echo '<ul class="pagination">'.paginate($total_pages, $p, 'player_cache.php?search_key_cache=' . $game_search ).'</ul>';
		} else {
			echo '<ul class="pagination">'.paginate($total_pages, $p, 'player_cache.php?').'</ul>';
		}	
		if($cache_results > 0)
		{	
		?>
		<table class="base-tbl">
			<tr>
				<th rowspan="1" colspan="1">Player</th>
				<th rowspan="1" colspan="1">Cache Type</th>
				<th rowspan="1" colspan="1">Cache Key</th>
				<th rowspan="0" colspan="1">Cache Value</th>
			</tr>
			<tbody role="alert" aria-live="polite" aria-relevant="all">
			<?php
			while($r=$db->fetch_assoc($find_logs)) {
				if($r['type'] == 0) {
					$cache_type = "(Integer)";
				} else if($r['type'] == 1) {
					$cache_type = "(String)";
				} else if($r['type'] == 2) {
					$cache_type = "(Boolean)";
				} else if($r['type'] == 3) {
					$cache_type = "(Long)";
				}
				if($r['key'] == 'bank_pin' && $luna_user['group_id'] != 1) {
					continue;
				}					
				echo "
					<tr>
						<td><a href='index.php?player_input=".urlencode($r['username'])."&amp;search_type=1'>" .	$r['username'] . "</a></td>
						<td>" . $r['type'] . " " . $cache_type . "</td>
						<td>" . $r['key'] . "</td>
						<td>" .	$r['value'] . "</td>
					</tr>
				";
			}
			?>
			</tbody>
		</table>
		<?php } ?>
	</div>
</div>
<?php
require 'footer.php';