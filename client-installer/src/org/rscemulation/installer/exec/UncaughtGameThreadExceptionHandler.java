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
package org.rscemulation.installer.exec;

import org.rscemulation.installer.Logger;

/**
 * The {@link Thread.UncaughtExceptionHandler} that shall handle all 
 * fatal exceptions that stem from a game client application instance.  The 
 * exception conditions are provided to a remote database.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public final class UncaughtGameThreadExceptionHandler
	implements
		Thread.UncaughtExceptionHandler
{

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void uncaughtException(Thread t, Throwable e)
	{
		Logger.error(e);
	}

}
