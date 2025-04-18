package com.openrsc.server.model.entity.player;

import com.openrsc.server.constants.Skills;
import com.openrsc.server.constants.*;
import com.openrsc.server.content.EnchantedCrowns;
import com.openrsc.server.content.achievement.Achievement;
import com.openrsc.server.content.clan.Clan;
import com.openrsc.server.content.clan.ClanInvite;
import com.openrsc.server.content.minigame.fishingtrawler.FishingTrawler;
import com.openrsc.server.content.party.Party;
import com.openrsc.server.content.party.PartyInvite;
import com.openrsc.server.content.party.PartyPlayer;
import com.openrsc.server.database.impl.mysql.queries.logging.GenericLog;
import com.openrsc.server.database.impl.mysql.queries.logging.LiveFeedLog;
import com.openrsc.server.database.struct.PlayerInventory;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.rsc.DuplicationStrategy;
import com.openrsc.server.event.rsc.PluginTask;
import com.openrsc.server.event.rsc.impl.DesertHeatEvent;
import com.openrsc.server.event.rsc.impl.PoisonEvent;
import com.openrsc.server.event.rsc.impl.PrayerDrainEvent;
import com.openrsc.server.event.rsc.impl.projectile.*;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.login.LoginRequest;
import com.openrsc.server.login.PlayerSaveRequest;
import com.openrsc.server.model.*;
import com.openrsc.server.model.action.WalkToAction;
import com.openrsc.server.model.container.*;
import com.openrsc.server.model.entity.*;
import com.openrsc.server.model.entity.UnregisterForcefulness;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.npc.NpcInteraction;
import com.openrsc.server.model.struct.UnequipRequest;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.ClientLimitations;
import com.openrsc.server.net.rsc.PayloadProcessorManager;
import com.openrsc.server.net.rsc.parsers.PayloadParser;
import com.openrsc.server.net.rsc.parsers.impl.*;
import com.openrsc.server.net.rsc.struct.AbstractStruct;
import com.openrsc.server.plugins.Batch;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.triggers.CatGrowthTrigger;
import com.openrsc.server.plugins.triggers.DropObjTrigger;
import com.openrsc.server.plugins.triggers.WineFermentTrigger;
import com.openrsc.server.util.PidShuffler;
import com.openrsc.server.util.UsernameChange;
import com.openrsc.server.util.languages.PreferredLanguage;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;
import com.openrsc.server.util.rsc.PrerenderedSleepword;
import io.netty.channel.Channel;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import static com.openrsc.server.plugins.Functions.changeloc;
import static com.openrsc.server.plugins.Functions.inArray;

/**
 * A single player.
 */
public final class Player extends Mob {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	// activity indicator for kitten to cat growth
	// 100 trigger up a Kitten to cat event
	// 1 walked step is +1 activity, 1 5-min warn to move is +25 activity (saved each 30 secs => 2.5 per save)
	// so everything is multiplied by 2 to avoid decimals
	private final int KITTEN_ACTIVITY_THRESHOLD = 50;
	public int sessionId;
	private Queue<PrivateMessage> privateMessageQueue = new LinkedList<PrivateMessage>();
	private int actionsMouseStill = 0;
	private long lastMouseMoved = 0;
	private Map<Integer, Integer> achievements = new ConcurrentHashMap<>();
	private PlayerSettings playerSettings;
	private Social social;
	private Duel duel;
	private DelayedEvent unregisterEvent;
	private ThrowingEvent throwingEvent;
	private DelayedEvent chargeEvent = null;
	private boolean sleeping = false;
	private int activity = 0;
	private int kills = 0;
	private long openPkPoints = 0;
	private int npcKills = 0;
	private int expShared = 0;
	private int deaths = 0;
	private int npcDeaths = 0;
	private volatile WalkToAction walkToAction;
	private volatile WalkToAction lastExecutedWalkToAction;
	private Trade trade;
	private Clan clan;
	private Party party;
	private ClanInvite activeClanInvitation;
	private PartyInvite activePartyInvitation;
	public final int MAX_FATIGUE = 150000;
	public final String MEMBER_MESSAGE = "This feature is only available for members only";
	private final Map<Integer, Integer> killCache = new HashMap<>();
	private boolean killCacheUpdated = false;
	private final Map<Integer, Integer> questStages = new ConcurrentHashMap<>();
	private int IRON_MAN_MODE = IronmanMode.None.id();
	private int IRON_MAN_RESTRICTION = 1;
	private int IRON_MAN_HC_DEATH = 0;
	public int click = -1;
	private FireCannonEvent cannonEvent = null;
	private long consumeTimer = 0;
	private long lastSaveTime = System.currentTimeMillis();
	private int appearanceID;
	private HashMap<Long, Integer> knownPlayersAppearanceIDs = new HashMap<Long, Integer>();
	private long lastCommand;
	private LinkedHashSet<Player> localPlayers = new LinkedHashSet<Player>();
	private LinkedHashSet<Npc> localNpcs = new LinkedHashSet<Npc>();
	private LinkedHashSet<GameObject> localObjects = new LinkedHashSet<GameObject>();
	private LinkedHashSet<GameObject> localWallObjects = new LinkedHashSet<GameObject>();
	private LinkedHashSet<GroundItem> localGroundItems = new LinkedHashSet<GroundItem>();
	private ArrayDeque<Point> locationsToClear = new ArrayDeque<Point>();
	private String currentIP = "0.0.0.0";
	private int incorrectSleepTries = 0;
	private volatile int questionOption;
	private List<PluginTask> ownedPlugins = Collections.synchronizedList(new ArrayList<>());
	private long lastExchangeTime = System.currentTimeMillis();
	private int clientVersion = 0;
	public int preferredIcon = -1;
	private boolean denyAllLogoutRequests = false;
	private boolean qolOptOutWarned = false;
	private boolean certOptOutWarned = false;
	public boolean speakTongues = false;
	private ClientLimitations clientLimitations;
	public PreferredLanguage preferredLanguage = PreferredLanguage.NONE_SET;
	public PrerenderedSleepword queuedSleepword = null;
	public Player queuedSleepwordSender = null;
	private int saveAttempts = 0;
	private int sceneryMorph = -1;

	public DesertHeatEvent desertHeatEvent = null;

	public boolean canceledMenuHandler = false;

	private Point lastTileClicked = null;

	private final UUID uuid;

	public int knownPlayersCount = 0;
	public int[] knownPlayerPids = new int[500];
	public int[] knownPlayerAppearanceIds = new int[500];
	/**
	 * An atomic reference to the players carried items.
	 * Multiple threads access this and it never changes.
	 */
	private final AtomicReference<CarriedItems> carriedItems = new AtomicReference<>();
	/**
	 * Players cache is used to store various objects into database
	 */
	private final Cache cache = new Cache();
	/**
	 * Received packets from this player yet to be processed.
	 */
	private final LinkedList<Packet> incomingPackets = new LinkedList<>();
	/**
	 * Outgoing packets from this player yet to be processed.
	 */
	private final ArrayList<Packet> outgoingPackets = new ArrayList<>();
	/**
	 * Current active packets - used on packets that should be rated to 1-per-player.
	 */
	private final ArrayList<Integer> activePackets = new ArrayList<>();
	/**
	 * The last menu reply this player gave in a quest
	 */
	public long lastCast = System.currentTimeMillis();
	/**
	 * Prayers
	 */
	private Prayers prayers;
	/**
	 * Bank for banked items
	 */
	private Bank bank;
	/**
	 * Controls if were allowed to accept appearance updates
	 */
	private boolean changingAppearance = false;
	/**
	 * Combat style: 0 - all, 1 - str, 2 - att, 3 - def
	 */
	private int combatStyle = 0;
	/**
	 * Unix time when the player logged in
	 */
	private long currentLogin = 0;
	/**
	 * DelayedEvent responsible for handling prayer drains
	 */
	private PrayerDrainEvent prayerDrainEvent;
	/**
	 * The drain rate of the prayers currently enabled
	 */
	private int drainRate = 0, prayerStatePoints = 0;
	/**
	 * Amount of fatigue - 0 to 150000
	 */
	private int fatigue = 0, sleepStateFatigue = 0;
	/**
	 * The main accounts group is
	 */
	private int groupID = Group.DEFAULT_GROUP;
	/**
	 * Is the player accessing their bank?
	 */
	private boolean inBank = false;
	/**
	 * Channel
	 */
	private Channel channel;
	/**
	 * Time of antidote protection from poison
	 */
	private long lastAntidote = 0;
	/**
	 * How long the poison protection should last
	 */
	private int poisonProtectionTime = 0;
	/**
	 * Stores the last IP address used
	 */
	private String lastIP = "0.0.0.0";
	/**
	 * Unix time when the player last logged in
	 */
	private long lastLogin = 0;
	/**
	 * Unix time when the player last requested a change in recovery questions
	 */
	private long lastRecoveryChangeRequest = 0;
	/**
	 * Last time a client activity was received
	 */
	private long lastClientActivity = System.currentTimeMillis();
	/**
	 * Time last report was sent, used to throttle reports
	 */
	private long lastReport = 0;
	/**
	 * The time of the last spell cast, used as a throttle
	 */
	private long lastSpellCast = 0;
	/**
	 * The time the player had a skull status from combat
	 */
	private long lastSkullEvent = 0;
	/**
	 * The time the player was charged
	 */
	private long lastChargeEvent = 0;
	/**
	 * Time of last trade/duel request
	 */
	private long lastTradeDuelRequest = 0;
	/**
	 * Whether the player is currently logged in
	 */
	private boolean loggedIn = false;
	/**
	 * Is the character male?
	 */
	private boolean maleGender;
	/**
	 * The current active batch
	 */
	private Batch batch;
	/**
	 * The current active menu
	 */
	private Menu menu;
	/**
	 * A handler for any menu we are currently in
	 */
	private MenuOptionListener menuHandler = null;
	/**
	 * The ID of the owning account
	 */
	private int owner = 1;
	/**
	 * Total quest points
	 */
	private int questPoints = 0;
	/**
	 * Ranging event
	 */
	private RangeEvent rangeEvent;
	/**
	 * If the player is reconnecting after connection loss
	 */
	private boolean reconnecting = false;
	/**
	 * Is a trade/duel update required?
	 */
	private boolean requiresOfferUpdate = false;
	/**
	 * The shop (if any) the player is currently accessing
	 */
	private Shop shop = null;
	/**
	 * DelayedEvent used for removing players skull after 20mins
	 */
	private DelayedEvent skullEvent = null;
	/**
	 * Player sleep word
	 */
	private String sleepword;

	/**
	 * Player sleep word
	 */
	private int prerenderedSleepwordIndex;

	/**
	 * If the player has been sending suspicious packets
	 */
	private boolean suspiciousPlayer;
	/**
	 * The player's username
	 */
	private String username;
	/**
	 * The player's most recent former username
	 */
	private String formerName;
	/**
	 * Details about the player's pending username change, will be saved to database on logout
	 */
	private UsernameChange usernameChangePending = null;
	/**
	 * The player's username hash
	 */
	private long usernameHash;
	/**
	 * The items being worn by the player
	 */
	private int[] wornItems = new int[12];
	/**
	 * Time when the player logged in, used to calculate the total play time.
	 */
	private long sessionStart;

	/**
	 * Controls if were allowed to accept recovery updates
	 */
	private boolean changingRecovery = false;

	/**
	 * Controls if were allowed to accept contact details updates
	 */
	private boolean changingDetails = false;
	private boolean[] unlockedSkinColours;

	/**
	 * Holds information related to an unregistration (log-out) request event, to be processed end-of-tick.
	 */
	private UnregisterRequest unregisterRequest = null;

	/**
	 * Did the player have a multi menu end early in dialogue by forcefully having their multi menu closed by another player?
	 */
	private boolean multiEndedEarly = false;

	/**
	 * Holds the damage received by a given mob
	 * Also holds the damage absorbed by the defense cape
	 */
	private Map<UUID, Pair<Integer, Integer>> trackedDamageFromMob = new HashMap<UUID, Pair<Integer, Integer>>();

	private Npc interactingNpc = null;
	private int lastNpcKilledId = -1;
	private boolean isSaving = false;
	private boolean isLoggingOut = false;

	/*
	 * Restricts P2P stuff in F2P wilderness.
	 */
	/*public void unwieldMembersItems() {
		if (!getServer().getConfig().MEMBER_WORLD) {
			boolean found = false;
			for (Item i : getCarriedItems().getInventory().getItems()) {

				if (i.isWielded() && i.getDef().isMembersOnly()) {
					getCarriedItems().getInventory().unwieldItem(i, true);
					found = true;
				}
				if (i.getID() == 2109 && i.isWielded()) {
					getCarriedItems().getInventory().unwieldItem(i, true);
				}
			}
			if (found) {
				message("Members objects can not be wielded on this world.");

				ActionSender.sendInventory(this);
				ActionSender.sendEquipmentStats(this);
			}
			for (int i = 0; i < 3; i++) {
				int min = skills.getLevel(i);
				int max = skills.getMaxStat(i);
				int baseStat = min > max ? max : min;
				int newStat = baseStat + DataConversions.roundUp((max / 100D) * 10) + 2;
				if (min > newStat || (min > max && (i == 1 || i == 0))) {
					skills.setLevel(i, max);
				}
			}
		}
	}*/

	/**
	 * Constructs a new Player instance from LoginRequest
	 *
	 * @param request
	 */
	public Player(final World world, final LoginRequest request) {
		super(world, EntityType.PLAYER);

		usernameHash = DataConversions.usernameToHash(request.getUsername());
		username = DataConversions.hashToUsername(usernameHash);
		sessionStart = System.currentTimeMillis();

		channel = request.getChannel();

		currentIP = ((InetSocketAddress) request.getChannel().remoteAddress()).getAddress().getHostAddress();
		clientVersion = request.getClientVersion();
		currentLogin = System.currentTimeMillis();

		setBusy(true);

		carriedItems.set(new CarriedItems(this));
		trade = new Trade(this);
		duel = new Duel(this);
		playerSettings = new PlayerSettings(this);
		social = new Social(this);
		prayers = new Prayers(this);
		this.uuid = new UUID(0, usernameHash);
	}

	/**
	 *
	 * Constructs a Player with given hash
	 *
	 * @param world
	 * @param hash
	 */
	public Player(final World world, final long hash) {
		super(world, EntityType.PLAYER);

		usernameHash = hash;
		username = DataConversions.hashToUsername(usernameHash);
		sessionStart = System.currentTimeMillis();

		carriedItems.set(new CarriedItems(this));
		this.getCarriedItems().setEquipment(new Equipment(this));
		this.getCarriedItems().setInventory(new Inventory(this, new PlayerInventory[0]));
		trade = new Trade(this);
		duel = new Duel(this);
		playerSettings = new PlayerSettings(this);
		social = new Social(this);
		prayers = new Prayers(this);
		this.uuid = new UUID(0, usernameHash);
	}

	public int getIronMan() {
		return IRON_MAN_MODE;
	}

	public void setIronMan(final int i) {
		this.IRON_MAN_MODE = i;
	}

	public void setOneXp(final boolean isOneXp) {
		if (getCache().hasKey("onexp_mode") && !isOneXp) {
			getCache().remove("onexp_mode");
		} else if (!getCache().hasKey("onexp_mode") && isOneXp) {
			getCache().store("onexp_mode", true);
		}
	}

	public int getIronManRestriction() {
		return IRON_MAN_RESTRICTION;
	}

	public void setIronManRestriction(final int i) {
		this.IRON_MAN_RESTRICTION = i;
	}

	public int getHCIronmanDeath() {
		return IRON_MAN_HC_DEATH;
	}

	public void setHCIronmanDeath(final int i) {
		this.IRON_MAN_HC_DEATH = i;
	}

	private void updateHCIronman(final int int1) {
		this.IRON_MAN_MODE = int1;
		this.IRON_MAN_HC_DEATH = int1;
	}

	/**
	 * Checks if the player is any type of Ironman (except a transfer character)
	 *
	 * @return True if the player is any type of Ironman, false otherwise
	 */
	public boolean isIronMan() {
		return getIronMan() == IronmanMode.Ironman.id()
			|| getIronMan() == IronmanMode.Ultimate.id()
			|| getIronMan() == IronmanMode.Hardcore.id();
	}

	/**
	 * Checks if the player is the specified type of Ironman
	 *
	 * @param mode The Ironman type to check for
	 * @return True if the player is of the specified Ironman Type, false otherwise
	 */
	public boolean isIronMan(final int mode) {
		if (mode == IronmanMode.Ironman.id() && getIronMan() == IronmanMode.Ironman.id()) {
			return true;
		} else if (mode == IronmanMode.Ultimate.id() && getIronMan() == IronmanMode.Ultimate.id()) {
			return true;
		} else if (mode == IronmanMode.Hardcore.id() && getIronMan() == IronmanMode.Hardcore.id()) {
			return true;
		} else if (mode == IronmanMode.Transfer.id() && getIronMan() == IronmanMode.Transfer.id()) {
			return true;
		}
		return false;
	}

	public boolean isOneXp() {
		if (getCache().hasKey("onexp_mode")) {
			return getCache().getBoolean("onexp_mode");
		}
		return false;
	}

