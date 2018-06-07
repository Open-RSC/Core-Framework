<?php

/*
 * Copyright (C) 2013-2016 Luna
 * Based on code by FluxBB copyright (C) 2008-2012 FluxBB
 * Based on code by Rickard Andersson copyright (C) 2002-2008 PunBB
 * License: http://opensource.org/licenses/MIT MIT
 */

// Make sure no one attempts to run this script "directly"
if (!defined('FORUM'))
	exit;

// Global variables
/* regular expression to match nested BBCode LIST tags
'%
\[list				# match opening bracket and tag name of outermost LIST tag
(?:=([1*]))?+		 # optional attribute capture in group 1
\]					# closing bracket of outermost opening LIST tag
(					 # capture contents of LIST tag in group 2
  (?:				 # non capture group for either contents or whole nested LIST
	[^\[]*+		   # unroll the loop! consume everything up to next [ (normal *)
	(?:			   # (See "Mastering Regular Expressions" chapter 6 for details)
	  (?!			 # negative lookahead ensures we are NOT on [LIST*] or [/LIST]
		\[list		# opening LIST tag
		(?:=[1*])?+   # with optional attribute
		\]			# closing bracket of opening LIST tag
		|			 # or...
		\[/list\]	 # a closing LIST tag
	  )			   # end negative lookahead assertion (we are not on a LIST tag)
	  \[			  # match the [ which is NOT the start of LIST tag (special)
	  [^\[]*+		 # consume everything up to next [ (normal *)
	)*+			   # finish up "unrolling the loop" technique (special (normal*))*
  |				   # or...
	(?R)			  # recursively match a whole nested LIST element
  )*				  # as many times as necessary until deepest nested LIST tag grabbed
)					 # end capturing contents of LIST tag into group 2
\[/list\]			 # match outermost closing LIST tag
%iex' */
$re_list = '%\[list(?:=([1*]))?+\]((?:[^\[]*+(?:(?!\[list(?:=[1*])?+\]|\[/list\])\[[^\[]*+)*+|(?R))*)\[/list\]%i';

//
// Make sure all BBCodes are lower case and do a little cleanup
//
function preparse_bbcode($text, &$errors, $is_signature = false) {
	global $luna_config, $re_list;

	// Remove empty tags
	while (($new_text = strip_empty_bbcode($text)) !== false) {
		if ($new_text != $text) {
			$text = $new_text;
			if ($new_text == '') {
				$errors[] = __('It seems your comment consisted of empty BBCodes only. It is possible that this happened because e.g. the innermost quote was discarded because of the maximum quote depth level.', 'luna');
				return '';
			}
		} else
			break;
	}

	if ($is_signature) {
		if (preg_match('%\[/?(?:quote|code|video|list|h|spoiler)\b[^\]]*\]%i', $text))
			$errors[] = __('The quote, code, list, video, spoiler and heading BBCodes are not allowed in signatures.', 'luna');
	}

	// If the message contains a code tag we have to split it up (text within [code][/code] shouldn't be touched)
	if (strpos($text, '[code]') !== false && strpos($text, '[/code]') !== false)
		list($inside, $text) = extract_blocks($text, '[code]', '[/code]');

	// Tidy up lists
	$temp = preg_replace_callback($re_list, create_function('$matches', 'return preparse_list_tag($matches[2], $matches[1]);'), $text);

	// If the regex failed
	if (is_null($temp))
		$errors[] = __('Your list was too long to parse, please make it smaller!', 'luna');
	else
		$text = str_replace('*'."\0".']', '*]', $temp);

	if ($luna_config['o_make_links'] == '1')
		$text = do_clickable($text);

	$temp_text = false;
	if (empty($errors))
		$temp_text = preparse_tags($text, $errors, $is_signature);

	if ($temp_text !== false)
		$text = $temp_text;

	// If we split up the message before we have to concatenate it together again (code tags)
	if (isset($inside)) {
		$outside = explode("\1", $text);
		$text = '';

		$num_tokens = count($outside);
		for ($i = 0; $i < $num_tokens; ++$i) {
			$text .= $outside[$i];
			if (isset($inside[$i]))
				$text .= '[code]'.$inside[$i].'[/code]';
		}

		unset($inside);
	}

	// Remove empty tags
	while (($new_text = strip_empty_bbcode($text)) !== false) {
		if ($new_text != $text) {
			$text = $new_text;
			if ($new_text == '') {
				$errors[] = __('It seems your comment consisted of empty BBCodes only. It is possible that this happened because e.g. the innermost quote was discarded because of the maximum quote depth level.', 'luna');
				break;
			}
		} else
			break;
	}

	return luna_trim($text);
}


