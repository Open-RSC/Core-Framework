<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<div class="panel panel-default">
	<div class="content-header content-header--highlight">
		<h2 class="content-header-title">Quest and Play Guides</h2>
	</div>
	<div class="embended-info">
		<p><i class="fa fa-info-circle" aria-hidden="true"></i> Use the RSCLegacy database to find information on quests, skills, play guides and more.</p>
	</div>
	<p class="guide-version">Updated: 2016-10-10</p>
	<div class="embended-add">
		<p><span><strong><i class="fa fa-arrow-down" aria-hidden="true"></i> Category</span></strong></p>
	</div>
	<div class="panel-body">
	<div class="col-sm-6">
		<div class="game_play">
			<div class="guide_title">How To Play</div>
			<div class="guide_con">
			<a href="?m=game_guide" class="guide_click">
				<div class="guide-view_image">
					<img src="img/icons/howto.png" alt="" width="312" height="120">
				</div>
				<p>The gameplay guide includes information on a number of important topics such as installing the game, controls, and navigation of the user interface.
					Additional information regarding Android can also be found in here.</p>
			</a>
			</div>
		</div>
	</div>
	<div class="col-sm-6">
		<div class="quest_guide">
			<div class="guide_title">Quest Guides</div>
			<div class="guide_con">
			<a href="?m=quest_db" class="guide_click">
				<div class="guide-view_image">
					<img src="img/icons/quest.png" alt="" width="312" height="120">
				</div>
				<p>Step-by-step quest guides for every F2P and P2P RuneScape Classic quests.
				We offer you the experience to relive ALL the 50 Quests.
				Do you dare to challange?</p>
			</a>
			</div>
		</div>
	</div>
	</div>
</div>