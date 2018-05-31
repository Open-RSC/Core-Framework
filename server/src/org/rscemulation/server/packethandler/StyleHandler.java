package org.rscemulation.server.packethandler;

import org.rscemulation.server.packethandler.PacketHandler;
import org.rscemulation.server.util.DataConversions;
import org.rscemulation.server.logging.Logger;
import org.rscemulation.server.logging.model.ExploitLog;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.net.Packet;
import org.apache.mina.common.IoSession;
public class StyleHandler implements PacketHandler {

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		
		if (player != null) {
			int style = p.readByte();
			if (style < 0 || style > 3) {
				Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "StyleHandler (1)", DataConversions.getTimeStamp()));
			} else
				player.setCombatStyle(style);
		}
	}	
}