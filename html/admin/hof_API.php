<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'hof_API');

require LUNA_ROOT.'include/common.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}
require 'header.php';	

$mode = isset($_GET['mode']) ? $_GET['mode'] : null;

// Fetch Hofs
$hof_query = $db->query('SELECT id, accomplishment, category, username, time FROM '.$db->prefix.'hof_entrys ORDER BY category') or error('Unable to fetch hofs info', __FILE__, __LINE__, $db->error());

// Add a "default" HoF
if (isset($_POST['add_hof_new'])) {
	confirm_referrer('admin/hof_API.php');

	$hof_name = luna_trim($_POST['new_hof']);
	$add_to_hof_cat = intval($_POST['add_to_hof']);
	if ($add_to_hof_cat < 1)
		message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

	$db->query('INSERT INTO '.$db->prefix.'hof_entrys (accomplishment, category) VALUES(\''.$db->escape($hof_name).'\', '.$add_to_hof_cat.')') or error('Unable to create HoF!', __FILE__, __LINE__, $db->error());

	redirect('admin/hof_API.php?mode=created_hof&saved=true');
}

// Add a new category
elseif (isset($_POST['add_hof_cat'])) {
	confirm_referrer('admin/hof_API.php');

	$new_cat_name = luna_trim($_POST['new_hof_accomplishment']);
	if ($new_cat_name == '')
		message_backstage(__('You must enter a HoF name for the Category', 'luna'));

	$db->query('INSERT INTO '.$db->prefix.'hof_categories (hof_name) VALUES(\''.$db->escape($new_cat_name).'\')') or error('Unable to create category', __FILE__, __LINE__, $db->error());

	redirect('admin/hof_API.php?saved=true');
}
elseif (isset($_GET['delete_hof'])) {
	confirm_referrer('admin/hof_API.php');

	$hof_to_delete = intval($_GET['delete_hof']);
	if ($hof_to_delete < 1)
		message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');
	
	// Delete the forum and any forum specific group permissions
		$db->query('DELETE FROM '.$db->prefix.'hof_entrys WHERE id='.$hof_to_delete) or error('Unable to delete HoF', __FILE__, __LINE__, $db->error());

	redirect('admin/hof_API.php?mode=deleted_hof&saved=true');
}
// Update HOF
elseif ( isset( $_POST['update_hof'] ) ) {
	confirm_referrer('admin/hof_API.php');
	
	$hof_items = $_POST['hof'];

	if ( empty( $hof_items ) )
		message_backstage( __( 'No forum and category data was found to save...', 'luna' ), false, '404 Not Found' );

	foreach ( $hof_items as $hof_id => $cur_hof ) {
		$cur_hof['accomplishment'] = luna_trim( $cur_hof['accomplishment'] );
		$cur_hof['category'] = luna_trim( $cur_hof['category'] );
		$cur_hof['username'] = luna_trim( $cur_hof['username'] );
		$cur_hof['time'] = luna_trim( $cur_hof['time'] );

		$db->query('UPDATE '.$db->prefix.'hof_entrys SET accomplishment=\''.$db->escape( $cur_hof['accomplishment'] ).'\', category=\''.$cur_hof['category'].'\', username=\''.$db->escape( $cur_hof['username'] ).'\', time=\''.$db->escape( $cur_hof['time'] ).'\' WHERE id='.intval( $hof_id)) or error( 'Unable to update hof', __FILE__, __LINE__, $db->error() );
	}

	redirect('admin/hof_API.php?saved=true');
}

