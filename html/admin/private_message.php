<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'private_message_log');

require LUNA_ROOT.'include/common.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}
$search_pm_author = isset($_GET['search_sender']) && strlen($_GET['search_sender']) <= 12 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_GET['search_sender']) ? strtolower($_GET['search_sender']) : null;
$search_pm_reciever = isset($_GET['search_rec']) && strlen($_GET['search_rec']) <= 12 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_GET['search_rec']) ? strtolower($_GET['search_rec']) : null;
$message_keyword = (isset($_GET["search_keyword"]) ? htmlspecialchars($_GET["search_keyword"]) : null);
$results_per_page = (isset($_GET["results_per_page"]) == null ? 50 : $_GET["results_per_page"]);


$sender_query_addition = ($search_pm_author != null ? "`sender` LIKE '".$search_pm_author."'" : "1=1");
$reciever_query_addition = ($search_pm_reciever != null ? "`reciever` LIKE '".$search_pm_reciever."'" : "1=1");
$keyword_query_addition = ($message_keyword != null ? "MATCH (message) AGAINST ('".$message_keyword."')" : "1=1");

$query = $db->query("SELECT `sender`, `message`, `reciever`, `time` FROM `" . GAME_BASE . "private_message_logs` WHERE ".$sender_query_addition." AND ".$reciever_query_addition." AND ".$keyword_query_addition." ORDER BY `time` DESC LIMIT 0,".$results_per_page."");
$total_query_count = $db->num_rows($query);

$page = isset($_GET['page']) ? (($_GET['page']) - 1) : 0;
$total_pages = ceil($total_query_count / $results_per_page);
$page_start = $page * $results_per_page;

require 'header.php';	

?>
<div class="content_wrapper col-sm-10">
	<form method="get" action="private_message.php">
	<div class="row title">
		<div class="small-12 columns">
			<h2 class="page-heading">Private Message Logs <span class="pull-right"><button class="block-top input-button" type="submit" tabindex="1">Search</button></span></h2>
		</div>
	</div>
	<div class="small-12 columns">
	<table class="table table-bordered">
		<tbody>
		<tr>
			<th>Sender:</th>
			<td colspan="2"><input type="text" class="form-control" name="search_sender" maxlength="12" value="<?php echo $search_pm_author; ?>" tabindex="2"></td>
			<th>Reciever:</th>
			<td colspan="2"><input type="text" class="form-control" name="search_rec" maxlength="12" value="<?php echo $search_pm_reciever; ?>" tabindex="3"></td>
		</tr>
		<tr>
			<th>Message keyword:</th>
			<td colspan="2"><input type="text" class="form-control" name="search_keyword" maxlength="50" value="<?php echo $message_keyword; ?>" tabindex="4"></td>
			<th>Results per page:</th>
			<td colspan="2"><input type="text" class="form-control" name="results_per_page" maxlength="40" value="<?php echo $results_per_page; ?>" tabindex="5"></td>
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
				<th><span class="indicator">Date</span></td>
				<th><span class="indicator">Sender</span></td>
				<th><span class="indicator">Reciever</span></td>
				<th><span class="indicator">Message</span></td>
			</tr>
			<?php
			while($cur_priv = $db->fetch_assoc($query)){
				echo "
					<tr>
					<td>" . format_time($cur_priv['time']) . "</td>
					<td><a href='index.php?player_input=".urlencode($cur_priv['sender'])."&amp;search_type=0'>" . $cur_priv['sender'] . "</a></td>
					<td><a href='index.php?player_input=".urlencode($cur_priv['reciever'])."&amp;search_type=0'>" . $cur_priv['reciever'] . "</a></td>
					<td>" . $cur_priv['message'] . "</td>
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
		echo "<p>No private messages found!</p>";
	}
	?>
	</div>
	</form>
</div>
<?php
require 'footer.php';