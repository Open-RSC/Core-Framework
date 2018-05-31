package com.rscdaemon.asset;

import java.io.Serializable;

/**
 * The interface for all externally loaded assets
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 * 
 */
public interface Asset
	extends
		Serializable
{
	/**
	 * Retrieves the name of this <code>Asset</code>.  This name should be 
	 * treated as though the end-user can see it.
	 * 
	 * @return the name of this <code>Asset</code>
	 * 
	 */
	String getName();
}
