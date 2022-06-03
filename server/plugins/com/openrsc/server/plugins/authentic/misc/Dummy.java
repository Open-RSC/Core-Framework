package com.openrsc.server.plugins.authentic.misc;

import com.google.inject.spi.Message;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.delay;
import static com.openrsc.server.plugins.Functions.mes;

public class Dummy implements OpLocTrigger {

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 49 || obj.getID() == 562;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == 49) { // Dummy
			mes("You swing at the dummy");
			delay(5);
			player.playerServerMessage(MessageType.QUEST, "You hit the dummy");
			ActionSender.sendSound(player, "combat1");
			if (player.getSkills().getLevel(Skill.ATTACK.id()) <= 7) {
				// this situation not found in replays
				player.incExp(Skill.ATTACK.id(), 20, true);
			} else {
				player.playerServerMessage(MessageType.QUEST, "There is nothing more you can learn from hitting a dummy");
			}
		} else if (obj.getID() == 562) { // fight Dummy
			boolean grantXP = false;
			if (player.getCache().hasKey("combat_dummy")) {
				if (player.getCache().getInt("combat_dummy") < 10) {
					player.getCache().set("combat_dummy", player.getCache().getInt("combat_dummy") + 1);
					grantXP = true;
				}
			} else {
				player.getCache().set("combat_dummy", 1);
				grantXP = true;
			}

			if (grantXP) {
				player.incExp(Skill.ATTACK.id(), 200, true);
			}
			mes("You swing at the dummy");
			delay(5);
			player.playerServerMessage(MessageType.QUEST, "You hit the dummy");
			ActionSender.sendSound(player, "combat1");
			if (!grantXP) {
				// this situation not found in replays
				player.playerServerMessage(MessageType.QUEST, "There is nothing more you can learn from hitting this dummy");
			}
		}
	}
}
