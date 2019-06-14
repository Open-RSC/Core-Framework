package com.openrsc.server.plugins.misc;

import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.replaceObjectDelayed;

public class BananaTree implements ObjectActionExecutiveListener,
	ObjectActionListener {

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {

		if (obj.getID() == 183) {
			p.setBatchEvent(new BatchEvent(p, 600, p.getInventory().getFreeSlots(), false) {
				@Override
				public void action () {
					int bananaCount = 1;
					if (p.getCache().hasKey("banana_pick"))
						bananaCount = p.getCache().getInt("banana_pick") + 1;

					p.getCache().set("banana_pick",bananaCount);
					addItem(p, ItemId.BANANA.id(), 1);

					if (bananaCount >= 5) {
						p.message("you pick the last banana");
						replaceObjectDelayed(obj, 60000 * 8, 184); // 8 minutes respawn time.
						p.getCache().remove("banana_pick");
						interrupt();
					} else {
						p.message("you pick a banana");
					}
				}
			});
		}

		if (obj.getID() == 184) {
			p.message("there are no bananas left on the tree");
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return obj.getID() == 183 || obj.getID() == 184;
	}
}
