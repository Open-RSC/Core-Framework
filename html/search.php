<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * Licensed under GPLv2 (http://getluna.org/license.php)
 */

// The contents of this file are very much inspired by the file search.php
// from the phpBB Group forum software phpBB2 (http://www.phpbb.com)

define('LUNA_ROOT', dirname(__FILE__).'/');
require LUNA_ROOT.'include/common.php';

$section = isset($_GET['section']) ? $_GET['section'] : null;

if ($luna_user['g_read_board'] == '0')
	message(__('You do not have permission to view this page.', 'luna'), false, '403 Forbidden');
elseif ($luna_user['g_search'] == '0')
	message(__('You do not have permission to use the search feature.', 'luna'), false, '403 Forbidden');

require LUNA_ROOT.'include/search_idx.php';

// Figure out what to do :-)
if (isset($_GET['action']) || isset($_GET['search_id'])) {
	$action = (isset($_GET['action'])) ? $_GET['action'] : null;
	$forums = isset($_GET['forums']) ? (is_array($_GET['forums']) ? $_GET['forums'] : array_filter(explode(',', $_GET['forums']))) : (isset($_GET['forum']) ? array($_GET['forum']) : array());
	$sort_dir = (isset($_GET['sort_dir']) && $_GET['sort_dir'] == 'DESC') ? 'DESC' : 'ASC';

	$forums = array_map('intval', $forums);

	// Allow the old action names for backwards compatibility reasons
	if ($action == 'show_user')
		$action = 'show_user_comments';
	elseif ($action == 'show_24h')
		$action = 'show_recent';

	// If a search_id was supplied
	if (isset($_GET['search_id'])) {
		$search_id = intval($_GET['search_id']);
		if ($search_id < 1)
			message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');
	}
	// If it's a regular search (keywords and/or author)
	elseif ($action == 'search') {
		$keywords = (isset($_GET['keywords'])) ? utf8_strtolower(luna_trim($_GET['keywords'])) : null;
		$author = (isset($_GET['author'])) ? utf8_strtolower(luna_trim($_GET['author'])) : null;

		if (preg_match('%^[\*\%]+$%', $keywords) || (luna_strlen(str_replace(array('*', '%'), '', $keywords)) < LUNA_SEARCH_MIN_WORD && !is_cjk($keywords)))
			$keywords = '';

		if (preg_match('%^[\*\%]+$%', $author) || luna_strlen(str_replace(array('*', '%'), '', $author)) < 2)
			$author = '';

		if (!$keywords && !$author)
			message(__('You have to enter at least one keyword and/or an author to search for.', 'luna'));

		if ($author)
			$author = str_replace('*', '%', $author);

		$show_as = (isset($_GET['show_as']) && $_GET['show_as'] == 'threads') ? 'threads' : 'comments';
		$sort_by = (isset($_GET['sort_by'])) ? intval($_GET['sort_by']) : 0;
		$search_in = (!isset($_GET['search_in']) || $_GET['search_in'] == '0') ? 0 : (($_GET['search_in'] == '1') ? 1 : -1);
	}
	// If it's a user search (by ID)
	elseif ($action == 'show_user_comments' || $action == 'show_user_threads' || $action == 'show_subscriptions') {
		$user_id = (isset($_GET['user_id'])) ? intval($_GET['user_id']) : $luna_user['id'];
		if ($user_id < 2)
			message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

		// Subscribed threads can only be viewed by admins, moderators and the users themselves
		if ($action == 'show_subscriptions' && !$luna_user['is_admmod'] && $user_id != $luna_user['id'])
			message(__('You do not have permission to access this page.', 'luna'), false, '403 Forbidden');
	} elseif ($action == 'show_recent')
		$interval = isset($_GET['value']) ? intval($_GET['value']) : 86400;
	elseif ($action != 'show_new' && $action != 'show_unanswered')
		message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

	// If a valid search_id was supplied we attempt to fetch the search results from the db
	if (isset($search_id)) {
		$ident = ($luna_user['is_guest']) ? get_remote_address() : $luna_user['username'];

		$result = $db->query('SELECT search_data FROM '.$db->prefix.'search_cache WHERE id='.$search_id.' AND ident=\''.$db->escape($ident).'\'') or error('Unable to fetch search results', __FILE__, __LINE__, $db->error());
		if ($row = $db->fetch_assoc($result)) {
			$temp = unserialize($row['search_data']);

			$search_ids = unserialize($temp['search_ids']);
			$num_hits = $temp['num_hits'];
			$sort_by = $temp['sort_by'];
			$sort_dir = $temp['sort_dir'];
			$show_as = $temp['show_as'];
			$search_type = $temp['search_type'];

			unset($temp);
		} else
			message(__('Your search returned no hits.', 'luna'));
	} else {
		$keyword_results = $author_results = array();

		// Search a specific forum?
		$forum_sql = (!empty($forums) || (empty($forums) && $luna_config['o_search_all_forums'] == '0' && !$luna_user['is_admmod'])) ? ' AND t.forum_id IN ('.implode(',', $forums).')' : '';

		if (!empty($author) || !empty($keywords)) {
			// Flood protection
			if ($luna_user['last_search'] && (time() - $luna_user['last_search']) < $luna_user['g_search_flood'] && (time() - $luna_user['last_search']) >= 0)
				message(sprintf(__('At least %s seconds have to pass between searches. Please wait %s seconds and try searching again.', 'luna'), $luna_user['g_search_flood'], $luna_user['g_search_flood'] - (time() - $luna_user['last_search'])));

			if (!$luna_user['is_guest'])
				$db->query('UPDATE '.$db->prefix.'users SET last_search='.time().' WHERE id='.$luna_user['id']) or error('Unable to update user', __FILE__, __LINE__, $db->error());
			else
				$db->query('UPDATE '.$db->prefix.'online SET last_search='.time().' WHERE ident=\''.$db->escape(get_remote_address()).'\'' ) or error('Unable to update user', __FILE__, __LINE__, $db->error());

			switch ($sort_by) {
				case 1:
					$sort_by_sql = ($show_as == 'threads') ? 't.commenter' : 'p.commenter';
					$sort_type = SORT_STRING;
					break;

				case 2:
					$sort_by_sql = 't.subject';
					$sort_type = SORT_STRING;
					break;

				case 3:
					$sort_by_sql = 't.forum_id';
					$sort_type = SORT_NUMERIC;
					break;

				case 4:
					$sort_by_sql = 't.last_comment';
					$sort_type = SORT_NUMERIC;
					break;

				default:
					$sort_by_sql = ($show_as == 'threads') ? 't.last_comment' : 'p.commented';
					$sort_type = SORT_NUMERIC;
					break;
			}

			// If it's a search for keywords
			if ($keywords) {
				// split the keywords into words
				$keywords_array = split_words($keywords, false);

				if (empty($keywords_array))
					message(__('Your search returned no hits.', 'luna'));

				// Should we search in message body or thread subject specifically?
				$search_in_cond = ($search_in) ? (($search_in > 0) ? ' AND m.subject_match = 0' : ' AND m.subject_match = 1') : '';

				$word_count = 0;
				$match_type = 'and';

				$sort_data = array();
				foreach ($keywords_array as $cur_word) {
					switch ($cur_word) {
						case 'and':
						case 'or':
						case 'not':
							$match_type = $cur_word;
							break;

						default: {
							if (is_cjk($cur_word)) {
								$where_cond = str_replace('*', '%', $cur_word);
								$where_cond = ($search_in ? (($search_in > 0) ? 'p.message LIKE \'%'.$db->escape($where_cond).'%\'' : 't.subject LIKE \'%'.$db->escape($where_cond).'%\'') : 'p.message LIKE \'%'.$db->escape($where_cond).'%\' OR t.subject LIKE \'%'.$db->escape($where_cond).'%\'');

								$result = $db->query('SELECT p.id AS comment_id, p.thread_id, '.$sort_by_sql.' AS sort_by FROM '.$db->prefix.'comments AS p INNER JOIN '.$db->prefix.'threads AS t ON t.id=p.thread_id LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=t.forum_id AND fp.group_id='.$luna_user['g_id'].') WHERE ('.$where_cond.') AND (fp.read_forum IS NULL OR fp.read_forum=1)'.$forum_sql, true) or error('Unable to search for comments', __FILE__, __LINE__, $db->error());
							} else
								$result = $db->query('SELECT m.comment_id, p.thread_id, '.$sort_by_sql.' AS sort_by FROM '.$db->prefix.'search_words AS w INNER JOIN '.$db->prefix.'search_matches AS m ON m.word_id = w.id INNER JOIN '.$db->prefix.'comments AS p ON p.id=m.comment_id INNER JOIN '.$db->prefix.'threads AS t ON t.id=p.thread_id LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=t.forum_id AND fp.group_id='.$luna_user['g_id'].') WHERE w.word LIKE \''.$db->escape(str_replace('*', '%', $cur_word)).'\''.$search_in_cond.' AND (fp.read_forum IS NULL OR fp.read_forum=1)'.$forum_sql, true) or error('Unable to search for comments', __FILE__, __LINE__, $db->error());

							$row = array();
							while ($temp = $db->fetch_assoc($result)) {
								$row[$temp['comment_id']] = $temp['thread_id'];

								if (!$word_count) {
									$keyword_results[$temp['comment_id']] = $temp['thread_id'];
									$sort_data[$temp['comment_id']] = $temp['sort_by'];
								} elseif ($match_type == 'or') {
									$keyword_results[$temp['comment_id']] = $temp['thread_id'];
									$sort_data[$temp['comment_id']] = $temp['sort_by'];
								} elseif ($match_type == 'not') {
									unset($keyword_results[$temp['comment_id']]);
									unset($sort_data[$temp['comment_id']]);
								}
							}

							if ($match_type == 'and' && $word_count) {
								foreach ($keyword_results as $comment_id => $thread_id) {
									if (!isset($row[$comment_id])) {
										unset($keyword_results[$comment_id]);
										unset($sort_data[$comment_id]);
									}
								}
							}

							++$word_count;
							$db->free_result($result);

							break;
						}
					}
				}

				// Sort the results - annoyingly array_multisort re-indexes arrays with numeric keys, so we need to split the keys out into a separate array then combine them again after
				$comment_ids = array_keys($keyword_results);
				$thread_ids = array_values($keyword_results);

				array_multisort(array_values($sort_data), $sort_dir == 'DESC' ? SORT_DESC : SORT_ASC, $sort_type, $comment_ids, $thread_ids);

				// combine the arrays back into a key=>value array (array_combine is PHP5 only unfortunately)
				$num_results = count($keyword_results);
				$keyword_results = array();
				for ($i = 0;$i < $num_results;$i++)
					$keyword_results[$comment_ids[$i]] = $thread_ids[$i];

				unset($sort_data, $comment_ids, $thread_ids);
			}

			// If it's a search for author name (and that author name isn't Guest)
			if ($author && $author != 'guest' && $author != utf8_strtolower(__('Guest', 'luna'))) {
				switch ($db_type) {
					case 'pgsql':
						$result = $db->query('SELECT id FROM '.$db->prefix.'users WHERE username ILIKE \''.$db->escape($author).'\'') or error('Unable to fetch users', __FILE__, __LINE__, $db->error());
						break;

					default:
						$result = $db->query('SELECT id FROM '.$db->prefix.'users WHERE username LIKE \''.$db->escape($author).'\'') or error('Unable to fetch users', __FILE__, __LINE__, $db->error());
						break;
				}

				if ($db->num_rows($result)) {
					$user_ids = array();
					while ($row = $db->fetch_row($result))
						$user_ids[] = $row[0];

					$result = $db->query('SELECT p.id AS comment_id, p.thread_id FROM '.$db->prefix.'comments AS p INNER JOIN '.$db->prefix.'threads AS t ON t.id=p.thread_id LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=t.forum_id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.read_forum IS NULL OR fp.read_forum=1) AND p.commenter_id IN('.implode(',', $user_ids).')'.$forum_sql.' ORDER BY '.$sort_by_sql.' '.$sort_dir) or error('Unable to fetch matched comments list', __FILE__, __LINE__, $db->error());
					while ($temp = $db->fetch_assoc($result))
						$author_results[$temp['comment_id']] = $temp['thread_id'];

					$db->free_result($result);
				}
			}

			// If we searched for both keywords and author name we want the intersection between the results
			if ($author && $keywords) {
				$search_ids = array_intersect_assoc($keyword_results, $author_results);
				$search_type = array('both', array($keywords, luna_trim($_GET['author'])), implode(',', $forums), $search_in);
			} elseif ($keywords) {
				$search_ids = $keyword_results;
				$search_type = array('keywords', $keywords, implode(',', $forums), $search_in);
			} else {
				$search_ids = $author_results;
				$search_type = array('author', luna_trim($_GET['author']), implode(',', $forums), $search_in);
			}

			unset($keyword_results, $author_results);

			if ($show_as == 'threads')
				$search_ids = array_values($search_ids);
			else
				$search_ids = array_keys($search_ids);

			$search_ids = array_unique($search_ids);

			$num_hits = count($search_ids);
			if (!$num_hits)
				message(__('Your search returned no hits.', 'luna'));
		} elseif ($action == 'show_new' || $action == 'show_recent' || $action == 'show_user_comments' || $action == 'show_user_threads' || $action == 'show_subscriptions' || $action == 'show_unanswered') {
			$search_type = array('action', $action);
			$show_as = 'threads';
			// We want to sort things after last comment
			$sort_by = 0;
			$sort_dir = 'DESC';

			// If it's a search for new comments since last visit
			if ($action == 'show_new') {
				if ($luna_user['is_guest'])
					message(__('You do not have permission to access this page.', 'luna'), false, '403 Forbidden');

				$result = $db->query('SELECT t.id FROM '.$db->prefix.'threads AS t LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=t.forum_id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.read_forum IS NULL OR fp.read_forum=1) AND t.last_comment>'.$luna_user['last_visit'].' AND t.moved_to IS NULL'.(isset($_GET['fid']) ? ' AND t.forum_id='.intval($_GET['fid']) : '').' ORDER BY t.last_comment DESC') or error('Unable to fetch thread list', __FILE__, __LINE__, $db->error());
				$num_hits = $db->num_rows($result);

				if (!$num_hits)
					message(__('There are no threads with new comments since your last visit.', 'luna'));
			}
			// If it's a search for recent comments (in a certain time interval)
			elseif ($action == 'show_recent') {
				$result = $db->query('SELECT t.id FROM '.$db->prefix.'threads AS t LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=t.forum_id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.read_forum IS NULL OR fp.read_forum=1) AND t.last_comment>'.(time() - $interval).' AND t.moved_to IS NULL'.(isset($_GET['fid']) ? ' AND t.forum_id='.intval($_GET['fid']) : '').' ORDER BY t.last_comment DESC') or error('Unable to fetch thread list', __FILE__, __LINE__, $db->error());
				$num_hits = $db->num_rows($result);

				if (!$num_hits)
					message(__('No new comments have been made within the last 24 hours.', 'luna'));
			}
			// If it's a search for comments by a specific user ID
			elseif ($action == 'show_user_comments') {
				$show_as = 'comments';

				$result = $db->query('SELECT p.id FROM '.$db->prefix.'comments AS p INNER JOIN '.$db->prefix.'threads AS t ON p.thread_id=t.id LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=t.forum_id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.read_forum IS NULL OR fp.read_forum=1) AND p.commenter_id='.$user_id.' ORDER BY p.commented DESC') or error('Unable to fetch user comments', __FILE__, __LINE__, $db->error());
				$num_hits = $db->num_rows($result);

				if (!$num_hits)
					message(__('There are no comments by this user in this forum.', 'luna'));

				// Pass on the user ID so that we can later know whose comments we're searching for
				$search_type[2] = $user_id;
			}
			// If it's a search for threads by a specific user ID
			elseif ($action == 'show_user_threads') {
				$result = $db->query('SELECT t.id FROM '.$db->prefix.'threads AS t INNER JOIN '.$db->prefix.'comments AS p ON t.first_comment_id=p.id LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=t.forum_id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.read_forum IS NULL OR fp.read_forum=1) AND p.commenter_id='.$user_id.' ORDER BY t.last_comment DESC') or error('Unable to fetch user threads', __FILE__, __LINE__, $db->error());
				$num_hits = $db->num_rows($result);

				if (!$num_hits)
					message(__('There are no threads by this user in this forum.', 'luna'));

				// Pass on the user ID so that we can later know whose threads we're searching for
				$search_type[2] = $user_id;
			}
			// If it's a search for subscribed threads
			elseif ($action == 'show_subscriptions') {
				if ($luna_user['is_guest'])
					message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

				$result = $db->query('SELECT t.id FROM '.$db->prefix.'threads AS t INNER JOIN '.$db->prefix.'thread_subscriptions AS s ON (t.id=s.thread_id AND s.user_id='.$user_id.') LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=t.forum_id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.read_forum IS NULL OR fp.read_forum=1) ORDER BY t.last_comment DESC') or error('Unable to fetch thread list', __FILE__, __LINE__, $db->error());
				$num_hits = $db->num_rows($result);

				if (!$num_hits)
					message(__('This user is currently not subscribed to any threads.', 'luna'));

				// Pass on user ID so that we can later know whose subscriptions we're searching for
				$search_type[2] = $user_id;
			}
			// If it's a search for unanswered comments
			else {
				$result = $db->query('SELECT t.id FROM '.$db->prefix.'threads AS t LEFT JOIN '.$db->prefix.'forum_perms AS fp ON (fp.forum_id=t.forum_id AND fp.group_id='.$luna_user['g_id'].') WHERE (fp.read_forum IS NULL OR fp.read_forum=1) AND t.num_replies=0 AND t.moved_to IS NULL ORDER BY t.last_comment DESC') or error('Unable to fetch thread list', __FILE__, __LINE__, $db->error());
				$num_hits = $db->num_rows($result);

				if (!$num_hits)
					message(__('There are no unanswered comments in this forum.', 'luna'));
			}

			$search_ids = array();
			while ($row = $db->fetch_row($result))
				$search_ids[] = $row[0];

			$db->free_result($result);
		} else
			message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');


		// Prune "old" search results
		$old_searches = array();
		$result = $db->query('SELECT ident FROM '.$db->prefix.'online') or error('Unable to fetch online list', __FILE__, __LINE__, $db->error());

		if ($db->num_rows($result)) {
			while ($row = $db->fetch_row($result))
				$old_searches[] = '\''.$db->escape($row[0]).'\'';

			$db->query('DELETE FROM '.$db->prefix.'search_cache WHERE ident NOT IN('.implode(',', $old_searches).')') or error('Unable to delete search results', __FILE__, __LINE__, $db->error());
		}

		// Fill an array with our results and search properties
		$temp = serialize(array(
			'search_ids'		=> serialize($search_ids),
			'num_hits'			=> $num_hits,
			'sort_by'			=> $sort_by,
			'sort_dir'			=> $sort_dir,
			'show_as'			=> $show_as,
			'search_type'		=> $search_type
		));
		$search_id = mt_rand(1, 2147483647);

		$ident = ($luna_user['is_guest']) ? get_remote_address() : $luna_user['username'];

		$db->query('INSERT INTO '.$db->prefix.'search_cache (id, ident, search_data) VALUES('.$search_id.', \''.$db->escape($ident).'\', \''.$db->escape($temp).'\')') or error('Unable to insert search results', __FILE__, __LINE__, $db->error());

		if ($search_type[0] != 'action') {
			$db->end_transaction();
			$db->close();

			// Redirect the user to the cached result page
			header('Location: search.php?search_id='.$search_id);
			exit;
		}
	}

	$forum_actions = array();

	// If we're on the new comments search, display a "mark all as read" link
	if (!$luna_user['is_guest'] && $search_type[0] == 'action' && $search_type[1] == 'show_new')
		$forum_actions[] = '<a href="misc.php?action=markread&amp;csrf_token='.luna_csrf_token().'">'.__('Mark as read', 'luna').'</a>';

	// Fetch results to display
	if (!empty($search_ids)) {
		switch ($sort_by) {
			case 1:
				$sort_by_sql = ($show_as == 'threads') ? 't.commenter' : 'p.commenter';
				break;

			case 2:
				$sort_by_sql = 't.subject';
				break;

			case 3:
				$sort_by_sql = 't.forum_id';
				break;

			default:
				$sort_by_sql = ($show_as == 'threads') ? 't.last_comment' : 'p.commented';
				break;
		}

		// Determine the thread or comment offset (based on $_GET['p'])
		$per_page = ($show_as == 'comments') ? $luna_user['disp_comments'] : $luna_user['disp_threads'];
		$num_pages = ceil($num_hits / $per_page);

		$p = (!isset($_GET['p']) || $_GET['p'] <= 1 || $_GET['p'] > $num_pages) ? 1 : intval($_GET['p']);
		$start_from = $per_page * ($p - 1);

		// Generate paging links
		$paging_links = forum_paginate($num_pages, $p, 'search.php?search_id='.$search_id);

		// throw away the first $start_from of $search_ids, only keep the top $per_page of $search_ids
		$search_ids = array_slice($search_ids, $start_from, $per_page);
        
        if (!$luna_user['g_soft_delete_view'])
            $sql_soft = 't.soft = 0 AND ';
        else
            $sql_soft = '';

		// Run the query and fetch the results
		if ($show_as == 'comments')
			$result = $db->query('SELECT p.id AS pid, p.commenter AS pcommenter, p.commented AS pcommented, p.commenter_id, p.message, p.hide_smilies, t.id AS tid, t.commenter, t.subject, t.first_comment_id, t.last_comment, t.last_comment_id, t.last_commenter, t.last_commenter_id, t.num_replies, t.forum_id, t.pinned, t.closed, t.solved, t.important, f.forum_name FROM '.$db->prefix.'comments AS p INNER JOIN '.$db->prefix.'threads AS t ON t.id=p.thread_id INNER JOIN '.$db->prefix.'forums AS f ON f.id=t.forum_id WHERE '.$sql_soft.'p.id IN('.implode(',', $search_ids).') ORDER BY '.$sort_by_sql.' '.$sort_dir) or error('Unable to fetch search results', __FILE__, __LINE__, $db->error());
		else
			$result = $db->query('SELECT t.id AS tid, t.commenter, t.subject, t.last_comment, t.last_comment_id, t.last_commenter, t.last_commenter_id, t.num_replies, t.closed, t.solved, t.important, t.pinned, t.forum_id, t.pinned, t.closed, f.forum_name FROM '.$db->prefix.'threads AS t INNER JOIN '.$db->prefix.'forums AS f ON f.id=t.forum_id WHERE '.$sql_soft.'t.id IN('.implode(',', $search_ids).') ORDER BY '.$sort_by_sql.' '.$sort_dir) or error('Unable to fetch search results', __FILE__, __LINE__, $db->error());

		$search_set = array();
		while ($row = $db->fetch_assoc($result))
			$search_set[] = $row;

		$crumbs_text = array();
		$crumbs_text['show_as'] = __('Search', 'luna');

		if ($search_type[0] == 'action') {
			if ($search_type[1] == 'show_user_threads')
				$crumbs_text['search_type'] = '<a class="btn btn-primary" href="search.php?action=show_user_threads&amp;user_id='.$search_type[2].'">'.sprintf(__('Threads by %s', 'luna'), luna_htmlspecialchars($search_set[0]['commenter'])).'</a>';
			elseif ($search_type[1] == 'show_user_comments')
				$crumbs_text['search_type'] = '<a class="btn btn-primary" href="search.php?action=show_user_comments&amp;user_id='.$search_type[2].'">'.sprintf(__('Comments by %s', 'luna'), luna_htmlspecialchars($search_set[0]['pcommenter'])).'</a>';
			elseif ($search_type[1] == 'show_subscriptions') {
				// Fetch username of subscriber
				$subscriber_id = $search_type[2];
				$result = $db->query('SELECT username FROM '.$db->prefix.'users WHERE id='.$subscriber_id) or error('Unable to fetch username of subscriber', __FILE__, __LINE__, $db->error());

				if ($db->num_rows($result))
					$subscriber_name = $db->result($result);
				else
					message(__('Bad request. The link you followed is incorrect, outdated or you are simply not allowed to hang around here.', 'luna'), false, '404 Not Found');

				$crumbs_text['search_type'] = '<a class="btn btn-primary" href="search.php?action=show_subscriptions&amp;user_id='.$subscriber_id.'">'.sprintf(__('Subscribed by %s', 'luna'), luna_htmlspecialchars($subscriber_name)).'</a>';
			} else
				$crumbs_text['search_type'] = '<a class="btn btn-primary" href="search.php?action='.$search_type[1].'">'.sprintf(__('Quick search %s', 'luna'), $search_type[1]).'</a>';
		} else {
			$keywords = $author = '';

			if ($search_type[0] == 'both') {
				list ($keywords, $author) = $search_type[1];
				$crumbs_text['search_type'] = sprintf(sprintf('By both show as %s', $show_as), luna_htmlspecialchars($keywords), luna_htmlspecialchars($author));
			} elseif ($search_type[0] == 'keywords') {
				$keywords = $search_type[1];
				$crumbs_text['search_type'] = sprintf(sprintf('By keywords show as %s', $show_as), luna_htmlspecialchars($keywords));
			} elseif ($search_type[0] == 'author') {
				$author = $search_type[1];
				$crumbs_text['search_type'] = sprintf(sprintf('By user show as %s', $show_as), luna_htmlspecialchars($author));
			}

			$crumbs_text['search_type'] = '<a class="btn btn-primary" href="search.php?action=search&amp;keywords='.urlencode($keywords).'&amp;author='.urlencode($author).'&amp;forums='.$search_type[2].'&amp;search_in='.$search_type[3].'&amp;sort_by='.$sort_by.'&amp;sort_dir='.$sort_dir.'&amp;show_as='.$show_as.'">'.$crumbs_text['search_type'].'</a>';
		}

		$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), __('Search results', 'luna'));
		define('LUNA_ACTIVE_PAGE', 'search');

		if ($show_as == 'threads') {
			$thread_count = 0;
		} elseif ($show_as == 'comments') {
			require LUNA_ROOT.'include/parser.php';

			$comment_count = 0;
		}

		// Get thread/forum tracking data
		if (!$luna_user['is_guest'])
			$tracked_threads = get_tracked_threads();

		require load_page('header.php');
		require load_page('search-results.php');
		require load_page('footer.php');
	} else
		message(__('Your search returned no hits.', 'luna'));
} else
	$search_id = '';

if (!$search_id) {
	if (!$section || $section == 'simple') {
		$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), __('Search', 'luna'));
		$focus_element = array('search', 'keywords');
		define('LUNA_ACTIVE_PAGE', 'search');
		require load_page('header.php');

		require load_page('search.php');

		require load_page('footer.php');
	} else {
		if ($luna_config['o_enable_advanced_search'] == 0) {
			message(__('You do not have permission to access this page.', 'luna'), false, '403 Forbidden');
		} else {
			$page_title = array(luna_htmlspecialchars($luna_config['o_board_title']), __('Search', 'luna'));
			$focus_element = array('search', 'keywords');
			define('LUNA_ACTIVE_PAGE', 'search');
			require load_page('header.php');

			require load_page('search-advanced.php');

			require load_page('footer.php');
		}
	}
}
