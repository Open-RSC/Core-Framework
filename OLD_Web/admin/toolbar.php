<div class="btn-toolbar btn-toolbar-top">
	<div class="btn-group btn-transparent">
		<a class="btn btn-editor" href="javascript:void(0);" onclick="AddTag('inline','b');" title="<?php _e('Bold', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-bold fa-fw"></span></a>
		<a class="btn btn-editor" href="javascript:void(0);" onclick="AddTag('inline','u');" title="<?php _e('Underline', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-underline fa-fw"></span></a>
		<a class="btn btn-editor hidden-sm hidden-xs" href="javascript:void(0);" onclick="AddTag('inline','i');" title="<?php _e('Italic', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-italic fa-fw"></span></a>
		<a class="btn btn-editor hidden-md hidden-sm hidden-xs" href="javascript:void(0);" onclick="AddTag('inline','s');" title="<?php _e('Strike', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-strikethrough fa-fw"></span></a>
	</div>
	<div class="btn-group btn-transparent">
		<a class="btn btn-editor" href="javascript:void(0);" onclick="AddTag('inline','h');" title="<?php _e('Heading', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-header fa-fw"></span></a>
		<a class="btn btn-editor hidden-md hidden-sm hidden-xs" href="javascript:void(0);" onclick="AddTag('inline','sub');" title="<?php _e('Subscript', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-subscript fa-fw"></span></a>
		<a class="btn btn-editor hidden-md hidden-sm hidden-xs" href="javascript:void(0);" onclick="AddTag('inline','sup');" title="<?php _e('Superscript', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-superscript fa-fw"></span></a>
	</div>
	<div class="btn-group btn-transparent">
		<a class="btn btn-editor" href="javascript:void(0);" onclick="AddTag('inline','quote');" title="<?php _e('Quote', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-quote-left fa-fw"></span></a>
		<a class="btn btn-editor hidden-sm hidden-xs" href="javascript:void(0);" onclick="AddTag('code','code');" title="<?php _e('Code', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-code fa-fw"></span></a>
		<a class="btn btn-editor hidden-md hidden-sm hidden-xs" href="javascript:void(0);" onclick="AddTag('inline','c');" title="<?php _e('Inline code', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-file-code-o fa-fw"></span></a>
	</div>
	<div class="btn-group btn-transparent">
		<a class="btn btn-editor" href="javascript:void(0);" onclick="AddTag('inline','url');" title="<?php _e('URL', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-link fa-fw"></span></a>
		<a class="btn btn-editor hidden-xs" href="javascript:void(0);" onclick="AddTag('inline','img');" title="<?php _e('Image', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-image fa-fw"></span></a>
		<a class="btn btn-editor hidden-xs" href="javascript:void(0);" onclick="AddTag('inline','video');" title="<?php _e('Video', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-play fa-fw"></span></a>
	</div>
	<div class="btn-group btn-transparent">
		<a class="btn btn-editor" href="javascript:void(0);" onclick="AddTag('list', 'list');" title="<?php _e('List', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-list-ul fa-fw"></span></a>
		<a class="btn btn-editor" href="javascript:void(0);" onclick="AddTag('inline','*');" title="<?php _e('List item', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-asterisk fa-fw"></span></a>
	</div>
	<div class="btn-group pull-right hidden-lg">
		<a class="btn btn-transparent dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
			<span class="fa fa-fw fa-ellipsis-h"></span>
		</a>
		<ul class="dropdown-menu" role="menu">
			<li class="hidden-lg hidden-md"><a href="javascript:void(0);" onclick="AddTag('inline','i');" title="<?php _e('Italic', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-italic fa-fw"></span> <?php _e('Italic', 'luna'); ?></a></li>
			<li class="hidden-lg hidden-md"><a href="javascript:void(0);" onclick="AddTag('code','code');" title="<?php _e('Code', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-code fa-fw"></span> <?php _e('Code', 'luna'); ?></a></li>
			<li class="hidden-lg"><a href="javascript:void(0);" onclick="AddTag('inline','c');" title="<?php _e('Inline code', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-file-code-o fa-fw"></span> <?php _e('Inline code', 'luna'); ?></a></li>
			<li class="hidden-lg hidden-md hidden-sm"><a href="javascript:void(0);" onclick="AddTag('inline','img');" title="<?php _e('Image', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-image fa-fw"></span> <?php _e('Image', 'luna'); ?></a></li>
			<li class="hidden-lg hidden-md hidden-sm"><a href="javascript:void(0);" onclick="AddTag('inline','video');" title="<?php _e('Video', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-play fa-fw"></span> <?php _e('Video', 'luna'); ?></a></li>
			<li><a href="javascript:void(0);" onclick="AddTag('inline','s');" title="<?php _e('Strike', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-strikethrough fa-fw"></span> <?php _e('Strike', 'luna'); ?></a></li>
			<li><a href="javascript:void(0);" onclick="AddTag('inline','sub');" title="<?php _e('Subscript', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-subscript fa-fw"></span> <?php _e('Subscript', 'luna'); ?></a></li>
			<li><a href="javascript:void(0);" onclick="AddTag('inline','sup');" title="<?php _e('Superscript', 'luna'); ?>" tabindex="-1"><span class="fa fa-fw fa-superscript fa-fw"></span> <?php _e('Superscript', 'luna'); ?></a></li>
		</ul>
	</div>
</div>
<script>
function AddTag(type, tag) {
   var Field = document.getElementById('comment_field');
   var val = Field.value;
   var selected_txt = val.substring(Field.selectionStart, Field.selectionEnd);
   var before_txt = val.substring(0, Field.selectionStart);
   var after_txt = val.substring(Field.selectionEnd, val.length);
   if (type == 'inline')
	   Field.value = before_txt + '[' + tag + ']' + selected_txt + '[/' + tag + ']' + after_txt;
   else if (type == 'list')
	   Field.value = before_txt + '[list]' + "\r" + '[*]' + selected_txt + '[/*]' + "\r" + '[/list]' + after_txt;
   else if (type == 'code')
	   Field.value = before_txt + '[' + tag + ']' + "\r" + '[[language]]' + "\r" + selected_txt + "\r" + '[/' + tag + ']' + after_txt;
   else if (type == 'emoji')
	   Field.value = before_txt + ' ' + tag + ' ' + after_txt;

	document.getElementById('comment_field').focus();
}
</script>