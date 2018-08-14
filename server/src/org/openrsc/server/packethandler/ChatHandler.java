package org.openrsc.server.packethandler;

import org.openrsc.server.Config;
import org.openrsc.server.util.ChatFilter;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.ChatLog;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.World;
import org.openrsc.server.net.Packet;

import org.apache.mina.common.IoSession;
public class ChatHandler implements PacketHandler {
	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player sender = (Player)session.getAttachment();
		if (sender != null) {
			if (sender.getLocation().onTutorialIsland())
			{
				sender.sendMessage(Config.getPrefix() + "Chat is disabled on tutorial island.");
				return;
			}
			if (!World.muted || sender.isSuperMod()) {
				if (sender.getMuted() == 0) {
					byte[] data = p.getData();
					String message = DataConversions.byteToString(data, 0, data.length);
					if (message.length() > 85)
						return;
						Logger.log(new ChatLog(sender.getUsernameHash(), sender.getAccount(), sender.getIP(), message, DataConversions.getTimeStamp()));
					message = ChatFilter.censor(message);
					sender.addMessageToChatQueue(message);
				} else
					sender.sendMessage(Config.getPrefix() + "You are muted");
			} else
				sender.sendMessage(Config.getPrefix() + "The world is muted");
		}
	}
}