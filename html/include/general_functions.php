<?php

/*
 * Copyright (C) 2013-2016 Luna
 * License: http://opensource.org/licenses/MIT MIT
 */

define("skills", "Overall,Attack,Defense,Strength,Hits,Ranged,Prayer,Magic,Cooking,Woodcut,Fletching,Fishing,Firemaking,Crafting,Smithing,Mining,Herblaw,Agility,Thieving,KD");
function questNameById($questID) {
     $questNames = array
    (
    'Black Knights\' Fortress',
    'Cook\'s Assistant',
    'Demon Slayer',
    'Doric\'s Quest',
    'The restless ghost',
    'Goblin diplomacy',
    'Ernest the chicken',
    'Imp catcher',
    'Pirate\'s treasure',
    'Prince Ali rescue',
    'Romeo & Juliet',
    'Sheep shearer',
    'Shield of Arrav',
    'The Knight\'s sword',
    'Vampire slayer',
    'Witch\'s potion',
    'Dragon slayer',
    'Witch\'s house (members)',
    'Lost City (members)',
    'Hero\'s quest (members)',
    'Druidic ritual (members)',
	'Merlin\'s crystal (members)',
	'Scorpion catcher (members)',
	'Family crest (members)',
    'Tribal totem (members)',
	'Fishing contest (members)',
    'Monk\'s friend (members)',
	'Temple of Ikov (members)',
    'Clock tower (members)',
	'The Holy Grail (members)',
    'Fight Arena (members)',
	'Tree Gnome Village (members)',
    'The Hazeel Cult (members)',
	'Sheep Herder (members)',
    'Plague City (members)',
	'Sea Slug (members)',
    'Waterfall Quest (members)',
	'Biohazard (members)',
    'Jungle potion (members)',
	'Grand tree (members)',
    'Shilo village (members)',
	'Underground pass (members)',
    'Observatory quest (members)',
	'Tourist trap (members)',
    'Watchtower (members)',
	'Dwarf Cannon (members)',
    'Murder Mystery (members)',
	'Digsite (members)',
    'Gertrude\'s Cat (members)',
	'Legend\'s Quest (members)'
    );
    foreach($questNames as $questName) {
        $questName = $questNames[$questID];
    }
    return $questName;
}

function checkStatus($ip, $port) {
	if(!$sock = @fsockopen($ip, "$port", $num, $error, 5)) {
		echo('<font style="color: red;">Offline</font>');
	} else {
		echo('<font style="color: green;">Online</font>');
	}
}

function playersOnline() {
	global $db;
	$getPlayersOnline = $db->query("SELECT sum(online) FROM ". GAME_BASE ."players WHERE online = '1'");
	$countPlayers = $db->result($getPlayersOnline);
	return number_format($countPlayers * 1.12);
}

function totalGameCharacters() {
	global $db;
	$game_accounts = $db->query("SELECT COUNT(*) FROM ". GAME_BASE ."players");			
	$countCharacters = $db->result($game_accounts);
	return number_format($countCharacters);
}
function newRegistrationsToday() {
	global $db;
	$registrations_today = $db->query("SELECT COUNT(*) FROM users WHERE registered >= '".strtotime(date('Y-m-d', time()). '00:00:00')."'");
	$countRegistrations = $db->result($registrations_today);
	return number_format($countRegistrations);
}

function isValidSkill($skill) {
		$skills = explode(',', skills);
		foreach ($skills as $sk) {
			if (strtolower($sk) == strtolower($skill))
				return true;
		}
		return false;
}

$exps = array(83, 174, 276, 388, 512, 650, 801, 969, 1154, 1358, 1584, 1833, 2107, 2411, 2746, 3115, 3523, 3973, 4470, 5018, 5624, 6291, 7028, 7842, 8740, 9730, 10824, 12031, 13363, 14833, 16456, 18247, 20224, 22406, 24815, 27473, 30408, 33648, 37224, 41171, 45529, 50339, 55649, 61512, 67983, 75127, 83014, 91721, 101333, 111945, 123660, 136594, 150872, 166636, 184040, 203254, 224466, 247886, 273742, 302288, 333804, 368599, 407015, 449428, 496254, 547953, 605032, 668051, 737627, 814445, 899257, 992895, 1096278, 1210421, 1336443, 1475581, 1629200, 1798808, 1986068, 2192818, 2421087, 2673114,2951373, 3258594, 3597792, 3972294, 4385776, 4842295, 5346332, 5902831, 6517253, 7195629, 7944614, 8771558, 9684577, 10692629, 11805606, 13034431, 14391160);
$skills = array("attack", "defense", "strength",
			"hits", "ranged", "prayer", "magic", "cooking", "woodcut",
			"fletching", "fishing", "firemaking", "crafting", "smithing",
			"mining", "herblaw", "agility", "thieving");
			
