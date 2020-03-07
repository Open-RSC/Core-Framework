package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.InvUseOnObjectListener;

import static com.openrsc.server.plugins.Functions.*;

public class SoilMound implements InvUseOnObjectListener {

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
		return obj.getID() == 1276 && item.getCatalogId() == ItemId.BUCKET.id();
	}

	@Override
	public void onInvUseOnObject(GameObject obj, final Item item, Player player) {
		final int itemID = item.getCatalogId();
		final int refilledID = ItemId.SOIL.id();
		if (item.getCatalogId() != ItemId.BUCKET.id()) {
			player.message("Nothing interesting happens");
			return;
		}
		player.setBatchEvent(new BatchEvent(player.getWorld(), player, 600, "Fill Bucket with Soil", player.getCarriedItems().getInventory().countId(itemID), true) {
			@Override
			public void action() {
				if (getOwner().getCarriedItems().getInventory().hasInInventory(itemID)) {
					showBubble(getOwner(), item);
					sleep(300);
					getOwner().message("you fill the bucket with soil");
					getOwner().getCarriedItems().getInventory().replace(itemID, refilledID,true);
				} else {
					interrupt();
				}
			}
		});
	}
}
