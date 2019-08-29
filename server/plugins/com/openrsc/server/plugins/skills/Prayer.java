package com.openrsc.server.plugins.skills;

import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class Prayer implements ObjectActionExecutiveListener, ObjectActionListener {

	@Override
	public void onObjectAction(final GameObject object, String command, Player player) {
		player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Prayer Recharge") {
			public void init() {
				addState(0, () -> {
					if (command.equalsIgnoreCase("recharge at")) {
						int maxPray = getMaxLevel(getPlayerOwner(), Skills.PRAYER) + (object.getID() == 200 ? 2 : 0);
						if (getCurrentLevel(getPlayerOwner(), Skills.PRAYER) == maxPray) {
							getPlayerOwner().message("You already have full prayer points");
						} else {
							getPlayerOwner().message("You recharge your prayer points");
							getPlayerOwner().playSound("recharge");
							if (getCurrentLevel(getPlayerOwner(), Skills.PRAYER) < maxPray) {
								getPlayerOwner().getSkills().setLevel(Skills.PRAYER, maxPray);
							}

						}
						if (object.getID() == 625 && object.getY() == 3573) {
							return invoke(1, 1);
						}
					}
					return null;
				});
				addState(1, () -> {
					message(getPlayerOwner(), "Suddenly a trapdoor opens beneath you");
					getPlayerOwner().teleport(608, 3525);
					return null;
				});
			}
		});
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return command.equals("recharge at");
	}

}
