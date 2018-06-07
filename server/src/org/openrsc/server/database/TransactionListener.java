/*
 * Copyright (C) openrsc 2009-13 All Rights Reserved
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * 
 * Written by openrsc Team <dev@openrsc.net>, January, 2013
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

package org.openrsc.server.database;

/**
 * <code>TransactionListeners</code> are special objects that provide a means 
 * by which to keep track of the results of asynchronous transaction 
 * services.  <code>TransactionListeners</code> implements two methods, one 
 * indicates that a Transaction completed successfully, while the other 
 * indicates that a Transaction has failed at some point in its execution, 
 * providing the error-code as an argument.  Example usage below:
 * <pre>
 *
 *	final Transaction t = new Transaction()
 * 	{
 * 		public final static int ERROR_CONNECTION_CLOSED = 1;
 * 
 *		public final Connection getConnection() { ... }
 *
 *		public final Integer call() throws Exception
 *		{
 *			/// Remember: If this.getConnection() returns null (NPE), then 
 *			/// Transaction.UNHANDLED_EXCEPTION is provided to the listener!
 *			if(getConnection().isClosed())
 *			{
 *				return ERROR_CONNECTION_CLOSED;
 *			}
 *			return Transaction.TRANSACTION_SUCCESS;
 *		}
 * 	};
 * 	databaseService.submit(t,
 *		new TransactionListener()
 *		{
 *			/// Remember: These methods are invoked by the DatabaseService!
 *
 *			public final void onSuccess()
 *			{
 *				...handle success...
 *			}
 *
 *			public final void onFailure(int code)
 *			{
 *				... handle failure...
 *			}
 *		});
 * </pre>
 * 
 * @author Zilent
 * 
 * @version 1.1, 1.27.2013
 * 
 * @since 3.0
 *
 */
public interface TransactionListener
{	
	/**
	 * Invoked upon the successful completion of a Transaction.  In other 
	 * words, when the Transaction's result is Transaction.TRANSACTION_SUCCESS
	 *
	 */
	void onSuccess();
	
	/**
	 * Invoked upon the failed execution of a Transaction.  In other 
	 * words, when the Transaction's result is <b>not</b> 
	 * Transaction.TRANSACTION_SUCCESS
	 *
	 * @param code the error-code
	 *
	 */
	void onFailure(int code);
}
