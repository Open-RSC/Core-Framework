<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<div class="col-sm-4 content-l-side">
	<div id="combatLvlResult">
		<div class="alert alert-combat">
			<h2>Combat calculator</h2>
			<p><i class="fa fa-sort-numeric-asc"></i> This tool can calculate stats for your character.</p>
		</div>
	</div>
</div>
<div class="col-sm-8 char-r-side">
<table class="table table-bordered">
	<thead>
		<tr>
			<th>Skill</th>
			<th>Value</th>
		</tr>
	</thead>
	<tbody>
	<?php
		foreach ($calc_valid_skills as $skill_id)
		{
			echo "
				<tr>
					<td style='width: 30%;'>
						<img src='http://wolfkingdom.net/img/skills/" . $skill_id . ".gif' alt='*' style='vertical-align:top;'/>&nbsp;" . ucfirst($skill_id) . "
					</td>
					";
				echo '
				  ' . ($skill_id == 'hits' ? ' <td class="input-group"> ' :  '<td>') . '
					<input type="text" maxlength="2" size="2" name="' . $skill_id . '" value="' . (isset(${$skill_id}) ? ${$skill_id} : ($skill_id == 'hits' ? 10 : 1)) . '" class="form-control">
					<span class="input-group-btn">
					  ' . ($skill_id == 'hits' ? '<button class="btn btn-primary" type="button" name="calchits" value="Hits" onclick="webcalcs.combat.autoGenerateHits()">Hits</button>' : null) . '
					</span>
				  </td>
				</tr>
			';
		}
	?>
	<tr>
		<td colspan="2">
			<button type="button" name="calccombat" class="btn btn-success" onClick = "webcalcs.combat.newCalculation()">Calculate Combat</button>
		</td>
	</tr>
	</tbody>
</table>
</div>