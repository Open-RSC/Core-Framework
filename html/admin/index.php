<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'player');

require LUNA_ROOT.'include/common.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}

$section = isset($_GET['section']) ? $_GET['section'] : null;

$search_query = isset($_GET['player_input'])  && strlen($_GET['player_input']) <= 20 ? trim($_GET['player_input']) : null;
$search_type = isset($_GET['search_type']) && is_numeric($_GET['search_type']) && $_GET['search_type'] >= 0 && $_GET['search_type'] <= 3 ? trim($_GET['search_type']) : null;
						
function displayError($text) {
	$buildTable = "<div class='col-sm-12'><div class='alert alert-danger'>" . $text . "</div></div>";
	return $buildTable;
} 
require 'header.php';
?>
<script type="text/javascript">
function checkAll(checkWhat,command){
	var inputs = document.getElementsByTagName('input');

	for(index = 0; index < inputs.length; index++){
		if(inputs[index].name == checkWhat){
			inputs[index].checked=document.getElementById(command).checked;
		}
	}
}
</script>
<div class="content-right col-sm-10">
<?php 
if(isset($search_query) && isset($search_type)) 
{ 
	switch($search_type) 
	{
		case 0: // player Name
		$pull_player = $db->query("SELECT 
		" . GAME_BASE . "players.online, 
		" . GAME_BASE . "players.banned,  
		" . GAME_BASE . "players.offences,  
		" . GAME_BASE . "players.id,  
		" . GAME_BASE . "players.quest_points, 
		" . GAME_BASE . "players.username AS 'pusername',  
		" . GAME_BASE . "players.creation_ip, 
		" . GAME_BASE . "players.creation_date, 
		" . GAME_BASE . "players.login_date, 
		" . GAME_BASE . "players.login_ip, 
		" . GAME_BASE . "players.x,
		" . GAME_BASE . "players.y, 
		" . GAME_BASE . "players.sub_expires, 
		" . GAME_BASE . "players.owner, 
		users.username AS 'fusername', 
		users.email,
		users.jewels, 
		users.registration_ip, 
		users.registered FROM " . GAME_BASE . "players JOIN users ON " . GAME_BASE . "players.owner = users.id WHERE " . GAME_BASE . "players.username = '" . $db->escape($search_query) . "' LIMIT 0 , 1");
		if($db->num_rows($pull_player) == 1) 
		{
			$p_data = $db->fetch_assoc($pull_player);
			
			if (isset($_POST['start_item_delete'])) // Delete inventory items
			{ 
				confirm_referrer('admin/index.php');
				if($luna_user['g_id'] != LUNA_ADMIN) {
					echo displayError("You do not have the privileges to use this feature..");
					return;
				}
				
				if (empty($_POST['selected_items']))
					message_backstage(__('You must select some items to delete.', 'luna'));

				$idlist = array_values($_POST['selected_items']);
				$idlist = array_map('intval', $idlist);
				$idlist = implode(',', array_values($idlist));

				$db->query('DELETE FROM ' . GAME_BASE . 'invitems WHERE slot IN ('.$idlist.') AND playerID='.$p_data['id']) or error('Unable to delete the selected items from players inventory', __FILE__, __LINE__, $db->error());
				redirect('admin/index.php?player_input='.$p_data['pusername'].'&amp;search_type=0#itemTables');
			}
			
			if (isset($_POST['start_bank_item_delete'])) // Delete bank items
			{ 
				confirm_referrer('admin/index.php');
				
				if($luna_user['g_id'] != LUNA_ADMIN) {
					echo displayError("You do not have the privileges to use this feature..");
					return;
				}

				if (empty($_POST['selected_bank_items']))
					message_backstage(__('You must select some items to delete.', 'luna'));

				$idlist = array_values($_POST['selected_bank_items']);
				$idlist = array_map('intval', $idlist);
				$idlist = implode(',', array_values($idlist));

				$db->query('DELETE FROM ' . GAME_BASE . 'bank WHERE slot IN ('.$idlist.') AND playerID='.$p_data['id']) or error('Unable to delete the selected items from players bank', __FILE__, __LINE__, $db->error());
				redirect('admin/index.php?player_input='.$p_data['pusername'].'&amp;search_type=0#itemTables');
			}
			
			$detectExpiration = time() > $p_data['sub_expires'] ? true : false;
			$additional_chars = $db->query("SELECT username,combat,online FROM " . GAME_BASE . "players WHERE owner = '" . $p_data['owner'] . "' ORDER BY creation_date");						
			$total_played = $db->query("SELECT SUM(value) FROM " . GAME_BASE . "player_cache WHERE playerID = '" . $p_data['id'] . "' AND `key` = 'total_played'");
			$sum_playtime = $db->result($total_played);
			?>
			<div class="columns col-sm-4">
				<div class="panel-block">
					<div class="profile-block__circle-img-wrap hidden-xs">
						<img class="profile-block__image" alt="<?php echo luna_htmlspecialchars($p_data['pusername']); ?> avatar" title="<?php echo luna_htmlspecialchars($p_data['pusername']); ?> avatar" src="<?php echo get_player_card($p_data['id']) ?>">
					</div>
					<!-- Avatar !! -->
					<h2 class="profile-block__name ng-binding"><?php echo luna_htmlspecialchars($p_data['pusername']); ?></h2>
					<div class="profile-block-imp">
						<div class="profile-block__playtime ng-scope">Time played</div>
						<div class="profile-block__time"><?php echo sec2view($sum_playtime / 1000); ?></div>
					</div>
					<div class="profile-block__playtime ng-scope">Status</div>
					<div class="profile-block__time ng-binding ng-scope"><?php echo ($p_data['online'] == 1 ? '<font style="color:green;">Playing</font>' : '<font style="color:red;">Offline</font>') ?></div>
					<div class="profile-block-imp">
						<div class="profile-block__playtime ng-scope">Clan</div>
						<div class="profile-block__time">--</div>
					</div>
					<div class="profile-block-imp">
						<div class="profile-block__time ng-binding ng-scope"><?php echo (($detectExpiration == false) ? "<label class='label-primary'>Subscription Expires: <span>".format_time($p_data['sub_expires']) ."</span></label>" : "<label class='label-danger'>Not a subcriber</label>") ?></div>
					</div>
				</div>
				<div class="rest-block">
					<h2 class="rest-block__title">Game Information</h2>
					<ul class="rest-block__list">
						<li id="a-rest-block__list-item--0" class="rest-block__list-item">
							<div class="rest-block__info">Character Status<span>
							<?php 
							if($p_data['banned'] > 0 && ($p_data['banned'] / 1000) > time()) {
								echo "Temporary ban - ".format_time($p_data['banned'] / 1000)."";
							} elseif($p_data['banned'] == -1) {
								echo "Permanent";
							} else {
								echo "Not banned";
							}
							?>
							</span></div>
						</li>
						<li id="a-rest-block__list-item--0" class="rest-block__list-item">
							<div class="rest-block__info">Offences<span>
							<?php 
							echo $p_data['offences'];
							?>
							</span></div>
						</li>
						<li id="a-rest-block__list-item--0" class="rest-block__list-item">
							<div class="rest-block__info">Character Creation<span><?php echo format_time($p_data['creation_date'], true) ?></span></div>
						</li>
						<li id="a-rest-block__list-item--0" class="rest-block__list-item">
							<div class="rest-block__info">Creation IP<span><?php echo $p_data['creation_ip'] ?></span></div>
						</li>
						<li id="a-rest-block__list-item--0" class="rest-block__list-item">
							<div class="rest-block__info">Last Login IP<span><?php echo $p_data['login_ip'] ?></span></div>
						</li>
						<li id="a-rest-block__list-item--0" class="rest-block__list-item">
							<div class="rest-block__info">Last Login<span><?php echo time_ago($p_data['login_date']) ?></span></div>
						</li>
						<li id="a-rest-block__list-item--0" class="rest-block__list-item">
							<div class="rest-block__info">Game (X,Y)<span>(<?php echo $p_data['x'] ?>, <?php echo $p_data['y'] ?>)</span></div>
						</li>
						<li id="a-rest-block__list-item--0" class="rest-block__list-item">
							<div id="block-fell" class="rest-block__info" data-toggle="collapse" data-target="#acoll">Additional Game Characters<span><i class="fa fa-caret-down"></i></span></div>
							<div class="collapse" id="acoll">
							<?php if($db->num_rows($additional_chars) > 1) 
							{ 
								while($r = $db->fetch_assoc($additional_chars)) 
									echo "<a class='block-fell-a-char' href='index.php?search_type=" . $search_type . "&amp;player_input=" . str_replace(" ", "+", $r['username']) . "'><i class='fa fa-user'></i> " . str_replace(" ", "&nbsp;", ucwords($r['username'])) . "</a><i class='block-status-field'>(".($r['online'] ? "<font style='color:green;'>Playing</font>" : "Offline").")</i><span>Lv: ".$r['combat']."</span><br />";
							} 
							else 
							{ 
							?>
							<p>Could not find any additional game characters on this forum account..</p>
							<?php 
							}  
							?>
							</div>
						</li>
						<li id="a-rest-block__list-item--0" class="rest-block__list-item">
							<div class="rest-block__info">Trades<span><?php echo "<a href='trades.php?character=".urlencode($p_data['pusername'])."'>View trade logs</a>" ?></span></div>
						</li>
					</ul>
				</div>
				<div class="rest-block">
					<h2 class="rest-block__title">Forum Information</h2>
					<ul class="rest-block__list">
						<li id="a-rest-block__list-item--0" class="rest-block__list-item">
							<div class="rest-block__info">Forum Name<span><a href="<?php echo LUNA_ROOT; ?>profile.php?id=<?php echo $p_data['owner'] ?>"><?php echo luna_htmlspecialchars($p_data['fusername']) ?></a></span></div>
						</li>
						<li id="a-rest-block__list-item--0" class="rest-block__list-item">
							<div class="rest-block__info">Forum Email<span><?php echo $p_data['email'] ?></span></div>
						</li>
						<li id="a-rest-block__list-item--0" class="rest-block__list-item">
							<div class="rest-block__info">Forum Creation<span><?php echo format_time($p_data['registered'], true) ?></span></div>
						</li>
						<li id="a-rest-block__list-item--0" class="rest-block__list-item">
							<div class="rest-block__info">Forum IP<span><?php echo $p_data['registration_ip'] ?></span></div>
						</li>
						<li id="a-rest-block__list-item--0" class="rest-block__list-item">
							<div class="rest-block__info"><img src="../img/icons/jewels.png" title="Jewels" /><span><?php echo number_format($p_data['jewels']) ?></span></div>
						</li>
					</ul>
				</div>
			</div>
			<?php 
			$p_skill = $db->query("SELECT p.username,p.combat,p.skill_total,e.exp_attack,e.exp_defense,e.exp_strength,
			e.exp_hits,e.exp_ranged,e.exp_prayer,e.exp_magic,e.exp_cooking,e.exp_woodcut,e.exp_fletching,e.exp_fishing,
			e.exp_firemaking,e.exp_crafting,e.exp_smithing,e.exp_mining,e.exp_herblaw,e.exp_agility,e.exp_thieving,(e.exp_attack+e.exp_defense+e.exp_strength+
			e.exp_hits+e.exp_ranged+e.exp_prayer+e.exp_magic+e.exp_cooking+e.exp_woodcut+e.exp_fletching+e.exp_fishing+
			e.exp_firemaking+e.exp_crafting+e.exp_smithing+e.exp_mining+e.exp_herblaw+e.exp_agility+e.exp_thieving) AS total_xp FROM " . GAME_BASE . "players AS p LEFT JOIN " . GAME_BASE . "experience AS e ON e.playerID = p.id WHERE p.id = '" . $p_data['id'] . "'");
			$s_data = $db->fetch_assoc($p_skill);
			?>
			<div class="columns col-sm-4">
				<div class="rest-block">
					<h2 class="skills-block__title">Skills</h2>
					<div class="skills-block__stat-title">
						Total Level<span id="a-skills-block__stat--total-level" class="skills-block__player-stat ng-binding"><?php echo number_format($s_data['skill_total'])?></span>
					</div>
					<div class="skills-block__stat-title">
						Total XP<span id="a-skills-block__stat--total-level" class="skills-block__player-stat ng-binding"><?php echo number_format($s_data['total_xp']) ?></span>
					</div>
					<div class="skills-block__stat-title">
						Combat Level<span id="a-skills-block__stat--total-level" class="skills-block__player-stat ng-binding"><?php echo number_format($s_data['combat'])?></span>
					</div>
					<div class="skills-block__skills-container">
					<ul class="skills-block__list">
					<?php 
					for($i = 0; $i < count($skills); $i++) 
					{ 
					?>
					<li class="skills-block__item">
						<img data-toggle="tooltip" data-placement="top" title="<?php echo $skills[$i] ?> - <?php echo number_format($s_data['exp_' . $skills[$i]]) ?> XP" src="../img/skills/<?php echo $skills[$i] ?>.gif"class="skills-block__icon skills-block__icon--<?php echo $skills[$i] ?>">
						<?php echo experience_to_level($s_data['exp_' . $skills[$i]]); ?>
						</img>
					</li>
					<?php 
					} 
					?>
					</ul>
					</div>
					<a class="skills-block__base-link" href="update_stats.php?char_search=<?php echo $s_data['username'] ?>">Edit Stats <i class="fa fa-caret-right" aria-hidden="true"></i></a>
				</div>
				<?php 
				$p_name_change = $db->query("SELECT old_name, new_name, date FROM " . GAME_BASE . "name_changes WHERE playerID = '" . $p_data['id'] . "' ORDER BY date DESC LIMIT 0, 10");
				?>
				<div class="rest-block">
					<h2 class="rest-block__title">Character name changes</h2>
					<?php 
					if($db->num_rows($p_name_change) > 0) 
					{ 
					echo '<ul class="rest-block__list">';
						while($name_data = $db->fetch_assoc($p_name_change)) 
						{
					?>
						<li id="a-rest-block__list-item--0" class="rest-block__list-item">
							<div class="rest-block__info">
							<dd class="col-sm-4"><?php echo format_time($name_data['date']) ?></dd>
							<dd class="col-sm-3" style="text-align:center;"><?php echo $name_data['old_name'] ?></dd>
							<dt class="col-sm-2" style="text-align:center;"><i class="fa fa-long-arrow-right" aria-hidden="true"></i></dt>
							<dd class="col-sm-3" style="text-align:center;"><?php echo $name_data['new_name'] ?></dd>
							</div>
						</li>
					<?php 
						}
					echo "</ul>";
					} else {
						echo '
						<ul class="rest-block__list">
							<li id="a-rest-block__list-item--0" class="rest-block__list-item">
								<div class="rest-block__info">No name changes has been made from this user.</div>
							</li>
						</ul>';
				} ?>
				</div>
			</div>
			<?php 
			$p_quests = $db->query("SELECT 
				SUM(CASE WHEN stage > 0 THEN 1 ELSE 0 END) AS in_progress,
				SUM(CASE WHEN stage = -1 OR stage = -2 THEN 1 ELSE 0 END) AS completed FROM " . GAME_BASE . "quests WHERE playerID = '" . $p_data['id'] . "' " .(!MEMBERS_CONTENT ? "AND id < 17" : ""));
			$q_data = $db->fetch_assoc($p_quests);
			$completed_quests = number_format($q_data['completed']);
			$in_progress_quests = number_format($q_data['in_progress']);
			$not_started_quests = (MEMBERS_CONTENT ? 50 : 17) - ($completed_quests + $in_progress_quests);
			?>
			<div class="columns col-sm-4">
				<div class="rest-block">
					<h2 class="quests-block__title">Quests</h2>
					<div class="quests-block__container">
						<div id="quest_points"><span>QP: <?php echo number_format($p_data['quest_points']) ?></span></div>
						<div class="quests-block__header">
							<div class="quests-block__key" style="background: green"></div>
							Completed:
							<span id="quests-block__quest-stat--complete" class="quests-block__quest-stat"><?php echo $completed_quests ?></span>
						</div>
						<div class="quests-block__header">
							<div class="quests-block__key" style="background: yellow"></div>
							In Progress:
							<span id="quests-block__quest-stat--in-progress" class="quests-block__quest-stat"><?php echo $in_progress_quests ?></span>
						</div>
						<div class="quests-block__header">
							<div class="quests-block__key" style="background: #c52037"></div>
							Not Started:
							<span id="quests-block__quest-stat--not-started" class="quests-block__quest-stat"><?php echo number_format($not_started_quests) ?></span>
						</div>
					</div>
					<a class="skills-block__base-link" href="quests.php?character=<?php echo $p_data['id']?>">All Quests <i class="fa fa-caret-right" aria-hidden="true"></i></a>
				</div>
				<?php 
				$ip_load = 'SELECT DISTINCT ip, time FROM ' . GAME_BASE . 'logins WHERE playerID = '.$p_data['id'].' ORDER BY time DESC';
				$ip_result_pages = 'SELECT COUNT(*) FROM ' . GAME_BASE . 'logins WHERE playerID = '.$p_data['id'].'';
	
				$total_pages = $db->result($db->query($ip_result_pages));
				
				$num_pages = ceil($total_pages / 12);
				$p = (!isset($_GET['p']) || $_GET['p'] <= 1 || $_GET['p'] > $total_pages) ? 1 : intval($_GET['p']);
				$fix_curr_page = ($p-1) * 12;
				$load_ips = $db->query($ip_load . " LIMIT " . $fix_curr_page . " , 12");

				// Generate paging links
				$paging_links = paginate($num_pages, $p, 'index.php?player_input='.$p_data['pusername'].'&amp;search_type=0&amp;iptable');
				$countIPS = 0;
				if($db->num_rows($load_ips) > 0) 
				{ 
				echo '<ul class="rest-block__list">';
					while($ips = $db->fetch_assoc($load_ips)) 
					{
					$ipContentTable .= '<li id="a-rest-block__list-item--0" class="rest-block__list-item">
						<div class="rest-block__info">
						<dd class="col-sm-6" style="text-align:left;">'.$ips['ip'].'</dd>
						<dd class="col-sm-6" style="text-align:right;">'.format_time($ips['time']).'</dd>
						</div>
					</li>';
					$countIPS++;
					}
				echo "</ul>";
				} else {
					$ipContentTable .=
					'<ul class="rest-block__list">
						<li id="a-rest-block__list-item--0" class="rest-block__list-item">
							<div class="rest-block__info">No logged game ips for this user.</div>
						</li>
					</ul>';
				} ?>
				<div class="rest-block">
					<h2 class="rest-block__title">Last (<?php echo $fix_curr_page + $countIPS; ?></span> of <span class="total"><?php echo $total_pages; ?>) IPs Used</h2>
					<ul class="rest-block__list">
						<?php echo $ipContentTable; ?>
					</ul>
					<div class="items-block__container"><?php echo $paging_links; ?></div>
				</div>
			</div>
			
			<?php 
			$fetch_inventory = $db->query("SELECT id, amount, slot, wielded FROM " . GAME_BASE . "invitems WHERE playerID = '" . $p_data['id'] . "' ORDER BY slot ASC");
			?>
			<div class="items-block_table" id="itemTables">
				<div class="columns col-sm-4">
					<div class="rest-block">
						<h2 class="quests-block__title">Inventory</h2>
						<div class="items-block__container">
						<form method="post" action="index.php?player_input=<?php echo $p_data['pusername'] ?>&amp;search_type=0">
						<?php 
						if($db->num_rows($fetch_inventory) == 0) 
						{ 
						?>
							<p>This character has no items in inventory.</p>
						<?php 
						} 
						else 
						{ 
						?>
							<p>Below is a built replica of their inventory. Red represents equipped items.</p>
							<div class="invitem_inventory">
							<?php 
							while($inv = $db->fetch_assoc($fetch_inventory)) 
							{ 
								$img_path = "../img/items/" . $inv['id'] . ".png";
							?>
								<div class="invitem_slot_preview" <?php echo ($inv['wielded'] == 1 ? "style='background-color:#cc0000 !important;'" : null) ?>>
									<h2 class="invitem_image-text"><?php echo number_format($inv['amount']) ?></h2>
									<img src="<?php echo $img_path ?>" title="Item ID: <?php echo $inv['id'] ?>" data-toggle="tooltip" data-placement="bottom" title="Item ID: <?php echo $inv['id'] ?>" />
									<?php if($luna_user['g_id'] == LUNA_ADMIN): ?>
									<input type="checkbox" name="selected_items[]" value="<?php echo $inv['slot'] ?>" />
									<?php endif; ?>
								</div>
							<?php 
							} ?>
							</div>
							<?php 
							if($luna_user['g_id'] == LUNA_ADMIN): ?>
							<div class="invitem_select_option">							
								<span class="checkThemAll"><input type="checkbox" id="checkInvItems" onclick="checkAll('selected_items[]','checkInvItems')" value="1" /> Select All</span>
								<button class="btn btn-danger pull-right" type="submit" name="start_item_delete" onClick="return confirm('Are you sure to delete these items from this users inventory?')"><i class="fa fa-trash-o" aria-hidden="true"></i></button>
							</div>
						<?php 
							endif;
						} 
						?>
						</form>
						</div>
					</div>
				</div>
				<?php 
				$fetch_bank_total = ceil($db->num_rows($db->query("SELECT slot FROM " . GAME_BASE . "bank WHERE playerID = '" . $p_data['id'] . "'")) / 50);
				$start_page = (isset($_GET['page']) && $_GET['page'] > 0 && $_GET['page'] <= $fetch_bank_total) ? ($_GET['page'] - 1) * 50 : 0;
				
				$fetch_bank = $db->query("SELECT id, amount, slot FROM " . GAME_BASE . "bank WHERE playerID = '" . $p_data['id'] . "' ORDER BY slot ASC LIMIT " . $start_page . " , 50");
				?>
				<div class="columns col-sm-8">
					<div class="rest-block">
						<h2 class="quests-block__title">Bank</h2>
						<div class="items-block__container">
						<form method="post" action="index.php?player_input=<?php echo $p_data['pusername'] ?>&amp;search_type=0">
						<?php 
						if($db->num_rows($fetch_bank) == 0) 
						{ 
						?>
							<p>This character doesn't have any items in bank.</p>
						<?php 
							} 
							else 
							{ 
							$page = isset($_GET['page']) ? $_GET['page'] : 1;
							for($i = 0; $i < $fetch_bank_total; $i++) 
							{
								echo "<div class='bank_item_pages'><a class='bank_item_page-item' " . (($page - 1) == $i ? "style='color:red !important;'" : null) . " href='?search_type=" . $search_type . "&amp;player_input=" . $search_query . "&amp;page=" . ($i + 1) . "'>&lt;page " . ($i + 1) . "&gt;</a></div>";
							}
							?>
							<div class="bank_inventory">
							<?php 
							while($bank = $db->fetch_assoc($fetch_bank)) 
							{ 
								$img_path = "../img/items/" . $bank['id'] . ".png";
							?>
								<div class="invitem_slot_preview">
									<h2 class="invitem_image-text"><?php echo number_format($bank['amount']) ?></h2>
									<img src="<?php echo $img_path ?>" data-toggle="tooltip" data-placement="bottom" title="Item ID: <?php echo $bank['id'] ?>" />
									<?php if($luna_user['g_id'] == LUNA_ADMIN): ?>
										<input type="checkbox" name="selected_bank_items[]" value="<?php echo $bank['slot'] ?>" />
									<?php endif; ?>
								</div>
							<?php 
							} ?>
							</div>
							<?php 
							if($luna_user['g_id'] == LUNA_ADMIN): ?>
							<div class="invitem_select_option">							
								<span class="checkThemAll"><input type="checkbox" id="checkBankItems" onclick="checkAll('selected_bank_items[]','checkBankItems')" value="2" /> Select All</span>
								<button class="btn btn-danger pull-right" type="submit" name="start_bank_item_delete" onClick="return confirm('Are you sure to delete these items from this users bank?')"><i class="fa fa-trash-o" aria-hidden="true"></i></button>
							</div>
						<?php 
							endif;
						} ?>
						</form>
						</div>
					</div>
				</div>
			</div>
			<?php
		} 
		else 
		{
			echo displayError('Sorry, that player could not be found.');
			require 'search.php';
		}
		break;
		case 1: // Forum Name
			$find_account = $db->query("SELECT id,registration_ip,username, email FROM users WHERE username = '" . $db->escape($search_query) . "';");
			if($db->num_rows($find_account) > 0) 
			{
				require 'search.php';
				$fetch = $db->fetch_assoc($find_account);
				echo "
				<div class='col-sm-9'>
					<table class='base-tbl'>
						<tr>
							<th colspan='2' class='active'><span class='indicator'>Forum Account Info for " . $search_query . "</span></th>
						</tr>
						<tr>
						<td width='20%'>Forum Name</td>
						<td>
							<a href='" . LUNA_ROOT . "profile.php?id=" . $fetch['id'] . "'>" . ucwords($search_query) . "</a>
						</td>
						</tr>
						<tr>
						<td width='20%'>Forum Email</td>
						<td>
							<span>" . $fetch['email'] . "</span></a>
						</td>
						</tr>
						<tr>
						<td width='20%'>Forum Registration IP</td>
						<td>
							<a href='index.php?player_input=" . $fetch['registration_ip'] . "&amp;search_type=2'>" . $fetch['registration_ip'] . "</a>
						</td>
						</tr>
				";
				$find_forum_acc = $db->query("SELECT username,registration_ip FROM users WHERE registration_ip = '" . $fetch['registration_ip'] . "'");
				if($db->num_rows($find_forum_acc) > 1) 
				{
					echo"
						<tr>
							<td>Matching Forum Accounts</td>
							<td>
					";
					while($r = $db->fetch_assoc($find_forum_acc)) 
					{
						if($r['registration_ip'] != '0.0.0.0')
							echo "<a href='index.php?player_input=" . urlencode($r['username']) . "&amp;search_type=1'>" . $r['username'] . "</a>, ";
					}
					echo "
							</td>
						</tr>
					";
				}
				$find_other_chars = $db->query("SELECT username, id FROM " . GAME_BASE . "players WHERE owner = '" . $fetch['id'] . "'");
				if($db->num_rows($find_other_chars) > 0)
				{
					echo"
						<tr>
							<td>Linked Characters</td>
							<td>
					";	
					while($r = $db->fetch_assoc($find_other_chars))
					{
						echo "<a href='index.php?player_input=" . urlencode($r['username']) . "&amp;search_type=0'>" . $r['username'] . "</a>, ";
					}
					echo"						
							</td>
						</tr>
					";
				}
				echo"
					</tbody>
				</table>
				</div>
				";
			} 
			else 
			{
				echo displayError("Forum Account '" . $search_query . "' does not exist.");
				require 'search.php';
			}
		break;
		case 2: // IP Address
			$ip_check = explode(".", $search_query);
			if(preg_match("/^([1-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])" . "(\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3}$/", $search_query))
			{
				require 'search.php';
				$find_play = $db->query("SELECT username FROM " . GAME_BASE . "players WHERE creation_ip = '" . $db->escape($search_query) . "'");
				$player_login_ip = $db->query("SELECT username FROM " . GAME_BASE . "players WHERE login_ip = '" . $db->escape($search_query) . "'");
				$find_accs = $db->query("SELECT username FROM users WHERE registration_ip = '" . $db->escape($search_query) . "';");
				$rscl_login = $db->query("SELECT DISTINCT(" . GAME_BASE . "logins.playerID)," . GAME_BASE . "players.username FROM " . GAME_BASE . "logins JOIN " . GAME_BASE . "players ON " . GAME_BASE . "logins.playerID = " . GAME_BASE . "players.id WHERE ip = '" . $db->escape($search_query) . "';");
				
				echo "
				<div class='col-sm-9'>
					<table class='base-tbl'>
					<tbody>
					<tr>
						<th colspan='2' class='active'><span class='indicator'>IP Matches on '" . $search_query . "'</span></th>
					</tr>
					<tr>
						<td width='20%'>Game - Creation IP</td>
					<td>
				";
				if($db->num_rows($find_play) > 0)
				{
					while($r = $db->fetch_assoc($find_play))
					{
						echo "<a href='index.php?player_input=" . urlencode($r['username']) . "&amp;search_type=0'>" . $r['username'] . "</a><br />";
					}
				} 
				else 
				{
					echo "No matches found.";
				}
				echo"					
					</td>
					</tr>
					<tr>
						<td width='20%'>Game - Login IP</td>
					<td>
				";
				if($db->num_rows($player_login_ip) > 0)
				{
					while($r = $db->fetch_assoc($player_login_ip))
					{
						echo "<a href='index.php?player_input=" . urlencode($r['username']) . "&amp;search_type=0'>" . $r['username'] . "</a>, ";
					}
				} 
				else 
				{
					echo "No matches found.";
				}
				echo"					
					</td>
					</tr>
					<tr>
						<td width='20%'>Included Forum Accounts</td>
					<td>
				";
				if($db->num_rows($find_accs) > 0)
				{
					while($r = $db->fetch_assoc($find_accs))
					{
						echo "<a href='index.php?player_input=" . urlencode($r['username']) . "&amp;search_type=1'>" . $r['username'] . "</a><br />";
					}
				} 
				else 
				{
					echo "No matches found.";
				}
				echo"				
					</td>
					</tr>
					<tr>
						<td width='20%'>Game - Distinct Login IP</td>
					<td>
				";
				if($db->num_rows($rscl_login) > 0)
				{
					while($r = $db->fetch_assoc($rscl_login))
					{
						echo "<a href='index.php?player_input=" . urlencode($r['username']) . "&amp;search_type=0'>" . $r['username'] . "</a>, ";
					}
				} 
				else 
				{
					echo "No matches found.";
				}
				echo"					
					</td>
					</tr>
					</tbody>
					</table>
					</div>
				";
			} 
			else 
			{
				echo displayError("Invalid IP format.");
				require 'search.php';
			}
		break;
	}
} 
else 
{ 
	require 'search.php';
} 
?>
</div>
<?php
require 'footer.php';
