package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.Constants;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.snapshot.Snapshot;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.plugins.PluginHandler;
import com.openrsc.server.sql.DatabaseConnection;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.query.logs.GameReport;

import java.sql.ResultSet;
import java.util.Iterator;

public final class ReportHandler implements PacketHandler {

	public void handlePacket(Packet p, Player player) throws Exception {

		String hash = p.readString();
		byte reason = p.readByte();
		byte suggestsOrMutes = p.readByte();

		if (hash.equalsIgnoreCase(player.getUsername())) {
			player.message("You can't report yourself!!");
			return;
		}

		if (reason < 0 || reason > 13) {
			player.setSuspiciousPlayer(true);
		}
		if (reason != 4 && reason != 6) {
			Iterator<Snapshot> i = World.getWorld().getSnapshots().iterator();
			if (i.hasNext()) {
				Snapshot s = i.next();
				if (!s.getOwner().equalsIgnoreCase(hash)) {
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

		ResultSet result = DatabaseConnection.getDatabase().executeQuery("SELECT `username` FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "players` WHERE username='" + hash + "'");

		if (!result.next()) {
			player.message("Invalid player name.");
			result.close();
			return;
		}

		player.message("Thank-you, your abuse report has been received.");
		GameLogging.addQuery(new GameReport(player, hash, reason, suggestsOrMutes != 0, player.isMod()));
		player.setLastReport();
		
		if (suggestsOrMutes != 0 && player.isMod()) {
			muteCommand(player, "mute " + hash + " -1");
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

		PluginHandler.getPluginHandler().handleAction("Command",
			new Object[]{cmd.toLowerCase(), args, player});
	}
}
