package org.rscemulation.server.packethandler;

import org.rscemulation.server.logging.Logger;
import org.rscemulation.server.logging.model.ErrorLog;
import org.rscemulation.server.model.World;
import org.rscemulation.server.packethandler.PacketHandler;
import org.rscemulation.server.util.DataConversions;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.net.Packet;
import org.apache.mina.common.IoSession;
public class ExceptionHandler implements PacketHandler {

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		Logger.log(new ErrorLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "Client-side Exception : " + p.readString(), DataConversions.getTimeStamp()));
		if (!player.isUnregistered())
			World.unregisterEntity(player);
	}
}
