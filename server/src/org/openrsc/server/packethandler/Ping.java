package org.openrsc.server.packethandler;

import org.apache.mina.common.IoSession;
import org.openrsc.server.model.Player;
import org.openrsc.server.net.Packet;
public class Ping implements PacketHandler {
	public void handlePacket(Packet p, IoSession session)
		throws
			Exception {
		((Player)session.getAttachment()).ping();
		((Player)session.getAttachment()).getActionSender().sendPing();
	}
}
