package org.openrsc.server.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeMap;

import org.openrsc.server.ClientUpdater;
import org.openrsc.server.Config;
import org.openrsc.server.DelayedEventHandler;
import org.openrsc.server.Server;
import org.openrsc.server.ServerBootstrap;
import org.openrsc.server.database.game.Save;
import org.openrsc.server.entityhandling.locs.GameObjectLoc;
import org.openrsc.server.entityhandling.locs.NPCLoc;
import org.openrsc.server.event.DelayedEvent;
import org.openrsc.server.event.NpcAggressionEvent;
import org.openrsc.server.event.ShortEvent;
import org.openrsc.server.event.ShutdownEvent;
import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.GenericLog;
import org.openrsc.server.model.auctions.AuctionHouse;
import org.openrsc.server.net.WorldLoader;
import org.openrsc.server.npchandler.NpcHandler;
import org.openrsc.server.states.CombatState;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.util.EntityList;
import org.openrsc.server.util.Formulae;

import com.rscdaemon.DefaultEventPump;
import com.rscdaemon.Event;
import com.rscdaemon.EventPump;
import com.rscdaemon.EventQueue;
import com.rscdaemon.Instance;
import com.rscdaemon.scripting.Script;
import com.rscdaemon.scripting.ScriptManager;
import com.rscdaemon.util.IPTracker;
import com.rscdaemon.util.IPTrackerPredicate;
import com.rscdaemon.util.impl.ThreadSafeIPTracker;

