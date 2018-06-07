<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<div class="panel panel-default">
	<div class="content-header content-header--highlight">
		<h2 class="content-header-title">RSCLegacy Database</h2>
	</div>
	<div class="embended-info">
		<p><i class="fa fa-info-circle" aria-hidden="true"></i> Use the RSCLegacy database to find information on items and npcs.</p>
	</div>
	<p class="guide-version"><span class="pull-left">Database type: F2P (Free To Play) & P2P (Members)</span>Updated: 2017-03-08</p>
	<div class="embended-add">
		<p><span><strong><i class="fa fa-arrow-down" aria-hidden="true"></i> Database Category</span></strong></p>
	</div>
	<div class="panel-body">
	<div class="col-sm-6">
		<div class="game_play">
			<div class="guide_title">Items Database (<?php echo $db->result($total_items) ?> items)</div>
			<div class="guide_con">
			<a href="?category=items" class="guide_click">
				<div class="guide-view_image">
					<img src="img/icons/items.png" alt="" width="295" height="120">
				</div>
				<p>Want to find out everything regarding an item in RuneScape Classic? We have listed everything important about each item in our item database.</p>
			</a>
			</div>
		</div>
	</div>
	<div class="col-sm-6">
		<div class="quest_guide">
			<div class="guide_title">NPCs Database (<?php echo $db->result($total_npcs) ?>  npcs)</div>
			<div class="guide_con">
			<a href="?category=npcs" class="guide_click">
				<div class="guide-view_image">
					<img src="img/icons/red_dragon.gif" alt="" width="177" height="107">
				</div>
				<p>Event NPCs, Shop NPCs and Enemie monsters? Find out and learn about the RuneScape Classic NPCs.</p>
			</a>
			</div>
		</div>
	</div>
	</div>
</div>