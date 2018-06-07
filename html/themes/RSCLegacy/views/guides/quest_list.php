<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<div class="panel panel-default">
	<div class="content-header content-header--highlight">
		<h2 class="content-header-title">Quest Guides</h2>
	</div>
	<div class="embended-info">
		<p>
		<i class="fa fa-info-circle" aria-hidden="true"></i> 
		Welcome to the RSCLegacy Quest Guides page. These guides will help you develop your RuneScape character and provide information about the game. This information was submitted and gathered by some of your fellow players to help you out. 
		If anything is incorrect or missing, or if you have any new information to submit to this database, please submit it to us in the <a href="#">forums</a>..
		</p>
	</div>
	<p class="guide-version"><span class="pull-left">Quest type: F2P (Free To Play)  & P2P (Members)</span>Quests last updated: 2017-03-08</p>
	<div class="embended-add">
		<p><span><strong><i class="fa fa-book" aria-hidden="true"></i> RSCL Quest List</span></strong></p>
	</div>
	<div class="panel-body">
	<div class="col-sm-12">
		<table class="table table-bordered">
			<thead>
				<tr>
				<th class="">Name</th>
				<th class="">Difficulty</th>
				<th class="">Length</th>
				<th class="">Quest Points</th>
				<th class="">Free / Member</th>
				</tr>
			</thead>
			<tbody>
			<?php 
			while($ql = $db->fetch_assoc($find_quests)) {
			?>
			<tr>
				<td><a href="?m=quest_db&id=<?php echo $ql['id']?>"><?php echo luna_htmlspecialchars($ql['title']) ?></a></td>
				<td><?php echo $ql['difficulty'] ?></td>
				<td><?php echo luna_htmlspecialchars($ql['length']) ?></td>
				<td><?php echo number_format($ql['quest_points']) ?></td>
				<td><?php echo luna_htmlspecialchars($ql['guide_type']) ?></td>
			</tr>
			<?php 
			}
			?>
			</tbody>
		</table>
	</div>
	</div>
</div>