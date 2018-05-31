package org.rscemulation.server.packethandler;
import org.rscemulation.server.packethandler.PacketHandler;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.net.Packet;
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