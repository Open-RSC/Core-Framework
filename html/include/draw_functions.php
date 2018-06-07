<?php

/*
 * Copyright (C) 2013-2016 Luna
 * License: http://opensource.org/licenses/MIT MIT
 */

// Show errors that occured when there are errors
function draw_error_panel($errors) {
	global $cur_error;

	if (!empty($errors)) {
?>
<div class="title-block title-block-danger">
    <h2><i class="fa fa-fw fa-exclamation-triangle "></i> <?php _e('Comment errors', 'luna') ?></h2>
</div>
<div class="tab-content tab-content-danger">
<?php
	foreach ($errors as $cur_error)
		echo $cur_error;
?>
</div>
<?php
	}

}
// Show errors that occured when there are errors
function registration_error_panel($errors) {
	global $cur_error;

	if (!empty($errors)) {
?>
<div class="title-block title-block-danger">
    <h2><i class="fa fa-fw fa-exclamation-triangle "></i> <?php _e('Registration errors', 'luna') ?></h2>
</div>
<div class="tab-content tab-content-danger">
<?php
	foreach ($errors as $cur_error)
		echo $cur_error;
?>
</div>
<?php
	}

}

// Show the preview panel
function draw_preview_panel($message) {
	global $hide_smilies, $message;

	if (!empty($message)) {
		require_once LUNA_ROOT.'include/parser.php';
		$preview_message = parse_message($message);

?>
<div class="title-block title-block-primary">
    <h2><i class="fa fa-fw fa-eye"></i> <?php _e('Comment preview', 'luna') ?></h2>
</div>
<div class="tab-content">
    <p><?php echo $preview_message?></p>
</div>
<?php
	}
}

