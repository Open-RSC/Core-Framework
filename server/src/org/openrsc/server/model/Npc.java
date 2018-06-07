package org.openrsc.server.model;

import java.util.ArrayList;
import java.util.HashMap;

import com.runescape.entity.attribute.DropItemAttr;

import org.openrsc.server.entityhandling.EntityHandler;
import org.openrsc.server.entityhandling.defs.NPCDef;
import org.openrsc.server.entityhandling.defs.extras.ItemDropDef;
import org.openrsc.server.entityhandling.locs.NPCLoc;
import org.openrsc.server.event.DelayedEvent;
import org.openrsc.server.event.FightEvent;
import org.openrsc.server.event.NpcAggressionEvent;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.ErrorLog;
import org.openrsc.server.logging.model.eventLog;
import org.openrsc.server.states.Action;
import org.openrsc.server.states.CombatState;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.util.Formulae;


public class Npc extends Mob {

	private NPCLoc loc;
	private NPCDef def;
	private int curHits;
	public int curAttack;
	public int curDefense;
	public int curStrength;
	public int killCount = 0;
	private DelayedEvent timeout = null;
	private Player blocker = null;
	private boolean shouldRespawn = true, movementLock = false;
	private NpcAggressionEvent aggression = null;
	private ArrayList<Player> rangers = new ArrayList<Player>();
	
	public boolean hasPlayersInZone() {
		for (Zone zone : updateZone) {
			if (zone.getPlayers().size() > 0)
				return true;
		}
		return false;
	}
	
	public boolean isAggressive() {
		return aggression != null;
	}

	public void removeAggression() {
		aggression = null;
	}
	
	public void resetAggression() {
		if (aggression != null) {
			aggression.stop();
			aggression = null;
		}
		resetPath();
	}

	public void setAggressive(final Player player) {
		World.getDelayedEventHandler().add(new NpcAggressionEvent(player, this));
	}

	public void setRespawn(boolean respawn) {
		shouldRespawn = respawn;
	}
	
	public void blockedBy(Player player) {
		blocker = player;
		player.setNpc(this);
		setBusy(true);
  		timeout = new DelayedEvent(null, 120000) {
  			public void run() {
  				unblock();
  				running = false;
  			}
  		};
  		World.getDelayedEventHandler().add(timeout);
	}

	public void unblock() {
		if (blocker != null) {
			blocker.setNpc(null);
			blocker = null;
		}
		if (timeout != null) {
			setBusy(false);
			timeout.stop();
			timeout = null;
		}
	}

	public Npc(NPCLoc loc, int sprite)
	{
		super(sprite);
		try {
			def = EntityHandler.getNpcDef(loc.getId());
			curHits = def.getHits();
			
			this.curAttack = def.getAtt();
			curDefense = def.getDef();
			curStrength = def.getStr();
			this.loc = loc;
			super.setID(loc.getId());
			super.setLocation(Point.location(loc.startX(), loc.startY()), true);
			super.setCombatLevel(Formulae.getCombatLevel(def.getAtt(), def.getDef(), def.getStr(), def.getHits(), 0, 0, 0));
		} catch(Exception ex) {ex.printStackTrace();System.exit(0);}		
	}
	
	public Npc(NPCLoc loc) {
		this(loc, 1);
	}
	
	public Npc(int id, int startX, int startY, int minX, int maxX, int minY, int maxY, int direction)
	{
		super(direction);
		def = EntityHandler.getNpcDef(id);
		curHits = def.getHits();
		this.curAttack = def.getAtt();
		curDefense = def.getDef();
		curStrength = def.getStr();
		this.loc = new NPCLoc(id, startX, startY, minX, maxX, minY, maxY);
		super.setID(id);
		super.setLocation(Point.location(startX, startY), true);
		super.setCombatLevel(Formulae.getCombatLevel(def.getAtt(), def.getDef(), def.getStr(), def.getHits(), 0, 0, 0));
	}
	
	public Npc(int id, int startX, int startY, int minX, int maxX, int minY, int maxY) {
		this(id, startX, startY, minX, maxX, minY, maxY, 1);
	}
	
	public Npc(int id, int startX, int startY, int minX, int maxX, int minY, int maxY, boolean lock) {
		def = EntityHandler.getNpcDef(id);
		curHits = def.getHits();
		this.curAttack = def.getAtt();
		curDefense = def.getDef();
		curStrength = def.getStr();
		this.loc = new NPCLoc(id, startX, startY, minX, maxX, minY, maxY);
		super.setID(id);
		super.setLocation(Point.location(startX, startY), true);
		super.setCombatLevel(Formulae.getCombatLevel(def.getAtt(), def.getDef(), def.getStr(), def.getHits(), 0, 0, 0));
		this.movementLock = lock;
	}
	
