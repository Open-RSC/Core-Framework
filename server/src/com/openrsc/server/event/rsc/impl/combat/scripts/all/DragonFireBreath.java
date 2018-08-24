package com.openrsc.server.event.rsc.impl.combat.scripts.all;

import com.openrsc.server.event.rsc.impl.combat.scripts.OnCombatStartScript;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.DataConversions;
/**
 * 
 * @author n0m
 *
 */
public class DragonFireBreath implements OnCombatStartScript {
	
	@Override
	public void executeScript(Mob attacker, Mob victim) {
		Player player = null;
		Npc dragon = null;
		if(attacker.isNpc() && victim.isPlayer()) {
			player = (Player) victim;
			dragon = (Npc) attacker;
		} else if(victim.isNpc() && attacker.isPlayer()) {
			player = (Player) attacker;
			dragon = (Npc) victim;
		}
		player.message("The dragon breathes fire at you");
		int maxHit = 65; // ELVARG & KING BLACK DRAGON - MAXIMUM HIT FOR BOTH
		if(dragon.getID() == 203) { // BABY BLUE DRAGON
			maxHit = 12; // NOT SURE BUT DEFINITELY NOT 65 FOR BABY BLUES.
		}
		else if(dragon.getID() == 202) { // BLUE DRAGON
			maxHit = 50; // 50 SOLID
		} 
		else if(dragon.getID() == 201 || dragon.getID() == 291) { // RED AND BLACK DRAGON
			maxHit = 55; // 50+
		}
		if (player.getInventory().wielding(420)) {
			maxHit = 10; // LOWER DOWN TO MAXIMUM 10 HIT IF WEARING SHIELD
			player.message("Your shield prevents some of the damage from the flames");
		}
		player.damage(DataConversions.random(0, maxHit));
	}

	@Override
	public boolean shouldExecute(Mob attacker, Mob victim) {
		Npc dragon = null;
		if(attacker.isNpc() && victim.isPlayer()) {
			dragon = (Npc) attacker;
		}
		else if(victim.isNpc() && attacker.isPlayer()) {
			dragon = (Npc) victim;
		}
		if(dragon == null) {
			return false;
		}
		return dragon.getDef().getName().toLowerCase().contains("dragon") || dragon.getID() == 196;
	}

}
