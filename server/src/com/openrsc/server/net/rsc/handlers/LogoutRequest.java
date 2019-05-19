package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PacketHandler;

public class LogoutRequest implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Player player) throws Exception {
		if (player.canLogout()) {
			// returns a single pet crystal if the player logs out without returning it
			/*if (Constants.GameServer.WANT_PETS && player.getInventory().hasItemId(ItemId.A_GLOWING_RED_CRYSTAL.id())) {
				removeItem(player, ItemId.A_GLOWING_RED_CRYSTAL.id(), 1);
				addItem(player, ItemId.A_RED_CRYSTAL.id(), 1);
			}*/
			player.unregister(false, "Player requested log out");
		} else {
			ActionSender.sendCantLogout(player);
		}
	}
}
