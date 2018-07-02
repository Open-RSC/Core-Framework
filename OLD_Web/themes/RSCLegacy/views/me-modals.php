<div class="modal fade modal-form" id="newmail" tabindex="-1" role="dialog" aria-hidden="true">
	<div class="modal-dialog modal-xs">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title"><?php _e('Change email address', 'luna') ?></h4>
			</div>
			<form id="change_email" method="post" action="settings.php?action=change_email&amp;id=<?php echo $id ?>" onsubmit="return process_form(this)">
				<div class="modal-body">
					<fieldset>
						<input type="hidden" name="form_sent" value="1" />
						<input class="form-control" type="text" name="req_new_email" placeholder="<?php _e('New email', 'luna') ?>" />
						<input class="form-control" type="password" name="req_password" placeholder="<?php _e('Password', 'luna') ?>" />
						<p><?php _e('An email will be sent to your new address with an activation link. You must click the link in the email you receive to activate the new address.', 'luna') ?></p>
					</fieldset>
				</div>
				<div class="modal-footer">
					<button type="submit" class="btn btn-default" name="new_email"><?php _e('Save', 'luna') ?></button>
				</div>
			</form>
		</div>
	</div>
</div>

<div class="modal fade modal-form" id="newpass" tabindex="-1" role="dialog" aria-hidden="true">
	<div class="modal-dialog modal-xs">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title"><?php _e('Change password', 'luna') ?></h4>
			</div>
			<form id="change_pass" method="post" action="settings.php?action=change_pass&amp;id=<?php echo $id ?>" onsubmit="return process_form(this)">
				<div class="modal-body">
					<input type="hidden" name="form_sent" value="1" />
					<fieldset>
						<?php if (!$luna_user['is_admmod']): ?>
							<input class="form-control" type="password" name="req_old_password" placeholder="<?php _e('Old password', 'luna') ?>" />
						<?php endif; ?>
						<input class="form-control" type="password" name="req_new_password1" placeholder="<?php _e('New password', 'luna') ?>" />
						<input class="form-control" type="password" name="req_new_password2" placeholder="<?php _e('Confirm new password', 'luna') ?>" />
						<p class="help-block"><?php _e('Passwords must be at least 6 characters long and are case sensitive', 'luna') ?></p>
					</fieldset>
				</div>
				<div class="modal-footer">
					<button type="submit" class="btn btn-default" name="update"><?php _e('Save', 'luna') ?></button>
				</div>
			</form>
		</div>
	</div>
</div>

<div class="modal fade modal-form" id="newavatar" tabindex="-1" role="dialog" aria-hidden="true">
	<div class="modal-dialog modal-xs">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title"><?php _e('Change avatar', 'luna') ?></h4>
			</div>
			<form id="upload_avatar" method="post" enctype="multipart/form-data" action="settings.php?action=upload_avatar2&amp;id=<?php echo $luna_user['id'] ?>" onsubmit="return process_form(this)">
				<div class="modal-body">
					<fieldset>
						<input type="hidden" name="form_sent" value="1" />
						<input type="hidden" name="MAX_FILE_SIZE" value="<?php echo $luna_config['o_avatars_size'] ?>" />
						<input name="req_file" type="file" />
                        <span class="help-block"><?php printf(__('An avatar is a small image that will be displayed under your username in your comments. It has to be smaller than %s by %s pixels and %s bytes.', 'luna'), $luna_config['o_avatars_width'], $luna_config['o_avatars_height'], forum_number_format($luna_config['o_avatars_size'])) ?></span>
					</fieldset>
				</div>
				<div class="modal-footer">
					<button type="submit" class="btn btn-default" name="upload"><span class="fa fa-fw fa-upload"></span> <?php _e('Upload', 'luna') ?></button>
				</div>
			</form>
		</div>
	</div>
</div>