	public void setPkMode(int isPk) {
		getCache().set("pk_mode", isPk);
	}

	public int getPkMode() {
		return getCache().hasKey("pk_mode") ? getCache().getInt("pk_mode") : 0;
	}

	public void setPkChanges(int changesLeft) {
		getCache().set("pk_changes_left", changesLeft);
	}

	public int getPkChanges() {
		int changes_left;
		if (!getCache().hasKey("pk_changes_left")) {
			if (this.getConfig().USES_PK_MODE) {
				changes_left = 2;
				setPkChanges(changes_left);
			} else {
				changes_left = 0;
			}
		} else {
			changes_left = getCache().getInt("pk_changes_left");
		}

		return changes_left;
	}

	public void setHideOnline(byte hideOnline) {
		getCache().set("setting_hide_online", hideOnline);
	}

	public int getHideOnline() {
		return getCache().hasKey("setting_hide_online") ? getCache().getInt("setting_hide_online") : 0;
	}

	public void setShowReceipts(boolean show) {
		getCache().store("show_receipts", show);
	}

	public boolean getShowReceipts() {
		return getCache().hasKey("show_receipts") ? getCache().getBoolean("show_receipts") : false;
	}

	public void resetCannonEvent() {
		if (cannonEvent != null) {
			cannonEvent.stop();
			cannonEvent = null;
		}
	}

	public boolean isCannonEventActive() {
		return cannonEvent != null;
	}

	public void setCannonEvent(final FireCannonEvent event) {
		cannonEvent = event;
	}

	public long getLastSaveTime() {
		return lastSaveTime;
	}

	public void setLastSaveTime(final long save) {
		lastSaveTime = save;
	}

	public int getAppearanceID() {
		return appearanceID;
	}

	public void incAppearanceID() {
		appearanceID++;
	}

	public long getLastCommand() {
		return lastCommand;
	}

	public void setLastCommand(final long newTime) {
		this.lastCommand = newTime;
	}

	public Point getLastTileClicked() {
		return this.lastTileClicked;
	}

	public void setLastTileClicked(Point lastTileClicked) {
		this.lastTileClicked = lastTileClicked;
	}

	public void setNextRegionLoad() {
		int x = this.getLocation().getX();
		int y = this.getLocation().getY();

		int PLANE_WIDTH = 2304;
		int PLANE_HEIGHT = 1776;

		int lx = x + PLANE_WIDTH;
		int ly = y + PLANE_HEIGHT;

		int sectionx = (lx + 24) / 48;
		int sectiony = (ly + 24) / 48;

		this.setAttribute("midpointRegion", new Point((sectionx * 48) - PLANE_WIDTH, (sectiony * 48) - PLANE_HEIGHT));
	}

	public boolean requiresAppearanceUpdateFor(final Player player) {
		for (Entry<Long, Integer> entry : knownPlayersAppearanceIDs.entrySet()) {
			if (entry.getKey() == player.getUsernameHash()) {
				if (entry.getValue() != player.getAppearanceID()) {
					knownPlayersAppearanceIDs.put(player.getUsernameHash(), player.getAppearanceID());
					return true;
				}
				return false;
			}
		}
		knownPlayersAppearanceIDs.put(player.getUsernameHash(), player.getAppearanceID());
		return true;
	}

	public boolean requiresAppearanceUpdateForPeek(final Player player) {
		for (Entry<Long, Integer> entry : knownPlayersAppearanceIDs.entrySet())
			if (entry.getKey() == player.getUsernameHash())
				return entry.getValue() != player.getAppearanceID();
		return true;
	}

	public HashMap<Long, Integer> getKnownPlayerAppearanceIDs() {
		return knownPlayersAppearanceIDs;
	}

	public void write(final Packet o) {
		if (channel != null && channel.isOpen() && isLoggedIn()) {
			synchronized (outgoingPackets) {
				outgoingPackets.add(o);
			}
		}
	}

	public LinkedHashSet<Npc> getLocalNpcs() {
		return localNpcs;
	}

	public LinkedHashSet<GameObject> getLocalWallObjects() {
		return localWallObjects;
	}

	public LinkedHashSet<GameObject> getLocalGameObjects() {
		return localObjects;
	}

	public LinkedHashSet<Player> getLocalPlayers() {
		return localPlayers;
	}

	public LinkedHashSet<GroundItem> getLocalGroundItems() {
		return localGroundItems;
	}

	public ArrayDeque<Point> getLocationsToClear() {
		return locationsToClear;
	}

	public boolean accessingBank() {
		return inBank;
	}

	//private int unreadMessages, teleportStones;

	public boolean accessingShop() {
		return shop != null;
	}

	public PrivateMessage getNextPrivateMessage() {
		return privateMessageQueue.poll();
	}

	public void addSkull(final long timeLeft) {
		if (skullEvent == null) {
			skullEvent = new DelayedEvent(getWorld(), this, timeLeft, "Player Add Skull") {
				@Override
				public void run() {
					removeSkull();
					if (getWorld().getServer().getConfig().WANT_PARTIES) {
						if (getParty() != null) {
							getParty().sendParty();
						}
					}
				}
			};
			getWorld().getServer().getGameEventHandler().add(skullEvent);
			getUpdateFlags().setAppearanceChanged(true);
		}
		if (getWorld().getServer().getConfig().WANT_PARTIES) {
			if (getParty() != null) {
				getParty().sendParty();
			}
		}
	}

	private void removeCharge() {
		if (chargeEvent == null) {
			return;
		}
		chargeEvent.stop();
		chargeEvent = null;
		cache.remove("charge_remaining");
	}

	public void addCharge(final long timeLeft) {
		if (chargeEvent != null) {
			chargeEvent.resetCountdown();
			return;
		}

		chargeEvent = new DelayedEvent(getWorld(), this, timeLeft, "Charge Spell Removal") {
			// 6 minutes taken from RS2.
			// the charge spell in RSC seem to be bugged, but 10 minutes most of the times.
			// sometimes you are charged for 1 hour lol.
			@Override
			public void run() {
				removeCharge();
				getOwner().message("@red@Your magic charge fades");
			}
		};
		getWorld().getServer().getGameEventHandler().add(chargeEvent);
	}

	public boolean isSaving() {
		return isSaving;
	}

	public void setSaving(boolean saving) {
		isSaving = saving;
	}

	public boolean isLoggingOut() {
		return isLoggingOut;
	}

	public void setLoggingOut(boolean loggingOut) {
		isLoggingOut = loggingOut;
	}

	public void unsetChannel() {
		channel = null;
	}

	public void close() {
		getChannel().close();
	}

	public boolean canLogout() {
		if (menuHandler != null) {
			return true;
		}
		if (denyAllLogoutRequests && System.currentTimeMillis() - getLastClientActivity() < 30000) {
			return false;
		}
		if (inCombat() || getDuel().isDuelActive()) {
			return false;
		}
		return !isBusy() && hasSatisfiedCooldown();
	}

	private boolean hasSatisfiedCooldown() {
		return (System.currentTimeMillis() - getLastClientActivity() > 30000 || System.currentTimeMillis() - getCombatTimer() > 10000)
			&& System.currentTimeMillis() - getAttribute("last_shot", (long) 0) > 10000
			&& System.currentTimeMillis() - getLastExchangeTime() > 3000;
	}

	public boolean canReport() {
		return isPlayerMod() || System.currentTimeMillis() - lastReport > 60000;
	}

	public boolean castTimer(boolean allowRapid) {
		final int holdTimer = allowRapid ? 0 : getConfig().MILLISECONDS_BETWEEN_CASTS;
		return System.currentTimeMillis() - lastSpellCast > holdTimer;
	}

	public boolean addOwnedPlugin(final PluginTask plugin) {
		return ownedPlugins.add(plugin);
	}

	public boolean removeOwnedPlugin(final PluginTask plugin) {
		return ownedPlugins.remove(plugin);
	}

	public Collection<PluginTask> getOwnedPlugins() {
		return ownedPlugins;
	}

	public void interruptPlugins() {
		try {
			for (final PluginTask ownedPlugin : ownedPlugins) {
				ownedPlugin.getScriptContext().setInterrupted(true);
				final Npc npc = ownedPlugin.getScriptContext().getInteractingNpc();
				if (npc != null) {
					npc.setBusy(false);
				}
			}
		} catch (ConcurrentModificationException e) {
			LOGGER.error(e);
		}
	}

	public boolean checkAttack(final Mob mob, final boolean missile) {
		if (mob.isPlayer()) {
			Player victim = (Player) mob;
			if (getParty() != null && ((Player) mob).getParty() != null && getParty() == ((Player) mob).getParty()) {
				message("You can't attack your party members");
				return false;
			}
			if ((inCombat() && getDuel().isDuelActive()) && (victim.inCombat() && victim.getDuel().isDuelActive())) {
				Player opponent = (Player) getOpponent();
				if (victim.equals(opponent)) {
					return true;
				}
			}
			if (!missile) {
				if (!((Player)mob).canBeReattacked()) {
					return false;
				}
			}

			if (getConfig().USES_PK_MODE) {
				if (getPkMode() == 0 || victim.getPkMode() == 0) {
					message("You are not allowed to attack that person");
					return false;
				} else if (getLocation().isInLumbridgeStartingChunk()
					|| victim.getLocation().isInLumbridgeStartingChunk()) {
					message("You can't attack other players here. Move out of Lumbridge");
					return false;
				}  else if (checkVisNpc(this, NpcId.BANKER.id(), 5) != null) {
					message("You cannot attack other players in the vicinity of a banker");
					return false;
				} else if (Math.abs(getCombatLevel() - victim.getCombatLevel()) > 5
					|| Math.abs(getSkills().getMaxStat(Skill.ATTACK.id()) - victim.getSkills().getMaxStat(Skill.ATTACK.id())) > 10
					|| Math.abs(getSkills().getMaxStat(Skill.DEFENSE.id()) - victim.getSkills().getMaxStat(Skill.DEFENSE.id())) > 10
					|| Math.abs(getSkills().getMaxStat(Skill.STRENGTH.id()) - victim.getSkills().getMaxStat(Skill.STRENGTH.id())) > 10) {
					// TODO: may need to check also hits?
					message("You can only attack players with combat close to your own");
					return false;
				}
			} else {
				int myWildLvl = getLocation().wildernessLevel();
				int victimWildLvl = victim.getLocation().wildernessLevel();
				if (myWildLvl < 1 || victimWildLvl < 1) {
					message("You can't attack other players here. Move to the wilderness");
					return false;
				}
				int combDiff = Math.abs(getCombatLevel() - victim.getCombatLevel());
				if (combDiff > myWildLvl) {
					message("You can only attack players within " + (myWildLvl) + " levels of your own here");
					message("Move further into the wilderness for less restrictions");
					return false;
				}
				if (combDiff > victimWildLvl) {
					message("You can only attack players within " + (victimWildLvl) + " levels of your own here");
					message("Move further into the wilderness for less restrictions");
					return false;
				}
			}

			if (victim.isInvulnerableTo(this) || victim.isInvisibleTo(this)) {
				message("You are not allowed to attack that person");
				return false;
			}
			return true;
		} else if (mob.isNpc()) {
			Npc victim = (Npc) mob;
			if (!victim.getDef().isAttackable()) {
				setSuspiciousPlayer(true, "NPC isn't attackable");
				return false;
			}
			return true;
		}
		return true;
	}

	@Override
	public void resetCombatEvent() {
		if (inCombat()) {
			super.resetCombatEvent();
		}
	}

	public int combatStyleToIndex() {
		if (getCombatStyle() == Skills.AGGRESSIVE_MODE) {
			return Skill.STRENGTH.id();
		}
		if (getCombatStyle() == Skills.ACCURATE_MODE) {
			return Skill.ATTACK.id();
		}
		if (getCombatStyle() == Skills.DEFENSIVE_MODE) {
			return Skill.DEFENSE.id();
		}
		return -1;
	}

	/**
	 * Sets a request to unregister this player instance from the server at the end of the tick.
	 *
	 * @param force  - UnregisterForcefulness enum. FAIL_IN_COMBAT, WAIT_UNTIL_COMBAT_ENDS, or FORCED.
	 * @param reason - reason why the player was unregistered.
	 */
	public void unregister(final UnregisterForcefulness force, final String reason) {
		if (this.isUnregistering() || this.hasUnregisterRequest()) {
			return;
		}

		this.setUnregisterRequest(new UnregisterRequest(this, force, reason));
	}

	public void updateTotalPlayed() {
		if (cache.hasKey("total_played")) {
			long oldTotal = cache.getLong("total_played");
			long newTotal = oldTotal + getSessionPlay();
			cache.store("total_played", newTotal);
		} else {
			cache.store("total_played", getSessionPlay());
		}
		sessionStart = System.currentTimeMillis();
	}

	public long getSessionPlay() {
		return System.currentTimeMillis() - sessionStart;
	}

	private void updateSkullRemaining() {
		if ((getCache().getLong("skull_remaining") <= 0) || (getCache().hasKey("skull_remaining") && !isSkulled())) { // Removes the skull remaining key once no longer needed
			cache.remove("skull_remaining");
		} else if (getSkullTime() - System.currentTimeMillis() > 0) {
			cache.store("skull_remaining", (getSkullTime() - System.currentTimeMillis()));
		}
	}

	private void updateChargeRemaining() {
		if ((getCache().getLong("charge_remaining") <= 0) || (getCache().hasKey("charge_remaining") && !isCharged())) { // Removes the charge remaining key once no longer needed
			cache.remove("charge_remaining");
		} else if (getChargeTime() - System.currentTimeMillis() > 0) {
			cache.store("charge_remaining", (getChargeTime() - System.currentTimeMillis()));
		}
	}

	public void updateCacheTimersForLogout() {
		updateTotalPlayed();
		if (isSkulled())
			updateSkullRemaining();
		if (isCharged())
			updateChargeRemaining();
		if (getConfig().WANT_OPENPK_POINTS) {
			getCache().store("openpk_points", getOpenPkPoints());
		}
		getCache().store("last_spell_cast", lastSpellCast);
	}

	public void alertQueuedSleepwordCancelledByLogout() {
		if (null != queuedSleepword) {
			try {
				queuedSleepwordSender.playerServerMessage(MessageType.QUEST, getUsername() + " logged out!!");
			} catch (Exception ex) {} // moderator may have logged out as well
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof Player) {
			Player player = (Player) o;
			return usernameHash == player.getUsernameHash();
		}
		return false;
	}

	/* TODO: implement hashCode
	@Override
	public int hashCode() {
		return Objects.hash(uuid);
	}
	 */

	public void checkEquipment2() {
		for (int slot = 0; slot < Equipment.SLOT_COUNT; slot++) {
			Item item = getCarriedItems().getEquipment().get(slot);
			if (item == null)
				continue;
			int requiredLevel = item.getDef(getWorld()).getRequiredLevel();
			int requiredSkillIndex = item.getDef(getWorld()).getRequiredSkillIndex();
			String itemLower = item.getDef(getWorld()).getName().toLowerCase();
			Optional<Integer> optionalLevel = Optional.empty();
			Optional<Integer> optionalSkillIndex = Optional.empty();
			boolean unWield = false;
			boolean bypass = !getWorld().getServer().getConfig().STRICT_CHECK_ALL &&
				(itemLower.startsWith("poisoned") &&
					((itemLower.endsWith("throwing dart") && !getWorld().getServer().getConfig().STRICT_PDART_CHECK) ||
						(itemLower.endsWith("throwing knife") && !getWorld().getServer().getConfig().STRICT_PKNIFE_CHECK) ||
						(itemLower.endsWith("spear") && !getWorld().getServer().getConfig().STRICT_PSPEAR_CHECK))
				);
			if (itemLower.endsWith("spear") || itemLower.endsWith("throwing knife")) {
				optionalLevel = Optional.of(requiredLevel <= 10 ? requiredLevel : requiredLevel + 5);
				optionalSkillIndex = Optional.of(Skill.AGILITY.id());
			}
			//staff of iban (usable)
			if (item.getCatalogId() == ItemId.STAFF_OF_IBAN.id()) {
				optionalLevel = Optional.of(requiredLevel);
				optionalSkillIndex = Optional.of(Skill.AGILITY.id());
			}
			//battlestaves (incl. enchanted version)
			if (itemLower.contains("battlestaff")) {
				optionalLevel = Optional.of(requiredLevel);
				optionalSkillIndex = Optional.of(Skill.AGILITY.id());
			}

			if (getSkills().getMaxStat(requiredSkillIndex) < requiredLevel) {
				if (!bypass) {
					message("You are not a high enough level to use this item");
					message("You need to have a " + getWorld().getServer().getConstants().getSkills().getSkillName(requiredSkillIndex) + " level of " + requiredLevel);
					unWield = true;
				}
			}
			if (optionalSkillIndex.isPresent() && getSkills().getMaxStat(optionalSkillIndex.get()) < optionalLevel.get()) {
				if (!bypass) {
					message("You are not a high enough level to use this item");
					message("You need to have a " + getWorld().getServer().getConstants().getSkills().getSkillName(optionalSkillIndex.get()) + " level of " + optionalLevel.get());
					unWield = true;
				}
			}

			if (unWield) {
				UnequipRequest request = new UnequipRequest();
				request.item = item;
				request.sound = false;
				request.player = this;
				request.requestType = UnequipRequest.RequestType.FROM_EQUIPMENT;
				request.equipmentSlot = Equipment.EquipmentSlot.get(slot);
				if (!getCarriedItems().getEquipment().unequipItem(request)) {
					request.requestType = UnequipRequest.RequestType.FROM_BANK;
					getCarriedItems().getEquipment().unequipItem(request, false);
				}

				//check to make sure their item was actually unequipped.
				//it might not have if they have a full inventory.
				if (getCarriedItems().getEquipment().get(slot) != null) {
					// TODO: Second argument to the plugin should NOT be null here as the Equipped Equipment for Cabbage server should still have an inventory index.
					getWorld().getServer().getPluginHandler().handlePlugin(DropObjTrigger.class, this, new Object[]{this, null, item, false});
				}
			}

		}
	}

