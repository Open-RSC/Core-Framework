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
package com.rscdaemon.concurrent;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;

/**
 * A {@link ThreadFactory} that makes use of the builder pattern.
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.4.0
 *
 */
public final class ConfigurableThreadFactory
	implements
		ThreadFactory
{

	/// The name to assign to new threads
	private final String name;
	
	/// The priority to assign to new threads
	private final int priority;

	/// Should new threads be marked as daemons?
	private final boolean daemon;
	
	/// The {@link UncaughtExceptionHandler} to assign to new threads
	private final UncaughtExceptionHandler uncaughtExceptionHandler;

	/**
	 * Constructs a <code>ConfigurableThreadFactory</code> with the provided 
	 * <code>ConfigurationBuilder</code>
	 * 
	 * @param config the <code>ConfigurationBuilder</code> that defines the 
	 * properties of this <code>ConfigurableThreadFactory</code>
	 * 
	 * @throws NullPointerException if the provided 
	 * <code>ConfiguratnoiBuilder</code> is null
	 * 
	 */
	public ConfigurableThreadFactory(ConfigurationBuilder config)
	{
		this.name = config.name;
		this.priority = config.priority;
		this.daemon = config.daemon;
		this.uncaughtExceptionHandler = config.uncaughtExceptionHandler;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public Thread newThread(Runnable r)
	{
		Thread rv = new Thread(r);
		rv.setName(name);
		rv.setPriority(priority);
		rv.setDaemon(daemon);
		rv.setUncaughtExceptionHandler(uncaughtExceptionHandler);
		return rv;
	}
	
	/**
	 * A helper class to build <code>ConfigurableThreadFactory</code> 
	 * properties with
	 * 
	 * @author Zilent
	 *
	 * @version 1.0
	 * 
	 * @since 3.4.0
	 *
	 */
	public final static class ConfigurationBuilder
	{
		/// The default name to assign to threads
		private final static String DEFAULT_THREAD_NAME = "";
		
		/// The name to assign to new threads
		private String name;
		
		/// The priority to assign to new threads
		private int priority;
		
		/// Should new threads be marked as daemons?
		private boolean daemon;
		
		/// The {@link UncaughtExceptionHandler} to assign to new threads
		private UncaughtExceptionHandler uncaughtExceptionHandler;
		
		/**
		 * Constructs a <code>ConfigurationBuilder</code>
		 * 
		 */
		public ConfigurationBuilder()
		{
			name = DEFAULT_THREAD_NAME;
			priority = Thread.NORM_PRIORITY;
		}
	
		/**
		 * Sets the name assigned to new threads
		 * 
		 * @param name the new name to assign
		 * 
		 * @return this
		 * 
		 */
		public ConfigurationBuilder setName(String name)
		{
			this.name = name;
			return this;
		}

		/**
		 * Sets the priority assigned to new threads
		 * 
		 * @param priority the new priority to assign
		 * 
		 * @return this
		 * 
		 */
		public ConfigurationBuilder setPriority(int priority)
		{
			this.priority = priority;
			return this;
		}

		/**
		 * Sets the daemon state assigned to new threads
		 * 
		 * @param daemon the new daemon state to assign
		 * 
		 * @return this
		 * 
		 */
		public ConfigurationBuilder setDaemon(boolean daemon)
		{
			this.daemon = daemon;
			return this;
		}

		/**
		 * Sets the <code>UncaughtExceptionHandler</code> assigned to new 
		 * threads
		 * 
		 * @param priority the new <code>UncaughtExceptionHandler</code> to 
		 * assign
		 * 
		 * @return this
		 * 
		 */
		public ConfigurationBuilder setUncaughtExceptionHandler(UncaughtExceptionHandler uncaughtExceptionHandler)
		{
			this.uncaughtExceptionHandler = uncaughtExceptionHandler;
			return this;
		}
	}
}
