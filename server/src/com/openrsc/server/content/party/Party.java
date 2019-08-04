package com.openrsc.server.content.party;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.MessageType;

import java.util.ArrayList;
import java.util.Arrays;

public class Party {
	private int id;
	private String name;
	private String tag;
	private PartyPlayer leader;
	private ArrayList<PartyPlayer> players = new ArrayList<PartyPlayer>();
	private int[] partySetting = new int[3];
	private int partyPoints;

	public PartyPlayer addPlayer(Player player) {
		if (getPlayers().size() < PartyManager.MAX_PARTY_SIZE) {
			player.setParty(this);

			PartyPlayer partyMember = new PartyPlayer(player.getUsername());
			partyMember.setRank(PartyRank.NORMAL);
			partyMember.setPlayerReference(player);
			if (leader == null) {
				partyMember.setRank(PartyRank.LEADER);
				leader = partyMember;
				Arrays.fill(partySetting, 1);
			}
			getPlayers().add(partyMember);

			if (partyMember.getUsername() != player.getUsername()) {
				messageChat(player, player.getUsername() + " has joined the party!");
			}
			updatePartyGUI();
			ActionSender.sendPartySetting(partyMember.getPlayerReference());

			if (getPlayers().size() > 1) {
				PartyManager.savePartyChanges(this);
			}
			return partyMember;
		}
		return null;
	}

	public void removePlayer(String username) {
		PartyPlayer member = getPlayer(username);
		if (member == null) {
			return;
		}

		if (member.isOnline()) {
			ActionSender.sendLeaveParty(member.getPlayerReference());
			member.getPlayerReference().setParty(null);
			member.getPlayerReference().message("You are no longer in a party");
		}

		getPlayers().remove(member);
		messagePartyInfo(username + " has left the party");

		if (getPlayers().size() >= 1) {
			if (getLeader().getUsername().equalsIgnoreCase(username)) {
				setLeader(getPlayers().get(0));
				getLeader().setRank(PartyRank.LEADER);
				messagePartyInfo("@red@Your party leader has left the party!");
				messagePartyInfo("@yel@" + getLeader().getUsername() + " is the new party leader!");
				//getPlayers().get(0).getPlayerReference().message("You are no longer in a party");
				ActionSender.sendParty(getPlayers().get(0).getPlayerReference());
				getPlayers().get(0).getPlayerReference().getParty().updatePartySettings();
			}
			PartyManager.savePartyChanges(this);
		} else if (getPlayers().size() == 0) {
			PartyManager.deleteParty(this);
		}
		updatePartyGUI();
	}

	public void updateRankPlayer(Player player, String username, int newRank) {
		PartyPlayer member = getPlayer(username);
		if (member == null) {
			return;
		}

		PartyRank setRank = PartyRank.getRankFor(newRank);
		if (member.getRank().rankIndex == newRank) {
			return;
		}

		if (getLeader().getUsername().equalsIgnoreCase(player.getUsername())) {
			if (newRank == 1) {
				player.getParty().getPlayer(player.getUsername()).setRank(PartyRank.NORMAL);
				setLeader(member);
				getLeader().setRank(PartyRank.LEADER);
				messagePartyInfo("@red@Your party leader has passed the leadership!");
				messagePartyInfo("@yel@" + getLeader().getUsername() + " is the new party leader!");
				PartyManager.savePartyChanges(this);
				ActionSender.sendPartySetting(player);
			} else {
				if (newRank == 2) {
					messagePartyInfo("Congratulations! " + member.getUsername() + " has been promoted to " + PartyRank.getRankFor(newRank).name().toLowerCase() + " rank.");
				} else {
					messagePartyInfo(member.getUsername() + " has been put back to " + PartyRank.getRankFor(newRank).name().toLowerCase() + " rank.");
				}
				member.setRank(setRank);
				PartyManager.updatePartyRankPlayer(member);
			}
			updatePartyGUI();
			if (member.isOnline()) {
				ActionSender.sendPartySetting(member.getPlayerReference());
			}
		}
	}

	public void updatePartyGUI() {
		for (PartyPlayer m : players) {
			if (m.isOnline()) {
				ActionSender.sendParty(m.getPlayerReference());
			}
		}
	}

	public void updatePartySettings() {
		for (PartyPlayer m : players) {
			if (m.isOnline()) {
				ActionSender.sendPartySetting(m.getPlayerReference());
			}
		}
	}

	public PartyPlayer getPlayer(String username) {
		for (PartyPlayer p : getPlayers()) {
			if (p.getUsername().equalsIgnoreCase(username)) {
				return p;
			}
		}
		return null;
	}

	public ArrayList<PartyPlayer> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<PartyPlayer> partyPlayers) {
		this.players = partyPlayers;
	}

	public void messageChat(Player player, String string) {
		for (PartyPlayer cMember : getPlayers()) {
			if (cMember.getPlayerReference() != null) {
				Player partyPlayer = cMember.getPlayerReference();
				ActionSender.sendMessage(partyPlayer, player, 1, MessageType.GAME, "" + string, player.getIcon());
			}
		}
	}

	public void messagePartyInfo(String string) {
		for (PartyPlayer cMember : getPlayers()) {
			if (cMember.getPlayerReference() != null) {
				Player partyPlayer = cMember.getPlayerReference();
				ActionSender.sendPlayerServerMessage(partyPlayer, MessageType.GAME, "" + string);
			}
		}
	}

	public String getPartyName() {
		return name;
	}

	public void setPartyName(String name) {
		this.name = name;
	}

	public String getPartyTag() {
		return tag;
	}

	public void setPartyTag(String tag) {
		this.tag = tag;
	}

	public int getPartyID() {
		return id;
	}

	public void setPartyID(int id) {
		this.id = id;
	}

	public PartyPlayer getLeader() {
		return leader;
	}

	public void setLeader(PartyPlayer leader) {
		this.leader = leader;
	}

	public int getKickSetting() {
		return partySetting[0];
	}

	public void setKickSetting(int state) {
		this.partySetting[0] = state;
	}

	public int getInviteSetting() {
		return partySetting[1];
	}

	public void setInviteSetting(int state) {
		this.partySetting[1] = state;
	}

	public boolean isAllowed(int setting, Player p) {
		if (p.getParty() != null) {
			if (partySetting[setting] == 0) {
				return true;
			} else if (partySetting[setting] == 1 && p.getParty().getPlayer(p.getUsername()).getRank().equals(PartyRank.LEADER)) {
				return true;
			} else if ((partySetting[setting] == 2 || partySetting[setting] == 3) && (p.getParty().getPlayer(p.getUsername()).getRank().equals(PartyRank.LEADER) || p.getParty().getPlayer(p.getUsername()).getRank().equals(PartyRank.GENERAL))) {
				return true;
			}
		}
		return false;
	}

	public int getAllowSearchJoin() {
		return partySetting[2];
	}

	public void setAllowSearchJoin(int state) {
		this.partySetting[2] = state;
	}

	public int getPartyPoints() {
		return partyPoints;
	}

	public void setPartyPoints(int p) {
		this.partyPoints = p;
	}
}
