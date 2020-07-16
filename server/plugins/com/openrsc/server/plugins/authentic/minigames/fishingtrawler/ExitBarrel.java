package com.openrsc.server.plugins.authentic.minigames.fishingtrawler;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class ExitBarrel implements OpLocTrigger {

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 1070;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		mes("you climb onto the floating barrel");
		delay(3);
		mes("and begin to kick your way to the shore");
		delay(3);
		mes("you make it to the shore tired and weary");
		delay(3);
		player.teleport(550, 711);
		player.damage(3);
	}

}
