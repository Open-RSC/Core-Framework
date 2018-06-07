<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */
define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'admin');
define('LUNA_PAGE', 'trade_log');

require LUNA_ROOT.'include/common.php';
require LUNA_ROOT.'include/parser.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}
$query = $db->query("SELECT u.email, u.username FROM users AS u LEFT JOIN groups AS g ON g.g_id=u.group_id WHERE u.id>1 AND g.g_id != 0 ORDER BY u.username");
$total_query_count = $db->num_rows($query);

require 'header.php';	
?>
<div class="content_wrapper col-sm-10">
	<form method="get" action="trades.php">
		<div class="row title">
			<div class="small-12 columns">
				<h2 class="page-heading">Email list and Username (<?php echo number_format($total_query_count); ?>)</h2>
			</div>
		</div>
		<div class="small-12 columns">
		<?php
		if($total_query_count > 0)
		{	
		?>
		<table class="base-tbl">
			<tr>
				<th rowspan="1" colspan="1">Email;Username</th>
				<th rowspan="0" colspan="1"></th>
			</tr>
			<tbody role="alert" aria-live="polite" aria-relevant="all">
			<?php
				while($email_list = $db->fetch_assoc($query)) 
				{
					echo "
						<tr>
							<td>".$email_list['email'].";".$email_list['username']."</td>
						</tr>
						";
				}
			?>
			</tbody>
		</table>
		<?php } ?>
	</div>
	</form>
</div>
<?php
require 'footer.php';