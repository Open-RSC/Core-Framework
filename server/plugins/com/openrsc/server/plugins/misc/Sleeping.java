package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

public class Sleeping implements ObjectActionExecutiveListener, ObjectActionListener, InvActionListener, InvActionExecutiveListener {

	@Override
	public void onObjectAction(final GameObject object, String command, Player owner) {
		if ((command.equalsIgnoreCase("rest") || command.equalsIgnoreCase("sleep")) && !owner.isSleeping() || command.equalsIgnoreCase("lie in")) {
			ActionSender.sendEnterSleep(owner);
			if (object.getID() == 1035 || object.getID() == 1162) // Crude Bed is like Sleeping Bag.
				owner.startSleepEvent(false);
			else
				owner.startSleepEvent(true);
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return command.equals("rest") || command.equals("sleep") || command.equals("lie in");
	}

	@Override
	public void onInvAction(Item item, Player player, String command) {
		if (item.getCatalogId() == ItemId.SLEEPING_BAG.id() && !player.isSleeping()) {
			ActionSender.sendEnterSleep(player);
			player.startSleepEvent(false);
			// player.resetPath(); - real rsc.
		}
	}

	@Override
	public boolean blockInvAction(Item item, Player player, String command) {
		return item.getCatalogId() == ItemId.SLEEPING_BAG.id() && !player.isSleeping();
	}
}
