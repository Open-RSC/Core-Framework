<?php
require ('header.php');
?>
<!DOCTYPE html>
<html>
	<head>
		<?php load_meta(); ?>
		<link rel="stylesheet" type="text/css" href="vendor/css/bootstrap.min.css">
		<link rel="stylesheet" type="text/css" href="vendor/css/font-awesome.min.css">
		<link rel="stylesheet" type="text/css" href="vendor/css/prism.css">
s		<link rel="stylesheet" type="text/css" href="themes/RSCLegacy/css/style.css?<?php echo filemtime('themes/RSCLegacy/css/style.css') ?>">
		<script src="vendor/js/jquery.min.js"></script>
		<script src="vendor/js/bootstrap.min.js"></script>
		<script src="vendor/js/prism.js"></script>
		<script src="vendor/js/rl.js"></script>
		<style>
		.emoji {
			font-size: <?php echo $luna_config['o_emoji_size'] ?>px;
		}
        <?php if (($luna_config['o_cookie_bar'] == 1) && ($luna_user['is_guest']) && (!isset($_COOKIE['LunaCookieBar']))) { ?>
			body { margin-bottom: 60px; }
			@media screen and (max-width: 767px) { body { margin-bottom: 80px; } }
        <?php } ?>
		</style>
	</head>
	<body>
	<div class="wrap">
	<?php if ($luna_user['is_guest']): require load_page('login.php'); endif; ?>
	<nav class="navbar navbar-inverse navbar-fixed-top navbar-xs custom-nav" role="navigation">
	<div class="container">
	  <div class="navbar-header">
		<button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#top-nav-bar-collapse">
		  <span class="sr-only">Toggle navigation</span>
		  <span class="icon-bar<?php echo (($num_new_pm != 0 || $num_notifications != 0)? ' flash' : '') ?>"></span>
						<span class="icon-bar<?php echo (($num_new_pm != 0 || $num_notifications != 0)? ' flash' : '') ?>"></span>
						<span class="icon-bar<?php echo (($num_new_pm != 0 || $num_notifications != 0)? ' flash' : '') ?>"></span>
		</button>
	  </div>
		<a href="http://wolfkingdom.net" class="navbar-brand navbar-left">
		<div class="brand__section">
			<div class="logoHeader">
				<div class="logo-container">
					RSC<img src="img/icons/rsclegacy.png" class="legacyLogo" ALT="Runescape Classic Legacy" width="32" height="32"/><span class="color2">LEGACY</span>
				</div>
			</div>
		</div>
		</a>
	  <div class="collapse navbar-collapse" id="top-nav-bar-collapse">
		<ul class="nav custom-navbar-nav navbar-right">
		<?php echo $usermenu; ?>
		</ul>
	  </div>
	 </div>
	</nav>
	<div class="header">
		<?php
		if ($luna_config['o_announcement'] == '1') {
		?>
			<div class="userbar">
				<div class="container">
					<ul class="left-userbar">
						<li>
						<?php if (!empty($luna_config['o_announcement_title'])) { ?><strong><?php echo $luna_config['o_announcement_title']; ?>: </strong><?php } ?>
						<?php echo $luna_config['o_announcement_message']; ?>
						</li>
					</ul>
				</div>
			</div>
		<?php
		}
		?>	
		<div class="banner-header">
			<div class="container">
			<?php if (!$luna_user['is_guest']) { ?>
			<div class="account-area content-r-side">
				<div class="account-area_head">
				<?php if($getActiveChar['forum_active'] == 1) { ?>
				<div class="character-selected_head">
				<a href="char_manager.php?id=<?php echo $luna_user['id']; ?>"><img src="<?php echo get_player_card($getActiveChar['id']); ?>" width="65" height="115" alt=""></a>
				</div>
				<li class="dropdown">
				<a href="char_manager.php?id=<?php echo $luna_user['id']; ?>" class="dropdown-toggle character-selected_holder" data-toggle="dropdown">
					<div class="character-selected-details">
						<span class="character-selected-username clearfix"><?php echo luna_htmlspecialchars($getActiveChar['username']) ?></span>
						<span class="character-selected_clan"><i class="fa fa-hashtag" aria-hidden="true"></i> Ruthless</span>
						<span class="character-selected_arrow"><i class="fa fa-angle-down" aria-hidden="true"></i></span>
					</div>
				</a>
				<ul class="dropdown-menu">
				<li><a href="char_manager.php?id=<?php echo $luna_user['id'] ?>"><i class="fa fa-fw fa-user"></i> Character Profile</a></li>
				<li class="divider"></li>
				<li><a href="adventure.php?player=<?php echo urlencode($getActiveChar['username']) ?>"><i class="fa fa-book"></i> Adventurer Log</a></li>
				<li><a href="char_manager.php?id=<?php echo $luna_user['id'] ?>&setting=achievements"><i class="fa fa-trophy"></i> Achievements</a></li>
				<li class="divider"></li>
				<li><a href="char_manager.php?id=<?php echo $luna_user['id'] ?>&setting=change_password"><i class="fa fa-lock"></i> Change Password</a></li>
				<li><a href="char_manager.php?id=<?php echo $luna_user['id'] ?>&setting=character_renaming"><i class="fa fa-pencil"></i> Change Character Name</a></li>
				<li><a href="char_manager.php?id=<?php echo $luna_user['id'] ?>&setting=highscore"><i class="fa fa-bar-chart"></i> Highscore Option</a></li>
				</ul>
				</li>
				<?php } else if($getActiveChar['forum_active'] == 0) { ?>
				<a href="char_manager.php?id=<?php echo $luna_user['id'] ?>" class="account-management-btn">
				<div class="account-area-click">
				<p>View your in-game characters</p>
				<div>Character Profile <i class="fa fa-arrow-right" aria-hidden="true"></i></div>
				</div>
				</a>
				<?php } ?>
				</div>
			</div>
			<?php } ?>
			</div>
		</div>
		<div class="navbar navbar-default navbar-static-top">
			<div class="container">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#main-bar-collapse">
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
						<span class="icon-bar"></span>
					</button>
				</div>
				<div class="collapse navbar-collapse" id="main-bar-collapse">
					<ul class="nav navbar-nav">
						<li id="nav-home"><a href="index.php">Home</a></li>
						<li id="nav-game">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown">Game <b class="caret"></b></a>
						<ul class="dropdown-menu">
							<li><a href="download.php">Download</a></li>
						<li class="divider"></li>
						<li><a href="guide.php">Guides</a></li>
						<li class="divider"></li>
						<li><a href="highscore.php">Highscores</a></li>
						<li class="divider"></li>
						<li><a href="hof.php">Hall Of Fame</a></li>
						<li class="divider"></li>
						<li><a href="db.php">RSCLegacy Database</a></li>
						<li class="divider"></li>
						<li><a href="calculators.php">Calculators</a></li>
						</ul>
						</li>
						<li id="nav-community">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown">Community <b class="caret"></b></a>
						<ul class="dropdown-menu">
						<li><a href="userlist.php">Members List</a></li>
						<li class="divider"></li>
						<li><a href="help.php">Forum Help</a></li>
						<li class="divider"></li>
						<li><a href="about.php">About us, Staff & FAQs</a></li>
						<li class="divider"></li>
						<li><a href="terms.php">ToS and Rules</a></li>
						</ul>
						</li>
						<li id="nav-forum"><a href="forum.php">Forums</a></li>				
						<li id="nav-donate" class="hidden-sm">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown">Donate <b class="caret"></b></a>
						<ul class="dropdown-menu">
						<li><a href="donate.php">Jewels</a></li>
						<li class="divider"></li>
						<li><a href="shop.php">Shop</a></li>
						</ul>
						</li>
						<li id="nav-play" class="hidden-sm"><a href="adventure.php">Adventurer Log</a></li>
					</ul>
				</div>
			</div>
		</div>
	</div>