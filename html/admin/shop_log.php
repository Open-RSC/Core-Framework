<?php

/*
 * Copyright (C) 2016 RSCL
 * Author: Imposter.
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'shop_log');

require LUNA_ROOT.'include/common.php';
require LUNA_ROOT.'include/parser.php';

if ($luna_user['g_id'] != LUNA_ADMIN) {
	header("Location: ../backstage/login.php");
    exit;
}
$forum_search = isset($_GET['search_forum']) && strlen($_GET['search_forum']) <= 12 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_GET['search_forum']) ? strtolower($_GET['search_forum']) : null;
$game_search = isset($_GET['search_game']) && strlen($_GET['search_game']) <= 12 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_GET['search_game']) ? strtolower($_GET['search_game']) : null;
if(isset($forum_search)) {
	$shop_log_query = "SELECT package,product_id,price,quantity,creation,forum_name,game_name FROM shop_logs WHERE forum_name = '".$db->escape($forum_search)."' ORDER BY creation DESC";
} else if(isset($game_search)) {
	$shop_log_query = "SELECT package,product_id,price,quantity,creation,forum_name,game_name FROM shop_logs WHERE game_name = '".$db->escape($game_search)."' ORDER BY creation DESC";
} else {
	$shop_log_query = "SELECT package,product_id,price,quantity,creation,forum_name,game_name FROM shop_logs ORDER BY creation DESC";
}
$total_results = $db->num_rows($db->query($shop_log_query));	
$total_pages = ceil($total_results / 20);
$p = (!isset($_GET['p']) || $_GET['p'] <= 1 || $_GET['p'] > $total_pages) ? 1 : intval($_GET['p']);
$fix_curr_page = ($p-1) * 20;
$find_logs = $db->query($shop_log_query . " LIMIT " . $fix_curr_page . " , 20");
$shop_results = $db->num_rows($find_logs);	

require 'header.php';	
?>
<div class="content_wrapper col-sm-10">
		<div class="row title">
			<div class="small-12 columns">
				<h2 class="page-heading">Shop Order Logs</h2>
			</div>
		</div>
		<div class="small-12 columns">
		<table class="table table-bordered">
		<tbody>
		<tr>
			<th>Forum Name: </th>
			<td>
			
				<form method="get" action="shop_log.php">
				<div class="input-wrap">
					<input class="form-control" type="text" maxlength="50" name="search_forum" value="<?php echo ($forum_search ? htmlspecialchars($forum_search) : null) ?>" />
					<span class="input-group-btn">
						<button class="input-button" type="submit" value="Search">Search</button>
					</span>
				</div>
				</form>
			</td>
			<th>Game Name: </th>
			<td>
				<form method="get" action="shop_log.php">
				<div class="input-wrap">
					<input class="form-control" type="text" maxlength="50" name="search_game" value="<?php echo ($game_search ? htmlspecialchars($game_search) : null) ?>" />
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
		echo "<h4><b>" . number_format($total_results) . "</b> orders were found, showing 20 orders per page.</h4>"; 
		if(isset($forum_search)) {
			echo '<ul class="pagination">'.paginate($total_pages, $p, 'shop_log.php?search_forum=' . $forum_search ).'</ul>';
		} else if(isset($game_search)) {
			echo '<ul class="pagination">'.paginate($total_pages, $p, 'shop_log.php?search_game=' . $game_search ).'</ul>';
		} else {
			echo '<ul class="pagination">'.paginate($total_pages, $p, 'shop_log.php?').'</ul>';
		}	
		if($shop_results > 0)
		{	
		?>
		<table class="base-tbl">
			<tr>
				<th rowspan="1" colspan="1">Forum Name</th>
				<th rowspan="1" colspan="1">Game Name</th>
				<th rowspan="1" colspan="1">Package</th>
				<th rowspan="0" colspan="1">Item ID</th>
				<th rowspan="1" colspan="1">Price</th>
				<th rowspan="1" colspan="1">Quantity</th>
				<th rowspan="1" colspan="1">Date</th>
			</tr>
			<tbody role="alert" aria-live="polite" aria-relevant="all">
			<?php
			while($r=$db->fetch_assoc($find_logs)) {
				echo "
					<tr>
						<td><a href='index.php?player_input=".urlencode($r['forum_name'])."&amp;search_type=1'>" .	$r['forum_name'] . "</a></td>
						<td><a href='index.php?player_input=".urlencode($r['game_name'])."&amp;search_type=0'>" . $r['game_name'] . "</a></td>
						<td>" . $r['package'] . "</td>
						<td>" .	($r['product_id'] == 0 ? "NaN" : $r['product_id']) . "</td>
						<td>" .	number_format($r['price']) . "</td>
						<td>" . $r['quantity'] . "</td>
						<td>" . format_time($r['creation']) . "</td>
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