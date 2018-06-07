<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

/*-----------------------------------------------------------------------------

  INSTRUCTIONS

  This script is used to include information about your board from
  pages outside the forums and to syndicate news about recent
  discussions via RSS/Atom/XML. The script can display a list of
  recent discussions, a list of active users or a collection of
  general board statistics. The script can be called directly via
  an URL, from a PHP include command or through the use of Server
  Side Includes (SSI).

  The scripts behaviour is controlled via variables supplied in the
  URL to the script. The different variables are: action (what to
  do), show (how many items to display), fid (the ID or IDs of
  the forum(s) to poll for threads), nfid (the ID or IDs of forums
  that should be excluded), tid (the ID of the thread from which to
  display comments) and type (output as HTML or RSS). The only
  mandatory variable is action. Possible/default values are:

	action: feed - show most recent threads/comments (HTML or RSS)
			online - show users online (HTML)
			online_full - as above, but includes a full list (HTML)
			stats - show board statistics (HTML)

	type:   rss - output as RSS 2.0
			atom - output as Atom 1.0
			xml - output as XML
			html - output as HTML (<li>'s)

	fid:	One or more forum IDs (comma-separated). If ignored,
			threads from all readable forums will be pulled.

	nfid:   One or more forum IDs (comma-separated) that are to be
			excluded. E.g. the ID of a a test forum.

	tid:	A thread ID from which to show comments. If a tid is supplied,
			fid and nfid are ignored.

	show:   Any integer value between 1 and 50. The default is 15.

	order:  last_comment - show threads ordered by when they were last
						commented in, giving information about the reply.
			commented - show threads ordered by when they were first
					 commented, giving information about the original comment.

-----------------------------------------------------------------------------*/

define('LUNA_QUIET_VISIT', 1);

if (!defined('LUNA_ROOT'))
	define('LUNA_ROOT', dirname(__FILE__).'/');
require LUNA_ROOT.'include/common.php';

// The length at which thread subjects will be truncated (for HTML output)
if (!defined('LUNA_EXTERN_MAX_SUBJECT_LENGTH'))
	define('LUNA_EXTERN_MAX_SUBJECT_LENGTH', 30);

// If we're a guest and we've sent a username/pass, we can try to authenticate using those details
if ($luna_user['is_guest'] && isset($_SERVER['PHP_AUTH_USER']))
	authenticate_user($_SERVER['PHP_AUTH_USER'], $_SERVER['PHP_AUTH_PW']);

if ($luna_user['g_read_board'] == '0') {
	http_authenticate_user();
	exit(__('You do not have permission to view this page.', 'luna'));
}

$action = isset($_GET['action']) ? strtolower($_GET['action']) : 'feed';

// Handle a couple old formats, from FluxBB 1.2
switch ($action) {
	case 'active':
		$action = 'feed';
		$_GET['order'] = 'last_comment';
		break;

	case 'new':
		$action = 'feed';
		$_GET['order'] = 'commented';
		break;
}

//
// Sends the proper headers for Basic HTTP Authentication
//
function http_authenticate_user() {
	global $luna_config, $luna_user;

	if (!$luna_user['is_guest'])
		return;

	header('WWW-Authenticate: Basic realm="'.$luna_config['o_board_title'].' External Syndication"');
	header('HTTP/1.0 401 Unauthorized');
}


