package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

public class Sleeping implements OpLocTrigger, OpInvTrigger {

	@Override
	public void onOpLoc(Player owner, final GameObject object, String command) {
		if ((command.equalsIgnoreCase("rest") || command.equalsIgnoreCase("sleep")) && !owner.isSleeping() || command.equalsIgnoreCase("lie in")) {
			ActionSender.sendEnterSleep(owner);
			if (object.getID() == 1035 || object.getID() == 1162) // Crude Bed is like Sleeping Bag.
				owner.startSleepEvent(false);
			else
				owner.startSleepEvent(true);
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return command.equals("rest") || command.equals("sleep") || command.equals("lie in");
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() == ItemId.SLEEPING_BAG.id() && !player.isSleeping()) {
			ActionSender.sendEnterSleep(player);
			player.startSleepEvent(false);
			// player.resetPath(); - real rsc.
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.SLEEPING_BAG.id() && !player.isSleeping();
	}
}