// Show the editor panel
function draw_editor($height, $meta_enabled = null) {
	global $orig_message, $quote, $fid, $is_admmod, $can_edit_subject, $cur_comment, $message, $luna_config, $cur_index, $p_message, $luna_user;

	$pin_btn = $silence_btn = '';

	if (isset($_POST['pin_thread']) || $cur_comment['pinned'] == '1') {
		$pin_status = ' checked';
		$pin_active = ' active';
	} else {
		$pin_status = '';
		$pin_active = '';
	}

	if ($fid && $is_admmod || $can_edit_subject && $is_admmod)
		$pin_btn = '<div class="btn-group" data-toggle="buttons" title="'.__('Pin thread', 'luna').'"><label class="btn btn-success'.$pin_active.'"><input type="checkbox" name="pin_thread" value="1" tabindex="-1"'.$pin_status.' /><span class="fa fa-fw fa-thumb-tack"></span></label></div>';

	if (LUNA_ACTIVE_PAGE == 'edit') {
		if ((isset($_POST['form_sent']) && isset($_POST['silent'])) || !isset($_POST['form_sent'])) {
			$silence_status = ' checked';
			$silence_active = ' active';
		}

		if ($is_admmod)
			$silence_btn = '<div class="btn-group" data-toggle="buttons" title="'.__('Mute edit', 'luna').'"><label class="btn btn-success'.$silence_active.'"><input type="checkbox" name="silent" value="1" tabindex="-1"'.$silence_status.' /><span class="fa fa-fw fa-microphone-slash"></span></label></div>';
	}

?>
<div class="editor">
	<input type="hidden" name="form_sent" value="1" />
<?php
	if ($luna_user['is_guest'] && $meta_enabled) {
?>
		<label class="required hidden"><?php _e('Name', 'luna') ?></label><input class="info-textfield form-control" type="text" placeholder="<?php _e('Name', 'luna') ?>" name="req_username" maxlength="25" tabindex="<?php echo $cur_index++ ?>" autofocus />
		<label class="conl<?php echo ($luna_config['o_force_guest_email'] == '1') ? ' required' : '' ?> hidden"><?php echo $email_label ?></label><input class="info-textfield form-control" type="text" placeholder="<?php _e('Email', 'luna') ?>" name="<?php echo $email_form_name ?>" maxlength="80" tabindex="<?php echo $cur_index++ ?>" />
<?php } ?>
	<?php if ($luna_config['o_allow_dialog_editor'] == 1 && $luna_user['dialog_editor'] == 1): ?>
<div class="modal fade modal-form" id="url-form" tabindex="-1" role="dialog" aria-hidden="true">
	<div class="modal-dialog modal-xs">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title"><?php _e('URL tag', 'luna') ?></h4>
			</div>
			<div class="modal-body">
                <input class="form-control" type="text" id="url_url" name="url_url" maxlength="255" tabindex="901" placeholder="<?php _e('URL', 'luna') ?>" />
                <input class="form-control" type="text" id="url_text" name="url_text" maxlength="255" tabindex="902" placeholder="<?php _e('Text', 'luna') ?>" />
			</div>
			<div class="modal-footer">
				<a class="btn btn-success" href="javascript:void(0);" onclick="AddTag('advanced','url');" title="<?php _e('URL', 'luna'); ?>" tabindex="-1"><span class="fa fa-link fa-fw"></span> <?php _e('Add URL', 'luna'); ?></a>
			</div>
		</div>
	</div>
</div>
<div class="modal fade modal-form" id="img-form" tabindex="-1" role="dialog" aria-hidden="true">
	<div class="modal-dialog modal-xs">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title"><?php _e('IMG tag', 'luna') ?></h4>
			</div>
			<div class="modal-body">
                <input class="form-control" type="text" id="img_url" name="img_url" maxlength="255" tabindex="901" placeholder="<?php _e('URL', 'luna') ?>" />
                <input class="form-control" type="text" id="img_text" name="img_text" maxlength="255" tabindex="902" placeholder="<?php _e('Text', 'luna') ?>" />
			</div>
			<div class="modal-footer">
				<a class="btn btn-success" href="javascript:void(0);" onclick="AddTag('advanced','img');" title="<?php _e('IMG', 'luna'); ?>" tabindex="-1"><span class="fa fa-image fa-fw"></span> <?php _e('Add IMG', 'luna'); ?></a>
			</div>
		</div>
	</div>
</div>
<?php endif; ?>
	<div class="btn-toolbar btn-toolbar-top">
		<?php echo $pin_btn ?>
		<?php echo $silence_btn ?>
		<div class="btn-group">
			<a class="btn btn-transparent btn-editor" href="javascript:void(0);" onclick="AddTag('inline','b');" title="<?php _e('Bold', 'luna'); ?>" tabindex="-1"><span class="fa fa-bold fa-fw"></span></a>
			<a class="btn btn-transparent btn-editor" href="javascript:void(0);" onclick="AddTag('inline','u');" title="<?php _e('Underline', 'luna'); ?>" tabindex="-1"><span class="fa fa-underline fa-fw"></span></a>
			<a class="btn btn-transparent btn-editor hidden-sm hidden-xs" href="javascript:void(0);" onclick="AddTag('inline','i');" title="<?php _e('Italic', 'luna'); ?>" tabindex="-1"><span class="fa fa-italic fa-fw"></span></a>
			<a class="btn btn-transparent btn-editor hidden-md hidden-sm hidden-xs" href="javascript:void(0);" onclick="AddTag('inline','s');" title="<?php _e('Strike', 'luna'); ?>" tabindex="-1"><span class="fa fa-strikethrough fa-fw"></span></a>
		</div>
		<div class="btn-group">
			<a class="btn btn-transparent btn-editor" href="javascript:void(0);" onclick="AddTag('inline','h');" title="<?php _e('Heading', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-header fa-fw"></span></a>
			<a class="btn btn-transparent btn-editor hidden-md hidden-sm hidden-xs" href="javascript:void(0);" onclick="AddTag('inline','sub');" title="<?php _e('Subscript', 'luna'); ?>" tabindex="-1"><span class="fa fa-subscript fa-fw"></span></a>
			<a class="btn btn-transparent btn-editor hidden-md hidden-sm hidden-xs" href="javascript:void(0);" onclick="AddTag('inline','sup');" title="<?php _e('Superscript', 'luna'); ?>" tabindex="-1"><span class="fa fa-superscript fa-fw"></span></a>
		</div>
		<div class="btn-group">
			<a class="btn btn-transparent btn-editor" href="javascript:void(0);" onclick="AddTag('inline','quote');" title="<?php _e('Quote', 'luna'); ?>" tabindex="-1"><span class="fa fa-quote-left fa-fw"></span></a>
			<a class="btn btn-transparent btn-editor hidden-sm hidden-xs" href="javascript:void(0);" onclick="AddTag('code','code');" title="<?php _e('Code', 'luna'); ?>" tabindex="-1"><span class="fa fa-code fa-fw"></span></a>
			<a class="btn btn-transparent btn-editor hidden-md hidden-sm hidden-xs" href="javascript:void(0);" onclick="AddTag('inline','c');" title="<?php _e('Inline code', 'luna'); ?>" tabindex="-1"><span class="fa fa-file-code-o fa-fw"></span></a>
		</div>
		<div class="btn-group">
            <?php if ($luna_config['o_allow_dialog_editor'] == 1 && $luna_user['dialog_editor'] == 1) { ?>
			<a class="btn btn-transparent btn-editor" href="javascript:void(0);" onclick="AdvancedTag('url');" title="<?php _e('URL', 'luna'); ?>" tabindex="-1"><span class="fa fa-link fa-fw"></span></a>
			<a class="btn btn-transparent btn-editor" href="javascript:void(0);" onclick="AdvancedTag('img');" title="<?php _e('IMG', 'luna'); ?>" tabindex="-1"><span class="fa fa-image fa-fw"></span></a>
            <?php } else { ?>
			<a class="btn btn-transparent btn-editor" href="javascript:void(0);" onclick="AddTag('inline','url');" title="<?php _e('URL', 'luna'); ?>" tabindex="-1"><span class="fa fa-link fa-fw"></span></a>
			<a class="btn btn-transparent btn-editor hidden-xs" href="javascript:void(0);" onclick="AddTag('inline','img');" title="<?php _e('Image', 'luna'); ?>" tabindex="-1"><span class="fa fa-image fa-fw"></span></a>
            <?php } ?>
			<a class="btn btn-transparent btn-editor hidden-xs" href="javascript:void(0);" onclick="AddTag('inline','video');" title="<?php _e('Video', 'luna'); ?>" tabindex="-1"><span class="fa fa-play fa-fw"></span></a>
		</div>
		<div class="btn-group">
			<a class="btn btn-transparent btn-editor" href="javascript:void(0);" onclick="AddTag('list', 'list');" title="<?php _e('List', 'luna'); ?>" tabindex="-1"><span class="fa fa-list-ul fa-fw"></span></a>
			<a class="btn btn-transparent btn-editor" href="javascript:void(0);" onclick="AddTag('inline','*');" title="<?php _e('List item', 'luna'); ?>" tabindex="-1"><span class="fa fa-asterisk fa-fw"></span></a>
		</div>
		<div class="btn-group pull-right hidden-lg">
			<a class="btn btn-transparent dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
				<span class="fa fa-fw fa-ellipsis-h"></span>
			</a>
			<ul class="dropdown-menu" role="menu">
				<li class="hidden-lg hidden-md"><a href="javascript:void(0);" onclick="AddTag('inline','i');" title="<?php _e('Italic', 'luna'); ?>" tabindex="-1"><span class="fa fa-italic fa-fw"></span> <?php _e('Italic', 'luna'); ?></a></li>
				<li class="hidden-lg hidden-md"><a href="javascript:void(0);" onclick="AddTag('code','code');" title="<?php _e('Code', 'luna'); ?>" tabindex="-1"><span class="fa fa-code fa-fw"></span> <?php _e('Code', 'luna'); ?></a></li>
				<li class="hidden-lg"><a href="javascript:void(0);" onclick="AddTag('inline','c');" title="<?php _e('Inline code', 'luna'); ?>" tabindex="-1"><span class="fa fa-file-code-o fa-fw"></span> <?php _e('Inline code', 'luna'); ?></a></li>
				<li class="hidden-lg hidden-md hidden-sm"><a href="javascript:void(0);" onclick="AddTag('inline','img');" title="<?php _e('Image', 'luna'); ?>" tabindex="-1"><span class="fa fa-image fa-fw"></span> <?php _e('Image', 'luna'); ?></a></li>
				<li class="hidden-lg hidden-md hidden-sm"><a href="javascript:void(0);" onclick="AddTag('inline','video');" title="<?php _e('Video', 'luna'); ?>" tabindex="-1"><span class="fa fa-play fa-fw"></span> <?php _e('Video', 'luna'); ?></a></li>
				<li><a href="javascript:void(0);" onclick="AddTag('inline','s');" title="<?php _e('Strike', 'luna'); ?>" tabindex="-1"><span class="fa fa-strikethrough fa-fw"></span> <?php _e('Strike', 'luna'); ?></a></li>
				<li><a href="javascript:void(0);" onclick="AddTag('inline','sub');" title="<?php _e('Subscript', 'luna'); ?>" tabindex="-1"><span class="fa fa-subscript fa-fw"></span> <?php _e('Subscript', 'luna'); ?></a></li>
				<li><a href="javascript:void(0);" onclick="AddTag('inline','sup');" title="<?php _e('Superscript', 'luna'); ?>" tabindex="-1"><span class="fa fa-superscript fa-fw"></span> <?php _e('Superscript', 'luna'); ?></a></li>
			</ul>
		</div>
	</div>
    <?php if ($luna_config['o_allow_advanced_editor'] == '1' && $luna_user['advanced_editor'] == '1'): ?>
	<div class="btn-toolbar btn-toolbar-top">
		<div class="btn-group">
			<a class="btn btn-transparent btn-editor" href="javascript:void(0);" onclick="AddTag('inline+','color');" title="<?php _e('Color', 'luna'); ?>" tabindex="-1"><span class="fa fa-paint-brush fa-fw"></span></a>
            <?php if ($luna_config['o_allow_size'] == '1'): ?>
			<a class="btn btn-transparent btn-editor" href="javascript:void(0);" onclick="AddTag('inline+','size');" title="<?php _e('Size', 'luna'); ?>" tabindex="-1"><span class="fa fa-font fa-fw"></span><span class="fa fa-font fa-ic-small"></span></a>
            <?php endif; ?>
		</div>
		<div class="btn-group">
            <?php if ($luna_config['o_allow_center'] == '1'): ?>
			<a class="btn btn-transparent btn-editor" href="javascript:void(0);" onclick="AddTag('inline','center');" title="<?php _e('Center', 'luna'); ?>" tabindex="-1"><span class="fa fa-align-center fa-fw"></span></a>
            <?php endif; ?>
			<a class="btn btn-transparent btn-editor" href="javascript:void(0);" onclick="AddTag('inline','email');" title="<?php _e('Email', 'luna'); ?>" tabindex="-1"><span class="fa fa-envelope-o fa-fw"></span></a>
		</div>
        <?php if ($luna_config['o_allow_spoiler'] == '1'): ?>
		<div class="btn-group">
			<a class="btn btn-transparent btn-editor" href="javascript:void(0);" onclick="AddTag('spoiler','spoiler');" title="<?php _e('Spoiler', 'luna'); ?>" tabindex="-1"><span class="fa fa-eye-slash fa-fw"></span></a>
		</div>
        <?php endif; ?>
    </div>
    <?php endif; ?>
	<textarea class="form-control textarea"  placeholder="<?php _e('Start typing...', 'luna') ?>" name="req_message" id="comment_field" rows="<?php echo $height ?>" tabindex="<?php echo $cur_index++ ?>"><?php
		if (LUNA_ACTIVE_PAGE == 'comment')
			echo isset($_POST['req_message']) ? luna_htmlspecialchars($orig_message) : (isset($quote) ? $quote : '');
		elseif (LUNA_ACTIVE_PAGE == 'edit')
			echo luna_htmlspecialchars(isset($_POST['req_message']) ? $message : $cur_comment['message']);
		elseif (LUNA_ACTIVE_PAGE == 'new-inbox')
			echo luna_htmlspecialchars(isset($p_message) ? $p_message : '');
	?></textarea>
	<?php
		if (LUNA_ACTIVE_PAGE == 'edit')
			$action = 'edit-comment';
		elseif (LUNA_ACTIVE_PAGE == 'new-inbox')
			$action = 'message';
		else
			$action = ($fid ? 'thread' : 'comment');
	?>
	<div class="btn-toolbar btn-toolbar-bottom">
		<div class="btn-group">
			<button class="btn btn-with-text btn-default-2" type="submit" name="preview" accesskey="p" tabindex="<?php echo $cur_index++ ?>" onclick="window.onbeforeunload=null"><span class="fa fa-fw fa-eye"></span> <?php _e('Preview', 'luna') ?></button>
		</div>
		<div class="btn-group pull-right">
			<button class="btn btn-with-text btn-default-2" type="submit" name="submit" accesskey="s" tabindex="<?php echo $cur_index++ ?>" onclick="window.onbeforeunload=null"><span class="fa fa-fw fa-plus"></span> <?php _e('Submit', 'luna') ?></button>
		</div>
	</div>
</div>
<script>
function AdvancedTag(tag) {
    var Field = document.getElementById('comment_field');
    var val = Field.value;
    var selected_txt = val.substring(Field.selectionStart, Field.selectionEnd);
    if (tag == 'url') {
        $('#url_text').val(selected_txt);
        $('#url-form').modal('toggle');
        $('#url-form').modal('show');
    } else if (tag == 'img') {
        $('#img_text').val(selected_txt);
        $('#img-form').modal('toggle');
        $('#img-form').modal('show');
    }
}
function AddTag(type, tag) {
    var Field = document.getElementById('comment_field');
    var val = Field.value;
    var selected_txt = val.substring(Field.selectionStart, Field.selectionEnd);
    var before_txt = val.substring(0, Field.selectionStart);
    var after_txt = val.substring(Field.selectionEnd, val.length);
    if (type == 'inline')
       Field.value = before_txt + '[' + tag + ']' + selected_txt + '[/' + tag + ']' + after_txt;
    else if (type == 'advanced') {
       if (tag == 'url') {
           var url = $('#url_url').val();
           var text = $('#url_text').val();
           if ( text == '' ) {
               Field.value = before_txt + '[url]' + url + '[/url]' + after_txt;
           } else {
               Field.value = before_txt + '[url=' + url + ']' + text + '[/url]' + after_txt;
           }
           $('#url-form').modal('hide');
           $('#url_text').val();
           $('#url_url').val();
       }
       if (tag == 'img') {
           var url = $('#img_url').val();
           var text = $('#img_text').val();
           if ( text == '' ) {
               Field.value = before_txt + '[img]' + url + '[/img]' + after_txt;
           } else {
               Field.value = before_txt + '[img=' + text + ']' + url + '[img]' + after_txt;
           }
           $('#img-form').modal('hide');
           $('#img_text').val();
           $('#img_url').val();
       }
    }
    else if (tag == 'color')
       Field.value = before_txt + '[color=red]' + selected_txt + '[/color]' + after_txt;
    else if (tag == 'size')
       Field.value = before_txt + '[size=200]' + selected_txt + '[/size]' + after_txt;
    else if (type == 'list')
       Field.value = before_txt + '[list]' + "\r" + '[*]' + selected_txt + '[/*]' + "\r" + '[/list]' + after_txt;
    else if (type == 'code')
       Field.value = before_txt + '[code]' + "\r" + '[[language]]' + "\r" + selected_txt + "\r" + '[/code]' + after_txt;
    else if (type == 'spoiler')
       Field.value = before_txt + '[spoiler]' + "\r" + selected_txt + "\r" + '[/spoiler]' + after_txt;
    else if (type == 'emoji')
       Field.value = before_txt + ' ' + tag + ' ' + after_txt;
    document.getElementById('comment_field').focus();
}
window.onbeforeunload = function() {
    if ( document.getElementById('comment_field').value ) {
	// Don't translate this; we can't change the confirm text anyway.
	return 'Unsaved changes!';
    }
}
</script>
<?php
}

// Show the editor panel
function draw_admin_note() {
	global $cur_comment, $luna_user, $cur_index, $is_admmod;

    
if ($luna_user['g_edit_comments'] == '1' && $luna_user['g_id'] == LUNA_ADMIN) {
?>
    <div class="admin-note-area">
        <h4><?php _e('Manage admin note', 'luna') ?></h4>
        <textarea class="form-control admin-note-edit"  placeholder="<?php _e('Add a note to this comment...', 'luna') ?>" name="admin_note" id="note_field" rows="3" tabindex="<?php echo $cur_index++ ?>"><?php echo $cur_comment['admin_note'] ?></textarea>
    </div>
<?php
                 }
}

