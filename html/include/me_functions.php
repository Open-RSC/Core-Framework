<?php

/*
 * Copyright (C) 2013-2016 Luna
 * License: http://opensource.org/licenses/MIT MIT
 */

// Display the me navigation
function load_me_nav($page = '', $class = '') {
	global $luna_config, $luna_user, $id;
    
    $can_edit = ($luna_user['id'] == $id && !$luna_user['is_guest'] || ($luna_user['g_id'] == LUNA_ADMIN || ($luna_user['g_moderator'] == '1' && $luna_user['g_mod_ban_users'] == '1')) || ($luna_user['g_id'] == LUNA_ADMIN || ($luna_user['g_moderator'] == '1' && $luna_user['g_mod_ban_users'] == '1')));
    $can_inbox = ($luna_config['o_enable_inbox'] == '1' && $luna_user['g_inbox'] == '1' && $luna_user['use_inbox'] == '1');

?>
<div class="hidden-xs">
    <div class="list-group list-group-luna <?php if (isset($class)) { echo $class; } ?>">
        <a class="<?php if ($page == 'profile') echo 'active'; ?> list-group-item" href="profile.php?id=<?php echo $id ?>"><i class="fa fa-fw fa-user"></i> <?php _e('Profile', 'luna') ?></a>
        <?php if ($can_edit): ?>
            <?php if ($can_inbox): ?>
                <a class="<?php if ($page == 'inbox') echo 'active'; ?> list-group-item" href="inbox.php"><i class="fa fa-fw fa-paper-plane-o"></i> <?php _e('Inbox', 'luna') ?></a>
            <?php endif; ?>
            <a class="<?php if ($page == 'notifications') echo 'active'; ?> list-group-item" href="notifications.php"><i class="fa fa-fw fa-circle-o"></i> <?php _e('Notifications', 'luna') ?></a>
            <a class="<?php if ($page == 'settings') echo 'active'; ?> list-group-item" href="settings.php?id=<?php echo $id ?>"><i class="fa fa-fw fa-cogs"></i> <?php _e('Settings', 'luna') ?></a>
        <?php endif; ?>
    </div>
</div>
<div class="hidden-sm hidden-md hidden-lg profile-btn-nav">
    <div class="btn-group btn-group-justified">
        <a class="btn btn-primary <?php if ($page == 'profile') echo 'active'; ?>" href="profile.php?id=<?php echo $id ?>"><h4><i class="fa fa-fw fa-user"></i></h4><?php _e('Profile', 'luna') ?></a>
        <?php if ($can_edit): ?>
            <?php if ($can_inbox): ?>
                <a class="btn btn-primary <?php if ($page == 'inbox') echo 'active'; ?>" href="inbox.php"><h4><i class="fa fa-fw fa-paper-plane-o"></i></h4><?php _e('Inbox', 'luna') ?></a>
            <?php endif; ?>
            <a class="btn btn-primary <?php if ($page == 'notifications') echo 'active'; ?>" href="notifications.php"><h4><i class="fa fa-fw fa-circle-o"></i></h4><?php _e('Notifications', 'luna') ?></a>
            <a class="btn btn-primary <?php if ($page == 'settings') echo 'active'; ?>" href="settings.php?id=<?php echo $id ?>"><h4><i class="fa fa-fw fa-cogs"></i></h4><?php _e('Settings', 'luna') ?></a>
        <?php endif; ?>
    </div>
</div>
<?php
}