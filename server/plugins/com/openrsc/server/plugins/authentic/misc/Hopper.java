package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.*;

public class Hopper implements UseLocTrigger, OpLocTrigger {

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return (obj.getID() == 52 || obj.getID() == 173 || obj.getID() == 246 || obj.getID() == 343) && item.getCatalogId() == ItemId.GRAIN.id();
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getAttribute("contains_item", null) != null) {
			player.message("There is already grain in the hopper");
			return;
		}
		thinkbubble(item);
		obj.setAttribute("contains_item", item.getCatalogId());
		player.getCarriedItems().remove(item);
		player.message("You put the grain in the hopper");
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getGameObjectDef() != null && obj.getGameObjectDef().getName().toLowerCase().equals("hopper") && command.equals("operate");
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		mes("You operate the hopper");
		delay();
		player.playSound("mechanical");
		int contains = obj.getAttribute("contains_item", -1);
		if (contains != ItemId.GRAIN.id()) {
			player.message("Nothing interesting happens");
			return;
		}
		player.message("The grain slides down the chute");

		int offY = 0;
		/* Chute in Chef's guild has offsetY -2 from calculated */
		if (obj.getX() == 179 && obj.getY() == 2371) {
			offY = -2;
		}

		if (obj.getID() == 246) {
			addobject(player.getWorld(), ItemId.FLOUR.id(), 1, 162, 3533);
		} else {
			addobject(player.getWorld(), ItemId.FLOUR.id(), 1, obj.getX(), Formulae.getNewY(Formulae.getNewY(obj.getY(), false), false) + offY);
		}
		obj.removeAttribute("contains_item");
	}

}