//
// Strip empty bbcode tags from some text
//
function strip_empty_bbcode($text) {
	// If the message contains a code tag we have to split it up (empty tags within [code][/code] are fine)
	if (strpos($text, '[code]') !== false && strpos($text, '[/code]') !== false)
		list($inside, $text) = extract_blocks($text, '[code]', '[/code]');

	// Remove empty tags
	while (!is_null($new_text = preg_replace('%\[(b|u|s|ins|i|h|color|size|center|quote|c|img|url|email|list|sup|sub|video|spoiler)(?:\=[^\]]*)?\]\s*\[/\1\]%', '', $text))) {
		if ($new_text != $text)
			$text = $new_text;
		else
			break;
	}

	// If we split up the message before we have to concatenate it together again (code tags)
	if (isset($inside)) {
		$parts = explode("\1", $text);
		$text = '';
		foreach ($parts as $i => $part) {
			$text .= $part;
			if (isset($inside[$i]))
				$text .= '[code]'.$inside[$i].'[/code]';
		}
	}

	// Remove empty code tags
	while (!is_null($new_text = preg_replace('%\[(code)\]\s*\[/\1\]%', '', $text))) {
		if ($new_text != $text)
			$text = $new_text;
		else
			break;
	}

	return $text;
}


//
// Check the structure of bbcode tags and fix simple mistakes where possible
//
function preparse_tags($text, &$errors, $is_signature = false) {
	global $luna_config;

	// Start off by making some arrays of bbcode tags and what we need to do with each one

	// List of all the tags
	$tags = array('quote', 'code', 'c', 'b', 'i', 'u', 's', 'ins', 'size', 'center', 'color', 'url', 'email', 'img', 'list', '*', 'h', 'sup', 'sub', 'video', 'spoiler');
	// List of tags that we need to check are open (You could not put b, i, u in here then illegal nesting like [b][i][/b][/i] would be allowed)
	$tags_opened = $tags;
	// and tags we need to check are closed (the same as above, added it just in case)
	$tags_closed = $tags;
	// Tags we can nest and the depth they can be nested to
	$tags_nested = array('quote' => $luna_config['o_quote_depth'], 'list' => 5, '*' => 5, spoiler => '5');
	// Tags to ignore the contents of completely (just code)
	$tags_ignore = array('code', 'c');
	// Tags not allowed
	$tags_forbidden = array();
	// Block tags, block tags can only go within another block tag, they cannot be in a normal tag
	$tags_block = array('quote', 'code', 'list', 'h', '*', 'spoiler');
	// Inline tags, we do not allow new lines in these
	$tags_inline = array('b', 'i', 'u', 's', 'c', 'ins', 'color', 'sup', 'sub');
	// Tags we trim interior space
	$tags_trim = array('img', 'video');
	// Tags we remove quotes from the argument
	$tags_quotes = array('url', 'email', 'img', 'video');
	// Tags we limit bbcode in
	$tags_limit_bbcode = array(
		'*' 	=> array('b', 'i', 'u', 's', 'c', 'ins', 'color', 'url', 'email', 'list', 'sup', 'sub'),
		'list' 	=> array('*'),
		'url' 	=> array('img'),
		'email' => array('img'),
		'img' 	=> array(),
		'h'		=> array('b', 'i', 'u', 's', 'c', 'ins', 'color', 'sub', 'sup', 'url', 'email'),
		'video'	=> array('url'),
		'center'=> array('b', 'i', 'u', 's', 'c', 'ins', 'color', 'sub', 'sup', 'url', 'email', 'img'),
		'size'	=> array('b', 'i', 'u', 's', 'c', 'ins', 'color', 'sub', 'sup', 'url', 'email')
	);
	// Tags we can automatically fix bad nesting
	$tags_fix = array('quote', 'b', 'i', 'u', 's', 'ins', 'sub', 'sup', 'color', 'url', 'email', 'h');

	$split_text = preg_split('%(\[[\*a-zA-Z0-9-/]*?(?:=.*?)?\])%', $text, -1, PREG_SPLIT_DELIM_CAPTURE|PREG_SPLIT_NO_EMPTY);

	$open_tags = array('fluxbb-bbcode');
	$open_args = array('');
	$opened_tag = 0;
	$new_text = '';
	$current_ignore = '';
	$current_nest = '';
	$current_depth = array();
	$limit_bbcode = $tags;
	$count_ignored = array();

	foreach ($split_text as $current) {
		if ($current == '')
			continue;

		// Are we dealing with a tag?
		if (substr($current, 0, 1) != '[' || substr($current, -1, 1) != ']') {
			// It's not a bbcode tag so we put it on the end and continue
			// If we are nested too deeply don't add to the end
			if ($current_nest)
				continue;

			$current = str_replace("\r\n", "\n", $current);
			$current = str_replace("\r", "\n", $current);
			if (in_array($open_tags[$opened_tag], $tags_inline) && strpos($current, "\n") !== false) {
				// Deal with new lines
				$split_current = preg_split('%(\n\n+)%', $current, -1, PREG_SPLIT_DELIM_CAPTURE|PREG_SPLIT_NO_EMPTY);
				$current = '';

				if (!luna_trim($split_current[0], "\n")) // The first part is a linebreak so we need to handle any open tags first
					array_unshift($split_current, '');

				for ($i = 1; $i < count($split_current); $i += 2) {
					$temp_opened = array();
					$temp_opened_arg = array();
					$temp = $split_current[$i - 1];
					while (!empty($open_tags)) {
						$temp_tag = array_pop($open_tags);
						$temp_arg = array_pop($open_args);

						if (in_array($temp_tag , $tags_inline)) {
							array_push($temp_opened, $temp_tag);
							array_push($temp_opened_arg, $temp_arg);
							$temp .= '[/'.$temp_tag.']';
						} else {
							array_push($open_tags, $temp_tag);
							array_push($open_args, $temp_arg);
							break;
						}
					}
					$current .= $temp.$split_current[$i];
					$temp = '';
					while (!empty($temp_opened)) {
						$temp_tag = array_pop($temp_opened);
						$temp_arg = array_pop($temp_opened_arg);
						if (empty($temp_arg))
							$temp .= '['.$temp_tag.']';
						else
							$temp .= '['.$temp_tag.'='.$temp_arg.']';
						array_push($open_tags, $temp_tag);
						array_push($open_args, $temp_arg);
					}
					$current .= $temp;
				}

				if (array_key_exists($i - 1, $split_current))
					$current .= $split_current[$i - 1];
			}

			if (in_array($open_tags[$opened_tag], $tags_trim))
				$new_text .= luna_trim($current);
			else
				$new_text .= $current;

			continue;
		}

		// Get the name of the tag
		$current_arg = '';
		if (strpos($current, '/') === 1) {
			$current_tag = substr($current, 2, -1);
		} elseif (strpos($current, '=') === false) {
			$current_tag = substr($current, 1, -1);
		} else {
			$current_tag = substr($current, 1, strpos($current, '=')-1);
			$current_arg = substr($current, strpos($current, '=')+1, -1);
		}
		$current_tag = strtolower($current_tag);

		// Is the tag defined?
		if (!in_array($current_tag, $tags)) {
			// It's not a bbcode tag so we put it on the end and continue
			if (!$current_nest)
				$new_text .= $current;

			continue;
		}

		// We definitely have a bbcode tag

		// Make the tag string lower case
		if ($equalpos = strpos($current,'=')) {
			// We have an argument for the tag which we don't want to make lowercase
			if (strlen(substr($current, $equalpos)) == 2) {
				// Empty tag argument
				$errors[] = sprintf(__('[%s] tag had an empty attribute section', 'luna'), $current_tag);
				return false;
			}
			$current = strtolower(substr($current, 0, $equalpos)).substr($current, $equalpos);
		} else
			$current = strtolower($current);

		// This is if we are currently in a tag which escapes other bbcode such as code
		// We keep a count of ignored bbcodes (code tags) so we can nest them, but
		// only balanced sets of tags can be nested
		if ($current_ignore) {
			// Increase the current ignored tags counter
			if ('['.$current_ignore.']' == $current)
				$count_ignored[$current_tag]++;

			// Decrease the current ignored tags counter
			if ('[/'.$current_ignore.']' == $current)
				$count_ignored[$current_tag]--;

			if ('[/'.$current_ignore.']' == $current && $count_ignored[$current_tag] == 0) {
				// We've finished the ignored section
				$current = '[/'.$current_tag.']';
				$current_ignore = '';
				$count_ignored = array();
			}

			$new_text .= $current;

			continue;
		}

		if ($current_nest) {
			// We are currently too deeply nested so lets see if we are closing the tag or not
			if ($current_tag != $current_nest)
				continue;

			if (substr($current, 1, 1) == '/')
				$current_depth[$current_nest]--;
			else
				$current_depth[$current_nest]++;

			if ($current_depth[$current_nest] <= $tags_nested[$current_nest])
				$current_nest = '';

			continue;
		}

		// Check the current tag is allowed here
		if (!in_array($current_tag, $limit_bbcode) && $current_tag != $open_tags[$opened_tag]) {
			$errors[] = sprintf(__('[%1$s] was opened within [%2$s], this is not allowed', 'luna'), $current_tag, $open_tags[$opened_tag]);
			return false;
		}

		if (substr($current, 1, 1) == '/') {
			// This is if we are closing a tag
			if ($opened_tag == 0 || !in_array($current_tag, $open_tags)) {
				// We tried to close a tag which is not open
				if (in_array($current_tag, $tags_opened)) {
					$errors[] = sprintf(__('[/%1$s] was found without a matching [%1$s]', 'luna'), $current_tag);
					return false;
				}
			} else {
				// Check nesting
				while (true) {
					// Nesting is ok
					if ($open_tags[$opened_tag] == $current_tag) {
						array_pop($open_tags);
						array_pop($open_args);
						$opened_tag--;
						break;
					}

					// Nesting isn't ok, try to fix it
					if (in_array($open_tags[$opened_tag], $tags_closed) && in_array($current_tag, $tags_closed)) {
						if (in_array($current_tag, $open_tags)) {
							$temp_opened = array();
							$temp_opened_arg = array();
							$temp = '';
							while (!empty($open_tags)) {
								$temp_tag = array_pop($open_tags);
								$temp_arg = array_pop($open_args);

								if (!in_array($temp_tag, $tags_fix)) {
									// We couldn't fix nesting
									$errors[] = sprintf(__('[%1$s] was found without a matching [/%1$s]', 'luna'), $temp_tag);
									return false;
								}
								array_push($temp_opened, $temp_tag);
								array_push($temp_opened_arg, $temp_arg);

								if ($temp_tag == $current_tag)
									break;
								else
									$temp .= '[/'.$temp_tag.']';
							}
							$current = $temp.$current;
							$temp = '';
							array_pop($temp_opened);
							array_pop($temp_opened_arg);

							while (!empty($temp_opened)) {
								$temp_tag = array_pop($temp_opened);
								$temp_arg = array_pop($temp_opened_arg);
								if (empty($temp_arg))
									$temp .= '['.$temp_tag.']';
								else
									$temp .= '['.$temp_tag.'='.$temp_arg.']';
								array_push($open_tags, $temp_tag);
								array_push($open_args, $temp_arg);
							}
							$current .= $temp;
							$opened_tag--;
							break;
						} else {
							// We couldn't fix nesting
							$errors[] = sprintf(__('[/%1$s] was found without a matching [%1$s]', 'luna'), $current_tag);
							return false;
						}
					} elseif (in_array($open_tags[$opened_tag], $tags_closed))
						break;
					else {
						array_pop($open_tags);
						array_pop($open_args);
						$opened_tag--;
					}
				}
			}

			if (in_array($current_tag, array_keys($tags_nested))) {
				if (isset($current_depth[$current_tag]))
					$current_depth[$current_tag]--;
			}

			if (in_array($open_tags[$opened_tag], array_keys($tags_limit_bbcode)))
				$limit_bbcode = $tags_limit_bbcode[$open_tags[$opened_tag]];
			else
				$limit_bbcode = $tags;

			$new_text .= $current;

			continue;
		} else {
			// We are opening a tag
			if (in_array($current_tag, array_keys($tags_limit_bbcode)))
				$limit_bbcode = $tags_limit_bbcode[$current_tag];
			else
				$limit_bbcode = $tags;

			if (in_array($current_tag, $tags_block) && !in_array($open_tags[$opened_tag], $tags_block) && $opened_tag != 0) {
				// We tried to open a block tag within a non-block tag
				$errors[] = sprintf(__('[%1$s] was opened within [%2$s], this is not allowed', 'luna'), $current_tag, $open_tags[$opened_tag]);
				return false;
			}

			if (in_array($current_tag, $tags_ignore)) {
				// It's an ignore tag so we don't need to worry about what's inside it
				$current_ignore = $current_tag;
				$count_ignored[$current_tag] = 1;
				$new_text .= $current;
				continue;
			}

			// Deal with nested tags
			if (in_array($current_tag, $open_tags) && !in_array($current_tag, array_keys($tags_nested))) {
				// We nested a tag we shouldn't
				$errors[] = sprintf(__('[%s] was opened within itself, this is not allowed', 'luna'), $current_tag);
				return false;
			} elseif (in_array($current_tag, array_keys($tags_nested))) {
				// We are allowed to nest this tag

				if (isset($current_depth[$current_tag]))
					$current_depth[$current_tag]++;
				else
					$current_depth[$current_tag] = 1;

				// See if we are nested too deep
				if ($current_depth[$current_tag] > $tags_nested[$current_tag]) {
					$current_nest = $current_tag;
					continue;
				}
			}

			// Remove quotes from arguments for certain tags
			if (strpos($current, '=') !== false && in_array($current_tag, $tags_quotes)) {
				$current = preg_replace('%\['.$current_tag.'=("|\'|)(.*?)\\1\]\s*%i', '['.$current_tag.'=$2]', $current);
			}

			if (in_array($current_tag, array_keys($tags_limit_bbcode)))
				$limit_bbcode = $tags_limit_bbcode[$current_tag];

			$open_tags[] = $current_tag;
			$open_args[] = $current_arg;
			$opened_tag++;
			$new_text .= $current;
			continue;
		}
	}

	// Check we closed all the tags we needed to
	foreach ($tags_closed as $check) {
		if (in_array($check, $open_tags)) {
			// We left an important tag open
			$errors[] = sprintf(__('[%1$s] was found without a matching [/%1$s]', 'luna'), $check);
			return false;
		}
	}

	if ($current_ignore) {
		// We left an ignore tag open
		$errors[] = sprintf(__('[%1$s] was found without a matching [/%1$s]', 'luna'), $current_ignore);
		return false;
	}

	return $new_text;
}


