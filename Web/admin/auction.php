<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'auction_log');

require LUNA_ROOT.'include/common.php';
require LUNA_ROOT.'include/parser.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}
$username_seller = isset($_GET['search']) && strlen($_GET['search']) <= 12 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_GET['search']) ? strtolower($_GET['search']) : null;
$username_buyer = isset($_GET['search_buyer']) && strlen($_GET['search_buyer']) <= 12 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_GET['search_buyer']) ? strtolower($_GET['search_buyer']) : null;
$item_name_search = isset($_GET['item_name_search']) && strlen($_GET['item_name_search']) <= 50 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_GET['item_name_search']) ? strtolower($_GET['item_name_search']) : null;
$results_per_page = (isset($_GET["results_per_page"]) == null ? 50 : $_GET["results_per_page"]);

$seller_query_addition = ($username_seller != null? "match (". GAME_BASE ."auctions.seller_username) against ('".$username_seller."')" : "1=1");
$buyer_query_addition = ($username_buyer != null? "match (". GAME_BASE ."auctions.buyer_info) against ('".$username_buyer."')" : "1=1");
$item_name_query_addition = ($item_name_search != null ? "match (". GAME_BASE ."itemdef.name) against ('".$item_name_search."')" : "1=1");

$fetch_auctions = "SELECT ". GAME_BASE ."itemdef.name, ". GAME_BASE ."auctions.`auctionID`,". GAME_BASE ."auctions.`sold-out`, ". GAME_BASE ."auctions.itemID, ". GAME_BASE ."auctions.amount_left, ". GAME_BASE ."auctions.amount, 
". GAME_BASE ."auctions.price, ". GAME_BASE ."auctions.seller_username, ". GAME_BASE ."auctions.buyer_info, ". GAME_BASE ."auctions.was_cancel, ". GAME_BASE ."auctions.time FROM ". GAME_BASE ."auctions 
INNER JOIN ". GAME_BASE ."itemdef ON ". GAME_BASE ."itemdef.id = ". GAME_BASE ."auctions.itemID WHERE ".$seller_query_addition." AND ".$buyer_query_addition." AND ".$item_name_query_addition." ORDER BY ". GAME_BASE ."auctions.time DESC";

$fetch_auctions_p = "SELECT COUNT(*) FROM ". GAME_BASE ."auctions INNER JOIN ". GAME_BASE ."itemdef ON ". GAME_BASE ."itemdef.id = ". GAME_BASE ."auctions.itemID WHERE ".$seller_query_addition." AND ".$buyer_query_addition." AND ".$item_name_query_addition."";


$total_results = $db->result($db->query($fetch_auctions_p));			
$total_pages = ceil($total_results / $results_per_page);
$p = (!isset($_GET['p']) || $_GET['p'] <= 1 || $_GET['p'] > $total_pages) ? 1 : intval($_GET['p']);
$fix_curr_page = ($p-1) * $results_per_page;
$find_ah = $db->query($fetch_auctions . " LIMIT " . $fix_curr_page . " , ".$results_per_page."");
$ah_results = $db->num_rows($find_ah);

require 'header.php';	
?>
<div class="content_wrapper col-sm-10">
	<form method="get" action="auction.php">
		<div class="row title">
			<div class="small-12 columns">
				<h2 class="page-heading">Active Auctions Logs<span class="pull-right"><button class="block-top input-button" type="submit" tabindex="1">Search</button></span></h2>
		</div>
		<div class="small-12 columns">
		<table class="table table-bordered">
			<tbody><tr>
				<th>Seller:</th>
				<td><input type="text" class="form-control" name="search" maxlength="12" value="<?php echo (isset($_GET['search']) ? htmlspecialchars($_GET['search']) : null) ?>" tabindex="2"></td>
				<th>Buyer:</</th>
				<td><input type="text" class="form-control" name="search_buyer" maxlength="12" value="<?php echo (isset($_GET['search_buyer']) ? htmlspecialchars($_GET['search_buyer']) : null) ?>" tabindex="3"></td>
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
		if(isset($username_seller)) {
			echo '<ul class="pagination">'.paginate($total_pages, $p, 'auction.php?search=' . $username_seller.'&amp;results_per_page='.$results_per_page).'</ul>';
		} else if(isset($username_buyer)) {
			echo '<ul class="pagination">'.paginate($total_pages, $p, 'auction.php?search_buyer=' . $username_buyer.'&amp;results_per_page='.$results_per_page).'</ul>';
		} else if(isset($item_name_search)) {
			echo '<ul class="pagination">'.paginate($total_pages, $p, 'auction.php?item_name_search=' . $item_name_search.'&amp;results_per_page='.$results_per_page).'</ul>';
		}  else {
			echo '<ul class="pagination">'.paginate($total_pages, $p, 'auction.php?results_per_page='.$results_per_page).'</ul>';
		}
		
		if($ah_results > 0)
		{	
		?>
		<table class="base-tbl">
			<tr>
				<th rowspan="0" colspan="1">Picture</th>
				<th rowspan="1" colspan="1">Item (ID)</th>
				<th rowspan="1" colspan="1">Amount / Left</th>
				<th rowspan="1" colspan="1">Price</th>
				<th rowspan="1" colspan="1">Seller</th>
				<th rowspan="1" colspan="1">Buyer info</th>
				<th rowspan="1" colspan="1">Active</th>
				<th rowspan="1" colspan="1">Canceled</th>
				<th rowspan="1" colspan="1">Time</th>
			</tr>
			<tbody role="alert" aria-live="polite" aria-relevant="all">
			<?php
				while($r = $db->fetch_assoc($find_ah)) {
					$sold = ($r['sold-out'] == 0 ? "<font style='color:green;'>YES</font>" : "<strike><font style='color:red;'>NO</font></strike>");
					$canceled = ($r['was_cancel'] == 0 ? "<font style='color:green;'>NO</font>" : "<strike><font style='color:orange;'>YES</font></strike>");
					$buyer_info = explode(',',$r['buyer_info']);
					$string = "";
					if($r['auctionID'] > 5369) {
						foreach($buyer_info as $arr) {
							$something = explode(':', $arr);
							if($r['buyer_info'] != '') {
								$string .= "[" . format_time(str_replace("[", "", $something[0])) . ", " . $something[1] . ", " . $something[2] . "<br />";
							} else {
								$string = "N/A";
							}
						}
					}
					echo"
						<tr>
							<td><img src='../img/items/" . $r['itemID'] . ".png' alt='x' /></td>
						
							<td>" . $r['name'] . " (".$r['itemID'].")</td>
						
							<td>" . $r['amount'] . " / ".$r['amount_left']."</td>
						
							<td>" . number_format($r['price']) . "</td>
							
							<td>" . $r['seller_username'] . "</td>
							
							<td>" .  ($r['auctionID'] > 5369 ? $string : $r['buyer_info']) ."</td>
							
							<td>" . $sold . "</td>
							
							<td>" . $canceled . "</td>
							
							<td>" . date("Y-m-d H:i:s", $r['time']). "</td>
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