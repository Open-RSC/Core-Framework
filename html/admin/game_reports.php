<?php

/*
 * Copyright (C) 2016 RSCL
 * Author: Imposter.
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'game_report');

require LUNA_ROOT.'include/common.php';
require LUNA_ROOT.'include/parser.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}
$reporter_search = isset($_GET['search_reporter']) && strlen($_GET['search_reporter']) <= 12 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_GET['search_reporter']) ? strtolower($_GET['search_reporter']) : null;
$reported_search = isset($_GET['search_reported']) && strlen($_GET['search_reported']) <= 12 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_GET['search_reported']) ? strtolower($_GET['search_reported']) : null;
if(isset($reporter_search)) {
	$report_abuse_query = "SELECT reporter,reported,time,reason,chatlog,reporter_x,reporter_y,reported_x,reported_y FROM ". GAME_BASE ."game_reports WHERE reporter = '".$db->escape($reporter_search)."' ORDER BY time DESC";
} else if(isset($reported_search)) {
	$report_abuse_query = "SELECT reporter,reported,time,reason,chatlog,reporter_x,reporter_y,reported_x,reported_y FROM ". GAME_BASE ."game_reports WHERE reported = '".$db->escape($reported_search)."' ORDER BY time DESC";
} else {
	$report_abuse_query = "SELECT reporter,reported,time,reason,chatlog,reporter_x,reporter_y,reported_x,reported_y FROM ". GAME_BASE ."game_reports WHERE id > 0 ORDER BY time DESC";
}
$total_results = $db->num_rows($db->query($report_abuse_query));	
$total_pages = ceil($total_results / 15);
$p = (!isset($_GET['p']) || $_GET['p'] <= 1 || $_GET['p'] > $total_pages) ? 1 : intval($_GET['p']);
$fix_curr_page = ($p-1) * 15;
$find_reports = $db->query($report_abuse_query . " LIMIT " . $fix_curr_page . " , 15");
$report_results = $db->num_rows($find_reports);	

function reportReasonText($id) {
	$reason = array('Buying or selling an account', 'Encouraging rule-breaking', 
	'Staff impersonation', 'Macroing or use of bots', 'Scamming', 'Exploiting a bug', 
	'Seriously offensive language', 'Solicitation','Disruptive behaviour',
	'Offensive account name','Real-life threats','Asking for or providing contact information',
	'Breaking real-world laws','Advertising websites');
	foreach($reason as $output) {
		$text = $reason[$id - 1];
	}
	return $text;
}
require 'header.php';	
?>
<div class="content_wrapper col-sm-10">
		<div class="row title">
			<div class="small-12 columns">
				<h2 class="page-heading">Game Report Logs</h2>
			</div>
		</div>
		<div class="small-12 columns">
		<table class="table table-bordered">
		<tbody>
		<tr>
			<th>Reporter: </th>
			<td>
			
				<form method="get" action="game_reports.php">
				<div class="input-wrap">
					<input class="form-control" type="text" maxlength="50" name="search_reporter" value="<?php echo (isset($_GET['search_reporter']) ? htmlspecialchars($_GET['search_reporter']) : null) ?>" />
					<span class="input-group-btn">
						<button class="input-button" type="submit" value="Search">Search</button>
					</span>
				</div>
				</form>
			</td>
			<th>Reported: </th>
			<td>
				<form method="get" action="game_reports.php">
				<div class="input-wrap">
					<input class="form-control" type="text" maxlength="50" name="search_reported" value="<?php echo (isset($_GET['search_reported']) ? htmlspecialchars($_GET['search_reported']) : null) ?>" />
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
		echo "<h4><b>" . number_format($total_results) . "</b> game reports were found, showing 15 reports per page.</h4>"; 
		if(isset($reporter_search)) {
			echo '<ul class="pagination">'.paginate($total_pages, $p, 'game_reports.php?search_reporter=' . $reporter_search ).'</ul>';
		} else if(isset($reported_search)) {
			echo '<ul class="pagination">'.paginate($total_pages, $p, 'game_reports.php?search_reported=' . $reported_search ).'</ul>';
		} else {
			echo '<ul class="pagination">'.paginate($total_pages, $p, 'game_reports.php?').'</ul>';
		}	
		if($report_results > 0)
		{	
		?>
		<table class="base-tbl">
			<tr>
				<th rowspan="1" colspan="1">Time</th>
				<th rowspan="1" colspan="1">Reporter</th>
				<th rowspan="1" colspan="1">Reported</th>
				<th rowspan="0" colspan="1">Reason</th>
				<th rowspan="1" colspan="1">Snapshot</th>
				<th rowspan="1" colspan="1">Reporter (X, Y)</th>
				<th rowspan="1" colspan="1">Reported (X, Y)</th>
			</tr>
			<tbody role="alert" aria-live="polite" aria-relevant="all">
			<?php
			while($r=$db->fetch_assoc($find_reports)) {
				echo "
					<tr>
						<td>" .	format_time($r['time']) . "</td>
						<td><a href='index.php?player_input=".$r['reporter']."&amp;search_type=0'>" . $r['reporter'] . "</a></td>
						<td><a href='index.php?player_input=".$r['reported']."&amp;search_type=0'>" . ucwords($r['reported']) . "</a></td>
						<td>" .	reportReasonText($r['reason']) . "</td>
						<td>" .	($r['chatlog'] == '' ? "NaN" : parse_message($r['chatlog'])) . "</td>
						<td>(" . $r['reporter_x'] . ", " . $r['reporter_y'] . ")</td>
						<td>(" . $r['reported_x'] . ", " . $r['reported_y'] . ")</td>
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