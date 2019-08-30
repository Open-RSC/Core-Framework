package com.openrsc.server.event.rsc.impl.combat;

import com.openrsc.server.constants.Constants;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.model.entity.update.Damage;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.states.CombatState;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

/***
 *
 * @author n0m
 *
 */
public class CombatEvent extends GameTickEvent {

	private final Mob attackerMob, defenderMob;
	private int roundNumber = 0;

	public CombatEvent(World world, Mob attacker, Mob defender) {
		super(world, null, 0, "Combat Event");
		this.attackerMob = attacker;
		this.defenderMob = defender;
		attacker.getWorld().getServer().getCombatScriptLoader().checkAndExecuteOnStartCombatScript(attacker, defender);
		if (attacker.isNpc()) {
			((Npc) attacker).setExecutedAggroScript(false);
		} else if (defender.isNpc()) {
			((Npc) defender).setExecutedAggroScript(false);
		}
	}

	private static void onDeath(Mob killed, Mob killer) {
		if (killer.isPlayer() && killed.isNpc()) {
			if (killed.getWorld().getServer().getPluginHandler().blockDefaultAction("PlayerKilledNpc",
				new Object[]{((Player) killer), ((Npc) killed)})) {
				return;
			}
		}

		killed.setLastCombatState(CombatState.WON);
		killer.setLastCombatState(CombatState.LOST);

		if (killed.isPlayer() && killer.isPlayer()) {
			Player playerKiller = (Player) killer;
			Player playerKilled = (Player) killed;

			int exp = Formulae.combatExperience(playerKilled);
			switch (playerKiller.getCombatStyle()) {
				case 0:
					for (int x = 0; x < 3; x++) {
						playerKiller.incExp(x, exp, true);
					}
					break;
				case 1:
					playerKiller.incExp(2, exp * 3, true);
					break;
				case 2:
					playerKiller.incExp(0, exp * 3, true);
					break;
				case 3:
					playerKiller.incExp(1, exp * 3, true);
					break;
			}
			playerKiller.incExp(3, exp, true);
		}
		killer.setKillType(0);
		killed.killedBy(killer);
		if (killer.getWorld().getServer().getConfig().WANT_PARTIES) {
			if(killer.isPlayer() && ((Player) killer).getParty() != null){
				((Player) killer).getParty().sendParty();
			}
		}
	}

	public final void run() {
		setDelayTicks(2);
		Mob hitter, target = null;

		if (roundNumber++ % 2 == 0) {
			hitter = attackerMob;
			target = defenderMob;
		} else {
			hitter = defenderMob;
			target = attackerMob;
		}

		if (!combatCanContinue()) {
			hitter.setLastCombatState(CombatState.ERROR);
			target.setLastCombatState(CombatState.ERROR);
			resetCombat();
		} else {
			//if(hitter.isNpc() && target.isPlayer() || target.isNpc() && hitter.isPlayer()) {
			int damage = MeleeFormula.getDamage(hitter, target);
			inflictDamage(hitter, target, damage);
			if (target.isPlayer()) {
				if (Functions.isWielding((Player) target, ItemId.RING_OF_RECOIL.id())) {
					int reflectedDamage = damage/10 + ((damage > 0) ? 1 : 0);
					if (reflectedDamage == 0)
						return;

					if (((Player) target).getCache().hasKey("ringofrecoil")) {
						int ringCheck = ((Player) target).getCache().getInt("ringofrecoil");
						if (getWorld().getServer().getConfig().RING_OF_RECOIL_LIMIT - ringCheck <= reflectedDamage) {
							reflectedDamage = getWorld().getServer().getConfig().RING_OF_RECOIL_LIMIT - ringCheck;
							((Player) target).getCache().remove("ringofrecoil");
							((Player) target).getInventory().shatter(ItemId.RING_OF_RECOIL.id());
						} else {
							((Player) target).getCache().set("ringofrecoil", ringCheck + reflectedDamage);
						}
					} else {
						((Player) target).getCache().put("ringofrecoil", reflectedDamage);
						((Player) target).message("You start a new ring of recoil");
					}
					inflictDamage(target, hitter, reflectedDamage);
				}
			}
		}
	}

