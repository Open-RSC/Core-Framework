package org.rscemulation.server.packethandler;

import org.rscemulation.server.packethandler.PacketHandler;
import org.rscemulation.server.net.Packet;
import org.rscemulation.server.model.Player;
import org.apache.mina.common.IoSession;
public class Trap implements PacketHandler {
	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		if (player != null)
			System.out.println(player.getUsername() + " was caught by a trap (" + player.getIP() + ")");
	}
}