function draw_threads_list() {
	global $luna_user, $luna_config, $db, $sort_by, $start_from, $id, $db_type, $tracked_threads, $cur_forum;

	// Retrieve a list of thread IDs, LIMIT is (really) expensive so we only fetch the IDs here then later fetch the remaining data
	if ($luna_user['g_soft_delete_view'])
		$result = $db->query('SELECT id FROM '.$db->prefix.'threads WHERE forum_id='.$id.' ORDER BY pinned DESC, '.$sort_by.', id DESC LIMIT '.$start_from.', '.$luna_user['disp_threads']) or error('Unable to fetch thread IDs', __FILE__, __LINE__, $db->error());
	else
		$result = $db->query('SELECT id FROM '.$db->prefix.'threads WHERE soft = 0 AND forum_id='.$id.' ORDER BY pinned DESC, '.$sort_by.', id DESC LIMIT '.$start_from.', '.$luna_user['disp_threads']) or error('Unable to fetch thread IDs', __FILE__, __LINE__, $db->error());

	// If there are threads in this forum
	if ($db->num_rows($result)) {
		$thread_ids = array();
        $sql_addition = '';

		for ($i = 0; $cur_thread_id = $db->result($result, $i); $i++)
			$thread_ids[] = $cur_thread_id;

		// Fetch list of threads to display on this page
		if ($luna_user['is_guest'] || $luna_config['o_has_commented'] == '0') {
			// When not showing a commented label
			if (!$luna_user['is_admmod'])
				$sql_addition = 'threads.soft = 0 AND ';

			$sql = 'SELECT 
				ub.id as avatar_id,
				u.group_id as last_g_id,
				ub.group_id as first_g_id,
				u.gold_time as last_gold_time,
				ub.gold_time as first_gold_time,
				u.premium_time as last_premium_time,
				ub.premium_time as first_premium_time,
				threads.id, 
				threads.commenter, 
				threads.subject, 
				threads.commented, 
				threads.last_comment, 
				threads.last_comment_id, 
				threads.last_commenter, 
				threads.last_commenter_id, 
				threads.num_views, 
				threads.num_replies, 
				threads.closed, 
				threads.pinned, 
				threads.important, 
				threads.solved AS answer,
				threads.moved_to, 
				threads.soft FROM '.$db->prefix.'threads LEFT JOIN users AS u ON u.username = threads.last_commenter LEFT JOIN users AS ub ON ub.username = threads.commenter WHERE '.$sql_addition.'threads.id IN('.implode(',', $thread_ids).') ORDER BY threads.pinned DESC, '.$sort_by.', threads.id DESC';
		} else {
			// When showing a commented label
			if (!$luna_user['g_soft_delete_view'])
				$sql_addition = 't.soft = 0 AND ';

			$sql = 'SELECT 
				ub.id as avatar_id,
				u.group_id as last_g_id,
				ub.group_id as first_g_id, 
				u.gold_time as last_gold_time,
				ub.gold_time as first_gold_time,
				u.premium_time as last_premium_time,
				ub.premium_time as first_premium_time,
				p.commenter_id AS has_commented, 
				t.id, 
				t.subject, 
				t.commenter, 
				t.commented, 
				t.last_comment, 
				t.last_comment_id, 
				t.last_commenter, 
				t.last_commenter_id, 
				t.num_views,
				t.num_replies, 
				t.closed, 
				t.pinned, 
				t.moved_to, 
				t.important, 
				t.solved AS answer, 
				t.soft FROM '.$db->prefix.'threads AS t LEFT JOIN users AS u ON u.username = t.last_commenter LEFT JOIN users AS ub ON ub.username = t.commenter LEFT JOIN '.$db->prefix.'comments AS p ON t.id=p.thread_id AND p.commenter_id='.$luna_user['id'].' WHERE '.$sql_addition.'t.id IN('.implode(',', $thread_ids).') GROUP BY t.id'.($db_type == 'pgsql' ? ', t.subject, t.commenter, t.commented, t.last_comment, t.last_comment_id, t.last_commenter, t.num_views, t.num_replies, t.closed, t.pinned, t.moved_to, p.commenter_id' : '').' ORDER BY t.pinned DESC, t.'.$sort_by.', t.id DESC';
		}
		$result = $db->query($sql) or error('Unable to fetch thread list', __FILE__, __LINE__, $db->error());

		$thread_count = 0;
		while ($cur_thread = $db->fetch_assoc($result)) {

			++$thread_count;
			$status_text = array();
			$item_status = ($thread_count % 2 == 0) ? 'roweven' : 'rowodd';
			$icon_type = 'icon';
			$subject = '<a href="thread.php?id='.$cur_thread['id'].'">'.luna_htmlspecialchars($cur_thread['subject']).'</a> <br /><span class="byuser">'.user_append_rank_time(luna_htmlspecialchars($cur_thread['commenter']), $cur_thread['first_g_id'], ($cur_news['first_premium_time'] > time() && $cur_thread['first_gold_time'] > time() ? 2 : ($cur_thread['first_gold_time'] > time() && $cur_thread['first_premium_time'] < time() ? 1 : 0))).'</span>'.__(',', 'luna').' <span class="bytime">'.format_time($cur_thread['commented']).'</span>';
			$last_comment_date = '<a href="thread.php?pid='.$cur_thread['last_comment_id'].'#p'.$cur_thread['last_comment_id'].'">'.format_time($cur_thread['last_comment']).'</a>';

			if (is_null($cur_thread['moved_to'])) {
				$thread_id = $cur_thread['id'];

				$last_commenter = '<span class="byuser">'.__('by', 'luna').' '.luna_htmlspecialchars($cur_thread['last_commenter']).'</span>';
			} else {
				$last_commenter = '';
				$thread_id = $cur_thread['moved_to'];
			}

			if ($luna_config['o_censoring'] == '1')
				$cur_thread['subject'] = censor_words($cur_thread['subject']);

			if ($cur_thread['pinned'] == '1') {
				$item_status .= ' pinned-item';
				$status_text[] = '<i class="fa fa-fw fa-thumb-tack status-pinned"></i>';
			}

			if (isset($cur_thread['answer']) && $cur_forum['solved'] == 1) {
				$item_status .= ' solved-item';
				$status_text[] = '<i class="fa fa-fw fa-check status-solved"></i>';
			}

			if ($cur_thread['important']) {
				$item_status .= ' important-item';
				$status_text[] = '<i class="fa fa-fw fa-map-marker status-important"></i>';
			}

			if ($cur_thread['moved_to'] != 0) {
				$status_text[] = '<i class="fa fa-fw fa-arrows-alt status-moved"></i>';
				$item_status .= ' moved-item';
			}

			if ($cur_thread['closed'] == '1') {
				$status_text[] = '<i class="fa fa-fw fa-lock status-closed"></i>';
				$item_status .= ' closed-item';
			}

			if (!$luna_user['is_guest'] && $cur_thread['last_comment'] > $luna_user['last_visit'] && (!isset($tracked_threads['threads'][$cur_thread['id']]) || $tracked_threads['threads'][$cur_thread['id']] < $cur_thread['last_comment']) && (!isset($tracked_threads['forums'][$id]) || $tracked_threads['forums'][$id] < $cur_thread['last_comment']) && is_null($cur_thread['moved_to'])) {
				$item_status .= ' new-item';
				$icon_type = 'icon icon-new';
				$status_text[] = '<a href="thread.php?id='.$cur_thread['id'].'&amp;action=new" title="'.__('Go to the first new comment in the thread.', 'luna').'"><i class="fa fa-fw fa-bell status-new"></i></a>';
			}

			$url = 'thread.php?id='.$thread_id;
			$by = '<span class="byuser">'.user_append_rank_time(luna_htmlspecialchars($cur_thread['last_commenter']), $cur_thread['last_g_id'], ($cur_thread['last_premium_time'] > time() && $cur_thread['last_gold_time'] > time() ? 2 : ($cur_thread['last_gold_time'] > time() && $cur_thread['last_premium_time'] < time() ? 1 : 0))).'</span>';

			if (!$luna_user['is_guest'] && $luna_config['o_has_commented'] == '1') {
				if ($cur_thread['has_commented'] == $luna_user['id']) {
					$item_status .= ' commented-item';
				}
			}

			$subject_status = implode(' ', $status_text);

			$num_pages_thread = ceil(($cur_thread['num_replies'] + 1) / $luna_user['disp_comments']);

			if ($num_pages_thread > 1)
				$subject_multipage = '<span class="inline-pagination"> ... '.simple_paginate($num_pages_thread, -1, 'thread.php?id='.$cur_thread['id']).'</span>';
			else
				$subject_multipage = null;

			$comments_label = _n('Comment:', 'Comments:', $cur_thread['num_replies'], 'luna');
			$views_label = _n('View:', 'Views:', $cur_thread['num_views'], 'luna');

			require get_view_path('thread.php');

		}

	} else {
		echo '<h3 class="text-center">';
		printf(__('<a href="comment.php?fid=%s">Start the first thread in this forum</a>', 'luna'), $id);
		echo '</h3>';
	}

}

