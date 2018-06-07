<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'staff_log');

require LUNA_ROOT.'include/common.php';

if ($luna_user['g_id'] != LUNA_ADMIN) {
	header("Location: ../backstage/login.php");
    exit;
}
$staff_username_search = isset($_GET['search_staff_username']) && strlen($_GET['search_staff_username']) <= 12 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_GET['search_staff_username']) ? strtolower($_GET['search_staff_username']) : null;
if(isset($staff_username_search)) {
	$staff_log_query = "SELECT staff_username, action, affected_player, time, staff_x, staff_y, affected_x, affected_y, staff_ip, affected_ip, extra FROM " . GAME_BASE . "staff_logs WHERE staff_username = '".$db->escape($staff_username_search)."' ORDER BY time DESC";
	$staff_log_query_p = "SELECT COUNT(*) FROM " . GAME_BASE . "staff_logs WHERE staff_username = '".$db->escape($staff_username_search)."'";
} else {
	$staff_log_query = "SELECT staff_username, action, affected_player, time, staff_x, staff_y, affected_x, affected_y, staff_ip, affected_ip, extra FROM " . GAME_BASE . "staff_logs ORDER BY time DESC";
	$staff_log_query_p = "SELECT COUNT(*) FROM " . GAME_BASE . "staff_logs";
}
$total_results = $db->result($db->query($staff_log_query_p));
		
$total_pages = ceil($total_results / 30);
$p = (!isset($_GET['p']) || $_GET['p'] <= 1 || $_GET['p'] > $total_pages) ? 1 : intval($_GET['p']);
$fix_curr_page = ($p-1) * 30;
$find_staff_logs = $db->query($staff_log_query . " LIMIT " . $fix_curr_page . " , 30");
$log_results = $db->num_rows($find_staff_logs);

function staffAction($action) {
	$options = array('::mute', '::unmuted', '::summon', '::goto', '::take', '::put', '::kick', '::update', '::stopevent', '::setevent', '::blink', '::tban', '::putfatigue', '::say', '::invis', '::teleport', '::send', '::town', '::check', '::unban', '::ban', '::globaldrop');
	foreach ($options as $value) {
		$text = $options[$action];
	}
	return $text;
}

require 'header.php';	
?>
<div class="content_wrapper col-sm-10">
	<form method="get" action="staff.php">
		<div class="row title">
			<div class="small-12 columns">
				<h2 class="page-heading">Staff Logs</h2>
			</div>
		</div>
		<div class="small-12 columns">
		<table class="table table-bordered">
		<tbody>
		<tr>
			<th>Staff Username: </th>
			<td>
				<div class="input-wrap">
					<input class="form-control" type="text" maxlength="50" name="search_staff_username" value="<?php echo (isset($_GET['search_staff_username']) ? htmlspecialchars($_GET['search_staff_username']) : null)?>" />
					<span class="input-group-btn">
						<button class="input-button" type="submit" value="Search">Search</button>
					</span>
				</div>
			</td>
		</tr>
		</tbody>
		</table>
		<?php
		
		if(isset($staff_username_search)) {
			if($total_pages > 1)
				echo "<h4><b>" .  number_format($total_results) . "</b> staff logs were found for <b>". luna_htmlspecialchars($staff_username_search) ."</b>, showing 30 logs per page.</h4>";
				echo '<ul class="pagination">'.paginate($total_pages, $p, '?search_staff_username=' . $staff_username_search ).'</ul>';
		} else {
			if($total_pages > 1)
				
				echo "<h4><b>" .  number_format($total_results) . "</b> staff logs were found, showing 30 logs per page.</h4>";
				echo '<ul class="pagination">'.paginate($total_pages, $p, 'staff.php?').'</ul>';
		}	
		if($log_results > 0)
		{	
		?>
		<table class="base-tbl">
			<tr>
				<th rowspan="1" colspan="1">Time</th>
				<th rowspan="1" colspan="1">Staff</th>
				<th rowspan="0" colspan="1">Action</th>
				<th rowspan="1" colspan="1">Affected</th>
				<th rowspan="1" colspan="1">Staff (X, Y)</th>
				<th rowspan="1" colspan="1">Affected (X, Y)</th>
				<th rowspan="1" colspan="1">Staff IP</th>
				<th rowspan="1" colspan="1">Affected IP</th>
			</tr>
			<tbody role="alert" aria-live="polite" aria-relevant="all">
			<?php
				while($r=$db->fetch_assoc($find_staff_logs)) 
				{
					echo "
						<tr>
							<td>" .	format_time($r['time']) . "</td>
							<td><a href='index.php?player_input=".$r['staff_username']."&amp;search_type=0'>" . $r['staff_username'] . "</a></td>
							<td>" . staffAction($r['action']) . "</td>
							<td><a href='index.php?player_input=".$r['affected_player']."&amp;search_type=0'>" . $r['affected_player'] . "</a></td>
							<td>(" . $r['staff_x'] . ", " . $r['staff_y'] . ")</td>
							<td>(" . $r['affected_x'] . ", " . $r['affected_y'] . ")</td>
							<td>" .	$r['staff_ip'] . "</td>
							<td>" .	$r['affected_ip'] . "</td>
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