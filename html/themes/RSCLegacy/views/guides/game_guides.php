<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>

<div class="panel panel-default">
	<div class="content-header content-header--highlight">
		<h2 class="content-header-title">Play Guides</h2>
	</div>
	<div class="embended-info">
		<p>
		<i class="fa fa-info-circle" aria-hidden="true"></i> 
		Let the adventure start - we have created a couple of Play Guides to ease your game play.
		</p>
	</div>
	<p class="guide-version">Guides last updated: 2016-09-11</p>
	<div class="embended-add">
		<p><span><strong><i class="fa fa-spinner" aria-hidden="true"></i> Getting started</span></strong></p>
	</div>
	<div class="panel-body">
		<div class="col-sm-12">
			<div class="area_inner">
			<p class="">Click on the button below to start downloading the client to your device (PC / Android). </p>
			<p class="">When download is complete please double click on the Executable Jar or the APK to start the client. For more information on how to get the client to start please scroll down to <a href="guide.php?m=game_guide#gamePlay">Download & Run section</a>.</p>
			<h4 class="sub_headline">Client Download - Wolf Kingdom</h4>
			<table cellspacing="0" class="table table-bordered">
				<tbody><tr>
					<th>PC (Windows/Linux/Mac)</th>
					<td><a href="RSCLauncher.jar" class="bt_silverwhite"><i class="fa fa-windows" aria-hidden="true"></i> <i class="fa fa-apple" aria-hidden="true"></i> <i class="fa fa-linux" aria-hidden="true"></i> PC - Windows / Mac / Linux</a></td>
				</tr>
				<tr>
					<th>Android Devices</th>
					<td><a href="android/wolfkingdom.apk" class="bt_silverwhite"><i class="fa fa-android" aria-hidden="true"></i> Android (APK)</a></td>
				</tr>
			</tbody></table>
			</div>
		</div>
	</div>
	<div class="embended-add" id="gamePlay">
		<p><span><strong><i class="fa fa-book" aria-hidden="true"></i> Gameplay Guides</span></strong></p>
	</div>
	<div class="panel-body">
		<div class="col-sm-12">
			<div class="area_inner">
			<p class="">The gameplay guide includes information on a number of important topics such as download & running the game, creating a character, controls, and navigation of RSC Client interface.</p>
			<p class="">Additional information can also be found via the <a href="#">Frequently asked questions (FAQ)</a>.</p>
			<div class="panel-group" role="tablist" id="GameGuide" aria-multiselectable="true"> 
				<?php 
				if($db->num_rows($find_gameplay_guides) > 0 ) {
					$id = 1;
					while($gp = $db->fetch_assoc($find_gameplay_guides)) { ?>
					<div class="panel panel-default"> 
						<div class="panel-heading" role="tab" id="guide<?php echo $id ?>"> 
							<h4 class="panel-title"> 
								<a data-target="#gamePlay<?php echo $id ?>" role="button" data-toggle="collapse" data-parent="#gamePlay<?php echo $id ?>" aria-expanded="false" aria-controls="#gamePlay<?php echo $id ?>" class="collapsed"> 
								<?php echo $id . '. ' . $gp['title'] ?>
								</a> 
							</h4> 
						</div> 
						<div class="panel-collapse collapse" role="tabpanel" id="gamePlay<?php echo $id ?>" aria-labelledby="guide<?php echo $id ?>" aria-expanded="false" style="height: 0px;"> 
							<div class="panel-body"> 
									<?php echo parse_message($gp['post']) ?>
							</div> 
						</div> 
					</div> 
					<?php 
					$id++;
					} 
				}?>
			</div>
			</div>
		</div>
	</div>
	<div class="embended-add">
		<p><span><strong><i class="fa fa-book" aria-hidden="true"></i> Game Features</span></strong></p>
	</div>
	<div class="panel-body">
		<div class="col-sm-12">
			<div class="area_inner">
			<p class="">Guides with detailed explanations of the various features available in RSCLegacy.</p>
			<div class="panel-group" role="tablist" id="GameGuide" aria-multiselectable="true"> 
				<?php 
				if($db->num_rows($find_gamefeature_guides) > 0 ) {
					$id = 1;
					while($gf = $db->fetch_assoc($find_gamefeature_guides)) {					
					?>
					<div class="panel panel-default"> 
						<div class="panel-heading" role="tab" id="guide<?php echo $id ?>"> 
							<h4 class="panel-title"> 
								<a data-target="#gameFeature<?php echo $id ?>" role="button" data-toggle="collapse" data-parent="#gameFeature<?php echo $id ?>" aria-expanded="false" aria-controls="#gameFeature<?php echo $id ?>" class="collapsed"> 
								<?php echo $id . '. ' . $gf['title'] ?>
								</a> 
							</h4> 
						</div> 
						<div class="panel-collapse collapse" role="tabpanel" id="gameFeature<?php echo $id ?>" aria-labelledby="guide<?php echo $id ?>" aria-expanded="false" style="height: 0px;"> 
							<div class="panel-body"> 
									<?php echo parse_message($gf['post']) ?>
							</div> 
						</div> 
					</div> 
					<?php 
					$id++;
					} 
				}?>
			</div>
			</div>
		</div>
	</div>
</div>
