<?php
define('LUNA_ROOT', './');
require LUNA_ROOT.'include/common.php';
require LUNA_ROOT.'include/parser.php';
require LUNA_ROOT.'include/shop_container.php';

//[Anti CSRF]
// Start A Session
session_start();

// Generate a random string and the present time
$token_random = substr(hash('sha256', str_shuffle(mt_rand() . microtime() . $_SERVER["REMOTE_ADDR"])), 0, 20);
$token_generated = time();

// Set two session variables to hold the random string and time, once set they are static.
$_SESSION["curr_token"] = empty($_SESSION["curr_token"]) ? $token_random : $_SESSION["curr_token"];
$_SESSION["curr_token_time"] = empty($_SESSION["curr_token_time"]) ? $token_generated : $_SESSION["curr_token_time"];

// If the session token is 30 minutes old, generate new information and empty cart.
if($_SESSION["curr_token_time"] < time() - (60 * 30))
{
	$_SESSION["curr_token"] = $token_random;
	$_SESSION["curr_token_time"] = $token_generated;
	unset($_SESSION['cart']);	
}
// Validate the token being passed via url.
$is_valid_token = isset($_GET["token"]) && $_GET["token"] == $_SESSION["curr_token"] ? true : false;

//End [Anti CSRF]

// Did they select a char? Did they not mess with the url?
$selected_char = isset($_GET["char"]) && is_numeric($_GET["char"]) ? intval(trim($_GET["char"])) : null;

//the variable for item POST.
$cart_item = isset($_POST['item']) && is_numeric($_POST['item']) ? intval(trim($_POST['item'])) : null;

// Check for the users characters attached to forum account.
$find_user_characters = $db->query("SELECT username, id FROM " . GAME_BASE . "players WHERE owner = '" . $luna_user["id"] . "'");

// Attempt to find a matching record of the selected char, for jewel and username displayal, as well as joining inventory and bank queries.
$find_selected_char = $db->query("SELECT username, online FROM " . GAME_BASE . "players WHERE owner='" . $luna_user["id"] . "' AND id = '" . $db->escape($selected_char) . "'") or die('bad selector query....');	
$selected_char_valid = $db->num_rows($find_selected_char);

//generated token and character selected boolean.
$tokenCharIsValid = isset($selected_char) && $is_valid_token && $selected_char_valid ? true : false;

$page = isset($_GET['m']) ? $_GET['m'] : null;

$errors = array();
$success = array();

