<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<div class="col-sm-4 content-l-side">
	<div class="panel panel-default">
		<div class="content-header content-header--highlight">
			<h2 class="content-header-title">
			<?php 
			if($tokenCharIsValid) {
				echo 'Welcome, '.$curr_char_info['username'].'!';
			} else {
				echo 'Select a character';
			}
			?>
			</h2>
		</div>
		<?php 
		if(!$tokenCharIsValid && $db->num_rows($find_user_characters)) {
			?>
			<div class="embended-info">
				<p><i class="fa fa-info-circle" aria-hidden="true"></i> Use the dropdown to select which character you would like to modify, add an item or subscription to. Click continue to proceed to your shopping cart.</p>
			</div>
			<?php
		}
		?>
		<div class="panel-body">
		<?php 
			// If so..., and the anti csrf token is valid... display shop cart?
			if($tokenCharIsValid)
			{
				?>
				<div class="shop-list">
					<h2 class="shop-block_title">Your Shopping Cart <i class="fa fa-shopping-cart" aria-hidden="true"></i></h2>
					<ul class="item-block_list">
					<?php 
					if(isset($_SESSION['cart']) && !empty($_SESSION['cart']))
					{
						$cart = unserialize(serialize($_SESSION['cart']));
						$sum = 0;
						$index = 0;
						for($i = 0; $i < count($cart); $i++)
						{
							$sum += $cart[$i]->price * $cart[$i]->quantity;
							?>
							<li id="item-block_list-item" class="item-block_list-item">
								<div class="cart_info">
									<img class="shopimage" alt="image" src="<?php echo $cart[$i]->image; ?>">
									<div class="shopitem"><?php echo $cart[$i]->name; ?></div>
									<div class="shop_cost">
										<span class="shopvalue hidden-sm hidden-xs"><i class="fa fa-diamond" aria-hidden="true"></i> <?php echo $cart[$i]->price * $cart[$i]->quantity; ?></span>
										<span class="shopamount"><?php echo $cart[$i]->quantity; ?>x</span>
										<a class="remove_item" href="shop.php?token=<?php echo $_SESSION["curr_token"]; ?>&amp;char=<?php echo $selected_char; ?>&amp;index_remove=<?php echo $index; ?>" onClick="return confirm('Are you sure to delete this item?')"><i class="fa fa-trash-o fa-lg" aria-hidden="true"></i></a>
									</div>
								</div>
							</li>
							<?php 
						$index++;
						}
					} 
					else 
					{
						?>
						<li id="item-block_list-item" class="item-block_list-item">
							<div class="cart_info">
								<span>Your cart is empty.</span>
							</div>
						</li>
						<?php
					}
					?>
					<div class="cart_total">Total Cost: <?php echo ($sum != 0 ? $sum : 0); ?> <i class="fa fa-diamond" aria-hidden="true"></i></div>
					</ul>
					<div class="checkout">
						<?php 
						if($luna_user['jewels'] >= $sum) 
						{ 
						?>
						<a class="btn btn-default-2" href="shop.php?m=checkout&amp;token=<?php echo $_SESSION["curr_token"]; ?>&amp;char=<?php echo $selected_char; ?>"><i class="fa fa-diamond" aria-hidden="true"></i> Checkout with Jewels</a>
						<?php 
						}
						else 
						{ 
						?>
						<a class="btn btn-danger full-length" href="donate.php">Not enough Jewels - Charge!</a>
						<?php 
						} 
						?>
					</div>
					<hr class="draw-line">
					<div class="addit">
						<p class="shop_opt"><a href="shop.php">Change Character</a><span>|</span><a href="shop.php?token=<?php echo $_SESSION["curr_token"]; ?>&amp;char=<?php echo $selected_char; ?>&amp;empty_cart" onClick="return confirm('Are you sure? Your entire cart will be deleted.')">Empty cart</a></p>
					</div>
				</div>
				<?php
			} 
			else 
			{
				// If they have characters then..
				if($db->num_rows($find_user_characters))
				{
					// Draw them a character selection form..
					$select_output_menu = null;
					while($fetch_chars = $db->fetch_assoc($find_user_characters))
						$select_output_menu[] = '<option value="' . $fetch_chars["id"] . '" '.($fetch_chars["id"] == $_GET["char"] ? 'selected' : '').'>' . ucwords($fetch_chars["username"]) . '</option>';
					?>	
					<form method="get" class="form-horizontal" action="shop.php">
						<input type="hidden" name="token" value="<?php echo $_SESSION['curr_token']; ?>" />
						<span class="input-group">
							<select class="form-control" name="char"><?php echo is_array($select_output_menu) ? implode($select_output_menu, "") : "<option>Invalid</option>" ?></select>
							<span class="input-group-btn">
								<button class="btn btn-primary" type="submit">Continue</button>
							</span>
						</span>
					</form>
					<?php
				} 
				else 
				{
					echo "<p>You don't seem to have any game characters created yet. <a href='char_manager.php?id=".$luna_user['id']."'>Create one here</a>!</p>";
				}
			}
		?>
		</div>
	</div>
</div>