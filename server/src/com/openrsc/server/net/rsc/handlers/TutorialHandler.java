package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.Constants;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PacketHandler;

public class TutorialHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet p, Player player) throws Exception {
		if (player == null) {
			return;
		}
		if(player.getLocation().onTutorialIsland()) {
			if (player.isBusy()) {
				if (player.inCombat()) {
					player.message("You cannot do that whilst fighting!");
				}
				return;
			}
			if(player.getCache().hasKey("tutorial")) {
				player.getCache().remove("tutorial");
			}
			player.teleport(122, 647, false);
			player.message("Skipped tutorial, welcome to Lumbridge");
			World.getWorld().sendWorldAnnouncement("New adventurer @gre@" + player.getUsername() + "@whi@ has arrived in lumbridge!");
			ActionSender.sendBox(player, Constants.GameServer.WANT_GLOBAL_CHAT ? "Welcome to Open RSC! % To speak on global chat type ::g <message> or ::p <message> for PK chat" : "Welcome to Open RSC!"
					+ "% To view list of players online type ::onlinelist %"
					+ "To view your player statistics and information type ::info %"
					+ "We hope you enjoy playing! Have fun!", true);
			ActionSender.sendPlayerOnTutorial(player);
		}
	}
}