//
// Preparse the contents of [list] bbcode
//
function preparse_list_tag($content, $type = '*') {
	global $re_list;

	if (strlen($type) != 1)
		$type = '*';

	if (strpos($content,'[list') !== false) {
		$content = preg_replace_callback($re_list, create_function('$matches', 'return preparse_list_tag($matches[2], $matches[1]);'), $content);
	}

	$items = explode('[*]', str_replace('\"', '"', $content));

	$content = '';
	foreach ($items as $item) {
		if (luna_trim($item) != '')
			$content .= '[*'."\0".']'.str_replace('[/*]', '', luna_trim($item)).'[/*'."\0".']'."\n";
	}

	return '[list='.$type.']'."\n".$content.'[/list]';
}


//
// Truncate URL if longer than 55 characters (add http:// or ftp:// if missing)
//
function handle_url_tag($url, $link = '', $bbcode = false) {
	$url = luna_trim($url);

	// Deal with [url][img]http://example.com/test.png[/img][/url]
	if (preg_match('%<img class="img-responsive" src=\"(.*?)\"%', $url, $matches))
		return handle_url_tag($matches[1], $url, $bbcode);

	$full_url = str_replace(array(' ', '\'', '`', '"'), array('%20', '', '', ''), $url);
	if (strpos($url, 'www.') === 0) // If it starts with www, we add http://
		$full_url = 'http://'.$full_url;
	elseif (strpos($url, 'ftp.') === 0) // elseif it starts with ftp, we add ftp://
		$full_url = 'ftp://'.$full_url;
	elseif (strpos($url, '/') === 0) // Allow for relative URLs that start with a slash
		$full_url = get_base_url(true).$full_url;
	elseif (!preg_match('#^([a-z0-9]{3,6})://#', $url)) // elseif it doesn't start with abcdef://, we add http://
		$full_url = 'http://'.$full_url;

	// Ok, not very pretty :-)
	if ($bbcode) {
		if ($full_url == $link)
			return '[url]'.$link.'[/url]';
		else
			return '[url='.$full_url.']'.$link.'[/url]';
	} else {
		if ($link == '' || $link == $url) {
			$url = luna_htmlspecialchars_decode($url);
			$link = utf8_strlen($url) > 55 ? utf8_substr($url, 0 , 39).' … '.utf8_substr($url, -10) : $url;
			$link = luna_htmlspecialchars($link);
		} else
			$link = stripslashes($link);

		return '<a href="'.$full_url.'" target="_blank" rel="nofollow">'.$link.'</a>';
	}
}


