<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<div class="col-sm-3 sidebar content-l-side">
	<div class="list-group list-group-luna">				
		<?php
		for($i = 0; $i < count($s_skills); $i ++)
		{
			echo "<a class='" . ($curr_skill == $s_skills[$i] ? " active" : "") . " list-group-item' href='calculators.php?skill=" . $s_skills[$i] . "'><img src='http://wolfkingdom.net/img/skills/" . $s_skills[$i] . ".gif' alt='*' style='vertical-align:bottom;' />&nbsp;" . ucfirst($s_skills[$i]) . "</a>";
		}
		?>
	</div>
</div>
<div class="col-sm-9 char-r-side">
	<table class="table table-bordered">
		<thead>
			<tr>
				<th colspan="3">Options</th>
			</tr>
		</thead>
		<tr>
			<td>
				<label for="calculator-rate">Experience Rate</label>
			</td>
			<td colspan="2" >
				<select class="form-control input-sm" id="calculator-rate" >
					<option value="2">RSCLegacy</option>
					<option value="3.0">RSCLegacy Subscriber</option>
					<option value="3.5">RSCLegacy Premium</option>
					<option value="1">RuneScape Classic</option>
				</select>
			</td>
		</tr>
		<tr>
			<td>
				<select class="form-control input-sm" id="calculator-current-type" >
					<option value="xp">Current XP</option>
					<option value="level">Current Level</option>
				</select>
			</td>
			<td>
				<input class="input-form input-sm" id="calculator-current-value" type="number" min="0" max="13034431" value="0" />
			</td>
			<td>
				<span id="calculator-current-display"></span>
			</td>
		</tr>
		<tr>
			<td>
				<select class="form-control input-sm" id="calculator-target-type" >
					<option value="xp">Target XP</option>
					<option value="level">Target Level</option>
				</select>
			</td>
			<td>
				<input class="input-form input-sm" id="calulator-target-value" type="number" min="83" max="13034431" value="83" />
			</td>
			<td>
				<span id="calculator-target-display"></span>
			</td>
		</tr>
		<tr>
			<td>
				<label for="calculator-category">Category</label>
			</td>
			<td colspan="2">
				<select class="form-control input-sm" id="calculator-category" ></select>
			</td>
		</tr>
	</table>
	<table class="table table-bordered" id="skillOutput">
		<thead>
			<tr>
				<th>Number</th>
				<th>Picture</th>
				<th>Name</th>
				<th>Level</th>
				<th>XP</th>
			</tr>
		</thead>
		<tbody></tbody>
	</table>
</div>