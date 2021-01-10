package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;

public class PrivacySettingHandler implements PacketHandler {

	public void handlePacket(Packet packet, Player player) throws Exception {

		byte[] newSettings = new byte[4];
		for (int i = 0; i < 4; i++) {
			newSettings[i] = packet.readByte();
		}
		for (int i = 0; i < 4; i++) {
			player.getSettings().setPrivacySetting(i, newSettings[i]);
		}
	}

}
