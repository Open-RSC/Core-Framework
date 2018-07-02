<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'settings');
define('LUNA_PAGE', 'settings');

require LUNA_ROOT.'include/common.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}

if (isset($_GET['remove-favicon'])) {
	confirm_referrer('backstage/settings.php', __('Bad HTTP_REFERER. If you have moved these forums from one location to another or switched domains, you need to update the Base URL manually in the database (look for o_base_url in the config table) and then clear the cache by deleting all .php files in the /cache directory.', 'luna'));

    @unlink(LUNA_ROOT.'/favicon.png');

	redirect('backstage/settings.php?saved=true');
}

if (isset($_POST['form_sent'])) {
	confirm_referrer('backstage/settings.php', __('Bad HTTP_REFERER. If you have moved these forums from one location to another or switched domains, you need to update the Base URL manually in the database (look for o_base_url in the config table) and then clear the cache by deleting all .php files in the /cache directory.', 'luna'));

	$form = array(
		'board_title'			=> luna_trim($_POST['form']['board_title']),
		'board_slogan'			=> luna_trim($_POST['form']['board_slogan']),
		'board_description'   	=> luna_trim($_POST['form']['board_description']),
		'default_lang'			=> luna_trim($_POST['form']['default_lang']),
		'board_tags'			=> luna_trim($_POST['form']['board_tags']),
		'base_url'				=> luna_trim($_POST['form']['base_url']),
		'timezone'				=> luna_trim($_POST['form']['timezone']),
		'time_format'			=> luna_trim($_POST['form']['time_format']),
		'date_format'			=> luna_trim($_POST['form']['date_format']),
		'timeout_visit'			=> (intval($_POST['form']['timeout_visit']) > 0) ? intval($_POST['form']['timeout_visit']) : 1,
		'timeout_online'		=> (intval($_POST['form']['timeout_online']) > 0) ? intval($_POST['form']['timeout_online']) : 1,
		'feed_type'				=> intval($_POST['form']['feed_type']),
		'feed_ttl'				=> intval($_POST['form']['feed_ttl']),
		'report_method'			=> intval($_POST['form']['report_method']),
		'mailing_list'			=> luna_trim($_POST['form']['mailing_list']),
		'cookie_bar'			=> isset($_POST['form']['cookie_bar']) ? '1' : '0',
		'cookie_bar_url'		=> luna_trim($_POST['form']['cookie_bar_url']),
		'announcement'			=> isset($_POST['form']['announcement']) ? '1' : '0',
		'announcement_title'	=> luna_trim($_POST['form']['announcement_title']),
		'announcement_type'		=> luna_trim($_POST['form']['announcement_type']),
		'announcement_message'	=> luna_trim($_POST['form']['announcement_message']),
		'admin_email'			=> strtolower(luna_trim($_POST['form']['admin_email'])),
		'webmaster_email'		=> strtolower(luna_trim($_POST['form']['webmaster_email'])),
		'regs_allow'			=> isset($_POST['form']['regs_allow']) ? '1' : '0',
		'regs_verify'			=> isset($_POST['form']['regs_verify']) ? '1' : '0',
		'regs_report'			=> isset($_POST['form']['regs_report']) ? '1' : '0',
		'rules'					=> isset($_POST['form']['rules']) ? '1' : '0',
		'rules_message'			=> luna_trim($_POST['form']['rules_message']),
		'default_email_setting'	=> intval($_POST['form']['default_email_setting']),
		'smtp_host'				=> luna_trim($_POST['form']['smtp_host']),
		'smtp_user'				=> luna_trim($_POST['form']['smtp_user']),
		'smtp_ssl'				=> isset($_POST['form']['smtp_ssl']) ? '1' : '0',
		'gzip'							=> isset($_POST['form']['gzip']) ? '1' : '0',
		'allow_banned_email'	=> isset($_POST['form']['allow_banned_email']) ? '1' : '0',
		'allow_dupe_email'		=> isset($_POST['form']['allow_dupe_email']) ? '1' : '0',
	);

	if ($form['board_title'] == '')
		message_backstage(__('You must enter a title.', 'luna'));

	// Make sure base_url doesn't end with a slash
	if (substr($form['base_url'], -1) == '/')
		$form['base_url'] = substr($form['base_url'], 0, -1);

	// Convert IDN to Punycode if needed
	if (preg_match('/[^\x00-\x7F]/', $form['base_url'])) {
		if (!function_exists('idn_to_ascii'))
			message_backstage(__('Your installation does not support automatic conversion of internationalized domain names. As your base URL contains special characters, you <strong>must</strong> use an online converter.', 'luna'));
		else
			$form['base_url'] = idn_to_ascii($form['base_url']);
	}

	$languages = forum_list_langs();
	if (!in_array($form['default_lang'], $languages))
		message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

	if ($form['time_format'] == '')
		$form['time_format'] = 'H:i:s';

	if ($form['date_format'] == '')
		$form['date_format'] = 'Y-m-d';

	require LUNA_ROOT.'include/email.php';

	if ($form['mailing_list'] != '')
		$form['mailing_list'] = strtolower(preg_replace('%\s%S', '', $form['mailing_list']));

	// Change or enter a SMTP password
	if (isset($_POST['form']['smtp_change_pass'])) {
		$smtp_pass1 = isset($_POST['form']['smtp_pass1']) ? luna_trim($_POST['form']['smtp_pass1']) : '';
		$smtp_pass2 = isset($_POST['form']['smtp_pass2']) ? luna_trim($_POST['form']['smtp_pass2']) : '';

		if ($smtp_pass1 == $smtp_pass2)
			$form['smtp_pass'] = $smtp_pass1;
		else
			message_backstage(__('You need to enter the SMTP password twice exactly the same to change it.', 'luna'));
	}

	if ($form['announcement_message'] != '')
		$form['announcement_message'] = luna_linebreaks($form['announcement_message']);
	else {
		$form['announcement_message'] = __('Enter your announcement here.', 'luna');
		$form['announcement'] = '0';
	}

	if ($form['rules_message'] != '')
		$form['rules_message'] = luna_linebreaks($form['rules_message']);
	else {
		$form['rules_message'] = __('Enter your rules here.', 'luna');
		$form['rules'] = '0';
	}

	if ($form['default_email_setting'] < 0 || $form['default_email_setting'] > 2)
		message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

	if ($form['feed_type'] < 0 || $form['feed_type'] > 2)
		message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

	if ($form['feed_ttl'] < 0)
		message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

	if ($form['report_method'] < 0 || $form['report_method'] > 2)
		message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

	if ($form['timeout_online'] >= $form['timeout_visit'])
		message_backstage(__('The value of "Timeout online" must be smaller than the value of "Timeout visit".', 'luna'));

	// Change or enter a SMTP password
	if (isset($_POST['form']['smtp_change_pass'])) {
		$smtp_pass1 = isset($_POST['form']['smtp_pass1']) ? luna_trim($_POST['form']['smtp_pass1']) : '';
		$smtp_pass2 = isset($_POST['form']['smtp_pass2']) ? luna_trim($_POST['form']['smtp_pass2']) : '';

		if ($smtp_pass1 == $smtp_pass2)
			$form['smtp_pass'] = $smtp_pass1;
		else
			message_backstage(__('You need to enter the SMTP password twice exactly the same to change it.', 'luna'));
	}

	foreach ($form as $key => $input) {
		// Only update values that have changed
		if (array_key_exists('o_'.$key, $luna_config) && $luna_config['o_'.$key] != $input) {
			if ($input != '' || is_int($input))
				$value = '\''.$db->escape($input).'\'';
			else
				$value = 'NULL';

			$db->query('UPDATE '.$db->prefix.'config SET conf_value='.$value.' WHERE conf_name=\'o_'.$db->escape($key).'\'') or error('Unable to update board config', __FILE__, __LINE__, $db->error());
		}
	}
    
    if (isset($_FILES['req_file']['error']) && $_FILES['req_file']['error'] != 4) {
        $uploaded_file = $_FILES['req_file'];

        // Make sure the upload went smooth
        if (isset($uploaded_file['error'])) {
            switch ($uploaded_file['error']) {
                case 1: // UPLOAD_ERR_INI_SIZE
                case 2: // UPLOAD_ERR_FORM_SIZE
                    message(__('The selected file was too large to upload. The server didn\'t allow the upload.', 'luna'));
                    break;

                case 3: // UPLOAD_ERR_PARTIAL, skip 4, we already did that
                    message(__('The selected file was only partially uploaded. Please try again.', 'luna'));
                    break;

                case 6: // UPLOAD_ERR_NO_TMP_DIR
                    message(__('PHP was unable to save the uploaded file to a temporary location.', 'luna'));
                    break;

                default:
                    // No error occured, but was something actually uploaded?
                    if ($uploaded_file['size'] == 0)
                        message(__('You did not select a file for upload.', 'luna'));
                    break;
            }
        }

        if (is_uploaded_file($uploaded_file['tmp_name'])) {
            // Preliminary file check, adequate in most cases
            $allowed_types = array('image/png', 'image/x-png');
            if (!in_array($uploaded_file['type'], $allowed_types))
                message(__('The file you tried to upload is not of an allowed type. Only png is allowed.', 'luna'));

            // Move the file to the avatar directory. We do this before checking the width/height to circumvent open_basedir restrictions
            if (!@move_uploaded_file($uploaded_file['tmp_name'], LUNA_ROOT.'/favicon.tmp'))
                message(__('The server was unable to save the uploaded file. Please contact the forum administrator at', 'luna').' <a href="mailto:'.luna_htmlspecialchars($luna_config['o_admin_email']).'">'.luna_htmlspecialchars($luna_config['o_admin_email']).'</a>.');

            list($width, $height, $type,) = @getimagesize(LUNA_ROOT.'/favicon.tmp');
    
            // Clean up existing headers
            @unlink(LUNA_ROOT.'/favicon.png');
            
            // Do the final rename
            @rename(LUNA_ROOT.'/favicon.tmp', LUNA_ROOT.'/favicon.png');
            @chmod(LUNA_ROOT.'/favicon.png', 0644);
        } else
            message(__('An unknown error occurred. Please try again.', 'luna'));
    }

	// Regenerate the config cache
	if (!defined('LUNA_CACHE_FUNCTIONS_LOADED'))
		require LUNA_ROOT.'include/cache.php';

	generate_config_cache();
	clear_feed_cache();

	redirect('backstage/settings.php?saved=true');
}

