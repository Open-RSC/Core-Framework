package com.openrsc.server.plugins.authentic.quests.members.legendsquest.obstacles;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestGates implements OpLocTrigger {

	public static final int LEGENDS_HALL_DOOR = 1080;

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == LEGENDS_HALL_DOOR;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == LEGENDS_HALL_DOOR) {
			if (command.equalsIgnoreCase("open")) {
				if (player.getQuestStage(Quests.LEGENDS_QUEST) >= 11 || player.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
					doDoor(obj, player, 497);
					player.message("You open the impressive wooden doors.");
					if (player.getY() <= 539) {
						player.teleport(513, 541);
					} else {
						player.teleport(513, 539);
					}
				} else {
					mes("You need to complete the Legends Guild Quest");
					delay(2);
					mes("before you can enter the Legends Guild");
					delay(2);
				}
			} else if (command.equalsIgnoreCase("search")) {
				player.message("Nothing interesting happens");
			}
		}
	}
}
