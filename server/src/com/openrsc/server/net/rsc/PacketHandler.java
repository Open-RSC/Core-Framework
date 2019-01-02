package com.openrsc.server.net.rsc;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;

/**
 * @author n0m
 */
public interface PacketHandler {

	public void handlePacket(Packet p, Player player) throws Exception;
}
