<?php
define('LUNA_ROOT', './');
require LUNA_ROOT.'include/common.php';

$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), "Adventurer log");
define('LUNA_ALLOW_INDEX', 1);
require load_page('header.php');

// Did they select a player?
$getPlayer = isset($_GET["player"]) && trim($_GET["player"]) ? trim($_GET["player"]) : $getActiveChar['username'];

?>
<div id="wrapper" class="container">
	<div class="character" id="adventurer_log">
		<?php 
		if(!$luna_user['is_guest'] || isset($getPlayer)) 
		{
			?>
			<div class="adv_plank">
			<span class="adv_breadcrumb">
			<a href="http://wolfkingdom.net">Home</a> / 
			<a href="http://wolfkingdom.net/adventure.php">Adventurer Log</a>
			<?php
			if(isset($getPlayer)) 
				echo '/ <a href="adventure.php?player='.urlencode($getPlayer).'" class="active">'.ucwords($getPlayer).'</a>';
			?>
			</span>
				<div class="pull-right">
					<form id="search" class="input-group" method="get" action="adventure.php">
						<div class="input-group">
							<input type="text" class="form-control seacherer-form inbound" name="player" maxlength="12" placeholder="Search for a Player">
							<span class="input-group-btn"><button class="btn btn-default-2" type="submit">Search</button></span>									
						</div>
					</form>
				</div>
			</div>
			<?php
		}
		?>
		<div class="panel panel-default">
			<div class="content-header content-header--highlight">
				<h2 class="content-header_adv-title">Adventurer Log</h2>
			</div>
			<?php if (!$luna_user['is_guest'] || isset($getPlayer)) { ?>
			<div class="embended-info">
				<p class="big_info"><i class="fa fa-info-circle" aria-hidden="true"></i> Welcome to the RSCLegacy Adventurer's Log. All of your Adventurer's log data is available for <b>FREE</b>. </p>
			</div>
			<div class="panel-body">
			<?php 
			$character_sql_statement = "
				SELECT 
					`" . GAME_BASE . "players`.`username`,
					`" . GAME_BASE . "players`.`owner`,
					`" . GAME_BASE . "players`.`id`,
					`" . GAME_BASE . "players`.`male`,
					`" . GAME_BASE . "players`.`online`,
					`" . GAME_BASE . "players`.`sub_expires`,
					`" . GAME_BASE . "players`.`platinum_expires`,
					`" . GAME_BASE . "players`.`quest_points`,
					`" . GAME_BASE . "players`.`skill_total`,
					`" . GAME_BASE . "experience`.`exp_attack`,
					`" . GAME_BASE . "experience`.`exp_defense`,
					`" . GAME_BASE . "experience`.`exp_strength`,
					`" . GAME_BASE . "experience`.`exp_hits`,
					`" . GAME_BASE . "experience`.`exp_ranged`,
					`" . GAME_BASE . "experience`.`exp_prayer`,
					`" . GAME_BASE . "experience`.`exp_magic`,
					`" . GAME_BASE . "experience`.`exp_cooking`,
					`" . GAME_BASE . "experience`.`exp_woodcut`,
					`" . GAME_BASE . "experience`.`exp_fletching`,
					`" . GAME_BASE . "experience`.`exp_fishing`,
					`" . GAME_BASE . "experience`.`exp_firemaking`,
					`" . GAME_BASE . "experience`.`exp_crafting`,
					`" . GAME_BASE . "experience`.`exp_smithing`,
					`" . GAME_BASE . "experience`.`exp_mining`,
					`" . GAME_BASE . "experience`.`exp_herblaw`,
					`" . GAME_BASE . "experience`.`exp_agility`,
					`" . GAME_BASE . "experience`.`exp_thieving`,
					`" . GAME_BASE . "players`.`highscoreopt`,
					(" . GAME_BASE . "experience.exp_attack+" . GAME_BASE . "experience.exp_defense+" . GAME_BASE . "experience.exp_strength+" . GAME_BASE . "experience.exp_hits+" . GAME_BASE . "experience.exp_ranged+" . GAME_BASE . "experience.exp_prayer+" . GAME_BASE . "experience.exp_magic+" . GAME_BASE . "experience.exp_cooking+" . GAME_BASE . "experience.exp_woodcut+" . GAME_BASE . "experience.exp_fletching+" . GAME_BASE . "experience.exp_fishing+" . GAME_BASE . "experience.exp_firemaking+" . GAME_BASE . "experience.exp_crafting+" . GAME_BASE . "experience.exp_smithing+" . GAME_BASE . "experience.exp_mining+" . GAME_BASE . "experience.exp_herblaw+" . GAME_BASE . "experience.exp_agility+" . GAME_BASE . "experience.exp_thieving) AS total_xp
				FROM 
					`" . GAME_BASE . "players` LEFT JOIN `" . GAME_BASE . "experience` ON `" . GAME_BASE . "experience`.`playerID` = `" . GAME_BASE . "players`.`id`
				WHERE 
					`" . GAME_BASE . "players`.`username` = '" . $db->escape($getPlayer) . "' 
				LIMIT 0, 1
			";
			$check_for_char = $db->query($character_sql_statement);
			if($db->num_rows($check_for_char) && isset($getPlayer))
			{
				$pull_char = $db->fetch_assoc($check_for_char);
				
				function getTotalRank($user) {
					global $db;
					$tot = "skill_total";
					$stmt = $db->query("SELECT username FROM " . GAME_BASE . "players WHERE ".$tot." > 27 ORDER BY ".$tot." DESC");
					$count = 1;
					while ($entry = $db->fetch_assoc($stmt)) {
						if (strtolower($entry['username']) == strtolower($user)) {
							return $count;
						} 
						$count++;
					}
				}
			?>
			<div class="col-sm-6">
				<div class="adv-myuser">
					<div class="adv_userlabel content-r-side">
						<div class="adv_userlabel-block">
							<?php 
							$total_played = $db->query("SELECT SUM(value) FROM " . GAME_BASE . "player_cache WHERE playerID = '" . $db->escape($pull_char["id"]) . "' AND `key` = 'total_played'");
							$sum_playtime = $db->result($total_played);
							$detectExpiration = time() > $pull_char['sub_expires'] ? true : false;
							if($luna_user['id'] == $pull_char['owner']) //check so we own the char and then pull dropdown list.
							{
								$find_user_characters = @$db->query("SELECT username, id FROM " . GAME_BASE . "players WHERE owner = '" . $db->escape($pull_char["owner"]) . "' limit 0, 10");
								if($db->num_rows($find_user_characters) > 0) // more than one character = give list only for YOURSELF not as a viewer.
								{
							?>
								<li class="dropdown">
								<a href="char_manager.php?id=<?php echo $pull_char['owner']?>" class="dropdown-toggle character-selected_holder" data-toggle="dropdown">
									<div class="character-selected-details">
										<span class="character-selected-username clearfix"><?php echo $pull_char['username']; ?></span>
										<span class="character-selected_arrow"><i class="fa fa-angle-down" aria-hidden="true"></i></span>
									</div>
								</a>
								<ul class="dropdown-menu">
								<?php 
								while($fetch_chars = $db->fetch_assoc($find_user_characters))
									if($pull_char['username'] != $fetch_chars['username'])
										echo '<li><a href="adventure.php?player='.urlencode($fetch_chars['username']).'"><i class="fa fa-user"></i> '.ucwords($fetch_chars['username']).'</a></li>';
								?>
								</ul>
								</li>
								<?php 
								}
							} 
							else 
							{
								?>
								<a href="char_manager.php?id=2" class="character-selected_holder">
									<div class="character-selected-details">
										<span class="character-selected-username clearfix"><?php echo $pull_char['username']; ?></span>
									</div>
								</a>
								<?php
							}
							?>
						</div>
						<div class="adv_userlabel-avatar">
							<div class="adv_userlabel_avatarcard">
								<img src="<?php echo get_player_card($pull_char['id']); ?>" width="65" height="115" alt="">
							</div>
							<div class="adv_userinfo">
							<p>Gender: <?php echo ($pull_char['male'] == 1 ? 'Male' : 'Female')?></p>
							<p>Time Played: <?php echo sec2view($sum_playtime / 1000); ?></p>
							<p>Status: <?php echo ($pull_char['online'] == 1 ? '<font style="color:green;">Playing</font>' : '<font style="color:red;">Offline</font>') ?> </p>
							<p>Clan: <?php echo '--'; ?></p>
							<p>Player Title: 
							<?php 
							if(time() > $pull_char['platinum_expires']) 
								echo (($detectExpiration == false) ? "Subscriber" : "Member");
							else
								echo "Premium Subscriber"; 
							?>
							</p>
							</div>
						</div>
					</div>
				</div>
				<div class="adv_blockset content_advblock">
					<div class="adv_headerblock">
						<h2 class="block-heading">Recent Events</h2>
					</div>
					<div class="content_advblock">
					<div class="panel-group" role="tablist" id="events" aria-multiselectable="true"> 
						<?php 
						$find_events = @$db->query("SELECT username, message, time FROM " . GAME_BASE . "live_feeds WHERE username = '" . $db->escape($pull_char["username"]) . "' ORDER BY time LIMIT 0, 10");
						if($db->num_rows($find_events) > 0 ) {
							$id = 1;
							while($event = $db->fetch_assoc($find_events)) { ?>
							<div class="adv_event_block"> 
								<div class="panel-heading" role="tab" id="guide<?php echo $id ?>"> 
									<h4 class="panel-title"> 
										<a data-target="#feed<?php echo $id ?>" role="button" data-toggle="collapse" data-parent="#feed<?php echo $id ?>" aria-expanded="false" aria-controls="#feed<?php echo $id ?>" class="collapsed"> 
										<?php echo '<i class="fa fa-caret-right" aria-hidden="true"></i> '.$event['username'] ?>
										</a> 
									</h4> 
								</div> 
								<div class="panel-collapse collapse" role="tabpanel" id="feed<?php echo $id ?>" aria-labelledby="guide<?php echo $id ?>" aria-expanded="false" style="height: 0px;"> 
									<div class="panel-body"> 
											<?php echo $event['message'] . "\n" ?>
									</div> 
								</div> 
							</div> 
							<?php 
							$id++;
							} 
						} else {
							echo '<p>There are no activity to display.</p>';
						}
						?>
					</div>
					</div>
				</div>
				<div class="adv_blockset content_advblock">
					<div class="adv_headerblock">
						<h2 class="block-heading">Recent Achievements</h2>
					</div>
					<div class="content_advblock">
					<div class="panel-group" role="tablist" id="events" aria-multiselectable="true"> 
						<?php 
							echo '<p>No achievements earned.</p>';
						?>
					</div>
					</div>
				</div>
			</div>
			<div class="col-sm-6">
				<div class="adv_blockset content_advblock">
					<?php if($pull_char['highscoreopt'] == 0) { ?>
					<div class="adv_headerblock">
					<h5 class="advheader_title">Total level</h5>
					<div class="advtotal_adr">
					<span class="cblevel">Skill Total: <?php echo $pull_char['skill_total'] ?></span> - <span class="rank">Rank: <?php echo (getTotalRank($pull_char['username']) == '' ? 'Unranked' : getTotalRank($pull_char['username']) ) ?></span> - <span class="xp">XP: <?php echo number_format($pull_char['total_xp']) ?></span>
					</div>
					</div>
					<div class="skills-block__skills-container">
					<ul class="skills-block__list">
					<?php 
					foreach(getSkills() as $skills) 
					{
					$level = $skills != "Overall" ? experience_to_level($pull_char['exp_'.strtolower($skills)]) : number_format($pull_char['skill_total']);
					?>
						<li class="skills-block__item">
							<img title="" src="img/skills/<?php echo strtolower($skills) ?>.gif" class="skills-block__icon"><span class="skills-block__icon--<?php echo strtolower($skills) ?>"><?php echo $level ?></span>						
						</li>		
					<?php 
					}
					?>
					</ul>
					</div>
					<?php } else {
						?>
						<div class="adv_headerblock">
							<h5 class="advheader_title">Total level</h5>
							<div class="advtotal_adr">
								<span class="cblevel" style="color:#ff0000;">User has chosen to hide their stats!</span>
							</div>
						</div>
						<?php
					} ?>
				</div>
				<?php 
				$fetch_completed_quests = $db->query("SELECT id, stage FROM " . GAME_BASE . "quests WHERE playerID = '" . $db->escape($pull_char["id"]) . "' AND stage IN(-1, -2) ORDER BY id");
				$fetch_started_quests = $db->query("SELECT id, stage FROM " . GAME_BASE . "quests WHERE playerID = '" . $db->escape($pull_char["id"]) . "' AND stage > 0 ORDER BY id");
				$fetch_quest_query = $db->query("SELECT id, stage FROM " . GAME_BASE . "quests WHERE playerID = '" . $db->escape($pull_char["id"]) . "'  ORDER BY id");
				?>
				<div class="adv_blockset content_advblock">
					<div class="adv_headerblock">
						<h5 class="advheader_title">Quests</h5>
					</div>
					<div class="adv_contentin">
						<ul class="adv quest_tabs">
							<li role="presentation" class="active"><a href="#completed" aria-controls="completed" role="tab" class="completed" data-toggle="tab">Completed<span><?php echo $db->num_rows($fetch_completed_quests) ?></span></a></li>
							<li role="presentation"><a href="#started" aria-controls="started" role="tab" data-toggle="tab">Started<span><?php echo $db->num_rows($fetch_started_quests) ?></span></a></li>
							<li role="presentation"><a href="#notstarted" aria-controls="notstarted" role="tab" data-toggle="tab">Not Started<span><?php echo (49 - $db->num_rows($fetch_quest_query)) ?></span></a></li>
						</ul>
						<hr class="draw-line">
						<div class="tab-content">
							<div role="tabpanel" class="tab-pane active" id="completed">
								<?php 
								if($db->num_rows($fetch_completed_quests) > 0) 
								{
									while($q_statement = $db->fetch_assoc($fetch_completed_quests))
									{
										echo '<p class="completed_q"><i class="fa fa-check" aria-hidden="true"></i> '.questNameById($q_statement['id']).'</p>';
									}
								} 
								else 
								{
									echo '<p>This user has not yet completed any quests.</p>';
								}
								?>
							</div>
							<div role="tabpanel" class="tab-pane" id="started">
								<?php 
								if($db->num_rows($fetch_started_quests) > 0) 
								{
									while($q_statement = $db->fetch_assoc($fetch_started_quests))
									{
										echo '<p class="started_q">'.questNameById($q_statement['id']).'</p>';
									}
								} 
								else 
								{
									echo '<p>This user has no quests in progress.</p>';
								}
								?>
							</div>
							<div role="tabpanel" class="tab-pane" id="notstarted">
								<?php 
								$quest_num = range(0, (MEMBERS_CONTENT ? 49 : 16));
								foreach ($quest_num as $key) 
								{
									if($db->num_rows($fetch_quest_query) > 0) 
									{
										while($q = $db->fetch_assoc($fetch_quest_query)) 
										{
											unset($quest_num[$q['id']]);
										}
									}
									if(!empty($quest_num[$key]))
										echo '<p class="notstarted_q">'.questNameById($quest_num[$key]).'</p>';
								}
								?>
							</div>
						</div>
						
						<hr class="draw-line">
						<?php echo '<p class="adv_qp">Quest Points: '.$pull_char['quest_points'].' </p>'; ?>
					</div>
				</div>
				<?php 
				$friend_query = $db->query("SELECT f.playerID, f.friend, f.friendName, p.id, p.online, p.sub_expires, p.platinum_expires FROM " . GAME_BASE . "friends AS f INNER JOIN " . GAME_BASE . "players AS p ON f.friendName = p.username WHERE f.playerID = '" . $db->escape($pull_char["id"]) . "' ORDER BY f.friendName ASC LIMIT 0, 200");
				?>
				<div class="adv_blockset content_advblock">
					<div class="adv_headerblock">
					<h5 class="advheader_title">Friend List</h5>
					</div>
					<?php 
					if($db->num_rows($friend_query) > 0) 
					{
					?>
					<table id="adv_friendlist" class="table table-fixed table-friendlist scrollbar">
					<thead>
					<tr>
						<th>Friend</th>
						<th>Name / #Clan</th>
						<th>Title</th>
						<th>Status</th>
					</tr>
					</thead>
					<tbody>
						<?php
							while($friend = $db->fetch_assoc($friend_query)) 
							{
								$detectExpiration = time() > $friend['sub_expires'] ? true : false;
								echo 
								'
								<tr class="adv_tablebody">
								<td class="cColumn" width="10%"><div class="friendthumbnail"><img src="'.get_player_card($friend['id']).'" width="65" height="115"></div></td>
								<td class="friend_username"><a href="adventure.php?player='.urlencode($friend['friendName']).'">'.ucwords($friend['friendName']).'</a></td>
								<td class="cColumn" width="10%">';
								if(time() > $friend['platinum_expires']) 
									echo (($detectExpiration == false) ? "Subscriber" : "Member");
								else
									echo "Premium"; 
								echo '</td>
								<td class="cColumn" width="10%"><i class="online_status'.($friend['online'] == 1 ? ' -true' : '').'" data-toggle="tooltip" data-placement="top" title="'.($friend['online'] == 1 ? 'Playing' : 'Offline').'"></i></td>
								</tr>
								';
							}
						?>
					</tbody>
					</table>
					<?php 
					} else {
						echo '<p>This users friendlist is empty..</p>';
					}
					?>
				</div>
			</div>
				<?php
			} else {
				echo "<p>Could not find any character by that name...</p>";
			}
			?>
			</div>
			<?php } else { ?>
			<div class="embended-info">
				<p class="big_info"><i class="fa fa-info-circle" aria-hidden="true"></i> Welcome to the RSCLegacy Adventurer's Log. All of your Adventurer's log data is available for <b>FREE</b>. Start by logging in or searching for a player below.</p>
			</div>
			<div class="panel-body">
				<div class="col-sm-6 content-l-side">
					<div class="adventure_block">
						<h2 class="page-heading">Log in</h2>
						<div class="content_advblock">
							<p>To view your Adventurer's Log</p>
							<a href="#" data-toggle="modal" data-target="#login-form" class="btn btn-transparent">Log In</a>
						</div>
					</div>
				</div>
				<div class="col-sm-6 char-r-side">
					<div class="adventure_block">
						<h2 class="page-heading">Search for a Player</h2>
						<div class="content_advblock">
							<p>To view another Adventurer's Log</p>
							<form id="search" class="searcherer input-group" method="get" action="adventure.php">
								<div class="input-group">
									<input type="text" class="form-control seacherer-form" name="player" maxlength="12" placeholder="Enter the player name here">
									<span class="input-group-btn"><button class="btn btn-default-2" type="submit">Search</button></span>									
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>
			<?php } ?>
		</div>
	</div>
</div>
<?php
require load_page('footer.php');
