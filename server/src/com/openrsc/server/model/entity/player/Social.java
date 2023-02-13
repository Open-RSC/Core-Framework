package com.openrsc.server.model.entity.player;

import com.openrsc.server.database.struct.PlayerFriend;
import com.openrsc.server.database.struct.PlayerIgnore;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
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
	private TreeMap<Long, String> friendListNames = new TreeMap<Long, String>();
	private TreeMap<Long, String> friendListFormerNames = new TreeMap<Long, String>();
	/**
	 * List of usernameHash's of players on players ignore list
	 */
	private ArrayList<Long> ignoreList = new ArrayList<Long>();
	private TreeMap<Long, Long> ignoreListFormerNames = new TreeMap<Long, Long>();

	public Social(Player player) {
		this.player = player;
	}

	public void addFriend(long id, int world, String friendName, String friendFormerName) {
		friendList.put(id, world);
		friendListNames.put(id, friendName);
		friendListFormerNames.put(id, friendFormerName);
	}

	public void addIgnore(long id, long formerId) {
		ignoreList.add(id);
		ignoreListFormerNames.put(id, formerId);
	}

	public void removeFriend(long id) {
		friendList.remove(id);
		friendListNames.remove(id);
		friendListFormerNames.remove(id);
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

	public TreeMap<Long, String> getFriendListNames() {
		return friendListNames;
	}

	public TreeMap<Long, String> getFriendListFormerNames() {
		return friendListFormerNames;
	}

	public void setFriendList(TreeMap<Long, Integer> friendList) {
		this.friendList = friendList;
	}

	public ArrayList<Long> getIgnoreList() {
		return ignoreList;
	}

	public TreeMap<Long, Long> getIgnoreListFormerNames() {
		return ignoreListFormerNames;
	}

	public void setIgnoreList(ArrayList<Long> ignoreList) {
		this.ignoreList = ignoreList;
	}

	public int friendCount() {
		return player.getBlockGlobalFriend() ? friendList.size() - 1 : friendList.size();
	}

	public int ignoreCount() {
		return ignoreList.size();
	}

	public void addFriends(final PlayerFriend friends[]) {
		for (PlayerFriend l : friends) {
			friendList.put(l.playerHash, 0);
			friendListNames.put(l.playerHash, l.playerName);
			friendListFormerNames.put(l.playerHash, l.formerName);
		}
		if (player.getConfig().WANT_GLOBAL_FRIEND) {
			friendList.put(Long.MIN_VALUE, 0);
			friendListNames.put(Long.MIN_VALUE, "Global$");
		}
	}

	public void addIgnore(final PlayerIgnore ignores[]) {
		for (PlayerIgnore l : ignores) {
			ignoreList.add(l.ignoredUsernameHash);
			ignoreListFormerNames.put(l.ignoredUsernameHash, l.ignoredFormerUsernameHash);
		}
	}

	public void alertOfLogin(Player player) {
		boolean blockAll = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES, player.isUsingCustomClient())
			== PlayerSettings.BlockingMode.All.id();
		boolean blockNone = player.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_PRIVATE_MESSAGES, player.isUsingCustomClient())
			== PlayerSettings.BlockingMode.None.id();
		if (friendList.containsKey(player.getUsernameHash())
			&& (blockNone
			|| (player.getSocial().isFriendsWith(this.player.getUsernameHash())) && !blockAll)) {
			ActionSender.sendFriendUpdate(this.player, player.getUsernameHash(), player.getUsername(), player.getFormerName());
		} else if (!this.player.getSocial().isFriendsWith(player.getUsernameHash()) && this.player.getUsernameHash() != player.getUsernameHash() && player.getHideOnline() != 1) {
			// this.player.message("@cya@" + player.getUsername() + " has logged in");
		}
	}

	public void alertOfLogout(Player player) {
		if (friendList.containsKey(player.getUsernameHash())) {
			ActionSender.sendFriendUpdate(this.player, player.getUsernameHash(), player.getUsername(), player.getFormerName());
		}
	}

	public void addGlobalFriend(Player player) {
		if (player.getWorld().getServer().getConfig().WANT_GLOBAL_FRIEND) {
			player.getCache().store("setting_block_global_friend", false);
			player.playerServerMessage(MessageType.QUEST, "@whi@You will now be able to see & participate in Global chat features!");

			// Long.MIN_VALUE is the usernameHash of the global friend
			ActionSender.sendFriendUpdate(player, Long.MIN_VALUE, "Global$", "");
		}
	}

	public void removeGlobalFriend(Player player) {
		if (player.getWorld().getServer().getConfig().WANT_GLOBAL_FRIEND) {
			player.getCache().store("setting_block_global_friend", true);
			player.playerServerMessage(MessageType.QUEST, "@whi@You will no longer see any Global chat.");
			player.playerServerMessage(MessageType.QUEST, "@whi@Add @gre@Global$@whi@ as a friend if this was a mistake.");

			// Long.MIN_VALUE is the usernameHash of the global friend
			ActionSender.sendFriendUpdate(player, Long.MIN_VALUE, "Global$", "");
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
			ActionSender.sendFriendUpdate(player, Long.MIN_VALUE, "Global$", "");
		}
	}
}
