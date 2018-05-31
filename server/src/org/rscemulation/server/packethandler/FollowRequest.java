package org.rscemulation.server.packethandler;

import org.apache.mina.common.IoSession;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.World;
import org.rscemulation.server.net.Packet;
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