<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

define('LUNA_ROOT', '../');
define('LUNA_SECTION', 'backstage');
define('LUNA_PAGE', 'update');

require LUNA_ROOT.'include/common.php';

if (!$luna_user['is_admmod']) {
	header("Location: login.php");
    exit;
}

$action = isset($_GET['action']) ? $_GET['action'] : null;

// Show phpinfo() output
if ($action == 'phpinfo' && $luna_user['g_id'] == LUNA_ADMIN) {
	// Is phpinfo() a disabled function?
	if (strpos(strtolower((string) ini_get('disable_functions')), 'phpinfo') !== false)
		message_backstage(__('The PHP function phpinfo() has been disabled on this server.', 'luna'));

	phpinfo();
	exit;
}

// Get the server load averages (if possible)
if (@file_exists('/proc/loadavg') && is_readable('/proc/loadavg')) {
	// We use @ just in case
	$fh = @fopen('/proc/loadavg', 'r');
	$load_averages = @fread($fh, 64);
	@fclose($fh);

	if (($fh = @fopen('/proc/loadavg', 'r'))) 	{
		$load_averages = fread($fh, 64);
		fclose($fh);
	} else
		$load_averages = '';

	$load_averages = @explode(' ', $load_averages);
	$server_load = isset($load_averages[2]) ? $load_averages[0].' '.$load_averages[1].' '.$load_averages[2] : __('Not available', 'luna');
} elseif (!in_array(PHP_OS, array('WINNT', 'WIN32')) && preg_match('%averages?: ([0-9\.]+),?\s+([0-9\.]+),?\s+([0-9\.]+)%i', @exec('uptime'), $load_averages))
	$server_load = $load_averages[1].' '.$load_averages[2].' '.$load_averages[3];
else
	$server_load = __('Not available', 'luna');

// Get number of current visitors
$result = $db->query('SELECT COUNT(user_id) FROM '.$db->prefix.'online WHERE idle=0') or error('Unable to fetch online count', __FILE__, __LINE__, $db->error());
$num_online = $db->result($result);

// Collect some additional info about MySQL
if ($db_type == 'mysql' || $db_type == 'mysqli' || $db_type == 'mysql_innodb' || $db_type == 'mysqli_innodb') {
	// Calculate total db size/row count
	$result = $db->query('SHOW TABLE STATUS LIKE \''.$db->prefix.'%\'') or error('Unable to fetch table status', __FILE__, __LINE__, $db->error());

	$total_records = $total_size = 0;
	while ($status = $db->fetch_assoc($result)) {
		$total_records += $status['Rows'];
		$total_size += $status['Data_length'] + $status['Index_length'];
	}

	$total_size = file_size($total_size);
}

// Check for the existence of various PHP opcode caches/optimizers
if (function_exists('mmcache'))
	$php_accelerator = '<a href="http://turck-mmcache.sourceforge.net/">'.__('Turck MMCache', 'luna').'</a>';
elseif (isset($_PHPA))
	$php_accelerator = '<a href="http://www.php-accelerator.co.uk/">'.__('ionCube PHP Accelerator', 'luna').'</a>';
elseif (ini_get('apc.enabled'))
	$php_accelerator ='<a href="http://www.php.net/apc/">'.__('Alternative PHP Cache (APC)', 'luna').'</a>';
elseif (ini_get('zend_optimizer.optimization_level'))
	$php_accelerator = '<a href="http://www.zend.com/products/guard/zend-optimizer/">'.__('Zend Optimizer', 'luna').'</a>';
elseif (ini_get('eaccelerator.enable'))
	$php_accelerator = '<a href="http://www.eaccelerator.net/">'.__('eAccelerator', 'luna').'</a>';
elseif (ini_get('xcache.cacher'))
	$php_accelerator = '<a href="http://xcache.lighttpd.net/">'.__('XCache', 'luna').'</a>';
else
	$php_accelerator = __('Non available', 'luna');

require 'header.php';
?>
<div class="row">
	<div class="col-sm-4">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title"><?php _e('Luna version information', 'luna') ?></h3>
            </div>
            <table class="table">
                <thead>
                    <tr>
                        <th class="col-md-6"></th>
                        <th class="col-md-6"><?php _e('Version', 'luna') ?></th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td><?php _e('Software version', 'luna') ?></td>
                        <td><?php echo $luna_config['o_cur_version']; ?></td>
                    </tr>
                    <tr>
                        <td><?php _e('Core version', 'luna') ?></td>
                        <td><?php echo $luna_config['o_core_version']; ?></td>
                    </tr>
                    <tr>
                        <td><?php _e('Database version', 'luna') ?></td>
                        <td><?php echo $luna_config['o_database_revision']; ?></td>
                    </tr>
                    <tr>
                        <td><?php _e('Bootstrap version', 'luna') ?></td>
                        <td>3.3.6</td>
                    </tr>
                    <tr>
                        <td><?php _e('Font Awesome version', 'luna') ?></td>
                        <td>4.6.3</td>
                    </tr>
                    <tr>
                        <td><?php _e('jQuery version', 'luna') ?></td>
                        <td>2.2.4</td>
                    </tr>
                </tbody>
            </table>
        </div>
	</div>
	<div class="col-sm-8">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title"><?php _e('Server statistics', 'luna') ?></h3>
            </div>
            <table class="table">
                <thead>
                    <tr>
                        <th class="col-md-4"><?php _e('Server load', 'luna') ?></th>
                        <?php if ($luna_user['g_id'] == LUNA_ADMIN): ?>
                        <th class="col-md-4"><?php _e('Environment', 'luna') ?></th>
                        <th class="col-md-4"><?php _e('Database', 'luna') ?></th>
                        <?php endif; ?>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td><?php printf(__('%s - %s user(s) online', 'luna')."\n", $server_load, $num_online) ?></td>
                        <?php if ($luna_user['g_id'] == LUNA_ADMIN): ?>
                        <td>
                            <?php printf(__('Operating system: %s', 'luna'), PHP_OS) ?><br />
                            <?php printf(__('PHP: %s - %s', 'luna'), phpversion(), '<a href="system.php?action=phpinfo">'.__('Show info', 'luna').'</a>') ?><br />
                            <?php printf(__('Accelerator: %s', 'luna')."\n", $php_accelerator) ?>
                        </td>
                        <td>
                            <?php echo implode(' ', $db->get_version())."\n" ?>
                            <?php if (isset($total_records) && isset($total_size)): ?>
                            <br /><?php printf(__('Rows: %s', 'luna')."\n", forum_number_format($total_records)) ?>
                            <br /><?php printf(__('Size: %s', 'luna')."\n", $total_size) ?>
                            <?php endif; ?>
                        </td>
                        <?php endif; ?>
                    </tr>
                </tbody>
            </table>
        </div>
	</div>
</div>
<?php

require 'footer.php';
