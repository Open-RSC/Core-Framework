package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.database.impl.mysql.queries.logging.ChatLog;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.model.snapshot.Chatlog;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.ChatStruct;
import com.openrsc.server.util.MessageFilter;
import com.openrsc.server.util.rsc.DataConversions;

public final class ChatHandler implements PayloadProcessor<ChatStruct, OpcodeIn> {

	public void process(ChatStruct payload, Player sender) throws Exception {
		if (sender.isMuted()) {
			if (!sender.isShadowMuted()) {
				sender.message("You have been " + (sender.getMuteExpires() == -1 ? "permanently" : "temporarily") + " muted.");
				if (sender.getMuteExpires() != -1) {
					sender.message("This mute will expire in " + sender.getMinutesMuteLeft() + " minutes.");
					sender.message("To prevent further mutes please read the rules");
				}
			}
		}

		if (!sender.hasElevatedPriveledges() && sender.getLocation().onTutorialIsland()) {
			sender.message("Once you finish the tutorial, typing here sends messages to nearby players");
		}

		String message = payload.message;

		final boolean babyModeFiltered = sender.isBabyModeFiltered();
		boolean mutedChat = (sender.getLocation().onTutorialIsland() || sender.isMuted() || babyModeFiltered) && !sender.hasElevatedPriveledges();

		if (!mutedChat) {
			message = MessageFilter.filter(sender, message, "public chat");
		}

		if (babyModeFiltered) {
			sender.message("Sorry, but someone we banned for breaking our rules is actively throwing a tantrum right now.");
			sender.message("New accounts are not allowed to speak until they've reached " + sender.getConfig().BABY_MODE_LEVEL_THRESHOLD + " total level during this time.");
			sender.getWorld().getServer().getDiscordService().reportBabyModeFilteredMessageToDiscord(sender, message, "public chat");
		}

		if (!sender.speakTongues) {
			message = DataConversions.upperCaseAllFirst(
				DataConversions.stripBadCharacters(message));
		} else {
			message = DataConversions.speakTongues(message);
		}

		ChatMessage chatMessage = null;

		// chat messages while possessing another Player/NPC get sent by the entity being possessed
		if (sender.getPossessing() != null) {
			if (sender.getPossessing() instanceof Player && sender.isAdmin()) {
				chatMessage = new ChatMessage((Player)sender.getPossessing(), message, mutedChat);
				if (!chatMessage.getMessageString().trim().startsWith("::")) {
					sender.getPossessing().getUpdateFlags().setChatMessage(chatMessage);
				}
			} else if (sender.getPossessing() instanceof Npc) {
				chatMessage = new ChatMessage((Npc)sender.getPossessing(), message, null);
				sender.getPossessing().getUpdateFlags().setChatMessage(chatMessage);
			}
		}

		// Normal chat messages
		if (chatMessage == null) {
			chatMessage = new ChatMessage(sender, message, mutedChat);
			sender.getUpdateFlags().setChatMessage(chatMessage);
			sender.getUpdateFlags().setPluginChatMessage(false);
		}

		// We do not want muted/tutorial chat to be logged
		if (mutedChat) {
			return;
		}

		sender.getWorld().getServer().getGameLogger().addQuery(new ChatLog(sender.getWorld(), sender.getUsername(), chatMessage.getMessageString()));
		sender.getWorld().addEntryToSnapshots(new Chatlog(sender.getUsername(), chatMessage.getMessageString()));
	}
}
