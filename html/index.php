<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

define('LUNA_ROOT', dirname(__FILE__).'/');
require LUNA_ROOT.'include/common.php';
require LUNA_ROOT.'include/parser.php';

if ($luna_user['g_read_board'] == '0')
	message(__('You do not have permission to view this page.', 'luna'), false, '403 Forbidden');

if ($luna_config['o_board_slogan'] == '')
    $page_title = array(luna_htmlspecialchars($luna_config['o_board_title']));
else
    $page_title = array(luna_htmlspecialchars($luna_config['o_board_title']).' &middot; '.$luna_config['o_board_slogan']);

define('LUNA_ALLOW_INDEX', 1);
define('LUNA_ACTIVE_PAGE', 'index');
define('LUNA_CANONICAL_INDEX', 1);

$NEWS_BOARD = 16; // Board ID.
$BOARD_TOTAL = 5; // MAX news posts.

$fetch_news = $db->query("SELECT 
users.group_id, 
comments.commenter, 
comments.message, 
comments.commented, 
threads.subject, 
threads.num_views, 
threads.num_replies, 
threads.id FROM threads JOIN comments ON comments.commented = threads.commented JOIN users ON users.username = threads.commenter WHERE threads.forum_id = '" . $NEWS_BOARD . "' ORDER BY threads.commented DESC LIMIT 0 , " . $BOARD_TOTAL);

/*$fetch_topics = $db->query("SELECT 
users.group_id, 
users.id as uid, 
comments.commenter, 
threads.subject, 
threads.num_views, 
threads.num_replies, 
threads.id FROM threads JOIN comments ON comments.commented = threads.commented JOIN users ON users.username = threads.commenter ORDER BY threads.commented DESC LIMIT 0 , " . $BOARD_TOTAL);
*/
// How many top levlers do we want to display?
$MAX_STANDING_PLAYERS = 3;
// Fetch top levlers
$top_highscore = $db->query("SELECT (" . GAME_BASE . "experience.exp_attack+" . GAME_BASE . "experience.exp_defense+" . GAME_BASE . "experience.exp_strength+" . GAME_BASE . "experience.exp_hits+" . GAME_BASE . "experience.exp_ranged+" . GAME_BASE . "experience.exp_prayer+" . GAME_BASE . "experience.exp_magic+" . GAME_BASE . "experience.exp_cooking+" . GAME_BASE . "experience.exp_woodcut+" . GAME_BASE . "experience.exp_fletching+" . GAME_BASE . "experience.exp_fishing+" . GAME_BASE . "experience.exp_firemaking+" . GAME_BASE . "experience.exp_crafting+" . GAME_BASE . "experience.exp_smithing+" . GAME_BASE . "experience.exp_mining+" . GAME_BASE . "experience.exp_herblaw+" . GAME_BASE . "experience.exp_agility+" . GAME_BASE . "experience.exp_thieving) AS 'totals', " . GAME_BASE . "players.username, " . GAME_BASE . "players.id, " . GAME_BASE . "players.combat, " . GAME_BASE . "players.skill_total FROM " . GAME_BASE . "players JOIN " . GAME_BASE . "experience ON " . GAME_BASE . "experience.playerID = " . GAME_BASE . "players.id WHERE " . GAME_BASE . "players.skill_total > 0 AND " . GAME_BASE . "players.highscoreopt = 0 AND " . GAME_BASE . "players.group_id = 0 OR " . GAME_BASE . "players.group_id > 2 AND " . GAME_BASE . "players.banned = 0 ORDER BY " . GAME_BASE . "players.skill_total DESC, totals DESC LIMIT 0, " . $MAX_STANDING_PLAYERS . "") or error('Unable to fetch top lvlers', __FILE__, __LINE__, $db->error());

$top_kd = $db->query("SELECT username,id,deaths,kills FROM " . GAME_BASE . "players WHERE kills > 0 AND highscoreopt = 0 AND group_id = 0 OR group_id > 2 AND banned = 0 ORDER BY kills DESC LIMIT 0, " . $MAX_STANDING_PLAYERS . "") or error('Unable to fetch top pkers', __FILE__, __LINE__, $db->error());

$top_poster = $db->query("SELECT username,id,num_comments FROM users WHERE num_comments > 0 ORDER BY num_comments DESC LIMIT 0, " . $MAX_STANDING_PLAYERS . "") or error('Unable to fetch top posters', __FILE__, __LINE__, $db->error());

$activity = $db->query("SELECT message, time, username FROM " . GAME_BASE . "live_feeds ORDER BY id DESC LIMIT 0, 20");

$threads = $db->query('SELECT users.group_id, threads.commented, threads.commenter, threads.first_comment_id, threads.subject FROM '.$db->prefix.'threads JOIN '.$db->prefix.'users ON users.username = threads.commenter WHERE moved_to IS NULL AND forum_id != 16 AND forum_id != 17 AND forum_id != 18 ORDER BY commented DESC LIMIT 0 , 20') or error('Unable to fetch last_post/last_post_id/last_poster', __FILE__, __LINE__, $db->error());

require load_page('header.php');
?>
<div id="wrapper" class="container">
	<div class="content">
		<?php
			require load_page('frontpage.php');
		?>
		<div id="content-side">
			<div class="col-sm-3 content-r-side">
				<div class="content-right">
				<div class="mother-icons" id="download_rsc">
					<div class="panel panel-default">
						<div class="panel-heading"><h4 class="panel-title">Play RSCLegacy Now</h4></div>
						<div class="base_body">
							<p>RuneScape Classic is the original version of RuneScape. Play for Free on your Android Device or Computer by downloading the APK or Launcher.</p>
							<a href="RSCLauncher.jar">
								<img src="img/icons/download_box.png" width="223" height="80" alt="Game Download">
							</a>
							<a href="android/wolfkingdom.apk">
								<img src="img/icons/android_box.png" width="223" height="80" alt="Game Download">
							</a>
						</div>
					</div>
				</div>
				<?php
				require load_page('frontpage-statistics.php');
				require load_page('frontpage-standings.php');
				//require load_page('frontpage-social.php');
				?>
				</div>
			</div>
		</div>
	</div>
</div>
<?php
require load_page('footer.php');
