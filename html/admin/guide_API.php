<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'guide_API');

require LUNA_ROOT.'include/common.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}
require 'header.php';	

$guideID = isset($_GET['guideID']) ? $_GET['guideID'] : null;
$mode = isset($_GET['mode']) ? $_GET['mode'] : null;

$form = array(
	'title'			=> luna_trim($_POST['form']['guide_name']),
	'description'	=> luna_trim($_POST['form']['guide_desc']),
	'type'			=> intval($_POST['form']['guide_creation_type']),
	'difficulty'	=> luna_trim($_POST['form']['guide_diff']),
	'start_location' => luna_trim($_POST['form']['guide_start_location']),
	'post'    		 => luna_trim($_POST['form']['guide_post']),
	'poster'    	 => luna_trim($_POST['form']['guide_by']),
	'length'			=> luna_trim($_POST['form']['guide_length']),
	'quest_points'			=> intval($_POST['form']['quest_qp']),
	'guide_type'			=> luna_trim($_POST['form']['guide_type']),
	'reqs'			=> luna_trim($_POST['form']['guide_req']),
	'items_needed'			=> luna_trim($_POST['form']['quest_items']),
	'rewards'			=> luna_trim($_POST['form']['quest_reward'])
);

?>
<div class="content_wrapper col-sm-10">
	<div class="row title">
		<div class="small-12 columns">
			<h2 class="page-heading">Guide API - <?php echo ($mode == 'create_guide' ? 'Create Guide' : 'Edit Guide')?></h2>
		</div>
	</div>
	<div class="small-12 columns">
	<?php
	if (isset($_GET['saved']))
		if($mode == 'create_guide')
			echo '<div class="alert alert-success"><i class="fa fa-fw fa-check"></i> '.__('Guide has been created!', 'luna').'</div>';
		else 
			echo '<div class="alert alert-success"><i class="fa fa-fw fa-check"></i> '.__('Guide has been updated!', 'luna').'</div>';
	?>
		<div class="col-sm-3">
			<div class="panel-group" id="accordion">
				<div class="panel panel-primary">
						<div class="panel-heading">
							<h3 class="panel-title">Guide List <a href="guide_API.php?mode=create_guide" class="block-btn block-btn--form btn-xs pull-right">Create New Guide</a></h3>
						</div>
						<div id="collapseOne" class="panel-collapse collapse in">
						<ul class="list-group scrollable-menu scrollbar" id="ex4">
							 <?php
							$guide_query = $db->query("SELECT id, title FROM guides ORDER BY id ASC");
							if($db->num_rows($guide_query) > 0) {		
								while($pull_guides = $db->fetch_assoc($guide_query)) {
									echo  
									'<li class="list-group-item">
										'. $pull_guides['id'] . '.&nbsp;' .'<a href="?mode=edit&guideID='. $pull_guides['id'] .'" class="list-group-item-text"><strong>' . $pull_guides['title'] . '</strong>
										</a>
									</li>';
								}
							} else {
								echo '<li class="list-group-item"><p class="list-group-item-text">No guides written yet.</p></li>';
							}
							?>
						</ul>
					</div>
				</div>
			</div>
		</div>
		<?php 
		switch($mode) {
			case "create_guide":
			if (isset($_POST['form_sent']) && isset($_POST['create_guide']))
			{
				confirm_referrer('admin/guide_API.php?mode=create_guide', $lang['Bad HTTP Referer message']);
				
				// left side = database column name, right side = form name.

				if (empty($form))
					message($lang['Bad request'], false, '404 Not Found');

				$db->query('INSERT INTO guides (title, description, type, difficulty, start_location, post, poster, length, quest_points, guide_type, reqs, items_needed, rewards) VALUES(\''.$db->escape($form['title']).'\', \''.$db->escape($form['description']).'\', '.$form['type'].', \''.$db->escape($form['difficulty']).'\', \''.$db->escape($form['start_location']).'\', \''.$db->escape($form['post']).'\', \''.$db->escape($form['poster']).'\', \''.$db->escape($form['length']).'\', '.$form['quest_points'].', \''.$db->escape($form['guide_type']).'\', \''.$db->escape($form['reqs']).'\', \''.$db->escape($form['items_needed']).'\', \''.$db->escape($form['rewards']).'\')') or error('Unable to insert guide template', __FILE__, __LINE__, $db->error());
			
				redirect('admin/guide_API.php?mode=create_guide&saved=true');
			} else {
			?>
			<div id="guide_template" class="panel col-sm-9">
			<form id="quest_template" method="post" action="guide_API.php?mode=create_guide">
				<div class="panel-body">
				<input type="hidden" name="form_sent" value="1" />
				<div class="alert alert-info" role="alert"><p>Create your guide by using the API inputs below.</p></div>
				<fieldset>
				<div class="form-group">
					<div class="input-group input-group-sm">
						<span class="input-group-addon" id="guideAddons">Guide Title:</span>
						<input type="text" name="form[guide_name]" class="form-control" id="basic-url" maxlength="80" required="required">
					</div>
					<div class="input-group input-group-sm">
						<span class="input-group-addon" id="guideAddons">Guide Description:</span>
						<input type="text" name="form[guide_desc]" class="form-control" maxlength="255" required="required">
					</div>
					<div class="input-group input-group-sm">
						<span class="input-group-addon" id="guideAddons">Guide Type:</span>
						 <select id="guide_create_type" name="form[guide_type]" class="selectpicker form-control" data-live-search="true" title="Select a guide type">
							<option value="0">Quest</option>
							<option value="1">Game Play</option>
							<option value="2">Game Features</option>
						</select>
					</div>
					<div class="input-group input-group-sm">
						<span class="input-group-addon" id="guideAddons">Start Location:</span>
							<input type="text" name="form[guide_start_location]" class="form-control" maxlength="255">
					</div>
					<div class="input-group input-group-sm">
					<span class="input-group-addon" id="guideAddons">Difficulty:</span>
						 <select style="font-family: 'FontAwesome', Arial;" id="guide_diff" name="form[guide_diff]" class="selectpicker form-control" data-live-search="true" title="Select a guide difficulty">
							<optgroup label="Guide Difficulty Star Icons">
							<option value="<i class='fa fa-star'></i>">&#xf005;</option>
							<option value="<i class='fa fa-star'></i><i class='fa fa-star'></i>">&#xf005;&#xf005;</option>
							<option value="<i class='fa fa-star'></i><i class='fa fa-star'></i><i class='fa fa-star'></i>">&#xf005;&#xf005;&#xf005;</option>
							<option value="<i class='fa fa-star'></i><i class='fa fa-star'></i><i class='fa fa-star'></i><i class='fa fa-star'></i>">&#xf005;&#xf005;&#xf005;&#xf005;</option>
							<option value="<i class='fa fa-star'></i><i class='fa fa-star'></i><i class='fa fa-star'></i><i class='fa fa-star'></i><i class='fa fa-star'></i>">&#xf005;&#xf005;&#xf005;&#xf005;&#xf005;</option>
						</select>
						<span class="input-group-addon" id="guideAddons">Length:</span>
						 <select id="guide_len" name="form[guide_length]" class="selectpicker form-control" data-live-search="true" title="Select guide length">
							<optgroup label="Length of guide">
							<option value="Short">Short</option>
							<option value="Medium">Medium</option>
							<option value="Long">Long</option>
						</select>
						<span class="input-group-addon" id="guideAddons">Free/Members</span>
							<select id="guide_type" name="form[guide_type]" class="selectpicker form-control" data-live-search="true" title="Select guide length">
								<optgroup label="Guide Availability (Free / Members)">
								<option value="Free">Free</option>
								<option value="Members">Members</option>
							</select>
						<span class="input-group-addon" id="guideAddons">Quest Points</span>
							<input type="text" name="form[quest_qp]" class="form-control" maxlength="2">
					</div>
					</div>
					<div id="guide_requirements">
							<div class="panel panel-primary">
								<div class="panel-heading">
									<h3 class="panel-title">Requirements</h3>
								</div>
								<div class="panel-body">
									<div class="form-group">
										<div class="alert alert-danger" role="alert"><p>Add the requirements needed for quest (IF ANY)</p></div>
										<textarea class="form-control" name="form[guide_req]" rows="3" placeholder="Enter requirements..."></textarea>
									</div>
								</div>
							</div>
					</div>
					<div id="guide_items">
							<div class="panel panel-primary">
								<div class="panel-heading">
									<h3 class="panel-title">Items Needed</h3>
								</div>
								<div class="panel-body">
									<div class="form-group">
										<div class="alert alert-info" role="alert"><p>Add items needed - Write in item name (IF ANY)</p></div>
										<textarea class="form-control" name="form[quest_items]" rows="5" placeholder="Enter quest item names (or leave blank)..."></textarea>
									</div>
								</div>
							</div>
					</div>
					<div id="guide_presentation">
						<div class="panel panel-primary">
							<div class="panel-heading">
								<h3 class="panel-title">Guide Post</h3>
							</div>
							<div class="panel-body">
								<div class="form-group">
									<div class="alert alert-success" role="alert"><p>This is the textarea where you will present your entire guide</p></div>
									<?php include "toolbar.php"; ?>
									<textarea id="comment_field" class="form-control" name="form[guide_post]" rows="10" placeholder="Enter your guide..." required="required"></textarea>
								</div>
							</div>
						</div>
					</div>
					<div id="quest_reward">
						<div class="panel panel-primary">
							<div class="panel-heading">
								<h3 class="panel-title">Rewards</h3>
							</div>
							<div class="panel-body">
								<div class="form-group">
									<div class="alert alert-info" role="alert"><p>Add rewards - (IF ANY)</p></div>
									<textarea class="form-control" name="form[quest_reward]" rows="5" placeholder="Enter reward in text (or leave blank)..."></textarea>
								</div>
							</div>
						</div>
						<div class="input-group input-group-sm col-sm-5">
							<span class="input-group-addon" id="guideAddons">Made By:</span>
							<input type="text" name="form[guide_by]" class="form-control" id="basic-url" maxlength="12" placeholder="<?php echo $luna_user['username'] ?>" required="required">
						</div>
					</div>
					<button class="block-btn block-btn--form" name="create_guide" type="submit">Submit Guide</button>
				</fieldset>
				</div>
				</form>
			</div>
			<?php
			}
			break;
			case "edit":
			if (isset($_POST['form_sent']) && isset($_POST['edit_guide']))
			{
				confirm_referrer('admin/guide_API.php?mode=edit&guideID='.$guideID.'', $lang['Bad HTTP Referer message']);
				
				$temp = array();
				foreach ($form as $key => $input) {
					$value = ($input !== '') ? '\'' . $db->escape($input) . '\'' : 'NULL';

					$temp[] = $key . '=' . $value;
					echo $key.'='.$value;
				}

				if (empty($temp))
					message($lang['Bad request'], false, '404 Not Found');
				
				$db->query('UPDATE guides SET ' . implode(',', $temp) . ' WHERE id=' . $guideID) or error('Unable to update guide', __FILE__, __LINE__, $db->error());

				redirect('admin/guide_API.php?mode=edit&guideID='.$guideID.'&saved=true');
			} else {
				
				$fetch_guide = $db->query('SELECT * FROM `guides` WHERE `id`='.$guideID.'');
				$guide = $db->fetch_assoc($fetch_guide);
				?>
				<div id="guide_template" class="panel col-sm-9">
				<form id="quest_template" method="post" action="guide_API.php?mode=edit&guideID=<?php echo $guide['id'];?>">
					<div class="panel-body">
					<input type="hidden" name="form_sent" value="1" />
					<div class="alert alert-warning" role="alert"><p>You are in edit mode for <strong><?php echo $guide['title']; ?></strong>, click "Update Guide" button at the bottom of the page when done.</p></div>
					<div class="alert alert-info" role="alert"><p>Create your guide by using the API inputs below.</p></div>
					<fieldset>
					<div class="form-group">
						<div class="input-group input-group-sm">
							<span class="input-group-addon" id="guideAddons">Guide Title:</span>
							<input type="text" name="form[guide_name]" class="form-control" id="basic-url" maxlength="80" value="<?php echo $guide['title']; ?>">
						</div>
						<div class="input-group input-group-sm">
							<span class="input-group-addon" id="guideAddons">Guide Description:</span>
							<input type="text" name="form[guide_desc]" class="form-control" maxlength="255" value="<?php echo $guide['description']; ?>">
						</div>
						<div class="input-group input-group-sm">
							<span class="input-group-addon" id="guideAddons">Guide Type:</span>
							 <select id="guide_type" name="form[guide_creation_type]" class="selectpicker form-control" title="Select a guide type">
								<option value="0" <?php echo ($guide['type'] == 0 ? 'selected="selected"' : '')?>>Quest</option>
								<option value="1" <?php echo ($guide['type'] == 1 ? 'selected="selected"' : '')?>>Game Play</option>
								<option value="2" <?php echo ($guide['type'] == 2 ? 'selected="selected"' : '')?>>Game Features</option>
							</select>
						</div>
						<div class="input-group input-group-sm">
							<span class="input-group-addon" id="guideAddons">Start Location:</span>
								<input type="text" name="form[guide_start_location]" class="form-control" maxlength="255" value="<?php echo $guide['start_location']; ?>">
						</div>
						<div class="input-group input-group-sm">
							<span class="input-group-addon" id="guideAddons">Difficulty:</span>
							 <select style="font-family: 'FontAwesome', Arial;" id="guide_diff" name="form[guide_diff]" class="selectpicker form-control" title="Select a guide difficulty">
								<optgroup label="Guide Difficulty Star Icons">
								<option value="<i class='fa fa-star'></i>" <?php echo ($guide['difficulty'] == "<i class='fa fa-star'></i>" ? "selected='selected'" : "")?>>&#xf005;</option>
								<option value="<i class='fa fa-star'></i><i class='fa fa-star'></i>" <?php echo ($guide['difficulty'] == "<i class='fa fa-star'></i><i class='fa fa-star'></i>" ? "selected='selected'" : "")?>>&#xf005;&#xf005;</option>
								<option value="<i class='fa fa-star'></i><i class='fa fa-star'></i><i class='fa fa-star'></i>" <?php echo ($guide['difficulty'] == "<i class='fa fa-star'></i><i class='fa fa-star'></i><i class='fa fa-star'></i>" ? "selected='selected'" : "")?>>&#xf005;&#xf005;&#xf005;</option>
								<option value="<i class='fa fa-star'></i><i class='fa fa-star'></i><i class='fa fa-star'></i><i class='fa fa-star'></i>" <?php echo ($guide['difficulty'] == "<i class='fa fa-star'></i><i class='fa fa-star'></i><i class='fa fa-star'></i><i class='fa fa-star'></i>" ? "selected='selected'" : "")?>>&#xf005;&#xf005;&#xf005;&#xf005;</option>
								<option value="<i class='fa fa-star'></i><i class='fa fa-star'></i><i class='fa fa-star'></i><i class='fa fa-star'></i><i class='fa fa-star'></i>" <?php echo ($guide['difficulty'] == "<i class='fa fa-star'></i><i class='fa fa-star'></i><i class='fa fa-star'></i><i class='fa fa-star'></i><i class='fa fa-star'></i>" ? "selected='selected'" : "")?>>&#xf005;&#xf005;&#xf005;&#xf005;&#xf005;</option>
							</select>
							<span class="input-group-addon" id="guideAddons">Length:</span>
							 <select id="guide_len" name="form[guide_length]" class="selectpicker form-control" title="Select guide length">
								<optgroup label="Length of guide">
								<option value="Short" <?php echo ($guide['length'] == 'Short' ? 'selected="selected"' : '')?>>Short</option>
								<option value="Medium" <?php echo ($guide['length'] == 'Medium' ? 'selected="selected"' : '')?>>Medium</option>
								<option value="Long" <?php echo ($guide['length'] == 'Long' ? 'selected="selected"' : '')?>>Long</option>
							</select>
							<span class="input-group-addon" id="guideAddons">Free/Members</span>
								<select id="guide_type" name="form[guide_type]" class="selectpicker form-control" title="Select guide length">
									<optgroup label="Guide Availability (Free / Members)">
									<option value="Free" <?php echo ($guide['guide_type'] == 'Free' ? 'selected="selected"' : '')?>>Free</option>
									<option value="Members" <?php echo ($guide['guide_type'] == 'Members' ? 'selected="selected"' : '')?>>Members</option>
								</select>
							<span class="input-group-addon" id="guideAddons">Quest Points</span>
								<input type="text" name="form[quest_qp]" class="form-control" maxlength="2" value="<?php echo $guide['quest_points']; ?>">
						</div>
						</div>
						<div id="guide_requirements">
							<div class="panel panel-primary">
								<div class="panel-heading">
									<h3 class="panel-title">Requirements</h3>
								</div>
								<div class="panel-body">
									<div class="form-group">
										<div class="alert alert-danger" role="alert"><p>Add the requirements needed for quest (IF ANY)</p></div>
										<textarea class="form-control" name="form[guide_req]" rows="3" placeholder="Enter requirements (or leave blank)..."><?php echo ($guide['reqs'] != '') ? $guide['reqs'] : "" ?></textarea>
									</div>
								</div>
							</div>
						</div>
						<div id="guide_items">
							<div class="panel panel-primary">
								<div class="panel-heading">
									<h3 class="panel-title">Items Needed</h3>
								</div>
								<div class="panel-body">
									<div class="form-group">
										<div class="alert alert-info" role="alert"><p>Add items needed - Write in item name (IF ANY)</p></div>
										<textarea class="form-control" name="form[quest_items]" rows="5" placeholder="Enter quest item names (or leave blank)..."><?php echo ($guide['items_needed'] != '') ? $guide['items_needed'] : "" ?></textarea>
									</div>
								</div>
							</div>
						</div>
						<div id="guide_presentation">
							<div class="panel panel-primary">
								<div class="panel-heading">
									<h3 class="panel-title">Guide Post</h3>
								</div>
								<div class="panel-body">
									<div class="form-group">
										<div class="alert alert-success" role="alert"><p>This is the textarea where you will present your entire guide</p></div>
										<?php include "toolbar.php"; ?>
										<textarea id="comment_field" class="form-control" name="form[guide_post]" rows="10" placeholder="Enter your guide..."><?php echo ($guide['post'] != '') ? $guide['post'] : "" ?></textarea>
									</div>
								</div>
							</div>
						</div>
						<div id="quest_reward">
							<div class="panel panel-primary">
								<div class="panel-heading">
									<h3 class="panel-title">Rewards</h3>
								</div>
								<div class="panel-body">
									<div class="form-group">
										<div class="alert alert-info" role="alert"><p>Add rewards - (IF ANY)</p></div>
										<textarea class="form-control" name="form[quest_reward]" rows="5" placeholder="Enter reward in text (or leave blank)..."><?php echo ($guide['rewards'] != '') ? $guide['rewards'] : "" ?></textarea>
									</div>
								</div>
							</div>
							<div class="input-group input-group-sm col-sm-5">
								<span class="input-group-addon" id="guideAddons">Made By: </span>
								<input type="text" name="form[guide_by]" class="form-control" id="basic-url" maxlength="12" value="<?php echo $guide['poster'] ?>">
							</div>
						</div>
						<button class="block-btn block-btn--form" name="edit_guide" type="submit">Update Guide</button>
					</fieldset>
					</div>
					</form>
				</div>
				<?php
			}
			break;
		}
		?>
	</div>
</div>
<?php
require 'footer.php';