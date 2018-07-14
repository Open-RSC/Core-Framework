package org.openrsc.server.networking;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.rscdaemon.concurrent.ConfigurableThreadFactory;
import com.rscdaemon.concurrent.ConfigurableThreadFactory.ConfigurationBuilder;

public class ConnectionLimiter
{
	private final Map<String, Integer> connections = new HashMap<>();
	
	private final ScheduledExecutorService evictor = 
		Executors.newSingleThreadScheduledExecutor(
			new ConfigurableThreadFactory(
				new ConfigurationBuilder()
					.setName("ConnectionLimiter Evictor")
					.setPriority(Thread.MIN_PRIORITY)
					.setDaemon(true)
			)
		);

	private final int maxConnections;
	
	public ConnectionLimiter(int maxConnections, int duration)
	{
		this.maxConnections = maxConnections;
		evictor.scheduleAtFixedRate(
			new Runnable()
			{
				@Override
				public final void run()
				{
					synchronized(ConnectionLimiter.this)
					{
						for(Iterator<Map.Entry<String, Integer>> it = connections.entrySet().iterator(); it.hasNext();)
						{
							Map.Entry<String, Integer> entry = it.next();
							if(entry.getValue().intValue() == 1)
							{
								it.remove();
							}
							else
							{
								entry.setValue(entry.getValue().intValue() - 1);
							}
						}
					}
				}
			},
			duration,
			duration,
			TimeUnit.MILLISECONDS
		);
	}
	
	public synchronized boolean addConnection(String ip)
	{
		Integer count = connections.get(ip);
		if(count == null)
		{
			count = 0;
		}
		connections.put(ip, ++count);
		return count < maxConnections;
	}
	
}