//
// Output $feed as RSS 2.0
//
function output_rss($feed) {
	global $luna_config;

	// Send XML/no cache headers
	header('Content-Type: application/xml; charset=utf-8');
	header('Expires: '.date('D, d M Y H:i:s').' GMT');
	header('Cache-Control: must-revalidate, post-check=0, pre-check=0');
	header('Pragma: public');

	echo '<?xml version="1.0" encoding="utf-8"?>'."\n";
	echo '<rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">'."\n";
	echo "\t".'<channel>'."\n";
	echo "\t\t".'<atom:link href="'.luna_htmlspecialchars(get_current_url()).'" rel="self" type="application/rss+xml" />'."\n";
	echo "\t\t".'<title><![CDATA['.escape_cdata($feed['title']).']]></title>'."\n";
	echo "\t\t".'<link>'.luna_htmlspecialchars($feed['link']).'</link>'."\n";
	echo "\t\t".'<description><![CDATA['.escape_cdata($feed['description']).']]></description>'."\n";
	echo "\t\t".'<lastBuildDate>'.date('r', count($feed['items']) ? $feed['items'][0]['pubdate'] : time()).'</lastBuildDate>'."\n";
	echo "\t\t".'<generator>Luna '.$luna_config['o_cur_version'].'</generator>'."\n";

	foreach ($feed['items'] as $item) {
		echo "\t\t".'<item>'."\n";
		echo "\t\t\t".'<title><![CDATA['.escape_cdata($item['title']).']]></title>'."\n";
		echo "\t\t\t".'<link>'.luna_htmlspecialchars($item['link']).'</link>'."\n";
		echo "\t\t\t".'<description><![CDATA['.escape_cdata($item['description']).']]></description>'."\n";
		echo "\t\t\t".'<author><![CDATA['.(isset($item['author']['email']) ? escape_cdata($item['author']['email']) : 'dummy@example.com').' ('.escape_cdata($item['author']['name']).')]]></author>'."\n";
		echo "\t\t\t".'<pubDate>'.date('r', $item['pubdate']).'</pubDate>'."\n";
		echo "\t\t\t".'<guid>'.luna_htmlspecialchars($item['link']).'</guid>'."\n";

		echo "\t\t".'</item>'."\n";
	}

	echo "\t".'</channel>'."\n";
	echo '</rss>'."\n";
}


//
// Output $feed as Atom 1.0
//
function output_atom($feed) {
	global $luna_config;

	// Send XML/no cache headers
	header('Content-Type: application/atom+xml; charset=utf-8');
	header('Expires: '.date('D, d M Y H:i:s').' GMT');
	header('Cache-Control: must-revalidate, post-check=0, pre-check=0');
	header('Pragma: public');

	echo '<?xml version="1.0" encoding="utf-8"?>'."\n";
	echo '<feed xmlns="http://www.w3.org/2005/Atom">'."\n";

	echo "\t".'<title type="html"><![CDATA['.escape_cdata($feed['title']).']]></title>'."\n";
	echo "\t".'<link rel="self" href="'.luna_htmlspecialchars(get_current_url()).'"/>'."\n";
	echo "\t".'<link href="'.luna_htmlspecialchars($feed['link']).'"/>'."\n";
	echo "\t".'<updated>'.date('Y-m-d\TH:i:s\Z', count($feed['items']) ? $feed['items'][0]['pubdate'] : time()).'</updated>'."\n";
	echo "\t".'<generator version="'.$luna_config['o_cur_version'].'">Luna</generator>'."\n";

	echo "\t".'<id>'.luna_htmlspecialchars($feed['link']).'</id>'."\n";

	$content_tag = ($feed['type'] == 'comments') ? 'content' : 'summary';

	foreach ($feed['items'] as $item) {
		echo "\t".'<entry>'."\n";
		echo "\t\t".'<title type="html"><![CDATA['.escape_cdata($item['title']).']]></title>'."\n";
		echo "\t\t".'<link rel="alternate" href="'.luna_htmlspecialchars($item['link']).'"/>'."\n";
		echo "\t\t".'<'.$content_tag.' type="html"><![CDATA['.escape_cdata($item['description']).']]></'.$content_tag.'>'."\n";
		echo "\t\t".'<author>'."\n";
		echo "\t\t\t".'<name><![CDATA['.escape_cdata($item['author']['name']).']]></name>'."\n";

		if (isset($item['author']['email']))
			echo "\t\t\t".'<email><![CDATA['.escape_cdata($item['author']['email']).']]></email>'."\n";

		if (isset($item['author']['uri']))
			echo "\t\t\t".'<uri>'.luna_htmlspecialchars($item['author']['uri']).'</uri>'."\n";

		echo "\t\t".'</author>'."\n";
		echo "\t\t".'<updated>'.date('Y-m-d\TH:i:s\Z', $item['pubdate']).'</updated>'."\n";

		echo "\t\t".'<id>'.luna_htmlspecialchars($item['link']).'</id>'."\n";
		echo "\t".'</entry>'."\n";
	}

	echo '</feed>'."\n";
}


