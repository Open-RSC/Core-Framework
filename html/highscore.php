<?php
/*
 * Created by Imposter 2016-07-07.
 */
define('LUNA_ROOT', dirname(__FILE__).'/');
require LUNA_ROOT.'include/common.php';

if ($luna_user['g_read_board'] == '0')
	message(__('You do not have permission to view this page.', 'luna'), false, '403 Forbidden');

$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), __('Highscore', 'luna'));
define('LUNA_ACTIVE_PAGE', 'highscore');
define('LUNA_ALLOW_INDEX', 1);
require load_page('header.php');

$pages = array(
    'main',
    'ironman',
    'ultimate_ironman',
	'hardcore_ironman'
);
$selected_highscore = isset($_GET ['c']) && in_array($_GET ['c'], $pages) ? $_GET ['c'] : 'main';


$skill = isset($_GET['skill']) && is_string($_GET['skill']) && isValidSkill($_GET['skill']) ? $_GET['skill'] : 'overall';

$category = array('ranking', 'compare');
$selected_page = isset($_GET['m']) && in_array($_GET['m'], $category) ? $_GET['m'] : 'ranking';

$player = isset($_GET['user']) && strlen($_GET['user']) <= 12 ? trim($_GET['user']) : null;
$curr_player = isset($player) && preg_match("/^[a-zA-Z0-9\s]+?$/i", $player) ? $player : null; 

$player1 = isset($_GET['user1']) && strlen($_GET['user1']) <= 12 ? trim($_GET['user1']) : null;
$curr_player1 = isset($player1) && preg_match("/^[a-zA-Z0-9\s]+?$/i", $player1) ? $player1 : null; 

$player2 = isset($_GET['user2']) && strlen($_GET['user2']) <= 12 ? trim($_GET['user2']) : null;
$curr_player2 = isset($player2) && preg_match("/^[a-zA-Z0-9\s]+?$/i", $player2) ? $player2 : null; 

$skill_xp = strtolower('exp_'.$skill);

function getRank($user, $skill, $pid = null, $ironman) {
	global $db;
	if($skill != 'Overall') {
		$sk = "exp_".strtolower($skill);
		$stmt = $db->query("SELECT e.playerID, p.iron_man FROM " . GAME_BASE . "experience AS e LEFT JOIN " . GAME_BASE . "players AS p ON e.playerID = p.id WHERE p.iron_man = '".$ironman."' AND e.".$sk." > 0 ORDER BY e.".$sk." DESC");
		$count = 1;
		while ($entry = $db->fetch_assoc($stmt)) {
			if ($entry['playerID'] == $pid) {
				return $count;
			} 
			$count++;
		}
	} else {
		$tot = "skill_total";
		$stmt = $db->query("SELECT username FROM " . GAME_BASE . "players WHERE iron_man = ".$ironman." AND ".$tot." > 27 ORDER BY ".$tot." DESC");
		$count = 1;
		while ($entry = $db->fetch_assoc($stmt)) {
			if (strtolower($entry['username']) == strtolower($user)) {
				return $count;
			} 
			$count++;
		}
	}
}

