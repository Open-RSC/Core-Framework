<?php
/*
 * Created by Imposter 2016-07-07.
 */
define('LUNA_ROOT', dirname(__FILE__).'/');
require LUNA_ROOT.'include/common.php';

if ($luna_user['g_read_board'] == '0')
	message(__('You do not have permission to view this page.', 'luna'), false, '403 Forbidden');

$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), __('Download', 'luna'));
define('LUNA_ACTIVE_PAGE', 'download');
define('LUNA_ALLOW_INDEX', 1);
require load_page('header.php');

?>
<div id="wrapper" class="container">
	<div class="character" id="download">
		<div class="panel panel-default">
			<div class="content-header content-header--highlight">
			<h2 class="content-header_adv-title">Wolf Kingdom Clients</h2>
			</div>
			<div class="embended-info">
				<p></p><center>Version: 2.4 (14 Apr 2017)</center><p></p>
			</div>
			<div class="panel-body">
				<div class="col-sm-8 left-check">
					<p>Download Open RSC by clicking the image below.</p>
					<p class="register_now">Not registered? <a href="register.php">Create an account</a>.</p>
					<hr />
					<div class="game_clients">
						<div class="btn-group">
							<div class="col-sm-6">
								<a href="Open_RSC_Client.jar">
									<img src="img/icons/download_box.png" width="223" height="80" alt="Game Download">
								</a>
								<p class="download_info">Download the Open RSC game client.</p>
							</div>
						</div>
					</div>
				</div>		
			</div>
		</div>
	</div>
</div>
<?php
require load_page('footer.php');
