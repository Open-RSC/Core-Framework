<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'generic_log');

require LUNA_ROOT.'include/common.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}
$keyword = (isset($_GET["search_keyword"]) ? htmlspecialchars($_GET["search_keyword"]) : null);
$time_start = (isset($_GET["search_time_start"]) ? $_GET["search_time_start"] : null);
$time_end = (isset($_GET["search_time_end"]) ? $_GET["search_time_end"] : null);
$results_per_page = (isset($_GET["results_per_page"]) == null ? 50 : $_GET["results_per_page"]);

$keyword_query_addition = ($keyword != null? "match (message) against ('".$keyword."')" : "1=1");
$time_start_query_addition = ($time_start != null ? "`time` > '".strtotime($time_start)."'" : "1=1");
$time_end_query_addition = ($time_end != null ? "`time` < '".strtotime($time_end)."'" : "1=1");

$query = $db->query("SELECT `message`, `time` FROM `" . GAME_BASE . "generic_logs` WHERE  ".$keyword_query_addition." AND ".$time_start_query_addition." AND ".$time_end_query_addition." ORDER BY `time` DESC LIMIT 0,".$results_per_page."");

require 'header.php';	

?>
<div class="content_wrapper col-sm-10"">
	<form method="get" action="generic.php">
	<div class="row title">
		<div class="small-12 columns">
			<h2 class="page-heading">Generic Logs <span class="pull-right"><button class="block-top input-button" type="submit" tabindex="1">Search</button></span></h2>
		</div>
	</div>
	<div class="small-12 columns">
	<table class="table table-bordered">
		<tbody>
		<tr>
			<th>Search keyword:</th>
			<td colspan="5"><input type="text" class="form-control" name="search_keyword" maxlength="40" value="<?php echo (isset($_GET['search_keyword']) ? htmlspecialchars($_GET['search_keyword']) : null); ?>" tabindex="2"></td>
		</tr>
		<tr>
			<th>Date range:</th>
			
			<td><input type="text" class="form-control datepicker" name="search_time_start" placeholder="(mm/dd/yy)" maxlength="50" value="<?php echo $time_start; ?>" tabindex="3"></td>
			<th style="text-align:center;">to</th>
			<td><input type="text" class="form-control datepicker" name="search_time_end" placeholder="(mm/dd/yy)" maxlength="40" value="<?php echo $time_end; ?>" tabindex="4"></td>
			<th>Results per page:</th>
			<td><input type="text" class="form-control" name="results_per_page" maxlength="50" value="<?php echo $results_per_page;?>" tabindex="6"></td>
		</tr>
		</tbody>
	</table>
	<table class="base-tbl">
		<tbody>
			<tr>
				<th><span class="indicator">Date</span></td>
				<th><span class="indicator">Generic Message</span></td>
			</tr>
			<?php
			while($cur_log = $db->fetch_assoc($query)){
				$text = $cur_log['message'];
				$username = explode(' ', trim($text));
				
				echo "
					<tr>
						<td>" . format_time($cur_log['time']) . "</td>
						<td><a href='index.php?player_input=".$username[0]."&amp;search_type=0'>" . $cur_log['message'] . "</a></td>
					</tr>
				";
			}
			?>
		</tbody>
	</table>
	</div>
	</form>
</div>
<?php
require 'footer.php';