function draw_forum_list($forum_object_name = 'forum.php', $use_cat = 0, $cat_object_name = 'category.php', $close_tags = '') {
	global $db, $luna_config, $luna_user, $id, $new_threads;

	// Print the categories and forums
	$result = $db->query('SELECT c.id AS cid, c.cat_name, f.id AS fid, f.forum_name, f.forum_desc, f.parent_id, f.moderators, f.num_threads, f.num_comments, f.last_comment, f.last_comment_id, f.last_commenter_id, f.icon, f.color, u.username AS username, u.group_id AS g_ide, u.gold_time, u.premium_time, t.subject AS subject FROM '.$db->prefix.'categories AS c INNER JOIN '.$db->prefix.'forums AS f ON c.id=f.cat_id LEFT JOIN '.$db->prefix.'users AS u ON f.last_commenter_id=u.id LEFT JOIN '.$db->prefix.'threads AS t ON t.last_comment_id=f.last_comment_id LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=f.id AND fp.group_id='.$luna_user['g_id'].') WHERE fp.read_forum IS NULL OR fp.read_forum=1 ORDER BY c.disp_position, c.id, f.disp_position', true) or error('Unable to fetch category/forum list', __FILE__, __LINE__, $db->error());

	$cur_category = 0;
	$cat_count = 0;
	$forum_count = 0;
	while ($cur_forum = $db->fetch_assoc($result)) {
		if(!isset($cur_forum['parent_id']) || $cur_forum['parent_id'] == 0) {
			$moderators = '';

			if ($cur_forum['cid'] != $cur_category && $use_cat == 1) {
				if ($cur_category != 0)
					echo $close_tags;

				++$cat_count;
				$forum_count = 0;
				
				echo '<div id="idx'. $cat_count. '">
						<div class="category-box">';

				require get_view_path($cat_object_name);

				$cur_category = $cur_forum['cid'];
			}

			++$forum_count;
			$item_status = ($forum_count % 2 == 0) ? 'roweven' : 'rowodd';
			$forum_field_new = '';
			$forum_desc = '';
			$icon_type = 'icon';
			$last_comment = '';

			// Are there new comments since our last visit?
			if (isset($new_threads[$cur_forum['fid']])) {
				$item_status .= ' new-item';
				$forum_field_new = '<span class="newtext">[ <a href="search.php?action=show_new&amp;fid='.$cur_forum['fid'].'">'.__('New', 'luna').'</a> ]</span>';
				$icon_type = 'icon icon-new';
			}

			if ($cur_forum['icon'] != NULL)
				$faicon = '<span class="fa fa-fw fa-'.$cur_forum['icon'].'"></span> ';
			else
				$faicon = '';

			$forum_field = '<a href="viewforum.php?id='.$cur_forum['fid'].'" class="forum-title cat_text">'.luna_htmlspecialchars($cur_forum['forum_name']).'</a>'.(!empty($forum_field_new) ? ' '.$forum_field_new : '');

			if ($cur_forum['forum_desc'] != '')
				$forum_desc = '<div class="forum-description hidden-xs">'.$cur_forum['forum_desc'].'</div>';

			$threads_label = _n('thread', 'threads', $cur_forum['num_threads'], 'luna');
			$comments_label = _n('comment', 'comments', $cur_forum['num_comments'], 'luna');

			if ($id == $cur_forum['fid']) {
				$item_status .= ' active';
				$item_style = ' style="background-color: '.$cur_forum['color'].'; border-color: '.$cur_forum['color'].';"';
			} else {
				$item_style = '';
				$item_style = ' style="border-left: 6px solid '.$cur_forum['color'].';"';
			}

			// If there is a last_comment/last_commenter
			if ($cur_forum['last_comment'] != '') {
                if ($luna_user['g_view_users'] == '1' && $cur_forum['last_commenter_id'] > '1')
                    $last_comment = '<a href="thread.php?pid='.$cur_forum['last_comment_id'].'#p'.$cur_forum['last_comment_id'].'" class="thread-title">'.luna_htmlspecialchars($cur_forum['subject']).'</a><br /><span class="thread-meta"><span class="byuser"><a href="profile.php?id='.$cur_forum['last_commenter_id'].'">'.user_append_rank_time(luna_htmlspecialchars($cur_forum['username']), $cur_forum['g_ide'], ($cur_forum['premium_time'] > time() && $cur_forum['gold_time'] > time() ? 2 : ($cur_forum['gold_time'] > time() && $cur_forum['premium_time'] < time() ? 1 : 0))).'</a></span>'.__(',', 'luna').' <span class="bytime">'.format_time($cur_forum['last_comment']).' </span></span>';
                else
                    $last_comment = '<a href="thread.php?pid='.$cur_forum['last_comment_id'].'#p'.$cur_forum['last_comment_id'].'" class="thread-title">'.luna_htmlspecialchars($cur_forum['subject']).'</a><br /><span class="thread-meta"><span class="byuser">'.user_append_rank_time(luna_htmlspecialchars($cur_forum['username']), $cur_forum['g_ide'], ($cur_forum['premium_time'] > time() && $cur_forum['gold_time'] > time() ? 2 : ($cur_forum['gold_time'] > time() && $cur_forum['premium_time'] < time() ? 1 : 0))).'</span>'.__(',', 'luna').' <span class="bytime">'.format_time($cur_forum['last_comment']).' </span></span>';
			} else
				$last_comment = '<span class="thread-title">'.__('Never', 'luna').'</span>';

			require get_view_path($forum_object_name);
		}
	}

	// Any need to close of a category?
	if ($use_cat == 1) {
		if ($cur_category > 0)
			echo $close_tags;
		else
			echo '<div class="no-board">'.__('No forums available', 'luna').'</div>';
	}
}

function draw_subforum_list($object_name = 'forum.php', $display_in_sub = 1) {
	global $db, $luna_config, $luna_user, $id, $new_threads;

	$result = $db->query('SELECT parent_id FROM '.$db->prefix.'forums WHERE id='.$id) or error ('Unable to fetch information about the current forum', __FILE__, __LINE__, $db->error());
	$cur_parent = $db->fetch_assoc($result);

	if ($cur_parent['parent_id'] == '0')
		$subforum_parent_id = $id;
	else
		$subforum_parent_id = $cur_parent['parent_id'];

	// Print the categories and forums
	$result = $db->query('SELECT c.id AS cid, c.cat_name, f.id AS fid, f.forum_name, f.forum_desc, f.parent_id, f.moderators, f.num_threads, f.num_comments, f.last_comment, f.last_comment_id, f.last_commenter_id, f.icon, f.color, u.username AS username, t.subject AS subject FROM '.$db->prefix.'categories AS c INNER JOIN '.$db->prefix.'forums AS f ON c.id=f.cat_id LEFT JOIN '.$db->prefix.'users AS u ON f.last_commenter_id=u.id LEFT JOIN '.$db->prefix.'threads AS t ON t.last_comment_id=f.last_comment_id LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=f.id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.read_forum IS NULL OR fp.read_forum=1) AND f.parent_id='.$subforum_parent_id.' ORDER BY c.disp_position, c.id, f.disp_position', true) or error('Unable to fetch category/forum list', __FILE__, __LINE__, $db->error());

	$cur_category = 0;
	$cat_count = 0;
	$forum_count = 0;
	while ($cur_forum = $db->fetch_assoc($result)) {
		if ($cur_forum['parent_id'] != 0)
			$parent_id = $cur_forum['parent_id'];

		if($cur_forum['parent_id'] == $parent_id) {
			$moderators = '';

			++$forum_count;
			$item_status = ($forum_count % 2 == 0) ? 'roweven' : 'rowodd';
			$forum_field_new = '';
			$forum_desc = '';
			$icon_type = 'icon';
			$last_comment = '';
			$add_icon_comment = '<i class="fa fa-comments-o"></i>';

			// Are there new comments since our last visit?
			if (isset($new_threads[$cur_forum['fid']])) {
				$item_status .= ' new-item';
				$forum_field_new = '<span class="newtext">[ <a href="search.php?action=show_new&amp;fid='.$cur_forum['fid'].'">'.__('New', 'luna').'</a> ]</span>';
				$icon_type = 'icon icon-new';
				$add_icon_comment = '<i class="fa fa-comments"></i>';
			}

			if ($cur_forum['icon'] != NULL)
				$faicon = '<span class="fa fa-fw fa-'.$cur_forum['icon'].'"></span> ';
			else
				$faicon = '';

			$forum_field = '<a href="viewforum.php?id='.$cur_forum['fid'].'">'.luna_htmlspecialchars($cur_forum['forum_name']).'</a>'.(!empty($forum_field_new) ? ' '.$forum_field_new : '');

			if ($cur_forum['forum_desc'] != '')
				$forum_desc = '<div class="forum-description">'.$cur_forum['forum_desc'].'</div>';

			$threads_label = _n('thread', 'threads', $cur_forum['num_threads'], 'luna');
			$comments_label = _n('comment', 'comments', $cur_forum['num_comments'], 'luna');

			if ($id == $cur_forum['fid']) {
				$item_status .= ' active';
				$item_style = ' style="background-color: '.$cur_forum['color'].'; border-color: '.$cur_forum['color'].';"';
			} else {
				$item_style = '';
				$item_style = ' style="border-left: 6px solid '.$cur_forum['color'].';"';
			}

			// If there is a last_comment/last_commenter
			if ($cur_forum['last_comment'] != '') {
                if ($luna_user['g_view_users'] == '1' && $cur_forum['last_commenter_id'] > '1')
                    $last_comment = '<a href="thread.php?pid='.$cur_forum['last_comment_id'].'#p'.$cur_forum['last_comment_id'].'" class="thread-title">'.luna_htmlspecialchars($cur_forum['subject']).'</a><br /><span class="thread-meta"><span class="bytime">'.format_time($cur_forum['last_comment']).' </span><span class="byuser">'.__('by', 'luna').' <a href="profile.php?id='.$cur_forum['last_commenter_id'].'">'.luna_htmlspecialchars($cur_forum['username']).'</a></span></span>';
                else
                    $last_comment = '<a href="thread.php?pid='.$cur_forum['last_comment_id'].'#p'.$cur_forum['last_comment_id'].'" class="thread-title">'.luna_htmlspecialchars($cur_forum['subject']).'</a><br /><span class="thread-meta"><span class="bytime">'.format_time($cur_forum['last_comment']).' </span><span class="byuser">'.__('by', 'luna').' '.luna_htmlspecialchars($cur_forum['username']).'</span></span>';
			} else
				$last_comment = '<span class="thread-title">'.__('Never', 'luna').'</span>';

			require get_view_path($object_name);
		}
	}
}

