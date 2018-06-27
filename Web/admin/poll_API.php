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

if(isset($_POST['start_poll'])) {
	confirm_referrer('admin/poll_API.php');
	$poll_question = isset($_POST['question']) ? $_POST['question'] : null;
	$poll_option_a = isset($_POST['option_a']) ? $_POST['option_a'] : null;
	$poll_option_b = isset($_POST['option_b']) ? $_POST['option_b'] : null;
	$poll_option_c = isset($_POST['option_c']) ? $_POST['option_c'] : null;
	$poll_option_d = isset($_POST['option_d']) ? $_POST['option_d'] : null;
	$return_error = array();
	if(empty($poll_question)){
		$return_error[] = "You must set a question."; 
	}
	if(strlen($poll_question) > 140){
		$return_error[] = "Your poll question may not excede 140 characters, please shorten it.";
	}
	if(empty($poll_option_a) || empty($poll_option_b)){
		$return_error[] = "Your poll must have at least 2 valid options, please set option 1 and option 2.";
	}
	if(strlen($poll_option_a) > 140 || strlen($poll_option_b) > 140){
		$return_error[] = "Your poll options may not excede 140 characters, please shorten it.";
	}
	if(!empty($poll_option_d) && empty($poll_option_c)){
		$return_error[] = "Please fill out the poll options in order.";
	}
	if(isset($poll_option_c) && strlen($poll_option_c) > 140){
		$return_error[] = "Poll option 3 may not excede 140 characters, please shorten it.";
	}
	if(isset($poll_option_d) && strlen($poll_option_d) > 140){
		$return_error[] = "Poll option 4 may not excede 140 characters, please shorten it.";
	}
	if(count($return_error) > 0){
		echo "<div class='alert alert-danger' role='alert'><strong>Please make the follow corrections to your poll!</strong>";
		$i=1;
		foreach($return_error as $error){
			echo "<p>" .$i . ": " . $error . "</p>";
		++$i;
		}
		echo "</div>";
	} else {
		$poll_question = htmlspecialchars($poll_question);
		$poll_option_a = htmlspecialchars($poll_option_a);
		$poll_option_b = htmlspecialchars($poll_option_b);
		$poll_option_c = !empty($poll_option_c) ? htmlspecialchars($poll_option_c) : 0;
		$poll_option_d = !empty($poll_option_d) ? htmlspecialchars($poll_option_d) : 0;
		$add_poll = $db->query("INSERT INTO polls (started_by, started_when, question, option_1, option_2, option_3, option_4) VALUES ('" . $luna_user['id'] . "', '" . (time()) . "', '" . $db->escape($poll_question) . "', '" . $db->escape($poll_option_a) . "', '" . $db->escape($poll_option_b) . "', '" . $db->escape($poll_option_c) . "', '" . $db->escape($poll_option_d) . "');") or die('lol');
		redirect('admin/poll_API.php?saved=true');
	}
}

if($luna_user['group_id'] == 1 && isset($_POST['end_poll_id'])) {
	confirm_referrer('admin/poll_API.php');
	$active_poll = $db->query("SELECT id, poll_closed FROM polls WHERE id = '".intval($_POST['end_poll_id'])."'");
	$poll_info = $db->fetch_assoc($active_poll);
	$set_poll_this = $poll_info['poll_closed'] == 0 ? 1 : 0;
	$db->query("UPDATE polls SET poll_closed = '" . intval($set_poll_this) . "' WHERE id = '" . intval($poll_info['id']) . "'");
	redirect('admin/poll_API.php');
}

require 'header.php';	

?>
<div class="content_wrapper col-sm-10">
	<div class="row title">
		<div class="small-12 columns">
			<h2 class="page-heading">Poll API</h2>
		</div>
	</div>
	<div class="small-12 columns">
	<?php
	if (isset($_GET['saved']))
		echo '<div class="alert alert-success"><i class="fa fa-fw fa-check"></i> '.__('A poll has been created!', 'luna').'</div>';
	?>
	<div class='panel panel-default'>
		<div class='panel-heading'>
			<h3 class='panel-title'>Create a new poll</h3>
		</div>

		<form method='post' action='poll_API.php' class="form-horizontal">
			<div class='panel-body'>
				<div class="form-group">
					<label class="col-sm-3 control-label">Question</label>
					<div class="col-sm-9">
						<input type="text" class="form-control" name="question" size="50" maxlength="255" value="">
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Option 1</label>
					<div class="col-sm-9">
						<input type="text" class="form-control" name="option_a" size="50" maxlength="255" value="">
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Option 2</label>
					<div class="col-sm-9">
						<input type="text" class="form-control" name="option_b" size="50" maxlength="255" value="">
					</div>
				</div>
				<small>Leave additional option fields blank if they are not needed.</small>
				<div class="form-group">
					<label class="col-sm-3 control-label">Option 3</label>
					<div class="col-sm-9">
						<input type="text" class="form-control" name="option_c" size="50" maxlength="255" value="">
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-3 control-label">Option 4</label>
					<div class="col-sm-9">
						<input type="text" class="form-control" name="option_d" size="50" maxlength="255" value="">
					</div>
				</div>
			</div>
			<div class="panel-footer">
				<fieldset>
					<span class="btn-group">
						<input class="btn btn-primary" type="submit" name="start_poll" value="Start New Poll">
						<input class="btn btn-default" type="reset" value="Reset all fields">
					</span>
				</fieldset>
			</div>
		</form>
	</div>
	<?php 
		$find_latest_poll = $db->query("SELECT id, question, poll_closed FROM polls ORDER BY started_when DESC LIMIT 0 , 1");
		if($db->num_rows($find_latest_poll) > 0) {
			?>
			<form method='post' action='poll_API.php' class="form-horizontal">
				<div class='panel panel-default'>
					<div class='panel-heading'>
						<h3 class='panel-title'>Latest poll</h3>
					</div>
					<div class="panel-body">
					<?php
					while($poll_results = $db->fetch_assoc($find_latest_poll)) {
						if($poll_results['poll_closed'] == 0) {
							echo"<button class='btn btn-danger' type='submit' name='end_poll_id' value='".$poll_results['id']."'>Close: ".$poll_results['question']."</button>";
						} else {
							echo"<button class='btn btn-success' type='submit' name='end_poll_id' value='".$poll_results['id']."'>Open: ".$poll_results['question']."</button>";
						}
					}
					?>
					</div>
				</div>
			</form>
			<?php
		}
		?>
	</div>
</div>
<?php
require 'footer.php';