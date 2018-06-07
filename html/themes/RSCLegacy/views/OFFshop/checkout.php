<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<div class="panel panel-default">
	<div class="content-header content-header--highlight">
		<h2 class="content-header-title">Checkout</h2>
	</div>
	<?php 
	if((!isset($_SESSION['cart']) && empty($_SESSION['cart']) && !isset($_POST['final_checkout'])) || !$tokenCharIsValid) 
	{
		?>
		<div class="embended-info">
			<p>You have no items to process, please <a href="shop.php">go back</a>, select a game character and add items to your cart.</p>
		</div>
		<?php
	} 
	else 
	{
	?>
	<div class="embended-info">
		<p><strong style="color: #b2dbee;">IMPORTANT:</strong> You need to be logged out in-game while the order is processing.</p>
	</div>
	<div class="panel-body">
		<div class="col-sm-12 cart_info">
			<div class="checkout-user_details">
				<div class="col-md-4 col-sm-4 col-xs-12">
					<h3>Forum ID: <?php echo luna_htmlspecialchars($luna_user['username']); ?></h3>          
				</div>
				<div class="col-md-4 col-sm-4 col-xs-12">
					<h3>Character: <?php echo $curr_char_info['username']; ?></h3>             
				</div>
				<div class="col-md-4 col-sm-4 col-xs-12">
					<h3>Jewels: <?php echo number_format($luna_user['jewels']); ?></h3>                     
				</div>
			</div>
			<?php
			if(count($errors) > 0) 
			{
			?>
			<div class="alert alert-dismissable alert-danger">
				<button type="button" class="close" data-dismiss="alert">&#10006;</button>
				<strong>Could not process order!</strong>
					<ul class="error-list">
					<?php
						foreach($errors as $err)
						{
							echo "<p>" . $err . "</p>";
						}
					?>
					</ul>
			</div>
			<?php
			}
			else if(count($success) > 0) 
			{
			?>
			<div class="alert alert-dismissable alert-info">
				<strong>Completed!</strong>
					<?php
					foreach($success as $successMessage)
					{
					echo "<p>" . $successMessage . "</p>";
					}
				?>
			</div>
			<?php
			}
			?>
			<div class="area_inner">
				<p class="">To complete your order, click on the checkout button.</p>
				<h4 class="sub_headline">Shopping Cart - Checkout</h4>
				<a class="btn btn-transparent" href="shop.php?token=<?php echo $_SESSION["curr_token"]; ?>&amp;char=<?php echo $selected_char; ?>"><i class="fa fa-arrow-left" aria-hidden="true"></i> Continue Shopping</a>
				<table cellspacing="0" class="table table-bordered">
					<thead>
					<tr>
						<th>Image</span></th>
						<th>Package</span></th>
						<th>Price</span></th>
						<th>Qty</span></th>
						<th>Each</span></th>
						<th width="10%">Options</th>
					</tr>
					</thead>
					<tbody>
					<?php 
					$cart = unserialize(serialize($_SESSION['cart']));
					$sum = 0;
					$index = 0;
					for($i = 0; $i < count($cart); $i++)
					{
						$sum += $cart[$i]->price * $cart[$i]->quantity;
						?>
						<tr>
						<td><img class="shopimage" alt="image" src="<?php echo $cart[$i]->image; ?>"></td>
						<td><?php echo $cart[$i]->name; ?></td>
						<td><?php echo $cart[$i]->price * $cart[$i]->quantity; ?></td>
						<td><?php echo $cart[$i]->quantity; ?></td>
						<td><?php echo $cart[$i]->price; ?></td>
						<td><a class="remove_item" href="shop.php?m=checkout&amp;token=<?php echo $_SESSION["curr_token"]; ?>&amp;char=<?php echo $selected_char; ?>&amp;index_remove=<?php echo $index; ?>" onClick="return confirm('Are you sure to delete this item?')"><span class="fa fa-trash-o" aria-hidden="true"></span> Remove</a></td>
						</tr>
						<?php 
						$index++;
					} 
					?>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="6">
							<span style="vertical-align:top"><strong>Order total: <?php echo ($sum != 0 ? $sum : 0); ?> Jewels</strong></span>
							<?php 
								echo '
								<form method="post" class="form-horizontal pull-right" action="shop.php?m=checkout&amp;token='.$_SESSION["curr_token"].'&amp;char='.$selected_char.'">
								<a class="btn btn-danger btn-xs" href="shop.php?token='.$_SESSION["curr_token"].'&amp;char='.$selected_char.'&amp;empty_cart" onclick="return confirm(\'Are you sure? Your entire cart will be deleted.\')"><i class="fa fa-trash" aria-hidden="true"></i> Clear Cart</a>
									
									<input type="hidden" name="token" value="'.$_SESSION['curr_token'].'" />
									<button class="btn btn-success btn-xs" name="final_checkout" type="submit"><i class="fa fa-check-circle" aria-hidden="true"></i> Checkout - Place Order</button>
								</form>';
							
							?>
						  </td>
						</tr>
					</tfoot>
				</table>
			</div>
		</div>
	</div>
	<?php
	}
	?>
</div>