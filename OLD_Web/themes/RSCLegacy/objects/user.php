<?php
if (luna_strlen(luna_htmlspecialchars($user_data['username'])) > 14)
	$cur_user_name = utf8_substr(luna_htmlspecialchars($user_data['username']), 0, 12).'...';
else
	$cur_user_name = luna_htmlspecialchars($user_data['username']);
?>
<div class="col-lg-4 col-md-6 col-sm-6 col-xs-12">
    <div class="user-entry">
        <div class="media">
            <a class="pull-left" href="<?php echo 'profile.php?id='.$user_data['id'] ?>">
                <?php echo $user_avatar; ?>
            </a>
            <div class="media-body">
                <h2 class="media-heading"><?php echo '<a title="'.luna_htmlspecialchars($user_data['username']).'" href="profile.php?id='.$user_data['id'].'">'.$cur_user_name.'</a>' ?></h2>
                <h4><?php echo $user_title_field ?></h4>
                <?php echo forum_number_format($user_data['num_comments']).' '._n('comment since', 'comments since', $user_data['num_comments'], 'luna').' '.format_time($user_data['registered'], true); ?>
            </div>
        </div>
    </div>
</div>