package com.openrsc.server.plugins.quests.members.legendsquest.obstacles;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.doDoor;
import static com.openrsc.server.plugins.Functions.mes;

public class LegendsQuestGates implements OpLocTrigger {

	public static final int LEGENDS_HALL_DOOR = 1080;

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player p) {
		return obj.getID() == LEGENDS_HALL_DOOR;
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (obj.getID() == LEGENDS_HALL_DOOR) {
			if (command.equalsIgnoreCase("open")) {
				if (p.getQuestStage(Quests.LEGENDS_QUEST) >= 11 || p.getQuestStage(Quests.LEGENDS_QUEST) == -1) {
					doDoor(obj, p, 497);
					p.message("You open the impressive wooden doors.");
					if (p.getY() <= 539) {
						p.teleport(513, 541);
					} else {
						p.teleport(513, 539);
					}
				} else {
					Functions.mes(p, 1300, "You need to complete the Legends Guild Quest");
					Functions.mes(p, 1200, "before you can enter the Legends Guild");
				}
			} else if (command.equalsIgnoreCase("search")) {
				p.message("Nothing interesting happens");
			}
		}
	}
}
