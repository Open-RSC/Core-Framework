<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'server_totals');

require LUNA_ROOT.'include/common.php';

if ($luna_user['g_id'] != LUNA_ADMIN) {
	header("Location: ../backstage/login.php");
    exit;
}

function formTotals($id, $mode = null) 
{
	global $db;
	$query1 = $db->query("SELECT " . GAME_BASE . "invitems.id, SUM(" . GAME_BASE . "invitems.amount) AS i_am FROM " . GAME_BASE . "invitems INNER JOIN " . GAME_BASE . "players ON " . GAME_BASE . "invitems.playerID = " . GAME_BASE . "players.id WHERE " . GAME_BASE . "invitems.id = '" . $db->escape($id) ."' AND " . GAME_BASE . "players.banned = '0'");
	$query2 = $db->query("SELECT " . GAME_BASE . "bank.id, SUM(" . GAME_BASE . "bank.amount) AS b_am FROM " . GAME_BASE . "bank INNER JOIN " . GAME_BASE . "players ON " . GAME_BASE . "bank.playerID = " . GAME_BASE . "players.id WHERE " . GAME_BASE . "bank.id = '" . $db->escape($id) . "' AND " . GAME_BASE . "players.banned = '0'");
	$invtotal = 0;
	$banktotal = 0;
	while($t = $db->fetch_assoc($query1))
	{
		if($t['id'] == $id)
		{
			$invtotal = $invtotal + $t['i_am'];
		}
	}
	while($r = $db->fetch_assoc($query2))
	{
		if($r['id'] == $id)
		{
			$banktotal = $banktotal + $r['b_am'];
		}
	}
	if(isset($mode))
	{
		if($mode == 1)
		{
			return $invtotal;
		} else {
			return $banktotal;
		}
	} else {
		return ($invtotal + $banktotal);
	}
}

$cache_file = LUNA_ROOT . "cache/cache_items.php";
$default_string = "10\r\n575\r\n576\r\n577\r\n578\r\n579\r\n580\r\n581\r\n81\r\n971\r\n677\r\n1156\r\n1289";
$find_defaults = isset($_POST['set_default']) && preg_match("/^[0-9;]+?$/i", $_POST['set_default']) ? $_POST['set_default'] : null;
if(!file_exists($cache_file) || $_POST['set_default'] || $_POST['sdf']) 
{
	$createCache = @fopen($cache_file, 'wb');
	if (!$createCache)
		error('Unable to write cache file '.luna_htmlspecialchars($cache_file).' to cache directory. Please make sure PHP has write access to the directory \''.luna_htmlspecialchars(LUNA_CACHE_DIR).'\'', __FILE__, __LINE__);

	flock($createCache, LOCK_EX);
	ftruncate($createCache, 0);
	
	if(isset($find_defaults)) {
		$fix = str_replace(";", "\r\n", $find_defaults);
		fwrite($createCache, $fix);
	} else {
		fwrite($createCache, $default_string);
	}
	
	flock($createCache, LOCK_UN);
	fclose($createCache);
}
$item_counts = array_map('intval', file($cache_file));

require 'header.php';	
$lookup_id = isset($_GET['lookup']) && is_numeric($_GET['lookup']) && $_GET['lookup'] > 0 && $_GET['lookup'] < 2127 ? $_GET['lookup'] : null;
?>
<div class="content_wrapper col-sm-10">
	<form method="POST" action="totals.php">
		<div class="row title">
			<div class="small-12 columns">
				<h2 class="page-heading">Server Totals</h2>
			</div>
		</div>
		<div class="small-12 columns">
		<table class="table table-bordered">
		<tbody>
		<tr>
			<th>Search items: </th>
			<td>
				<div class="input-wrap">
					<input class="form-control" type="text" name="set_default" size="50" maxlength="255" value="<?php for($i = 0; $i < count($item_counts); $i++) { echo $item_counts[$i] . ";"; }?>" />
					<span class="input-group-btn">
						<button class="input-button" type="submit" name="sdf" value="Set Defaults">Search</button>
					</span>
				</div>
			</td>
		</tr>
		</tbody>
		</table>
