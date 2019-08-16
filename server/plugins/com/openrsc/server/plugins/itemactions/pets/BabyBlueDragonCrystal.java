package com.openrsc.server.plugins.itemactions.pets;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.PlayerLoginListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;

public class BabyBlueDragonCrystal implements InvActionListener, InvActionExecutiveListener, PlayerLoginListener {

	protected Player petOwnerA;

	@Override
	public boolean blockInvAction(Item item, Player player, String command) {
		return command.equalsIgnoreCase("inspect");
	}

	@Override
	public void onInvAction(Item item, Player player, String command) {
		//if (getConfig().DEBUG)
		System.out.println("Pet item clicked");
		int id = item.getID();

		if (id == ItemId.A_RED_CRYSTAL.id())
			if (player.getWorld().getServer().getConfig().WANT_PETS)
				handleBabyBlueDragon(player, item, command);
			else
				player.message("Nothing interesting happens");
	}

	private void handleBabyBlueDragon(Player player, Item item, String command) {
		if (player.getWorld().getServer().getConfig().DEBUG)
		System.out.println("Pet spawn attempt");
		if (player.getWorld().getServer().getConfig().WANT_PETS){
			if (player.getInventory().hasItemId(ItemId.A_RED_CRYSTAL.id())) {
				if (command.equalsIgnoreCase("inspect")) {
					if (player.getInventory().hasItemId(ItemId.A_GLOWING_RED_CRYSTAL.id())) {

						player.message("You may only summon one pet at a time!");
						return;
					}
				}
			}
	}
	}

	@Override
	public void onPlayerLogin(Player player) {
		if (player.getInventory().hasItemId(ItemId.A_GLOWING_RED_CRYSTAL.id())) {
			if (player.getInventory().remove(new Item(ItemId.A_GLOWING_RED_CRYSTAL.id())) != -1) {
				player.getInventory().remove(new Item(ItemId.A_GLOWING_RED_CRYSTAL.id()));
				player.getInventory().add(new Item(ItemId.A_RED_CRYSTAL.id()));
			}
		}
	}
}
