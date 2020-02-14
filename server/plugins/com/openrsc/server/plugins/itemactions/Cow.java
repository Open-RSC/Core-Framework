package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class Cow implements InvUseOnNpcListener, InvUseOnNpcExecutiveListener {

	@Override
	public boolean blockInvUseOnNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.COW_DAIRY.id() && item.getID() == ItemId.BUCKET.id() || npc.getID() == NpcId.COW_ATTACKABLE.id() && item.getID() == ItemId.BUCKET.id();
	}

	@Override
	public void onInvUseOnNpc(Player player, Npc npc, Item item) {
		npc.resetPath();
		npc.face(player);
		npc.setBusy(true);
		showBubble(player, item);
		if (player.getInventory().hasInInventory(item.getID())) {
			player.getInventory().replace(item.getID(), ItemId.MILK.id(),true);
		}
		message(player, 3500, "You milk the cow");
		npc.setBusy(false);
	}
}
