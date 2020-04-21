package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class SandPit implements UseLocTrigger {

	@Override
	public boolean blockUseLoc(GameObject obj, Item item, Player player) {
		return obj.getID() == 302 && item.getCatalogId() == ItemId.BUCKET.id();
	}

	@Override
	public void onUseLoc(GameObject obj, final Item item, Player player) {
		final int itemID = item.getCatalogId();
		final int refilledID = ItemId.SAND.id();
		if (item.getCatalogId() != ItemId.BUCKET.id()) {
			player.message("Nothing interesting happens");
			return;
		}
		player.setBatchEvent(new BatchEvent(player.getWorld(), player, player.getWorld().getServer().getConfig().GAME_TICK, "Fill Bucket with Sand", player.getCarriedItems().getInventory().countId(itemID), true) {
			@Override
			public void action() {
				if (getOwner().getCarriedItems().getInventory().hasInInventory(itemID)) {
					thinkbubble(getOwner(), item);
					delay(300);
					getOwner().message("you fill the bucket with sand");
					getOwner().getCarriedItems().getInventory().replace(itemID, refilledID,true);
				} else {
					interrupt();
				}
			}
		});
	}
}
