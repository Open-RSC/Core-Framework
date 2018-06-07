package org.openrsc.server.packethandler;

import org.openrsc.server.packethandler.PacketHandler;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.World;
import org.openrsc.server.net.Packet;
import org.openrsc.server.net.RSCPacket;


import org.apache.mina.common.IoSession;
public class InformationRequestHandler implements PacketHandler {
	public void handlePacket(Packet p, IoSession session) {
		try {
			Player player = (Player)session.getAttachment();
			if (player != null) {
				Player receiver = World.getPlayer(p.readLong());		
				if (receiver != null) {
					switch (((RSCPacket)p).getID()) {
						case 72:
							byte[] localhost = p.getRemainingData();
							receiver.sendMessage(player.getUsername() + "'s IP: " + player.getIP() + ": Local IP: " + new String(localhost));
							break;
					}
				}
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}