<?php
if(isset($lookup_id)) 
{
	$find_inv = $db->query("SELECT SUM(" . GAME_BASE . "invitems.amount) AS i_amt FROM " . GAME_BASE . "invitems LEFT JOIN " . GAME_BASE . "players ON " . GAME_BASE . "invitems.playerID = " . GAME_BASE . "players.id WHERE " . GAME_BASE . "invitems.id = '" . $db->escape($lookup_id) . "' AND " . GAME_BASE . "players.banned = '0'");
	$find_bank = $db->query("SELECT SUM(" . GAME_BASE . "bank.amount) AS b_amt FROM " . GAME_BASE . "bank LEFT JOIN " . GAME_BASE . "players ON " . GAME_BASE . "bank.playerID = " . GAME_BASE . "players.id WHERE " . GAME_BASE . "bank.id = '" . $db->escape($lookup_id) . "' AND " . GAME_BASE . "players.banned = '0'");
	
	$inventory_query = $db->query("SELECT SUM(" . GAME_BASE . "invitems.amount) AS amount, " . GAME_BASE . "players.username, " . GAME_BASE . "players.creation_ip FROM " . GAME_BASE . "invitems LEFT JOIN " . GAME_BASE . "players ON " . GAME_BASE . "invitems.playerID = " . GAME_BASE . "players.id WHERE " . GAME_BASE . "invitems.id = '" . $db->escape($lookup_id) . "' AND " . GAME_BASE . "players.banned >= 0 GROUP BY " . GAME_BASE . "invitems.playerID ORDER BY " . GAME_BASE . "invitems.amount DESC");
	$bank_query = $db->query("SELECT SUM(" . GAME_BASE . "bank.amount) AS amount, " . GAME_BASE . "players.username, " . GAME_BASE . "players.creation_ip FROM " . GAME_BASE . "bank LEFT JOIN " . GAME_BASE . "players ON " . GAME_BASE . "bank.playerID = " . GAME_BASE . "players.id WHERE " . GAME_BASE . "bank.id = '" . $db->escape($lookup_id) . "' AND " . GAME_BASE . "players.banned >= 0 GROUP BY " . GAME_BASE . "bank.playerID ORDER BY " . GAME_BASE . "bank.amount  DESC");
	
	$grab_name = $db->fetch_assoc($db->query("SELECT name, id FROM " . GAME_BASE . "itemdef WHERE id = '" . $db->escape($lookup_id) . "'"));
	$grab_i_lookup = $db->fetch_assoc($find_inv);
	$grab_b_lookup = $db->fetch_assoc($find_bank);
	
	$grab_overall_total = ($grab_i_lookup['i_amt'] + $grab_b_lookup['b_amt']);
	$grab_total_inv = $grab_i_lookup['i_amt'];
	$grab_bank_total = $grab_b_lookup['b_amt'];
	
	$in_bank_calc = ($grab_bank_total == 0 || $grab_overall_total == 0) ? 0 : round(($grab_bank_total / $grab_overall_total) * 100, 2);
	$in_inv_calc  = ($grab_total_inv == 0 || $grab_overall_total == 0) ? 0 : round(($grab_total_inv / $grab_overall_total) * 100, 2);
	
	?>
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title">Item Results for <?php echo $grab_name['name'].'&nbsp;('.$grab_name['id'].')'?></h3>
		</div>
		<div class="panel-body">
			<table class="base-tbl">
				<tr>
					<th rowspan="0" colspan="1"><span class="indicator">Existing Items</span></th>
					<th rowspan="1" colspan="1"><span class="indicator">Total</span></th>
					<th rowspan="1" colspan="1"><span class="indicator">Percentage</span></th>
				</tr>
				<tbody role="alert" aria-live="polite" aria-relevant="all">
				<tr>
					<td>Amount In Inventories</td>
					<td><?php echo number_format($grab_total_inv); ?></td>
					<td><?php echo $in_inv_calc; ?>%</td>
				</tr>
				<tr>
					<td>Amount In Banks</td>
					<td><?php echo number_format($grab_bank_total); ?></td>
					<td><?php echo $in_bank_calc; ?>%</td>
				</tr>
				<tr>
					<td>&nbsp;=</td>
					<td><?php echo number_format($grab_overall_total); ?></td>
					<td>100%</td>
				</tr>
				</tbody>
			</table>
		</div>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title"><a data-toggle="collapse" href="#e_inv_block">Inventory Placements <i class="fa fa-chevron-down" aria-hidden="true"></i></a></h3>
		</div>
		<div id="e_inv_block" class="panel-collapse collapse">
			<div class="panel-body">
			<?php
			if($db->num_rows($inventory_query) > 0) {
			?>
				<table class="base-tbl">
				<tr>
					<th rowspan="0" colspan="1">Order</th>
					<th rowspan="0" colspan="1">Character Name</th>
					<th rowspan="1" colspan="1">Registration IP</th>
					<th rowspan="1" colspan="1">Amount Held</th>
				</tr>
				<tbody role="alert" aria-live="polite" aria-relevant="all">
				<?php
				$i=1;
				while($disp_inv = $db->fetch_assoc($inventory_query)) 
				{
				?>
					<tr>
						<td><?php echo $i; ?></td>
						<td><a href="index.php?player_input=<?php echo urlencode($disp_inv['username']); ?>&amp;search_type=0"><?php echo ucwords($disp_inv['username']); ?></a></td>
						<td><a href="index.php?player_input=<?php echo $disp_inv['creation_ip']; ?>&amp;search_type=2"><?php echo $disp_inv['creation_ip']; ?></a></td>
						<td><?php echo number_format($disp_inv['amount']); ?></td>
					</tr>
				<?php
					$i++;
				}
				?>
				</tbody>
				</table>
				<?php
			} else {
				echo "<p>Nobody has this item in their inventory.</p>";
			}
			?>
			</div>
		</div>
	</div>
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title"><a data-toggle="collapse" href="#e_bank_block">Bank Placements <i class="fa fa-chevron-down" aria-hidden="true"></i></a></h3>
		</div>
		<div id="e_bank_block" class="panel-collapse collapse">
			<div class="panel-body">
			<?php 
			if($db->num_rows($bank_query) > 0) {
			?>
			<table class="base-tbl">
				<tr>
					<th rowspan="0" colspan="1">Order</th>
					<th rowspan="0" colspan="1">Player Account</th>
					<th rowspan="1" colspan="1">Registration IP</th>
					<th rowspan="1" colspan="1">Amount Held</th>
				</tr>
				<tbody role="alert" aria-live="polite" aria-relevant="all">
				<?php
				$i=1;
				while($disp_bank = $db->fetch_assoc($bank_query)) {
					?>
					<tr>
						<td><?php echo $i; ?></td>
						<td><a href="index.php?player_input=<?php echo urlencode($disp_bank['username']); ?>&amp;search_type=0"><?php echo ucwords($disp_bank['username']); ?></a></td>
						<td><a href="index.php?player_input=<?php echo $disp_bank['creation_ip']; ?>&amp;search_type=2"><?php echo $disp_bank['creation_ip']; ?></a></td>
						<td><?php echo number_format($disp_bank['amount']); ?></td>
					</tr>
					<?php
					$i++;
				}
				?>
				</tbody>
			</table>
			<?php
			} else {
				echo "<p>Nobody has this item in their bank.</p>";
			}
			?>
			</div>
		</div>
	</div>
	<?php
} 
else {
	?>
	<table class="base-tbl">
		<tr>
			<th><span class="indicator">Image</span></td>
			<th><span class="indicator">Name (ID)</span></td>
			<th><span class="indicator">Amount(s)</span></td>
		</tr>
		<tbody role="alert" aria-live="polite" aria-relevant="all">		
		<?php
		for($i = 0; $i < count($item_counts); $i++) 
		{
			$name_of_item = $db->fetch_assoc($db->query("SELECT name FROM " . GAME_BASE . "itemdef WHERE id = '" . $item_counts[$i] . "'"));
			?>
			<tr>
				<td><img src="<?php echo '../img/items/'.$item_counts[$i] . '.png'; ?>" alt="x" /></td>
				<td><?php echo $name_of_item['name'] . ' (' . $item_counts[$i] . ')'; ?></td>
				<td><a href="totals.php?lookup=<?php echo $item_counts[$i]; ?>"><?php echo number_format(formTotals($item_counts[$i])); ?></a></td>
			</tr>
			<?php
		}	
		?>
		</tbody>
	</table>
	<?php
}
?>
		</div>
	</form>
</div>
<?php
require 'footer.php';