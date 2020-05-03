package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class SoilMound implements UseLocTrigger {

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == 1276 && item.getCatalogId() == ItemId.BUCKET.id();
	}

	private void batchFill(Player player, Item bucket, int filledId, int repeat) {
		if (player.getCarriedItems().getInventory().hasInInventory(bucket.getCatalogId())) {
			thinkbubble(player, bucket);
			player.message("you fill the bucket with soil");
			player.getCarriedItems().remove(new Item(bucket.getCatalogId()));
			give(player, filledId, 1);
		} else return;

		if (ifinterrupted()) return;

		repeat--;
		if (repeat > 0) {
			delay(player.getWorld().getServer().getConfig().GAME_TICK);
			batchFill(player, bucket, filledId, repeat);
		}
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, final Item item) {
		final int itemID = item.getCatalogId();
		final int refilledID = ItemId.SOIL.id();
		if (itemID != ItemId.BUCKET.id()) {
			player.message("Nothing interesting happens");
			return;
		}

		int repeat = 1;
		if (player.getWorld().getServer().getConfig().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(itemID);
		}

		batchFill(player, item, refilledID, repeat);
	}
}