function draw_index_threads_list($limit = 30, $thread_object_name = 'thread.php', $link_to_new = false) {
	global $luna_user, $luna_config, $db, $start_from, $id, $sort_by, $start_from, $db_type, $cur_thread, $tracked_threads;

	// Retrieve a list of thread IDs, LIMIT is (really) expensive so we only fetch the IDs here then later fetch the remaining data
	$result = $db->query('SELECT t.id, t.moved_to FROM '.$db->prefix.'threads AS t LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=t.forum_id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.read_forum IS NULL OR fp.read_forum=1) AND t.moved_to IS NULL ORDER BY last_comment DESC LIMIT '.$limit) or error('Unable to fetch thread IDs', __FILE__, __LINE__, $db->error());

	// If there are threads in this forum
	if ($db->num_rows($result)) {
		$thread_ids = array();
		for ($i = 0; $cur_thread_id = $db->result($result, $i); $i++)
			$thread_ids[] = $cur_thread_id;

		// Fetch list of threads to display on this page
		$sql_soft = NULL;
		if ($luna_user['is_guest'] || $luna_config['o_has_commented'] == '0') {
			if (!$luna_user['g_soft_delete_view'])
				$sql_soft = 'soft = 0 AND ';

			$sql = 'SELECT id, commenter, subject, commented, last_comment, last_comment_id, last_commenter, last_commenter_id, num_views, num_replies, closed, pinned, important, moved_to, soft, solved AS answer, forum_id FROM '.$db->prefix.'threads WHERE '.$sql_soft.'id IN('.implode(',', $thread_ids).') ORDER BY last_comment DESC';

		} else {
			if (!$luna_user['g_soft_delete_view'])
				$sql_soft = 't.soft = 0 AND ';

			$sql = 'SELECT p.commenter_id AS has_commented, t.id, t.subject, t.commenter, t.commented, t.last_comment, t.last_comment_id, t.last_commenter, t.last_commenter_id, t.num_views, t.num_replies, t.closed, t.pinned, t.important, t.moved_to, t.soft, t.solved AS answer, t.forum_id FROM '.$db->prefix.'threads AS t LEFT JOIN '.$db->prefix.'comments AS p ON t.id=p.thread_id AND p.commenter_id='.$luna_user['id'].' WHERE '.$sql_soft.'t.id IN('.implode(',', $thread_ids).') GROUP BY t.id'.($db_type == 'pgsql' ? ', t.subject, t.commenter, t.commented, t.last_comment, t.last_comment_id, t.last_commenter, t.num_views, t.num_replies, t.closed, t.pinned, t.moved_to, p.commenter_id' : '').' ORDER BY t.last_comment DESC';
		}

		$result = $db->query($sql) or error('Unable to fetch thread list', __FILE__, __LINE__, $db->error());

		// Load cached forums
		if (file_exists(LUNA_CACHE_DIR.'cache_forums.php'))
			include_once LUNA_CACHE_DIR.'cache_forums.php';

		if (!defined('LUNA_LIST_LOADED')) {
			if (!defined('LUNA_CACHE_FUNCTIONS_LOADED'))
				require LUNA_ROOT.'include/cache.php';

			generate_forum_cache();
			require LUNA_CACHE_DIR.'cache_forums.php';
		}

		$thread_count = 0;
		while ($cur_thread = $db->fetch_assoc($result)) {

			++$thread_count;
			$status_text = array();
			$item_status = ($thread_count % 2 == 0) ? 'roweven' : 'rowodd';
			$icon_type = 'icon';
            $subject = luna_htmlspecialchars($cur_thread['subject']);
			$last_comment_date = '<a href="thread.php?pid='.$cur_thread['last_comment_id'].'#p'.$cur_thread['last_comment_id'].'">'.format_time($cur_thread['last_comment']).'</a>';

			if (is_null($cur_thread['moved_to'])) {
				$thread_id = $cur_thread['id'];

				$last_commenter = '<span class="byuser">'.__('by', 'luna').' '.luna_htmlspecialchars($cur_thread['last_commenter']).'</span>';

				foreach ($luna_forums as $cur_forum) {
					if ($cur_thread['forum_id'] == $cur_forum['id']) {
						$forum_name = luna_htmlspecialchars($cur_forum['forum_name']);
						$forum_color = $cur_forum['color'];
						if ($cur_forum['icon'] != NULL)
							$faicon = '<span class="fa fa-fw fa-'.$cur_forum['icon'].'"></span> ';
						else
							$faicon = '';
					}
				}

				$forum_name = '<a class="in-forum" href="viewforum.php?id='.$cur_thread['forum_id'].'" style="color: '.$forum_color.';">'.$faicon.' '.$forum_name.'</a>';
			} else {
				$last_commenter = '';
				$thread_id = $cur_thread['moved_to'];
			}

			if ($luna_config['o_censoring'] == '1')
				$cur_thread['subject'] = censor_words($cur_thread['subject']);

			if ($cur_thread['pinned'] == '1') {
				$item_status .= ' pinned-item';
				$status_text[] = '<i class="fa fa-fw fa-thumb-tack status-pinned"></i>';
			}

			if (isset($cur_thread['answer'])) {
				$item_status .= ' solved-item';
				$status_text[] = '<i class="fa fa-fw fa-check status-solved"></i>';
			}

			if ($cur_thread['important']) {
				$item_status .= ' important-item';
				$status_text[] = '<i class="fa fa-fw fa-map-marker status-important"></i>';
			}

			if ($cur_thread['moved_to'] != 0) {
				$status_text[] = '<i class="fa fa-fw fa-arrows-alt status-moved"></i>';
				$item_status .= ' moved-item';
			}

			if ($cur_thread['closed'] == '1') {
				$status_text[] = '<i class="fa fa-fw fa-lock status-closed"></i>';
				$item_status .= ' closed-item';
			}

			if (!$luna_user['is_guest'] && $cur_thread['last_comment'] > $luna_user['last_visit'] && (!isset($tracked_threads['threads'][$cur_thread['id']]) || $tracked_threads['threads'][$cur_thread['id']] < $cur_thread['last_comment']) && (!isset($tracked_threads['forums'][$id]) || $tracked_threads['forums'][$id] < $cur_thread['last_comment']) && is_null($cur_thread['moved_to'])) {
				$item_status .= ' new-item';
				$icon_type = 'icon icon-new';
				$status_text[] = '<a href="thread.php?id='.$cur_thread['id'].'&amp;action=new" title="'.__('Go to the first new comment in the thread.', 'luna').'"><i class="fa fa-fw fa-bell status-new"></i></a>';
			}

            if ($link_to_new == false) {
                $url = 'thread.php?id='.$thread_id;
            } else {
                $url = 'thread.php?id='.$thread_id.'&amp;action=new';
            }
            
			$by = '<span class="byuser">'.__('by', 'luna').' '.luna_htmlspecialchars($cur_thread['commenter']).'</span>';

			if (!$luna_user['is_guest'] && $luna_config['o_has_commented'] == '1') {
				if ($cur_thread['has_commented'] == $luna_user['id']) {
					$item_status .= ' commented-item';
				}
			}

			$subject_status = implode(' ', $status_text);

			$num_pages_thread = ceil(($cur_thread['num_replies'] + 1) / $luna_user['disp_comments']);

			if ($num_pages_thread > 1)
				$subject_multipage = '<span class="inline-pagination"> &middot; '.simple_paginate($num_pages_thread, -1, 'thread.php?id='.$cur_thread['id']).'</span>';
			else
				$subject_multipage = null;

			$replies_label = _n('reply', 'replies', $cur_thread['num_replies'], 'luna');
			$views_label = _n('view', 'views', $cur_thread['num_views'], 'luna');

			require get_view_path($thread_object_name);

		}
	} else
		echo '<div class="no-thread">'.__('The board is empty, select a forum and create a thread to begin.', 'luna').'</div>';
}

