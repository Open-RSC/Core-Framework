package com.openrsc.server.event.rsc.impl.combat;

import com.openrsc.server.constants.Constants;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.Prayers;
import com.openrsc.server.model.entity.update.Damage;
import com.openrsc.server.model.states.CombatState;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

public class CombatEvent extends GameTickEvent {

	private final Mob attackerMob, defenderMob;
	private int roundNumber = 0;

	public CombatEvent(World world, Mob attacker, Mob defender) {
		super(world, null, 0, "Combat Event", false);
		this.attackerMob = attacker;
		this.defenderMob = defender;
		attacker.getWorld().getServer().getCombatScriptLoader().checkAndExecuteOnStartCombatScript(attacker, defender);
		if (attacker.isNpc()) {
			((Npc) attacker).setExecutedAggroScript(false);
		} else if (defender.isNpc()) {
			((Npc) defender).setExecutedAggroScript(false);
		}
	}

	private void onDeath(Mob killed, Mob killer) {
		if (killer.isPlayer() && killed.isNpc()) {
			if (killed.getWorld().getServer().getPluginHandler().handlePlugin((Player)killer, "PlayerKilledNpc", new Object[]{((Player) killer), ((Npc) killed)})) {
				return;
			}
		} else if(killer.isPlayer() && killed.isPlayer()) {
			if (killed.getWorld().getServer().getPluginHandler().handlePlugin((Player)killer, "PlayerKilledPlayer", new Object[]{((Player) killer), ((Player) killed)})) {
				return;
			}
		}

		killed.setLastCombatState(CombatState.LOST);
		killer.setLastCombatState(CombatState.WON);

		if (killed.isPlayer() && killer.isPlayer()) {
			int skillsDist[] = {0, 0, 0, 0};

			Player playerKiller = (Player) killer;
			Player playerKilled = (Player) killed;

			int exp = Formulae.combatExperience(playerKilled);
			switch (playerKiller.getCombatStyle()) {
				case Skills.CONTROLLED_MODE:
					for (int x = 0; x < 3; x++) {
						skillsDist[x] = 1;
					}
					break;
				case Skills.AGGRESSIVE_MODE:
					skillsDist[Skills.STRENGTH] = 3;
					break;
				case Skills.ACCURATE_MODE:
					skillsDist[Skills.ATTACK] = 3;
					break;
				case Skills.DEFENSIVE_MODE:
					skillsDist[Skills.DEFENSE] = 3;
					break;
			}
			skillsDist[Skills.HITS] = 1;
			playerKiller.incExp(skillsDist, exp, true);
		}
		killer.setKillType(0);
		killed.killedBy(killer);
		if (killer.isPlayer()) {
			updateParty((Player)killer);
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
			int damage = CombatFormula.doMeleeDamage(hitter, target);
			inflictDamage(hitter, target, damage);
			if (target.isPlayer()) {
				if (((Player)target).getCarriedItems().getEquipment().hasEquipped(ItemId.RING_OF_RECOIL.id())) {
					int reflectedDamage = damage/10 + ((damage > 0) ? 1 : 0);
					if (reflectedDamage == 0)
						return;

					if (((Player) target).getCache().hasKey("ringofrecoil")) {
						int ringCheck = ((Player) target).getCache().getInt("ringofrecoil");
						if (getWorld().getServer().getConfig().RING_OF_RECOIL_LIMIT - ringCheck <= reflectedDamage) {
							reflectedDamage = getWorld().getServer().getConfig().RING_OF_RECOIL_LIMIT - ringCheck;
							((Player) target).getCache().remove("ringofrecoil");
							((Player) target).getCarriedItems().shatter(new Item(ItemId.RING_OF_RECOIL.id()));
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

		// Paralyze monster stops NPC from damaging players.
		if (hitter.isNpc() && target.isPlayer()) {
			Player targetPlayer = (Player) target;
			if (targetPlayer.getPrayers().isPrayerActivated(Prayers.PARALYZE_MONSTER)) {
				hitter.getWorld().getServer().getCombatScriptLoader().checkAndExecuteCombatScript(hitter, target);
				return;
			}
		}

		// Reduce targets hits by supplied damage amount.
		int lastHits = target.getLevel(Skills.HITPOINTS);
		target.getSkills().subtractLevel(Skills.HITS, damage, false);
		target.getUpdateFlags().setDamage(new Damage(target, damage));
		if (target.isNpc() && hitter.isPlayer()) {
			Npc n = (Npc) target;
			Player player = ((Player) hitter);
			damage = Math.min(damage, lastHits);
			n.addCombatDamage(player, damage);
		}

		// Update players sound and party.
		if (target.isPlayer()) {
			if (hitter.isNpc()) {
				sendSound((Player)target, (Npc)hitter, damage > 0);
			}
			updateParty((Player)target);
		}
		if (hitter.isPlayer()) {
			if (target.isNpc()) {
				sendSound((Player)hitter, (Npc)target, damage > 0);
			}
			updateParty((Player)hitter);
		}

		if (target.getSkills().getLevel(3) > 0) {

			// NPCs can run special combat scripts.
			// Custom: Ring of Life execution
			boolean ringOfLifeScript = false;
			if (target.isPlayer()) {
				Player player = (Player)target;
				ringOfLifeScript = player.getDuel().isDuelActive() && player.checkRingOfLife(hitter);
			}
			if (target.isNpc() || ringOfLifeScript) {
				target.getWorld().getServer().getCombatScriptLoader().checkAndExecuteCombatScript(hitter, target);
			}
		}

		// Mob has <= 0 hits.
		else {
			onDeath(target, hitter);
		}
	}

	// Players in combat with an NPC will receive unique NPC
	// sounds dependent on npc type.
	private void sendSound(Player player, Npc npc, boolean damaged) {
		String combatSound;
		if (DataConversions.inArray(Constants.ARMOR_NPCS, npc.getID())) {
			combatSound = damaged ? "combat2b" : "combat2a";
		} else if (DataConversions.inArray(Constants.UNDEAD_NPCS, npc.getID())) {
			combatSound = damaged ? "combat3b" : "combat3a";
		} else {
			combatSound = damaged ? "combat1b" : "combat1a";
		}

		ActionSender.sendSound(player, combatSound);
	}

	private void updateParty(Player player) {
		if (getWorld().getServer().getConfig().WANT_PARTIES) {
			if(player.getParty() != null){
				player.getParty().sendParty();
			}
		}
	}

	public void resetCombat() {
		if (running) {
			if (defenderMob != null) {
				int delayedAggro = 0;
				if (defenderMob.isPlayer()) {
					Player player = (Player) defenderMob;
					player.resetAll();
				} else {
					if (attackerMob.getCombatState() == CombatState.RUNNING) {
						delayedAggro = 17000; // 17 + 3 second aggro timer for npcs running
					}
				}

				defenderMob.setBusy(false);
				defenderMob.setOpponent(null);
				defenderMob.setCombatEvent(null);
				defenderMob.setHitsMade(0);
				defenderMob.setSprite(4);
				defenderMob.setCombatTimer(delayedAggro);
				defenderMob.face(defenderMob.getX(), defenderMob.getY() - 1);
				if(defenderMob.isPlayer()){
					Player p1;
					p1 = ((Player) defenderMob);
					if (p1.getParty() != null){
						for (Player player : getWorld().getPlayers()) {
							if(p1.getParty() == player.getParty()){
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
					player.resetAll();
				} else {
					if (attackerMob.getCombatState() == CombatState.RUNNING) {
						delayedAggro = 17000; // 17 + 3 second timer for npcs running
					}
				}

				attackerMob.setBusy(false);
				attackerMob.setOpponent(null);
				attackerMob.setCombatEvent(null);
				attackerMob.setHitsMade(0);
				attackerMob.setSprite(4);
				attackerMob.setCombatTimer(delayedAggro);
				attackerMob.face(attackerMob.getX(), attackerMob.getY() - 1);
				if(attackerMob.isPlayer()){
					Player p2;
					p2 = ((Player) attackerMob);
					if (p2.getParty() != null){
						for (Player player : getWorld().getPlayers()) {
							if(p2.getParty() == player.getParty()){
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
		boolean respawning = (attackerMob.isNpc() && ((Npc)attackerMob).isRespawning())
			|| (defenderMob.isNpc() && ((Npc)defenderMob).isRespawning());
		return bothLoggedIn && !removed && nextToVictim && running;
	}

	public Mob getAttacker() {
		return attackerMob;
	}

	public Mob getVictim() {
		return defenderMob;
	}

}
