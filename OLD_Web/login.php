<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

// Tell header.php to use the form template
define('LUNA_FORM', 1);

if (isset($_GET['action']))
	define('LUNA_QUIET_VISIT', 1);

define('LUNA_ROOT', dirname(__FILE__).'/');
require LUNA_ROOT.'include/common.php';

$action = isset($_GET['action']) ? $_GET['action'] : null;

if (isset($_POST['form_sent']) && $action == 'in') {
	$form_username = luna_trim($_POST['req_username']);
	$form_password = luna_trim($_POST['req_password']);
	$save_pass = isset($_POST['save_pass']);

	$username_sql = ($db_type == 'mysql' || $db_type == 'mysqli' || $db_type == 'mysql_innodb' || $db_type == 'mysqli_innodb') ? 'username=\''.$db->escape($form_username).'\'' : 'LOWER(username)=LOWER(\''.$db->escape($form_username).'\')';

	$result = $db->query('SELECT * FROM '.$db->prefix.'users WHERE '.$username_sql) or error('Unable to fetch user info', __FILE__, __LINE__, $db->error());
	$cur_user = $db->fetch_assoc($result);

	$authorized = false;

	if (!empty($cur_user['password']))
	{
        if (!isset($cur_user['salt']))
            $salt = random_pass(8);
        else
            $salt = $cur_user['salt'];
            
		$form_password_hash = luna_sha512($form_password, $salt); // Will result in a SHA-512 hash

		// If there isn't a salt, we're using a Luna 1.3-account
		if (strlen($cur_user['password']) == 40)
		{
			$is_hash_authorized = (luna_hash($form_password) == $cur_user['password']);
			if ($is_hash_authorized)
			{
				$authorized = true;

				$db->query('UPDATE '.$db->prefix.'users SET password=\''.$form_password_hash.'\', salt=\''.$salt.'\' WHERE id='.$cur_user['id']) or error('Unable to update user password', __FILE__, __LINE__, $db->error());
			}
		}
		// Otherwise we should have a normal sha512 password
		else
			$authorized = ($cur_user['password'] == $form_password_hash);
	}

	if (!$authorized)
		message(__('Wrong username and/or password.', 'luna').' <a data-toggle="modal" data-target="#reqpass" data-dismiss="modal">'.__('Forgotten password', 'luna').'</a>.');

	// Update the status if this is the first time the user logged in
	if ($cur_user['group_id'] == LUNA_UNVERIFIED) {
		$db->query('UPDATE '.$db->prefix.'users SET group_id='.$luna_config['o_default_user_group'].' WHERE id='.$cur_user['id']) or error('Unable to update user status', __FILE__, __LINE__, $db->error());

		// Regenerate the users info cache
		if (!defined('LUNA_CACHE_FUNCTIONS_LOADED'))
			require LUNA_ROOT.'include/cache.php';

		generate_users_info_cache();
	}

	// Remove this user's guest entry from the online list
	$db->query('DELETE FROM '.$db->prefix.'online WHERE ident=\''.$db->escape(get_remote_address()).'\'') or error('Unable to delete from online list', __FILE__, __LINE__, $db->error());

	$expire = ($save_pass == '1') ? time() + 1209600 : time() + $luna_config['o_timeout_visit'];
	luna_setcookie($cur_user['id'], $form_password_hash, $expire);

	// Reset tracked threads
	set_tracked_threads(null);

	// Try to determine if the data in redirect_url is valid (if not, we redirect to index.php after the email is sent)
	$redirect_url = validate_redirect($_POST['redirect_url'], 'index.php');

	redirect(luna_htmlspecialchars($redirect_url));
}


elseif ($action == 'out') {
	if ($luna_user['is_guest'] || !isset($_GET['id']) || $_GET['id'] != $luna_user['id']) {
		header('Location: index.php');
		exit;
	}

	check_csrf($_GET['csrf_token']);

	// Remove user from "users online" list
	$db->query('DELETE FROM '.$db->prefix.'online WHERE user_id='.$luna_user['id']) or error('Unable to delete from online list', __FILE__, __LINE__, $db->error());

	// Update last_visit (make sure there's something to update it with)
	if (isset($luna_user['logged']))
		$db->query('UPDATE '.$db->prefix.'users SET last_visit='.$luna_user['logged'].' WHERE id='.$luna_user['id']) or error('Unable to update user visit data', __FILE__, __LINE__, $db->error());

	luna_setcookie(1, luna_hash(uniqid(rand(), true)), time() + 31536000);

	redirect('index.php');
}


