package com.openrsc.server.plugins.authentic.quests.members.legendsquest.mechanism;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.DropObjTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestOnDrop implements DropObjTrigger {

	@Override
	public boolean blockDropObj(Player player, Integer invIndex, Item item, Boolean fromInventory) {
		return inArray(item.getCatalogId(), ItemId.A_CHUNK_OF_CRYSTAL.id(), ItemId.A_LUMP_OF_CRYSTAL.id(), ItemId.A_HUNK_OF_CRYSTAL.id(),
				ItemId.A_RED_CRYSTAL.id(), ItemId.A_GLOWING_RED_CRYSTAL.id(), ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id(),
				ItemId.BLESSED_GOLDEN_BOWL_WITH_PLAIN_WATER.id(), ItemId.GOLDEN_BOWL_WITH_PURE_WATER.id(), ItemId.GOLDEN_BOWL_WITH_PLAIN_WATER.id());
	}

	@Override
	public void onDropObj(Player player, Integer invIndex, Item item, Boolean fromInventory) {
		if (item.getCatalogId() == ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id()
			|| item.getCatalogId() == ItemId.BLESSED_GOLDEN_BOWL_WITH_PLAIN_WATER.id()) {
			player.getCarriedItems().remove(new Item(item.getCatalogId()));
			player.message("You drop the bowl on the floor and the water spills out everywhere.");
			addobject(player.getWorld(), ItemId.BLESSED_GOLDEN_BOWL.id(), 1, player.getX(), player.getY());
		}
		else if (item.getCatalogId() == ItemId.GOLDEN_BOWL_WITH_PURE_WATER.id()
				|| item.getCatalogId() == ItemId.GOLDEN_BOWL_WITH_PLAIN_WATER.id()) {
				player.getCarriedItems().remove(new Item(item.getCatalogId()));
				player.message("You drop the bowl on the floor and the water spills out everywhere.");
				addobject(player.getWorld(), ItemId.GOLDEN_BOWL.id(), 1, player.getX(), player.getY());
			}
		else if (inArray(item.getCatalogId(), ItemId.A_CHUNK_OF_CRYSTAL.id(), ItemId.A_LUMP_OF_CRYSTAL.id(), ItemId.A_HUNK_OF_CRYSTAL.id(),
				ItemId.A_RED_CRYSTAL.id(), ItemId.A_GLOWING_RED_CRYSTAL.id())) {
			player.getCarriedItems().remove(new Item(item.getCatalogId()));
		}
	}
}
