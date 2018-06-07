package org.openrsc.server.event;

import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.eventLog;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.util.Formulae;
import org.openrsc.server.states.CombatState;

import java.util.ArrayList;
import org.openrsc.server.model.ChatMessage;
import org.openrsc.server.model.MenuHandler;
import org.openrsc.server.model.Mob;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Path;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.World;

public class DelrithFightEvent extends DelayedEvent implements IFightEvent{
	private Mob affectedMob;
	private int hits;
	private int firstHit;
	private boolean menuOpen;
	private boolean runOnce;
	
	@Override
	public final Mob getOpponent()
	{
		return affectedMob;
	}
	
	public DelrithFightEvent(Player owner, Mob mob) {
		this(owner, mob, false);
		
		owner.resetAllExceptDMing();
		owner.setOpponent(mob); mob.setOpponent(owner);
		owner.setLocation(mob.getLocation(), true); mob.setLocation(owner.getLocation(), true);
		owner.setSprite(9); mob.setSprite(8);
		owner.setCombatTimer(); mob.setCombatTimer();
		owner.setBusy(true); mob.setBusy(true);
		owner.resetPath(); mob.resetPath();		
	}
	
	public DelrithFightEvent(Player owner, Mob affectedMob, boolean attacked) {
		super(owner, 1000);
		this.affectedMob = affectedMob;
		firstHit = attacked ? 1 : 0;
		hits = 0;
		menuOpen = false;
		runOnce = false;
		owner.sendMessage("As you strike the demon with silverlight he appears to weaken a lot");
	}
	
	private void setMenuClosed() {
		menuOpen = false;
	}
	
	private void resetRunOnce() {
		runOnce = false;
	}
	
	public void run() {
		if(!owner.loggedIn() || (affectedMob instanceof Player && !((Player)affectedMob).loggedIn())) {
			owner.resetCombat(CombatState.ERROR);
			affectedMob.resetCombat(CombatState.ERROR);
			return;
		}
		
		Mob attacker, opponent;
		if (hits++ % 2 == firstHit) {
			attacker = owner;
			opponent = affectedMob;
			final Mob fAttacker = owner;
			final Mob fOpponent = affectedMob;
			if (runOnce) {
				if (!menuOpen) {
					owner.sendMessage("As you strike Delrith a vortex opens up");
					World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
						public void action() {
							for(Player informee : owner.getViewArea().getPlayersInView()) {
								informee.informOfChatMessage(new ChatMessage(owner, "Now what was that incantation again", owner));
							}
							final String[] options2 = {"Carlem Gabindo Purchai Zaree Camerinthum", "Purchai Zaree Gabindo Carlem Camerinthum", "Purchai Camerinthum Gabindo Carlem", "Carlem Aber Camerinthum Purchai Gabindo"};
							owner.sendMenu(options2);
							owner.setMenuHandler(new MenuHandler(options2) {
								public void handleReply(final int option, final String reply) {
									for (Player informee : owner.getViewArea().getPlayersInView())
										informee.informOfChatMessage(new ChatMessage(owner, reply, owner));
									switch(option) {
										case 0:
										case 1:
										case 2:
											World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
												public void action() {
													owner.sendMessage("As you chant, Delrith is sucked towards the vortex");
													World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
														public void action() {
															owner.sendMessage("Suddenly the vortex closes");
															World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
																public void action() {
																	owner.sendMessage("And Delrith is still here");
																	setMenuClosed();
																	resetRunOnce();
																}
															});
														}
													});
												}
											});
											break;
										case 3:
											World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
												public void action() {
													((Npc)fOpponent).killedBy(((Player)fAttacker));
													owner.sendMessage("Delrith is sucked back into the dark dimension from which he came");
													owner.sendMessage("You have completed the demonslayer quest");
													owner.sendMessage("@gre@You have gained 3 quest points!");
													owner.finishQuest(3);
													owner.incQuestCompletionStage(3);
													Logger.log(new eventLog(owner.getUsernameHash(), owner.getAccount(), owner.getIP(), DataConversions.getTimeStamp(), "<strong>" + owner.getUsername() + "</strong>" + " has completed the <span class=\"recent_quest\">Demon Slayer</span> quest!"));
												}
											});
									}
								}
							});
							menuOpen = true;
						}
					});
				}
			}
			runOnce = true;
		} else {
			attacker = affectedMob;
			opponent = owner;
		}
		if (opponent instanceof Player && opponent.getHits() <= 0) {
			attacker.resetCombat(CombatState.WON);
      		opponent.resetCombat(CombatState.LOST);
      		return;
		}
		attacker.incHitsMade();
		if (attacker instanceof Npc && opponent.isPrayerActivated(12))
			return;
		int damage = Formulae.calcFightHitWithNPC(attacker, opponent);
  		opponent.setLastDamage(damage);
  		int newHp = opponent.getHits() - damage;
  		opponent.setHits(newHp);
  		ArrayList<Player> playersToInform = new ArrayList<Player>();
  		playersToInform.addAll(opponent.getViewArea().getPlayersInView());
  		playersToInform.addAll(attacker.getViewArea().getPlayersInView());
  		
  		for (Player p : playersToInform)
  			p.informOfModifiedHits(opponent);
  		
  		String combatSound = damage > 0 ? "combat1b" : "combat1a";
  		if (opponent instanceof Player) {
  			Player opponentPlayer = ((Player)opponent);
  			opponentPlayer.sendStat(3);
  			opponentPlayer.sendSound(combatSound, false);
		}
		if (attacker instanceof Player) {
			Player attackerPlayer = (Player)attacker;
			attackerPlayer.sendSound(combatSound, false);
		}
  		if (attacker instanceof Player && opponent instanceof Npc) {
			Npc opponentNpc = (Npc)opponent;
			if (attacker.getHitsMade() >= 3) {
				if (!(((Player)attacker).getInventory().wielding(52))) {
					opponentNpc.setHits(7);
					int midX = (opponentNpc.maxX() - opponentNpc.minX()) / 2;
					int midY = (opponentNpc.maxY() - opponentNpc.minY()) / 2;
					if (opponentNpc.getX() > midX) {
						if (opponentNpc.getY() > midY)
							opponentNpc.setPath(new Path(opponentNpc.getX(), opponentNpc.getY(), opponentNpc.minX(), opponentNpc.minY()));
						else if(opponentNpc.getY() < midY)
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
		if (newHp <= 0) {
			if (opponent instanceof Player) {
				((Player)opponent).killedBy(attacker, false);
				attacker.resetCombat(CombatState.WON);
				opponent.resetCombat(CombatState.LOST);
			} else
				opponent.setHits(7);
  		}
	}
	public Mob getAffectedMob() {
		return affectedMob;
	}
	
	public boolean equals(Object o) {
		if (o instanceof FightEvent) {
			FightEvent e = (FightEvent)o;
			return e.belongsTo(owner) && e.getAffectedMob().equals(affectedMob);
		}
		return false;
	}
}