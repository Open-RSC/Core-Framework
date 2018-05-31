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

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

///import org.apache.commons.logging.Log;
///import org.apache.commons.logging.LogFactory;

/**
 * The default DatabaseService implementation for RSCEmulation.  This service 
 * consists of a single threaded transaction executor that guarantees that 
 * only one Transaction will be processed at any given time.  This 
 * implementation is inherently thread-safe.  <b>Due to the highly concurrent 
 * nature of large scale server applications, it may be deemed that this area 
 * is a bottleneck in future versions.</b>
 * 
 * @author Zilent
 * 
 * @version 1.1, 1.27.2013
 * 
 * @since 3.0
 *
 */
public final class DefaultDatabaseService
	implements
		DatabaseService
{
	/// A specialized pair that overrides Object#equals to compare only 
	/// the transaction field, ignoring the listener field
	private final static class Pair
	{
		private final Transaction transaction;
		private final TransactionListener listener;
		
		public Pair(Transaction transaction, TransactionListener listener)
		{
			this.transaction = transaction;
			this.listener = listener;
		}
		
		@Override
		public boolean equals(Object rhs)
		{
			return ((Pair)rhs).transaction.equals(transaction);
		}
	}

	/// An internal logger instance
	///private final static Log logger = 
	///		LogFactory.getLog(DefaultDatabaseService.class);
	
	/// A producer-consumer based queue for storing pending transactions
	private final BlockingDeque<Pair> 
		transactions =
			new LinkedBlockingDeque<Pair>();

	/// The underlying executor service
	private final ExecutorService executor = 
			Executors.newSingleThreadExecutor();

	/// A consumer task that transfers tasks from the queue to the executor,
	/// monitors the success or failure of said tasks, and fires applicable 
	/// listeners.
	private final Callable<Object> consumer =
			new Callable<Object>()
	{
		@Override
		public Object call()
			throws
				Exception
		{
			/// Loop until the service is shut down and all tasks are done...
			while(!DefaultDatabaseService.this.executor.isShutdown() ||
					!transactions.isEmpty())
			{
				try
				{
					/// Try to grab a task from the queue
					Pair p = transactions.pollLast(1000, TimeUnit.MILLISECONDS);
					
					if(p == null)
					{
						continue;
					}
					
					int code = Transaction.UNDEFINED;

					Transaction t = p.transaction;

					try
					{					
						/// Execute the transaction
						code = t.call();
						/// If the database is down and retry is enabled...
						if(code == Transaction.DATABASE_UNAVAILABLE && 
								t.retryOnFatalError())
						{
							System.out.println("Transaction " + t + " has " +
									"failed and has been requeued " +
									"{cause = \"Database Offline\"}");
							transactions.offerLast(p);
							continue;
						}
					}
					catch(Exception e)
					{
						/// Catch any exceptions that might have happened
						/// and set the code appropriately
						code = Transaction.UNHANDLED_EXCEPTION;
						e.printStackTrace();
					}
					System.out.println("Transaction " + t + " completed with " + 
							"exit code [" + code + "]");
					///logger.debug("Transaction " + t + " completed with " + 
					///		"exit code [" + code + "]");

					/// If a listener is associated, fire it
					TransactionListener listener = p.listener;
					if(listener != null)
					{
						if(code == Transaction.TRANSACTION_SUCCESS)
						{
							listener.onSuccess();
						}
						else
						{
							listener.onFailure(code);
						}
					}
				}
				catch(Throwable fatal)
				{
					//logger.fatal("A would-be fatal exception occurred", fatal);
					System.out.println("A would-be fatal exception occurred" + fatal.getMessage());
					fatal.printStackTrace();
				}
			}
			//logger.debug("Database Service Closed");
			return null;
		}
	};
	
	/**
	 * Constructs a new <code>DefaultDatabaseService</code>
	 * 
	 */
	public DefaultDatabaseService()
	{
		executor.submit(consumer);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean submit(Transaction transaction, 
			TransactionListener listener)
	{
		return transactions.offerFirst(new Pair(transaction, listener));
	}

	/**
	 * {@inheritDoc}
	 *
	 */
	@Override
	public boolean submit(Transaction transaction)
	{
		return submit(transaction, null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void close()
		throws
			Exception
	{
		/// Request shutdown...
		executor.shutdown();
		
		/// Uninterruptibly wait for the service to stop...
		boolean interrupted = false;
		do
		{
			try
			{
				executor.awaitTermination(60000, TimeUnit.MILLISECONDS);				
			}
			catch(InterruptedException ie)
			{
				interrupted = true;
			}
		}
		while(!executor.isTerminated());
		
		/// Preserve interrupt status (if applicable)
		if(interrupted)
		{
			Thread.currentThread().interrupt();
		}
	}

}
