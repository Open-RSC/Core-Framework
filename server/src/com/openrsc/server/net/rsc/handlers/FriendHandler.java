package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.Constants;
import com.openrsc.server.model.PrivateMessage;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.util.rsc.DataConversions;

public final class FriendHandler implements PacketHandler {

	private final int MAX_FRIENDS = 100;

	private final int MEMBERS_MAX_FRIENDS = 200;

	public void handlePacket(Packet p, Player player) throws Exception {
		int pID = p.getID();

		long friend = DataConversions.usernameToHash(p.readString());

		int packetOne = OpcodeIn.SOCIAL_ADD_FRIEND.getOpcode();
		int packetTwo = OpcodeIn.SOCIAL_REMOVE_FRIEND.getOpcode();
		int packetThree = OpcodeIn.SOCIAL_ADD_IGNORE.getOpcode();
		int packetFour = OpcodeIn.SOCIAL_REMOVE_IGNORE.getOpcode();
		int packetFive = OpcodeIn.SOCIAL_SEND_PRIVATE_MESSAGE.getOpcode();

		Player affectedPlayer = World.getWorld().getPlayer(friend);
		if (pID == packetOne) { // Add friend
			int maxFriends = Constants.GameServer.MEMBER_WORLD ? MEMBERS_MAX_FRIENDS
				: MAX_FRIENDS;
			if (player.getSocial().friendCount() >= maxFriends) {
				player.message("Friend list is full");
				ActionSender.sendFriendList(player);
				return;
			}

			boolean added = player.getSocial().addFriend(friend, 0, DataConversions.hashToUsername(friend));
			if (added) {
				ActionSender.sendFriendUpdate(player, friend);
				if (affectedPlayer != null && affectedPlayer.loggedIn()) {
					if (affectedPlayer.getSocial().isFriendsWith(player.getUsernameHash())) {
						ActionSender.sendFriendUpdate(affectedPlayer, player.getUsernameHash());
						ActionSender.sendFriendUpdate(player, friend);
					} else if (!affectedPlayer.getSettings().getPrivacySetting(1)) {
						ActionSender.sendFriendUpdate(player, friend);
					}
				}
			}
		} else if (pID == packetTwo) { // Remove friend
			player.getSocial().removeFriend(friend);
			if (affectedPlayer != null && affectedPlayer.loggedIn()) {
				if (player.getSettings().getPrivacySetting(1) && affectedPlayer.getSocial().isFriendsWith(player.getUsernameHash())) {
					ActionSender.sendFriendUpdate(affectedPlayer, player.getUsernameHash());
				}
			}
		} else if (pID == packetThree) { // Add ignore
			int maxFriends = Constants.GameServer.MEMBER_WORLD ? MEMBERS_MAX_FRIENDS
				: MAX_FRIENDS;
			if (player.getSocial().ignoreCount() >= maxFriends) {
				player.message("Ignore list full");
				return;
			}
			boolean added = player.getSocial().addIgnore(friend, 0, DataConversions.hashToUsername(friend));
			if (added)
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
					DataConversions.getEncryptedString(p, 32576)));
			player.addPrivateMessage(new PrivateMessage(player, message, friend));
		}
	}
}
