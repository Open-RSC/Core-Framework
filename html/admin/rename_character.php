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
		$find_user = $db->query("SELECT id, owner, username, online FROM " . GAME_BASE . "players WHERE username = '" . $db->escape($_GET['char_search']) . "'");
		if($db->num_rows($find_user) > 0) {
			$fetch_user = $db->fetch_assoc($find_user);
			if(isset($_POST['start_rename'])) {
				confirm_referrer('admin/rename_character.php');
				if($fetch_user['online'] != 0) {
					message_backstage(__('Player is currently online, to character rename the user must be offline.', 'luna'));
				}
				$char_rename_to = isset($_POST['rename_to_this']) && strlen($_POST['rename_to_this']) <= 16 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_POST['rename_to_this']) ? trim($_POST['rename_to_this']) : null;
				if(isset($char_rename_to)) {
					$rename_char = $db->query("SELECT user,online FROM " . GAME_BASE . "players WHERE username = '" . $char_rename_to . "'");
					if($db->num_rows($rename_char) > 0) {
						message_backstage(__('The name already exist.', 'luna'));
					} else {
						$db->query("UPDATE " . GAME_BASE . "players SET username='" . $db->escape($char_rename_to) . "' WHERE id ='" . $fetch_user['id'] . "'") or error('Failed to rename player username', __FILE__, __LINE__, $db->error());
						$db->query("UPDATE " . GAME_BASE . "auctions SET seller_username = '" . $db->escape($char_rename_to) . "' WHERE seller_username='" . $fetch_user['username'] . "'") or die('ew13');
						$db->query("UPDATE " . GAME_BASE . "friends SET friendName = '" . $db->escape($char_rename_to) . "' WHERE friendName='" . $fetch_user['username'] . "'") or die('ew13');
						$db->query("UPDATE " . GAME_BASE . "clan SET leader = '" . $db->escape($char_rename_to) . "' WHERE leader='" . $fetch_user['username']  . "'") or die('ew14');
						$db->query("UPDATE " . GAME_BASE . "clan_players SET username = '" . $db->escape($char_rename_to) . "' WHERE username='" . $fetch_user['username'] . "'") or die('ew15');
						
						// Insert into name change table			
						$db->query('INSERT INTO ' . GAME_BASE . 'name_changes (playerID, owner, old_name, new_name, date) VALUES('.intval($fetch_user['id']).', '.intval($fetch_user['owner']).', \''.$db->escape($fetch_user['username']).'\',  \''.$db->escape($char_rename_to).'\', '.time().')') or error('Unable to save character name change!', __FILE__, __LINE__, $db->error());
											
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