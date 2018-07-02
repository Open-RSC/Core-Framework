<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'shop_API');

require LUNA_ROOT.'include/common.php';

if ($luna_user['g_id'] != LUNA_ADMIN) {
	header("Location: ../backstage/login.php");
    exit;
}
require 'header.php';	

$mode = isset($_GET['mode']) ? $_GET['mode'] : null;

// Fetch shop products
$shop_query = $db->query('SELECT id, item_id, product_name, product_desc, product_category, redirect_url, product_image, product_price FROM '.$db->prefix.'shop ORDER BY id') or error('Unable to fetch shop info', __FILE__, __LINE__, $db->error());

// Add to shop
if (isset($_POST['add_item_shop'])) {
	confirm_referrer('admin/shop_API.php');
	$shop_item_id = intval($_POST['item_id']);
	$shop_item = luna_trim($_POST['item_name']);
	$shop_desc = luna_trim($_POST['item_desc']);
	$add = intval($_POST['item_category']);
	$shop_image = luna_trim($_POST['item_image']);
	$shop_price = intval($_POST['item_price']);
	$shop_redirect = intval($_POST['item_redirect']);
	if ($add < 0)
		message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

	$db->query('INSERT INTO '.$db->prefix.'shop (item_id, product_name, product_desc, product_category, product_image, product_price, redirect_url) VALUES('.$shop_item_id.', \''.$db->escape($shop_item).'\', \''.$db->escape($shop_desc).'\', '.$add.', \''.$db->escape($shop_image).'\', '.$shop_price.', \''.$db->escape($shop_redirect).'\')') or error('Unable to create item to shop!', __FILE__, __LINE__, $db->error());

	redirect('admin/shop_API.php?mode=add_item');
}
// Delete from shop
elseif (isset($_GET['delete_item'])) {
	confirm_referrer('admin/shop_API.php');

	$delete_i = intval($_GET['delete_item']);
	if ($delete_i < 1)
		message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');
	
	// Delete the forum and any forum specific group permissions
		$db->query('DELETE FROM '.$db->prefix.'shop WHERE id='.$delete_i) or error('Unable to delete item from shop', __FILE__, __LINE__, $db->error());

	redirect('admin/shop_API.php?mode=delete_item&saved=true');
}
// Update item in shop
elseif ( isset( $_POST['update_shop'] ) ) {
	confirm_referrer('admin/shop_API.php');
	
	$shop_items = $_POST['shop'];

	if ( empty( $shop_items ) )
		message_backstage( __( 'No shop data were found to update/save...', 'luna' ), false, '404 Not Found' );

	foreach ( $shop_items as $shop_id => $cur_shop ) {
		$cur_shop['item_id'] = luna_trim( $cur_shop['item_id'] );
		$cur_shop['product_name'] = luna_trim( $cur_shop['product_name'] );
		$cur_shop['product_desc'] = luna_trim( $cur_shop['product_desc'] );
		$cur_shop['product_category'] = intval( $cur_shop['product_category'] );
		$cur_shop['product_image'] = luna_trim( $cur_shop['product_image'] );
		$cur_shop['product_price'] = intval( $cur_shop['product_price'] );
		$cur_shop['redirect_url'] = luna_trim( $cur_shop['product_redirect'] );

		$db->query('UPDATE '.$db->prefix.'shop SET item_id=\''.intval($cur_shop['item_id']) .'\', product_name=\''.$db->escape( $cur_shop['product_name'] ).'\', product_desc=\''.$db->escape($cur_shop['product_desc']).'\', product_category=\''.intval($cur_shop['product_category']) .'\', product_image=\''.$db->escape( $cur_shop['product_image'] ).'\', product_price=\''. intval($cur_shop['product_price']) .'\', redirect_url=\''. $db->escape($cur_shop['redirect_url']) .'\' WHERE id='.intval( $shop_id)) or error( 'Unable to update shop item', __FILE__, __LINE__, $db->error() );
	}

	redirect('admin/shop_API.php?saved=true');
}

