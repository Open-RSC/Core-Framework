package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.InvUseOnNpcListener;

import static com.openrsc.server.plugins.Functions.*;

public class Cow implements InvUseOnNpcListener {

	@Override
	public boolean blockInvUseOnNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.COW_DAIRY.id() && item.getCatalogId() == ItemId.BUCKET.id() || npc.getID() == NpcId.COW_ATTACKABLE.id() && item.getCatalogId() == ItemId.BUCKET.id();
	}

	@Override
	public void onInvUseOnNpc(Player player, Npc npc, Item item) {
		npc.resetPath();
		npc.face(player);
		npc.setBusy(true);
		showBubble(player, item);
		if (player.getCarriedItems().getInventory().hasInInventory(item.getCatalogId())) {
			player.getCarriedItems().getInventory().replace(item.getCatalogId(), ItemId.MILK.id(),true);
		}
		message(player, 3500, "You milk the cow");
		npc.setBusy(false);
	}
}
