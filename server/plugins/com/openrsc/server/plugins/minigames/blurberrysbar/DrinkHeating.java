package com.openrsc.server.plugins.minigames.blurberrysbar;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class DrinkHeating implements UseLocTrigger {

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		if (item.getCatalogId() == ItemId.FULL_COCKTAIL_GLASS.id() && obj.getID() == 119) {
			return true;
		}
		if ((item.getCatalogId() == ItemId.HALF_COCKTAIL_GLASS.id() || item.getCatalogId() == ItemId.ODD_LOOKING_COCKTAIL.id())
			&& obj.getID() == 119) {
			return true;
		}
		return false;
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (item.getCatalogId() == ItemId.FULL_COCKTAIL_GLASS.id() && obj.getID() == 119) {
			mes(player, "you briefly place the drink in the oven");
			player.message("you remove the warm drink");
			if (player.getCache().hasKey("drunk_dragon_base") && player.getCache().hasKey("diced_pa_to_drink") && player.getCache().hasKey("cream_into_drink")) {
				player.getCarriedItems().remove(new Item(ItemId.FULL_COCKTAIL_GLASS.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.DRUNK_DRAGON.id()));
				checkAndRemoveBlurberry(player, true);
			}
			if (player.getCache().hasKey("chocolate_saturday_base") && player.getCache().hasKey("choco_bar_in_drink")) {
				if (checkAndRemoveBlurberry(player, true)) {
					player.getCache().store("heated_choco_saturday", true);
				}
			} else {
				player.getCarriedItems().remove(new Item(ItemId.FULL_COCKTAIL_GLASS.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.ODD_LOOKING_COCKTAIL.id()));
			}
		}
		if ((item.getCatalogId() == ItemId.HALF_COCKTAIL_GLASS.id() || item.getCatalogId() == ItemId.ODD_LOOKING_COCKTAIL.id())
			&& obj.getID() == 119) {
			mes(player, "you briefly place the drink in the oven");
			player.message("you remove the warm drink");
		}
	}
}