//
// Output $feed as XML
//
function output_xml($feed) {
	global $luna_config;

	// Send XML/no cache headers
	header('Content-Type: application/xml; charset=utf-8');
	header('Expires: '.date('D, d M Y H:i:s').' GMT');
	header('Cache-Control: must-revalidate, post-check=0, pre-check=0');
	header('Pragma: public');

	echo '<?xml version="1.0" encoding="utf-8"?>'."\n";
	echo '<source>'."\n";
	echo "\t".'<url>'.luna_htmlspecialchars($feed['link']).'</url>'."\n";

	$forum_tag = ($feed['type'] == 'comments') ? 'comment' : 'thread';

	foreach ($feed['items'] as $item) {
		echo "\t".'<'.$forum_tag.' id="'.$item['id'].'">'."\n";

		echo "\t\t".'<title><![CDATA['.escape_cdata($item['title']).']]></title>'."\n";
		echo "\t\t".'<link>'.luna_htmlspecialchars($item['link']).'</link>'."\n";
		echo "\t\t".'<content><![CDATA['.escape_cdata($item['description']).']]></content>'."\n";
		echo "\t\t".'<author>'."\n";
		echo "\t\t\t".'<name><![CDATA['.escape_cdata($item['author']['name']).']]></name>'."\n";

		if (isset($item['author']['email']))
			echo "\t\t\t".'<email><![CDATA['.escape_cdata($item['author']['email']).']]></email>'."\n";

		if (isset($item['author']['uri']))
			echo "\t\t\t".'<uri>'.luna_htmlspecialchars($item['author']['uri']).'</uri>'."\n";

		echo "\t\t".'</author>'."\n";
		echo "\t\t".'<commented>'.date('r', $item['pubdate']).'</commented>'."\n";

		echo "\t".'</'.$forum_tag.'>'."\n";
	}

	echo '</source>'."\n";
}


//
// Output $feed as HTML (using <li> tags)
//
function output_html($feed) {

	// Send the Content-type header in case the web server is setup to send something else
	header('Content-type: text/html; charset=utf-8');
	header('Expires: '.date('D, d M Y H:i:s').' GMT');
	header('Cache-Control: must-revalidate, post-check=0, pre-check=0');
	header('Pragma: public');

	foreach ($feed['items'] as $item) {
		if (utf8_strlen($item['title']) > LUNA_EXTERN_MAX_SUBJECT_LENGTH)
			$subject_truncated = luna_htmlspecialchars(luna_trim(utf8_substr($item['title'], 0, (LUNA_EXTERN_MAX_SUBJECT_LENGTH - 5)))).' …';
		else
			$subject_truncated = luna_htmlspecialchars($item['title']);

		echo '<li><a href="'.luna_htmlspecialchars($item['link']).'" title="'.luna_htmlspecialchars($item['title']).'">'.$subject_truncated.'</a></li>'."\n";
	}
}

