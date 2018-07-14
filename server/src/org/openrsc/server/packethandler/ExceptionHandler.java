package org.openrsc.server.packethandler;

import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.ErrorLog;
import org.openrsc.server.model.World;
import org.openrsc.server.packethandler.PacketHandler;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.model.Player;
import org.openrsc.server.net.Packet;
import org.apache.mina.common.IoSession;
public class ExceptionHandler implements PacketHandler {

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		Logger.log(new ErrorLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "Client-side Exception : " + p.readString(), DataConversions.getTimeStamp()));
		if (!player.isUnregistered())
			World.unregisterEntity(player);
	}
}
