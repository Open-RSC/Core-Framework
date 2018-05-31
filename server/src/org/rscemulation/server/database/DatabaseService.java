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

/**
 * <code>DatabaseServices</code> are windows through which the server 
 * can asynchronously access external storage mediums, such as databases.
 * 
 * @author Zilent
 * 
 * @version 1.1, 1.27.2013
 * 
 * @since 3.0
 *
 */
public interface DatabaseService
	extends
		AutoCloseable
{
	/**
	 * Submits a Transaction to this <code>DatabaseService</code> with the 
	 * provided TransactionListener
	 * 
	 * @param transaction the <code>Transaction</code> to submit
	 * 
	 * @param listener the <code>TransactionListener</code> to fire upon 
	 * completion
	 * 
	 * @return was the <code>Transaction</code> submitted successfully?
	 * 
	 */
	boolean submit(Transaction transaction, TransactionListener listener);
	
	/**
	 * Submits a Transaction to this <code>DatabaseService</code>.  
	 * Essentially, this is roughly equivalent to:
	 * <pre>
	 * DatabaseService.submit(transaction, null);
	 * </pre>
	 * 
	 * @param transaction the <code>Transaction</code> to submit
	 * 
	 * @return was the <code>Transaction</code> submitted successfully?
	 * 
	 */
	boolean submit(Transaction transaction);
	
}
