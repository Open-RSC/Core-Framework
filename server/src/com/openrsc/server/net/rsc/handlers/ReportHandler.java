package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.Constants;
import com.openrsc.server.database.impl.mysql.queries.logging.GameReport;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.snapshot.Snapshot;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.ReportStruct;
import com.openrsc.server.plugins.triggers.CommandTrigger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;

public final class ReportHandler implements PayloadProcessor<ReportStruct, OpcodeIn> {
	private static final Logger LOGGER = LogManager.getLogger();

	public void process(ReportStruct payload, Player player) throws Exception {

		String playerName;
		playerName = payload.targetPlayerName;
		byte reason = payload.reason;
		byte suggestsOrMutes = payload.suggestsOrMutes;

		if (playerName.equalsIgnoreCase(player.getUsername())) {
			player.message("You can't report yourself!!");
			return;
		}


		// botting or bug exploiting; "other" or impersonating jagex staff
		if (reason != 4 && reason != 6 && reason != 8 + 64 && reason != 6 + 64 && reason != 4 + 32 && reason != 5 + 32 && reason != 6 + 32 && reason != 7 + 32 && reason != 12 + 32) {
			Iterator<Snapshot> i = player.getWorld().getSnapshots().iterator();
			boolean foundPlayer = false;
			if (i.hasNext()) {
				while (i.hasNext()) {
					Snapshot s = i.next();
					if (System.currentTimeMillis() - s.getTimestamp() > 60000) {
						player.message("For that rule you can only report players who have spoken or traded recently.");
						return;
					}
					if (s.getOwner().equalsIgnoreCase(playerName)) {
						foundPlayer = true;
						break; // player reported was found to have spoken.
					}
				}
				// needed in case there is not a message in the queue older than 1 minute and user wasn't found.
				if (!foundPlayer) {
					player.message("For that rule you can only report players who have spoken or traded recently.");
					return;
				}
			} else {
				player.message("For that rule you can only report players who have spoken or traded recently.");
				return;
			}
		}
		if (!player.canReport()) {
			player.message("You already sent an abuse report under 60 secs ago! Do not abuse this system!");
			return;
		}

		boolean playerExists = player.getWorld().getServer().getDatabase().playerExists(playerName);

		if (!playerExists) {
			player.message("Invalid player name.");
			return;
		}

		player.message("Thank-you, your abuse report has been received.");
		String reportMessage = player.getUsername() + " reported " + playerName + " for \"" + Constants.reportReasons.getOrDefault((int)reason, "Unknown Reason") + "\"";
		LOGGER.info(reportMessage);

		GameReport gameReport = new GameReport(player, playerName, reason, suggestsOrMutes != 0, player.isMod());
		player.getWorld().getServer().getGameLogger().addQuery(gameReport);
		player.getWorld().getServer().getDiscordService().reportSendToDiscord(gameReport, player.getWorld().getServer().getName());
		player.setLastReport();

		if (suggestsOrMutes != 0 && player.isMod()) {
			muteCommand(player, "mute " + playerName + " -1");
		}
	}

	private void muteCommand(Player player, String s) {
		int firstSpace = s.indexOf(" ");
		String cmd = s;
		String[] args = new String[0];
		if (firstSpace != -1) {
			cmd = s.substring(0, firstSpace).trim();
			args = s.substring(firstSpace + 1).trim().split(" ");
		}

		player.getWorld().getServer().getPluginHandler().handlePlugin(
				CommandTrigger.class,
				player,
				new Object[]{player, cmd.toLowerCase(), args}
		);
	}
}
