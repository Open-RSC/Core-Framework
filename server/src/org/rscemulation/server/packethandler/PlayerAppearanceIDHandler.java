package org.rscemulation.server.packethandler;

import org.apache.mina.common.IoSession;
import org.rscemulation.server.logging.Logger;
import org.rscemulation.server.logging.model.ErrorLog;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.net.Packet;
import org.rscemulation.server.util.DataConversions;
public class PlayerAppearanceIDHandler implements PacketHandler {

	public void handlePacket(Packet p, IoSession session) { // throws Exception {
		Player player = (Player) session.getAttachment();
		try {
			if (player != null) {
				int mobCount = p.readShort();
				int[] indicies = new int[mobCount];
				int[] appearanceIDs = new int[mobCount];
				for (int x = 0; x < mobCount; x++) {
					indicies[x] = p.readShort();
					appearanceIDs[x] = p.readShort();
				}
				player.addPlayersAppearanceIDs(indicies, appearanceIDs);
			}
		} catch(Exception ex) {
			Logger.log(new ErrorLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "PlayerAppearanceIDHandler Exception: " + ex, DataConversions.getTimeStamp()));
		}
	}
}