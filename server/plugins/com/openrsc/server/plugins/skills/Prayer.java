package com.openrsc.server.plugins.skills;

import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.getMaxLevel;
import static com.openrsc.server.plugins.Functions.mes;
import static com.openrsc.server.plugins.Functions.delay;

public class Prayer implements OpLocTrigger {

	@Override
	public void onOpLoc(final GameObject object, String command, Player player) {
		if (command.equalsIgnoreCase("recharge at")) {
			int maxPray = getMaxLevel(player, Skills.PRAYER) + (object.getID() == 200 ? 2 : 0);
			if (getCurrentLevel(player, Skills.PRAYER) == maxPray) {
				player.playerServerMessage(MessageType.QUEST, "You already have full prayer points");
			} else {
				player.playerServerMessage(MessageType.QUEST, "You recharge your prayer points");
				player.playSound("recharge");
				if (getCurrentLevel(player, Skills.PRAYER) < maxPray) {
					player.getSkills().setLevel(Skills.PRAYER, maxPray);
				}

			}
			if (object.getID() == 625 && object.getY() == 3573) {
				delay(650);
				mes(player, "Suddenly a trapdoor opens beneath you");
				player.teleport(608, 3525);
			}
		}
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player player) {
		return command.equals("recharge at");
	}

}
