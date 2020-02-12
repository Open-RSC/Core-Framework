package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;

public final class Logout implements PacketHandler {

	public void handlePacket(Packet p, Player player) throws Exception {
		player.getWorld().getServer().getPluginHandler().handlePlugin(player, "PlayerLogout", new Object[]{player});
		player.unregister(false, "Player logged out");
	}
}
