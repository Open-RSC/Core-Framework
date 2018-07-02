<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>

<div class="base-body">
<?php
while($user = $db->fetch_assoc($FETCH_SINGLE_PLAYER)) {
	$skills = explode(',', skills);
	
	echo'
		<div class="area-body-sub">
			<div class="sub-page-header">
			<h3 class="ic_silver">Hiscores for '.$user['username'].'</h3>
			</div>
	</div>
	<div class="col-sm-6">
		<div class="highscore-profile-block">
		<div class="highscore-profile-block-img-wrap">
			<img alt="'.$user['username'].'" title="'.$user['username'].' avatar" src="'.get_player_card($user['id']).'">
		</div>
		<h2 class="highscore-profile-name">'.$user['username'].'</h2>';
		if($user['iron_man'] == 0):
			echo "<h2 class='highscore-profile-data'>Mode: Regular</h2>";
		   endif;
		if($user['iron_man'] == 1):
			echo "<h2 class='highscore-profile-data'>Mode: <img src='img/icons/ironman.png'></img> Ironman</h2>";
		   endif;
		if($user['iron_man'] == 2):
			echo "<h2 class='highscore-profile-data'>Mode: <img src='img/icons/u_ironman.png'></img> Ultimate Ironman</h2>";
		   endif;
		if($user['iron_man'] == 3):
			echo "<h2 class='highscore-profile-data'>Mode: <img src='img/icons/hc_ironman.png'></img> Hardcore Ironman</h2>";
		   endif;
		echo '<h2 class="highscore-profile-data">Member Since: '.format_time($user['creation_date'], true).'</h2>
		<h2 class="highscore-profile-data">Last Login: '.format_time($user['login_date'], true).'</h2>
	</div>
	</div>
	<div class="col-sm-6">
		';
		if($user['highscoreopt'] == 0) {
		echo '
		<div class="highscore-profile-block">
		<h2 class="megdata-block_title">General Data</h2>
		<h2 class="highscore-profile-data">Skill Total: '.number_format($user['skill_total']).'</h2>
		<h2 class="highscore-profile-data">Total XP: '.number_format($user['total_experience']).'</h2>
		<h2 class="highscore-profile-data">Kills: '.number_format($user['kills']).'</h2>
		<h2 class="highscore-profile-data">Deaths: '.number_format($user['deaths']).'</h2>
		</div>';
		} else {
			echo '<div class="highscore-profile-block"><h2 class="megdata-non-block_title">User is hidden from highscores!</h2></div>';
		}
	
	echo '</div>';
	if($user['highscoreopt'] == 0) {
		echo '<table class="table table-bordered table-hover">
			<tr>
			<thead>
			<th style="width: 50%;">Total XP</th>
			<th style="width: 25%;">Level</th>
			<th class="text-center" style="width: 5%;">Skill</th>
			</thead>
			</tr>';
		foreach ($skills as $sk) {
			if($sk == 'KD')
				continue;
			$exp = $sk != "Overall" ? $user['exp_'.strtolower($sk)] : $user['total_experience'];

	
			$level = $sk != "Overall" ? experience_to_level($exp) : number_format($user['skill_total']);

			echo '<tr class="player-row">
					<td class="lvl"><a href="?m=ranking&skill='.$sk.'&page=1">'.number_format($exp).'</a></td>
					<td class="lvl"><a href="?m=ranking&skill='.$sk.'&page=1">'.$level.'</a></td>
					<td class="exp text-center"><a href="?m=ranking&skill='.$sk.'&page=1"><img src="img/skills/'.strtolower($sk).'.gif" data-toggle="tooltip" data-placement="left" title="'.$sk.'"></a></td>
				</tr>';
		}
		echo '</table>';
	}
}
?>
</div>