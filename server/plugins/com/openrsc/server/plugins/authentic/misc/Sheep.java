package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class Sheep implements UseNpcTrigger {

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.SHEEP.id() && item.getCatalogId() == ItemId.SHEARS.id();
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		npc.resetPath();

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().getFreeSlots();
		}

		startbatch(repeat);
		batchShear(player, item);
	}

	private void batchShear(Player player, Item item) {
		thinkbubble(item);
		player.message("You attempt to shear the sheep");

		if (random(0, 4) != 0) {
			player.message("You get some wool");
			give(player, ItemId.WOOL.id(), 1);
		} else {
			player.message("The sheep manages to get away from you!");
			return;
		}

		delay(2);

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			batchShear(player, item);
		}
	}
}
