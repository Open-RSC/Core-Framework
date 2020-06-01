package com.openrsc.server.plugins.skills.runecrafting;

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
			mes("You need a pickaxe to mine Rune Essence.");
			return;
		}

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().getFreeSlots();
		}

		startbatch(repeat);
		batchEssence(player);
	}

	private void batchEssence(Player player) {
		player.playSound("mine");
		thinkbubble(new Item(ItemId.IRON_PICKAXE.id()));
		give(player, ItemId.RUNE_ESSENCE.id(), 1);
		player.incExp(Skills.MINING, 20, true);
		delay(config().GAME_TICK);

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !ifbatchcompleted()) {
			batchEssence(player);
		}
	}
}
