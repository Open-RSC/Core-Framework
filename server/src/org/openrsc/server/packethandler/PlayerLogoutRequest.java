package org.openrsc.server.packethandler;

import org.apache.mina.common.IoSession;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.World;
import org.openrsc.server.net.Packet;

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
