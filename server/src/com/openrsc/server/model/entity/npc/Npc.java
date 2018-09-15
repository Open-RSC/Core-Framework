package com.openrsc.server.model.entity.npc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.content.achievement.AchievementSystem;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.rsc.ImmediateEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemDropDef;
import com.openrsc.server.external.NPCDef;
import com.openrsc.server.external.NPCLoc;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.PluginHandler;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

public class Npc extends Mob {

	/**
	 * World instance
	 */
	protected static final World world = World.getWorld();

	private int armourPoints = 1;

	/**
	 * Holds players that did damage with combat
	 */
	private Map<Integer, Integer> combatDamagers = new HashMap<Integer, Integer>();
	/**
	 * The definition of this npc
	 */
	protected NPCDef def;
	/**
	 * The location of this npc
	 */
	protected NPCLoc loc;

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

	private int weaponAimPoints = 1;
	private int weaponPowerPoints = 1;

	public Npc(int id, int x, int y) {
		this(new NPCLoc(id, x, y, x - 5, x + 5, y - 5, y + 5));
	}
	
	public Npc(int id, int x, int y, int radius) {
		this(new NPCLoc(id, x, y, x - radius, x + radius, y - radius, y + radius));
	}

	public Npc(int id, int startX, int startY, int minX, int maxX, int minY, int maxY) {
		this(new NPCLoc(id, startX, startY, minX, maxX, minY, maxY));
	}

