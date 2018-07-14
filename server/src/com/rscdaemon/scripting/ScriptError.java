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

/**
 * All exceptions thrown from DaemonScript will be derived from this type.
 * 
 * <code>ScriptError</code> messages are in the following format: 
 * [Script Name]: [Message] (eg. "TestScript: Unable to bind a null variable!")
 * 
 * @author Zilent
 *
 * @version 1.0
 *
 * @since 3.3.0
 * 
 */
public class ScriptError
	extends
		RuntimeException
{
	// Compatible with version 1.0 and later
	private static final long serialVersionUID = -960363189817331232L;

	/**
	 * Constructs a <code>ScriptException</code>
	 * 
	 * @param script the <code>Script</code> that encountered an error
	 * 
	 * @param message a brief message about the circumstance
	 * 
	 */
	public ScriptError(Script script, String message)
	{
		super((script != null ? script.getClass().getSimpleName() : "Null Script") + ": " + message);
	}
		
}
