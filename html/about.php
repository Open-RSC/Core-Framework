<?php
define('LUNA_ROOT', './');
require LUNA_ROOT.'include/common.php';

$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), "RSCLegacy Database");
define('LUNA_ALLOW_INDEX', 1);
require load_page('header.php');

$find_staff = $db->query('SELECT u.group_id, u.id, u.username, u.title, u.num_comments, u.registered, u.last_visit, g.g_id, g.g_user_title FROM ' . $db->prefix . 'users AS u LEFT JOIN ' . $db->prefix . 'groups AS g ON g.g_id=u.group_id WHERE u.group_id >= 1 AND u.group_id != 4 AND u.group_id != 3 AND u.group_id != 9 ORDER BY group_id ASC') or error('Unable to fetch user list', __FILE__, __LINE__, $db->error());
?>
<div id="wrapper" class="container">
	<div class="character">
	<div class="col-sm-8 content-l-side">
		<div id="rscl-about">
			<div class="panel panel-default">
				<div class="content-header content-header--highlight">
					<h2 class="content-header-title">About us</h2>
				</div>
				<div class="panel-body">
					<?php 
						echo "
						<strong>What is Open RSC?</strong>
						<p>Open RSC is a Runescape Classic Private Server hosted by our devoted team which is composed of dedicated players that been active since the first stage of the game. Our passion for Runescape Classic is what thrive us to be the best in the game.</p>
						<strong>Our experience</strong>
						<p>We have a decade of RSC private server development experience and near two decades of experience as RSC players. Our team consists of <font style='color: yellow !important;font-weight: bold;'>Administrators</font>, <font style='color: red !important;font-weight: bold;'>Game Developer</font>, <font style='color: red !important;font-weight: bold;'>Web Developer</font>, <font style='color: lightblue !important;font-weight: bold;'>Graphic Artist</font> and several <font style='color: silver !important;font-weight: bold;'>Moderators</font>.</p>
						<strong>The future</strong>
						<p>We want to match the original game as close as possible and provide it completley free to anyone who wants to join in the development or host their own version.</p>
						";
					?>
				</div>
			</div>
		</div>
		<div id="rscl-faqs">
			<div class="panel panel-default">
				<div class="content-header content-header--highlight">
					<h2 class="content-header-title">Frequently asked questions (FAQ)</h2>
				</div>
				<div class="panel-body">
					 <div class="panel-group" id="faqAccordion">
						<div class="panel panel-default ">
							<div class="panel-heading accordion-toggle question-toggle collapsed" data-toggle="collapse" data-parent="#faqAccordion" data-target="#question0">
								 <h4 class="panel-title">
									<a href="#rscl-faqs" class="ing">Q: What is Open RSC?</a>
								</h4>
							</div>
							<div id="question0" class="panel-collapse collapse" style="height: 0px;">
								<div class="panel-body">
									<h5><span class="label label-rscl">Answer</span></h5>
									<p>Open RSC is a completely free of charge Runescape Classic private server designed to be just like the original game with no 3rd party additions.</p>
								</div>
							</div>
						</div>
						<div class="panel panel-default ">
							<div class="panel-heading accordion-toggle collapsed question-toggle" data-toggle="collapse" data-parent="#faqAccordion" data-target="#question1">
								 <h4 class="panel-title">
									<a href="#rscl-faqs" class="ing">Q: Do I have to pay money to play?</a>
								</h4>
							</div>
							<div id="question1" class="panel-collapse collapse" style="height: 0px;">
								<div class="panel-body">
									<h5><span class="label label-rscl">Answer</span></h5>
									<p>No absolutely not! We will always be free of charge. We don't believe in donations, subscriptions, or any exchange of money.</p>
								</div>
							</div>
						</div>
						<div class="panel panel-default ">
							<div class="panel-heading accordion-toggle collapsed question-toggle" data-toggle="collapse" data-parent="#faqAccordion" data-target="#question2">
								 <h4 class="panel-title">
									<a href="#rscl-faqs" class="ing">Q: Can I play the whole content without investing money?</a>
								</h4>
							</div>
							<div id="question2" class="panel-collapse collapse" style="height: 0px;">
								<div class="panel-body">
									<h5><span class="label label-rscl">Answer</span></h5>
									<p>The whole game itself can be played to the fullest without having to invest real money.</p>
								</div>
							</div>
						</div>
						<div class="panel panel-default ">
							<div class="panel-heading accordion-toggle collapsed question-toggle" data-toggle="collapse" data-parent="#faqAccordion" data-target="#question3">
								 <h4 class="panel-title">
									<a href="#rscl-faqs" class="ing">Q: What can I expect from Open RSC?</a>
								</h4>
							</div>
							<div id="question3" class="panel-collapse collapse" style="height: 0px;">
								<div class="panel-body">
									<h5><span class="label label-rscl">Answer</span></h5>
									<p>You can expect 99.9% uptime, constant development, active and fair staff and all the nostalgia that come with playing the game. Designed to replicate the original version as close as possible. No pay to win, no subscriptions, no donations, no in-game government to hassle anyone.</p>
								</div>
							</div>
						</div>
						<div class="panel panel-default ">
							<div class="panel-heading accordion-toggle collapsed question-toggle" data-toggle="collapse" data-parent="#faqAccordion" data-target="#question4">
								 <h4 class="panel-title">
									<a href="#rscl-faqs" class="ing">Q: Can I use a third party program (bot) that will keep my account playing for me while I am away from the keyboard?</a>
								</h4>
							</div>
							<div id="question4" class="panel-collapse collapse" style="height: 0px;">
								<div class="panel-body">
									<h5><span class="label label-rscl">Answer</span></h5>
									<p>Do what ever you wish. No game staff will hassle you in game.</p>
								</div>
							</div>
						</div>
						<div class="panel panel-default ">
							<div class="panel-heading accordion-toggle collapsed question-toggle" data-toggle="collapse" data-parent="#faqAccordion" data-target="#question5">
								 <h4 class="panel-title">
									<a href="#rscl-faqs" class="ing">Q: How can I support development?</a>
								</h4>
							</div>
							<div id="question5" class="panel-collapse collapse" style="height: 0px;">
								<div class="panel-body">
									<h5><span class="label label-rscl">Answer</span></h5>
									<p>Just simply enjoy the game and tell everyone else to come play it! If you know how to program, we could use the help.</p>
								</div>
							</div>
						</div>
						<div class="panel panel-default ">
							<div class="panel-heading accordion-toggle collapsed question-toggle" data-toggle="collapse" data-parent="#faqAccordion" data-target="#question6">
								 <h4 class="panel-title">
									<a href="#rscl-faqs" class="ing">Q: Can I play from my phone?</a>
								</h4>
							</div>
							<div id="question6" class="panel-collapse collapse" style="height: 0px;">
								<div class="panel-body">
									<h5><span class="label label-rscl">Answer</span></h5>
									<!--<p>Yes, we support all Android devices, to download the .apk please visit this link: <a href="/android/wolfkingdom.apk">WolfKingdom.apk</a>.</p>-->
                                                                        <p>Not right now.</p>
								</div>
							</div>
						</div>
						<div class="panel panel-default ">
							<div class="panel-heading accordion-toggle collapsed question-toggle" data-toggle="collapse" data-parent="#faqAccordion" data-target="#question7">
								 <h4 class="panel-title">
									<a href="#rscl-faqs" class="ing">Q: I remember the holidays event on Runescape Classic, will you host these as well?</a>
								</h4>
							</div>
							<div id="question7" class="panel-collapse collapse" style="height: 0px;">
								<div class="panel-body">
									<h5><span class="label label-rscl">Answer</span></h5>
									<p>Yes, we intend to host the regular holiday events following real RuneScape Classic.</p>
								</div>
							</div>
						</div>
						<div class="panel panel-default ">
							<div class="panel-heading accordion-toggle collapsed question-toggle" data-toggle="collapse" data-parent="#faqAccordion" data-target="#question8">
								 <h4 class="panel-title">
									<a href="#rscl-faqs" class="ing">Q: Will there be server wipe?</a>
								</h4>
							</div>
							<div id="question8" class="panel-collapse collapse" style="height: 0px;">
								<div class="panel-body">
									<h5><span class="label label-rscl">Answer</span></h5>
									<p>No, we do not plan to have any server wipes. Your progress are permanent. We are having daily backups to ensure that we are not losing any players valuable time.</p>
								</div>
							</div>
						</div>
						<div class="panel panel-default ">
							<div class="panel-heading accordion-toggle collapsed question-toggle" data-toggle="collapse" data-parent="#faqAccordion" data-target="#question9">
								 <h4 class="panel-title">
									<a href="#rscl-faqs" class="ing">Q: Do you have achievements?</a>
								</h4>
							</div>
							<div id="question9" class="panel-collapse collapse" style="height: 0px;">
								<div class="panel-body">
									<h5><span class="label label-rscl">Answer</span></h5>
									<p>Yes we have an achievements system designed for Open RSC. Please visit your <a href="char_manager.php?id=<?php echo $luna_user['id'] ?>&setting=achievements">achievement history</a> </p>
								</div>
							</div>
						</div>
						<div class="panel panel-default ">
							<div class="panel-heading accordion-toggle collapsed question-toggle" data-toggle="collapse" data-parent="#faqAccordion" data-target="#question11">
								 <h4 class="panel-title">
									<a href="#rscl-faqs" class="ing">Q: How many play may I own?</a>
								</h4>
							</div>
							<div id="question11" class="panel-collapse collapse" style="height: 0px;">
								<div class="panel-body">
									<h5><span class="label label-rscl">Answer</span></h5>
									<p>As many as you want with a limit of 10 players per forum account.</p>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="staff-card">
		<div class="col-sm-4 char-r-side">
			<div class="panel panel-default">
				<div class="content-header content-header--highlight">
					<h2 class="content-header-title">Staff team</h2>
				</div>
				<div class="panel-body">
					<?php
					if ($db->num_rows($find_staff) > 0) {
						while ($r = $db->fetch_assoc($find_staff)) {
							echo '
								<div class="staff-card_panel">
									<div class="staff-card-head">
										<div class="forum-avatar">
										' . generate_avatar_markup($r["id"]) . '
										</div>
										<div class="staff-card_button">
											<div class="btn-group">
												<a class="btn btn-primary btn-sm" href="new_inbox.php?uid=' . $r['id'] . '">
													<span class="fa fa-envelope"></span> Contact
												</a>
											</div>
										</div>
									</div>
									<div class="staff-card-body">
										<h3 class="staff-name">' . user_append_rank(luna_htmlspecialchars($r['username']), $r['group_id']) . '</h3>
										<h3 class="staff-title">' . get_title($r) . '</h3>
										<div class="staff-info"><span class="staff-info_d">Posts:</span> <span class="staff-info_t">' . forum_number_format($r['num_comments']) . '</div>
										<div class="staff-info"><span class="staff-info_d">Registered:</span> <span class="staff-info_t">' . format_time($r["registered"]) . '</div>
										<div class="staff-info"><span class="staff-info_d">Last Online:</span> <span class="staff-info_t">' . format_time($r['last_visit']) . '</div>
									</div>
								</div>
							';
						}
					}
					?>
				</div>
			</div>
		</div>
	</div>
	</div>
</div>
<?php
require load_page('footer.php');
