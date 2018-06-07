<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<div class="col-sm-8 char-r-side">
	<div class="panel panel-default">
		<div class="content-header content-header--highlight">
			<h2 class="content-header-title">Shop</h2>
		</div>
		<div class="embended-info">
		<p><i class="fa fa-info-circle" aria-hidden="true"></i> To determine which add-on you would like (Services, Cosmetics or Subscription), you can click on each to find out what they have to offer. Once you find the option you like, select it and add it to your cart.</p>
		<br />
		<p>The currency used to buy from this shop are called jewels. You can see your jewel count in the top navigation bar. To obtain more jewels, you can donate <a href="donate.php">here</a>.</p>
		</div>
		<div class="panel-body">
			<div class="cv-tab-group has-clearfix">
				<ul class="tabs has-clearfix">
					<li role="presentation" class="active" data-toggle="tooltip" data-placement="top" title="Game Services"><a href="#services" aria-controls="services" role="tab" class="services" data-toggle="tab"><img style="height:32px" src="img/items/728.png"><small style="display:block;">Services</small></a></li>
					<li role="presentation" data-toggle="tooltip" data-placement="top" title="Cosmetic Items"><a href="#cosmetic" aria-controls="cosmetic" role="tab" class="cosmetic" data-toggle="tab"><img style="height:32px" src="img/items/754.png"><small style="display:block;">Cosmetics</small></a></li>
					<li role="presentation" data-toggle="tooltip" data-placement="top" title="Game Subscription"><a href="#subscription" aria-controls="subscription" role="tab" class="subscription" data-toggle="tab"><img style="height:32px" src="img/items/2092.png"><small style="display:block;">Subscription</small></a></li>
				</ul>
				<form action="shop.php?token=<?php echo $_SESSION["curr_token"]; ?>&amp;char=<?php echo $selected_char; ?>" method="post">
				<div class="tab-content panes">
					<div class="tab-pane pane active" role="tabpanel" id="services">
						<div class="pane-content">
							<div class="column-row">
								<?php 
								if($db->num_rows($shop_item_service) > 0) 
								{
									while($add_shop = $db->fetch_assoc($shop_item_service)) 
									{
								?>
								<div class="col-item col-sm-4 content-l-side">
									<div class="column-inner">
										<div class="store-thumb">
											<span class="info-icon-details" data-toggle="modal" data-target="#store-item-detail<?php echo $add_shop['id'] ?>-popup"><i class="fa fa-info-circle" aria-hidden="true"></i></span>
											<div class="store-thumb-holder">
											<img class="store-thumb-img" src="<?php echo $add_shop['product_image'] ?>" title="<?php echo $add_shop['product_name'] ?>"  /></div>
											<p class="store-item-name"><?php echo $add_shop['product_name'] ?></p>
										</div>
										<h3 class="store-item-price"><?php echo $add_shop['product_price'] ?> <i class="fa fa-diamond" aria-hidden="true"></i></h3>
										<nav class="cv-button-group" style="text-align:right;">
											<a class="cv-button button is-ghost" data-toggle="modal" data-target="#store-item-detail<?php echo $add_shop['id'] ?>-popup" href="#">DETAILS</a>
											<?php include load_page('shop/shop-detail-modal.php'); 
											if($add_shop['redirect_url'] != '') {
												echo '<a class="cv-button button redirect" href="'.$add_shop['redirect_url'].'" >Go to service</a>';
											} else {
												echo '<button type="submit" name="item" value="'.$add_shop['id'].'" class="cv-button button" >+ ADD TO CART</button>';
											}
											?>
										</nav>
									</div>
								</div>
								<?php 
									}
								}
								else 
								{
									echo "<p>No services are being sold in shop.</p>";
								}
								?>
							</div>
						</div>
					</div>
					<div class="tab-pane pane" role="tabpanel" id="cosmetic">
						<div class="pane-content">
							<div class="column-row">
								<?php 
								if($db->num_rows($shop_item_cosmetic) > 0) 
								{
									while($add_shop = $db->fetch_assoc($shop_item_cosmetic)) 
									{
								?>
								<div class="col-item col-sm-4 content-l-side">
									<div class="column-inner">
										<div class="store-thumb">
											<span class="info-icon-details" data-toggle="modal" data-target="#store-item-detail<?php echo $add_shop['id'] ?>-popup"><i class="fa fa-info-circle" aria-hidden="true"></i></span>
											<div class="store-thumb-holder">
											<img class="store-thumb-img" src="<?php echo $add_shop['product_image'] ?>" title="<?php echo $add_shop['product_name'] ?>"  /></div>
											<p class="store-item-name"><?php echo $add_shop['product_name'] ?></p>
										</div>
										<h3 class="store-item-price"><?php echo $add_shop['product_price'] ?> <i class="fa fa-diamond" aria-hidden="true"></i></h3>
										<nav class="cv-button-group" style="text-align:right;">
											<a class="cv-button button is-ghost" data-toggle="modal" data-target="#store-item-detail<?php echo $add_shop['id'] ?>-popup" href="#">DETAILS</a>
											<?php include load_page('shop/shop-detail-modal.php'); 
											if($add_shop['redirect_url'] != '') {
												echo '<a class="cv-button button redirect" href="'.$add_shop['redirect_url'].'" >Redirect me</a>';
											} else {
												echo '<button type="submit" name="item" value="'.$add_shop['id'].'" class="cv-button button" >+ ADD TO CART</button>';
											}
											?>
										</nav>
									</div>
								</div>
								<?php 
									}
								}
								else 
								{
									echo "<p>There are no cosmetic items in store right now, keep checking in regularly.</p>";
								}
								?>
							</div>
						</div>
					</div>
					<div class="tab-pane pane" role="tabpanel" id="subscription">
						<div class="pane-content">
							<div class="column-row">
								<?php 
								if($db->num_rows($shop_item_subscription) > 0) 
								{
									while($add_shop = $db->fetch_assoc($shop_item_subscription)) 
									{
								?>
								<div class="col-item col-sm-4 content-l-side">
									<div class="column-inner">
										<div class="store-thumb">
											<span class="info-icon-details" data-toggle="modal" data-target="#store-item-detail<?php echo $add_shop['id'] ?>-popup"><i class="fa fa-info-circle" aria-hidden="true"></i></span>
											<div class="store-thumb-holder">
											<img class="store-thumb-img" src="<?php echo $add_shop['product_image'] ?>" title="<?php echo $add_shop['product_name'] ?>"  /></div>
											<p class="store-item-name"><?php echo $add_shop['product_name'] ?></p>
										</div>
										<h3 class="store-item-price"><?php echo $add_shop['product_price'] ?> <i class="fa fa-diamond" aria-hidden="true"></i></h3>
										<nav class="cv-button-group" style="text-align:right;">
											<a class="cv-button button is-ghost" data-toggle="modal" data-target="#store-item-detail<?php echo $add_shop['id'] ?>-popup" href="#">DETAILS</a>
											<?php include load_page('shop/shop-detail-modal.php'); 
											if($add_shop['redirect_url'] != '') {
												echo '<a class="cv-button button redirect" href="'.$add_shop['redirect_url'].'" >Redirect me</a>';
											} else {
												echo '<button type="submit" name="item" value="'.$add_shop['id'].'" class="cv-button button" >+ ADD TO CART</button>';
											}
											?>
										</nav>
									</div>
								</div>
								<?php 
									}
								}
								else 
								{
									echo "<p>There are no subscription items for sale at the moment!</p>";
								}
								?>
							</div>
						</div>
					</div>
				</div>
				</form>
			</div>
		</div>
	</div>
</div>