?>
<div class="content_wrapper col-sm-10">
	<div class="row title">
		<div class="small-12 columns">
			<h2 class="page-heading">Shop API</h2>
		</div>
	</div>
	<div class="small-12 columns">
	<?php
	if (isset($_GET['saved']))
		if($mode == 'add_item')
			echo '<div class="alert alert-success"><i class="fa fa-fw fa-check"></i> '.__('A new item has been added to the shop!', 'luna').'</div>';
		elseif($mode == 'delete_item')
			echo '<div class="alert alert-success"><i class="fa fa-fw fa-check"></i> '.__('You have successfully deleted an item from the shop!', 'luna').'</div>';
		else 
			echo '<div class="alert alert-success"><i class="fa fa-fw fa-check"></i> '.__('The shop item has been updated!', 'luna').'</div>';
	?>
		<div class="col-sm-3">
			<form method="post" action="shop_API.php">
				<div class="panel panel-default">
					<div class="panel-heading">
						<h4 class="panel-title">Add new product<span class="pull-right"><button class="block-btn stick-heading" type="submit" name="add_item_shop" tabindex="2"><span class="fa fa-fw fa-plus"></span> Add</button></span>
					</h4>
					</div>
					<div class="panel-body">
						<select class="form-control" name="item_category" tabindex="1">
							<option value="0">Services</option>
							<option value="1">Cosmetics</option>
							<option value="2">Subscriptions</option>
						</select>
						<hr>
						<input type="text" class="form-control" name="item_image" maxlength="60" placeholder="Image URL" required="required">
						<hr>
						<input type="text" class="form-control" name="item_id" maxlength="4" placeholder="Item ID" required="required">
						<hr>
						<input type="text" class="form-control" name="item_name" maxlength="60" placeholder="Item Name" required="required">
						<hr>
						<div class="form-group">
							<div class="editor">
								<div class="btn-toolbar btn-toolbar-top">
									<div class="btn-group btn-transparent">
										<a class="btn btn-editor" href="javascript:void(0);" onclick="AddTag('inline','b');" title="Bold" tabindex="-1"><span class="fa fa-fw fa-bold fa-fw"></span></a>
										<a class="btn btn-editor" href="javascript:void(0);" onclick="AddTag('inline','u');" title="Underline" tabindex="-1"><span class="fa fa-fw fa-underline fa-fw"></span></a>
										<a class="btn btn-editor hidden-sm hidden-xs" href="javascript:void(0);" onclick="AddTag('inline','i');" title="Italic" tabindex="-1"><span class="fa fa-fw fa-italic fa-fw"></span></a>
										<a class="btn btn-editor hidden-md hidden-sm hidden-xs" href="javascript:void(0);" onclick="AddTag('inline','s');" title="Strike" tabindex="-1"><span class="fa fa-fw fa-strikethrough fa-fw"></span></a>
									</div>
									<div class="btn-group btn-transparent">
										<a class="btn btn-editor" href="javascript:void(0);" onclick="AddTag('inline','url');" title="URL" tabindex="-1"><span class="fa fa-fw fa-link fa-fw"></span></a>
										<a class="btn btn-editor hidden-xs" href="javascript:void(0);" onclick="AddTag('inline','img');" title="Image" tabindex="-1"><span class="fa fa-fw fa-image fa-fw"></span></a>
										<a class="btn btn-editor hidden-xs" href="javascript:void(0);" onclick="AddTag('inline','video');" title="Video" tabindex="-1"><span class="fa fa-fw fa-play fa-fw"></span></a>
									</div>
									<div class="btn-group btn-transparent">
										<a class="btn btn-editor" href="javascript:void(0);" onclick="AddTag('list', 'list');" title="List" tabindex="-1"><span class="fa fa-fw fa-list-ul fa-fw"></span></a>
										<a class="btn btn-editor" href="javascript:void(0);" onclick="AddTag('inline','*');" title="List item" tabindex="-1"><span class="fa fa-fw fa-asterisk fa-fw"></span></a>
									</div>
									<div class="btn-group pull-right hidden-lg">
										<a class="btn btn-transparent dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
											<span class="fa fa-fw fa-ellipsis-h"></span>
										</a>
										<ul class="dropdown-menu" role="menu">
											<li class="hidden-lg hidden-md"><a href="javascript:void(0);" onclick="AddTag('inline','i');" title="Italic" tabindex="-1"><span class="fa fa-fw fa-italic fa-fw"></span> Italic</a></li>
											<li class="hidden-lg hidden-md"><a href="javascript:void(0);" onclick="AddTag('code','code');" title="Code" tabindex="-1"><span class="fa fa-fw fa-code fa-fw"></span> Code</a></li>
											<li class="hidden-lg"><a href="javascript:void(0);" onclick="AddTag('inline','c');" title="Inline code" tabindex="-1"><span class="fa fa-fw fa-file-code-o fa-fw"></span> Inline code</a></li>
											<li class="hidden-lg hidden-md hidden-sm"><a href="javascript:void(0);" onclick="AddTag('inline','img');" title="Image" tabindex="-1"><span class="fa fa-fw fa-image fa-fw"></span> Image</a></li>
											<li class="hidden-lg hidden-md hidden-sm"><a href="javascript:void(0);" onclick="AddTag('inline','video');" title="Video" tabindex="-1"><span class="fa fa-fw fa-play fa-fw"></span> Video</a></li>
											<li><a href="javascript:void(0);" onclick="AddTag('inline','s');" title="Strike" tabindex="-1"><span class="fa fa-fw fa-strikethrough fa-fw"></span> Strike</a></li>
											<li><a href="javascript:void(0);" onclick="AddTag('inline','sub');" title="Subscript" tabindex="-1"><span class="fa fa-fw fa-subscript fa-fw"></span> Subscript</a></li>
											<li><a href="javascript:void(0);" onclick="AddTag('inline','sup');" title="Superscript" tabindex="-1"><span class="fa fa-fw fa-superscript fa-fw"></span> Superscript</a></li>
										</ul>
									</div>
								</div>
								<textarea class="form-control textarea" placeholder="Item Description..." name="item_desc" id="item_desc_field" rows="5" tabindex="3"></textarea>
							</div>
							<script>
							function AddTag(type, tag) {
							var Field = document.getElementById('item_desc_field');
							var val = Field.value;
							var selected_txt = val.substring(Field.selectionStart, Field.selectionEnd);
							var before_txt = val.substring(0, Field.selectionStart);
							var after_txt = val.substring(Field.selectionEnd, val.length);
							if (type == 'inline')
							Field.value = before_txt + '[' + tag + ']' + selected_txt + '[/' + tag + ']' + after_txt;
							else if (type == 'list')
							Field.value = before_txt + '[list]' + "\r" + '[*]' + selected_txt + '[/*]' + "\r" + '[/list]' + after_txt;
							else if (type == 'code')
							Field.value = before_txt + '[' + tag + ']' + "\r" + '[[language]]' + "\r" + selected_txt + "\r" + '[/' + tag + ']' + after_txt;
							else if (type == 'emoji')
							Field.value = before_txt + ' ' + tag + ' ' + after_txt;

							document.getElementById('item_desc_field').focus();
							}
							</script>
						</div>
						<hr>
						<input type="text" class="form-control" name="item_price" maxlength="10" placeholder="Jewel Price" required="required">
						<hr>
						<p>Redirect product URL <small>(Redirect product to the actual page for it's service)</small>.</p>
						<input type="text" class="form-control" name="item_redirect" maxlength="10" placeholder="Redirect URL">
                    </div>
				</div>
			</form>
		</div>
		<div id="shop_template" class="col-sm-9">
			<form id="shop_form" action="shop_API.php" method="post">
			<table class="base-tbl">
				<tbody>
					<tr>
						<th><span class="indicator">Item ID</span></th>
						<th><span class="indicator">Item Name</span></th>
						<th><span class="indicator">Item Desc</span></th>
						<th><span class="indicator">Category</span></th>
						<th><span class="indicator">Image URL</span></th>
						<th><span class="indicator">Price</span></th>
						<th><span class="indicator">Redirect URL</span></th>
						<th width="2%"></th>
						<th width="2%"></th>
					</tr>
					<?php 
					if($db->num_rows($shop_query) > 0)
					{
						while($shop = $db->fetch_assoc($shop_query)) 
						{
							?>
							<tr>
							<td><input class="form-control input-sm" type="text" maxlength="4" size="5" name="shop[<?php echo $shop['id'] ?>][item_id]" value="<?php echo ($shop['item_id'] == 0 ? '-' :  $shop['item_id']) ?>"></td>
							<td><input class="form-control input-sm" type="text" maxlength="60" size="30" name="shop[<?php echo $shop['id'] ?>][product_name]" value="<?php echo $shop['product_name'] ?>"></td>
							<td><textarea class="form-control input-sm" placeholder="Description.." name="shop[<?php echo $shop['id'] ?>][product_desc]" id="item_desc_field" rows="1"><?php echo ($shop['product_desc'] != '') ? $shop['product_desc'] : "" ?></textarea></td>
							<td> 
							<select class="form-control m-b-control" name="shop[<?php echo $shop['id'] ?>][product_category]" tabindex="1">
								<option value="0" <?php echo ($shop['product_category'] == 0 ? 'selected="selected"' : '')?>>Services</option>
								<option value="1" <?php echo ($shop['product_category'] == 1 ? 'selected="selected"' : '')?>>Cosmetics</option>
								<option value="2" <?php echo ($shop['product_category'] == 2 ? 'selected="selected"' : '')?>>Subscriptions</option>
							</select>
							</td>
							<td><input class="form-control input-sm" type="text" maxlength="60" size="30" name="shop[<?php echo $shop['id'] ?>][product_image]" value="<?php echo ($shop['product_image'] == '' ? '-' :  $shop['product_image']) ?>"></td>
							<td><input class="form-control input-sm" type="text" maxlength="5" size="2" name="shop[<?php echo $shop['id'] ?>][product_price]" value="<?php echo $shop['product_price'] ?>"></td>
							<td><input class="form-control input-sm" type="text" maxlength="60" size="30" name="shop[<?php echo $shop['id'] ?>][product_redirect]" value="<?php echo ($shop['redirect_url'] == '' ? '' :  $shop['redirect_url']) ?>"></td>
							<td width="2%" class="cColumn">
								<button class="block-btn" type="submit" name="update_shop"><i class="fa fa-floppy-o" aria-hidden="true"></i></button>
							</td>
							<td width="2%" class="cColumn">
								<a href="shop_API.php?delete_item=<?php echo $shop['id'] ?>" onClick="return confirm('Are you sure to delete this product?')"><i class="fa fa-times" aria-hidden="true"></i></a>
							</td>
							</tr>
							<?php
						}
					} else {
						echo "<tr><td>No items were found to load.</td><td></td><td></td><td></td><td></td><td></td><td></td></tr>";
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