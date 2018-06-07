<?php
/*
 * Created by Imposter 2016-07-07.
 */
define('LUNA_ROOT', dirname(__FILE__).'/');
require LUNA_ROOT.'include/common.php';

if ($luna_user['g_read_board'] == '0')
	message(__('You do not have permission to view this page.', 'luna'), false, '403 Forbidden');

$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), __('Calculators', 'luna'));
define('LUNA_ACTIVE_PAGE', 'calculators');
define('LUNA_ALLOW_INDEX', 1);
require load_page('header.php');

$calc_valid_skills = array("attack", "defense", "strength", "hits", "ranged", "prayer", "magic");

$s_skills = array(
			"prayer", "magic", "cooking", "woodcut",
			"fletching", "fishing", "firemaking", "crafting", "smithing",
			"mining", "herblaw", "agility", "thieving");

$curr_skill = isset($_GET['skill']) && in_array($_GET['skill'], $s_skills) || isset($_GET['skill']) && in_array($_GET['skill'], $s_skills) ? $_GET['skill'] : 'prayer';
?>
<body onload="pageLoaded()">
<div id="wrapper" class="container">
	<div class="content character">
		<div class="panel panel-default">
			<div class="content-header content-header--highlight">
				<h2 class="content-header-title">RSCLegacy Calculators</h2>
			</div>
			<div class="embended-info" id="calcInfo">
				<p>
					<i class="fa fa-info-circle" aria-hidden="true"></i> Game calculators provide players with useful tools to help plan how they play RSCLegacy.
				<ul>
					<li>The Combat calculator can calculate stats for your character.</li>
					<li>Our Skill calculators can determine the amount of actions you need to perform in order achieve a goal level or experience.</li>
					<li>Our Max Hit calculator will determine if you are primarly a Ranger or a Fighter and numerate your combat level for your desired stats.</li>
				</ul>
				</p>
			</div>
			<div class="embended-add">
				<p><span><strong><i class="fa fa-arrow-down" aria-hidden="true"></i> Select Calculator</span></strong></p>
			</div>
			<div class="panel-body">
				<div id="calculatorContent">
					<ul class="nav nav-tabs" id="calculatorTab">
						<li class="col-sm-4 active"><a data-target="#skillCalc" data-toggle="tab">Skill</a></li>
						<li class="col-sm-4"><a data-target="#combat" data-toggle="tab">Combat</a></li>
						<li class="col-sm-4"><a data-target="#maxHit" data-toggle="tab">Max Hit</a></li>
					</ul>
					<div class="tab-content">
						<div class="tab-pane active" id="skillCalc">
							<?php require load_page('calculator/skill.php'); ?>
							<script src="vendor/js/bundle.js"></script>
						</div>
						<div class="tab-pane" id="combat">
							<?php require load_page('calculator/combat.php'); ?>
						</div>
						<div class="tab-pane" id="maxHit">
							<?php require load_page('calculator/maxHit.php'); ?>
						</div>
						
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="vendor/js/calc/expTable.js"></script>
<script type="text/javascript" src="vendor/js/calc/firemaking.js"></script>
<script type="text/javascript" src="vendor/js/calc/combat.js"></script>
<script type="text/javascript" src="vendor/js/calc/maxHit.js"></script>
<script type="text/javascript" src="vendor/js/entrypoint.js"></script>
</body>
<?php
require load_page('footer.php');
