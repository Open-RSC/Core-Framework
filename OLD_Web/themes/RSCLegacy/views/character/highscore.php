<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<div class="col-sm-8 char-r-side">
	<div class="panel panel-default">
	<div class="content-header content-header--highlight">
		<h2 class="content-header-title">Highscore Option</h2>
	</div>
	<div class="embended-info">
		<p>Decide whether or not you wish to disable your character from being indexed on the highscores. <strong class="accountName"><?php echo luna_htmlspecialchars($apply_char['username']); ?> </strong>is <?php echo ($apply_char['highscoreopt'] == 0 ? "<strong>not</strong>" : "currently");?> hidden on the highscores.</p>
	</div>
	<div class="panel-body">
		<div class="select_character">
			<div class="char_box">
			<form method="post"  class="form-horizontal" action="char_manager.php?id=<?php echo $id ?>&setting=highscore&player=<?php echo $apply_char['id']; ?>">
				<div class="form-group">
				<?php
				echo "<button class='btn btn-primary' name='highscore' type='submit'>" . ($apply_char['highscoreopt'] == 0 ? "<span class='fa fa-eye-slash'></span> Hide ".luna_htmlspecialchars($apply_char['username'])."" : "<span class='fa fa-eye'></span> Show ".luna_htmlspecialchars($apply_char['username'])."") . "</button> 
				" 
				;
				?>	
				</div>
			</form>
			<hr class="draw-line" />
			<div class="btn-group-wrap">
				<div class="btn-group">
				<a class="btn btn-danger" href="char_manager.php?id=<?php echo $id ?>"><i class="fa fa-arrow-left" aria-hidden="true"></i> Go Back</a>
				</div>
			</div>
			</div>
		</div>
	</div>
</div>
</div>