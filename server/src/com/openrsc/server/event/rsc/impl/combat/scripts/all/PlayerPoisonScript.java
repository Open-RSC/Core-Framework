package com.openrsc.server.event.rsc.impl.combat.scripts.all;

import com.openrsc.server.Constants;
import com.openrsc.server.event.rsc.impl.combat.scripts.CombatScript;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.DataConversions;

/**
 * @author Imposter
 */
public class PlayerPoisonScript implements CombatScript {

	@Override
	public void executeScript(Mob attacker, Mob victim) {
		if (victim.isPlayer()) {
			Player p = (Player) victim;
			if (p.isAntidoteProtected()) {
				return;
			}
			p.poisonDamage = p.getSkills().getLevel(3);
			p.startPoisonEvent();
		}
	}

	@Override
	public boolean shouldExecute(Mob attacker, Mob victim) {
		if (attacker.isPlayer() && DataConversions.random(0, 100) <= 10) {
			Player p = (Player) attacker;
			if (p.getDuel().isDuelActive()) {
				return false;
			}
			if (Constants.GameServer.WANT_EQUIPMENT_TAB) {
				for (Item i : p.getEquipment().list) {
					if (i == null)
						continue;
					if (i.getDef().getName().toLowerCase().contains("poisoned"))
						return true;
				}
			} else {
				for (Item i : p.getInventory().getItems()) {
					if (i.getDef().getName().toLowerCase().contains("poisoned")
						&& i.isWielded()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean shouldCombatStop() {
		return false;
	}

}