	public void checkEquipment() {
		if (getWorld().getServer().getConfig().NO_LEVEL_REQUIREMENT_WIELD) {
			return;
		}
		if (getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			checkEquipment2();
			return;
		}
		ListIterator<Item> iterator = getCarriedItems().getInventory().iterator();
		for (int slot = 0; iterator.hasNext(); slot++) {
			Item item = iterator.next();
			if (item.isWielded()) {
				int requiredLevel = item.getDef(getWorld()).getRequiredLevel();
				int requiredSkillIndex = item.getDef(getWorld()).getRequiredSkillIndex();
				String itemLower = item.getDef(getWorld()).getName().toLowerCase();
				Optional<Integer> optionalLevel = Optional.empty();
				Optional<Integer> optionalSkillIndex = Optional.empty();
				boolean unWield = false;
				boolean bypass = !getWorld().getServer().getConfig().STRICT_CHECK_ALL &&
					(itemLower.startsWith("poisoned") &&
						((itemLower.endsWith("throwing dart") && !getWorld().getServer().getConfig().STRICT_PDART_CHECK) ||
							(itemLower.endsWith("throwing knife") && !getWorld().getServer().getConfig().STRICT_PKNIFE_CHECK) ||
							(itemLower.endsWith("spear") && !getWorld().getServer().getConfig().STRICT_PSPEAR_CHECK))
					);
				if (itemLower.endsWith("spear") || itemLower.endsWith("throwing knife")) {
					optionalLevel = Optional.of(requiredLevel <= 10 ? requiredLevel : requiredLevel + 5);
					optionalSkillIndex = Optional.of(Skill.ATTACK.id());
				}
				//staff of iban (usable)
				if (item.getCatalogId() == ItemId.STAFF_OF_IBAN.id()) {
					optionalLevel = Optional.of(requiredLevel);
					optionalSkillIndex = Optional.of(Skill.ATTACK.id());
				}
				//battlestaves (incl. enchanted version)
				if (itemLower.contains("battlestaff")) {
					optionalLevel = Optional.of(requiredLevel);
					optionalSkillIndex = Optional.of(Skill.ATTACK.id());
				}

				if (getSkills().getMaxStat(requiredSkillIndex) < requiredLevel) {
					if (!bypass) {
						message("You are not a high enough level to use this item");
						message("You need to have a " + getWorld().getServer().getConstants().getSkills().getSkillName(requiredSkillIndex) + " level of " + requiredLevel);
						unWield = true;
					}
				}
				if (optionalSkillIndex.isPresent() && getSkills().getMaxStat(optionalSkillIndex.get()) < optionalLevel.get()) {
					if (!bypass) {
						message("You are not a high enough level to use this item");
						message("You need to have a " + getWorld().getServer().getConstants().getSkills().getSkillName(optionalSkillIndex.get()) + " level of " + optionalLevel.get());
						unWield = true;
					}
				}

				if (unWield) {
					UnequipRequest.RequestType type = getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB
						? UnequipRequest.RequestType.FROM_EQUIPMENT : UnequipRequest.RequestType.FROM_INVENTORY;
					getCarriedItems().getEquipment().unequipItem(
						new UnequipRequest(this, item, type, true)
					);
				}
			}
		}
	}

	public int getFreeBankSlots() {
		return getWorld().getMaxBankSize() - getBank().size();
	}

	public synchronized Bank getBank() {
		return bank;
	}

	public synchronized void setBank(final Bank b) {
		bank = b;
	}

	public Cache getCache() {
		return cache;
	}

	public Map<Integer, Integer> getKillCache() {
		return killCache;
	}

	public boolean getKillCacheUpdated() {
		return killCacheUpdated;
	}

	public void setKillCacheUpdated(boolean value) {
		killCacheUpdated = value;
	}

	public long getCastTimer() {
		return lastSpellCast;
	}

	public long getSkullTimer() {
		return lastSkullEvent;
	}

	public int getClick() {
		return click;
	}

	public void setClick(final int click) {
		this.click = click;
	}

	@Override
	public int getCombatStyle() {
		return combatStyle;
	}

	public void setCombatStyle(final int style) {
		combatStyle = style;
		ActionSender.sendCombatStyle(this);
	}

	public String getCurrentIP() {
		return currentIP;
	}

	private void setCurrentIP(final String currentIP) {
		this.currentIP = currentIP;
	}

	public long getCurrentLogin() {
		return currentLogin;
	}

	public void setCurrentLogin(final long currentLogin) {
		this.currentLogin = currentLogin;
	}

	public int getDaysSinceLastLogin() {
		long now = Calendar.getInstance().getTimeInMillis() / 1000;
		return (int) ((now - lastLogin) / 86400);
	}

	public void setLastRecoveryChangeRequest(final long l) {
		lastRecoveryChangeRequest = l;
	}

	public int getDaysSinceLastRecoveryChangeRequest() {
		long now = Calendar.getInstance().getTimeInMillis() / 1000;
		return (int) ((now - lastRecoveryChangeRequest) / 86400);
	}

	public PrayerDrainEvent getDrainer() {
		return prayerDrainEvent;
	}

	public int getDrainRate() {
		return drainRate;
	}

	public void setDrainRate(final int rate) {
		drainRate = rate;
	}

	public int getFatigue() {
		if (getWorld().getServer().getConfig().WANT_FATIGUE) {
			return fatigue;
		} else {
			return 0;
		}
	}

	public void setFatigue(final int fatigue) {
		if (getWorld().getServer().getConfig().WANT_FATIGUE) {
			this.fatigue = fatigue;
			ActionSender.sendFatigue(this);
		} else {
			this.fatigue = 0;
		}
	}

	public int getIncorrectSleepTimes() {
		return incorrectSleepTries;
	}

	public String getLastIP() {
		return lastIP;
	}

	public void setLastIP(final String ip) {
		lastIP = ip;
	}

	public long getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(final long l) {
		lastLogin = l;
	}

	public long getLastClientActivity() {
		return lastClientActivity;
	}

	public Batch getBatch() {
		return batch;
	}

