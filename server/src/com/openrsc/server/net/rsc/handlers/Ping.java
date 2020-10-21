package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;

public class Ping implements PacketHandler {

	public void handlePacket(Packet packet, Player player) throws Exception {
		// Instead of handling the heartbeat packet here, every packet
		// that comes into the server updates the time-since-last-seen-player

		// player.addToPacketQueue(...) updates it.
	}
}
