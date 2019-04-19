package com.openrsc.server.model.entity.player;

import com.openrsc.server.Server;
import com.openrsc.server.net.rsc.ActionSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
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

	public boolean addFriend(long id, int world, String friendName) {
		boolean added = Server.getPlayerDataProcessor().getDatabase().addFriend(player.getDatabaseID(), id, friendName);
		if (added)
			friendList.put(id, world);
		return added;
	}

	public boolean addIgnore(long id, int i, String friendName) {
		boolean added = Server.getPlayerDataProcessor().getDatabase().addIgnore(player.getDatabaseID(), id, friendName);
		if (added)
			ignoreList.add(id);
		return added;
	}

	public void removeFriend(long id) {
		friendList.remove(id);
		Server.getPlayerDataProcessor().getDatabase().removeFriend(player.getDatabaseID(), id);
	}

	public void removeIgnore(long id) {
		ignoreList.remove(id);
		Server.getPlayerDataProcessor().getDatabase().removeIgnore(player.getDatabaseID(), id);
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

	public void addFriends(List<Long> longListFromResultSet) {
		for (Long l : longListFromResultSet) {
			friendList.put(l, 0);
		}
	}

	public void addIgnore(List<Long> longListFromResultSet) {
		for (Long l : longListFromResultSet) {
			ignoreList.add(l);
		}
	}

	public void alertOfLogin(Player p) {
		if (friendList.containsKey(p.getUsernameHash()) && (!p.getSettings().getPrivacySetting(1) || p.getSocial().isFriendsWith(player.getUsernameHash()))) {
			ActionSender.sendFriendUpdate(player, p.getUsernameHash());
		}
	}

	public void alertOfLogout(Player p) {
		if (friendList.containsKey(p.getUsernameHash())) {
			ActionSender.sendFriendUpdate(player, p.getUsernameHash());
		}
	}
}