	private boolean scriptScope;
	
	public boolean isScriptScope()
	{
		return scriptScope;
	}
	
	public Npc(int id, int x, int y)
	{
		this(id, x, y, x, y, x, y);
		scriptScope = true;
	}

	public void unconditionalRemove() {
		resetAggression();
		World.unregisterEntity(this);
		removed = true;
	}
	
	public void remove() {
		resetAggression();
		World.unregisterEntity(this);
		if (!removed && shouldRespawn && def.respawnTime() > 0) {
			World.getDelayedEventHandler().add(new DelayedEvent(null, def.respawnTime() * 1000) {
				public void run() {
					World.registerEntity(new Npc(loc));
					running = false;
				}
			});
		}
		removed = true;
	}

	protected HashMap<Player, Integer> totalDamageTable = new HashMap<Player, Integer>(), rangeDamageTable = new HashMap<Player, Integer>(), meleeDamageTable = new HashMap<Player, Integer>();
	
	public void updateKillStealing(Player player, int damage, int attackType) {
		if (totalDamageTable.containsKey(player))
				totalDamageTable.put(player, (totalDamageTable.get(player) + damage));
		else
			totalDamageTable.put(player, damage);
		switch (attackType) {
			case 0:
				if (meleeDamageTable.containsKey(player))
					meleeDamageTable.put(player, (meleeDamageTable.get(player) + damage));
				else
					meleeDamageTable.put(player, damage);
			break;
			
			case 1:
				if (rangeDamageTable.containsKey(player))
					rangeDamageTable.put(player, (rangeDamageTable.get(player) + damage));
				else
					rangeDamageTable.put(player, damage);
			break;
		}
	}
	
	public void killedBy(Npc npc) {
		Mob opponent = super.getOpponent();
		resetCombat(CombatState.LOST);
		if (opponent != null)
			opponent.resetCombat(CombatState.WON);
		this.remove();
		return;
	}

