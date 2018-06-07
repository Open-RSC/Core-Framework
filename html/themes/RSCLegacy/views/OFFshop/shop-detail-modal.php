<?php
// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;

?>
<div class="modal fade modal-form" id="store-item-detail<?php echo $add_shop['id'] ?>-popup" tabindex="-1" role="dialog" aria-hidden="true">
	<div class="modal-dialog modal-xs">
		<div class="popup">
			<a href="#" class="close" data-dismiss="modal">× CLOSE DETAILS</a>
			<form id="add_cart" method="post" enctype="multipart/form-data" action="shop.php?token=<?php echo $_SESSION["curr_token"]; ?>&amp;char=<?php echo $selected_char; ?>" onsubmit="return process_form(this)">
				<div class="content-section-detail">
					<fieldset class="column-split col-sm-5">
						<div class="image-wrapper">
							<img class="modal-user-image" alt="" src="<?php echo $add_shop['product_image'] ?>">
						</div>
                    </fieldset>
					<fieldset class="column-split col-sm-7">
						<h4 style="font-weight: bold;text-align:left;"><?php echo $add_shop['product_name'] ?></h4>
                        <span class="help-block" style="text-align:left;"><?php echo parse_message($add_shop['product_desc']) ?></span>
						<h2 style="text-align: right; font-weight: bold;"><?php echo $add_shop['product_price'] ?> <i class="fa fa-diamond" aria-hidden="true"></i></h2>
						<nav class="cv-button-group" style="text-align:right;">
						<?php
						if($add_shop['redirect_url'] != '') {
							echo '<a class="cv-button button redirect is-large" href="'.$add_shop['redirect_url'].'" >Go To Service</a>';
						} else {
							echo '<button type="submit" class="cv-button button is-large" name="item" value="'.$add_shop['id'].'">+ ADD TO CART</button>';
							
						}
						?>
						</nav>
					</fieldset>
				</div>
			</form>
		</div>
	</div>
</div>
