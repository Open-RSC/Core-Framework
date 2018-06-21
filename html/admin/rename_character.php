<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'rename_char');

require LUNA_ROOT.'include/common.php';

if ($luna_user['g_id'] != LUNA_ADMIN) {
	header("Location: ../backstage/login.php");
    exit;
}

if(isset($_GET['char_search']) && strlen($_GET['char_search']) >= 1 && strlen($_GET['char_search']) <= 12) {
		$find_user = $db->query("SELECT id, user, owner, username, online FROM " . GAME_BASE . "players WHERE username = '" . $db->escape($_GET['char_search']) . "'");
		if($db->num_rows($find_user) > 0) {
			$fetch_user = $db->fetch_assoc($find_user);
			if(isset($_POST['start_rename'])) {
				confirm_referrer('admin/rename_character.php');
				if($fetch_user['online'] != 0) {
					message_backstage(__('Player is currently online, to character rename the user must be offline.', 'luna'));
				}
				//Data conversion
                                function usernameToHash($s) {
                                        $s = strtolower($s);
                                        $s1 = '';
                                        for ($i = 0;$i < strlen($s);$i++) {
                                                $c = $s{$i};
                                                if ($c >= 'a' && $c <= 'z') {
                                                    $s1 = $s1 . $c;
                                                } else if ($c >= '0' && $c <= '9') {
                                                    $s1 = $s1 . $c;
                                                } else {
                                                    $s1 = $s1 . ' ';
                                                }
                                        }

                                        $s1 = trim($s1);
                                        if (strlen($s1) > 12) {
                                            $s1 = substr($s1, 0, 12); //trims the username down to 12 characters if more are sent
                                        }

                                        $l = 0;
                                        for ($j = 0;$j < strlen($s1);$j++) {
                                                $c1 = $s1{$j};
                                                $l *= 37;
                                                if ($c1 >= 'a' && $c1 <= 'z') {
                                                    $l += (1 + ord($c1)) - 97;
                                                } else if ($c1 >= '0' && $c1 <= '9') {
                                                    $l += (27 + ord($c1)) - 48;
                                                }
                                        }
                                        return $l;
                                }
                                function hashToUsername($l) {
                                        if ($l < 0) {
                                                return 'invalid_name';
                                        }
                                        $s = '';
                                        while ($l != 0) {
                                                $i = floor(floatval($l % 37));
                                                $l = floor(floatval($l / 37));
                                                if ($i == 0) {
                                                    $s = ' ' . $s;
                                                } 
                                                else if ($i < 27) {
                                                        if ($l % 37 == 0) {
                                                            $s = chr(($i + 65) - 1) . $s;
                                                        }
                                                        else {
                                                                $s = chr(($i + 97) - 1) . $s;
                                                        }
                                                }
                                                else {
                                                        $s = chr(($i + 48) - 27) . $s;
                                                }
                                        }
                                        return $s;
}
                                $char_rename_to = isset($_POST['rename_to_this']) && strlen($_POST['rename_to_this']) <= 16 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_POST['rename_to_this']) ? trim($_POST['rename_to_this']) : null;
                                $usernameHash = usernameToHash($char_rename_to);
				if(isset($char_rename_to)) {
					$rename_char = $db->query("SELECT user,online FROM " . GAME_BASE . "players WHERE username = '" . $char_rename_to . "'");
					if($db->num_rows($rename_char) > 0) {
						message_backstage(__('The name already exist.', 'luna'));
					} else {
						$db->query("UPDATE " . GAME_BASE . "players SET username='" . $db->escape($char_rename_to) . "' WHERE id ='" . $fetch_user['id'] . "'") or error('Failed to rename player username', __FILE__, __LINE__, $db->error());
                                                $db->query("UPDATE " . GAME_BASE . "players SET user = '" . $db->escape($usernameHash) . "' WHERE id='" . $fetch_user['id'] . "'");
                                                $db->query("UPDATE " . GAME_BASE . "experience SET user = '" . $db->escape($usernameHash) . "' WHERE id='" . $fetch_user['id'] . "'");
                                                $db->query("UPDATE " . GAME_BASE . "curstats SET user = '" . $db->escape($usernameHash) . "' WHERE id='" . $fetch_user['id'] . "'");
                                                $db->query("UPDATE " . GAME_BASE . "invitems SET user = '" . $db->escape($usernameHash) . "' WHERE user='" . $fetch_user['user'] . "'");
                                                $db->query("UPDATE " . GAME_BASE . "quests SET user = '" . $db->escape($usernameHash) . "' WHERE id='" . $fetch_user['id'] . "'");
						$db->query("UPDATE " . GAME_BASE . "auctions SET player = '" . $db->escape($char_rename_to) . "' WHERE player='" . $fetch_user['id'] . "'");
						$db->query("UPDATE " . GAME_BASE . "friends SET user = '" . $db->escape($usernameHash) . "' WHERE user='" . $fetch_user['user'] . "'");
                                                $db->query("UPDATE " . GAME_BASE . "ignores SET user = '" . $db->escape($usernameHash) . "' WHERE user='" . $fetch_user['user'] . "'");
												
						// Insert into name change table			
						$db->query('INSERT INTO ' . GAME_BASE . 'name_changes (user, owner, old_name, new_name, date) VALUES('.intval($fetch_user['id']).', '.intval($fetch_user['owner']).', \''.$db->escape($fetch_user['username']).'\',  \''.$db->escape($char_rename_to).'\', '.time().')') or error('Unable to save character name change!', __FILE__, __LINE__, $db->error());
											
						new_notification($luna_user['id'], '#', __('You have renamed '. luna_htmlspecialchars($_GET['char_search']) . ' to '. $char_rename_to . ' via admin panel.', 'luna'), 'fa-bar-chart');
						redirect('admin/rename_character.php?char_search=' . $char_rename_to . '&amp;saved=true');
					}
				} else {
					message_backstage(__('Invalid format on second field.', 'luna'));
				}
			}
		} else {
			message_backstage(__('Could not find the player name.', 'luna'));
		}
