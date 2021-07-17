package com.openrsc.server.model.entity.player;

import com.openrsc.server.database.struct.PlayerFriend;
import com.openrsc.server.database.struct.PlayerIgnore;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.MessageType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public class Social {

	private Player player;
	/**
	 * Map of players on players friend list
	 */
	private TreeMap<Long, Integer> friendList = new TreeMap<Long, Integer>();
	/**
	 * List of usernameHash's of players on players ignore list
	 */
	private ArrayList<Long> ignoreList = new ArrayList<Long>();

	public Social(Player player) {
		this.player = player;
	}

	public void addFriend(long id, int world, String friendName) {
		friendList.put(id, world);
	}

	public void addIgnore(long id, int i, String friendName) {
		ignoreList.add(id);
	}

	public void removeFriend(long id) {
		friendList.remove(id);
	}

	public void removeIgnore(long id) {
		ignoreList.remove(id);
	}

	public boolean isFriendsWith(long usernameHash) {
		return friendList.containsKey(usernameHash);
	}

	public boolean isIgnoring(long usernameHash) {
		return ignoreList.contains(usernameHash);
	}

	public Collection<Entry<Long, Integer>> getFriendListEntry() {
		return friendList.entrySet();
	}

	public TreeMap<Long, Integer> getFriendList() {
		return friendList;
	}

	public void setFriendList(TreeMap<Long, Integer> friendList) {
		this.friendList = friendList;
	}

	public ArrayList<Long> getIgnoreList() {
		return ignoreList;
	}

	public void setIgnoreList(ArrayList<Long> ignoreList) {
		this.ignoreList = ignoreList;
	}

	public int friendCount() {
		return friendList.size();
	}

	public int ignoreCount() {
		return ignoreList.size();
	}

	public void addFriends(final PlayerFriend friends[]) {
		for (PlayerFriend l : friends) {
			friendList.put(l.playerHash, 0);
		}
		if (player.getConfig().WANT_GLOBAL_FRIEND) {
			friendList.put(Long.MIN_VALUE, 0);
		}
	}

	public void addIgnore(final PlayerIgnore ignores[]) {
		for (PlayerIgnore l : ignores) {
			ignoreList.add(l.playerHash);
		}
	}

	public void alertOfLogin(Player player) {
		boolean blockAll = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES, player.isUsingCustomClient())
			== PlayerSettings.BlockingMode.All.id();
		boolean blockNone = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES, player.isUsingCustomClient())
			== PlayerSettings.BlockingMode.None.id();
		if (friendList.containsKey(player.getUsernameHash())
			&& (blockNone
			|| (player.getSocial().isFriendsWith(player.getUsernameHash())) && !blockAll)) {
			ActionSender.sendFriendUpdate(this.player, player.getUsernameHash());
		}
	}

	public void alertOfLogout(Player player) {
		if (friendList.containsKey(player.getUsernameHash())) {
			ActionSender.sendFriendUpdate(this.player, player.getUsernameHash());
		}
	}

	public void addGlobalFriend(Player player) {
		if (player.getWorld().getServer().getConfig().WANT_GLOBAL_FRIEND) {
			player.getCache().store("setting_block_global_friend", false);
			player.playerServerMessage(MessageType.QUEST, "@whi@You will now be able to see & participate in Global chat features!");

			// Long.MIN_VALUE is the usernameHash of the global friend
			ActionSender.sendFriendUpdate(player, Long.MIN_VALUE);
		}
	}

	public void removeGlobalFriend(Player player) {
		if (player.getWorld().getServer().getConfig().WANT_GLOBAL_FRIEND) {
			player.getCache().store("setting_block_global_friend", true);
			player.playerServerMessage(MessageType.QUEST, "@whi@You will no longer see any Global chat.");
			player.playerServerMessage(MessageType.QUEST, "@whi@Add @gre@Global$@whi@ as a friend if this was a mistake.");

			// Long.MIN_VALUE is the usernameHash of the global friend
			ActionSender.sendFriendUpdate(player, Long.MIN_VALUE);
		}
	}

	public void toggleGlobalFriend(Player player) {
		if (player.getWorld().getServer().getConfig().WANT_GLOBAL_FRIEND) {
			boolean currentSetting;
			try {
				currentSetting = player.getCache().getBoolean("setting_block_global_friend");
			} catch (NoSuchElementException e) {
				currentSetting = false;
			}

			if (currentSetting) {
				player.playerServerMessage(MessageType.QUEST, "You will now be able to see & participate in Global chat features.");
			} else {
				player.playerServerMessage(MessageType.QUEST, "You will no longer see any Global chat.");
				player.playerServerMessage(MessageType.QUEST, "Manually remove the Global$ friend or relog.");
			}
			player.getCache().store("setting_block_global_friend", !currentSetting);

			// Long.MIN_VALUE is the usernameHash of the global friend
			ActionSender.sendFriendUpdate(player, Long.MIN_VALUE);
		}
	}
}
