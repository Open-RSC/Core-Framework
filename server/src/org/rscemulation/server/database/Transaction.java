/*
 * Copyright (C) RSCEmulation 2009-13 All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by RSCEmulation Team <dev@rscemulation.net>, January, 2013
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
 *
 *
 *
 *
 */
 
package org.rscemulation.server.database;

import java.sql.Connection;
import java.util.concurrent.Callable;

/**
 * <code>Transactions</code> are units of database interaction that 
 * may be submitted to a DatabaseService.  Special care must be taken 
 * such that multiple <code>Transactions</code> do not rely on each-other 
 * to function correctly, as there is no guarantee that multiple 
 * <code>Transactions</code> will be processed A) in order, and 
 * B) atomically by the underlying database driver.
 * 
 * <strong> Transactions may not return integers from 0 - 15, as those values 
 * are reserved for internal use.  Failure to comply may lead to 
 * strange behavior!
 *
 * All queries contained  within a <code>Transaction</code> must handle 
 * their own synchronization in respect to the parts of the database that 
 * they access.</strong>  Example:
 * 
 * <pre>
 * SELECT * FROM `users` FOR UPDATE
 * ...after lock has been obtained...update users...
 * </pre>
 * 
 * @author Zilent
 * 
 * @version 1.1, 1.27.2013
 * 
 * @since 3.0
 *
 */
 public interface Transaction
	extends
		Callable<Integer>
{
	 
	/**
	 * A symbolic constant of a successful execution
	 *
	 */
	static int TRANSACTION_SUCCESS = 0x0;
	
	/**
	 * A symbolic constant of an unhandled exception encountered in execution
	 *
	 */
	static int UNHANDLED_EXCEPTION = 0x1;
	
	/**
	 * A symbolic constant of a condition where the database is inaccessible
	 * 
	 */
	static int DATABASE_UNAVAILABLE = 0x2;

	/**
	 * A symbolic constant of an undefined state
	 * 
	 */
	static int UNDEFINED = 0x3;
	
	/**
	 * Retrieves a {@link java.sql.Connection connection} to the requested 
	 * database.
	 * 
	 * @return a connection to the requested database
	 * 
	 */
	Connection getConnection();
	
	boolean retryOnFatalError();
}
