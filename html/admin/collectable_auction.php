<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'collectable_log');

require LUNA_ROOT.'include/common.php';
require LUNA_ROOT.'include/parser.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}
$username = isset($_GET['search']) && strlen($_GET['search']) <= 12 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_GET['search']) ? strtolower($_GET['search']) : null;
$item_name_search = isset($_GET['item_name_search']) && strlen($_GET['item_name_search']) <= 50 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_GET['item_name_search']) ? strtolower($_GET['item_name_search']) : null;
$results_per_page = (isset($_GET["results_per_page"]) == null ? 50 : $_GET["results_per_page"]);

$seller_query_addition = ($username != null? "(". GAME_BASE ."players.username) LIKE  '".$username."%'" : "1=1");
$item_name_query_addition = ($item_name_search != null ? "match (". GAME_BASE ."itemdef.name) against ('".$item_name_search."')" : "1=1");

$fetch_auctions = "SELECT ". GAME_BASE ."itemdef.name AS 'auctionItemName', ". GAME_BASE ."players.username AS 'ggname', ". GAME_BASE ."players.id, ". GAME_BASE ."expired_auctions.item_id, 
". GAME_BASE ."expired_auctions.item_amount, ". GAME_BASE ."expired_auctions.explanation, ". GAME_BASE ."expired_auctions.time, ". GAME_BASE ."expired_auctions.claim_time, ". GAME_BASE ."expired_auctions.claimed 
FROM ". GAME_BASE ."expired_auctions INNER JOIN ". GAME_BASE ."itemdef ON ". GAME_BASE ."itemdef.id = ". GAME_BASE ."expired_auctions.item_id 
JOIN ". GAME_BASE ."players ON ". GAME_BASE ."players.id = ". GAME_BASE ."expired_auctions.playerID WHERE ".$seller_query_addition." AND ".$item_name_query_addition." ORDER BY ". GAME_BASE ."expired_auctions.time DESC";

$fetch_auctions_p = "SELECT COUNT(*) FROM ". GAME_BASE ."expired_auctions INNER JOIN ". GAME_BASE ."itemdef ON ". GAME_BASE ."itemdef.id = ". GAME_BASE ."expired_auctions.item_id INNER JOIN ". GAME_BASE ."players ON ". GAME_BASE ."players.id = ". GAME_BASE ."expired_auctions.playerID WHERE ".$seller_query_addition." AND ".$item_name_query_addition."";

$total_results = $db->result($db->query($fetch_auctions_p));			
$total_pages = ceil($total_results / $results_per_page);
$p = (!isset($_GET['p']) || $_GET['p'] <= 1 || $_GET['p'] > $total_pages) ? 1 : intval($_GET['p']);
$fix_curr_page = ($p-1) * $results_per_page;
$find_ah = $db->query($fetch_auctions . " LIMIT " . $fix_curr_page . " , ".$results_per_page."");
$ah_results = $db->num_rows($find_ah);

require 'header.php';	
?>
<div class="content_wrapper col-sm-10">
	<form method="get" action="collectable_auction.php">
		<div class="row title">
			<div class="small-12 columns">
				<h2 class="page-heading">Active Auctions Logs<span class="pull-right"><button class="block-top input-button" type="submit" tabindex="1">Search</button></span></h2>
		</div>
		<div class="small-12 columns">
		<table class="table table-bordered">
			<tbody><tr>
				<th>User:</th>
				<td><input type="text" class="form-control" name="search" maxlength="12" value="<?php echo (isset($_GET['search']) ? htmlspecialchars($_GET['search']) : null) ?>" tabindex="2"></td>
				<th>Item Name:</</th>
				<td><input type="text" class="form-control" name="item_name_search" maxlength="50" value="<?php echo $item_name_search; ?>" tabindex="3"></td>
			</tr>
			<tr>
				<th>Results per page:</th>
				<td><input type="text" class="form-control" name="results_per_page" maxlength="50" value="<?php echo $results_per_page;?>" tabindex="6"></td>
			</tr>
			</tbody>
		</table>
		<?php
		if(isset($username)) {
			echo '<ul class="pagination">'.paginate($total_pages, $p, 'collectable_auction.php?search=' . $username.'&amp;results_per_page='.$results_per_page).'</ul>';
		} else if(isset($item_name_search)) {
			echo '<ul class="pagination">'.paginate($total_pages, $p, 'collectable_auction.php?item_name_search=' . $item_name_search.'&amp;results_per_page='.$results_per_page).'</ul>';
		}  else {
			echo '<ul class="pagination">'.paginate($total_pages, $p, 'collectable_auction.php?results_per_page='.$results_per_page).'</ul>';
		}
		
		if($ah_results > 0)
		{	
		?>
		<table class="base-tbl">
			<tr>
				<th rowspan="0" colspan="1">Picture</th>
				<th rowspan="1" colspan="1">Item (ID)</th>
				<th rowspan="1" colspan="1">Amount</th>
				<th rowspan="1" colspan="1">User</th>
				<th rowspan="1" colspan="1">Explanation</th>
				<th rowspan="1" colspan="1">Claimed</th>
				<th rowspan="1" colspan="1">Time</th>
				<th rowspan="1" colspan="1">Claimed Time</th>
			</tr>
			<tbody role="alert" aria-live="polite" aria-relevant="all">
			<?php
			while($r = $db->fetch_assoc($find_ah)) {
				$sold = ($r['claimed'] == 1 ? "<font style='color:green;'>YES</font>" : "<font style='color:red;'>NO</font>");
				//$buyer_info = explode(",",$r['buyer_info']);
				echo"
					<tr>
						<td><img src='../img/items/" . $r['item_id'] . ".png' alt='x' /></td>
					
						<td>" . $r['auctionItemName'] . " (".$r['item_id'].")</td>
					
						<td>" . number_format($r['item_amount']) ."</td>
						
						<td>" . $r['ggname'] . "</td>
						
						<td>" . $r['explanation'] . "</td>
						
						<td>" . $sold . "</td>
						
						<td>" . date("Y-m-d H:i:s", $r['time']). "</td>
						
						<td>" . ($r['claimed'] == 1 ? date("Y-m-d H:i:s", $r['claim_time'] / 1000) : "N/A"). "</td>
					</tr>
					";
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