$timestamp = time();

require 'header.php';
?>
<div class="row">
	<div class="col-sm-12">
<?php
if (isset($_GET['saved']))
	echo '<div class="alert alert-success"><i class="fa fa-fw fa-check"></i> '.__('Your settings have been saved.', 'luna').'</div>';
?>
        <form class="form-horizontal" method="post" enctype="multipart/form-data" action="settings.php">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('Branding', 'luna') ?><span class="pull-right"><button class="btn btn-primary" type="submit" name="save"><span class="fa fa-fw fa-check"></span> <?php _e('Save', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <input type="hidden" name="form_sent" value="1" />
                    <fieldset>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Title', 'luna') ?></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="form[board_title]" maxlength="255" value="<?php echo luna_htmlspecialchars($luna_config['o_board_title']) ?>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Slogan', 'luna') ?></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="form[board_slogan]" maxlength="255" value="<?php echo luna_htmlspecialchars($luna_config['o_board_slogan']) ?>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Description', 'luna') ?></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="form[board_description]" maxlength="255" value="<?php echo luna_htmlspecialchars($luna_config['o_board_description']) ?>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Default language', 'luna') ?></label>
                            <div class="col-sm-9">
                                <select class="form-control" name="form[default_lang]">
<?php

		$languages = forum_list_langs();

		foreach ($languages as $temp) {
			if ($luna_config['o_default_lang'] == $temp)
				echo "\t\t\t\t\t\t\t\t\t\t\t".'<option value="'.$temp.'" selected>'.$temp.'</option>'."\n";
			else
				echo "\t\t\t\t\t\t\t\t\t\t\t".'<option value="'.$temp.'">'.$temp.'</option>'."\n";
		}