function experience_to_level($exp) {
	global $exps;

	for($level = 0;$level < 98;$level++) {
		if($exp >= $exps[$level]) {
			continue;
		}
		return ($level + 1);
	}
	return 99;
}

function getSkills() {
	return explode(",", skills);
}
 
 
function sec2view($seconds)
{
	/**
	* Convert number of seconds into years, days, hours, minutes and seconds
	* and return an string containing those values
	*
	* @param integer $seconds Number of seconds to parse
	* @return string
	*/

	$y = floor($seconds / (86400*365.25));
	$d = floor(($seconds - ($y*(86400*365.25))) / 86400);
	$h = gmdate('H', $seconds);
	$m = gmdate('i', $seconds);
	$s = gmdate('s', $seconds);
	
	if($s <= 0) 
		return '--';

	$string = '';

	if($d > 0)
	{
	$dw = $d > 1 ? ' days ' : ' day ';
	$string .= intval($d) . $dw;
	}

	if($h > 0)
	{
	$hw = $h > 1 ? ' hours ' : ' hour ';
	$string .= intval($h) . $hw;
	}
	if($d < 1) {
		if($m > 0)
		{
		$mw = $m > 1 ? ' minutes ' : ' minute ';
		$string .= intval($m) . $mw;
		}
	}
	if($h < 1) {
		if($s > 0)
		{
		$sw = $s > 1 ? ' seconds ' : ' second ';
		$string .= intval($s) . $sw;
		}
	}


return preg_replace('/\s+/',' ',$string);
}
 
//
// Add a notification
//
function new_notification($user, $link, $message, $icon) {
	global $db;
	
	$now = time();
	
	$db->query('INSERT INTO '.$db->prefix.'notifications (user_id, message, icon, link, time) VALUES('.$user.', \''.$message.'\', \''.$icon.'\', \''.$link.'\', '.$now.')') or error('Unable to add new notification', __FILE__, __LINE__, $db->error());

}

//
// Forum user color ranks and images
//
function user_append_rank($string, $g_id = null) {
    if (isset($g_id)) {
        return '<span class="u_group_' . $g_id . '">' . $string . '</span>';
    } else
        return '<span class="u_group_de' . $g_id . '">' . $string . '</span>';
}

function user_append_rank_time($string, $g_id = null, $subType) {
	$ADMIN = 1;
	$MOD = 2;
	$GLOBAL_MOD = 11;
	$DEVELOPER = 12;
	$COMMUNITY_MANAGER = 13;
	$EVENT_MOD = 14;
	$MEMBER = 4;
	$SUBSCRIBER = 9;
	$GOLD = 1;
	$PREMIUM = 2;
	
	if($subType == $GOLD && ($g_id != $ADMIN && $g_id != $MOD && $g_id != $GLOBAL_MOD && $g_id != $DEVELOPER && $g_id != $COMMUNITY_MANAGER)) {
		return '<span class="u_group_9">' . $string . '</span>';
	} else if($subType == $PREMIUM && ($g_id != $ADMIN && $g_id != $MOD && $g_id != $GLOBAL_MOD && $g_id != $DEVELOPER && $g_id != $COMMUNITY_MANAGER)) {
		return '<span class="u_group_10">' . $string . '</span>';
	} else {
		if (isset($g_id) && ($g_id == $ADMIN || $g_id == $MOD || $g_id == $GLOBAL_MOD || $g_id == $DEVELOPER || $g_id == $COMMUNITY_MANAGER)) {
			return '<span class="u_group_' . $g_id . '">' . $string . '</span>';
		} else {
			return $string;
		}
	}
}

function required_fields() {
	global $required_fields;

	if (isset($required_fields)) {
	// Output JavaScript to validate form (make sure required fields are filled out)

?>
	<script type="text/javascript">
	/* <![CDATA[ */
	function process_form(the_form) {
		var required_fields = {
<?php
		// Output a JavaScript object with localised field names
		$tpl_temp = count($required_fields);
		foreach ($required_fields as $elem_orig => $elem_trans) {
			echo "\t\t\"".$elem_orig.'": "'.addslashes(str_replace('&#160;', ' ', $elem_trans));
			if (--$tpl_temp) echo "\",\n";
			else echo "\"\n\t};\n";
		}
?>
		if (document.all || document.getElementById) {
			for (var i = 0; i < the_form.length; ++i) {
				var elem = the_form.elements[i];
				if (elem.name && required_fields[elem.name] && !elem.value && elem.type && (/^(?:text(?:area)?|password|file)$/i.test(elem.type))) {
					alert('"' + required_fields[elem.name] + '" <?php _e('is a required field in this form.', 'luna') ?>');
					elem.focus();
					return false;
				}
			}
		}
		return true;
	}
	/* ]]> */
	</script>
<?php

	}
}

function check_url() {
	$redirect_url = 'http://'.$_SERVER['HTTP_HOST'].$_SERVER['REQUEST_URI'];

	return $redirect_url;
}
