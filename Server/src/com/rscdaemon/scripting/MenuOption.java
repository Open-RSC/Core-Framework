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
package com.rscdaemon.scripting;

import com.rscdaemon.scripting.util.FunctionPointer;

/**
 * A representation of an option that is presented as a choice to 
 * a player in a menu.  Every <code>MenuOption</code> has a 
 * caption as well as a method that shall be fired when this option 
 * is selected.
 * 
 * @author Zilent
 *
 * @version 1.0
 * 
 * @since 3.3.0
 * 
 * @see FunctionPointer
 * 
 */
public class MenuOption
{
	/// The caption that is presented to the client
	private final String text;
	
	/// The method that is invoked upon selection
	private final FunctionPointer handler;
	
	/**
	 * Constructs a <code>MenuOption</code> with the provided caption and 
	 * <code>FunctionPointer</code>
	 * 
	 * @param text the caption that is presented to the client
	 * 
	 * @param handler the method that is invoked upon selection
	 * 
	 */
	public MenuOption(String text, FunctionPointer handler)
	{
		this.text = text;
		this.handler = handler;
	}

	/**
	 * Retrieves the caption that is presented to the client
	 * 
	 * @return the caption that is presented to the client
	 * 
	 */
	public final String getText()
	{
		return text;
	}
	
	/**
	 * Retrieves the method that is invoked upon selection
	 * 
	 * @return the method that is invoked upon selection
	 * 
	 */
	public final FunctionPointer getHandler()
	{
		return handler;
	}
}