?>
<div id="wrapper" class="container">
	<div class="content">
	<div class="ironman-nav">
		<?php echo ($selected_highscore == "main" ? "<span class='ironman-nav_category ironman-nav_current'>Default Highscore</span>" : "<a class='ironman-nav_category' href='highscore.php?c=main'>Default Highscore</a>"); ?>
		<?php echo ($selected_highscore == "ironman" ? "<span class='ironman-nav_category ironman-nav_current'>Ironman</span>" : "<a class='ironman-nav_category' href='highscore.php?c=ironman'>Ironman</a>"); ?>
		<?php echo ($selected_highscore == "ultimate_ironman" ? "<span class='ironman-nav_category ironman-nav_current'>Ultimate Ironman</span>" : "<a class='ironman-nav_category' href='highscore.php?c=ultimate_ironman'>Ultimate Ironman</a>"); ?>
		<?php echo ($selected_highscore == "hardcore_ironman" ? "<span class='ironman-nav_category ironman-nav_current'>Hardcore Ironman</span>" : "<a class='ironman-nav_category' href='highscore.php?c=hardcore_ironman'>Hardcore Ironman</a>"); ?>
	</div>
		<?php if($selected_page == "ranking") { ?> <div class="col-sm-8 content-l-side" id="highscore"> <?php } else { ?> <div class="compare" id="highscore"> <?php } ?>
			<div class="base-header cb10">
				<div class="base-footer">
				<div class="col-sm-12 skill-icons">
					<?php 
						foreach(getSkills() as $skrilla) {
							echo '
							<a href="?c='.$selected_highscore.'&skill='.$skrilla.'"><img src="img/skills/'.strtolower($skrilla).'.gif" class="skill-ico" data-toggle="tooltip" title="'.$skrilla.'"></a>';
						}
					?>
					<hr>
				</div>
				<?php 
				if($selected_page == "ranking") {
					if (!isset($curr_player)) {	
						switch(strtolower($skill)) {
							case 'overall':
								$ALL_USERS = "SELECT username,iron_man,hc_ironman_death,combat,skill_total,total_experience FROM " . GAME_BASE . "players WHERE skill_total > 27 AND highscoreopt = 0 ". ($selected_highscore == 'ironman' ? ' AND iron_man = 1' : '') . ($selected_highscore == 'ultimate_ironman' ? ' AND iron_man = 2' : '') . ($selected_highscore == 'hardcore_ironman' ? ' AND iron_man = 3 OR hc_ironman_death = 1' : '') ." AND banned = '0' AND group_id IN (0, 6) ORDER BY skill_total DESC, total_experience DESC";
								$COUNT = "SELECT COUNT(*) FROM " . GAME_BASE . "players WHERE skill_total > 27 AND highscoreopt = '0' ". ($selected_highscore == 'ironman' ? ' AND iron_man = 1' : '') . ($selected_highscore == 'ultimate_ironman' ? ' AND iron_man = 2' : '') . ($selected_highscore == 'hardcore_ironman' ? ' AND iron_man = 3 OR hc_ironman_death = 1' : '') ." AND banned = '0' AND group_id IN (0, 6)";
							break;
							case 'kd':
								$ALL_USERS = "SELECT username, kills, deaths FROM " . GAME_BASE . "players WHERE kills > 0 AND highscoreopt = '0' AND banned = '0' ORDER BY kills DESC";
								$COUNT = "SELECT COUNT(*) FROM " . GAME_BASE . "players WHERE kills > 0 AND highscoreopt = 0 AND banned = '0'";
							break;
							default:
								$ALL_USERS = "SELECT e.".$skill_xp.",p.username,p.iron_man,p.hc_ironman_death, p.combat,p.skill_total FROM " . GAME_BASE . "players AS p LEFT JOIN " . GAME_BASE . "experience AS e ON e.playerID = p.id WHERE p.skill_total > 27 AND p.highscoreopt = 0 ". ($selected_highscore == 'ironman' ? ' AND p.iron_man = 1' : '') . ($selected_highscore == 'ultimate_ironman' ? ' AND p.iron_man = 2' : '') . ($selected_highscore == 'hardcore_ironman' ? ' AND p.iron_man = 3 OR p.hc_ironman_death = 1' : '') ." AND p.banned = '0' AND group_id IN (0, 6) ORDER BY e.".$skill_xp." DESC";
								$COUNT = "SELECT COUNT(*) FROM " . GAME_BASE . "players WHERE skill_total > 27 AND highscoreopt = '0' ". ($selected_highscore == 'ironman' ? ' AND iron_man = 1' : '') . ($selected_highscore == 'ultimate_ironman' ? ' AND iron_man = 2' : '') . ($selected_highscore == 'hardcore_ironman' ? ' AND iron_man = 3 OR hc_ironman_death = 1' : '') ." AND banned = '0' AND group_id IN (0, 6)";
							break;
						}	
						$total_pages = $db->result($db->query($COUNT));
						$num_pages = ceil($total_pages / 25);
						if(isset($_POST['p'])) {
							$p = (!isset($_POST['p']) || $_POST['p'] <= 1 || $_POST['p'] > $num_pages) ? 1 : intval($_POST['p']);
						} else {
							$p = (!isset($_GET['p']) || $_GET['p'] <= 1 || $_GET['p'] > $num_pages) ? 1 : intval($_GET['p']);
						}
						$fix_curr_page = ($p-1) * 25;
						$grab_skill = $db->query($ALL_USERS . " LIMIT " . $fix_curr_page . " , 25");
						$paging_links = forum_paginate($num_pages, $p, 'highscore.php?c='.$selected_highscore.'&m=ranking&skill='.$skill);
						
						require load_page('highscore/main.php');
					} else {
						if (isset($curr_player)) {
							$FETCH_SINGLE_PLAYER = $db->query("SELECT e.exp_attack,e.exp_defense,e.exp_strength,e.exp_hits,e.exp_ranged,e.exp_prayer,e.exp_magic,e.exp_cooking,e.exp_woodcut,e.exp_fletching,e.exp_fishing,e.exp_firemaking,e.exp_crafting,e.exp_smithing,e.exp_mining,e.exp_herblaw,e.exp_agility,e.exp_thieving, p.total_experience,p.username, p.combat,p.skill_total,p.id,p.creation_date,p.login_date,p.kills,p.deaths,p.highscoreopt,p.iron_man FROM " . GAME_BASE . "players AS p LEFT JOIN " . GAME_BASE . "experience AS e ON e.playerID = p.id WHERE p.username = '".$db->escape($curr_player)."'") or die('Failed to fetch user profile');
							if ($db->num_rows($FETCH_SINGLE_PLAYER) > 0) {
								require load_page('highscore/player.php');
							} else {
							?>
							<div class="HSError">
								<p><?php echo $curr_player ?> was not found in the Highscore table</p>
							</div>
							<?php
							}
						}
					}
				} else if($selected_page == "compare") {
					if(isset($curr_player1) && !isset($curr_player2)) {
						redirect('highscore.php?c='.$selected_highscore.'&m=ranking&user='.$curr_player1.'');
					} else if(!isset($curr_player1) && isset($curr_player2)) {
						redirect('highscore.php?c='.$selected_highscore.'&m=ranking&user='.$curr_player2.'');
					} else if(isset($curr_player1) && isset($curr_player2)) {
						$FETCH_PLAYER1 = $db->query("SELECT e.exp_attack,e.exp_defense,e.exp_strength,e.exp_hits,e.exp_ranged,e.exp_prayer,e.exp_magic,e.exp_cooking,e.exp_woodcut,e.exp_fletching,e.exp_fishing,e.exp_firemaking,e.exp_crafting,e.exp_smithing,e.exp_mining,e.exp_herblaw,e.exp_agility,e.exp_thieving,p.total_experience,p.username,p.skill_total,p.id,p.kills,p.deaths,p.highscoreopt,p.iron_man FROM " . GAME_BASE . "players AS p LEFT JOIN " . GAME_BASE . "experience AS e ON e.playerID = p.id WHERE p.username = '".$db->escape($curr_player1)."'") or die('Failed to fetch user1 profile');
						$FETCH_PLAYER2 = $db->query("SELECT e.exp_attack,e.exp_defense,e.exp_strength,e.exp_hits,e.exp_ranged,e.exp_prayer,e.exp_magic,e.exp_cooking,e.exp_woodcut,e.exp_fletching,e.exp_fishing,e.exp_firemaking,e.exp_crafting,e.exp_smithing,e.exp_mining,e.exp_herblaw,e.exp_agility,e.exp_thieving,p.total_experience,p.username,p.skill_total,p.id,p.kills,p.deaths,p.highscoreopt,p.iron_man FROM " . GAME_BASE . "players AS p LEFT JOIN " . GAME_BASE . "experience AS e ON e.playerID = p.id WHERE p.username = '".$db->escape($curr_player2)."'") or die('Failed to fetch user2 profile');
						if ($db->num_rows($FETCH_PLAYER1) == 0) {
							?>
						<div class="HSError">
							<p><?php echo $curr_player1 ?> was not found in the Highscore table</p>
						</div>
						<?php
						} 
						if($db->num_rows($FETCH_PLAYER2) == 0) {
						?>
						<div class="HSError">
							<p><?php echo $curr_player2 ?> was not found in the Highscore table</p>
						</div>
						<?php
						} 
						else if($db->num_rows($FETCH_PLAYER1) > 0 && $db->num_rows($FETCH_PLAYER2) > 0) {
							require load_page('highscore/compare.php');
						}
					} else {
						redirect('highscore.php?c='.$selected_highscore.'&m=ranking');
					}
				}
				?>
				</div>
			</div>
		</div>
		<?php if($selected_page == "ranking") { ?>
		<div class="col-sm-4 char-r-side">
			<div class="panel panel-default">
				<div class="panel-content">
					<h4 style="margin:0;">Search For A <strong class="text-primary">Player</strong></h4>
					<hr>
					<form action="highscore.php" method="get" id="search">
						<input type="hidden" name="m" value="ranking">
						<div class="form-group">
							<input type="text" name="user" class="form-control" placeholder="Player Name" id="user" value="<?php echo isset($curr_player) ? $curr_player : null; ?>" maxlength="12">
						</div>
						<div class="form-group">
							<button type="submit" class="btn btn-default-2 btn-block">Lookup Player</button>
						</div>
					</form>
				</div>
			</div>
			
			<div class="panel panel-default">
				<div class="panel-content">
					<h4 style="margin:0;">Compare <strong class="text-primary">Players</strong></h4>
					<hr>
					<form action="highscore.php" id="compare">
						<input type="hidden" name="m" value="compare">
						<div class="form-group">
							<input type="text" name="user1" class="form-control" placeholder="Player 1" id="user1" value="<?php echo isset($curr_player1) ? $curr_player1 : null; ?>" maxlength="12" required="required">
						</div>
						<div class="form-group">
							<input type="text" name="user2" class="form-control" placeholder="Player 2" id="user2" value="<?php echo isset($curr_player2) ? $curr_player2 : null; ?>" maxlength="12">
						</div>
						<div class="form-group">
							<button type="submit" class="btn btn-default-2 btn-block">Compare Players</button>
						</div>
					</form>
				</div>
			</div>
		</div>
		<?php } ?>
	</div>
</div>
<?php
require load_page('footer.php');
