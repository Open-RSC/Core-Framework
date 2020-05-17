package com.openrsc.server.model.entity.npc;

import com.openrsc.server.constants.*;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.custom.NpcLootEvent;
import com.openrsc.server.event.rsc.ImmediateEvent;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.external.ItemDropDef;
import com.openrsc.server.external.NPCDef;
import com.openrsc.server.external.NPCLoc;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.GoldDrops;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Npc extends Mob {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private long healTimer = 0;
	private boolean shouldRespawn = true;
	private boolean isRespawning = false;
	private boolean executedAggroScript = false;
	private NpcBehavior npcBehavior;
	private ArrayList<NpcLootEvent> deathListeners = new ArrayList<NpcLootEvent>(1); // TODO: Should use a more generic class. Maybe PlayerKilledNpcListener, but that is in plugins jar.
	private static int[] removeHandledInPlugin = {
		NpcId.RAT_TUTORIAL.id(),
		NpcId.DELRITH.id(),
		NpcId.COUNT_DRAYNOR.id(),
		NpcId.CHRONOZON.id(),
		NpcId.SIR_MORDRED.id(),
		NpcId.LUCIEN_EDGE.id(),
		NpcId.BLACK_KNIGHT_TITAN.id()
	};

	/**
	 * The definition of this npc
	 */
	protected NPCDef def;
	/**
	 * The location of this npc
	 */
	private NPCLoc loc;

	/**
	 * Holds players that did damage with combat
	 */
	private Map<String, Integer> combatDamagers = new HashMap<String, Integer>();
	/**
	 * Holds players that did damage with mage
	 */
	private Map<String, Integer> mageDamagers = new HashMap<String, Integer>();
	/**
	 * Holds players that did damage with range
	 */
	private Map<String, Integer> rangeDamagers = new HashMap<String, Integer>();

	public Npc(final World world, final int id, final int x, final int y) {
		this(world, new NPCLoc(id, x, y, x - 5, x + 5, y - 5, y + 5));
	}

	public Npc(final World world, final int id, final int x, final int y, final int radius) {
		this(world, new NPCLoc(id, x, y, x - radius, x + radius, y - radius, y + radius));
	}

	public Npc(final World world, final int id, final int startX, final int startY, final int minX, final int maxX, final int minY, final int maxY) {
		this(world, new NPCLoc(id, startX, startY, minX, maxX, minY, maxY));
	}

	public Npc(final World world, final NPCLoc loc) {
		super(world);

		for (int i : Constants.UNDEAD_NPCS) {
			if (loc.getId() == i) {
				setAttribute("isUndead", true);
			}
		}
		for (int i : Constants.ARMOR_NPCS) {
			if (loc.getId() == i) {
				setAttribute("hasArmor", true);
			}
		}
		def = getWorld().getServer().getEntityHandler().getNpcDef(loc.getId());
		if (def == null) {
			throw new NullPointerException("NPC definition is invalid for NPC ID: " + loc.getId() + ", coordinates: " + "("
				+ loc.startX() + ", " + loc.startY() + ")");
		}
		this.loc = loc;
		this.setNpcBehavior(new NpcBehavior(this));
		super.setID(loc.getId());
		super.setLocation(Point.location(loc.startX(), loc.startY()), true);

		getSkills().setLevelTo(Skills.ATTACK, def.getAtt());
		getSkills().setLevelTo(Skills.DEFENSE, def.getDef());
		getSkills().setLevelTo(Skills.RANGED, def.getRanged());
		getSkills().setLevelTo(Skills.STRENGTH, def.getStr());
		getSkills().setLevelTo(Skills.HITS, def.getHits());

		/*
		  Unique ID for event tracking.
		 */
		setUUID(UUID.randomUUID().toString());

		getWorld().getServer().getGameEventHandler().add(getStatRestorationEvent());
	}

	/**
	 * Adds combat damage done by a player
	 *
	 * @param mob mob dealing damage
	 * @param damage current attack's damage
	 */
	public void addCombatDamage(final Player mob, final int damage) {
		if (combatDamagers.containsKey(mob.getUUID())) {
			combatDamagers.put(mob.getUUID(), combatDamagers.get(mob.getUUID()) + damage);
		} else {
			combatDamagers.put(mob.getUUID(), damage);
		}
	}

	/**
	 * Adds mage damage done by a player
	 *
	 * @param mob mob dealing damage
	 * @param damage current attack's damage
	 */
	public void addMageDamage(final Mob mob, final int damage) {
		if (mageDamagers.containsKey(mob.getUUID())) {
			mageDamagers.put(mob.getUUID(), mageDamagers.get(mob.getUUID()) + damage);
		} else {
			mageDamagers.put(mob.getUUID(), damage);
		}
	}

	/**
	 * Adds range damage done by a player
	 *
	 * @param mob mob dealing damage
	 * @param damage current attack's damage
	 */
	public void addRangeDamage(final Mob mob, final int damage) {
		if (rangeDamagers.containsKey(mob.getUUID())) {
			rangeDamagers.put(mob.getUUID(), rangeDamagers.get(mob.getUUID()) + damage);
		} else {
			rangeDamagers.put(mob.getUUID(), damage);
		}
	}

	public void displayNpcTeleportBubble(final int x, final int y) {
		for (Object o : getViewArea().getPlayersInView()) {
			Player player = ((Player) o);
			ActionSender.sendTeleBubble(player, x, y, false);
		}
		setTeleporting(true);
	}

	public int getNPCCombatLevel() {
		return getDef().combatLevel;
	}

	/**
	 * Combat damage done by Mob ID
	 *
	 * @param ID uuid of mob
	 * @return int
	 */
	private int getCombatDamageDoneBy(final String ID) {
		if (!combatDamagers.containsKey(ID)) {
			return 0;
		}
		int dmgDone = combatDamagers.get(ID);
		return Math.min(dmgDone, this.getDef().getHits());
	}

	/**
	 * Iterates over combatDamagers map and returns the keys
	 *
	 * @return ArrayList<String>
	 */
	private ArrayList<String> getCombatDamagers() {
		return new ArrayList<String>(combatDamagers.keySet());
	}

	public int getCombatStyle() {
		return 0;
	}

	public NPCDef getDef() {
		return getWorld().getServer().getEntityHandler().getNpcDef(getID());
	}

	public NPCLoc getLoc() {
		return loc;
	}

	/**
	 * Mage damage done by Mob ID
	 *
	 * @param ID uuid of mob
	 * @return int
	 */
	private int getMageDamageDoneBy(final String ID) {
		if (!mageDamagers.containsKey(ID)) {
			return 0;
		}
		int dmgDone = mageDamagers.get(ID);
		return Math.min(dmgDone, this.getDef().getHits());
	}

	/**
	 * Iterates over mageDamagers map and returns the keys
	 *
	 * @return ArrayList<String>
	 */
	private ArrayList<String> getMageDamagers() {
		return new ArrayList<String>(mageDamagers.keySet());
	}

	/**
	 * Range damage done by Mob ID
	 *
	 * @param ID uuid of mob
	 * @return int
	 */
	private int getRangeDamageDoneBy(final String ID) {
		if (!rangeDamagers.containsKey(ID)) {
			return 0;
		}
		int dmgDone = rangeDamagers.get(ID);
		return Math.min(dmgDone, this.getDef().getHits());
	}

	/**
	 * Iterates over rangeDamagers map and returns the keys
	 *
	 * @return ArrayList<String>
	 */
	private ArrayList<String> getRangeDamagers() {
		return new ArrayList<String>(rangeDamagers.keySet());
	}

	public int getArmourPoints() {
		return 0;
	}

	public int getWeaponAimPoints() {
		return 0;
	}

	public int getWeaponPowerPoints() {
		return 0;
	}

	public boolean stateIsInvisible() { return false; }
	public boolean stateIsInvulnerable() { return false; }

	@Override
	public void killedBy(Mob mob) {
		Player owner = getWorld().getPlayerUUID(mob.getUUID());
		if (owner == null) {
			Npc npcKiller = getWorld().getNpcByUUID(mob.getUUID());
			if (npcKiller != null && npcKiller.relatedMob instanceof Player)
				// owner is Npc with a related Player
				owner = (Player) npcKiller.relatedMob;
		}

		if (owner == null) return;

		owner.getWorld().getServer().getPluginHandler().handlePlugin(owner, "KillNpc", new Object[]{owner, this});
		for (int npcId : removeHandledInPlugin) {
			if (this.getID() == npcId) return;
		}

		String ownerId = handleXpDistribution(mob);
		owner = getWorld().getPlayerUUID(ownerId);

		// Remove poison event(s)
		this.cure();

		ActionSender.sendSound(owner, "victory");
		owner.getWorld().getServer().getAchievementSystem().checkAndIncSlayNpcTasks(owner, this);
		owner.incnpc_kills();
		ActionSender.sendnpc_kills(owner);

		//If NPC kill messages are enabled and the filter is enabled and the NPC is in the list of NPCs, display the messages,
		//otherwise we will display the message for all NPCs if NPC kill messages are enabled if there is no filter.
		//Also, if we don't have NPC kill logging enabled, we can't have NPC kill messages.
		if (getWorld().getServer().getConfig().NPC_KILL_LOGGING) {
			logNpcKill(owner);
		}

		// Custom KDB Specific Rare Drop Table (RDT)
		if (getWorld().getServer().getConfig().WANT_CUSTOM_SPRITES) {
			if (this.getID() == NpcId.KING_BLACK_DRAGON.id()) {
				calculateCustomKingBlackDragonDrop(owner);
			}
		}

		// Custom Rare Drop Table (RDT)
		boolean rdtHit = false;
		if (getWorld().getServer().getConfig().WANT_NEW_RARE_DROP_TABLES) {
			rdtHit = rollForCustomRareItem(owner);
		}

		ItemDropDef[] drops = def.getDrops();
		if (drops == null) {
			// Some enemies have no drops
			deathListeners.clear();
			remove();
			return;
		}

		// Drops that always occur on every kill
		int total = 0;
		int weightTotal = 0;
		for (ItemDropDef drop : drops) {
			total = weightTotal = total + drop.getWeight();
			if (drop.getWeight() == 0 && drop.getID() != ItemId.NOTHING.id()) {

				// If Ring of Avarice (custom) is equipped, and the item is a stack,
				// we will award the item with slightly different logic.
				if (handleRingOfAvarice(owner, new Item(drop.getID(), drop.getAmount()))) continue;

				// Otherwise, create a normal GroundItem.
				GroundItem groundItem = new GroundItem(owner.getWorld(), drop.getID(), getX(), getY(), drop.getAmount(), owner);
				groundItem.setAttribute("npcdrop", true);
				getWorld().registerItem(groundItem);
			}

		}

		if (!rdtHit) {
			int hit = DataConversions.random(0, total);
			total = 0;

			// Loop on the drops from the Mob
			for (ItemDropDef drop : drops) {
				if (drop.getID() == com.openrsc.server.constants.ItemId.UNHOLY_SYMBOL_MOULD.id()) {
					if (!wantUnholySymbols(owner)) {
						continue;
					}
				}

				int dropID = drop.getID();
				int amount = drop.getAmount();
				int weight = drop.getWeight();

				// hit (% chance) is found by taking the weight of the current item
				// and dividing it by the total weight of all drops from this mob.
				if (hit >= total && hit < (total + weight)) {
					if (dropID != ItemId.NOTHING.id()) {
						if (getWorld().getServer().getEntityHandler().getItemDef(dropID).isMembersOnly()
							&& !getWorld().getServer().getConfig().MEMBER_WORLD) {
							continue; // Members only item on a free world
						}

						if (getWorld().getServer().getEntityHandler().getItemDef(dropID).isStackable()) {
							dropStackItem(dropID, amount, owner);
						} else {
							dropStandardItem(dropID, amount, owner);
						}

						if (getWorld().getServer().getConfig().VALUABLE_DROP_MESSAGES) {
							checkValuableDrop(dropID, amount, weight, weightTotal, owner);
						}
					}
					break;
				}
				total += weight;
			}
		}

		for (NpcLootEvent e : deathListeners) {
			e.onLootNpcDeath((Player) mob, this);
		}

		deathListeners.clear();
		remove();
	}

	private void logNpcKill(Player owner) {
		if (owner.getCache().hasKey("show_npc_kc") && owner.getCache().getBoolean("show_npc_kc")
			&& getWorld().getServer().getConfig().NPC_KILL_MESSAGES) {
			owner.addNpcKill(this,!getWorld().getServer().getConfig().NPC_KILL_MESSAGES_FILTER
				|| getWorld().getServer().getConfig().NPC_KILL_MESSAGES_NPCs.contains(this.getDef().getName()));
		} else
			owner.addNpcKill(this, false);
	}

	private void calculateCustomKingBlackDragonDrop(Player owner) {
		boolean ringOfWealth = owner.getCarriedItems().getEquipment().hasEquipped(ItemId.RING_OF_WEALTH.id());
		if (getWorld().kbdTable.rollAccess(this.getID(), ringOfWealth)) {
			Item kbdSpecificLoot = getWorld().kbdTable.rollItem(ringOfWealth, owner);
			if (kbdSpecificLoot != null) {
				GroundItem groundItem = new GroundItem(getWorld(), kbdSpecificLoot.getCatalogId(), getX(), getY(), kbdSpecificLoot.getAmount(), owner);
				groundItem.setAttribute("npcdrop", true);
				getWorld().registerItem(groundItem);
				try {

					getWorld().getServer().getDatabase().addDropLog(
						owner, this, kbdSpecificLoot.getCatalogId(), kbdSpecificLoot.getAmount());
				} catch (final GameDatabaseException ex) {
					LOGGER.catching(ex);
				}
				if (kbdSpecificLoot.getCatalogId() == ItemId.DRAGON_2_HANDED_SWORD.id())
					owner.message("Congratulations! You have received a dragon 2-Handed Sword!");
			}
		}
	}

	private boolean rollForCustomRareItem(Player owner) {
		boolean ringOfWealth = owner.getCarriedItems().getEquipment().hasEquipped(ItemId.RING_OF_WEALTH.id());

		Item rare = null;
		if (getWorld().standardTable.rollAccess(this.getID(), ringOfWealth)) {
			rare = getWorld().standardTable.rollItem(ringOfWealth, owner);
		} else if (getWorld().gemTable.rollAccess(this.getID(), ringOfWealth)) {
			rare = getWorld().gemTable.rollItem(ringOfWealth, owner);
		}

		if (rare != null) {
			if (!handleRingOfAvarice(owner, rare)) {
				GroundItem groundItem = new GroundItem(owner.getWorld(), rare.getCatalogId(), getX(), getY(), rare.getAmount(), owner, rare.getNoted());
				groundItem.setAttribute("npcdrop", true);
				getWorld().registerItem(groundItem);
			}

			try {
				getWorld().getServer().getDatabase().addDropLog(
					owner, this, rare.getCatalogId(), rare.getAmount());
			} catch (final GameDatabaseException ex) {
				LOGGER.catching(ex);
			}
			return true;
		}
		return false;
	}

	private boolean wantUnholySymbols(Player owner) {
		if (owner.getQuestStage(Quests.OBSERVATORY_QUEST) > -1)
			return false; // Quest started.

		if (owner.getConfig().WANT_CUSTOM_QUESTS)
			if (owner.getCache().hasKey("want_unholy_symbol_drops") &&
				!owner.getCache().getBoolean("want_unholy_symbol_drops"))
				return false; //
		return true;
	}

	private void dropStackItem(final int dropID, int amount, Player owner) {
		// Gold Drops
		if (dropID == com.openrsc.server.constants.ItemId.COINS.id()) {
			amount = Formulae.calculateGoldDrop(
				GoldDrops.drops.getOrDefault(this.getID(), new int[]{1})
			);
			if (owner.getCarriedItems().getEquipment().hasEquipped(ItemId.RING_OF_SPLENDOR.id())) {
				amount += Formulae.getSplendorBoost(amount);
				owner.message("Your ring of splendor shines brightly!");
			}
		}

		try {
			getWorld().getServer().getDatabase().addDropLog(owner, this, dropID, amount);
		} catch (final GameDatabaseException ex) {
			LOGGER.catching(ex);
		}

		if (!handleRingOfAvarice(owner, new Item(dropID, amount))) {
			GroundItem groundItem = new GroundItem(owner.getWorld(), dropID, getX(), getY(), amount, owner);
			groundItem.setAttribute("npcdrop", true);
			getWorld().registerItem(groundItem);
		}
	}

	private void dropStandardItem(int dropID, int amount, Player owner) {
		try {
			getWorld().getServer().getDatabase().addDropLog(owner, this, dropID, amount);
		} catch (final GameDatabaseException ex) {
			LOGGER.catching(ex);
		}
		GroundItem groundItem;

		// We need to drop multiple counts of "1" item if it's not a stack
		for (int count = 0; count < amount; count++) {

			// Gem Drop Table + 1/128 chance to roll into very rare item
			if (dropID == com.openrsc.server.constants.ItemId.UNCUT_SAPPHIRE.id()) {
				dropID = Formulae.calculateGemDrop(owner);
				amount = 1;
			}

			// Herb Drop Table
			else if (dropID == com.openrsc.server.constants.ItemId.UNIDENTIFIED_GUAM_LEAF.id()) {
				dropID = Formulae.calculateHerbDrop();
			}

			if (dropID != com.openrsc.server.constants.ItemId.NOTHING.id() && getWorld().getServer().getEntityHandler().getItemDef(dropID).isMembersOnly() && !getWorld().getServer().getConfig().MEMBER_WORLD) {
				continue; // Members item on a non-members world.
			} else if (dropID != com.openrsc.server.constants.ItemId.NOTHING.id()) {
				groundItem = new GroundItem(owner.getWorld(), dropID, getX(), getY(), 1, owner);
				groundItem.setAttribute("npcdrop", true);
				getWorld().registerItem(groundItem);
			}
		}
	}

	private void checkValuableDrop(int dropID, int amount, int weight, int weightTotal, Player owner) {
		// Check if we have a "valuable drop" (configurable)
		Item temp = new Item(dropID);
		double currentRatio = (double) weight / (double) weightTotal;
		if (dropID != com.openrsc.server.constants.ItemId.NOTHING.id() &&
			amount > 0 &&
			(
				currentRatio > getWorld().getServer().getConfig().VALUABLE_DROP_RATIO ||
					(
						getWorld().getServer().getConfig().VALUABLE_DROP_EXTRAS &&
							getWorld().getServer().getConfig().valuableDrops.contains(temp.getDef(getWorld()).getName())
					)
			)
		) {
			if (amount > 1) {
				owner.message("@red@Valuable drop: " + amount + " x " + temp.getDef(getWorld()).getName() + " (" +
					(temp.getDef(getWorld()).getDefaultPrice() * amount) + " coins)");
			} else {
				owner.message("@red@Valuable drop: " + temp.getDef(getWorld()).getName() + " (" +
					(temp.getDef(getWorld()).getDefaultPrice()) + " coins)");
			}
		}
	}

	/**
	 * Distributes the XP from this monster and the loot
	 *
	 * @param attacker the person that "finished off" the npc
	 * @return the player who did the most damage / should get the loot
	 */
	private String handleXpDistribution(final Mob attacker) {
		final int totalCombatXP = Formulae.combatExperience(this);
		String UUIDWithMostDamage = attacker.getUUID();
		int currentHighestDamage = 0;

		// Melee damagers
		for (String ID : getCombatDamagers()) {
			final int damageDoneByPlayer = getCombatDamageDoneBy(ID);

			if (damageDoneByPlayer > currentHighestDamage) {
				UUIDWithMostDamage = ID;
				currentHighestDamage = damageDoneByPlayer;
			}

			Player player = getWorld().getPlayerUUID(ID);
			if (player != null) {
				int skillsDist[] = {0, 0, 0, 0};
				// Give the player their share of the experience.
				int totalXP = (int) (((double) (totalCombatXP) / (double) (getDef().hits)) * (double) (damageDoneByPlayer));
				switch (player.getCombatStyle()) {
					case Skills.CONTROLLED_MODE: // CONTROLLED
						for (int x = 0; x < 3; x++) {
							skillsDist[x] = 1;
						}
						break;
					case Skills.AGGRESSIVE_MODE: // AGGRESSIVE
						skillsDist[Skills.STRENGTH] = 3;
						break;
					case Skills.ACCURATE_MODE: // ACCURATE
						skillsDist[Skills.ATTACK] = 3;
						break;
					case Skills.DEFENSIVE_MODE: // DEFENSIVE
						skillsDist[Skills.DEFENSE] = 3;
						break;
				}
				skillsDist[Skills.HITS] = 1;
				player.incExp(skillsDist, totalXP, true);
			}
		}

		// Ranged damagers
		for (String ID : getRangeDamagers()) {
			int damageDoneByPlayer = getRangeDamageDoneBy(ID);
			if (damageDoneByPlayer > currentHighestDamage) {
				UUIDWithMostDamage = ID;
				currentHighestDamage = damageDoneByPlayer;
			}

			Player player = getWorld().getPlayerUUID(ID);
			if (player != null) {
				int totalXP = (int) (((double) (totalCombatXP) / (double) (getDef().hits)) * (double) (damageDoneByPlayer));
				player.incExp(Skills.RANGED, totalXP * 4, true);
				ActionSender.sendStat(player, Skills.RANGED);
			}
		}

		// Magic damagers
		for (String ID : getMageDamagers()) {
			int dmgDoneByPlayer = getMageDamageDoneBy(ID);

			if (dmgDoneByPlayer > currentHighestDamage) {
				UUIDWithMostDamage = ID;
				currentHighestDamage = dmgDoneByPlayer;
			}
		}
		return UUIDWithMostDamage;
	}

	public void initializeTalkScript(final Player player) {
		final Npc npc = this;
		//p.setBusyTimer(600);
		getWorld().getServer().getGameEventHandler().add(new ImmediateEvent(getWorld(), "Init Talk Script") {
			@Override
			public void action() {
				getWorld().getServer().getPluginHandler().handlePlugin(player, "TalkNpc", new Object[]{player, npc});
			}
		});
	}

	public void remove() {
		double respawnMult = getWorld().getServer().getConfig().NPC_RESPAWN_MULTIPLIER;

		this.setLastOpponent(null);
		if (getCombatEvent() != null) {
			getCombatEvent().resetCombat();
		}
		if (!isRemoved() && shouldRespawn && def.respawnTime() > 0) {
			super.remove();
			startRespawning();
			getWorld().removeNpcPosition(this);
			Npc n = this;
			teleport(loc.startX, loc.startY);
			setRespawning(true);
			getWorld().getServer().getGameEventHandler().add(new DelayedEvent(getWorld(), null, (long)(def.respawnTime() * respawnMult * 1000), "Respawn NPC", false) {
				public void run() {
					n.setRemoved(false);
					n.getRegion().addEntity(n);

					// Take 4 ticks away from the current time to get a 1 tick pause while the npc spawns,
					// before it is allowed to attack (if aggressive).
					setCombatTimer(-getWorld().getServer().getConfig().GAME_TICK * 4);
					setRespawning(false);
					getSkills().normalize();
					tryResyncHitEvent();

					running = false;
					mageDamagers.clear();
					rangeDamagers.clear();
					combatDamagers.clear();

					teleport(loc.startX, loc.startY);
					getWorld().setNpcPosition(n);
				}
			});
		} else if (!shouldRespawn) {
			setUnregistering(true);
		}
	}

	private void startRespawning() {

	}

	public void setShouldRespawn(final boolean respawn) {
		shouldRespawn = respawn;
	}

	public boolean shouldRespawn() {
		return shouldRespawn;
	}

	public void teleport(final int x, final int y) {
		setLocation(Point.location(x, y), true);
	}

	@Override
	public String toString() {
		return "[NPC:" + getIndex() + ":" + getDef().getName() + " @ (" + getX() + ", " + getY() + ")]";
	}

	public void updatePosition() {
		getNpcBehavior().tick();
		super.updatePosition();
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

	public boolean isChasing() {
		return getNpcBehavior().isChasing();
	}

	public void setChasing(final Player player) {
		getNpcBehavior().setChasing(player);
	}

	public void setChasing(final Npc npc) {
		getNpcBehavior().setChasing(npc);
	}

	public Player getChasedPlayer() {
		return getNpcBehavior().getChasedPlayer();
	}

	public Npc getChasedNpc() {
		return getNpcBehavior().getChasedNpc();
	}

	public NpcBehavior getBehavior() {
		return getNpcBehavior();
	}

	public void setBehavior(final NpcBehavior behavior) {
		this.setNpcBehavior(behavior);
	}

	public void setNPCLoc(final NPCLoc loc2) {
		this.loc = loc2;
	}

	public boolean isPlayer() {
		return false;
	}

	public boolean isNpc() {
		return true;
	}

	public boolean isRespawning() {
		return isRespawning;
	}

	private void setRespawning(final boolean isRespawning) {
		this.isRespawning = isRespawning;
	}

	public void superRemove() {
		super.remove();
	}

	public boolean addDeathListener(final NpcLootEvent event) {
		return deathListeners.add(event);
	}

	public boolean cantHeal() {
		return healTimer - System.currentTimeMillis() > 0;
	}

	public void setHealTimer(final long l) {
		healTimer = System.currentTimeMillis() + l;
	}

	public void setExecutedAggroScript(final boolean executed) {
		this.executedAggroScript = executed;
	}

	public boolean executedAggroScript() {
		return this.executedAggroScript;
	}

	public static boolean handleRingOfAvarice(final Player player, final Item item) {
		try {
			int slot = -1;
			if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.RING_OF_AVARICE.id())) {
				ItemDefinition itemDef = player.getWorld().getServer().getEntityHandler().getItemDef(item.getCatalogId());
				if (itemDef != null && itemDef.isStackable()) {
					if (player.getCarriedItems().getInventory().hasInInventory(item.getCatalogId())) {
						player.getCarriedItems().getInventory().add(item);
						return true;
					} else if (player.getConfig().WANT_EQUIPMENT_TAB && (slot = player.getCarriedItems().getEquipment().searchEquipmentForItem(item.getCatalogId())) != -1) {
						Item equipped = player.getCarriedItems().getEquipment().get(slot);
						equipped.changeAmount(player.getWorld().getServer().getDatabase(), item.getAmount());
						return true;
					} else {
						if (player.getCarriedItems().getInventory().getFreeSlots() > 0) {
							player.getCarriedItems().getInventory().add(item);
							return true;
						} else {
							player.message("Your ring of Avarice tried to activate, but your inventory was full.");
							return false;
						}
					}
				}
			}
		} catch (GameDatabaseException ex) {
			LOGGER.error(ex.getMessage());
		}
		return false;
	}

	public Point walkablePoint(final Point minP, final Point maxP) {
		final int currX = getX();
		final int currY = getY();
		final int radius = 8;
		final int newX = DataConversions.random(Math.max(minP.getX(), currX - radius), Math.min(maxP.getX(), currX + radius));
		final int newY = DataConversions.random(Math.max(minP.getY(), currY - radius), Math.min(maxP.getY(), currY + radius));
		if (Point.location(newX, newY).inBounds(680, 491, 696, 511)) {
			return Point.location(currX, currY);
		}
		return Point.location(newX, newY);
	}

	public NpcBehavior getNpcBehavior() {
		return npcBehavior;
	}

	public void setNpcBehavior(final NpcBehavior npcBehavior) {
		this.npcBehavior = npcBehavior;
	}

}