	private void inflictDamage(final Mob hitter, final Mob target, int damage) {
		hitter.incHitsMade();
		if (hitter.isNpc() && target.isPlayer()) {
			Player targetPlayer = (Player) target;

			if (targetPlayer.getPrayers().isPrayerActivated(Prayers.PARALYZE_MONSTER)) {
				hitter.getWorld().getServer().getCombatScriptLoader().checkAndExecuteCombatScript(hitter, target);
				return;
			}
		}

		target.getSkills().subtractLevel(Skills.HITS, damage, false);
		target.getUpdateFlags().setDamage(new Damage(target, damage));
		if (target.isNpc() && hitter.isPlayer()) {
			Npc n = (Npc) target;
			Player p = ((Player) hitter);
			n.addCombatDamage(p, damage);
		}

		String combatSound = null;
		combatSound = damage > 0 ? "combat1b" : "combat1a";

		if (target.isPlayer()) {
			if (hitter.isNpc()) {
				Npc n = (Npc) hitter;
				if (DataConversions.inArray(Constants.ARMOR_NPCS, n.getID())) {
					combatSound = damage > 0 ? "combat2b" : "combat2a";
				} else if (DataConversions.inArray(Constants.UNDEAD_NPCS, n.getID())) {
					combatSound = damage > 0 ? "combat3b" : "combat3a";
				} else {
					combatSound = damage > 0 ? "combat1b" : "combat1a";
				}
			}
			if (getWorld().getServer().getConfig().WANT_PARTIES) {
				if(((Player) target).getParty() != null){
					((Player) target).getParty().sendParty();
				}
			}
			Player opponentPlayer = ((Player) target);
			ActionSender.sendSound(opponentPlayer, combatSound);
		}
		if (hitter.isPlayer()) {
			if (target.isNpc()) {
				Npc n = (Npc) target;
				if (DataConversions.inArray(Constants.ARMOR_NPCS, n.getID())) {
					combatSound = damage > 0 ? "combat2b" : "combat2a";
				} else if (DataConversions.inArray(Constants.UNDEAD_NPCS, n.getID())) {
					combatSound = damage > 0 ? "combat3b" : "combat3a";
				} else {
					combatSound = damage > 0 ? "combat1b" : "combat1a";
				}
			}
			if (getWorld().getServer().getConfig().WANT_PARTIES) {
				if(((Player) hitter).getParty() != null){
					((Player) hitter).getParty().sendParty();
				}
			}
			Player attackerPlayer = (Player) hitter;
			ActionSender.sendSound(attackerPlayer, combatSound);
		}

		if (target.getSkills().getLevel(3) > 0) {
			if (!(target.isPlayer() && !((Player)target).getDuel().isDuelActive() && ((Player)target).checkRingOfLife(hitter)))
				target.getWorld().getServer().getCombatScriptLoader().checkAndExecuteCombatScript(hitter, target);
		} else {
			onDeath(target, hitter);
		}
	}

	public void resetCombat() {
		if (running) {
			if (defenderMob != null) {
				int delayedAggro = 0;
				if (defenderMob.isPlayer()) {
					Player player = (Player) defenderMob;
					player.setStatus(Action.IDLE);
					player.resetAll();
				} else {
					delayedAggro = 17000; // 17 + 3 second aggro timer for npds running
				}

				defenderMob.setBusy(false);
				defenderMob.setOpponent(null);
				defenderMob.setCombatEvent(null);
				defenderMob.setHitsMade(0);
				defenderMob.setSprite(4);
				defenderMob.setCombatTimer(delayedAggro);
				if(defenderMob.isPlayer()){
					Player p1;
					p1 = ((Player) defenderMob);
					if (p1.getParty() != null){
						for (Player p : getWorld().getPlayers()) {
							if(p1.getParty() == p.getParty()){
								//ActionSender.sendParty(p);
							}
						}
					}
				}
			}
			if (attackerMob != null) {
				int delayedAggro = 0;
				if (attackerMob.isPlayer()) {
					Player player = (Player) attackerMob;
					player.setStatus(Action.IDLE);
					player.resetAll();
				} else {
					if (attackerMob.getCombatState() == CombatState.RUNNING)
						delayedAggro = 17000; // 17 + 3 second timer for npcs running
				}

				attackerMob.setBusy(false);
				attackerMob.setOpponent(null);
				attackerMob.setCombatEvent(null);
				attackerMob.setHitsMade(0);
				attackerMob.setSprite(4);
				attackerMob.setCombatTimer(delayedAggro);
				if(attackerMob.isPlayer()){
					Player p2;
					p2 = ((Player) attackerMob);
					if (p2.getParty() != null){
						for (Player p : getWorld().getPlayers()) {
							if(p2.getParty() == p.getParty()){
								//ActionSender.sendParty(p);
							}
						}
					}
				}
			}
		}
		stop();
	}

	private boolean combatCanContinue() {
		boolean removed = attackerMob.isRemoved() || defenderMob.isRemoved();
		boolean nextToVictim = attackerMob.getLocation().equals(defenderMob.getLocation());
		if (defenderMob.isNpc() && attackerMob.isNpc()) {
			return !removed && nextToVictim && running;
		}
		boolean bothLoggedIn = (attackerMob.isPlayer() && ((Player) attackerMob).loggedIn())
			|| (defenderMob.isPlayer() && ((Player) defenderMob).loggedIn());
		return bothLoggedIn && !removed && nextToVictim && running;
	}

	public Mob getAttacker() {
		return attackerMob;
	}

	public Mob getVictim() {
		return defenderMob;
	}

}
