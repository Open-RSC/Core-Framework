<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<div class="<?php if($isActive) { ?>col-sm-8 char-r-side <?php } else {?> col-sm-12 content-r-side<?php } ?>">
	<div class="panel panel-default">
		<div class="content-header content-header--highlight">
			<h2 class="content-header-title">Select Character</h2>
		</div>
		<div class="embended-info">
			<p>Select a character by clicking the manage button.
			<br />
			The profile - settings of your selected character can be located in the box to the left.
		</div>
		<div class="panel-body">
			<div class="select_character">
				<div class="char_box">
				<?php if($character_slots_remaining == $my_character_slots) { ?>
					<span class="info-txt-create">You have no characters. Click on the button below.</span>
				<?php } else { ?>
					<div class="col-sm-12">
						<?php 
						while($cmp = $db->fetch_assoc($find_chars))
						{ ?>
						<div class="col-sm-6">
							<div class="character-entry">
								<div class="media">
									<div class="pull-left">
									<img class="img-responsive media-object" src="<?php echo get_player_card($cmp['id']); ?>" width="65" height="115" alt="">            
									</div>
									<div class="character-body">
										<h4><?php echo luna_htmlspecialchars($cmp['username']); ?></h4>
										<h5>Level: <?php echo number_format($cmp['combat']); ?></h5>
										<h5>Created: <?php echo format_time($cmp['creation_date'], true, "j/m/Y"); ?></h5>
										<?php if($cmp['forum_active'] == 1) { ?>
											<span><i class="fa fa-check" aria-hidden="true"></i> Selected Character</span>
											<?php } else  { ?>
											<form method="POST" class="form-horizontal btn-group" action="char_manager.php?id=<?php echo $id ?>&amp;view=select&amp;setting=active&amp;player=<?php echo $cmp['id'];?>">
												<button type="submit" class="btn btn-rounded" name="put_active" value="1"><i class="fa fa-cog" aria-hidden="true"></i> Manage</button>
											</form>
											
											<form method="POST" class="form-horizontal btn-group" action="char_manager.php?id=<?php echo $id ?>&amp;view=select&amp;setting=delete_character&amp;player=<?php echo $cmp['id'];?>">
												<button type="submit" class="btn btn-rounded-red" name="delete_verify" value="submit"><i class="fa fa-trash-o" aria-hidden="true"></i> Delete</button>
											</form>
										<?php 
										} ?>
									</div>
								</div>
							</div>
						</div>
						<?php }?>
					</div>
				<?php }  ?>
				<div class="col-sm-12">
					<hr class="draw-line" />
					<div class="btn-group-wrap">
						<div class="progress">
							<?php 
							$percent_curr = 100;
							echo ($character_slots_remaining <= 0 ? "<div class='progress-bar progress-bar-danger' style='width: ". $percent_curr  ."%'>No character slots available!</div>" : "<div class='progress-bar progress-bar-success' style='width: ". $percent_curr  ."%'>Character slots: ". $character_slots_remaining ." / ". $my_character_slots . "</div>"); 
							?>	
						</div>
						<?php 
						if($character_slots_remaining > 0) 
						{ 
						?>
						<div class="btn-group">
							<a class="btn btn-default-2" href="char_manager.php?view=create&amp;id=<?php echo $id ?>"><i class="fa fa-user-plus" aria-hidden="true"></i> Create a Character</a>
						</div>
						<?php 
						} 
						else if($luna_user['character_slots'] != $maximum_character_slots)
						{
						?>
						<div class="btn-group">
							<a class="btn btn-success" href="shop.php"><i class="fa fa-user-circle" aria-hidden="true"></i> Buy more slots!</a>
						</div>
						<?php 
						}
						else 
						{
							echo "<p>You have reached the maximum character slot limit (10)!</p>";
						}
						?>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>