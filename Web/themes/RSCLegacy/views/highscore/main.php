<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<div class="base-body">
<div class="area-body">
	<div class="categories">
	<h3 class="skill-text">
	<?php echo ucwords($skill); ?>
	</h3>
	<div class="pull-right" id="highscore_pagination">
		<?php echo $paging_links ?>
	</div>
	</div>
</div>
<?php
if($db->num_rows($grab_skill) > 0) {
?>
	<table class="table table-bordered table-hover">
		<thead>
		<tr>
		<th class="text-right" style="width:10%;">Rank</th>
		<th style="width: 50%;">Player</th>
		<?php 
		if($skill == 'KD') { ?>
			<th class="text-right" style="width: 17%;">Kills</th>
			<th class="text-right" style="width: 23%;">Deaths</th>
		<?php } else { ?>
			<th class="text-right" style="width: 17%;"><?php echo (strtolower($skill) == "overall" ? 'Skill Total' : 'Level') ?></th>
			<th class="text-right" style="width: 23%;"><?php echo (strtolower($skill) == "overall" ? 'Total Experience' : 'Experience') ?></th>
			<?php } ?>
		</tr>
		</thead>
		<tbody>
<?php

$rank = $fix_curr_page + 1;

	while ($users = $db->fetch_assoc($grab_skill)) {
		echo '
			<tr '. ($users['hc_ironman_death'] == 1 && $selected_highscore == "hardcore_ironman" ? "class='highscore_dead'" : "").'>
				<td class="text-right"><a href="highscore.php?c='.$selected_highscore.'&amp;m=ranking&user='.urlencode($users['username']).'" class="text-primary">'.$rank.'</td>
				
				<td><a href="highscore.php?c='.$selected_highscore.'&m=ranking&user='.urlencode($users['username']).'" class="text-primary">'.luna_htmlspecialchars($users['username']).'';
				if($users['hc_ironman_death'] == 1 && $selected_highscore == "hardcore_ironman"):
					echo '<img class="hc-ironman-death" src="img/icons/hcim_skull.png" class="skill-ico" data-toggle="tooltip" alt="skull icon" data-placement="bottom" title="Player died">';
				endif;
				switch($skill) {
					case 'KD':
					echo '<td class="text-right"><a href="highscore.php?c='.$selected_highscore.'&amp;m=ranking&user='.urlencode($users['username']).'" class="text-primary">'. number_format($users['kills']).'</td>
					<td class="text-right"><a href="highscore.php?c='.$selected_highscore.'&amp;m=ranking&user='.urlencode($users['username']).'" class="text-primary">'. number_format($users['deaths']) .'</td></tr>';
					break;
					default:
					echo '<td class="text-right"><a href="highscore.php?c='.$selected_highscore.'&amp;m=ranking&user='.urlencode($users['username']).'" class="text-primary">'.(strtolower($skill) == "overall" ? number_format($users['skill_total']) : experience_to_level($users[$skill_xp])).'</td>
				<td class="text-right"><a href="highscore.php?c='.$selected_highscore.'&amp;m=ranking&user='.urlencode($users['username']).'" class="text-primary">'.(strtolower($skill) == "overall" ? number_format($users['total_experience']) : number_format($users[$skill_xp])).'</td></tr>';
					break;
				}
		$rank++;
	}
	?>
	</tbody></table>
	<?php
} else {
	echo "<p>No players could be found..</p>";
}
?>
</div>
<div class="area-body bottom_h" id="highscore_pagination">
	<div class="pull-left">
	<?php echo $paging_links ?>
	</div>
	<form action="?c=<?php echo $selected_highscore ?>&amp;=ranking&amp;skill=<?php echo $skill ?>" method="post">
		<label for="gotopage" class="gotoPage">Go to
			<input type="number" min="1" max="<?php echo $num_pages ?>" name="p" maxlength="7" class="paginationWrap__number text" id="gotopage" required="">
		</label>
	</form>
</div>