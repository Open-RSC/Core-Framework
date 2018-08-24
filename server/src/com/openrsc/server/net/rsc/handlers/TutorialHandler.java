package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PacketHandler;

public class TutorialHandler implements PacketHandler {

	@Override
	public void handlePacket(Packet p, Player player) throws Exception {
		if (player == null) {
			return;
		}
		if(player.getLocation().onTutorialIsland()) {
			if (player.isBusy()) {
				if (player.inCombat()) {
					player.message("You cannot do that whilst fighting!");
				}
				return;
			}
			if(player.getCache().hasKey("tutorial")) {
				player.getCache().remove("tutorial");
			}
			if (!player.getInventory().hasItemId(70)) { // bronze long sword
				player.getInventory().add(new Item(70, 1));
			}
			if (!player.getInventory().hasItemId(108)) { // bronze large
				player.getInventory().add(new Item(108, 1));
			}
			if (!player.getInventory().hasItemId(117)) { // bronze chain
				player.getInventory().add(new Item(117, 1));
			}
			if (!player.getInventory().hasItemId(206)) { // bronze legs
				player.getInventory().add(new Item(206, 1));
			}
			if (!player.getInventory().hasItemId(4)) { // wooden shield
				player.getInventory().add(new Item(4, 1));
			}
			if (!player.getInventory().hasItemId(376)) { // net
				player.getInventory().add(new Item(376, 1));
			}
			if (!player.getInventory().hasItemId(156)) { // bronze pickaxe
				player.getInventory().add(new Item(156, 1));
			}
			if (!player.getInventory().hasItemId(33)) { // air runes
				player.getInventory().add(new Item(33, 12));
			}
			if (!player.getInventory().hasItemId(35)) { // mind runes
				player.getInventory().add(new Item(35, 8));
			}
			if (!player.getInventory().hasItemId(32)) { // water runes
				player.getInventory().add(new Item(32, 3));
			}
			if (!player.getInventory().hasItemId(34)) { // earth runes
				player.getInventory().add(new Item(34, 2));
			}
			if (!player.getInventory().hasItemId(36)) { // body runes
				player.getInventory().add(new Item(36, 1));
			}
			if (!player.getInventory().hasItemId(1263)) { // sleeping bag
				player.getInventory().add(new Item(1263, 1));
			}
			player.teleport(122, 647, false);
			player.message("Skipped tutorial, welcome to Lumbridge");
			World.getWorld().sendWorldAnnouncement("New adventurer @gre@" + player.getUsername() + "@whi@ has arrived in lumbridge!");
			ActionSender.sendBox(player, "Welcome to Open RSC! % To speak on global chat type ::g <message> or ::p <message> for PK chat"
					+ "% To view list of players online type ::onlinelist %"
					+ "To view your player statistics and information type ::info %"
					+ "We hope you enjoy playing! Have fun!", true);
			ActionSender.sendPlayerOnTutorial(player);
		}
	}
}
