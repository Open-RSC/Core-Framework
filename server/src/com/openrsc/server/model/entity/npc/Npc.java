package com.openrsc.server.model.entity.npc;

import com.openrsc.server.constants.*;
import com.openrsc.server.content.DropTable;
import com.openrsc.server.content.EnchantedCrowns;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.custom.NpcLootEvent;
import com.openrsc.server.event.rsc.DuplicationStrategy;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.event.rsc.ImmediateEvent;
import com.openrsc.server.external.NPCDef;
import com.openrsc.server.external.NPCLoc;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.EntityType;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.KillType;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MathUtil;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.commons.lang3.tuple.Pair;
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
		NpcId.BLACK_KNIGHT_TITAN.id(),
		NpcId.PETER_SKIPPIN.id()
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
	private Map<UUID, Pair<Integer, Long>> combatDamagers = new HashMap<UUID, Pair<Integer,Long>>();
	/**
	 * Holds players that did damage with mage
	 */
	private Map<UUID, Pair<Integer, Long>> mageDamagers = new HashMap<UUID, Pair<Integer,Long>>();
	/**
	 * Holds players that did damage with range
	 */
	private Map<UUID, Pair<Integer, Long>> rangeDamagers = new HashMap<UUID, Pair<Integer,Long>>();


	/**
	 * Tracking for timing out the multi menu if another player attempts to talk to an NPC locked in dialog
	 */
	private long multiTimeout = -1;

	/**
	 * Another player wants to access the NPC, and can't access it right now.
	 */
	private boolean playerWantsNpc = false;

	private NpcInteraction npcInteraction = null;

	private Player interactingPlayer = null;


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
			combatDamagers.put(mob.getUUID(), Pair.of(combatDamagers.get(mob.getUUID()).getLeft() + damage, mob.getUsernameHash()));
		} else {
			combatDamagers.put(mob.getUUID(), Pair.of(damage, mob.getUsernameHash()));
		}
	}

	/**
	 * Adds mage damage done by a player
	 *
	 * @param mob    mob dealing damage
	 * @param damage current attack's damage
	 */
	public void addMageDamage(final Player mob, final int damage) {
		if (mageDamagers.containsKey(mob.getUUID())) {
			mageDamagers.put(mob.getUUID(), Pair.of(mageDamagers.get(mob.getUUID()).getLeft() + damage, mob.getUsernameHash()));
		} else {
			mageDamagers.put(mob.getUUID(), Pair.of(damage, mob.getUsernameHash()));
		}
	}

	/**
	 * Adds range damage done by a player
	 *
	 * @param mob    mob dealing damage
	 * @param damage current attack's damage
	 */
	public void addRangeDamage(final Player mob, final int damage) {
		if (rangeDamagers.containsKey(mob.getUUID())) {
			rangeDamagers.put(mob.getUUID(), Pair.of(rangeDamagers.get(mob.getUUID()).getLeft() + damage, mob.getUsernameHash()));
		} else {
			rangeDamagers.put(mob.getUUID(), Pair.of(damage, mob.getUsernameHash()));
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
	 * @return Pair
	 */
	private Pair<Integer, Long> getCombatDamageInfoBy(final UUID ID) {
		if (!combatDamagers.containsKey(ID)) {
			return Pair.of(0, 0L);
		}
		int dmgDone = combatDamagers.get(ID).getLeft();
		return Pair.of(Math.min(dmgDone, this.getDef().getHits()), combatDamagers.get(ID).getRight());
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
	 * @return Pair
	 */
	private Pair<Integer, Long> getMageDamageInfoBy(final UUID ID) {
		if (!mageDamagers.containsKey(ID)) {
			return Pair.of(0, 0L);
		}
		int dmgDone = mageDamagers.get(ID).getLeft();
		return Pair.of(Math.min(dmgDone, this.getDef().getHits()), mageDamagers.get(ID).getRight());
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
	 * @return Pair
	 */
	private Pair<Integer, Long> getRangeDamageInfoBy(final UUID ID) {
		if (!rangeDamagers.containsKey(ID)) {
			return Pair.of(0, 0L);
		}
		int dmgDone = rangeDamagers.get(ID).getLeft();
		return Pair.of(Math.min(dmgDone, this.getDef().getHits()), rangeDamagers.get(ID).getRight());
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

		owner.getWorld().getServer().getPluginHandler().handlePlugin(KillNpcTrigger.class, owner, new Object[]{owner, this});
		for (int npcId : removeHandledInPlugin) {
			if (this.getID() == npcId) {
				if (this.getID() == NpcId.RAT_TUTORIAL.id()) {
					remove();
				}
				return;
			}
		}

		// Defense skillcape message
		int totalBlockedDamage = owner.getTrackedBlockedDamage(this);
		if (totalBlockedDamage > 0) {
			owner.playerServerMessage(MessageType.QUEST, "@dcy@Your defense cape blocked " + totalBlockedDamage + " damage!");
		}

		owner.setLastNpcKilledId(this.getID());

		Pair<UUID, Long> ownerInfo = handleXpDistribution(mob);
		owner = getWorld().getPlayerByUUID(ownerInfo.getLeft());

		if (owner == null) {
			Player killOwner = new Player(getWorld(), ownerInfo.getRight());
			/** Item Drops **/
			dropItems(killOwner);
			deathListeners.clear();
			remove();
			return;
		}

		ActionSender.sendSound(owner, "victory");
		owner.getWorld().getServer().getAchievementSystem().checkAndIncSlayNpcTasks(owner, this);
		owner.incNpcKills();

		//If NPC kill messages are enabled and the filter is enabled and the NPC is in the list of NPCs, display the messages,
		//otherwise we will display the message for all NPCs if NPC kill messages are enabled if there is no filter.
		//Also, if we don't have NPC kill logging enabled, we can't have NPC kill messages.
		if (getConfig().NPC_KILL_LOGGING) {
			logNpcKill(owner);
		}

		ActionSender.sendNpcKills(owner);

		/** Item Drops **/
		dropItems(owner);

		for (NpcLootEvent e : deathListeners) {
			e.onLootNpcDeath((Player) mob, this);
		}

		deathListeners.clear();
		remove();
	}

	private void logNpcKill(Player owner) {
		if (owner.getCache().hasKey("npc_kc_messages") && (owner.getCache().getBoolean("npc_kc_messages"))
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
			boolean destroyBones = false;
			if (EnchantedCrowns.shouldActivate(owner, ItemId.CROWN_OF_THE_OCCULT)) {
				int conf = owner.getCache().hasKey("bone_conf") ? owner.getCache().getInt("bone_conf") : 7;
				int boneTier = getBoneTier(bones);
				destroyBones = MathUtil.isKthBitSet(conf, boneTier + 1);
			}

			if (!destroyBones) {
				GroundItem groundItem = new GroundItem(
					owner.getWorld(), bones, getX(), getY(), 1, owner
				);
				groundItem.setAttribute("npcdrop", true);
				getWorld().registerItem(groundItem);
			} else {
				EnchantedCrowns.giveBonesExperience(owner, new Item(bones));
				owner.playerServerMessage(MessageType.QUEST, "Your crown shines and the bone gets destroyed");
				EnchantedCrowns.useCharge(owner, ItemId.CROWN_OF_THE_OCCULT);
			}
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
					if ((getWorld().getServer().getConfig().RESTRICT_ITEM_ID >= 0 && item.getCatalogId() > getWorld().getServer().getConfig().RESTRICT_ITEM_ID)
						|| (getWorld().getServer().getConfig().ONLY_BASIC_RUNES
						&& getWorld().getServer().getEntityHandler().getItemDef(item.getCatalogId()).getName().endsWith("-Rune")
						&& item.getCatalogId() >= ItemId.LIFE_RUNE.id())) {
						// world does not allow drop
						continue;
					}
					if (getWorld().getServer().getEntityHandler().getItemDef(item.getCatalogId()).isStackable()) {
						dropStackItem(item.getCatalogId(), item.getAmount(), owner);
					} else {
						dropStandardItem(item, owner);
					}
				}
			}
		}
	}

	private int getBoneTier(int boneId) {
		switch(ItemId.getById(boneId)) {
			case BONES:
			case BAT_BONES:
			default:
				return 0;
			case BIG_BONES:
				return 1;
			case DRAGON_BONES:
				return 2;
		}
	}

	private int getHerbTier(int boneId) {
		switch(ItemId.getById(boneId)) {
			case UNIDENTIFIED_GUAM_LEAF:
			case UNIDENTIFIED_MARRENTILL:
			case UNIDENTIFIED_TARROMIN:
			case UNIDENTIFIED_HARRALANDER:
			default:
				return 0;
			case UNIDENTIFIED_RANARR_WEED:
			case UNIDENTIFIED_IRIT_LEAF:
			case UNIDENTIFIED_AVANTOE:
				return 1;
			case UNIDENTIFIED_KWUARM:
			case UNIDENTIFIED_CADANTINE:
			case UNIDENTIFIED_DWARF_WEED:
				return 2;
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

	public static ArrayList<Item> calculateCustomKingBlackDragonDropTest(Player owner, boolean ringOfWealth) {
		ArrayList<Item> returnMe = new ArrayList<Item>();
		if (owner.getWorld().getNpcDrops().getKbdTableCustom().rollAccess(NpcId.KING_BLACK_DRAGON.id(), ringOfWealth)) {
			ArrayList<Item> kbdSpecificLoot = owner.getWorld().getNpcDrops().getKbdTableCustom().rollItem(ringOfWealth, owner);
			if (kbdSpecificLoot != null) {
				return kbdSpecificLoot;
			}
		}
		return returnMe;
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
				boolean destroyHerbs = false;
				if (Formulae.isUnidHerb(new Item(dropID)) && EnchantedCrowns.shouldActivate(owner, ItemId.CROWN_OF_THE_HERBALIST)) {
					int conf = owner.getCache().hasKey("herb_conf") ? owner.getCache().getInt("herb_conf") : 7;
					int herbTier = getHerbTier(dropID);
					destroyHerbs = MathUtil.isKthBitSet(conf, herbTier + 1);
				}

				if (!destroyHerbs) {
					groundItem = new GroundItem(owner.getWorld(), dropID, getX(), getY(), amount, owner, item.getNoted());
					groundItem.setAttribute("npcdrop", true);
					getWorld().registerItem(groundItem);
				} else {
					EnchantedCrowns.giveHerbExperience(owner, new Item(item.getCatalogId()));
					owner.playerServerMessage(MessageType.QUEST, "Your crown shines and the herb gets destroyed");
					EnchantedCrowns.useCharge(owner, ItemId.CROWN_OF_THE_HERBALIST);
				}

			}
		}
	}

	/**
	 * Distributes the XP from this monster and the loot
	 *
	 * @param attacker the person that "finished off" the npc
	 * @return the player who did the most damage / should get the loot
	 */
	private Pair<UUID, Long> handleXpDistribution(final Mob attacker) {
		final int totalCombatXP = Formulae.combatExperience(this);
		UUID UUIDWithMostDamage = attacker.getUUID();
		Long hashWithMostDamage = attacker instanceof Player ? ((Player)attacker).getUsernameHash() : 0;
		int currentHighestDamage = 0;

		if (this.getWorld().getServer().getConfig().WANTS_KILL_STEALING && attacker.isPlayer()) {
			// determine to what skill give xp
			KillType type = attacker.getKillType();
			Player lastAttacker = (Player)attacker;

			if (type == KillType.COMBAT) {
				int[] skillsDist = new int[Skill.maxId(Skill.ATTACK.name(), Skill.DEFENSE.name(),
					Skill.STRENGTH.name(), Skill.HITS.name()) + 1];
				switch (lastAttacker.getCombatStyle()) {
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
				lastAttacker.incExp(skillsDist, totalCombatXP, true);
			} else if (type == KillType.RANGED) {
				int maxTotalXP = totalCombatXP * 4;
				Pair<Integer, Long> damageInfoByPlayer = getRangeDamageInfoBy(lastAttacker.getUUID());
				int damageDoneByPlayer = damageInfoByPlayer.getLeft();
				int alreadyGivenXp = this.getWorld().getServer().getConfig().RANGED_GIVES_XP_HIT ? 16 * damageDoneByPlayer / 3 : 0;
				int remainderXP = maxTotalXP - alreadyGivenXp;
				if (remainderXP > 0) {
					lastAttacker.incExp(Skill.RANGED.id(), remainderXP, true);
					ActionSender.sendStat(lastAttacker, Skill.RANGED.id());
				}
			} // for MAGIC is of type per spell

			return Pair.of(lastAttacker.getUUID(), lastAttacker.getUsernameHash());
		}

		// Melee damagers
		for (UUID ID : getCombatDamagers()) {
			final Pair<Integer, Long> damageInfoByPlayer = getCombatDamageInfoBy(ID);
			final int damageDoneByPlayer = damageInfoByPlayer.getLeft();

			if (damageDoneByPlayer > currentHighestDamage) {
				UUIDWithMostDamage = ID;
				hashWithMostDamage = damageInfoByPlayer.getRight();
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

				player.resetTrackedDamageAndBlockedDamage(this);
			}
		}

		// Ranged damagers
		for (UUID ID : getRangeDamagers()) {
			Pair<Integer, Long> damageInfoByPlayer = getRangeDamageInfoBy(ID);
			int damageDoneByPlayer = damageInfoByPlayer.getLeft();
			if (damageDoneByPlayer > currentHighestDamage) {
				UUIDWithMostDamage = ID;
				hashWithMostDamage = damageInfoByPlayer.getRight();
				currentHighestDamage = damageDoneByPlayer;
			}

			Player player = getWorld().getPlayerByUUID(ID);
			if (player != null) {
				int maxTotalXP = (int) (((double) (totalCombatXP * 4) / (double) (getDef().hits)) * (double) (damageDoneByPlayer));
				int alreadyGivenXp = this.getWorld().getServer().getConfig().RANGED_GIVES_XP_HIT ? 16 * damageDoneByPlayer / 3 : 0;
				int remainderXP = maxTotalXP - alreadyGivenXp;
				if (remainderXP > 0) {
					player.incExp(Skill.RANGED.id(), remainderXP, true);
					ActionSender.sendStat(player, Skill.RANGED.id());
				}
			}
		}

		// Magic damagers
		for (UUID ID : getMageDamagers()) {
			Pair<Integer, Long> damageInfoByPlayer = getMageDamageInfoBy(ID);
			int dmgDoneByPlayer = damageInfoByPlayer.getLeft();

			if (dmgDoneByPlayer > currentHighestDamage) {
				UUIDWithMostDamage = ID;
				hashWithMostDamage = damageInfoByPlayer.getRight();
				currentHighestDamage = dmgDoneByPlayer;
			}
		}
		return Pair.of(UUIDWithMostDamage, hashWithMostDamage);
	}

	public void initializeTalkScript(final Player player) {
		final Npc npc = this;
		getWorld().getServer().getGameEventHandler().add(new ImmediateEvent(getWorld(), "Init Talk Script") {
			@Override
			public void action() {
				NpcInteraction interaction = NpcInteraction.NPC_TALK_TO;
				NpcInteraction.setInteractions(npc, player, interaction);
				npc.setMultiTimeout(-1);
				npc.setPlayerWantsNpc(false);
				getWorld().getServer().getPluginHandler().handlePlugin(TalkNpcTrigger.class, player, new Object[]{player, npc});
			}
		});
	}

	public void setMultiTimeout(long currentTimeMillis) {
		this.multiTimeout = currentTimeMillis;
	}

	public void setInteractingPlayer(Player player) {
		this.interactingPlayer = player;
	}

	public void setPlayerWantsNpc(boolean wantsNpc) {
		this.playerWantsNpc = wantsNpc;
	}

	public void remove() {
		this.killed = true;
		double respawnMult = getConfig().NPC_RESPAWN_MULTIPLIER;
		Npc n = this;
		//In RSC, the player only gets updated about combat ending the tick after the kill.
		//Causes issues with retro clients.
		//TODO: Come up with a solution that works with retro clients? May not be authentic for older clients anyway.
		if(getConfig().BASED_CONFIG_DATA > 18) {
			getWorld().getServer().getGameEventHandler().add(new GameTickEvent(getWorld(), null, 0, "Remove Combat Event", DuplicationStrategy.ONE_PER_MOB) {
				@Override
				public void run() {
					n.resetCombatEvent();
					running = false;
				}
			});
		} else {
			n.resetCombatEvent();
		}
		this.setLastOpponent(null);
		if (!isRemoved() && shouldRespawn && def.respawnTime() > 0) {
			super.remove();
			startRespawning();
			setRespawning(true);
			getWorld().getServer().getGameEventHandler().add(new DelayedEvent(getWorld(), null, (long) (def.respawnTime() * respawnMult * 1000), "Respawn NPC", DuplicationStrategy.ONE_PER_MOB) {
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

	/**
	 * Gets the NPC to move to an adjacent tile, with a priority system.
	 */
	public void moveToAdjacentTile() {
		ArrayList<Point> possiblePoints = new ArrayList<>();
		//Walk priority seems to be positives first? This is different from the client pathfinding.
		//TODO: More investigation on the direction an NPC would move towards in this case.
		for (int x = 1; x >= -1; x = x - 2) {
			possiblePoints.add(new Point(getX() + x, getY()));
		}
		for (int y = 1; y >= -1; y = y - 2) {
			possiblePoints.add(new Point(getX(), getY() + y));
		}

		possiblePoints.add(new Point(getX() + 1, getY() + 1));
		possiblePoints.add(new Point(getX() + 1, getY() - 1));
		possiblePoints.add(new Point(getX() - 1, getY() + 1));
		possiblePoints.add(new Point(getX() - 1, getY() - 1));

		for (Point possiblePoint : possiblePoints) {
			if (possiblePoint.inBounds(getLoc().minX(), getLoc().minY(), getLoc().maxX(), getLoc().maxY())
				&& canWalk(getWorld(), possiblePoint.getX(), possiblePoint.getY())) {
				walk(possiblePoint.getX(), possiblePoint.getY());
				break;
			}
		}
	}

	public void updatePosition() {
		NpcInteraction interaction = getNpcInteraction();
		Player player = getInteractingPlayer();
		if (player != null && player.getInteractingNpc() == this) {
			switch (interaction) {
				//Interactions that should reset the NPC's path.
				case NPC_TALK_TO:
				case NPC_USE_ITEM:
					resetPath();
					resetRange();
				default:
					break;
			}

			switch (interaction) {
				//Other interaction specific handling.
				case NPC_TALK_TO:
					// NPCs on the same tile as you will walk somewhere else.
					if (player.getLocation().equals(getLocation())) {
						moveToAdjacentTile();
					}
				case NPC_USE_ITEM:
				case NPC_GNOMEBALL_OP:
					if (finishedPath() && !inCombat()) face(player);
					break;
				case NPC_OP:
				default:
					break;
			}
		} else {
			getNpcBehavior().tick();
		}
		super.updatePosition();
	}

	private boolean canWalk(World world, int x, int y) {
		int myX = getX();
		int myY = getY();
		int newX = x;
		int newY = y;
		boolean myXBlocked = false, myYBlocked = false, newXBlocked = false, newYBlocked = false;
		if (myX > x) {
			myXBlocked = checkBlocking(world,myX - 1, myY, 8); // Check right
			// tiles
			newX = myX - 1;
		} else if (myX < x) {
			myXBlocked = checkBlocking(world,myX + 1, myY, 2); // Check left
			// tiles
			newX = myX + 1;
		}
		if (myY > y) {
			myYBlocked = checkBlocking(world, myX, myY - 1, 4); // Check top tiles
			newY = myY - 1;
		} else if (myY < y) {
			myYBlocked = checkBlocking(world, myX, myY + 1, 1); // Check bottom
			// tiles
			newY = myY + 1;
		}

		if ((myXBlocked && myYBlocked) || (myXBlocked && myY == newY) || (myYBlocked && myX == newX)) {
			return false;
		}

		if (newX > myX) {
			newXBlocked = checkBlocking(world, newX, newY, 2);
		} else if (newX < myX) {
			newXBlocked = checkBlocking(world, newX, newY, 8);
		}

		if (newY > myY) {
			newYBlocked = checkBlocking(world, newX, newY, 1);
		} else if (newY < myY) {
			newYBlocked = checkBlocking(world, newX, newY, 4);
		}
		if ((newXBlocked && newYBlocked) || (newXBlocked && myY == newY) || (myYBlocked && myX == newX)) {
			return false;
		}
		if ((myXBlocked && newXBlocked) || (myYBlocked && newYBlocked)) {
			return false;
		}
		return true;
	}

	private boolean checkBlocking(World world, int x, int y, int bit) {
		TileValue t = world.getTile(x, y);
		Point point = new Point(x, y);
		for (Npc n : getViewArea().getNpcsInView()) {
			if (n.getLocation().equals(point)) {
				return true;
			}
		}
		for (Player areaPlayer : getViewArea().getPlayersInView()) {
			if (areaPlayer.getLocation().equals(point)) {
				return true;
			}
		}
		return isBlocking(t.traversalMask, (byte) bit);
	}

	private boolean isBlocking(int objectValue, byte bit) {
		if ((objectValue & bit) != 0) { // There is a wall in the way
			return true;
		}
		if ((objectValue & 16) != 0) { // There is a diagonal wall here:
			// \
			return true;
		}
		if ((objectValue & 32) != 0) { // There is a diagonal wall here:
			// /
			return true;
		}
		if ((objectValue & 64) != 0) { // This tile is unwalkable
			return true;
		}
		return false;
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
		// gnome agility course
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

	public void walkToRespawn() {
		walkToEntityAStar(loc.startX, loc.startY);
	}

	public long getMultiTimeout() {
		return multiTimeout;
	}

	public boolean getPlayerWantsNpc() {
		return playerWantsNpc;
	}

	public Player getInteractingPlayer() {
		return interactingPlayer;
	}
}
