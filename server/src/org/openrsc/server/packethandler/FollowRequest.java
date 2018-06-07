package org.openrsc.server.packethandler;

import org.apache.mina.common.IoSession;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.World;
import org.openrsc.server.net.Packet;
public class FollowRequest implements PacketHandler {
	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		int index = p.readShort();
		if (player != null) {
			Player affectedPlayer = World.getPlayer(index);
			if (affectedPlayer != null) {
				if (!player.isBusy()) {
					player.resetAllExceptDMing();
					player.setFollowing(affectedPlayer, 1);
					player.sendMessage("Now following " + affectedPlayer.getUsername());
				}
			}
		}
	}
}