elseif ($action == 'forget' || $action == 'forget_2') {
	if (!$luna_user['is_guest']) {
		header('Location: index.php');
		exit;
	}

	if (isset($_POST['form_sent'])) {
		// Start with a clean slate
		$errors = array();

		require LUNA_ROOT.'include/email.php';

		// Validate the email address
		$email = strtolower(luna_trim($_POST['req_email']));
		if (!is_valid_email($email)) {
			message(__('The email address you entered is invalid.', 'luna'));
			exit;
		}

		// Did everything go according to plan?
		if (empty($errors)) {
			$result = $db->query('SELECT id, username, last_email_sent FROM '.$db->prefix.'users WHERE email=\''.$db->escape($email).'\'') or error('Unable to fetch user info', __FILE__, __LINE__, $db->error());

			if ($db->num_rows($result)) {
				// Load the "activate password" template
				$mail_tpl = trim(__('Subject: New password requested

Hello <username>,

You have requested to have a new password assigned to your account in the discussion forum at <base_url>. If you did not request this or if you do not want to change your password you should just ignore this message. Only if you visit the activation page below will your password be changed.

Your new password is: <new_password>

To change your password, please visit the following page:
<activation_url>

--
<board_mailer> Mailer
(Do not reply to this message)', 'luna'));

				// The first row contains the subject
				$first_crlf = strpos($mail_tpl, "\n");
				$mail_subject = trim(substr($mail_tpl, 8, $first_crlf-8));
				$mail_message = trim(substr($mail_tpl, $first_crlf));

				// Do the generic replacements first (they apply to all emails sent out here)
				$mail_message = str_replace('<base_url>', get_base_url().'/', $mail_message);
				$mail_message = str_replace('<board_mailer>', $luna_config['o_board_title'], $mail_message);

				// Loop through users we found
				while ($cur_hit = $db->fetch_assoc($result)) {
					if ($cur_hit['last_email_sent'] != '' && (time() - $cur_hit['last_email_sent']) < 3600 && (time() - $cur_hit['last_email_sent']) >= 0)
						message(sprintf(__('This account has already requested a password reset in the past hour. Please wait %s minutes before requesting a new password again.', 'luna'), intval((3600 - (time() - $cur_hit['last_email_sent'])) / 60)), true);

					// Generate a new password and a new password activation code
					$new_password = random_pass(12);
					$new_password_key = random_pass(8);

					$db->query('UPDATE '.$db->prefix.'users SET activate_string=\''.$db->escape($new_password).'\', activate_key=\''.$db->escape($new_password_key).'\', last_email_sent = '.time().' WHERE id='.$cur_hit['id'])
                        or error('Unable to update activation data', __FILE__, __LINE__, $db->error());
					// Do the user specific replacements to the template
					$cur_mail_message = str_replace('<username>', $cur_hit['username'], $mail_message);
					$cur_mail_message = str_replace('<activation_url>', get_base_url().'/settings.php?id='.$cur_hit['id'].'&action=change_pass&key='.$new_password_key, $cur_mail_message);
					$cur_mail_message = str_replace('<new_password>', $new_password, $cur_mail_message);

					luna_mail($email, $mail_subject, $cur_mail_message);
				}

				message(__('An email has been sent to the specified address with instructions on how to change your password. If it does not arrive you can contact the forum administrator at', 'luna').' <a href="mailto:'.luna_htmlspecialchars($luna_config['o_admin_email']).'">'.luna_htmlspecialchars($luna_config['o_admin_email']).'</a>.', true);
			}
			else
				message(__('There is no user registered with the email address', 'luna').' '.htmlspecialchars($email).'.');
		}
	}
}

// Try to determine if the data in HTTP_REFERER is valid (if not, we redirect to index.php after login)
if (!empty($_SERVER['HTTP_REFERER']))
	$redirect_url = validate_redirect($_SERVER['HTTP_REFERER'], null);

if (!isset($redirect_url))
	$redirect_url = get_base_url(true).'/index.php';
elseif (preg_match('%thread\.php\?pid=(\d+)$%', $redirect_url, $matches))
	$redirect_url .= '#p'.$matches[1];
