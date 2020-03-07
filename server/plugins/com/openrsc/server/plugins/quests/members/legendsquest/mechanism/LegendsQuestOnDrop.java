package com.openrsc.server.plugins.quests.members.legendsquest.mechanism;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.DropObjTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class LegendsQuestOnDrop implements DropObjTrigger {

	@Override
	public boolean blockDropObj(Player p, Item i, Boolean fromInventory) {
		return inArray(i.getCatalogId(), ItemId.A_CHUNK_OF_CRYSTAL.id(), ItemId.A_LUMP_OF_CRYSTAL.id(), ItemId.A_HUNK_OF_CRYSTAL.id(),
				ItemId.A_RED_CRYSTAL.id(), ItemId.A_GLOWING_RED_CRYSTAL.id(), ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id(),
				ItemId.BLESSED_GOLDEN_BOWL_WITH_PLAIN_WATER.id(), ItemId.GOLDEN_BOWL_WITH_PURE_WATER.id(), ItemId.GOLDEN_BOWL_WITH_PLAIN_WATER.id());
	}

	@Override
	public void onDropObj(Player p, Item i, Boolean fromInventory) {
		if (i.getCatalogId() == ItemId.BLESSED_GOLDEN_BOWL_WITH_PURE_WATER.id()
			|| i.getCatalogId() == ItemId.BLESSED_GOLDEN_BOWL_WITH_PLAIN_WATER.id()) {
			removeItem(p, i.getCatalogId(), 1);
			p.message("You drop the bowl on the floor and the water spills out everywhere.");
			createGroundItem(p.getWorld(), ItemId.BLESSED_GOLDEN_BOWL.id(), 1, p.getX(), p.getY());
		}
		else if (i.getCatalogId() == ItemId.GOLDEN_BOWL_WITH_PURE_WATER.id()
				|| i.getCatalogId() == ItemId.GOLDEN_BOWL_WITH_PLAIN_WATER.id()) {
				removeItem(p, i.getCatalogId(), 1);
				p.message("You drop the bowl on the floor and the water spills out everywhere.");
				createGroundItem(p.getWorld(), ItemId.GOLDEN_BOWL.id(), 1, p.getX(), p.getY());
			}
		else if (inArray(i.getCatalogId(), ItemId.A_CHUNK_OF_CRYSTAL.id(), ItemId.A_LUMP_OF_CRYSTAL.id(), ItemId.A_HUNK_OF_CRYSTAL.id(),
				ItemId.A_RED_CRYSTAL.id(), ItemId.A_GLOWING_RED_CRYSTAL.id())) {
			removeItem(p, i.getCatalogId(), 1);
			message(p, 600, "The crystal starts fading..");
		}
	}
}