function draw_comment_list() {
	global $db, $luna_config, $id, $comment_ids, $is_admmod, $start_from, $comment_count, $admin_ids, $luna_user, $cur_thread, $started_by, $cur_forum;

	// Retrieve the comments (and their respective commenter/online status)
	$result = $db->query('SELECT u.gold_time, u.premium_time, u.email, u.title, u.url, u.location, u.signature, u.email_setting, u.num_comments, u.registered, u.admin_note, p.id, p.commenter AS username, p.commenter_id, p.commenter_ip, p.commenter_email, p.message, p.admin_note, p.hide_smilies, p.commented, p.edited, p.edited_by, p.marked, p.soft, g.g_id, g.g_user_title, o.user_id AS is_online FROM '.$db->prefix.'comments AS p INNER JOIN '.$db->prefix.'users AS u ON u.id=p.commenter_id INNER JOIN '.$db->prefix.'groups AS g ON g.g_id=u.group_id LEFT JOIN '.$db->prefix.'online AS o ON (o.user_id=u.id AND o.user_id!=1 AND o.idle=0) WHERE p.id IN ('.implode(',', $comment_ids).') ORDER BY p.id', true) or error('Unable to fetch comment info', __FILE__, __LINE__, $db->error());
	while ($cur_comment = $db->fetch_assoc($result)) {
		$comment_count++;
		$user_avatar = '';
		$user_info = array();
		$user_actions = array();
		$comment_actions = array();
		$is_online = '';
		$signature = '';

		// If the commenter is a registered user
		if ($cur_comment['commenter_id'] > 1) {
			if ($luna_user['g_view_users'] == '1')
				$username = '<a class="username" itemprop="name" href="profile.php?id='.$cur_comment['commenter_id'].'">'.user_append_rank_time(luna_htmlspecialchars($cur_comment['username']), $cur_comment['g_id'], ($cur_comment['premium_time'] > time() && $cur_comment['gold_time'] > time() ? 2 : ($cur_comment['gold_time'] > time() && $cur_comment['premium_time'] < time() ? 1 : 0))).'</a>';
			else
				$username = user_append_rank_time(luna_htmlspecialchars($cur_comment['username']), $cur_comment['g_id'], ($cur_comment['premium_time'] > time() && $cur_comment['gold_time'] > time() ? 2 : ($cur_comment['gold_time'] > time() && $cur_comment['premium_time'] < time() ? 1 : 0)));

			$user_title = get_title($cur_comment);

			if ($luna_config['o_censoring'] == '1')
				$user_title = censor_words($user_title);

			// Format the online indicator, those are ment as CSS classes
			$is_online = ($cur_comment['is_online'] == $cur_comment['commenter_id']) ? 'is-online' : 'is-offline';

			// We only show location, register date, comment count and the contact links if "Show user info" is enabled
			if ($luna_config['o_show_user_info'] == '1') {
				if ($cur_comment['location'] != '') {
					if ($luna_config['o_censoring'] == '1')
						$cur_comment['location'] = censor_words($cur_comment['location']);

					$user_info[] = '<dd><span>'.__('From:', 'luna').' '.luna_htmlspecialchars($cur_comment['location']).'</span></dd>';
				}

				if ($luna_config['o_show_comment_count'] == '1' || $luna_user['is_admmod'])
					$user_info[] = '<dd><span>'._n('Comment:', 'Comments:', $cur_comment['num_comments'], 'luna').' '.forum_number_format($cur_comment['num_comments']).'</span></dd>';

				// Now let's deal with the contact links (Email and URL)
				if ((($cur_comment['email_setting'] == '0' && !$luna_user['is_guest']) || $luna_user['is_admmod']) && $luna_user['g_send_email'] == '1')
					$user_actions[] = '<a class="btn btn-primary btn-xs" href="mailto:'.luna_htmlspecialchars($cur_comment['email']).'">'.__('Email', 'luna').'</a>';
				elseif ($cur_comment['email_setting'] == '1' && !$luna_user['is_guest'] && $luna_user['g_send_email'] == '1')
					$user_actions[] = '<a class="btn btn-primary btn-xs" href="misc.php?email='.$cur_comment['commenter_id'].'">'.__('Email', 'luna').'</a>';

				if ($cur_comment['url'] != '') {
					if ($luna_config['o_censoring'] == '1')
						$cur_comment['url'] = censor_words($cur_comment['url']);

					$user_actions[] = '<a class="btn btn-primary btn-xs" href="'.luna_htmlspecialchars($cur_comment['url']).'" rel="nofollow">'.__('Website', 'luna').'</a>';
				}


				if ($luna_user['is_admmod']) {
					$user_actions[] = '<a class="btn btn-primary btn-xs" href="backstage/moderate.php?get_host='.$cur_comment['id'].'" title="'.luna_htmlspecialchars($cur_comment['commenter_ip']).'">'.__('IP log', 'luna').'</a>';
				}
			}


			if ($luna_user['is_admmod']) {
				if ($cur_comment['admin_note'] != '')
					$user_info[] = '<dd><span>'.__('Note:', 'luna').' <strong>'.luna_htmlspecialchars($cur_comment['admin_note']).'</strong></span></dd>';
			}
		}
		// If the commenter is a guest (or a user that has been deleted)
		else {
			$username = luna_htmlspecialchars($cur_comment['username']);
			$user_title = get_title($cur_comment);

			if ($luna_user['is_admmod'])
				$user_info[] = '<dd><span><a href="backstage/moderate.php?get_host='.$cur_comment['id'].'" title="'.luna_htmlspecialchars($cur_comment['commenter_ip']).'">'.__('IP log', 'luna').'</a></span></dd>';

			if ($luna_config['o_show_user_info'] == '1' && $cur_comment['commenter_email'] != '' && !$luna_user['is_guest'] && $luna_user['g_send_email'] == '1')
				$user_actions[] = '<span class="email"><a href="mailto:'.luna_htmlspecialchars($cur_comment['commenter_email']).'">'.__('Email', 'luna').'</a></span>';
		}

		// Get us the avatar
		if ($luna_config['o_avatars'] == '1' && $luna_user['show_avatars'] != '0') {
			if (isset($user_avatar_cache[$cur_comment['commenter_id']]))
				$user_avatar = $user_avatar_cache[$cur_comment['commenter_id']];
			else
				$user_avatar = draw_user_avatar($cur_comment['commenter_id'], false, 'media-object media-avatar');
		}

		// Generation comment action array (quote, edit, delete etc.)
        if (!$is_admmod) {
			if ($cur_thread['closed'] == 0) {
				if ($cur_comment['commenter_id'] == $luna_user['id']) {
					if ($luna_user['g_edit_comments'] == 1)
						$comment_actions[] = '<a href="edit.php?id='.$cur_comment['id'].'" class="btn btn-link"><i class="fa fa-fw fa-pencil"></i> '.__('Edit', 'luna').'</a>';
                }
                
				if (($cur_thread['comment'] == 0 && $luna_user['g_comment'] == 1) || $cur_thread['comment'] == 1)
					$comment_actions[] = '<a href="comment.php?tid='.$id.'&amp;qid='.$cur_comment['id'].'" class="btn btn-link"><i class="fa fa-fw fa-quote-right"></i> '.__('Quote', 'luna').'</a>';

				if ($cur_forum['solved'] == 1)
					if ($luna_user['username'] == $started_by)
						if ($cur_comment['id'] == $cur_thread['answer'])
							$comment_actions[] = '<a href="misc.php?unanswer='.$cur_comment['id'].'&amp;tid='.$id.'" class="btn btn-link"><i class="fa fa-fw fa-times"></i> '.__('Unsolved', 'luna').'</a>';
						else
							$comment_actions[] = '<a href="misc.php?answer='.$cur_comment['id'].'&amp;tid='.$id.'" class="btn btn-link"><i class="fa fa-fw fa-check"></i> '.__('Answer', 'luna').'</a>';
                
				if ($cur_comment['commenter_id'] == $luna_user['id']) {
					if ((($start_from + $comment_count) == 1 && $luna_user['g_delete_threads'] == 1) || (($start_from + $comment_count) > 1 && $luna_user['g_delete_comments'] == 1))
						$comment_actions[] = '<a href="delete.php?id='.$cur_comment['id'].'&action=delete" class="btn btn-link"><i class="fa fa-fw fa-trash"></i> '.__('Delete', 'luna').'</a>';
                    
					if ((($start_from + $comment_count) == 1 && $luna_user['g_soft_delete_threads'] == 1) || (($start_from + $comment_count) > 1 && $luna_user['g_soft_delete_comments'] == 1))
						if ($cur_comment['soft'] == 0)
							$comment_actions[] = '<a href="delete.php?id='.$cur_comment['id'].'&action=soft" class="btn btn-link"><i class="fa fa-fw fa-eye-slash"></i> '.__('Hide', 'luna').'</a>';
						else
							$comment_actions[] = '<a href="delete.php?id='.$cur_comment['id'].'&action=reset" class="btn btn-link"><i class="fa fa-fw fa-eye"></i> '.__('Show', 'luna').'</a>';
					
				}
			}
            
			if (!$luna_user['is_guest'])
				if ($cur_comment['marked'] == false)
					$comment_actions[] = '<a href="misc.php?report='.$cur_comment['id'].'" class="btn btn-link"><i class="fa fa-fw fa-flag"></i> '.__('Report', 'luna').'</a>';
				else
					$comment_actions[] = '<a class="btn btn-danger" disabled="disabled" href="misc.php?report='.$cur_comment['id'].'"><i class="fa fa-fw fa-flag"></i> '.__('Report', 'luna').'</a>';
		} else {
			if ($luna_user['g_id'] == LUNA_ADMIN || !in_array($cur_comment['commenter_id'], $admin_ids))
				$comment_actions[] = '<a href="edit.php?id='.$cur_comment['id'].'" class="btn btn-link"><i class="fa fa-fw fa-pencil"></i> '.__('Edit', 'luna').'</a>';
            
			$comment_actions[] = '<a href="comment.php?tid='.$id.'&amp;qid='.$cur_comment['id'].'" class="btn btn-link"><i class="fa fa-fw fa-quote-right"></i> '.__('Quote', 'luna').'</a>';

			if ($cur_forum['solved'] == 1)
				if ($cur_comment['id'] == $cur_thread['answer'])
					$comment_actions[] = '<a href="misc.php?unanswer='.$cur_comment['id'].'&amp;tid='.$id.'" class="btn btn-link"><i class="fa fa-fw fa-times"></i> '.__('Unsolved', 'luna').'</a>';
				else
					$comment_actions[] = '<a href="misc.php?answer='.$cur_comment['id'].'&amp;tid='.$id.'" class="btn btn-link"><i class="fa fa-fw fa-check"></i> '.__('Answer', 'luna').'</a>';
                
			if ($luna_user['g_id'] == LUNA_ADMIN || !in_array($cur_comment['commenter_id'], $admin_ids)) {
				$comment_actions[] = '<a href="delete.php?id='.$cur_comment['id'].'&action=delete" class="btn btn-link"><i class="fa fa-fw fa-trash"></i> '.__('Delete', 'luna').'</a>';
                
				if ($cur_comment['soft'] == 0)
					$comment_actions[] = '<a href="delete.php?id='.$cur_comment['id'].'&action=soft" class="btn btn-link"><i class="fa fa-fw fa-eye-slash"></i> '.__('Hide', 'luna').'</a>';
				else
					$comment_actions[] = '<a href="delete.php?id='.$cur_comment['id'].'&action=reset" class="btn btn-link"><i class="fa fa-fw fa-eye"></i> '.__('Show', 'luna').'</a>';
			}
            
			if ($cur_comment['marked'] == false)
				$comment_actions[] = '<a href="misc.php?report='.$cur_comment['id'].'" class="btn btn-link"><i class="fa fa-fw fa-flag"></i> '.__('Report', 'luna').'</a>';
			else
				$comment_actions[] = '<a class="btn btn-danger" disabled="disabled" href="misc.php?report='.$cur_comment['id'].'"><i class="fa fa-fw fa-flag"></i> '.__('Report', 'luna').'</a>';
		}

		// Perform the main parsing of the message (BBCode, smilies, censor words etc)
		$cur_comment['message'] = parse_message($cur_comment['message']);

		// Do signature parsing/caching
		if ($luna_config['o_signatures'] == '1' && $cur_comment['signature'] != '' && $luna_user['show_sig'] != '0') {
			if (isset($signature_cache[$cur_comment['commenter_id']]))
				$signature = $signature_cache[$cur_comment['commenter_id']];
			else {
				$signature = parse_signature($cur_comment['signature']);
				$signature_cache[$cur_comment['commenter_id']] = $signature;
			}
		}

		require get_view_path('comment.php');
	}

}

