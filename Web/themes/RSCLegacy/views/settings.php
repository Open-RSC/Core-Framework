<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;

$sections = array( 'profile', 'personalize', 'message', 'threads', 'time', 'admin' );
if ( isset( $_GET['section'] ) && in_array( $_GET['section'], $sections ) )
	$section = luna_htmlspecialchars( $_GET['section'] );
else
	$section = 'profile';

?>
<div class="profile-header container-fluid">
	<div class="jumbotron profile">
		<div class="container">
			<div class="col-sm-12">
				<h2 class="username"><?php echo $user['username'] ?></h2>
			</div>
		</div>
	</div>
</div>
<div id="wrapper" class="container">
	<div class="profile">
		<div class="col-xs-12 col-sm-3 sidebar">
			<div class="container-avatar">
				<img src="<?php echo get_avatar( $user['id'] ) ?>" alt="Avatar" class="img-avatar img-center">
			</div>
			<?php load_me_nav('settings'); ?>
		</div>
		<div class="col-xs-12 col-sm-9">
			<form id="profile-settings" method="post" action="settings.php?id=<?php echo $id ?>">
				<div class="title-block title-block-primary title-block-nav">
					<h2><i class="fa fa-fw fa-cogs"></i> <?php _e('Settings', 'luna') ?><span class="pull-right"><button class="btn btn-success" type="submit" name="update"><span class="fa fa-fw fa-check"></span> <?php _e('Save', 'luna') ?></button></span></h2>
					<ul class="nav nav-tabs" role="tablist">
						<li role="presentation" class="active"><a href="#profile" aria-controls="profile" role="tab" data-toggle="tab"><i class="fa fa-fw fa-user"></i><span class="hidden-sm hidden-xs"> <?php _e('Profile', 'luna') ?></span></a></li>
						<li role="presentation"><a href="#contact" aria-controls="contact" role="tab" data-toggle="tab"><i class="fa fa-fw fa-share-alt"></i><span class="hidden-sm hidden-xs"> <?php _e('Contact', 'luna') ?></span></a></li>
						<li role="presentation"><a href="#thread" aria-controls="thread" role="tab" data-toggle="tab"><i class="fa fa-fw fa-list"></i><span class="hidden-sm hidden-xs"> <?php _e('Thread', 'luna') ?></span></a></li>
						<li role="presentation"><a href="#time" aria-controls="time" role="tab" data-toggle="tab"><i class="fa fa-fw fa-clock-o"></i><span class="hidden-sm hidden-xs"> <?php _e('Time', 'luna') ?></span></a></li>
						<?php if ($luna_user['g_id'] == LUNA_ADMIN || ($luna_user['g_moderator'] == '1' && $luna_user['g_mod_ban_users'] == '1')): ?>
							<li role="presentation"><a href="#admin" aria-controls="admin" role="tab" data-toggle="tab"><i class="fa fa-fw fa-dashboard"></i><span class="hidden-sm hidden-xs"> <?php _e('Admin', 'luna') ?></span></a></li>
						<?php endif; ?>
					</ul>
				</div>
				<div class="tab-content">
					<div role="tabpanel" class="tab-pane active" id="profile">
						<fieldset class="form-horizontal form-setting">
							<input type="hidden" name="form_sent" value="1" />
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Username', 'luna')?></label>
								<div class="col-sm-9">
									<?php echo $username_field ?>
								</div>
							</div>
							<?php if (($luna_user['id'] == $id || $luna_user['g_id'] == LUNA_ADMIN || ($user['g_moderator'] == '0' && $luna_user['g_mod_change_passwords'] == '1')) ): ?>
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Password', 'luna') ?></label>
								<div class="col-sm-9">
									<a class="btn btn-primary" href="#" data-toggle="modal" data-target="#newpass"><?php _e('Change password', 'luna') ?></a>
								</div>
							</div>
							<?php endif; ?>
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Email', 'luna') ?></label>
								<div class="col-sm-9">
									<div class="input-group">
										<?php echo $email_field ?>
										<?php echo $email_button ?>
									</div>
								</div>
							</div>
							<hr />
							<input type="hidden" name="form_sent" value="1" />
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Real name', 'luna') ?></label>
								<div class="col-sm-9">
									<input type="text" class="form-control" name="form[realname]" value="<?php echo luna_htmlspecialchars($user['realname']) ?>" maxlength="40" />
								</div>
							</div>
							<?php if (isset($title_field)): ?>
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Title', 'luna') ?><span class="help-block"><?php _e('Leave blank to use default', 'luna') ?></span></label>
								<div class="col-sm-9">
									<?php echo $title_field ?>
								</div>
							</div>
							<?php endif; ?>
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Location', 'luna') ?></label>
								<div class="col-sm-9">
									<input type="text" class="form-control" name="form[location]" value="<?php echo luna_htmlspecialchars($user['location']) ?>" maxlength="30" />
								</div>
							</div>
							<?php if ($luna_config['o_avatars'] == '1' || $luna_config['o_signatures'] == '1') { ?>
							<hr />
							<?php } if ($luna_config['o_avatars'] == '1') { ?>
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Avatar', 'luna') ?><span class="help-block"><?php _e('Upload an image to represent you', 'luna') ?></span></label>
								<div class="col-sm-9">
									<?php echo $avatar_user ?>
									<?php echo $avatar_field ?>
								</div>
							</div>
							<?php } if ($luna_config['o_signatures'] == '1') { ?>
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Signature', 'luna') ?><span class="help-block"><?php _e('Write a small piece to attach to every comment you make', 'luna') ?></span></label>
								<div class="col-sm-9">
									<textarea class="form-control" name="signature" rows="4"><?php echo luna_htmlspecialchars($user['signature']) ?></textarea>
									<span class="help-block"><?php printf(__('Max length: %s characters / Max lines: %s', 'luna'), forum_number_format($luna_config['o_sig_length']), $luna_config['o_sig_lines']) ?></span>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Signature preview', 'luna') ?></label>
								<div class="col-sm-9">
									<div class="well">
										<?php echo $signature_preview ?>
									</div>
								</div>
							</div>
							<?php } ?>
						</fieldset>
					</div>
					<div role="tabpanel" class="tab-pane" id="appearance">
						<fieldset class="form-horizontal form-setting">
		<?php
		
		$languages = forum_list_langs();
		
		// Only display the language selection box if there's more than one language available
		if (count($languages) > 1) {
		?>
							<hr />
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Language', 'luna') ?></label>
								<div class="col-sm-9">
									<select class="form-control" name="form[language]">
		<?php
				foreach ($languages as $temp) {
					if ($user['language'] == $temp)
						echo "\t\t\t\t\t\t\t\t".'<option value="'.$temp.'" selected>'.$temp.'</option>'."\n";
					else
						echo "\t\t\t\t\t\t\t\t".'<option value="'.$temp.'">'.$temp.'</option>'."\n";
				}
		?>
									</select>
								</div>
							</div>
		<?php } ?>
						</fieldset>
					</div>
					<div role="tabpanel" class="tab-pane" id="contact">
						<fieldset class="form-horizontal form-setting">
							<?php if ($luna_config['o_enable_inbox'] == 1) { ?>
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Inbox', 'luna') ?></label>
								<div class="col-sm-9">
									<div class="checkbox">
										<label>
											<input type="checkbox" name="form[use_inbox]" value="1"<?php if ($user['use_inbox'] == '1') echo ' checked' ?> />
											<?php _e('Allow users to send messages with Inbox.', 'luna') ?>
										</label>
									</div>
								</div>
							</div>
							<hr />
							<?php } ?>
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Email settings', 'luna') ?></label>
								<div class="col-sm-9">
									<div class="radio">
										<label>
											<input type="radio" name="form[email_setting]" value="0"<?php if ($user['email_setting'] == '0') echo ' checked' ?> />
											<?php _e('Display your email address.', 'luna') ?>
										</label>
									</div>
									<div class="radio">
										<label>
											<input type="radio" name="form[email_setting]" value="1"<?php if ($user['email_setting'] == '1') echo ' checked' ?> />
											<?php _e('Hide your email address but allow form email.', 'luna') ?>
										</label>
									</div>
									<div class="radio">
										<label>
											<input type="radio" name="form[email_setting]" value="2"<?php if ($user['email_setting'] == '2') echo ' checked' ?> />
											<?php _e('Hide your email address and disallow form email.', 'luna') ?>
										</label>
									</div>
								</div>
							</div>
							<?php if ($luna_config['o_forum_subscriptions'] == '1' || $luna_config['o_thread_subscriptions'] == '1') { ?>
							<hr />
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Subscriptions', 'luna') ?></label>
								<div class="col-sm-9">
									<div class="checkbox">
										<label>
											<input type="checkbox" name="form[notify_with_comment]" value="1"<?php if ($user['notify_with_comment'] == '1') echo ' checked' ?> />
											<?php _e('Include a plain text version of new comments in subscription notification emails.', 'luna') ?>
										</label>
									</div>
									<?php if ($luna_config['o_thread_subscriptions'] == '1') { ?>
									<div class="checkbox">
										<label>
											<input type="checkbox" name="form[auto_notify]" value="1"<?php if ($user['auto_notify'] == '1') echo ' checked' ?> />
											<?php _e('Automatically subscribe to every thread you comment in.', 'luna') ?>
										</label>
									</div>
									<?php } ?>
								</div>
							</div>
							<?php } ?>
							<hr />
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Website', 'luna') ?></label>
								<div class="col-sm-9">
									<div class="input-group input">
										<span class="input-group-addon" id="website-addon"><span class="fa fa-fw fa-link"></span></span>
										<input id="website" type="text" class="form-control" name="form[url]" value="<?php echo luna_htmlspecialchars($user['url']) ?>" maxlength="80" aria-describedby="website-addon">
									</div>
								</div>
							</div>
							<hr />
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Microsoft Account', 'luna') ?></label>
								<div class="col-sm-9">
									<div class="input-group input">
										<span class="input-group-addon" id="microsoft-addon"><span class="fa fa-fw fa-windows"></span></span>
										<input id="microsoft" type="text" class="form-control" name="form[msn]" value="<?php echo luna_htmlspecialchars($user['msn']) ?>" maxlength="50" aria-describedby="microsoft-addon">
									</div>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Facebook', 'luna') ?></label>
								<div class="col-sm-9">
									<div class="input-group">
										<span class="input-group-addon" id="facebook-addon"><span class="fa fa-fw fa-facebook-square"></span></span>
										<input id="facebook" type="text" class="form-control" name="form[facebook]" value="<?php echo luna_htmlspecialchars($user['facebook']) ?>" maxlength="50" aria-describedby="facebook-addon">
									</div>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Twitter', 'luna') ?></label>
								<div class="col-sm-9">
									<div class="input-group">
										<span class="input-group-addon" id="twitter-addon"><span class="fa fa-fw fa-twitter"></span></span>
										<input id="twitter" type="text" class="form-control" name="form[twitter]" value="<?php echo luna_htmlspecialchars($user['twitter']) ?>" maxlength="50" aria-describedby="twitter-addon">
									</div>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Google+', 'luna') ?></label>
								<div class="col-sm-9">
									<div class="input-group">
										<span class="input-group-addon" id="google-addon"><span class="fa fa-fw fa-google-plus"></span></span>
										<input id="google" type="text" class="form-control" name="form[google]" value="<?php echo luna_htmlspecialchars($user['google']) ?>" maxlength="50" aria-describedby="google-addon">
									</div>
								</div>
							</div>
						</fieldset>
					</div>
					<div role="tabpanel" class="tab-pane" id="thread">
						<fieldset class="form-horizontal form-setting">
							<?php if ($luna_config['o_allow_advanced_editor'] == '1' || $luna_config['o_allow_dialog_editor'] == '1'): ?>
								<div class="form-group">
									<label class="col-sm-3 control-label"><?php _e('Editor', 'luna') ?></label>
									<div class="col-sm-9">
										<?php if ($luna_config['o_allow_advanced_editor'] == '1'): ?>
											<div class="checkbox">
												<label>
													<input type="checkbox" name="form[advanced_editor]" value="1"<?php if ($user['advanced_editor'] == '1') echo ' checked' ?> />
													<?php _e('Show a secondary toolbar with more options in the editor.', 'luna') ?>
												</label>
											</div>
										<?php endif; if ($luna_config['o_allow_dialog_editor'] == '1'): ?>
											<div class="checkbox">
												<label>
													<input type="checkbox" name="form[dialog_editor]" value="1"<?php if ($user['dialog_editor'] == '1') echo ' checked' ?> />
													<?php _e('Show dialogs for advanced BBCode-tags like URL.', 'luna') ?>
												</label>
											</div>
										<?php endif; ?>
									</div>
								</div>
							<?php endif; if ($luna_config['o_avatars'] == '1' || $luna_config['o_message_img_tag'] == '1'): ?>
								<div class="form-group">
									<label class="col-sm-3 control-label"><?php _e('Comments', 'luna') ?></label>
									<div class="col-sm-9">
										<?php if ($luna_config['o_message_img_tag'] == '1'): ?>
											<div class="checkbox">
												<label>
													<input type="checkbox" name="form[show_img]" value="1"<?php if ($user['show_img'] == '1') echo ' checked' ?> />
													<?php _e('Show images in comments.', 'luna') ?>
												</label>
											</div>
										<?php endif; ?>
									</div>
								</div>
							<?php endif; if ($luna_config['o_signatures'] == '1'): ?>
								<div class="form-group">
									<label class="col-sm-3 control-label"><?php _e('Signatures', 'luna') ?></label>
									<div class="col-sm-9">
											<div class="checkbox">
												<label>
													<input type="checkbox" name="form[show_sig]" value="1"<?php if ($user['show_sig'] == '1') echo ' checked' ?> />
													<?php _e('Show user signatures.', 'luna') ?>
												</label>
											</div>
										<?php if ($luna_config['o_sig_img_tag'] == '1'): ?>
											<div class="checkbox">
												<label>
													<input type="checkbox" name="form[show_img_sig]" value="1"<?php if ($user['show_img_sig'] == '1') echo ' checked' ?> />
													<?php _e('Show images in user signatures.', 'luna') ?>
												</label>
											</div>
										<?php endif; if ($luna_config['o_smilies_sig'] == '1'): ?>
											<div class="checkbox">
												<label>
													<input type="checkbox" name="form[show_smilies]" value="1"<?php if ($user['show_smilies'] == '1') echo ' checked' ?> />
													<?php _e('Show smilies as graphic icons.', 'luna') ?>
												</label>
											</div>
										<?php endif; ?>
									</div>
								</div>
							<?php endif; ?>
							<hr />
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Threads per page', 'luna') ?></label>
								<div class="col-sm-9">
									<input type="number" class="form-control" name="form[disp_threads]" value="<?php echo $user['disp_threads'] ?>" maxlength="3" />
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Comments per page', 'luna') ?></label>
								<div class="col-sm-9">
									<input type="number" class="form-control" name="form[disp_comments]" value="<?php echo $user['disp_comments'] ?>" maxlength="3" />
								</div>
							</div>
						</fieldset>
					</div>
					<div role="tabpanel" class="tab-pane" id="time">
						<fieldset class="form-horizontal form-setting">
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Time zone', 'luna') ?></label>
								<div class="col-sm-9">
									<select class="form-control" name="form[php_timezone]">
		<?php
		$timezones = DateTimeZone::listIdentifiers();
		foreach ($timezones as $timezone) {
		?>
										<option value="<?php echo $timezone ?>"<?php if ($user['php_timezone'] == $timezone) echo ' selected' ?>><?php echo $timezone ?></option>
		<?php
		}
		?>
									</select>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Time format', 'luna') ?></label>
								<div class="col-sm-9">
									<select class="form-control" name="form[time_format]">
		<?php
							foreach (array_unique($forum_time_formats) as $key => $time_format) {
								echo "\t\t\t\t\t\t\t\t".'<option value="'.$key.'"';
								if ($user['time_format'] == $key)
									echo ' selected';
								echo '>'. format_time(time(), false, null, $time_format, true, true);
								if ($key == 0)
									echo ' ('.__('Default', 'luna').')';
								echo "</option>\n";
							}
		?>
									</select>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label"><?php _e('Date format', 'luna') ?></label>
								<div class="col-sm-9">
									<select class="form-control" name="form[date_format]">
		<?php
							foreach (array_unique($forum_date_formats) as $key => $date_format) {
								echo "\t\t\t\t\t\t\t\t".'<option value="'.$key.'"';
								if ($user['date_format'] == $key)
									echo ' selected';
								echo '>'. format_time(time(), true, $date_format, null, false, true);
								if ($key == 0)
									echo ' ('.__('Default', 'luna').')';
								echo "</option>\n";
							}
		?>
									</select>
								</div>
							</div>
						</fieldset>
					</div>
					<?php if ($luna_user['g_id'] == LUNA_ADMIN || ($luna_user['g_moderator'] == '1' && $luna_user['g_mod_ban_users'] == '1')): ?>
					<div role="tabpanel" class="tab-pane" id="admin">
						<fieldset class="form-horizontal form-setting">
							<?php if (($luna_user['g_moderator'] == '1')  && $luna_user['id'] != $id) { ?>
								<div class="form-group">
									<label class="col-sm-3 control-label"><?php _e('Choose user group', 'luna') ?></label>
									<div class="col-sm-9">
										<div class="input-group">
											<select id="group_id" class="form-control" name="group_id">
		<?php
		
					$result = $db->query('SELECT g_id, g_title FROM '.$db->prefix.'groups WHERE g_id!='.LUNA_GUEST.' ORDER BY g_title') or error('Unable to fetch user group list', __FILE__, __LINE__, $db->error());
		
					while ($cur_group = $db->fetch_assoc($result)) {
						if ($cur_group['g_id'] == $user['g_id'] || ($cur_group['g_id'] == $luna_config['o_default_user_group'] && $user['g_id'] == ''))
							echo "\t\t\t\t\t\t\t\t".'<option value="'.$cur_group['g_id'].'" selected>'.luna_htmlspecialchars($cur_group['g_title']).'</option>'."\n";
						else
							echo "\t\t\t\t\t\t\t\t".'<option value="'.$cur_group['g_id'].'">'.luna_htmlspecialchars($cur_group['g_title']).'</option>'."\n";
					}
		
		?>
											</select>
											<span class="input-group-btn">
												<input type="submit" class="btn btn-primary" name="update_group_membership" value="<?php _e('Save', 'luna') ?>" />
											</span>
										</div>
									</div>
								</div>
								<hr />
							<?php } else if ($user['g_moderator'] == '1' || $user['g_id'] == LUNA_ADMIN) { ?>
								<div class="form-group">
									<label class="col-sm-3 control-label"><?php _e('Delete or ban user', 'luna') ?></label>
									<div class="col-sm-9">
										<button class="btn btn-danger" type="submit" name="ban"><i class="fa fa-fw fa-ban"></i> <?php _e('Ban', 'luna') ?></button>
										<?php if ($user['g_id'] == LUNA_ADMIN) { ?>
											<button class="btn btn-danger" type="submit" name="delete_user"><i class="fa fa-fw fa-trash"></i> <?php _e('Delete', 'luna') ?></button>
										<?php } ?>
									</div>
								</div>
								<hr />
								<div class="form-group">
									<label class="col-sm-3 control-label"><?php _e('Set moderator access', 'luna') ?><br /><button type="submit" class="btn btn-primary" name="update_forums"><span class="fa fa-fw fa-check"></span> <?php _e('Update forums', 'luna') ?></button></label>
									<div class="col-sm-9">
										<p><?php _e('Choose which forums this user should be allowed to moderate. Note: This only applies to moderators. Administrators always have full permissions in all forums.', 'luna') ?></p>
		<?php
		
					$result = $db->query('SELECT c.id AS cid, c.cat_name, f.id AS fid, f.forum_name, f.moderators FROM '.$db->prefix.'categories AS c INNER JOIN '.$db->prefix.'forums AS f ON c.id=f.cat_id ORDER BY c.disp_position, c.id, f.disp_position') or error('Unable to fetch category/forum list', __FILE__, __LINE__, $db->error());
		
					$cur_category = 0;
					while ($cur_forum = $db->fetch_assoc($result)) {
						if ($cur_forum['cid'] != $cur_category) { // A new category since last iteration?
		
							echo "\t\t\t\t\t\t\t".'<div>'."\n\t\t\t\t\t\t\t\t".'<br /><strong>'.luna_htmlspecialchars($cur_forum['cat_name']).'</strong>'."\n\t\t\t\t\t\t\t\t".'</div>';
							$cur_category = $cur_forum['cid'];
						}
		
						$moderators = ($cur_forum['moderators'] != '') ? unserialize($cur_forum['moderators']) : array();
		
						echo "\n\t\t\t\t\t\t\t\t\t".'<input type="checkbox" name="moderator_in['.$cur_forum['fid'].']" value="1"'.((in_array($id, $moderators)) ? ' checked' : '').' /> '.luna_htmlspecialchars($cur_forum['forum_name']).'<br />'."\n";
					}
		
		?>
										</div>
									</div>
									<hr />
								<?php } ?>
								<?php if ($luna_user['g_id'] == LUNA_ADMIN): ?>
									<div class="form-group">
										<label class="col-sm-3 control-label"><?php _e('Comments', 'luna') ?></label>
										<div class="col-sm-9">
											<input type="number" class="form-control" name="num_comments" value="<?php echo $user['num_comments'] ?>" maxlength="8" />
										</div>
									</div>
								<?php endif; if ($luna_user['is_admmod']): ?>
									<div class="form-group">
										<label class="col-sm-3 control-label"><?php _e('Admin note', 'luna') ?></label>
										<div class="col-sm-9">
											<input id="admin_note" type="text" class="form-control" name="admin_note" value="<?php echo luna_htmlspecialchars($user['admin_note']) ?>" maxlength="30" />
										</div>
									</div>
								<?php endif; ?>
							</fieldset>
						</div>
					<?php endif; ?>
				</div>
			</form>
		</div>
	</div>
</div>