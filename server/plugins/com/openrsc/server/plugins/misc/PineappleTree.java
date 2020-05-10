package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class PineappleTree implements
	OpLocTrigger {

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == 430) {
			player.message("you pick a pineapple");
			give(player, ItemId.FRESH_PINEAPPLE.id(), 1);
			if (!player.getCache().hasKey("pineapple_pick")) {
				player.getCache().set("pineapple_pick", 1);
			} else {
				int pineappleCount = player.getCache().getInt("pineapple_pick");
				player.getCache().set("pineapple_pick", (pineappleCount + 1));
				if (pineappleCount >= 4) {
					changeloc(obj, 60000 * 8, 431); // 8 minutes respawn time.
					player.getCache().remove("pineapple_pick");
				}
			}
		}
		if (obj.getID() == 431) {
			player.message("there are no pineapples left on the tree");
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 431 || obj.getID() == 430;
	}

}
