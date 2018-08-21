package org.openrsc.server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.mina.common.IoSession;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.PlayerLoginLog;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.World;
import org.openrsc.server.net.RSCPacket;
import org.openrsc.server.net.WebPacket;
import org.openrsc.server.packetbuilder.RSCPacketBuilder;
import org.openrsc.server.packethandler.PacketHandler;
import org.openrsc.server.packethandler.web.WebPacketHandler;
import org.openrsc.server.util.Pair;

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
			if(World.getPlayerCount() < Config.getMaxPlayers())
			{		
				if(World.getPlayerByOwner(load.getAccount()) == null)
				{
                    if(
                        Config.getMaxLoginsPerIp() <= 0 ||
                        (Config.getMaxLoginsPerIp() > 0 && World.getPlayersByIp(load.getIP()).size() < Config.getMaxLoginsPerIp())
                    )
                    {
                        load.getSession().write(new RSCPacketBuilder().setBare(true).addByte((byte)LoginResponse.LOGIN_SUCCESS.ordinal()).toPacket());
                        World.registerEntity(load);
                        Logger.log(new PlayerLoginLog(load.getUsernameHash(), load.getAccount(), load.getIP(), (int)(System.currentTimeMillis() / 1000)));
                    }
                    else
                    {
                        load.getSession().write(new RSCPacketBuilder().setBare(true).addByte((byte)LoginResponse.IP_ALREADY_IN_USE.ordinal()).toPacket());
                        load.getSession().close();
                    }
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
        
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
	
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
					System.out.println(dateFormat.format(date)+": " + "THIS IS A FATAL ERROR:");
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