package org.rscemulation.server.packethandler;

import org.apache.mina.common.IoSession;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.World;
import org.rscemulation.server.net.Packet;

public class PlayerLogoutRequest implements PacketHandler {
	
	public void handlePacket(Packet p, IoSession session) throws Exception {
		final Player player = (Player) session.getAttachment();
		if (player.canLogout() && !player.isUnregistered()) {
			player.remove();
			World.unregisterEntity(player);				
		} else
			player.sendCantLogout();
	}
}
