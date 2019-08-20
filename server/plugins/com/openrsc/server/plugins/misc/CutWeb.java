package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnWallObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnWallObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.*;

public class CutWeb implements InvUseOnWallObjectListener, InvUseOnWallObjectExecutiveListener,
	WallObjectActionListener, WallObjectActionExecutiveListener {

	private static int WEB = 24;

	@Override
	public boolean blockInvUseOnWallObject(GameObject obj, Item item, Player p) {
		return obj.getID() == WEB;
	}

	@Override
	public void onInvUseOnWallObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == WEB) {
			if (item.getDef(p.getWorld()).getWieldPosition() != 4 && item.getID() != ItemId.KNIFE.id()) {
				p.message("Nothing interesting happens");
				return;
			}
			message(p, "You try to destroy the web...");
			if (Formulae.cutWeb()) {
				p.message("You slice through the web");
				removeObject(obj);
				delayedSpawnObject(obj.getWorld(), obj.getLoc(), 30000);
			} else {
				p.message("You fail to cut through it");
			}
		}
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player p) {
		return p.getWorld().getServer().getConfig().WANT_LEFTCLICK_WEBS
			&& obj.getID() == WEB;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player player) {

		boolean canCut = false;
		//First, check their equipment for an appropriate weapon
		if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			Item weapon = player.getEquipment().get(4);
			if (weapon != null)
				canCut = true;
		}

		if (!canCut) {
			//Next check their inventory for an appropriate weapon
			Item inventoryItem = null;
			for (int i = 0; i < player.getInventory().size(); i++) {
				inventoryItem = player.getInventory().get(i);
				if (inventoryItem != null && inventoryItem.getDef(player.getWorld()).getWieldPosition() == 4)
					canCut = true;
			}

			if (!canCut) {
				//Lastly, check if they have a knife
				if (player.getInventory().hasItemId(ItemId.KNIFE.id())) {
					canCut = true;
				}
			}
		}

		if (canCut) {
			message(player, "You try to destroy the web...");
			if (Formulae.cutWeb()) {
				player.message("You slice through the web");
				removeObject(obj);
				delayedSpawnObject(obj.getWorld(), obj.getLoc(), 30000);
			} else {
				player.message("You fail to cut through it");
			}
		} else
			player.message("Nothing interesting happens");
	}
}
