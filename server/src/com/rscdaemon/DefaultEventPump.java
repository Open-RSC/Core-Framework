package com.rscdaemon;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.DelayQueue;

/**
 * The default implementation of the {@link EventPump} interface.  This 
 * implementation is inherently thread-safe and is geared towards highly 
 * concurrent environments.
 * 
 * @author Zilent
 *
 * @param <T> The {@link Event} extension that this <code>EventPump</code> 
 * operates on.
 * 
 */
public class DefaultEventPump<T extends Event>
	implements
		EventPump<T>
{

	/// The {@link Queue} that holds the currently active {@link Event events}
	private final transient Queue<T> events = new DelayQueue<T>();
	
	/**
	 * Constructs a <code>DefaultEventPump</code>
	 * 
	 */
	public DefaultEventPump()
	{

	}
	
	/**
	 * Constructs a <code>DefaultEventPump</code> with the provided 
	 * {@link Collection} of {@link Event events} as submitted events
	 * 
	 * @param events the <code>Collection</code> of <code>Events</code> that 
	 * are initially submitted
	 * 
	 * @throws NullPointerException if the provided <code>Collection</code> 
	 * of <code>Events</code> is null
	 * 
	 */
	public DefaultEventPump(Collection<T> events)
		throws
			NullPointerException
	{
		events = new DelayQueue<T>(events);
	}
	
	/**
	 * {@inheritDoc}
	 * <br>
	 * <br>
	 * The default implementation shall run all eligible {@link Event events} 
	 * without blocking and return when no more <code>Events</code> are 
	 * eligible to be ran.
	 * 
	 */
	@Override
	public void run()
	{
		T event = null;
		while((event = events.poll()) != null)
		{
			try
			{
				event.run();
			}
			catch(RuntimeException e)
			{
				
			}
			catch(Throwable t)
			{
				
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * <br>
	 * <br>
	 * The default implementation returns {@link Queue#offer(Object)}
	 * 
	 * @see Event
	 * 
	 * @see EventPump
	 * 
	 * @see Queue
	 * 
	 */
	@Override
	public final boolean submit(T task)
	{
		return events.offer(task);
	}
}
