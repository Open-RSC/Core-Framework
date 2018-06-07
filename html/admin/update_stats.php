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

if ($luna_user['g_id'] != LUNA_ADMIN) {
	header("Location: ../backstage/login.php");
    exit;
}

if(isset($_GET['char_search']) && strlen($_GET['char_search']) >= 1 && strlen($_GET['char_search']) <= 12) {
		$find_user = $db->query("SELECT " . GAME_BASE . "experience.* FROM " . GAME_BASE . "players JOIN " . GAME_BASE . "experience ON " . GAME_BASE . "players.id = " . GAME_BASE . "experience.playerID WHERE username = '" . $db->escape($_GET['char_search']) . "'");
		if($db->num_rows($find_user) > 0) {
			$fetch_skills = $db->fetch_assoc($find_user);
			if(isset($_POST['updatestats'])) {
				confirm_referrer('admin/update_stats.php');
				$build_exp_ql = null;
				$build_cur_ql = null;
				for($i = 0; $i < count($skills); $i++) {
					$updated_skill[$skills[$i]] = isset($_POST['update_' . $skills[$i]]) && $_POST['update_' . $skills[$i]] > 0 && $_POST['update_' . $skills[$i]] <= 99 ? $_POST['update_' . $skills[$i]] : 1;
					$build_cur_ql .= ($skills[$i] == 'hits') ? null : "cur_" . $skills[$i] . " = " . "'" . $updated_skill[$skills[$i]] . "',";
					$exp_skill[$skills[$i]] = isset($exps[($updated_skill[$skills[$i]]) - 2]) ? $exps[($updated_skill[$skills[$i]]) - 2] : 0;
					if($updated_skill[$skills[$i]] >= experience_to_level($fetch_skills['exp_' . $skills[$i]])){
						$fix_exp_skill[$skills[$i]] = ($fetch_skills['exp_' . $skills[$i]] > $exp_skill[$skills[$i]]) ? $fetch_skills['exp_' . $skills[$i]] : $exp_skill[$skills[$i]];
					} else {
						$fix_exp_skill[$skills[$i]] = $exp_skill[$skills[$i]];
					}
					$build_exp_ql .= ($skills[$i] == 'hits') ? null :  "exp_" . $skills[$i] . " = " . "'" . $fix_exp_skill[$skills[$i]] . "',";
				}
				$auto_calc_hits = ceil(($fix_exp_skill['attack'] + $fix_exp_skill['defense'] + $fix_exp_skill['strength']) / 3) + 1154;
				$convert_calc_hits = experience_to_level($auto_calc_hits);
				//$db->query("UPDATE " . GAME_BASE . "players SET exp_hits = '" . $auto_calc_hits . "', " . substr($build_exp_ql, 0, -1) . ", cur_hits = '" . $convert_calc_hits . "', " . substr($build_cur_ql, 0, -1) . " WHERE id = '" . $db->escape($fetch_skills['id']) . "'") or error('Unable to update player stats', __FILE__, __LINE__, $db->error());
				$db->query("UPDATE " . GAME_BASE . "experience SET exp_hits = '" . $auto_calc_hits . "', " . substr($build_exp_ql, 0, -1) . " WHERE playerID = '" . $fetch_skills['playerID'] . "'");
				$db->query("UPDATE " . GAME_BASE . "curstats SET cur_hits = '" . $convert_calc_hits . "', " . substr($build_cur_ql, 0, -1) . " WHERE playerID = '" . $fetch_skills['playerID'] . "'");
								
				new_notification($luna_user['id'], '#', __('You have updated '. luna_htmlspecialchars($_GET['char_search']) . ' stats via admin panel.', 'luna'), 'fa-bar-chart');
				redirect('admin/update_stats.php?char_search=' . luna_htmlspecialchars($_GET['char_search']) . '&amp;saved=true');
			}
		} else {
			message_backstage(__('Could not find the player name.', 'luna'));
		}
require 'header.php';	
?>
<div class="content_wrapper col-sm-10">
	<form method="post" action="update_stats.php?char_search=<?php echo $_GET['char_search'] ?>">
	<div class="row title">
		<div class="small-12 columns">
			<h2 class="page-heading">Edit Stats for <?php echo $_GET['char_search']?> <span class="pull-right"><button class="block-btn block-btn--form" name="updatestats" type="submit">Update Stats</button></span></h2>
		</div>
	</div>
	<div class="small-12 columns">
	<?php
	if (isset($_GET['saved']))
		echo '<div class="alert alert-success"><i class="fa fa-fw fa-check"></i> '.__('Stats has been updated!', 'luna').'</div>';
	?>
	<table class="base-tbl">
		<tbody>
			<tr>
				<th><span class="indicator">Skill</span></td>
				<th><span class="indicator">Set Level</span></td>
				<th><span class="indicator">Experience</span></td>
			</tr>
			<?php 
			for($i = 0; $i < count($skills); $i++) {
				if($skills[$i] == 'hits'){
					echo "
						<tr>
							<td><img src='../img/skills/hits.gif' alt='x' /> <span class='base-tbl__highlight'>Hits</span> (Auto calculated)</td>
							<td>" . experience_to_level($fetch_skills['exp_' . $skills[$i]]) . "</td>
							<td>" . number_format($fetch_skills['exp_' . $skills[$i]]) . "</td>
						</tr>
					";
				} else {
					echo "
						<tr>
							<td><img src='../img/skills/" . $skills[$i] . ".gif' alt='x' /> <span class='base-tbl__highlight'>" . ucfirst($skills[$i]) . "</span></td>";
					echo"
							<td><input class='form-control input-sm' type='text' maxlength='2' size='2' name='update_" . $skills[$i] . "' value='" . experience_to_level($fetch_skills['exp_' . $skills[$i]]) . "' /></td>
							<td>" . number_format($fetch_skills['exp_' . $skills[$i]]) . "</td>
						</tr>
					";
				}
			}
		?>
		</tbody>
	</table>
	<button class="block-btn block-btn--form" name="updatestats" type="submit">Update Stats</button>
	</div>
	</form>
</div>
<?php } else { 
require 'header.php';	
?>
<div class="content_wrapper col-sm-10">
	<div class="row title">
		<div class="small-12 columns">
			<h2 class="page-heading">Search for a Player</h2>
		</div>
	</div>
	<div class="small-12 columns">
	<form class="form-horizontal" method="get" action="">
		<div class="form-group">
		<label class="search-form__label" for="player">
			Player Name
		</label>
		<div class="searchTab">
			<input class="form-control" type="text" name="char_search" value="<?php echo (isset($_GET['char_search']) ? $_GET['char_search'] : null) ?>" maxlength="12" />
		</div>
		<button class="block-btn block-btn--form" type="submit">Search</button>
		</div>
	</form>
	</div>
</div>
<?php 
} 
require 'footer.php';