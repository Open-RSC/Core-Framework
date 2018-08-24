package com.openrsc.server.content.minigame.fishingtrawler;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import com.openrsc.server.util.rsc.DataConversions;

public class FishingTrawler extends DelayedEvent {
	
	/**
     * The asynchronous logger.
     */
    private static final Logger LOGGER = LogManager.getLogger();

	public static final int WAITING = -1, FIRST_SHIP = 0, SECOND_SHIP = 1;

	private static final int SHIP_WATER_LIMIT_SECOND_BOAT = 500;
	private static final int SHIP_WATER_LIMIT_SINK = 1000;

	public static final Point SPAWN_LAND = new Point(538, 703);
	private static final Point spawnEastFail = new Point(254, 759);

	public static final int MAX_PLAYERS = 10;
	private final int LEAK1 = 1077; 
	private final int LEAK2 = 1071;

	private WaitingShip waitingShip;

	private Area shipArea;
	private Point spawnLocation;
	private Area shipAreaWater;
	private Point shipAreaWaterSpawn;

	private boolean netBroken = false;
	private int waterLevel;
	private int fishCaught = 0;
	private int timeTillReturn = 0;
	private int ticksTillNextLeak = 6;
	private int currentStage = 0;

	private GameObject[] leaks = new GameObject[14];
	private ArrayList<Player> players = new ArrayList<Player>();
	private String[] murphys_messages_ship1 = new String[] { "That's the stuff, fill those holes",
			" it's a fierce sea today traveller", "check those nets" };
	private String[] murphys_messages_ship2 = new String[] { "we're going under", "we'll all end up in a watery grave",
			"check those nets" };

	public FishingTrawler() {
		super(null, 600);
		shipArea = new Area(270, 278, 740, 744, "FishingTrawler: Fine");
		spawnLocation = new Point(272, 742);

		setShipAreaWater(new Area(247, 253, 727, 731, "FishingTrawler: Water"));
		shipAreaWaterSpawn = new Point(251, 729);

		setWaitingShip(new WaitingShip(this));
	}

	@Override
	public void run() {
		try {
			getWaitingShip().update();
			if (players.size() == 0) {
				resetGame();
				currentStage = WAITING;
			}
			if (currentStage != WAITING) {
				if (currentStage == FIRST_SHIP) {
					if (getWaterLevel() >= SHIP_WATER_LIMIT_SECOND_BOAT) {
						for (Player p : players) {
							p.message("the boats full of water");
							p.message("it's sinking!");
							p.setLocation(shipAreaWaterSpawn, true);
						}
						for (int i = 0; i < leaks.length; i++) {
							if (leaks[i] != null) {
								World.getWorld().unregisterGameObject(leaks[i]);
								leaks[i] = null;
							}
						}
						currentStage = SECOND_SHIP;

					}
				} else if (currentStage == SECOND_SHIP) {
					if (getWaterLevel() >= SHIP_WATER_LIMIT_SINK) {
						for (Player p : players) {
							p.message("the boats gone under");
							p.message("you're lost at sea!");
							p.setLocation(spawnEastFail, true);
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
						"@red@the trawler net is damaged" + "% %" + " you cannot catch any fish with damaged net",
						false);
			}
		}
	}

	void murphySpeak() {
		Npc npc = null;
		String message = "";
		if (currentStage == FIRST_SHIP) {
			npc = World.getWorld().getNpc(734, shipArea.getMinX(), shipArea.getMaxX(), shipArea.getMinY(),
					shipArea.getMaxY());
			message = murphys_messages_ship1[DataConversions.random(0, murphys_messages_ship1.length - 1)];
		} else if (currentStage == SECOND_SHIP) {
			npc = World.getWorld().getNpc(734, getShipAreaWater().getMinX(), getShipAreaWater().getMaxX(),
					getShipAreaWater().getMinY(), getShipAreaWater().getMaxY());
			message = murphys_messages_ship2[DataConversions.random(0, murphys_messages_ship2.length - 1)];
		}
		if (npc != null) {
			npc.getUpdateFlags().setChatMessage(new ChatMessage(npc, message, null));
		}
	}

	private void endGame() {
		if(players.size() == 0 || fishCaught == 0) {
			players.clear();
			resetGame();
			return;
		}
		int rewardForEach = fishCaught / players.size();
		for (Player p : players) {
			ActionSender.sendBox(p,
					"@yel@you have trawled a full net% %@yel@It's time to go back in and inspect the catch", false);
			p.message("murphy turns the boat towards shore");
			p.setLocation(SPAWN_LAND, true);
			p.getCache().set("fishing_trawler_reward", rewardForEach);
			ActionSender.hideFishingTrawlerInterface(p);
		}
		players.clear();
		resetGame();
	}

	public void start() {
		resetGame();
		timeTillReturn = 1200;
		currentStage = FIRST_SHIP;

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

			if (currentStage == FIRST_SHIP) {
				x = DataConversions.random(shipArea.getMinX() + 1, shipArea.getMaxX() - 1);
				y = (DataConversions.random(0, 1) == 0 ? spawnLocation.getY() - 1 : spawnLocation.getY() + 1);
			} else if (currentStage == SECOND_SHIP) {
				x = DataConversions.random(shipAreaWater.getMinX() + 1 , shipAreaWater.getMaxX() - 1);
				y = (DataConversions.random(0, 1) == 0 ? shipAreaWaterSpawn.getY() - 1 : shipAreaWaterSpawn.getY() + 1);
			}
			int freeLeakIndex = getFreeLeakIndex();
			/* The ship is leaking hardcore. */
			if (freeLeakIndex == -1) {
				break;
			} else if (RegionManager.getRegion(x, y).getGameObject(x, y) != null) {
				continue;
			}
			int southSide = currentStage == FIRST_SHIP ? spawnLocation.getY() - 1 : shipAreaWaterSpawn.getY() - 1;
			int northSide = currentStage == FIRST_SHIP ? spawnLocation.getY() + 1 : shipAreaWaterSpawn.getY() + 1;
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

	public int getStage() {
		return currentStage;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void addPlayer(Player waitingPlayer) {
		waitingPlayer.teleport(272, 741, true);
		getPlayers().add(waitingPlayer);
		ActionSender.showFishingTrawlerInterface(waitingPlayer);
	}

	public WaitingShip getWaitingShip() {
		return waitingShip;
	}

	public void setWaitingShip(WaitingShip waitingShip) {
		this.waitingShip = waitingShip;
	}

	public void bailWater() {
		
		waterLevel -= DataConversions.random(1, 3);
		if (waterLevel < 0)
			waterLevel = 0;
	}

	public void quitPlayer(Player p) {
		players.remove(p);
		p.teleport(254, 759, false);
		ActionSender.hideFishingTrawlerInterface(p);
	}

	public Area getShipAreaWater() {
		return shipAreaWater;
	}

	public void setShipAreaWater(Area shipAreaWater) {
		this.shipAreaWater = shipAreaWater;
	}
}
