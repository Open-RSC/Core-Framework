package com.openrsc.server.plugins.minigames.fishingtrawler;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.mes;

public class ExitBarrel implements OpLocTrigger {

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player player) {
		return obj.getID() == 1070;
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player player) {
		Functions.mes(player, player.getWorld().getServer().getConfig().GAME_TICK * 3, "you climb onto the floating barrel", "and begin to kick your way to the shore",
			"you make it to the shore tired and weary");
		player.teleport(550, 711);
		player.damage(3);
	}

}
