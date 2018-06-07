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
					<p>Download RuneScape Classic Legacy on your PC or Android device, by clicking the image below.</p>
					<p class="register_now">Not registered? <a href="register.php">Create an account</a>.</p>
					<hr />
					<div class="game_clients">
						<div class="btn-group">
							<div class="col-sm-6">
								<a href="RSCLauncher.jar">
									<img src="img/icons/download_box.png" width="223" height="80" alt="Game Download">
								</a>
								<p class="download_info">Download the executable RSCLauncher - <em>PC</em>.</p>
							</div>
							<div class="col-sm-6">
								<a href="android/wolfkingdom.apk">
									<img src="img/icons/android_box.png" width="223" height="80" alt="Game Download">
								</a>
								<p class="download_info">Android application (APK) <em>Android Devices</em>.</p>
							</div>
						</div>
					</div>
				</div>
				<div class="col-sm-4 right-check">
					<div class="dl_requirements">
						<h2><em>RSCLegacy</em> Requirements</h2>
                        <p class="title"><strong>PC Client:</strong></p>
                        <p><span class="requirements">Requirements:</span></p>
                        <ul>
                            <li><strong>Operating System:</strong> Linux, Windows 98 or Higher, Mac OSX</li>
                            <li><strong>CPU:</strong> Intel Pentium III or compatible at 800 MHz or higher</li>
                            <li><strong>Memory:</strong> 500mhz minimum, 1 GB or more recommended</li>
							<li><strong>Required software:</strong> <a href="http://www.java.com">Latest Version Of Java</a></li>
                        </ul>
                        <p class="title"><strong>Android Application:</strong></p>
                        <p><span class="requirements">Requirements:</span></p>
                        <ul>
                            <li><strong>Device:</strong> Android 3.0 or higher</li>
							<li><strong>Memory:</strong> 512MB of RAM</li>
							<li><strong>Permissions:</strong> <p>Internet-access</p><p>Install-packages (application self-updating)</p> </li>
                        </ul>
                    </div>
				</div>
				<div class="col-sm-12">
					<hr />
					<div class="download-sizes">
						<span class="download-size" id="download-size-pc">PC: 645 Kb</span><span class="download-size" id="download-size-android">Android: 598 Kb</span>
					</div>
					<p class="download-terms">By downloading this software you agree to our <a href="terms.php" target="_blank">Terms of Service</a>.</p>
					</DIV>
			</div>
		</div>
	</div>
</div>
<?php
require load_page('footer.php');
