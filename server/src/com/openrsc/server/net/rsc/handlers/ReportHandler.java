package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.Constants;
import com.openrsc.server.database.impl.mysql.queries.logging.GameReport;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.snapshot.Snapshot;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.ReportStruct;
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
		if (reason != 4 && reason != 6 && reason != 8 + 64 && reason != 6 + 64) {
			Iterator<Snapshot> i = player.getWorld().getSnapshots().iterator();
			if (i.hasNext()) {
				Snapshot s = i.next();
				if (!s.getOwner().equalsIgnoreCase(playerName)) {
					player.message("For that rule you can only report players who have spoken or traded recently.");
					return;
				}
				if (System.currentTimeMillis() - s.getTimestamp() > 60000) {
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
		LOGGER.info(player.getUsername() + " reported " + playerName + " for \"" + Constants.reportReasons.getOrDefault((int)reason, "Unknown Reason") + "\"");
		player.getWorld().getServer().getGameLogger().addQuery(new GameReport(player, playerName, reason, suggestsOrMutes != 0, player.isMod()));
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

		player.getWorld().getServer().getPluginHandler().handlePlugin(player, "Command",
			new Object[]{player, cmd.toLowerCase(), args});
	}
}
