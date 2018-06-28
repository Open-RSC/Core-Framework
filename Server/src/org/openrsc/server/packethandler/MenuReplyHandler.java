package org.openrsc.server.packethandler;

import org.openrsc.server.packethandler.PacketHandler;
import org.openrsc.server.model.Player;
import org.openrsc.server.net.Packet;
import org.openrsc.server.model.MenuHandler;
import org.apache.mina.common.IoSession;
public class MenuReplyHandler implements PacketHandler {

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		if (player != null) {
			MenuHandler menuHandler = player.getMenuHandler();
			if (menuHandler == null)
				return;
			int option = (int)p.readByte();
			String reply = menuHandler.getOption(option);
			player.resetMenuHandler();
			if (reply == null)
			{
				menuHandler.onMenuCancelled();
				return;
			}
			menuHandler.handleReply(option, reply);
		}
	}
}