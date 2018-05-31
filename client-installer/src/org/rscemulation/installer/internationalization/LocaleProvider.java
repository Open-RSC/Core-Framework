/*
 * Copyright (C) RSCDaemon - All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by RSCDaemon Team <dev@rscdaemon.com>, Unknown Date
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
package org.rscemulation.installer.internationalization;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * A singleton class to encapsulate the {@link ResourceBundle} implementation.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 * 
 * @see {@link <a href=
 * http://docs.oracle.com/javase/tutorial/i18n/resbundle/concept.html>
 * About the ResourceBundle Class</a> (Oracle Tutorial)}
 *
 */
public final class LocaleProvider
{
	private final static String PACKAGE = 
		"org.rscemulation.installer.strings";//$NON-NLS-1$

	/// Load the system default bundle (Default to English if N/A)
	private static final ResourceBundle DEFAULT_BUNDLE = ResourceBundle
			.getBundle(PACKAGE);

	/**
	 * Private constructor
	 * 
	 */
	private LocaleProvider()
	{
		
	}

	/**
	 * Retrieves the string at the provided key
	 * 
	 * @param key the key whose string to find
	 * 
	 * @return the string at the provided key
	 * 
	 * @throws NullPointerException - if key is null 
	 * 
	 * @throws MissingResourceException - if no object for the 
	 * given key can be found 
	 * 
	 * @throws ClassCastException - if the object found for the 
	 * given key is not a string
	 */
	public static String getString(String key)
	{
		return DEFAULT_BUNDLE.getString(key);
	}
}
