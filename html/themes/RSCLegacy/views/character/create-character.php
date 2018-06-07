<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<div class="panel panel-default">
	<div class="content-header content-header--highlight">
		<h2 class="content-header-title">Create Your In-Game Character</h2>
	</div>
	<div class="embended-info">
		<p>Enter your username between 2 and 12 characters, your password between 4 and 16 characters. Your username and password can only contain number, letters and spaces. The character you create will be your in-game identity, the name you choose will be displayed for everyone else.</p>
		<br />
		<p><strong style="color: #b2dbee;">IMPORTANT:</strong> We strongly advice you to use a unique password for both your forum account and in-game character(s). <strong>DO NOT</strong> use the same password that you use elsewhere.</p>
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
			<?php 
                        } 
                        else 
                            if(isset($_GET['saved']) && counting($errors) == 0) 
                        { ?>
			<div class="alert alert-dismissable alert-info alert-info">
				<?php
				echo "<strong>Congratulations! Account successfully created.</strong>";
				?>
			</div>
			<?php } ?>
			<form method="post" class="form-horizontal" action="char_manager.php?view=create&amp;setting=add&amp;id=<?php echo $luna_user['id'] ?>">
				<div class="form-group">
					<label class="col-sm-4 control-label2">Character Username</label>
					<div class="col-sm-8">
						<input type="text" class="form-control" name="char_name" maxlength="12" value="">
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-4 control-label2">Character Password</label>
					<div class="col-sm-8">
						<input type="password" class="form-control" name="char_pass_1" maxlength="20" value="">
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-4 control-label2">Confirm Character Password</label>
					<div class="col-sm-8">
						<input type="password" class="form-control" name="char_pass_2" maxlength="20" value="">
					</div>
				</div>

				<p>By clicking "Register", you are inidicating that you have read and ageed to abide our <a href="terms.php">Rules</a>.</p>
				<div class="form-group">
				<button type="submit" class="btn btn-default-2" name="addcharacter" value="Register"><span class="fa fa-user"></span> register</button>
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