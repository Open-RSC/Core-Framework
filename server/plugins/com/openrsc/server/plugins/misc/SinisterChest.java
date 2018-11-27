package com.openrsc.server.plugins.misc;

import com.openrsc.server.event.rsc.impl.PoisonEvent;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class SinisterChest implements ObjectActionListener, ObjectActionExecutiveListener, InvUseOnObjectListener, InvUseOnObjectExecutiveListener {

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 645 && obj.getX() == 617 && obj.getY() == 3567;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		if (obj.getID() == 645 && obj.getX() == 617 && obj.getY() == 3567) {
			player.message("the chest is locked");
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
		if (item.getID() == 932 && obj.getID() == 645 && obj.getX() == 617 && obj.getY() == 3567) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (item.getID() == 932 && obj.getID() == 645 && obj.getX() == 617 && obj.getY() == 3567) {
			message(p, "you unlock the chest with your key");
			p.message("A foul gas seeps from the chest");
			p.message("You find a lot of herbs in the chest");

			GameObject openedChest = new GameObject(obj.getLocation(), 339, obj.getDirection(), obj.getType());
			GameObject sinisterChest = new GameObject(obj.getLoc());

			World.getWorld().unregisterGameObject(obj);
			World.getWorld().registerGameObject(openedChest);
			sleep(2000);
			World.getWorld().registerGameObject(sinisterChest);

			removeItem(p, ItemId.SINISTER_KEY.id(), 1); // remove the sinister key.
			// ADD 9 HERB ITEMS FROM CHEST.
			// they are always the same rewarded herbs (see replay, there's also a rsc vid of
			// someone looting it)
			addItem(p, ItemId.UNIDENTIFIED_HARRALANDER.id(), 2);
			addItem(p, ItemId.UNIDENTIFIED_RANARR_WEED.id(), 3);
			addItem(p, ItemId.UNIDENTIFIED_IRIT_LEAF.id(), 1);
			addItem(p, ItemId.UNIDENTIFIED_AVANTOE.id(), 1);
			addItem(p, ItemId.UNIDENTIFIED_KWUARM.id(), 1);
			addItem(p, ItemId.UNIDENTIFIED_TORSTOL.id(), 1);
			// Poison player with damage 6.
			p.startPoisonEvent();
			PoisonEvent poisonEvent = p.getAttribute("poisonEvent", null);
			poisonEvent.setPoisonPower(68);
		}
	}
}