require 'header.php';	
?>
<div class="content_wrapper col-sm-10">
	<form method="post" action="rename_character.php?char_search=<?php echo $_GET['char_search'] ?>">
	<div class="row title">
		<div class="small-12 columns">
			<h2 class="page-heading">Character rename for <?php echo $_GET['char_search']?></h2>
		</div>
	</div>
	<div class="small-12 columns">
	<?php
	if (isset($_GET['saved']))
		echo '<div class="alert alert-success"><i class="fa fa-fw fa-check"></i> '.__('Character have been renamed successfully!', 'luna').'</div>';
	?>
	<table class="base-tbl">
	<tbody>
		<tr>
			<td>
				<strong>Character you are renaming:</strong>
			</td>
			<td>
				<input class='form-control' type='text' maxlength='12' value='<?php echo $_GET['char_search']; ?>' disabled/>
			</td>
		</tr>
		<tr>
			<td>
				<strong>The name you wish to rename it to:</strong>
			</td>
			<td>
				<input class='form-control' type='text' maxlength='12' name='rename_to_this' value='' />
			</td>
		</tr>
		<tr>
			<td colspan='2'>
				<input onclick='return confirm(\"ARE YOU SURE?\");' class='block-btn block-btn--form' type='submit' name='start_rename' value='Click To Rename Character' />
			</td>
		</tr>
		</tbody>
	</table>
	</div>
	</form>
</div>
<?php } else { 
require 'header.php';	
?>
<div class="content_wrapper col-sm-10">
	<div class="row title">
		<div class="small-12 columns">
			<h2 class="page-heading">Search for a Player</h2>
		</div>
	</div>
	<div class="small-12 columns">
	<form class="form-horizontal" method="get" action="">
		<div class="form-group">
		<label class="search-form__label" for="player">
			Player Name
		</label>
		<div class="searchTab">
			<input class="form-control" type="text" name="char_search" value="<?php echo (isset($_GET['char_search']) ? $_GET['char_search'] : null) ?>" maxlength="12" />
		</div>
		<button class="block-btn block-btn--form" type="submit">Search</button>
		</div>
	</form>
	</div>
</div>
<?php 
} 
require 'footer.php';