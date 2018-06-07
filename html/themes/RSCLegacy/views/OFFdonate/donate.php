<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<ol class="breadcrumb">
	<li class="active">1. Donator: <?php echo luna_htmlspecialchars($luna_user['username']) ?></li>
	<li class="active"><a href="donate.php">2. Select Amount</a></li>
	<li>3. Thank You</li>
</ol>
<div class="don_container">
	<div class="col-sm-12">
		<h4 class="imp-header-bar">
			<span class="number">1. </span>Information				
		</h4>
		<div class="head">Donation Package</div>
		<p>Select the package of Jewels you are interested in buying, which will take you to the <strong>PayPal checkout</strong>. Once you have paid using PayPal, you will instantly have access to the Jewels in your forum account. Please note, jewels are tied to your forum account, you will be able to use them on any character you have added to your forum account. With Jewels, you can subscribe for faster experience rates, add more characters to your forum account, buy cosmetic items in-game, and much more. </p>
		<div class="head">Teleport Stones</div>
		<p>Additionally, you will get <strong>Teleport Stones</strong> with most orders, as shown below. Each teleport stone allows you to freely teleport to known towns as well as custom areas in-game. This will allow you to save some precious time. Each teleport stone is non-tradeable and is stored in your inventory or bank as an item.</p>
		<div class="head">Important</div>
		<p>We offer absolutely no refunds which means if your character(s) gets banned for breaking our rules there is nothing we can do. Any attempt to charge back your donation will result in an immediate deletion on all associated accounts.</p>
	</div>
	<div class="col-sm-8 left-check">
		<h4 class="header-bar donate-amount">
			<span class="number">2. </span>Select Amount				
		</h4>
		<ul id="rewards">
			<?php
			foreach($donationOffers as $key => $value):
			?>
			<div class="col-sm-4">
			<li class="reward" data-value="<?php echo $value['Price'] ?>">
				<div class="game-jewels">
					<p class="pack_amount">
					<span class="jewel-icon" style="background-image: url(&quot;img/icons/jewel_currency.png&quot;);"></span>
					<span class="value"><?php echo $value['Jewel'] ?></span>
					<?php 
					if($value['Stone'] != ''): 
					?>
					<span class="tpc"><?php echo "+ " . $value['Stone'] . " Teleport Stones" ?></span>
					<?php
					endif;
					?>
					</p>
				</div>
				<div class="donate_currency">
					<span class="pack_amount">$<?php echo number_format($value['Price'], 2, '.<span class="double">', '</span>') ?></span>
				</div>
			</li>
			</div>
			<?php
			endforeach;
			?>
		</ul>
		<form id="paypal" action="https://www.paypal.com/cgi-bin/webscr" method="POST">
			<input type="hidden" name="cmd" value="_donations" /> 
			<input type="hidden" name="business" value="service@wolfkingdom.net" />
			<input type="hidden" name="item_name" value="RSCLegacy Jewels" />
			<input type="hidden" name="amount">
			<input type="hidden" name="no_shipping" value="1" /> 
			<input type="hidden" name="no_note" value="1" /> 
			<input type="hidden" name="currency_code" value="USD" /> 
			<input type="hidden" name="lc" value="US" /> 
			<input type="hidden" name="bn" value="PP-DonationsBF:btn_donate_LG.gif:NonHostedGuest" /> 
			<input type="hidden" name="custom" value="<?php echo $luna_user['id'] ?>" />
			<input type="hidden" name="return" value="http://wolfkingdom.net/donate.php?thank_you" /> 
			<input type="hidden" name="rm" value="2" /> 
			<input type="hidden" name="notify_url" value="http://wolfkingdom.net/paypal/ipn.php" />
		</form>
	</div>
	<div class="col-sm-4 right-check">
		<h4 class="header-bar">
			Donate Method				
		</h4>
		<ul class="checkout-list" id="checkout">
			<li class="donate_method paypal">
				<p class="method-name">Paypal</p>
				<span><img src="https://www.paypal.com/en_US/i/logo/PayPal_mark_37x23.gif"></span>
				<span><img src="https://secure.payproglobal.com/images/cards.png"></span>
				<p class="small-text">Remember, Jewels are a reward for donating which is the currency used to buy subscription tokens and other game services!</p>
			</li>
		</ul>
	</div>
	<div class="col-sm-12">
		<h4 class="imp-header-bar">
			<span class="number">3. </span>Agreement				
		</h4>
		<p>You agree by subscribing from this page that you are 100% donating your money to RSCLegacy and are receiving the tokens as a "reward gift" and "thank you" for your generous donation.</p>
		<p>By making this donation you also agree to our <a href="terms.php">Rules & Terms of Service</a>.</p>
	</div>
</div>