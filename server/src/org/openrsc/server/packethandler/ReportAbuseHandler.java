package org.openrsc.server.packethandler;

import org.openrsc.server.packethandler.PacketHandler;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.ReportLog;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.World;
import org.openrsc.server.net.Packet;

import org.apache.mina.common.IoSession;
import org.openrsc.server.Config;
public class ReportAbuseHandler implements PacketHandler {
	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		try {
			if (player != null) {
				//if (player.isSub()) {
					if (System.currentTimeMillis() - player.getLastReport() > 60000) {
						Player reported = World.getPlayer(p.readLong());
						int rule = p.readInt();
						if (reported != null) {
							Logger.log(new ReportLog(player.getUsernameHash(), player.getAccount(), player.getIP(), reported.getUsernameHash(), reported.getAccount(), reported.getIP(), rule, DataConversions.getTimeStamp()));
							player.setLastReport();
							player.sendMessage("Thank-you, your abuse report has been received.");
						} else
							player.sendMessage("Invalid player name.");
					} else
						player.sendMessage("You already sent an abuse report under 60 secs ago! Do not abuse this system!");
				//} else
				//	player.sendMessage(Config.PREFIX + " Only subscribers can use the Report Abuse feature");
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}