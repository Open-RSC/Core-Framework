<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'stats');

require LUNA_ROOT.'include/common.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}
require 'header.php';	

$character = isset($_GET['character'])  && strlen($_GET['character']) <= 12 ? trim($_GET['character']) : null;

$get_quests = $db->query("SELECT " . GAME_BASE . "quests.stage," . GAME_BASE . "quests.id, " . GAME_BASE . "players.username, " . GAME_BASE . "players.id FROM " . GAME_BASE . "quests LEFT JOIN " . GAME_BASE . "players ON " . GAME_BASE . "quests.playerID = " . GAME_BASE . "players.id WHERE " . GAME_BASE . "quests.playerID = '" . $db->escape($character) . "'");
$result_quest = $db->fetch_assoc($get_quests);

if(isset($character) && $db->num_rows($get_quests) > 0) {

function quests($id) {
	switch($id) {
		case 0:
			$array = array('<td><span class="base-tbl__highlight">Black Knights\' Fortress</span></td>', '<td><i class="fa fa-star"></i><i class="fa fa-star"></i></td>', '<td>3</td>', '<td>No</td>');
		break;
		case 1:
			$array = array('<td><span class="base-tbl__highlight">Cook\'s Assistant</span></td>', '<td><i class="fa fa-star"></i></td>', '<td>1</td>', '<td>No</td>');
		break;
		case 2:
			$array = array('<td><span class="base-tbl__highlight">Demon Slayer</span></td>', '<td><i class="fa fa-star"></i><i class="fa fa-star"></i></td>', '<td>3</td>', '<td>No</td>');
		break;
		case 3:
			$array = array('<td><span class="base-tbl__highlight">Doric\'s Quest</span></td>', '<td><i class="fa fa-star"></i></td>', '<td>1</td>', '<td>No</td>');
		break;
		case 4:
			$array = array('<td><span class="base-tbl__highlight">Dragon slayer</span></td>', '<td><i class="fa fa-star"></i><i class="fa fa-star"></i><i class="fa fa-star"></i><i class="fa fa-star"></i></td>', '<td>2</td>', '<td>No</td>');
		break;
		case 5:
			$array = array('<td><span class="base-tbl__highlight">Ernest the Chicken</span></td>', '<td><i class="fa fa-star"></i></td>', '<td>4</td>', '<td>No</td>');
		break;
		case 6:
			$array = array('<td><span class="base-tbl__highlight">Goblin Diplomacy</span></td>', '<td><i class="fa fa-star"></i></td>', '<td>5</td>', '<td>No</td>');
		break;
		case 7:
			$array = array('<td><span class="base-tbl__highlight">Imp Catcher</span></td>', '<td><i class="fa fa-star"></i></td>', '<td>1</td>', '<td>No</td>');
		break;
		case 8:
			$array = array('<td><span class="base-tbl__highlight">The Knight\'s Sword</span></td>', '<td><i class="fa fa-star"></i><i class="fa fa-star"></i></td>', '<td>1</td>', '<td>No</td>');
		break;
		case 9:
			$array = array('<td><span class="base-tbl__highlight">Pirate\'s Treasure</span></td>', '<td><i class="fa fa-star"></i></td>', '<td>2</td>', '<td>No</td>');
		break;
		case 10:
			$array = array('<td><span class="base-tbl__highlight">Prince Ali Rescue</span></td>', '<td><i class="fa fa-star"></i><i class="fa fa-star"></i></td>', '<td>3</td>', '<td>No</td>');
		break;
		case 11:
			$array = array('<td><span class="base-tbl__highlight">The Restless Ghost</span></td>', '<td><i class="fa fa-star"></i></td>', '<td>1</td>', '<td>No</td>');
		break;
		case 12:
			$array = array('<td><span class="base-tbl__highlight">Romeo & Juliet</span></td>', '<td><i class="fa fa-star"></i></td>', '<td>5</td>', '<td>No</td>');
		break;
		case 13:
			$array = array('<td><span class="base-tbl__highlight">Sheep Shearer</span></td>', '<td><i class="fa fa-star"></i></td>', '<td>1</td>', '<td>No</td>');
		break;
		case 14:
			$array = array('<td><span class="base-tbl__highlight">Shield of Arrav</span></td>', '<td><i class="fa fa-star"></i><i class="fa fa-star"></i><i class="fa fa-star"></i></td>', '<td>1</td>', '<td>No</td>');
		break;
		case 15:
			$array = array('<td><span class="base-tbl__highlight">Vampire Slayer</span></td>', '<td><i class="fa fa-star"></i><i class="fa fa-star"></i></td>', '<td>3</td>', '<td>No</td>');
		break;
		case 16:
			$array = array('<td><span class="base-tbl__highlight">Witch\'s Potion</span></td>', '<td><i class="fa fa-star"></i></td>', '<td>1</td>', '<td>No</td>');
		break;
	}
	
	if(!empty($array))
		return implode(" ", $array);
}
?>
<div class="content_wrapper col-sm-10">
	<div class="row title">
		<div class="small-12 columns">
			<h2 class="page-heading">Quests [<?php echo ucwords($result_quest['username']) ?>]</h2>
		</div>
	</div>
	<div class="small-12 columns">
	<table class="base-tbl">
		<tbody>
			<tr>
				<th><span class="indicator">Name</span></td>
				<th><span class="indicator">Difficulty</span></td>
				<th><span class="indicator">Quest Points</span></td>
				<th><span class="indicator">Member</span></td>
				<th><span class="indicator">Status</span></td>
			</tr>
			<?php 
			for($i = 0; $i < 17; $i++) {
				echo "
					<tr>
					" . quests($i) . "
					<td>N/A</td>
					</tr>
				";
			}
		?>
		</tbody>
	</table>
	</div>
</div>
<?php 
} else {
?>
<div class="content_wrapper col-sm-10">
	<div class="row title">
		<div class="small-12 columns">
			<h2 class="page-heading">Quests</h2>
		</div>
	</div>
	<div class="small-12 columns">
	<p>Could not fetch quest data! (user has probably not logged in to have their quest list loaded to database yet).</p>
	</div>
</div>
<?php
}
require 'footer.php';