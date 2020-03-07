package com.openrsc.server.plugins.misc;

import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;

import static com.openrsc.server.plugins.Functions.give;
import static com.openrsc.server.plugins.Functions.random;
import static com.openrsc.server.plugins.Functions.thinkbubble;

public class Sheep implements UseNpcTrigger {

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.SHEEP.id() && item.getCatalogId() == ItemId.SHEARS.id();
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		npc.resetPath();

		npc.face(player);
		player.face(npc);
		Functions.thinkbubble(player, item);
		player.message("You attempt to shear the sheep");
		npc.setBusyTimer(1600);
		player.setBatchEvent(new BatchEvent(player.getWorld(), player, 1200, "Crafting Shear Wool", player.getCarriedItems().getInventory().getFreeSlots(), true) {

			@Override
			public void action() {
				npc.setBusyTimer(1600);
				if (random(0, 4) != 0) {
					player.message("You get some wool");
					give(player, ItemId.WOOL.id(), 1);
				} else {
					player.message("The sheep manages to get away from you!");
					npc.setBusyTimer(0);
					interrupt();
				}
			}
		});
	}
}
