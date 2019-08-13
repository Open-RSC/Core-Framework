package com.openrsc.server.plugins.minigames.blurberrysbar;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;

import static com.openrsc.server.plugins.Functions.checkAndRemoveBlurberry;
import static com.openrsc.server.plugins.Functions.message;

public class DrinkHeating implements InvUseOnObjectListener, InvUseOnObjectExecutiveListener {

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
		if (item.getID() == ItemId.FULL_COCKTAIL_GLASS.id() && obj.getID() == 119) {
			return true;
		}
		if ((item.getID() == ItemId.HALF_COCKTAIL_GLASS.id() || item.getID() == ItemId.ODD_LOOKING_COCKTAIL.id())
			&& obj.getID() == 119) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (item.getID() == ItemId.FULL_COCKTAIL_GLASS.id() && obj.getID() == 119) {
			message(p, "you briefly place the drink in the oven");
			p.message("you remove the warm drink");
			if (p.getCache().hasKey("drunk_dragon_base") && p.getCache().hasKey("diced_pa_to_drink") && p.getCache().hasKey("cream_into_drink")) {
				p.getInventory().replace(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.DRUNK_DRAGON.id());
				checkAndRemoveBlurberry(p, true);
			}
			if (p.getCache().hasKey("chocolate_saturday_base") && p.getCache().hasKey("choco_bar_in_drink")) {
				if (checkAndRemoveBlurberry(p, true)) {
					p.getCache().store("heated_choco_saturday", true);
				}
			} else {
				p.getInventory().replace(ItemId.FULL_COCKTAIL_GLASS.id(), ItemId.ODD_LOOKING_COCKTAIL.id());
			}
		}
		if ((item.getID() == ItemId.HALF_COCKTAIL_GLASS.id() || item.getID() == ItemId.ODD_LOOKING_COCKTAIL.id())
			&& obj.getID() == 119) {
			message(p, "you briefly place the drink in the oven");
			p.message("you remove the warm drink");
		}
	}
}
