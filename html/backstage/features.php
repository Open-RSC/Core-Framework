<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'settings');
define('LUNA_PAGE', 'features');

require LUNA_ROOT.'include/common.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}

if (isset($_GET['remove-avatar'])) {
	confirm_referrer('backstage/appearance.php', __('Bad HTTP_REFERER. If you have moved these forums from one location to another or switched domains, you need to update the Base URL manually in the database (look for o_base_url in the config table) and then clear the cache by deleting all .php files in the /cache directory.', 'luna'));

    @unlink(LUNA_ROOT.$luna_config['o_avatars_dir'].'/cplaceholder.png');

	redirect('backstage/features.php?saved=true');
}

if (isset($_POST['form_sent'])) {
	confirm_referrer('backstage/features.php', __('Bad HTTP_REFERER. If you have moved these forums from one location to another or switched domains, you need to update the Base URL manually in the database (look for o_base_url in the config table) and then clear the cache by deleting all .php files in the /cache directory.', 'luna'));

	$form = array(
		'users_online'					=> isset($_POST['form']['users_online']) ? '1' : '0',
		'censoring'						=> isset($_POST['form']['censoring']) ? '1' : '0',
		'signatures'					=> isset($_POST['form']['signatures']) ? '1' : '0',
		'ranks'							=> isset($_POST['form']['ranks']) ? '1' : '0',
		'thread_views'					=> isset($_POST['form']['thread_views']) ? '1' : '0',
		'has_commented'					=> isset($_POST['form']['has_commented']) ? '1' : '0',
		'smilies_sig'					=> isset($_POST['form']['smilies_sig']) ? '1' : '0',
		'make_links'					=> isset($_POST['form']['make_links']) ? '1' : '0',
		'allow_advanced_editor'			=> isset($_POST['form']['allow_advanced_editor']) ? '1' : '0',
		'allow_dialog_editor'		    => isset($_POST['form']['allow_dialog_editor']) ? '1' : '0',
		'allow_center'					=> isset($_POST['form']['allow_center']) ? '1' : '0',
		'allow_size'					=> isset($_POST['form']['allow_size']) ? '1' : '0',
		'allow_spoiler'					=> isset($_POST['form']['allow_spoiler']) ? '1' : '0',
		'indent_num_spaces'				=> (intval($_POST['form']['indent_num_spaces']) >= 0) ? intval($_POST['form']['indent_num_spaces']) : 0,
		'quote_depth'					=> (intval($_POST['form']['quote_depth']) > 0) ? intval($_POST['form']['quote_depth']) : 1,
		'search_all_forums'				=> isset($_POST['form']['search_all_forums']) ? '1' : '0',
		'enable_advanced_search'		=> isset($_POST['form']['enable_advanced_search']) ? '1' : '0',
		'enable_inbox'					=> isset($_POST['form']['enable_inbox']) ? '1' : '0',
		'inbox_notification'			=> isset($_POST['form']['inbox_notification']) ? '1' : '0',
		'max_receivers'					=> (intval($_POST['form']['max_receivers']) > 0) ? intval($_POST['form']['max_receivers']) : 5,
		'emoji'					=> isset($_POST['form']['emoji']) ? '1' : '0',
		'emoji_size'			=> intval($_POST['form']['emoji_size']),
		'forum_subscriptions'	        => isset($_POST['form']['forum_subscriptions']) ? '1' : '0',
		'thread_subscriptions'          => isset($_POST['form']['thread_subscriptions']) ? '1' : '0',
		'message_img_tag'		=> isset($_POST['form']['message_img_tag']) ? '1' : '0',
		'message_all_caps'		=> isset($_POST['form']['message_all_caps']) ? '1' : '0',
		'subject_all_caps'		=> isset($_POST['form']['subject_all_caps']) ? '1' : '0',
		'force_guest_email'		=> isset($_POST['form']['force_guest_email']) ? '1' : '0',
		'sig_img_tag'			=> isset($_POST['form']['sig_img_tag']) ? '1' : '0',
		'sig_all_caps'			=> isset($_POST['form']['sig_all_caps']) ? '1' : '0',
		'sig_length'			=> luna_trim($_POST['form']['sig_length']),
		'sig_lines'				=> luna_trim($_POST['form']['sig_lines']),
		'avatars'				=> isset($_POST['form']['avatars']) ? '1' : '0',
		'avatars_dir'			=> luna_trim($_POST['form']['avatars_dir']),
		'avatars_width'			=> (intval($_POST['form']['avatars_width']) > 0) ? intval($_POST['form']['avatars_width']) : 1,
		'avatars_height'		=> (intval($_POST['form']['avatars_height']) > 0) ? intval($_POST['form']['avatars_height']) : 1,
		'avatars_size'			=> (intval($_POST['form']['avatars_size']) > 0) ? intval($_POST['form']['avatars_size']) : 1,
	);

	// Make sure avatars_dir doesn't end with a slash
	if (substr($form['avatars_dir'], -1) == '/')
		$form['avatars_dir'] = substr($form['avatars_dir'], 0, -1);

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
            if (!@move_uploaded_file($uploaded_file['tmp_name'], LUNA_ROOT.$luna_config['o_avatars_dir'].'/cplaceholder.tmp'))
                message(__('The server was unable to save the uploaded file. Please contact the forum administrator at', 'luna').' <a href="mailto:'.luna_htmlspecialchars($luna_config['o_admin_email']).'">'.luna_htmlspecialchars($luna_config['o_admin_email']).'</a>.');

            list($width, $height, $type,) = @getimagesize(LUNA_ROOT.$luna_config['o_avatars_dir'].'/cplaceholder.tmp');
    
            // Clean up existing headers
            @unlink(LUNA_ROOT.$luna_config['o_avatars_dir'].'/cplaceholder.png');
            
            // Do the final rename
            @rename(LUNA_ROOT.$luna_config['o_avatars_dir'].'/cplaceholder.tmp', LUNA_ROOT.$luna_config['o_avatars_dir'].'/cplaceholder.png');
            @chmod(LUNA_ROOT.$luna_config['o_avatars_dir'].'/cplaceholder.png', 0644);
        } else
            message(__('An unknown error occurred. Please try again.', 'luna'));
    }

	// Regenerate the config cache
	if (!defined('LUNA_CACHE_FUNCTIONS_LOADED'))
		require LUNA_ROOT.'include/cache.php';

	generate_config_cache();
	clear_feed_cache();

	redirect('backstage/features.php?saved=true');
}

