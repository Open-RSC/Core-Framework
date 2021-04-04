package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.StringStruct;

public final class CommandHandler implements PayloadProcessor<StringStruct, OpcodeIn> {
	public void process(StringStruct payload, Player player) throws Exception {
		if (System.currentTimeMillis() - player.getLastCommand() < 1000 && !player.isAdmin()) {
			player.message(player.getConfig().MESSAGE_PREFIX + "There's a second delay between using commands");
		} else {
			String s = payload.string;
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
}