$curr_char_info = $db->fetch_assoc($find_selected_char);
//Start adding item to CART Session
if(isset($_POST['item'])) 
{
	confirm_referrer('shop.php');
	
	$result = $db->query("SELECT id, item_id, product_name, product_image, product_price, redirect_url FROM shop WHERE id = '".$db->escape($cart_item)."'");
	if(!$db->num_rows($result) || !$tokenCharIsValid) 
		message(__('Bad request. The session was either closed, you did not select a character or invalid request! Please <a href="shop.php">click here</a> to reload the shop.', 'luna'), false, '404 Not Found');
	
	
	$product = mysqli_fetch_object($result);
	
	if($product->redirect_url != '') 
		message(__('This service is not a cart item, click on the "go to service" button to get redirected to it\'s actual page.', 'luna'), false, '404 Not Found');
	
	$item = new ShopContainer();
	$item->id = $product->id;
	$item->item_id = $product->item_id;
	$item->name = $product->product_name;
	$item->image = $product->product_image;
	$item->price = $product->product_price;
	$item->quantity = 1;
	
	//Does product exist in cart?
	$index = -1;
	$cart = unserialize(serialize($_SESSION['cart']));
	for($i = 0; $i < count($cart); $i++)
		if($cart[$i]->id == $cart_item) 
		{
			$index = $i;
			break;
		}
		if($index == -1) 
			$_SESSION['cart'][] = $item;
		else 
		{
			if($cart[$index]->quantity < 30)
			{
				$cart[$index]->quantity++;
				$_SESSION['cart'] = $cart;
			}
		}
		
	redirect('shop.php?token='.$_SESSION["curr_token"].'&amp;char='.$selected_char.'');
}
elseif(isset($_GET['index_remove'])) 
{
	confirm_referrer('shop.php');
	
	if(!$tokenCharIsValid)
		message(__('Bad request. The session was either closed or invalid request! Please <a href="shop.php">click here</a> to reload the shop.', 'luna'), false, '404 Not Found');
	
	$cart = unserialize(serialize($_SESSION['cart']));
	unset($cart[$_GET['index_remove']]);
	$cart = array_values($cart);
	$_SESSION['cart'] = $cart;
	
	redirect('shop.php?'.($page == 'checkout' ? "m=checkout&amp;" : "").'token='.$_SESSION["curr_token"].'&amp;char='.$selected_char.'');
}
elseif(isset($_GET['empty_cart'])) 
{
	confirm_referrer('shop.php');
	
	if(!$tokenCharIsValid)
		message(__('Bad request. The session was either closed or invalid request! Please <a href="shop.php">click here</a> to reload the shop.', 'luna'), false, '404 Not Found');
	
	unset($_SESSION['cart']);	
	
	redirect('shop.php?token='.$_SESSION["curr_token"].'&amp;char='.$selected_char.'');
}
elseif(isset($_POST['final_checkout']) && $page == 'checkout') 
{
	confirm_referrer('shop.php');
	
	if(!$tokenCharIsValid)
		message(__('Bad request. The session was either closed or invalid request! Please <a href="shop.php">click here</a> to go back to the shop.', 'luna'), false, '404 Not Found');
	
	if((!isset($_SESSION['cart']) && empty($_SESSION['cart']))) 
		message(__('Bad request. Your cart has either expired or is empty. <a href="shop.php">Click here</a> to back to shop', 'luna'), false, '404 Not Found');
	
	$cart = unserialize(serialize($_SESSION['cart']));
	$sum = 0;
	$item_index = 0;
	for($i = 0; $i < count($cart); $i++)
	{
		$sum += $cart[$i]->price * $cart[$i]->quantity;
		if($cart[$i]->name != 'Character Slot')
			$item_index++;
	}
	
	if($sum > 0) 
	{
		if($luna_user['jewels'] >= $sum) 
		{
			$character_check = $db->query("SELECT id FROM ".GAME_BASE ."players WHERE id = ".$db->escape($selected_char)." AND owner = ".$luna_user['id']) or error('Unable to check if character belongs to forum owner', __FILE__, __LINE__, $db->error());;
			$myChar = $db->num_rows($character_check);
			if($myChar == 1) 
			{
				if($curr_char_info['online'] == 1) 
				{
					$errors[] = "Please logout your character from game. Your character has to be logged out during the whole process for a safe purchase.";
				}
				
				$bank = $db->query("SELECT id FROM ".GAME_BASE ."bank WHERE playerID = ".$db->escape($selected_char)) or error('Unable to check for bank space', __FILE__, __LINE__, $db->error());;
				$bank_space = $db->num_rows($bank);
				if($bank_space + $item_index > 192) 
				{
					$errors[] = "Your characters bank is too full! Please make some free space for new items!";
				}
				
				if(count($errors) == 0)
				{
					for($i = 0; $i < count($cart); $i++)
					{
						if($cart[$i]->name == 'Character Slot') 
						{
							$db->query("UPDATE users SET character_slots = character_slots + ". $cart[$i]->quantity ." WHERE id = ".$luna_user['id']);
							$success[] = "\t\t\t\t\t".$cart[$i]->quantity."x additional character slot".($cart[$i]->quantity > 1 ? "s" : "")." has been added to your forum account!";	
						}
						else 
						{
							$bank_item_lookup = $db->query("SELECT id FROM ".GAME_BASE ."bank WHERE playerID = ".$db->escape($selected_char)." AND id = ".$cart[$i]->item_id) or error('Unable to check if item already exist', __FILE__, __LINE__, $db->error());;
							$has_item = $db->num_rows($bank_item_lookup);
							if($has_item > 0) {
								$db->query("UPDATE ".GAME_BASE ."bank SET amount = amount  + ". $cart[$i]->quantity ." WHERE playerID = ".$db->escape($selected_char)." AND id = ".$cart[$i]->item_id) or error('Unable to update bank with the new items', __FILE__, __LINE__, $db->error());;
							} else {
								$db->query("INSERT INTO ".GAME_BASE ."bank (playerID, id, amount, slot) VALUES (".$selected_char.", '".$cart[$i]->item_id."', '". $cart[$i]->quantity."', '".$bank_space++."')") or error('Unable to insert new items to bank', __FILE__, __LINE__, $db->error());;
							}
							$success[] = "\t\t\t\t\t". $cart[$i]->quantity ."x ". $cart[$i]->name ."".($cart[$i]->quantity > 1 ? "s" : "")." has been deposited in ".ucwords($curr_char_info['username'])."'s bank.";	
						}
						//insert shop logs.
						$db->query('INSERT INTO '.$db->prefix.'shop_logs (package, product_id, price, quantity, creation, forum_name, game_name) VALUES(\''.$db->escape($cart[$i]->name).'\', '.intval($cart[$i]->item_id).', '.intval($cart[$i]->price).', '.intval($cart[$i]->quantity).', '.time().', \''.$db->escape($luna_user['username']).'\', \''.$db->escape($curr_char_info['username']).'\')') or error('Unable to create shop log!', __FILE__, __LINE__, $db->error());
						//add user notification
						new_notification($luna_user['id'], 'shop.php', __('Package: '.$cart[$i]->name.' x: '.$cart[$i]->quantity.' Jewels: '.$cart[$i]->price * $cart[$i]->quantity.' Char: '.ucwords($curr_char_info['username']).'', 'luna'), 'fa fa-diamond');
					}
					//restore cart after purchase!
					$db->query("UPDATE users SET jewels = jewels - ".$sum." WHERE id=".$luna_user['id']) or error('Unable to update jewel count', __FILE__, __LINE__, $db->error());	
					unset($_SESSION['cart']);
				}
			} 
			else 
			{
				$errors[] = "You are not the owner of this character!";
			}
		} 
		else 
		{
			$errors[] = "You don't have enough jewels! Please donate for more jewels <a href='donate.php'>click here</a>.";
		}
	}
}

$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), "Shop");
define('LUNA_ALLOW_INDEX', 1);
require load_page('header.php');
?>
<div id="wrapper" class="container">
	<div class="character" id="shop">
		<?php 
		switch($page) 
		{
			case "checkout":
				require load_page('shop/checkout.php');
				break;
			default:
				require load_page('shop/cart.php');
				
				// Shop listings.
				$shop_item_service = $db->query("SELECT id, product_name, redirect_url, product_image, product_desc, product_price FROM shop WHERE product_category = '0' ORDER BY product_price DESC");
				$shop_item_cosmetic = $db->query("SELECT id, product_name, redirect_url, product_image, product_desc, product_price FROM shop WHERE product_category = '1' ORDER BY product_price DESC");
				$shop_item_subscription = $db->query("SELECT id, product_name, redirect_url, product_image, product_desc, product_price FROM shop WHERE product_category = '2' ORDER BY product_price ASC");
				require load_page('shop/shop.php');
				break;
		}
		?>
	</div>
</div>
<?php
require load_page('footer.php');
