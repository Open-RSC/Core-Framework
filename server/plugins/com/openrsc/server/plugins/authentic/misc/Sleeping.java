package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.custom.minigames.micetomeetyou.Death;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

public class Sleeping implements OpLocTrigger, OpInvTrigger {

	@Override
	public void onOpLoc(Player owner, final GameObject object, String command) {
		// special bed that can teleport you; TODO: should use unique object ID
		if (owner.getConfig().DEATH_ISLAND && !owner.getConfig().MICE_TO_MEET_YOU_EVENT && object.getX() == 116 && object.getY() == 534) {
			if (Death.bedTeleport(owner)) {
				return;
			}
		}

		// regular beds
		if ((command.equalsIgnoreCase("rest") || command.equalsIgnoreCase("sleep")) || command.equalsIgnoreCase("lie in")) {
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
		// note that entering the sleeping bag doesn't require you to already not be sleeping nor does it interrupt walking
		if (item.getCatalogId() == ItemId.SLEEPING_BAG.id()) {
			ActionSender.sendEnterSleep(player);
			player.startSleepEvent(false);
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.SLEEPING_BAG.id();
	}
}
