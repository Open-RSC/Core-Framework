package com.openrsc.server.plugins.authentic.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class SoilMound implements UseLocTrigger {

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == 1276 && item.getCatalogId() == ItemId.BUCKET.id();
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, final Item item) {
		final int itemID = item.getCatalogId();
		final int refilledID = ItemId.SOIL.id();
		if (item.getNoted() || itemID != ItemId.BUCKET.id()) {
			player.message("Nothing interesting happens");
			return;
		}

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(itemID, Optional.of(false));
		}

		startbatch(repeat);
		batchFill(player, item, refilledID);
	}

	private void batchFill(Player player, Item bucket, int filledId) {
		bucket = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(bucket.getCatalogId(), Optional.of(false)));

		if (bucket == null) return;

		thinkbubble(bucket);
		player.message("you fill the bucket with soil");
		player.getCarriedItems().remove(bucket);
		give(player, filledId, 1);

		delay();

		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			batchFill(player, bucket, filledId);
		}
	}
}
