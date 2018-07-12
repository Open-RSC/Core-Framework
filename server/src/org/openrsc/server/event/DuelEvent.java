package org.openrsc.server.event;

import java.util.ArrayList;

import org.openrsc.server.ServerBootstrap;
import org.openrsc.server.database.game.Save;
import org.openrsc.server.model.Mob;
import org.openrsc.server.model.Player;
import org.openrsc.server.states.CombatState;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.util.Formulae;

public class DuelEvent extends DelayedEvent implements IFightEvent {
	private Player affectedPlayer;
	private int hits;
	@Override
	public final Mob getOpponent()
	{
		return affectedPlayer;
	}
	public DuelEvent(Player owner, Player affectedPlayer) {
		super(owner, 1400);
		this.affectedPlayer = affectedPlayer;
		hits = 0;
		
		setLastRun(0);
		
		owner.resetAllExceptDueling(); affectedPlayer.resetAllExceptDueling();
		owner.setLocation(affectedPlayer.getLocation(), true);
		
		for (Player p : owner.getViewArea().getPlayersInView())
			p.removeWatchedPlayer(owner);	
		
		owner.setOpponent(affectedPlayer); affectedPlayer.setOpponent(owner);
		owner.setSprite(9); affectedPlayer.setSprite(8);
		owner.setCombatTimer(); affectedPlayer.setCombatTimer();
		owner.setBusy(true); affectedPlayer.setBusy(true);
		owner.resetPath(); affectedPlayer.resetPath();	
	}
	
	public void run() {
		if (!owner.loggedIn() || !affectedPlayer.loggedIn()) {
			owner.resetCombat(CombatState.ERROR);
			affectedPlayer.resetCombat(CombatState.ERROR);
			return;
		}
		Player attacker, opponent;
		if (hits++ % 2 == 0) {
			attacker = owner;
			opponent = affectedPlayer;
		} else {
			attacker = affectedPlayer;
			opponent = owner;
		}
		if (opponent.getHits() <= 0) {
			attacker.resetCombat(CombatState.WON);
      		opponent.resetCombat(CombatState.LOST);
      		return;
		}
		attacker.incHitsMade();
		attacker.setLastMoved();
		
		int damage = (attacker instanceof Player && opponent instanceof Player ? Formulae.calcFightHit(attacker, opponent) : Formulae.calcFightHitWithNPC(attacker, opponent));
  		opponent.setLastDamage(damage);
  		int newHp = opponent.getHits() - damage;
  		opponent.setHits(newHp);
  		ArrayList<Player> playersToInform = new ArrayList<Player>();
  		playersToInform.addAll(opponent.getViewArea().getPlayersInView());
  		playersToInform.addAll(attacker.getViewArea().getPlayersInView());
  		for (Player p : playersToInform)
  			p.informOfModifiedHits(opponent);
  		String combatSound = damage > 0 ? "combat1b" : "combat1a";
  		
  		opponent.sendStat(3);
  		opponent.sendSound(combatSound, false);
		attacker.sendSound(combatSound, false);
		
  		if(newHp <= 0) {
  			opponent.killedBy(attacker, true);
      			int exp = Formulae.combatExperience(opponent);
      			switch(attacker.getCombatStyle()) {
				case 0:
					attacker.increaseXP(0, exp, 1);
					attacker.increaseXP(1, exp, 1);
					attacker.increaseXP(2, exp, 1);
					attacker.sendStat(0);
					attacker.sendStat(1);
					attacker.sendStat(2);
				break;
				
				case 1:
					attacker.increaseXP(2, exp * 3, 1);
					attacker.sendStat(2);
				break;
				
				case 2:
					attacker.increaseXP(0, exp * 3, 1);
					attacker.sendStat(0);
				break;
				
				case 3:
					attacker.increaseXP(1, exp * 3, 1);
					attacker.sendStat(1);
				break;
			}
      		attacker.increaseXP(3, exp, 1);
      		attacker.sendStat(3);

  			attacker.resetCombat(CombatState.WON);
  			opponent.resetCombat(CombatState.LOST);
  			
  			attacker.resetDueling();
  			opponent.resetDueling();
  			Save s1 = new Save(attacker);
  			Save s2 = new Save(opponent);
			ServerBootstrap.getDatabaseService().submit(s1, s1.new DefaultSaveListener());
			ServerBootstrap.getDatabaseService().submit(s2, s2.new DefaultSaveListener());
  		}
	}
	
	public Player getAffectedPlayer() {
		return affectedPlayer;
	}
	
	public boolean equals(Object o) {
		if(o instanceof DuelEvent) {
			DuelEvent e = (DuelEvent)o;
			return e.belongsTo(owner) && e.getAffectedPlayer().equals(affectedPlayer);
		}
		return false;
	}
}