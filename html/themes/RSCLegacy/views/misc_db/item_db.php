<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<ol class="breadcrumb">
	<li><a href="index.php">Home</a></li>
	<li><a href="db.php">RSCLegacy Database</a></li>
	<li class="active"><a href="db.php?category=items">Item Database</a></li>
</ol>
<div class="sidebar col-sm-3 content-l-side">
	<div class="list-group-set">
	<div class="content-header content-header--highlight">
		<h2 class="content-header-title guideColor">Item Index</h2>
	</div>
	<ul class="item_db-index">
	<?php 
		foreach($index as $i) {
			echo '<li '.($selected_index == $i ? 'class="selected"' : '').'><a href="?category=items&index='.$i.'" class="item_index">'.$i.'</a></li>';
		}
	?>
	</ul>
	</div>
</div>
<div class="col-sm-9 char-r-side">
	<div class="panel panel-default">
	<div class="content-header content-header--highlight">
		<h2 class="content-header-title">Index <?php echo ucwords($selected_index) ?> - RSCLegacy Item Database</h2>
	</div>
	<div class="embended-info">
		<p>
		Here you can find information on all the items in RSCLegacy. Click one of the letters to show all items that start with that letter. You can also use the quick search box to find an item easily by searching for it's name.
		</p>
	</div>
	<span class="embended-space"></span>
	<div class="embended-add">
		<p><span><strong><i class="fa fa-search" aria-hidden="true"></i> Search Item</span></strong></p>
	</div>
	<div class="panel-body">
		<form class="form-horizontal" id="item_db" method="get" action="db.php?category=items">
		<input type="hidden" name="category" value="items">
		<div class="form-group">
			<label class="col-sm-2 char-label"><strong>Item Name</strong></label>
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
	<p><span><strong>Item Index <?php echo (!isset($selected_item) ? ''.$selected_index. ' ('.$db->num_rows($list_items).' Items)' :  "(".$selected_index. ") - " .str_replace('_', ' ', $selected_item)) ?> </span></strong></p>
	</div>
	<div class="panel-body">
		<div class="col-sm-12">
		<?php 
		if(!$selected_item) {
		?>
		<table class="table table-bordered">
			<thead>
				<tr>
				<th>Image</th>
				<th>Item Name</th>
				<th>Item ID</th>
				</tr>
			</thead>
			<tbody>
			<?php 
			while($result = $db->fetch_assoc($list_items)) {
			?>
			<tr>
				<td class="cColumn" style="width:12%"><img alt="picture <?php echo $result['name'] ?>" src="img/items/<?php echo $result['id'] ?>.png"></td>
				<td><a href="db.php?category=items&index=<?php echo substr($result['name'], 0, $result['name'] + 1) ?>&id=<?php echo $result['id'] ?>&item=<?php echo str_replace(" ", "_", $result['name']) ?>"><?php echo $result['name'] ?></a><br /><?php echo $result['description'] ?></td>
				<td class="cColumn" style="width:12%"><?php echo $result['id'] ?></td>
			</tr>
			<?php 
			}
			?>
			</tbody>
		</table>
		<?php 
		}  else {
		?>
		<table class="table table-bordered">
			<thead>
				<tr>
				<th colspan="2">Item Information</th>
				</tr>
			</thead>
			<tbody>
			<?php 
			$result = $db->fetch_assoc($fetch_item);
			?>
			<tr>
				<td style="width:20%;" class="itemdetails-header-alternate">
					<b> <?php echo $result['name'] ?></b>
				</td>
				<td style="width:80%" class="itemdb-info">
					<img alt="picture <?php echo $result['name'] ?>" src="img/items/<?php echo $result['id'] ?>.png">
					<?php if($result['bankNoteID'] != 0) { ?>
					<img alt="Noted <?php echo $result['name'] ?>" src="img/items/<?php echo $result['bankNoteID'] ?>.png">
					<?php } ?>
				</td>
			</tr>
			<tr>
				<td style="width:20%;" class="itemdetails-header-alternate">
					<b>Item ID</b>
				</td>
				<td style="width:80%" class="itemdb-info">
					<?php echo $result['id'] ?>
				</td>
			</tr>
			<tr>
				<td style="width:20%;" class="itemdetails-header-alternate">
					<b>Members Item</b>
				</td>
				<td style="width:80%" class="itemdb-info">
					<?php echo ($result['isMembersOnly'] == 1 ? 'Yes' : 'No') ?>
				</td>
			</tr>
			<tr>
				<td style="width:20%;" class="itemdetails-header-alternate">
					<b>Tradeable</b>
				</td>
				<td style="width:80%" class="itemdb-info">
					<?php echo ($result['isUntradable'] == 1 ? 'No' : 'Yes') ?>
				</td>
			</tr>
			<tr>
				<td style="width:20%;" class="itemdetails-header-alternate">
					<b>Equipable</b>
				</td>
				<td style="width:80%" class="itemdb-info">
					<?php echo ($result['isWearable'] == 1 ? 'Yes' : 'No') ?>
				</td>
			</tr>
			<tr>
				<td style="width:20%;" class="itemdetails-header-alternate">
					<b>Stackable</b>
				</td>
				<td style="width:80%" class="itemdb-info">
					<?php echo ($result['isStackable'] == 1 ? 'Yes' : 'No') ?>
				</td>
			</tr>
			<tr>
				<td style="width:20%;" class="itemdetails-header-alternate">
					<b>High Alch</b>
				</td>
				<td style="width:80%" class="itemdb-info">
					<?php echo number_format(($result['basePrice'] / 100 * 40 * 1.5))  ?>gp
				</td>
			</tr>
			<tr>
				<td style="width:20%;" class="itemdetails-header-alternate">
					<b>Low Alch</b>
				</td>
				<td style="width:80%" class="itemdb-info">
					<?php echo number_format(($result['basePrice'] / 100 * 40))  ?>gp
				</td>
			</tr>
			<tr>
				<td style="width:20%;" class="itemdetails-header-alternate">
					<b>Shop Price</b>
				</td>
				<td style="width:80%" class="itemdb-info">
					<?php echo number_format($result['basePrice'])  ?>gp - (From base store)
				</td>
			</tr>
			<tr>
				<td style="width:20%;" class="itemdetails-header-alternate">
					<b>Examine</b>
				</td>
				<td style="width:80%" class="itemdb-info">
					<?php echo $result['description'] ?>
				</td>
			</tr>
			</tbody>
		</table>
		<?php 
		} 
		?>
		</div>
	</div>
	</div>
</div>