?>
<div class="content_wrapper col-sm-10">
	<div class="row title">
		<div class="small-12 columns">
			<h2 class="page-heading">Hall Of Fame API</h2>
		</div>
	</div>
	<div class="small-12 columns">
	<?php
	if (isset($_GET['saved']))
		if($mode == 'created_hof')
			echo '<div class="alert alert-success"><i class="fa fa-fw fa-check"></i> '.__('A new HoF has been created!', 'luna').'</div>';
		elseif($mode == 'deleted_hof')
			echo '<div class="alert alert-success"><i class="fa fa-fw fa-check"></i> '.__('You have successfully deleted an entry for the HoF Table!', 'luna').'</div>';
		else 
			echo '<div class="alert alert-success"><i class="fa fa-fw fa-check"></i> '.__('HoF has been updated!', 'luna').'</div>';
	?>
		<div class="col-sm-3">
			<form method="post" action="hof_API.php">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h4 class="panel-title">Add new HoF<span class="pull-right"><button class="block-btn stick-heading" type="submit" name="add_hof_new" tabindex="2"><span class="fa fa-fw fa-plus"></span> Add</button></span>
					</h4>
					</div>
					<div class="panel-body">
				        <select class="form-control" name="add_to_hof" tabindex="1">
							<?php
								$result = $db->query('SELECT id, hof_name FROM '.$db->prefix.'hof_categories ORDER BY id') or error('Unable to fetch hof category list', __FILE__, __LINE__, $db->error());
								while ($cur_hof = $db->fetch_assoc($result)) {
									$selected = ($cur_hof['id'] == $check_hof['category']) ? ' selected' : '';
									echo "\t\t\t\t\t\t\t\t\t\t\t".'<option value="'.$cur_hof['id'].'"'.$selected.'>'.luna_htmlspecialchars($cur_hof['hof_name']).'</option>'."\n";
								}
							?>
						</select>
                        <hr>
						<input type="text" class="form-control" name="new_hof" maxlength="80" placeholder="Accomplishment" required="required">
                    </div>
				</div>
			</form>
			<form method="post" action="hof_API.php">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h4 class="panel-title">Add HoF Categories<span class="pull-right"><button class="block-btn stick-heading" type="submit" name="add_hof_cat" tabindex="2"><span class="fa fa-fw fa-plus"></span> Add</button></span>
					</h4>
					</div>
					<div class="panel-body">
						<input type="text" class="form-control" name="new_hof_accomplishment" maxlength="80" placeholder="Name" tabindex="1">
					</div>
				</div>
			</form>
		</div>
		<div id="hof_template" class="col-sm-9">
			<form id="hof_form" action="hof_API.php" method="post">
			<table class="base-tbl">
				<tbody>
					<tr>
						<th><span class="indicator">Accomplishment</span></th>
						<th><span class="indicator">Category</span></th>
						<th><span class="indicator">Username</span></th>
						<th><span class="indicator">Time</span></th>
						<th width="5%"></th>
						<th width="5%"></th>
					</tr>
					<?php 
					if($db->num_rows($hof_query) > 0)
					{
						while($hof = $db->fetch_assoc($hof_query)) 
						{
							?>
							<tr>
							<td><input class="form-control input-sm" type="text" maxlength="60" size="60" name="hof[<?php echo $hof['id'] ?>][accomplishment]" value="<?php echo $hof['accomplishment'] ?>"></td>
							<td> 
							<select class="form-control m-b-control" name="hof[<?php echo $hof['id'] ?>][category]" tabindex="1">
							<?php
								$result = $db->query('SELECT id, hof_name FROM '.$db->prefix.'hof_categories ORDER BY id') or error('Unable to fetch hof category list', __FILE__, __LINE__, $db->error());
								while ($cur_hof = $db->fetch_assoc($result)) {
									$selected = ($cur_hof['id'] == $hof['category']) ? ' selected' : '';
									echo "\t\t\t\t\t\t\t\t\t\t\t".'<option value="'.$cur_hof['id'].'"'.$selected.'>'.luna_htmlspecialchars($cur_hof['hof_name']).'</option>'."\n";
								}
							?>
							</select>
							</td>
							<td><input class="form-control input-sm" type="text" maxlength="12" size="12" name="hof[<?php echo $hof['id'] ?>][username]" value="<?php echo ($hof['username'] == '') ? '-' :  $hof['username'] ?>"></td>
							<td><input class="form-control input-sm" type="text" maxlength="8" size="8" name="hof[<?php echo $hof['id'] ?>][time]" value="<?php echo ($hof['time'] == '') ? '-' :  $hof['time'] ?>"></td>
							<td width="5%" class="cColumn">
								<button class="block-btn" type="submit" name="update_hof"><i class="fa fa-floppy-o" aria-hidden="true"></i></button>
							</td>
							<td width="5%" class="cColumn">
								<a href="hof_API.php?delete_hof=<?php echo $hof['id'] ?>" onClick="return confirm('Are you sure to delete this entry?')"><i class="fa fa-times" aria-hidden="true"></i></a>
							</td>
							</tr>
							<?php
						}
					} else {
						echo "<tr><td>No HoFs to load.</td><td></td><td></td><td></td><td></td><td></td></tr>";
					}
					?>
				</tbody>
			</table>
			</form>
		</div>
	</div>
</div>
<?php
require 'footer.php';