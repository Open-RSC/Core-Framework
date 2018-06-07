<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */
if (!defined('FORUM'))
	exit;
?>
<div class="col-sm-3">
	<div class="panel">
	<form class="form-horizontal panel-body" method="get" action="index.php">
		<div class="form-group">
		<label class="search-form__label" for="player">
			Search
		</label>
		<div class="searchTab">
			<input type="text" class="form-control" name="player_input" maxlength="25" tabindex="2" value="<?php echo $search_query; ?>">
		</div>
		<label class="search-form__label" for="player">
		   Search Option
		</label>
		<div class="selectTab">
		   <select class='form-control' id='base_group' name='search_type'>
		   <?php 
		   echo "
		   
			" . (isset($search_type) ? "<option selected='selected' value='" . $search_type . "'>" . ($search_type == 1 ? "Forum Name" : ($search_type == 0 ? "Character Name" : "IP Address")) . "</option>" : null) . "
			" . (isset($search_type) && $search_type == 0 ? null : "<option value='0'>Character Name</option>") . "
			" . (isset($search_type) && $search_type == 1 ? null : "<option value='1'>Forum Name</option>") . "
			" . (isset($search_type) && $search_type == 2 ? null : "<option value='2'>IP Address</option>") . "
			";
			
			?>
			</select>
		</div>
		<button class="block-btn block-btn--form" type="submit">Search</button>
		</div>
	</form>
	</div>
</div>