?>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Tags', 'luna') ?><span class="help-block"><?php _e('Add some words that describe your board, separated by a comma', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="form[board_tags]" maxlength="255" value="<?php echo luna_htmlspecialchars($luna_config['o_board_tags']) ?>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label">
                                <?php _e('Favicon', 'luna') ?><span class="help-block"><?php _e('You can upload a favicon here to show in the browser', 'luna') ?></span>
                                <?php if (file_exists(LUNA_ROOT.'/favicon.png')) { ?>
                                    <a class="btn btn-danger" href="?remove-favicon"><span class="fa fa-fw fa-trash"></span> <?php _e('Delete favicon', 'luna') ?></a>
                                <?php } ?>
                            </label>
                            <div class="col-sm-9">
                                <?php if (file_exists(LUNA_ROOT.'/favicon.png')) { ?>
                                    <img class="img-responsive img-bs-favicon" src="<?php echo LUNA_ROOT.'favicon.png' ?>" alt="<?php _e('Favicon', 'luna') ?>" />
                                <?php } else { ?>
                                    <img class="img-responsive img-bs-favicon" src="<?php echo LUNA_ROOT.'img/favicon.png' ?>" alt="<?php _e('Default favicon', 'luna') ?>" />
                                <?php } ?>
                                <input type="hidden" name="MAX_FILE_SIZE" value="51200" />
                                <input name="req_file" type="file" />
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('Announcement', 'luna') ?><span class="pull-right"><button class="btn btn-primary" type="submit" name="save"><span class="fa fa-fw fa-check"></span> <?php _e('Save', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <fieldset>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Announcement', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[announcement]" value="1" <?php if ($luna_config['o_announcement'] == '1') echo ' checked' ?> />
                                        <?php _e('Enable this to display the below message in the board.', 'luna') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <hr />
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Title', 'luna') ?><span class="help-block"><?php _e('You can leave this empty if there is no title', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="form[announcement_title]" value="<?php echo $luna_config['o_announcement_title'] ?>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Type', 'luna') ?></label>
                            <div class="col-sm-9">
                                <label class="radio-inline">
                                    <input type="radio" name="form[announcement_type]" value="default"<?php if ($luna_config['o_announcement_type'] == 'default') echo ' checked' ?>>
                                    <?php _e('Default', 'luna') ?>
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" name="form[announcement_type]" value="info"<?php if ($luna_config['o_announcement_type'] == 'info') echo ' checked' ?>>
                                    <?php _e('Info', 'luna') ?>
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" name="form[announcement_type]" value="success"<?php if ($luna_config['o_announcement_type'] == 'success') echo ' checked' ?>>
                                    <?php _e('Success', 'luna') ?>
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" name="form[announcement_type]" value="warning"<?php if ($luna_config['o_announcement_type'] == 'warning') echo ' checked' ?>>
                                    <?php _e('Warning', 'luna') ?>
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" name="form[announcement_type]" value="danger"<?php if ($luna_config['o_announcement_type'] == 'danger') echo ' checked' ?>>
                                    <?php _e('Danger', 'luna') ?>
                                </label>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Message', 'luna') ?></label>
                            <div class="col-sm-9">
                                <textarea class="form-control" name="form[announcement_message]" rows="5"><?php echo luna_htmlspecialchars($luna_config['o_announcement_message']) ?></textarea>
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('Time and timeouts', 'luna') ?><span class="pull-right"><button class="btn btn-primary" type="submit" name="save"><span class="fa fa-fw fa-check"></span> <?php _e('Save', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <fieldset>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Time format', 'luna') ?><span class="help-block"><?php printf(__('Now: %s. See %s for more info', 'luna'), date($luna_config['o_time_format'], $timestamp), '<a href="http://www.php.net/manual/en/function.date.php">'.__('PHP manual', 'luna').'</a>') ?></span></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="form[time_format]" maxlength="25" value="<?php echo luna_htmlspecialchars($luna_config['o_time_format']) ?>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Date format', 'luna') ?><span class="help-block"><?php printf(__('Now: %s. See %s for more info', 'luna'), date($luna_config['o_date_format'], $timestamp), '<a href="http://www.php.net/manual/en/function.date.php">'.__('PHP manual', 'luna').'</a>') ?></span></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="form[date_format]" maxlength="25" value="<?php echo luna_htmlspecialchars($luna_config['o_date_format']) ?>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Default time zone', 'luna') ?></label>
                            <div class="col-sm-9">
                                <select class="form-control" name="form[timezone]">
