package com.openrsc.server.model.world;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.content.clan.ClanManager;
import com.openrsc.server.content.minigame.fishingtrawler.FishingTrawler;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.external.GameObjectLoc;
import com.openrsc.server.external.NPCLoc;
import com.openrsc.server.io.WorldLoader;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.snapshot.Snapshot;
import com.openrsc.server.model.world.region.RegionManager;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.WorldPopulation;
import com.openrsc.server.sql.query.PlayerOnlineFlagQuery;
import com.openrsc.server.sql.query.logs.LoginLog;
import com.openrsc.server.sql.web.AvatarGenerator;
import com.openrsc.server.util.EntityList;
import com.openrsc.server.util.IPTracker;
import com.openrsc.server.util.ThreadSafeIPTracker;
import com.openrsc.server.util.rsc.CollisionFlag;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.time.*;

public final class World {

    /**
     * The asynchronous logger.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Double ended queue to store snapshots into
     */
    private Deque<Snapshot> snapshots = new LinkedList<Snapshot>();

    /**
     * Returns double-ended queue for snapshots.
     */
    public synchronized Deque<Snapshot> getSnapshots() {
        return snapshots;
    }

    /**
     * Add entry to snapshots
     */
    public synchronized void addEntryToSnapshots(Snapshot snapshot) {
        snapshots.offerFirst(snapshot);
    }

    /**
     * IP filtering for wilderness entry
     */

    private final static IPTracker<String> wildernessIPTracker = new ThreadSafeIPTracker<String>();

    /**
     * Avatar generator upon logout save to PNG.
     */
    private final static AvatarGenerator avatarGenerator = new AvatarGenerator();


    public static IPTracker<String> getWildernessIPTracker() {
        return wildernessIPTracker;
    }


    public static int membersWildStart = 48;
    public static int membersWildMax = 56;

    public static int godSpellsStart = 1;
    public static int godSpellsMax = 5;

    public static final int MAX_HEIGHT = 4032; // 3776

    public static final int MAX_WIDTH = 1008; // 944

    public static boolean EVENT = false;
    public static int EVENT_X = -1, EVENT_Y = -1;

    private static World worldInstance;
    public static int EVENT_COMBAT_MIN, EVENT_COMBAT_MAX;

    public static boolean WORLD_TELEGRAB_TOGGLE = false;
    /**
     * AutoRestart settings
     */
    public static ZoneId zone1 = ZoneId.of("Europe/Paris"); // America/Toronto
    public static ZoneId zone2 = ZoneId.of("America/Toronto"); // America/Toronto
    public static int H1 = Constants.GameServer.RESTART_HOUR1;
    public static int H2 = Constants.GameServer.RESTART_HOUR2;
    public static int M1 = Constants.GameServer.RESTART_MINUTE;
    public static int COUNTDOWN = Constants.GameServer.RESTART_DELAY;
   
    public static synchronized World getWorld() {
        if (worldInstance == null) {
            worldInstance = new World();

        }
        return worldInstance;
    }

    private final WorldLoader db = new WorldLoader();

    private final EntityList<Npc> npcs = new EntityList<Npc>(4000);

    private final EntityList<Player> players = new EntityList<Player>(2000);

    private final List<QuestInterface> quests = new LinkedList<QuestInterface>();

    private final List<Shop> shopData = new ArrayList<Shop>();

    private final List<Shop> shops = new ArrayList<Shop>();

    private final TileValue[][] tiles = new TileValue[MAX_WIDTH][MAX_HEIGHT];

    public WorldLoader wl;

    private FishingTrawler fishingTrawler;

    public void addShopData(Shop... shop) {
        shopData.addAll(Arrays.asList(shop));
    }

    public void clearShopData() {
        shopData.clear();
    }

    public int countNpcs() {
        return npcs.size();
    }

    public int countPlayers() {
        return players.size();
    }

    public void delayedRemoveObject(final GameObject object, final int delay) {
        Server.getServer().getEventHandler().add(new SingleEvent(null, delay) {
            public void action() {
                unregisterGameObject(object);
            }
        });
    }

    /**
     * Adds a DelayedEvent that will spawn a GameObject
     */
    public void delayedSpawnObject(final GameObjectLoc loc, final int respawnTime) {
        Server.getServer().getEventHandler().add(new SingleEvent(null, respawnTime) {
            public void action() {
                registerGameObject(new GameObject(loc));
            }
        });
    }

    public WorldLoader getDB() {
        return db;
    }

