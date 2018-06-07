<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'trade_log');

require LUNA_ROOT.'include/common.php';
require LUNA_ROOT.'include/parser.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}

$view_trade_id = isset($_GET['view_trade']) ? intval($_GET['view_trade']) : null;

if(isset($view_trade_id)) 
{ 
$trade_fetch_sql = $db->query("SELECT `player1`, `player2`, `player1_items`, `player2_items`, `player1_ip`, `player2_ip`, `time` FROM `" . GAME_BASE . "trade_logs` WHERE id = '".intval($view_trade_id)."'");

if(!$db->num_rows($trade_fetch_sql))
	message_backstage(__('Could not find the trade view for these players and trade id.', 'luna'), false, '404 Not Found');
	
$show_trade = $db->fetch_assoc($trade_fetch_sql);
require 'header.php';	
?>
<div class="content_wrapper col-sm-10">
	<div class="row title">
		<div class="small-12 columns">
			<h2 class="page-heading">Viewing trade (<?php echo  $show_trade['player1']." - ".$show_trade['player2']; ?>)<span class="pull-right"><button onclick="window.history.back()" class="block-top input-button"><i class="fa fa-chevron-left" aria-hidden="true"></i> Go back</button></span></h2>
		</div>
	</div>
	<div class="small-12 columns">
		<div class="col-sm-6">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title"><?php echo $show_trade['player1']. " [IP: ".$show_trade['player1_ip']."]"; ?></h3>
				</div>
				<div class="panel-body">
							<?php
							if(empty($show_trade['player1_items']))
							{
								echo "Traded nothing!";
							} else {
								echo '
								<table class="base-tbl">
								<tbody><tr>
									<th rowspan="0" colspan="1">Image</th>
									<th rowspan="1" colspan="1">Item</th>
									<th rowspan="1" colspan="1">Quantity</th>
								</tr>
								</tbody><tbody role="alert" aria-live="polite" aria-relevant="all">
								';
								$items = explode(",", $show_trade['player1_items']);
								for($i = 0; $i < count($items) - 1; $i ++)
								{
									if(isset($items[$i]))
									{
										$stacked = explode(":", $items[$i]);
										$name_of_item = $db->fetch_assoc($db->query("SELECT `name` FROM `".GAME_BASE."itemdef` WHERE `id` = '" . $stacked[0] . "'"));
										echo "
										<tr>
										<td>
											<img src='../img/items/" . $stacked[0] . ".png' alt='" . $name_of_item['name'].' ('.$stacked[0] . ")' title='" . $name_of_item['name'] . ' ('.$stacked[0] . ")' />
										</td>
										<td>
											".$name_of_item['name']." (ID: ".$stacked[0].")
										</td>
										<td>
											".number_format($stacked[1])."
										</td>
										</tr>
										";
									}
								}
								echo '</tbody>
								</table>';
							}
							?>
						
				</div>
			</div>
		</div>
		<div class="col-sm-6">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title"><?php echo $show_trade['player2']. " [IP: ".$show_trade['player2_ip']."]"; ?></h3>
				</div>
				<div class="panel-body">
							<?php
							if(empty($show_trade['player2_items']))
							{
								echo "Traded nothing!";
							} else {
								echo '
								<table class="base-tbl">
								<tbody><tr>
									<th rowspan="0" colspan="1">Image</th>
									<th rowspan="1" colspan="1">Item</th>
									<th rowspan="1" colspan="1">Quantity</th>
								</tr>
								</tbody><tbody role="alert" aria-live="polite" aria-relevant="all">
								';
								$items = explode(",", $show_trade['player2_items']);
								for($i = 0; $i < count($items) - 1; $i ++)
								{
									if(isset($items[$i]))
									{
										$stacked = explode(":", $items[$i]);
										$name_of_item = $db->fetch_assoc($db->query("SELECT `name` FROM `".GAME_BASE."itemdef` WHERE `id` = '" . $stacked[0] . "'"));
										echo "
										<tr>
										<td>
											<img src='../img/items/" . $stacked[0] . ".png' alt='" . $name_of_item['name'].' ('.$stacked[0] . ")' title='" . $name_of_item['name'] . ' ('.$stacked[0] . ")' />
										</td>
										<td>
											".$name_of_item['name']." (ID: ".$stacked[0].")
										</td>
										<td>
											".number_format($stacked[1])."
										</td>
										</tr>
										";
									}
								}
								echo '</tbody>
								</table>';
							}
							?>
				</div>
			</div>
		</div>
	</div>
</div>
<?php
} else {
$character = isset($_GET['character']) && strlen($_GET['character']) <= 12 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_GET['character']) ? strtolower($_GET['character']) : null;
$ip_adress = (isset($_GET['ip'])  && strlen($_GET['ip']) <= 20 ? trim($_GET['ip']) : null);
$item = (isset($_GET["item"]) ? intval($_GET["item"]) : null);

$time_start = (isset($_GET["search_time_start"]) ? $_GET["search_time_start"] : null);
$time_end = (isset($_GET["search_time_end"]) ? $_GET["search_time_end"] : null);

$results_per_page = (isset($_GET["results_per_page"]) == null ? 50 : $_GET["results_per_page"]);

$character_query = ($character != null ? " WHERE (`player1` LIKE '".$character."' OR `player2` LIKE '".$character."')" : "");
$ip_query = ($ip_adress != null ? "".($character != null ? "AND" : " WHERE")." (`player1_ip` LIKE '".$ip_adress."' OR `player2_ip` LIKE '".$ip_adress."')" : "");
$item_query = ($item != null ? " ".(($character != null || $ip_query) != null ? "AND" : " WHERE")." (`player1_items` LIKE '%".$item."%' OR `player2_items` LIKE '%".$item."%')" : "");

if($time_start && !$time_end) {
	$time_start_query = ($time_start != null ? "".(($character != null || $ip_query != null || $item != null) ? "AND" : " WHERE")." (`time` > '".strtotime($time_start)."')" : "");
} elseif(!$time_start && $time_end) {
	$time_end_query = ($time_end != null ? "".(($character != null || $ip_query != null || $item != null) ? "AND" : " WHERE")." (`time` < '".strtotime($time_end)."')" : "");
} else {
	$time_start_query = ($time_start != null ? "".(($character != null || $ip_query != null || $item) != null ? "AND" : " WHERE")." (`time` > '".strtotime($time_start)."'" : "");
	$time_end_query = ($time_end != null ? " AND `time` < '".strtotime($time_end)."')" : "");
}

$query = $db->query("SELECT `id`, `player1`, `player2`, `time` FROM `" . GAME_BASE . "trade_logs` ".$character_query." ".$ip_query." ".$item_query." ".$time_start_query." ".$time_end_query." ORDER BY `time` DESC LIMIT 0, ".$results_per_page."");
$total_query_count = $db->num_rows($query);

$page = isset($_GET['page']) ? (($_GET['page']) - 1) : 0;
$total_pages = ceil($total_query_count / $results_per_page);
$page_start = $page * $results_per_page;

require 'header.php';	
?>
<div class="content_wrapper col-sm-10">
	<form method="get" action="trades.php">
		<div class="row title">
			<div class="small-12 columns">
				<h2 class="page-heading">Trade logs <span class="pull-right"><button class="block-top input-button" type="submit" tabindex="1">Search</button></span></h2>
			</div>
		</div>
		<div class="small-12 columns">
		<table class="table table-bordered">
		<tbody>
		<tr>
			<th>Character:</th>
			<td><input type="text" class="form-control" name="character" maxlength="12" value="<?php echo $character; ?>" tabindex="1"></td>
			<th>IP:</th>
			<td><input type="text" class="form-control" name="ip" maxlength="25" value="<?php echo $ip_adress; ?>" tabindex="2"></td>
			<th>Item ID:</th>
			<td><input type="text" class="form-control" name="item" maxlength="255" value="<?php echo $item; ?>" tabindex="3"></td>
		</tr>
		<tr>
			<th>Date range:</th>
			<td><input type="text" class="form-control datepicker" name="search_time_start" placeholder="(mm/dd/yy)" maxlength="50" value="<?php echo $time_start ?>" tabindex="4"></td>
			<th style="text-align:center;">to</th>
			<td><input type="text" class="form-control datepicker" name="search_time_end" placeholder="(mm/dd/yy)" maxlength="50" value="<?php echo $time_end; ?>" tabindex="5"></td>
			<th>Limit page:</th>
			<td><input type="text" class="form-control" name="results_per_page" maxlength="40" value="<?php echo $results_per_page; ?>" tabindex="6"></td>
		</tr>
		</tbody>
		</table>
		<?php
		if($total_query_count > 0)
		{	
		?>
		<table class="base-tbl">
			<tr>
				<th rowspan="1" colspan="1">Player 1</th>
				<th rowspan="1" colspan="1">Player 2</th>
				<th rowspan="0" colspan="1">Date</th>
				<th rowspan="0" colspan="1"></th>
			</tr>
			<tbody role="alert" aria-live="polite" aria-relevant="all">
			<?php
				while($trade_list = $db->fetch_assoc($query)) 
				{
					echo "
						<tr>
							<td><a href='index.php?player_input=".urlencode($trade_list['player1'])."&amp;search_type=0'>" . $trade_list['player1'] . "</a></td>
							<td><a href='index.php?player_input=".urlencode($trade_list['player2'])."&amp;search_type=0'>" . $trade_list['player2'] . "</a></td>
							<td>" . format_time($trade_list['time']) . "</td>
							<td><a href='trades.php?view_trade=".$trade_list['id']."'>View trade log</td>
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
}
require 'footer.php';