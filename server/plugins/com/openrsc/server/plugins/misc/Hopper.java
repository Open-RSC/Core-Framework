package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.*;

public class Hopper implements InvUseOnObjectListener, InvUseOnObjectExecutiveListener, ObjectActionListener, ObjectActionExecutiveListener {

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
		return (obj.getID() == 52 || obj.getID() == 173 || obj.getID() == 246) && item.getID() == ItemId.GRAIN.id();
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player player) {
		if (obj.getAttribute("contains_item", null) != null) {
			player.message("There is already grain in the hopper");
			return;
		}
		showBubble(player, item);
		obj.setAttribute("contains_item", item.getID());
		removeItem(player, item);
		player.message("You put the grain in the hopper");
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getGameObjectDef() != null && obj.getGameObjectDef().getName().toLowerCase().equals("hopper") && command.equals("operate");
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		message(player, 500, "You operate the hopper");
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
			createGroundItem(player.getWorld(), ItemId.FLOUR.id(), 1, 162, 3533);
		} else {
			createGroundItem(player.getWorld(), ItemId.FLOUR.id(), 1, obj.getX(), Formulae.getNewY(Formulae.getNewY(obj.getY(), false), false) + offY);
		}
		obj.removeAttribute("contains_item");
	}

}
