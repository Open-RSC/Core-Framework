package com.openrsc.server.model.entity.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.openrsc.server.Server;
import com.openrsc.server.net.rsc.ActionSender;

public class Social {

	private Player player;

	public Social(Player player) {
		this.player = player;
	}

	/**
	 * Map of players on players friend list
	 */
	private TreeMap<Long, Integer> friendList = new TreeMap<Long, Integer>();

	/**
	 * List of usernameHash's of players on players ignore list
	 */
	private ArrayList<Long> ignoreList = new ArrayList<Long>();

	public void addFriend(long id, int world, String friendName) {
		friendList.put(id, world);
		Server.getPlayerDataProcessor().getDatabase().addFriend(player.getDatabaseID(), id, friendName);
	}

	public void addIgnore(long id, int i) {
		ignoreList.add(id);
		Server.getPlayerDataProcessor().getDatabase().addIgnore(player.getDatabaseID(), id);
	}
	
	public void removeFriend(long id) {
		friendList.remove(id);
		Server.getPlayerDataProcessor().getDatabase().removeFriend(player.getDatabaseID(), id);
	}

	public void removeIgnore(long id) {
		ignoreList.remove(id);
		Server.getPlayerDataProcessor().getDatabase().removeIgnore(player.getDatabaseID(), id);
	}

	public void setIgnoreList(ArrayList<Long> ignoreList) {
		this.ignoreList = ignoreList;
	}
	
	public void setFriendList(TreeMap<Long, Integer> friendList) {
		this.friendList = friendList;
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
	
	public ArrayList<Long> getIgnoreList() {
		return ignoreList;
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
			ActionSender.sendFriendUpdate(player, p.getUsernameHash(), 99);
		}
	}

	public void alertOfLogout(Player p) {
		if (friendList.containsKey(p.getUsernameHash())) {
			ActionSender.sendFriendUpdate(player, p.getUsernameHash(), 0);
		}
	}
}
