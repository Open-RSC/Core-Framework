<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<div class="col-sm-8 char-r-side">
	<div class="panel panel-default">
	<div class="content-header content-header--highlight">
		<h2 class="content-header-title">Stat reduction</h2>
	</div>
	<div class="embended-info">
		<!--<p>
		This tool can reset one combat stat of your choice. By using either Gold or Premium token.
		</p>-->
		<p><strong>Stat Reset:</strong></p>
		<!--<p>- One Gold or Premium Token.</p>
		<p>- Resets a stat below 20. IE: Defense level 19 lowered to 1.</p>
		<br />
		<p>Select the reset type and make sure you have your token in your characters inventory during the process.</p>-->
	</div>
	<div class="panel-body">
		<div class="select_character">
			<div class="char_box">
			<?php 
                        //Corrected issue with depreciation in PHP 7.2
                        //if(count($errors) > 0) { 
                        function counting($errors) {
                            if($errors === null) return 0; 
                            if(is_array($errors)) return count($errors); 
                            if(is_object($errors) && $errors instanceof \Countable) return count($errors);
                                return 1;
                        }
                        if(counting($errors) > 0) {
                        ?>
			<div class="alert alert-dismissable alert-info alert-danger">
				<button type="button" class="close" data-dismiss="alert">&#10006;</button>
				<h4>Error!</h4>
				<p>Please make the following corrections:</p>
				<ul class="error-list">
				<?php
				foreach($errors as $err)
				{
					echo "<li><strong>" . $err . "</strong></li>";
				}
				?>
				</ul>
			</div>
			<?php } else if(isset($_GET['saved']) && counting($errors) == 0) { ?>
			<div class="alert alert-dismissable alert-info alert-info">
				<?php
				echo "<strong>Stat reset successful!</strong>";
				?>
			</div>
			<?php 
			} 
			$grab_char = $db->query("SELECT " . GAME_BASE . "players.owner," . GAME_BASE . "players.online, " . GAME_BASE . "experience.exp_attack, 
				" . GAME_BASE . "experience.exp_defense, " . GAME_BASE . "experience.exp_strength, " . GAME_BASE . "experience.exp_hits, 
				" . GAME_BASE . "experience.exp_prayer, " . GAME_BASE . "experience.exp_ranged, " . GAME_BASE . "experience.exp_magic 
				FROM " . GAME_BASE . "players JOIN " . GAME_BASE . "experience ON " . GAME_BASE . "players.id = " . GAME_BASE . "experience.playerID  WHERE " . GAME_BASE . "players.id = '" . $db->escape($curr_char) . "' AND " . GAME_BASE . "players.owner = '" . $id . "'");

			//$inv_info = $db->query("SELECT id FROM " . GAME_BASE . "invitems WHERE playerID = '" . $db->escape($curr_char) . "' AND id IN (2092, 2094)");
			if($db->num_rows($grab_char) > 0) 
			{
				$fetch = $db->fetch_assoc($grab_char);	
				//if($db->num_rows($inv_info) > 0) 
				//{
				?>
				<form method="post" class="form-horizontal" action="char_manager.php?id=<?php echo $id;?>&amp;setting=reduction">
					<table class='table table-bordered'>
						<thead>
							<tr>
								<th>Skill</th>
								<th>Current Level</th>
								<th>Reset</th>
							</tr>
						</thead>
						<tbody>
						<?php
						for($i = 0; $i < count($validskills); $i++){
							$levelskill = experience_to_level($fetch['exp_' . $validskills[$i]['name']]);
							if($validskills[$i]['name'] == 'hits') {
								echo "
								<tr>
								<td style='width:30%;'><img src='img/skills/hits.gif' alt='x' />&nbsp;" . ucfirst($validskills[$i]['name']) . "</td>
								<td style='width:40%;'>" . $levelskill . " (Auto Calculation)</td>
								<td>" . ($validskills[$i]['modify'] == true ? "<input type='radio' name='reset_stat' value='" . $i . "," . $validskills[$i]['name'] . "' />" : "<input type='radio' disabled />" ) . "
								</td>
								</tr>";
							} else {
								echo "
								<tr>
								<td style='width:30%;'><img src='img/skills/".strtolower($validskills[$i]['name']).".gif' alt='x' />&nbsp;" . ucfirst($validskills[$i]['name']) . "</td>
								<td style='width:40%;'>" . $levelskill . "</td>
								<td>" . (($levelskill > 1 && $levelskill < 20) ? "<input type='radio' name='reset_stat' value='" . $i . "," . $validskills[$i]['name'] . "' />" : "<input type='radio' disabled />" ) . "
								</td>
								</tr>";
							}
						}
						?>
						</tbody>
					</table>
					<div class="form-group">
						<button type="submit" class="btn btn-primary" name="stat_reset" value="Reset Stat"><span class="fa fa-crosshairs"></span> Reset Stat</button>
					</div>
					</form>
					<?php 
				/*} 
				else 
				{
					echo "<span class='label label-danger'>You need to have a Gold or Premium token in your inventory.</span><div class='spacer'></div>";
				}*/
			}
			?>
			<hr class="draw-line" />
			<div class="btn-group-wrap">
				<div class="btn-group">
				<a class="btn btn-danger" href="char_manager.php?id=<?php echo $luna_user['id'] ?>"><i class="fa fa-arrow-left" aria-hidden="true"></i> Go Back</a>
				</div>
			</div>
			</div>
		</div>
	</div>
</div>
</div>