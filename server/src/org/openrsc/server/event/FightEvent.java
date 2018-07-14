package org.openrsc.server.event;

import java.util.ArrayList;

import org.openrsc.server.model.Mob;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Path;
import org.openrsc.server.model.Player;
import org.openrsc.server.states.Action;
import org.openrsc.server.states.CombatState;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.util.Formulae;

public class FightEvent extends DelayedEvent implements IFightEvent {
	private Mob mob;
	private int hits;
	private int firstHit;
	
	@Override
	public final Mob getOpponent()
	{
		return mob;
	}
	
	public FightEvent(Player owner, Mob mob) {
		this(owner, mob, false);
	}
	
	public FightEvent(Player owner, Mob mob, boolean attacked) {
		super(owner, 1800);
		
		this.mob = mob;
		firstHit = attacked ? 1 : 0;
		hits = 0;
		
		setLastRun(0);
		
		owner.resetAllExceptDMing();
		owner.setStatus(Action.FIGHTING_MOB);
		
		for (Player p : owner.getViewArea().getPlayersInView())
			p.removeWatchedPlayer(owner);
		
		if (mob instanceof Player) {
			((Player)mob).resetAllExceptDMing();
			((Player)mob).setStatus(Action.FIGHTING_MOB);
			
			for (Player p : mob.getViewArea().getPlayersInView())
				p.removeWatchedPlayer((Player)mob);
		} else if (mob instanceof Npc) {
			for (Player p : mob.getViewArea().getPlayersInView())
				p.removeWatchedNpc((Npc)mob);			
		}
		
		owner.setOpponent(mob); mob.setOpponent(owner);
		
		if (attacked) {
			owner.sendMessage("You are under attack!");
			mob.setLocation(owner.getLocation(), true);
		} else
		{
			owner.setLocation(mob.getLocation(), true);
		}
		owner.setSprite(9); mob.setSprite(8);

		owner.setCombatTimer(); mob.setCombatTimer();
		owner.setBusy(true); mob.setBusy(true);
		owner.resetPath(); mob.resetPath();
		
		if (mob instanceof Npc) {
			if (((Npc)mob).getDef().isDragon() && (attacked || ((Npc)mob).getID() == 477)) {
				owner.sendMessage("The dragon breathes fire at you");
				int damage = 0;
				if (!owner.getInventory().wielding(420)) {
					switch (mob.getID()) {
						default:
							damage = Formulae.rand(10, 30);
						break;
					}
				} else {
					owner.sendMessage("Your shield prevents some of the damage from the flames");
					switch (mob.getID()) {
						default:
							damage = Formulae.rand(0, 10);
						break;
					}
				}
				if (damage > 0) {
					int hits = owner.getHits() - damage;
					owner.setHits(hits);
					owner.setLastDamage(damage);
					for (Player p : owner.getViewArea().getPlayersInView())
						p.informOfModifiedHits(owner);
					owner.sendStat(3);
					if (hits <= 0) {
						owner.killedBy(mob, false);
						mob.resetCombat(CombatState.WON);
						owner.resetCombat(CombatState.LOST);
						((Npc)mob).resetAggression();
						// 3.9.2013 "Dragonfire Bug" fix (no idea why this was commented out)
						this.running = false;
						// 3.9.2013 "Dragonfire Bug" fix (no idea why this was commented out)
						return;
					}
				}
			}
		}
	}
	
