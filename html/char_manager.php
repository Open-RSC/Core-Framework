<?php
/*
 * Character management
 * Created by Imposter 2016-07-07.
 */
define('LUNA_ROOT', dirname(__FILE__).'/');
require LUNA_ROOT.'include/common.php';

$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), __('Character Management', 'luna'));
define('LUNA_ACTIVE_PAGE', 'character');

$id = isset($_GET['id']) ? intval($_GET['id']) : $luna_user['id'];

if ($id < 2)
	message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

if ($luna_user['g_view_users'] == '0')
	message(__('You do not have permission to access this page.', 'luna'), false, '403 Forbidden');
else if ($luna_user['is_guest'] || ($luna_user['id'] != $id && $luna_user['g_id'] != LUNA_ADMIN))
	message(__('You do not have permission to access this page.', 'luna'), false, '403 Forbidden');


//PAGE SELECTORS
$pages = array('select', 'create');
$view_page = isset($_GET ['view']) && in_array($_GET ['view'], $pages) ? $_GET ['view'] : 'select';
$setting = isset($_GET['setting']) ? $_GET['setting'] : 'select';

//LOAD AND FIND CHARACTERS
$find_chars = $db->query("
			SELECT id,username,combat,forum_active,creation_date
			FROM " . GAME_BASE . "players
			WHERE owner = '" . $id .  "' 
			ORDER BY creation_date
	");
$fetch_char_info = $db->query("SELECT p.id,p.username,p.combat,p.quest_points,p.skill_total,p.highscoreopt,e.exp_attack,e.exp_defense,e.exp_strength,
e.exp_hits,e.exp_ranged,e.exp_prayer,e.exp_magic,e.exp_cooking,e.exp_woodcut,e.exp_fletching,e.exp_fishing,
e.exp_firemaking,e.exp_crafting,e.exp_smithing,e.exp_mining,e.exp_herblaw,e.exp_agility,e.exp_thieving FROM " . GAME_BASE . "players AS p LEFT JOIN " . GAME_BASE . "experience AS e ON e.playerID = p.id WHERE p.owner = '" . $id . "' AND p.forum_active = '1' LIMIT 1");

$apply_char = $db->fetch_assoc($fetch_char_info);

//SQL RESULT OF ACTIVE CHARACTER
$isActive = ($db->num_rows($fetch_char_info));

// WHEN SETTING PLAYER ID
$curr_char = isset($_GET['player']) && is_numeric($_GET['player']) ? intval(trim($_GET['player'])) : $apply_char['id'];

/** TOTAL ACCOUNT SLOTS PER FORUM ACCOUNT ID **/
$maximum_character_slots = 10;
$my_character_slots = ($luna_user['character_slots'] > $maximum_character_slots ? 10 : $luna_user['character_slots']);
$character_slots_remaining = $my_character_slots - $db->num_rows($find_chars);

// Character renaming price
//$RENAME_PRICE = 200;

/** Stat Reduction Holder **/
$validskills = 	array(
	0 => array('name' => 'attack', 'modify' => true),
	1 => array('name' => 'defense', 'modify' => true),
	2 => array('name' => 'strength', 'modify' => true),
	3 => array('name' => 'hits', 'modify' => false),
	4 => array('name' => 'prayer', 'modify' => true),
	5 => array('name' => 'ranged', 'modify' => true),
	6 => array('name' => 'magic', 'modify' => true)
	);

//ACHIEVEMENTS DATA
if($setting == 'achievements') 
{
	if($curr_char != $apply_char['id'] && $luna_user['g_id'] != LUNA_ADMIN) 
		redirect('char_manager.php?id='.$id);
	
	$achievement_data = $db->query("SELECT 1 FROM " . GAME_BASE . "achievements");
	$total_achievements = $db->num_rows($achievement_data);
	
	$achievement_status = $db->query("SELECT 1 FROM " . GAME_BASE . "achievement_progress WHERE playerID = '".$curr_char."'");
	$total_completed = $db->num_rows($achievement_status);
	
	$achievement_result = $db->query("SELECT a.dbid, a.name, a.description, a.extra, ap.completed, ap.unlocked FROM " . GAME_BASE . "achievements AS a LEFT JOIN " . GAME_BASE . "achievement_progress AS ap ON ap.id = a.dbid AND ap.playerID = '".$curr_char."' ORDER BY ap.unlocked DESC");
	
	// Function to calculate achievement progress percentages
	function get_achievement_percentage($completed, $total, $boolean = false) 
	{
		$percentage = 0;
		if($completed != 0) 
			$percentage = ($completed / $total) * 100;
		if($boolean == true)
			return number_format($completed, 0) . " of " . number_format($total, 0) . " (" . number_format($percentage, 0) . "%)";
		else 
			return $percentage;
	}
}
//FUNCTIONALITY
else if($setting != 'achievements') {
	switch($setting) {
		case "active":
			if(isset($_POST['put_active']) && $view_page == 'select' && isset($curr_char)) 
			{
				confirm_referrer('char_manager.php');
				
				$value = isset($_POST['put_active']) ? '1' : '0';
				if($value == 1) 
				{
					$db->query("UPDATE " . GAME_BASE . "players SET forum_active='0' WHERE id = '". $apply_char['id'] . "'") or error('Unable to remove current active character', __FILE__, __LINE__, $db->error());
					$db->query("UPDATE " . GAME_BASE . "players SET forum_active='1' WHERE id = '".$db->escape($curr_char)."'") or error('Unable to set active character', __FILE__, __LINE__, $db->error());
				}
				redirect('char_manager.php?id='.$id);
			}
		break;
		case "highscore":
			if(isset($_POST['highscore']) && $view_page == 'select' && isset($curr_char)) 
			{
				confirm_referrer('char_manager.php');
				
				if($curr_char != $apply_char['id'] && $luna_user['g_id'] != LUNA_ADMIN) 
					redirect('char_manager.php?id='.$id);
				
				$result = $db->query("SELECT highscoreopt FROM " . GAME_BASE . "players WHERE id = '" . $db->escape($curr_char) . "' AND owner = '" . $id . "'") or error('Unable to update highscore option', __FILE__, __LINE__, $db->error());
				if($db->num_rows($result) > 0)
				{
					$option = $db->fetch_assoc($result);
					$update = $option['highscoreopt'] == 0 ? 1 : 0;
					$db->query("UPDATE " . GAME_BASE . "players SET highscoreopt = '" . $update . "' WHERE id = '" . $db->escape($curr_char) . "'");
					redirect('char_manager.php?id='.$id.'&setting=highscore');
				}
			}
		break;
		case "change_password":
			if($view_page == 'select' && isset($curr_char) && isset($_POST['change_password']))
			{
				confirm_referrer('char_manager.php');	
				
				if($curr_char != $apply_char['id'] && $luna_user['g_id'] != LUNA_ADMIN) 
					redirect('char_manager.php?id='.$id);
				
				$find = $db->query("SELECT username,pass,salt FROM " . GAME_BASE . "players WHERE " . GAME_BASE . "players.id = '" . $db->escape($curr_char) . "' AND owner = '" . $id . "'");
				$arrayit = $db->fetch_assoc($find);
				if($db->num_rows($find) > 0)
				{
					$first_pass = isset($_POST['c_pass_1']) ? $_POST['c_pass_1'] : null;
					$second_pass = isset($_POST['c_pass_2']) ? $_POST['c_pass_2'] : null;
					$current_pass = isset($_POST['current_pass']) && strlen($_POST['current_pass']) <= 16 ? $_POST['current_pass'] : null;
					$errors = array();
					if(empty($first_pass) || empty($second_pass))
					{
						$errors[] = "Please fill in all the fields.";
					}
					/*if(empty($current_pass))
					{
						$errors[] = "You did not enter your current password.";
					}*/
					if($first_pass != $second_pass)
					{
						$errors[] = "Your passwords did not match.";
					}
					if(strlen($first_pass) < 4 || strlen($first_pass) > 16)
					{
						$errors[] = "Your password must be at least 4 to 16 characters in length.";
					}
					if(count($errors) == 0)
					{
						/*if($arrayit['pass'] != game_hmac($arrayit['salt'].$current_pass, $HMAC_PRIVATE_KEY))
						{
							$errors[] = "The current password you have entered is invalid.";
						} 
						else 
						{*/
							$new_salt = random_pass(16); // 8 default?
							$new_password_hash = game_hmac($new_salt.$first_pass, $HMAC_PRIVATE_KEY);
							
							$db->query("UPDATE " . GAME_BASE . "players SET pass= '" . $new_password_hash . "', salt='".$new_salt."' WHERE id = '" . $db->escape($curr_char) . "'") or die('Failed to update game character password');
							new_notification($id, 'char_manager.php?id='.$id.'', __('You have changed in-game password on character: '. luna_htmlspecialchars($arrayit['username']) . '.', 'luna'), 'fa-lock');
							redirect('char_manager.php?id='.$id.'&setting=change_password&saved=true');
						//}
					}
				} 
				else
				{
					$errors[] = "This character does not belong to you.";
				}
			}	
		break;
		case "reduction":
			if($view_page == 'select' && isset($curr_char) && isset($_POST['stat_reset']))
			{
				confirm_referrer('char_manager.php');	
				
				if($curr_char != $apply_char['id'] && $luna_user['g_id'] != LUNA_ADMIN) 
					redirect('char_manager.php?id='.$id);
					
				$stat_char = $db->query(
				"SELECT " . GAME_BASE . "players.id, " . GAME_BASE . "players.owner," . GAME_BASE . "players.online, " . GAME_BASE . "experience.exp_attack, 
				" . GAME_BASE . "experience.exp_defense, " . GAME_BASE . "experience.exp_strength, " . GAME_BASE . "experience.exp_hits, 
				" . GAME_BASE . "experience.exp_prayer, " . GAME_BASE . "experience.exp_ranged, " . GAME_BASE . "experience.exp_magic 
				FROM " . GAME_BASE . "players JOIN " . GAME_BASE . "experience ON " . GAME_BASE . "players.id = " . GAME_BASE . "experience.playerID  WHERE " . GAME_BASE . "players.id = '" . $db->escape($curr_char) . "' AND " . GAME_BASE . "players.owner = '" . $id . "'"
				);

				if($db->num_rows($stat_char) > 0) 
				{
					$grab_char = $db->fetch_assoc($stat_char);
					if($grab_char['online'] == 0) 
					{
						//$payment = $db->query("SELECT id FROM " . GAME_BASE . "invitems WHERE playerID = '" . $db->escape($grab_char['id']) . "' AND id IN (2092, 2094)");
						//if($db->num_rows($payment) > 0)
						//{
							if(isset($_POST['reset_stat'])) 
							{
								$rehash_inputs = explode("," , $_POST['reset_stat']);
								if($validskills[$rehash_inputs[0]]['name'] == $rehash_inputs[1] && $validskills[$rehash_inputs[0]]['modify'] == true) 
								{
									if($grab_char['exp_' . $rehash_inputs[1]] == 0) 
									{
											$errors[] = "This skill is already lower than the limit";
									} 
									else 
									{
										$max_stat_exp = 4469;
										switch($rehash_inputs[1]) 
										{
										case "strength":
											case "attack":	
											case "defense":
												if($rehash_inputs[1] == 'strength' && $grab_char['exp_strength'] > $max_stat_exp || $rehash_inputs[1] == 'attack' && $grab_char['exp_attack'] > $max_stat_exp || $rehash_inputs[1] == 'defense' && $grab_char['exp_defense'] > $max_stat_exp) 
												{
													$errors[] = "Sorry, but you cannot reset your " . $rehash_inputs[1] . " above level " . experience_to_level($max_stat_exp) . ".";
												} 
												else 
												{
													if($rehash_inputs[1] == 'attack') {
														$auto_calc_hits = ceil(($grab_char['exp_defense'] + $grab_char['exp_strength']) / 3) + 1154;
													} 
													else if($rehash_inputs[1] == 'defense') {
														$auto_calc_hits = ceil(($grab_char['exp_attack'] + $grab_char['exp_strength']) / 3) + 1154;
													} 
													else if($rehash_inputs[1] == 'strength'){
														$auto_calc_hits = ceil(($grab_char['exp_attack'] + $grab_char['exp_defense']) / 3) + 1154;
													}
													$convert_calc_hits = experience_to_level($auto_calc_hits);
													$db->query("UPDATE " . GAME_BASE . "experience SET exp_hits = '" . $db->escape($auto_calc_hits)  . "', exp_" . $db->escape($rehash_inputs[1]) . " = '0' WHERE playerID = '" . $db->escape($grab_char['id']) . "'");
													$db->query("UPDATE " . GAME_BASE . "curstats SET cur_hits = '" . $db->escape($convert_calc_hits) . "', cur_" . $db->escape($rehash_inputs[1]) . " = '1' WHERE playerID = '" . $db->escape($grab_char['id']) . "'");
													// Delete the sub card from inventory
													//$db->query("DELETE FROM " . GAME_BASE . "invitems WHERE playerID = '" . $db->escape($grab_char['id']) . "' AND id IN (2092, 2094) LIMIT 1");
													redirect('char_manager.php?id='.$id.'&setting=reduction&saved=true');
												}
											break;
											default:
												 $db->query("UPDATE " . GAME_BASE . "experience SET exp_" . $db->escape($rehash_inputs[1]) . " = '0' WHERE playerID = '" . $db->escape($grab_char['id']) . "'");
												 $db->query("UPDATE " . GAME_BASE . "curstats SET cur_" . $db->escape($rehash_inputs[1]) . " = '1' WHERE playerID = '" . $db->escape($grab_char['id']) . "'");	
												 // Delete the sub card from inventory
												 //$db->query("DELETE FROM " . GAME_BASE . "invitems WHERE playerID = '" . $grab_char['id'] . "' AND id IN (2092, 2094) LIMIT 1");
												 redirect('char_manager.php?id='.$id.'&setting=reduction&saved=true');
											break;
										}
									}
								}
							}
						/*} 
						else 
						{
							$errors[] = "You need to have a Gold or Premium token in your inventory.";
						}*/
					}
					else 
					{
						$errors[] = "You need to stay offline in-game to use this feature.";
					}
				} 
				else 
				{
					$errors[] = "This character does not belong to you.";
				}
			}
		break;
		case "character_renaming":
			if($view_page == 'select' && isset($_POST['character_rename']))
			{
				confirm_referrer('char_manager.php');
				
				$current_name = isset($_POST['character_name']) && strlen($_POST['character_name']) <= 12 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_POST['character_name']) ? trim($_POST['character_name']) : null;
				$new_name = isset($_POST['new_name']) && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_POST['new_name']) ? trim($_POST['new_name']) : null;
				$errors = array();
				if(empty($new_name))
				{
					$errors[] = "Please enter your new name.";
				}
				if(!preg_match("/^[a-zA-Z0-9\s]+?$/i", $new_name)) 
				{
					$errors[] = "Your character name can only contain letters, numbers, and spaces.";
				}
				if(strlen($new_name) < 2 || strlen($new_name) > 12) 
				{
					$errors[] = "Your new character name must be minimum 2 charcaters and maximum 12 characters in length.";
				}
				if (preg_match('/^Mod\s+/i', $new_name) || preg_match('/^Admin\s+/i', $new_name))
				{
					 $errors[] = "Sorry, your new name cannot contain \"Mod\" or \"Admin\" in it's username";
				}
				if(count($errors) == 0)
				{
					
					$character = $db->query("SELECT id, online, owner, banned FROM " . GAME_BASE . "players WHERE username = '" . $db->escape($current_name) . "' AND owner = '". $id ."'");
					$check = $db->fetch_assoc($character);	
					if($db->num_rows($character) > 0) 
					{
						if($check['banned'] != 0) {
							$errors[] = "Banned characters cannot use this feature.";
						} else {
							$check_availability = $db->query("SELECT id FROM " . GAME_BASE . "players WHERE username = '" . $db->escape($new_name) . "'");
							if($db->num_rows($check_availability) > 0)
							{
								$errors[] = "The name you are attempting to rename this character to already exists.";
							} 
							else 
							{
								if($check['online'] == 0) 
								{
									//$payment = $db->query("SELECT id FROM " . GAME_BASE . "invitems WHERE playerID = '" . $db->escape($check['id']) . "' AND id IN (2092, 2094)");
									//if($db->num_rows($payment) > 0)
									//{
										$db->query("UPDATE " . GAME_BASE . "players SET username='" . $db->escape($new_name) . "' WHERE id ='" . $check['id'] . "'") or error('Failed to rename player username', __FILE__, __LINE__, $db->error());
										//$db->query("UPDATE users SET jewels=jewels - ".$RENAME_PRICE." WHERE id ='" . $id . "'") or error('Failed to rename player username', __FILE__, __LINE__, $db->error());										
										$db->query("UPDATE " . GAME_BASE . "auctions SET seller_username = '" . $db->escape($new_name) . "' WHERE seller_username='" . $current_name . "'") or die('ew13');
										$db->query("UPDATE " . GAME_BASE . "friends SET friendName = '" . $db->escape($new_name) . "' WHERE friendName='" . $current_name . "'") or die('ew13');
										// Delete the sub card from inventory
										$db->query("DELETE FROM " . GAME_BASE . "invitems WHERE playerID = '" . $db->escape($check['id']) . "' AND id IN (2092, 2094) LIMIT 1");

										// Insert into name change table			
										$db->query('INSERT INTO ' . GAME_BASE . 'name_changes (playerID, owner, old_name, new_name, date) VALUES('.intval($check['id']).', '.intval($check['owner']).', \''.$db->escape($current_name).'\',  \''.$db->escape($new_name).'\', '.time().')') or error('Unable to save character name change!', __FILE__, __LINE__, $db->error());
										new_notification($id, 'char_manager.php?id='.$id.'', __('Character: '. luna_htmlspecialchars($current_name) . ' has been renamed to: '. luna_htmlspecialchars($new_name) . '!', 'luna'), 'fa-pencil');
										redirect('char_manager.php?id='.$id.'&setting=character_renaming&saved=true');
									/*} 
									else {
										$errors[] = "You need to have a gold or premium token in your inventory for the purchase.";
									}*/
								} 
								else 
								{
									$errors[] = "You need to stay logged out from the game during the renaming process.";
								}	
							}
						}
					} 
					else 
					{
						$errors[] = "Character to be renamed doesn't exist or doesn't belong to you.";
					}
				}
			}
		break;
		case "add":
			if(isset($_POST['addcharacter']) && $view_page == 'create') 
			{
				confirm_referrer('char_manager.php');
				
				$username = isset($_POST['char_name']) ? trim($_POST['char_name']) : null;
				$password_1 = isset($_POST['char_pass_1']) ? $_POST['char_pass_1'] : null;
				$password_2 = isset($_POST['char_pass_2']) ? $_POST['char_pass_2'] : null;
				$errors = array();
				if(empty($username) || empty($password_1) || empty($password_2))
				{
					$errors[] = "Please fill in every field in the registration form.";
				}
				if(!preg_match("/^[a-zA-Z0-9\s]+?$/i", $username))
				{
					$errors[] = "Your username can only contain regular letters, numbers and spaces.";
				}
				if (preg_match('/^Mod\s+/i', $username) || preg_match('/^Admin\s+/i', $username)){
				$errors[] = "Sorry, but you can not create a character that begins with \"Mod\" or \"Admin\"";
				}
				if(strlen($username) < 2 || strlen($username) > 12)
				{
					$errors[] = "Your username must be from 2 to 12 characters in length.";
				}
				if($password_1 != $password_2)
				{
					$errors[] = "Your passwords did not match.";
				}
				if(strlen($password_1) < 4 || strlen($password_1) > 16)
				{
					$errors[] = "Your password must be from 4 to 16 characters in length.";
				}
				if(!preg_match("/^[a-zA-Z0-9\s]+?$/i", $password_1))
				{
					$errors[] = "Your password can only contain regular letters, numbers and spaces.";
				}
				if(count($errors) == 0)
				{
					$check_name_in_use = $db->query("SELECT id FROM " . GAME_BASE . "players WHERE username = '" . $db->escape($username) . "'");
					$check_user_amount = $db->num_rows($db->query("SELECT id FROM " . GAME_BASE . "players WHERE owner = '" . $id . "'"));
					if($check_user_amount >= $my_character_slots)
					{
						$errors[] = "Sorry you have reached your maximum limit of in-game characters (".$my_character_slots.").";
					}
					else
					{
						if($db->num_rows($check_name_in_use) > 0)
						{
							$errors[] = "The username '" . luna_htmlspecialchars($username) . "' is already in use.";
						}
						else
						{
							// SALT + SHA512 + Secrety key here.
							$salt = random_pass(16); // 8 default?
							$password_hash = game_hmac($salt.$password_1, $HMAC_PRIVATE_KEY);
							
							$db->query("INSERT INTO " . GAME_BASE . "players (username,owner,pass,salt,creation_date,creation_ip) VALUES ('" . $db->escape($username) . "', '" . $id . "', '" . $password_hash . "', '" . $salt . "', '".(time())."', '". $_SERVER['REMOTE_ADDR'] ."');") or error('Unable to insert game character', __FILE__, __LINE__, $db->error());
							$new_uid = $db->insert_id();
							$db->query("INSERT INTO " . GAME_BASE . "curstats (playerID) VALUES ('" . $new_uid . "');") or error('Unable to insert current stats on game character', __FILE__, __LINE__, $db->error());
							$db->query("INSERT INTO " . GAME_BASE . "experience (playerID) VALUES ('" . $new_uid . "');") or error('Unable to insert experience on game character', __FILE__, __LINE__, $db->error());
							new_notification($id, 'char_manager.php?id='.$id.'', __('Adventurer! You have created a RSCLegacy character: '. luna_htmlspecialchars($username) . '!', 'luna'), 'fa-user-plus');
							redirect('char_manager.php?id='.$id.'&view=create&saved=true');
						}
					}
				}
			}
		break;
		case "delete_character":
			if ($luna_user['is_guest']) {
			header('Location: index.php');
			exit;
			}
			if (isset($_GET['key'])) {
				$key = $_GET['key'];
				$result = $db->query('SELECT * FROM '.$db->prefix.'users WHERE id='.$id) or error('Unable to fetch deletion', __FILE__, __LINE__, $db->error());
				$cur_user = $db->fetch_assoc($result);
				
				if ($key == '' || $key != $cur_user['activate_key'])
					message(__('The specified activation key was incorrect or has expired. Please re-request a new deletion. If that fails, contact the forum administrator at', 'luna').' <a href="mailto:'.luna_htmlspecialchars($luna_config['o_admin_email']).'">'.luna_htmlspecialchars($luna_config['o_admin_email']).'</a>.');
				//No donations!
                                //if($cur_user['jewels'] < 100) 
				//	message(__('You need to have 100 jewels in order for this service. You can donate for jewels at: <a href="wolfkingdom.net/donate.php">wolfkingdom.net/donate.php</a>', 'luna').'');
				//else {
					$character_to_delete = $cur_user['activate_string'];
					$character_to_delete_query = $db->query('SELECT username FROM ' . GAME_BASE . 'players WHERE id='.$character_to_delete) or error('Unable to fetch deletion extra', __FILE__, __LINE__, $db->error());
					if (!$db->num_rows($character_to_delete_query)) 
						message(__('User could not be found on your account', 'luna').'.');
				
					$cur_del_char = $db->fetch_assoc($character_to_delete_query);
					
					// DELETE CHARACTER
					$db->query("DELETE FROM " . GAME_BASE . "players WHERE id = '" . $db->escape($character_to_delete) . "'");
					$db->query("DELETE FROM " . GAME_BASE . "curstats WHERE playerID = '" . $db->escape($character_to_delete) . "'");
					$db->query("DELETE FROM " . GAME_BASE . "experience WHERE playerID = '" .  $db->escape($character_to_delete) . "'");
					$db->query("DELETE FROM " . GAME_BASE . "friends WHERE playerID = '" .  $db->escape($character_to_delete) . "'");
					$db->query("DELETE FROM " . GAME_BASE . "ignores WHERE playerID = '" .  $db->escape($character_to_delete) . "'");
					$db->query("DELETE FROM " . GAME_BASE . "invitems WHERE playerID = '" .  $db->escape($character_to_delete). "'");
					$db->query("DELETE FROM " . GAME_BASE . "logins WHERE playerID = '" .  $db->escape($character_to_delete) . "'");
					$db->query("DELETE FROM " . GAME_BASE . "bank WHERE playerID = '" .  $db->escape($character_to_delete) . "'");
					$db->query("DELETE FROM " . GAME_BASE . "player_cache WHERE playerID = '" .  $db->escape($character_to_delete) . "'");
					$db->query("DELETE FROM " . GAME_BASE . "quests WHERE playerID = '" .  $db->escape($character_to_delete) . "'");
					$db->query("DELETE FROM " . GAME_BASE . "auctions WHERE seller = '" .  $db->escape($character_to_delete) . "'");
					$db->query("DELETE FROM " . GAME_BASE . "expired_auctions WHERE playerID = '" .  $db->escape($character_to_delete) . "'");
					$db->query("DELETE FROM " . GAME_BASE . "clan WHERE leader = '" .  $db->escape($cur_del_char['username']) . "'");
					$db->query("DELETE FROM " . GAME_BASE . "clan_players WHERE username = '" .  $db->escape($cur_del_char['username']) . "'");
					
					// UPDATE THE ACTIVATION KEYS TO NULL.
                                        // No donations!
					//$db->query('UPDATE '.$db->prefix.'users SET jewels=jewels - 100, activate_string=NULL, activate_key=NULL WHERE id='.$id) or error('Unable to update user defaults', __FILE__, __LINE__, $db->error());
                                        $db->query('UPDATE '.$db->prefix.'users SET activate_string=NULL, activate_key=NULL WHERE id='.$id) or error('Unable to update user defaults', __FILE__, __LINE__, $db->error());
					message(__('Your character has been successfully deleted.', 'luna'), true);
				//}
			} else {
				if (isset($_POST['delete_verify'])) {
					require LUNA_ROOT.'include/email.php';
					$result = $db->query('SELECT id, username, last_email_sent, jewels FROM '.$db->prefix.'users WHERE email=\''.$db->escape($luna_user['email']).'\'') or error('Unable to fetch user info', __FILE__, __LINE__, $db->error());
					$char_delete_result = $db->query('SELECT id, username, banned FROM ' . GAME_BASE . 'players WHERE id='.$db->escape($curr_char).' AND owner='.$db->escape($luna_user['id']).'') or error('Unable to find player character info', __FILE__, __LINE__, $db->error());
					if (!$db->num_rows($char_delete_result)) 
						message(__('User could not be found on your account', 'luna').'.');
				
					$char_delete = $db->fetch_assoc($char_delete_result);
					if($char_delete['banned'] != 0)
						message(__('Banned characters cannot use this feature', 'luna').'.');
					
					if ($db->num_rows($result)) {
						// Load the "delete character" template
						$mail_tpl = trim(__('Subject: Delete character requested

Hello <username>,

You have requested to have a game character deleted from your forum account at <base_url>. If you did not request this or if you do not want to delete this character you should just ignore this message. Only if you visit the activation page below will confirm the deletion.

Character to be deleted: <char_delete>

To confirm the deletion of your character, please click the activation url below:
<activation_url>

--
<board_mailer> Service
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
							//This is silly.
                                                        /*if ($cur_hit['last_email_sent'] != '' && (time() - $cur_hit['last_email_sent']) < 3600 && (time() - $cur_hit['last_email_sent']) >= 0) {
								message(sprintf(__('This account has already requested a character delete in the past hour. Please wait %s minutes before requesting again.', 'luna'), intval((3600 - (time() - $cur_hit['last_email_sent'])) / 60)), true);
							}*/
                                                        
							//No donations!
							//if($cur_hit['jewels'] < 100) {
							//	message(__('This service cost 100 jewels, you can donate for more jewels at: <a href="http://wolfkingdom.net/donate.php">wolfkingdom.net/donate.php</a>', 'luna'));
							//}
							
							$activation_key = random_pass(8);

							$db->query('UPDATE '.$db->prefix.'users SET activate_string=\''.$db->escape($char_delete['id']).'\', activate_key=\''.$db->escape($activation_key).'\', last_email_sent = '.time().' WHERE id='.$cur_hit['id'])
							or error('Unable to update activation data', __FILE__, __LINE__, $db->error());
							
							// Do the user specific replacements to the template
							$cur_mail_message = str_replace('<username>', $cur_hit['username'], $mail_message);
							$cur_mail_message = str_replace('<activation_url>', get_base_url().'/char_manager.php?id='.$cur_hit['id'].'&view=select&setting=delete_character&key='.$activation_key, $cur_mail_message);
							$cur_mail_message = str_replace('<char_delete>', $char_delete['username'], $cur_mail_message);

							luna_mail($luna_user['email'], $mail_subject, $cur_mail_message);
                                                        //message(__('neat'), true);
						}
						//message(__('An email has been sent to the forum account email address with instructions on how to delete the selected character. If it does not arrive you can contact the forum administrator at', 'luna').' <a href="mailto:'.luna_htmlspecialchars($luna_config['o_admin_email']).'">'.luna_htmlspecialchars($luna_config['o_admin_email']).'</a>.', true);
					}
				}
			}
		break;
	}
} 
require load_page('header.php');
?>
<div id="wrapper" class="container">
	<div class="character">
		<?php 
		if($view_page == 'select') 
		{ 
			if($isActive) 
			{
				require load_page('character/active-character.php');
				switch($setting) 
				{
					case "change_password":
						require load_page('character/change-password.php');
					break;
					case "character_renaming":
						require load_page('character/rename-character.php');
					break;
					case "highscore":
						require load_page('character/highscore.php'); 
					break;
					case "achievements":
						require load_page('character/achievement.php'); 
					break;
					case "reduction":
						require load_page('character/stat-reduction.php');
					break;
					default:
						require load_page('character/select-character.php'); 
					break;
				}
			} 
			else 
			{
				require load_page('character/select-character.php'); 
			}
		} 
		else if($view_page == 'create') 
		{ 
			require load_page('character/create-character.php');
		}
		else 
		{ 
			echo 'Error entering character manager.';
		} ?>
	</div>
</div>
<?php
require load_page('footer.php');