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

// Send the Content-type header in case the web server is setup to send something else
header('Content-type: text/html; charset=utf-8');

// Define $p if it's not set to avoid a PHP notice
$p = isset($p) ? $p : null;

// add code?


?>
<!DOCTYPE html>
<html class="backstage">
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0, shrink-to-fit=no">
		<link rel="stylesheet" href="../vendor/css/bootstrap.min.css">
		<link rel="stylesheet" href="../vendor/css/font-awesome.min.css">
		<link rel="stylesheet" type="text/css" href="css/style.css" />
		<link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.9.1/themes/smoothness/jquery-ui.css">
        <link rel="icon" href="../favicon.ico" />
		<meta name="ROBOTS" content="NOINDEX, FOLLOW" />
		<title><?php _e('Admin Game Panel', 'luna') ?></title>
	</head>
	<body>
        <nav class="navbar navbar-default" role="navigation">
            <div class="container">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="navbar-brand" href="../index.php"><i class="fa fa-fw fa-angle-left hidden-xs"></i><span class="visible-xs-inline"><?php echo $luna_config['o_board_title'] ?></span></a>
                </div>
                <div class="navbar-collapse collapse">
                    <ul class="nav navbar-nav">
                        <li id="nav-first"><a href="../backstage/"><i class="fa fa-fw fa-dashboard"></i> <?php _e('Forum Backstage', 'luna') ?></a></li>
						<li><a href="../admin"><i class="fa fa-cogs"></i> <?php _e('Game Panel', 'luna') ?></a></li>
                    </ul>
        <?php
        $logout_url = '../login.php?action=out&amp;id='.$luna_user['id'].'&amp;csrf_token='.luna_csrf_token();
        ?>
                    <ul class="nav navbar-nav navbar-right">
						<form class="navbar-form navbar-left" role="search" method="get" action="index.php">
						  <div class="form-group">
							<input type="text" class="form-control" name="player_input" placeholder="Search for a player" maxlength="12">
							<input type="hidden" class="form-control" name="search_type" value='0'>
							<button class="search-small__submit" type="submit"><i class="fa fa-fw fa-search"></i></button>
						  </div>
						</form>
                        <li class="dropdown usermenu">
                            <a href="../profile.php?id=<?php echo $luna_user['id'] ?>" class="dropdown-toggle dropdown-user" data-toggle="dropdown">
                                <?php echo draw_user_avatar($luna_user['id'], true, 'avatar'); ?><span class="hidden-lg hidden-md hidden-sm"> <?php echo luna_htmlspecialchars($luna_user['username']); ?></span>
                            </a>
                            <ul class="dropdown-menu">
                                <li><a href="../profile.php?id=<?php echo $luna_user['id'] ?>"><i class="fa fa-fw fa-user"></i> <?php _e('Profile', 'luna') ?></a></li>
                                <li><a href="../inbox.php"><i class="fa fa-fw fa-paper-plane-o"></i> <?php _e('Inbox', 'luna') ?></a></li>
                                <li><a href="../settings.php?id=<?php echo $luna_user['id'] ?>"><i class="fa fa-fw fa-cogs"></i> <?php _e('Settings', 'luna') ?></a></li>
                                <li class="divider"></li>
                                <li><a href="../help.php"><i class="fa fa-fw fa-info-circle"></i> <?php _e('Help', 'luna') ?></a></li>
                                <li class="divider"></li>
                                <li><a href="<?php echo $logout_url; ?>"><i class="fa fa-fw fa-sign-out"></i> <?php _e('Logout', 'luna') ?></a></li>
                            </ul>
                        </li>
                    </ul>
                </div>
            </div>
        </nav>
        <div class="content ">
            <div class="container main">
                <?php if ($luna_config['o_maintenance'] == '1') { ?>
                    <div class="row"><div class="col-xs-12"><div class="alert alert-danger"><i class="fa fa-fw fa-exclamation-triangle"></i> <?php _e('Luna is currently set in Maintenance Mode. Do not log off.', 'luna') ?></div></div></div>
                <?php } ?>
				<div class="content-left">
					<div class="col-sm-2 sidebar">
						<div class="list-group list-group-luna">
							<a class="<?php if (LUNA_PAGE == 'player') echo 'active'; ?> list-group-item" href="index.php"><i class="fa fa-user-secret" aria-hidden="true"></i> <?php _e('Player Summary', 'luna') ?></a>
							<?php if($luna_user['group_id'] == 1) { ?>
							<a class="<?php if (LUNA_PAGE == 'server_totals') echo 'active'; ?> list-group-item" href="totals.php"><i class="fa fa-pie-chart" aria-hidden="true"></i> <?php _e('Server Totals', 'luna') ?></a>
							<?php } ?>
							<li class="list-group-item">
								<p class="list-group-item_header"><i class="fa fa-history" aria-hidden="true"></i> <?php _e('Logging', 'rscl') ?></p>
							</li>
							<ul class="list-group-item_sub-list">
							<li class="list-group-item_sub">
							<a class="<?php if (LUNA_PAGE == 'player_cache') echo 'active'; ?> list-group-sub_item" href="player_cache.php"><?php _e('Player Cache log', 'luna') ?></a>
							</li>
							<li class="list-group-item_sub">
							<a class="<?php if (LUNA_PAGE == 'player_name_change') echo 'active'; ?> list-group-sub_item" href="player_name_change.php"><?php _e('Player Name Change log', 'luna') ?></a>
							</li>
							<li class="list-group-item_sub">
							<a class="<?php if (LUNA_PAGE == 'auction_log') echo 'active'; ?> list-group-sub_item" href="auction.php"><?php _e('Active Auction log', 'luna') ?></a>
							</li>
							<li class="list-group-item_sub">
							<a class="<?php if (LUNA_PAGE == 'collectable_log') echo 'active'; ?> list-group-sub_item" href="collectable_auction.php"><?php _e('Collectable Auction log', 'luna') ?></a>
							</li>
							<li class="list-group-item_sub">
							<a class="<?php if (LUNA_PAGE == 'generic_log') echo 'active'; ?> list-group-sub_item" href="generic.php"><?php _e('Generic log', 'luna') ?></a>
							</li>
							<li class="list-group-item_sub">
							<a class="<?php if (LUNA_PAGE == 'chat_log') echo 'active'; ?> list-group-sub_item" href="chat_log.php"><?php _e('Chat log', 'luna') ?></a>
							</li>
							<li class="list-group-item_sub">
							<a class="<?php if (LUNA_PAGE == 'private_message_log') echo 'active'; ?> list-group-sub_item" href="private_message.php"><?php _e('Private Message log', 'luna') ?></a>
							</li>
							<li class="list-group-item_sub">
							<a class="<?php if (LUNA_PAGE == 'game_report') echo 'active'; ?> list-group-sub_item" href="game_reports.php"><?php _e('Game Report log', 'luna') ?></a>
							</li>
							<li class="list-group-item_sub">
							<a class="<?php if (LUNA_PAGE == 'trade_log') echo 'active'; ?> list-group-sub_item" href="trades.php"><?php _e('Trade log', 'luna') ?></a>
							</li>
							<li class="list-group-item_sub">
							<a class="<?php if (LUNA_PAGE == 'live_feed_log') echo 'active'; ?> list-group-sub_item" href="live_feed.php"><?php _e('Live Feed log', 'luna') ?></a>
							</li>
							<?php if($luna_user['group_id'] == 1) { ?>
							<li class="list-group-item_sub">
							<a class="<?php if (LUNA_PAGE == 'staff_log') echo 'active'; ?> list-group-sub_item" href="staff.php"><?php _e('Staff log', 'luna') ?></a>
							</li>
							<li class="list-group-item_sub">
							<a class="<?php if (LUNA_PAGE == 'shop_log') echo 'active'; ?> list-group-sub_item" href="shop_log.php"><?php _e('Shop log', 'luna') ?></a>
							</li>
							<li class="list-group-item_sub">
							<a class="<?php if (LUNA_PAGE == 'donations') echo 'active'; ?> list-group-sub_item" href="donations.php"><?php _e('Donation log', 'luna') ?></a>
							</li>
							<?php } ?>
							</ul>
							<li class="list-group-item">
								<p class="list-group-item_header"><i class="fa fa-sticky-note" aria-hidden="true"></i> <?php _e('APIs', 'rscl') ?></p>
							</li>
							<ul class="list-group-item_sub-list">
							<li class="list-group-item_sub">
							<a class="<?php if (LUNA_PAGE == 'poll_API') echo 'active'; ?> list-group-sub_item" href="poll_API.php"><?php _e('Poll API', 'luna') ?></a>
							</li>
							<li class="list-group-item_sub">
							<a class="<?php if (LUNA_PAGE == 'guide_API') echo 'active'; ?> list-group-sub_item" href="guide_API.php?mode=create_guide"><?php _e('Guide API', 'luna') ?></a>
							</li>
							<li class="list-group-item_sub">
							<a class="<?php if (LUNA_PAGE == 'shop_API') echo 'active'; ?> list-group-sub_item" href="shop_API.php?mode=add_product"><?php _e('Shop API', 'luna') ?></a>
							</li>
							<li class="list-group-item_sub">
							<a class="<?php if (LUNA_PAGE == 'hof_API') echo 'active'; ?> list-group-sub_item" href="hof_API.php?mode=add_product"><?php _e('Hall Of Fame API', 'luna') ?></a>
							</li>
							<?php if($luna_user['group_id'] == 1) { ?>
							<li class="list-group-item_sub">
							<a class="<?php if (LUNA_PAGE == 'drop_API') echo 'active'; ?> list-group-sub_item" href="drop_API.php"><?php _e('NPCDrop API', 'luna') ?></a>
							</li>
							<?php } ?>
							</ul>
							<?php if($luna_user['group_id'] == 1) { ?>
							<li class="list-group-item">
								<p class="list-group-item_header"><i class="fa fa-wrench" aria-hidden="true"></i> <?php _e('Admin Tools', 'rscl') ?></p>
							</li>
							<ul class="list-group-item_sub-list">
							<li class="list-group-item_sub">
							<a class="<?php if (LUNA_PAGE == 'stats') echo 'active'; ?> list-group-sub_item" href="update_stats.php"><?php _e('Stat Modification', 'luna') ?></a>
							</li>
							<li class="list-group-item_sub">
							<a class="<?php if (LUNA_PAGE == 'rename_char') echo 'active'; ?> list-group-sub_item" href="rename_character.php"><?php _e('Rename Character', 'luna') ?></a>
							</li>
							<li class="list-group-item_sub">
							<a class="<?php if (LUNA_PAGE == 'transfer_char') echo 'active'; ?> list-group-sub_item" href="transfer.php"><?php _e('Transfer Character', 'luna') ?></a>
							</li>
							</ul>
							<?php } ?>
						</div>
					</div>
				</div>
	<?php
	if (isset($required_fields)) {
		// Output JavaScript to validate form (make sure required fields are filled out)

	?>
	<script type="text/javascript">
	/* <![CDATA[ */
	function process_form(the_form) {
		var required_fields = {
	<?php
		// Output a JavaScript object with localised field names
		$tpl_temp = count($required_fields);
		foreach ($required_fields as $elem_orig => $elem_trans) {
			echo "\t\t\"".$elem_orig.'": "'.addslashes(str_replace('&#160;', ' ', $elem_trans));
			if (--$tpl_temp) echo "\",\n";
			else echo "\"\n\t};\n";
		}
	?>
	if (document.all || document.getElementById) {
		for (var i = 0; i < the_form.length; ++i) {
			var elem = the_form.elements[i];
			if (elem.name && required_fields[elem.name] && !elem.value && elem.type && (/^(?:text(?:area)?|password|file)$/i.test(elem.type))) {
				alert('"' + required_fields[elem.name] + '" <?php _e('is a required field in this form.', 'luna') ?>');
				elem.focus();
				return false;
			}
		}
	}
	return true;
}
/* ]]> */
</script>
<?php

}

if (isset($page_head))
	echo implode("\n", $page_head);
