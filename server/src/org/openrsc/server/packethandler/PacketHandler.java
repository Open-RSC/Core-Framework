package org.openrsc.server.packethandler;

import org.openrsc.server.net.Packet;
import org.apache.mina.common.IoSession;

public interface PacketHandler {
	public void handlePacket(Packet p, IoSession session) throws Exception;
}