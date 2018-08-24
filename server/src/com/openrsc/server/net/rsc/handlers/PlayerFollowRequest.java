package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;

public final class PlayerFollowRequest implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Player player) throws Exception {

		Player affectedPlayer = world.getPlayer(p.readShort());
		if (affectedPlayer == null) {
			player.setSuspiciousPlayer(true);
			return;
		}
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		if (System.currentTimeMillis() - player.getLastRun() < 3000)
			return;
		player.resetAll();
		player.setFollowing(affectedPlayer, 1);
		player.message("Following " + affectedPlayer.getUsername());
	}
}
