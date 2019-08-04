package com.openrsc.server.content.clan;

import com.openrsc.server.Server;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;

public class ClanInvite {

	public Player inviter, invited;
	private SingleEvent timeOutEvent;

	private ClanInvite(Player inviter, Player invited) {
		this.inviter = inviter;
		this.invited = invited;
	}

	public static void createClanInvite(Player player, Player invited) {

		if (player.getClan() == null) {
			return;
		}
		if (invited.getActiveClanInvite() != null) {
			player.message(invited.getUsername() + " has already and active clan invitation, please try again later.");
			return;
		}
		if (invited.getClan() != null) {
			player.message(invited.getUsername() + " is already in a clan");
			return;
		}
		if (invited.getCache().hasKey("p_block_invites")) {
			boolean blockInvites = invited.getCache().getBoolean("p_block_invites");
			if (blockInvites) {
				player.message("This player has clan invitations blocked");
				invited.message(player.getUsername() + " tried to send you an invite, you have clans invite setting blocked");
				return;
			}
		} else {
			if (invited.getClanInviteSetting()) {
				ActionSender.sendBox(player,
					"@lre@Clan: %"
						+ " %"
						+ invited.getUsername() + " has clan invitations blocked %"
					, true);
				return;
			}
		}

		if (player.getClan().getPlayers().size() >= ClanManager.MAX_CLAN_SIZE) {
			player.message("Your clan has reached the maximum clan members limit");
			return;
		}
		if (invited.equals(player)) {
			player.message("You can't send an invite to yourself..");
			return;
		}
		ClanInvite ClanInvite = new ClanInvite(player, invited);
		ClanInvite.startTimeoutCounter();

		player.message("You have invited " + invited.getUsername() + " to your clan");

		invited.setActiveClanInvite(ClanInvite);
		invited.message(player.getUsername() + " has invited you to join clan: [@cla@" + player.getClan().getClanName() + "@whi@]");

		if (invited.getLocation().inWilderness()) {
			invited.message("Type ::clanaccept to accept your invitation");
		} else {
			ActionSender.sendClanInvitationGUI(invited, player.getClan().getClanName(), player.getUsername());
		}
	}

	private void startTimeoutCounter() {
		timeOutEvent = new SingleEvent(null, 60000, "Clan Invite") {
			@Override
			public void action() {
				inviter.message(invited.getUsername() + " did not respond to your invitation");
				invited.setActiveClanInvite(null);
				invited.message("You did not respond to your Clan invite in time");
				inviter.message(invited.getUsername() + "'s Clan invitation is no longer active");
			}
		};
		Server.getServer().getEventHandler().add(timeOutEvent);
	}

	public void accept() {
		if (inviter.getClan() != null) {
			if (invited.getClan() != null) {
				return;
			}
			inviter.getClan().addPlayer(invited);
		}
		invited.setActiveClanInvite(null);
		timeOutEvent.stop();
	}

	public void decline() {
		if (inviter != null) {
			inviter.message(invited.getUsername() + " has declined your invitation");
		}
		invited.setActiveClanInvite(null);
		timeOutEvent.stop();
	}
}