//
// Turns an URL from the [img] tag into an <img> tag or a <a href...> tag
//
function handle_img_tag($url, $is_signature = false, $alt = null) {
	global $luna_user;

	if (is_null($alt))
		$alt = basename($url);

	$img_tag = '<a href="'.$url.'" rel="nofollow">&lt;'.__('image', 'luna').' - '.$alt.'&gt;</a>';

	if ($is_signature && $luna_user['show_img_sig'] != '0')
		$img_tag = '<img class="sigimage img-responsive" style="margin: 0 auto" src="'.$url.'" alt="'.$alt.'" />';
	elseif (!$is_signature && $luna_user['show_img'] != '0')
		$img_tag = '<span class="commentimg"><img class="img-responsive" src="'.$url.'" alt="'.$alt.'" /></span>';

	return $img_tag;
}


//
// Parse the contents of [list] bbcode
//
function handle_list_tag($content, $type = '*') {
	global $re_list;

	if (strlen($type) != 1)
		$type = '*';

	if (strpos($content,'[list') !== false) {
		$content = preg_replace_callback($re_list, create_function('$matches', 'return handle_list_tag($matches[2], $matches[1]);'), $content);
	}

	$content = preg_replace('#\s*\[\*\](.*?)\[/\*\]\s*#s', '<li>$1</li>', luna_trim($content));

	if ($type == '*')
		$content = '<ul>'.$content.'</ul>';
	else
		$content = '<ol class="decimal">'.$content.'</ol>';

	return $content;
}


