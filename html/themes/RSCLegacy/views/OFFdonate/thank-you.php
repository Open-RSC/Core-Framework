<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<ol class="breadcrumb">
	<li>1. Donator: <?php echo luna_htmlspecialchars($luna_user['username']) ?></li>
	<li><a href="donate.php">2. Select Amount</a></li>
	<li class="active">3. Thank You</li>
</ol>
<div class="container">
	<div class="well">
		<h4 class="imp-header-bar">
			<span class="number">3. </span>Thank you				
		</h4>
		<div class="head">RSCLegacy would like to thank you for donating!</div>
		<p>This is what keeps us together and the server alive. You are rewarded with Jewels for your generous donation, please visit our: <a href="shop.php">Shop</a></p>
	</div>
</div>