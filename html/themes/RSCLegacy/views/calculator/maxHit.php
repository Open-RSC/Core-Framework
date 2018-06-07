<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<form class="form-horizontal" name="MaxHit">
<table class="table table-bordered">
	<thead>
		<tr>
			<th colspan="2">Options</th>
		</tr>
	</thead>
	<tbody>
	<tr>
		<td><label class="control-label">Strength Level:</label></td>
		<td> 
			<div class="col-lg-2">
				<input type="text" class="form-control input-sm" name="Strength" size="4" value="0">
			</div>
		</td>
	</tr>
	<tr>
		<td><label class="control-label">Weapon Power:</label></td>
		<td> 
			<div class="col-lg-2">
				<input type="text" class="form-control input-sm" name="WP" size="4" value="0">
			</div>
		</td>
	</tr>
	<tr>
		<td><label class="control-label">Potion Type</label></font></td>
		<td>
			<div class="col-lg-10">
			  <select size="1" class="form-control input-sm" id="select" name="Potion">
				  <option selected="" value="1">No Potion</option>
				  <option value="1.14">Regular Strength Potion</option>
				  <option value="1.205">Super Strength Potion</option>
			  </select>
			</div>
		</td>
	</tr>
	<tr>
		<td><label class="control-label">Prayer Type</label></font></td>
		<td>
			<div class="col-lg-10">
			  <select size="1" class="form-control input-sm" id="select" name="Prayer">
			  <option selected="" value="0">No Prayer</option>
			  <option value="0.05">Burst Of Strength</option>
			  <option value="0.1">Super Human Strength</option>
			  <option value="0.15">Ultimate Strength</option>
			  </select>
			</div>
		</td>
	</tr>
	<tr>
		<td><label class="control-label">Combat Style</label></font></td>
		<td>
			<div class="col-lg-10">
			  <select size="1" class="form-control input-sm" id="select" name="Style">
			  <option value="1" selected="">Controlled</option>
			  <option value="3">Aggressive</option>
			  <option value="0">Accurate</option>
			  <option value="0">Defensive</option>
			  </select>
			</div>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<button type="button" value="Calculate" name="B1" onclick="webcalcs.maxHit.newCalc()" class="btn btn-primary">Calculate</button>
		</td>
	</tr>
	</tbody>
</table>
<table class="table table-bordered">
	<thead>
	<tr>
		<th>
			<b><font face="Verdana" size="1">Rounded Result</font></b>
		</th>
		<th>
			<b><font face="Verdana" size="1">Unrounded Result</font></b>
		</th>
	</tr>
	</thead>
	<tbody>
	<tr>
		<td>
			<div class="col-lg-12">
				<input type="text"  class="form-control input-sm" name="Result" size="15" placeholder="1">
			</div>
		</td>
		<td>
			<div class="col-lg-12">
				<input type="text"  class="form-control input-sm" name="NR" size="20" placeholder="1.1500000000000001">
			</div>	
		</td>
	</tr>
	</tbody>
</table>
</form>