<?php
$timezones = DateTimeZone::listIdentifiers();
foreach ($timezones as $timezone) {
?>
							     	<option value="<?php echo $timezone ?>"<?php if ($luna_config['o_timezone'] == $timezone) echo ' selected' ?>><?php echo $timezone ?></option>
<?php
}
?>
                                </select>
                            </div>
                        </div>
                        <hr />
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Visit timeout', 'luna') ?><span class="help-block"><?php _e('Time before a visit ends', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <div class="input-group">
                                    <input type="number" class="form-control" name="form[timeout_visit]" maxlength="5" value="<?php echo $luna_config['o_timeout_visit'] ?>" />
                                    <span class="input-group-addon"><?php _e('seconds', 'luna') ?></span>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Online timeout', 'luna') ?><span class="help-block"><?php _e('Time before someone isn\'t online anymore', 'luna') ?></span>
        </label>
                            <div class="col-sm-9">
                                <div class="input-group">
                                    <input type="number" class="form-control" name="form[timeout_online]" maxlength="5" value="<?php echo $luna_config['o_timeout_online'] ?>" />
                                    <span class="input-group-addon"><?php _e('seconds', 'luna') ?></span>
                                </div>
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('Syndication', 'luna') ?><span class="pull-right"><button class="btn btn-primary" type="submit" name="save"><span class="fa fa-fw fa-check"></span> <?php _e('Save', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <fieldset>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Default feed type', 'luna') ?></label>
                            <div class="col-sm-9">
                                <label class="radio-inline">
                                    <input type="radio" name="form[feed_type]" value="0"<?php if ($luna_config['o_feed_type'] == '0') echo ' checked' ?>>
                                    <?php _e('None', 'luna') ?>
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" name="form[feed_type]" value="1"<?php if ($luna_config['o_feed_type'] == '1') echo ' checked' ?>>
                                    <?php _e('RSS', 'luna') ?>
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" name="form[feed_type]" value="2"<?php if ($luna_config['o_feed_type'] == '2') echo ' checked' ?>>
                                    <?php _e('Atom', 'luna') ?>
                                </label>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Duration to cache feeds', 'luna') ?><span class="help-block"><?php _e('Reduce sources by caching feeds', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <select class="form-control" name="form[feed_ttl]">
                                    <option value="0"<?php if ($luna_config['o_feed_ttl'] == '0') echo ' selected'; ?>><?php _e('Don\'t cache', 'luna') ?></option>
<?php

		$times = array(5, 15, 30, 60);

		foreach ($times as $time)
			echo "\t\t\t\t\t\t\t\t\t\t\t".'<option value="'.$time.'"'.($luna_config['o_feed_ttl'] == $time ? ' selected' : '').'>'.sprintf(__('%d minutes', 'luna'), $time).'</option>'."\n";

?>
                                </select>
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('E-mail', 'luna') ?><span class="pull-right"><button class="btn btn-primary" type="submit" name="save"><span class="fa fa-fw fa-check"></span> <?php _e('Save', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <fieldset>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Admin email', 'luna') ?></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="form[admin_email]" maxlength="80" value="<?php echo luna_htmlspecialchars($luna_config['o_admin_email']) ?>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Webmaster email', 'luna') ?><span class="help-block"><?php _e('The email where the boards mails will be addressed from', 'luna') ?></span></label>
                                <div class="col-sm-9"><input type="text" class="form-control" name="form[webmaster_email]" maxlength="80" value="<?php echo luna_htmlspecialchars($luna_config['o_webmaster_email']) ?>" />
                            </div>
                        </div>
                        <hr />
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Reporting method', 'luna') ?><span class="help-block"><?php _e('How should we handle reports?', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <label class="radio-inline">
                                    <input type="radio" name="form[report_method]" value="0"<?php if ($luna_config['o_report_method'] == '0') echo ' checked' ?> />
                                    <?php _e('Internal', 'luna') ?>
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" name="form[report_method]" value="1"<?php if ($luna_config['o_report_method'] == '1') echo ' checked' ?> />
                                    <?php _e('Email', 'luna') ?>
                                </label>
                                <label class="radio-inline">
                                    <input type="radio" name="form[report_method]" value="2"<?php if ($luna_config['o_report_method'] == '2') echo ' checked' ?> />
                                    <?php _e('Both', 'luna') ?>
                                </label>
                            </div>
                        </div>
                        <hr />
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Mailing list', 'luna') ?><span class="help-block"><?php _e('A comma separated list of subscribers who get e-mails when new reports are made', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <textarea class="form-control" name="form[mailing_list]" rows="5"><?php echo luna_htmlspecialchars($luna_config['o_mailing_list']) ?></textarea>
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('Registration', 'luna') ?><span class="pull-right"><button class="btn btn-primary" type="submit" name="save"><span class="fa fa-fw fa-check"></span> <?php _e('Save', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <fieldset>
                    <input type="hidden" name="form_sent" value="1" />
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('New registrations', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[regs_allow]" value="1" <?php if ($luna_config['o_regs_allow'] == '1') echo ' checked' ?> />
                                        <?php _e('Allow new users to be made by people.', 'luna') ?>
                                    </label>
                                </div>
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[regs_report]" value="1" <?php if ($luna_config['o_regs_report'] == '1') echo ' checked' ?> />
                                        <?php _e('Notify people on the mailing list when new user registers.  ', 'luna') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Verify', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[regs_verify]" value="1" <?php if ($luna_config['o_regs_verify'] == '1') echo ' checked' ?> />
                                        <?php _e('Send a random password to users to verify their email address.  ', 'luna') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Registration', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[allow_banned_email]" value="1" <?php if ($luna_config['o_allow_banned_email'] == '1') echo ' checked' ?> />
                                        <?php _e('Allow users to use a banned email address, mailing list will be warned when this happens.', 'luna') ?>
                                    </label>
                                </div>
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[allow_dupe_email]" value="1" <?php if ($luna_config['o_allow_dupe_email'] == '1') echo ' checked' ?> />
                                        <?php _e('Allow users to use an email address that is already used, mailing list will be warned when this happens.', 'luna') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <hr />
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Rules', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[rules]" value="1" <?php if ($luna_config['o_rules'] == '1') echo ' checked' ?> />
                                        <?php _e('Require users to agree with the rules. This will enable a "Rules" panel in Help.', 'luna') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Message', 'luna') ?><span class="help-block"><?php _e('Enter rules or useful information, required when rules are enabled, in HTML', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <textarea class="form-control" name="form[rules_message]" rows="10"><?php echo luna_htmlspecialchars($luna_config['o_rules_message']) ?></textarea>
                            </div>
                        </div>
                        <hr />
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Default email setting', 'luna') ?><span class="help-block"><?php _e('Default privacy setting for new registrations', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <div class="radio">
                                    <label>
                                        <input type="radio" name="form[default_email_setting]" id="form_default_email_setting_0" value="0"<?php if ($luna_config['o_default_email_setting'] == '0') echo ' checked' ?> />
                                        <?php _e('Display email address to other users.', 'luna') ?>
                                    </label>
                                </div>
                                <div class="radio">
                                    <label>
                                        <input type="radio" name="form[default_email_setting]" id="form_default_email_setting_1" value="1"<?php if ($luna_config['o_default_email_setting'] == '1') echo ' checked' ?> />
                                        <?php _e('Hide email address but allow form e-mail.', 'luna') ?>
                                    </label>
                                </div>
                                <div class="radio">
                                    <label>
                                        <input type="radio" name="form[default_email_setting]" id="form_default_email_setting_2" value="2"<?php if ($luna_config['o_default_email_setting'] == '2') echo ' checked' ?> />
                                        <?php _e('Hide email address and disallow form email.', 'luna') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('Cookie bar', 'luna') ?><span class="pull-right"><button class="btn btn-primary" type="submit" name="save"><span class="fa fa-fw fa-check"></span> <?php _e('Save', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <fieldset>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Cookie bar', 'luna') ?><span class="help-block"><a href="http://getluna.org/docs/cookies.php"><?php _e('More info', 'luna') ?></a></span></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[cookie_bar]" value="1" <?php if ($luna_config['o_cookie_bar'] == '1') echo ' checked' ?> />
                                        <?php _e('Show a bar with information about cookies at the bottom of the page.', 'luna') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Cookie info URL', 'luna') ?><span class="help-block"><?php _e('Use your own URL for cookie information, by default, we provide our own page', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="form[cookie_bar_url]" maxlength="255" value="<?php echo luna_htmlspecialchars($luna_config['o_cookie_bar_url']) ?>" />
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('SMTP settings', 'luna') ?><span class="pull-right"><button class="btn btn-primary" type="submit" name="save"><span class="fa fa-fw fa-check"></span> <?php _e('Save', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <input type="hidden" name="form_sent" value="1" />
                    <fieldset>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('SMTP server address', 'luna') ?><span class="help-block"><?php _e('The address of an external SMTP server to send emails with', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="form[smtp_host]" maxlength="100" value="<?php echo luna_htmlspecialchars($luna_config['o_smtp_host']) ?>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('SMTP username', 'luna') ?><span class="help-block"><?php _e('Username for SMTP server, only if required', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="form[smtp_user]" maxlength="50" value="<?php echo luna_htmlspecialchars($luna_config['o_smtp_user']) ?>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('SMTP password', 'luna') ?><span class="help-block"><?php _e('Password and confirmation for SMTP server, only when required', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[smtp_change_pass]" id="form_smtp_change_pass" value="1" />
                                        <?php _e('Check this if you want to change or delete the currently stored password.', 'luna') ?>
                                    </label>
                                </div>
        <?php $smtp_pass = !empty($luna_config['o_smtp_pass']) ? random_key(luna_strlen($luna_config['o_smtp_pass']), true) : ''; ?>
                                <div class="row">
                                    <div class="col-sm-6">
                                        <input class="form-control" type="password" name="form[smtp_pass1]" maxlength="50" value="<?php echo $smtp_pass ?>" />
                                    </div>
                                    <div class="col-sm-6">
                                        <input class="form-control" type="password" name="form[smtp_pass2]" maxlength="50" value="<?php echo $smtp_pass ?>" />
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('SMTP encryption', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[smtp_ssl]" value="1" <?php if ($luna_config['o_smtp_ssl'] == '1') echo ' checked' ?> />
                                        <?php _e('Encrypts the connection to the SMTP server using SSL, only when required and supported.', 'luna') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('System', 'luna') ?><span class="pull-right"><button class="btn btn-primary" type="submit" name="save"><span class="fa fa-fw fa-check"></span> <?php _e('Save', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <input type="hidden" name="form_sent" value="1" />
                    <fieldset>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Root URL', 'luna') ?></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="form[base_url]" maxlength="100" value="<?php echo luna_htmlspecialchars($luna_config['o_base_url']) ?>" />
                            </div>
                        </div>
                        <hr />
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Advanced', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[gzip]" value="1" <?php if ($luna_config['o_gzip'] == '1') echo ' checked' ?> />
                                        <?php _e('Gzip output sent to the browser. This will reduce bandwidth usage, but use some more CPU. This feature requires that PHP is configured with zlib. If you already have one of the Apache modules (mod_gzip/mod_deflate) set up to compress PHP scripts, disable this feature.', 'luna') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>
        </form>
    </div>
</div>
<?php

require 'footer.php';
