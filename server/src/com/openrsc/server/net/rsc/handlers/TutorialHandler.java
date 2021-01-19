package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PacketHandler;

public class TutorialHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet, Player player) throws Exception {
		
		if (player == null || !player.getWorld().getServer().getConfig().SHOW_TUTORIAL_SKIP_OPTION) {
			return;
		}
		if (player.getLocation().onTutorialIsland()) {
			if (player.inCombat()) {
				player.message("You cannot do that whilst fighting!");
			}
			if (player.isBusy()) {
				return;
			}
			if (player.getCache().hasKey("tutorial")) {
				player.getCache().remove("tutorial");
			}
			player.teleport(120, 648, false);
			player.message("Skipped tutorial, welcome to Lumbridge");
			ActionSender.sendPlayerOnTutorial(player);
		}
	}
}