	public void run() {	
		if (!owner.loggedIn() || (mob instanceof Player && !((Player)mob).loggedIn())) {
			owner.resetCombat(CombatState.ERROR);
			mob.resetCombat(CombatState.ERROR);
			return;
		}
		Mob attacker, opponent;
		if (hits++ % 2 == firstHit) {
			attacker = owner;
			opponent = mob;
		} else {
			attacker = mob;
			opponent = owner;
		}
		if (opponent.getHits() <= 0) {
			attacker.resetCombat(CombatState.WON);
      		opponent.resetCombat(CombatState.LOST);
      		return;
		}
		
		if (attacker instanceof Npc) {
			Npc n = (Npc) attacker;
			if (attacker.getHits() <= 0) {
				n.resetCombat(CombatState.ERROR);
			}
		}
		if (opponent instanceof Npc) {
			Npc n = (Npc) opponent;
			if (opponent.getHits() <= 0) {
				n.resetCombat(CombatState.ERROR);
			}
		}
		
		attacker.incHitsMade();
		if (attacker instanceof Npc && opponent.isPrayerActivated(12))
			return;
		int damage = (attacker instanceof Player && opponent instanceof Player ? Formulae.calcFightHit(attacker, opponent) : Formulae.calcFightHitWithNPC(attacker, opponent));
		// if > 0 damage and npc hits player and !player.poisoned and npc id = 292 or 271
		if(damage > 0 && attacker instanceof Npc && opponent instanceof Player)
		{
			if(!owner.isPoisoned())
			{
  				Npc npc = (Npc)attacker;
				if (npc.getID() == 292 || npc.getID() == 271)
				{
					owner.poison(npc.getID() == 292 ? 27 : 15);
				} 
  			}
  		}
		
		if(owner.getLocation().onTutorialIsland()) {
			if(attacker instanceof Npc) {
				if(owner.getHits() <= 4) {
					damage = 0;
				}
			}
		}
		int hitsRemaining = opponent.getHits();
		if (damage > hitsRemaining)
			damage = hitsRemaining;
		if (attacker instanceof Player && opponent instanceof Npc)
			((Npc)opponent).updateKillStealing(owner, damage, 0);
  		opponent.setLastDamage(damage);
  		int newHp = opponent.getHits() - damage;
  		opponent.setHits(newHp);
  		ArrayList<Player> playersToInform = new ArrayList<Player>();
  		playersToInform.addAll(opponent.getViewArea().getPlayersInView());
  		playersToInform.addAll(attacker.getViewArea().getPlayersInView());
  		for (Player p : playersToInform)
  			p.informOfModifiedHits(opponent);
		String combatSound = null;
		combatSound = damage > 0 ? "combat1b" : "combat1a";
		if (opponent instanceof Player) {
			if (attacker instanceof Npc) {
				Npc n = (Npc) attacker;
				if (n.getDef().armoured) {
					combatSound = damage > 0 ? "combat2b" : "combat2a";
				} else if (n.getDef().undead) {
					combatSound = damage > 0 ? "combat3b" : "combat3a";
				} else {
					combatSound = damage > 0 ? "combat1b" : "combat1a";
				}
			}
			Player opponentPlayer = ((Player) opponent);
			opponentPlayer.getActionSender().sendStat(3);
			opponentPlayer.getActionSender().sendSound(combatSound, false);
		}
		if (attacker instanceof Player) {
			if (opponent instanceof Npc) {
				Npc n = (Npc) opponent;
				if (n.getDef().armoured) {
					combatSound = damage > 0 ? "combat2b" : "combat2a";
				} else if (n.getDef().undead) {
					combatSound = damage > 0 ? "combat3b" : "combat3a";
				} else {
					combatSound = damage > 0 ? "combat1b" : "combat1a";
				}
			}
			Player attackerPlayer = (Player) attacker;
			attackerPlayer.getActionSender().sendSound(combatSound, false);
		}
  		if (newHp <= 0) {
  			if(opponent instanceof Player)
  				((Player)opponent).killedBy(attacker, false);
  			else
  				((Npc)opponent).killedBy((Player)attacker);
  			if (attacker instanceof Player && !(opponent instanceof Npc)) {
  				Player attackerPlayer = (Player)attacker;
	      			int xp = Formulae.combatExperience(opponent);
	      			switch (attackerPlayer.getCombatStyle()) {
						case 0:
							attackerPlayer.increaseXP(0, xp, 0);
							attackerPlayer.increaseXP(1, xp, 0);
							attackerPlayer.increaseXP(2, xp, 0);
							attackerPlayer.sendStat(0);
							attackerPlayer.sendStat(1);
							attackerPlayer.sendStat(2);
						break;
						
						case 1:
							attackerPlayer.increaseXP(2, xp * 3, 0);
							attackerPlayer.sendStat(2);
						break;
						
						case 2:
							attackerPlayer.increaseXP(0, xp * 3, 0);
							attackerPlayer.sendStat(0);
						break;
						
						case 3:
							attackerPlayer.increaseXP(1, xp * 3, 0);
							attackerPlayer.sendStat(1);
						break;
	      			}
	      			attackerPlayer.increaseXP(3, xp, 1);
	      			attackerPlayer.sendStat(3);
  			}
  			attacker.resetCombat(CombatState.WON);
  			opponent.resetCombat(CombatState.LOST);
  		} else if(attacker instanceof Player && opponent instanceof Npc) {
			Npc opponentNpc = (Npc)opponent;
			if (attacker.getHitsMade() >= 3) {
				if (opponentNpc.getDef().retreats) {
					if (newHp <= opponentNpc.getDef().retreatHits) {
						int midX = (opponentNpc.maxX() - opponentNpc.minX()) / 2;
						int midY = (opponentNpc.maxY() - opponentNpc.minY()) / 2;
						if (opponentNpc.getX() > midX) {
							if (opponentNpc.getY() > midY)
								opponentNpc.setPath(new Path(opponentNpc.getX(), opponentNpc.getY(), opponentNpc.minX(), opponentNpc.minY()));
							else if (opponentNpc.getY() < midY)
								opponentNpc.setPath(new Path(opponentNpc.getX(), opponentNpc.getY(), opponentNpc.minX(), opponentNpc.maxY()));
							else
								opponentNpc.setPath(new Path(opponentNpc.getX(), opponentNpc.getY(), opponentNpc.minX(), midY));
						} else if(opponentNpc.getX() < midX) {
							if (opponentNpc.getY() > midY)
								opponentNpc.setPath(new Path(opponentNpc.getX(), opponentNpc.getY(), opponentNpc.maxX(), opponentNpc.minY()));
							else if(opponentNpc.getY() < midY)
								opponentNpc.setPath(new Path(opponentNpc.getX(), opponentNpc.getY(), opponentNpc.maxX(), opponentNpc.maxY()));
							else
								opponentNpc.setPath(new Path(opponentNpc.getX(), opponentNpc.getY(), opponentNpc.maxX(), midY));				
						} else {
							if (opponentNpc.getY() > midY)
								opponentNpc.setPath(new Path(opponentNpc.getX(), opponentNpc.getY(), midX, opponentNpc.minY()));
							else
								opponentNpc.setPath(new Path(opponentNpc.getX(), opponentNpc.getY(), midX, opponentNpc.maxY()));
						}
						opponent.resetCombat(CombatState.RUNNING);
						attacker.resetCombat(CombatState.WAITING);
					}
				}
			}
		}
	}
	
	public Mob getAffectedMob() {
		return mob;
	}
	
	public boolean equals(Object o) {
		if (o instanceof FightEvent) {
			FightEvent e = (FightEvent)o;
			return e.belongsTo(owner) && e.getAffectedMob().equals(mob) && e.running == this.running;
		}
		return false;
	}
}