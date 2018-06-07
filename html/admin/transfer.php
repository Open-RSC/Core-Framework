<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'transfer_char');

require LUNA_ROOT.'include/common.php';

if ($luna_user['g_id'] != LUNA_ADMIN) {
	header("Location: ../backstage/login.php");
    exit;
}

if(isset($_GET['character_transfer']) && strlen($_GET['character_transfer']) >= 1 && strlen($_GET['character_transfer']) <= 12) {
		$find_user = $db->query("SELECT id, owner, online FROM " . GAME_BASE . "players WHERE username = '" . $db->escape($_GET['character_transfer']) . "'") or error('Failed to host user info', __FILE__, __LINE__, $db->error());
		if(!$db->num_rows($find_user)) 
		{
			message_backstage(__('Could not find the player name.', 'luna'));
		}
		$fetch_user = $db->fetch_assoc($find_user);

		$find_forum = $db->query("SELECT id, username FROM ".$db->prefix."users WHERE id = '" . $db->escape($fetch_user['owner']) . "'") or error('Failed to host forum user query', __FILE__, __LINE__, $db->error());
		if(!$db->num_rows($find_forum)) 
		{
			message_backstage(__('Cannot find forum user info.', 'luna'));
		}
		$forum_user = $db->fetch_assoc($find_forum);
		
		if(isset($_POST['start_transfer'])) 
		{
			confirm_referrer('admin/transfer.php');
			if($fetch_user['online'] != 0) 
			{
				message_backstage(__('Player is currently online, to transfer the account it must be offline.', 'luna'));
			}
			$transfer_char = isset($_POST['transfer_to_this']) && strlen($_POST['transfer_to_this']) <= 16 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_POST['transfer_to_this']) ? trim($_POST['transfer_to_this']) : null;
			if(isset($transfer_char)) 
			{
				if($forum_user['username'] == $_POST['transfer_to_this']) 
				{
					message_backstage(__('This character already belong to this forum account.', 'luna'));
				}
				
				$forum_reciever = $db->query("SELECT id, character_slots FROM ".$db->prefix."users WHERE username = '" . $db->escape($transfer_char) . "'");
				$forum_result = $db->fetch_assoc($forum_reciever);
				if(!$db->num_rows($forum_reciever)) 
				{
					message_backstage(__('Cannot find the forum username.', 'luna'));
				} 
				else 
				{
					/** TOTAL ACCOUNT SLOTS FOR FORUM ACCOUNT ID **/
					$check_slots = $db->query("SELECT id FROM " . GAME_BASE . "players WHERE owner = '" . $db->escape($forum_result['id']) . "'") or error('Failed to host forum info', __FILE__, __LINE__, $db->error());
					$maximum_character_slots = 10;
					$my_character_slots = ($forum_result['character_slots'] > $maximum_character_slots ? 10 : $forum_result['character_slots']);
					$character_slots_remaining = $my_character_slots - $db->num_rows($check_slots);

					if($character_slots_remaining > 0) 
					{
						$db->query("UPDATE " . GAME_BASE . "players SET owner='" . $db->escape($forum_result['id']) . "' WHERE id ='" . $db->escape($fetch_user['id']) . "'") or error('Failed to transfer player account: '.luna_htmlspecialchars($transfer_char).'', __FILE__, __LINE__, $db->error());
					
						new_notification($luna_user['id'], '#', __('You have transfered '. luna_htmlspecialchars($_GET['character_transfer']) . ' to '. luna_htmlspecialchars($transfer_char) . ' via admin panel.', 'luna'), 'fa fa-truck');
						redirect('admin/transfer.php?character_transfer=' . $_GET['character_transfer'] . '&amp;saved=true');
				
					} 
					else 
					{
						message_backstage(__('No available character slots on forum account: '.luna_htmlspecialchars($transfer_char).'', 'luna'));
					}
				}
			} 
			else 
			{
				message_backstage(__('Invalid format on second field.', 'luna'));
			}
		}
require 'header.php';	
?>
<div class="content_wrapper col-sm-10">
	<form method="post" action="transfer.php?character_transfer=<?php echo $_GET['character_transfer'] ?>">
	<div class="row title">
		<div class="small-12 columns">
			<h2 class="page-heading">Character transfer for <?php echo $_GET['character_transfer']?></h2>
		</div>
	</div>
	<div class="small-12 columns">
	<?php
	if (isset($_GET['saved']))
		echo '<div class="alert alert-success"><i class="fa fa-fw fa-check"></i> '.__('Character have been transfered successfully!', 'luna').'</div>';
	?>
	<table class="base-tbl">
	<tbody>
		<tr>
			<td>
				<strong>Character you are transfering:</strong>
			</td>
			<td>
				<input class='form-control' type='text' maxlength='12' value='<?php echo $_GET['character_transfer']; ?>' disabled/>
			</td>
		</tr>
		<tr>
			<td>
				<strong>Character currently belongs to forum:</strong>
			</td>
			<td>
				<input class='form-control' type='text' maxlength='12' value='<?php echo $forum_user['username']; ?>' disabled/>
			</td>
		</tr>
		<tr>
			<td>
				<strong>The forum account you wish to transfer the character to:</strong>
			</td>
			<td>
				<input class='form-control' type='text' maxlength='12' name='transfer_to_this' value='' />
			</td>
		</tr>
		<tr>
			<td colspan='2'>
				<input onclick='return confirm(\"ARE YOU SURE TO MAKE THIS TRANSFER?\");' class='block-btn block-btn--form' type='submit' name='start_transfer' value='Click To Transfer Character' />
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
			<input class="form-control" type="text" name="character_transfer" value="<?php echo (isset($_GET['character_transfer']) ? $_GET['character_transfer'] : null) ?>" maxlength="12" />
		</div>
		<button class="block-btn block-btn--form" type="submit">Search</button>
		</div>
	</form>
	</div>
</div>
<?php 
} 
require 'footer.php';