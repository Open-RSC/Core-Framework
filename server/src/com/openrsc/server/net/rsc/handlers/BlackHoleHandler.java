package com.openrsc.server.net.rsc.handlers;

import static com.openrsc.server.plugins.Functions.hasItem;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PacketHandler;

public class BlackHoleHandler implements PacketHandler {
	
	@Override
	public void handlePacket(Packet p, Player player) throws Exception {
		if (player == null) {
			return;
		}
		if (player.getLocation().onBlackHole()) {
			if (player.isBusy()) {
				if (player.inCombat()) {
					player.message("You cannot do that whilst fighting!");
				}
				return;
			}
			player.teleport(311, 3348);
			player.message("you return to the dwarven mines");
			if (hasItem(player, ItemId.DISK_OF_RETURNING.id())) {
				player.getInventory().remove(ItemId.DISK_OF_RETURNING.id(), 1);
				player.message("consuming your disk of returning");
			}
			ActionSender.sendPlayerOnBlackHole(player);
		}
	}
}
