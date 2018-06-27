<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;

	$find_latest_poll = $db->query("SELECT id, question, poll_closed, option_1, option_2, option_3, option_4 FROM polls WHERE poll_closed = 0 ORDER BY started_when DESC LIMIT 0 , 1");
	if($db->num_rows($find_latest_poll) > 0) {
		$poll_info = $db->fetch_assoc($find_latest_poll);
		$check_voter = $db->query("SELECT 1 FROM poll_results WHERE user = '" . $luna_user['id'] . "'  AND poll_id = '" . $poll_info['id'] . "'");
		$has_voted = $db->num_rows($check_voter) > 0 ? true : false;
		
		if($has_voted == false && $poll_info['poll_closed'] == 0) {
			if(isset($_POST['cast_vote']) && isset($_POST['option'])) {
				$check_option = !empty($poll_info['option_' . $_POST['option']]) ? $_POST['option'] : null;
				if(isset($check_option)) {
					//confirm_referrer('index.php');
					$db->query("INSERT INTO poll_results (poll_id, user, user_ip, option_selected) VALUES ('" . $poll_info['id'] . "', '" . $luna_user['id'] . "', '" . $_SERVER['REMOTE_ADDR'] . "', '" . $check_option ."');");
					redirect($_SERVER['REQUEST_URI']);
				}
			}
		}
		$valid_poll_options = array('option_1', 'option_2', 'option_3', 'option_4');
		$gather_results = $db->query("SELECT option_selected FROM poll_results WHERE poll_id = '" . $poll_info['id'] . "'");
		$total_poll_results = $db->num_rows($gather_results);
		if($total_poll_results > 0) {
			while($poll_results = $db->fetch_assoc($gather_results)) {
				$option_selected['option_' . $poll_results['option_selected']]++;
			}
		}
		?>
		<div id="poll">
			<form method='post' action='index.php'>
				<table class="table table-bordered">
					<thead>
					<tr>
						<th colspan="3">Poll: <?php echo $poll_info['question'] . "&nbsp;" . ($total_poll_results > 0 ? "(Total Votes: ". $total_poll_results .")" : "")?></th>
					</tr>
					</thead>
					<tbody>
					<?php
						$base_option = 1;
						foreach($valid_poll_options as $option_num) {
							if(!empty($poll_info[$option_num])) {
								if($has_voted == false && !$luna_user['is_guest'] && $poll_info['poll_closed'] == 0) {
									echo "
									<tr>
										<td>
											<input type='radio' name='option' value='" . $base_option . "' />
											<span class='poll-text'>" . $poll_info[$option_num] ."</span>
										</td>
									<tr>
									";
								} else {
									$total_current = isset($option_selected['option_' . $base_option]) ? $option_selected['option_' . $base_option] : 0;
									if($total_current > 0) {
									$percent_curr = round(($total_current / $total_poll_results) * 100);
									} else {
									 $percent_curr = 0;
									}
									echo "
									<tr>
										<td width='55%'>
											<span class='poll-question'>- " . $poll_info[$option_num] ."</span>
										</td>
										<td width='25%'>
											<div class='progress'>
												<div class='progress-bar progress-bar-legacy' role='progressbar' style='width: ".$percent_curr. "%;' 'aria-valuenow='".$percent_curr. "' aria-valuemin='0' aria-valuemax='100'>
												</div>
											</div>
										</td>
										<td>
											<span class='votes'>Vote". ($total_current == 1 ? ":" : "s:") . "&nbsp;" . $total_current . ", " . number_format($percent_curr, 2) . "%</span>
										</td>";
								}
							}
							++$base_option;
						}
						if($has_voted == false && !$luna_user['is_guest']) {
							?> 
							<tr>
								<td>
									<div class="btn-toolbar" role="toolbar">
										<button class='btn btn-rounded' type='submit' name='cast_vote' value='Vote'>Vote Now</button>
									</div>
								</td>
							</tr>
							<?php
						}
						?>
					</tbody>
				</table>
			</form>
		</div>
		<?php
	}
?>