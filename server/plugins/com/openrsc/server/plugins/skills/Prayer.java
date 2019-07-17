package com.openrsc.server.plugins.skills;

import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.getMaxLevel;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.sleep;

public class Prayer implements ObjectActionExecutiveListener, ObjectActionListener {

	@Override
	public void onObjectAction(final GameObject object, String command, Player player) {
		if (command.equalsIgnoreCase("recharge at")) {
			int maxPray = getMaxLevel(player, SKILLS.PRAYER.id()) + (object.getID() == 200 ? 2 : 0);
			if (getCurrentLevel(player, SKILLS.PRAYER.id()) == maxPray) {
				player.message("You already have full prayer points");
			} else {
				player.message("You recharge your prayer points");
				player.playSound("recharge");
				if (getCurrentLevel(player, SKILLS.PRAYER.id()) < maxPray) {
					player.getSkills().setLevel(SKILLS.PRAYER.id(), maxPray);
				}

			}
			if (object.getID() == 625 && object.getY() == 3573) {
				sleep(650);
				message(player, "Suddenly a trapdoor opens beneath you");
				player.teleport(608, 3525);
			}
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return command.equals("recharge at");
	}

}