//
// Convert BBCodes to their HTML equivalent
//
function do_bbcode($text, $is_signature = false) {
	global $luna_user, $luna_config, $re_list;

	if (strpos($text, '[quote') !== false) {
		$text = preg_replace('%\[quote\]\s*%', '</p><blockquote><p>', $text);
        $text = preg_replace('%\[quote=(?P<quote>(?:&quot;|&\#039;|"|\'))?((?(quote)[^\r\n]+?|[^\r\n\]]++))(?(quote)(?P=quote))\]\s*%', '</p><blockquote><footer><cite>$2 '.__('wrote', 'luna').'</cite></footer><p>', $text);
		$text = preg_replace('%\s*\[\/quote\]%S', '</p></blockquote><p>', $text);
	}
    
    if ($luna_config['o_allow_spoiler'] == 1 && strpos($text, '[spoiler') !== false) {
        $text = str_replace('[spoiler]', "</p><div class=\"bbCodeSpoilerContainer\" style=\"padding: 0px;\"><div class=\"bbCodeSpoilerButton\" onclick=\"var e,d,c=this.parentNode,a=c.getElementsByTagName('div')[1],b=this.getElementsByTagName('.fa')[0];if(a.style.display!=''){while(c.parentNode&&(!d||!e||d==e)){e=d;d=(window.getComputedStyle?getComputedStyle(c, null):c.currentStyle)['backgroundColor'];if(d=='transparent'||d=='rgba(0, 0, 0, 0)')d=e;c=c.parentNode;}a.style.display='';b.innerHTML='(click to hide) &#9650;';}else{a.style.display='none';b.innerHTML='(click to show) &#9660;';}\"><span>".__('Spoiler', 'luna')."</span></div><div class=\"spoilerContent\" style=\"display: none;\"><p>", $text);
        $text = preg_replace('#\[spoiler=(.*?)\]#s', '</p><div class="bbCodeSpoilerContainer" style="padding: 0px;"><div class="bbCodeSpoilerButton" onclick="var e,d,c=this.parentNode,a=c.getElementsByTagName(\'div\')[1],b=this.getElementsByTagName(\'span\')[0];if(a.style.display!=\'\'){while(c.parentNode&&(!d||!e||d==e)){e=d;d=(window.getComputedStyle?getComputedStyle(c, null):c.currentStyle)[\'backgroundColor\'];if(d==\'transparent\'||d==\'rgba(0, 0, 0, 0)\')d=e;c=c.parentNode;}a.style.display=\'\';b.innerHTML=\'(click to hide) &#9650;\';}else{a.style.display=\'none\';b.innerHTML=\'(click to show) &#9660;\';}">$1 <span>(click to show) &#9660;</span></div><div class="spoilerContent" style="display: none;"><p>', $text);
        $text = str_replace('[/spoiler]', '</p></div></div><p>', $text);
    }
    
	if (!$is_signature) {
		$pattern_callback[] = $re_list;
		$replace_callback[] = 'handle_list_tag($matches[2], $matches[1])';
	}

	$pattern[] = '%\[b\](.*?)\[/b\]%ms';
	$pattern[] = '%\[i\](.*?)\[/i\]%ms';
	$pattern[] = '%\[u\](.*?)\[/u\]%ms';
	$pattern[] = '%\[s\](.*?)\[/s\]%ms';
	$pattern[] = '%\[c\](.*?)\[/c\]%ms';
	$pattern[] = '%\[ins\](.*?)\[/ins\]%ms';
	$pattern[] = '%\[color=([a-zA-Z]{3,20}|\#[0-9a-fA-F]{6}|\#[0-9a-fA-F]{3})](.*?)\[/color\]%ms';
	$pattern[] = '%\[h\](.*?)\[/h\]%ms';
	$pattern[] = '%\[sup\](.*?)\[/sup\]%ms';
	$pattern[] = '%\[sub\](.*?)\[/sub\]%ms';
	if ($luna_config['o_allow_center'] == 1)
		$pattern[] = '%\[center\](.*?)\[/center\]%ms';
	if ($luna_config['o_allow_size'] == 1)
		$pattern[] = '%\[size=([50-250]*)](.*?)\[/size\]%ms';

	// DailyMotion Videos
	$pattern[] = '%\[video\](\[url\])?([^\[<]*?)/video/([^_\[<]*?)(_([^\[<]*?))?(\[/url\])?\[/video\]%ms';
	// Youtube Videos
	$pattern[] = '%\[video\](\[url\])?([^\[<]*?)/(youtu\.be/|watch\?v=)([^\[<]*?)(&.+)?(\[/url\])?\[/video\]%ms';
	// Vimeo videos
	$pattern[] = '%\[video\](\[url\])?([^\[<]*?)/(vimeo\.com/)([^\[<]*?)(\[/url\])?\[/video\]%ms';

	$replace[] = '<strong>$1</strong>';
	$replace[] = '<em>$1</em>';
	$replace[] = '<span class="underline">$1</span>';
	$replace[] = '<span class="strikethrough">$1</span>';
	$replace[] = '<code>$1</code>';
	$replace[] = '<ins>$1</ins>';
	$replace[] = '<span style="color: $1">$2</span>';
	$replace[] = '<h3 class="comment-h3">$1</h3>';
	$replace[] = '<sup>$1</sup>';
	$replace[] = '<sub>$1</sub>';
	if ($luna_config['o_allow_center'] == 1)
		$replace[] = '</p><p style="text-align: center">$1</p><p>';
	if ($luna_config['o_allow_size'] == 1)
		$replace[] = '<span style="font-size: $1%">$2</span>';

	// DailyMotion videos
	$replace[] = '<div class="embed-responsive embed-responsive-16by9"><iframe class="embed-responsive-item" src="http://www.dailymotion.com/embed/video/$3"></iframe></div>';
	// Youtube Videos
    $replace[] = '<div class="embed-responsive embed-responsive-16by9"><iframe class="embed-responsive-item" src="https://www.youtube.com/embed/$4" allowfullscreen></iframe></div>';
	// Vimeo Videos
	$replace[] = '<div class="embed-responsive embed-responsive-16by9"><iframe class="embed-responsive-item" src="http://player.vimeo.com/video/$4"></iframe></div>';

	if (($is_signature && $luna_config['o_sig_img_tag'] == '1') || (!$is_signature && $luna_config['o_message_img_tag'] == '1')) {
		$pattern_callback[] = '%\[img\]((ht|f)tps?://)([^\s<"]*?)\[/img\]%';
		$pattern_callback[] = '%\[img=([^\[]*?)\]((ht|f)tps?://)([^\s<"]*?)\[/img\]%';
		if ($is_signature) {
			$replace_callback[] = 'handle_img_tag($matches[1].$matches[3], true)';
			$replace_callback[] = 'handle_img_tag($matches[2].$matches[4], true, $matches[1])';
		} else {
			$replace_callback[] = 'handle_img_tag($matches[1].$matches[3], false)';
			$replace_callback[] = 'handle_img_tag($matches[2].$matches[4], false, $matches[1])';
		}
	}

	$pattern_callback[] = '%\[url\]([^\[]*?)\[/url\]%';
	$pattern_callback[] = '%\[url=([^\[]+?)\](.*?)\[/url\]%';
	$pattern[] = '%\[email\]([^\[]*?)\[/email\]%';
	$pattern[] = '%\[email=([^\[]+?)\](.*?)\[/email\]%';

	$replace_callback[] = 'handle_url_tag($matches[1])';
	$replace_callback[] = 'handle_url_tag($matches[1], $matches[2])';
	$replace[] = '<a href="mailto:$1">$1</a>';
	$replace[] = '<a href="mailto:$1">$2</a>';

	// This thing takes a while! :)
	$text = preg_replace($pattern, $replace, $text);
	$count = count($pattern_callback);
	for($i = 0 ; $i < $count ; $i++) {
		$text = preg_replace_callback($pattern_callback[$i], 
		//Replaced create_function due to being depreciated in PHP 7.2
		//create_function('$matches', 'return '.$replace_callback[$i].';'), 
		function ($matches) {
        	return $replace_callback[$i];
        }, $text);
		//$text);
	}
	return $text;
}


