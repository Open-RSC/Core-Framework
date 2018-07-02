<div id="p<?php echo $cur_comment['id'] ?>" class="comment comment-default <?php echo ($comment_count % 2 == 0) ? ' roweven' : ' rowodd' ?><?php if (!isset($inbox)) { if ($cur_comment['id'] == $cur_thread['first_comment_id']) echo ' firstcomment'; if ($comment_count == 1) echo ' only-comment'; if ($cur_comment['marked'] == true) echo ' marked'; if ($cur_comment['soft'] == true) echo ' soft'; } ?><?php if (!isset($inbox) && $cur_comment['id'] == $cur_thread['answer'] && $cur_forum['solved'] == 1) echo ' answer'; ?>">

	<div class="comment-body">
	<div class="postnr pull-right hidden-xs">#<?php echo ($comment_count) ?></div>
		<div class="postUserBlock">
			<div class="postCardHolder">
				<div class="postAvatarHolder"> 
					<a href="profile.php?id=<?php echo $cur_comment['commenter_id']; ?>"  class="avatarBit">
					<img class="comment-avatar" src="<?php echo get_avatar( (!isset($inbox))? $cur_comment['commenter_id'] : $cur_comment['sender_id'] ) ?>" alt="Avatar">
					</a>
				</div>
				<h3 class="userText">
					<?php printf(__('%s', 'luna'), $username) ?>
					<em class="userTitle" itemprop="title"><?php echo get_title( $cur_comment ) ?></em>
				</h3>
				<div class="userStats hidden-xs">
					<?php if ($cur_comment['commenter_id'] != 1) { ?>
					<dl class="pairsJustified">
						<dt><?php _e( 'Comments:', 'luna' ) ?></dt>
						<dd><span class="concealed" rel="nofollow"><?php echo forum_number_format($cur_comment['num_comments']) ?></span></dd>
					</dl>
					<dl class="pairsJustified">
						<dt><?php _e( 'Joined:', 'luna' ) ?></dt>
						<dd><span class="concealed" rel="nofollow"><?php echo format_time($cur_comment['registered'], false, "j M Y") ?></span></dd>
					</dl>
					<dl class="pairsJustified">
						<dt><?php _e( 'Private Message:', 'luna' ) ?></dt>
						<dd>
						<span class="concealed" rel="nofollow">
							<a href="new_inbox.php?uid=<?php echo $cur_comment['commenter_id'] ?>">
							<img class="post-privicon-section" src="img/icons/mail.gif" alt="Send PM" title="Send Private Message">
							</a>
						</span>
						</dd>
					</dl>
					<dl class="pairsJustified">
						<dt><?php _e( 'Status:', 'luna' ) ?></dt>
						<dd class="concealed" rel="nofollow">
						<?php if($cur_comment['is_online']) {?>
						<?php _e( '<font style="color: green;">Online</font>', 'luna' ) ?>
						<?php } else { ?>
						<?php _e( 'Offline', 'luna' ) ?>
						<?php } ?>
						</dd>
					</dl>
					<?php } ?>
				</div>
			</div>
		</div>
		<div class="postInfoContent">
		<div class="messageContent">
		<?php echo $cur_comment['message']."\n" ?>
        <?php if ($cur_comment['admin_note'] != '') { ?>
            <div class="note">
                <b>Admin note:</b>
				<br />
                <?php echo $cur_comment['admin_note'] ?>
            </div>
        <?php } 
		if ($signature != '') echo '<div class="comment-signature"><aside>'.$signature.'</aside></div>'."\n"; ?>
			</div>
		</div>
	</div>
		
		<div class="panel-footer comment-actions ">
			<small> <?php __('on', 'luna') ?> <a class="commenttime" href="<?php if (!isset($inbox)) { echo 'thread.php?pid='.$cur_comment['id'].'#p'.$cur_comment['id']; } else { echo 'viewinbox.php?tid='.$cur_comment['shared_id'].'&mid='.$cur_comment['mid']; } ?>"><?php echo format_time($cur_comment['commented']) ?></a><?php if (!isset($inbox)) { if ($cur_comment['edited'] != '') echo '<span class="comment-edited"><em>'.__(' - Last edited by', 'luna').' '.luna_htmlspecialchars($cur_comment['edited_by']).' ('.format_time($cur_comment['edited']).')</em></span>'; }; ?></small>
			<?php if (!$luna_user['is_guest']) { ?>
			<?php if (count($comment_actions)) echo '<span class="pull-right">' . implode("", $comment_actions) . '</span>' ?>
			<?php } ?>
		</div>
</div>