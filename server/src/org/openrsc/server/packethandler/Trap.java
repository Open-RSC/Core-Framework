package org.openrsc.server.packethandler;

import org.openrsc.server.packethandler.PacketHandler;
import org.openrsc.server.net.Packet;
import org.openrsc.server.model.Player;
import org.apache.mina.common.IoSession;
public class Trap implements PacketHandler {
	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		if (player != null)
			System.out.println(player.getUsername() + " was caught by a trap (" + player.getIP() + ")");
	}
}