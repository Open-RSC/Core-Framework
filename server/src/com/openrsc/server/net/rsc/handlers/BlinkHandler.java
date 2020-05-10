package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;

public class BlinkHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet packet, Player player) throws Exception {
		int coordX = packet.readShort();
		int coordY = packet.readShort();
		if (player.isMod())
			player.teleport(coordX, coordY);
		else
			player.setSuspiciousPlayer(true, "non mod player tried to blink");
	}

}
