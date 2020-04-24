package com.openrsc.server.plugins.misc;

import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class BananaTree implements
	OpLocTrigger {

	@Override
	public void onOpLoc(GameObject obj, String command, Player player) {

		if (obj.getID() == 183) {
			player.setBatchEvent(new BatchEvent(player.getWorld(), player, player.getWorld().getServer().getConfig().GAME_TICK, "Pick Banana Tree", player.getCarriedItems().getInventory().getFreeSlots(), false) {
				@Override
				public void action () {
					int bananaCount = 1;
					if (player.getCache().hasKey("banana_pick"))
						bananaCount = player.getCache().getInt("banana_pick") + 1;

					player.getCache().set("banana_pick",bananaCount);
					give(player, ItemId.BANANA.id(), 1);

					if (bananaCount >= 5) {
						player.message("you pick the last banana");
						changeloc(obj, 60000 * 8, 184); // 8 minutes respawn time.
						player.getCache().remove("banana_pick");
						interruptBatch();
					} else {
						player.message("you pick a banana");
					}
				}
			});
		}

		if (obj.getID() == 184) {
			player.message("there are no bananas left on the tree");
		}
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player player) {
		return obj.getID() == 183 || obj.getID() == 184;
	}
}
