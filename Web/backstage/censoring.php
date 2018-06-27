<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'content');
define('LUNA_PAGE', 'censoring');

require LUNA_ROOT.'include/common.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}

// Add a censor word
if (isset($_POST['add_word'])) {
	confirm_referrer('backstage/censoring.php');

	$search_for = luna_trim($_POST['new_search_for']);
	$replace_with = luna_trim($_POST['new_replace_with']);

	if ($search_for == '')
		message_backstage(__('You must enter a word to censor.', 'luna'));

	$db->query('INSERT INTO '.$db->prefix.'censoring (search_for, replace_with) VALUES (\''.$db->escape($search_for).'\', \''.$db->escape($replace_with).'\')') or error('Unable to add censor word', __FILE__, __LINE__, $db->error());

	// Regenerate the censoring cache
	if (!defined('LUNA_CACHE_FUNCTIONS_LOADED'))
		require LUNA_ROOT.'include/cache.php';

	generate_censoring_cache();

	redirect('backstage/censoring.php');
}

// Update a censor word
elseif (isset($_POST['update'])) {
	confirm_referrer('backstage/censoring.php');

	$id = intval(key($_POST['update']));

	$search_for = luna_trim($_POST['search_for'][$id]);
	$replace_with = luna_trim($_POST['replace_with'][$id]);

	if ($search_for == '')
		message_backstage(__('You must enter a word to censor.', 'luna'));

	$db->query('UPDATE '.$db->prefix.'censoring SET search_for=\''.$db->escape($search_for).'\', replace_with=\''.$db->escape($replace_with).'\' WHERE id='.$id) or error('Unable to update censor word', __FILE__, __LINE__, $db->error());

	// Regenerate the censoring cache
	if (!defined('LUNA_CACHE_FUNCTIONS_LOADED'))
		require LUNA_ROOT.'include/cache.php';

	generate_censoring_cache();

	redirect('backstage/censoring.php');
}

// Remove a censor word
elseif (isset($_POST['remove'])) {
	confirm_referrer('backstage/censoring.php');

	$id = intval(key($_POST['remove']));

	$db->query('DELETE FROM '.$db->prefix.'censoring WHERE id='.$id) or error('Unable to delete censor word', __FILE__, __LINE__, $db->error());

	// Regenerate the censoring cache
	if (!defined('LUNA_CACHE_FUNCTIONS_LOADED'))
		require LUNA_ROOT.'include/cache.php';

	generate_censoring_cache();

	redirect('backstage/censoring.php');
}

$focus_element = array('censoring', 'new_search_for');

require 'header.php';

?>
<div class="row">
    <div class="col-sm-12">
        <?php if ($luna_config['o_censoring'] == 0) { ?>
        <div class="alert alert-danger">
            <i class="fa fa-fw fa-exclamation"></i> <?php echo sprintf(__('Censoring is disabled in %s.', 'luna'), '<a href="features.php">'.__('Features', 'luna').'</a>') ?>
        </div>
        <?php } ?>
    </div>
	<div class="col-sm-4">
		<form id="censoring" method="post" action="censoring.php">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title"><?php _e('Add word', 'luna') ?><span class="pull-right"><button class="btn btn-primary" type="submit" name="add_word" tabindex="3"><span class="fa fa-fw fa-plus"></span> <?php _e('Add', 'luna') ?></button></span></h3>
				</div>
				<fieldset>
					<div class="panel-body">
						<p><?php _e('Enter a word that you want to censor and the replacement text for this word. Wildcards are accepted.', 'luna') ?></p>
                        <hr />
                        <input type="text" class="form-control" placeholder="<?php _e('Censored word', 'luna') ?>" name="new_search_for" maxlength="60" tabindex="1" />
                        <hr />
                        <input type="text" class="form-control" placeholder="<?php _e('Replacement word', 'luna') ?>" name="new_replace_with" maxlength="60" tabindex="2" />
                    </div>
				</fieldset>
			</div>
		</form>
	</div>
	<div class="col-sm-8">
		<div class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title"><?php _e('Manage words', 'luna') ?></h3>
			</div>
			<form id="censoring" method="post" action="censoring.php">
				<fieldset>
					<table class="table table-striped">
						<thead>
							<tr>
								<th class="col-xs-4"><?php _e('Censored word', 'luna') ?></th>
								<th class="col-xs-4"><?php _e('Replacement word', 'luna') ?></th>
								<th class="col-xs-4"><?php _e('Action', 'luna') ?></th>
							</tr>
						</thead>
						<tbody>
<?php

$result = $db->query('SELECT id, search_for, replace_with FROM '.$db->prefix.'censoring ORDER BY id') or error('Unable to fetch censor word list', __FILE__, __LINE__, $db->error());
if ($db->num_rows($result)) {

	while ($cur_word = $db->fetch_assoc($result)) {
?>
							<tr>
								<td>
									<input type="text" class="form-control" name="search_for[<?php echo $cur_word['id'] ?>]" value="<?php echo luna_htmlspecialchars($cur_word['search_for']) ?>" maxlength="60" />
								</td>
								<td>
									<input type="text" class="form-control" name="replace_with[<?php echo $cur_word['id'] ?>]" value="<?php echo luna_htmlspecialchars($cur_word['replace_with']) ?>" maxlength="60" />
								</td>
								<td>
									<div class="btn-group">
										<button class="btn btn-primary" type="submit" name="update[<?php echo $cur_word['id'] ?>]"><span class="fa fa-fw fa-check"></span> <?php _e('Update', 'luna') ?></button>
										<button class="btn btn-danger" type="submit" name="remove[<?php echo $cur_word['id'] ?>]"><span class="fa fa-fw fa-trash"></span> <?php _e('Remove', 'luna') ?></button>
									</div>
								</td>
								</tr>
<?php
	}
} else
	echo "\t\t\t\t\t\t\t".'<tr><td colspan="3">'.__('No censor words in list.', 'luna').'</td></tr>'."\n";

?>
						</tbody>
					</table>
				</fieldset>
			</form>
		</div>
	</div>
</div>
<?php

require 'footer.php';