public final class World
	implements
		Instance
{
	private final static World instance = new World();
	
	public final static World getWorld()
	{
		return instance;
	}
	
	private final EventPump<Event> eventPump =
			new DefaultEventPump<>();
			
	@Override
	public final EventPump<Event> getEventPump()
	{
		return eventPump;
	}
	
	private final static ScriptManager scriptManager = new ScriptManager();

	public final static ScriptManager getScriptManager()
	{
		return scriptManager;
	}
	
	private static final EventQueue events = new EventQueue();
	
	public static final EventQueue getEventQueue()
	{
		return events;
	}
	
	public static final int MAX_WIDTH = 944;
	public static final int MAX_HEIGHT = 3776;

	private static Zone[][] zones = new Zone[1 + MAX_WIDTH / 48][1 + MAX_HEIGHT / 48];

	public static Zone[][] getZones() {
		return zones;
	}
	
	/**
	 * The amount of points blue team has currently in CTF
	 */
	public static int BLUECTF_POINTS = 0;
	/**
	 * The amount of points red team has currently in CTF
	 */
	public static int REDCTF_POINTS = 0;
	/**
	 * Checks if the redflag is in use
	 */
	public static int redFlagInUse = 0;
	/**
	 * Checks if the blueflag is in use
	 */
	public static int blueFlagInUse = 0;
	
	/** IP filtering for wilderness entry */
	
	private final static IPTracker<String> wildernessIPTracker = new ThreadSafeIPTracker<String>();
	
	private AuctionHouse auctionHouse = new AuctionHouse();
	
	public AuctionHouse getAuctionHouse() {
		return auctionHouse;
	}

	public void setAuctionHouse(AuctionHouse auctionHouse) {
		this.auctionHouse = auctionHouse;
	}
	
	public static IPTracker<String> getWildernessIPTracker()
	{
		return wildernessIPTracker;
	}
	
	/** IP filtering for wilderness entry */

	public static List<Zone> getUpdateZone(int x, int y) {
		List<Zone> updateZones = new ArrayList<Zone>();
		try {
			int xIndex = x / 48;
			int yIndex = y / 48;
			updateZones.add(zones[xIndex][yIndex]);
			int xModifier = x % 48;
			int yModifier = y % 48;
			if (xModifier > 28) {
				if (xIndex < 17)
					updateZones.add(zones[xIndex + 1][yIndex]);
				if (yModifier < 20 && yIndex > 0) {
					updateZones.add(zones[xIndex][yIndex - 1]);
					if (xIndex < 17)
						updateZones.add(zones[xIndex + 1][yIndex - 1]);
				} else if(yModifier > 27 && yIndex < 76) {
					updateZones.add(zones[xIndex][yIndex + 1]);
					if(xIndex < 17)
						updateZones.add(zones[xIndex + 1][yIndex + 1]);
				}
			} else if(xModifier < 20) {
				if (xIndex > 0)
					updateZones.add(zones[xIndex - 1][yIndex]);
				if (yModifier < 20 && yIndex > 0) {
					updateZones.add(zones[xIndex][yIndex - 1]);
					if(xIndex > 0)
						updateZones.add(zones[xIndex - 1][yIndex - 1]);
				} else if(yModifier > 28 && yIndex < 77) {
					updateZones.add(zones[xIndex][yIndex + 1]);
					if(xIndex > 0)
						updateZones.add(zones[xIndex - 1][yIndex + 1]);
				}
			}
		} catch(Exception e) {
			Logger.log(new GenericLog("An error was encountered with World.getUpdateZone(int, int)", DataConversions.getTimeStamp()));
		}	
		return updateZones;
	}
	
	public static void addZone(int x, int y) {
		zones[x][y] = new Zone(x, y);
	}

	public static Zone getZone(int x, int y) {
		try {
			return zones[x / 48][y / 48];
		} catch(Exception e) {}
		return null;
	}

	public static boolean entityExists(Entity e) {
		if (e != null && e.getZone() != null)
			return e.getZone().entityExists(e);
		return false;
	}
	
	public static byte[][] mapValues = new byte[MAX_WIDTH][MAX_HEIGHT];
	public static byte[][] objectValues = new byte[MAX_WIDTH][MAX_HEIGHT];
	public static byte[][] mapSIDValues = new byte[MAX_WIDTH][MAX_HEIGHT];
	public static byte[][] mapNIDValues = new byte[MAX_WIDTH][MAX_HEIGHT];
	public static byte[][] mapEIDValues = new byte[MAX_WIDTH][MAX_HEIGHT];
	public static byte[][] mapWIDValues = new byte[MAX_WIDTH][MAX_HEIGHT];
	public static byte[][] mapDIDValues = new byte[MAX_WIDTH][MAX_HEIGHT];
	public static byte[][] groundOverlayValues = new byte[MAX_WIDTH][MAX_HEIGHT];
	
	private static EntityList<Player> players = new EntityList<Player>(2000);
	
	private static EntityList<Npc> npcs = new EntityList<Npc>(4000);
	
	private static ClientUpdater clientUpdater;
	
	private static DelayedEventHandler delayedEventHandler;
	
	private static ArrayList<Shop> shops = new ArrayList<Shop>();
	
	private static TreeMap<Integer, NpcHandler> npcHandlers = new TreeMap<Integer, NpcHandler>();
	
	public static boolean global = true, dueling = true;

	private static WorldLoader worldLoader;
	public static boolean eventRunning = false;
    public static boolean pvpEnabled = true;
	public static boolean safeCombat = false;
	public static boolean muted = false;
	public static Point eventPoint = null;
	public static int eventLow, eventHigh;
		
	private static long wildernessCountdown = 0;	
	private static byte wildernessSwitchType = (byte)0;

	private static boolean changingWildernessState;
	public static boolean wildernessP2P = false;

	
	public static boolean joinEvent(Player player) {
		if (player.getCombatLevel() <= eventHigh && player.getCombatLevel() >= eventLow) {
			if (eventPoint.inWilderness()) {
				player.sendAlert("This event is in the @red@wilderness@whi@ meaning it's unsafe and you could lose everything you have in your inventory. Are you sure that you want to teleport to the event?");
				World.getDelayedEventHandler().add(new ShortEvent(player) {
					public void action() {
						String[] options = new String[]{"Yes, teleport me to the event", "No, don't teleport me to the event"};
						owner.setMenuHandler(new MenuHandler(options) {
							public void handleReply(final int option, final String reply) {
								switch (option) {
									case 0:
										owner.teleport(eventPoint, true);
									break;																	
								}
							}
						});
						owner.sendMenu(options);
					}
				});				
			} else
				player.teleport(eventPoint, true);
			return true;
		}
		return false;
	}
	
	public static void setEvent(int x, int y) {
		if (!World.withinWorld(x, y)) {
			eventLow = -1;
			eventHigh = -1;
			eventRunning = false;
			eventPoint = null;
		} else {
			eventRunning = true;
			eventPoint = Point.location(x, y);
		}
	}

	public static WorldLoader getWorldLoader() {
		return worldLoader;
	}
	
	public static void load() throws SQLException {	
		worldLoader = new WorldLoader();
		worldLoader.loadWorld();
		scriptManager.loadListeners("com.rscdaemon.scripting.listener");
		scriptManager.loadScripts("com.rscdaemon.scripts");
		//minuteChecks(); Disabling autorestart, weaken/godspell day changes
	}
		
	private static void minuteChecks() 
	{
		World.getDelayedEventHandler().add(new SingleEvent(null, 60000) 
		{
			public void action() 
			{
				if ((System.currentTimeMillis() - Config.START_TIME) > 54000000) 
				{
					World.getWorld().getEventPump().submit(new ShutdownEvent(true, "Auto Restart"));
					global = false;
				} 
				else 
				{
					if (!World.isWildernessChanging())
					{
						Calendar c = Calendar.getInstance();
						int day_of_week = c.get(Calendar.DAY_OF_WEEK);
						
						switch (day_of_week)
						{
							case 1:
								Config.ALLOW_WEAKENS = true;
								
								if (World.isP2PWilderness())
									toggleWilderness();
							break;
							
							case 2:
								Config.ALLOW_GODSPELLS = true;
								Config.ALLOW_WEAKENS = false;
								
								if (!World.isP2PWilderness())
									toggleWilderness();
							break;
							
							case 3:
								Config.ALLOW_WEAKENS = true;
								
								if (World.isP2PWilderness())
									toggleWilderness();
							break;
							
							case 4:
								Config.ALLOW_WEAKENS = false;
								
								if (World.isP2PWilderness())
									toggleWilderness();
							break;
							
							case 5:
								Config.ALLOW_GODSPELLS = false;
								Config.ALLOW_WEAKENS = false;

								
								if (!World.isP2PWilderness())
									toggleWilderness();
							break;
							
							case 6:
								Config.ALLOW_WEAKENS = false;
								
								if (World.isP2PWilderness())
									toggleWilderness();
							break;
							
							case 7:
								Config.ALLOW_WEAKENS = true;
								
								if (World.isP2PWilderness())
									toggleWilderness();
							break;
							
							default:
								
						}
						
					}
					minuteChecks();
				}
			}
		});
	}
	
	public static void toggleWilderness() {
		if (!World.isWildernessChanging()) {
			for (Player p : getPlayers()) {
				p.sendGraciousAlert("The wilderness state will change to " + (wildernessP2P ? "F2P" : "P2P") + " in 30 seconds!" + (wildernessP2P ? " After the time is up you will no longer be able to eat P2P food, drink P2P potions, use P2P armour/weapons or cast P2P spells." : ""));
				p.startWildernessUpdate(30, (byte)(wildernessP2P ? 0 : 1));
				setWildernessSwitchType((byte)(wildernessP2P ? 0 : 1));
				setWildernessCountdown(System.currentTimeMillis() + 30000);
			}
			World.getDelayedEventHandler().add(new SingleEvent(null, 30000) {
				public void action() {
					setWildernessCountdown(0);
					wildernessP2P = !wildernessP2P;
					for (Player p : getPlayers()) {
						p.sendMessage(Config.PREFIX + "The wilderness state has been changed to: @gre@" + (wildernessP2P ? "P2P" : "F2P"));
						if (!wildernessP2P) {
							if (p.getLocation().inWilderness()) {
								for (InvItem currentItem : p.getInventory().getItems()) {
									if (currentItem.isWielded() && currentItem.getDef().isP2P()) {
										currentItem.setWield(false);
										p.updateWornItems(currentItem.getWieldableDef().getWieldPos(), p.getPlayerAppearance().getSprite(currentItem.getWieldableDef().getWieldPos()));
										p.sendSound("click", false);
										p.sendEquipmentStats();
										p.sendInventory();
									}
								}
								for (int i = 0; i < 5; i++) {
									if (p.getCurStat(i) > p.getMaxStat(i)) {
										p.setCurStat(i, p.getMaxStat(i));
										p.sendStat(i);
									}
								}
							}
						}
					}
				}
			});				
		}
	}
	
	public static void registerEntity(final Entity e, int delay) {
		if (e instanceof Player)
			registerPlayer((Player)e);
		else if (e instanceof Npc)
			registerNpc((Npc)e);
		else if (e instanceof Item)
			registerItem((Item)e);
		else if ((e instanceof GameObject)) {
			GameObject object = (GameObject)e;
			if (object.getType() == 0) {
				GameObject oldObject = getZone(e.getX(), e.getY()).getObjectAt(e.getX(), e.getY());
				if (oldObject != null)
					unregisterEntity(oldObject);
				registerObject(object);
			} else if (object.getType() == 1) {
				GameObject oldDoor = getZone(e.getX(), e.getY()).getDoorAt(e.getX(), e.getY());
				if (oldDoor != null)
					unregisterEntity(oldDoor);
				registerDoor(object);
			}
		}
		setLocation(e, null, e.getLocation());
		e.register();
		if (delay > -1) {
			delayedEventHandler.add(new SingleEvent(null, delay) {
				public void action() {
					unregisterEntity(e);
				}
			});
		}
	}

	public static void registerEntity(Entity e) {
		registerEntity(e, -1);
	}
	
	private static void unregisterPlayer(final Player p) {
		synchronized(delayedEventHandler.getEvents()) {
			for (DelayedEvent event : delayedEventHandler.getEvents()) {
				if (event.belongsTo(p)) {
					if(event instanceof NpcAggressionEvent) {
						Npc npc = ((NpcAggressionEvent)(event)).getNpc();
						if (npc != null)
							npc.removeAggression();
					}
					event.stop();
				}
			}
		}
		Player ranger = World.getPlayer(p.getRanger());
		if(ranger != null) {
			ranger.resetRange();
			ranger.clearTarget();
		}
		if (p.getWishToTrade() != null)
			p.getWishToTrade().resetTrading();
		if (p.getWishToDuel() != null)
			p.getWishToDuel().resetDueling();

		// Clean up the active script
		Script script = p.getScript();
		if(script != null)
		{
			script.cancel();
		}
		p.setScript(null);
		
		
		p.unregister();
		p.setLoggedIn(false);
		p.sendLogout();
		players.remove(p);
		
		/** Wilderness IP Filter access point */
		if(p.getLocation().inWilderness())
		{
			wildernessIPTracker.remove(p.getIP());
		}
		/** Wilderness IP Filter access point */
		
		/** CTF Unregister Player **/
		if (p.getLocation().inCtf())
		{
			p.removeFromCtf(p);
		}
		/** CTF Unregister Player **/
		
		setLocation(p, p.getLocation(), null);
		p.destroy(true);
		Mob opponent = p.getOpponent();
		if (opponent != null) {
			p.resetCombat(CombatState.ERROR);
			opponent.resetCombat(CombatState.ERROR);
		}
		Npc interacting = p.getNpc();
		if (interacting != null)
			interacting.unblock();	
		for(Player friend : getPlayers()) {
			if (friend != null && (friend.isFriendsWith(p.getUsernameHash()) && p.getPrivacySetting(1)) || (friend.isFriendsWith(p.getUsernameHash()) && p.isFriendsWith(friend.getUsernameHash())))
				friend.sendFriendUpdate(p.getUsernameHash(), (byte)0);
		}
		Save s = new Save(p);
		ServerBootstrap.getDatabaseService().submit(s, s.new DefaultSaveListener());
	}

	private static void registerPlayer(final Player player) {
		try {
			/** Wilderness IP Filter access point */
			if(player.getLocation().inWilderness())
			{
				if(!wildernessIPTracker.add(
					player.getIP(),
					new IPTrackerPredicate()
					{
						@Override
						public boolean proceedIf()
						{
							return wildernessIPTracker.ipCount(player.getIP())
									< Config.ALLOWED_CONCURRENT_IPS_IN_WILDERNESS;
						}
					}
				))
				{
					player.onWildernessEntryBlocked();
					player.getSession().close();
					return;
				}
			}
			/** Wilderness IP Filter access point */
				for(Player p : players) {
					try {
						if (p.getAccount() == player.getAccount()) {
							player.getSession().close();
							return;
						}
					} catch(Exception e) {
						System.out.println("Exception caught with null owner");
						return;
					}
				}
			if (!players.contains(player)) {
				player.setInitialized();
				players.add(player);
				//System.out.println("Added:   " + player.getUsername());
				player.load();
			} else {
				player.getSession().close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isWildernessChanging() {
		return changingWildernessState;
	}
	
	public static byte getWildernessSwitchType() {
		return wildernessSwitchType;
	}
	
	public static void setWildernessSwitchType(byte type) {
		wildernessSwitchType = type;
	}
	
	public static long getWildernessCountdown() {
		return wildernessCountdown;
	}
	
	public static void setWildernessCountdown(long countdown) {
		wildernessCountdown = countdown;
		if (wildernessCountdown == 0)
			changingWildernessState = false;
		else
			changingWildernessState = true;
	}
	
	public static boolean isP2PWilderness() {
		return wildernessP2P;
	}
	
	public static TreeMap<Integer, NpcHandler> getNpcHandlers() {
		return npcHandlers;
	}
	
	public static NpcHandler getNpcHandler(int npcID) {
		return npcHandlers.get(npcID);
	}
	
	public static void registerShop(final Shop shop) {
		shop.setEquilibrium();
		shops.add(shop);
		shop.initRestock();
		shop.initReduction();
	}
	
	public static List<Shop> getShops() {
		return shops;
	}
	
	public static Shop getShop(int id) {
		for (Shop shop : shops) {
			if (shop.getID() == id)
				return shop;
		}
		return null;
	}
	
	public static Shop getShop(Point location) {
		for (Shop shop : shops) {
			if (shop.withinShop(location))
				return shop;
		}
		return null;
	}
	
	public static void setClientUpdater(ClientUpdater updater) {
		clientUpdater = updater;
	}
	
	public static void setDelayedEventHandler(DelayedEventHandler handler) {
		delayedEventHandler = handler;
	}
	
	public static ClientUpdater getClientUpdater() {
		return clientUpdater;
	}
	
	public static DelayedEventHandler getDelayedEventHandler() {
		return delayedEventHandler;
	}
	
	public static boolean withinWorld(int x, int y) {
		return x >= 0 && x < MAX_WIDTH && y >= 0 && y < MAX_HEIGHT;
	}
	
	public static void delayedSpawnObject(final GameObjectLoc loc, final int respawnTime) {
		delayedEventHandler.add(new SingleEvent(null, respawnTime) {
			public void action() {
				registerEntity(new GameObject(loc));
			}
		});
	}
	
	public static void delayedRemoveObject(final GameObject object, final int delay) {
		delayedEventHandler.add(new SingleEvent(null, delay) {
			public void action() {
				if (entityExists(object))
					unregisterEntity(object);
			}
		});
	}
	
	private static void registerNpc(Npc n) {
		NPCLoc npc = n.getLoc();
		if (npc.startX < npc.minX || npc.startX > npc.maxX || npc.startY < npc.minY || npc.startY > npc.maxY || (World.mapValues[npc.startX][npc.startY] & 64) != 0)
			System.out.println("Broken NPC: " + npc.id + " " + npc.startX + " " + npc.startY);
		npcs.add(n);
	}
	
	public static boolean isLoggedIn(long hash) {
		Player friend = getPlayer(hash);
		if (friend != null)
			return friend.loggedIn();
		return false;
	}
	
	public static void registerObject(GameObject o) {
		if (o.getGameObjectDef().getType() != 1 && o.getGameObjectDef().getType() != 2)
			return;
		int dir = o.getDirection();
      		int width, height;
      		if (dir == 0 || dir == 4) {
      			width = o.getGameObjectDef().getWidth();
      			height = o.getGameObjectDef().getHeight();
      		} else {
      			height = o.getGameObjectDef().getWidth();
      			width = o.getGameObjectDef().getHeight();
      		}
		for (int x = o.getX();x < o.getX() + width;x++) {
			for (int y = o.getY();y < o.getY() + height;y++) {		
				if (o.getGameObjectDef().getType() == 1)
					objectValues[x][y] |= 0x40;
				else if (dir == 0) {
					objectValues[x][y] |= 2;
					objectValues[x - 1][y] |= 8;
				} else if (dir == 2) {
					objectValues[x][y] |= 4;
					objectValues[x][y + 1] |= 1;
				} else if (dir == 4) {
					objectValues[x][y] |= 8;
					objectValues[x + 1][y] |= 2;
				} else if (dir == 6) {
					objectValues[x][y] |= 1;
					objectValues[x][y - 1]|= 4;
				}
			}
		}
	}
	
	private static void registerDoor(GameObject o) {
		if (o.getDoorDef().getDoorType() != 1)
			return;
		
		int dir = o.getDirection();
		int x = o.getX(), y = o.getY();
		
		if (dir == 0) {
			objectValues[x][y] |= 1;
			objectValues[x][y - 1] |= 4;
		} else if (dir == 1) {
			objectValues[x][y]|= 2;
			objectValues[x - 1][y] |= 8;
		} else if (dir == 2)
			objectValues[x][y]|= 0x10; // 16
		else if (dir == 3)
			objectValues[x][y] |= 0x20; //32
	}
	
	private static void unregisterObject(GameObject o) {
		if (o.getGameObjectDef().getType() != 1 && o.getGameObjectDef().getType() != 2)
			return;
		
		int dir = o.getDirection();
		int width, height;
		
		if (dir == 0 || dir == 4) {
			width = o.getGameObjectDef().getWidth();
			height = o.getGameObjectDef().getHeight();
		} else {
			height = o.getGameObjectDef().getWidth();
			width = o.getGameObjectDef().getHeight();
		}
		
		for (int x = o.getX();x < o.getX() + width;x++) {
			for (int y = o.getY();y < o.getY() + height;y++) {
				if (o.getGameObjectDef().getType() == 1)
					objectValues[x][y] &= 0xffbf;
				else if (dir == 0) {
					objectValues[x][y] &= 0xfffd;
					objectValues[x - 1][y] &= 65535 - 8;
				} else if (dir == 2) {
					objectValues[x][y] &= 0xfffb;
					objectValues[x][y + 1] &= 65535 - 1;
				} else if (dir == 4) {
					objectValues[x][y] &= 0xfff7;
					objectValues[x + 1][y] &= 65535 - 2;
				} else if (dir == 6) {
					objectValues[x][y] &= 0xfffe;
					objectValues[x][y - 1] &= 65535 - 4;
				}
			}
		}
	}
	
	private static void unregisterDoor(GameObject o) {
		if (o.getDoorDef().getDoorType() != 1)
			return;
		
		int dir = o.getDirection();
		int x = o.getX(), y = o.getY();
	
		if (dir == 0) {
			objectValues[x][y] &= 0xfffe;
			objectValues[x][y - 1] &= 65535 - 4;
		} else if (dir == 1) {
			objectValues[x][y] &= 0xfffd;
			objectValues[x - 1][y] &= 65535 - 8;
		} else if (dir == 2)
			objectValues[x][y] &= 0xffef;
		else if (dir == 3)
			objectValues[x][y] &= 0xffdf;
	}
	
	private static void registerItem(final Item i) {
  		if (i.getLoc() == null) {
  			delayedEventHandler.add(new DelayedEvent(null, 180000) { // 3 Minutes 180000
  				public void run() {
  					if (entityExists(i))
						unregisterEntity(i);
  					running = false;
  				}
  			});
  		}
	}

	public static void unregisterEntity(Entity e) {
		setLocation(e, e.getLocation(), null);
		
		if (e instanceof Player)
			unregisterPlayer((Player)e);
		else if (e instanceof Npc)
			unregisterNpc((Npc)e);
		else if (e instanceof Item)
			unregisterItem((Item)e);
		else if ((e instanceof GameObject)) {
			GameObject object = (GameObject)e;
			if (object.getType() == 0)
				unregisterObject(object);
			else if (object.getType() == 1)
				unregisterDoor(object);
		}
		
		e.unregister();
	}	
	
	private static void unregisterItem(Item i) {
		i.remove();		
	}
	
	public static void setLocation(Entity entity, Point oldPoint, Point newPoint) {
		if (oldPoint != null)
			entity.getZone().remove(entity);
		if (newPoint != null) {
			Zone newZone = getZone(newPoint.getX(), newPoint.getY());

			entity.setZone(newZone);
			newZone.add(entity);
			entity.setUpdateZone(getUpdateZone(newPoint.getX(), newPoint.getY()));
		}
	}	
	
	private static void unregisterNpc(Npc n) {
		if (hasNpc(n)) {
			npcs.remove(n);
		}
		setLocation(n, n.getLocation(), null);
	}
	
	public static EntityList<Player> getPlayers()
	{
		return players;
	}
	
	public static EntityList<Npc> getNpcs() {
		return npcs;
	}
	
	public static int countNpcs() {
		return npcs.size();
	}
	
	public static boolean hasNpc(Npc n) {
		return npcs.contains(n);
	}

	public static boolean hasPlayer(Player p) {
		return players.contains(p);
	}
	
	public static Player getPlayer(long hash) {
		for (Player player : players)
			if (player.getUsernameHash() == hash)
				return player;
		return null;
	}
	
	public static Player getPlayerByOwner(int owner) {
		for (Player player : players)
			if (player.getAccount() == owner)
				return player;
		return null;
	}	
    
    public static EntityList<Player> getPlayersByIp(String ip) {
        EntityList<Player> playersByIp = new EntityList();
        
		for (Player player : players)
			if (player.getIP().trim().equalsIgnoreCase(ip.trim()))
                playersByIp.add(player);
                
        return playersByIp;
    }
	
	public static Npc getNpc(int idx) {
		return npcs.get(idx);
	}
	
	public static Npc getNpc(int id, int minX, int maxX, int minY, int maxY) {
		for (Npc npc : npcs) {
			if (npc.getID() == id && npc.getX() >= minX && npc.getX() <= maxX && npc.getY() >= minY && npc.getY() <= maxY)
				return npc;
		}
		return null;
	}
	
	public static Npc getNpc(int x, int y, int radius) {
		int minX = x - radius;
		int maxX = x + radius;
		int minY = y - radius;
		int maxY = y + radius;
		for (Npc npc : npcs) {
			if (npc.getX() < maxX && npc.getX() > minX && npc.getY() < maxY && npc.getY() > minY)
				return npc;
		}
		return null;
	}
	
	public static Player getPlayer(int idx) {
		return players.get(idx);
	}
	
	public static Player getRandomPlayer() {
		return players.getRandomEntity();
	}
	
	public static int getPlayerCount() {
		return players.count();
	}

	/*
	 * Lottery Mini Game
	 */
	private static boolean lotteryRunning = false;
	private static int lotteryPrice, lotteryPot, lotteryNotify = 0;
	private static ArrayList<Player> lotteryEntries, lotterySent = new ArrayList<Player>();

	public static boolean lotteryRunning() {
	        return lotteryRunning;
	}

	public static void getLotteryPot(Player player) {
	        if (lotteryRunning)
	                player.sendMessage(Config.PREFIX + "@whi@ The lottery pot is now at @gre@" + DataConversions.insertCommas("" + lotteryPot) + " GP@whi@!");
	        else
	                player.sendMessage(Config.PREFIX + "@whi@ There's currently no lottery running");
	}

	public static void startLottery(int price) {
	        lotteryRunning = true;
	        lotteryPrice = price;
	        lotteryPot = lotteryNotify = 0;
	        lotteryEntries = lotterySent = new ArrayList<Player>();
	       
	        for (Player informee : World.getPlayers()) {
	                informee.sendNotification("@gre@Lottery:@whi@ Each entry is @gre@" + DataConversions.insertCommas("" + lotteryPrice) + " GP@whi@. Type @gre@::LOTTERY@whi@ to purchase a ticket!");
	                informee.sendNotification("@gre@Lottery:@whi@ Open RSC Lottery is now running!");
	        }               
	}

	public static void findLotteryWinner() {
	        Player p = null;
	        p = lotteryEntries.get(lotteryEntries.size() == 1 ? 0 : Formulae.rand(0, lotteryEntries.size() - 1));
	        if (p != null) {
	                InvItem coins = new InvItem(10);
	                coins.setAmount(lotteryPot);
	                p.getInventory().add(coins);
	                p.sendInventory();
	                p.sendAlert("Congratulations! You have won the lottery! % % @gre@" + DataConversions.insertCommas("" + lotteryPot) + " GP@whi@ has been added to your inventory. Thanks for playing!", false);

	                for (Player informee : World.getPlayers())
	                        informee.sendNotification("@gre@Lottery:@whi@ " + p.getUsername() + " has won the lottery and is now @gre@" + DataConversions.insertCommas("" + lotteryPot) + " GP@whi@ richer!");
	               
	                for (Player informee : lotteryEntries) {
	                        if (informee != p && !informee.getLocation().inWilderness() && !lotterySent.contains(informee)) {
	                                informee.sendAlert(p.getUsername() + " has won the lottery and is now @gre@" + DataConversions.insertCommas("" + lotteryPot) + " GP@whi@ richer! % % Thanks for playing and better luck next time.", false);
	                                lotterySent.add(informee);
	                        }
	                }
	                lotterySent = new ArrayList<Player>();                                               
	        }
	        return;
	}

	public static void stopLottery() {
	        if (lotteryEntries.size() > 0)
	                findLotteryWinner();               
	        else
	                for (Player informee : World.getPlayers())
	                        informee.sendNotification("@gre@Lottery:@whi@ The lottery has been stopped. No-one entered.");       
	        lotteryRunning = false;
	        lotteryPrice = lotteryPot = lotteryNotify = 0;
	        lotteryEntries = lotterySent = new ArrayList<Player>();               
	}       

	public static void buyTicket(Player player) {
	        if (player.getBank().countId(10) < lotteryPrice && player.getInventory().countId(10) < lotteryPrice) {
	                player.sendMessage(Config.PREFIX + "It seems that you don't have enough to buy a lottery ticket...");
	                player.sendMessage(Config.PREFIX + "Please ensure that you have @gre@" + DataConversions.insertCommas("" + lotteryPrice) + " GP@whi@ in your inventory or bank and try again");
	        } else if (player.getLocation().inWilderness())
	                player.sendMessage(Config.PREFIX + "You cannot enter the lottery whilst in the wilderness");
	        else if (player.isDueling())
	                player.sendMessage(Config.PREFIX + "You cannot enter the lottery whilst you are dueling");
	        else if (player.isTrading())
	                player.sendMessage(Config.PREFIX + "You cannot enter the lottery whilst you are trading");
	        else if (player.isBusy())
	                player.sendMessage(Config.PREFIX + "You cannot enter the lottery whilst you're set as busy");
	        else if (player.accessingBank())
	                player.sendMessage(Config.PREFIX + "You cannot enter the lottery whilst you're banking");
	        else if (player.isMod())
	                player.sendMessage(Config.PREFIX + "You cannot enter the lottery on a staff character");
	        else if (player.accessingShop())
	                player.sendMessage(Config.PREFIX + "You cannot enter the lottery whilst you're in a shop");
	        else {
	                if (player.getInventory().countId(10) >= lotteryPrice) {
	                        if (player.getInventory().remove(new InvItem(10, lotteryPrice)) == -1)
	                                return;
	                } else if (player.getBank().countId(10) >= lotteryPrice) {
	                        if (player.getBank().remove(10, lotteryPrice) == -1)
	                                return;
	                }
		            player.sendInventory();
	                lotteryPot += (lotteryPrice - (lotteryPrice / 4.5));
	                lotteryEntries.add(player);
	                player.sendAlert("You have successfully bought a lottery ticket for " + DataConversions.insertCommas("" + lotteryPrice) + " GP % % @red@Note:@whi@ If you logout during the lottery you will be removed from the draw with no refund!", false);
	                lotterySent = new ArrayList<Player>();
	                if (lotteryNotify < 10)
	                        lotteryNotify++;
	                else {
	                        for (Player informee : World.getPlayers())
	                                informee.sendNotification(Config.PREFIX + " The lottery pot is now at @gre@" + DataConversions.insertCommas("" + lotteryPot) + " GP@whi@! Type @gre@::LOTTERY@whi@ to enter.");                                       
	                        lotteryNotify = 0;
	                }                               
	        }               
	}
}