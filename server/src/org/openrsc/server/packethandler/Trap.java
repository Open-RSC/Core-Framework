package org.openrsc.server.packethandler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.openrsc.server.packethandler.PacketHandler;
import org.openrsc.server.net.Packet;
import org.openrsc.server.model.Player;
import org.apache.mina.common.IoSession;
public class Trap implements PacketHandler {
	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date date = new Date();
		if (player != null)
			System.out.println(dateFormat.format(date)+": "+player.getUsername() + " was caught by a trap (" + player.getIP() + ")");
	}
}