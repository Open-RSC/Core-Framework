<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<div class="col-sm-4 content-l-side">
	<div class="my-active_char">
		<div class="my-active_char_header">
			<h2>Active Character Profile</h2>
		</div>
		<div class="my-active_char_body">
			<h2 class="my-active_username">
				<a href="#" title="Adventurer log"><?php echo luna_htmlspecialchars($apply_char['username']); ?></a>
			<p><i class="fa fa-tag" aria-hidden="true"></i> Ruthless</p>
			</h2>
			<div id="combat_level">Level: <?php echo number_format($apply_char['combat']); ?></div>
			<hr class="draw-line">
			<div class="my-active_char_story">
			<a class="adventure_log" href="adventure.php?player=<?php echo urlencode($apply_char['username']) ?>"><i class="fa fa-book" aria-hidden="true"></i> Adventurer Log</a>
			<br />
			<a href="char_manager.php?id=<?php echo $luna_user['id'] ?>&setting=achievements&player=<?php echo $apply_char['id'] ?>"><i class="fa fa-trophy" aria-hidden="true"></i> Achievements</a>
			</div>
			<hr class="draw-line">
			<div class="my-active_char_setting">
			<a href="char_manager.php?id=<?php echo $luna_user['id'] ?>&setting=change_password"><i class="fa fa-dot-circle-o" aria-hidden="true"></i> Change Password</a>
			<br />
			<a href="char_manager.php?id=<?php echo $luna_user['id'] ?>&setting=character_renaming"><i class="fa fa-dot-circle-o" aria-hidden="true"></i> Change Character Name</a>
			<br />
			<a href="char_manager.php?id=<?php echo $luna_user['id'] ?>&setting=highscore"><i class="fa fa-dot-circle-o" aria-hidden="true"></i> Highscore Option</a>
			<br />
			<a href="char_manager.php?id=<?php echo $luna_user['id'] ?>&setting=reduction"><i class="fa fa-dot-circle-o" aria-hidden="true"></i> Stat Reduction</a>
			</div>
		</div>
		<div class="my-active_char_footer">
			<div class="my-active_char_card">
				<div class="my-active_char_skills">
					<ul id="char-stats-cb">
					<h3 id="char_stat_title">Combat</h3>
						<li id="rsc_attack"><span class="name">Attack: </span><span><?php echo number_format(experience_to_level($apply_char['exp_attack'])); ?></span></li>
						<li id="rsc_defense"><span class="name">Defense: </span><span><?php echo number_format(experience_to_level($apply_char['exp_defense'])); ?></span></li>
						<li id="rsc_strength"><span class="name">Strength: </span><span><?php echo number_format(experience_to_level($apply_char['exp_strength'])); ?></span></li>
						<li id="rsc_hits"><span class="name">Hits: </span><span><?php echo number_format(experience_to_level($apply_char['exp_hits'])); ?></span></li>
						<li id="rsc_ranged"><span class="name">Ranged: </span><span><?php echo number_format(experience_to_level($apply_char['exp_ranged'])); ?></span></li>
						<li id="rsc_prayer"><span class="name">Prayer: </span><span><?php echo number_format(experience_to_level($apply_char['exp_prayer'])); ?></span></li>
						<li id="rsc_magic"><span class="name">Magic: </span><span><?php echo number_format(experience_to_level($apply_char['exp_magic'])); ?></span></li>
					</ul>
					<ul id="char-stats-overall">
					<h3 id="char_stat_title">Overall</h3>
						<li id="rsc_questpoints"><span class="name">QP: </span><span><?php echo number_format($apply_char['quest_points']); ?></span></li>
						<li id="rsc_skilltotal"><span class="name">Total Lv: </span><span><?php echo number_format($apply_char['skill_total']); ?></span></li>
					</ul>
				</div>
				<div id="my-active_char_img" class="mb0">
					<div class="playercard">
						<img src="<?php echo get_player_card($apply_char['id']); ?>" width="65" height="115">
					</div>
				</div>
				<div class="my-active_char_skills pull-right">
				<ul id="char-stats-s">
					<h3 id="char_stat_title">Skills</h3>
						<li id="rsc_cooking"><span class="name">Cooking: </span><span><?php echo number_format(experience_to_level($apply_char['exp_cooking'])); ?></span></li>
						<li id="rsc_woodcut"><span class="name">Woodcut: </span><span><?php echo number_format(experience_to_level($apply_char['exp_woodcut'])); ?></span></li>
						<li id="rsc_fletching"><span class="name">Fletching: </span><span><?php echo number_format(experience_to_level($apply_char['exp_fletching'])); ?></span></li>
						<li id="rsc_fishing"><span class="name">Fishing: </span><span><?php echo number_format(experience_to_level($apply_char['exp_fishing'])); ?></span></li>
						<li id="rsc_firemaking"><span class="name">Firemaking: </span><span><?php echo number_format(experience_to_level($apply_char['exp_firemaking'])); ?></span></li>
						<li id="rsc_crafting"><span class="name">Crafting: </span><span><?php echo number_format(experience_to_level($apply_char['exp_crafting'])); ?></span></li>
						<li id="rsc_smithing"><span class="name">Smithing: </span><span><?php echo number_format(experience_to_level($apply_char['exp_smithing'])); ?></span></li>
						<li id="rsc_mining"><span class="name">Mining: </span><span><?php echo number_format(experience_to_level($apply_char['exp_mining'])); ?></span></li>
						<li id="rsc_herblaw"><span class="name">Herblaw: </span><span><?php echo number_format(experience_to_level($apply_char['exp_herblaw'])); ?></span></li>
						<li id="rsc_agility"><span class="name">Agility: </span><span><?php echo number_format(experience_to_level($apply_char['exp_agility'])); ?></span></li>
						<li id="rsc_thieving"><span class="name">Thieving: </span><span><?php echo number_format(experience_to_level($apply_char['exp_thieving'])); ?></span></li>
					</ul>
				</div>
			</div>
		</div>
	</div>
</div>