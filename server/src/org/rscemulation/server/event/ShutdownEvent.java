package org.rscemulation.server.event;

import org.rscemulation.server.Config;
import org.rscemulation.server.Server;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.World;

import com.rscdaemon.Event;

/**
 * A 'poison' event that shuts down the server
 * 
 * @author Zilent
 * 
 * @version 1.0
 * 
 * @since 3.4.0
 *
 */
public class ShutdownEvent
	extends
		Event
{

	private final boolean autoRestart;
	
	public ShutdownEvent(long delay, boolean autoRestart, String... messages)
	{
		super(delay);
		this.autoRestart = autoRestart;		
		for (Player p : World.getPlayers())
		{
			if (messages != null && messages.length >= 1 && messages[0].length() > 0)
			{
				p.sendGraciousAlert(Config.SERVER_NAME + " will be shutting down in " + (Config.SHUTDOWN_TIME_MILLIS / 1000) + " seconds: " + messages[0]);
			}
			p.startShutdown(Config.SHUTDOWN_TIME_MILLIS / 1000);
		}
	}
	
	/**
	 * Constructs a <code>ShutdownEvent</code>
	 * 
	 */
	public ShutdownEvent(boolean autoRestart, String... messages)
	{
		this(Config.SHUTDOWN_TIME_MILLIS, autoRestart, messages);
	}

	/**
	 * {@Override}
	 * 
	 */
	@Override
	public void run()
	{
		Server.getServer().shutdown(autoRestart);
	}
}
