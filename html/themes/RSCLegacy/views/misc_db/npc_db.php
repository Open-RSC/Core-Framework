<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<ol class="breadcrumb">
	<li><a href="index.php">Home</a></li>
	<li><a href="db.php">RSCLegacy Database</a></li>
	<li class="active"><a href="db.php?category=npcs">NPC Database</a></li>
</ol>
<div class="sidebar col-sm-3 content-l-side">
	<div class="list-group-set">
	<div class="content-header content-header--highlight">
		<h2 class="content-header-title guideColor">NPC Index</h2>
	</div>
	<ul class="item_db-index">
	<?php 
		foreach($index as $i) {
			echo '<li '.($selected_index == $i ? 'class="selected"' : '').'><a href="?category=npcs&index='.$i.'" class="item_index">'.$i.'</a></li>';
		}
	?>
	</ul>
	</div>
</div>
<div class="col-sm-9 char-r-side">
	<div class="panel panel-default">
	<div class="content-header content-header--highlight">
		<h2 class="content-header-title">Index <?php echo ucwords($selected_index) ?> - RSCLegacy NPC Database</h2>
	</div>
	<div class="embended-info">
		<p>
		Here you can find information on all the NPCs in RSCLegacy. Click one of the letters to show all items that start with that letter. You can also use the quick search box to find a NPC easily by searching for it's name.
		</p>
	</div>
	<span class="embended-space"></span>
	<div class="embended-add">
		<p><span><strong><i class="fa fa-search" aria-hidden="true"></i> Search for NPC</span></strong></p>
	</div>
	<div class="panel-body">
		<form class="form-horizontal" id="item_db" method="get" action="db.php?category=npcs">
		<input type="hidden" name="category" value="npcs">
		<div class="form-group">
			<label class="col-sm-2 char-label"><strong>NPC Name</strong></label>
			<div class="col-sm-10">
				<div class="input-group">
					<input class="form-control" type="text" name="term" value="<?php echo $search_term ?>" placeholder="Search database" maxlength="36" required="required">
					<span class="input-group-btn">
						<button class="btn btn-success" type="submit" accesskey="s"><span class="fa fa-fw fa-search"></span></button>
					</span>
				</div>
			</div>
		</div>
		</form>
	</div>
	<div class="embended-add">
	<p><span><strong>NPC Index <?php echo (!isset($selected_npc) ? ''.$selected_index. ' ('.$db->num_rows($list_npcs).' NPCs)' :  "(".$selected_index. ") - " .str_replace('_', ' ', $selected_npc)) ?> </span></strong></p>
	</div>
	<div class="panel-body">
		<div class="col-sm-12">
		<?php 
		if(!$selected_npc) {
		?>
		<table class="table table-bordered">
			<thead>
				<tr>
				<th>Image</th>
				<th>NPC Name</th>
				<th>Combat Level</th>
				</tr>
			</thead>
			<tbody>
			<?php 
			while($result = $db->fetch_assoc($list_npcs)) {
			?>
			<tr>
				<td class="cColumn"><img src="img/npc/minor/<?php echo $result['id'] ?>.png" alt="picture <?php echo $result['name'] ?>"  ></td>
				<td><a href="db.php?category=npcs&index=<?php echo substr($result['name'], 0, $result['name'] + 1) ?>&id=<?php echo $result['id'] ?>&npc=<?php echo str_replace(" ", "_", $result['name']) ?>"><?php echo $result['name'] ?></a><br /><?php echo $result['description'] ?></td>
				<td class="cColumn"><?php echo ($result['combatlvl'] == 0 ? "N/A" : number_format($result['combatlvl'])) ?></td>
			</tr>
			<?php 
			}
			?>
			</tbody>
		</table>
		<?php 
		}  else {
		
			$result = $db->fetch_assoc($fetch_npc);
		?>
		<div class="col-sm-5">
		<div class="npc_picture">
			<img alt="picture <?php echo $result['name'] ?>" src="img/npc/major/<?php echo $result['id'] ?>.png">
		</div>
		<?php if($result['attackable'] == 1) { ?>
		<table class="table table-bordered">
			<thead>
				<tr>
				<th colspan="3">NPC Drops</th>
				</tr>
				<tr>
				<th>Picture</th>
				<th>Item</th>
				<th>Amount</th>
				</tr>
			</thead>
			<tbody>
			<?php 
			while($r = $db->fetch_assoc($fetch_drop)) {
			?>
			<tr>
				<td><img alt="picture <?php echo $r['name'] ?>" src="img/items/<?php echo $r['id'] ?>.png"></td>
				<td><a href="db.php?category=items&index=<?php echo substr($r['name'], 0, $r['name'] + 1) ?>&id=<?php echo $r['id'] ?>&item=<?php echo str_replace(" ", "_", $r['name']) ?>"><?php echo $r['name'] ?></a></td>
				<td><?php echo ($r['amount'] == 0 ? "N/A" : number_format($r['amount'])) ?></td>
			</tr>
			<?php 
			}
			?>
			</tbody>
		</table>
		<?php } ?>
		</div>
		<div class="col-sm-7">
		<table class="table table-bordered">
			<thead>
				<tr>
				<th colspan="2">NPC Generic Information</th>
				</tr>
			</thead>
			<tbody>
			<tr>
				<td colspan="2" class="itemdetails-header-alternate">
					<b STYLE="COLOR:#d49d1e;"> <?php echo $result['name'] ?></b>
				</td>
			</tr>
			<tr>
				<td style="width:20%;" class="itemdetails-header-alternate">
					<b>Description</b>
				</td>
				<td style="width:80%" class="itemdb-info">
					<?php echo $result['description'] ?>
				</td>
			</tr>
			<tr>
				<td style="width:20%;" class="itemdetails-header-alternate">
					<b>Attackable</b>
				</td>
				<td style="width:80%" class="itemdb-info">
					<?php echo ($result['attackable'] == 1 ? 'Yes' : 'No') ?>
				</td>
			</tr>
			<tr>
				<td style="width:20%;" class="itemdetails-header-alternate">
					<b>Aggressive</b>
				</td>
				<td style="width:80%" class="itemdb-info">
					<?php echo ($result['aggressive'] == 1 ? 'Yes' : 'No') ?>
				</td>
			</tr>
			<tr>
				<td style="width:20%;" class="itemdetails-header-alternate">
					<b>Respawn Time</b>
				</td>
				<td style="width:80%" class="itemdb-info">
					<?php echo $result['respawntime'] . ' Seconds' ?>
				</td>
			</tr>
			</tbody>
		</table>
		<?php if($result['attackable'] == 1) { ?>
		<table class="table table-bordered">
			<thead>
				<tr>
				<th colspan="2">NPC Statistics</th>
				</tr>
			</thead>
			<tbody>
			<tr>
				<td style="width:20%;" class="itemdetails-header-alternate">
					<b>Combat Level</b>
				</td>
				<td style="width:80%" class="itemdb-info">
					<?php echo $result['combatlvl'] ?>
				</td>
			</tr>
			<tr>
				<td style="width:20%;" class="itemdetails-header-alternate">
					<b>Attack</b>
				</td>
				<td style="width:80%" class="itemdb-info">
					<?php echo ($result['attack'] == 0 ? 'N/A' : $result['attack']) ?>
				</td>
			</tr>
			<tr>
				<td style="width:20%;" class="itemdetails-header-alternate">
					<b>Defense</b>
				</td>
				<td style="width:80%" class="itemdb-info">
					<?php echo ($result['defense'] == 0 ? 'N/A' : $result['defense']) ?>
				</td>
			</tr>
			<tr>
				<td style="width:20%;" class="itemdetails-header-alternate">
					<b>Strength</b>
				</td>
				<td style="width:80%" class="itemdb-info">
					<?php echo ($result['strength'] == 0 ? 'N/A' : $result['strength']) ?>
				</td>
			</tr>
			<tr>
				<td style="width:20%;" class="itemdetails-header-alternate">
					<b>Hits</b>
				</td>
				<td style="width:80%" class="itemdb-info">
					<?php echo ($result['hits'] == 0 ? 'N/A' : $result['hits']) ?>
				</td>
			</tr>
			</tbody>
		</table>
		<?php } ?>
		</div>
		<?php 
		} 
		?>
		</div>
	</div>
	</div>
</div>