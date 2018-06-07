<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;

$num_users = num_users_online();
$num_guests = num_guests_online();
?>
<div id="wrapper" class="container">
	<div class="content">
		<div class="tabLinks forumsTabLinks">
			<ul class="secondaryContent blockLinksList">
				<?php if(!$luna_user['is_guest']) { ?>
				<li><a href="search.php?action=show_new"><span class="fa fa-fw fa-newspaper-o"></span> New posts</a></li>
				<?php } ?>
				<li><a href="search.php?action=show_recent" rel="nofollow"><span class="fa fa-fw fa-clock-o"></span> Active posts</a></li>
				<li><a href="search.php?action=show_unanswered" rel="nofollow"><span class="fa fa-fw fa-question"></span> Unanswered posts</a></li>
				<li><?php draw_mark_read('', 'forum') ?></li>
			</ul>
			<?php if ($luna_config['o_header_search'] && $luna_user['g_search'] == '1') { ?>
			<div class="col-sm-3 pull-right content-l-side">
			<form id="search" class="forum-search input-group" method="get" action="search.php?section=simple">
				<input type="hidden" name="action" value="search" />
				<input type="hidden" name="sort_dir" value="DESC" />
				<input class="form-control breadcrumb-search" type="text" name="keywords" placeholder="<?php _e('Search in comments', 'luna') ?>" maxlength="100" />
				<span class="input-group-btn">
					<button class="btn btn-default btn-search btn-search-crumb-icon" type="submit" name="search" accesskey="s"><i class="fa fa-fw fa-search"></i></button>
				</span>
			</form>
			</div>
			<?php } ?> 
		</div>
		<?php 
		draw_forum_list('forum.php', 1, 'category.php', '</div></div>'); ?> 
		<?php if ($luna_config['o_board_statistics'] == 1): ?>
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title">Statistics & Usergroup</h3>
				</div>
				<div class="panel-body">
                <div class="stats">
                    <div class="row">
                        <div class="col-md-2 col-sm-4 col-xs-12 text-center">
                            <h4><?php total_users(); ?></h4>
                            <?php echo _n( 'User', 'Users', get_total_users(), 'luna' ) ?>
                        </div>
                        <div class="col-md-2 col-sm-4 col-xs-12 text-center">
                            <h4><?php total_threads() ?></h4>
                            <?php echo _n( 'Thread', 'Threads', get_total_threads(), 'luna' ) ?>
                        </div>
                        <div class="col-md-2 col-sm-4 col-xs-12 text-center">
                            <h4><?php total_comments() ?></h4>
                            <?php echo _n('Comment', 'Comments', get_total_comments(), 'luna') ?>
                        </div>
                        <div class="col-md-2 col-sm-4 col-xs-12 text-center">
                            <h4><?php newest_user() ?></h4>
                            <?php _e('Newest user', 'luna') ?>
                        </div>
                        <div class="col-md-2 col-sm-4 col-xs-12 text-center">
                            <h4><?php echo forum_number_format($num_users) ?></h4>
                           <?php
                                echo _n('User online', 'Users online', $num_users, 'luna'); ?>
                        </div>
                        <div class="col-md-2 col-sm-4 col-xs-12 text-center">
                            <h4><?php echo forum_number_format($num_guests) ?></h4>
                            <?php echo _n('Guest online', 'Guests online', $num_guests, 'luna') ?>
                        </div>
                    </div>
					</div>
					</div>
					<div class="panel-footer">
					<?php
					if ($luna_config['o_users_online'] == '1')
					{
						// Fetch users online info and generate strings for output
						$result = $db->query('SELECT u.gold_time, u.premium_time, u.group_id, o.user_id, o.ident FROM '.$db->prefix.'online AS o LEFT JOIN users AS u ON u.username = o.ident WHERE o.idle=0 AND o.user_id>1 ORDER BY o.ident', true) or error('Unable to fetch online list', __FILE__, __LINE__, $db->error());

						if ($db->num_rows($result) > 0) {
							echo "\n\t\t\t\t".'<span class="users-online"><strong>'._n('User online:', 'Users online:', $num_users, 'luna').'</strong>';

							$ctr = 1;
							while ($luna_user_online = $db->fetch_assoc($result))
							{
								if ($luna_user['g_view_users'] == '1')
									echo "\n\t\t\t\t".'<a href="profile.php?id='.$luna_user_online['user_id'].'">'.user_append_rank_time(luna_htmlspecialchars($luna_user_online['ident']), $luna_user_online['group_id'], ($luna_user_online['premium_time'] > time() && $luna_user_online['gold_time'] > time() ? 2 : ($luna_user_online['gold_time'] > time() && $luna_user_online['premium_time'] < time() ? 1 : 0))).'</a>';
								else
									echo "\n\t\t\t\t".user_append_rank_time(luna_htmlspecialchars($luna_user_online['ident']), $luna_user_online['group_id'], ($luna_user_online['premium_time'] > time() && $luna_user_online['gold_time'] > time() ? 2 : ($luna_user_online['gold_time'] > time() && $luna_user_online['premium_time'] < time() ? 1 : 0)));

								if ($ctr < $num_users) echo ', '; $ctr++;
							}
						
							echo "\n\t\t\t\t".'</span><hr />';
						}
					}
					?>
						<div style="line-height: 25px; vertical-align:top;">
							<span style="background: transparent url('img/crown/admin.gif') 0 2px no-repeat;padding: 0 0 5px 18px;color: yellow;font-weight: bold;text-shadow: 1px 1px 10px yellow;font-size: 12px;">Administrator</span> |
							<span style="background: transparent url('img/crown/global_mod.png') 0 2px no-repeat;padding: 0 0 10px 18px;color: #4CAF50;font-weight: bold;text-shadow: 1px 1px 10px #4CAF50;font-size: 12px;">Global Moderator</span> |
							<span style="background: transparent url('img/crown/mod.gif') 0 2px no-repeat;padding: 0 0 10px 18px;color: silver;font-weight: bold;text-shadow: 1px 1px 10px silver;font-size: 12px;">Moderator</span> |
							<span style="background: transparent url('img/crown/event_mod.png') 0 2px no-repeat;padding: 0 0 10px 18px;color: magenta;font-weight: bold;text-shadow: 1px 1px 10px magenta;font-size: 12px;">Event Moderator</span> |
							
							<span style="background: transparent url('img/crown/developer.png') 0 -1px no-repeat;padding: 0 0 10px 18px;color: #A73C32;font-weight: bold;text-shadow: 1px 1px 10px #A73C32;font-size: 12px;">Developer</span> |
							<span style="color: darkorange;font-weight: bold;text-shadow: 1px 1px 10px darkorange;font-size: 12px;">Subscriber</span> |
						    <span style="color: #44eadf;font-weight: bold;text-shadow: 1px 1px 10px #44eadf;font-size: 12px;">Premium Subscriber</span>
						</div>
					</div>
                </div>
            <?php endif; ?>
			</div>
</div>