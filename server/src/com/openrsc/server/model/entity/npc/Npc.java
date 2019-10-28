package com.openrsc.server.model.entity.npc;

import com.openrsc.server.constants.Constants;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.custom.NpcLootEvent;
import com.openrsc.server.event.rsc.ImmediateEvent;
import com.openrsc.server.event.rsc.impl.BankEventNpc;
import com.openrsc.server.event.rsc.impl.RangeEventNpc;
import com.openrsc.server.event.rsc.impl.ThrowingEvent;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.external.ItemDropDef;
import com.openrsc.server.external.NPCDef;
import com.openrsc.server.external.NPCLoc;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.states.CombatState;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
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
	 * Logger instance
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * The current status of the player
	 */
	private Action status = Action.IDLE;

	/**
	 * RANGED
	 */
	private RangeEventNpc rangeEventNpc;
	private BankEventNpc bankEventNpc;
	private ThrowingEvent throwingEvent;

	public RangeEventNpc getRangeEventNpc() {
		return rangeEventNpc;
	}

	public BankEventNpc getBankEventNpc() {
		return bankEventNpc;
	}

	public void setRangeEventNpc(RangeEventNpc event) {
		if (rangeEventNpc != null) {
			rangeEventNpc.stop();
		}
		rangeEventNpc = event;
		setStatus(Action.RANGING_MOB);
		getWorld().getServer().getGameEventHandler().add(rangeEventNpc);
	}

	public void setBankEventNpc(BankEventNpc event) {
		if (bankEventNpc != null) {
			bankEventNpc.stop();
		}
		bankEventNpc = event;
		getWorld().getServer().getGameEventHandler().add(bankEventNpc);
	}

	public boolean isRanging() {
		return rangeEventNpc != null || throwingEvent != null;
	}

	public boolean isBanking() {
		return bankEventNpc != null;
	}

	public void resetRange() {
		if (rangeEventNpc != null) {
			rangeEventNpc.stop();
			rangeEventNpc = null;
		}
		if (throwingEvent != null) {
			throwingEvent.stop();
			throwingEvent = null;
		}
		setStatus(Action.IDLE);
	}

	public void resetBankEvent() {
		if (bankEventNpc != null) {
			bankEventNpc.stop();
			bankEventNpc = null;
		}
		setStatus(Action.IDLE);
	}

	private long consumeTimer2 = 0;

	public long getConsumeTimer2() {
		return consumeTimer2;
	}

	public void setConsumeTimer2(int delay) {
		consumeTimer2 = System.currentTimeMillis() + 0;
	}

	public void setConsumeTimer2() {
		consumeTimer2 = System.currentTimeMillis();
	}

	public boolean checkAttack(Npc mob, boolean missile) {
		if (mob.isPlayer()) {
			Mob victim = (Mob) mob;
			/*if (victim.inCombat() && victim.getDuel().isDuelActive()) {
				Mob opponent = (Mob) getOpponent();
				if (opponent != null && victim.equals(opponent)) {
					return true;
				}
			}*/
			if (!missile) {
				if (System.currentTimeMillis() - mob.getCombatTimer() < (mob.getCombatState() == CombatState.RUNNING
					|| mob.getCombatState() == CombatState.WAITING ? 3000 : 500)) {
					return false;
				}
			}

			int myWildLvl = getLocation().wildernessLevel();
			int victimWildLvl = victim.getLocation().wildernessLevel();
			if (myWildLvl < 1 || victimWildLvl < 1) {
				//message("You can't attack other players here. Move to the wilderness");
				return false;
			}
			int combDiff = Math.abs(getCombatLevel() - victim.getCombatLevel());
			if (combDiff > myWildLvl) {
				//message("You can only attack players within " + (myWildLvl) + " levels of your own here");
				//message("Move further into the wilderness for less restrictions");
				return false;
			}
			if (combDiff > victimWildLvl) {
				//message("You can only attack players within " + (victimWildLvl) + " levels of your own here");
				//message("Move further into the wilderness for less restrictions");
				return false;
			}

			/*if (victim.isInvulnerable(mob) || victim.isInvisible(mob)) {
				//message("You are not allowed to attack that person");
				return false;
			}*/
			return true;
		} else if (mob.isNpc()) {
			Npc victim = (Npc) mob;
			if (!victim.getDef().isAttackable()) {
				//setSuspiciousPlayer(true);
				return false;
			}
			return true;
		}
		return true;
	}
	/*public int getRangeEquip() {
		for (Item item : getInventory().getItems()) {
			if (item.isWielded() && (DataConversions.inArray(Formulae.bowIDs, item.getID())
				|| DataConversions.inArray(Formulae.xbowIDs, item.getID()))) {
				return item.getID();
			}
		}
		return -1;
	}*/
	/**
	 * The definition of this npc
	 */
	protected NPCDef def;
	/**
	 * The location of this npc
	 */
	private NPCLoc loc;
	private int armourPoints = 1;
	/**
	 * Holds players that did damage with combat
	 */
	private Map<Integer, Integer> combatDamagers = new HashMap<Integer, Integer>();
	/**
	 * Holds players that did damage with mage
	 */
	private Map<Integer, Integer> mageDamagers = new HashMap<Integer, Integer>();
	/**
	 * Holds players that did damage with range
	 */
	private Map<Integer, Integer> rangeDamagers = new HashMap<Integer, Integer>();
	private boolean shouldRespawn = true;
	private boolean isRespawning = false;
	private boolean executedAggroScript = false;
	private int weaponAimPoints = 1;
	private int weaponPowerPoints = 1;
	private NpcBehavior npcBehavior;
	private ArrayList<NpcLootEvent> deathListeners = new ArrayList<NpcLootEvent>(1); // TODO: Should use a more generic class. Maybe PlayerKilledNpcListener, but that is in plugins jar.

	public Npc(World world, int id, int x, int y) {
		this(world, new NPCLoc(id, x, y, x - 5, x + 5, y - 5, y + 5));
	}

	public Npc(World world, int id, int x, int y, int radius) {
		this(world, new NPCLoc(id, x, y, x - radius, x + radius, y - radius, y + radius));
	}

	public Npc(World world, int id, int startX, int startY, int minX, int maxX, int minY, int maxY) {
		this(world, new NPCLoc(id, startX, startY, minX, maxX, minY, maxY));
	}

	public Npc(World world, Point location, int x, String username) {
		super(world);
		//Default constructor for NPC, this is useful for finding the name of an NPC without having to spawn an NPC.
	}

	public Npc(World world, NPCLoc loc) {
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
		this.npcBehavior = new NpcBehavior(this);
		this.loc = loc;
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

		getWorld().getServer().getGameEventHandler().add(statRestorationEvent);
	}

	/**
	 * Adds combat damage done by a player
	 *
	 * @param p
	 * @param damage
	 */
	public void addCombatDamage(Player p, int damage) {
		if (combatDamagers.containsKey(p.getDatabaseID())) {
			combatDamagers.put(p.getDatabaseID(), combatDamagers.get(p.getDatabaseID()) + damage);
		} else {
			combatDamagers.put(p.getDatabaseID(), damage);
		}
	}

	/**
	 * Adds mage damage done by a player
	 *
	 * @param p
	 * @param damage
	 */
	public void addMageDamage(Player p, int damage) {
		if (mageDamagers.containsKey(p.getDatabaseID())) {
			mageDamagers.put(p.getDatabaseID(), mageDamagers.get(p.getDatabaseID()) + damage);
		} else {
			mageDamagers.put(p.getDatabaseID(), damage);
		}
	}

	/**
	 * Adds range damage done by a player
	 *
	 * @param p
	 * @param damage
	 */
	public void addRangeDamage(Player p, int damage) {
		if (rangeDamagers.containsKey(p.getDatabaseID())) {
			rangeDamagers.put(p.getDatabaseID(), rangeDamagers.get(p.getDatabaseID()) + damage);
		} else {
			rangeDamagers.put(p.getDatabaseID(), damage);
		}
	}

	public void useNormalPotionNpc(final int affectedStat, final int percentageIncrease, final int modifier, final int left) {
		//mob.message("You drink some of your " + item.getDef().getName().toLowerCase());
		int baseStat = this.getSkills().getLevel(affectedStat) > this.getSkills().getMaxStat(affectedStat) ? this.getSkills().getMaxStat(affectedStat) : this.getSkills().getLevel(affectedStat);
		int newStat = baseStat
			+ DataConversions.roundUp((this.getSkills().getMaxStat(affectedStat) / 100D) * percentageIncrease)
			+ modifier;
		if (newStat > this.getSkills().getLevel(affectedStat)) {
			this.getSkills().setLevel(affectedStat, newStat);
		}
		//sleep(1200);
		if (left <= 0) {
			//player.message("You have finished your potion");
		} else {
			//player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}

	public void displayNpcTeleportBubble(int x, int y) {
		for (Object o : getViewArea().getPlayersInView()) {
			Player p = ((Player) o);
			ActionSender.sendTeleBubble(p, x, y, false);
		}
		setTeleporting(true);
	}

	public int getArmourPoints() {
		return armourPoints;
	}

	public int getNPCCombatLevel() {
		return getDef().combatLevel;
	}

	/**
	 * Combat damage done by player p
	 *
	 * @param p
	 * @return
	 */
	private int getCombatDamageDoneBy(Player p) {
		if (p == null) {
			return 0;
		}
		if (!combatDamagers.containsKey(p.getDatabaseID())) {
			return 0;
		}
		int dmgDone = combatDamagers.get(p.getDatabaseID());
		return (dmgDone > this.getDef().getHits() ? this.getDef().getHits() : dmgDone);
	}

	private int getCombatDamageDoneBy(Npc n) {
		if (n == null) {
			return 0;
		}
		int dmgDone = combatDamagers.get(n.getID());
		return (dmgDone > this.getDef().getHits() ? this.getDef().getHits() : dmgDone);
	}

	/**
	 * Iterates over combatDamagers map and returns the keys
	 *
	 * @return
	 */
	private ArrayList<Integer> getCombatDamagers() {
		return new ArrayList<Integer>(combatDamagers.keySet());
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
	 * Mage damage done by player p
	 *
	 * @param p
	 * @return
	 */
	private int getMageDamageDoneBy(Player p) {
		if (p == null || !mageDamagers.containsKey(p.getDatabaseID())) {
			return 0;
		}
		int dmgDone = mageDamagers.get(p.getDatabaseID());
		return (dmgDone > this.getDef().getHits() ? this.getDef().getHits() : dmgDone);
	}

	private int getMageDamageDoneBy(Npc n) {
		if (n == null || !mageDamagers.containsKey(n.getID())) {
			return 0;
		}
		int dmgDone = mageDamagers.get(n.getID());
		return (dmgDone > this.getDef().getHits() ? this.getDef().getHits() : dmgDone);
	}

	/**
	 * Iterates over mageDamagers map and returns the keys
	 *
	 * @return
	 */
	private ArrayList<Integer> getMageDamagers() {
		return new ArrayList<Integer>(mageDamagers.keySet());
	}

	/**
	 * Range damage done by player p
	 *
	 * @param p
	 * @return
	 */
	private int getRangeDamageDoneBy(Player p) {
		if (p == null || !rangeDamagers.containsKey(p.getDatabaseID())) {
			return 0;
		}
		int dmgDone = rangeDamagers.get(p.getDatabaseID());
		return (dmgDone > this.getDef().getHits() ? this.getDef().getHits() : dmgDone);
	}

	private int getRangeDamageDoneBy(Npc n) {
		if (n == null || !rangeDamagers.containsKey(n.getID())) {
			return 0;
		}
		int dmgDone = rangeDamagers.get(n.getID());
		return (dmgDone > this.getDef().getHits() ? this.getDef().getHits() : dmgDone);
	}

	/**
	 * Iterates over rangeDamagers map and returns the keys
	 *
	 * @return
	 */
	private ArrayList<Integer> getRangeDamagers() {
		return new ArrayList<Integer>(rangeDamagers.keySet());
	}

	public int getWeaponAimPoints() {
		return weaponAimPoints;
	}

	public int getWeaponPowerPoints() {
		return weaponPowerPoints;
	}

	@Override
	public void killedBy(Mob mob) {
		if (!mob.isNpc()) {
			mob = handleLootAndXpDistribution(mob);
			this.cure();
			Player owner = mob instanceof Player ? (Player) mob : null;
			if (owner != null) {
				ActionSender.sendSound(owner, "victory");
				owner.getWorld().getServer().getAchievementSystem().checkAndIncSlayNpcTasks(owner, this);
				owner.incKills2();
				ActionSender.sendKills2(owner);

				//If NPC kill messages are enabled and the filter is enabled and the NPC is in the list of NPCs, display the messages,
				//otherwise we will display the message for all NPCs if NPC kill messages are enabled if there is no filter.
				//Also, if we don't have NPC kill logging enabled, we can't have NPC kill messages.
				if (getWorld().getServer().getConfig().NPC_KILL_LOGGING) {
					if (owner.getCache().hasKey("show_npc_kc") && owner.getCache().getBoolean("show_npc_kc")
						&& getWorld().getServer().getConfig().NPC_KILL_MESSAGES) {
							owner.addNpcKill(this,!getWorld().getServer().getConfig().NPC_KILL_MESSAGES_FILTER
								|| getWorld().getServer().getConfig().NPC_KILL_MESSAGES_NPCs.contains(this.getDef().getName()));
					} else
						owner.addNpcKill(this, false);
				}


				owner = handleLootAndXpDistribution(((Player) mob));

				//Determine if the RDT is hit first
				boolean rdtHit = false;
				Item rare = null;
				if (getWorld().getServer().getConfig().WANT_NEW_RARE_DROP_TABLES && mob.isPlayer()) {
					if (getWorld().standardTable.rollAccess(this.id, Functions.isWielding(((Player) mob), com.openrsc.server.constants.ItemId.RING_OF_WEALTH.id()))) {
						rdtHit = true;
						rare = getWorld().standardTable.rollItem(Functions.isWielding(((Player) mob), com.openrsc.server.constants.ItemId.RING_OF_WEALTH.id()), ((Player) mob));
					} else if (getWorld().gemTable.rollAccess(this.id, Functions.isWielding(((Player) mob), com.openrsc.server.constants.ItemId.RING_OF_WEALTH.id()))) {
						rdtHit = true;
						rare = getWorld().gemTable.rollItem(Functions.isWielding(((Player) mob), com.openrsc.server.constants.ItemId.RING_OF_WEALTH.id()), ((Player) mob));
					}
				}

				if (rare != null) {
					if (!handleRingOfAvarice(owner, rare)) {
						GroundItem groundItem = new GroundItem(owner.getWorld(), rare.getID(), getX(), getY(), rare.getAmount(), owner);
						groundItem.setAttribute("npcdrop", true);
						getWorld().registerItem(groundItem);
					}
					getWorld().getServer().getLoginExecutor().getPlayerDatabase().addNpcDrop(
						owner, this, rare.getID(), rare.getAmount());
				}

				ItemDropDef[] drops = def.getDrops();

				int total = 0;
				int weightTotal = 0;
				for (ItemDropDef drop : drops) {
					total += drop.getWeight();
					weightTotal += drop.getWeight();
					if (drop.getWeight() == 0 && drop.getID() != -1) {
						if (!handleRingOfAvarice(owner, new Item(drop.getID(), drop.getAmount()))) {
							GroundItem groundItem = new GroundItem(owner.getWorld(), drop.getID(), getX(), getY(), drop.getAmount(), owner);
							groundItem.setAttribute("npcdrop", true);
							getWorld().registerItem(groundItem);
						}
						continue;
					}

				}

				if (!rdtHit) {
					int hit = DataConversions.random(0, total);
					total = 0;

					for (ItemDropDef drop : drops) {
						if (drop.getID() == com.openrsc.server.constants.ItemId.UNHOLY_SYMBOL_MOULD.id() && owner.getQuestStage(Quests.OBSERVATORY_QUEST) > -1) {
							continue;
						}

						Item temp = new Item(drop.getID());

						if (drop == null) {
							continue;
						}

						int dropID = drop.getID();
						int amount = drop.getAmount();
						int weight = drop.getWeight();

						double currentRatio = (double) weight / (double) weightTotal;
						if (hit >= total && hit < (total + weight)) {
							if (dropID != -1) {
								if (getWorld().getServer().getEntityHandler().getItemDef(dropID).isMembersOnly()
									&& !getWorld().getServer().getConfig().MEMBER_WORLD) {
									continue;
								}

								if (!getWorld().getServer().getEntityHandler().getItemDef(dropID).isStackable()) {

									getWorld().getServer().getLoginExecutor().getPlayerDatabase().addNpcDrop(
										owner, this, dropID, amount);
									GroundItem groundItem;

									// We need to drop multiple counts of "1" item if it's not a stack
									for (int count = 0; count < amount; count++) {

										// Gem Drop Table + 1/128 chance to roll into very rare item
										if (drop.getID() == com.openrsc.server.constants.ItemId.UNCUT_SAPPHIRE.id()) {
											dropID = Formulae.calculateGemDrop((Player) mob);
											amount = 1;
										}

										// Herb Drop Table
										else if (drop.getID() == com.openrsc.server.constants.ItemId.UNIDENTIFIED_GUAM_LEAF.id()) {
											dropID = Formulae.calculateHerbDrop();
										}

										if (dropID != com.openrsc.server.constants.ItemId.NOTHING.id() && getWorld().getServer().getEntityHandler().getItemDef(dropID).isMembersOnly() && !getWorld().getServer().getConfig().MEMBER_WORLD) {
											continue;
										} else if (dropID != com.openrsc.server.constants.ItemId.NOTHING.id()) {
											if (!handleRingOfAvarice(owner, new Item(drop.getID(), drop.getAmount()))) {
												groundItem = new GroundItem(owner.getWorld(), dropID, getX(), getY(), 1, owner);
												groundItem.setAttribute("npcdrop", true);
												getWorld().registerItem(groundItem);
											}
										}
									}

								} else {

									// Gold Drops
									if (drop.getID() == com.openrsc.server.constants.ItemId.COINS.id()) {
										amount = Formulae.calculateGoldDrop(
											GoldDrops.drops.getOrDefault(this.getID(), new int[]{1})
										);
										if (Functions.isWielding(((Player) mob), com.openrsc.server.constants.ItemId.RING_OF_SPLENDOR.id())) {
											amount += Formulae.getSplendorBoost(amount);
											((Player) mob).message("Your ring of splendor shines brightly!");
										}
									}

									getWorld().getServer().getLoginExecutor().getPlayerDatabase().addNpcDrop(
										owner, this, dropID, amount);
									if (!handleRingOfAvarice(owner, new Item(drop.getID(), amount))) {
										GroundItem groundItem = new GroundItem(owner.getWorld(), dropID, getX(), getY(), amount, owner);
										groundItem.setAttribute("npcdrop", true);
										getWorld().registerItem(groundItem);
									}
								}

								// Check if we have a "valuable drop" (configurable)
								if (dropID != com.openrsc.server.constants.ItemId.NOTHING.id() &&
									amount > 0 &&
									getWorld().getServer().getConfig().VALUABLE_DROP_MESSAGES &&
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
							break;
						}
						total += weight;
					}
				}

				if (mob instanceof Player) {
					for (NpcLootEvent e : deathListeners) {
						e.onLootNpcDeath((Player) mob, this);
					}
				}
				if (mob instanceof Npc) {
					for (NpcLootEvent e : deathListeners) {
						e.onLootNpcDeath((Npc) mob, this);
					}
				}
				deathListeners.clear();
				remove();
			}
		} else {
			mob = handleLootAndXpDistribution(mob);
			Npc owner = mob instanceof Npc ? (Npc) mob : null;
			Player owner2 = mob instanceof Player ? (Player) mob : null;
			if (owner != null) {
				/*if(owner.getPetNpc() > 0) {
				owner = handleLootAndXpDistributionPet((Npc) mob);
				} else*/
				owner = handleLootAndXpDistribution((Npc) mob);
				//if(owner2 != null){
				//owner2 = handleLootAndXpDistribution((Player) mob);
				//}

				//Determine if the RDT is hit first
				boolean rdtHit = false;
				Item rare = null;
				if (getWorld().getServer().getConfig().WANT_NEW_RARE_DROP_TABLES && mob.isPlayer()) {
					if (getWorld().standardTable.rollAccess(this.id, Functions.isWielding(((Player) mob), com.openrsc.server.constants.ItemId.RING_OF_WEALTH.id()))) {
						rdtHit = true;
						rare = getWorld().standardTable.rollItem(Functions.isWielding(((Player) mob), com.openrsc.server.constants.ItemId.RING_OF_WEALTH.id()), ((Player) mob));
					} else if (getWorld().gemTable.rollAccess(this.id, Functions.isWielding(((Player) mob), com.openrsc.server.constants.ItemId.RING_OF_WEALTH.id()))) {
						rdtHit = true;
						rare = getWorld().gemTable.rollItem(Functions.isWielding(((Player) mob), com.openrsc.server.constants.ItemId.RING_OF_WEALTH.id()), ((Player) mob));
					}
				}

				if (rare != null) {
					if (!handleRingOfAvarice((Player) mob, rare)) {
						GroundItem groundItem = new GroundItem(owner.getWorld(), rare.getID(), getX(), getY(), rare.getAmount(), owner);
						groundItem.setAttribute("npcdrop", true);
						getWorld().registerItem(groundItem);
					}
				}


				ItemDropDef[] drops = def.getDrops();

				int total = 0;
				int weightTotal = 0;
				for (ItemDropDef drop : drops) {
					total += drop.getWeight();
					weightTotal += drop.getWeight();
					if (drop.getWeight() == 0 && drop.getID() != -1) {
						if (!handleRingOfAvarice((Player) mob, new Item(drop.getID(), drop.getAmount()))) {
							GroundItem groundItem = new GroundItem(owner.getWorld(), drop.getID(), getX(), getY(), drop.getAmount(), owner);
							groundItem.setAttribute("npcdrop", true);
							getWorld().registerItem(groundItem);
						}
						continue;
					}
				}

				if (!rdtHit) {
					int hit = DataConversions.random(0, total);
					total = 0;

					for (ItemDropDef drop : drops) {

						Item temp = new Item(drop.getID());

						if (drop == null) {
							continue;
						}

						int dropID = drop.getID();
						int amount = drop.getAmount();
						int weight = drop.getWeight();

						double currentRatio = (double) weight / (double) weightTotal;
						if (hit >= total && hit < (total + weight)) {
							if (dropID != -1) {
								if (getWorld().getServer().getEntityHandler().getItemDef(dropID).isMembersOnly()
									&& !getWorld().getServer().getConfig().MEMBER_WORLD) {
									continue;
								}

								if (!getWorld().getServer().getEntityHandler().getItemDef(dropID).isStackable()) {
									GroundItem groundItem;

									// We need to drop multiple counts of "1" item if it's not a stack
									for (int count = 0; count < amount; count++) {

										// Gem Drop Table + 1/128 chance to roll into very rare item
										if (drop.getID() == com.openrsc.server.constants.ItemId.UNCUT_SAPPHIRE.id()) {
											dropID = Formulae.calculateGemDrop((Player) mob);
											amount = 1;
										}

										// Herb Drop Table
										else if (drop.getID() == com.openrsc.server.constants.ItemId.UNIDENTIFIED_GUAM_LEAF.id()) {
											dropID = Formulae.calculateHerbDrop();
										}

										if (dropID != com.openrsc.server.constants.ItemId.NOTHING.id() && getWorld().getServer().getEntityHandler().getItemDef(dropID).isMembersOnly() && !getWorld().getServer().getConfig().MEMBER_WORLD) {
											continue;
										} else if (dropID != com.openrsc.server.constants.ItemId.NOTHING.id()) {
											if (!handleRingOfAvarice((Player) mob, new Item(drop.getID(), drop.getAmount()))) {
												groundItem = new GroundItem(owner.getWorld(), dropID, getX(), getY(), 1, owner);
												groundItem.setAttribute("npcdrop", true);
												getWorld().registerItem(groundItem);
											}
										}
									}
								} else {
									// Gold Drops
									if (drop.getID() == com.openrsc.server.constants.ItemId.COINS.id()) {
										amount = Formulae.calculateGoldDrop(
											GoldDrops.drops.getOrDefault(this.getID(), new int[]{1})
										);
										if (Functions.isWielding(((Player) mob), com.openrsc.server.constants.ItemId.RING_OF_SPLENDOR.id())) {
											amount += Formulae.getSplendorBoost(amount);
											((Player) mob).message("Your ring of splendor shines brightly!");
										}
									}

									if (!handleRingOfAvarice((Player) mob, new Item(drop.getID(), amount))) {
										GroundItem groundItem = new GroundItem(owner.getWorld(), dropID, getX(), getY(), amount, owner);
										groundItem.setAttribute("npcdrop", true);
										getWorld().registerItem(groundItem);
									}
								}
							}
							break;
						}
						total += weight;
					}
				}

				if (mob instanceof Npc) {
					for (NpcLootEvent e : deathListeners) {
						e.onLootNpcDeath((Npc) mob, this);
					}
				}
				if (mob instanceof Player) {
					for (NpcLootEvent e : deathListeners) {
						e.onLootNpcDeath((Player) mob, this);
					}
				}
				deathListeners.clear();
				remove();
			}
		}
	}

	/**
	 * Distributes the XP from this monster and the loot
	 *
	 * @param attacker the person that "finished off" the npc
	 * @return the player who did the most damage / should get the loot
	 */
	/*private Player handleLootAndXpDistribution(Player attacker) {

		Player playerWithMostDamage = attacker;
		int currentHighestDamage = 0;

		int totalCombatXP = Formulae.combatExperience(this);
		// Melee damagers
		for (int playerID : getCombatDamagers()) {

			final Player p = getWorld().getPlayerID(playerID);
			if (p == null)
				continue;
			final int damageDoneByPlayer = getCombatDamageDoneBy(p);

			if (damageDoneByPlayer > currentHighestDamage) {
				playerWithMostDamage = p;
				currentHighestDamage = damageDoneByPlayer;
			}

			// Give the player their share of the experience.
			int totalXP = (int) (((double) (totalCombatXP) / (double) (getDef().hits)) * (double) (damageDoneByPlayer));

			switch (p.getCombatStyle()) {
				case 0: //CONTROLLED
					for (int x = 0; x < 3; x++) {
						p.incExp(x, totalXP, true);
					}
					break;
				case 1: //AGGRESSIVE
					p.incExp(Skills.STRENGTH, totalXP * 3, true);
					break;
				case 2: //ACCURATE
					p.incExp(Skills.ATTACK, totalXP * 3, true);
					break;
				case 3: //DEFENSIVE
					p.incExp(Skills.DEFENSE, totalXP * 3, true);
					break;
			}
			p.incExp(Skills.HITS, totalXP, true);
		}

		// Ranged damagers
		for (int playerID : getRangeDamagers()) {
			int newXP = 0;
			Player p = getWorld().getPlayerID(playerID);
			int dmgDoneByPlayer = getRangeDamageDoneBy(p);
			if (p == null)
				continue;

			if (dmgDoneByPlayer > currentHighestDamage) {
				playerWithMostDamage = p;
				currentHighestDamage = dmgDoneByPlayer;
			}
			newXP = (int) (((double) (totalCombatXP) / (double) (this.getDef().hits)) * (double) (dmgDoneByPlayer));
			p.incExp(Skills.RANGED, newXP * 4, true);
			ActionSender.sendStat(p, Skills.RANGED);
		}

		// Magic damagers
		for (int playerID : getMageDamagers()) {

			Player p = getWorld().getPlayerID(playerID);

			int dmgDoneByPlayer = getMageDamageDoneBy(p);
			if (p == null)
				continue;

			if (dmgDoneByPlayer > currentHighestDamage) {
				playerWithMostDamage = p;
				currentHighestDamage = dmgDoneByPlayer;
			}
		}
		return playerWithMostDamage;
	}*/
	private Mob handleLootAndXpDistribution(Mob attacker) {

		Mob playerWithMostDamage = attacker;
		int currentHighestDamage = 0;

		int totalCombatXP = Formulae.combatExperience(this);
		// Melee damagers
		for (int playerID : getCombatDamagers()) {

			final Player p = getWorld().getPlayerID(playerID);
			if (p == null)
				continue;
			final int damageDoneByPlayer = getCombatDamageDoneBy(p);

			if (damageDoneByPlayer > currentHighestDamage) {
				playerWithMostDamage = p;
				currentHighestDamage = damageDoneByPlayer;
			}

			// Give the player their share of the experience.
			int totalXP = (int) (((double) (totalCombatXP) / (double) (getDef().hits)) * (double) (damageDoneByPlayer));

			switch (p.getCombatStyle()) {
				case 0: //CONTROLLED
					for (int x = 0; x < 3; x++) {
						p.incExp(x, totalXP, true);
					}
					break;
				case 1: //AGGRESSIVE
					p.incExp(Skills.STRENGTH, totalXP * 3, true);
					break;
				case 2: //ACCURATE
					p.incExp(Skills.ATTACK, totalXP * 3, true);
					break;
				case 3: //DEFENSIVE
					p.incExp(Skills.DEFENSE, totalXP * 3, true);
					break;
			}
			p.incExp(Skills.HITS, totalXP, true);
		}

		// Ranged damagers
		for (int playerID : getRangeDamagers()) {
			int newXP = 0;
			Player p = getWorld().getPlayerID(playerID);
			int dmgDoneByPlayer = getRangeDamageDoneBy(p);
			if (p == null)
				continue;

			if (dmgDoneByPlayer > currentHighestDamage) {
				playerWithMostDamage = p;
				currentHighestDamage = dmgDoneByPlayer;
			}
			newXP = (int) (((double) (totalCombatXP) / (double) (this.getDef().hits)) * (double) (dmgDoneByPlayer));
			p.incExp(Skills.RANGED, newXP * 4, true);
			ActionSender.sendStat(p, Skills.RANGED);
		}

		// Magic damagers
		for (int playerID : getMageDamagers()) {

			Player p = getWorld().getPlayerID(playerID);

			int dmgDoneByPlayer = getMageDamageDoneBy(p);
			if (p == null)
				continue;

			if (dmgDoneByPlayer > currentHighestDamage) {
				playerWithMostDamage = p;
				currentHighestDamage = dmgDoneByPlayer;
			}
		}
		//return playerWithMostDamage;

		Mob npcWithMostDamage = attacker;
		// Melee damagers
		for (int npcID : getCombatDamagers()) {
			int newXP = 0;
			final Npc n = getWorld().getNpcById(npcID);
			if (n == null)
				continue;

			final int dmgDoneByNpc = getCombatDamageDoneBy(n);

			if (dmgDoneByNpc > currentHighestDamage) {
				npcWithMostDamage = n;
				currentHighestDamage = dmgDoneByNpc;
			}

		}

		// Ranged damagers
		for (int npcID : getRangeDamagers()) {
			int newXP = 0;
			Npc n = getWorld().getNpcById(npcID);
			int dmgDoneByNpc = getRangeDamageDoneBy(n);
			if (n == null)
				continue;

			if (dmgDoneByNpc > currentHighestDamage) {
				npcWithMostDamage = n;
				currentHighestDamage = dmgDoneByNpc;
			}
		}

		// Magic damagers
		for (int npcID : getMageDamagers()) {
			int newXP = 0;
			Npc n = getWorld().getNpcById(npcID);

			int dmgDoneByNpc = getMageDamageDoneBy(n);
			if (n == null)
				continue;
		}
		return npcWithMostDamage;
	}

	private Player handleLootAndXpDistribution(Player attacker) {

		Player playerWithMostDamage = attacker;
		int currentHighestDamage = 0;

		int totalCombatXP = Formulae.combatExperience(this);
		// Melee damagers
		for (int playerID : getCombatDamagers()) {

			final Player p = getWorld().getPlayerID(playerID);
			if (p == null)
				continue;
			final int damageDoneByPlayer = getCombatDamageDoneBy(p);

			if (damageDoneByPlayer > currentHighestDamage) {
				playerWithMostDamage = p;
				currentHighestDamage = damageDoneByPlayer;
			}

			// Give the player their share of the experience.
			int totalXP = (int) (((double) (totalCombatXP) / (double) (getDef().hits)) * (double) (damageDoneByPlayer));

			switch (p.getCombatStyle()) {
				case 0: //CONTROLLED
					for (int x = 0; x < 3; x++) {
						//p.incExp(x, totalXP, true);
					}
					break;
				case 1: //AGGRESSIVE
					//p.incExp(Skills.STRENGTH, totalXP * 3, true);
					break;
				case 2: //ACCURATE
					//p.incExp(Skills.ATTACK, totalXP * 3, true);
					break;
				case 3: //DEFENSIVE
					//p.incExp(Skills.DEFENSE, totalXP * 3, true);
					break;
			}
			//p.incExp(Skills.HITS, totalXP, true);
		}

		// Ranged damagers
		for (int playerID : getRangeDamagers()) {
			int newXP = 0;
			Player p = getWorld().getPlayerID(playerID);
			int dmgDoneByPlayer = getRangeDamageDoneBy(p);
			if (p == null)
				continue;

			if (dmgDoneByPlayer > currentHighestDamage) {
				playerWithMostDamage = p;
				currentHighestDamage = dmgDoneByPlayer;
			}
			newXP = (int) (((double) (totalCombatXP) / (double) (this.getDef().hits)) * (double) (dmgDoneByPlayer));
			//p.incExp(Skills.RANGED, newXP * 4, true);
			ActionSender.sendStat(p, Skills.RANGED);
		}

		// Magic damagers
		for (int playerID : getMageDamagers()) {

			Player p = getWorld().getPlayerID(playerID);

			int dmgDoneByPlayer = getMageDamageDoneBy(p);
			if (p == null)
				continue;

			if (dmgDoneByPlayer > currentHighestDamage) {
				playerWithMostDamage = p;
				currentHighestDamage = dmgDoneByPlayer;
			}
		}
		return playerWithMostDamage;
	}

	private Npc handleLootAndXpDistribution(Npc attacker) {
		Npc npcWithMostDamage = attacker;
		int currentHighestDamage = 0;

		// Melee damagers
		for (int npcID : getCombatDamagers()) {

			final Npc n = getWorld().getNpcById(npcID);
			if (n == null)
				continue;
			final int dmgDoneByNpc = getCombatDamageDoneBy(n);

			if (dmgDoneByNpc > currentHighestDamage) {
				npcWithMostDamage = n;
				currentHighestDamage = dmgDoneByNpc;
			}
		}

		// Ranged damagers
		for (int npcID : getRangeDamagers()) {
			int newXP = 0;
			Npc n = getWorld().getNpcById(npcID);
			int dmgDoneByNpc = getRangeDamageDoneBy(n);
			if (n == null)
				continue;

			if (dmgDoneByNpc > currentHighestDamage) {
				npcWithMostDamage = n;
				currentHighestDamage = dmgDoneByNpc;
			}
		}

		// Magic damagers
		for (int npcID : getMageDamagers()) {

			Npc n = getWorld().getNpcById(npcID);

			int dmgDoneByNpc = getMageDamageDoneBy(n);
			if (n == null)
				continue;

			if (dmgDoneByNpc > currentHighestDamage) {
				npcWithMostDamage = n;
				currentHighestDamage = dmgDoneByNpc;
			}
		}
		return npcWithMostDamage;
	}

	public void initializeTalkScript(final Player p) {
		final Npc npc = this;
		//p.setBusyTimer(600);
		getWorld().getServer().getGameEventHandler().add(new ImmediateEvent(getWorld(), "Init Talk Script") {
			@Override
			public void action() {
				getWorld().getServer().getPluginHandler().blockDefaultAction("TalkToNpc", new Object[]{p, npc});
			}
		});
	}

	public void initializeIndirectTalkScript(final Player p) {
		final Npc npc = this;
		//p.setBusyTimer(600);
		getWorld().getServer().getGameEventHandler().add(new ImmediateEvent(getWorld(), "Init Indirect Talk Script") {
			@Override
			public void action() {
				getWorld().getServer().getPluginHandler().blockDefaultAction("IndirectTalkToNpc", new Object[]{p, npc});
			}
		});
	}

	public void remove() {
		this.setLastOpponent(null);
		if (getCombatEvent() != null) {
			getCombatEvent().resetCombat();
		}
		if (!isRemoved() && shouldRespawn && def.respawnTime() > 0) {
			startRespawning();
			teleport(0, 0);
			getWorld().getServer().getGameEventHandler().add(new DelayedEvent(getWorld(), null, def.respawnTime() * 1000, "Respawn NPC") {
				public void run() {
					setRespawning(false);
					teleport(loc.startX, loc.startY);
					getSkills().normalize();

					running = false;
					mageDamagers.clear();
					rangeDamagers.clear();
					combatDamagers.clear();
				}
			});
			setRespawning(true);
		} else if (!shouldRespawn) {
			setUnregistering(true);
		}
	}

	private void startRespawning() {

	}

	public void setBonuses(int armour, int weapon, int aim) {
		this.armourPoints = armour;
		this.weaponAimPoints = aim;
		this.weaponPowerPoints = weapon;
	}

	public void setShouldRespawn(boolean respawn) {
		shouldRespawn = respawn;
	}

	public boolean shouldRespawn() {
		return shouldRespawn;
	}

	public void teleport(int x, int y) {
		setLocation(Point.location(x, y), true);
	}

	@Override
	public String toString() {
		return "Warning! @yel@" + getWorld().getServer().getEntityHandler().getNpcDef(id).getName() + "@whi@";
	}

	public void updatePosition() {
		npcBehavior.tick();
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

	public void setStatus(Action a) {
		status = a;
	}

	public boolean isChasing() {
		return npcBehavior.isChasing();
	}

	public void setChasing(Player player) {
		npcBehavior.setChasing(player);
	}

	public void setChasing(Npc npc) {
		npcBehavior.setChasing(npc);
	}

	public Player getChasedPlayer() {
		return npcBehavior.getChasedPlayer();
	}

	public Npc getChasedNpc() {
		return npcBehavior.getChasedNpc();
	}

	public NpcBehavior getBehavior() {
		return npcBehavior;
	}

	public void setBehavior(NpcBehavior behavior) {
		this.npcBehavior = behavior;
	}

	public void setNPCLoc(NPCLoc loc2) {
		this.loc = loc2;
	}

	public boolean isPlayer() {
		return false;
	}

	public boolean isNpc() {
		return true;
	}

	boolean isRespawning() {
		return isRespawning;
	}

	private void setRespawning(boolean isRespawning) {
		this.isRespawning = isRespawning;
	}

	public void superRemove() {
		super.remove();
	}

	public boolean addDeathListener(NpcLootEvent event) {
		return deathListeners.add(event);
	}

	public void setExecutedAggroScript(boolean executed) {
		this.executedAggroScript = executed;
	}

	public boolean executedAggroScript() {
		return this.executedAggroScript;
	}

	public static boolean handleRingOfAvarice(Player p, Item item) {
		int slot = -1;
		if (Functions.isWielding(p, ItemId.RING_OF_AVARICE.id())) {
			ItemDefinition itemDef = p.getWorld().getServer().getEntityHandler().getItemDef(item.getID());
			if (itemDef != null && itemDef.isStackable()) {
				if (p.getInventory().hasInInventory(item.getID())) {
					p.getInventory().add(item);
					return true;
				} else if (p.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB && (slot = p.getEquipment().hasEquipped(item.getID())) != -1) {
					Item equipped = p.getEquipment().get(slot);
					equipped.setAmount(equipped.getAmount() + item.getAmount());
					p.getEquipment().equip(slot, equipped);
					return true;
				} else {
					if (p.getInventory().getFreeSlots() > 0) {
						p.getInventory().add(item);
						return true;
					} else {
						p.message("Your ring of Avarice tried to activate, but your inventory was full.");
						return false;
					}
				}
			}
		}
		return false;
	}
}