function draw_response_list() {
	global $result, $db, $luna_config, $id, $comment_ids, $is_admmod, $start_from, $comment_count, $admin_ids, $luna_user, $inbox;

	while ($cur_comment = $db->fetch_assoc($result)) {
		$comment_count++;
		$user_avatar = '';
		$user_info = array();
		$user_contacts = array();
		$comment_actions = array();
		$is_online = '';
		$signature = '';

		// If the commenter is a registered user
		if ($cur_comment['id']) {
			if ($luna_user['g_view_users'] == '1')
				$username = '<a href="profile.php?id='.$cur_comment['sender_id'].'">'.user_append_rank_time(luna_htmlspecialchars($cur_comment['sender']), $cur_comment['g_id'], ($cur_comment['premium_time'] > time() && $cur_comment['gold_time'] > time() ? 2 : ($cur_comment['gold_time'] > time() && $cur_comment['premium_time'] < time() ? 1 : 0))).'</a>';
			else
				$username = user_append_rank_time(luna_htmlspecialchars($cur_comment['sender']), $cur_comment['g_id'], ($cur_comment['premium_time'] > time() && $cur_comment['gold_time'] > time() ? 2 : ($cur_comment['gold_time'] > time() && $cur_comment['premium_time'] < time() ? 1 : 0)));

			$user_title = get_title($cur_comment);

			if ($luna_config['o_censoring'] == '1')
				$user_title = censor_words($user_title);

			// Format the online indicator
			$is_online = ($cur_comment['is_online'] == $cur_comment['sender_id']) ? '<strong>'.__('Online:', 'luna').'</strong>' : '<span>'.__('Offline', 'luna').'</span>';

			if ($luna_config['o_avatars'] == '1' && $luna_user['show_avatars'] != '0') {
				if (isset($user_avatar_cache[$cur_comment['sender_id']]))
					$user_avatar = $user_avatar_cache[$cur_comment['sender_id']];
				else
					$user_avatar = $user_avatar_cache[$cur_comment['sender_id']] = generate_avatar_markup($cur_comment['sender_id']);
			}

			// We only show location, register date, comment count and the contact links if "Show user info" is enabled
			if ($luna_config['o_show_user_info'] == '1') {
				if ($cur_comment['location'] != '') {
					if ($luna_config['o_censoring'] == '1')
						$cur_comment['location'] = censor_words($cur_comment['location']);

					$user_info[] = '<dd><span>'.__('From:', 'luna').' '.luna_htmlspecialchars($cur_comment['location']).'</span></dd>';
				}

				$user_info[] = '<dd><span>'.__('Registered since', 'luna').' '.format_time($cur_comment['registered'], true).'</span></dd>';

				if ($luna_config['o_show_comment_count'] == '1' || $luna_user['is_admmod'])
					$user_info[] = '<dd><span>'.__('Comments:', 'luna').' '.forum_number_format($cur_comment['num_comments']).'</span></dd>';

				// Now let's deal with the contact links (Email and URL)
				if ((($cur_comment['email_setting'] == '0' && !$luna_user['is_guest']) || $luna_user['is_admmod']) && $luna_user['g_send_email'] == '1')
					$user_contacts[] = '<span class="email"><a href="mailto:'.$cur_comment['email'].'">'.__('Email', 'luna').'</a></span>';
				elseif ($cur_comment['email_setting'] == '1' && !$luna_user['is_guest'] && $luna_user['g_send_email'] == '1')
					$user_contacts[] = '<span class="email"><a href="misc.php?email='.$cur_comment['sender_id'].'">'.__('Email', 'luna').'</a></span>';

				if ($luna_config['o_enable_inbox'] == '1' && !$luna_user['is_guest'] && $luna_user['g_inbox'] == '1' && $luna_user['use_inbox'] == '1' && $cur_comment['use_inbox'] == '1') {
					$pid = isset($cur_comment['sender_id']) ? $cur_comment['sender_id'] : $cur_comment['sender_id'];
					$user_contacts[] = '<span class="email"><a href="new_inbox.php?uid='.$pid.'">'.__('PM', 'luna').'</a></span>';
				}

				if ($cur_comment['url'] != '')
					$user_contacts[] = '<span class="website"><a href="'.luna_htmlspecialchars($cur_comment['url']).'">'.__('Website', 'luna').'</a></span>';

			}

			if ($luna_user['is_admmod']) {
				$user_info[] = '<dd><span><a href="backstage/moderate.php?get_host='.$cur_comment['sender_ip'].'" title="'.$cur_comment['sender_ip'].'">'.__('IP log', 'luna').'</a></span></dd>';

				if ($cur_comment['admin_note'] != '')
					$user_info[] = '<dd><span>'.__('Note:', 'luna').' <strong>'.luna_htmlspecialchars($cur_comment['admin_note']).'</strong></span></dd>';
			}
		} else { // If the commenter is a guest (or a user that has been deleted)
			$username = luna_htmlspecialchars($cur_comment['username']);
			$user_title = get_title($cur_comment);

			if ($luna_user['is_admmod'])
				$user_info[] = '<dd><span><a href="backstage/moderate.php?get_host='.$cur_comment['sender_id'].'" title="'.$cur_comment['sender_ip'].'">'.__('IP log', 'luna').'</a></span></dd>';

			if ($luna_config['o_show_user_info'] == '1' && $cur_comment['commenter_email'] != '' && !$luna_user['is_guest'] && $luna_user['g_send_email'] == '1')
				$user_contacts[] = '<span class="email"><a href="mailto:'.$cur_comment['commenter_email'].'">'.__('Email', 'luna').'</a></span>';
		}

		$username_quickreply = luna_htmlspecialchars($cur_comment['username']);

		$comment_actions[] = '<a href="new_inbox.php?reply='.$cur_comment['shared_id'].'&amp;quote='.$cur_comment['mid'].'" class="btn btn-link"><i class="fa fa-fw fa-quote-right"></i> '.__('Quote', 'luna').'</a>';

		// Perform the main parsing of the message (BBCode, smilies, censor words etc)
		$cur_comment['message'] = parse_message($cur_comment['message']);

		// Do signature parsing/caching
		if ($luna_config['o_signatures'] == '1' && $cur_comment['signature'] != '' && $luna_user['show_sig'] != '0') {
			if (isset($signature_cache[$cur_comment['id']]))
				$signature = $signature_cache[$cur_comment['id']];
			else {
				$signature = parse_signature($cur_comment['signature']);
				$signature_cache[$cur_comment['id']] = $signature;
			}
		}

		require get_view_path('comment.php');
	}
}

function draw_user_list() {
	global $db, $where_sql, $sort_query, $start_from;

	// Retrieve a list of user IDs, LIMIT is (really) expensive so we only fetch the IDs here then later fetch the remaining data
	$result = $db->query('SELECT u.id FROM '.$db->prefix.'users AS u WHERE u.id>1 AND u.group_id!='.LUNA_UNVERIFIED.(!empty($where_sql) ? ' AND '.implode(' AND ', $where_sql) : '').' ORDER BY '.$sort_query.', u.id ASC LIMIT '.$start_from.', 50') or error('Unable to fetch user IDs', __FILE__, __LINE__, $db->error());

	if ($db->num_rows($result)) {
		$user_ids = array();
		for ($i = 0;$cur_user_id = $db->result($result, $i);$i++)
			$user_ids[] = $cur_user_id;

		// Grab the users
		$result = $db->query('SELECT u.id, u.username, u.title, u.num_comments, u.registered, g.g_id, g.g_user_title FROM '.$db->prefix.'users AS u LEFT JOIN '.$db->prefix.'groups AS g ON g.g_id=u.group_id WHERE u.id IN('.implode(',', $user_ids).') ORDER BY '.$sort_query.', u.id ASC') or error('Unable to fetch user list', __FILE__, __LINE__, $db->error());

		while ($user_data = $db->fetch_assoc($result)) {
			$user_title_field = get_title($user_data);
			$user_avatar = draw_user_avatar($user_data['id'], true, 'media-object');

			require get_view_path('user.php');

		}
	} else
		echo '<p>'.__('Your search returned no hits.', 'luna').'</p>';
}

function draw_delete_form($id) {
	global $is_thread_comment;

?>
		<form method="post" action="delete.php?id=<?php echo $id ?>">
			<p><?php echo ($is_thread_comment) ? '<strong>'.__('This is the first comment in the thread, the whole thread will be permanently deleted.', 'luna').'</strong>' : '' ?><br /><?php _e('The comment you have chosen to delete is set out below for you to review before proceeding.', 'luna') ?></p>
			<div class="btn-toolbar">
				<a class="btn btn-default" href="thread.php?pid=<?php echo $id ?>#p<?php echo $id ?>"><span class="fa fa-fw fa-chevron-left"></span> <?php _e('Cancel', 'luna') ?></a>
				<button type="submit" class="btn btn-danger" name="delete"><span class="fa fa-fw fa-trash"></span> <?php _e('Delete', 'luna') ?></button>
			</div>
		</form>
<?php
}

function draw_soft_delete_form($id) {
	global $is_thread_comment;

?>
		<form method="post" action="delete.php?id=<?php echo $id ?>&action=soft">
			<p><?php echo ($is_thread_comment) ? '<strong>'.__('This is the first comment in the thread, the whole thread will be hidden.', 'luna').'</strong>' : '' ?><br /><?php _e('The comment you have chosen to hide is set out below for you to review before proceeding.', 'luna') ?></p>
			<div class="btn-toolbar">
				<a class="btn btn-default" href="thread.php?pid=<?php echo $id ?>#p<?php echo $id ?>"><span class="fa fa-fw fa-chevron-left"></span> <?php _e('Cancel', 'luna') ?></a>
				<button type="submit" class="btn btn-danger" name="soft_delete"><span class="fa fa-fw fa-trash"></span> <?php _e('Hide', 'luna') ?></button>
			</div>
		</form>
<?php
}

function draw_soft_reset_form($id) {
	global $is_thread_comment;

?>
		<form method="post" action="delete.php?id=<?php echo $id ?>&action=reset">
			<p><?php _e('This comment has been hidden. We\'ll unhide it again with a click on the button.', 'luna') ?></p>
			<div class="btn-toolbar">
				<a class="btn btn-default" href="thread.php?pid=<?php echo $id ?>#p<?php echo $id ?>"><span class="fa fa-fw fa-chevron-left"></span> <?php _e('Cancel', 'luna') ?></a>
				<button type="submit" class="btn btn-primary" name="reset"><span class="fa fa-fw fa-undo"></span> <?php _e('Show', 'luna') ?></button>
			</div>
		</form>
<?php
}

function draw_delete_title() {
	global $is_thread_comment, $cur_comment;

	printf($is_thread_comment ? __('Thread started by %s - %s', 'luna') : __('Comment by %s - %s', 'luna'), '<strong>'.luna_htmlspecialchars($cur_comment['commenter']).'</strong>', format_time($cur_comment['commented']));
}

function draw_rules_form() {
	global $luna_config;
?>

<form method="get" action="register.php">
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title"><?php _e('You must agree to the following in order to register', 'luna') ?></h3>
		</div>
		<div class="panel-body">
			<fieldset>
				<div class="usercontent"><?php echo $luna_config['o_rules_message'] ?></div>
			</fieldset>
		</div>
		<div class="panel-footer">
			<div class="btn-group"><input type="submit" class="btn btn-primary" name="agree" value="<?php _e('Agree', 'luna') ?>" /></div>
		</div>
	</div>
</form>
<?php
}

