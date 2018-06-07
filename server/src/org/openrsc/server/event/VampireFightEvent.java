package org.openrsc.server.event;

import java.util.ArrayList;

import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.eventLog;
import org.openrsc.server.model.Mob;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Quest;
import org.openrsc.server.states.CombatState;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.util.Formulae;

public class VampireFightEvent extends DelayedEvent implements IFightEvent {

	private Mob affectedMob;
	private int hits;
	private int firstHit;

	@Override
	public final Mob getOpponent()
	{
		return affectedMob;
	}
	
	public VampireFightEvent(Player owner, Mob mob) {
		this(owner, mob, false);
		owner.resetAllExceptDMing();
		owner.setOpponent(mob); mob.setOpponent(owner);
		owner.setLocation(mob.getLocation(), true); mob.setLocation(owner.getLocation(), true);
		owner.setSprite(9); mob.setSprite(8);
		owner.setCombatTimer(); mob.setCombatTimer();
		owner.setBusy(true); mob.setBusy(true);
		owner.resetPath(); mob.resetPath();			
	}
	
	public VampireFightEvent(Player owner, Mob mob, boolean attacked) {
		super(owner, 1000);
		this.affectedMob = mob;
		firstHit = attacked ? 1 : 0;
		hits = 0;
		
		mob.setLocation(owner.getLocation(), true);
		
		owner.setCombatTimer(); mob.setCombatTimer();
		owner.setBusy(true); mob.setBusy(true);
		owner.setOpponent(mob); mob.setOpponent(owner);
		owner.resetPath(); mob.resetPath();		
	}
	
	public void run() {
		if(!owner.loggedIn() || (affectedMob instanceof Player && !((Player)affectedMob).loggedIn())) {
			owner.resetCombat(CombatState.ERROR);
			affectedMob.resetCombat(CombatState.ERROR);
			return;
		}
		Mob attacker, opponent;
		if(hits++ % 2 == firstHit) {
			attacker = owner;
			opponent = affectedMob;
		} else {
			attacker = affectedMob;
			opponent = owner;
		}
		if(opponent instanceof Player && opponent.getHits() <= 0) {
			attacker.resetCombat(CombatState.WON);
      		opponent.resetCombat(CombatState.LOST);
      		return;
		}
		attacker.incHitsMade();
		if(attacker instanceof Npc && opponent.isPrayerActivated(12)) {
			return;
		}
		int damage = Formulae.calcFightHitWithNPC(attacker, opponent);
  		opponent.setLastDamage(damage);
  		int newHp = opponent.getHits() - damage;
  		opponent.setHits(newHp);
  		ArrayList<Player> playersToInform = new ArrayList<Player>();
  		playersToInform.addAll(opponent.getViewArea().getPlayersInView());
  		playersToInform.addAll(attacker.getViewArea().getPlayersInView());
  		for(Player p : playersToInform) {
  			p.informOfModifiedHits(opponent);
  		}
  		String combatSound = damage > 0 ? "combat1b" : "combat1a";
  		if(opponent instanceof Player) {
  			Player opponentPlayer = ((Player)opponent);
  			opponentPlayer.sendStat(3);
  			opponentPlayer.sendSound(combatSound, false);
		}
		if(attacker instanceof Player) {
			Player attackerPlayer = (Player)attacker;
			attackerPlayer.sendSound(combatSound, false);
		}
		if(newHp <= 0) {
			if(opponent instanceof Player) {
				((Player)opponent).killedBy(attacker, false);
				attacker.resetCombat(CombatState.WON);
				opponent.resetCombat(CombatState.LOST);
			} else {
				Quest q = owner.getQuest(15);
				if(!owner.getInventory().wielding(217) || owner.getInventory().countId(168) == 0 || q == null) {
					owner.sendMessage("The vampire seems to regenerate");
					opponent.setHits(opponent.getHits() + 20);
				} else {
					((Npc)opponent).killedBy((Player)attacker);
					attacker.resetCombat(CombatState.WON);
					opponent.resetCombat(CombatState.LOST);
					if(!q.finished()) {
						owner.sendMessage("You hammer the stake into the vampires chest!");
						owner.sendMessage("Well done you have completed the vampire slayer quest");
						owner.getInventory().remove(217);
						owner.getInventory().remove(168);
						owner.sendInventory();
						owner.incQuestExp(0, 4000);
						owner.sendStat(0);
						owner.sendMessage("@gre@You have gained 3 quest points!");
						owner.finishQuest(15);
						Logger.log(new eventLog(owner.getUsernameHash(), owner.getAccount(), owner.getIP(), DataConversions.getTimeStamp(), "<strong>" + owner.getUsername() + "</strong>" + " has completed the <span class=\"recent_quest\">Vampire Slayer</span> quest!"));
					}
				}
			}
  		}
	}
	
	public Mob getAffectedMob() {
		return affectedMob;
	}
	
	public boolean equals(Object o) {
		if(o instanceof FightEvent) {
			FightEvent e = (FightEvent)o;
			return e.belongsTo(owner) && e.getAffectedMob().equals(affectedMob);
		}
		return false;
	}
}