//
// Make hyperlinks clickable
//
function do_clickable($text) {
	$text = ' '.$text;
	$text = ucp_preg_replace_callback('%(?<=[\s\]\)])(<)?(\[)?(\()?([\'"]?)(https?|ftp|news){1}://([\p{L}\p{N}\-]+\.([\p{L}\p{N}\-]+\.)*[\p{L}\p{N}]+(:[0-9]+)?(/(?:[^\s\[]*[^\s.,?!\[;:-])?)?)\4(?(3)(\)))(?(2)(\]))(?(1)(>))(?![^\s]*\[/(?:url|img)\])%ui', 'stripslashes($matches[1].$matches[2].$matches[3].$matches[4]).handle_url_tag($matches[5]."://".$matches[6], $matches[5]."://".$matches[6], true).stripslashes($matches[4].$matches[10].$matches[11].$matches[12])', $text);
	$text = ucp_preg_replace_callback('%(?<=[\s\]\)])(<)?(\[)?(\()?([\'"]?)(www|ftp)\.(([\p{L}\p{N}\-]+\.)+[\p{L}\p{N}]+(:[0-9]+)?(/(?:[^\s\[]*[^\s.,?!\[;:-])?)?)\4(?(3)(\)))(?(2)(\]))(?(1)(>))(?![^\s]*\[/(?:url|img)\])%ui','stripslashes($matches[1].$matches[2].$matches[3].$matches[4]).handle_url_tag($matches[5].".".$matches[6], $matches[5].".".$matches[6], true).stripslashes($matches[4].$matches[10].$matches[11].$matches[12])', $text);

	return substr($text, 1);
}

