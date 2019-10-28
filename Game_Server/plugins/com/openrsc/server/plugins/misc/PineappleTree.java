package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.replaceObjectDelayed;

public class PineappleTree implements ObjectActionExecutiveListener,
	ObjectActionListener {

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == 430) {
			p.message("you pick a pineapple");
			addItem(p, ItemId.FRESH_PINEAPPLE.id(), 1);
			if (!p.getCache().hasKey("pineapple_pick")) {
				p.getCache().set("pineapple_pick", 1);
			} else {
				int pineappleCount = p.getCache().getInt("pineapple_pick");
				p.getCache().set("pineapple_pick", (pineappleCount + 1));
				if (pineappleCount >= 4) {
					replaceObjectDelayed(obj, 60000 * 8, 431); // 8 minutes respawn time.
					p.getCache().remove("pineapple_pick");
				}
			}
		}
		if (obj.getID() == 431) {
			p.message("there are no pineapples left on the tree");
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 431 || obj.getID() == 430;
	}

}
