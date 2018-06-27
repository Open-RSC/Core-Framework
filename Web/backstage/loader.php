<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

define('LUNA_ROOT', '../');
require LUNA_ROOT.'include/common.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}

// The plugin to load should be supplied via GET
$plugin = isset($_GET['plugin']) ? $_GET['plugin'] : '';
if (!preg_match('%^AM?P_(\w*?)\.php$%i', $plugin))
	message_backstage(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

// AP_ == Admins only, AMP_ == admins and moderators
$prefix = substr($plugin, 0, strpos($plugin, '_'));
if ($luna_user['g_moderator'] == '1' && $prefix == 'AP')
	message_backstage(__('You do not have permission to access this page.', 'luna'), false, '403 Forbidden');

// Make sure the file actually exists
if (!file_exists(LUNA_ROOT.'plugins/'.$plugin))
	message_backstage(sprintf(__('There is no plugin called %s in the plugin directory.', 'luna'), $plugin));

// Construct REQUEST_URI if it isn't set
if (!isset($_SERVER['REQUEST_URI']))
	$_SERVER['REQUEST_URI'] = (isset($_SERVER['PHP_SELF']) ? $_SERVER['PHP_SELF'] : '').'?'.(isset($_SERVER['QUERY_STRING']) ? $_SERVER['QUERY_STRING'] : '');

define('LUNA_ACTIVE_PAGE', 'admin');
require 'header.php';

// Attempt to load the plugin. We don't use @ here to suppress error messages,
// because if we did and a parse error occurred in the plugin, we would only
// get the "blank page of death"
include LUNA_ROOT.'plugins/'.$plugin;

if (!defined('LUNA_PLUGIN_LOADED'))
	message_backstage(sprintf(__('Loading of the plugin - <strong>%s</strong> - failed.', 'luna'), $plugin));

// Output the clearer div

require 'footer.php';
