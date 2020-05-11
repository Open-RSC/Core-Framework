package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class BananaTree implements OpLocTrigger {

	private final int BANANA_TREE_ID = 183;
	private final int EMPTY_TREE_ID = 184;

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == BANANA_TREE_ID || obj.getID() == EMPTY_TREE_ID;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == BANANA_TREE_ID) {
			int repeat = 1;
			if (player.getWorld().getServer().getConfig().BATCH_PROGRESSION) {
				repeat = player.getCarriedItems().getInventory().getFreeSlots();
				startBatchProgressBar(repeat);
			}
			batchBananaPick(player, obj, repeat);
		}

		if (obj.getID() == EMPTY_TREE_ID) {
			player.message("there are no bananas left on the tree");
		}
	}

	private void batchBananaPick(Player player, GameObject bananaTree, int repeat) {
		int tick = player.getWorld().getServer().getConfig().GAME_TICK;
		int bananaCount = 1;
		if (player.getCache().hasKey("banana_pick"))
			bananaCount = player.getCache().getInt("banana_pick") + 1;

		player.getCache().set("banana_pick",bananaCount);
		give(player, ItemId.BANANA.id(), 1);

		if (bananaCount >= 5) {
			player.message("you pick the last banana");
			changeloc(bananaTree, tick * 750, EMPTY_TREE_ID); // 8 minutes respawn time.
			player.getCache().remove("banana_pick");
			return;
		} else {
			player.message("you pick a banana");
		}

		delay(tick);

		updateBatchBar();
		if (!ifinterrupted() && --repeat > 0) {
			batchBananaPick(player, bananaTree, repeat);
		}
	}
}
