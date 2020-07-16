package com.openrsc.server.plugins.authentic.skills.prayer;

import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class Prayer implements OpLocTrigger {

	@Override
	public void onOpLoc(Player player, final GameObject object, String command) {
		if (command.equalsIgnoreCase("recharge at")) {
			int maxPray = getMaxLevel(player, Skills.PRAYER) + (object.getID() == 200 ? 2 : 0);
			if (getCurrentLevel(player, Skills.PRAYER) == maxPray) {
				player.playerServerMessage(MessageType.QUEST, "You already have full prayer points");
				player.setPrayerStatePoints(maxPray * 120);
			} else {
				player.playerServerMessage(MessageType.QUEST, "You recharge your prayer points");
				player.playSound("recharge");
				if (getCurrentLevel(player, Skills.PRAYER) < maxPray) {
					player.getSkills().setLevel(Skills.PRAYER, maxPray);
				}

			}
			if (object.getID() == 625 && object.getY() == 3573) {
				delay();
				mes("Suddenly a trapdoor opens beneath you");
				delay(3);
				player.teleport(608, 3525);
			}
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return command.equals("recharge at");
	}

}
