<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<div class="col-sm-8 char-r-side">
	<div class="panel panel-default">
	<div class="content-header content-header--highlight">
		<h2 class="content-header-title">Change Character Name</h2>
	</div>
	<div class="embended-info">
		<!--<p>Using this service has a cost of either a Gold or Premium token which need to be in your characters inventory. You can change the name of any given character associated with your forum account. After the renaming process is done, you can login with your new name.</p>-->
	</div>
	<div class="panel-body">
		<div class="select_character">
			<div class="char_box">
			<?php 
                        //Corrected issue with depreciation in PHP 7.2
                        //if(count($errors) > 0) { 
                        function counting($errors) {
                            if($errors === null) return 0; 
                            if(is_array($errors)) return count($errors); 
                            if(is_object($errors) && $errors instanceof \Countable) return count($errors);
                                return 1;
                        }
                        if(counting($errors) > 0) {
                        ?>
			<div class="alert alert-dismissable alert-info alert-danger">
				<button type="button" class="close" data-dismiss="alert">&#10006;</button>
				<h4>Error!</h4>
				<p>Please make the following corrections:</p>
				<ul class="error-list">
				<?php
				foreach($errors as $err)
				{
					echo "<li><strong>" . $err . "</strong></li>";
				}
				?>
				</ul>
			</div>
			<?php } else if(isset($_GET['saved']) && counting($errors) == 0) { ?>
			<div class="alert alert-dismissable alert-info alert-info">
				<?php
				echo "<strong>Congratulations! Your new name is now active.</strong><br />";
				?>
			</div>
			<?php } ?>
			<script type="text/javascript">
				function confirm_renaming() {
				  return confirm('Are you sure that you want to rename this character?');
				}
			</script>
			<form method="post" class="form-horizontal" action="char_manager.php?id=<?php echo $id;?> &amp;setting=character_renaming">
				<?php
				$checkleader = $db->query("SELECT id FROM " . GAME_BASE . "clan WHERE leader = '" . $db->escape($getActiveChar['username']) . "'");
				if(!$db->num_rows($checkleader))
				{
					$checkclan = $db->query("SELECT id FROM " . GAME_BASE . "clan_players WHERE username = '" . $db->escape($getActiveChar['username']) . "'");
					if(!$db->num_rows($checkclan))
					{
					//$payment = $db->query("SELECT id FROM " . GAME_BASE . "invitems WHERE playerID = '" . $db->escape($curr_char) . "' AND id IN (2092, 2094)");
						//if($db->num_rows($payment) > 0)
						//{
								?>
								<div class="form-group">
								<label class="col-sm-4 char-label" for="disabledInput">Current Character Name</label>
									<div class="col-sm-8">
										<input class="form-control" id="disabledInput" type="text" name="character_name" value="<?php echo luna_htmlspecialchars($apply_char['username']); ?>" readonly="readonly">
									</div>
								</div>
								<div class="form-group">
								<label class="col-sm-4 char-label">New Character Name</label>
									<div class="col-sm-8">
									<input type="text" class="form-control" name="new_name" maxlength="12" value="" />
									</div>
								</div>
								<div class="form-group">
									<button onclick="return confirm_renaming()" type="submit" class="btn btn-primary" name="character_rename" value="Rename Character"><span class="fa fa-pencil"></span> Change Name</button>
								</div>	
							<?php 
						/*} else {
							?> 
							<div class="form-group">
								<!--<span class="label label-danger">Your character do not have any tokens in it's inventory.
								</span>-->
							</div>
						<?php }*/ ?>
					<?php 
					} else {
						?> 
						<div class="form-group">
							<span class="label label-danger">You are in a clan, please leave your clan from the 'Clan Setup' interface in-game.
							</span>
						</div>
					<?php } ?>
				<?php 
				} else {
					?> 
					<div class="form-group">
						<span class="label label-danger">You are leader in a clan! Please pass your leadership or leave your clan from the 'Clan Setup' interface in-game.
						</span>
					</div>
				<?php } ?>
			</form>
			<hr class="draw-line" />
			<div class="btn-group-wrap">
				<div class="btn-group">
				<a class="btn btn-danger" href="char_manager.php?id=<?php echo $luna_user['id'] ?>"><i class="fa fa-arrow-left" aria-hidden="true"></i> Go Back</a>
				</div>
			</div>
			</div>
		</div>
	</div>
</div>
</div>