package org.openrsc.server.packethandler;
import org.openrsc.server.packethandler.PacketHandler;
import org.openrsc.server.model.Player;
import org.openrsc.server.net.Packet;
import org.apache.mina.common.IoSession;

public class WornItemIDUpdate implements PacketHandler {
	public void handlePacket(Packet p, IoSession session) {
		Player player = (Player)session.getAttachment();
		try {
			if (player != null) {
				int mobCount = p.readShort();
				int[] indicies = new int[mobCount];
				int[] wornItemIDs = new int[mobCount];
				for (int index = 0; index < mobCount; index++) {
					indicies[index] = p.readShort();
					wornItemIDs[index] = p.readShort();
				}
				player.addWornItemAppearanceIDs(indicies, wornItemIDs);
			}
		} catch(Exception ex) {
			System.out.println("WornItemIDUpdate exception from " + player.getIP());
		}
	}
}