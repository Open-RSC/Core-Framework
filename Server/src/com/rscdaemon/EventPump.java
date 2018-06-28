package com.rscdaemon;


public interface EventPump<T extends Event>
	extends
		Runnable
{
	/**
	 * Attempts to add the provided {@link Event} to this 
	 * <code>EventPump</code>
	 * 
	 * @param task the <code>Event</code> to add
	 * 
	 * @return true if the provided <code>Event</code> was added, 
	 * otherwise false
	 * 
	 */
	boolean submit(T task);
}
