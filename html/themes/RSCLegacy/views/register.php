<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;

?>
<div id="wrapper" class="main container">
	<div id="register">
			<?php registration_error_panel($errors); ?>
			<form class="form-horizontal" id="register" method="post" action="register.php?action=register" onsubmit="this.register.disabled=true;if(process_form(this)){return true;}else{this.register.disabled=false;return false;}">
				<div class="title-block title-block-primary">
				<h2><i class="fa fa-fw fa-user"></i> <?php _e('Register', 'luna') ?><span class="pull-right"><button type="submit" class="btn btn-default-2" name="register"><span class="fa fa-fw fa-check"></span> <?php _e('Register', 'luna') ?></button></span></h2>
				</div>
				<div class="tab-content">
					<input type="hidden" name="form_sent" value="1" />
					<label class="required hidden"><?php _e('If you are human please leave this field blank!', 'luna') ?><input type="text" class="form-control" name="req_username" value="" maxlength="25" /></label>
					<div class="alert alert-notice">
					<p><strong style="color: darkred;">IMPORTANT:</strong> We strongly advice you to use a unique password for both your forum account and in-game character(s). <strong>DO NOT</strong> use the same password that you use elsewhere.</p>
					<p>By clicking "Register", you are also inidicating that you have read and ageed to abide our <a href="terms.php">Terms of Service & Rules</a>.</p>
					</div>
					<h2>Forum Account</h2>
					<div class="form-group">
						<label class="col-sm-3 control-label"><?php _e('Forum Username', 'luna') ?><span class="help-block"><?php _e('Enter a username between 2 and 12 characters long', 'luna') ?></span></label>
						<div class="col-sm-9">
							<input type="text" class="form-control" name="req_user" value="<?php if (isset($_POST['req_user'])) echo luna_htmlspecialchars($_POST['req_user']); ?>" maxlength="12" />
						</div>
					</div>
	<?php if ($luna_config['o_regs_verify'] == '0'): ?>
					<div class="form-group">
						<label class="col-sm-3 control-label"><?php _e('Password', 'luna') ?><span class="help-block"><?php _e('Passwords must be at least 6 characters long and are case sensitive', 'luna') ?></span></label>
						<div class="col-sm-9">
							<div class="row">
								<div class="col-sm-6">
									<input id="password" type="password" class="form-control" name="req_password1" value="<?php if (isset($_POST['req_password1'])) echo luna_htmlspecialchars($_POST['req_password1']); ?>" />
								</div>
								<div class="col-sm-6">
									<input type="password" class="form-control" name="req_password2" value="<?php if (isset($_POST['req_password2'])) echo luna_htmlspecialchars($_POST['req_password2']); ?>" />
								</div>
							</div>
						</div>
					</div>
	<?php endif; ?>
					<div class="form-group">
						<label class="col-sm-3 control-label"><?php _e('Email', 'luna') ?><?php if ($luna_config['o_regs_verify'] == '1'): ?><span class="help-block"><?php _e('Your password will be sent to this address, make sure it\'s valid', 'luna') ?></span><?php endif; ?></label>
						<div class="col-sm-9">
							<?php if ($luna_config['o_regs_verify'] == '1'): ?>
							<div class="row">
								<div class="col-sm-6">
							<?php endif; ?>
									<input type="text" class="form-control" name="req_email1" value="<?php if (isset($_POST['req_email1'])) echo luna_htmlspecialchars($_POST['req_email1']); ?>" maxlength="80" />
							<?php if ($luna_config['o_regs_verify'] == '1'): ?>
								</div>
								<div class="col-sm-6">
									<input type="text" class="form-control" name="req_email2" value="<?php if (isset($_POST['req_email2'])) echo luna_htmlspecialchars($_POST['req_email2']); ?>" maxlength="80" />
								</div>
							</div>
							<?php endif; ?>
						</div>
					</div>
					<h2>Game Account</h2>
					<div class="alert alert-notice">
					<p>To view, manage or create more in-game characters please visit the character profile from the website header!</p>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label"><?php _e('Character Username', 'luna') ?><span class="help-block"><?php _e('Enter a username between 2 and 12 characters long', 'luna') ?></span></label>
						<div class="col-sm-9">
							<input type="text" class="form-control" name="char_username" value="<?php if (isset($_POST['char_username'])) echo luna_htmlspecialchars($_POST['char_username']); ?>" maxlength="12" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label"><?php _e('Password', 'luna') ?><span class="help-block"><?php _e('Passwords must be at least 4 characters long and maximum 16 characters (regular number and letters only)', 'luna') ?></span></label>
						<div class="col-sm-9">
							<div class="row">
								<div class="col-sm-6">
									<input id="password" type="password" class="form-control" name="char_password1" value="<?php if (isset($_POST['char_password1'])) echo luna_htmlspecialchars($_POST['char_password1']); ?>" />
								</div>
								<div class="col-sm-6">
									<input type="password" class="form-control" name="char_password2" value="<?php if (isset($_POST['char_password2'])) echo luna_htmlspecialchars($_POST['char_password2']); ?>" />
								</div>
							</div>
						</div>
					</div>
					<?php $captcha->hook_register_before_submit(); ?>
				</div>
			</form>
	</div>
</div>