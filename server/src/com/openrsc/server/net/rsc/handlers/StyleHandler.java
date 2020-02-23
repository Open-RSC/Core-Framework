package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;

public class StyleHandler implements PacketHandler {

	public void handlePacket(Packet p, Player player) throws Exception {

		int style = p.readByte();
		if (style < Skills.CONTROLLED_MODE || style > Skills.DEFENSIVE_MODE) {
			player.setSuspiciousPlayer(true, "style handler style < 0 or style > 3");
			return;
		}
		player.setCombatStyle(style);
	}

}