function get_smilies() {
	global $luna_config;

	// Here you can add additional smilies if you like (please note that you must escape single quote and backslash)
	if ($luna_config['o_emoji'] == 1) {
		$smilies = array(
			':)' => '&#x1f601;',
			':|' => '&#x1f611;',
			':(' => '&#x1f629;',
			':d' => '&#x1f604;',
			':D' => '&#x1f604;',
			':o' => '&#x1f62f;',
			':O' => '&#x1f62f;',
			';)' => '&#x1f609;',
			':/' => '&#x1f612;',
			':P' => '&#x1f60b;',
			':p' => '&#x1f60b;',
			':lol:' => '&#x1f601;',
			':-))' => '&#x1f601;',
			':@' => '&#x1f620;',
			'%)' => '&#x1f606;',
			'b:' => '&#x1f60e;',
			'B:' => '&#x1f60e;',
			':hc:' => '&#x1f605;',
			'(A)' => '&#x1f607;',
			'(a)' => '&#x1f607;',
			'^-^' => '&#x1f60f;',
			'^.^' => '&#x1f60f;'
		);
	} else {
		$smilies = array(
			':)' => 'smile.png',
			':|' => 'neutral.png',
			':(' => 'sad.png',
			':d' => 'big_smile.png',
			':D' => 'big_smile.png',
			':o' => 'yikes.png',
			':O' => 'yikes.png',
			';)' => 'wink.png',
			':/' => 'hmm.png',
			':P' => 'tongue.png',
			':p' => 'tongue.png',
			':lol:' => 'happy.png',
			':-))' => 'happy.png',
			':@' => 'angry.png',
			'%)' => 'roll.png',
			'b:' => 'cool.png',
			'B:' => 'cool.png',
			':hc:' => 'happycry.png',
			'(A)' => 'angel.png',
			'^-^' => 'ohyeah.png',
			'(a)' => 'angel.png',
			'(A)' => 'angel.png',
			'^-^' => 'happy.png',
			'^.^' => 'happy.png'
		);
	}

	return $smilies;
}


//
// Convert a series of smilies to images
//
function do_smilies($text) {
	global $luna_config;

	$smilies = get_smilies();

	$text = ' '.$text.' ';

	foreach ($smilies as $smiley_text => $smiley_img) {
		if (strpos($text, $smiley_text) !== false)
			if ($luna_config['o_emoji'] == 1)
				$text = ucp_preg_replace('%(?<=[>\s])'.preg_quote($smiley_text, '%').'(?=[^\p{L}\p{N}])%um', '<span class="emoji">'.$smiley_img.'</span>', $text);
			else
				$text = ucp_preg_replace('%(?<=[>\s])'.preg_quote($smiley_text, '%').'(?=[^\p{L}\p{N}])%um', '<img src="'.luna_htmlspecialchars(get_base_url(true).'/img/smilies/'.$smiley_img).'" width="'.$luna_config['o_emoji_size'].'" height="'.$luna_config['o_emoji_size'].'" alt="'.substr($smiley_img, 0, strrpos($smiley_img, '.')).'" />', $text);
	}

	return substr($text, 1, -1);
}


