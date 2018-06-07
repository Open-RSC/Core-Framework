package com.rscdaemon;

import java.util.Queue;
import java.util.concurrent.DelayQueue;

import org.openrsc.server.model.Player;

import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptVariable;
import com.rscdaemon.scripting.event.ChainableEvent;

public class EventQueue
{
	private final Queue<ChainableEvent> events;
	
	private ChainableEvent event;	

	public EventQueue()
	{
		events = new DelayQueue<>();
	}
	
	public final void runEvents()
	{
		while((event = events.poll()) != null)
		{
			if(!event.isCancelled())
			{
				try
				{
					event.run();
				}
				catch(Throwable t)
				{
					Script script = event.getScript();
					if(script != null)
					{
						((Player)script.__internal_get_variable(ScriptVariable.OWNER)).setScript(null);
						script.cancel();
						script.__internal_unbind_all();
					}
				}
			}
		}
	}
	
	public final boolean offer(ChainableEvent event)
	{
		((Event)event).submitted = System.currentTimeMillis();
		return events.offer(event);
	}
}
