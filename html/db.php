<?php
define('LUNA_ROOT', './');
require LUNA_ROOT.'include/common.php';

$id = isset($_GET['id']) ? intval($_GET['id']) : null;

$category = array('items', 'npcs', 'objects');
$selected_category = isset($_GET['category']) ? $_GET['category'] : null;

$search_term = isset($_GET['term']) && strlen($_GET['term']) <= 36 && preg_match("/^[a-zA-Z0-9\s]+?$/i", $_GET['term']) ? strtolower($_GET['term']) : null;

if($selected_category == 'items') { // On P2P Add: 'Q', 'X', 'Z'
	$index = array('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'R', 'S', 'T', 'U', 'V', 'W', 'Y');
	$selected_index = isset($_GET['index']) && in_array($_GET['index'], $index) ? luna_trim($_GET['index']) : 'A';
	
	$selected_item = isset($_GET['item']) ? luna_trim($_GET['item']) : null;
	
	if(isset($search_term) && !isset($selected_item)) {
		$list_items = $db->query('SELECT id, name, description FROM ' . GAME_BASE . 'itemdef WHERE name LIKE \'%'.$db->escape(strtolower($search_term)).'%\' AND originalItemID = -1 '.(!MEMBERS_CONTENT ? 'AND isMembersOnly = 0' : '').' ORDER BY name ASC') or error('Unable to find searched item', __FILE__, __LINE__, $db->error());
		if (!$db->num_rows($list_items))
			message(__('Sorry. We could not find the item you are looking for!', 'luna'), false, '404 Not Found');
	} else if(!isset($search_term) && !isset($selected_item)) {
		$list_items = $db->query('SELECT id, name, description FROM ' . GAME_BASE . 'itemdef WHERE name LIKE \''.$db->escape(strtolower($selected_index)).'%\' AND originalItemID = -1 '.(!MEMBERS_CONTENT ? 'AND isMembersOnly = 0' : '').' ORDER BY name ASC') or error('Unable to fetch item db', __FILE__, __LINE__, $db->error());
		
		if (!$db->num_rows($list_items))
			message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');
	} else {
		$fetch_item = $db->query('SELECT id, bankNoteID, name, description, isMembersOnly, isUntradable, isWearable, isStackable, basePrice FROM ' . GAME_BASE . 'itemdef WHERE name = \''.$db->escape(strtolower(str_replace('_',' ',$selected_item))).'\' '.(!MEMBERS_CONTENT ? 'AND isMembersOnly = 0' : '').' AND id = '.$db->escape($id).'') or error('Unable to fetch item information', __FILE__, __LINE__, $db->error());
		
		if (!$db->num_rows($fetch_item))
			message(__('The database could not load the definitions of this item, please try again.', 'luna'), false, '404 Not Found');
	}
	
} else if($selected_category == 'npcs') { // TODO check later on for: 'X' and for P2P add: '1', 'Y'
	$index = array('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'Z');
	$selected_index = isset($_GET['index']) && in_array($_GET['index'], $index) ? $_GET['index'] : 'A';
	
	$selected_npc = isset($_GET['npc']) ? luna_trim($_GET['npc']) : null;
	
	if(isset($search_term) && !isset($selected_npc)) {
		$list_npcs = $db->query('SELECT id, name, description, combatlvl FROM ' . GAME_BASE . 'npcdef WHERE name LIKE \'%'.$db->escape(strtolower($search_term)).'%\'  '.(!MEMBERS_CONTENT ? 'AND isMembers = 0' : '').' ORDER BY name ASC') or error('Unable to find searched npc', __FILE__, __LINE__, $db->error());
		
		if (!$db->num_rows($list_npcs))
			message(__('No npcs found with that search term, please try again.', 'luna'), false, '404 Not Found');
	} else if(!isset($search_term) && !isset($selected_npc)) {
		$list_npcs = $db->query('SELECT id, name, description, combatlvl FROM ' . GAME_BASE . 'npcdef WHERE name '.($selected_index == '1' ? 'regexp \''.$db->escape(strtolower('^[0-9]+')).'\'' : 'LIKE \''.$db->escape(strtolower($selected_index)).'%\'').' '.(!MEMBERS_CONTENT ? 'AND isMembers = 0' : '').' ORDER BY name ASC') or error('Unable to fetch npc db', __FILE__, __LINE__, $db->error());
		
		if (!$db->num_rows($list_npcs))
			message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');
	} else {
		$fetch_npc = $db->query('SELECT id, name, description, attack, strength, hits, defense, combatlvl, attackable, aggressive, respawntime FROM ' . GAME_BASE . 'npcdef WHERE name = \''.$db->escape(strtolower(str_replace('_',' ',$selected_npc))).'\' '.(!MEMBERS_CONTENT ? 'AND isMembers = 0' : '').' AND id = '.$db->escape($id).'') or error('Unable to fetch npc information', __FILE__, __LINE__, $db->error());
		$fetch_drop = $db->query('SELECT ' . GAME_BASE . 'npcdrops.id, ' . GAME_BASE . 'npcdrops.amount, ' . GAME_BASE . 'itemdef.name FROM ' . GAME_BASE . 'npcdrops JOIN ' . GAME_BASE . 'itemdef ON ' . GAME_BASE . 'itemdef.id = ' . GAME_BASE . 'npcdrops.id WHERE ' . GAME_BASE . 'npcdrops.npcdef_id = '.$db->escape($id).'') or error('Unable to fetch npc drops', __FILE__, __LINE__, $db->error());
	
		if (!$db->num_rows($fetch_npc))
			message(__('Could not load the information for this NPC, try another NPC found in the database.', 'luna'), false, '404 Not Found');
	}
} else {
	$total_items = $db->query('SELECT COUNT(id) FROM ' . GAME_BASE . 'itemdef WHERE originalItemID = -1'.(!MEMBERS_CONTENT ? ' AND isMembersOnly = 0' : '')) or error('Item totals could not be calculated', __FILE__, __LINE__, $db->error());
	$total_npcs = $db->query('SELECT COUNT(id) FROM ' . GAME_BASE . 'npcdef '.(!MEMBERS_CONTENT ? ' WHERE isMembers = 0' : '').'') or error('Unable to count total npcs', __FILE__, __LINE__, $db->error());
}

$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), "RSCLegacy Database");
define('LUNA_ALLOW_INDEX', 1);
require load_page('header.php');
?>
<div id="wrapper" class="container">
	<div class="character">
		<?php 
		if($selected_category != null) 
		{
			switch($selected_category) 
			{
				case "items":
					require load_page('misc_db/item_db.php');
				break;
				case "npcs":
					require load_page('misc_db/npc_db.php');
				break;
			}
		} else {
				require load_page('misc_db/default.php');
		}
		?>
	</div>
</div>
<?php
require load_page('footer.php');