require 'header.php';
?>
<div class="row">
	<div class="col-sm-12">
<?php
if (isset($_GET['saved']))
	echo '<div class="alert alert-success"><i class="fa fa-fw fa-check"></i> '.__('Your settings have been saved.', 'luna').'</div>';
?>
        <form class="form-horizontal" method="post" action="features.php">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('General', 'luna') ?><span class="pull-right"><button class="btn btn-primary" type="submit" name="save"><span class="fa fa-fw fa-check"></span> <?php _e('Save', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <input type="hidden" name="form_sent" value="1" />
                    <fieldset>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Threads and comments', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[censoring]" value="1" <?php if ($luna_config['o_censoring'] == '1') echo ' checked' ?> />
                                        <?php _e('Censor words in comments.', 'luna') ?>
                                    </label>
                                </div>
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[thread_views]" value="1" <?php if ($luna_config['o_thread_views'] == '1') echo ' checked' ?> />
                                        <?php _e('Show the number of views for each thread.', 'luna') ?>
                                    </label>
                                </div>
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[has_commented]" value="1" <?php if ($luna_config['o_has_commented'] == '1') echo ' checked' ?> />
                                        <?php _e('Show a label in front of the thread where users have commented.', 'luna') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('User features', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[users_online]" value="1" <?php if ($luna_config['o_users_online'] == '1') echo ' checked' ?> />
                                        <?php _e('Display info on the index page about users currently browsing the board.', 'luna') ?>
                                    </label>
                                </div>
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[ranks]" value="1" <?php if ($luna_config['o_ranks'] == '1') echo ' checked' ?> />
                                        <?php _e('Use user ranks.', 'luna') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Search', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[enable_advanced_search]" value="1" <?php if ($luna_config['o_enable_advanced_search'] == '1') echo ' checked' ?> />
                                        <?php _e('Allow users to use the advanced search options.', 'luna') ?>
                                    </label>
                                </div>
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[search_all_forums]" value="1" <?php if ($luna_config['o_search_all_forums'] == '1') echo ' checked' ?> />
                                        <?php _e('Allow search only in 1 forum at a time.', 'luna') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('Inbox', 'luna') ?><span class="pull-right"><button class="btn btn-primary" type="submit" name="save"><span class="fa fa-fw fa-check"></span> <?php _e('Save', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <input type="hidden" name="form_sent" value="1" />
                    <fieldset>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Inbox', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[enable_inbox]" value="1" <?php if ($luna_config['o_enable_inbox'] == '1') echo ' checked' ?> />
                                        <?php _e('Allow users to use Inbox.', 'luna') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <hr />
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Receivers', 'luna') ?><span class="help-block"><?php _e('The number of receivers an Inbox message can have', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" name="form[max_receivers]" maxlength="5" value="<?php echo $luna_config['o_max_receivers'] ?>" />
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('Subscriptions', 'luna') ?><span class="pull-right"><button class="btn btn-primary" type="submit" name="save"><span class="fa fa-fw fa-check"></span> <?php _e('Save', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <input type="hidden" name="form_sent" value="1" />
                    <fieldset>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Subscriptions', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[forum_subscriptions]" value="1" <?php if ($luna_config['o_forum_subscriptions'] == '1') echo ' checked' ?> />
                                        <?php _e('Enable users to subscribe to forums.', 'luna') ?>
                                    </label>
                                </div>
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[thread_subscriptions]" value="1" <?php if ($luna_config['o_thread_subscriptions'] == '1') echo ' checked' ?> />
                                        <?php _e('Enable users to subscribe to threads.', 'luna') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('BBCode', 'luna') ?><span class="pull-right"><button class="btn btn-primary" type="submit" name="save"><span class="fa fa-fw fa-check"></span> <?php _e('Save', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <fieldset>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Universal', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[allow_center]" value="1" <?php if ($luna_config['o_allow_center'] == '1') echo ' checked' ?> />
                                        <?php _e('Allow the use of the [center]-tag.', 'luna') ?>
                                    </label>
                                </div>
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[allow_size]" value="1" <?php if ($luna_config['o_allow_size'] == '1') echo ' checked' ?> />
                                        <?php _e('Allow the use of the [size]-tag.', 'luna') ?>
                                    </label>
                                </div>
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[make_links]" value="1" <?php if ($luna_config['o_make_links'] == '1') echo ' checked' ?> />
                                        <?php _e('Convert URLs automatically to clickable hyperlinks.', 'luna') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Threads and comments', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[message_img_tag]" value="1" <?php if ($luna_config['o_message_img_tag'] == '1') echo ' checked' ?> />
                                        <?php _e('Allow the use of the [img]-tag.', 'luna') ?>
                                    </label>
                                </div>
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[allow_spoiler]" value="1" <?php if ($luna_config['o_allow_spoiler'] == '1') echo ' checked' ?> />
                                        <?php _e('Allow the use of the [spoiler]-tag.', 'luna') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Signatures', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[smilies_sig]" value="1" <?php if ($luna_config['o_smilies_sig'] == '1') echo ' checked' ?> />
                                        <?php _e('Convert smilies to small graphic icons in user signatures.', 'luna') ?>
                                    </label>
                                </div>
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[sig_img_tag]" value="1" <?php if ($luna_config['o_sig_img_tag'] == '1') echo ' checked' ?> />
                                        <?php _e('Allow the use of the [img]-tag.', 'luna') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <hr />
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Emoji', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[emoji]" value="1" <?php if ($luna_config['o_emoji'] == '1') echo ' checked' ?> />
                                        <?php _e('Use emojis instead of emoticons.', 'luna') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Smilie size', 'luna') ?><span class="help-block"><?php _e('The size emoticons and emojis are shown in', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <div class="input-group">
                                    <input type="number" class="form-control" name="form[emoji_size]" maxlength="2" value="<?php echo $luna_config['o_emoji_size'] ?>" />
                                    <span class="input-group-addon"><?php _e('pixels', 'luna') ?></span>
                                </div>
                            </div>
                        </div>
                        <hr />
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Indent size', 'luna') ?><span class="help-block"><?php _e('Amount of spaces that represent a tab', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" name="form[indent_num_spaces]" maxlength="3" value="<?php echo $luna_config['o_indent_num_spaces'] ?>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Maximum [quote] depth', 'luna') ?><span class="help-block"><?php _e('Maximum [quote] can be used in [quote]', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" name="form[quote_depth]" maxlength="3" value="<?php echo $luna_config['o_quote_depth'] ?>" />
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('Commenting', 'luna') ?><span class="pull-right"><button class="btn btn-primary" type="submit" name="save"><span class="fa fa-fw fa-check"></span> <?php _e('Save', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <input type="hidden" name="form_sent" value="1" />
                    <fieldset>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('All caps', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[message_all_caps]" value="1" <?php if ($luna_config['o_message_all_caps'] == '1') echo ' checked' ?> />
                                        <?php _e('Allow a comment to contain only capital letters.', 'luna') ?>
                                    </label>
                                </div>
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[subject_all_caps]" value="1" <?php if ($luna_config['o_subject_all_caps'] == '1') echo ' checked' ?> />
                                        <?php _e('Allow a subject to contain only capital letters.', 'luna') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Guests', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[force_guest_email]" value="1" <?php if ($luna_config['o_force_guest_email'] == '1') echo ' checked' ?> />
                                        <?php _e('Require guests to supply an email address when commenting.', 'luna') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('Avatars', 'luna') ?><span class="pull-right"><button class="btn btn-primary" type="submit" name="save"><span class="fa fa-fw fa-check"></span> <?php _e('Save', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <fieldset>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Use avatars', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[avatars]" value="1" <?php if ($luna_config['o_avatars'] == '1') echo ' checked' ?> />
                                        <?php _e('Enable so users can upload avatars.', 'luna') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Upload directory', 'luna') ?><span class="help-block"><?php _e('Where avatars will be stored relative to Lunas root, write permission required', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <input type="text" class="form-control" name="form[avatars_dir]" maxlength="50" value="<?php echo luna_htmlspecialchars($luna_config['o_avatars_dir']) ?>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Max width', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="input-group">
                                    <input type="number" class="form-control" name="form[avatars_width]" maxlength="5" value="<?php echo $luna_config['o_avatars_width'] ?>" />
                                    <span class="input-group-addon"><?php _e('pixels', 'luna') ?></span>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Max height', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="input-group">
                                    <input type="number" class="form-control" name="form[avatars_height]" maxlength="5" value="<?php echo $luna_config['o_avatars_height'] ?>" />
                                    <span class="input-group-addon"><?php _e('pixels', 'luna') ?></span>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Max size', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="input-group">
                                    <input type="number" class="form-control" name="form[avatars_size]" maxlength="6" value="<?php echo $luna_config['o_avatars_size'] ?>" />
                                    <span class="input-group-addon"><?php _e('bytes', 'luna') ?></span>
                                </div>
                            </div>
                        </div>
                        <hr />
                        <div class="form-group">
                            <label class="col-sm-3 control-label">
                                <?php _e('Default avatar', 'luna') ?><span class="help-block"><?php _e('You can upload a custom default avatar for all users', 'luna') ?></span>
                                <?php if (file_exists(LUNA_ROOT.$luna_config['o_avatars_dir'].'/cplaceholder.png')) { ?>
                                    <a class="btn btn-danger" href="?remove-avatar"><span class="fa fa-fw fa-trash"></span> <?php _e('Delete avatar', 'luna') ?></a>
                                <?php } ?>
                            </label>
                            <div class="col-sm-9">
                                <?php if (file_exists(LUNA_ROOT.$luna_config['o_avatars_dir'].'/cplaceholder.png')) { ?>
                                    <img class="img-responsive img-bs-avatar" src="<?php echo LUNA_ROOT.$luna_config['o_avatars_dir'].'/cplaceholder.png' ?>" alt="<?php _e('Default placeholder avatar', 'luna') ?>" />
                                <?php } else { ?>
                                    <img class="img-responsive img-bs-avatar" src="<?php echo LUNA_ROOT.'img/avatars/placeholder.png' ?>" alt="<?php _e('Default placeholder avatar', 'luna') ?>" />
                                <?php } ?>
                                <input type="hidden" name="MAX_FILE_SIZE" value="512000" />
                                <input name="req_file" type="file" />
                            </div>
                        </div>
                    </fieldset>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><?php _e('Signatures', 'luna') ?><span class="pull-right"><button class="btn btn-primary" type="submit" name="save"><span class="fa fa-fw fa-check"></span> <?php _e('Save', 'luna') ?></button></span></h3>
                </div>
                <div class="panel-body">
                    <fieldset>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Signatures', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[signatures]" value="1" <?php if ($luna_config['o_signatures'] == '1') echo ' checked' ?> />
                                        <?php printf(__('Allow users to attach a signature to their comments.', 'luna'), '<a href="permissions.php">'.__('Permissions', 'luna').'</a>') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <hr />
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('All caps', 'luna') ?></label>
                            <div class="col-sm-9">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" name="form[sig_all_caps]" value="1" <?php if ($luna_config['o_sig_all_caps'] == '1') echo ' checked' ?> />
                                        <?php _e('Allow a signature to contain only capital letters.', 'luna') ?>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Maximum signature length', 'luna') ?><span class="help-block"><?php _e('Maximum amount of characters a signature can have', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" name="form[sig_length]" maxlength="5" value="<?php echo $luna_config['o_sig_length'] ?>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="col-sm-3 control-label"><?php _e('Maximum signature lines', 'luna') ?><span class="help-block"><?php _e('Maximum amount of lines a signature can have', 'luna') ?></span></label>
                            <div class="col-sm-9">
                                <input type="number" class="form-control" name="form[sig_lines]" maxlength="3" value="<?php echo $luna_config['o_sig_lines'] ?>" />
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