    public Npc getNpc(int idx) {
        try {
            return npcs.get(idx);
        } catch (Exception e) {
            return null;
        }
    }

    public Npc getNpc(int id, int minX, int maxX, int minY, int maxY) {
        for (Npc npc : npcs) {
            if (npc.getID() == id && npc.getX() >= minX && npc.getX() <= maxX && npc.getY() >= minY
                    && npc.getY() <= maxY) {
                return npc;
            }
        }
        return null;
    }

    public Npc getNpc(int id, int minX, int maxX, int minY, int maxY, boolean notNull) {
        for (Npc npc : npcs) {
            if (npc.getID() == id && npc.getX() >= minX && npc.getX() <= maxX && npc.getY() >= minY
                    && npc.getY() <= maxY) {
                if (!npc.inCombat()) {
                    return npc;
                }
            }
        }
        return null;
    }

    public Npc getNpcById(int id) {
        for (Npc npc : npcs) {
            if (npc.getID() == id) {
                return npc;
            }
        }
        return null;
    }

    /**
     * Gets the list of npcs on the server
     */
    public synchronized EntityList<Npc> getNpcs() {
        return npcs;
    }

    /**
     * Gets a Player by their server index
     */
    public Player getPlayer(int idx) {
        try {
            Player p = players.get(idx);
            return p;
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * Gets a player by their username hash
     */
    public Player getPlayer(long usernameHash) {
        for (Player p : players) {
            if (p.getUsernameHash() == usernameHash) {
                return p;
            }
        }
        return null;
    }

    /**
     * Gets a player by their username hash
     */
    public Player getPlayerID(int databaseID) {
        for (Player p : players) {
            if (p.getDatabaseID() == databaseID) {
                return p;
            }
        }
        return null;
    }

    public synchronized EntityList<Player> getPlayers() {
        return players;
    }

    /**
     * Finds a specific quest by ID
     *
     * @param q
     * @return
     * @throws IllegalArgumentException when a quest by that ID isn't found
     */
    public QuestInterface getQuest(int q) throws IllegalArgumentException {
        for (QuestInterface quest : this.getQuests()) {
            if (quest.getQuestId() == q) {
                return quest;
            }
        }
        throw new IllegalArgumentException("No quest found");
    }

    public List<QuestInterface> getQuests() {
        return quests;
    }

    public List<Shop> getShops() {
        return shops;
    }

    public TileValue getTile(int x, int y) {
        if (!withinWorld(x, y)) {
            return null;
        }
        TileValue t = tiles[x][y];
        if (t == null) {
            t = new TileValue();
            tiles[x][y] = t;
        }
        return t;
    }

    public TileValue getTile(Point p) {
        return getTile(p.getX(), p.getY());
    }

    public boolean hasNpc(Npc n) {
        return npcs.contains(n);
    }

    public boolean hasPlayer(Player p) {
        return players.contains(p);
    }

    public boolean isLoggedIn(long usernameHash) {
        Player friend = getPlayer(usernameHash);
        if (friend != null) {
            return friend.loggedIn();
        }
        return false;
    }    
    

    public void load() {
        try {
            ClanManager.init();
            worldInstance.wl = new WorldLoader();
            worldInstance.wl.loadWorld(worldInstance);
            WorldPopulation.populateWorld(worldInstance);
            shutdownCheck();

            //AchievementSystem.loadAchievements();
            // Server.getServer().getEventHandler().add(new WildernessCycleEvent());
            //setFishingTrawler(new FishingTrawler());
            //Server.getServer().getEventHandler().add(getFishingTrawler());
        } catch (Exception e) {
            LOGGER.catching(e);
        }
    }
    /*
     * Note to self - Remove CollidingWallObject, Remove getWallGameObject, And others if this doesn't work in long run.
     * Classes - viewArea, world, region, gameObjectAction, GameObjectWallAction, ItemUseOnObject
     */

    public void registerGameObject(GameObject o) {
        Point objectCoordinates = Point.location(o.getLoc().getX(), o.getLoc().getY());
        GameObject collidingGameObject = RegionManager.getRegion(objectCoordinates).getGameObject(objectCoordinates);
        GameObject collidingWallObject = RegionManager.getRegion(objectCoordinates).getWallGameObject(objectCoordinates, o.getLoc().getDirection());
        if (collidingGameObject != null && o.getType() == 0) {
            unregisterGameObject(collidingGameObject);
        }
        if (collidingWallObject != null && o.getType() == 1) {
            unregisterGameObject(collidingWallObject);
        }
        o.setLocation(Point.location(o.getLoc().getX(), o.getLoc().getY()));

        int dir = o.getDirection();
        if (o.getID() == 1147) {
            return;
        }
        switch (o.getType()) {
            case 0:
                if (o.getGameObjectDef().getType() != 1 && o.getGameObjectDef().getType() != 2) {
                    return;
                }
                int width, height;
                if (dir == 0 || dir == 4) {
                    width = o.getGameObjectDef().getWidth();
                    height = o.getGameObjectDef().getHeight();
                } else {
                    height = o.getGameObjectDef().getWidth();
                    width = o.getGameObjectDef().getHeight();
                }
                for (int x = o.getX(); x < o.getX() + width; ++x) {
                    for (int y = o.getY(); y < o.getY() + height; ++y) {
                        if (isProjectileClipAllowed(o)) {
                            getTile(x, y).projectileAllowed = true;
                        }
                        if (o.getGameObjectDef().getType() == 1) {
                            getTile(x, y).traversalMask |= CollisionFlag.FULL_BLOCK_C;
                        } else if (dir == 0) {
                            getTile(x, y).traversalMask |= CollisionFlag.WALL_EAST;
                            if (getTile(x - 1, y) != null)
                                getTile(x - 1, y).traversalMask |= CollisionFlag.WALL_WEST;
                        } else if (dir == 2) {
                            getTile(x, y).traversalMask |= CollisionFlag.WALL_SOUTH;
                            if (getTile(x, y + 1) != null)
                                getTile(x, y + 1).traversalMask |= CollisionFlag.WALL_NORTH;
                        } else if (dir == 4) {
                            getTile(x, y).traversalMask |= CollisionFlag.WALL_WEST;
                            if (getTile(x + 1, y) != null)
                                getTile(x + 1, y).traversalMask |= CollisionFlag.WALL_EAST;
                        } else if (dir == 6) {
                            getTile(x, y).traversalMask |= CollisionFlag.WALL_NORTH;
                            if (getTile(x, y - 1) != null)
                                getTile(x, y - 1).traversalMask |= CollisionFlag.WALL_SOUTH;
                        }
                    }
                }
                break;

            case 1:
                if (o.getDoorDef().getDoorType() != 1) {
                    return;
                }
                int x = o.getX(), y = o.getY();
                if (isProjectileClipAllowed(o)) {
                    getTile(x, y).projectileAllowed = true;
                }
                if (dir == 0) {

                    getTile(x, y).traversalMask |= CollisionFlag.WALL_NORTH;
                    if (getTile(x, y - 1) != null)
                        getTile(x, y - 1).traversalMask |= CollisionFlag.WALL_SOUTH;
                } else if (dir == 1) {
                    getTile(x, y).traversalMask |= CollisionFlag.WALL_EAST;
                    if (getTile(x - 1, y) != null)
                        getTile(x - 1, y).traversalMask |= CollisionFlag.WALL_WEST;
                } else if (dir == 2) {
                    getTile(x, y).traversalMask |= CollisionFlag.FULL_BLOCK_A;
                } else if (dir == 3) {
                    getTile(x, y).traversalMask |= CollisionFlag.FULL_BLOCK_B;
                }
                break;
        }
    }

    private static final String[] objectsProjectileClipAllowed = {"gravestone", "sign", "broken pillar", "bone",
            "animalskull", "skull", "egg", "eggs", "ladder", "torch", "rock", "treestump", "railing",
            "railings", "gate", "fence", "table", "smashed chair", "smashed table", "longtable", "fence", "chair"};

    private boolean isProjectileClipAllowed(GameObject o) {
        for (String s : objectsProjectileClipAllowed) {
            if (o.getType() == 0) {
                // there are many of the objects that need to
                // have clip enabled.
                if (!o.getGameObjectDef().getName().equalsIgnoreCase("tree")) {
                    if (o.getGameObjectDef().getHeight() == 1 && o.getGameObjectDef().getWidth() == 1 && !o.getGameObjectDef().getName().toLowerCase().equalsIgnoreCase("chest"))
                        return true;
                }
                if (o.getGameObjectDef().getName().toLowerCase().equalsIgnoreCase(s)) {
                    return true;
                }
            } else if (o.getType() == 1) {
                if (o.getDoorDef().getName().toLowerCase().equalsIgnoreCase(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void registerItem(final GroundItem i) {
        try {
            if (i.getLoc() == null) {
                Server.getServer().getEventHandler().add(new SingleEvent(null, 180000) {
                    public void action() {
                        unregisterItem(i);
                    }
                });
            }
        } catch (Exception e) {
            i.remove();
            LOGGER.catching(e);
        }
    }

    public Npc registerNpc(Npc n) {
        NPCLoc npc = n.getLoc();
        if (npc.startX < npc.minX || npc.startX > npc.maxX || npc.startY < npc.minY || npc.startY > npc.maxY
                || (getTile(npc.startX, npc.startY).overlay & 64) != 0) {
            LOGGER.error("Broken Npc: <id>" + npc.id + "</id><startX>" + npc.startX + "</startX><startY>"
                    + npc.startY + "</startY>");
        }

        /**
         * Unique ID for event tracking.
         */
        n.setUUID(UUID.randomUUID().toString());

        npcs.add(n);
        return n;
    }

    public void registerObjects(GameObject... obs) {
        for (GameObject o : obs) {
            o.setLocation(Point.location(o.getLoc().getX(), o.getLoc().getY()));
        }
    }

    public boolean registerPlayer(Player player) {

        if (!players.contains(player)) {
            player.setUUID(UUID.randomUUID().toString());

            players.add(player);
            player.updateRegion();
            if (Server.getPlayerDataProcessor() != null) {
                GameLogging.addQuery(new PlayerOnlineFlagQuery(player.getDatabaseID(), player.getCurrentIP(), true));
                GameLogging.addQuery(new LoginLog(player.getDatabaseID(), player.getCurrentIP()));
            }
            for (Player other : getPlayers()) {
                other.getSocial().alertOfLogin(player);
            }
            ClanManager.checkAndAttachToClan(player);
            LOGGER.info("Registered " + player.getUsername() + " to server");
            return true;
        }
        return false;
    }

    public void registerQuest(QuestInterface quest) {
        if (quest.getQuestName() == null) {
            throw new IllegalArgumentException("Quest name cannot be null");
        } else if (quest.getQuestName().length() > 40) {
            throw new IllegalArgumentException("Quest name cannot be longer then 40 characters");
        }
        for (QuestInterface q : quests) {
            if (q.getQuestId() == quest.getQuestId()) {
                throw new IllegalArgumentException("Quest ID must be unique");
            }
        }
        quests.add(quest);
    }

    public void registerShop(Shop shop) {
        shops.add(shop);
    }

    public void registerShops(Shop... shop) {
        shops.addAll(Arrays.asList(shop));
    }

    public void replaceGameObject(GameObject old, GameObject _new) {
        unregisterGameObject(old);
        registerGameObject(_new);
    }

    public void sendKilledUpdate(long killedHash, long killerHash, int type) {
        for (final Player player : players)
            ActionSender.sendKillUpdate(player, killedHash, killerHash, type);
    }

    public void sendModAnnouncement(String string) {
        for (Player p : players) {
            if (p.isMod()) {
                p.message("[@cya@SERVER@whi@]: " + string);
            }
        }
    }

    public void sendWorldAnnouncement(String msg) {
        if (Constants.GameServer.WANT_GLOBAL_CHAT) {
            for (Player p : getPlayers()) {
                p.playerServerMessage(MessageType.QUEST, "@gre@[Global] @whi@" + msg);
            }
        }
    }

    public void sendWorldMessage(String msg) {
        synchronized (players) {
            for (Player p : players) {
                p.playerServerMessage(MessageType.QUEST, msg);
            }
        }
    }

    /**
     * Removes an object from the server
     */
    public void unregisterGameObject(GameObject o) {
        o.remove();
        int dir = o.getDirection();
        switch (o.getType()) {
            case 0:
                if (o.getGameObjectDef().getType() != 1 && o.getGameObjectDef().getType() != 2) {
                    return;
                }
                int width, height;
                if (dir == 0 || dir == 4) {
                    width = o.getGameObjectDef().getWidth();
                    height = o.getGameObjectDef().getHeight();
                } else {
                    height = o.getGameObjectDef().getWidth();
                    width = o.getGameObjectDef().getHeight();
                }
                for (int x = o.getX(); x < o.getX() + width; ++x) {
                    for (int y = o.getY(); y < o.getY() + height; ++y) {
                        if (o.getGameObjectDef().getType() == 1) {
                            getTile(x, y).traversalMask &= 0xffbf;
                        } else if (dir == 0) {
                            getTile(x, y).traversalMask &= 0xfffd;
                            getTile(x - 1, y).traversalMask &= 65535 - 8;
                        } else if (dir == 2) {
                            getTile(x, y).traversalMask &= 0xfffb;
                            getTile(x, y + 1).traversalMask &= 65535 - 1;
                        } else if (dir == 4) {
                            getTile(x, y).traversalMask &= 0xfff7;
                            getTile(x + 1, y).traversalMask &= 65535 - 2;
                        } else if (dir == 6) {
                            getTile(x, y).traversalMask &= 0xfffe;
                            getTile(x, y - 1).traversalMask &= 65535 - 4;
                        }
                    }
                }
                break;
            case 1:
                if (o.getDoorDef().getDoorType() != 1) {
                    return;
                }
                int x = o.getX(), y = o.getY();
                if (dir == 0) {
                    getTile(x, y).traversalMask &= 0xfffe;
                    getTile(x, y - 1).traversalMask &= 65535 - 4;
                } else if (dir == 1) {
                    getTile(x, y).traversalMask &= 0xfffd;
                    getTile(x - 1, y).traversalMask &= 65535 - 8;
                } else if (dir == 2) {
                    getTile(x, y).traversalMask &= 0xffef;
                } else if (dir == 3) {
                    getTile(x, y).traversalMask &= 0xffdf;
                }
                break;
        }
    }

    /**
     * Removes an item from the server
     */
    public void unregisterItem(GroundItem i) {
        i.remove();
    }

    /**
     * Removes an npc from the server
     */
    public void unregisterNpc(Npc n) {
        if (hasNpc(n)) {
            npcs.remove(n);
        }
        n.superRemove();
    }

    /**
     * Removes a player from the server and saves their account
     */
    public void unregisterPlayer(final Player player) {
        try {
            ActionSender.sendLogoutRequestConfirm(player);
            player.setLoggedIn(false);
            player.resetAll();

            Mob opponent = player.getOpponent();
            if (opponent != null) {
                player.resetCombatEvent();
            }
            if (Server.getPlayerDataProcessor() != null) {
                GameLogging.addQuery(new PlayerOnlineFlagQuery(player.getDatabaseID(), false));
                if (Constants.GameServer.AVATAR_GENERATOR)
                    avatarGenerator.generateAvatar(player.getDatabaseID(), player.getSettings().getAppearance(), player.getWornItems());
            }
			/*if(getFishingTrawler().getPlayers().contains(player)) {
				getFishingTrawler().quitPlayer(player);
			}*/
            if (player.getLocation().inMageArena()) {
                player.teleport(228, 109);
            }
            player.save();
            player.remove();
            players.remove(player);

            /** IP Tracking in wilderness removal */
			/*if(player.getLocation().inWilderness())
			{
				wildernessIPTracker.remove(player.getCurrentIP());
			}*/

            for (Player other : getPlayers()) {
                other.getSocial().alertOfLogout(player);
            }

            ClanManager.checkAndUnattachFromClan(player);
            LOGGER.info("Unregistered " + player.getUsername() + " from player list.");
        } catch (Exception e) {
            LOGGER.catching(e);
        }

    }
    private void shutdownCheck() {    	
        Server.getServer().getEventHandler().add(new SingleEvent(null, 60000) {
            public void action() {
            	int hour1 = LocalTime.now(zone1).getHour(); 
                int mins = LocalTime.now().getMinute();
                // System.currentTimeMillis() - Constants.GameServer.START_TIME > 3600000 
                if ((hour1 == H1 || hour1 == H2)          		
                        && mins >= M1) {               	
                    int seconds = COUNTDOWN;                   
                    if (Server.getServer().restart(seconds)) {                      
                        for (Player p : World.getWorld().getPlayers()) {
                            //ActionSender.sendBox(p, message, false);
                            ActionSender.startShutdown(p, seconds);                        	
                        }
                    }              
                }
                shutdownCheck();                
            }
        });
    }
    
    public void unregisterQuest(QuestInterface quest) {
        if (quests.contains(quest)) {
            quests.remove(quest);
        }
    }

    /**
     * Are the given coords within the world boundaries
     */
    public boolean withinWorld(int x, int y) {
        return x >= 0 && x < MAX_WIDTH && y >= 0 && y < MAX_HEIGHT;
    }

    public FishingTrawler getFishingTrawler() {
        return fishingTrawler;
    }

    public void setFishingTrawler(FishingTrawler fishingTrawler) {
        this.fishingTrawler = fishingTrawler;
    }
}