	public void killedBy(Player player) {
		Mob opponent = super.getOpponent();
		if (opponent != null)
			opponent.resetCombat(CombatState.WON);
		for (Player p : rangers)
			p.resetRange();
		resetCombat(CombatState.LOST);
		remove();
		if (totalDamageTable.get(player) != null) {
			Player winner = player;
			Player[] owners = null;
			
			for (Player p : totalDamageTable.keySet()) {
				if (p != null) {
					if (totalDamageTable.get(winner) < totalDamageTable.get(p))
						winner = p;
				}
			}
			
			owners = new Player[] { winner };
			
			int total = 0;
			for (ItemDropDef drop : def.getDrops()) {				
				total += drop.getWeight();
			}
			int hit = DataConversions.random(0, total);
			total = 0;
			
			for(ItemDropDef drop : def.getDrops()) {
				if(drop != null && drop.id != -1) {
				if (drop.weight == 0) {
					World.registerEntity(new Item(drop.getID(), getX(), getY(), drop.getAmount(), owners));
					continue;
				}
				if (hit >= total && hit < (total + drop.getWeight()))
					World.registerEntity(new Item(drop.getID(), getX(), getY(), (EntityHandler.getItemDef(drop.getID()).isStackable() && winner.isSub() ? drop.getAmount() *2 : drop.getAmount()), owners));

				total += drop.getWeight();
				}
			}
			
			DropItemAttr attr = attr(DropItemAttr.class);
			if (attr != null) {
				attr.onDeath(player);
			}
			
			switch (id) {

				case 19: // Tutorial Island Rat
				if (player.getLocation().onTutorialIsland())
				{
					Quest tutorialIsland = player.getQuest(100);
					if (tutorialIsland != null)
					{
						if (tutorialIsland.getStage() == 3)
						{
							player.sendMessage("You have successfully killed the rat.");
							player.sendMessage("Speak to the combat instructor for furhter instructions");
							player.incQuestCompletionStage(100);
						}
					}
				}
				break;
				
				// make sure the player is on the right stage...
				case 216:
					if(player.getQuest(19) != null && player.getQuest(19).getStage() == 3)
					{
						player.incQuestCompletionStage(19);
						player.sendMessage("You can now cut your branch.");
					}
				break;
				
				case 25:
					Quest phoenix = winner.getQuest(52);
					if(winner.getInventory().countId(49) == 0) {
						if(phoenix != null) {
							if(!phoenix.finished()) {
								World.registerEntity(new Item(49, getX(), getY(), 1, winner));
							}
						}
					}
				break;
				
				case 47: //Rat (Red Key)
					if(Formulae.withinBounds(getX(), getY(), 338, 348, 626, 636)) {
						if(Formulae.rand(0, 5) == 0) {
							World.registerEntity(new Item(390, getX(), getY(), 1, winner));
						}
					}
				break;
				
				case 53:
					if(Formulae.withinBounds(getX(), getY(), 345, 350,1570, 1575))
						if(Formulae.rand(0, 3) == 0)
							World.registerEntity(new Item(391, getX(), getY(), 1, winner));
				break;
				
				case 45:
					if (Formulae.withinBounds(getX(), getY(), 341, 350, 2514, 2519))
						if (Formulae.rand(0, 4) == 0)
							World.registerEntity(new Item(392, getX(), getY(), 1, winner));
				break;
				
				case 68:
					if (Formulae.withinBounds(getX(), getY(), 343, 345, 3467, 3470))
						if (Formulae.rand(0, 2) == 0)
							World.registerEntity(new Item(393, getX(), getY(), 1, winner));
				break;
				
				case 182:
					if(Formulae.withinBounds(getX(), getY(), 345, 347, 3462, 3467))
						World.registerEntity(new Item(394, getX(), getY(), 1, winner));
				break;
				
				case 181:
					if(Formulae.withinBounds(getX(), getY(), 340, 348, 3460, 3465))
						World.registerEntity(new Item(395, getX(), getY(), 1, winner));
				break;
				
				case 192:
					Quest dragonslayer = winner.getQuest(17);
					if (dragonslayer != null)
						if (!dragonslayer.finished())
							if (dragonslayer.getStage() > 0)
								if (winner.getInventory().countId(416) == 0 && winner.getInventory().countId(415) == 0)
									World.registerEntity(new Item(416, getX(), getY(), 1, winner));
				break;
				
				case 196: // Elvarg
					Quest q = player.getQuest(17);
					if (q != null) {
						if (q.getStage() == 3 && !q.finished()) {
							player.finishQuest(17);
							player.sendQuestPointUpdate();
							player.sendMessage("You have completed the Dragon Slayer!");
							player.sendMessage("You have now earned the right to wear Rune Platemail armor.");
							player.sendMessage("@gre@You have gained " + q.getQuestPoints() + " quest points!");
							player.teleport(411, 3480, false);
							Logger.log(new eventLog(player.getUsernameHash(), player.getAccount(), player.getIP(), DataConversions.getTimeStamp(), "<strong>" + player.getUsername() + "</strong>" + " has completed the <span class=\"recent_quest\">Dragon Slayer</span> quest!"));
						}
					}
				break;
				
				case 259:
							
					if(Formulae.withinBounds(getX(), getY(), 461, 672, 464, 675))
						World.registerEntity(new Item(583, getX(), getY(), 1, winner));
					
					Npc Grip = new Npc(259, 463, 678, 459, 469, 672, 680, true);
					World.registerEntity(Grip);
					
				break;
			}
			for (Player p : meleeDamageTable.keySet()) {
				if (p != null) {
					if(!p.isDueling()) {
						int exp;
						float partialExp = DataConversions.roundUp(Formulae.combatExperience(this) / 4D);
						exp = (int)(partialExp * ((float)meleeDamageTable.get(p) / (float)getDef().getHits()) / 4);
						switch (p.getCombatStyle()) {
							case 0:
								p.increaseXP(0, exp, 0);
								p.increaseXP(1, exp, 0);
								p.increaseXP(2, exp, 0);
								p.increaseXP(3, exp, 1);
								p.sendStat(0);
								p.sendStat(1);
								p.sendStat(2);
								p.sendStat(3);
							break;
							
							case 1:
								p.increaseXP(2, exp * 3, 1);
								p.sendStat(2);
								p.increaseXP(3, exp, 0);
								p.sendStat(3);							
							break;
							
							case 2:
								p.increaseXP(0, exp * 3, 1);
								p.sendStat(0);
								p.increaseXP(3, exp, 0);
								p.sendStat(3);							
							break;
							
							case 3:
								p.increaseXP(1, exp * 3, 1);
								p.sendStat(1);
								p.increaseXP(3, exp, 0);
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
						float partialExp = DataConversions.roundUp(Formulae.combatExperience(this) / 4D);
						exp = (int)(partialExp * ((float)rangeDamageTable.get(p) / (float)getDef().getHits()) / 4);
						p.increaseXP(4, exp * 3, 1);
						p.sendStat(4);
					}
				}
			}
			meleeDamageTable.clear();
			rangeDamageTable.clear();
			totalDamageTable.clear();
		} else
			Logger.log(new ErrorLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "Kill-Stealing fucked up - killer was not in damage table", DataConversions.getTimeStamp()));
	}

