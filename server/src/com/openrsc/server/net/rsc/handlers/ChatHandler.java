package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.database.impl.mysql.queries.logging.ChatLog;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.model.snapshot.Chatlog;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.util.rsc.DataConversions;

public final class ChatHandler implements PacketHandler {

	public void handlePacket(Packet packet, Player sender) throws Exception {
		if (sender.isMuted()) {
			if (sender.getMuteNotify()) {
				//sender.message(sender.getConfig().MESSAGE_PREFIX + "You are muted " + (sender.getMuteExpires() == -1 ? "@red@permanently" : "for @cya@" + sender.getMinutesMuteLeft() + "@whi@ minutes."));
				sender.message("You have been " + (sender.getMuteExpires() == -1 ? "permanently" : "temporarily") + " due to breaking a rule");
				if (sender.getMuteExpires() != -1) {
					sender.message("This mute will remain for a further " + DataConversions.formatTimeString(sender.getMinutesMuteLeft()));
				}
				sender.message("To prevent further mutes please read the rules");
			}
		}

		if (!sender.hasElevatedPriveledges() && sender.getLocation().onTutorialIsland()) {
			sender.message("Once you finish the tutorial, typing here sends messages to nearby players");
		}

		String message = DataConversions.upperCaseAllFirst(
			DataConversions.stripBadCharacters(
				DataConversions.getEncryptedString(packet, Short.MAX_VALUE)));


		boolean mutedChat = (sender.getLocation().onTutorialIsland() || sender.isMuted()) && !sender.hasElevatedPriveledges();

		ChatMessage chatMessage = new ChatMessage(sender, message, mutedChat);
		sender.getUpdateFlags().setChatMessage(chatMessage);

		// We do not want muted/tutorial chat to be logged
		if (mutedChat) {
			return;
		}

		sender.getWorld().getServer().getGameLogger().addQuery(new ChatLog(sender.getWorld(), sender.getUsername(), chatMessage.getMessageString()));
		sender.getWorld().addEntryToSnapshots(new Chatlog(sender.getUsername(), chatMessage.getMessageString()));
	}
}
