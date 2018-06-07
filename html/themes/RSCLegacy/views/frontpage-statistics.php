<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<div id="statistics">
	<div class="panel panel-default">
		<div class="panel-heading">
		<h4 class="panel-title"><i class="fa fa-pie-chart" aria-hidden="true"></i> Statistics</h4>
		</div>
		<div class="base_body">
			<div class="base_title"><h2 class="t-online">Players Online: <?php echo "<span class='t-onlineCount'>" . playersOnline() . "</span>"; ?></h2></div>
			<span class="space"></span>
			<div class="base_title"><h2 class="t-green">Game</h2></div>
			<ul class="list-group">
				<li>
					<div class="inner">
					<h4>
						<strong>Server:</strong><span><?php echo checkStatus("127.0.0.1", "43594"); ?></span>
					</h4>
					</div>
				</li>
				<li>
					<div class="inner">
					<h4>
						<strong>Server Type:</strong><span><?php echo (!MEMBERS_CONTENT ? "F2P (Free)" : "P2P (Free/Members)"); ?></span>
					</h4>
					</div>
				</li>
				<li>
					<div class="inner">
					<h4>
						<strong>Total Characters:</strong><span><?php echo totalGameCharacters(); ?></span>
					</h4>
					</div>
				</li>
				<li>
					<div class="inner">
					<h4>
						<strong>Experience Rates:</strong>
						<ul class="xp_list">
							<li>Combat:</li>
							<li><label class="label label-default">Normal: 8.0x</label></li> 
							<li><label class="label label-warning">Gold: 10.0x</span></li>
							<li><label class="label label-info">Premium: 12.0x</span></li>
							<li><label class="label label-danger">Wildy: +0.5x</span></li>
							<li><label class="label label-danger">Skulled: +1.0x</span></li>
							<li><label class="label label-elixir">Elixir: +1.0x</span></li>
						</ul>
						<ul class="xp_list">
							<li>Skilling:</li>
							<li><label class="label label-default">Normal: 2.0x</label></li> 
							<li><label class="label label-warning">Gold: 3.0x</span></li>
							<li><label class="label label-info">Premium: 3.5x</span></li>
							<li><label class="label label-danger">Wildy: +0.5x</span></li>
							<li><label class="label label-danger">Skulled: +1.0x</span></li>
							<li><label class="label label-elixir">Elixir: +1.0x</span></li>
						</ul>
					</h4>
					</div>
				</li>	
				<li>
					<div class="inner">
					<h4>
					<li>To see your total XP rate, type ::info in-game.</li>
					</h4>
					</div>
				</li>	
			</ul>
		</div>
		<span class="space"></span>
		<div class="base_body">
			<div class="base_title"><h2 class="t-green">Website</h2></div>
			<ul class="list-group">
				<li>
					<div class="inner">
					<h4>
						<strong>Registrations today:</strong><span><?php echo newRegistrationsToday(); ?></span>
					</h4>
					</div>
				</li>		
			</ul>
		</div>
		<span class="space"></span>
		<div class="base_body">
			<div class="base_title"><h2 class="t-green">Chat</h2></div>
			<ul class="list-group">
				<li>
					<div class="inner">
					<h4>
						<strong>Discord:</strong><span><a class="discord" href="https://discord.gg/ecaU7f8">Chat Now!</a></span>
					</h4>
					</div>
				</li>		
			</ul>
		<div class="frontpage-button">
			<?php if($luna_user['is_guest']) { ?>
				<span>Not registered?</span>
				<a class="btn btn-rounded-black" href="register.php">Create an account</a>	
			<?php } else { ?>
				<span>Start your adventure now!</span>
				<a class="btn btn-rounded-black" href="guide.php?m=game_guide">Play Now</a>	
			<?php } ?>			
		</div>
		</div>
	</div>
</div>