	public int getCombatStyle() {
		return 0;
	}
	
	public int getWeaponPowerPoints() {
		return 1;
	}
	
	public int getWeaponAimPoints() {
		return 1;
	}
	
	public int getArmourPoints() {
		return 1;
	}
	
	public int getAttack() {
		return curAttack;
	}
	
	public int getDefense() {
		return curDefense;
	}
	
	public int getStrength() {
		return curStrength;
	}
	
	public int getHits() {
		return curHits;
	}
	
	public int maxX() {
		return getLoc().maxX();
	}
	
	public int minX() {
		return getLoc().minX();
	}
	
	public int maxY() {
		return getLoc().maxY();
	}
	
	public int minY() {
		return getLoc().minY();
	}
		
	public void setHits(int lvl) {
		if (lvl <= 0)
			lvl = 0;
		curHits = lvl;
	}
	
	public boolean findUniqueVictim(Player player) {
		if (player.withinRange(this, 2)) {
			if (player.nextTo(this) && player.withinOneSquare(this) && player.getLocation().inBounds(this.minX(), this.minY(), this.maxX(), this.maxY()))
				return true;
		}
		return false;	
	}
	
	private Player findVictim() {
		for (Player p : World.getZone(this.getX(), this.getY()).getPlayers()) {
			if (p.withinRange(this, 2)) {
				if ((p.getCombatLevel() > 2 * super.getCombatLevel() && !getLocation().inWilderness()) || p.isMod() || p.isDev() || p.isBusy() || System.currentTimeMillis() - p.getCombatTimer() < 500 || System.currentTimeMillis() - p.getRunTimer() < 3000 || !p.nextTo(this))	
					continue;
				if (getID() == 232) {
					for (Player player : getViewArea().getPlayersInView())
						player.informOfNpcMessage(new ChatMessage(this, "You shall not pass", p));
				}
				return p;
			}
		}
		return null;
	}

	public boolean withinAggressionRange(Player owner) {
		return !(owner.getLocation().getX() > getLoc().maxX() || owner.getLocation().getX() < getLoc().minX() || owner.getLocation().getY() > getLoc().maxY() || owner.getLocation().getY() < getLoc().minY());
	}
	
	public boolean withinRanges(Player owner, int radius) {
		if(owner.getLocation().getX() > getLoc().maxX() || owner.getLocation().getX() < getLoc().minX() || owner.getLocation().getY() > getLoc().maxY() || owner.getLocation().getY() < getLoc().minY())
			return false; // Out of bounds
		
		int xDiff = getLocation().getX() - owner.getLocation().getX(); 
		int yDiff = getLocation().getY() - owner.getLocation().getY();
		
		return xDiff <= radius && xDiff >= -radius && yDiff <= radius && yDiff >= -radius;
	}
	
	public void updatePosition() {
		if (aggression == null) {
			long now = System.currentTimeMillis();
			Player victim = findVictim();
			if (!isBusy() && def.isAggressive() && now - getCombatTimer() > 3000 && victim != null) {
				if(!this.isFighting())
				{
					FightEvent fe = new FightEvent(victim, this, true);
					victim.setStatus(Action.FIGHTING_MOB);
					this.setFightEvent(fe);
					victim.setFightEvent(fe);
					World.getDelayedEventHandler().add(fe);
				}
				return;
			}
			if (!movementLock && now - lastMovement > (600 + DataConversions.random(0, 3000))) {
				lastMovement = now;
				if (!isBusy() && finishedPath() && DataConversions.random(0, 2) == 1)
					super.setPath(new Path(getX(), getY(), DataConversions.random(loc.minX(), loc.maxX()), DataConversions.random(loc.minY(), loc.maxY())));
			}
		}
		super.updatePosition();
	}
	
	public NPCLoc getLoc() {
		return loc;
	}
	
	public void moveNpc(Path path){
		super.setPath(path);
		super.updatePosition();
	}	
	
	public NPCDef getDef() {
		return EntityHandler.getNpcDef(getID());
	}
}