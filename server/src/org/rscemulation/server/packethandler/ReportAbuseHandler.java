package org.rscemulation.server.packethandler;

import org.rscemulation.server.packethandler.PacketHandler;
import org.rscemulation.server.util.DataConversions;
import org.rscemulation.server.logging.Logger;
import org.rscemulation.server.logging.model.ReportLog;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.World;
import org.rscemulation.server.net.Packet;

import org.apache.mina.common.IoSession;
public class ReportAbuseHandler implements PacketHandler {
	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		try {
			if (player != null) {
				if (player.isSub()) {
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
				} else
					player.sendMessage("@gre@RSCU @whi@Only subscribers can use the Report Abuse feature");
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}