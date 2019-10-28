package com.openrsc.server.content.clan;

import com.openrsc.server.constants.Constants;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.MessageType;

import java.util.ArrayList;
import java.util.Arrays;

public class Clan {
	private int id;
	private String name;
	private String tag;
	private ClanPlayer leader;
	private ArrayList<ClanPlayer> players = new ArrayList<ClanPlayer>();
	private int[] clanSetting = new int[3];
	private int clanPoints;

	private final World world;

	public Clan (World world) {
		this.world = world;
	}

	public ClanPlayer addPlayer(Player player) {
		if (getPlayers().size() < Constants.MAX_CLAN_SIZE) {
			player.setClan(this);

			ClanPlayer clanMember = new ClanPlayer(player.getUsername());
			clanMember.setRank(ClanRank.NORMAL);
			clanMember.setPlayerReference(player);
			if (leader == null) {
				clanMember.setRank(ClanRank.LEADER);
				leader = clanMember;
				Arrays.fill(clanSetting, 1);
			}
			getPlayers().add(clanMember);

			messageChat(player, player.getUsername() + " has joined the clan!");
			updateClanGUI();
			ActionSender.sendClanSetting(clanMember.getPlayerReference());

			if (getPlayers().size() > 1) {
				getWorld().getClanManager().saveClanChanges(this);
			}
			return clanMember;
		}
		return null;
	}

	public void removePlayer(String username) {
		ClanPlayer member = getPlayer(username);
		if (member == null) {
			return;
		}

		if (member.isOnline()) {
			ActionSender.sendLeaveClan(member.getPlayerReference());
			member.getPlayerReference().setClan(null);
			member.getPlayerReference().message("You have left clan: " + getClanName());
		}

		getPlayers().remove(member);
		messageClanInfo(username + " left " + getClanName());

		if (getPlayers().size() >= 1) {
			if (getLeader().getUsername().equalsIgnoreCase(username)) {
				setLeader(getPlayers().get(0));
				getLeader().setRank(ClanRank.LEADER);
				messageClanInfo("@red@Your clan leader has left the clan!");
				messageClanInfo("@yel@" + getLeader().getUsername() + " is the new clan leader!");
			}
			getWorld().getClanManager().saveClanChanges(this);
		} else if (getPlayers().size() == 0) {
			getWorld().getClanManager().deleteClan(this);
		}
		updateClanGUI();
	}

	public void updateRankPlayer(Player player, String username, int newRank) {
		ClanPlayer member = getPlayer(username);
		if (member == null) {
			return;
		}

		ClanRank setRank = ClanRank.getRankFor(newRank);
		if (member.getRank().rankIndex == newRank) {
			return;
		}

		if (getLeader().getUsername().equalsIgnoreCase(player.getUsername())) {
			if (newRank == 1) {
				player.getClan().getPlayer(player.getUsername()).setRank(ClanRank.NORMAL);
				setLeader(member);
				getLeader().setRank(ClanRank.LEADER);
				messageClanInfo("@red@Your clan leader has passed the leadership!");
				messageClanInfo("@yel@" + getLeader().getUsername() + " is the new clan leader!");
				getWorld().getClanManager().saveClanChanges(this);
				ActionSender.sendClanSetting(player);
			} else {
				if (newRank == 2) {
					messageClanInfo("Congratulations! " + member.getUsername() + " has been promoted to " + ClanRank.getRankFor(newRank).name().toLowerCase() + " rank.");
				} else {
					messageClanInfo(member.getUsername() + " has been put back to " + ClanRank.getRankFor(newRank).name().toLowerCase() + " rank.");
				}
				member.setRank(setRank);
				getWorld().getClanManager().updateClanRankPlayer(member);
			}
			updateClanGUI();
			if (member.isOnline()) {
				ActionSender.sendClanSetting(member.getPlayerReference());
			}
		}
	}

	public void updateClanGUI() {
		for (ClanPlayer m : players) {
			if (m.isOnline()) {
				ActionSender.sendClan(m.getPlayerReference());
			}
		}
	}

	public void updateClanSettings() {
		for (ClanPlayer m : players) {
			if (m.isOnline()) {
				ActionSender.sendClanSetting(m.getPlayerReference());
			}
		}
	}

	public ClanPlayer getPlayer(String username) {
		for (ClanPlayer p : getPlayers()) {
			if (p.getUsername().equalsIgnoreCase(username)) {
				return p;
			}
		}
		return null;
	}

	public ArrayList<ClanPlayer> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<ClanPlayer> clanPlayers) {
		this.players = clanPlayers;
	}

	public void messageChat(Player player, String string) {
		for (ClanPlayer cMember : getPlayers()) {
			if (cMember.getPlayerReference() != null) {
				Player clanPlayer = cMember.getPlayerReference();
				ActionSender.sendMessage(clanPlayer, player, 1, MessageType.CLAN_CHAT, "@whi@[@cla@" + getClanName() + "@whi@] " + string, player.getIcon());
			}
		}
	}

	public void messageClanInfo(String string) {
		for (ClanPlayer cMember : getPlayers()) {
			if (cMember.getPlayerReference() != null) {
				Player clanPlayer = cMember.getPlayerReference();
				ActionSender.sendPlayerServerMessage(clanPlayer, MessageType.CLAN_CHAT, "@whi@[@cla@" + getClanName() + "@whi@] " + string);
			}
		}
	}

	public String getClanName() {
		return name;
	}

	public void setClanName(String name) {
		this.name = name;
	}

	public String getClanTag() {
		return tag;
	}

	public void setClanTag(String tag) {
		this.tag = tag;
	}

	public int getClanID() {
		return id;
	}

	public void setClanID(int id) {
		this.id = id;
	}

	public ClanPlayer getLeader() {
		return leader;
	}

	public void setLeader(ClanPlayer leader) {
		this.leader = leader;
	}

	public int getKickSetting() {
		return clanSetting[0];
	}

	public void setKickSetting(int state) {
		this.clanSetting[0] = state;
	}

	public int getInviteSetting() {
		return clanSetting[1];
	}

	public void setInviteSetting(int state) {
		this.clanSetting[1] = state;
	}

	public boolean isAllowed(int setting, Player p) {
		if (p.getClan() != null) {
			if (clanSetting[setting] == 0) {
				return true;
			} else if (clanSetting[setting] == 1 && p.getClan().getPlayer(p.getUsername()).getRank().equals(ClanRank.LEADER)) {
				return true;
			} else if ((clanSetting[setting] == 2 ||clanSetting[setting] == 3) && (p.getClan().getPlayer(p.getUsername()).getRank().equals(ClanRank.LEADER) || p.getClan().getPlayer(p.getUsername()).getRank().equals(ClanRank.GENERAL))) {
				return true;
			}
		}
		return false;
	}

	public int getAllowSearchJoin() {
		return clanSetting[2];
	}

	public void setAllowSearchJoin(int state) {
		this.clanSetting[2] = state;
	}

	public int getClanPoints() {
		return clanPoints;
	}

	public void setClanPoints(int p) {
		this.clanPoints = p;
	}

	public World getWorld() {
		return world;
	}
}
