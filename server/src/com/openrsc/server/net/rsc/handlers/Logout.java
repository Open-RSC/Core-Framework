package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.util.rsc.MessageType;

public final class Logout implements PacketHandler {
	public void handlePacket(Packet packet, Player player) throws Exception {
		if (player.canLogout()) {
			player.getWorld().getServer().getPluginHandler().handlePlugin(player, "PlayerLogout", new Object[]{player});
			player.unregister(false, "Player logged out");
		} else {
			if (player.getDenyAllLogoutRequests()) {
				player.playerServerMessage(MessageType.QUEST, "Type @or2@::stayin@whi@ if you would actually like to log out.");
			}
		}
	}
}
