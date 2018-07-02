<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'chat_log');

require LUNA_ROOT.'include/common.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}
$sender = isset($_GET['sender']) && strlen($_GET['sender']) <= 12 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_GET['sender']) ? strtolower($_GET['sender']) : null;
$message_keyword = (isset($_GET["search_keyword"]) ? htmlspecialchars($_GET["search_keyword"]) : null);
$time_start = (isset($_GET["search_time_start"]) ? $_GET["search_time_start"] : null);
$time_end = (isset($_GET["search_time_end"]) ? $_GET["search_time_end"] : null);
$results_per_page = (isset($_GET["results_per_page"]) == null ? 50 : $_GET["results_per_page"]);

$sender_query_addition = ($sender != null? "`sender` LIKE '".$sender."'" : "1=1");
$keyword_query_addition = ($message_keyword != null ? "MATCH (message) AGAINST ('".$message_keyword."')" : "1=1");
$time_start_query_addition = ($time_start != null ? "`time` > '".strtotime($time_start)."'" : "1=1");
$time_end_query_addition = ($time_end != null ? "`time` < '".strtotime($time_end)."'" : "1=1");

$query = $db->query("SELECT `sender`, `message`, `time` FROM `" . GAME_BASE . "chat_logs` WHERE ".$sender_query_addition." AND ".$keyword_query_addition." AND ".$time_start_query_addition." AND ".$time_end_query_addition." ORDER BY `time` DESC LIMIT 0,".$results_per_page."");
$total_query_count = $db->num_rows($query);

$page = isset($_GET['page']) ? (($_GET['page']) - 1) : 0;
$total_pages = ceil($total_query_count / $results_per_page);
$page_start = $page * $results_per_page;

require 'header.php';	

?>
<div class="content_wrapper col-sm-10">
	<form method="get" action="chat_log.php">
	<div class="row title">
		<div class="small-12 columns">
			<h2 class="page-heading">Chat & Global Log <span class="pull-right"><button class="block-top input-button" type="submit" tabindex="1">Search</button></span></h2>
		</div>
	</div>
	<div class="small-12 columns">
	<table class="table table-bordered">
		<tbody>
		<tr>
			<th>Sender:</th>
			<td colspan="2"><input type="text" class="form-control" name="sender" maxlength="12" value="<?php echo $sender; ?>" tabindex="2"></td>
			<th>Message keyword:</th>
			<td colspan="4"><input type="text" class="form-control" name="search_keyword" maxlength="12" value="<?php echo $message_keyword; ?>" tabindex="3"></td>
		</tr>
		<tr>
			<th>Date range:</th>
			<td><input type="text" class="form-control datepicker" name="search_time_start" placeholder="(mm/dd/yy)" maxlength="50" value="<?php echo $time_start ?>" tabindex="4"></td>
			<th style="text-align:center;">to</th>
			<td><input type="text" class="form-control datepicker" name="search_time_end" placeholder="(mm/dd/yy)" maxlength="50" value="<?php echo $time_end; ?>" tabindex="5"></td>
			<th>Results per page:</th>
			<td><input type="text" class="form-control" name="results_per_page" maxlength="40" value="<?php echo $results_per_page; ?>" tabindex="6"></td>
		</tr>
		</tbody>
	</table>
	<?php
	if($total_query_count > 0)
	{
	?>
	<table class="base-tbl">
		<tbody>
			<tr>
				<th><span class="indicator">Sender</span></td>
				<th><span class="indicator">Message</span></td>
				<th><span class="indicator">Date</span></td>
			</tr>
			<?php
			while($cur_chat = $db->fetch_assoc($query)){
				echo "
				<tr>
					<td><a href='index.php?player_input=".urlencode($cur_chat['sender'])."&amp;search_type=0'>" . $cur_chat['sender'] . "</a></td>
					<td>" . $cur_chat['message'] . "</td>
					<td>" . format_time($cur_chat['time']) . "</td>
				</tr>
				";
			}
			?>
		</tbody>
	</table>
	<?php
	} 
	else 
	{
		echo "<p>No chat messages found!</p>";
	}
	?>
	</div>
	</form>
</div>
<?php
require 'footer.php';