function draw_search_results() {
	global $search_set, $cur_search, $luna_user, $luna_config, $thread_count, $cur_thread, $subject_status, $last_comment_date, $tracked_threads, $start_from;

	foreach ($search_set as $cur_search) {
		$forum = '<a href="viewforum.php?id='.$cur_search['forum_id'].'">'.luna_htmlspecialchars($cur_search['forum_name']).'</a>';

		if ($luna_config['o_censoring'] == '1')
			$cur_search['subject'] = censor_words($cur_search['subject']);

		/* if ($show_as == 'comments') {
			require get_view_path('comment.php');
		} else { */
			++$thread_count;
			$status_text = array();
			$item_status = ($thread_count % 2 == 0) ? 'roweven' : 'rowodd';
			$icon_type = 'icon';

			$subject = luna_htmlspecialchars($cur_search['subject']);
			$by = '<span class="byuser">'.__('by', 'luna').' '.luna_htmlspecialchars($cur_search['commenter']).'</span>';
        
            if (isset($cur_search['pid']))
                $url = 'thread.php?id='.$cur_search['tid'].'#p'.$cur_search['pid'];
            else
                $url = 'thread.php?id='.$cur_search['tid'];

			if ($cur_search['pinned'] == '1') {
				$item_status .= ' pinned-item';
				$status_text[] = '<i class="fa fa-fw fa-thumb-tack status-pinned"></i>';
			}

			if (isset($cur_search['solved'])) {
				$item_status .= ' solved-item';
				$status_text[] = '<i class="fa fa-fw fa-check status-solved"></i>';
			}

			if ($cur_search['important']) {
				$item_status .= ' important-item';
				$status_text[] = '<i class="fa fa-fw fa-map-marker status-important"></i>';
			}

			if ($cur_search['closed'] != '0') {
				$status_text[] = '<i class="fa fa-fw fa-lock status-closed"></i>';
				$item_status .= ' closed-item';
			}

			if (!$luna_user['is_guest'] && $cur_search['last_comment'] > $luna_user['last_visit'] && (!isset($tracked_threads['threads'][$cur_search['tid']]) || $tracked_threads['threads'][$cur_search['tid']] < $cur_search['last_comment']) && (!isset($tracked_threads['forums'][$cur_search['forum_id']]) || $tracked_threads['forums'][$cur_search['forum_id']] < $cur_search['last_comment'])) {
				$item_status .= ' new-item';
				$icon_type = 'icon icon-new';
				$status_text[] = '<a href="thread.php?id='.$cur_search['tid'].'&amp;action=new" title="'.__('Go to the first new comment in the thread.', 'luna').'"><i class="fa fa-fw fa-bell status-new"></i></a>';
			}
        
            $subject_status = implode(' ', $status_text);

			$num_pages_thread = ceil(($cur_search['num_replies'] + 1) / $luna_user['disp_comments']);

			if ($num_pages_thread > 1)
				$subject_multipage = '<span class="inline-pagination"> &middot;'.simple_paginate($num_pages_thread, -1, 'thread.php?id='.$cur_search['tid']).'</span>';
			else
				$subject_multipage = null;

			$last_commenter = '<a href="thread.php?pid='.$cur_search['last_comment_id'].'#p'.$cur_search['last_comment_id'].'">'.format_time($cur_search['last_comment']).'</a> <span class="byuser">'.__('by', 'luna').'</span> '.luna_htmlspecialchars($cur_search['last_commenter']);

            // Load cached forums
            if (file_exists(LUNA_CACHE_DIR.'cache_forums.php'))
                include_once LUNA_CACHE_DIR.'cache_forums.php';

            if (!defined('LUNA_LIST_LOADED')) {
                if (!defined('LUNA_CACHE_FUNCTIONS_LOADED'))
                    require LUNA_ROOT.'include/cache.php';

                generate_forum_cache();
                require LUNA_CACHE_DIR.'cache_forums.php';
            }

            foreach ($luna_forums as $cur_forum) {
                if ($cur_search['forum_id'] == $cur_forum['id']) {
                    $forum_name = luna_htmlspecialchars($cur_forum['forum_name']);
                    $forum_color = $cur_forum['color'];
                    if ($cur_forum['icon'] != NULL)
                        $faicon = '<span class="fa fa-fw fa-'.$cur_forum['icon'].'"></span> ';
                    else
                        $faicon = '';
                    
                    $forum_name = '<a class="in-forum" href="viewforum.php?id='.$cur_forum['id'].'" style="color: '.$forum_color.';">'.$faicon.' '.$forum_name.'</a>';
                }
            }


			require get_view_path('search-thread.php');
		//}
	}

}

function draw_mail_form($recipient_id) {
	global $recipient_id, $redirect_url, $cur_index;
?>

<form id="email" method="post" action="misc.php?email=<?php echo $recipient_id ?>" onsubmit="this.submit.disabled=true;if(process_form(this)){return true;}else{this.submit.disabled=false;return false;}">
	<div class="editor">
		<input type="hidden" name="form_sent" value="1" />
		<input type="hidden" name="redirect_url" value="<?php echo luna_htmlspecialchars($redirect_url) ?>" />
		<input class="info-textfield form-control" placeholder="<?php _e('Subject', 'luna') ?>" type="text" name="req_subject" maxlength="70" tabindex="<?php echo $cur_index++ ?>" autofocus />
		<textarea name="req_message" class="form-control textarea" placeholder="<?php _e('Mail', 'luna') ?>" rows="10" tabindex="<?php echo $cur_index++ ?>"></textarea>
		<div class="btn-toolbar btn-toolbar-bottom">
			<div class="btn-group pull-right">
				<button class="btn btn-with-text btn-default" type="submit" name="submit" accesskey="s" tabindex="<?php echo $cur_index++ ?>"><span class="fa fa-fw fa-envelope-o"></span> <?php _e('Send', 'luna') ?></button>
			</div>
		</div>
	</div>
</form>
<?php
}

function draw_report_form($comment_id) {
	global $comment_id;
?>

<form class="form-horizontal" id="report" method="post" action="misc.php?report=<?php echo $comment_id ?>" onsubmit="this.submit.disabled=true;if(process_form(this)){return true;}else{this.submit.disabled=false;return false;}">
	<div class="panel panel-default">
		<div class="panel-heading">
			<h3 class="panel-title"><?php _e('Report', 'luna') ?></h3>
		</div>
		<div class="panel-body">
			<fieldset>
				<input type="hidden" name="form_sent" value="1" />
				<div class="form-group">
					<label class="col-sm-3 control-label"><?php _e('Tell us why you are reporting this', 'luna') ?></label>
					<div class="col-sm-9">
						<textarea class="form-control" name="req_reason" rows="5"></textarea>
					</div>
				</div>
				<div class="form-group">
					<div class="col-sm-3"></div>
					<div class="col-sm-9">
						<a href="thread.php?pid=<?php echo $comment_id ?>#p<?php echo $comment_id ?>" class="btn btn-default"><span class="fa fa-fw fa-chevron-left"></span> <?php _e('Cancel', 'luna') ?></a>
						<button type="submit" class="btn btn-primary" name="submit" accesskey="s"><span class="fa fa-fw fa-check"></span> <?php _e('Submit', 'luna') ?></button>
					</div>
				</div>
			</fieldset>
		</div>
	</div>
</form>
<?php
}


function draw_search_forum_list() {
	global $db, $luna_config, $luna_user;

	$result = $db->query('SELECT c.id AS cid, c.cat_name, f.id AS fid, f.forum_name FROM '.$db->prefix.'categories AS c INNER JOIN '.$db->prefix.'forums AS f ON c.id=f.cat_id LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=f.id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.read_forum IS NULL OR fp.read_forum=1) ORDER BY c.disp_position, c.id, f.disp_position', true) or error('Unable to fetch category/forum list', __FILE__, __LINE__, $db->error());

	// We either show a list of forums of which multiple can be selected
	if ($luna_config['o_search_all_forums'] == '1' || $luna_user['is_admmod']) {
		echo "\t\t\t\t\t\t".'<div class="conl multiselect"><h3>'.__('Forums to search in', 'luna').'</h3>'."\n";
		echo "\t\t\t\t\t\t".'<div>'."\n";

		$cur_category = 0;
		while ($cur_forum = $db->fetch_assoc($result)) {
			if ($cur_forum['cid'] != $cur_category) { // A new category since last iteration?
				if ($cur_category) {
					echo "\t\t\t\t\t\t\t\t".'</div>'."\n";
					echo "\t\t\t\t\t\t\t".'</fieldset>'."\n";
				}
				echo "\t\t\t\t\t\t\t".'<fieldset><h4><span>'.luna_htmlspecialchars($cur_forum['cat_name']).'</span></h4>'."\n";
				echo "\t\t\t\t\t\t\t\t".'<div>';
				$cur_category = $cur_forum['cid'];
			}
			echo "\t\t\t\t\t\t\t\t".'<label><input type="checkbox" name="forums[]" id="forum-'.$cur_forum['fid'].'" value="'.$cur_forum['fid'].'" /> '.luna_htmlspecialchars($cur_forum['forum_name']).'</label><br />'."\n";
		}

		if ($cur_category) {
			echo "\t\t\t\t\t\t\t\t".'</div>'."\n";
			echo "\t\t\t\t\t\t\t".'</fieldset>'."\n";
		}

		echo "\t\t\t\t\t\t".'</div>'."\n";
		echo "\t\t\t\t\t\t".'</div>'."\n";
	}
	// ... or a simple select list for one forum only
	else {
		echo "\t\t\t\t\t\t".'<h3>'.__('Forum to search in', 'luna').'</h3>'."\n";
		echo "\t\t\t\t\t\t".'<select id="forum" name="forum" class="form-control">'."\n";

		$cur_category = 0;
		while ($cur_forum = $db->fetch_assoc($result)) {
			if ($cur_forum['cid'] != $cur_category) { // A new category since last iteration?
				if ($cur_category)
					echo "\t\t\t\t\t\t\t".'</optgroup>'."\n";

				echo "\t\t\t\t\t\t\t".'<optgroup label="'.luna_htmlspecialchars($cur_forum['cat_name']).'">'."\n";
				$cur_category = $cur_forum['cid'];
			}

			echo "\t\t\t\t\t\t\t\t".'<option value="'.$cur_forum['fid'].'">'.($cur_forum['parent_forum_id'] == 0 ? '' : '&nbsp;&nbsp;&nbsp;').luna_htmlspecialchars($cur_forum['forum_name']).'</option>'."\n";
		}

		echo "\t\t\t\t\t\t\t".'</optgroup>'."\n";
		echo "\t\t\t\t\t\t".'</select>'."\n";
	}
}

function draw_mark_read($class, $page) {
	global $luna_user, $id;

	if (!empty($class))
		$classes = ' class="'.$class.'"';

	if ($page == 'forum')
		$url = 'misc.php?action=markread&amp;csrf_token='.luna_csrf_token();
	elseif ($page == 'forumview')
		$url = 'misc.php?action=markforumread&amp;fid='.$id.'&amp;csrf_token='.luna_csrf_token();

	if (!$luna_user['is_guest'])
		echo '<a'.$classes.' href="'.$url.'"><span class="fa fa-map-marker"></span> '.__('Mark as read', 'luna').'</a>';
}

function draw_wall_error($description, $action = NULL, $title = NULL) {
?>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><?php _e('Luna', 'luna') ?></title>
		<link rel="stylesheet" type="text/css" href="vendor/css/bootstrap.min.css" />
		<link rel="stylesheet" type="text/css" href="vendor/css/system.css" />
	</head>
	<body class="wall">
		<h1><?php if ($title != NULL) { echo $title; } else { echo 'Luna'; } ?></h1>
		<p class="lead"><?php echo $description; ?></p>
		<?php if ($action != NULL) { ?><p><?php echo $action; ?></p><?php } ?>
	</body>
</html>
<?php
}