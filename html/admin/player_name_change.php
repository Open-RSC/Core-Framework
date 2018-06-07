<?php

/*
 * Copyright (C) 2016 RSCL
 * Author: Imposter.
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'player_name_change');

require LUNA_ROOT.'include/common.php';
require LUNA_ROOT.'include/parser.php';

if ($luna_user['g_id'] != LUNA_ADMIN) {
	header("Location: ../backstage/login.php");
    exit;
}
$player_search = isset($_GET['search_player']) && strlen($_GET['search_player']) <= 12 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_GET['search_player']) ? strtolower($_GET['search_player']) : null;
$forum_search = isset($_GET['search_forum']) && strlen($_GET['search_forum']) <= 12 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_GET['search_forum']) ? strtolower($_GET['search_forum']) : null;
if(isset($player_search)) {
	$name_query = "SELECT p.username, u.username AS 'fuser', n.old_name, n.new_name, n.date FROM ".GAME_BASE."name_changes AS n LEFT JOIN ".GAME_BASE."players AS p ON n.playerID = p.id LEFT JOIN users AS u ON n.owner = u.id WHERE p.username = '".$db->escape($player_search)."' ORDER BY n.date DESC";
} else if(isset($forum_search)) {
	$name_query = "SELECT p.username, u.username AS 'fuser', n.old_name, n.new_name, n.date FROM ".GAME_BASE."name_changes AS n LEFT JOIN ".GAME_BASE."players AS p ON n.playerID = p.id LEFT JOIN users AS u ON n.owner = u.id WHERE u.username = '".$db->escape($forum_search)."' ORDER BY n.date DESC";
} else {
	$name_query = "SELECT p.username, u.username AS 'fuser', n.old_name, n.new_name, n.date FROM ".GAME_BASE."name_changes AS n LEFT JOIN ".GAME_BASE."players AS p ON n.playerID = p.id LEFT JOIN users AS u ON n.owner = u.id ORDER BY n.date DESC";
}
$total_results = $db->num_rows($db->query($name_query));	
$total_pages = ceil($total_results / 25);
$p = (!isset($_GET['p']) || $_GET['p'] <= 1 || $_GET['p'] > $total_pages) ? 1 : intval($_GET['p']);
$fix_curr_page = ($p-1) * 25;
$find_logs = $db->query($name_query . " LIMIT " . $fix_curr_page . " , 25");
$name_results = $db->num_rows($find_logs);	

require 'header.php';	
?>
<div class="content_wrapper col-sm-10">
		<div class="row title">
			<div class="small-12 columns">
				<h2 class="page-heading">Player Name Change Logs</h2>
			</div>
		</div>
		<div class="small-12 columns">
		<table class="table table-bordered">
		<tbody>
		<tr>
			<th>Current Player Name: </th>
			<td>
			
				<form method="get" action="player_name_change.php">
				<div class="input-wrap">
					<input class="form-control" type="text" maxlength="50" name="search_player" value="<?php echo ($player_search ? htmlspecialchars($player_search) : null) ?>" />
					<span class="input-group-btn">
						<button class="input-button" type="submit" value="Search">Search</button>
					</span>
				</div>
				</form>
			</td>
			<th>Find ALL name changes for Forum Name: </th>
			<td>
				<form method="get" action="player_name_change.php">
				<div class="input-wrap">
					<input class="form-control" type="text" maxlength="50" name="search_forum" value="<?php echo ($forum_search ? htmlspecialchars($forum_search) : null) ?>" />
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
			echo '<ul class="pagination">'.paginate($total_pages, $p, 'player_name_change.php?search_player=' . $forum_search ).'</ul>';
		} else if(isset($key_search)) {
			echo '<ul class="pagination">'.paginate($total_pages, $p, 'player_name_change.php?search_forum=' . $game_search ).'</ul>';
		} else {
			echo '<ul class="pagination">'.paginate($total_pages, $p, 'player_name_change.php?').'</ul>';
		}	
		if($name_results > 0)
		{	
		?>
		<table class="base-tbl">
			<tr>
				<th rowspan="1" colspan="1">Current Player Name</th>
				<th rowspan="1" colspan="1">Forum User</th>
				<th rowspan="1" colspan="1">Old Name</th>
				<th rowspan="0" colspan="1">New Name</th>
				<th rowspan="0" colspan="1">Date of change</th>
			</tr>
			<tbody role="alert" aria-live="polite" aria-relevant="all">
			<?php
			while($r=$db->fetch_assoc($find_logs)) {				
				echo "
					<tr>
						<td><a href='index.php?player_input=".urlencode($r['username'])."&amp;search_type=1'>" .	$r['username'] . "</a></td>
						<td>" . $r['fuser'] . " " . $cache_type . "</td>
						<td>" . $r['old_name'] . "</td>
						<td>" .	$r['new_name'] . "</td>
						<td>" .	format_time($r['date']) . "</td>
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