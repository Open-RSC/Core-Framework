package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.skills.mining.Mining;

import static com.openrsc.server.plugins.Functions.*;

public class RawEssence implements OpLocTrigger {
	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 1227;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		int axeID = Mining.getAxe(player);
		if (axeID < 0) {
			mes(player, "You need a pickaxe to mine Rune Essence.");
			return;
		}

		int repeat = 1;
		if (player.getWorld().getServer().getConfig().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().getFreeSlots();
		}

		batchEssence(player, repeat);
	}

	private void batchEssence(Player player, int repeat) {
		player.playSound("mine");
		thinkbubble(player, new Item(ItemId.IRON_PICKAXE.id()));
		give(player, ItemId.RUNE_ESSENCE.id(), 1);
		player.incExp(Skills.MINING, 20, true);
		delay(player.getWorld().getServer().getConfig().GAME_TICK);

		// Repeat
		if (!ifinterrupted() && --repeat > 0) {
			batchEssence(player, repeat);
		}
	}
}
