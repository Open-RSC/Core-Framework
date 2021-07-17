package com.openrsc.server.plugins.authentic.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class SandPit implements UseLocTrigger {

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == 302 && item.getCatalogId() == ItemId.BUCKET.id();
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, final Item item) {
		if (item.getCatalogId() != ItemId.BUCKET.id()) {
			player.message("Nothing interesting happens");
			return;
		}

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false));
		}

		startbatch(repeat);
		batchSand(player, item);
	}

	private void batchSand(Player player, Item item) {
		item = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
		);
		thinkbubble(item);
		player.message("you fill the bucket with sand");
		player.getCarriedItems().remove(item);
		player.getCarriedItems().getInventory().add(new Item(ItemId.SAND.id()));
		delay();

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			batchSand(player, item);
		}
	}
}
