package com.openrsc.server.plugins.minigames.blurberrysbar;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;

import static com.openrsc.server.plugins.Functions.checkAndRemoveBlurberry;
import static com.openrsc.server.plugins.Functions.mes;

public class DrinkHeating implements UseLocTrigger {

	@Override
	public boolean blockUseLoc(GameObject obj, Item item, Player p) {
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
	public void onUseLoc(GameObject obj, Item item, Player p) {
		if (item.getCatalogId() == ItemId.FULL_COCKTAIL_GLASS.id() && obj.getID() == 119) {
			mes(p, "you briefly place the drink in the oven");
			p.message("you remove the warm drink");
			if (p.getCache().hasKey("drunk_dragon_base") && p.getCache().hasKey("diced_pa_to_drink") && p.getCache().hasKey("cream_into_drink")) {
				p.getCarriedItems().getInventory().replace(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.DRUNK_DRAGON.id());
				checkAndRemoveBlurberry(p, true);
			}
			if (p.getCache().hasKey("chocolate_saturday_base") && p.getCache().hasKey("choco_bar_in_drink")) {
				if (checkAndRemoveBlurberry(p, true)) {
					p.getCache().store("heated_choco_saturday", true);
				}
			} else {
				p.getCarriedItems().getInventory().replace(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.ODD_LOOKING_COCKTAIL.id());
			}
		}
		if ((item.getCatalogId() == ItemId.HALF_COCKTAIL_GLASS.id() || item.getCatalogId() == ItemId.ODD_LOOKING_COCKTAIL.id())
			&& obj.getID() == 119) {
			mes(p, "you briefly place the drink in the oven");
			p.message("you remove the warm drink");
		}
	}
}
