package org.openrsc.server.model;

import java.awt.image.BufferedImage;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.mina.common.IoSession;
import org.openrsc.server.Config;
import org.openrsc.server.ServerBootstrap;
import org.openrsc.server.core.Watcher;
import org.openrsc.server.database.DefaultTransaction;
import org.openrsc.server.database.Transaction;
import org.openrsc.server.database.game.Save;
import org.openrsc.server.entityhandling.EntityHandler;
import org.openrsc.server.entityhandling.defs.PrayerDef;
import org.openrsc.server.event.DelayedEvent;
import org.openrsc.server.event.MiniEvent;
import org.openrsc.server.event.NpcRangeEvent;
import org.openrsc.server.event.PlayerRangeEvent;
import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.internal.DefaultFatigueApplicator;
import org.openrsc.server.internal.FatigueApplicator;
import org.openrsc.server.internal.NoOpFatigueApplicator;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.DeathLog;
import org.openrsc.server.logging.model.ErrorLog;
import org.openrsc.server.logging.model.GenericLog;
import org.openrsc.server.logging.model.eventLog;
import org.openrsc.server.net.RSCPacket;
import org.openrsc.server.packetbuilder.MiscPacketBuilder;
import org.openrsc.server.packetbuilder.RSCPacketBuilder;
import org.openrsc.server.packethandler.DMHandler;
import org.openrsc.server.states.Action;
import org.openrsc.server.states.CombatState;
import org.openrsc.server.util.Captcha;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.util.Formulae;
import org.openrsc.server.util.Pair;
import org.openrsc.server.util.StatefulEntityCollection;

