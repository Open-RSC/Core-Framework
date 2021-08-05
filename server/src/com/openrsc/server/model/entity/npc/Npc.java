package com.openrsc.server.model.entity.npc;

import com.openrsc.server.constants.*;
import com.openrsc.server.content.DropTable;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.custom.NpcLootEvent;
import com.openrsc.server.event.rsc.ImmediateEvent;
import com.openrsc.server.external.NPCDef;
import com.openrsc.server.external.NPCLoc;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.EntityType;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
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
	private Map<UUID, Integer> combatDamagers = new HashMap<UUID, Integer>();
	/**
	 * Holds players that did damage with mage
	 */
	private Map<UUID, Integer> mageDamagers = new HashMap<UUID, Integer>();
	/**
	 * Holds players that did damage with range
	 */
	private Map<UUID, Integer> rangeDamagers = new HashMap<UUID, Integer>();

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
		super(world, EntityType.NPC);

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

		getSkills().setLevelTo(Skill.ATTACK.id(), def.getAtt());
		getSkills().setLevelTo(Skill.DEFENSE.id(), def.getDef());
		getSkills().setLevelTo(Skill.RANGED.id(), def.getRanged());
		getSkills().setLevelTo(Skill.STRENGTH.id(), def.getStr());
		getSkills().setLevelTo(Skill.HITS.id(), def.getHits());

		/*
		  Unique ID for event tracking.
		 */
		setUUID(UUID.randomUUID());

		getWorld().getServer().getGameEventHandler().add(getStatRestorationEvent());
	}

	/**
	 * Adds combat damage done by a player
	 *
	 * @param mob    mob dealing damage
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
	 * @param mob    mob dealing damage
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
	 * @param mob    mob dealing damage
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
	private int getCombatDamageDoneBy(final UUID ID) {
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
	private ArrayList<UUID> getCombatDamagers() {
		return new ArrayList<UUID>(combatDamagers.keySet());
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
	private int getMageDamageDoneBy(final UUID ID) {
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
	private ArrayList<UUID> getMageDamagers() {
		return new ArrayList<UUID>(mageDamagers.keySet());
	}

	/**
	 * Range damage done by Mob ID
	 *
	 * @param ID uuid of mob
	 * @return int
	 */
	private int getRangeDamageDoneBy(final UUID ID) {
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
	private ArrayList<UUID> getRangeDamagers() {
		return new ArrayList<UUID>(rangeDamagers.keySet());
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

	public boolean stateIsInvisible() {
		return false;
	}

	public boolean stateIsInvulnerable() {
		return false;
	}

	@Override
	public void killedBy(Mob mob) {
		if (mob == null) {
			this.cure();
			deathListeners.clear();
			return;
		}
		if (this.killed) return;
		//this.killed = true; remove() assures everything went fine, and set killed to true

		Player owner = getWorld().getPlayerByUUID(mob.getUUID());
		if (owner == null) {
			Npc npcKiller = getWorld().getNpcByUUID(mob.getUUID());
			if (npcKiller != null && npcKiller.relatedMob instanceof Player)
				// owner is Npc with a related Player
				owner = (Player) npcKiller.relatedMob;
		}

		// Remove poison event(s)
		this.cure();

		if (owner == null) {
			deathListeners.clear();
			remove();
			return;
		}

		owner.getWorld().getServer().getPluginHandler().handlePlugin(owner, "KillNpc", new Object[]{owner, this});
		for (int npcId : removeHandledInPlugin) {
			if (this.getID() == npcId) {
				if (this.getID() == NpcId.RAT_TUTORIAL.id()) {
					remove();
				}
				return;
			}
		}

		UUID ownerId = handleXpDistribution(mob);
		owner = getWorld().getPlayerByUUID(ownerId);

		if (owner == null) {
			deathListeners.clear();
			remove();
			return;
		}

		ActionSender.sendSound(owner, "victory");
		owner.getWorld().getServer().getAchievementSystem().checkAndIncSlayNpcTasks(owner, this);
		owner.incNpcKills();
		ActionSender.sendNpcKills(owner);

		//If NPC kill messages are enabled and the filter is enabled and the NPC is in the list of NPCs, display the messages,
		//otherwise we will display the message for all NPCs if NPC kill messages are enabled if there is no filter.
		//Also, if we don't have NPC kill logging enabled, we can't have NPC kill messages.
		if (getConfig().NPC_KILL_LOGGING) {
			logNpcKill(owner);
		}

		/** Item Drops **/
		dropItems(owner);

		for (NpcLootEvent e : deathListeners) {
			e.onLootNpcDeath((Player) mob, this);
		}

		deathListeners.clear();
		remove();
	}

	private void logNpcKill(Player owner) {
		if (owner.getCache().hasKey("show_npc_kc") && owner.getCache().getBoolean("show_npc_kc")
			&& getConfig().NPC_KILL_MESSAGES) {
			owner.addNpcKill(this, !getConfig().NPC_KILL_MESSAGES_FILTER
				|| getConfig().NPC_KILL_MESSAGES_NPCs.contains(this.getDef().getName()));
		} else
			owner.addNpcKill(this, false);
	}

	public void dropItems(Player owner) {
		/* 1. Custom Rare Drops */
		if (getConfig().WANT_CUSTOM_SPRITES) {
			if (this.getID() == NpcId.KING_BLACK_DRAGON.id()) {
				calculateCustomKingBlackDragonDrop(owner); // Custom KDB Specific RDT
			}
		}

		/* 2. Drop bones (or nothing). */
		int bones = getBonesDrop();
		if (bones != ItemId.NOTHING.id()) {
			GroundItem groundItem = new GroundItem(
				owner.getWorld(), bones, getX(), getY(), 1, owner
			);
			groundItem.setAttribute("npcdrop", true);
			getWorld().registerItem(groundItem);
		}

		/* 3. Get the rest of the mob's drops. */
		DropTable drops = getWorld().npcDrops.getDropTable(this.getID());
		if (drops == null) {
			// Some enemies have no drops
			deathListeners.clear();
			remove();
			return;
		}
		drops = drops.clone(drops.getDescription());

		/* 4. Drop items that should always drop, that are not bones. */
		ArrayList<Item> invariableItems = drops.invariableItems(owner);
		for (Item item : invariableItems) {
			GroundItem groundItem = new GroundItem(owner.getWorld(), item.getCatalogId(), getX(), getY(), item.getAmount(), owner);
			groundItem.setAttribute("npcdrop", true);
			owner.getWorld().registerItem(groundItem);
		}

		/* 5. Roll for drops. */
		boolean ringOfWealth = false;
		if (getConfig().WANT_NEW_RARE_DROP_TABLES) {
			ringOfWealth = owner.getCarriedItems().getEquipment().hasEquipped(ItemId.RING_OF_WEALTH.id());
		}

		if (drops.getTotalWeight() > 0) {
			ArrayList<Item> items = drops.rollItem(ringOfWealth, owner);
			for (Item item : items) {
				if (item != null) {
					if (getWorld().getServer().getEntityHandler().getItemDef(item.getCatalogId()).isStackable()) {
						dropStackItem(item.getCatalogId(), item.getAmount(), owner);
					} else {
						dropStandardItem(item, owner);
					}
				}
			}
		}
	}

	private void calculateCustomKingBlackDragonDrop(Player owner) {
		boolean ringOfWealth = owner.getCarriedItems().getEquipment().hasEquipped(ItemId.RING_OF_WEALTH.id());
		if (getWorld().getNpcDrops().getKbdTableCustom().rollAccess(this.getID(), ringOfWealth)) {
			ArrayList<Item> kbdSpecificLoot = getWorld().getNpcDrops().getKbdTableCustom().rollItem(ringOfWealth, owner);
			if (kbdSpecificLoot != null) {
				for (Item item : kbdSpecificLoot) {
					GroundItem groundItem = new GroundItem(getWorld(), item.getCatalogId(), getX(), getY(), item.getAmount(), owner);
					groundItem.setAttribute("npcdrop", true);
					getWorld().registerItem(groundItem);
					try {

						getWorld().getServer().getDatabase().addDropLog(
							owner, this, item.getCatalogId(), item.getAmount());
					} catch (final GameDatabaseException ex) {
						LOGGER.catching(ex);
					}
					if (item.getCatalogId() == ItemId.DRAGON_2_HANDED_SWORD.id()) {
						owner.message("Congratulations! You have received a dragon 2-Handed Sword!");
					}
				}
			}
		}
	}

	private int getBonesDrop() {
		int bones = ItemId.NOTHING.id();
		// Big Bones
		if (getWorld().npcDrops.isBigBoned(this.getID())) {
			bones = boneItem(ItemId.BIG_BONES.id());
		}
		// Bat
		else if (getWorld().npcDrops.isBatBoned(this.getID())) {
			bones = boneItem(ItemId.BAT_BONES.id());
		}
		// Dragon
		else if (getWorld().npcDrops.isDragon(this.getID())) {
			bones = boneItem(ItemId.DRAGON_BONES.id());
		}
		// Demon
		else if (getWorld().npcDrops.isDemon(this.getID())) {
			bones = ItemId.ASHES.id();
		}
		// Not boneless
		else if (!getWorld().npcDrops.isBoneless(this.getID())) {
			bones = boneItem(ItemId.BONES.id());
		}
		return bones;
	}

	private int boneItem(int boneId) {
		return getConfig().ONLY_REGULAR_BONES ? ItemId.BONES.id() : boneId;
	}

	private void dropStackItem(final int dropID, int amount, Player owner) {
		// Gold Drops
		if (dropID == ItemId.COINS.id() && owner.getCarriedItems().getEquipment().hasEquipped(ItemId.RING_OF_SPLENDOR.id())) {
			amount += Formulae.getSplendorBoost(amount);
			owner.message("Your ring of splendor shines brightly!");
		}

		try {
			getWorld().getServer().getDatabase().addDropLog(owner, this, dropID, amount);
		} catch (final GameDatabaseException ex) {
			LOGGER.catching(ex);
		}

		if (!DropTable.handleRingOfAvarice(owner, new Item(dropID, amount))) {
			GroundItem groundItem = new GroundItem(owner.getWorld(), dropID, getX(), getY(), amount, owner);
			groundItem.setAttribute("npcdrop", true);
			getWorld().registerItem(groundItem);
		}
	}

	private void dropStandardItem(Item item, Player owner) {
		int dropID = item.getCatalogId();
		int amount = item.getAmount();
		try {
			getWorld().getServer().getDatabase().addDropLog(owner, this, dropID, amount);
		} catch (final GameDatabaseException ex) {
			LOGGER.catching(ex);
		}
		GroundItem groundItem;

		// We need to drop multiple counts of "1" item if it's not a stack
		// But if it's noted, just drop it all.
		int loop = amount;
		if (item.getNoted()) loop = 1;
		else amount = 1;
		for (int count = 0; count < loop; count++) {
			if (dropID != ItemId.NOTHING.id()
				&& getWorld().getServer().getEntityHandler().getItemDef(dropID).isMembersOnly()
				&& !getConfig().MEMBER_WORLD) {
				continue; // Members item on a non-members world.
			} else if (dropID != ItemId.NOTHING.id()) {
				groundItem = new GroundItem(owner.getWorld(), dropID, getX(), getY(), amount, owner, item.getNoted());
				groundItem.setAttribute("npcdrop", true);
				getWorld().registerItem(groundItem);
			}
		}
	}

	/**
	 * Distributes the XP from this monster and the loot
	 *
	 * @param attacker the person that "finished off" the npc
	 * @return the player who did the most damage / should get the loot
	 */
	private UUID handleXpDistribution(final Mob attacker) {
		final int totalCombatXP = Formulae.combatExperience(this);
		UUID UUIDWithMostDamage = attacker.getUUID();
		int currentHighestDamage = 0;

		// Melee damagers
		for (UUID ID : getCombatDamagers()) {
			final int damageDoneByPlayer = getCombatDamageDoneBy(ID);

			if (damageDoneByPlayer > currentHighestDamage) {
				UUIDWithMostDamage = ID;
				currentHighestDamage = damageDoneByPlayer;
			}

			Player player = getWorld().getPlayerByUUID(ID);
			if (player != null) {
				int[] skillsDist = new int[Skill.maxId(Skill.ATTACK.name(), Skill.DEFENSE.name(),
					Skill.STRENGTH.name(), Skill.HITS.name()) + 1];
				// Give the player their share of the experience.
				int totalXP = (int) (((double) (totalCombatXP) / (double) (getDef().hits)) * (double) (damageDoneByPlayer));
				switch (player.getCombatStyle()) {
					case Skills.CONTROLLED_MODE: // CONTROLLED
						for (int skillId : new int[]{Skill.ATTACK.id(), Skill.DEFENSE.id(), Skill.STRENGTH.id()}) {
							skillsDist[skillId] = 1;
						}
						break;
					case Skills.AGGRESSIVE_MODE: // AGGRESSIVE
						skillsDist[Skill.STRENGTH.id()] = 3;
						break;
					case Skills.ACCURATE_MODE: // ACCURATE
						skillsDist[Skill.ATTACK.id()] = 3;
						break;
					case Skills.DEFENSIVE_MODE: // DEFENSIVE
						skillsDist[Skill.DEFENSE.id()] = 3;
						break;
				}
				skillsDist[Skill.HITS.id()] = 1;
				player.incExp(skillsDist, totalXP, true);
			}
		}

		// Ranged damagers
		for (UUID ID : getRangeDamagers()) {
			int damageDoneByPlayer = getRangeDamageDoneBy(ID);
			if (damageDoneByPlayer > currentHighestDamage) {
				UUIDWithMostDamage = ID;
				currentHighestDamage = damageDoneByPlayer;
			}

			Player player = getWorld().getPlayerByUUID(ID);
			if (player != null) {
				int totalXP = (int) (((double) (totalCombatXP) / (double) (getDef().hits)) * (double) (damageDoneByPlayer));
				player.incExp(Skill.RANGED.id(), totalXP * 4, true);
				ActionSender.sendStat(player, Skill.RANGED.id());
			}
		}

		// Magic damagers
		for (UUID ID : getMageDamagers()) {
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
		this.killed = true;
		double respawnMult = getConfig().NPC_RESPAWN_MULTIPLIER;
		resetCombatEvent();
		this.setLastOpponent(null);
		if (!isRemoved() && shouldRespawn && def.respawnTime() > 0) {
			super.remove();
			startRespawning();
			getWorld().removeNpcPosition(this);
			Npc n = this;
			setRespawning(true);
			getWorld().getServer().getGameEventHandler().add(new DelayedEvent(getWorld(), null, (long) (def.respawnTime() * respawnMult * 1000), "Respawn NPC", false) {
				public void run() {
					n.killed = false;
					n.setRemoved(false);
					n.getRegion().addEntity(n);

					// Take 4 ticks away from the current time to get a 1 tick pause while the npc spawns,
					// before it is allowed to attack (if aggressive).
					teleport(loc.startX, loc.startY);
					face(loc.startX, loc.startY - 1);
					setCombatTimer(-getConfig().GAME_TICK * 4);
					setRespawning(false);
					getSkills().normalize();
					tryResyncHitEvent();

					running = false;
					mageDamagers.clear();
					rangeDamagers.clear();
					combatDamagers.clear();


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

	public NpcBehavior getBehavior() {
		return getNpcBehavior();
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
