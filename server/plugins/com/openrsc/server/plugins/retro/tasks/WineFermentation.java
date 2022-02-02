package com.openrsc.server.plugins.retro.tasks;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.WineFermentTrigger;

public class WineFermentation implements WineFermentTrigger {
	@Override
	public void onWineFerment(Player player) {
		if (player.getCarriedItems().hasCatalogID(ItemId.BAD_OR_UNFERMENTED_WINE.id())) {
			wineFermentStep(player);
		}
	}

	public void wineFermentStep(Player player) {
		// To ferment wine needs 18 zone changes
		if (!player.getCache().hasKey("part_wine_ferment")) {
			player.getCache().set("part_wine_ferment", 1);
		} else {
			int parts = player.getCache().getInt("part_wine_ferment");
			if (parts >= 17) {
				player.getCache().remove("part_wine_ferment");
				player.getCarriedItems().remove(new Item(ItemId.BAD_OR_UNFERMENTED_WINE.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.WINE.id()));
			} else {
				player.getCache().put("part_wine_ferment", parts + 1);
			}
		}
	}

	@Override
	public boolean blockWineFerment(Player player) {
		return player.getCarriedItems().hasCatalogID(ItemId.BAD_OR_UNFERMENTED_WINE.id());
	}
}