	public Npc(NPCLoc loc) {
		for (int i : Constants.GameServer.UNDEAD_NPCS) {
			if (loc.getId() == i) {
				setAttribute("isUndead", true);
			}
		}
		for (int i : Constants.GameServer.ARMOR_NPCS) {
			if (loc.getId() == i) {
				setAttribute("hasArmor", true);
			}
		}
		def = EntityHandler.getNpcDef(loc.getId());
		if (def == null) {
			throw new NullPointerException("Npc definition is null - " + loc.getId());
		}

		this.npcBehavior = new NpcBehavior(this);
		this.loc = loc;
		super.setID(loc.getId());
		super.setLocation(Point.location(loc.startX(), loc.startY()), true);

		getSkills().setLevelTo(Skills.ATTACK, def.getAtt());
		getSkills().setLevelTo(Skills.DEFENCE, def.getDef());
		getSkills().setLevelTo(Skills.STRENGTH, def.getStr());
		getSkills().setLevelTo(Skills.HITPOINTS, def.getHits());

		Server.getServer().getGameEventHandler().add(statRestorationEvent);
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
	public int getCombatDamageDoneBy(Player p) {
		if (p == null) {
			return 0;
		}
		if (!combatDamagers.containsKey(p.getDatabaseID())) {
			return 0;
		}
		int dmgDone = combatDamagers.get(p.getDatabaseID());
		return (dmgDone > this.getDef().getHits() ? this.getDef().getHits() : dmgDone);
	}

	/**
	 * Iterates over combatDamagers map and returns the keys
	 * 
	 * @return
	 */
	public ArrayList<Integer> getCombatDamagers() {
		return new ArrayList<Integer>(combatDamagers.keySet());
	}

	public int getCombatStyle() {
		return 0;
	}

	public NPCDef getDef() {
		return EntityHandler.getNpcDef(getID());
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
	public int getMageDamageDoneBy(Player p) {
		if (p == null || !mageDamagers.containsKey(p.getDatabaseID())) {
			return 0;
		}
		int dmgDone = mageDamagers.get(p.getDatabaseID());
		return (dmgDone > this.getDef().getHits() ? this.getDef().getHits() : dmgDone);
	}

	/**
	 * Iterates over mageDamagers map and returns the keys
	 * 
	 * @return
	 */
	public ArrayList<Integer> getMageDamagers() {
		return new ArrayList<Integer>(mageDamagers.keySet());
	}

	/**
	 * Range damage done by player p
	 * 
	 * @param p
	 * @return
	 */
	public int getRangeDamageDoneBy(Player p) {
		if (p == null || !rangeDamagers.containsKey(p.getDatabaseID())) {
			return 0;
		}
		int dmgDone = rangeDamagers.get(p.getDatabaseID());
		return (dmgDone > this.getDef().getHits() ? this.getDef().getHits() : dmgDone);
	}

	/**
	 * Iterates over rangeDamagers map and returns the keys
	 * 
	 * @return
	 */
	public ArrayList<Integer> getRangeDamagers() {
		return new ArrayList<Integer>(rangeDamagers.keySet());
	}

	public int getWeaponAimPoints() {
		return weaponAimPoints;
	}

	public int getWeaponPowerPoints() {
		return weaponPowerPoints;
	}

	/**
	 * Distributes the XP from this monster and the loot
	 * 
	 * @param attacker
	 *            the person that "finished off" the npc
	 * @return the player who did the most damage / should get the loot
	 */
	public Player handleLootAndXpDistribution(Player attacker) {

		Player playerWithMostDamage = attacker;
		int currentHighestDamage = 0;

		int totalCombatXP = Formulae.combatExperience(this);
		// Melee damagers
		for (int playerID : getCombatDamagers()) {

			final Player p = World.getWorld().getPlayerID(playerID);
			if (p == null)
				continue;
			final int damageDoneByPlayer = getCombatDamageDoneBy(p);

			if (damageDoneByPlayer > currentHighestDamage) {
				playerWithMostDamage = p;
				currentHighestDamage = damageDoneByPlayer;
			}

			// Give the player their share of the experience.
			int totalXP = (int)(((double)(totalCombatXP) / (double)(getDef().hits)) * (double)(damageDoneByPlayer));

			switch (p.getCombatStyle()) {
			case 0:
				for (int x = 0; x < 3; x++) {
					p.incExp(x, totalXP, true);
				}
				break;
			case 1:
				p.incExp(2, totalXP * 3, true);
				break;
			case 2:
				p.incExp(0, totalXP * 3, true);
				break;
			case 3:
				p.incExp(1, totalXP * 3, true);
				break;
			}
			p.incExp(3, totalXP, true);
		}

		// Ranged damagers
		for (int playerID : getRangeDamagers()) {
			int newXP = 0;
			Player p = World.getWorld().getPlayerID(playerID);
			int dmgDoneByPlayer = getRangeDamageDoneBy(p);
			if (p == null)
				continue;

			if (dmgDoneByPlayer > currentHighestDamage) {
				playerWithMostDamage = p;
				currentHighestDamage = dmgDoneByPlayer;
			}
			newXP = (totalCombatXP / this.getDef().hits) * dmgDoneByPlayer;
			p.incExp(4, newXP * 4, true);
			ActionSender.sendStat(p, 4);
		}

		// Magic damagers
		for (int playerID : getMageDamagers()) {

			Player p = World.getWorld().getPlayerID(playerID);

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

	public void initializeTalkScript(final Player p) {
		final Npc npc = this;
		//p.setBusyTimer(600);
		Server.getServer().getGameEventHandler().add(new ImmediateEvent() {
			@Override
			public void action() {
				PluginHandler.getPluginHandler().blockDefaultAction("TalkToNpc", new Object[] { p, npc });
			}
		});

	}

	public void killedBy(Mob mob) {
		this.cure();
		Player owner = mob instanceof Player ? (Player) mob : null;
		if (owner != null) {
			ActionSender.sendSound(owner, "victory");
			AchievementSystem.checkAndIncSlayNpcTasks(owner, this);

			owner = handleLootAndXpDistribution((Player) mob);

			ItemDropDef[] drops = def.getDrops();

			int total = 0;
			for (ItemDropDef drop : drops) {
				total += drop.getWeight();
			}
			//
			int hit = DataConversions.random(0, total);
			total = 0;
			
			for (ItemDropDef drop : drops) {
				if (drop == null) {
					continue;
				}
				if (drop.getWeight() == 0 && drop.getID() != -1) {
					GroundItem groundItem = new GroundItem(drop.getID(), getX(), getY(), drop.getAmount(), owner);
					groundItem.setAttribute("npcdrop", true);
					world.registerItem(groundItem);
					continue;
				}
				if (hit >= total && hit < (total + drop.getWeight())) {

					if (drop.getID() != -1) {
						if (EntityHandler.getItemDef(drop.getID()).isMembersOnly()
								&& !Constants.GameServer.MEMBER_WORLD) {
							continue;
						}

						if (!EntityHandler.getItemDef(drop.getID()).isStackable()) {

							int dropID  = drop.getID();
							int dropAmt = drop.getAmount();

							// Rare Drop Table
							if (drop.getID() == 160) {
								dropID = Formulae.calculateRareDrop();
								dropAmt = 1;
							}

							// Herb Drop Table
							else if (drop.getID() == 165) {
								dropID = Formulae.calculateHerbDrop();
							}

							else {

							}

							GroundItem groundItem = new GroundItem(dropID, getX(), getY(), 1, owner);
							groundItem.setAttribute("npcdrop", true);
							for (int count = 0; count < dropAmt; count++)
								world.registerItem(groundItem);
						} else {
							int amount = drop.getAmount();
							GroundItem groundItem = new GroundItem(drop.getID(), getX(), getY(), amount, owner);
							groundItem.setAttribute("npcdrop", true);

							world.registerItem(groundItem);
						}
					}
					break;
				}
				total += drop.getWeight();
			}
		}
		remove();
	}

	public void remove() {
		if (getCombatEvent() != null) {
			getCombatEvent().resetCombat();
		}
		if (!isRemoved() && shouldRespawn && def.respawnTime() > 0) {
			startRespawning();
			teleport(0, 0);
			Server.getServer().getEventHandler().add(new DelayedEvent(null, def.respawnTime() * 1000) {
				public void run() {
					setRespawning(false);
					teleport(loc.startX, loc.startY);
					getSkills().normalize();

					matchRunning = false;
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

	public void startRespawning() {

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
		return "[NPC:" + EntityHandler.getNpcDef(id).getName() + "]";
	}

	private NpcBehavior npcBehavior;

	public void updatePosition() {

		npcBehavior.tick();
		super.updatePosition();
	}

	public boolean isChasing() {
		return npcBehavior.isChasing();
	}

	public Player getChasedPlayer() {
		return npcBehavior.getChasedPlayer();
	}

	public void setChasing(Player player) {
		npcBehavior.setChasing(player);
	}

	public void setBehavior(NpcBehavior behavior) {
		this.npcBehavior = behavior;
	}

	public NpcBehavior getBehavior() {
		return npcBehavior;
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

	public boolean isRespawning() {
		return isRespawning;
	}

	public void setRespawning(boolean isRespawning) {
		this.isRespawning = isRespawning;
	}

	public void superRemove() {
		super.remove();
	}
}
