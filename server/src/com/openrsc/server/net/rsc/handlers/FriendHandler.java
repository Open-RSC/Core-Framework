package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.GlobalMessage;
import com.openrsc.server.model.PrivateMessage;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.PlayerSettings;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.util.rsc.DataConversions;

public final class FriendHandler implements PacketHandler {

	private final int MAX_FRIENDS = 100;

	private final int MEMBERS_MAX_FRIENDS = 200;

	public void handlePacket(Packet packet, Player player) {
		int pID = packet.getID();

		String friendName;
		if (player.isUsingAuthenticClient()) {
			friendName = packet.readZeroPaddedString();
		} else {
			friendName = packet.readString();
		}
		long friend = DataConversions.usernameToHash(friendName);

		int packetOne = OpcodeIn.SOCIAL_ADD_FRIEND.getOpcode();
		int packetTwo = OpcodeIn.SOCIAL_REMOVE_FRIEND.getOpcode();
		int packetThree = OpcodeIn.SOCIAL_ADD_IGNORE.getOpcode();
		int packetFour = OpcodeIn.SOCIAL_REMOVE_IGNORE.getOpcode();
		int packetFive = OpcodeIn.SOCIAL_SEND_PRIVATE_MESSAGE.getOpcode();
		int packetSix = OpcodeIn.SOCIAL_ADD_DELAYED_IGNORE.getOpcode();

		Player affectedPlayer = player.getWorld().getPlayer(friend);
		if (pID == packetOne) { // Add friend
			int maxFriends = player.getConfig().MEMBER_WORLD ? MEMBERS_MAX_FRIENDS
				: MAX_FRIENDS;
			if (player.getSocial().friendCount() >= maxFriends) {
				player.message("Friend list is full");
				ActionSender.sendFriendList(player);
				return;
			}

			if (friendName.equalsIgnoreCase("Global$")) {
				player.getSocial().addGlobalFriend(player);
				return;
			}

			player.getSocial().addFriend(friend, 0, DataConversions.hashToUsername(friend));
			ActionSender.sendFriendUpdate(player, friend);
			if (affectedPlayer != null && affectedPlayer.loggedIn()) {
				boolean blockAll = affectedPlayer.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES, affectedPlayer.isUsingAuthenticClient())
					== PlayerSettings.BlockingMode.All.id();
				boolean blockNone = affectedPlayer.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES, affectedPlayer.isUsingAuthenticClient())
					== PlayerSettings.BlockingMode.None.id();
				if (!blockAll && affectedPlayer.getSocial().isFriendsWith(player.getUsernameHash())) {
					ActionSender.sendFriendUpdate(affectedPlayer, player.getUsernameHash());
					ActionSender.sendFriendUpdate(player, friend);
				} else if (blockNone && !affectedPlayer.getSocial().isFriendsWith(player.getUsernameHash())) {
					ActionSender.sendFriendUpdate(player, friend);
				}
			}
		} else if (pID == packetTwo) { // Remove friend
			if (friendName.equalsIgnoreCase("Global$")) {
				player.getSocial().removeGlobalFriend(player);
				return;
			}

			player.getSocial().removeFriend(friend);
			if (affectedPlayer != null && affectedPlayer.loggedIn()) {
				boolean blockAll = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES, player.isUsingAuthenticClient())
					== PlayerSettings.BlockingMode.All.id();
				if (!blockAll && affectedPlayer.getSocial().isFriendsWith(player.getUsernameHash())) {
					ActionSender.sendFriendUpdate(affectedPlayer, player.getUsernameHash());
				}
			}
		} else if (pID == packetThree) { // Add ignore
			int maxFriends = player.getConfig().MEMBER_WORLD ? MEMBERS_MAX_FRIENDS
				: MAX_FRIENDS;
			if (player.getSocial().ignoreCount() >= maxFriends) {
				player.message("Ignore list full");
				ActionSender.sendIgnoreList(player);
				return;
			}
			player.getSocial().addIgnore(friend, 0, DataConversions.hashToUsername(friend));
			ActionSender.sendIgnoreList(player);
		} else if (pID == packetFour) { // Remove ignore
			player.getSocial().removeIgnore(friend);
		} else if (pID == packetFive) { // Send PM
			if (player.getLocation().onTutorialIsland()) {
				player.message("@cya@Once you finish the tutorial, this lets you send messages to your friends");
				return;
			}
			String message = DataConversions.upperCaseAllFirst(
				DataConversions.stripBadCharacters(
					DataConversions.getEncryptedString(packet, 32576)));
			if (friendName.toLowerCase().startsWith("global$") && player.getConfig().WANT_GLOBAL_FRIEND) {
				player.getWorld().addGlobalMessage(new GlobalMessage(player, message));
			}
			else {
				player.addPrivateMessage(new PrivateMessage(player, message, friend));
			}
		} else if (pID == packetSix) {
			int maxFriends = player.getConfig().MEMBER_WORLD ? MEMBERS_MAX_FRIENDS
				: MAX_FRIENDS;
			if (player.getSocial().ignoreCount() >= maxFriends) {
				player.message("Ignore list full");
				return;
			}
			player.getSocial().addIgnore(friend, 0, DataConversions.hashToUsername(friend));
			ActionSender.sendIgnoreList(player);
			player.getWorld().getServer().getGameEventHandler().add(new DelayedEvent(player.getWorld(), null, 150000, "Delayed ignore") {
				public void run() {
					player.getSocial().removeIgnore(friend);
					ActionSender.sendIgnoreList(player);
				}
			});
		}
	}
}

