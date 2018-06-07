<?php
define('LUNA_ROOT', './');
require LUNA_ROOT.'include/common.php';
require LUNA_ROOT.'include/parser.php';

$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), "Hall Of Fame");
define('LUNA_ALLOW_INDEX', 1);
require load_page('header.php');

$result = $db->query("SELECT c.id AS cid, c.hof_name, e.accomplishment, e.username, e.time FROM hof_categories AS c INNER JOIN hof_entrys AS e ON c.id = e.category ORDER BY c.id,e.id ASC");
?>
<div id="wrapper" class="container">
	<div class="character">
		<div class="panel panel-default">
			<div class="content-header content-header--highlight">
			<h2 class="content-header_adv-title">Hall Of Fame</h2>
			</div>
			<div class="embended-info">
				<p><center>Listed below are the first players to accomplish some of the more impressive feats on RSCLegacy, as well as the accomplishments we are tracking that are yet to be achieved.</center></p>
			</div>
			<p class="guide-version">Last tracking: 2016-11-18</p>
			<div class="table_add">
				<div class="col-sm-8"><b style="color:#d49d1e;">Accomplishment</b></div>
				<div class="col-sm-2"><b style="color:#d49d1e;">Username</b></div>
				<div class="col-sm-2"><b style="color:#d49d1e;">Date</b></div>
			</div>
			<div class="panel-body">
				<table class="table bottomstraw">
				<tbody>
				<?php 
				if($db->num_rows($result)) 
				{	
					$cur_category = 0;
					while($disp_hof = $db->fetch_assoc($result)) 
					{
					if ($disp_hof['cid'] != $cur_category) {
					?>
					<tr style="height: 50px; line-height: 50px;color: #f1f1f1;">
						<td><b><?php echo $disp_hof['hof_name'] ?></b></td>
						<td></td><td></td>
					</tr>
					<?php 
					$cur_category = $disp_hof['cid'];
					}
					?>
					<tr style="height: 25px; line-height: 25px;">
						<td width="67%"><?php echo $disp_hof['accomplishment'] ?></td>
						<td width="16.5%"><?php echo ($disp_hof['username'] == '' ? "-" : $disp_hof['username']) ?></td>
						<td width="16.5%"><?php echo ($disp_hof['time'] == '' ? "-" : $disp_hof['time']) ?></td>
					</tr>
					<?php 
					}
				}
				?>
				</tbody>
				</table>
			</div>
		</div>
	</div>
</div>
<?php
require load_page('footer.php');
