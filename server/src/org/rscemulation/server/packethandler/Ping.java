package org.rscemulation.server.packethandler;

import org.apache.mina.common.IoSession;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.net.Packet;
public class Ping implements PacketHandler {
	public void handlePacket(Packet p, IoSession session)
		throws
			Exception {
		((Player)session.getAttachment()).ping();
		((Player)session.getAttachment()).getActionSender().sendPing();
	}
}
