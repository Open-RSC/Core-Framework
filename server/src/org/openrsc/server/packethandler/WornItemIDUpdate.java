package org.openrsc.server.packethandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
                        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                        Date date = new Date();
			System.out.println(dateFormat.format(date)+": WornItemIDUpdate exception from " + player.getIP());
		}
	}
}