//
// Parse message text
//
function parse_message($text) {
	global $luna_config, $luna_user;

	if ($luna_config['o_censoring'] == '1')
		$text = censor_words($text);

	// Convert applicable characters to HTML entities
	$text = luna_htmlspecialchars($text);

	// If the message contains a code tag we have to split it up (text within [code][/code] shouldn't be touched)
	if (strpos($text, '[code]') !== false && strpos($text, '[/code]') !== false)
		list($inside, $text) = extract_blocks($text, '[code]', '[/code]');

	if (strpos($text, '[') !== false && strpos($text, ']') !== false)
		$text = do_bbcode($text);

	$text = do_smilies($text);

	// Deal with newlines, tabs and multiple spaces
	$pattern = array("\n", "\t", '  ', '  ');
	$replace = array('<br />', '&#160; &#160; ', '&#160; ', ' &#160;');
	$text = str_replace($pattern, $replace, $text);

	// If we split up the message before we have to concatenate it together again (code tags)
	if (isset($inside)) {
		$parts = explode("\1", $text);
		$text = '';
		foreach ($parts as $i => $part) {
			$text .= $part;
			if (isset($inside[$i])) {
				$num_lines = (substr_count($inside[$i], "\n"));$code_line = explode("\n", $inside[$i]);
				$first_line = trim($code_line[1]);
				if (strpos($first_line, '[[') !== false && strpos($first_line, ']]') !== false) {
					// fetching the language name
					$language = strtolower(trim(str_replace(array('[[', ']]'), '', $first_line)));

					if ($language == 'html' || $language == 'xhtml' || $language == 'xml') { // Markup case
						$h_class = ' class="language-markup"';
					} elseif ($language == 'c') { // C case
						$h_class = ' class="language-c"';
					} elseif ($language == 'c#') { // C# case
						$h_class = ' class="language-csharp"';
					} elseif ($language == 'c++') { // C++ case
						$h_class = ' class="language-cpp"';
					} elseif ($language == 'java') { // Java case
						$h_class = ' class="language-java"';
					} elseif ($language == 'javascript') { // JavaScript case
						$h_class = ' class="language-javascript"';
					} elseif ($language == 'markdown') { // MarkDown case
						$h_class = ' class="language-markdown"';
					} elseif ($language == 'pascal') { // Pascal case
						$h_class = ' class="language-pascal"';
					} elseif ($language == 'php') { // PHP case
						$h_class = ' class="language-php"';
					} elseif ($language == 'python') { // Python case
						$h_class = ' class="language-python"';
					} elseif ($language == 'sql') { // SQL case
						$h_class = ' class="language-sql"';
					} else { // Other cases
						$h_class = '';
					}

					// Deleting the line giving the code name
					$inside[$i] = str_replace($first_line, '', $inside[$i]);
					// Generating the the HTML code block
					$text .= '</p><div class="codebox"><pre'.(($num_lines > 28) ? ' class="vscroll"' : '').'><code'.$h_class.'>'.luna_trim($inside[$i], "\n\r").'</code></pre></div><p>';
				} else {
					$text .= '</p><div class="codebox"><pre'.(($num_lines > 28) ? ' class="vscroll"' : '').'><code>'.luna_trim($inside[$i], "\n\r").'</code></pre></div><p>';
				}
			}
		}
	}

	return clean_paragraphs($text);
}


//
// Clean up paragraphs and line breaks
//
function clean_paragraphs($text)
{
	// Add paragraph tag around post, but make sure there are no empty paragraphs

	$text = '<p>'.$text.'</p>';

	// Replace any breaks next to paragraphs so our replace below catches them
	$text = preg_replace('%(</?p>)(?:\s*?<br />){1,2}%i', '$1', $text);
	$text = preg_replace('%(?:<br />\s*?){1,2}(</?p>)%i', '$1', $text);

	// Remove any empty paragraph tags (inserted via quotes/lists/code/etc) which should be stripped
	$text = str_replace('<p></p>', '', $text);

	$text = preg_replace('%<br />\s*?<br />%i', '</p><p>', $text);

	$text = str_replace('<p><br />', '<br /><p>', $text);
	$text = str_replace('<br /></p>', '</p><br />', $text);
	$text = str_replace('<p></p>', '<br /><br />', $text);

	return $text;
}


//
// Parse signature text
//
function parse_signature($text) {
	global $luna_config, $luna_user;

	if ($luna_config['o_censoring'] == '1')
		$text = censor_words($text);

	// Convert applicable characters to HTML entities
	$text = luna_htmlspecialchars($text);

	if (strpos($text, '[') !== false && strpos($text, ']') !== false)
		$text = do_bbcode($text, true);

	if ($luna_config['o_smilies_sig'] == '1' && $luna_user['show_smilies'] == '1')
		$text = do_smilies($text);


	// Deal with newlines, tabs and multiple spaces
	$pattern = array("\n", "\t", '  ', '  ');
	$replace = array('<br />', '&#160; &#160; ', '&#160; ', ' &#160;');
	$text = str_replace($pattern, $replace, $text);

	return clean_paragraphs($text);
}
