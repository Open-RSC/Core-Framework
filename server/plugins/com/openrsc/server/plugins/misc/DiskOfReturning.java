package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvActionListener;

public class DiskOfReturning implements InvActionListener {

	public boolean insideMines(Player p) {
		return ((p.getX() >= 250 && p.getX() <= 315) && (p.getY() >= 3325 && p.getY() <= 3400));
	}

	@Override
	public void onInvAction(Item item, Player player, String command) {
		if(item.getCatalogId() == ItemId.DISK_OF_RETURNING.id()) {
			if (player.getLocation().onBlackHole()) {
				player.message("You spin your disk of returning");
				player.teleport(311, 3348, true);
				player.getCarriedItems().remove(ItemId.DISK_OF_RETURNING.id(), 1);
			} else {
				player.message("The disk will only work from in Thordur's black hole");
			}
		}
	}

	@Override
	public boolean blockInvAction(Item item, Player player, String command) {
		return item.getCatalogId() == ItemId.DISK_OF_RETURNING.id();
	}
}
