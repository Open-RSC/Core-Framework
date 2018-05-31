package org.rscemulation.server.packethandler;

import org.rscemulation.server.packethandler.PacketHandler;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.net.Packet;
import org.rscemulation.server.model.MenuHandler;
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