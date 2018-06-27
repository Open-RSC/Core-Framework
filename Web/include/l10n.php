<?php

function get_locale() {

	global $luna_user;

	if (isset($luna_user['language'])) {
		$luna_locale = $luna_user['language'];
	} else {
		$luna_locale = 'English';
	}

	return $luna_locale;
}

/**
 * Load a .mo file into the text domain $domain.
 *
 * If the text domain already exists, the translations will be merged. If both
 * sets have the same string, the translation from the original value will be taken.
 *
 * On success, the .mo file will be placed in the $l10n global by $domain
 * and will be a MO object.
 *
 * @since 1.1
 *
 * @param    string     $domain Text domain. Unique identifier for retrieving translated strings.
 * @param    string     $mofile Path to the .mo file.
 *
 * @return   boolean    True on success, false on failure.
 */
function load_textdomain($domain, $mofile) {

	global $l10n;

	if (!is_readable($mofile)) {
		return false;
	}

	$mo = new MO();
	if (!$mo->import_from_file($mofile)) {
		return false;
	}

	if (isset($l10n[$domain])) {
		$mo->merge_with($l10n[$domain]);
	}

	$l10n[$domain] = &$mo;

	return true;
}

function __($text, $domain = 'default') {

	return luna_translate($text, $domain);
}

function _e($text, $domain = 'default') {

	echo luna_translate($text, $domain);
}

function _n($single, $plural, $number, $domain = 'default') {

	$translations = load_translations($domain);
    
    if ($number == 1)
        $translation = __( $single, $domain );
    else
        $translation = __( $plural, $domain );

	return $translation;
}

function luna_translate($text, $domain = 'default') {

	$translations = load_translations($domain);
	$translations = $translations->translate($text);

	return $translations;
}

function load_translations($domain) {

	global $l10n;

	if (!isset($l10n[$domain])) {
		$l10n[$domain] = new NOOPTranslations;
	}

	return $l10n[$domain];
}
