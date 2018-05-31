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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.rscemulation.installer.Config;

/**
 * An {@link ActionListener} that uses reflection to launch the game client.  
 * Upon being invoked, a new thread is spawned that begins the 
 * execution of a game client instance.
 * <br>
 * <br>
 * The threads spawned by this class have the following properties:
 * <br>
 * <br>
 * <ul>
 * 	<li>Threads are named based on 
 * 	{@link Config#CLIENT_MAIN_THREAD_NAME_PREFIX} appended with an underscore
 * 	and an auto-incrementing static accumulator ID.</li>
 * 	<li>Executed with {@link Thread#MAX_PRIORITY}</li>
 * 	<li>Are non-daemon</li>
 * 	<li>Have a {@link UncaughtGameThreadExceptionHandler} attached to them 
 * 	in order to properly report fatal client crashes</li>
 * </ul>
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 1.0
 *
 */
public final class ClientLauncher
	implements
		ActionListener
{

	/// An auto-incrementing ID to append to each thread name
	private final static AtomicInteger threadIDAccumulator = new AtomicInteger(0);
	
	/// The list of arguments to pass to the main method
	private final String[] args;
	
	/**
	 * Constructs a <code>ClientLauncher</code> with the provided arguments.
	 * 
	 * @param args the list of arguments to pass to the main method (null is 
	 * permitted)
	 * 
	 */
	public ClientLauncher(String[] args)
	{
		if(args == null)
		{
			args = new String[] {};
		}
		this.args = args;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public void actionPerformed(ActionEvent event)
	{
		Thread gameThread = new Thread(
			new Runnable(){
				@Override
				public void run()
				{
					// Uncomment to test the error logger...
					//if(true) throw new InternalError();
					JarClassLoader jcl = null;
					try
					{
						jcl = new JarClassLoader(
								new File(Config.MAIN_CLASS_JAR_FILE)
														.toURI().toURL());
						jcl.invokeClass(jcl.getMainClassName(), args);
					}
					catch(Exception e)
					{
						e.printStackTrace();
						throw (RuntimeException)
							new RuntimeException("Unhandled Game Exception")
								.initCause(e);
					}
					finally
					{
						if(jcl != null)
						{
							try
							{
								jcl.close();
							}
							catch(IOException ioe)
							{
								/** Ignore */
							}
						}
					}
				}
			}
		);
		gameThread.setName(Config.CLIENT_MAIN_THREAD_NAME_PREFIX + 
				threadIDAccumulator.getAndIncrement());
		gameThread.setPriority(Thread.MAX_PRIORITY);
		gameThread.setDaemon(false);
		gameThread.setUncaughtExceptionHandler(
				new UncaughtGameThreadExceptionHandler());
		gameThread.start();
	}
}
