package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.delay;
import static com.openrsc.server.plugins.Functions.mes;

public class Dummy implements OpLocTrigger {

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 49 || obj.getID() == 562;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		mes("You swing at the dummy");
		delay(5);
		if (obj.getID() == 49) { // Dummy
			if (player.getSkills().getLevel(Skill.ATTACK.id()) > 7) {
				player.message("There is only so much you can learn from hitting a dummy");
			} else {
				player.message("You hit the dummy");
				player.incExp(Skill.ATTACK.id(), 20, true);
			}
		} else if (obj.getID() == 562) { // fight Dummy
			if (player.getCache().hasKey("combat_dummy")) {
				if (player.getCache().getInt("combat_dummy") < 10) {
					player.getCache().set("combat_dummy", player.getCache().getInt("combat_dummy") + 1);
				} else {
					player.message("There is nothing more you can learn from hitting this dummy");
					return;
				}
			} else
				player.getCache().set("combat_dummy", 1);

			// TODO: Proper message for this prop.
			player.message("You hit the dummy");
			player.incExp(Skill.ATTACK.id(), 200, true);
		}
	}
}