	public void setBatch(final Batch batch) {
		this.batch = batch;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(final Menu menu) {
		resetMenuHandler();
		this.menu = menu;
	}

	public MenuOptionListener getMenuHandler() {
		return menuHandler;
	}

	public void setMenuHandler(final MenuOptionListener menuHandler) {
		resetMenuHandler();
		menuHandler.setOwner(this);
		this.menuHandler = menuHandler;
	}

	public int getMinutesMuteLeft() {
		long now = System.currentTimeMillis();
		return (int) ((getMuteExpires() - now) / 60000);
	}

	public long getMuteExpires() {
		if (getCache().hasKey("mute_expires"))
			return getCache().getLong("mute_expires");
		else
			return 0;
	}

	public void setMuteExpires(final long l) {
		getCache().store("mute_expires", l);
		getCache().store("global_mute", l);
	}

	public void setGlobalMuteExpires(final long l) {
		getCache().store("global_mute", l);
	}

	public void setShadowMute(final boolean n) {
		getCache().store("shadow_mute", n);
	}

	public boolean isShadowMuted() {
		if (getCache().hasKey("shadow_mute"))
			return getCache().getBoolean("shadow_mute");
		else
			return false;
	}

	public synchronized int getOption() {
		return questionOption;
	}

	public synchronized void setOption(final int option) {
		this.questionOption = option;
		if (this.questionOption == -1) {
			this.menuHandler = null;
		}
	}

	public int getOwner() {
		return owner;
	}

	public int getQuestPoints() {
		return questPoints;
	}

	public void setQuestPoints(final int questPoints) {
		this.questPoints = questPoints;
	}

	public int calculateQuestPoints() {
		int qps = 0;
		for (QuestInterface quest : getWorld().getQuests()) {
			if (this.getQuestStage(quest.getQuestId()) < 0) {
				qps += quest.getQuestPoints();
			}
		}
		this.setQuestPoints(qps);
		return qps;
	}

	public int getQuestStage(final int id) {
		if (getQuestStages().containsKey(id)) {
			return getQuestStages().get(id);
		}
		return 0;
	}

	public int getQuestStage(final QuestInterface q) {
		if (getQuestStages().containsKey(q.getQuestId())) {
			return getQuestStages().get(q.getQuestId());
		}
		return 0;
	}

	public int getRangeEquip() {
		if (getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			Item item;
			for (int i = 0; i < Equipment.SLOT_COUNT; i++) {
				item = getCarriedItems().getEquipment().get(i);
				if(item != null) {
					int weaponId = item.getCatalogId();
					if (RangeUtils.isCrossbow(weaponId) || RangeUtils.isBow(weaponId)) {
						return item.getCatalogId();
					}
				}
			}
		} else {
			synchronized (getCarriedItems().getInventory().getItems()) {
				for (Item item : getCarriedItems().getInventory().getItems()) {
					int weaponId = item.getCatalogId();
					if (item.isWielded() && (RangeUtils.isCrossbow(weaponId) || RangeUtils.isBow(weaponId))) {
						return item.getCatalogId();
					}
				}
			}
		}
		return -1;
	}

	public int getThrowingEquip() {
		if (getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			Item item;
			for (int i = 0; i < Equipment.SLOT_COUNT; i++) {
				item = getCarriedItems().getEquipment().get(i);
				if (item != null && DataConversions.inArray(Formulae.throwingIDs, item.getCatalogId())) {
					return item.getCatalogId();
				}
			}
		} else {
			synchronized (getCarriedItems().getInventory().getItems()) {
				for (Item item : getCarriedItems().getInventory().getItems()) {
					if (item.isWielded() && (DataConversions.inArray(Formulae.throwingIDs, getEquippedWeaponID()) && item.getDef(getWorld()).getWieldPosition() == 4)) {
						return item.getCatalogId();
					}
				}
			}
		}

		return -1;
	}

	public RangeEvent getRangeEvent() {
		return rangeEvent;
	}

	public void setRangeEvent(final RangeEvent event) {
		rangeEvent = event;
	}

	public ThrowingEvent getThrowingEvent() {
		return throwingEvent;
	}

	public void setThrowingEvent(final ThrowingEvent event) {
		throwingEvent = event;
	}

	public String getStaffName() {
		return Group.getStaffPrefix(getWorld(), getGroupID()) + getUsername();
	}

	public Channel getChannel() {
		return channel;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(final Shop shop) {
		this.shop = shop;
	}

	public DelayedEvent getSkullEvent() {
		return skullEvent;
	}

	public void setSkullEvent(final DelayedEvent skullEvent) {
		this.skullEvent = skullEvent;
	}

	public DelayedEvent getChargeEvent() {
		return chargeEvent;
	}

	public void setChargeEvent(final DelayedEvent chargeEvent) {
		this.chargeEvent = chargeEvent;
	}

	public long getSkullTime() {
		if (isSkulled() && getSkullType() == 1) {
			return skullEvent.timeTillNextRun();
		}
		return 0;
	}

	private long getSkullExpires() {
		if (getCache().hasKey("skull_remaining"))
			return getCache().getLong("skull_remaining");
		if (!getCache().hasKey("skull_remaining"))
			getSkullTime();
		return 0;
	}

	public int getMinutesSkullLeft() {
		long now = System.currentTimeMillis();
		return (int) ((getSkullExpires() - now) / 60000);
	}

	public long getChargeTime() {
		if (isCharged())
			return chargeEvent.timeTillNextRun();
		return 0;
	}

	private long getChargeExpires() {
		if (getCache().hasKey("charge_remaining"))
			return getCache().getLong("charge_remaining");
		if (!getCache().hasKey("charge_remaining"))
			getChargeTime();
		return 0;
	}

	public String getSleepword() {
		return sleepword;
	}

	public int getPrerenderedSleepwordIndex() {
		return prerenderedSleepwordIndex;
	}

	public void setSleepword(final String sleepword) {
		this.sleepword = sleepword;
	}

	public void setSleepword(int sleepwordIndex) {
		this.prerenderedSleepwordIndex = sleepwordIndex;
	}

	public int getSpellWait() {
		return Math.max((int)(((getConfig().MILLISECONDS_BETWEEN_CASTS - (System.currentTimeMillis() - lastSpellCast)) / 1000D)), 1);
	}

	public boolean hasNoTradeConfirm() {
		return hasNoTradeConfirm(0);
	}

	public boolean hasNoTradeConfirm(int atLeastMinutes) {
		return System.currentTimeMillis() - getNoTradeConfirmTime() < Math.max(0L, (5L - atLeastMinutes)) * 60000;
	}

	public long getNoTradeConfirmTime() {
		if (this.getCache().hasKey("last_noconfirm")) {
			return this.getCache().getLong("last_noconfirm");
		}
		return 0;
	}

	public String getUsername() {
		return username;
	}

	public String getFormerName() {
		return formerName;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public void setFormerName(final String formerName) {
		this.formerName = formerName;
	}

	public long getUsernameHash() {
		if (getAttribute("fakeuser", null) != null) {
			return DataConversions.usernameToHash(getAttribute("fakeuser", null));
		}
		return usernameHash;
	}

	private void setUsernameHash(final long usernameHash) {
		this.usernameHash = usernameHash;
	}

	@Override
	public int getArmourPoints() {
		//Currently the only thing that affects armour is the equipment
		return Math.max(getCarriedItems().getEquipment().getArmour(), 1);
	}

	@Override
	public int getWeaponAimPoints() {
		//Currently the only thing that affects weapon aim is the equipment
		return Math.max(getCarriedItems().getEquipment().getWeaponAim(), 1);
	}

	@Override
	public int getWeaponPowerPoints() {
		//Currently the only thing that affects weapon power is the equipment
		return Math.max(getCarriedItems().getEquipment().getWeaponPower(), 1);
	}

	public int getPrayerPoints() {
		//Currently the only thing that affects prayer is the equipment
		return Math.max(getCarriedItems().getEquipment().getPrayer(), 1);
	}

	public int getMagicPoints() {
		//Currently the only thing that affects prayer is the equipment
		return Math.max(getCarriedItems().getEquipment().getMagic(), 1);
	}

	public int getHidingPoints() {
		//Currently the only thing that affects hiding is the equipment
		//no equipment known to have given hiding
		return 1;
		//return Math.max(getCarriedItems().getEquipment().getHiding(), 1);
	}

	public int[] getWornItems() {
		return wornItems;
	}

	public void setWornItems(final int[] worn) {
		wornItems = worn;
		getUpdateFlags().setAppearanceChanged(true);
	}

	public int getPrayerStatePoints() {
		return prayerStatePoints;
	}

	public void setPrayerStatePoints(int prayerStatePoints) {
		this.prayerStatePoints = prayerStatePoints;
	}

	public void handleWakeup() {
		fatigue = sleepStateFatigue;
		ActionSender.sendFatigue(this);
	}

	public void incQuestExp(final int i, final int amount, final boolean useFatigue) {
		int appliedAmount = amount;
		if (!isOneXp())
			appliedAmount = (int) Math.round(getExperienceMultiplier(i) * amount);
		if (isExperienceFrozen()) {
			ActionSender.sendMessage(this, "You passed on " + appliedAmount / 4 + " " +
				getWorld().getServer().getConstants().getSkills().getSkill(i).getLongName() + " experience because your exp is frozen.");
			return;
		}
		incExp(i, amount, useFatigue, true);
	}

	/**
	 * Gets the experience multiplier for the player, based on the server's configurations
	 *
	 * @param skill The skill the player is training
	 * @return The modifier that should be applied to the player's gained XP
	 */
	private double getExperienceMultiplier(final int skill) {
		double multiplier = 1.0;

		// Check to see if the server has double XP enabled.
		if (getConfig().IS_DOUBLE_EXP) {
			multiplier *= 2.0;
		}

		// If the player has opted into 1x, they get no multipliers
		// (save for the DXP multiplier if enabled).
		if (isOneXp()) return multiplier;

		// Check if the skill is a non-combat skill and
		// apply the non-combat skilling rate.
		int[] skillIDs = {
			Skill.ATTACK.id(), Skill.DEFENSE.id(), Skill.STRENGTH.id(), Skill.HITS.id(),
			Skill.RANGED.id(), Skill.PRAYGOOD.id(), Skill.PRAYEVIL.id(), Skill.PRAYER.id(),
			Skill.GOODMAGIC.id(), Skill.EVILMAGIC.id(), Skill.MAGIC.id()
		};
		if (!DataConversions.inArray(skillIDs, skill)) {
			multiplier = getConfig().SKILLING_EXP_RATE;
		}

		// Otherwise apply the combat skilling rate.
		else {
			multiplier = getConfig().COMBAT_EXP_RATE;
		}

		// Apply the Wilderness and Skull multipliers.
		// You won't get the Wilderness multiplier if you're standing in the mage bank entrance.
		if (getLocation().inWilderness() && !getLocation().inBounds(220, 108, 225, 111)) {
			multiplier += getConfig().WILDERNESS_BOOST;
			if (isSkulled()) {
				multiplier += getConfig().SKULL_BOOST;
			}
		}

		return multiplier;
	}

	public void incExp(final int[] skillDist, int skillXP, final boolean useFatigue) {
		// If player was 100% fatigue, OG RSC always sent out 4 messages with melee kill,
		// regardless if vs pvp or npc or if specific attack style was used
		if (getWorld().getServer().getConfig().WANT_FATIGUE && useFatigue && fatigue >= this.MAX_FATIGUE) {
			for (int i = 0; i < 4; i++) {
				ActionSender.sendMessage(this, "@gre@You are too tired to gain experience, get some rest!");
			}
		} else {
			int xp;
			for (int i = 0; i < skillDist.length; i++) {
				xp = skillXP * skillDist[i];
				if (xp == 0) continue;
				incExp(i, xp, useFatigue, false);
			}
		}
	}

	public void incExp(final int skill, int origSkillXP, final boolean useFatigue) {
		incExp(skill, origSkillXP, useFatigue, false);
	}

	public void incExp(final int skill, int origSkillXP, final boolean useFatigue, final boolean fromQuest) {
		// Warn the player that they currently cannot gain XP.
		if (isExperienceFrozen()) {
			if (getWorld().getServer().getConfig().WANT_FATIGUE) {
				ActionSender.sendMessage(this, "You can not gain experience right now!");
			}

			// If we have fatigue disabled, that means the player has slept to disable XP
			// gain. We will tell them once per login, to make sure that they didn't do it
			// by accident.
			else if (!this.getAttribute("warned_xp_off", false)) {
				ActionSender.sendMessage(this, "You have disabled experience gain." +
					"Use a sleeping bag or bed to re-enable,");
				this.setAttribute("warned_xp_off", true);
			}
			return;
		}

		int skillXP = origSkillXP;
		boolean doubledXp = false;

		if (useFatigue && !fromQuest
			&& inArray(skill, Skill.HERBLAW.id(), Skill.CRAFTING.id(), Skill.FLETCHING.id(),
			Skill.SMITHING.id(), Skill.RUNECRAFT.id(), Skill.COOKING.id())
			&& EnchantedCrowns.shouldActivate(this, ItemId.CROWN_OF_THE_ARTISAN)) {
			skillXP *= 2;
			doubledXp = true;
		}

		if (getWorld().getServer().getConfig().WANT_FATIGUE) {
			// If the action uses fatigue, and the player is too tired,
			// send a message saying so, and do not give xp.
			if (useFatigue) {
				if (fatigue >= this.MAX_FATIGUE) {
					ActionSender.sendMessage(this, "@gre@You are too tired to gain experience, get some rest!");
					return;
				}
				//if (fatigue >= 139500) {
				//	ActionSender.sendMessage(this, "@gre@You start to feel tired, maybe you should rest soon.");
				//}

				// Give fatigue for non-melee skills (all skills after skill ID 4)
				int[] skillIDs = {
					Skill.ATTACK.id(), Skill.DEFENSE.id(), Skill.STRENGTH.id(), Skill.HITS.id()
				};
				if (!DataConversions.inArray(skillIDs, skill)) {
					fatigue += skillXP * 8;
				}

				// Give fatigue for melee skills (all skills between skill ID 0 and 3 inclusive)
				else {
					fatigue += skillXP * 5;
				}
				if (fatigue > this.MAX_FATIGUE) {
					fatigue = this.MAX_FATIGUE;
				}
			}
		}

		if (doubledXp) {
			this.playerServerMessage(MessageType.QUEST, "Your crown shines and you become more experienced");
			EnchantedCrowns.useCharge(this, ItemId.CROWN_OF_THE_ARTISAN);
		}

		// Player cannot gain more than 200 fishing xp on tutorial island
		if (getLocation().onTutorialIsland()) {
			if (getSkills().getExperience(skill) + skillXP > 200) {
				if (skill == Skill.FISHING.id()) {
					getSkills().setExperience(skill, 200);
				}
			}
		}

		// This is how much XP will be given to this player at the end.
		// If they aren't in a party, or if there aren't any players that are close
		// enough, this player will get all the XP.
		int thisXp = skillXP;

		// Check if the player is an Ironman and in a party
		final boolean notIronMan = getConfig().PARTY_IRON_MAN_CAN_SHARE || !this.isIronMan();
		if (getConfig().WANT_PARTY_XP_SHARE && this.getParty() != null && notIronMan) {
			ArrayList<PartyPlayer> sharers = new ArrayList<PartyPlayer>();
			int xpLeftToReward = skillXP;

			// Get the players to share with
			for (PartyPlayer partyMember : getParty().getPlayers()) {
				final Player partyMemberPlayer = partyMember.getPlayerReference();

				// Make sure the player is in range.
				final boolean inRange = getConfig().PARTY_SHARE_INFINITE_RANGE
					|| (Formulae.getHeight(this.getLocation()) == Formulae.getHeight(partyMemberPlayer.getLocation()) && Math.abs(this.getX() - partyMemberPlayer.getX()) <= getConfig().PARTY_SHARE_MAX_X
					&& Math.abs(normalizeFloor(this.getY()) - normalizeFloor(partyMemberPlayer.getY())) <= getConfig().PARTY_SHARE_MAX_Y);

				// Make sure the player isn't on the same IP
				final boolean notSameIp = getConfig().PARTY_SHARE_WITH_SAME_IP || !this.getCurrentIP().equals(partyMemberPlayer.getCurrentIP());

				// Make sure the player isn't an Ironman
				final boolean isntIronMan = getConfig().PARTY_IRON_MAN_CAN_SHARE || !partyMemberPlayer.isIronMan();

				// Make sure the party member isn't this!!
				final boolean notMe = !this.equals(partyMemberPlayer);

				if (inRange && notSameIp && isntIronMan && notMe) {
					sharers.add(partyMember);
				}
			}

			int shareCount = sharers.size();
			if (shareCount > 0) {
				// Include this player in the math
				shareCount++;

				// Do some maths to get the XP to reward
				switch (getConfig().PARTY_SHARE_SIZE_ALGORITHM) {
					case "linear":
						xpLeftToReward *= 1.0 + (getConfig().PARTY_ADDITIONAL_XP_PERCENT_PER_PLAYER
							* Math.min(shareCount, getConfig().PARTY_MAX_SIZE_FOR_ADDITIONAL_XP));
						break;
					case "exponential":
						xpLeftToReward *= Math.pow(1.0 + getConfig().PARTY_ADDITIONAL_XP_PERCENT_PER_PLAYER,
							Math.min(shareCount, getConfig().PARTY_MAX_SIZE_FOR_ADDITIONAL_XP));
						break;
					default:
						LOGGER.error("Unrecognized PARTY_SHARE_SIZE_ALGORITHM provided in config");
						break;
				}

				// The total XP that should be awarded out to the party
				final int totalXpToReward = xpLeftToReward;
				// The max XP that each player besides the skiller should get
				final int maxXpPerSharedPlayer = (int) (((1.0 / shareCount) * (1.0 - getConfig().PARTY_SAVE_XP_FOR_SKILLER_PERCENT)) * totalXpToReward);

				// Calculate and award XP to each party member
				for (PartyPlayer partyMember : sharers) {
					final Player partyMemberPlayer = partyMember.getPlayerReference();

					double xpDropoffPercent = 1.0;
					final int playerDistance = Math.abs(this.getX() - partyMemberPlayer.getX())
						+ Math.abs(normalizeFloor(this.getY()) - normalizeFloor(partyMemberPlayer.getY()));

					// Decrease the amount of XP the player gets depending on how far away they are
					switch (getConfig().PARTY_SHARE_DISTANCE_ALGORITHM) {
						case "linear":
							xpDropoffPercent *= 1.0 - (getConfig().PARTY_DISTANCE_PERCENT_DECREASE
								* playerDistance);
							break;
						case "exponential":
							xpDropoffPercent *= Math.pow(1.0 - getConfig().PARTY_DISTANCE_PERCENT_DECREASE,
								playerDistance);
							break;
						default:
							LOGGER.error("Unrecognized PARTY_SHARE_DISTANCE_ALGORITHM provided in config");
							break;
					}

					// Award XP to the party member
					int playerXp = (int) (maxXpPerSharedPlayer * xpDropoffPercent);
					xpLeftToReward -= playerXp;
					playerXp *= partyMemberPlayer.getExperienceMultiplier(skill);
					if (getConfig().WANT_OPENPK_POINTS) {
						partyMemberPlayer.addOpenPkPoints(playerXp);
					} else {
						partyMemberPlayer.getSkills().addExperience(skill, playerXp);
					}
				}
				thisXp = xpLeftToReward;
			}
		}

		// Update this player's XP.
		thisXp = Math.min(thisXp, skillXP);
		thisXp *= getExperienceMultiplier(skill);
		if (getConfig().WANT_OPENPK_POINTS) {
			addOpenPkPoints(thisXp);
		} else {
			getSkills().addExperience(skill, thisXp);
		}

		// packet order; fatigue update comes after XP update authentically.
		// still, will need to check fatigue is not too high before awarding XP, so this check is in 2 places
		if (getWorld().getServer().getConfig().WANT_FATIGUE) {
			if (useFatigue) {
				ActionSender.sendFatigue(this);
			}
		}
	}

	public void incQuestPoints(final int amount) {
		setQuestPoints(getQuestPoints() + amount);
	}

	public void incrementSleepTries() {
		if (getWorld().getServer().getConfig().WANT_FATIGUE) {
			incorrectSleepTries++;
		}
	}

	private void incrementActivity(final int amount) {
		if (!(getWorld().getServer().getConfig().RESTRICT_ITEM_ID >= 0 && getWorld().getServer().getConfig().RESTRICT_ITEM_ID < ItemId.CAT.id())) {
			activity += amount;
			if (activity >= KITTEN_ACTIVITY_THRESHOLD) {
				activity -= KITTEN_ACTIVITY_THRESHOLD;
				getWorld().getServer().getPluginHandler().handlePlugin(CatGrowthTrigger.class, this, new Object[]{this});
			}
		}
	}

	/*
	 * Called on periodic saves
	 */
	public void timeIncrementActivity() {
		incrementActivity(5);
	}

	/*
	 * Called when walking a single step
	 */
	public void stepIncrementActivity() {
		incrementActivity(2);
	}

	public void changeZone() {
		if (getConfig().FERMENTED_WINE ||
			(getConfig().RESTRICT_ITEM_ID >= 0 && getConfig().RESTRICT_ITEM_ID < ItemId.CHEESE.id())) {
			getWorld().getServer().getPluginHandler().handlePlugin(WineFermentTrigger.class, this, new Object[]{this});
		}
	}

	public int getGroupID() {
		return groupID;
	}

	public void setGroupID(final int id) {
		getUpdateFlags().setAppearanceChanged(true);
		groupID = id;
	}

	public boolean isOwner() {
		return groupID == Group.OWNER;
	}

	public void setOwner(final int owner) {
		this.owner = owner;
	}

	public boolean isAdmin() {
		return groupID == Group.ADMIN || isOwner();
	}

	public boolean isSuperMod() {
		return groupID == Group.SUPER_MOD || isAdmin();
	}

	public boolean isMod() {
		return groupID == Group.MOD || isSuperMod();
	}

	public boolean isPlayerMod() {
		return groupID == Group.PLAYER_MOD || isMod();
	}

	public boolean isDev() {
		return groupID == Group.DEV || isAdmin();
	}

	public boolean isEvent() {
		return groupID == Group.EVENT || isMod() || isDev();
	}

	public boolean hasElevatedPriveledges() {
		switch (groupID) {
			case Group.OWNER:
			case Group.ADMIN:
			case Group.SUPER_MOD:
			case Group.MOD:
				return true;
		}
		return false;
	}

	public boolean isDefaultUser() {
		return groupID == Group.DEFAULT_GROUP;
	}

	public boolean isChangingAppearance() {
		return changingAppearance;
	}

	public void setChangingAppearance(boolean b) {
		changingAppearance = b;
	}

	public boolean isChangingRecovery() {
		return changingRecovery;
	}

	public void setChangingRecovery(boolean b) {
		changingRecovery = b;
	}

	public boolean isChangingDetails() {
		return changingDetails;
	}

	public void setChangingDetails(boolean b) {
		changingDetails = b;
	}

	public boolean isAntidoteProtected() {
		return System.currentTimeMillis() - lastAntidote < poisonProtectionTime;
	}

	public boolean isInBank() {
		return inBank;
	}

	public void setInBank(final boolean inBank) {
		this.inBank = inBank;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(final boolean loggedIn) {
		if (loggedIn) {
			currentLogin = System.currentTimeMillis();
			if (getCache().hasKey("poisoned")) {
				startPoisonEvent();
				PoisonEvent poisonEvent = getAttribute("poisonEvent", null);
				poisonEvent.setPoisonPower(getCache().getInt("poisoned"));
			}
			if (!getConfig().LACKS_PRAYERS) {
				prayerStatePoints = getSkills().getLevel(Skill.PRAYER.id()) * 120;
				prayerDrainEvent = new PrayerDrainEvent(getWorld(), this);
				getWorld().getServer().getGameEventHandler().add(prayerDrainEvent);
			}
			getWorld().getServer().getGameEventHandler().add(getStatRestorationEvent());
		}
		this.loggedIn = loggedIn;
	}

	public void toggleDenyAllLogoutRequests() {
		if (isMod() || isAdmin() || isDev() || isEvent()) {
			denyAllLogoutRequests = !denyAllLogoutRequests;
			if (denyAllLogoutRequests) {
				playerServerMessage(MessageType.QUEST, "All logout requests will now be @red@denied.");
				playerServerMessage(MessageType.QUEST, "Type @or2@::stayin@whi@ to toggle this.");
			} else {
				playerServerMessage(MessageType.QUEST, "Logout requests will now be @gre@possible to fulfill.");
				playerServerMessage(MessageType.QUEST, "Type @or2@::stayin@whi@ to toggle this.");
			}
		}
	}

	public boolean getDenyAllLogoutRequests() {
		return denyAllLogoutRequests;
	}

	public boolean isMale() {
		return maleGender;
	}

	public void setMale(final boolean male) {
		maleGender = male;
	}

	public boolean isMuted() {
		final long muteExpires = getMuteExpires();
		if (muteExpires == 0)
			return false;
		if (muteExpires == -1)
			return true;

		return muteExpires - System.currentTimeMillis() > 0;
	}

	public boolean isGlobalMuted() {
		if (getCache().hasKey("global_mute")) {
			final long globalMute = getCache().getLong("global_mute");
			return globalMute - System.currentTimeMillis() > 0 || globalMute == -1;
		}
		return false;
	}

	public boolean isRanging() {
		return rangeEvent != null || throwingEvent != null;
	}

	public boolean isReconnecting() {
		return reconnecting;
	}

	public void setReconnecting(final boolean reconnecting) {
		this.reconnecting = reconnecting;
	}

	public boolean isRequiresOfferUpdate() {
		return requiresOfferUpdate;
	}

	public void setRequiresOfferUpdate(final boolean b) {
		requiresOfferUpdate = b;
	}

	public boolean isSkulled() {
		return skullEvent != null;
	}

	public boolean isCharged() {
		return chargeEvent != null;
	}

	public int getSkullType() {
		if (isSkulled()) {
			return 1;
		}
		return 0;
	}

	public boolean isSleeping() {
		return sleeping;
	}

	public void setSleeping(final boolean isSleeping) {
		this.sleeping = isSleeping;
	}

	public boolean isSuspiciousPlayer() {
		return suspiciousPlayer;
	}

	public void setSuspiciousPlayer(final boolean suspicious, final String reason) {
		suspiciousPlayer = suspicious;
		LOGGER.info("player " + getUsername() + " suspicious for " + reason);
		// Disabled because this is currently overzealous
		/*if (suspicious) {
			getWorld().getServer().getPacketFilter().ipBanHost(getCurrentIP(), System.currentTimeMillis() + ServerConfiguration.SUSPICIOUS_PLAYER_IP_BAN_MINUTES * 60 * 1000, reason);
		}*/
	}

	@Override
	public void killedBy(final Mob mob) {
		if (!isLoggedIn()) return;
		if (killed) return;
		killed = true;

		ActionSender.sendSound(this, "death");
		ActionSender.sendDied(this);

		// Cabbage tutorial skip
		if (this.getLocation().onTutorialIsland()
			&& (mob.isNpc() && mob.getID() == NpcId.PETER_SKIPPIN.id())) {
			killed = false;
			resetCombatEvent();
			setLastOpponent(null);
			getSkills().setLevel(Skill.HITS.id(), getSkills().getMaxStat(Skill.HITS.id()));
			setBusy(false);
			skipTutorial();
			return;
		}

		// Seems to never be set
		final ProjectileEvent projectileEvent = getAttribute("projectile");
		if (projectileEvent != null) projectileEvent.setCanceled(true);

		getSettings().getAttackedBy().clear();
		getCache().store("last_death", System.currentTimeMillis());

		final Player player = mob instanceof Player ? (Player) mob : null;

		if (player != null) {
			player.message(String.format("You have defeated %s!", getUsername()));
			ActionSender.sendSound(player, "victory");

			if (player.getLocation().inWilderness()) {
				final int killTypeId;

				switch (player.getKillType()) {
					case COMBAT:
						final int weaponId = player.getEquippedWeaponID();

						if (weaponId == ItemId.NOTHING.id() || weaponId == ItemId.PHOENIX_CROSSBOW.id() ||
							weaponId == ItemId.CROSSBOW.id()) {
							killTypeId = 16;
						} else {
							killTypeId = weaponId;
						}
						break;
					case RANGED:
						killTypeId = -2;
						break;
					case MAGIC:
					default:
						killTypeId = -1;
						break;
				}

				getWorld().sendKilledUpdate(getUsernameHash(), player.getUsernameHash(), killTypeId);
				player.incKills();
				incDeaths();
				getWorld().getServer().getGameLogger().addQuery(new LiveFeedLog(player, String.format("has PKed %s", getUsername())));
			}

			// Defense skillcape message
			int totalBlockedDamage = player.getTrackedBlockedDamage(this);
			if (totalBlockedDamage > 0) {
				player.playerServerMessage(MessageType.QUEST, "@dcy@Your defense cape blocked " + totalBlockedDamage + " damage!");
			}


			// Reset the tracked damage for anyone who was attacked by this player
			for (Player curPlayer : getWorld().getPlayers()) {
				if (curPlayer.getTrackedDamage(this) != -1) {
					curPlayer.resetTrackedDamageAndBlockedDamage(this);
				}
			}
		}

		// Drops to world if player is null
		getWorld().registerItem(new GroundItem(getWorld(), ItemId.BONES.id(), getX(), getY(), 1, player));

		if (getDuel().isDuelActive() || (player != null && player.getDuel().isDuelActive())) {
			getDuel().dropOnDeath();
			 // disables duel spam in activity feed
			 // if (player != null) getWorld().getServer().getGameLogger().addQuery(new LiveFeedLog(player, String.format("has just won a stake against <strong>%s</strong>", username)));
		} else if (!hasElevatedPriveledges()) {
			getCarriedItems().getInventory().dropOnDeath(mob);
		}

		if (isIronMan(IronmanMode.Hardcore.id())) {
			updateHCIronman(IronmanMode.Ironman.id());
			ActionSender.sendIronManMode(this);
			getWorld().getServer().getGameLogger().addQuery(new LiveFeedLog(this, "has died and lost the HC Ironman Rank!"));
		}

		resetCombatEvent();
		setLastOpponent(null);

		if (getCache().hasKey("death_location_x") || getCache().hasKey("death_location_y")) {
			// Seems to never be set
			setLocation(Point.location(getCache().getInt("death_location_x"), getCache().getInt("death_location_y")), true);
		} else {
			setLocation(Point.location(getConfig().RESPAWN_LOCATION_X, getConfig().RESPAWN_LOCATION_Y), true);
		}

		ActionSender.sendWorldInfo(this);
		ActionSender.sendEquipmentStats(this);
		ActionSender.sendInventory(this);

		resetPath();

		final boolean party = getWorld().getServer().getConfig().WANT_PARTIES && getParty() != null;
		if (party) getParty().sendParty();

		cure();

		// OG RSC did not reset active prayers after death
		// prayers.resetPrayers();
		getSkills().normalize();

		if (party) getParty().sendParty();

		getUpdateFlags().reset();
		removeSkull();

		getWorld().getServer().getGameEventHandler().add(
			new DelayedEvent(getWorld(), this, getConfig().GAME_TICK * 5L, "Reset Killed") {
				@Override
				public void run() {
					getOwner().killed = false;
					stop();
				}
			}
		);
	}

	private int getEquippedWeaponID() {
		if (getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			Item i = getCarriedItems().getEquipment().get(4);
			if (i != null)
				return i.getCatalogId();
		} else {
			synchronized (getCarriedItems().getInventory().getItems()) {
				for (Item i : getCarriedItems().getInventory().getItems()) {
					if (i.isWielded() && (i.getDef(getWorld()).getWieldPosition() == 4))
						return i.getCatalogId();
				}
			}
		}
		return -1;
	}

	public boolean loggedIn() {
		return loggedIn;
	}

	public void message(final String string) {
		// resetMenuHandler();
		// setOption(-1);
		ActionSender.sendMessage(this, string);
	}

	public void playerServerMessage(final MessageType type, final String string) {
		ActionSender.sendPlayerServerMessage(this, type, string);
	}

	public void teleport(final int x, final int y) {
		teleport(x, y, false);
	}

	public void addPrivateMessage(final PrivateMessage privateMessage) {
		if (getPrivateMessageQueue().size() < 2) {
			getPrivateMessageQueue().add(privateMessage);
		}
	}

	public void updateClientActivity() {
		lastClientActivity = System.currentTimeMillis();
	}

	public void playSound(final String sound) {
		ActionSender.sendSound(this, sound);
	}

	public void checkForMouseMovement(final boolean movedMouse) {
		if (!movedMouse) {
			actionsMouseStill++;

			float minutesFlagged = (float) (System.currentTimeMillis() - lastMouseMoved) / (float) 60000;
			if (actionsMouseStill >= 30 && minutesFlagged >= 1) {
				String string = "Check " + getUsername() + "! " + actionsMouseStill
					+ " actions with mouse still. Mouse was last moved " + String.format("%.02f", minutesFlagged)
					+ " mins ago";

				for (Player player : getWorld().getPlayers()) {
					if (player.isMod()) {
						player.message("@red@Server@whi@: " + string);
					}
				}
				setSuspiciousPlayer(true, "mouse movement check");
			}
		} else {
			actionsMouseStill = 0;
			lastMouseMoved = System.currentTimeMillis();
		}
	}

	public void addToPacketQueue(final Packet packet) {
		updateClientActivity();
		int packetID = packet.getID();

		if (incomingPackets.size() <= getWorld().getServer().getConfig().PACKET_LIMIT) {
			synchronized (incomingPackets) {
				incomingPackets.add(packet);
				activePackets.add(packetID);
			}
		}
	}

	public void processTick() {
		getWorld().getServer().incrementLastIncomingPacketsDuration(processIncomingPackets());
		getWorld().getServer().incrementLastExecuteWalkToActionsDuration(
			getWorld().getServer().getGameUpdater().executeWalkToActions(this));
		getWorld().getServer().incrementLastEventsDuration(
			getWorld().getServer().getGameEventHandler().runPlayerEvents(this));
		getWorld().getServer().incrementLastProcessPlayersDuration(
			getWorld().getServer().getGameUpdater().movePlayer(this));
		getWorld().getServer().incrementLastProcessMessageQueuesDuration(
			getWorld().getServer().getGameUpdater().processMessageQueue(this));
	}

	public void updatePosition() {
		Npc npc = getInteractingNpc();
		NpcInteraction interaction = getNpcInteraction();
		super.updatePosition();

		if (npc != null) {
			switch (interaction) {
				case NPC_TALK_TO:
				case NPC_GNOMEBALL_OP:
				case NPC_USE_ITEM:
					if (!inCombat() && finishedPath()) face(npc);
					break;
				case NPC_OP:
				default:
					break;
			}
		}

		if (isFollowing()
			&& getEndFollowRadius() > -1
			&& getFollowEvent().getTimesRan() >= 1
			&& withinRange(getFollowing(), getEndFollowRadius())
			&& PathValidation.checkAdjacentDistance(getWorld(), getLocation(), getFollowing().getLocation(), true, false)) {
			resetFollowing();
			resetPath();
		}
	}

	public void processLogout() {
		// any time that `Player.unregister(force, reason)` was called throughout a tick,
		// now is the time to process the logic for if they are allowed to log out.
		if (hasUnregisterRequest()) {
			getUnregisterRequest().executeUnregisterRequest();
			unsetUnregisterRequest();
		}

		// Check isLoggedIn() because we don't want to unregister more than once
		if (this.isUnregistering() && this.isLoggedIn()) {
			this.getWorld().unregisterPlayer(this);
		}
	}

	public void sendUpdates() {
		getWorld().getServer().incrementLastUpdateClientsDuration(
			getWorld().getServer().getGameUpdater().updateClient(this));
		getWorld().getServer().incrementLastOutgoingPacketsDuration(processOutgoingPackets());
	}

	public long processIncomingPackets() {
		return getWorld().getServer().bench(() -> {
			if (channel == null || (!channel.isOpen() && !channel.isWritable())) {
				return;
			}
			synchronized (incomingPackets) {
				Packet packet = incomingPackets.poll();
				while (packet != null) {
					// Final copied variable needed to pass into lambda
					final Packet curPacket = packet;
					final long packetTime = getWorld().getServer().bench(
						() -> {
							activePackets.remove(activePackets.indexOf(curPacket.getID()));
							PayloadParser<com.openrsc.server.net.rsc.enums.OpcodeIn> parser;
							if (isUsing38CompatibleClient() || isUsing39CompatibleClient()) {
								parser = new Payload38Parser();
							} else if (isUsing69CompatibleClient()) {
								parser = new Payload69Parser();
							} else if (isUsing233CompatibleClient()) {
								parser = new Payload235Parser();
							} else if (isUsing203CompatibleClient()) {
								parser = new Payload203Parser();
							} else if (isUsing202CompatibleClient()) {
								parser = new Payload202Parser();
							} else if (isUsing201CompatibleClient()) {
								parser = new Payload201Parser();
							} else if (isUsing199CompatibleClient()) {
								parser = new Payload199Parser();
							} else if (isUsing198CompatibleClient()) {
								parser = new Payload198Parser();
							} else if (isUsing196CompatibleClient()) {
								parser = new Payload196Parser();
							} else if (isUsing177CompatibleClient()) {
								parser = new Payload177Parser();
							} else if (isUsing140CompatibleClient()) {
								parser = new Payload140Parser();
							} else if (isUsing115CompatibleClient()) {
								parser = new Payload115Parser();
							} else {
								parser = new PayloadCustomParser();
							}
							AbstractStruct<com.openrsc.server.net.rsc.enums.OpcodeIn> res = parser.parse(curPacket, this);
							if (res != null) {
								boolean couldProcess;
								try {
									couldProcess = PayloadProcessorManager.processed(res, this);
								} catch (final Exception e) {
									LOGGER.catching(e);
									couldProcess = false;
								}
								if (!couldProcess) {
									unregister(UnregisterForcefulness.WAIT_UNTIL_COMBAT_ENDS, "Malformed packet!");
								}
							}
						}
					);
					getWorld().getServer().addIncomingPacketDuration(curPacket.getID(), packetTime);
					getWorld().getServer().incrementIncomingPacketCount(curPacket.getID());

					packet = incomingPackets.poll();
				}

				incomingPackets.clear();
			}
		});
	}

	public long processOutgoingPackets() {
		// Unsure if we want to clear right now. Probably OK not to since the player should be cleaned up when the channel is no longer open.
		/*if(!channel.isOpen() || !isLoggedIn()) {
			outgoingPackets.clear();
		}*/

		return getWorld().getServer().bench(() -> {
			if (!channel.isOpen() || !isLoggedIn() || !channel.isActive() || !channel.isWritable()) {
				return;
			}
			synchronized (outgoingPackets) {
				try {
					for (final Packet outgoing : outgoingPackets) {
						final long packetTime = getWorld().getServer().bench(
							() -> {
								channel.writeAndFlush(outgoing);
							}
						);
						getWorld().getServer().addOutgoingPacketDuration(outgoing.getID(), packetTime);
						getWorld().getServer().incrementOutgoingPacketCount(outgoing.getID());
					}
				} catch (final Exception e) {
					LOGGER.catching(e);
				}
				//channel.flush();
				outgoingPackets.clear();
			}
		});
	}

	public void removeSkull() {
		if (skullEvent != null) {
			skullEvent.stop();
			skullEvent = null;
		}
		cache.remove("skull_remaining");
		getUpdateFlags().setAppearanceChanged(true);
	}

	public boolean requiresOfferUpdate() {
		return requiresOfferUpdate;
	}

	public void resetAll() {
		resetAll(true, true);
	}

	public void resetAll(boolean resetWalkAction, boolean resetFollowing) {
		interruptPlugins();
		Npc npc = getInteractingNpc();
		if (npc != null && npc.getInteractingPlayer() != null && npc.getInteractingPlayer().equals(this)) {
			npc.setNpcInteraction(null);
			npc.setInteractingPlayer(null);
		}
		setNpcInteraction(null);
		setInteractingNpc(null);
		resetAllExceptTradeOrDuel(true, resetWalkAction, resetFollowing);
		getTrade().resetAll();
		getDuel().resetAll();
		dropItemEvent = null;
		setAttribute("auctionhouse", false);
	}

	public void resetAllExceptBank() {
		resetAllExceptTradeOrDuel(false);
		getTrade().resetAll();
		getDuel().resetAll();
	}

	public void resetAllExceptDueling() {
		resetAllExceptTradeOrDuel(true);
		getTrade().resetAll();
	}

	private void resetAllExceptTradeOrDuel(boolean resetBank) {
		resetAllExceptTradeOrDuel(resetBank, true, true);
	}

	private void resetAllExceptTradeOrDuel(boolean resetBank, boolean resetWalkAction, boolean resetFollowing) {
		resetCannonEvent();
		setAttribute("bank_pin_entered", "cancel");

		if (resetWalkAction && getWalkToAction() != null) {
			setWalkToAction(null);
		}

		if (getMenu() != null) {
			menu = null;
		}
		if (getMenuHandler() != null) {
			resetMenuHandler();
		}
		if (accessingBank() && resetBank) {
			resetBank();
		}
		if (accessingShop()) {
			resetShop();
		}
		if (resetFollowing && isFollowing()) {
			resetFollowing();
		}
		if (resetFollowing && getPossessing() != null) {
			resetFollowing();
		}
		if (isRanging()) {
			resetRange();
		}
	}

	public void resetAllExceptTrading() {
		resetAllExceptTradeOrDuel(true);
		getDuel().resetAll();
	}

	public void resetBank() {
		setAccessingBank(false);
		ActionSender.hideBank(this);
	}

	public void resetMenuHandler() {
		resetMenuHandler(true);
	}

	public void resetMenuHandler(boolean hideMenu) {
		setOption(-1);
		menu = null;
		menuHandler = null;
		if (hideMenu)
			ActionSender.hideMenu(this);
	}

	public void cancelMenuHandler() {
		if (menuHandler != null) {
			canceledMenuHandler = true;
			resetMenuHandler(false);
			setBusy(false);
		}
	}

	public void resetRange() {
		if (rangeEvent != null) {
			rangeEvent.stop();
			rangeEvent = null;
		}
		if (throwingEvent != null) {
			throwingEvent.stop();
			throwingEvent = null;
		}
	}

	public void resetShop() {
		if (shop != null) {
			shop.removePlayer(this);
			shop = null;
			ActionSender.hideShop(this);
		}
	}

	public void resetSleepTries() {
		incorrectSleepTries = 0;
	}

	public void save() {
		save(false, false);
	}

	public void save(boolean logout, boolean force) {
		//If we want to log out (but we already mass-saved earlier in the same tick), we prioritize logging out over mass-saves so the player can log out the same tick. We make sure to check if they are already logging out in the same tick so that we only have one logout save per tick per player. Force saves always save.
		if ((!logout || isLoggingOut()) && isSaving() && !force) {
			return;
		}
		setSaving(true);
		if (logout) {
			setLoggingOut(true);
		}
		getWorld().getServer().getLoginExecutor().add(new PlayerSaveRequest(getWorld().getServer(), this, logout));
	}

	public void logout() {
		LOGGER.info("Player logout requested for " + this.getUsername());
		try {
			ActionSender.sendLogoutRequestConfirm(this);
		} catch (NullPointerException ex) {
			LOGGER.info("Connection closed quickly for " + this.getUsername());
		}

		FishingTrawler trawlerInstance = getWorld().getFishingTrawler(this);

		resetAll();

		Mob opponent = getOpponent();
		if (opponent != null) {
			resetCombatEvent();
		}

		Mob lastOpponent = getLastOpponent();
		if (lastOpponent != null && this.equals(lastOpponent.getLastOpponent())) {
			lastOpponent.setLastOpponent(null);
		}

		this.setLastOpponent(null);
		if (trawlerInstance != null && trawlerInstance.getPlayers().contains(this)) {
			trawlerInstance.disconnectPlayer(this, true);
		}
		if (getLocation().inMageArenaLogOutZone()) {
			teleport(228, 109); // see note in Point.java (Goto declaration of inMageArenaLogOutZone())
		}
		if (getLocation().inIbansChamberLogOutZone()) {
			teleport(791, 3469); // see [Logg/Tylerbeg/07-19-2018 11.11.46 log back in outside iban's chamber]
		}

		if (getParty() != null) {
			getParty().removePlayer(this.getUsername());
		}
		// store kitten growth progress
		getCache().set("kitten_events", getAttribute("kitten_events", 0));
		getCache().set("kitten_hunger", getAttribute("kitten_hunger", 0));
		getCache().set("kitten_loneliness", getAttribute("kitten_loneliness", 0));
		// any gnome ball progress
		getCache().set("gnomeball_goals", getAttribute("gnomeball_goals", 0));
		getCache().set("gnomeball_npc", getAttribute("gnomeball_npc", 0));

		save(true, false);
		LOGGER.info("Player save & logout request queued for " + this.getUsername());
	}

	public void sendMemberErrorMessage() {
		message(MEMBER_MESSAGE);
	}

	public void sendQuestComplete(final int questId) { // REMEMBER THIS
		if (getQuestStage(questId) != -1) {
			getWorld().getQuest(questId).handleReward(this);
			updateQuestStage(questId, -1);
			ActionSender.sendStats(this);
			getWorld().getServer().getGameLogger().addQuery(new LiveFeedLog(this,
				"has completed the " + getWorld().getQuest(questId).getQuestName()
					+ " quest and now has " + this.getQuestPoints() + " quest points!"));
		}
	}

	public void sendMiniGameComplete(final int miniGameId, final Optional<String> message) {
		getWorld().getMiniGame(miniGameId).handleReward(this);
		getWorld().getServer().getGameLogger().addQuery(new LiveFeedLog(this,
			"has completed the " + getWorld().getMiniGame(miniGameId).getMiniGameName()
				+ " minigame! " + (message.orElse(""))));
	}

	public void setAccessingBank(final boolean b) {
		inBank = b;
	}

	public void setAccessingShop(final Shop shop) {
		this.shop = shop;
		if (shop != null) {
			shop.addPlayer(this);
		}
	}

	public void setCastTimer(final long timer) {
		lastSpellCast = timer;
	}

	public void setCastTimer() {
		lastSpellCast = System.currentTimeMillis();
	}

	public void setSkullTimer(final long timer) {
		lastSkullEvent = timer;
	}

	public void setChargeTimer(final long timer) {
		lastChargeEvent = timer;
	}

	public void setCurePoisonProtection() {
		// Cure poison last for 3 minutes
		// But we don't want to override a poison antidote
		long remainingProtection = (lastAntidote + poisonProtectionTime) - System.currentTimeMillis();
		if (remainingProtection > 180000) {
			return;
		}

		lastAntidote = System.currentTimeMillis();
		poisonProtectionTime = 180000;
	}

	public void setAntidoteProtection() {
		// Poison antidote last for 6 minutes
		lastAntidote = System.currentTimeMillis();
		poisonProtectionTime = 360000;
	}

	public void setLastReport() {
		lastReport = System.currentTimeMillis();
	}

	public void setLastReport(final long lastReport) {
		this.lastReport = lastReport;
	}

	public void setQuestStage(final int q, final int stage) {
		getQuestStages().put(q, stage);
	}

	public void updateQuestStage(final int q, final int stage) {
		getQuestStages().put(q, stage);
		ActionSender.sendQuestInfo(this, q, stage);
	}

	public void updateQuestStage(final QuestInterface q, final int stage) {
		getQuestStages().put(q.getQuestId(), stage);
		ActionSender.sendQuestInfo(this, q.getQuestId(), stage);
	}

	private Map<Integer, Integer> getAchievements() {
		return achievements;
	}

	public void setAchievementStatus(final int achid, final int status) {
		getAchievements().put(achid, status);

		getWorld().getServer().getAchievementSystem().achievementListGUI(this, achid, status);
	}

	public void updateAchievementStatus(final Achievement ach, final int status) {
		getAchievements().put(ach.getId(), status);

		getWorld().getServer().getAchievementSystem().achievementListGUI(this, ach.getId(), status);
	}

	public int getAchievementStatus(final int id) {
		if (getAchievements().containsKey(id)) {
			return getAchievements().get(id);
		}
		return 0;
	}

	public void setSkulledOn(final Player player) {
		player.getSettings().addAttackedBy(this);

		if ((System.currentTimeMillis() - getSettings().lastAttackedBy(player)) > 1200000) { // Checks if the player has attacked within the last 20 minutes
			addSkull(1200000); // Sets the skull timer to 20 minutes
			cache.store("skull_remaining", 1200000); // Saves the skull timer to the database if the player logs out before it expires
			cache.store("last_skull", System.currentTimeMillis() - getSettings().lastAttackedBy(player)); // Sets the last time a player had a skull
		}

		player.getUpdateFlags().setAppearanceChanged(true);
	}

	public void setSpellFail() {
		lastSpellCast = System.currentTimeMillis() + 20000;
	}

	public void startSleepEvent(final boolean bed) {
		DelayedEvent sleepEvent = new DelayedEvent(getWorld(), this, getWorld().getServer().getConfig().GAME_TICK, "Start Sleep Event", DuplicationStrategy.ONE_PER_MOB) {
			@Override
			public void run() {
				if (getOwner().isRemoved() || sleepStateFatigue == 0 || !sleeping) {
					running = false;
					return;
				}

				if (bed) {
					getOwner().sleepStateFatigue -= 42000;
				} else {
					getOwner().sleepStateFatigue -= 8400;
				}

				if (getOwner().sleepStateFatigue < 0) {
					getOwner().sleepStateFatigue = 0;
				}
				ActionSender.sendSleepFatigue(getOwner(), getOwner().sleepStateFatigue);
			}
		};
		sleepStateFatigue = fatigue;
		ActionSender.sendSleepFatigue(this, sleepStateFatigue);
		getWorld().getServer().getGameEventHandler().addOrUpdate(sleepEvent);
	}

	public void teleport(final int x, final int y, final boolean bubble) {
		if (inCombat()) {
			this.setLastOpponent(null);
			combatEvent.resetCombat();
		}

		if (bubble) {
			for (Player player : getViewArea().getPlayersInView()) {
				if (!isInvisibleTo(player)) {
					ActionSender.sendTeleBubble(player, getX(), getY(), false);
				}
			}
			ActionSender.sendTeleBubble(this, getX(), getY(), false);
		}

		setLocation(Point.location(x, y), true);
		resetPath();
		ActionSender.sendWorldInfo(this);
	}

	@Override
	public void setLocation(final Point point, final boolean teleported) {
		if (teleported || getSkullType() == 2 || getSkullType() == 0) {
			// Inappropriate place for this to be getting set at for skulls, to me.
			getUpdateFlags().setAppearanceChanged(true);
			if (teleported)
				setTeleporting(true);
		}

		if (sceneryMorph >= 0) {
			doSceneryMorphWalk(point);
		}

		super.setLocation(point, teleported);

	}

	private void doSceneryMorphWalk(Point point) {
		final GameObject morphObject = getViewArea().getGameObject(getLocation());
		if (morphObject != null) {
			resetScenery(morphObject);
		}
		final GameObject newObject = new GameObject(getWorld(), point, sceneryMorph, 0, 0);
		getWorld().registerGameObject(newObject);
	}

	public void produceUnderAttack() {
		getWorld().produceUnderAttack(this);
	}

	public boolean checkUnderAttack() {
		return getWorld().checkUnderAttack(this);
	}

	public void releaseUnderAttack() {
		getWorld().releaseUnderAttack(this);
	}

	@Override
	public String toString() {
		return "[Player:" + getIndex() + ":" + username + " @ (" + getX() + ", " + getY() + ")]";
	}

	public boolean tradeDuelThrottling() {
		long now = System.currentTimeMillis();
		if (now - lastTradeDuelRequest > 1000) {
			lastTradeDuelRequest = now;
			return false;
		}
		return true;
	}

	public void updateWornItems(final AppearanceId appearanceId) {
		this.updateWornItems(appearanceId.getSuggestedWieldPosition(), appearanceId.id());
	}

	public void updateWornItems(final int indexPosition, final AppearanceId appearanceId) {
		this.updateWornItems(indexPosition, appearanceId.id());
	}

	public void updateWornItems(final int indexPosition, final int appearanceId) {
		this.updateWornItems(indexPosition, appearanceId, 0, false);
	}

	public void updateWornItems(final int indexPosition, final int appearanceId, final int wearableId, final boolean isEquipped) {
		// metal skirts (ideally all !full pants should update pants appearance to minishorts when that anim exists)
		if (getWorld().getServer().getConfig().WANT_CUSTOM_SPRITES && wearableId == 640) {
			if (isEquipped) wornItems[AppearanceId.SLOT_PANTS] = 0;
			else wornItems[AppearanceId.SLOT_PANTS] = 3;
		}

		// Generally don't need to show arrows or rings
		if (indexPosition <= 11) {
			if (ringMorphAllows()) {
				wornItems[indexPosition] = appearanceId;
				getUpdateFlags().setAppearanceChanged(true);
			}
		} else {
			if (indexPosition == AppearanceId.SLOT_MORPHING_RING) {
				final AppearanceId newAppearance = AppearanceId.getById(appearanceId);
				if (newAppearance != AppearanceId.NOTHING) {
					// update is for equipping (not unequipping)
					if (appearanceId != AppearanceId.NOTHING.id()) {
						// ring has an appearance id, so it is a morphing ring
						enterMorph(appearanceId);
					}
				} else {
					// may or may not be morphed, but a ring was removed.
					exitMorph();
				}
			}
		}
	}

	private boolean ringMorphAllows() {
		if (getCarriedItems().getEquipment() == null) {
			return true;
		} else {
			final Item wornRing = getCarriedItems().getEquipment().getRingItem();
			if (wornRing == null ||
				wornRing.getDef(getWorld()).getAppearanceId() == AppearanceId.NOTHING.id()) {
				return true;
			}
		}
		return false;
	}

	private Queue<PrivateMessage> getPrivateMessageQueue() {
		return privateMessageQueue;
	}

	public Map<Integer, Integer> getQuestStages() {
		return questStages;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int i) {
		this.kills = i;
	}

	public long getOpenPkPoints() {
		return openPkPoints;
	}

	public void setOpenPkPoints(long openPkPoints) {
		this.openPkPoints = openPkPoints;
		ActionSender.sendPoints(this);
	}

	public void addOpenPkPoints(long openPkPoints) {
		this.openPkPoints += openPkPoints;
		ActionSender.sendPoints(this);
	}

	public void subtractOpenPkPoints(long openPkPoints) {
		this.openPkPoints -= openPkPoints;
		ActionSender.sendPoints(this);
	}

	public int getDeaths() {
		return deaths;
	}

	public int getNpcDeaths() {
		return npcDeaths;
	}

	public int getNpcKills() {
		return npcKills;
	}

	public int getRecentNpcKills() {
		if (getLastNpcKilledId() == -1) return 0;
		return getKillCache().getOrDefault(getLastNpcKilledId(), 0);
	}

	public int getExpShared() {
		return expShared;
	}

	public void setDeaths(final int i) {
		this.deaths = i;
	}

	public void setNpcDeaths(final int i) {
		this.npcDeaths = i;
	}

	public void setNpcKills(final int i) {
		this.npcKills = i;
		ActionSender.sendNpcKills(this);
	}

	public synchronized WalkToAction getLastExecutedWalkToAction() {
		return lastExecutedWalkToAction;
	}

	public synchronized void setLastExecutedWalkToAction(final WalkToAction lastExecutedWalkToAction) {
		this.lastExecutedWalkToAction = lastExecutedWalkToAction;
	}

	public void setExpShared(final int i) {
		this.expShared = i;
		ActionSender.sendExpShared(this);
	}

	private void incDeaths() {
		deaths++;
	}

	private void incNpcDeaths() {
		npcDeaths++;
	}

	private void incKills() {
		kills++;
	}

	public void incNpcKills() {
		npcKills++;
	}

	public void addKill(final boolean add) {
		if (!add) {
			kills++;
		}
	}

	public synchronized WalkToAction getWalkToAction() {
		return walkToAction;
	}

	public synchronized void setWalkToAction(final WalkToAction action) {
		this.walkToAction = action;
	}

	public int getElixir() {
		if (getCache().hasKey("elixir_time")) {
			int now = (int) (System.currentTimeMillis() / 1000);
			int time = ((int) getCache().getLong("elixir_time") - now);
			return Math.max(time, 0);
		}
		return 0;
	}

	public void addElixir(final int seconds) {
		long now = System.currentTimeMillis() / 1000;
		long experience = (now + (long) seconds);
		getCache().store("elixir_time", experience);
	}

	public void removeElixir() {
		if (getCache().hasKey("elixir_time"))
			getCache().remove("elixir_time");

		ActionSender.sendElixirTimer(this, 0);
	}

	public int getGlobalBlock() {
		if (getCache().hasKey("setting_block_global")) {
			return getCache().getInt("setting_block_global");
		}
		return 1;
	}

	public int getVolumeFunction() {
		if (getCache().hasKey("setting_volume_function")) {
			return getCache().getInt("setting_volume_function");
		}
		return 0;
	}

	public int getSwipeToRotateMode() {
		if (getCache().hasKey("setting_swipe_rotate_mode")) {
			return getCache().getInt("setting_swipe_rotate_mode");
		}
		return 1;
	}

	public int getSwipeToScrollMode() {
		if (getCache().hasKey("setting_swipe_scroll_mode")) {
			return getCache().getInt("setting_swipe_scroll_mode");
		}
		return 1;
	}

	public int getSwipeToZoomMode() {
		if (getCache().hasKey("setting_swipe_zoom_mode")) {
			return getCache().getInt("setting_swipe_zoom_mode");
		}
		return 1;
	}

	public Boolean getBatchProgressBar() {
		if (getWorld().getServer().getConfig().BATCH_PROGRESSION) {
			if (getCache().hasKey("setting_batch_progressbar")) {
				return getCache().getBoolean("setting_batch_progressbar");
			}
			return true;
		} else {
			return false;
		}
	}

	public Boolean getExperienceDrops() {
		if (getWorld().getServer().getConfig().EXPERIENCE_DROPS_TOGGLE) {
			if (getCache().hasKey("setting_experience_drops")) {
				return getCache().getBoolean("setting_experience_drops");
			}
			return true;
		} else {
			return false;
		}
	}

	public Boolean getHideFog() {
		if (getWorld().getServer().getConfig().FOG_TOGGLE) {
			if (getCache().hasKey("setting_showfog")) {
				return getCache().getBoolean("setting_showfog");
			}
			return true;
		} else {
			return false;
		}
	}

	public Boolean getAutoMessageSwitch() {
		if (getWorld().getServer().getConfig().AUTO_MESSAGE_SWITCH_TOGGLE) {
			if (getCache().hasKey("setting_auto_messageswitch")) {
				return getCache().getBoolean("setting_auto_messageswitch");
			}
			return true;
		} else {
			return false;
		}
	}

	public Boolean getHideLoginBox() {
		if (getWorld().getServer().getConfig().HIDE_LOGIN_BOX_TOGGLE) {
			if (getCache().hasKey("setting_hide_login_box")) {
				return getCache().getBoolean("setting_hide_login_box");
			}
		}
		return false;
	}

	public Boolean getBlockGlobalFriend() {
		if (getWorld().getServer().getConfig().WANT_GLOBAL_FRIEND) {
			if (getLocation().onTutorialIsland() && !isMod()) {
				return true;
			}
			if (getTotalLevel() < getConfig().GLOBAL_MESSAGE_READING_TOTAL_LEVEL_REQ && !isPlayerMod()) {
				return true;
			}
			if (getCache().hasKey("setting_block_global_friend")) {
				return getCache().getBoolean("setting_block_global_friend");
			}
		}
		return false;
	}

	public Boolean getHideSideMenu() {
		if (getWorld().getServer().getConfig().SIDE_MENU_TOGGLE) {
			if (getCache().hasKey("setting_side_menu")) {
				return getCache().getBoolean("setting_side_menu");
			}
			return true;
		} else {
			return false;
		}
	}

	public Boolean getCustomUI() {
		if (getWorld().getServer().getConfig().WANT_CUSTOM_UI) {
			if (getCache().hasKey("custom_ui")) {
				return getCache().getBoolean("custom_ui");
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public Boolean getHideKillFeed() {
		if (getWorld().getServer().getConfig().WANT_KILL_FEED) {
			if (getCache().hasKey("setting_kill_feed")) {
				return getCache().getBoolean("setting_kill_feed");
			}
			return true;
		} else {
			return false;
		}
	}

	public Boolean getHideInventoryCount() {
		if (getWorld().getServer().getConfig().INVENTORY_COUNT_TOGGLE) {
			if (getCache().hasKey("setting_inventory_count")) {
				return getCache().getBoolean("setting_inventory_count");
			}
			return true;
		} else {
			return false;
		}
	}

	public Boolean getHideNameTag() {
		if (getWorld().getServer().getConfig().SHOW_FLOATING_NAMETAGS) {
			if (getCache().hasKey("setting_floating_nametags")) {
				return getCache().getBoolean("setting_floating_nametags");
			}
			return true;
		} else {
			return false;
		}
	}

	public Boolean getHideRoofs() {
		if (getWorld().getServer().getConfig().SHOW_ROOF_TOGGLE) {
			if (getCache().hasKey("setting_showroof")) {
				return getCache().getBoolean("setting_showroof");
			}
			return true;
		} else {
			return false;
		}
	}

	public Boolean getHideUndergroundFlicker() {
		if (getWorld().getServer().getConfig().SHOW_ROOF_TOGGLE) {
			if (getCache().hasKey("setting_showunderground_flicker")) {
				return getCache().getBoolean("setting_showunderground_flicker");
			}
			return true;
		} else {
			return false;
		}
	}

	public int getGroundItemsToggle() {
		if (getWorld().getServer().getConfig().GROUND_ITEM_TOGGLE) {
			if (getCache().hasKey("setting_ground_items")) {
				return getCache().getInt("setting_ground_items");
			}
			return 0;
		} else {
			return 0;
		}
	}

	public int getFightModeSelectorToggle() {
		if (getWorld().getServer().getConfig().FIGHTMODE_SELECTOR_TOGGLE) {
			if (getCache().hasKey("setting_fightmode_selector")) {
				return getCache().getInt("setting_fightmode_selector");
			}
			return 1;
		} else {
			return 1;
		}
	}

	public int getExperienceCounterToggle() {
		if (getWorld().getServer().getConfig().EXPERIENCE_COUNTER_TOGGLE) {
			if (getCache().hasKey("setting_experience_counter")) {
				return getCache().getInt("setting_experience_counter");
			}
			return 1;
		} else {
			return 0;
		}
	}

	public int getLongPressDelay() {
		if (getCache().hasKey("setting_press_delay")) {
			return getCache().getInt("setting_press_delay");
		}
		return 5;
	}

	public int getLastZoom() {
		if (getCache().hasKey("setting_last_zoom")) {
			return getCache().getInt("setting_last_zoom");
		}
		return 125;
	}

	public int getFontSize() {
		if (getCache().hasKey("setting_font_size")) {
			return getCache().getInt("setting_font_size");
		}
		return 3;
	}

	public int getStatusBar() {
		if (getCache().hasKey("setting_status_bar")) {
			return getCache().getInt("setting_status_bar");
		}
		return 0;
	}

	public Boolean getHoldAndChoose() {
		if (getCache().hasKey("setting_hold_choose")) {
			return getCache().getBoolean("setting_hold_choose");
		}
		return true;
	}

	public boolean getClanInviteSetting() {
		if (getWorld().getServer().getConfig().WANT_CLANS) {
			if (getCache().hasKey("p_block_invites")) {
				return getCache().getBoolean("p_block_invites");
			}
			return true;
		} else {
			return false;
		}
	}

	public boolean getPartyInviteSetting() {
		//if (getServer().getConfig().WANT_PARTIES) {
		if (getCache().hasKey("party_block_invites")) {
			return getCache().getBoolean("party_block_invites");
		}
		return false;
		//}
	}

	public boolean getShowNPCKC() {
		if (getCache().hasKey("show_npc_kc")) {
			return getCache().getBoolean("show_npc_kc");
		}
		return false;
	}

	public boolean getShowRecentNPCKC() {
		if (getCache().hasKey("show_recent_npc_kc")) {
			return getCache().getBoolean("show_recent_npc_kc");
		}
		return false;
	}

	public boolean getAndroidInvToggle() {
		if (getCache().hasKey("android_inv_toggle")) {
			return getCache().getBoolean("android_inv_toggle");
		}
		return false;
	}

	public boolean getPartyLootSetting() {
		return getPartyInviteSetting();
	}

	public PlayerSettings getSettings() {
		return playerSettings;
	}

	public Social getSocial() {
		return social;
	}

	public Prayers getPrayers() {
		return prayers;
	}

	public int getIcon() {
		if (preferredIcon != -1) {
			if (isAdmin()) {
				return preferredIcon;
			}
		}

		if (getWorld().getServer().getConfig().WANT_CUSTOM_RANK_DISPLAY) {
			if (isAdmin())
				return 0x0100FF00;

			if (isMod())
				return 0x010000FF;

			if (isDev())
				return 0x01FF0000;

			if (isEvent())
				return 0x014D33BD;

			if (isPlayerMod())
				return 0x03FFFFFF;

			return 0;
		}

		if (isAdmin())
			return 0x02FFFFFF;

		if (isMod())
			return 0x03FFFFFF;

		if (isPlayerMod())
			return 0x03FFFFFF;

		return 0;
	}

	public byte getIconAuthentic() {
		if (preferredIcon != -1) {
			// You can choose icon > 2 for cool effect e.g. 15 is a Log icon.
			if (isAdmin() || isMod() || isDev() || isEvent()) {
				return (byte) (preferredIcon & 0xFF);
			}
			if (isPlayerMod()) {
				// Don't allow PMod to pose as admin. :-)
				// but otherwise it is cool for them to have a weird tree icon if they want
				if ((byte) (preferredIcon & 0xFF) != 2) {
					return (byte) (preferredIcon & 0xFF);
				}
			}
		}

		if (isAdmin())
			return 2;

		if (isMod())
			return 2;

		if (isDev())
			return 2;

		if (isEvent())
			return 2;

		if (isPlayerMod())
			return 1;

		return 0;
	}

	public Trade getTrade() {
		return trade;
	}

	public Duel getDuel() {
		return duel;
	}
	/*public int getUnreadMessages() {
		return unreadMessages + 1;
	}*/
	/*public void setUnreadMessages(int unreadMessages) {
		this.unreadMessages = unreadMessages;
	}*/
	/*public int getTeleportStones() {
		return teleportStones;
	}*/
	/*public void setTeleportStones(int stones) {
		this.teleportStones = stones;
	}*/

	public int getDatabaseID() {
		return super.getID();
	}

	public void setDatabaseID(final int i) {
		super.setID(i);
	}

	public Party getParty() {
		return party;
	}

	public void setParty(final Party party) {
		this.party = party;
		getUpdateFlags().setAppearanceChanged(true);
	}

	public PartyInvite getActivePartyInvite() {
		return activePartyInvitation;
	}

	public void setActivePartyInvite(final PartyInvite inv) {
		activePartyInvitation = inv;
	}

	public Clan getClan() {
		return clan;
	}

	public void setClan(final Clan clan) {
		this.clan = clan;
		getUpdateFlags().setAppearanceChanged(true);
	}

	public ClanInvite getActiveClanInvite() {
		return activeClanInvitation;
	}

	public void setActiveClanInvite(final ClanInvite inv) {
		activeClanInvitation = inv;
	}

	public long secondsUntillPool() {
		return (90 - ((System.currentTimeMillis() - (getCache().hasKey("last_death") ? getCache().getLong("last_death") : 0)) / 1000));
	}

	public boolean canUsePool() {
		return System.currentTimeMillis() - (getCache().hasKey("last_death") ? getCache().getLong("last_death") : 0) > 90000;
	}

	public void addNpcKill(final Npc n, final boolean sendUpdate) {
		int kills = getKillCache().containsKey(n.getID()) ? getKillCache().get(n.getID()) + 1 : 1;
		getKillCache().put(n.getID(), kills);
		setKillCacheUpdated(true);
		if (sendUpdate) message("Your " + n.getDef().getName() + " kill count is: @red@" + kills + "@whi@.");
	}

	public boolean hasHigherRankThan(final Entity observer) {
		// Players always have a higher rank than NPCs/GameObject/GroundItem
		if (!(observer instanceof Player)) {
			return true;
		}

		final Player subject = this;
		final Player obs = (Player) observer;

		return subject.getGroupID() < obs.getGroupID();
	}

	public boolean toggleCacheInvisible() {
		return setCacheInvisible(!cacheIsInvisible());
	}

	public boolean isInvisibleTo(final Entity observer) {
		return stateIsInvisible() && hasHigherRankThan(observer);
	}

	private boolean cacheIsInvisible() {
		if (!getCache().hasKey("invisible"))
			return false;

		return getCache().getBoolean("invisible");
	}

	public boolean stateIsInvisible() {
		return cacheIsInvisible();
	}

	public boolean setCacheInvisible(final boolean invisible) {
		getUpdateFlags().setAppearanceChanged(true);
		this.getCache().store("invisible", invisible);
		return invisible;
	}

	public boolean isInvulnerableTo(final Entity observer) {
		return stateIsInvulnerable() && hasHigherRankThan(observer);
	}

	private boolean cacheIsInvulnerable() {
		if (!getCache().hasKey("invulnerable"))
			return false;

		return getCache().getBoolean("invulnerable");
	}

	public boolean stateIsInvulnerable() {
		return cacheIsInvulnerable();
	}

	public boolean setCacheInvulnerable(final boolean invulnerable) {
		getUpdateFlags().setAppearanceChanged(true);
		this.getCache().store("invulnerable", invulnerable);
		return invulnerable;
	}

	public boolean toggleCacheInvulnerable() {
		return setCacheInvulnerable(!cacheIsInvulnerable());
	}

	public boolean isExperienceFrozen() {
		if (!getCache().hasKey("freezexp"))
			return false;

		return getCache().getBoolean("freezexp");
	}

	public boolean setFreezeXp(final boolean freezeXp) {
		this.getCache().store("freezexp", freezeXp);
		return freezeXp;
	}

	public boolean toggleFreezeXp() {
		return setFreezeXp(!isExperienceFrozen());
	}

	public Point summon(final Point summonLocation) {
		Point originalLocation = getLocation();
		resetSummonReturnPoint();
		setSummonReturnPoint();
		teleport(summonLocation.getX(), summonLocation.getY(), true);
		return originalLocation;
	}

	public Point summon(final Player summonTo) {
		return summon(summonTo.getLocation());
	}

	public void setSummonReturnPoint() {
		if (wasSummoned())
			return;

		getCache().set("return_x", getX());
		getCache().set("return_y", getY());
		getCache().store("was_summoned", true);
	}

	private void resetSummonReturnPoint() {
		getCache().remove("return_x");
		getCache().remove("return_y");
		getCache().remove("was_summoned");
	}

	private int getSummonReturnX() {
		if (!getCache().hasKey("return_x"))
			return -1;

		return getCache().getInt("return_x");
	}

	private int getSummonReturnY() {
		if (!getCache().hasKey("return_y"))
			return -1;

		return getCache().getInt("return_y");
	}

	public Point returnFromSummon() {
		if (!wasSummoned())
			return null;

		Point originalLocation = getLocation();
		teleport(getSummonReturnX(), getSummonReturnY(), true);
		resetSummonReturnPoint();
		return originalLocation;
	}

	public void setSummoned(final boolean wasSummoned) {
		getCache().store("was_summoned", wasSummoned);
	}

	public boolean wasSummoned() {
		if (!getCache().hasKey("was_summoned"))
			return false;

		return getCache().getBoolean("was_summoned");
	}

	public Point jail() {
		Point originalLocation = getLocation();
		setJailReturnPoint();
		teleport(75, 1641, true);
		return originalLocation;
	}

	private void setJailReturnPoint() {
		if (isJailed())
			return;

		getCache().set("jail_return_x", getX());
		getCache().set("jail_return_y", getY());
		getCache().store("is_jailed", true);
	}

	private void resetJailReturnPoint() {
		getCache().remove("jail_return_x");
		getCache().remove("jail_return_y");
		getCache().remove("is_jailed");
	}

	private int getJailReturnX() {
		if (!getCache().hasKey("jail_return_x"))
			return -1;

		return getCache().getInt("jail_return_x");
	}

	private int getJailReturnY() {
		if (!getCache().hasKey("jail_return_y"))
			return -1;

		return getCache().getInt("jail_return_y");
	}

	public Point releaseFromJail() {
		if (!isJailed())
			return null;

		Point originalLocation = getLocation();
		teleport(getJailReturnX(), getJailReturnY(), true);
		resetJailReturnPoint();
		return originalLocation;
	}

	public void setJailed(final boolean isJailed) {
		getCache().store("is_jailed", isJailed);
	}

	public boolean isJailed() {
		if (!getCache().hasKey("is_jailed"))
			return false;

		return getCache().getBoolean("is_jailed");
	}

	public boolean groundItemTake(final GroundItem item) {
		Item itemFinal = new Item(item.getID(), item.getAmount(), item.getNoted());
		if (item.getOwnerUsernameHash() == 0 || item.getAttribute("npcdrop", false)) {
			itemFinal.setAttribute("npcdrop", true);
		}

		if (!getCarriedItems().getInventory().canHold(itemFinal)) {
			return false;
		}

		if (item.isRemoved()) {
			return false;
		}

		getWorld().unregisterItem(item);
		this.playSound("takeobject");
		getCarriedItems().getInventory().add(itemFinal);
		getWorld().getServer().getGameLogger().addQuery(new GenericLog(this.getWorld(), this.getUsername() + " picked up " + item.getDef().getName() + " x"
			+ item.getAmount() + " at " + this.getLocation().toString()));

		return true;
	}

	public boolean checkRingOfLife(final Mob hitter) {
		if (this.isPlayer() && getCarriedItems().getEquipment().hasEquipped(ItemId.RING_OF_LIFE.id())
			&& (!this.getLocation().inWilderness()
			|| (this.getLocation().inWilderness() && this.getLocation().wildernessLevel() <= Constants.GLORY_TELEPORT_LIMIT))) {
			if (((float) this.getSkills().getLevel(Skill.HITS.id())) / ((float) this.getSkills().getMaxStat(Skill.HITS.id())) <= 0.1f) {
				this.resetCombatEvent();
				this.resetRange();
				this.resetAll();
				hitter.resetCombatEvent();
				hitter.resetRange();
				if (hitter.isPlayer()) {
					((Player) hitter).resetAll();
				}
				this.teleport(getConfig().RESPAWN_LOCATION_X, getConfig().RESPAWN_LOCATION_Y, false);
				this.message("Your ring of Life shines brightly");
				getCarriedItems().shatter(new Item(ItemId.RING_OF_LIFE.id()));
				return true;
			}
		}
		return false;
	}

	/**
	 * Return a synchronized access to the player's carried Items
	 */
	public synchronized CarriedItems getCarriedItems() {
		return this.carriedItems.get();
	}

	// Ensures crossbows and shortbows have a shorter radius of 4 instead of the default 5
	public int getProjectileRadius() {
		final int weaponId = getRangeEquip();
		final boolean shortRadius = RangeUtils.isCrossbow(weaponId) || RangeUtils.isShortBow(weaponId);
		return shortRadius ? DEFAULT_PROJECTILE_RADIUS - 1 : DEFAULT_PROJECTILE_RADIUS;
	}

	public boolean wantUnholySymbols() {
		if (getQuestStage(Quests.OBSERVATORY_QUEST) > -1)
			return true; // Quest started.

		if (getConfig().WANT_CUSTOM_QUESTS) {
			if (getCache().hasKey("want_unholy_symbol_drops") &&
				getCache().getBoolean("want_unholy_symbol_drops")) {
				return true;
			}
		}
		return false;
	}

	public int getTotalLevel() {
		return getSkills().getTotalLevel();
	}

	public long getLastExchangeTime() {
		return lastExchangeTime;
	}

	public void setLastExchangeTime() {
		this.lastExchangeTime = System.currentTimeMillis();
	}

	public void setClientVersion(int cv) {
		this.clientVersion = cv;
	}

	public int getClientVersion() {
		return this.clientVersion;
	}

	public boolean isUsingClientBeforeQP() {
		return this.clientVersion >= 14 && this.clientVersion <= 38;
	}

	public boolean isUsing38CompatibleClient() { return this.clientVersion == 38; }

	public boolean isUsing39CompatibleClient() { return this.clientVersion == 39 || this.clientVersion == 40; }

	public boolean isUsing69CompatibleClient() { return this.clientVersion == 69; }

	public boolean isUsing115CompatibleClient() {
		return this.clientVersion == 115;
	}

	public boolean isUsing140CompatibleClient() {
		return this.clientVersion == 140;
	}

	public boolean isUsing177CompatibleClient() {
		return this.clientVersion == 177;
	}

	public boolean isUsing196CompatibleClient() {
		return this.clientVersion == 196;
	}

	public boolean isUsing198CompatibleClient() {
		// 197 and 198 are identical protocol-wise.
		return this.clientVersion > 196 && this.clientVersion < 199;
	}

	public boolean isUsing199CompatibleClient() {
		return this.clientVersion == 199;
	}

	public boolean isUsing201CompatibleClient() {
		// 200 and 201 are identical protocol-wise.
		return this.clientVersion > 199 && this.clientVersion < 202;
	}

	public boolean isUsing202CompatibleClient() {
		return this.clientVersion == 202;
	}

	public boolean isUsing203CompatibleClient() {
		return this.clientVersion == 203;
	}

	public boolean isUsing233CompatibleClient() {
		return this.clientVersion >= 233 && this.clientVersion <= 235;
	}

	public boolean isUsingCustomClient() {
		return this.clientVersion > 10000 && this.clientVersion < 20000;
	}

	public boolean supportsPlayerUnlockedAppearancesPacket() {
		return this.clientVersion >= 10009 && this.clientVersion < 20000;
	}

	public boolean getQolOptOutWarned() {
		return this.qolOptOutWarned;
	}

	public void setQolOptOutWarned(boolean warned) {
		this.qolOptOutWarned = warned;
	}

	public void setQolOptOut() {
		getCache().store("qol_optout", true);
	}

	public boolean getQolOptOut() {
		return getCache().hasKey("qol_optout");
	}

	public boolean getCertOptOutWarned() {
		return this.certOptOutWarned;
	}

	public void setCertOptOutWarned(boolean warned) {
		this.certOptOutWarned = warned;
	}

	public void setCertOptOut() {
		getCache().store("cert_optout", true);
	}

	public boolean getCertOptOut() {
		return getCache().hasKey("cert_optout");
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}

	private void enterMorph(int appearanceId) {
		for (int i = 0; i < 12; i++) {
			wornItems[i] = AppearanceId.NOTHING.id();
		}
		wornItems[AppearanceId.SLOT_BODY] = appearanceId;
		getUpdateFlags().setAppearanceChanged(true);
	}

	public void exitMorph() {
		getSettings().getAppearance().restorePlayerAppearance();
		for (int i = 0; i < 12; i++) {
			updateWornItems(i, getSettings().getAppearance().getSprite(i));
		}

		if (getConfig().WANT_EQUIPMENT_TAB) {
			synchronized (getCarriedItems().getEquipment().getList()) {
				for (Item item : getCarriedItems().getEquipment().getList()) {
					if (item == null) continue;
					ItemDefinition itemDef = item.getDef(getWorld());
					if (itemDef.getWieldPosition() < 12) {
						updateWornItems(itemDef.getWieldPosition(), itemDef.getAppearanceId(), itemDef.getWearableId(), true);
					}
				}
			}
		} else {
			for (Item item : getCarriedItems().getInventory().getItems()) {
				if (item == null) continue;
				ItemDefinition itemDef = item.getDef(getWorld());
				if (item.getItemStatus().isWielded()) {
					updateWornItems(itemDef.getWieldPosition(), itemDef.getAppearanceId(), itemDef.getWearableId(), true);
				}
			}
		}
		getUpdateFlags().setAppearanceChanged(true);
	}

	public void setClientLimitations(ClientLimitations cl) {
		this.clientLimitations = cl;
	}
	public ClientLimitations getClientLimitations() {
		return this.clientLimitations;
	}

	public boolean isKnownPlayer(int pid) {
		for (int i = 0; i < knownPlayersCount; i++) {
			if (knownPlayerPids[i] == pid) {
				return true;
			}
		}
		return false;
	}

	public boolean skipTutorial() {
		if (getLocation().onTutorialIsland() && this.getWorld().getServer().getConfig().SHOW_TUTORIAL_SKIP_OPTION) {
			if (inCombat()) {
				message("You cannot do that whilst fighting!");
				return false;
			}

			if (isBusy()) {
				return false;
			}

			if (getCache().hasKey("tutorial")) {
				getCache().remove("tutorial");
			}
			teleport(getConfig().RESPAWN_LOCATION_X, getConfig().RESPAWN_LOCATION_Y, false);
			message("Skipped tutorial, welcome to Lumbridge");
			ActionSender.sendPlayerOnTutorial(this);
			final Player player = this;
			getWorld().getServer().getGameEventHandler().add(
				new DelayedEvent(getWorld(), null, 15000, "TellAboutGlobalChat") {
					public void run() {
						getSocial().messagePlayerOffTutorialIfTheyAreEligibleForGlobalChat(player);
						stop();
					}
				}
			);
			return true;
		}
		return false;
	}

	public Npc checkVisNpc(Player player, final int npcId, final int radius) {
		final Iterable<Npc> npcsInView = player.getViewArea().getNpcsInView();
		Npc closestNpc = null;
		for (int next = 0; next < radius; next++) {
			for (final Npc n : npcsInView) {
				if (n.getID() == npcId && n.withinRange(player.getLocation(), next) && !n.isBusy()) {
					closestNpc = n;
				}
			}
		}
		return closestNpc;
	}

	public void tellCoordinates() {
		playerServerMessage(MessageType.QUEST, "@whi@You are at @cya@" + getLocation() + "@whi@ or in Jagex notation: @cya@" + getLocation().pointToJagexPoint());
	}

	public boolean getBankPinOptOut() {
		return getCache().hasKey("bankpin_optout");
	}

	public boolean getBankPinOptIn() { return getCache().hasKey("bankpin_optin") || getCache().hasKey("bank_pin"); }

	public boolean getBankPinOption() {
		return (getConfig().WANT_BANK_PINS && !getBankPinOptOut()) || (getConfig().TOLERATE_BANK_PINS && getBankPinOptIn());
	}

	public boolean isUsingAndroidClient() {
		return getClientLimitations().isAndroidClient;
	}

	public PreferredLanguage getPreferredLanguage() {
		return preferredLanguage;
	}

	public void setPreferredLanguage(PreferredLanguage language) {
		getCache().store("preferredLanguage", language.getLocaleName());
		preferredLanguage = language;
	}

	public String getText(String key) {
		return getWorld().getServer().getI18nService().getText(key, this);
	}

	public String getMez(String key) {
		return getWorld().getServer().getI18nService().getMez(key, this);
	}

	public boolean checkAndIncrementSaveAttempts() {
		return saveAttempts++ > 3;
	}

	public void resetSaveAttempts() {
		saveAttempts = 0;
	}

    public boolean[] getUnlockedSkinColours() {
		return this.unlockedSkinColours;
    }

	public void setUnlockedSkinColours(boolean[] colours) {
		this.unlockedSkinColours = colours;
		if (loggedIn)
			ActionSender.sendUnlockedAppearances(this);
	}

	public void desertHeatInit() {
		if (getWorld().getServer().getConfig().WANT_FIXED_BROKEN_MECHANICS) {
			if (!this.hasElevatedPriveledges()) {
				this.desertHeatEvent = new DesertHeatEvent(getWorld(), this);
				getWorld().getServer().getGameEventHandler().add(this.desertHeatEvent);
			}
		}
	}

	public Item getEquippedChest() {
		if (this.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			Item platebody = this.getCarriedItems().getEquipment().get(Equipment.EquipmentSlot.SLOT_PLATE_BODY.getIndex());
			if (platebody == null)
				return this.getCarriedItems().getEquipment().get(Equipment.EquipmentSlot.SLOT_CHAIN_BODY.getIndex());

			return platebody;
		}

		Inventory inv = this.getCarriedItems().getInventory();
		for (Item item : inv.getItems()) {
			if (item.isWielded()) {
				ItemDefinition def = item.getDef(this.getWorld());
				if (def.isWieldable()) {
					if (def.getWieldPosition() == Equipment.EquipmentSlot.SLOT_PLATE_BODY.getIndex()
						|| def.getWieldPosition() == Equipment.EquipmentSlot.SLOT_CHAIN_BODY.getIndex())
						return item;
				}
			}
		}

		return null;
	}

	public Item getEquippedLegs() {
		if (this.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			Item platelegs = this.getCarriedItems().getEquipment().get(Equipment.EquipmentSlot.SLOT_PLATE_LEGS.getIndex());
			if (platelegs == null)
				return this.getCarriedItems().getEquipment().get(Equipment.EquipmentSlot.SLOT_SKIRT.getIndex());

			return platelegs;
		}

		Inventory inv = this.getCarriedItems().getInventory();
		for (Item item : inv.getItems()) {
			if (item.isWielded()) {
				ItemDefinition def = item.getDef(this.getWorld());
				if (def.isWieldable()) {
					if (def.getWieldPosition() == Equipment.EquipmentSlot.SLOT_PLATE_LEGS.getIndex()
						|| def.getWieldPosition() == Equipment.EquipmentSlot.SLOT_SKIRT.getIndex())
						return item;
				}
			}
		}

		return null;
	}

	public Item getEquippedBoots() {
		if (this.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB)
			return this.getCarriedItems().getEquipment().get(Equipment.EquipmentSlot.SLOT_BOOTS.getIndex());


		Inventory inv = this.getCarriedItems().getInventory();
		for (Item item : inv.getItems()) {
			if (item.isWielded()) {
				ItemDefinition def = item.getDef(this.getWorld());
				if (def.isWieldable()) {
					if (def.getWieldPosition() == Equipment.EquipmentSlot.SLOT_BOOTS.getIndex())
						return item;
				}
			}
		}

		return null;
	}

	public Item getEquippedGloves() {
		if (this.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB)
			return this.getCarriedItems().getEquipment().get(Equipment.EquipmentSlot.SLOT_GLOVES.getIndex());


		Inventory inv = this.getCarriedItems().getInventory();
		for (Item item : inv.getItems()) {
			if (item.isWielded()) {
				ItemDefinition def = item.getDef(this.getWorld());
				if (def.isWieldable()) {
					if (def.getWieldPosition() == Equipment.EquipmentSlot.SLOT_GLOVES.getIndex())
						return item;
				}
			}
		}

		return null;
	}

	public Item getEquippedHelmet() {
		if (this.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			Item fullhelm = this.getCarriedItems().getEquipment().get(Equipment.EquipmentSlot.SLOT_LARGE_HELMET.getIndex());
			if (fullhelm == null)
				return this.getCarriedItems().getEquipment().get(Equipment.EquipmentSlot.SLOT_MEDIUM_HELMET.getIndex());

			return fullhelm;
		}

		Inventory inv = this.getCarriedItems().getInventory();
		for (Item item : inv.getItems()) {
			if (item.isWielded()) {
				ItemDefinition def = item.getDef(this.getWorld());
				if (def.isWieldable()) {
					if (def.getWieldPosition() == Equipment.EquipmentSlot.SLOT_LARGE_HELMET.getIndex()
						|| def.getWieldPosition() == Equipment.EquipmentSlot.SLOT_MEDIUM_HELMET.getIndex())
						return item;
				}
			}
		}

		return null;
	}

	public void setSceneryMorph(int id) {
		this.sceneryMorph = id;
		if (id < 0) { // leaving morph
			final GameObject existingObject = getViewArea().getGameObject(getLocation());
			if (existingObject != null && existingObject.getType() != 1) {
				resetScenery(existingObject);
			}
		}
	}
	public void resetSceneryMorph() {
		if (this.sceneryMorph >= 0)
			setSceneryMorph(-1);
	}

	public void resetScenery(GameObject gameObject) {
		Point objectCoordinates = Point.location(gameObject.getLoc().getX(), gameObject.getLoc().getY());
		final int initialObjectID = gameObject.getWorld().getSceneryLoc(objectCoordinates);
		if (initialObjectID != gameObject.getID()) {
			if (initialObjectID != -1) {
				// world object from initial json
				final GameObject replaceObj = new GameObject(gameObject.getWorld(), gameObject.getLocation(), initialObjectID, gameObject.getDirection(), gameObject.getType());
				changeloc(gameObject, replaceObj);
			} else {
				// dynamic stuck object
				// unregister
				gameObject.getWorld().unregisterGameObject(gameObject);
			}
		}
	}

	public void setUsernameChangePending(UsernameChange usernameChangePending) {
		this.usernameChangePending = usernameChangePending;
	}

	public UsernameChange getUsernameChangePending() {
		return this.usernameChangePending;
	}

	public boolean isElligibleToGlobalChat() {
		final String messagePrefix = getConfig().MESSAGE_PREFIX;
		if (isMuted()) {
			if (!isShadowMuted()) {
				final long muteDelay = getMuteExpires();
				message(messagePrefix + "You are " + (muteDelay == -1 ? "permanently muted" : "temporarily muted for " + getMinutesMuteLeft() + " minutes") + ".");
			}
			return false;
		}
		if (isGlobalMuted()) {
			if (!isShadowMuted()) {
				final long globalMuteDelay = getCache().getLong("global_mute");
				message(messagePrefix + "You are " + (globalMuteDelay == -1 ? "permanently muted" : "temporarily muted for " + (int) ((globalMuteDelay - System.currentTimeMillis()) / 1000 / 60) + " minutes") + " from global chat.");
			}
			return false;
		}

		long sayDelay = 0;
		if (getCache().hasKey("say_delay")) {
			sayDelay = getCache().getLong("say_delay");
		}

		long waitTime = getConfig().GLOBAL_MESSAGE_COOLDOWN;

		if (isPlayerMod()) {
			waitTime = 0;
		}

		if (System.currentTimeMillis() - sayDelay < waitTime) {
			message(messagePrefix + "You can only send a message to global every " + (waitTime / 1000) + " seconds");
			return false;
		}

		if (getTotalLevel() < getConfig().GLOBAL_MESSAGE_TOTAL_LEVEL_REQ && !isPlayerMod()) {
			message("You can only send a message to global chat if you have at least " + getConfig().GLOBAL_MESSAGE_TOTAL_LEVEL_REQ + " total level.");
			message("Type @gre@::globalchat@whi@ or @gre@::gc@whi@ for more information.");
			return false;
		}

		if (getLocation().onTutorialIsland() && !isMod()) {
			message("@cya@Once you finish the tutorial, this lets you send messages to everyone on the server");
			return false;
		}

		if (isBabyModeFiltered()) {
			message("Sorry, but someone we banned for breaking our rules is actively throwing a tantrum right now.");
			message("New accounts are not allowed to speak until they've reached " + getConfig().BABY_MODE_LEVEL_THRESHOLD + " total level during this time.");
			return false;
		}

		if (getConfig().WANT_GLOBAL_RULES_AGREEMENT && !getCache().hasKey("accepted_global_rules") && !isPlayerMod()) {
			message("@cya@You must agree to the global chat rules before using global chat");
			message("@cya@Use the ::globalrules command to view them.");
			return false;
		}

		getCache().store("say_delay", System.currentTimeMillis());
		return true;
	}

	public UnregisterRequest getUnregisterRequest() {
		return this.unregisterRequest;
	}

	public DelayedEvent getUnregisterEvent() {
		return unregisterEvent;
	}

	public void setUnregisterEvent(DelayedEvent unregisterEvent) {
		this.unregisterEvent = unregisterEvent;
	}

	public void setUnregisterRequest(UnregisterRequest unregisterRequest) {
		this.unregisterRequest = unregisterRequest;
	}

	public void unsetUnregisterRequest() {
		this.unregisterRequest = null;
	}

	public boolean hasUnregisterRequest() {
		return this.unregisterRequest != null;
	}

	public boolean getMultiEndedEarly() {
		return multiEndedEarly;
	}

	public void setMultiEndedEarly(boolean endedEarly) {
		this.multiEndedEarly = endedEarly;
	}

	public void updateDamageAndBlockedDamageTracking(Mob mob, int damage, int blockedDamage) {
		UUID uuid = mob.getUUID();
		if (trackedDamageFromMob.containsKey(uuid)) {
			int oldDamage = trackedDamageFromMob.get(uuid).getLeft();
			int oldBlockedDamage = trackedDamageFromMob.get(uuid).getRight();
			trackedDamageFromMob.put(uuid, Pair.of(oldDamage + damage, oldBlockedDamage + blockedDamage));
		} else {
			trackedDamageFromMob.put(uuid, Pair.of(damage, blockedDamage));
		}
	}

	public int getTrackedDamage(Mob damageInflictingMob) {
		if (trackedDamageFromMob.containsKey(damageInflictingMob.getUUID())) {
			return trackedDamageFromMob.get(damageInflictingMob.getUUID()).getLeft();
		}
		return -1;
	}

	public int getTrackedBlockedDamage(Mob damageInflictingMob) {
		if (trackedDamageFromMob.containsKey(damageInflictingMob.getUUID())) {
			return trackedDamageFromMob.get(damageInflictingMob.getUUID()).getRight();
		}
		return -1;
	}

	public void resetTrackedDamageAndBlockedDamage(Mob damageInflictingMob) {
		trackedDamageFromMob.remove(damageInflictingMob.getUUID());
	}

	public boolean canBeReattacked() {
		return this.getRanAwayTimer() + getConfig().PVP_REATTACK_TIMER <= getWorld().getServer().getCurrentTick();
	}

	public void setInteractingNpc(Npc npc) {
		this.interactingNpc = npc;
	}

	public Npc getInteractingNpc() {
		return this.interactingNpc;
	}

	public boolean isBabyModeFiltered() {
		return getTotalLevel() < getConfig().BABY_MODE_LEVEL_THRESHOLD;
	}

	public int getLastNpcKilledId() {
		return this.lastNpcKilledId;
	}

	public void setLastNpcKilledId(int npcId) {
		this.lastNpcKilledId = npcId;
	}

	public boolean willBeProcessedBefore(Player affectedMob) {
		if (getConfig().SHUFFLE_PID_ORDER) {
			// search for the pid of one of the two players involved
			for (int curPid : PidShuffler.pidProcessingOrder) {
				if (curPid == getIndex()) {
					return true;
				}
				if (curPid == affectedMob.getIndex()) {
					return false;
				}
			}
			return false;
		} else {
			return getIndex() < affectedMob.getIndex();
		}
	}
}
