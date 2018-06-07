<?php
define('LUNA_ROOT', './');
require LUNA_ROOT.'include/common.php';
require_once ('paypal/paypal_settings.php');

$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), "Donate");
define('LUNA_ALLOW_INDEX', 1);
require load_page('header.php');

// PAYPAL URL.
if ($settings ['sandbox'] == true) 
{
	$url = "https://www.sandbox.paypal.com/cgi-bin/webscr";
} 
else 
{
	$url = "https://www.paypal.com/cgi-bin/webscr";
}
?>
<script>
	$(function() {
	$('li.reward').on('click',function() {
	var content = $(this).data('value');
	$('input[name="amount"]').val(content);
		$(".col-sm-4").click(function() {
			$("#paypal").submit();
		});
	  });
	});
</script>
<div id="wrapper" class="container">
	<div class="character" id="donate">
		<?php 
		if ($luna_user ['is_guest']) 
		{
			echo "<p>You need to <a href='#' data-toggle='modal' data-target='#login-form'>login</a> in order to donate. If you don't have an account, you can register <a href='register.php'>here</a>.</p>";
		} 
		else 
		{
			if(isset($_GET['thank_you'])) 
			{
				require load_page('donate/thank-you.php');
			} 
			else 
			{
				require load_page('donate/donate.php');
			}
		}
		?>
	</div>
</div>
<?php
require load_page('footer.php');
