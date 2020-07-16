package com.openrsc.server.plugins.authentic.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class Cow implements UseNpcTrigger {

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.COW_DAIRY.id() && item.getCatalogId() == ItemId.BUCKET.id() || npc.getID() == NpcId.COW_ATTACKABLE.id() && item.getCatalogId() == ItemId.BUCKET.id();
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		npc.resetPath();
		thinkbubble(item);
		if (player.getCarriedItems().getInventory().hasInInventory(item.getCatalogId())) {
			player.getCarriedItems().remove(new Item(item.getCatalogId()));
			player.getCarriedItems().getInventory().add(new Item(ItemId.MILK.id()));
		}
		mes("You milk the cow");
		delay(5);
	}
}
