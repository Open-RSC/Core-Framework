package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.give;
import static com.openrsc.server.plugins.Functions.changeloc;

public class PineappleTree implements
	OpLocTrigger {

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (obj.getID() == 430) {
			p.message("you pick a pineapple");
			give(p, ItemId.FRESH_PINEAPPLE.id(), 1);
			if (!p.getCache().hasKey("pineapple_pick")) {
				p.getCache().set("pineapple_pick", 1);
			} else {
				int pineappleCount = p.getCache().getInt("pineapple_pick");
				p.getCache().set("pineapple_pick", (pineappleCount + 1));
				if (pineappleCount >= 4) {
					changeloc(obj, 60000 * 8, 431); // 8 minutes respawn time.
					p.getCache().remove("pineapple_pick");
				}
			}
		}
		if (obj.getID() == 431) {
			p.message("there are no pineapples left on the tree");
		}
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player player) {
		return obj.getID() == 431 || obj.getID() == 430;
	}

}
