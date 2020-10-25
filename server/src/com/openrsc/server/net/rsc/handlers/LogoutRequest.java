package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.util.rsc.MessageType;

public class LogoutRequest implements PacketHandler {
	public void handlePacket(Packet packet, Player player) throws Exception {
		if (player.canLogout()) {
			ActionSender.sendLogout(player);
			player.unregister(false, "Player requested log out");
		} else {
			ActionSender.sendCantLogout(player);
			if (player.getDenyAllLogoutRequests()) {
				player.playerServerMessage(MessageType.QUEST, "Type @or2@::stayin@whi@ if you would actually like to log out.");
			}
		}
	}
}
