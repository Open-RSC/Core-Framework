<?php

// Make sure no one attempts to run this view directly.
if (!defined('FORUM'))
	exit;
?>
<div class="col-sm-8 char-r-side">
	<div class="panel panel-default">
		<div class="content-header content-header--highlight">
			<h2 class="content-header-title achievement">Achievements</h2>
		</div>
		<div class="achievement-info">
			<div class="achievement-history col-sm-6 col-xs-12">
				<p><strong>Achievement History</strong></p>
				<p>History and progress of your achievements, each achievement is listed with a description and reward.</p>
			</div>
			<div class="achievement-progress col-sm-6 col-xs-12">
			<span><?php echo get_achievement_percentage($total_completed, $total_achievements, true) ?> achievements earned:</span>
				<div class="progress">
				  <div class="progress-bar" role="progressbar" aria-valuenow="<?php echo get_achievement_percentage($total_completed, $total_achievements, false) ?>"  aria-valuemin="0" aria-valuemax="100" style="width:<?php echo get_achievement_percentage($total_completed, $total_achievements, false) ?>%">
				  </div>
				</div>
			</div>
		</div>
		<div class="panel-body">
			<div class="view_achievements">
				<div class="char_box">
					<ul class="achievement_list">
					<?php while($achievement = $db->fetch_assoc($achievement_result)) { 
					?>
					<li>
						<div class="achievement_area">
							<div class="achievement_body <?php echo ($achievement['completed'] == 1 ? 'achievement-complete' : '') ?>">
								<div class="achievement_icon">
								<a href="#">
									<img src="http://img.finalfantasyxiv.com/lds/pc/global/images/itemicon/e0/e01f87a100e95d127de007e5b56d17878afb3ef4.png?1463979253" width="40" height="40" alt="">
								</a>
								</div>
								<div class="achievement_txt">
									<div class="achievement_name_date">
									<?php echo $achievement['name'] ?>
									<span id="achievement_date" class="pull-right"><?php echo ($achievement['completed'] == 1 ? "Completed @ ".format_time($achievement['unlocked'], true, "j/m/Y") : 'Unfinished')?></span>
									</div>
									<div class="achievement_desc">
									<?php echo $achievement['description'] ?>
									<?php if($achievement['extra'] != NULL): ?>
									<span id="achievement_extra">
										<button class="btn btn-rounded pull-right" data-toggle="collapse" data-target="#achievement_extra_info<?php echo $achievement['dbid']?>">Read more</button>
									</span>
									<?php endif; ?>
									</div>
								</div>
								<div id="achievement_extra_info<?php echo $achievement['dbid'] ?>" class="collapse">
									<div class="achievement_exra_full">
										<p><?php echo $achievement['extra'] ?></p>
									</div>
								</div>
							</div>
						</div>
					</li>
					<?php } ?>
					</ul>
				</div>
			</div>
		</div>
	</div>
</div>