import com.rscdaemon.scripting.Script;
import com.rscdaemon.util.IPTrackerPredicate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public final class Player extends Mob implements Watcher, Comparable<Player>
{
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        
        private static final FatigueApplicator fatigueApplicator =
			Config.isDisableFatigue() ?
					new NoOpFatigueApplicator() :
					new DefaultFatigueApplicator();

	// @serial
	private Map<Integer, com.rscdaemon.scripting.quest.Quest> scriptableQuests;
	
	public final com.rscdaemon.scripting.quest.Quest getScriptableQuest(int id)
	{
		if(!scriptableQuests.containsKey(id))
		{
			scriptableQuests.put(id, ServerBootstrap.getQuest(id));
			System.out.println(dateFormat.format(date)+": SCRIPTABLE_QUEST: " + ServerBootstrap.getQuest(id));
		}
		return scriptableQuests.get(id);
	}
	
	public final int getScriptableQuestPoints()
	{
		int rv = 0;
		for(com.rscdaemon.scripting.quest.Quest quest : scriptableQuests.values())
		{
			if(quest.getVariable(com.rscdaemon.scripting.quest.Quest.QUEST_STAGE).equals(com.rscdaemon.scripting.quest.Quest.QUEST_FINISHED))
			{
				rv += quest.getQuestPoints();
			}
		}
		return rv;
	}

	public final Map<Integer, com.rscdaemon.scripting.quest.Quest> getScriptableQuests()
	{
		return scriptableQuests;
	}
	
	public final void setScriptableQuests(Map<Integer, com.rscdaemon.scripting.quest.Quest> scriptableQuests)
	{
		this.scriptableQuests = scriptableQuests;
	}
	
	private Script script;
	
	public final Script getScript()
	{
		return script;
	}
	
	public final void setScript(Script script)
	{
		this.script = script;
	}
	
	private final static class QuestSave
		extends
			DefaultTransaction
	{
		private final long user;
		private final int questID, questStage, questPoints;
		private final boolean finished;
		
		public QuestSave(long user, int questID, int questStage, boolean finished, int questPoints)
		{
			this.user = user;
			this.questID = questID;
			this.questStage = questStage;
			this.questPoints = questPoints;
			this.finished = finished;
		}
		
		@Override
		public String toString()
		{
			return "\"QuestSave\" {user=" + DataConversions.hashToUsername(user) + "}";
		}
	
		@Override
		public Integer call()
			throws
				SQLException
		{
			Connection connection = super.getConnection();
			if(connection == null)
			{
				return Transaction.DATABASE_UNAVAILABLE;
			}
			if(questStage == 0)
			{
				connection.createStatement().executeUpdate("INSERT INTO `rscd_quests`(`user`, `quest_id`, `quest_stage`, `finished`, `quest_points`) VALUES ('" + user + "', '" + questID + "', '" + questStage + "', '" + (finished ? 1 : 0) + "', '" + questPoints + "')");
			}
			else
			{
				connection.createStatement().executeUpdate("UPDATE `rscd_quests` SET `quest_stage` = " + questStage + ", `finished` = '" + (finished ? 1 : 0) + "' WHERE `user` = '" + user + "' AND `quest_id` = '" + questID + "'");
			}
			return Transaction.TRANSACTION_SUCCESS;
		}

		@Override
		public boolean retryOnFatalError()
		{
			return true;
		}
	}
	
	public final static class BanTransaction
		extends
			DefaultTransaction
	{
		
		private final long user;
		private final boolean ban;

		public BanTransaction(long user, boolean ban)
		{
			this.user = user;
			this.ban = ban;
		}
		
		@Override
		public String toString()
		{
			return "\"Ban\" {target=" + DataConversions.hashToUsername(user) + "}";
		}

		@Override
		public Integer call()
			throws
				SQLException
		{
			Connection connection = super.getConnection();
			if(connection == null)
			{
				return Transaction.DATABASE_UNAVAILABLE;
			}
			try(Statement statement = connection.createStatement())
			{
				try(ResultSet rs = statement.executeQuery("SELECT `owner` FROM `rscd_players` WHERE `user` = '" + user + "' FOR UPDATE"))
				{
					if(rs.next())
					{
						statement.executeUpdate("UPDATE `users` SET `banned` = '" + (ban ? 1 : 0) + "' WHERE `id` = '" + rs.getInt("owner") + "' LIMIT 1");
					}
				}
			}
			return Transaction.TRANSACTION_SUCCESS;
		}

		@Override
		public boolean retryOnFatalError()
		{
			return true;
		}	
	}
	
	public final static class MuteTransaction
		extends
			DefaultTransaction
	{
		
		private final long user;
		private final boolean mute;
				
		public MuteTransaction(long user, boolean mute)
		{
			this.user = user;
			this.mute = mute;
		}
	
		@Override
		public String toString()
		{
			return "\"Mute\" {target=" + DataConversions.hashToUsername(user) + "}";
		}
		
		@Override
		public Integer call()
			throws
				SQLException
		{
			Connection connection = super.getConnection();
			if(connection == null)
			{
				return Transaction.DATABASE_UNAVAILABLE;
			}
			try(Statement statement = connection.createStatement())
			{
				try(ResultSet rs = statement.executeQuery("SELECT `owner` FROM `rscd_players` WHERE `user` = '" + user + "'"))
				{
					if(rs.next())
					{
						statement.executeUpdate("UPDATE `users` SET `muted` = '" + (mute ? 1 : 0) + "' WHERE `id` = '" + rs.getInt("owner") + "' LIMIT 1");
					}
				}
			}
			return Transaction.TRANSACTION_SUCCESS;
		}

		@Override
		public boolean retryOnFatalError()
		{
			return true;
		}
		
	}
	
	/** Wilderness IP Tracker Modification */
	
	@Override
	public void setLocation(Point p, boolean teleported)
	{
		if(super.getLocation() != null && p != null)
		{
			if(!super.getLocation().inWilderness() && p.inWilderness())
			{
				if(!World.getWildernessIPTracker().add(
						getIP(),
						new IPTrackerPredicate()
						{
							@Override
							public boolean proceedIf()
							{
								return World.getWildernessIPTracker().ipCount(getIP())
										< Config.getAllowedConcurrentIpsInWilderness();
							}
						}
					))
					{
						onWildernessEntryBlocked();
						setPath(null);
						return;
					}
			}
			else if(super.getLocation().inWilderness() && !p.inWilderness())
			{
				World.getWildernessIPTracker().remove(getIP());
			}
		}
		super.setLocation(p, teleported);
	}
	
	public void onWildernessEntryBlocked()
	{
		sendMessage(Config.getWildernessEntryBlockedMessage());
	}

	/** Wilderness IP Tracker Modification */
	
	/**
	 * Declarations
	 */
	
	public int npcKillCount = 0;
	private boolean inAuctionHouse = false;
	
	private String username, sleepWord, lastIP, IP;
	private String[] parts = {"Pipe", "Barrel", "Axle", "Shaft"};
	
	private long usernameHash, lastCommand, lastSleep, lastReport, ranger, target, lastXP;
	private long muted = 0, subscriptionExpires = 0, recoveryQuestions = 0, lastLogin = 0, lastLogout = 0, lastDeath = 0, currentLogin = 0, lastEat = 0, lastDrink = 0, lastWield = 0, lastAttacked = 0, lastCharge = 0, lastSpellCast = 0, DMStarted = 0, lastTradeDuelRequest = 0, lastArrow = 0, lastCount = 0, lastRanged = 0;
	private long canWarp = System.currentTimeMillis(), lastTradeDuelUpdate = System.currentTimeMillis(), lastPing = System.currentTimeMillis(), lastGlobal = System.currentTimeMillis();	
	
	private int unreadMessages, kills, deaths, groupID, ladyPatches, returnX, returnY, killCount;	
	private int sleepCount = 0, bones = 0, ballsOfWool = 0, bananasInCrate = 0, poisonPower = 0, account = 0, autocastProtection = 0, combatStyle = 0, drainRate = 0, packetCount = 0, combatWindow = 0, DMCasts = 0,  wornItemID = 0, questPoints = 0, drainerPartialFactor = 0;	
	private final static short EAT_DELAY = 100, DRINK_DELAY = 1000, WIELD_DELAY = 500, CAST_DELAY  = 1250;
	
	private int[] wornItems = new int[12];
	private int[] curStat = new int[19], maxStat = new int[19];
	private int[] exp = new int[19];
	private int fatigue = 0, temporaryFatigue = 0;

	private byte lastSpellRandom = 0;
	
	private boolean sleepingBag, onCrandor, hasMapPieceA, maleGender, isSub, groceryStoreEmployee, grainInDraynorHopper, grainInCookingGuildHopper, bananaJobAccepted, canUseCooksRange, canUseDoricsAnvil, rumInKaramjaCrate, rumInSarimCrate, hasKilledSkeleton, collectingBones, hasTraibornKey, recievedKeyPayment, leelaHasKey, phoenixGangMember, blackArmGangMember, killedFish, leverA, leverB, leverC, leverD, leverE, leverF, inShantayPrison, unlockEvent, unlockDev, unlockMod, unlockAdmin, wildernessFlag, ourWornItemsChanged, wasSummoned, requiresOfferUpdate, initialized, destroyed, inBank, loggedIn, changingAppearance, inUnregisterQueue, unregistered, isTrading, isDueling, isDMing, tradeOfferAccepted, duelOfferAccepted, DMOfferAccepted, tradeConfirmAccepted, duelConfirmAccepted, DMConfirmAccepted;
	public boolean teleport, cancelBatch;	
	
	private boolean[] duelOptions = new boolean[4], DMOptions = new boolean[4], privacySettings = new boolean[5], gameSettings = new boolean[6];

	private IoSession session;
	
	private Player wishToTrade, wishToDuel, wishToDM, inDMWith, inCBWith;
	
	private PlayerAppearance appearance;
	
	private HashMap<Integer, Integer> knownPlayersAppearanceIDs = new HashMap<Integer, Integer>(), knownPlayersWornItemIDs = new HashMap<Integer, Integer>();
	private HashMap<Long, Long> attackedBy = new HashMap<Long, Long>();
	
	private StatefulEntityCollection<Player> watchedPlayers = new StatefulEntityCollection<Player>();
	private StatefulEntityCollection<GameObject> watchedObjects = new StatefulEntityCollection<GameObject>();
	private StatefulEntityCollection<Item> watchedItems = new StatefulEntityCollection<Item>();
	private StatefulEntityCollection<Npc> watchedNpcs = new StatefulEntityCollection<Npc>();
	
	private Inventory inventory = new Inventory();
	private Bank bank = new Bank();
	
	private MiscPacketBuilder actionSender;

	private ArrayList<InvItem> tradeOffer = new ArrayList<InvItem>();
	private ArrayList<InvItem> duelOffer = new ArrayList<InvItem>();
	private ArrayList<Long> friendList = new ArrayList<Long>(), ignoreList = new ArrayList<Long>();
	private ArrayList<Projectile> projectilesNeedingDisplayed = new ArrayList<Projectile>();
	private ArrayList<Player> playersNeedingHitsUpdate = new ArrayList<Player>();
	private ArrayList<Npc> npcsNeedingHitsUpdate = new ArrayList<Npc>();
	private ArrayList<Quest> quests = new ArrayList<Quest>();
	
	private List<ChatMessage> chatMessagesNeedingDisplayed = new ArrayList<ChatMessage>(), npcMessagesNeedingDisplayed = new ArrayList<ChatMessage>();	
	
	private MenuHandler menuHandler = null;
	
	private DelayedEvent drainer, skullEvent, followEvent, muteEvent, poisonEvent, fatigueEvent, sleepEvent;
	
	private Shop shop;
	
	private Npc interactingNpc;

	private Mob following;
	
	private long deathTime, logoutDate;
	
	private PlayerRangeEvent playerRangeEvent;
	
	private NpcRangeEvent npcRangeEvent;
	
	private LinkedList<ChatMessage> chatQueue = new LinkedList<ChatMessage>();

	private Action status = Action.IDLE;

	private BufferedImage sleepImage;	
	
	/**
	 * Checks if the player is on blue team
	 */
	private boolean isBlueTeam = false;
	/**
	 * Checks if the player is on red team
	 */
	private boolean isRedTeam = false;
	/**
	 * Checks if the player has the red flag
	 */
	private boolean hasRedFlag = false;
	/**
	 * Checks if the player has the blue flag
	 */
	private boolean hasBlueFlag = false;	
	/**
	 * List of players in CTF blue team
	 */
	public static ArrayList<Player> ctfBlue = new ArrayList<Player>();
	/**
	 * List of players in CTF red team
	 */
	public static ArrayList<Player> ctfRed = new ArrayList<Player>();
    
    private boolean isInvulnerable = false;
	private boolean invisible = false;
    
    public static final int MAX_FATIGUE = 18750;
	
	/**
	 * List of players who have attacked you last
	 * 
	 */
	
	public static ArrayList<Player> lastAttackee = new ArrayList<Player>();
	
	public DelayedEvent flagDelay = null;
	
	public void clearRedTeam() {
		ctfRed.clear();
	}
	
	public void addToRedTeam(Player player) {
		ctfRed.add(player);
		player.setRedTeam(true);
	}
	
	public void removeFromRedTeam(Player player) {
		ctfRed.remove(player);
		player.setRedTeam(false);
	}
	
	public void clearBlueTeam() {
		ctfBlue.clear();
	}
	
	public void addToBlueTeam(Player player) {
		ctfBlue.add(player);
		player.setBlueTeam(true);
	}
	
	public void removeFromBlueTeam(Player player) {
		ctfBlue.remove(player);
		player.setBlueTeam(false);
	}
	
	public void setBlueTeam(boolean BlueTeam) {
		isBlueTeam = BlueTeam;
	}

	public boolean isBlueTeam() {
		return isBlueTeam;
	}
	
	public void setRedTeam(boolean RedTeam) {
		isRedTeam = RedTeam;
	}

	public boolean isRedTeam() {
		return isRedTeam;
	}
	
	public void setHasRedFlag(boolean pickedRedFlag) {
		hasRedFlag = pickedRedFlag;
	}

	public boolean hasRedFlag() {
		return hasRedFlag;
	}
	
	public void setHasBlueFlag(boolean pickedBlueFlag) {
		hasBlueFlag = pickedBlueFlag;
	}

	public boolean hasBlueFlag() {
		return hasBlueFlag;
	}
	
	public void removeFromCtf(Player player)
	{	
		if (player.isBlueTeam())
		{
			player.removeFromBlueTeam(player);
			player.teleport(220, 441, false);
			if (player.hasRedFlag())
			{
				setHasRedFlag(false);
				World.redFlagInUse = 0;
				World.registerEntity(new GameObject(Point.location(801, 74), 1193, 2, 0));
				for (Player owner : World.getPlayers()) 
				{
					if (owner.getLocation().inCtf())
					{
						owner.getActionSender().sendMessage("@red@" + getUsername() + " has dropped the red flag!");
					}
					else
					{
						continue;
					}
				}
			}
		}
		if (player.isRedTeam())
		{
			player.removeFromRedTeam(player);
			player.teleport(220, 441, false);
			if (player.hasBlueFlag())
			{
				setHasBlueFlag(false);
				World.blueFlagInUse = 0;
				World.registerEntity(new GameObject(Point.location(779, 74), 1194, 6, 0));
				for (Player owner : World.getPlayers()) 
				{
					if (owner.getLocation().inCtf())
					{
						owner.getActionSender().sendMessage("@cya@" + getUsername() + " has dropped the blue flag!");
					}
					else
					{
						continue;
					}
				}
			}
		}
		else
		{
			player.teleport(220, 441, false);
		}
		//player.updateWornItems(11, -1); //minor bug, need to figure out how to remove capes after win
	}
			
	public void setShantayPrison(boolean jailed) {
		if (jailed)
			teleport(66, 729, false);
		inShantayPrison = jailed;
	}

	public boolean inShantayPrison() {
		return inShantayPrison;
	}

	public void setLeverA(boolean down) {
		this.leverA = down;
	}
	
	public void setLeverB(boolean down) {
		this.leverB = down;
	}
	
	public void setLeverC(boolean down) {
		this.leverC = down;
	}
	
	public void setLeverD(boolean down) {
		this.leverD = down;
	}
	
	public void setLeverE(boolean down) {
		this.leverE = down;
	}
	
	public void setLeverF(boolean down) {
		this.leverF = down;
	}
	
	public boolean leverADown() {
		return leverA;
	}
	
	public boolean leverBDown() {
		return leverB;
	}
	
	public boolean leverCDown() {
		return leverC;
	}
	
	public boolean leverDDown() {
		return leverD;
	}
	public boolean leverEDown() {
		return leverE;
	}
	
	public boolean leverFDown() {
		return leverF;
	}
	
	public void pullLeverADown() {
		leverA = true;
	}
	
	public void pullLeverAUp() {
		leverA = false;
	}
	
	public void pullLeverBDown() {
		leverB = true;
	}
	
	public void pullLeverBUp() {
		leverB = false;
	}
	
	public void pullLeverCDown() {
		leverC = true;
	}
	
	public void pullLeverCUp() {
		leverC = false;
	}
	
	public void pullLeverDDown() {
		leverD = true;
	}
	
	public void pullLeverDUp() {
		leverD = false;
	}
	
	public void pullLeverEDown() {
		leverE = true;
	}
	
	public void pullLeverEUp() {
		leverE = false;
	}
	
	public void pullLeverFDown() {
		leverF = true;
	}
	
	public void pullLeverFUp() {
		leverF = false;
	}

	public boolean willDoorAOpen() {
		return leverB && !leverF;
	}
	
	public boolean willDoorBOpen() {
		return leverA && leverB && !leverF;
	}
	
	public boolean willDoorCOpen() {
		return leverD;
	}
	
	public boolean willDoorDOpen() {
		return leverD && !leverF;
	}
	
	public boolean willDoorEOpen() {
		return leverD && !leverB && !leverF;
	}
	
	public boolean willDoorFOpen() {
		return leverF && leverD && !leverB;
	}
	
	public boolean willDoorGOpen() {
		return !leverA && leverE && leverF;
	}
	
	public boolean willDoorHOpen() {
		return leverD && leverF && !leverE;
	}
	
	public boolean willDoorIOpen() {
		return leverC && leverD && leverF && !leverA && !leverB && !leverE;
	}
	
	public boolean ladyFixed() {
		return ladyPatches > 2;
	}

	public void applyShipPatch(int holeID) {
		if (ladyPatches < 4)
			ladyPatches++;
		if (ladyPatches > 2 && holeID == 226) {
			sendMessage("The hole has been completely patched");
			Quest q = getQuest(Quests.DRAGON_SLAYER);
			if (q != null) {
				if (q.getStage() == 3)
					this.teleport(258, 3494, false);
				else
					this.teleport(280, 3494, false);
			}
		} else if(ladyPatches > 2 && holeID == 232) {
			sendMessage("The hole has been completely patched");
			Quest q = getQuest(Quests.DRAGON_SLAYER);
			if (q != null) {
				if (q.getStage() == 3)
					this.teleport(258, 3494, false);
				else
					this.teleport(280, 3494, false);
			}
		}
	}

	public int getLadyPatches() {
		return ladyPatches;
	}
	
	public void setLadyPatches(int patches) {
		this.ladyPatches = patches;
	}

	public void breakShip() {
		ladyPatches = 0;
	}
	
	public boolean onCrandor() {
		return onCrandor;
	}

	public void setCrandor(boolean crandor) {
		onCrandor = crandor;
	}
	
	public boolean hasMapPieceA() {
		return hasMapPieceA;
	}
	
	public void setHasMap(boolean has) {
		this.hasMapPieceA = has;
	}
	
	private HashMap<Integer, Boolean> cannonPartsFixed = new HashMap<Integer, Boolean>() {
		{
			put(0, false);
			put(1, false);
			put(2, false);
			put(3, false);
		}
	};
	
	public boolean isPipeFixed() {
		return cannonPartsFixed.get(0);
	}
	
	public boolean isBarrelFixed() {
		return cannonPartsFixed.get(1);
	}
	
	public boolean isAxleFixed() {
		return cannonPartsFixed.get(2);
	}
	
	public boolean isShaftFixed() {
		return cannonPartsFixed.get(3);
	}
	
	public void setPipeFixed(boolean fixed) {
		cannonPartsFixed.put(0, fixed);
	}
	
	public void setBarrelFixed(boolean fixed) {
		cannonPartsFixed.put(1, fixed);
	}
	
	public void setAxleFixed(boolean fixed) {
		cannonPartsFixed.put(2, fixed);
	}
	
	public void setShaftFixed(boolean fixed) {
		cannonPartsFixed.put(3, fixed);
	}
	
	public boolean isCannonFixed() {
		for (boolean b : cannonPartsFixed.values()) {
			if (b == false)
				return false;
		}
		return true;
	}
	
	public String[] getBrokenCannonParts() {
		ArrayList<String> brokenParts = new ArrayList<String>();
		int index = 0;
		for (boolean b : cannonPartsFixed.values()) {
			if (b == false)
				brokenParts.add(parts[index]);
			index++;
		}
		index = 0;
		String[] ret = new String[brokenParts.size()];
		ret = brokenParts.toArray(ret);
		return ret;
	}
	
	public boolean fixedPart(int index) {
		return cannonPartsFixed.get(index);
	}
	
	public void fixPart(String part) {
		if (inventory.countId(1055) > 0) {
			int index = 0;
			for (String brokenPart : parts) {
				if (brokenPart.equals(part) && cannonPartsFixed.get(index) == false) {
					final int fixedIndex = index;
					sendMessage("you use your tool kit and attempt to fix the " + part.toLowerCase());
//					Bubble bubble = new Bubble(this.getIndex(), 1055);
					for (Player p : this.getViewArea().getPlayersInView())
					{
						p.watchItemBubble(getIndex(), 1055);
//						p.informOfBubble(bubble);
					}
					World.getDelayedEventHandler().add(new SingleEvent(this, 1500) {
						public void action() {
							if (new java.util.Random().nextInt(4) != 0) {
								sendMessage("it's too hard, you fail to fix it");
								World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
									public void action() {
										sendMessage("maybe you should try again");
										setBusy(false);
										return;
									}
								});
							} else {
								World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
									public void action() {
										sendMessage("after some tinkering you manage to fix it");
										cannonPartsFixed.put(fixedIndex, true);
										if (isCannonFixed()) {
											sendMessage("The cannon now seems to be in complete working order");
											sendMessage("Lawgof should be very pleased");
										}
										setBusy(false);
										return;
									}
								});
							}
						}
					});
				}
				index++;
			}
		} else {
			sendMessage("you'll need a tool kit to fix this");
			setBusy(false);
		}
	}
	
	private HashMap<Integer, Boolean> railingsFixed = new HashMap<Integer, Boolean>() {
		{
			put(181, false);
			put(182, false);
			put(183, false);
			put(184, false);
			put(185, false);
			put(186, false);
		}
	};
	
	public void loadFixedRailing(int railingID, boolean fixed) {
		if (fixed)
			railingsFixed.put(railingID, true);
	}
	
	public boolean fixRailing(int railingID) {
		if (railingsFixed.get(railingID) == false)
			railingsFixed.put(railingID, true);
		for (boolean b : railingsFixed.values()) {
			if (b == false)
				return false;
		}
		return true;
	}
	
	public boolean fixedRailings() {
		for (boolean b : railingsFixed.values()) {
			if (b == false)
				return false;
		}
		return true;
	}
	
	public boolean isRailingFixed(int railingID) {
		return railingsFixed.get(railingID);
	}

	public final boolean leelaHasKey() {
		return leelaHasKey;
	}
	
	public final void giveLeelaKey() {
		leelaHasKey = true;
	}
	
	public final void takeLeelaKey() {
		leelaHasKey = false;
	}

	public final void setLeelaKey(boolean has) {
		leelaHasKey = has;
	}
	
	public final boolean hasRecievedKeyPayment() {
		return recievedKeyPayment;
	}

	public final void setRecievedKeyPayment(boolean paid) {
		recievedKeyPayment = paid;
	}
	
	public boolean wasSummoned() {
		return wasSummoned;
	}
	
	public void setSummoned(boolean summoned) {
		this.wasSummoned = summoned;
	}
	
	public void setReturnPoint() {
		wasSummoned = true;
		returnX = getX();
		returnY = getY();
	}
	
	public int getReturnX() {
		return returnX;
	}
	
	public int getReturnY() {
		return returnY;
	}
	
	public void setQuestPoints(int questPoints) {
		this.questPoints = questPoints;
	}

	public int getQuestPoints() {
		return questPoints;
	}

	public void incBones() {
		bones++;
	}
	
	public int getBones() {
		return bones;
	}
	
	public void setBones(int bones) {
		this.bones = bones;
	}
	
	public void startCollectingBones() {
		collectingBones = true;
	}
	
	public void setTraibornKey(boolean has) {
		hasTraibornKey = has;
	}
	
	public boolean hasTraibornKey() {
		return hasTraibornKey;
	}
	
	public void finishBones() {
		bones = 0;
		collectingBones = false;
	}
	
	public boolean collectingBones() {
		return collectingBones;
	}
	
	public void setCollectingBones(boolean collecting) {
		collectingBones = collecting;
	}
	
	public void incBallsOfWool() {
		ballsOfWool++;
	}
	
	public void setBallsOfWool(int number) {
		ballsOfWool = number;
	}
	
	public int getBallsOfWool() {
		return ballsOfWool;
	}
	
	public void setKilledSkeleton(boolean killed) {
		hasKilledSkeleton = killed;
	}
	
	public void killSkeleton() {
		hasKilledSkeleton = true;
	}
	
	public boolean hasKilledSkeleton() {
		return hasKilledSkeleton;
	}
	
	public void takeRum() {
		rumInSarimCrate = false;
	}
	
	public boolean rumInKaramjaCrate() {
		return rumInKaramjaCrate;
	}
	
	public void setRumInKaramjaCrate(boolean in) {
		rumInKaramjaCrate = in;
	}
	
	public boolean rumInSarimCrate() {
		return rumInSarimCrate;
	}
	
	public void setRumInSarimCrate(boolean in) {
		rumInSarimCrate = in;
	}
	
	public void putRumInCrate() {
		rumInKaramjaCrate = true;
	}
	
	public void shipRum() {
		rumInKaramjaCrate = false;
		rumInSarimCrate = true;
	}
	
	public void hire() {
		groceryStoreEmployee = true;
	}
	
	public void setStoreEmployee(boolean hired) {
		groceryStoreEmployee = hired;
	}
	
	public boolean isGroceryStoreEmployee() {
		return groceryStoreEmployee;
	}
	
	public int getBananas() {
		return bananasInCrate;
	}
	
	public void setBananas(int bananas) {
		bananasInCrate = bananas;
	}
	
	public void putBananaInCrate() {
		bananasInCrate++;
	}
	
	public boolean isJobFinished() {
		return bananasInCrate >= 10;
	}
	
	public void acceptBananaJob() {
		bananaJobAccepted = true;
	}
	
	public void setBananaJob(boolean hired) {
		bananaJobAccepted = hired;
	}
	
	public void finishBananaJob() {
		inventory.add(new InvItem(10, 30));
		sendInventory();
		bananasInCrate = 0;
		bananaJobAccepted = false;
		if (rumInKaramjaCrate)
			shipRum();
	}
	
	public boolean hasBananaJob() {
		return bananaJobAccepted;
	}

	public boolean isPoisoned() {
		return poisonPower > 0;
	}	
	
	public void grainInCookingGuildHopper(boolean flag) {
		grainInCookingGuildHopper = flag;
	}
	
	public boolean isGrainInCookingGuildHopper() {
		return grainInCookingGuildHopper;
	}
	
	public void grainInDraynorHopper(boolean flag) {
		grainInDraynorHopper = flag;
	}
	
	public boolean isGrainInDraynorHopper() {
		return grainInDraynorHopper;
	}
	
	public boolean canUseDoricsAnvil() {
		return canUseDoricsAnvil;
	}
	
	public boolean canUseCooksRange() {
		return canUseCooksRange;
	}
	
	public boolean isPhoenix() {
		return phoenixGangMember;
	}
	
	public void setPhoenix(boolean join) {
		this.phoenixGangMember = join;
	}
	
	public boolean isBlackarm() {
		return blackArmGangMember;
	}
	
	public void setBlackarm(boolean join) {
		this.blackArmGangMember = join;
	}
	
	public void setWildernessFlag(boolean flag) {
		wildernessFlag = flag;
	}
	
	public boolean getWildernessFlag() {
		return wildernessFlag;
	}
	
	public void setWornItemsChanged(boolean changed) {
		ourWornItemsChanged = changed;
	}
	
	public int getWornItemID() {
		return wornItemID;
	}
	
	public ArrayList<Quest> getQuests() {
		ArrayList<Quest> retQuests = new ArrayList<Quest>();
		for (Quest q : quests) {
			if (q.getID() < 52)
				retQuests.add(q);
		}
		return retQuests;
	}
	
	public Quest getQuest(int id) {
		for (Quest q : quests) {
			if (q.getID() == id)
				return q;
		}
		return null;
	}
		
	public void setLastSpellRandom(byte newRandom) {
		lastSpellRandom = newRandom;
	}
	
	public byte getLastSpellRandom() {
		return lastSpellRandom;
	}
	
	public int getAutocastProtection() {
		return autocastProtection;
	}
	
	public void incAutocastProtection() {
		autocastProtection++;
	}
	
	public void resetAutocastProtection() {
		autocastProtection = 0;
	}
	
	public void setCombatWindow(int newSetting) {
		this.combatWindow = newSetting;
	}
	
	public int getCombatWindow() {
		return combatWindow;
	}
	
	public boolean isInUnregisterQueue() {
		return inUnregisterQueue;
	}
	
	public void setMuted(
		long muted) {this.muted = muted;
	}
	
	public long getMuted() {
		return muted;
	}
	
	public boolean isUnregistered() {
		return unregistered;
	}
	
	public void unregister() {
		unregistered = true;
	}
	
	public void setCancelBatch(boolean batch) {		
		cancelBatch = batch;
	}	
	
	public boolean getCancelBatch() {
		return cancelBatch;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}
	
	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}
	
	public int getKills() {
		return kills;
	}
	
	public int getDeaths() {
		return deaths;
	}
	
	public void mute(long duration) {
		if (duration == 0)
			muted = 1;
		else
			muted = duration;
	}
	
	public void unmute() {
		muted = 0;
		if (muteEvent != null)
			muteEvent.stop();
	}
	
	public void ban() {
		ServerBootstrap.getDatabaseService().submit(new BanTransaction(usernameHash, true));
		World.unregisterEntity(this);		
	}	
	
	public void startDMCountdown() {
		World.getDelayedEventHandler().add(new SingleEvent(this, 3000) {
			public void action() {
				sendDMMessage("Prepare to fight...");								
			}
		});
		World.getDelayedEventHandler().add(new SingleEvent(this, 8000) {
			public void action() {
				sendDMMessage("3...");							
			}
		});	
		World.getDelayedEventHandler().add(new SingleEvent(this, 9000) {
			public void action() {
				sendDMMessage("2...");										
			}
		});							
		World.getDelayedEventHandler().add(new SingleEvent(this, 10000) {
			public void action() {
				sendDMMessage("1...");											
			}
		});	
		World.getDelayedEventHandler().add(new SingleEvent(this, 11000) {
			public void action() {
				teleport(getLocation().getX(), getLocation().getY(), true);
				sendDMMessage("@ran@F@ran@I@ran@G@ran@H@ran@T@ran@!");
				setDMStarted(DataConversions.getTimeStamp());				
				setBusy(false);
			}
		});
	}
	
	public void poison(int power) {
		if (!isPoisoned()) {
			this.poisonPower = power;
			poisonEvent = new DelayedEvent(this, 19500) {
				public void run() {
					damagePoison();
				}
			};
			World.getDelayedEventHandler().add(poisonEvent);
		} else
			this.poisonPower = power;
	}

	public int getPoison() {
		return poisonPower;
	}	
	
	public void curePoison() {
		this.poisonPower = 0;
		if (poisonEvent != null)
			poisonEvent.stop();
	}

	public void setPoisonPower(final int power) {
		if (power > 0) {
			World.getDelayedEventHandler().add(new MiniEvent(this) {
				public void action() {
					startPoison(power);
				}
			});	
		}
	}

	public void startPoison(int power) {
		this.poison(power);
		this.poisonPower = power;
		
		double poison = Math.ceil(poisonPower / 5);
		int damage = (int)poison + 1;			
		
		setLastDamage(damage);
		setCurStat(3, getCurStat(3) - damage);
		sendStat(3);			
		sendMessage("@gr3@You @gr2@are @gr1@Poisoned! @gr2@You @gr3@lose " + damage + " @gr1@health.");
		
		for (Player p : getViewArea().getPlayersInView())
			p.informOfModifiedHits(this);
		if (getCurStat(3) <= 0)
			killedBy(null, false);	
	}
	
	public void damagePoison() {
		if (this.poisonPower > 0) {
			double calcDamage = Math.ceil(poisonPower / 5);
			int damage = (int)calcDamage + 1;			
			poisonPower--;
			
			setLastDamage(damage);
			setCurStat(3, getCurStat(3) - damage);
			sendStat(3);			
			sendMessage("@gr3@You @gr2@are @gr1@Poisoned! @gr2@You @gr3@lose " + damage + " @gr1@health.");
			
			for (Player p : getViewArea().getPlayersInView())
				p.informOfModifiedHits(this);
			if (getCurStat(3) <= 0)
				killedBy(null, false);
		} else {
			if (poisonEvent != null)
				poisonEvent.stop();
			this.poisonPower = 0;
		}		
	}
	
	public boolean unlockAdmin() {
		return unlockAdmin;
	}
	
	public boolean unlockMod() {
		return unlockMod;
	}
	
	public boolean unlockDev() {
		return unlockDev;
	}
	
	public boolean unlockEvent() {
		return unlockEvent;
	}
	
	public void setAdmin() {
		unlockAdmin = unlockMod = unlockEvent = unlockDev = true;
	}
	
	public void setMod() {
		unlockMod = unlockDev = unlockEvent = true;
	}
	
	public void setDev() {
		unlockDev = true;
	}
	
	public void setEvent() {
		unlockEvent = true;
	}
	
	public void setLastGlobal(long timestamp) {
		this.lastGlobal = timestamp;
	}
	
	public long getLastGlobal() {
		return lastGlobal;
	}
	
	public void setLastTradeDuelUpdate(long timestamp) {
		this.lastTradeDuelUpdate = timestamp;
	}
	
	public void setDMStarted(long timestamp) {
		DMStarted = timestamp;
	}
	
	public long getLastTradeDuelUpdate() {
		return lastTradeDuelUpdate;
	}
	
	public long getDMStarted() {
		return DMStarted;
	}
	
	public String getDMAverageCasts() {
		double duration = DataConversions.getTimeStamp() - getDMStarted();	
		DecimalFormat df = new DecimalFormat("#.##");
		
		return df.format(duration / getDMCasts());
	}
	
	public void setLastCommand(long newTime) {
		this.lastCommand = newTime;
	}
	
	public long getLastCommand() {
		return lastCommand;
	}
	
	public void setRequiresOfferUpdate(boolean b) {
		requiresOfferUpdate = b;
	}
	
	public boolean requiresOfferUpdate() {
		return requiresOfferUpdate;
	}
	
	public void setStatus(Action a) {
		status = a;
	}
	
	public Action getStatus() {
		return status;
	}
		
	public void setAccount(int account) {
		this.account = account;
	}
	
	public int getAccount() {
		return account;
	}
	
	public Npc getNpc() {
		return interactingNpc;
	}
	
	public void setNpc(Npc npc) {
		interactingNpc = npc;
	}
	
	public void remove() {
		removed = true;
		resetAllExceptDMing();
	}
	
	public boolean initialized() {
		return initialized;
	}
	
	public void setInitialized() {
		initialized = true;
	}
	
	public int getDrainRate() {
		return drainRate;
	}
	
	public void setDrainRate(int rate) {
		drainRate = rate;
	}
	
	public MenuHandler getMenuHandler() {
		return menuHandler;
	}
	
	public boolean accessingShop() {
		return shop != null;
	}	
	
	public void setCastTimer() {
		lastSpellCast = System.currentTimeMillis();
	}
	
	public void setSpellFail() {
		lastSpellCast = System.currentTimeMillis() + 20000;
	}
	
	public int getSpellWait() { // Fix for 'have to wait 2 seconds...'
		return DataConversions.roundUp((double)(CAST_DELAY - (System.currentTimeMillis() - lastSpellCast)) / CAST_DELAY);
	}
	
	public long getCastTimer() {
		return lastSpellCast;
	}
	
	public boolean castTimer() {
		return System.currentTimeMillis() - lastSpellCast > CAST_DELAY;
	}
		
	public synchronized void informOfChatMessage(ChatMessage cm) {
		chatMessagesNeedingDisplayed.add(cm);
	}
	
	public void setLastSleep() {
		this.lastSleep = System.currentTimeMillis();
	}
	
	public void stopSleepEvent() {
		if (fatigueEvent != null) {
			fatigueEvent.stop();
			fatigueEvent = null;
		}
		if (sleepEvent != null) {
			sleepEvent.stop();
			sleepEvent = null;
		}
	}
	
	public void resetSleepCount() {
		sleepCount = 0;
	}
	
	public void increaseSleepCount() {
		sleepCount++;
	}
	
	public void setLastReport() {
		this.lastReport = System.currentTimeMillis();
	}	
	
	public String getSleepString() {
		return sleepWord;
	}
	
	public void setSleepString(String newString) {
		sleepWord = newString;
	}
	
	public long getLastSleep() {
		return lastSleep;
	}
	
	public long getLastReport() {
		return lastReport;
	}
	
	public void setLastEat() {
		lastEat = System.currentTimeMillis();
	}
	
	public boolean canEat() {
		return (System.currentTimeMillis() - lastEat) > EAT_DELAY && (System.currentTimeMillis() - lastDrink) > DRINK_DELAY;
	}
	
	public void setLastWield() {
		lastWield = System.currentTimeMillis();
	}
	
	public boolean canWield() {
		return (System.currentTimeMillis() - lastWield) > WIELD_DELAY;
	}	
	
	public void setLastDrink() {
		lastDrink = System.currentTimeMillis();
	}
	
	public boolean canDrink() {
		return (System.currentTimeMillis() - lastDrink) > DRINK_DELAY;
	}
	
	public void setUsername(String username) {
		this.username = username;
		this.usernameHash = DataConversions.usernameToHash(username);
	}
	
	public void setUsernameHash(long usernameHash) {
		this.usernameHash = usernameHash;
	}	

	public boolean accessingBank() {
		return inBank;
	}
	

	public void resetAuctionHouse() {
		setInAuctionHouse(false);
		actionSender.hideAuctionHouse();
	}

	public Shop getShop() {
		return shop;
	}
	
	public void setAccessingBank(boolean b) {
		inBank = b;
	}
	
	public int getCombatStyle() {
		return combatStyle;
	}
	
	public synchronized boolean destroyed() {
		return destroyed;
	}	
	
	public void setCombatStyle(int style) {
		combatStyle = style;
	}
	
	/*
	 * Pyru added
	 */
	
	public long lastAttackedTime = 0;
	public long getLastAttacker;

	public long setLastAttacker(Player player)
	{
		return getLastAttacker = player.getUsernameHash();
	}

	public void setLastAttackedTime()
	{
		lastAttackedTime = System.currentTimeMillis();
	}
	
	public void setInCombatWith(Player p) {
		inCBWith = p;
	}
	
	public Player getInCBWith() {
		return inCBWith;
	}
	
	/*
	 * 
	 */
	
	public void setCharged() {
		lastCharge = System.currentTimeMillis();
	}
	
	public boolean isCharged() {
		return lastCharge + 420000 > System.currentTimeMillis() && lastCharge != 0;
	}
	
	public List<ChatMessage> getNpcMessagesNeedingDisplayed() {
		return npcMessagesNeedingDisplayed;
	}
	
	public List<ChatMessage> getChatMessagesNeedingDisplayed() {
		return chatMessagesNeedingDisplayed;
	}
	
	public void clearNpcMessagesNeedingDisplayed() {
		npcMessagesNeedingDisplayed.clear();
	}
	
	public void clearChatMessagesNeedingDisplayed() {
		chatMessagesNeedingDisplayed.clear();
	}
	
	public List<Player> getPlayersRequiringHitsUpdate() {
		return playersNeedingHitsUpdate;
	}
	
	public List<Npc> getNpcsRequiringHitsUpdate() {
		return npcsNeedingHitsUpdate;
	}
	
	public void clearPlayersNeedingHitsUpdate() {
		playersNeedingHitsUpdate.clear();
	}
	
	public void clearNpcsNeedingHitsUpdate() {
		npcsNeedingHitsUpdate.clear();
	}
	
	public void informOfProjectile(Projectile p) {
		projectilesNeedingDisplayed.add(p);
	}
	
	public List<Projectile> getProjectilesNeedingDisplayed() {
		return projectilesNeedingDisplayed;
	}
	
	public void clearProjectilesNeedingDisplayed() {
		projectilesNeedingDisplayed.clear();
	}		

	public ArrayList<Long> getFriendList() {
		return friendList;
	}
	
	public ArrayList<Long> getIgnoreList() {
		return ignoreList;
	}
	
	public void removeFriend(long id) {
		friendList.remove(id);
	}
	
	public void removeIgnore(long id) {
		ignoreList.remove(id);
	}
	
	public void addFriend(long id) {
		friendList.add(id);
	}
	
	public void addIgnore(long id) {
		ignoreList.add(id);
	}
	
	public void setFriends(long[] friends) {
		for (long l : friends)
			friendList.add(l);
	}
	
	public void setIgnores(long[] ignores) {
		for (long l : ignores)
			ignoreList.add(l);
	}
	
	public int friendCount() {
		return friendList.size();
	}
	
	public int ignoreCount() {
		return ignoreList.size();
	}
	
	public void setTradeConfirmAccepted(boolean b) {
		tradeConfirmAccepted = b;
	}	
	
	public void setDuelConfirmAccepted(boolean b) {
		duelConfirmAccepted = b;
	}
	
	public void setDMConfirmAccepted(boolean b) {
		DMConfirmAccepted = b;
	}
	
	public boolean isTradeConfirmAccepted() {
		return tradeConfirmAccepted;
	}
	
	public boolean isDuelConfirmAccepted() {
		return duelConfirmAccepted;
	}
	
	public boolean isDMConfirmAccepted() {
		return DMConfirmAccepted;
	}
	
	public void setTradeOfferAccepted(boolean b) {
		tradeOfferAccepted = b;
	}
	
	public void setDuelOfferAccepted(boolean b) {
		duelOfferAccepted = b;
	}
	
	public void setDMOfferAccepted(boolean b) {
		DMOfferAccepted = b;
	}
	
	public boolean isTradeOfferAccepted() {
		return tradeOfferAccepted;
	}
	
	public boolean isDuelOfferAccepted() {
		return duelOfferAccepted;
	}
	
	public boolean isDMOfferAccepted() {
		return DMOfferAccepted;
	}
	
	public void resetTradeOffer() {
		tradeOffer.clear();
	}
	
	public void resetDuelOffer() {
		duelOffer.clear();
	}
	
	public void addToTradeOffer(InvItem item) {
		tradeOffer.add(item);
	}
	
	public void addToDuelOffer(InvItem item) {
		duelOffer.add(item);
	}
	
	public ArrayList<InvItem> getTradeOffer() {
		return tradeOffer;
	}
	
	public ArrayList<InvItem> getDuelOffer() {
		return duelOffer;
	}
	
	public void setTrading(boolean b) {
		isTrading = b;
	}
	
	public void setDueling(boolean b) {
		isDueling = b;
	}
	
	public void setDMing(boolean b) {
		isDMing = b;
	}
	
	public boolean isTrading() {
		return isTrading;
	}
	
	public boolean isDueling() {
		return isDueling;
	}
	
	public boolean isDMing() {
		return isDMing;
	}
	
	public void setWishToTrade(Player p) {
		wishToTrade = p;
	}
	
	public void setWishToDuel(Player p) {
		wishToDuel = p;
	}
	
	public void setWishToDM(Player p) {
		wishToDM = p;
	}
	
	public void setInDMWith(Player p) {
		inDMWith = p;
	}
	
	public Player getWishToTrade() {
		return wishToTrade;
	}
	
	public Player getWishToDuel() {
		return wishToDuel;
	}
	
	public Player getWishToDM() {
		return wishToDM;
	}
	
	public Player getInDMWith() {
		return inDMWith;
	}
	
	public void setDuelSetting(int i, boolean b) {
		duelOptions[i] = b;
	}
	
	public void setDMSetting(int i, boolean b) {
		DMOptions[i] = b;
	}
	
	public void setMale(boolean male) {
		maleGender = male;
	}
	
	public boolean isMale() {
		return maleGender;
	}
	
	public void setChangingAppearance(boolean b) {
		changingAppearance = b;
	}
	
	public boolean isChangingAppearance() {
		return changingAppearance;
	}
	
	public void setLastLogin(long l) {
		lastLogin = l;
	}
	
	public long getLastLogin() {
		return lastLogin;
	}
	
	public void setLastLogout(long l) {
		lastLogout = l;
	}
	
	public long getLastLogout() {
		return lastLogout;
	}
	
	public void setLastDeath(long l) {
		lastDeath = l;
	}
	
	public long getLastDeath() {
		return lastDeath;
	}
	
	public int getDaysSinceLastLogin() {
		return (int)(((Calendar.getInstance().getTimeInMillis() / 1000) - lastLogin) / 86400);
	}
	
	public long getCurrentLogin() {
		return currentLogin;
	}
	
	public void setLastIP(String ip) {
		lastIP = ip;
	}
	
	public String getIP() {
		return IP;
	}
	
	public String getLastIP() {
		return lastIP;
	}
	
	public void setGroupID(int id) {
		groupID = id;
        ourAppearanceChanged = true; // Trigger an appearance update to pass new group to the client.
	}
	
	public int getGroupID() {
		return groupID;
	}
	
	public boolean isSub() {
		if (DataConversions.getTimeStamp() < subscriptionExpires) {
			isSub = true;
            if (groupID == Group.USER)
                setGroupID(Group.SUBSCRIBER);
			return true;
		} else {
			if (isSub) {
				isSub = false;
				if (groupID == Group.SUBSCRIBER)
					setGroupID(Group.USER);
				sendAlert("Your subscription period has expired.");
			}
			return false;
		}
	}
    
	public void setInvulnerable(boolean isInvulnerable)
    {
		this.isInvulnerable = isInvulnerable;
        ourAppearanceChanged = true; // Trigger an appearance update to pass invulnerability to client
	}
    
    public boolean toggleInvulnerable()
    {
        setInvulnerable(!isInvulnerable());
        return isInvulnerable();
    }
	
    public boolean isInvulnerable()
    {
        return isInvulnerable;
    }
    
	public void setInvisible(boolean invisible)
    {
		this.invisible = invisible;
        ourAppearanceChanged = true; // Trigger an appearance update to pass invisibility to client
        
        List<Player> playersInView = this.getViewArea().getPlayersInView();
        
        if (this.isInvisible())
        {
            for (Player remove : playersInView)
                if(remove != this && !remove.isAdmin())
                    remove.removeWatchedPlayer(this);
        }
        else
        {
            for (Player informee : playersInView)
                if(informee != this)
                    informee.informOfPlayer(this);
        }
	}
    
    public boolean toggleInvisible()
    {
        setInvisible(!isInvisible());
        return isInvisible();
    }
	
    public boolean isInvisible()
    {
        return invisible;
    }
    
    public boolean isOwner() {
        return groupID == Group.OWNER;
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
	
	public boolean isDev() {
		return groupID == Group.DEV || isAdmin();
	}	
	
	public boolean isEvent() {
		return groupID == Group.EVENT || isAdmin();
	}
    
    public boolean isStaff(){
        return isMod() || isDev() || isEvent();
    }
	
	public Inventory getInventory() {
		return inventory;
	}
	
	public void setInventory(Inventory i) {
		inventory = i;
	}
	
	public Bank getBank() {
		return bank;
	}
	
	public void setBank(Bank b) {
		bank = b;
	}
	
	public void setGameSetting(int i, boolean b) {
		gameSettings[i] = b;
	}
	
	public boolean getGameSetting(int i) {
		return gameSettings[i];
	}
	
	public boolean[] getGameSettings() {
		return gameSettings;
	}
	
	public void setPrivacySetting(int i, boolean b) {
		privacySettings[i] = b;
	}
	
	public boolean getPrivacySetting(int i) {
		return privacySettings[i];
	}
	
	public boolean[] getPrivacySettings() {
		return privacySettings;
	}
	
	public long getLastPing() {
		return lastPing;
	}

	public IoSession getSession() {
		return session;
	}

	public boolean loggedIn() {
		return loggedIn;
	}
	
	public String getUsername() {
		return DataConversions.ucwords(username);
	}

	public long getUsernameHash() {
		return usernameHash;
	}

	public boolean isSkulled() {
		return skullEvent != null;
	}

	public PlayerAppearance getPlayerAppearance() {
		return appearance;
	}
	
	public void setAppearance(PlayerAppearance appearance) {
		this.appearance = appearance;
		super.ourAppearanceChanged = true;
	}
	
	public boolean isFriendsWith(long usernameHash) {
		return friendList.contains(usernameHash);
	}
	
	public boolean isIgnoring(long usernameHash) {
		return ignoreList.contains(usernameHash);
	}
	
	public int[] getWornItems() {
		return wornItems;
	}	
	
	public StatefulEntityCollection<Player> getWatchedPlayers() {
		return watchedPlayers;
	}
	
	public HashMap<Integer, Integer> getKnownPlayersAppearanceIDs() {
		return knownPlayersAppearanceIDs;
	}
	
	public HashMap<Integer, Integer> getKnownPlayersWornItemIDs() {
		return knownPlayersWornItemIDs;
	}
	
	public StatefulEntityCollection<GameObject> getWatchedObjects() {
		return watchedObjects;
	}
	
	public StatefulEntityCollection<Item> getWatchedItems() {
		return watchedItems;
	}
	
	public StatefulEntityCollection<Npc> getWatchedNpcs() {
		return watchedNpcs;
	}
	
	public void removeWatchedNpc(Npc n) {
		watchedNpcs.remove(n);
	}
	
	public void removeWatchedItem(Item i) {
		watchedItems.remove(i);
	}	
	
	public void removeWatchedPlayer(Player p) {
		watchedPlayers.remove(p);
	}
	
	public int[] getCurStats() {
		return curStat;
	}
	
	public int getCurStat(int id) {
		return curStat[id];
	}
	
	public int getHits() {
		return getCurStat(3);
	}
	
	public int getAttack() {
		return getCurStat(0);
	}
	
	public void setFatigue(int fatigue) {
		this.fatigue = fatigueApplicator.getFatigueIncrement(fatigue);
	}
	
	public int getFatigue() {
		return fatigue;
	}
    
    public boolean isFatigued(){
        return fatigue >= Player.MAX_FATIGUE;
    }
	
	public int getTemporaryFatigue() {
		return temporaryFatigue;
	}
	
	public int[] getExps() {
		return exp;
	}
	
	public int getExp(int id) {
		return exp[id];
	}
	
	public void setExp(int[] lvls) {
		exp = lvls;
	}	
	
	public int getDefense() {
		return getCurStat(1);
	}	
	
	public int getStrength() {
		return getCurStat(2);
	}	
	
	public void setHits(int lvl) {
		setCurStat(3, lvl);
	}
	
	public void setAttack(int lvl) {
		setCurStat(0, lvl);
	}
	
	public void setStrength(int lvl) {
		setCurStat(2, lvl);
	}
	
	public void setDefense(int lvl) {
		setCurStat(1, lvl);
	}
	
	public int getMaxStat(int id) {
		return maxStat[id];
	}
	
	public int[] getMaxStats() {
		return maxStat;
	}	

	public boolean isWearing(int id) {
		boolean isWearing = false;
		for (InvItem item : inventory.getItems()) {
			if (item.getID() == id && item.isWielded()) {
				isWearing = true;
				break;
			}
		}
		return isWearing;
	}
		
	public void setQuests(ArrayList<Quest> quests) {
		this.quests = quests;
	}
	
	public void addQuest(int questID, int questPoints) {
		quests.add(new Quest(questID, 0, false, questPoints));
		if (questID < 52)
			sendQuestStarted(questID);
		ServerBootstrap.getDatabaseService().submit(new QuestSave(usernameHash, questID, 0, false, questPoints));
	}
	
	public void addQuest(int questID, int questCompletionStage, boolean finished, int questPoints) {
		quests.add(new Quest(questID, questCompletionStage, finished, questPoints));
	}

	public int getQuestCompletionStage(int questID) {
		for (Quest q : quests) {
			if (q.getID() == questID)
				return q.getStage();
		}
		return -1;		
	}
	
	public void incQuestCompletionStage(int questID) {
		for(Quest q : quests) {
			if (q.getID() == questID) {
				q.incStage();
				if (!q.finished()) {
					ServerBootstrap.getDatabaseService().submit(new QuestSave(usernameHash, q.getID(), q.getStage(), false, q.getQuestPoints()));
					break;
				}
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void decQuestCompletionStage(int questID) {
		for (Quest q : quests) {
			if (q.getID() == questID) {
				q.decStage();
				ServerBootstrap.getDatabaseService().submit(new QuestSave(usernameHash, q.getID(), q.getStage(), false, q.getQuestPoints()));
				break;
			}
		}
	}
	
	public void finishQuest(int questID) {
		for (Quest q : quests) {
			if (q.getID() == questID) {
				q.finish();
				ServerBootstrap.getDatabaseService().submit(new QuestSave(usernameHash, q.getID(), q.getStage() + 1, true, q.getQuestPoints()));
				questPoints += q.getQuestPoints();
				if (q.getID() < 52) {
					sendCompletedQuest(questID);
					sendQuestPointUpdate();
				}
				break;
			}
		}
	}
	
	public void sleep() {
		sleep(false);
	}
	
	public void sleep(boolean bag) {
		if (!fatigueApplicator.isFatigueEnabled()) {
			return;
		}

		this.sleepingBag = (bag);
			if (this.sleepEvent != null)
				this.sleepEvent.stop();

			if (Config.isBanFailedSleep()) {
					// Bans if 10 sleep words are incorrect
					if (this.sleepCount >= 10) {
						synchronized (World.getPlayers()) {
							for (Player p : World.getPlayers()) {
								if (p.isSuperMod()) {
									p.sendAlert(getUsername() + " was banned for getting 10 sleep words wrong");
								}
							}
						}
						Logger.log(new GenericLog(getUsername() + " was banned for getting 10 sleep words wrong", DataConversions.getTimeStamp()));
						ban();
					}
			}

			// if (Config.ENABLE_SLEEP_WORDS) {

			this.sleepEvent = new DelayedEvent(this, (this.fatigueEvent == null) ? DataConversions.random(100, 500) : DataConversions.random((isSub()) ? 100 : 500, (isSub()) ? 100 : 500)) {
				public void run() {
					if (Config.isDisableSleepWords()) {
						Player.this.sendSuccess();
						Player.this.setFatigue(0);
						Player.this.sendFatigue();
						Player.this.sendMessage("You wake up - feeling refreshed");
						Player.this.sleepEvent.stop();
						Player.this.sleepEvent = null;
					} else {
					Pair<String, BufferedImage> pair = Captcha.getCaptcha();
					if (pair == null) {
						Logger.log(new GenericLog("A BUG HAS BEEN FOUND WITH THE CAPTCHA! (NULL PAIR RETURNED!)\n This could mean that no captchas are currently loaded!", (int) System.currentTimeMillis()));
						return;
					}
					Player.this.sleepImage = pair.getSecond();
					if (Player.this.fatigueEvent == null) {
						Player.this.temporaryFatigueThrottle();
						Player.this.temporaryFatigue = Player.this.getFatigue();
					}
					if (Player.this.sleepImage == null) {
						Player.this.sendSuccess();
						Player.this.setFatigue(0);
						Player.this.sendFatigue();
						Player.this.sendMessage("You wake up - feeling refreshed");
					} else {
						Player.this.sleepWord = pair.getFirst();
						Player.this.actionSender.sendSleepImage(Player.this.sleepImage);
					}
					Player.this.sleepEvent.stop();
					Player.this.sleepEvent = null;
					}
				}
			};
			World.getDelayedEventHandler().add(this.sleepEvent);
		}
	
	private void temporaryFatigueThrottle() {
        if (!fatigueApplicator.isFatigueEnabled()) {
            return;
        }

        fatigueEvent = new DelayedEvent(this, 600) {
            public void run() {
                int tick = sleepingBag ? 350 : 700;
                if (isSub())
                    tick *= 2;
                if (temporaryFatigue - tick < 0)
                    temporaryFatigue = 0;
                else
                    temporaryFatigue -= tick;
                sendTemporaryFatigue();
            }
        };

        World.getDelayedEventHandler().add(fatigueEvent);
	}

	public boolean tradeDuelThrottling() {
		long now = System.currentTimeMillis();
		if (now - lastTradeDuelRequest > 1000) {
			lastTradeDuelRequest = now;
			return false;
		}
		return true;
	}
	
	public void addMessageToChatQueue(String messageData) {
		byte[] message = DataConversions.stringToByteArray(messageData);
		chatQueue.add(new ChatMessage(this, message));
	}
	
	public ChatMessage getNextChatMessage() {
		return chatQueue.poll();
	}

	public int checkForUsableArrows() {
		int usableArrow = -1;
		List<Integer> cantUse = new ArrayList<Integer>();
		List<InvItem> items = getInventory().getItems();
		int[][] wepArray;
		int bowType = getBowType();
		if (bowType == 2) {
			if (World.isP2PWilderness() || getLocation().wildernessLevel() < 1)
				wepArray = Formulae.boltsP2P;
			else
				wepArray = Formulae.boltsF2P;
		} else {
			if (World.isP2PWilderness() || getLocation().wildernessLevel() < 1)
				wepArray = Formulae.arrowsP2P;
			else
				wepArray = Formulae.arrowsF2P;
		}
		if(bowType != -1) {
			for (int i=0; i<items.size(); i++) {
				if (items.get(i).getDef().isStackable()) {
					for (int n=0; n<wepArray[0].length; n++) {
						if (items.get(i).getID() == wepArray[1][n]) {
							if (getMaxStat(4) >= wepArray[0][n])
								usableArrow = wepArray[1][n];
							else
								cantUse.add(n);
						}
					}
				}
			}
			if (usableArrow == -1) {
				for (int i : cantUse)
					sendMessage("You need at least " + wepArray[0][i] + " range to use " + EntityHandler.getItemDef(wepArray[1][i]).getName());
			}
		}
		return usableArrow;
	}

	public double[] getBowStats() {
		try {
			double[] longBowStats = {2000, 1.55, 5};
			double[] shortBowStats = {1550, 1.2, 4};
			double[] xbowStats = {1800, 1.4, 4};
			if (inventory != null) {
				for (InvItem it : this.getInventory().getItems()) {
					if (!it.isWielded())
						continue;
					for (int i1 : Formulae.longBowIds)
						if (it.getID() == i1)
							return longBowStats;
					for (int i1 : Formulae.shortBowIds)
						if (it.getID() == i1)
							return shortBowStats;
					for (int i1 : Formulae.xbowIDs)
						if (it.getID() == i1)
							return xbowStats;
				}
			}
		} catch(Exception ex) {}
		return null;
	}
	
	public void setArrowFired() {
		lastArrow = System.currentTimeMillis();
	}

	public void setNpcRangeEvent(NpcRangeEvent event) {
		if (isRanging())
			resetRange();
		npcRangeEvent = event;
		npcRangeEvent.setLastRun(lastArrow);
		World.getDelayedEventHandler().add(npcRangeEvent);
	}
	
	public void setRanger(long player) {
		this.ranger = player;
	}
	
	public long getRanger() {
		return ranger;
	}
	
	public void setPlayerRangeEvent(PlayerRangeEvent event, long target) {
		if (isRanging())
			resetRange();
		playerRangeEvent = event;
		playerRangeEvent.setLastRun(lastArrow);
		World.getDelayedEventHandler().add(playerRangeEvent);
		Player targetPlayer = World.getPlayer(target);
		if (targetPlayer != null)
			targetPlayer.setRanger(usernameHash);
		setTarget(target);
	}
	
	public int getBowType() {
		if (inventory != null) {
			for (InvItem it : inventory.getItems()) {
				if (!it.isWielded())
					continue;
				for (int i1 : Formulae.longBowIds)
					if(it.getID() == i1) return 0;
				for (int i1 : Formulae.shortBowIds)
					if(it.getID() == i1) return 1;
				for (int i1 : Formulae.xbowIDs)
					if(it.getID() == i1) return 2;
			}
		}
		return -1;
	}

	public boolean canRange() {
		int rangeDelay = (getBowStats() != null ? (int)getBowStats()[0] : 1500);
		return System.currentTimeMillis() - lastRanged > rangeDelay;
	}
	
	public void resetRangeTimer() {
		lastRanged = System.currentTimeMillis();
	}

	public boolean isRanging() {
		return playerRangeEvent != null || npcRangeEvent != null;
	}
	
	public void clearRanger() {
		ranger = -1;
	}
	
	public void clearTarget() {
		target = -1;
	}
	
	public void setTarget(long target) {
		this.target = target;
	}
	
	public void resetRange() {
		if (playerRangeEvent != null) {
			playerRangeEvent.stop();
			playerRangeEvent = null;
		}
		if (npcRangeEvent != null) {
			npcRangeEvent.stop();
			npcRangeEvent = null;
		}
		if (target != -1) {
			Player targetPlayer = World.getPlayer(target);
			if (targetPlayer != null)
				targetPlayer.clearRanger();
		}
		clearTarget();
      	setStatus(Action.IDLE);
	}
	
	public boolean canLogout()
	{
		if(deathmatchEvent != null || isBusy() || inCombat() || System.currentTimeMillis() - getCombatTimer() < 10000) return false;
		return true;
	}
	
	public boolean isFollowing() {
		return followEvent != null && following != null;
	}
	
	public boolean isFollowing(Mob mob) {
		return isFollowing() && mob.equals(following);
	}
	
	public void setFollowing(Mob mob) {
		setFollowing(mob, 0);
	}
	
	public void setFollowing(final Mob mob, final int radius) {
		if (isFollowing())
			resetFollowing();
		following = mob;
		followEvent = new DelayedEvent(this, 500) {
			public void run() {
				if (!owner.withinRange(mob) || mob.isRemoved() || (owner.isBusy() && !owner.isDueling()))
					resetFollowing();
				else if (!owner.finishedPath() && owner.withinRange(mob, radius))
					owner.resetPath();
				else if (owner.finishedPath() && !owner.withinRange(mob, radius + 1))
					owner.setPath(new Path(owner.getX(), owner.getY(), mob.getX(), mob.getY()));
			}
		};
		World.getDelayedEventHandler().add(followEvent);
	}
	
	public void resetFollowing() {
		following = null;
		if (followEvent != null) {
			followEvent.stop();
			followEvent = null;
		}
		resetPath();
	}
	
	public void setSkulledOn(Player player) {
		player.addAttackedBy(this);
        /*if (System.currentTimeMillis() - lastAttackedBy(player) > 1200000)
			addSkull(1200000);*/
		if (!wasAttackedBy(player))
			addSkull();
	}
	
	public long getSubscriptionExpires() {
		return subscriptionExpires;
	}
	
	public void setSubscriptionExpires(long expires) {
		subscriptionExpires = expires;
	}
	
	public void setRecoveryQuestions(long recoveries) {
		recoveryQuestions = recoveries;
	}
	
	public boolean getRecovery() {
		if (recoveryQuestions == -1)
			return false;
		else
			return true;		
	}
	
	public int getRecoveryDays() {
		if (recoveryQuestions == 0) {
			return (int)100;
		} else {
			int days = (int)((DataConversions.getTimeStamp()) - recoveryQuestions) / 86400;
			if (days > 6)
				return 200;
			else
				return days;
		}
	}	
	
	public void setUnreadMessages(int unread) {
		unreadMessages = unread;
	}
	
	public int getUnreadMessages() {
		return unreadMessages;
	}
	
	public int getDaysSubscriptionLeft() {
		long now = DataConversions.getTimeStamp();
		if (subscriptionExpires == 0 || now >= subscriptionExpires)
			return 0;
		return (int)((subscriptionExpires - now) / 86400);
	}
	
	public boolean addPacket(RSCPacket p) {
		ping();
		long now = System.currentTimeMillis();
		if (now - lastCount > 3000) {
			lastCount = now;
			packetCount = 0;
		}
		if (!DataConversions.inArray(Formulae.safePacketIDs, p.getID()) && packetCount++ >= 60) {
			destroy(false);
			return false;
		}
		return true;
	}
	
	public int getRangeType(int id) {
		if (DataConversions.inArray(Formulae.longBowIds, id))
			return 5;
		return 4;
	}
	
	public int getRangeEquip() {
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded() && (DataConversions.inArray(Formulae.bowIDs, item.getID()) || DataConversions.inArray(Formulae.xbowIDs, item.getID())))
				return item.getID();
		}
		return -1;
	}
	
	public void resetAll() {
		resetAllExceptTradeOrDuel();
		resetTrade();
		resetDuel();
		resetDM();
	}
	
	public void resetTrade() {
		Player opponent = getWishToTrade();
		if (opponent != null)
			opponent.resetTrading();
		resetTrading();
	}
	
	public void resetDuel() {
		Player opponent = getWishToDuel();
		if (opponent != null)
			opponent.resetDueling();
		resetDueling();
	}
	
	public void resetDM() {
		Player opponent = getWishToDM();
		if (getLocation().isInDMArena())
			teleport(216, 2905);		
		if (opponent != null) {
			if (opponent.getLocation().isInDMArena())
				opponent.teleport(216, 2905);			
			opponent.resetDMing();
		}
		resetDMing();
	}
	
	public void resetAllExceptTrading() {
		resetAllExceptTradeOrDuel();
		resetDuel();
	}
	
	public void resetAllExceptDueling() {
		resetAllExceptTradeOrDuel();
		resetTrade();
	}
	
	public void resetAllExceptDMing() {
		resetAllExceptTradeOrDuel();
		resetTrade();
		resetDuel();
	}
	
	public void resetAllExceptTradeOrDuel() {
		if (getMenuHandler() != null)
		{
			getMenuHandler().onMenuCancelled();
			resetMenuHandler();
		}
		if (accessingBank())
			resetBank();
		if (accessingShop())
			resetShop();
		if (interactingNpc != null)
			interactingNpc.unblock();
		if (isFollowing())
			resetFollowing();
		if (isRanging())
			resetRange();
		setStatus(Action.IDLE);
	}
	
	public void setMenuHandler(MenuHandler menuHandler) {
		menuHandler.setOwner(this);
		this.menuHandler = menuHandler;
	}
	
	public void resetMenuHandler() {
		menuHandler = null;
		hideMenu();
	}
	
	public void setAccessingShop(Shop shop) {
		this.shop = shop;
		if (shop != null)
			shop.addPlayer(this);
	}
	
	public void resetShop() {
		if(shop != null) {
			shop.removePlayer(this);
			shop = null;
			hideShop();
		}
	}
	
	public void resetBank() {
		setAccessingBank(false);
		hideBank();
	}

	public Player(IoSession session) {
		this.session = session;
		IP = ((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress();
		currentLogin = DataConversions.getTimeStamp();
		actionSender = new MiscPacketBuilder(this);
		setBusy(true);
	}
		
	private transient boolean destroying;
	
	public synchronized boolean destroying()
	{
		return destroying;
	}
	
	public synchronized void destroy(boolean force) {
		if (destroyed)
			return;		
		if (force || canLogout())
		{
			destroyed = true;
			remove();
		}
		else {
			destroying = true;
			final long startDestroy = System.currentTimeMillis();
			
			World.getDelayedEventHandler().add(new DelayedEvent(this, 3000) {
				public void run() {
					if(owner.canLogout() || (!owner.isFightingInCage() && !owner.isDueling() && System.currentTimeMillis() - startDestroy > 60000))
					{
						owner.destroy(true);
						destroying = false;
						running = false;
					}
				}
			});
		}
	}
	
	public final void addDrainer(int delay, int lastRun) {
		drainer = new DelayedEvent(this, delay) {
			public void run() {
				int curPrayer = getCurStat(5);
				if (getDrainRate() > 0 && curPrayer > 0) {
					incCurStat(5, -1);
					sendStat(5);
					if (curPrayer <= 1) {
						for (int prayerID = 0; prayerID < 14; prayerID++) {
							if (activatedPrayers[prayerID]) {
								removePrayerDrain(prayerID);
								setPrayer(prayerID, false);
							}
						}
						stop();
						sendMessage("You have run out of prayer points. Return to a church to recharge");
						sendPrayers();
					}
				}
			}
		};
		drainer.setLastRun(System.currentTimeMillis() - lastRun);
		World.getDelayedEventHandler().add(drainer);
	}
	
	public final void enforceWildernessRules(int x, int y)
	{
		enforceWildernessRules(new Point(x, y));
	}
	
	
	public final void enforceWildernessRules(Point p)
	{
		if (p.inWilderness()) {
			if (!wildernessFlag) {
				boolean recievedMessage = false;
				
				for (InvItem currentItem : inventory.getItems()) 
				{
					if (currentItem.isWielded() && currentItem.getDef().isP2P() && !World.isP2PWilderness())
					{			
						currentItem.setWield(false);
						updateWornItems(currentItem.getWieldableDef().getWieldPos(), appearance.getSprite(currentItem.getWieldableDef().getWieldPos()));
						sendSound("click", false);
						sendEquipmentStats();
						sendInventory();
						if (!recievedMessage) 
						{
							sendMessage(Config.getPrefix() + "P2P items are not wieldable during F2P wilderness.");
							recievedMessage = true;
						}
					}
				}				
				if (getMaxStat(6) >= 51 && getMaxStat(1) >= 40 && getMaxStat(0) < 40 && getMaxStat(2) < 50)
				{
					if (getLocation().inWilderness())
					{
						teleport(231, 442);
						sendMessage("Tank mages are exempt from the wilderness.");
						return;
					}	
				}
				if (!World.isP2PWilderness() && getLocation().wildernessLevel() > 0)
				{
					for (int i = 0; i < 5; i++) {
						int min = getCurStat(i);
						int max = getMaxStat(i);
						int baseStat = getCurStat(i) > getMaxStat(i) ? getMaxStat(i)
								: getCurStat(i);
						int newStat = baseStat
								+ DataConversions.roundUp((getMaxStat(i) / 100D) * 10)
								+ 2;
						if (min > newStat || (min > max && (i == 0 || i == 1 || i == 4))) {
							setCurStat(i, max);
							sendMessage(Config.getPrefix() + "Your super / P2P potion effect has been reset as the wilderness state is not P2P.");
							getActionSender().sendStat(i);
						}
					}
				}
				sendStats();
			}
		} else {
			if (wildernessFlag)
				wildernessFlag = false;
		}
	}
	
	
	
	private final class HealthRestoreEvent
		extends
			DelayedEvent
	{
		
		HealthRestoreEvent()
		{
			super(Player.this, 60000);
		}
		
		public void run()
		{
			int i = 3;
			int curStat = getCurStat(i);
			int maxStat = getMaxStat(i);
			if (curStat > maxStat) {
				setCurStat(i, curStat - 1);
				sendStat(i);
				checkStat(i);
			} else if(curStat < maxStat) {
				setCurStat(i, curStat + 1);
				sendStat(i);
				checkStat(i);
			}
		}
		private void checkStat(int statIndex) {
			if (statIndex != 3 && owner.getCurStat(statIndex) == owner.getMaxStat(statIndex))
				owner.sendMessage("Your " + Formulae.STAT_ARRAY[statIndex] + " ability has returned to normal.");
		}
	}

	private final class StatRestoreEvent
		extends
			DelayedEvent
	{
		
		StatRestoreEvent()
		{
			super(Player.this, 60000);
		}
		
		public void run()
		{
			for (int i = 0; i < Formulae.STAT_ARRAY.length; i++) {
				if (i == 5 || i == 3)
					continue;
				int curStat = getCurStat(i);
				int maxStat = getMaxStat(i);
				if (curStat > maxStat) {
					setCurStat(i, curStat - 1);
					sendStat(i);
					checkStat(i);
				} else if(curStat < maxStat) {
					setCurStat(i, curStat + 1);
					sendStat(i);
					checkStat(i);
				}
			}
		}
		private void checkStat(int statIndex) {
			if (statIndex != 3 && owner.getCurStat(statIndex) == owner.getMaxStat(statIndex))
				owner.sendMessage("Your " + Formulae.STAT_ARRAY[statIndex] + " ability has returned to normal.");
		}
	}
	
	private final HealthRestoreEvent healthRestoreEvent = new HealthRestoreEvent();
	private final StatRestoreEvent statRestoreEvent = new StatRestoreEvent();
	public void load() {
		returnX = getX();
		returnY = getY();
		super.ourAppearanceChanged = true;
		ourWornItemsChanged = true;
		loggedIn = true;
		setBusy(false);
		setLastMoved();
		synchronized (World.getDelayedEventHandler().getEvents()) {
			World.getDelayedEventHandler().add(healthRestoreEvent);
			World.getDelayedEventHandler().add(statRestoreEvent);
			
			World.getDelayedEventHandler().add(new DelayedEvent(this, 10000) { //autosaves players every 10 seconds
				public void run() {
					Save s = new Save(owner);
					ServerBootstrap.getDatabaseService().submit(s, s.new DefaultSaveListener());
				}
			});
		}
		
		sendLoginInformation();
		/*
		if(getLocation().inWilderness() && System.currentTimeMillis() - getLogoutDate() >= 1 * 60 * 60 * 1000) {
			setLocation(Point.location(220, 445), true);
			sendMessage("You have been logged out in the wilderness for over 1 hour.");
			sendMessage("Your character has been returned to Edgeville.");
		}
		*/
		if (getLocation().inWilderness())
			enforceWildernessRules(super.getLocation());

		for (Quest q : quests) {
			if (q.getID() == 2) {
				if (q.finished())
					canUseCooksRange = true;
			} else if(q.getID() == 4) {
				if (q.finished())
					canUseDoricsAnvil = true;
			}
		}
	}
	
	public void resetTrading() {
		if (isTrading()) {
			sendTradeWindowClose();
			setStatus(Action.IDLE);
		}
      	setWishToTrade(null);
      	setTrading(false);
      	setTradeOfferAccepted(false);
      	setTradeConfirmAccepted(false);
      	resetTradeOffer();
	}
	
	public void resetDueling() {
		if (isDueling()) {
			sendDuelWindowClose();
			setStatus(Action.IDLE);
		}
      	setWishToDuel(null);
      	setDueling(false);
      	setDuelOfferAccepted(false);
      	setDuelConfirmAccepted(false);
      	resetDuelOffer();
      	clearDuelOptions();
	}
	
	public void resetDMing() {
		this.deathmatchEvent = null;
		this.dmVictoryEvent = null;
      	if (isDMing()) {
      		sendDMWindowClose();
      		setStatus(Action.IDLE);
      	}
      	setWishToDM(null);
      	setDMing(false);
      	setDMOfferAccepted(false);
      	setDMConfirmAccepted(false);
      	clearDMOptions();
      	setInDMWith(null);
      	setDMStarted(0);
      	DMCasts = 0;
	}
	
	public void clearDuelOptions() {
		for (int i = 0;i < 4;i++)
			duelOptions[i] = false;
	}
	
	public void increaseDMCasts() {
		DMCasts++;
	}
	
	public int getDMCasts() {
		return DMCasts;
	}
	
	public void clearDMOptions() {
		for (int i = 0; i < 4; i++)
			DMOptions[i] = false;
	}
	
	public final class DMVictoryEvent
		extends
			SingleEvent
	{
		public final DMHandler.Cage cage;
		private boolean playerExited;
		public DMVictoryEvent(Player player, DMHandler.Cage cage)
		{
			super(player, 180 * 1000);
			this.cage = cage;
			super.owner.sendMessage("You have 180 seconds to loot.");
			playerExited = false;
		}

		@Override
		public void action()
		{
			if(!playerExited)
			{
				super.owner.sendMessage("Time's up");
				owner.teleport(Point.location(218, 2901), true);
			}
			cage.setActive(false);
			owner.dmVictoryEvent.stop();
			owner.dmVictoryEvent = null;
		}
		
		public void setPlayerExited()
		{
			playerExited = true;
		}
	}
	
	public void killedBy(Mob mob, boolean stake) {
		if (!loggedIn) {
			Logger.log(new ErrorLog(usernameHash, account, IP, "Not logged in and killedBy()", DataConversions.getTimeStamp()));
			return;
		}
		if (poisonEvent != null) {
			poisonEvent.stop();
			poisonPower = 0;
		}
		if (inventory.contains(318)) { // Karamja Rum
			while (inventory.contains(318))
				inventory.remove(318, 1);
			sendInventory();
		}
		if (mob instanceof Player) {
      		Player player = (Player)mob;
      		player.sendMessage("You have defeated " + getUsername() + "!");
      		player.sendSound("victory", false);
      		player.getActionSender().sendTakeScreenshot();
      		if (player.getLocation().inWilderness()) {
				deaths += 1;
				player.setKills(player.getKills() + 1);
				player.sendKills();
				sendDeaths();
				killCount += 1;
				Logger.log(new eventLog(player.getUsernameHash(), player.getAccount(), player.getIP(), DataConversions.getTimeStamp(), "<strong>" + player.getUsername() + "</strong>" + " has defeated " + "<strong>" + getUsername() + "</strong>"));
      		}
		}
		Mob opponent = super.getOpponent();
		if (opponent != null)
		{
			opponent.resetCombat(CombatState.WON);
		}
		else
		{
			// Experimental 'npc stuck in combat' fix 8.2.2013
			try
			{
                if(this.getFightEvent()!= null)
                    this.getFightEvent().getOpponent().resetCombat(CombatState.WON);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		sendDied();
		resetStats();
		sendStats();
		
		Player player = mob instanceof Player ? (Player)mob : null;
		Player[] owners = null;
		if (stake) {
			for (InvItem item : duelOffer) {
      			InvItem affectedItem = getInventory().get(item);
      			if (affectedItem == null) {
      				Logger.log(new ErrorLog(usernameHash, account, IP, "Missing staked item [" + item.getID() + ", " + item.getAmount() + "] from = " + usernameHash + "; to = " + player.getUsernameHash() + ";", DataConversions.getTimeStamp()));
      				continue;
      			}
      			if (affectedItem.isWielded()) {
      				affectedItem.setWield(false);
      				updateWornItems(affectedItem.getWieldableDef().getWieldPos(), getPlayerAppearance().getSprite(affectedItem.getWieldableDef().getWieldPos()));
      			}
      			getInventory().remove(item);
      			long amount = item.getAmount();
      			World.registerEntity(new Item(item.getID(), getX(), getY(), amount, player));
      		}
			owners = new Player[] { player };
		}
		else if (getLocation().inCtf())
		{
			getActionSender().sendMessage("Your items are safe in CTF");
		}
		else {
			if (!this.getLocation().isInWarZone() || !World.safeCombat) {
				DeathLog log = new DeathLog(usernameHash, account, IP, getX(), getY(), DataConversions.getTimeStamp());
				inventory.sort();
				ListIterator<InvItem> iterator = inventory.iterator();
				if (!isSkulled()) {
					for (int i = 0; i < 3 && iterator.hasNext(); i++) {
						if ((iterator.next()).getDef().isStackable()) {
							iterator.previous();
							break;
						}
					}
				}
				if (activatedPrayers[8] && iterator.hasNext()) {
					if (iterator.next().getDef().isStackable())
						iterator.previous();
				}
				
				owners = new Player[] { player };
				
				while(iterator.hasNext()) {
					InvItem item = iterator.next();
					if (item.isWielded()) {
						item.setWield(false);
						updateWornItems(item.getWieldableDef().getWieldPos(), appearance.getSprite(item.getWieldableDef().getWieldPos()));
					}
					iterator.remove();
					log.addLostItem(item);
					
					//if (item.getDef().isTradable())
					//	World.registerEntity(new Item(item.getID(), getX(), getY(), item.getAmount(), this));
					if(mob instanceof Npc) // hack :(  this entire routine should be redone at some point. ready to test this bug now.
					{
						World.registerEntity(new Item(item.getID(), getX(), getY(), item.getAmount(), (Player[])null));
					}
					else {
						World.registerEntity(new Item(item.getID(), getX(), getY(), item.getAmount(), owners));
					}
				}
				removeSkull();
				setWarp();
				Logger.log(log);
			}
		}
		// drop the players bones
		if(mob instanceof Npc) { 
			World.registerEntity(new Item(20, getX(), getY(), 1, (Player[])null));
		} else
			World.registerEntity(new Item(20, getX(), getY(), 1, owners));
		
		for (int x = 0; x < activatedPrayers.length; x++) {
			if (activatedPrayers[x]) {
				removePrayerDrain(x);
				activatedPrayers[x] = false;
			}
		}
		sendPrayers();
		if(this.deathmatchEvent != null)
		{
			setLocation(Point.location(218, 2901), true);
			sendMessage("You lost the DM");
			((Player)mob).sendMessage("You won the DM");
			
			DMVictoryEvent temp = new DMVictoryEvent((Player)mob, ((Player)mob).deathmatchEvent.cage);
			
			deathmatchEvent.end();
			
			((Player)mob).dmVictoryEvent = temp;
			World.getDelayedEventHandler().add(temp);
			
		}
		else
		{
			if (getLocation().inCtf())
			{
				if (isRedTeam() == true)
				{
					if(hasBlueFlag() == true)
					{
						for (Player owner : World.getPlayers()) 
						{
							if (owner.getLocation().inCtf())
							{
								owner.getActionSender().sendNotification("@red@" + getUsername() + " has dropped the blue flag!");
								World.registerEntity(new GameObject(Point.location(779, 74), 1194, 6, 0));
								setHasBlueFlag(false);
								World.blueFlagInUse = 0;
							}
						}
					}
					setLocation(Point.location(806, 75), true);
				}
				if (isBlueTeam() == true)
				{
					if(hasRedFlag() == true)
					{
						for (Player owner : World.getPlayers()) 
						{
							if (owner.getLocation().inCtf())
							{
								owner.getActionSender().sendNotification("@cya@" + getUsername() + " has dropped the red flag!");
								World.registerEntity(new GameObject(Point.location(801, 74), 1193, 2, 0));
								setHasRedFlag(false);
								World.redFlagInUse = 0;
							}
						}
					}
					setLocation(Point.location(774, 74), true);
				}
			}
			else
			{
				/*if(this.isSub())
					setLocation(Point.location(220, 445), true);
				else*/
					setLocation(Point.location(122, 648), true);
			}
		}	
		setDeathTime(System.currentTimeMillis());
		Collection<Player> allWatched = watchedPlayers.getAllEntities();
		for (Player p : allWatched)
			p.removeWatchedPlayer(this);
		
		resetPath();
		resetCombat(CombatState.LOST);
		resetMenuHandler();
		sendWorldInfo();
		sendEquipmentStats();
		sendInventory();
        
        if (mob instanceof Player) {
			for (Player p : meleeDamageTable.keySet()) {
				if (p != null) {
					if(!p.isDueling()) {
						int exp;
						float partialExp = Formulae.combatExperience(this);
						exp = (int)(partialExp * ((float)meleeDamageTable.get(p) / (float)getMaxStat(3)));
						switch (p.getCombatStyle()) {
							case 0:
								p.increaseXP(Skills.ATTACK, exp);
								p.increaseXP(Skills.DEFENSE, exp);
								p.increaseXP(Skills.STRENGTH, exp);
								p.increaseXP(Skills.HITS, exp);
								p.sendStat(0);
								p.sendStat(1);
								p.sendStat(2);
								p.sendStat(3);
							break;
							
							case 1:
								p.increaseXP(Skills.STRENGTH, exp * 3);
								p.sendStat(2);
								p.increaseXP(Skills.HITS, exp);
								p.sendStat(3);							
							break;
							
							case 2:
								p.increaseXP(Skills.ATTACK, exp * 3);
								p.sendStat(0);
								p.increaseXP(Skills.HITS, exp);
								p.sendStat(3);							
							break;
							
							case 3:
								p.increaseXP(Skills.DEFENSE, exp * 3);
								p.sendStat(1);
								p.increaseXP(Skills.HITS, exp);
								p.sendStat(3);
							break;
						}
					}
				}
			}
			for(Player p : rangeDamageTable.keySet()) {
				if (p != null) {
					if (!p.isDueling()) {
						int exp;
						float partialExp = Formulae.combatExperience(this);
						exp = (int)(partialExp * ((float)rangeDamageTable.get(p) / (float)getMaxStat(3)));
						p.increaseXP(Skills.RANGED, exp * 4);
						p.sendStat(4);
					}
				}
			}
			meleeDamageTable.clear();
			rangeDamageTable.clear();
            magicDamageTable.clear();
			totalDamageTable.clear();
        }
	}
	
	public DMVictoryEvent dmVictoryEvent;

	public boolean isFightingInCage()
	{
		return deathmatchEvent != null || dmVictoryEvent != null;
	}
	
	public boolean canPullDMExitLever()
	{
		return dmVictoryEvent != null;
	}
	
	private DMHandler.DMEvent deathmatchEvent;
	
	public DMHandler.DMEvent getDeathmatchEvent()
	{
		return deathmatchEvent;
	}
	
	public void setDeathmatchEvent(DMHandler.DMEvent deathmatchEvent)
	{
		this.deathmatchEvent = deathmatchEvent;
	}
	
	public int countFood(Player p) {
		int count = 0;
		for (InvItem item : p.getInventory().getItems())
			if (item.isEdible())
				count++;
		return count;
	}
	
	public void addAttackedBy(Player p) {
        if(!p.wasAttackedBy(this))
            attackedBy.put(p.getUsernameHash(), System.currentTimeMillis());
	}
	
	public long lastAttackedBy(Player p) {
		Long time = attackedBy.get(p.getUsernameHash());
		if (time != null)
			return time;
		return 0;
	}
    
    public boolean wasAttackedBy(Player p) {
		return attackedBy.get(p.getUsernameHash()) != null;
    }
	
	public boolean checkAttack(Mob mob, boolean missile) {
		if (mob instanceof Player) {
			Player victim = (Player)mob;
			if ((inCombat() && isDueling()) && (victim.inCombat() && victim.isDueling())) {
				Player opponent = (Player)getOpponent();
				if (opponent != null && victim.equals(opponent))
					return true;
			}
			if ((System.currentTimeMillis() - mob.getCombatTimer() < 500 || System.currentTimeMillis() - mob.getRunTimer() < 3000) && !mob.inCombat())
				return false;
			int myWildLvl = getLocation().wildernessLevel();
			int victimWildLvl = victim.getLocation().wildernessLevel();
			if (!getLocation().inCtf())
			{
				if (myWildLvl < 1 || victimWildLvl < 1) {
					sendMessage("You can't attack other players here. Move to the wilderness");
					resetFollowing();
					resetPath();
					return false;
				}
				int combDiff = Math.abs(getCombatLevel() - victim.getCombatLevel());
				if (combDiff > myWildLvl || combDiff > victimWildLvl) {
					sendMessage("You can only attack players within " + myWildLvl + " level" + (myWildLvl > 1 ? "s" : "") + " of your own here");
					sendMessage("Move further into the wilderness for less restrictions");
					resetFollowing();
					resetPath();				
					return false;
				}
				return true;
			}
			if (isBlueTeam() && victim.isBlueTeam() || isRedTeam() && victim.isRedTeam()) //sick cunt
	  		{
	  			getActionSender().sendMessage("You cannot attack team members");
	  			resetPath();
	  			return false;
	  		}
		} else if (mob instanceof Npc) {
			Npc victim = (Npc)mob;
			if (!victim.getDef().isAttackable())
				return false;
		}
		return true;
	}
	
	public synchronized void informOfNpcMessage(ChatMessage cm) {
		npcMessagesNeedingDisplayed.add(cm);
	}
	
	public void informOfModifiedHits(Mob mob) {
		if (mob instanceof Player)
			playersNeedingHitsUpdate.add((Player)mob);
		else if (mob instanceof Npc)
			npcsNeedingHitsUpdate.add((Npc)mob);
	}

	public final void modifyPrayerDrain() {
		if (drainer != null) {
			if (drainer.running())
				drainer.setDelay((int)((180000 / drainRate) * (1 + (0.04D * getPrayerPoints()))));
		} else
			addDrainer((int)(200 + (180000 / drainRate) * (1 + (0.04D * getPrayerPoints()))), drainerPartialFactor);
	}
	
	public final void addPrayerDrain(int prayerID) {
		if(prayerID == 6) //rapid restore
		{
			healthRestoreEvent.setDelay(30000);
		}
		else if(prayerID == 7) //rapid heal
		{
			healthRestoreEvent.setDelay(30000);
		}
		PrayerDef prayer = EntityHandler.getPrayerDef(prayerID);
		drainRate += prayer.getDrainRate();
		if (drainer != null) {
			if (!drainer.running())
				addDrainer((int)(200 + (180000 / drainRate) * (1 + (0.04D * getPrayerPoints()))), drainerPartialFactor);
			else
				drainer.setDelay((int)(200 + (180000 / drainRate) * (1 + (0.04D * getPrayerPoints()))));
		} else
			addDrainer((int)(200 + (180000 / drainRate) * (1 + (0.04D * getPrayerPoints()))), drainerPartialFactor);
	}
	
	public final void removePrayerDrain(int prayerID) {
        if(prayerID == 6) //rapid restore
        {
            healthRestoreEvent.setDelay(60000);
        }
        else if(prayerID == 7) //rapid heal
        {
            healthRestoreEvent.setDelay(60000);
        }
		PrayerDef prayer = EntityHandler.getPrayerDef(prayerID);
		drainRate -= prayer.getDrainRate();
		if (drainRate <= 0) {
			drainerPartialFactor = drainer.getDelay() - drainer.timeTillNextRun();
			drainRate = 0;
			drainer.stop();
		} else
			drainer.setDelay((int)(200 + (180000 / drainRate) * (1 + (0.04D * getPrayerPoints()))));
	}

	public int[] getDuelOptions() {
		return new int[] {duelOptions[0] ? 1 : 0, duelOptions[1] ? 1 : 0, duelOptions[2] ? 1 : 0, duelOptions[3] ? 1 : 0};
	}
	public int[] getDMOptions() {
		return new int[] {DMOptions[0] ? 1 : 0, DMOptions[1] ? 1 : 0, DMOptions[2] ? 1 : 0, DMOptions[3] ? 1 : 0};
	}
	
	public boolean getDuelSetting(int i) {
		try {
			Player affectedPlayer = this.getWishToDuel();
			for (InvItem item : duelOffer) {
				if (DataConversions.inArray(Formulae.runeIDs, item.getID())) {
					if (!duelOptions[1]) {
						affectedPlayer.sendMessage("When runes are staked, magic can't be used during the duel");
						setDuelSetting(1, true);
						break;
					} else
						break;
				}
			}
			for (InvItem item : wishToDuel.getDuelOffer()) {
				if (DataConversions.inArray(Formulae.runeIDs, item.getID())) {
					if (!duelOptions[1]) {
						affectedPlayer.sendMessage("When runes are staked, magic can't be used during the duel");
						setDuelSetting(1, true);
						break;
					} else
						break;
				}
			}
		} catch(Exception e) {}
		return duelOptions[i];
	}
	
	public boolean getDMSetting(int i) {
		return DMOptions[i];
	}	
	
	public int getArmourPoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded())
				points += item.getWieldableDef().getArmourPoints();
		}
		return points < 1 ? 1 : points;
	}
	
	public int getWeaponAimPoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded())
				points += item.getWieldableDef().getWeaponAimPoints();
		}
		return points < 1 ? 1 : points;
	}
	
	public int getWeaponPowerPoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded())
				points += item.getWieldableDef().getWeaponPowerPoints();
		}
		return points < 1 ? 1 : points;
	}
	
	public int getMagicPoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded())
				points += item.getWieldableDef().getMagicPoints();
		}
		return points < 1 ? 1 : points;
	}
	
	public int getPrayerPoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded())
				points += item.getWieldableDef().getPrayerPoints();
		}
		return points < 1 ? 1 : points;
	}
	
	public int getRangePoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded())
				points += item.getWieldableDef().getRangePoints();
		}
		return points < 1 ? 1 : points;
	}

	public void updateWornItems(int index, int id) {
		wornItems[index] = id;
		wornItemID++;
		ourWornItemsChanged = true;
		if (drainer != null) {
			if (drainer.running())
				modifyPrayerDrain();
		}
	}
	
	public void setWornItems(int[] worn) {
		wornItems = worn;
		wornItemID++;
		ourWornItemsChanged = true;
	}
	
	public void setLoggedIn(boolean loggedIn) {
		if (loggedIn)
			currentLogin = DataConversions.getTimeStamp();
		this.loggedIn = loggedIn;
	}
	
	public void ping() {
		lastPing = System.currentTimeMillis();
	}

	public int getSkullTime() {
		if (isSkulled())
			return skullEvent.timeTillNextRun();
		return 0;
	}
    
    public void addSkull() {
        addSkull(1200000);
    }
	
	public void addSkull(long timeLeft) {
		if (!isSkulled()) {
			skullEvent = new DelayedEvent(this, 1200000) {
				public void run() {
					removeSkull();
				}
			};
			World.getDelayedEventHandler().add(skullEvent);
			super.setAppearnceChanged(true);
		}
		skullEvent.setLastRun(System.currentTimeMillis() - (1200000 - timeLeft));
	}
	
	public void removeSkull() {
		if (!isSkulled())
			return;
		super.setAppearnceChanged(true);
		skullEvent.stop();
		skullEvent = null;
	}
	
	public void addWornItemAppearanceIDs(int[] indicies, int[] wornItemIDs) {
		for (int index = 0; index < indicies.length; index++)
			knownPlayersWornItemIDs.put(indicies[index], wornItemIDs[index]);
	}
	
	public void addPlayersAppearanceIDs(int[] indicies, int[] appearanceIDs) {
		for (int x = 0; x < indicies.length; x++)
			knownPlayersAppearanceIDs.put(indicies[x], appearanceIDs[x]);
	}

	public List<Player> getPlayersRequiringAppearanceUpdate() {
		List<Player> needingUpdates = new ArrayList<Player>();
		needingUpdates.addAll(watchedPlayers.getNewEntities());
		if (super.ourAppearanceChanged)
			needingUpdates.add(this);
		for (Player p : watchedPlayers.getKnownEntities()) {
			if (needsAppearanceUpdateFor(p) && (!p.isInvisible() || this.isAdmin()))
				needingUpdates.add(p);
		}
		return needingUpdates;
	}
	
	public List<Player> getPlayersRequiringUsernameUpdate() {
		List<Player> needingUpdates = new ArrayList<Player>();
		needingUpdates.addAll(watchedPlayers.getNewEntities());
		needingUpdates.add(this);
		needingUpdates.addAll(watchedPlayers.getKnownEntities());
		return needingUpdates;
	}
	
	public List<Player> getPlayersRequiringWornItemUpdate() {
		List<Player> needingUpdates = new ArrayList<Player>();
		needingUpdates.addAll(watchedPlayers.getNewEntities());
		if (ourWornItemsChanged)
			needingUpdates.add(this);
		for (Player p : watchedPlayers.getKnownEntities()) {
			if (needsWornItemUpdateFor(p))
				needingUpdates.add(p);
		}
		return needingUpdates;
	}
	
	private boolean needsWornItemUpdateFor(Player p) {
		int sIndex = p.getIndex();
		if (knownPlayersWornItemIDs.containsKey(sIndex)) {
			int knownWornItemID = knownPlayersWornItemIDs.get(p.getIndex());
			if (knownWornItemID != p.getWornItemID())
				return true;
		} else
			return true;
		return false;
	}
	
	private boolean needsAppearanceUpdateFor(Player p) {
		int playerServerIndex = p.getIndex();
		if (knownPlayersAppearanceIDs.containsKey(playerServerIndex)) {
			int knownPlayerAppearanceID = knownPlayersAppearanceIDs.get(playerServerIndex);
			if (knownPlayerAppearanceID != p.getAppearanceID())
				return true;
		} else
			return true;
		return false;
	}
	
	public void updateViewedPlayers() {
		List<Player> playersInView = viewArea.getPlayersInView();
		for (Player p : playersInView) {
			if (p.getIndex() != getIndex() && p.loggedIn()) {
                p.informOfPlayer(this);
                this.informOfPlayer(p);
			}
		}
	}
	
	public void updateViewedObjects() {
		List<GameObject> objectsInView = viewArea.getGameObjectsInView();
		for (GameObject o : objectsInView) {
			if (!watchedObjects.contains(o) && !o.isRemoved() && withinRange(o))
				watchedObjects.add(o);
		}
	}
	
	public void updateViewedItems() {
		for (Item i : viewArea.getItemsInView()) {
			if (!watchedItems.contains(i) && !i.isRemoved() && withinRange(i) && i.visibleTo(this))
				watchedItems.add(i);
		}
		
	}
	
	public void updateViewedNpcs() {
		List<Npc> npcsInView = viewArea.getNpcsInView();
		for (Npc n : npcsInView) {
			if ((!watchedNpcs.contains(n) || watchedNpcs.isRemoving(n)) && withinRange(n))
				watchedNpcs.add(n);
		}
	}
	
	public void teleport(int x, int y) {
		teleport(x, y, false);
	}
	
	public void teleport(Point point, boolean bubble) {
		teleport(point.getX(), point.getY(), bubble);
	}

	public void teleport(int x, int y, boolean bubble) {
		boolean farAway = 	// If it's on a different tier
				(Formulae.getHeight(getLocation()) != Formulae.getHeight(new Point(x, y))) 
				||
				// Or if the distance is greater than 45 (the hypotenuse of a 32, 32 right triangle)
				(Formulae.distance2D(getLocation(), new Point(x, y)) > 45);
		if(farAway) {
			resetLevers();
		}
		Mob opponent = super.getOpponent();
		if (inCombat())
			resetCombat(CombatState.ERROR);
		if (opponent != null)
			opponent.resetCombat(CombatState.ERROR);
    	for (Object o : getWatchedPlayers().getAllEntities()) {
    		Player p = ((Player)o);
    		if (bubble)
    			p.sendTeleBubble(getX(), getY(), false);
    		p.removeWatchedPlayer(this);
    	}
    	if (bubble)
    		sendTeleBubble(getX(), getY(), false);
		if (getLocation().inWilderness()) 
		{			
			Point p = Point.location(x,y);
			enforceWildernessRules(p);
		}
		setLastWalk(System.currentTimeMillis());
		setLastMoved();
    	setLocation(Point.location(x, y), true);
    	resetPath();
		resetAllExceptDMing();
		if(farAway)
		{
			sendWorldInfo();
		}
	}
	
	public void informOfPlayer(Player p) {
		if ((!p.isInvisible() || this.isAdmin()) && (!watchedPlayers.contains(p) || watchedPlayers.isRemoving(p)) && withinRange(p))
			watchedPlayers.add(p);
	}
	
	public void revalidateWatchedPlayers() {
		for (Player p : watchedPlayers.getKnownEntities()) {
			if (!p.registered() || !withinRange(p) || !p.loggedIn() || p.isInvisible()) {
				watchedPlayers.remove(p);
				knownPlayersAppearanceIDs.remove(p.getIndex());
				knownPlayersWornItemIDs.remove(p.getIndex());
			}
		}
	}
	
	public void revalidateWatchedObjects() {
		for (GameObject o : watchedObjects.getKnownEntities()) {
			if (!withinRange(o) || o.isRemoved() || !o.registered())
				watchedObjects.remove(o);
		}
	}
	
	public void revalidateWatchedItems() {
		for (Item i : watchedItems.getKnownEntities()) {
			if (!i.registered() || !withinRange(i) || i.isRemoved() || !i.visibleTo(this))
				watchedItems.remove(i);
		}
	}
	
	public void revalidateWatchedNpcs() {
		for (Npc n : watchedNpcs.getKnownEntities()) {
			if (!n.registered() || !withinRange(n) || n.isRemoved())
				watchedNpcs.remove(n);
		}
	}

	public boolean withinRange(Entity e) {
		int xDiff = location.getX() - e.getLocation().getX(); 
		int yDiff = location.getY() - e.getLocation().getY();
		return xDiff <= 16 && xDiff >= -15 && yDiff <= 16 && yDiff >= -15;
	}
	
	public void setCurStats(int[] levels) {
		for (int i = 0; i < Formulae.STAT_ARRAY.length; i++)
			curStat[i] = levels[i];
	}
	
	public void setCurStat(int id, int lvl) {
		if (lvl <= 0)
			lvl = 0;
		curStat[id] = lvl;
	}
	
	public void setMaxStats(int[] levels) {
		for (int i = 0; i < Formulae.STAT_ARRAY.length; i++)
			maxStat[i] = levels[i];
	}
	
	public void setMaxStat(int id, int lvl) {
		if (lvl < 0)
			lvl = 0;
		maxStat[id] = lvl;
	}
		
	public int getSkillTotal() {
		int total = 0;
		for (int stat : maxStat)
			total += stat;
		return total;
	}
	
	public void incCurStat(int i, int amount) {
		curStat[i] += amount;
		if (curStat[i] < 0)
			curStat[i] = 0;
	}
	
	public void incMaxStat(int i, int amount) {
		maxStat[i] += amount;
		if (maxStat[i] < 0)
			maxStat[i] = 0;
	}
	
	public void incQuestExp(int stat, int amount) {
		/*if (isFatigued()) {
			sendMessage("@gre@You recieve no experience from this quest");
			return;
		}*/
		exp[stat] += amount;
		if (exp[stat] < 0)
			exp[stat] = 0;
		int level = Formulae.experienceToLevel((int) exp[stat]);
		if (level != maxStat[stat]) {
			int advanced = level - maxStat[stat];
			incCurStat(stat, advanced);
			incMaxStat(stat, advanced);
			sendStat(stat);
			sendMessage("@gre@You just advanced " + advanced + " " + Formulae.STAT_ARRAY[stat] + " level" + (advanced > 1 ? "s" : "") + "!");
			sendSound("advance", false);
			World.getDelayedEventHandler().add(new MiniEvent(this) {
				public void action() {
					owner.sendScreenshot();
				}
			});
			int combat = Formulae.getCombatlevel(maxStat);
			if (combat != getCombatLevel())
				setCombatLevel(combat);
		}		
	}
	
	public void increaseXP(int stat, int xp) {
		if (isDMing)
			return;

		if (fatigueApplicator.isFatigueEnabled()) {
			int currentFatigue = getFatigue();
			if(isFatigued()) {
				sendMessage("@gre@You are too tired to gain experience, get some rest!");
				return;
			}

			currentFatigue += isSub() ? xp / 4 : xp;

			// Clamp the fatigue at a maximum value.
			if (currentFatigue > Player.MAX_FATIGUE) {
				currentFatigue = Player.MAX_FATIGUE;
			}

			setFatigue(currentFatigue);
			sendFatigue();
		}
        
		if (getLocation().onTutorialIsland()) {
			if (exp[stat] + xp > 800) {
				if (stat != 3) {
					exp[stat] = 800;
				} else {
					exp[stat] = 4800;
				}
			}
		}

		if (isSub() && stat > 6) // Non combat, subbed
		{
			if (getLocation().inWilderness())
			{
				if (isSkulled())
					xp *= (Config.getSkulledXpBonus() + Config.getSkillXpSub());
				else
					xp *= (Config.getWildXpBonus() + Config.getSkillXpSub());
			}
			else
			{
				xp *= Config.getSkillXpSub();
			}
		} 
		else 
		if (!isSub() && stat > 6) // Non combat, non subbed
		{
			xp *= Config.getSkillXpRate();
		}
		else
		if (isSub() && stat <= 6) // Combat, subbed.
		{
			if (getLocation().inWilderness()) // Combat, in wilderness
			{
				if (isSkulled())
					xp *= (Config.getCombatXpSub() + Config.getSkulledXpBonus());
				else
					xp *= (Config.getCombatXpSub() + Config.getWildXpBonus());
				
			}
			else
			{
				xp *= Config.getCombatXpSub();
			}
		}
        else // combat, nonsubbed
		{
            xp *= Config.getCombatXpRate();
		}
						
        exp[stat] += xp;
        
		if (exp[stat] < 0)
			exp[stat] = 0;
        
		int level = Formulae.experienceToLevel((int) exp[stat]);
		if (level != maxStat[stat]) {
			int advanced = level - maxStat[stat];
			incCurStat(stat, advanced);
			incMaxStat(stat, advanced);
			sendStat(stat);
			sendMessage("@gre@You just advanced " + advanced + " " + Formulae.STAT_ARRAY[stat] + " level!");
			sendSound("advance", false);
			
			/*if (level >= 95) {
				synchronized (World.getPlayers()) {
					for (Player player : World.getPlayers())
						player.sendNotification(Config.PREFIX + "Congratulations " + getUsername() + " on reaching level-" + level + " in " + Formulae.STAT_ARRAY[stat] + "!");
				}
				// Add the achievement to the event log.
				Logger.log(new eventLog(getUsernameHash(), getAccount(), getIP(), DataConversions.getTimeStamp(), "<strong>" + getUsername() + "</strong>" + " has reached level-" + level + " in " + Formulae.STAT_ARRAY[stat] + "!"));
			}
			if(level == 99)
				getActionSender().sendTakeScreenshot();*/
			int combat = Formulae.getCombatlevel(maxStat);
			if (combat != combatLevel)
				setCombatLevel(combat);
		}
	}
	
	public void setExp(int id, int lvl) {
		if (lvl < 0)
			lvl = 0;
		exp[id] = lvl;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Player) {
			Player p = (Player)o;
			return usernameHash == p.getUsernameHash();
		}
		return false;
	}
	
	public void setKilledFish(boolean killedFish) {
		this.killedFish = killedFish;
	}
	
	public boolean killedFish() {
		return killedFish;
	}

	private long lastThrownGnomeBall = System.currentTimeMillis();
	
	public boolean canThrowGnomeBall() {
		if (System.currentTimeMillis() - lastThrownGnomeBall > 5000) {
			lastThrownGnomeBall = System.currentTimeMillis();
			return true;
		}
		return false;
	}
	
	public void resetLevers() {
		setLeverA(false);
		setLeverB(false);
		setLeverC(false);
		setLeverD(false);
		setLeverE(false);
		setLeverF(false);
	}

	private int birdSeeds = 0;
	
	public void setSeedsUsed(int seeds)
	{
		this.birdSeeds = seeds;
	}

	public int getSeedsUsed() {
		return birdSeeds;
	}	
	
	private int bucketsUsedOnSoil = 0;
	
	public void setBucketsUsedOnSoil(int buckets) {
		this.bucketsUsedOnSoil = buckets;
	}
	
	public int getBucketsUsedOnSoil() {
		return bucketsUsedOnSoil;
	}

	public boolean isQuestStarted(int questID) {
		Quest q = this.getQuest(questID);
		if (q != null)
			return true;
		return false;
	}
	
	public boolean isQuestFinished(int questID) {
		Quest q = this.getQuest(questID);
		if (q != null) {
			if (q.finished())
				return true;
		}
		return false;
	}
	
	public boolean isOnQuestStage(int questID, int questStage) {
		Quest q = this.getQuest(questID);
		if (q != null) {
			if (q.getStage() == questStage)
				return true;
		}
		return false;
	}

	public void resetStats() {
		for (int i = 0; i < Formulae.STAT_ARRAY.length; i++)
			curStat[i] = maxStat[i];
	}
	
	public boolean canWarp() {
		return canWarp + (isSub ? 30000 : 60000) > System.currentTimeMillis();
	}
	
	private void setWarp() {
		canWarp = System.currentTimeMillis();
	}
	
	public MiscPacketBuilder getActionSender()
	{
		return actionSender;
	}
	
	/**
	 * actionSender "Short-Cuts"
	 */
	
	public void sendMessage(String message) {
		actionSender.sendMessage(message);
	}
	
	public void sendGlobalMessage(long usernameHash, int rank, String message) {
		actionSender.sendGlobalMessage(usernameHash, rank, message);
	}	
	
	public void sendNotification(String notification) {
		actionSender.sendNotification(notification);
	}	
	
	public void sendAlert(String message, boolean big) {
		actionSender.sendAlert(message, big);
	}	
	
	public void sendQuestStarted(int questID) {
		actionSender.sendQuestStarted(questID);
	}
	
	public void sendCompletedQuest(int questID) {
		actionSender.sendQuestFinished(questID);
	}
	
	public void sendFriendUpdate(long usernameHash, byte online) {
		actionSender.sendFriendUpdate(usernameHash, online);
	}
	
	public void sendMenu(String[] options) {
		actionSender.sendMenu(options);
	}	
	
	public void sendQuestPointUpdate() {
		actionSender.sendQuestUpdate();
	}	
	
	public void sendDMMessage(String message) {
		actionSender.sendDMMessage(message);
	}	
	
	public void sendInventory() {
		actionSender.sendInventory();
	}
	
	public void sendFatigue() {
		actionSender.sendFatigue();
	}
	
	public void sendTemporaryFatigue() {
		actionSender.sendTemporaryFatigue();
	}
	
	public void sendWorldInfo() {
		actionSender.sendWorldInfo();
	}
	
	public void sendPrayers() {
		actionSender.sendPrayers();
	}	
	
	public void sendEquipmentStats() {
		actionSender.sendEquipmentStats();
	}
	
	public void hideShop() {
		actionSender.hideShop();
	}
	
	public void sendDied() {
		actionSender.sendDied();
	}	
	
	public void sendDeaths() {
		actionSender.sendDeaths();
	}

	public void sendTradeWindowClose() {
		actionSender.sendTradeClose();
	}	
	
	public void sendDuelWindowClose() {
		actionSender.sendDuelClose();
	}	
	
	public void sendDMWindowClose() {
		actionSender.sendDMClose();
	}	
	
	public void sendStat(int stat) {
		actionSender.sendStat(stat);
	}
	
	public void sendStats() {
		actionSender.sendStats();
	}	

	public void showShop(Shop shop) {
		actionSender.showShop(shop);
	}
	
	public void sendLogout() {
		actionSender.sendLogout();
	}		
	
	public void hideBank() {
		actionSender.hideBank();
	}	
	
	public void hideMenu() {
		actionSender.hideMenu();
	}	
	
	public void sendLoginInformation() {
		actionSender.sendLoginInformation();
	}
	
	public void sendTradeItems() {
		actionSender.sendTradeItems();
	}
	
	public void sendDuelItems() {
		actionSender.sendDuelItems();
	}
	
	public void showBank() {
		actionSender.showBank();
	}
	
	public void sendDuelSettingUpdate() {
		actionSender.sendDuelSettingUpdate();
	}
	
	public void updateGroupID(int newID) {
		actionSender.updateGroupID((byte)newID);
	}
	
	public void sendAppearanceScreen() {
		actionSender.sendAppearanceScreen();
	}
	
	public void sendOnlineCount() {
		actionSender.sendOnlineCount();
	}	
	
	public void sendTeleBubble(int x, int y, boolean grab) {
		actionSender.sendTeleBubble(x, y, grab);
	}
	
	public void sendSound(String soundName, boolean mp3) {
		actionSender.sendSound(soundName, mp3);
	}
	
	public void sendScreenshot() {
		actionSender.sendScreenshot();
	}
	
	public void sendKills() {
		actionSender.sendKills();
	}
	
	public void startShutdown(int seconds) {
		actionSender.startShutdown(seconds);
	}
	
	public void updateBankItem(int slot, int newID, long amount) {
		actionSender.updateBankItem(slot, newID, amount);
	}
	
	public void sendSuccess() {
		actionSender.sendSleepSuccess();
	}
	
	public void sendFailure() {
		actionSender.sendSleepFailure();
	}
	
	public void requestLocalhost(long requestee) {
		actionSender.requestLocalhost(requestee);
	}
	
	public void startWildernessUpdate(int seconds, byte type) {
		actionSender.startWildernessUpdate(seconds, type);
	}
	
	public void sendDMWindowOpen() {
		actionSender.sendDMOpen();
	}
	
	public void sendDMAcceptUpdate() {
		actionSender.sendDMAcceptUpdate();
	}
	
	public void sendDMSettingUpdate() {
		actionSender.sendDMSettingUpdate();
	}
	
	public void sendDMAccept() {
		actionSender.sendDMAccept();
	}
	
	public void sendDuelAcceptUpdate() {
		actionSender.sendDuelAcceptUpdate();
	}
	
	public void sendDuelAccept() {
		actionSender.sendDuelAccept();
	}
	
	public void sendDuelWindowOpen() {
		actionSender.sendDuelOpen();
	}
	
	public void sendPM(long user, byte[] message, int rank, boolean sent) {
		actionSender.sendPM(user, message, rank, sent);
	}

	public void sendUpdateItem(int slot) {
		actionSender.sendUpdateItem(slot);
	}
	
	public void sendCantLogout() {
		actionSender.sendCantLogout();
	}
	
	public void sendTradeWindowOpen() {
		actionSender.sendTradeOpen();
	}
	
	public void sendTradeAcceptUpdate() {
		actionSender.sendTradeAcceptUpdate();
	}
	
	public void sendTradeAccept() {
		actionSender.sendTradeAccept();
	}
    
    public void sendConfiguration(HashMap<String, String> config) {
        actionSender.sendConfiguration(config);
    }
    
	public String getStaffName() {
        return Group.getStaffPrefix(this.getGroupID()) + getUsername();
	}
	
	public void sendAlert(String alert) {
		sendAlert(alert, false);
	}
    
    public void sendGraciousAlert(final String alert) {
        sendGraciousAlert(alert, false);
    }
	
	public void sendGraciousAlert(final String alert, boolean big) {
		if (/*getLocation().inWilderness() ||*/ isFighting() || isDueling || isDMing || isTrading || accessingBank() || accessingShop()) {
			World.getDelayedEventHandler().add(new SingleEvent(null, 1000) {
				public void action() {
					sendGraciousAlert(alert);
				}
			});
		} else
			sendAlert(alert, big);		
	}

	/*
	 * (non-Javadoc)
	 * @see org.openrsc.server.core.Watcher#watchItemBubble(int, int)
	 */
	public void watchItemBubble(int playerIndex, int itemID)
	{
		// TODO: Remove the first field (hard-coded 1) from the client!
		session.write(new RSCPacketBuilder(54).addShort(1).addShort(playerIndex).addShort(itemID).toPacket());
	}
	
	private final static int MOB_MESSAGE_ID = 89;

	public void watchChatMessage(Mob sender, Mob recipient,
			String message)
	{
		if(sender instanceof Player)
		{
			session.write(new RSCPacketBuilder(MOB_MESSAGE_ID).addByte((byte)((0 + ((sender == this || recipient == this || recipient == null ? 0x10 : 0))))).addShort(sender.getIndex()).addBytes(message.getBytes()).toPacket());
		}
		else if(sender instanceof Npc)
		{
			session.write(new RSCPacketBuilder(MOB_MESSAGE_ID).addByte((byte)((1 + ((recipient == null || recipient == this ? 0x10 : 0))))).addShort(sender.getIndex()).addBytes(message.getBytes()).toPacket());
		}
	}
	
	@Override
	public int compareTo(Player o) {
		if (usernameHash == o.usernameHash)
			return 0;
		return -1;
	}
	
	private boolean sentScript;
	
	public boolean hasSentScript()
	{
		return sentScript;
	}
	
	public void setSentScript(boolean sentScript)
	{
		this.sentScript = sentScript;
	}

	public void onGeneralPurposeMessageReceived(String[] strings)
	{
		for(String message : strings)
		{
			sendMessage("@gre@"+ Config.getServerName() +" Security: @whi@" + message);
		}
	}

	public boolean isInAuctionHouse() {
		return inAuctionHouse;
	}

	public void setInAuctionHouse(boolean inAuctionHouse) {
		this.inAuctionHouse = inAuctionHouse;
	}

	public long getDeathTime() {
		return deathTime;
	}

	public void setDeathTime(long deathTime) {
		this.deathTime = deathTime;
	}

	public long getLogoutDate() {
		return logoutDate;
	}

	public void setLogoutDate(long logoutDate) {
		this.logoutDate = logoutDate;
	}
}