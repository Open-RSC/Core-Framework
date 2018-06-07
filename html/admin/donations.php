<?php

/*
 * Copyright (C) 2016 RSCL
 * Author: Imposter.
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'donations');

require LUNA_ROOT.'include/common.php';
require LUNA_ROOT.'include/parser.php';

if (($luna_user['id'] != 2) && ($luna_user['id'] != 3)) {
	header("Location: ../backstage/login.php");
    exit;
}

$payer_email = (isset($_GET["search_email"]) ? htmlspecialchars($_GET["search_email"]) : null);
$forum_user = (isset($_GET["search_user"]) ? htmlspecialchars($_GET["search_user"]) : null);
$time_start = (isset($_GET["search_time_start"]) ? $_GET["search_time_start"] : null);
$time_end = (isset($_GET["search_time_end"]) ? $_GET["search_time_end"] : null);

$payer_email_query_addition = ($payer_email != null? "`payer_email` LIKE '%".$payer_email."%'" : "1=1");
$user_query_addition = ($forum_user != null? "`username` LIKE '%".$forum_user."%'" : "1=1");
$time_start_query_addition = ($time_start != null ? "`time` > '".strtotime($time_start)."'" : "1=1");
$time_end_query_addition = ($time_end != null ? "`time` < '".strtotime($time_end)."'" : "1=1");


$result = $db->query('SELECT COUNT(order_id) FROM `'.GAME_BASE.'orders` JOIN users ON '.GAME_BASE.'orders.user = users.id WHERE '.$payer_email_query_addition." AND ".$user_query_addition." AND ".$time_start_query_addition." AND ".$time_end_query_addition.'') or error('Unable to fetch user list count', __FILE__, __LINE__, $db->error());
$num_don = $db->result($result);

// Determine the user offset (based on $_GET['p'])
$num_pages = ceil($num_don / 50);

$p = (!isset($_GET['p']) || $_GET['p'] <= 1 || $_GET['p'] > $num_pages) ? 1 : intval($_GET['p']);
$start_from = 50 * ($p - 1);

$query = $db->query("SELECT ".GAME_BASE."orders.*, users.username FROM `".GAME_BASE."orders` JOIN users ON ".GAME_BASE."orders.user = users.id WHERE ".$payer_email_query_addition."  AND ".$user_query_addition." AND ".$time_start_query_addition." AND ".$time_end_query_addition." ORDER BY `time` DESC LIMIT ".$start_from.", 50");

require 'header.php';	
?>
<div class="content_wrapper col-sm-10">
	<form method="get" action="donations.php">
		<div class="row title">
			<div class="small-12 columns">
				<h2 class="page-heading">Donations & Sales Monitoring <span class="pull-right"><button class="block-top input-button" type="submit" tabindex="1">Search</button></span></h2>
			</div>
		</div>
		<div class="small-12 columns">
		<table class="table table-bordered">
			<tbody>
			<tr>
				<th>Payer email:</th>
				<td colspan="2"><input type="text" class="form-control" name="search_email" maxlength="40" value="<?php echo ($payer_email ? htmlspecialchars($payer_email) : null); ?>" tabindex="2"></td>
				<th>Forum Username:</th>
				<td colspan="2"><input type="text" class="form-control" name="search_user" maxlength="40" value="<?php echo ($forum_user ? htmlspecialchars($forum_user) : null); ?>" tabindex="3"></td>
			</tr>
			<tr>
				<th>Date range:</th>
				<td colspan="2"><input type="text" class="form-control datepicker" name="search_time_start" placeholder="(mm/dd/yy)" maxlength="50" value="<?php echo $time_start; ?>" tabindex="4"></td>
				<th style="text-align:center;">to</th>
				<td colspan="2"><input type="text" class="form-control datepicker" name="search_time_end" placeholder="(mm/dd/yy)" maxlength="40" value="<?php echo $time_end; ?>" tabindex="5"></td>
			</tr>
			</tbody>
		</table>
		<?php
		$total_revenue = 0;
		$revenue_month = 0;
		$revenue_week = 0;
		$revenue_today = 0;
		$first_day_of_month = strtotime(date('Y-m-01 00:00:00'));
		$j=0;
		$value=array();
		while($r=$db->fetch_assoc($query)){
			if($r['time'] > $first_day_of_month) {
				$revenue_month = $revenue_month + $r['paid'];
			}
			if($r['time'] > strtotime("-1 week")) {
				$revenue_week = $revenue_week + $r['paid'];
			}
			if($r['time'] > strtotime("today")) {
				$revenue_today = $revenue_today + $r['paid'];
			}
			$table_contents = $table_contents."
				<tr " . (($j % 2 == 0) ? " " : null ) . ">
					<td>" . format_time($r['time']) . "</td>
					<td>".$r['txn_id']."</td>
					<td>".$r['paid']."$ | ".$r['jewels_purchased']."</td>
					<td><a href='index.php?player_input=".urlencode($r['username'])."&amp;search_type=1'>".$r['username']."</a></td>
					<td>".$r['payer_email']."</td>
				</tr>
			";
			$total_revenue = $total_revenue + $r['paid'];
		}
		?>
		<p><b>Statistics:</b></p>
		<p><b>Income this month:</b> <?php echo $revenue_month; ?> USD</p>
		<p><b>Income this week:</b> <?php echo $revenue_week; ?>  USD</p>
		<p><b>Income today:</b> <?php echo $revenue_today; ?>  USD</p>
		<p><b>Income from set dates:</b>  <?php echo $total_revenue;?> USD</p>
		<?php 
			echo '<ul class="pagination">'.paginate($num_pages, $p, 'donations.php?search_email='.urlencode($payer_email).'&amp;search_forum='.urlencode($forum_user).'&amp;search_time_start='.$time_start.'&amp;search_time_start='.$time_end).'</ul>'; 
		?>
		<table class="base-tbl">
			<tbody>
				<tr>
					<th><span class="indicator">Date</span></td>
					<th><span class="indicator">TXN ID</span></td>
					<th><span class="indicator">Paid/Jewels</span></td>
					<th><span class="indicator">Forum User</span></td>
					<th><span class="indicator">E-mail</span></td>
				</tr>
				<?php echo $table_contents; ?>
			</tbody>
		</table>
		</div>
	</form>
</div>
<?php
require 'footer.php';