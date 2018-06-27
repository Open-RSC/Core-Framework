<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<div class="col-sm-8 char-r-side">
	<div class="panel panel-default">
	<div class="content-header content-header--highlight">
		<h2 class="content-header-title">Change Password</h2>
	</div>
	<div class="embended-info">
		<p>Be sure to choose a strong password that is difficult to guess. Never give your password to <strong>any one</strong> and remember, account sharing is <a id="danger-text" href="ToS.php"><strong>against the rules</strong></a>.</p>
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
				echo "<strong>Your password was successfully changed!</strong>";
				?>
			</div>
			<?php } ?>
			<form method='post' class="form-horizontal" action='char_manager.php?id=<?php echo $luna_user['id'];?>&amp;setting=change_password&amp;player=<?php echo $apply_char['id']; ?>'>
				<!--<div class="form-group">
					<label class="col-sm-4 char-label">Enter Current Password</label>
					<div class="col-sm-8">
						<input type='password' class="form-control" name='current_pass' maxlength='16' value='' />
					</div>
				</div>-->
				<div class="form-group">
					<label class="col-sm-4 char-label">New Password</label>
					<div class="col-sm-8">
						<input type='password' class="form-control" name='c_pass_1' maxlength='16' value='' />
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-4 char-label">Confirm Password</label>
					<div class="col-sm-8">
						<input type='password' class="form-control" name='c_pass_2' maxlength='16' value='' />
					</div>
				</div>
				<div class="form-group">
				<button type="submit" class="btn btn-primary" name="change_password" value="Change Password"><span class="fa fa-lock"></span> Change Password</button>
				</div>
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