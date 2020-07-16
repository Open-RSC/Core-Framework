package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;

public class DiskOfReturning implements OpInvTrigger {

	public boolean insideMines(Player player) {
		return ((player.getX() >= 250 && player.getX() <= 315) && (player.getY() >= 3325 && player.getY() <= 3400));
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if(item.getCatalogId() == ItemId.DISK_OF_RETURNING.id()) {
			if (player.getLocation().onBlackHole()) {
				player.message("You spin your disk of returning");
				player.teleport(311, 3348, true);
				player.getCarriedItems().remove(new Item(ItemId.DISK_OF_RETURNING.id()));
			} else {
				player.message("The disk will only work from in Thordur's black hole");
			}
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.DISK_OF_RETURNING.id();
	}
}
