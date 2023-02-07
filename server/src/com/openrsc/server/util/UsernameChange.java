package com.openrsc.server.util;

import com.openrsc.server.Server;
import com.openrsc.server.database.struct.UsernameChangeType;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UsernameChange {
	private static final Logger LOGGER = LogManager.getLogger(UsernameChange.class);

	Player commandUser; // person who instigated the name change
	Server server;
	int targetPlayerDatabaseId;
	String formerFormerName;
	String formerName;
	String newUsername;
	UsernameChangeType changeType;
	String reason;

	public UsernameChange(Player commandUser, Server server, int targetPlayerDatabaseId, String formerFormerName, String formerName, String newUsername, UsernameChangeType changeType, String reason) {
		this.commandUser = commandUser;
		this.server = server;
		this.targetPlayerDatabaseId = targetPlayerDatabaseId;
		this.formerFormerName = formerFormerName;
		this.formerName = formerName;
		this.newUsername = newUsername;
		this.changeType = changeType;
		this.reason = reason;
	}

	public void doChangeUsername() {
		String commandUserUsername = null == commandUser ? "(login/self)" : commandUser.getUsername();
		long formerUsernameHash = DataConversions.usernameToHash(formerName);
		Player shouldBeOffline = server.getWorld().getPlayer(formerUsernameHash);
		if (null != shouldBeOffline && !shouldBeOffline.isUnregistering()) {
			LOGGER.info(formerName + " name change attempted to process while still online, aborted.");
			return;
		}

		LOGGER.info("Processing name change for " + formerName + " to " + newUsername);
		long formerNameHash = DataConversions.usernameToHash(formerName);
		long newUsernameHash = DataConversions.usernameToHash(newUsername);

		// update everyone online who has this guy on their friends/ignore list
		for (final Player player : server.getWorld().getPlayers()) {
			if (player.getSocial().getFriendList().getOrDefault(formerNameHash, -1) != -1) {
				// remove old friend
				player.getSocial().removeFriend(formerNameHash);

				// add new friend
				String formerNameFriendList = formerName;
				if (changeType == UsernameChangeType.RELEASED) {
					formerNameFriendList = "$" + formerName;
				}
				player.getSocial().addFriend(newUsernameHash, 0, newUsername, formerNameFriendList);

				// alert player of the change
				ActionSender.updateFriendListBecauseNameChange(player, formerName, newUsername);
			}

			if (player.getSocial().getIgnoreList().indexOf(formerNameHash) != -1) {
				// remove old ignore
				player.getSocial().removeIgnore(formerNameHash);

				// add new ignore
				player.getSocial().addIgnore(newUsernameHash, formerNameHash);

				// alert player of the change
				ActionSender.sendUpdateIgnoreBecauseOfNameChange(player, DataConversions.hashToUsername(newUsernameHash), DataConversions.hashToUsername(formerNameHash), true);
			}
		}

		server.getDatabase().renamePlayer(targetPlayerDatabaseId, formerFormerName, formerName, newUsername, changeType.id());
		server.getDatabase().insertFormerName(targetPlayerDatabaseId, formerName, commandUserUsername, changeType.id(), reason);

		if (null != commandUser) {
			commandUser.message("@red@" + formerName + "@whi@ was successfully renamed to @cya@" + newUsername);
		}
	}
}
