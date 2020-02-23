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
	private Map<Integer, Integer> combatDamagers = new HashMap<Integer, Integer>();
	/**
	 * Holds players that did damage with mage
	 */
	private Map<Integer, Integer> mageDamagers = new HashMap<Integer, Integer>();
	/**
	 * Holds players that did damage with range
	 */
	private Map<Integer, Integer> rangeDamagers = new HashMap<Integer, Integer>();

	public Npc(final World world, final int id, final int x, final int y) {
		this(world, new NPCLoc(id, x, y, x - 5, x + 5, y - 5, y + 5));
	}

	public Npc(final World world, final int id, final int x, final int y, final int radius) {
		this(world, new NPCLoc(id, x, y, x - radius, x + radius, y - radius, y + radius));
	}

	public Npc(final World world, final int id, final int startX, final int startY, final int minX, final int maxX, final int minY, final int maxY) {
		this(world, new NPCLoc(id, startX, startY, minX, maxX, minY, maxY));
	}

	protected Npc(final World world) {
		// For PKbots only
		super(world);
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
		this.setNpcBehavior(new NpcBehavior(this));
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

		getWorld().getServer().getGameEventHandler().add(getStatRestorationEvent());
	}

	/**
	 * Adds combat damage done by a player
	 *
	 * @param p
	 * @param damage
	 */
	public void addCombatDamage(final Player p, final int damage) {
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
	public void addMageDamage(final Player p, final int damage) {
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
	public void addRangeDamage(final Player p, final int damage) {
		if (rangeDamagers.containsKey(p.getDatabaseID())) {
			rangeDamagers.put(p.getDatabaseID(), rangeDamagers.get(p.getDatabaseID()) + damage);
		} else {
			rangeDamagers.put(p.getDatabaseID(), damage);
		}
	}

	public void displayNpcTeleportBubble(final int x, final int y) {
		for (Object o : getViewArea().getPlayersInView()) {
			Player p = ((Player) o);
			ActionSender.sendTeleBubble(p, x, y, false);
		}
		setTeleporting(true);
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
	private int getCombatDamageDoneBy(final Player p) {
		if (p == null) {
			return 0;
		}
		if (!combatDamagers.containsKey(p.getDatabaseID())) {
			return 0;
		}
		int dmgDone = combatDamagers.get(p.getDatabaseID());
		return (dmgDone > this.getDef().getHits() ? this.getDef().getHits() : dmgDone);
	}

	private int getCombatDamageDoneBy(final Npc n) {
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
	private int getMageDamageDoneBy(final Player p) {
		if (p == null || !mageDamagers.containsKey(p.getDatabaseID())) {
			return 0;
		}
		int dmgDone = mageDamagers.get(p.getDatabaseID());
		return (dmgDone > this.getDef().getHits() ? this.getDef().getHits() : dmgDone);
	}

	private int getMageDamageDoneBy(final Npc n) {
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
	private int getRangeDamageDoneBy(final Player p) {
		if (p == null || !rangeDamagers.containsKey(p.getDatabaseID())) {
			return 0;
		}
		int dmgDone = rangeDamagers.get(p.getDatabaseID());
		return (dmgDone > this.getDef().getHits() ? this.getDef().getHits() : dmgDone);
	}

	private int getRangeDamageDoneBy(final Npc n) {
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

	public int getArmourPoints() {
		return 0;
	}

	public int getWeaponAimPoints() {
		return 0;
	}

	public int getWeaponPowerPoints() {
		return 0;
	}

	public boolean stateIsInvisible() { return false; };
	public boolean stateIsInvulnerable() { return false; };

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

				//KDB Specific RDT
				if (getWorld().getServer().getConfig().WANT_CUSTOM_SPRITES) {
					if (this.getID() == NpcId.KING_BLACK_DRAGON.id()) {
						if (getWorld().kbdTable.rollAccess(this.getID(), owner.getEquipment().hasEquipped(ItemId.RING_OF_WEALTH.id()))) {
							Item kbdSpecificLoot = getWorld().kbdTable.rollItem(owner.getEquipment().hasEquipped(ItemId.RING_OF_WEALTH.id()), owner);
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
				}

				//Determine if the RDT is hit first
				boolean rdtHit = false;
				Item rare = null;
				if (getWorld().getServer().getConfig().WANT_NEW_RARE_DROP_TABLES && mob.isPlayer()) {
					if (getWorld().standardTable.rollAccess(this.getID(), ((Player)mob).getEquipment().hasEquipped(com.openrsc.server.constants.ItemId.RING_OF_WEALTH.id()))) {
						rdtHit = true;
						rare = getWorld().standardTable.rollItem(((Player)mob).getEquipment().hasEquipped(com.openrsc.server.constants.ItemId.RING_OF_WEALTH.id()), ((Player) mob));
					} else if (getWorld().gemTable.rollAccess(this.getID(), ((Player)mob).getEquipment().hasEquipped(com.openrsc.server.constants.ItemId.RING_OF_WEALTH.id()))) {
						rdtHit = true;
						rare = getWorld().gemTable.rollItem(((Player)mob).getEquipment().hasEquipped(com.openrsc.server.constants.ItemId.RING_OF_WEALTH.id()), ((Player) mob));
					}
				}

				if (rare != null) {
					if (!handleRingOfAvarice(owner, rare)) {
						GroundItem groundItem = new GroundItem(owner.getWorld(), rare.getCatalogId(), getX(), getY(), rare.getAmount(), owner);
						groundItem.setAttribute("npcdrop", true);
						getWorld().registerItem(groundItem);
					}
					try {
						getWorld().getServer().getDatabase().addDropLog(
							owner, this, rare.getCatalogId(), rare.getAmount());
					} catch (final GameDatabaseException ex) {
						LOGGER.catching(ex);
					}
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
						if (drop.getID() == com.openrsc.server.constants.ItemId.UNHOLY_SYMBOL_MOULD.id()) {
							if (owner.getQuestStage(Quests.OBSERVATORY_QUEST) > -1)
								continue;

							if (owner.getWorld().getServer().getConfig().WANT_CUSTOM_QUESTS)
								if (owner.getCache().hasKey("want_unholy_symbol_drops") &&
									!owner.getCache().getBoolean("want_unholy_symbol_drops"))
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
									try {
										getWorld().getServer().getDatabase().addDropLog(owner, this, dropID, amount);
									} catch (final GameDatabaseException ex) {
										LOGGER.catching(ex);
									}
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
										if (((Player)mob).getEquipment().hasEquipped(com.openrsc.server.constants.ItemId.RING_OF_SPLENDOR.id())) {
											amount += Formulae.getSplendorBoost(amount);
											((Player) mob).message("Your ring of splendor shines brightly!");
										}
									}

									try {
										getWorld().getServer().getDatabase().addDropLog(owner, this, dropID, amount);
									} catch (final GameDatabaseException ex) {
										LOGGER.catching(ex);
									}

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
				if (getWorld().getServer().getConfig().WANT_NEW_RARE_DROP_TABLES && mob.isPlayer() && owner.isPlayer()) {
					if (getWorld().standardTable.rollAccess(this.getID(), ((Player)mob).getEquipment().hasEquipped(com.openrsc.server.constants.ItemId.RING_OF_WEALTH.id()))) {
						rdtHit = true;
						rare = getWorld().standardTable.rollItem(((Player)mob).getEquipment().hasEquipped(com.openrsc.server.constants.ItemId.RING_OF_WEALTH.id()), ((Player) mob));
					} else if (getWorld().gemTable.rollAccess(this.getID(), ((Player)mob).getEquipment().hasEquipped(com.openrsc.server.constants.ItemId.RING_OF_WEALTH.id()))) {
						rdtHit = true;
						rare = getWorld().gemTable.rollItem(((Player)mob).getEquipment().hasEquipped(com.openrsc.server.constants.ItemId.RING_OF_WEALTH.id()), ((Player) mob));
					}
				}

				if (rare != null) {
					if(!owner.isNpc()){
						if (!handleRingOfAvarice((Player) mob, rare)) {
								GroundItem groundItem = new GroundItem(owner.getWorld(), rare.getCatalogId(), getX(), getY(), rare.getAmount());
								groundItem.setAttribute("npcdrop", true);
								getWorld().registerItem(groundItem);
						}
					}
				}


				ItemDropDef[] drops = def.getDrops();

				int total = 0;
				int weightTotal = 0;
				for (ItemDropDef drop : drops) {
					total += drop.getWeight();
					weightTotal += drop.getWeight();
					if (drop.getWeight() == 0 && drop.getID() != -1) {
						if(!owner.isNpc()){
							if (!handleRingOfAvarice((Player) mob, new Item(drop.getID(), drop.getAmount()))) {
								GroundItem groundItem = new GroundItem(owner.getWorld(), drop.getID(), getX(), getY(), drop.getAmount());
								groundItem.setAttribute("npcdrop", true);
								getWorld().registerItem(groundItem);
							}
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
										if(!owner.isNpc()){
											if (drop.getID() == com.openrsc.server.constants.ItemId.UNCUT_SAPPHIRE.id()) {
												dropID = Formulae.calculateGemDrop((Player) mob);
												amount = 1;
											}
										}

										// Herb Drop Table
										else if (!owner.isNpc() && drop.getID() == com.openrsc.server.constants.ItemId.UNIDENTIFIED_GUAM_LEAF.id()) {
											dropID = Formulae.calculateHerbDrop();
										}

										if (dropID != com.openrsc.server.constants.ItemId.NOTHING.id() && getWorld().getServer().getEntityHandler().getItemDef(dropID).isMembersOnly() && !getWorld().getServer().getConfig().MEMBER_WORLD) {
											continue;
										} else if (dropID != com.openrsc.server.constants.ItemId.NOTHING.id()) {
											if(!owner.isNpc()){
												if (!handleRingOfAvarice((Player) mob, new Item(drop.getID(), drop.getAmount()))) {
													groundItem = new GroundItem(owner.getWorld(), dropID, getX(), getY(), 1);
													groundItem.setAttribute("npcdrop", true);
													getWorld().registerItem(groundItem);
												}
											}
										}
									}
								} else {
									// Gold Drops
									if (drop.getID() == com.openrsc.server.constants.ItemId.COINS.id()) {
										amount = Formulae.calculateGoldDrop(
											GoldDrops.drops.getOrDefault(this.getID(), new int[]{1})
										);
										if(!owner.isNpc()){
											if (((Player)mob).getEquipment().hasEquipped(com.openrsc.server.constants.ItemId.RING_OF_SPLENDOR.id())) {
												amount += Formulae.getSplendorBoost(amount);
												((Player) mob).message("Your ring of splendor shines brightly!");
											}
										}
									}

									if(!owner.isNpc()){
										if (!handleRingOfAvarice((Player) mob, new Item(drop.getID(), amount))) {
											GroundItem groundItem = new GroundItem(owner.getWorld(), dropID, getX(), getY(), amount);
												getWorld().registerItem(groundItem);
												groundItem.setAttribute("npcdrop", true);
										}
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
	private Mob handleLootAndXpDistribution(final Mob attacker) {

		Mob playerWithMostDamage = attacker;
		int currentHighestDamage = 0;

		int totalCombatXP = Formulae.combatExperience(this, getDef().roundMode);
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
				case Skills.CONTROLLED_MODE: //CONTROLLED
					for (int x = 0; x < 3; x++) {
						p.incExp(x, totalXP, true);
					}
					break;
				case Skills.AGGRESSIVE_MODE: //AGGRESSIVE
					p.incExp(Skills.STRENGTH, totalXP * 3, true);
					break;
				case Skills.ACCURATE_MODE: //ACCURATE
					p.incExp(Skills.ATTACK, totalXP * 3, true);
					break;
				case Skills.DEFENSIVE_MODE: //DEFENSIVE
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

	private Player handleLootAndXpDistribution(final Player attacker) {

		Player playerWithMostDamage = attacker;
		int currentHighestDamage = 0;

		int totalCombatXP = Formulae.combatExperience(this, 0);
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
				case Skills.CONTROLLED_MODE: //CONTROLLED
					for (int x = 0; x < 3; x++) {
						//p.incExp(x, totalXP, true);
					}
					break;
				case Skills.AGGRESSIVE_MODE: //AGGRESSIVE
					//p.incExp(Skills.STRENGTH, totalXP * 3, true);
					break;
				case Skills.ACCURATE_MODE: //ACCURATE
					//p.incExp(Skills.ATTACK, totalXP * 3, true);
					break;
				case Skills.DEFENSIVE_MODE: //DEFENSIVE
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

	private Npc handleLootAndXpDistribution(final Npc attacker) {
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
				getWorld().getServer().getPluginHandler().handlePlugin(npc, "TalkToNpc", new Object[]{p, npc});
			}
		});
	}

	public void initializeIndirectTalkScript(final Player p) {
		final Npc npc = this;
		//p.setBusyTimer(600);
		getWorld().getServer().getGameEventHandler().add(new ImmediateEvent(getWorld(), "Init Indirect Talk Script") {
			@Override
			public void action() {
				getWorld().getServer().getPluginHandler().handlePlugin(npc, "IndirectTalkToNpc", new Object[]{p, npc});
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
		return "[NPC:" + getDef().getName() + "]";
	}

	public boolean isPkBot() {
		return getDef().isPkBot() && this instanceof PkBot;
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

	boolean isRespawning() {
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

	public static boolean handleRingOfAvarice(final Player p, final Item item) {
		int slot = -1;
		if (p.getEquipment().hasEquipped(ItemId.RING_OF_AVARICE.id())) {
			ItemDefinition itemDef = p.getWorld().getServer().getEntityHandler().getItemDef(item.getCatalogId());
			if (itemDef != null && itemDef.isStackable()) {
				if (p.getInventory().hasInInventory(item.getCatalogId())) {
					p.getInventory().add(item);
					return true;
				} else if (p.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB && (slot = p.getEquipment().searchEquipmentForItem(item.getCatalogId())) != -1) {
					Item equipped = p.getEquipment().get(slot);
					equipped.setAmount(equipped.getAmount() + item.getAmount());
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
