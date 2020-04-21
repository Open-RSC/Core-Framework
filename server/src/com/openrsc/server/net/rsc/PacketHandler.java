package com.openrsc.server.net.rsc;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;

public interface PacketHandler {

	public void handlePacket(Packet packet, Player player) throws Exception;
}