// Show recent discussions
if ($action == 'feed') {
	require LUNA_ROOT.'include/parser.php';

	// Determine what type of feed to output
	$type = isset($_GET['type']) ? strtolower($_GET['type']) : 'html';
	if (!in_array($type, array('html', 'rss', 'atom', 'xml')))
		$type = 'html';

	$show = isset($_GET['show']) ? intval($_GET['show']) : 15;
	if ($show < 1 || $show > 50)
		$show = 15;

	// Was a thread ID supplied?
	if (isset($_GET['tid'])) {
		$tid = intval($_GET['tid']);

		// Fetch thread subject
		$result = $db->query('SELECT t.subject, t.first_comment_id FROM '.$db->prefix.'threads AS t LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=t.forum_id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.read_forum IS NULL OR fp.read_forum=1) AND t.moved_to IS NULL AND t.id='.$tid) or error('Unable to fetch thread info', __FILE__, __LINE__, $db->error());
		if (!$db->num_rows($result)) {
			http_authenticate_user();
			exit(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'));
		}

		$cur_thread = $db->fetch_assoc($result);

		if ($luna_config['o_censoring'] == '1')
			$cur_thread['subject'] = censor_words($cur_thread['subject']);

		// Setup the feed
		$feed = array(
			'title' 		=>	$luna_config['o_board_title'].__(' / ', 'luna').$cur_thread['subject'],
			'link'			=>	get_base_url(true).'/thread.php?id='.$tid,
			'description'		=>	sprintf(__('The most recent comments in %s.', 'luna'), $cur_thread['subject']),
			'items'			=>	array(),
			'type'			=>	'comments'
		);

		// Fetch $show comments
		$result = $db->query('SELECT p.id, p.commenter, p.message, p.hide_smilies, p.commented, p.commenter_id, u.email_setting, u.email, p.commenter_email FROM '.$db->prefix.'comments AS p INNER JOIN '.$db->prefix.'users AS u ON u.id=p.commenter_id WHERE p.thread_id='.$tid.' ORDER BY p.commented DESC LIMIT '.$show) or error('Unable to fetch comment info', __FILE__, __LINE__, $db->error());
		while ($cur_comment = $db->fetch_assoc($result)) {
			$cur_comment['message'] = parse_message($cur_comment['message']);

			$item = array(
				'id'			=>	$cur_comment['id'],
				'title'			=>	$cur_thread['first_comment_id'] == $cur_comment['id'] ? $cur_thread['subject'] : __('Re: ', 'luna').$cur_thread['subject'],
				'link'			=>	get_base_url(true).'/thread.php?pid='.$cur_comment['id'].'#p'.$cur_comment['id'],
				'description'		=>	$cur_comment['message'],
				'author'		=>	array(
					'name'	=> $cur_comment['commenter'],
				),
				'pubdate'		=>	$cur_comment['commented']
			);

			if ($cur_comment['commenter_id'] > 1) {
				if ($cur_comment['email_setting'] == '0' && !$luna_user['is_guest'])
					$item['author']['email'] = $cur_comment['email'];

				$item['author']['uri'] = get_base_url(true).'/profile.php?id='.$cur_comment['commenter_id'];
			} elseif ($cur_comment['commenter_email'] != '' && !$luna_user['is_guest'])
				$item['author']['email'] = $cur_comment['commenter_email'];

			$feed['items'][] = $item;
		}

		$output_func = 'output_'.$type;
		$output_func($feed);
	} else {
		$order_commented = isset($_GET['order']) && strtolower($_GET['order']) == 'commented';
		$forum_name = '';
		$forum_sql = '';

		// Were any forum IDs supplied?
		if (isset($_GET['fid']) && is_scalar($_GET['fid']) && $_GET['fid'] != '') {
			$fids = explode(',', luna_trim($_GET['fid']));
			$fids = array_map('intval', $fids);

			if (!empty($fids))
				$forum_sql .= ' AND t.forum_id IN('.implode(',', $fids).')';

			if (count($fids) == 1) {
				// Fetch forum name
				$result = $db->query('SELECT f.forum_name FROM '.$db->prefix.'forums AS f LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=f.id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.read_forum IS NULL OR fp.read_forum=1) AND f.id='.$fids[0]) or error('Unable to fetch forum name', __FILE__, __LINE__, $db->error());
				if ($db->num_rows($result))
					$forum_name = __(' / ', 'luna').$db->result($result);
			}
		}

		// Any forum IDs to exclude?
		if (isset($_GET['nfid']) && is_scalar($_GET['nfid']) && $_GET['nfid'] != '') {
			$nfids = explode(',', luna_trim($_GET['nfid']));
			$nfids = array_map('intval', $nfids);

			if (!empty($nfids))
				$forum_sql .= ' AND t.forum_id NOT IN('.implode(',', $nfids).')';
		}

		// Only attempt to cache if caching is enabled and we have all or a single forum
		if ($luna_config['o_feed_ttl'] > 0 && ($forum_sql == '' || ($forum_name != '' && !isset($_GET['nfid']))))
			$cache_id = 'feed'.sha1($luna_user['g_id'].'|'.__('en', 'luna').'|'.($order_commented ? '1' : '0').($forum_name == '' ? '' : '|'.$fids[0]));

		// Load cached feed
		if (isset($cache_id) && file_exists(LUNA_CACHE_DIR.'cache_'.$cache_id.'.php'))
			include LUNA_CACHE_DIR.'cache_'.$cache_id.'.php';

		$now = time();
		if (!isset($feed) || $cache_expire < $now) {
			// Setup the feed
			$feed = array(
				'title' 		=>	$luna_config['o_board_title'].$forum_name,
				'link'			=>	'/index.php',
				'description'	=>	sprintf(__('The most recent threads at %s.', 'luna'), $luna_config['o_board_title']),
				'items'			=>	array(),
				'type'			=>	'threads'
			);

			// Fetch $show threads
			$result = $db->query('SELECT t.id, t.commenter, t.subject, t.commented, t.last_comment, t.last_commenter, p.message, p.hide_smilies, u.email_setting, u.email, p.commenter_id, p.commenter_email FROM '.$db->prefix.'threads AS t INNER JOIN '.$db->prefix.'comments AS p ON p.id='.($order_commented ? 't.first_comment_id' : 't.last_comment_id').' INNER JOIN '.$db->prefix.'users AS u ON u.id=p.commenter_id LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=t.forum_id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.read_forum IS NULL OR fp.read_forum=1) AND t.moved_to IS NULL'.$forum_sql.' ORDER BY '.($order_commented ? 't.commented' : 't.last_comment').' DESC LIMIT '.(isset($cache_id) ? 50 : $show)) or error('Unable to fetch thread info', __FILE__, __LINE__, $db->error());
			while ($cur_thread = $db->fetch_assoc($result)) {
				if ($luna_config['o_censoring'] == '1')
					$cur_thread['subject'] = censor_words($cur_thread['subject']);

				$cur_thread['message'] = parse_message($cur_thread['message']);

				$item = array(
					'id'			=>	$cur_thread['id'],
					'title'			=>	$cur_thread['subject'],
					'link'			=>	'/thread.php?id='.$cur_thread['id'].($order_commented ? '' : '&action=new'),
					'description'	=>	$cur_thread['message'],
					'author'		=>	array(
						'name'	=> $order_commented ? $cur_thread['commenter'] : $cur_thread['last_commenter']
					),
					'pubdate'		=>	$order_commented ? $cur_thread['commented'] : $cur_thread['last_comment']
				);

				if ($cur_thread['commenter_id'] > 1) {
					if ($cur_thread['email_setting'] == '0' && !$luna_user['is_guest'])
						$item['author']['email'] = $cur_thread['email'];

					$item['author']['uri'] = '/profile.php?id='.$cur_thread['commenter_id'];
				} elseif ($cur_thread['commenter_email'] != '' && !$luna_user['is_guest'])
					$item['author']['email'] = $cur_thread['commenter_email'];

				$feed['items'][] = $item;
			}

			// Output feed as PHP code
			if (isset($cache_id)) {
				if (!defined('LUNA_CACHE_FUNCTIONS_LOADED'))
					require LUNA_ROOT.'include/cache.php';

				$content = '<?php'."\n\n".'$feed = '.var_export($feed, true).';'."\n\n".'$cache_expire = '.($now + ($luna_config['o_feed_ttl'] * 60)).';'."\n\n".'?>';
				luna_write_cache_file('cache_'.$cache_id.'.php', $content);
			}
		}

		// If we only want to show a few items but due to caching we have too many
		if (count($feed['items']) > $show)
			$feed['items'] = array_slice($feed['items'], 0, $show);

		// Prepend the current base URL onto some links. Done after caching to handle http/https correctly
		$feed['link'] = get_base_url(true).$feed['link'];

		foreach ($feed['items'] as $key => $item) {
			$feed['items'][$key]['link'] = get_base_url(true).$item['link'];

			if (isset($item['author']['uri']))
				$feed['items'][$key]['author']['uri'] = get_base_url(true).$item['author']['uri'];
		}

		$output_func = 'output_'.$type;
		$output_func($feed);
	}

	exit;
}

// Show users online
elseif ($action == 'online' || $action == 'online_full') {
	// Fetch users online info and generate strings for output
	$num_guests = $num_users = 0;
	$users = array();

	$result = $db->query('SELECT user_id, ident FROM '.$db->prefix.'online WHERE idle=0 ORDER BY ident', true) or error('Unable to fetch online list', __FILE__, __LINE__, $db->error());

	while ($luna_user_online = $db->fetch_assoc($result)) {
		if ($luna_user_online['user_id'] > 1) {
			$users[] = ($luna_user['g_view_users'] == '1') ? '<a href="'.luna_htmlspecialchars(get_base_url(true)).'/profile.php?id='.$luna_user_online['user_id'].'">'.luna_htmlspecialchars($luna_user_online['ident']).'</a>' : luna_htmlspecialchars($luna_user_online['ident']);
			++$num_users;
		} else
			++$num_guests;
	}

	// Send the Content-type header in case the web server is setup to send something else
	header('Content-type: text/html; charset=utf-8');
	header('Expires: '.date('D, d M Y H:i:s').' GMT');
	header('Cache-Control: must-revalidate, post-check=0, pre-check=0');
	header('Pragma: public');

	echo sprintf(__('Guests online', 'luna'), forum_number_format($num_guests)).'<br />'."\n";

	if ($action == 'online_full' && !empty($users))
		echo sprintf(__('Users online', 'luna'), implode(', ', $users)).'<br />'."\n";
	else
		echo sprintf(__('Users online', 'luna'), forum_number_format($num_users)).'<br />'."\n";

	exit;
}

// Show board statistics
elseif ($action == 'stats') {
	// Collect some statistics from the database
	if (file_exists(LUNA_CACHE_DIR.'cache_users_info.php'))
		include LUNA_CACHE_DIR.'cache_users_info.php';

	if (!defined('LUNA_USERS_INFO_LOADED')) {
		if (!defined('LUNA_CACHE_FUNCTIONS_LOADED'))
			require LUNA_ROOT.'include/cache.php';

		generate_users_info_cache();
		require LUNA_CACHE_DIR.'cache_users_info.php';
	}

	$result = $db->query('SELECT SUM(num_threads), SUM(num_comments) FROM '.$db->prefix.'forums') or error('Unable to fetch thread/comment count', __FILE__, __LINE__, $db->error());
	list($stats['total_threads'], $stats['total_comments']) = $db->fetch_row($result);

	// Send the Content-type header in case the web server is setup to send something else
	header('Content-type: text/html; charset=utf-8');
	header('Expires: '.date('D, d M Y H:i:s').' GMT');
	header('Cache-Control: must-revalidate, post-check=0, pre-check=0');
	header('Pragma: public');

	echo sprintf(__('Users', 'luna'), forum_number_format($stats['total_users'])).'<br />'."\n";
	echo sprintf(__('Newest user', 'luna'), (($luna_user['g_view_users'] == '1') ? '<a href="'.luna_htmlspecialchars(get_base_url(true)).'/profile.php?id='.$stats['last_user']['id'].'">'.luna_htmlspecialchars($stats['last_user']['username']).'</a>' : luna_htmlspecialchars($stats['last_user']['username']))).'<br />'."\n";
	echo sprintf(__('Threads', 'luna'), forum_number_format($stats['total_threads'])).'<br />'."\n";
	echo sprintf(__('Comments', 'luna'), forum_number_format($stats['total_comments'])).'<br />'."\n";

	exit;
}

// If we end up here, the script was called with some wacky parameters
exit(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'));
