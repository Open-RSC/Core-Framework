package org.rscemulation.server.packethandler;

import org.rscemulation.server.Config;
import org.rscemulation.server.packethandler.PacketHandler;
import org.rscemulation.server.util.ChatFilter;
import org.rscemulation.server.util.DataConversions;
import org.rscemulation.server.logging.Logger;
import org.rscemulation.server.logging.model.ChatLog;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.World;
import org.rscemulation.server.net.Packet;

import org.apache.mina.common.IoSession;
public class ChatHandler implements PacketHandler {
	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player sender = (Player)session.getAttachment();
		if (sender != null) {
			if (sender.getLocation().onTutorialIsland())
			{
				sender.sendMessage(Config.PREFIX + "Chat is disabled on tutorial island.");
				return;
			}
			if (!World.muted || sender.isMod()) {
				if (sender.getMuted() == 0) {
					byte[] data = p.getData();
					String message = DataConversions.byteToString(data, 0, data.length);
					if (message.length() > 85)
						return;
						Logger.log(new ChatLog(sender.getUsernameHash(), sender.getAccount(), sender.getIP(), message, DataConversions.getTimeStamp()));
					message = ChatFilter.censor(message);
					sender.addMessageToChatQueue(message);
				} else
					sender.sendMessage(Config.PREFIX + "You are muted");
			} else
				sender.sendMessage(Config.PREFIX + "The world is muted");
		}
	}
}