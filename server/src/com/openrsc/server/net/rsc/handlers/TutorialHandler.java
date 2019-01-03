package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PacketHandler;

public class TutorialHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet p, Player player) throws Exception {
		if (player == null) {
			return;
		}
		if (player.getLocation().onTutorialIsland()) {
			if (player.isBusy()) {
				if (player.inCombat()) {
					player.message("You cannot do that whilst fighting!");
				}
				return;
			}
			if (player.getCache().hasKey("tutorial")) {
				player.getCache().remove("tutorial");
			}
			player.teleport(122, 647, false);
			player.message("Skipped tutorial, welcome to Lumbridge");
			ActionSender.sendPlayerOnTutorial(player);
		}
	}
}
