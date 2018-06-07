<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>

<div class="base-body">
<?php
	$skills = explode(",", skills);

	$player = $db->fetch_assoc($FETCH_PLAYER1);
	$other = $db->fetch_assoc($FETCH_PLAYER2);
	
	echo'
		<div class="area-body-sub">
			<div class="sub-page-header">
			<h3 class="ic_silver">Compare Players</h3>
			</div>
	</div>
	<div class="col-sm-12">
	<div class="col-sm-4">
		<section class="playerA">
			<div class="playerAvatar playerLeft">
				<div class="frame">
				<img alt="'.$player['username'].'" title="'.$player['username'].' avatar" src="'.get_player_card($player['id']).'">
				</div>
			</div>
			<h2>'.$player['username'].'</h2>
			<h2 class="mode_status">'.($player['iron_man'] == 1 ? "<img src='img/icons/ironman.png'></img> Ironman" : ($player['iron_man'] == 2 ? "<img src='img/icons/u_ironman.png'></img> Ultimate Ironman" : ($player['iron_man'] == 3 ? "<img src='img/icons/hc_ironman.png'></img> Harcore Ironman" : "Regular"))).'</h2>
		</section>
	</div>
	<div class="col-sm-4">
	<div class="pVSp">
	VS
	</div>
	</div>
	<div class="col-sm-4">
		<section class="playerB">
			<div class="playerAvatar playerRight">
				<div class="frame">
				<img alt="'.$other['username'].'" title="'.$other['username'].' avatar" src="'.get_player_card($other['id']).'">
				</div>
			</div>
			<h2>'.$other['username'].'</h2>
			<h2 class="mode_status">'.($other['iron_man'] == 1 ? "<img src='img/icons/ironman.png'></img> Ironman" : ($other['iron_man'] == 2 ? "<img src='img/icons/u_ironman.png'></img> Ultimate Ironman" : ($other['iron_man'] == 3 ? "<img src='img/icons/hc_ironman.png'></img> Harcore Ironman" : "Regular"))).'</h2>
		</section>
	</div>
	</div>
	';
	if($player['highscoreopt'] == 0 && $other['highscoreopt'] == 0) {
	?>
	<table class="table table-bordered table-hover">
		
		<thead>
		<tr>
			
			<!--<th>Rank</th>
			
				<td><a href="?m=ranking&skill='.$skill.'&page=1">'.($level1 == 1 ? '--' : $level1.''.$class1).'</a></td>

				<td class="text-right"><a href="?m=ranking&skill='.$skill.'&page=1">'.($rank2 == '' ? '--' : $rank2).'</a></td>
				
			-->
			<th class="hidden-xs">Total XP</th>
			<th>Level</th>

			<th></th>

			<th class="text-right">Level</th>
			<th class="text-right hidden-xs">Total XP</th>
			<!--<th class="text-right">Rank</th>-->
			
		</tr>
		</thead>
		<tbody>
		
		<?php 
		foreach ($skills as $skill) {
			if($skill == 'KD')
				continue;
			$exp1 = $skill != "Overall" ? $player['exp_'.strtolower($skill)] : $player['total_experience'];
			$exp2 = $skill != "Overall" ? $other['exp_'.strtolower($skill)] : $other['total_experience'];
			
			//$rank1 = $player == null ? 0 : getRank($player['username'], $skill, $player['id'], $player['iron_man']);
			//$rank2 = $other == null ? 0 : getRank($other['username'], $skill, $other['id'], $other['iron_man']);
			
			$level1 = $skill != "Overall" ? experience_to_level($exp1) : number_format($player['skill_total']);
			$level2 = $skill != "Overall" ? experience_to_level($exp2) : number_format($other['skill_total']);
			
			$class1 = (($rank1 < $rank2) || ($player['exp_'.strtolower($skill)] > $other['exp_'.strtolower($skill)] && $player['exp_'.strtolower($skill)] != $other['exp_'.strtolower($skill)]) ? "<i class='winnerA fa fa-caret-left'></i>" : "");
			$class2 = (($rank1 > $rank2) || ($player['exp_'.strtolower($skill)] < $other['exp_'.strtolower($skill)] && $player['exp_'.strtolower($skill)] != $other['exp_'.strtolower($skill)]) ? "<i class='winnerB fa fa-caret-right'></i>" : "");
			
			echo '
			<tr>
				
				<td><a href="?m=ranking&skill='.$skill.'&page=1">'.($rank1 == '' ? '--' : $rank1).'</a></td> 
				<td class="hidden-xs"><a href="?m=ranking&skill='.$skill.'&page=1">'.($exp1 == 0 ? '--' : number_format($exp1)).'</a></td>
				
				<td style="text-align:center;"><img src="img/skills/'.strtolower($skill).'.gif" data-toggle="tooltip" data-placement="top" title="'.$skill.'"></td>

				<td class="text-right"><a href="?m=ranking&skill='.$skill.'&page=1">'.($level2 == 1 ? '--' : $level2.''.$class2).'</a></td>
				<td class="text-right hidden-xs"><a href="?m=ranking&skill='.$skill.'&page=1">'.($exp2 == 0 ? '--' : number_format($exp2)).'</a></td>
				
			</tr>
			';
		}
		?>
	</tbody></table>
	<?php 
	} else {
		echo '<div class="col-sm-8"><p>'.($player['highscoreopt'] == 0 ? "" : 'Player: '.$player['username'].' is hidden from the highscores!').'</p></div>';
		echo '<div class="col-sm-4"><p>'.($other['highscoreopt'] == 0 ? "" : 'Player: '.$other['username'].' is hidden from the highscores!').'</p></div>';
	}
	?>
</div>