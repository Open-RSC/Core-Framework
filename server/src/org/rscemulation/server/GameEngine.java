package org.rscemulation.server;

import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.mina.common.IoSession;
import org.rscemulation.server.logging.Logger;
import org.rscemulation.server.logging.model.PlayerLoginLog;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.World;
import org.rscemulation.server.net.RSCPacket;
import org.rscemulation.server.net.WebPacket;
import org.rscemulation.server.packetbuilder.RSCPacketBuilder;
import org.rscemulation.server.packethandler.PacketHandler;
import org.rscemulation.server.packethandler.web.WebPacketHandler;
import org.rscemulation.server.util.Pair;

public final class GameEngine
	extends
		Thread
{
	
	/// A queue of messages received from web connections
	final BlockingQueue<Pair<IoSession, WebPacket>> webMessageQueue =
			new LinkedBlockingQueue<Pair<IoSession, WebPacket>>();
	
	/// A queue of messages received from connected clients
	final BlockingQueue<Pair<IoSession, RSCPacket>> messageQueue =
			new LinkedBlockingQueue<Pair<IoSession, RSCPacket>>();

	/// A queue of messages received from the login service
	private final BlockingQueue<Player> playerLoads =
			new LinkedBlockingDeque<Player>();
		
	/**
	 * Adds a player to the load queue.
	 * <b>THIS METHOD WILL BE REMOVED WHEN THE SERVER IS EVENT DRIVEN!</b>
	 * 
	 * @param load the player to add
	 * 
	 */
	public final void addPlayerToLoadQueue(Player load)
	{
		playerLoads.add(load);
	}
	
	private void processLogins()
	{
		Player load = null;
		while((load = playerLoads.poll()) != null)
		{
			if(!load.getSession().isConnected())
			{
				continue;
			}
			if(World.getPlayerCount() < Config.MAX_PLAYERS)
			{		
				if(World.getPlayerByOwner(load.getAccount()) == null)
				{
					load.getSession().write(new RSCPacketBuilder().setBare(true).addByte((byte)LoginResponse.LOGIN_SUCCESS.ordinal()).toPacket());
					World.registerEntity(load);
					// Quick-Fix 3.9.2013 Fixes player login logging
					Logger.log(new PlayerLoginLog(load.getUsernameHash(), load.getAccount(), load.getIP(), (int)(System.currentTimeMillis() / 1000)));
					// Quick-Fix 3.9.2013
				}
				else
				{
					load.getSession().write(new RSCPacketBuilder().setBare(true).addByte((byte)LoginResponse.ACCOUNT_ALREADY_IN_USE.ordinal()).toPacket());
					load.getSession().close();
				}
			}
			else
			{
				load.getSession().write(new RSCPacketBuilder().setBare(true).addByte((byte)LoginResponse.SERVER_FULL.ordinal()).toPacket());
				load.getSession().close();
			}
		}
	}
	
	/// Handle all messages received since the last tick
	private void processIncomingPackets()
	{
		Pair<IoSession, RSCPacket> p = null;
		while((p = messageQueue.poll()) != null)
		{
			IoSession session = p.getFirst();
			RSCPacket message = p.getSecond();
			
			try
			{
				packetHandlers.get(message.getID()).handlePacket(message, session);
			}
			catch(Exception e)
			{
				/// All errors occurring within the engine should 'destroy' 
				/// the affected player (eventually, might cause issues).
				
				e.printStackTrace();
			}
		}
	}
	
	/// Handle all web messages received since the last tick
	private void processWebPackets()
	{
		Pair<IoSession, WebPacket> p = null;
		while((p = webMessageQueue.poll()) != null)
		{
			try
			{
				WebPacket message = p.getSecond();
				webPacketHandlers.get(message.getID()).handlePacket(p.getFirst(), message);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
		
	private TreeMap<Integer, PacketHandler> packetHandlers = new TreeMap<Integer, PacketHandler>();
	private TreeMap<Integer, WebPacketHandler> webPacketHandlers = new TreeMap<Integer, WebPacketHandler>();
	private ClientUpdater clientUpdater = new ClientUpdater();
	private DelayedEventHandler eventHandler = new DelayedEventHandler();
	
	private long lastSentClientUpdate = 0;
	private boolean running = true;
	
	public TreeMap<Integer, PacketHandler> getPacketHandlers() {
		return packetHandlers;
	}

	public TreeMap<Integer, WebPacketHandler> getWebHandlers() {
		return webPacketHandlers;
	}
	
	public GameEngine() {
		this.setPriority(Thread.MAX_PRIORITY);
	}

	public void run()
	{
		while (running)
		{
			try { Thread.sleep(50); } catch(InterruptedException ie) {}
			processLogins();
			processIncomingPackets();
			processWebPackets();
			eventHandler.doEvents();
			
			World.getWorld().getEventPump().run();
			try
			{
				// New 'DaemonScript' Event System
				World.getEventQueue().runEvents();
			}
			catch(Throwable t)
			{
				t.printStackTrace();
			}
			if (System.currentTimeMillis() - lastSentClientUpdate >= 650)
			{
				lastSentClientUpdate = System.currentTimeMillis();
				try
				{
					clientUpdater.updateClients();
				}
				catch(Exception e)
				{
					System.out.println("THIS IS A FATAL ERROR:");
					e.printStackTrace();
				}
			}
		}
	}
	
	public void emptyWorld()
	{
		for (Player p : World.getPlayers())
		{
			if (!p.isUnregistered())
			{
				World.unregisterEntity(p);
			}
		}
	}

	public void kill()
	{
		running = false;
	}
}