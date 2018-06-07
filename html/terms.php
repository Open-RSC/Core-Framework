<?php
/*
 * Created by Imposter 2016-07-07.
 */
define('LUNA_ROOT', dirname(__FILE__).'/');
require LUNA_ROOT.'include/common.php';

if ($luna_user['g_read_board'] == '0')
	message(__('You do not have permission to view this page.', 'luna'), false, '403 Forbidden');

$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), __('Terms Of Service & Rules', 'luna'));
define('LUNA_ACTIVE_PAGE', 'terms');
define('LUNA_ALLOW_INDEX', 1);
require load_page('header.php');
?>
<div id="wrapper" class="container">
	<div class="misc-module">
		<section class="terms-body">
			<div class="item-header">
                <h1>RSCLEGACY - TERMS OF SERVICE</h1>
            </div>
			<div class="item-section">
			<p>
				<strong>
				PLEASE CAREFULLY READ THE FOLLOWING TERMS OF USE AGREEMENT (THE “TERMS OF USE” OR “AGREEMENT”).
				YOUR USE OF THE RSCLEGACY’ WEBSITE (INCLUDING ANY RELATED SUBSITE, SERVICE OR FEATURE THERETO) (THE “SITE”) CONSTITUTES YOUR AGREEMENT TO THESE TERMS, CONDITIONS, COVENANTS, POLICIES AND NOTICES (THE “TERMS”). IF YOU ARE UNDER THE AGE OF MAJORITY IN YOUR JURISDICTION, YOU MUST MAKE SURE THAT YOUR PARENT OR GUARDIAN ACCEPTS THIS AGREEMENT ON YOUR BEHALF PRIOR TO YOUR USE OF THE SITE. PLEASE PRINT A COPY OF THIS AGREEMENT FOR YOUR RECORDS. IF YOU DO NOT AGREE WITH ALL OF THE TERMS OF THIS AGREEMENT, YOU ARE NOT PERMITTED TO USE THIS SITE.
				</strong>
			</p>
			</div>
			<div class="item-section terms">
				<h2>1. THIRD PARTY SOFTWARE:</h2>
				<p>
				<span class="num">1.1</span>
				Use cheats, automation software (bots), hacks, modifications (mods) or any other unauthorized third-party software designed to modify the Game experience;
				</p>
				<p>
				<span class="num">1.2</span>
				Use the Site, Account, Game(s) (including Game-related Virtual Items, if any), Game Client(s) and/or Service in any way not expressly permitted by this Agreement. Without limiting the foregoing, you agree that you will not (a) institute, assist or become involved in any type of attack, including without limitation denial of service attacks, upon the Service or otherwise attempt to disrupt the Service or any other person’s use of the Service; or (b) attempt to gain unauthorized access to the Service, Accounts registered to other players, or the computer systems or networks connected to the Service;
				</p>
				<p>
				<span class="num">1.3</span>
				Use any unauthorized third-party software that intercepts, “mines,” or otherwise collects information from or through the Site, Account(s), Game(s) (including Game-related Virtual Items, if any), Game Client(s) and/or the Service, including without limitation any software that reads areas of RAM used by the Game(s) or the Service to store information about a character or the game environment; provided, however, that RSCLegacy may, in its sole and absolute discretion, allow the use of certain third party user interfaces.
				</p>
			</div>
			<div class="item-section terms">
				<h2>2. SALES POLICIES:</h2>
				<p>
				<span class="num">2.1</span>
				If you have elected to purchase any Game, product, service (including any subscription service) and/or Virtual Items through RSCLegacy’s commerce systems, upon your acceptance of these terms and submission of your order, you hereby agree that we have the right to automatically charge your credit card/paypal or debit your account for the applicable fees or charges, plus any applicable taxes we are required to collect, and you authorize us to do so. You agree to pay all fees incurred by you or anyone using an Account registered to you. You agree to notify RSCLegacy promptly of any changes to your credit or debit card account number, its expiration date and/or your billing address, and you agree to notify RSCLegacy promptly if your credit card expires or is canceled for any reason. After the submission of your order, you will receive an e-mail receipt from RSCLegacy providing details of your purchase. Please print a copy of such e-mail for your records.
				</p>
				<p>
				<span class="num">2.2</span>
				If you elect to redeem any promotional code in exchange for any products and/or services, you acknowledge that the applicable promotional code will then be permanently consumed and converted into the purchased product(s) and/or service(s). Unless otherwise specified, the cash value posted on the Store for any product or service being offered for redemption is inclusive of applicable sales taxes.
				</p>
				<p>
				<span class="num">2.3</span>
				RSCLegacy may revise the pricing for the goods and services offered, including without limitation subscription plans and/or Virtual Items for any Game, at any time.
				</p>
				<p>
				<span class="num">2.4</span>
				All charges made to your credit card or debits to your account can be viewed by you under the Transaction History section of RSCLegacy’s Account Management site.
				</p>
				<p>
				<span class="num">2.5</span>
				Any money transactions sent from you to the Authors of this Game, product, service will be considered a donation and will not be refunded under any circumstances.
				</p>
			</div>
			<div class="item-section terms">
				<h2>3. ACCOUNT SUSPENSION/DELETION:</h2>
				<p>
				<span class="num">3.1</span>
				RSCLEGACY MAY SUSPEND, MODIFY, TERMINATE OR DELETE ANY ACCOUNT AT ANY TIME FOR ANY REASON OR FOR NO REASON, WITH OR WITHOUT NOTICE TO YOU. Accounts terminated by RSCLegacy for any type of abuse, including without limitation a violation of these Terms of Use will not be reactivated for any reason. For purposes of explanation and not limitation, most account suspensions, terminations and/or deletions are the result of violations of this Terms of Use.
				</p>
			</div>
			<div class="item-header">
                <h1>RSCLEGACY - CHAT RULES</h1>
            </div>
			<div class="item-section">
			<p>
				<strong>
				Communicating on the Site, in a Game or through the Service with other users and RSCLegacy representatives, whether by text, voice or any other method, (collectively, “Chat”) is an integral part of the Game and the Service.
				</strong>
			</p>
			<p>
				<strong>
				You understand that, by using the Chat features, you may be exposed to communications (including in written, verbal, electronic, digital, machine-readable or other form) that you might find objectionable. You understand that any content sent or appearing through a Chat feature is the sole responsibility of the individual(s) transmitting such content. This means that you, and not RSCLegacy, are entirely responsible for all content that you transmit. Under no circumstances will RSCLegacy or its third party providers be liable for any errors or omissions in any content or for any loss or damages of any kind incurred as a result of the access to, downloading, viewing, listening, use of or inability to use any content, including User Content.
				</strong>
			</p>
			<p>
				<strong>
				When engaging in Chat, you may not:
				</strong>
			</p>
			</div>
			<div class="item-section terms">
				<p>
				<span class="num">1)</span>
				Transmit or post any content or language which, in the sole and absolute discretion of RSCLegacy, is deemed to be offensive, including without limitation content or language that is unlawful, harmful, threatening, abusive, harassing, defamatory, vulgar, obscene, hateful, sexually explicit, or racially, ethnically or otherwise objectionable, nor may you use a misspelling or an alternative spelling to circumvent the content and language restrictions listed above;
				</p>
				<p>
				<span class="num">2)</span>
				Carry out any action with a disruptive effect, such as intentionally causing the Chat screen to scroll faster than other users are able to read, or setting up macros with large amounts of text that, when used, can have a disruptive effect on the normal flow of Chat;
				</p>
				<p>
				<span class="num">3)</span>
				Disrupt the normal flow of dialogue in Chat or otherwise act in a manner that negatively affects other users, including without limitation posting commercial solicitations and/or advertisements for goods and services available outside of the Game universe;
				</p>
				<p>
				<span class="num">4)</span>
				Sending repeated unsolicited or unwelcome messages to a single user or repeatedly posting similar messages in a Chat area, including without limitation continuous advertisements to sell goods or services;
				</p>
				<p>
				<span class="num">5)</span>
				Communicate or post any user’s personal information in the Game, or on websites or forums related to the Game, except that a user may communicate his or her own personal information in a private message directed to a single user;
				</p>
				<p>
				<span class="num">6)</span>
				Harass, threaten, stalk, embarrass or cause distress, unwanted attention or discomfort to any user of the Game or the Site;
				</p>
				<p>
				<span class="num">7)</span>
				Participate in any action that, in the sole and absolute opinion of RSCLegacy, results or may result in an authorized user of the Game or the Site being “scammed” or defrauded out of gold, weapons, armor, or any other items that user has earned through authorized game play in the Game (including but not limited to participation in player-run casinos);
				</p>
				<p>
				<span class="num">8)</span>
				Impersonate any real person, including without limitation any “staff members” or any other RSCLegacy employee, nor may you communicate in the Game, or on the Site or Service in any way designed to make others believe that your message constitutes a Server message or was otherwise posted by any RSCLegacy employee.
				</p>
			</div>
			<div class="item-header">
                <h1>RSCLEGACY - FORUM RULES</h1>
            </div>
			<div class="item-section terms">
				<p>
				<span class="num">1)</span>
				<span class="item-text-f">Stay out of Real Life</span>
				Many people expect to keep personal and real life matters separate from those that happen on the Internet. We expect all forum members to respect eachother's privacy. You may not post any real life information about another user, including but not limited to, name and address, phone numbers, pictures, employers, associates, etc. This also extends to protect other forms of communication including IM Aliases and E-Mail addresses.
				<br /><br />
				Those that violate this rule will be banned for RL Information and ALL posts will be deleted as spam.
				</p>
				<p>
				<span class="num">2)</span>
				<span class="item-text-f">Server Negativity</span>
				Spreading negative propaganda about the server and serving no purpose other than to slander the community and it's staff will not be allowed. This goes hand in hand with trolling. The staff work hard to provide this experience to you, and we will not tolerate negative conjecture about our agendas or actions.
				</p>
				<p>
				<span class="num">3)</span>
				<span class="item-text-f">Content</span>
				Any threads that contain content unsafe for work should be reported to staff members so actions can be taken accordingly.
				</p>
				<p>
				<span class="num">4)</span>
				<span class="item-text-f">Avatars and Signatures</span>
				All Avatars and signatures must not contain any explicit content. Signatures must be no higher than 150 pixels, and no wider than 500 pixels. If there are multiple images, or images as well as text, the combination must not exceed the height of what a single 200 pixel image would cover.
				</p>
				<p>
				<span class="num">5)</span>
				<span class="item-text-f">Thread Titles</span>
				Thread Titles, regardless of the forum it is posted in, may not contain profanity or unacceptable language.
				</p>
				<p>
				<span class="num">6)</span>
				<span class="item-text-f">Ban Evasion</span>
				Forum accounts may not be created to circumvent a forum ban. Violations of this rule can lead to ingame disciplinary action under the Forum Behavior game rule.
				</p>
				<p>
				<span class="num">7)</span>
				<span class="item-text-f">Racism</span>
				Racism will not be tolerated and you will be permanently banned.
				</p>
				<p>
				<span class="num">8)</span>
				<span class="item-text-f">Attacks/Flames</span>
				Argue the point, not the poster. Do not attack, slander, or bash fellow forum users.
				</p>
				<p>
				<span class="num">9)</span>
				<span class="item-text-f">Gore or explicit content</span>
				Content is expected to remain work friendly. Do not post graphic/gross/disturbing images. This includes forum avatars and signatures.
				</p>
			</div>
			<div class="item-header">
                <h1>RSCLEGACY - GAME RULES</h1>
            </div>
			<div class="item-section terms">
				<p>
				<span class="num">1)</span>
				<span class="item-text-g">Trade Scams</span>
				Do not scam players in any way. As a player you are responsible for the safekeeping/loss of your items. Players caught scamming or attempting to scam will have their scammed items removed and/or all accounts involved will be banned.
				</p>
				<p>
				<span class="num">2)</span>
				<span class="item-text-g">Illegal Trades</span>
				Do not buy/sell RSCLegacy gold, items or accounts for real cash.
				<br /><br />
				This also applies for exchanging them for those of any other game. Players caught will be receive a permanent ban.
				</p>
				<p>
				<span class="num">3)</span>
				<span class="item-text-g">RMT (Real Money Trade) Sales</span>
				The involvement of any currency, items, or services external to RSCLegacy Server (Including but not limited to: real money (USD, Euro, Etc), regulated or otherwise (Bitcoins), Food/Delivery, Virtual currency of any other game, Beta Keys for the latest new game, etc... for any of that RSCLegacy server's virtual items or service (Including by not limited to: Items, Powerleveling, Accounts/Characters sales) is strictly forbidden.
				<br /><br />
				The economy of RSCLegacy is tracked very closely by the Game Masters and Management, and we take this type of behavior very seriously. Violation of this rule will often result in permanent bans for all accounts involved (Buyers, Sellers, Mules/Transferers).
				<br /><br />
				Offers/Solicitations to violate this rule, even jokingly, will also result in disciplinary action.
				</p>
				<p>
				<span class="num">4)</span>
				<span class="item-text-g">Account Phishing</span>
				Do not pretend to be any member of the RSCLegacy staff.
				<br /><br />
				Players found pretending to be a staff member will be permanently banned.
				</p>
				<p>
				<span class="num">5)</span>
				<span class="item-text-g">Macros/bots</span>
				Do not use bots when playing our server.
				<br /><br />
				Permanent bans will be given out to anyone who caught botting.
				</p>
				<p>
				<span class="num">6)</span>
				<span class="item-text-g">Harassment</span>
				Do not harass other players.
				<br />
				This usually involves repeatedly bothering a player with inappropriate behaviour and attempting to bypass their efforts to avoid you. Players that harass will get a temporary ban.
				</p>
				<p>
				<span class="num">7)</span>
				<span class="item-text-g">Language</span>
				Do not use inappropriate language. Players caught using inappropriate language on a regular basis will receive a temporary mute.
				</p>
				<p>
				<span class="num">8)</span>
				<span class="item-text-g">Inappropriate character names </span>
				Do not pick names that are defamatory, vulgar, prejudice/racist, or explicitly sexual. Poorly chosen names result in a permanent ban. Please take the time to think up a non-offensive name before creating an account. RSCLegacy staff members reserve the right to change a character name for any reason.
				</p>
			</div>
			<div class="item-section terms">
            	<h2>Thank you</h2>
                <p>
				On behalf of RSCLegacy, thank you for reading and applying to these rules to make RSCLegacy a better place and also keeping your account safe! Please remember that you are a guest and act with the same courtesy and respect you expect from other guests. 
				</p>
            </div>
		</section>
	</div>
</div>
<?php
require load_page('footer.php');
