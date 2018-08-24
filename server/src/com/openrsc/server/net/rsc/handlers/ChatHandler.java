package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.PlayerSettings;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.model.snapshot.Chatlog;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.query.logs.ChatLog;
import com.openrsc.server.util.rsc.DataConversions;

public final class ChatHandler implements PacketHandler {

	public void handlePacket(Packet p, Player sender) throws Exception {
		if (sender.isMuted()) {
			sender.message("You are muted " + (sender.getMuteExpires() == -1 ? "@red@permanently" : "for @cya@" + sender.getMinutesMuteLeft() + "@whi@ minutes."));
			return;
		}
		if(sender.getSettings()
					.getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_CHAT_MESSAGES)) {
			sender.message("@red@Attention: @whi@You have blocked chat messages. You can find the option in the wrench menu.");
		}
		
		String message = DataConversions.getEncryptedString(p, Short.MAX_VALUE);
		byte[] array = DataConversions.stringToByteArray(message);
		message = DataConversions.byteToString(array, 0, array.length); 
		
		ChatMessage chatMessage = new ChatMessage(sender, message);
		sender.getUpdateFlags().setChatMessage(chatMessage);
		
		GameLogging.addQuery(new ChatLog(sender.getUsername(), chatMessage.getMessageString()));
		World.getWorld().addEntryToSnapshots(new Chatlog(sender.getUsername(), chatMessage.getMessageString()));
	}
}