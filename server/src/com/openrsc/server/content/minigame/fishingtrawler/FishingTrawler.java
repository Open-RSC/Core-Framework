package com.openrsc.server.content.minigame.fishingtrawler;

import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.model.world.Area;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.RegionManager;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.SimpleSubscriber;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FishingTrawler extends DelayedEvent {
	
	public static final Point SPAWN_LAND = new Point(538, 703);
	public static final int MAX_PLAYERS = 10;
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int SHIP_WATER_LIMIT_SECOND_BOAT = 500;
	private static final int SHIP_WATER_LIMIT_SINK = 1000;
	
	private static final Point SPAWN_EAST_FAIL = new Point(254, 759);
	private static final Point SPAWN_WEST_FAIL = new Point(302, 759);
	
	private final int LEAK1 = 1077;
	private final int LEAK2 = 1071;
	
	private Area shipArea;
	private Point spawnLocation;
	private Area shipAreaWater;
	private Point shipAreaWaterSpawn;
	private TrawlerBoat boat;
	private Point spawnFail;
	
	private boolean netBroken = false;
	private int waterLevel;
	private int fishCaught = 0;
	private int timeTillReturn = 0;
	private int ticksTillNextLeak = 6;
	private volatile State currentStage = State.STANDBY;
	private int currentCleanTries;

	private GameObject[] leaks = new GameObject[14];
	private CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<Player>();
	private CopyOnWriteArrayList<Player> unteledPlayers = new CopyOnWriteArrayList<Player>();
	private String[] murphys_messages_ship1 = new String[]{"That's the stuff, fill those holes",
		"it's a fierce sea today traveller", "check those nets"};
	private String[] murphys_messages_ship2 = new String[]{"we're going under", "we'll all end up in a watery grave",
		"check those nets"};
	
	private List<SimpleSubscriber<FishingTrawler>> subscribers = new ArrayList<SimpleSubscriber<FishingTrawler>>();
	
	public FishingTrawler(TrawlerBoat selectedBoat) {
		super(null, 600, "Fishing Trawler Event");
		
		if (selectedBoat == TrawlerBoat.EAST) {
			shipArea = new Area(270, 278, 740, 744, "FishingTrawler: Fine");
			spawnLocation = new Point(272, 742);
			
			setShipAreaWater(new Area(245, 253, 727, 731, "FishingTrawler: Water"));
			shipAreaWaterSpawn = new Point(251, 729);
			spawnFail = SPAWN_EAST_FAIL;
		}
		else {
			shipArea = new Area(318, 326, 740, 744, "FishingTrawler: Fine");
			spawnLocation = new Point(320, 742);
			
			setShipAreaWater(new Area(293, 301, 727, 731, "FishingTrawler: Water"));
			shipAreaWaterSpawn = new Point(299, 729);
			spawnFail = SPAWN_WEST_FAIL;
		}
		currentCleanTries = 0;
		boat = selectedBoat;
	}
	
	public TrawlerBoat getBoat() {
		return boat;
	}
	
	// fairness rule, if 4 minutes is remaining just play with current players
	public boolean isAvailable() {
		return this.currentStage == State.STANDBY || (this.currentStage == State.FIRST_SHIP &&
				timeTillReturn >= 400 && players.size() < MAX_PLAYERS);
	}
	
	public boolean register(SimpleSubscriber<FishingTrawler> subscriber) {
		return subscribers.add(subscriber);
	}
	
	private void unregisterAll() {
		subscribers.clear();
	}
	
	@Override
	public void run() {
		try {
			if (this.currentStage == State.CLEANUP) {
				Iterator<Player> it = unteledPlayers.iterator();
				Player aPlayer;
				// attempt to teleport any players
				// who had not been able to get teleported from stage 2
				currentCleanTries++;
				while (it.hasNext()) {
					aPlayer = it.next();
					try {
						aPlayer.setLocation(spawnFail, true);
						unteledPlayers.remove(aPlayer);
					}
					catch(RuntimeException e) {
					}
				}
				
				// safe to clean up
				// or maximum attempts to teleport remaining players exceeded
				// finalize
				if (unteledPlayers.size() == 0 || currentCleanTries > 5) {
					unteledPlayers.clear();
					resetGame();
					stop();
					subscribers.parallelStream().forEach(subscriber -> {
	                    subscriber.update(this);
	                });
					unregisterAll();
				}
				return;
			}
			if (this.currentStage == State.STANDBY) {
				//handled on add player
			}
			else {
				Iterator<Player> iter = players.iterator();
				Player pl;
				// sweep players to see if they are still there
				while (iter.hasNext()) {
					synchronized (this) {
						pl = iter.next();
						if (!pl.isLoggedIn() || pl.isRemoved()) {
							try {
								disconnectPlayer(pl, false);
							}
							catch(RuntimeException e) {
							}
						}
					}
				}
				// players logged out or left
				if (players.size() == 0) {
					resetGame();
				}
				else if (currentStage == State.FIRST_SHIP) {
					if (getWaterLevel() >= SHIP_WATER_LIMIT_SECOND_BOAT) {
						for (Player p : players) {
							p.message("the boats full of water");
							p.message("it's sinking!");
							// this may fail, unknown why, if it does attempt to teleport players
							// in the other stage
							try {
								p.setLocation(shipAreaWaterSpawn, true);
							}
							catch(RuntimeException e) {
								unteledPlayers.add(p);
							}
						}
						for (int i = 0; i < leaks.length; i++) {
							if (leaks[i] != null) {
								World.getWorld().unregisterGameObject(leaks[i]);
								leaks[i] = null;
							}
						}
						currentStage = State.SECOND_SHIP;
					}
				} else if (currentStage == State.SECOND_SHIP) {
					Iterator<Player> it = unteledPlayers.iterator();
					Player aPlayer;
					// attempt to teleport any players
					// who had not been able to get teleported from stage 1
					while (it.hasNext()) {
						aPlayer = it.next();
						try {
							aPlayer.setLocation(shipAreaWaterSpawn, true);
							unteledPlayers.remove(aPlayer);
						}
						catch(RuntimeException e) {
						}
					}
					if (getWaterLevel() >= SHIP_WATER_LIMIT_SINK) {
						for (Player p : players) {
							p.message("the boats gone under");
							p.message("you're lost at sea!");
							// defensive code, in case this teleport fails
							// attempt to teleport in the cleanup stage
							try {
								p.setLocation(spawnFail, true);
							}
							catch(RuntimeException e) {
								unteledPlayers.add(p);
							}
							ActionSender.hideFishingTrawlerInterface(p);
						}
						resetGame();
					}
				}
				cleanRemovedLeaks();
				if (ticksTillNextLeak-- <= 0) {
					int minimumLeaks = 4;
					int maximumLeaks = 5 + players.size();
					if (players.size() == 2) {
						minimumLeaks = 2;
						maximumLeaks = 6;
					} else if (players.size() == 1) {
						minimumLeaks = 1;
						maximumLeaks = 5;
					}
					int newLeakMax = DataConversions.random(minimumLeaks, maximumLeaks);
					createLeaks(newLeakMax);
					catchFish();
					netBreak();
					murphySpeak();

					int minimumBreak = 5 - (players.size() / 2);
					int maximumBreak = 10;
					if (players.size() == 2) {
						minimumBreak = 8;
						maximumBreak = 15;
					} else if (players.size() == 1) {
						minimumBreak = 15;
						maximumBreak = 24;
					}

					ticksTillNextLeak = DataConversions.random(minimumBreak, maximumBreak);
				}
				setWaterLevel(getWaterLevel() + ((int) (getLeakCount())));
				updateInterfaces();
				if (timeTillReturn-- <= 0) {
					endGame();
				}
			}
		} catch (Exception e) {
			LOGGER.error("Fishing Trawler ERROR:");
			LOGGER.catching(e);
		}
	}

	private void updateInterfaces() {
		int minutesLeft = timeTillReturn / 100;// think its correct?
		for (Player p : players) {
			ActionSender.updateFishingTrawler(p, waterLevel, minutesLeft, fishCaught, isNetBroken());
		}
	}

	private void cleanRemovedLeaks() {
		for (int leakIndex = 0; leakIndex < leaks.length; leakIndex++) {
			GameObject leak = leaks[leakIndex];
			if (leak != null && leak.isRemoved()) {
				leaks[leakIndex] = null;
			}
		}
	}

	private void catchFish() {
		if (!isNetBroken() && DataConversions.random(0, 1) == 0) {
			fishCaught += DataConversions.random(0, players.size() + 3);
		}
	}

	private void netBreak() {
		if (DataConversions.random(0, 100) >= 75) {
			setNetBroken(true);
			for (Player p : players) {
				ActionSender.sendBox(p,
					"@red@ The trawler net is damaged% %you cannot catch any fish with a damaged net",
					false);
			}
		}
	}

	void murphySpeak() {
		Npc npc = null;
		String message = "";
		if (currentStage == State.FIRST_SHIP) {
			npc = World.getWorld().getNpc(734, shipArea.getMinX(), shipArea.getMaxX(), shipArea.getMinY(),
				shipArea.getMaxY());
			message = murphys_messages_ship1[DataConversions.random(0, murphys_messages_ship1.length - 1)];
		} else if (currentStage == State.SECOND_SHIP) {
			npc = World.getWorld().getNpc(734, getShipAreaWater().getMinX(), getShipAreaWater().getMaxX(),
				getShipAreaWater().getMinY(), getShipAreaWater().getMaxY());
			message = murphys_messages_ship2[DataConversions.random(0, murphys_messages_ship2.length - 1)];
		}
		if (npc != null) {
			npc.getUpdateFlags().setChatMessage(new ChatMessage(npc, message, null));
		}
	}

	private void endGame() {
		if (players.size() == 0 || fishCaught == 0) {
			// no reward
		}
		else {
			// x2 since about half will be filled with junk
			int rewardForEach = 2 * fishCaught / players.size();
			for (Player p : players) {
				ActionSender.sendBox(p,
					"@yel@you have trawled a full net% %@yel@It's time to go back in and inspect the catch", false);
				p.message("murphy turns the boat towards shore");
				p.setLocation(SPAWN_LAND, true);
				p.getCache().set("fishing_trawler_reward", rewardForEach);
				ActionSender.hideFishingTrawlerInterface(p);
			}
		}
		players.clear();
		resetGame();
		this.currentStage = State.CLEANUP;
	}

	public void start() {
		timeTillReturn = DataConversions.random(5,12) * 100;
		currentStage = State.FIRST_SHIP;
	}

	public void resetGame() {
		setFishCaught(0);
		setTimeTillReturn(-1);
		setWaterLevel(0);
		setNetBroken(false);

		for (int i = 0; i < leaks.length; i++) {
			if (leaks[i] != null) {
				World.getWorld().unregisterGameObject(leaks[i]);
				leaks[i] = null;
			}
		}
		players.clear();
	}

	public boolean isNetBroken() {
		return netBroken;
	}

	public void setNetBroken(boolean netBroken) {
		this.netBroken = netBroken;
	}

	public int getWaterLevel() {
		return waterLevel;
	}

	public void setWaterLevel(int waterLevel) {
		this.waterLevel = waterLevel;
	}

	public Area getShipArea() {
		return shipArea;
	}

	public void setShipArea(Area shipArea) {
		this.shipArea = shipArea;
	}

	public void createLeaks(int count) {
		for (int i = 0; i < count; i++) {
			int x = -1;
			int y = -1;

			if (currentStage == State.FIRST_SHIP) {
				x = DataConversions.random(shipArea.getMinX() + 1, shipArea.getMaxX() - 1);
				y = (DataConversions.random(0, 1) == 0 ? spawnLocation.getY() - 1 : spawnLocation.getY() + 1);
			} else if (currentStage == State.SECOND_SHIP) {
				x = DataConversions.random(shipAreaWater.getMinX() + 1, shipAreaWater.getMaxX() - 1);
				y = (DataConversions.random(0, 1) == 0 ? shipAreaWaterSpawn.getY() - 1 : shipAreaWaterSpawn.getY() + 1);
			}
			int freeLeakIndex = getFreeLeakIndex();
			/* The ship is leaking hardcore. */
			if (freeLeakIndex == -1) {
				break;
			} else if (RegionManager.getRegion(x, y).getGameObject(x, y) != null) {
				continue;
			}
			int southSide = currentStage == State.FIRST_SHIP ? spawnLocation.getY() - 1 : shipAreaWaterSpawn.getY() - 1;
			int northSide = currentStage == State.FIRST_SHIP ? spawnLocation.getY() + 1 : shipAreaWaterSpawn.getY() + 1;
			if (y == southSide) {
				GameObject newHoleNorth = new GameObject(new Point(x, y), LEAK1, 0, 0);
				World.getWorld().registerGameObject(newHoleNorth);
				leaks[getFreeLeakIndex()] = newHoleNorth;
			} else if (y == northSide) {
				GameObject newHoleSouth = new GameObject(new Point(x, y), LEAK1, 4, 0);
				World.getWorld().registerGameObject(newHoleSouth);
				leaks[getFreeLeakIndex()] = newHoleSouth;
			}
		}
	}

	public int getFreeLeakIndex() {
		for (int i = 0; i < leaks.length; i++) {
			if (leaks[i] == null) {
				return i;
			}
		}
		return -1;
	}

	public int getLeakCount() {
		int count = 0;
		for (int i = 0; i < leaks.length; i++) {
			if (leaks[i] != null) {
				count++;
			}
		}
		return count;
	}

	public int getFishCaught() {
		return fishCaught;
	}

	public void setFishCaught(int fishCaught) {
		this.fishCaught = fishCaught;
	}

	public int getTimeTillReturn() {
		return timeTillReturn;
	}

	public void setTimeTillReturn(int timeTillReturn) {
		this.timeTillReturn = timeTillReturn;
	}

	public State getStage() {
		return currentStage;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void bailWater() {
		if (this.currentStage == State.FIRST_SHIP) {
			waterLevel -= DataConversions.random(1, 3);
		}
		else if (this.currentStage == State.SECOND_SHIP) {
			waterLevel -= DataConversions.random(2, 4);
		}
		if (waterLevel < 0)
			waterLevel = 0;
	}
	
	public void addPlayer(Player p) {
		p.setLocation(spawnLocation, true);
		players.add(p);
		ActionSender.showFishingTrawlerInterface(p);
		if (this.currentStage == State.STANDBY) {
			start();
		}
	}
	
	public void disconnectPlayer(Player p, boolean fromAction) {
		players.remove(p);
		p.setLocation(spawnFail, true);
		if (fromAction) {
			ActionSender.hideFishingTrawlerInterface(p);
		}
	}

	//quitting players (by talking to Murphy) always got the west fail spawn
	//regardless of chosen boat
	public void quitPlayer(Player p) {
		players.remove(p);
		p.setLocation(SPAWN_WEST_FAIL, true);
		ActionSender.hideFishingTrawlerInterface(p);
	}
	
	public Area getShipAreaWater() {
		return shipAreaWater;
	}

	public void setShipAreaWater(Area shipAreaWater) {
		this.shipAreaWater = shipAreaWater;
	}
	
	public enum TrawlerBoat {
		WEST(0),
		EAST(1);
		private int id;
		
		TrawlerBoat(int id) {
			this.id = id;
		}
		
		public int id() {
			return this.id;
		}
	}
	
	public enum State {
		CLEANUP(-1),
		STANDBY(0),
		FIRST_SHIP(1),
		SECOND_SHIP(2);
		private int id;
		
		State(int id) {
			this.id = id;
		}
		
		public int id() {
			return this.id;
		}
	}

}
