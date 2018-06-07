<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

// Make sure no one attempts to run this script "directly"
if (!defined('FORUM'))
	exit;

?>
<div id="nb-footer">
<div class="container">
	<div class="col-md-3 col-sm-6 hidden-xs">
		<div class="footer-info-single">
			<h2 class="title">Community</h2>
			<ul class="list-unstyled">
				<li><a href="http://wolfkingdom.net" title=""><i class="fa fa-angle-double-right"></i> Home</a></li>
				<li><a href="http://wolfkingdom.net/forum.php" title=""><i class="fa fa-angle-double-right"></i> Forum</a></li>
				<li><a href="http://wolfkingdom.net/donate.php" title=""><i class="fa fa-angle-double-right"></i> Donate</a></li>
				<li><a href="http://wolfkingdom.net/shop.php" title=""><i class="fa fa-angle-double-right"></i> Shop</a></li>
			</ul>
		</div>
	</div>
	<div class="col-md-3 col-sm-6 hidden-xs">
		<div class="footer-info-single">
			<h2 class="title">Support</h2>
			<ul class="list-unstyled">
				<li><a href="http://wolfkingdom.net/guide.php" title=""><i class="fa fa-angle-double-right"></i> Guides</a></li>
				<li><a href="http://wolfkingdom.net/help.php" title=""><i class="fa fa-angle-double-right"></i> Forum Help</a></li>
				<li><a href="http://wolfkingdom.net/about.php" title=""><i class="fa fa-angle-double-right"></i> Staff, FAQ & About Us</a></li>
				<li><a href="http://wolfkingdom.net/terms.php" title=""><i class="fa fa-angle-double-right"></i> ToS / Rules</a></li>
			</ul>
		</div>
	</div>
	<div class="col-md-3 col-sm-6 hidden-xs">
		<div class="footer-info-single">
			<h2 class="title">Game</h2>
			<ul class="list-unstyled">
				<li><a href="http://wolfkingdom.net/highscore.php" title=""><i class="fa fa-angle-double-right"></i> Highscore</a></li>
				<li><a href="http://wolfkingdom.net/calculators.php" title=""><i class="fa fa-angle-double-right"></i> Calculators</a></li>
				<li><a href="http://wolfkingdom.net/db.php" title=""><i class="fa fa-angle-double-right"></i> RSCLegacy Database</a></li>
				<li><a href="http://wolfkingdom.net/guide.php?m=game_guide" title=""><i class="fa fa-angle-double-right"></i> Play Now</a></li>
			</ul>
		</div>
	</div>
	<div class="col-md-3 col-sm-6">
		<div class="footer-info-single">
			<h2 class="title">Social Networks</h2>
			<div class="about">
				<div class="social-media">
					<ul class="list-inline">
						<li><a href="http://www.facebook.com/rsclegacy" title=""><i class="fa fa-facebook"></i></a></li>
						<li><a href="http://www.twitter.com/rsclegacy" title=""><i class="fa fa-twitter"></i></a></li>
						<li><a href="http://www.youtube.com/rsclegacy" title=""><i class="fa fa-youtube"></i></a></li>
						<li><a href="https://www.reddit.com/r/rsclegacy" title=""><i class="fa fa-reddit"></i></a></li>
					</ul>
				</div>
			</div>
			<p><a href="http://wolfkingdom.net">Wolf Kingdom</a> is in no-way affiliated with <a href="http://www.jagex.com">Jagex Games Studio</a> or their games. Any references are made for identification purposes only.
				<strong>RuneScape</strong>, <strong>RuneScape Classic</strong> and <strong>Old RuneScape</strong> are registered trademarks of <a href="http://www.jagex.com">Jagex Games Studio</a>.</p>
			
		</div>
	</div>
</div>
<section class="copyright">
	<div class="container">
		<div class="col-sm-6">
		<p>Copyright &copy; RSCLegacy™ 2017 - <a href="terms.php">Terms of Service</a> &#149; <a href="about.php">Contact Us</a></p>
		</div>
	</div>
</section>
</div>
<?php if (($luna_config['o_cookie_bar'] == 1) && ($luna_user['is_guest']) && (!isset($_COOKIE['LunaCookieBar']))) { ?>
	<div class="navbar navbar-inverse navbar-fixed-bottom cookie-bar">
		<div class="container">
			<p class="navbar-text"><?php _e('We use cookies to give you the best experience on this board.', 'luna') ?></p>
			<form class="navbar-form navbar-right">
				<div class="form-group">
					<div class="btn-toolbar"><a class="btn btn-link" href="<?php echo $luna_config['o_cookie_bar_url'] ?>"><?php _e('More info', 'luna') ?></a><a class="btn btn-default" href="index.php?action=disable_cookiebar"><?php _e('Don\'t show again', 'luna') ?></a></div>
				</div>
			</form>
		</div>
	</div>
<?php
}

// End the transaction
$db->end_transaction();

// Display executed queries (if enabled)
if (defined('LUNA_DEBUG')) {
?>
<div class="container main">
    <div class="row">
        <div class="col-xs-12">
            <?php display_saved_queries(); ?>
        </div>
    </div>
</div>
<div class="footer container text-center">
<?php

// Display debug info (if enabled/defined)
if (defined('LUNA_DEBUG')) {
	// Calculate script generation time
	$time_diff = sprintf('%.3f', get_microtime() - $luna_start);
	echo sprintf(__('Generated in %1$s seconds &middot; %2$s queries executed', 'luna'), $time_diff, $db->get_num_queries());

	if (function_exists('memory_get_usage')) {
		echo ' &middot; '.sprintf(__('Memory usage: %1$s', 'luna'), file_size(memory_get_usage()));

		if (function_exists('memory_get_peak_usage'))
			echo ' '.sprintf(__('(Peak: %1$s)', 'luna'), file_size(memory_get_peak_usage()));
	}
}
?>
</div>
<?php
}

// Close the db connection (and free up any result data)
$db->close();
?>
	<script>
	  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

	  ga('create', 'UA-90953204-1', 'auto');
	  ga('send', 'pageview');

	</script>
	</body>
</html>
