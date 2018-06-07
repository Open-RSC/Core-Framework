<?php
define('LUNA_ROOT', './');
require LUNA_ROOT.'include/common.php';
require LUNA_ROOT.'include/parser.php';

$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), "Guides");
define('LUNA_ALLOW_INDEX', 1);
require load_page('header.php');

$id = isset($_GET['id']) ? intval($_GET['id']) : null;

$category = array('quest_db', 'game_guide');
$selected_page = isset($_GET['m']) && in_array($_GET['m'], $category) ? $_GET['m'] : NULL;
?>
<div id="wrapper" class="container">
	<div class="character">
	<?php 
	switch($selected_page) {
		case "quest_db": 
			if(isset($id)) {
				$load_quest = $db->query("SELECT title, description, difficulty, length, quest_points, start_location, poster, post, reqs, items_needed, rewards FROM guides WHERE id = '".$db->escape($id)."'" . (!MEMBERS_CONTENT ? "AND guide_type = 'Free'" : ""));
				$result = $db->fetch_assoc($load_quest);
				require load_page('guides/view_quest.php');
			} else {
				$find_quests = $db->query("SELECT id, title, difficulty, length, quest_points, guide_type FROM guides WHERE type = '0'" . (!MEMBERS_CONTENT ? "AND guide_type = 'Free'" : ""));
				require load_page('guides/quest_list.php');
			}
		break;
		case "game_guide":
			$find_gameplay_guides = $db->query("SELECT title, post FROM guides WHERE type = '1'");
			$find_gamefeature_guides = $db->query("SELECT title, post FROM guides WHERE type = '2'");
			require load_page('guides/game_guides.php');
		break;
		default:
			require load_page('guides/default.php');
		break;
	}
	?>
	</div>
</div>
<?php
require load_page('footer.php');
