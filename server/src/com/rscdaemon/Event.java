package com.rscdaemon;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public abstract class Event
	implements
		Delayed,
		Runnable
{
	private boolean cancelled;
	long submitted;
	private final long delay;
	
	@Override
	public final int compareTo(Delayed event)
	{
		return (int)(this.getDelay(TimeUnit.MILLISECONDS) - event.getDelay(TimeUnit.MILLISECONDS));
	}
	
	@Override
	public final long getDelay(TimeUnit unit)
	{
		return submitted - System.currentTimeMillis() + delay;
	}
	
	public Event(long delay)
	{
		this.submitted = System.currentTimeMillis();
		this.delay = delay;
		cancelled = false;
	}
	
	public final boolean isCancelled()
	{
		return cancelled;
	}
	
	public final void cancel()
	{
		if(!cancelled)
		{
			cancelled = true;
			onCancellation();
		}
	}
	
	protected void onCancellation() { }
}
