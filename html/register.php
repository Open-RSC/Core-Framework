<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

define('LUNA_ROOT', dirname(__FILE__).'/');
require LUNA_ROOT.'include/common.php';

// If we are logged in, we shouldn't be here
if (!$luna_user['is_guest']) {
	header('Location: index.php');
	exit;
}

if ($luna_config['o_regs_allow'] == '0')
	message(__('This forum is not accepting new registrations.', 'luna'));

require LUNA_ROOT.'captcha/recaptcha.php';

// INSTALL THE CAPTCHA
$captcha = new addon_recaptcha();

if ($luna_config['o_rules'] == '1' && !isset($_GET['agree']) && !isset($_POST['form_sent'])) {
	$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), __('Register', 'luna'), __('Forum rules', 'luna'));
	define('LUNA_ACTIVE_PAGE', 'register');
	require load_page('header.php');

	require load_page('rules.php');

	require load_page('footer.php');
} else {

	// Start with a clean slate
	$errors = array();

	if (isset($_POST['form_sent'])) {
		// Check that someone from this IP didn't register a user within the last hour (DoS prevention)
		$result = $db->query('SELECT 1 FROM '.$db->prefix.'users WHERE registration_ip=\''.$db->escape(get_remote_address()).'\' AND registered>'.(time() - 3600)) or error('Unable to fetch user info', __FILE__, __LINE__, $db->error());

		//if ($db->num_rows($result))
			//message(__('A new user was registered with the same IP address as you within the last hour. To prevent registration flooding, at least an hour has to pass between registrations from the same IP. Sorry for the inconvenience.', 'luna'));


		$username = luna_trim($_POST['req_user']);
		$email1 = strtolower(luna_trim($_POST['req_email1']));

		if ($luna_config['o_regs_verify'] == '1') {
			$email2 = strtolower(luna_trim($_POST['req_email2']));

			$password1 = random_pass(12);
			$password2 = $password1;
		} else {
			$password1 = luna_trim($_POST['req_password1']);
			$password2 = luna_trim($_POST['req_password2']);
		}
		
		$char_username = luna_trim($_POST['char_username']);
		
		$char_password1 = luna_trim($_POST['char_password1']);
		$char_password2 = luna_trim($_POST['char_password2']);

		// Validate username and passwords
		check_username($username);

		if (luna_strlen($password1) < 6)
			$errors[] = __('Forum Passwords must be at least 6 characters long. Please choose another (longer) password.', 'luna');
		elseif ($password1 != $password2)
			$errors[] = __('Forum Passwords do not match.', 'luna');
		else if(strlen($char_username) < 2 || strlen($char_username) > 12)
			$errors[] = __('Your game character username must be from 2 to 12 characters in length.', 'luna');
		else if(!preg_match("/^[a-zA-Z0-9\s]+?$/i", $char_username))
			$errors[] = __('Your game character username can only contain regular letters, numbers and spaces.', 'luna');
		else if (preg_match('/^Mod\s+/i', $char_username) || preg_match('/^Admin\s+/i', $char_username))
			$errors[] = __('Sorry, but you can not create a game character that begins with Mod or Admin.', 'luna');
		else if (luna_strlen($char_password1) < 4 || luna_strlen($char_password1) > 16)
			$errors[] = __('Your game character password needs to be at least 4 in length and maximum 16 in length.', 'luna');
		else if ($char_password1 != $char_password2)
			$errors[] = __('Your game character passwords do not match.', 'luna');
		else if(!preg_match("/^[a-zA-Z0-9\s]+?$/i", $char_password1))
			$errors[] = __('Your game character password can only contain regular letters, numbers and spaces.', 'luna');
		
		$check_name_in_use = $db->query("SELECT id FROM " . GAME_BASE . "players WHERE username = '" . $db->escape($char_username) . "'");
		if ($db->num_rows($check_name_in_use))  {
			$errors[] = __('The username "' . luna_htmlspecialchars($char_username) . '" is already in use.', 'luna');
		}

		// Validate email
		require LUNA_ROOT.'include/email.php';

		if (!is_valid_email($email1))
			$errors[] = __('The email address you entered is invalid.', 'luna');
		elseif ($luna_config['o_regs_verify'] == '1' && $email1 != $email2)
			$errors[] = __('Email addresses do not match.', 'luna');

		// Check if it's a banned email address
		if (is_banned_email($email1)) {
			if ($luna_config['o_allow_banned_email'] == '0')
				$errors[] = __('The email address you entered is banned in this forum. Please choose another email address.', 'luna');

			$banned_email = true; // Used later when we send an alert email
		} else
			$banned_email = false;

		// Check if someone else already has registered with that email address
		$dupe_list = array();

		$result = $db->query('SELECT username FROM '.$db->prefix.'users WHERE email=\''.$db->escape($email1).'\'') or error('Unable to fetch user info', __FILE__, __LINE__, $db->error());
		if ($db->num_rows($result)) {
			if ($luna_config['o_allow_dupe_email'] == '0')
				$errors[] = __('Someone else is already registered with that email address. Please choose another email address.', 'luna');

			while ($cur_dupe = $db->fetch_assoc($result))
				$dupe_list[] = $cur_dupe['username'];
		}
		
		// Google new reCAPTCHA implemention and validation after submit request.
		$captcha->hook_register_after_validation();

		$req_username = empty($username) ? luna_trim($_POST['req_username']) : $username;
		if (!empty($_POST['req_username'])) {
			// Since we found a spammer, lets report the bastard!
			message(__('Unfortunately it looks like your request is spam. If you feel this is a mistake, please direct any inquiries to the forum administrator at', 'luna').' <a href="mailto:'.luna_htmlspecialchars($luna_config['o_admin_email']).'">'.luna_htmlspecialchars($luna_config['o_admin_email']).'</a>.', true);
		}

		// Did everything go according to plan?
		if (empty($errors)) {
			// Insert the new user into the database. We do this now to get the last inserted ID for later use
			$now = time();

			$intial_group_id = ($luna_config['o_regs_verify'] == '0') ? $luna_config['o_default_user_group'] : LUNA_UNVERIFIED;
            $salt = random_pass(8);
			$password_hash = luna_sha512($password1, $salt);

			// Add the user
			$db->query('INSERT INTO '.$db->prefix.'users (username, group_id, password, email, language, registered, registration_ip, last_visit, php_timezone, salt) VALUES(\''.$db->escape($username).'\', '.$intial_group_id.', \''.$password_hash.'\', \''.$db->escape($email1).'\', \''.$luna_config['o_default_lang'].'\', '.$now.', \''.$db->escape(get_remote_address()).'\', '.$now.', \''.$luna_config['o_timezone'].'\', \''.$salt.'\')') or error('Unable to create user', __FILE__, __LINE__, $db->error());
			$new_uid = $db->insert_id();
			
			// FIRST GAME CHAR
			// SALT + SHA512 + Secrety key here.
			$char_salt = random_pass(16); // 8 default?
			$char_password_hash = game_hmac($char_salt.$char_password1, $HMAC_PRIVATE_KEY);

			$db->query("INSERT INTO " . GAME_BASE . "players (username,owner,pass,salt,creation_date,creation_ip) VALUES ('" . $db->escape($char_username) . "', '" . $new_uid . "', '" . $char_password_hash . "', '" . $char_salt . "', '".(time())."', '". $_SERVER['REMOTE_ADDR'] ."');") or error('Unable to insert game character', __FILE__, __LINE__, $db->error());
			$new_playerID = $db->insert_id();
			$db->query("INSERT INTO " . GAME_BASE . "curstats (playerID) VALUES ('" . $new_playerID . "');") or error('Unable to insert current stats on game character', __FILE__, __LINE__, $db->error());
			$db->query("INSERT INTO " . GAME_BASE . "experience (playerID) VALUES ('" . $new_playerID . "');") or error('Unable to insert experience on game character', __FILE__, __LINE__, $db->error());
				
			new_notification($new_uid, 'char_manager.php?id='.$id.'', __('Adventurer! You have created a RSCLegacy character: '. luna_htmlspecialchars($char_username) . '!', 'luna'), 'fa-user-plus');
			
			if ($luna_config['o_regs_verify'] == '0') {
				// Regenerate the users info cache
				if (!defined('LUNA_CACHE_FUNCTIONS_LOADED'))
					require LUNA_ROOT.'include/cache.php';

				generate_users_info_cache();
			}

			// If the mailing list isn't empty, we may need to send out some alerts
			if ($luna_config['o_mailing_list'] != '') {
				// If we previously found out that the email was banned
				if ($banned_email) {
					// Load the "banned email register" template
					$mail_tpl = trim(__('Subject: Alert - Banned email detected

User "<username>" registered with banned email address: <email>

User profile: <profile_url>

--
<board_mailer> Mailer
(Do not reply to this message)', 'luna'));

					// The first row contains the subject
					$first_crlf = strpos($mail_tpl, "\n");
					$mail_subject = trim(substr($mail_tpl, 8, $first_crlf-8));
					$mail_message = trim(substr($mail_tpl, $first_crlf));

					$mail_message = str_replace('<username>', $username, $mail_message);
					$mail_message = str_replace('<email>', $email1, $mail_message);
					$mail_message = str_replace('<profile_url>', get_base_url().'/profile.php?id='.$new_uid, $mail_message);
					$mail_message = str_replace('<admin_url>', get_base_url().'/settings.php?id='.$new_uid, $mail_message);
					$mail_message = str_replace('<board_mailer>', $luna_config['o_board_title'], $mail_message);

					luna_mail($luna_config['o_mailing_list'], $mail_subject, $mail_message);
				}

				// If we previously found out that the email was a dupe
				if (!empty($dupe_list)) {
					// Load the "dupe email register" template
					$mail_tpl = trim(__('Subject: Alert - Duplicate email detected

User "<username>" registered with an email address that also belongs to: <dupe_list>

User profile: <profile_url>

--
<board_mailer> Mailer
(Do not reply to this message)', 'luna'));

					// The first row contains the subject
					$first_crlf = strpos($mail_tpl, "\n");
					$mail_subject = trim(substr($mail_tpl, 8, $first_crlf-8));
					$mail_message = trim(substr($mail_tpl, $first_crlf));

					$mail_message = str_replace('<username>', $username, $mail_message);
					$mail_message = str_replace('<dupe_list>', implode(', ', $dupe_list), $mail_message);
					$mail_message = str_replace('<profile_url>', get_base_url().'/profile.php?id='.$new_uid, $mail_message);
					$mail_message = str_replace('<board_mailer>', $luna_config['o_board_title'], $mail_message);

					luna_mail($luna_config['o_mailing_list'], $mail_subject, $mail_message);
				}

				// Should we alert people on the admin mailing list that a new user has registered?
				if ($luna_config['o_regs_report'] == '1') {
					// Load the "new user" template
					$mail_tpl = trim(__('Subject: Alert - New registration

User "<username>" registered in the forums at <base_url>

User profile: <profile_url>

To administer this account, please visit the following page:
<admin_url>

--
<board_mailer> Mailer
(Do not reply to this message)', 'luna'));

					// The first row contains the subject
					$first_crlf = strpos($mail_tpl, "\n");
					$mail_subject = trim(substr($mail_tpl, 8, $first_crlf-8));
					$mail_message = trim(substr($mail_tpl, $first_crlf));

					$mail_message = str_replace('<username>', $username, $mail_message);
					$mail_message = str_replace('<base_url>', get_base_url().'/', $mail_message);
					$mail_message = str_replace('<profile_url>', get_base_url().'/profile.php?id='.$new_uid, $mail_message);
				    $mail_message = str_replace('<admin_url>', get_base_url().'/profile.php?id='.$new_uid, $mail_message);
					$mail_message = str_replace('<board_mailer>', $luna_config['o_board_title'], $mail_message);

					luna_mail($luna_config['o_mailing_list'], $mail_subject, $mail_message);
				}
			}

			// Must the user verify the registration or do we log him/her in right now?
			if ($luna_config['o_regs_verify'] == '1') {
				// Load the "welcome" template
				$mail_tpl = trim(__('Subject: Welcome to <board_title>!

Thank you for registering in the forums at <base_url>. Your account details are:

Username: <username>
Password: <password>

Login at <login_url> to activate the account.

--
<board_mailer> Mailer
(Do not reply to this message)', 'luna'));

				// The first row contains the subject
				$first_crlf = strpos($mail_tpl, "\n");
				$mail_subject = trim(substr($mail_tpl, 8, $first_crlf-8));
				$mail_message = trim(substr($mail_tpl, $first_crlf));

				$mail_subject = str_replace('<board_title>', $luna_config['o_board_title'], $mail_subject);
				$mail_message = str_replace('<base_url>', get_base_url().'/', $mail_message);
				$mail_message = str_replace('<username>', $username, $mail_message);
				$mail_message = str_replace('<password>', $password1, $mail_message);
				$mail_message = str_replace('<login_url>', get_base_url().'/login.php', $mail_message);
				$mail_message = str_replace('<board_mailer>', $luna_config['o_board_title'], $mail_message);

				luna_mail($email1, $mail_subject, $mail_message);

				message(__('Thank you for registering. Your forum account password has been sent to the specified address. If it doesn\'t arrive you can contact the forum administrator at', 'luna').' <a href="mailto:'.luna_htmlspecialchars($luna_config['o_admin_email']).'">'.luna_htmlspecialchars($luna_config['o_admin_email']).'</a>.<p>Your game character is activated, you may login to our game now.</p><p><strong>DOWNLOAD PC CLIENT: <a href="RSCLauncher.jar">RSCLauncher</a></strong></p><p><strong>DOWNLOAD ANDROID APPLICATION: <a href="android/wolfkingdom.apk">RSCLegacy.APK</a></strong></p>', true);
			}

			luna_setcookie($new_uid, $password_hash, time() + $luna_config['o_timeout_visit']);

			redirect('index.php');
		}
	}

	$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), __('Register', 'luna'));
	$required_fields = array('req_user' => __('Username', 'luna'), 'req_password1' => __('Password', 'luna'), 'req_password2' => __('Confirm password', 'luna'), 'req_email1' => __('Email', 'luna'), 'req_email2' => __('Email', 'luna').' 2');
	$focus_element = array('register', 'req_user');
	define('LUNA_ACTIVE_PAGE', 'register');
	require load_page('header.php');

	require load_page('register.php');

	require load_page('footer.php');

}
