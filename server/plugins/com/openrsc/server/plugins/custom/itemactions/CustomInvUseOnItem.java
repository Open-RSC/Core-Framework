package com.openrsc.server.plugins.custom.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseInvTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class CustomInvUseOnItem implements UseInvTrigger {

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (item1.getItemStatus().getNoted() || item2.getItemStatus().getNoted()) return;

		if (compareItemsIds(item1, item2, ItemId.COCONUT.id(), ItemId.MACHETTE.id())) {
			int repeat = 1;
			if (config().BATCH_PROGRESSION) {
				repeat = player.getCarriedItems().getInventory().countId(ItemId.COCONUT.id());
			}

			startbatch(repeat);
			batchSlice(player, new Item(ItemId.COCONUT.id()), new Item(ItemId.MACHETTE.id()),
				ItemId.HALF_COCONUT.id(), "You slice open the coconut with the machette");
			return;
		}

		else if (compareItemsIds(item1, item2, ItemId.DRAGONFRUIT.id(), ItemId.KNIFE.id())) {
			int repeat = 1;
			if (config().BATCH_PROGRESSION) {
				repeat = player.getCarriedItems().getInventory().countId(ItemId.DRAGONFRUIT.id());
			}

			startbatch(repeat);
			batchSlice(player, new Item(ItemId.DRAGONFRUIT.id()), new Item(ItemId.KNIFE.id()),
				ItemId.SLICED_DRAGONFRUIT.id(), "You peel the dragonfruit with the knife");
			return;
		}

		else if ((item1.getCatalogId() == ItemId.SUGARCANE.id()
			&& inArray(item2.getCatalogId(), ItemId.LIME_CHUNKS.id(), ItemId.DICED_LEMON.id(),
			ItemId.DICED_ORANGE.id(), ItemId.DICED_GRAPEFRUIT.id(), ItemId.PINEAPPLE_CHUNKS.id()))
			|| (item2.getCatalogId() == ItemId.SUGARCANE.id()
			&& inArray(item1.getCatalogId(), ItemId.LIME_CHUNKS.id(), ItemId.DICED_LEMON.id(),
			ItemId.DICED_ORANGE.id(), ItemId.DICED_GRAPEFRUIT.id(), ItemId.PINEAPPLE_CHUNKS.id()))) {
			int repeat = 1;
			if (config().BATCH_PROGRESSION) {
				repeat = Math.min(player.getCarriedItems().getInventory().countId(item1.getCatalogId()),
					player.getCarriedItems().getInventory().countId(item2.getCatalogId()));
			}

			startbatch(repeat);
			batchSweeten(player, item1, item2, ItemId.SWEETENED_CHUNKS.id(), "You sweeten the fruit chunks");

			return;
		}

		else if ((item1.getCatalogId() == ItemId.SUGARCANE.id()
			&& inArray(item2.getCatalogId(), ItemId.LIME_SLICES.id(), ItemId.LEMON_SLICES.id(),
			ItemId.ORANGE_SLICES.id(), ItemId.GRAPEFRUIT_SLICES.id(), ItemId.PINEAPPLE_RING.id()))
			|| (item2.getCatalogId() == ItemId.SUGARCANE.id()
			&& inArray(item1.getCatalogId(), ItemId.LIME_SLICES.id(), ItemId.LEMON_SLICES.id(),
			ItemId.ORANGE_SLICES.id(), ItemId.GRAPEFRUIT_SLICES.id(), ItemId.PINEAPPLE_RING.id()))) {
			int repeat = 1;
			if (config().BATCH_PROGRESSION) {
				repeat = Math.min(player.getCarriedItems().getInventory().countId(item1.getCatalogId()),
					player.getCarriedItems().getInventory().countId(item2.getCatalogId()));
			}

			startbatch(repeat);
			batchSweeten(player, item1, item2, ItemId.SWEETENED_SLICES.id(), "You sweeten the fruit slices");

			return;
		}
	}

	private void batchSlice(Player player, Item toSlice, Item tool, int sliceId, String sliceString) {
		// Make sure they still have the tool
		if (!player.getCarriedItems().getInventory().hasCatalogID(tool.getCatalogId(), false)) {
			return;
		}

		// Make sure they still have the item to be sliced
		if (player.getCarriedItems().remove(toSlice) > -1) {
			player.message(sliceString);
			player.getCarriedItems().getInventory().add(new Item(sliceId));
		}

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchSlice(player, toSlice, tool, sliceId, sliceString);
		}
	}

	private void batchSweeten(Player player, Item item1, Item item2, int sweetenedId, String processString) {
		item1 = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item1.getCatalogId(), Optional.of(false)));
		item2 = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item2.getCatalogId(), Optional.of(false)));

		if (item1 == null || item2 == null) return;

		player.message(processString);
		player.getCarriedItems().remove(item1);
		player.getCarriedItems().remove(item2);
		give(player, sweetenedId, 1);
		delay();

		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			batchSweeten(player, item1, item2, sweetenedId, processString);
		}
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (compareItemsIds(item1, item2, ItemId.COCONUT.id(), ItemId.MACHETTE.id()))
			return true;
		else if (compareItemsIds(item1, item2, ItemId.DRAGONFRUIT.id(), ItemId.KNIFE.id()))
			return true;
		else if ((item1.getCatalogId() == ItemId.SUGARCANE.id()
			&& inArray(item2.getCatalogId(), ItemId.LIME_CHUNKS.id(), ItemId.DICED_LEMON.id(),
			ItemId.DICED_ORANGE.id(), ItemId.DICED_GRAPEFRUIT.id(), ItemId.PINEAPPLE_CHUNKS.id()))
			|| (item2.getCatalogId() == ItemId.SUGARCANE.id()
			&& inArray(item1.getCatalogId(), ItemId.LIME_CHUNKS.id(), ItemId.DICED_LEMON.id(),
			ItemId.DICED_ORANGE.id(), ItemId.DICED_GRAPEFRUIT.id(), ItemId.PINEAPPLE_CHUNKS.id())))
			return true;
		else if ((item1.getCatalogId() == ItemId.SUGARCANE.id()
			&& inArray(item2.getCatalogId(), ItemId.LIME_SLICES.id(), ItemId.LEMON_SLICES.id(),
			ItemId.ORANGE_SLICES.id(), ItemId.GRAPEFRUIT_SLICES.id(), ItemId.PINEAPPLE_RING.id()))
			|| (item2.getCatalogId() == ItemId.SUGARCANE.id()
			&& inArray(item1.getCatalogId(), ItemId.LIME_SLICES.id(), ItemId.LEMON_SLICES.id(),
			ItemId.ORANGE_SLICES.id(), ItemId.GRAPEFRUIT_SLICES.id(), ItemId.PINEAPPLE_RING.id())))
			return true;

		return false;
	}
}
