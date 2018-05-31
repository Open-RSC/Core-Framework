package org.rscemulation.server.packethandler;

import org.rscemulation.server.packethandler.PacketHandler;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.World;
import org.rscemulation.server.net.Packet;
import org.rscemulation.server.net.RSCPacket;


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